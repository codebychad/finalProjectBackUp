package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.user.ViceDean;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.user.ViceDeanMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.user.ViceDeanRequest;
import com.project.schoolmanagment.payload.response.message.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.ViceDeanResponse;
import com.project.schoolmanagment.repository.user.ViceDeanRepository;
import com.project.schoolmanagment.service.helper.PageableHelper;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViceDeanService {

    private final ViceDeanRepository viceDeanRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final ViceDeanMapper viceDeanMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;



    private ViceDean isViceDeanExist(Long id){
        return viceDeanRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE,id))
        );
    }

    public ResponseMessage<ViceDeanResponse> saveViceDean(ViceDeanRequest viceDeanRequest) {

        uniquePropertyValidator.checkDuplicate(viceDeanRequest.getUsername(),
                viceDeanRequest.getSsn(),
                viceDeanRequest.getPhoneNumber());

        ViceDean viceDean = viceDeanMapper.mapViceDeanRequestToViceDean(viceDeanRequest);

        viceDean.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));
        viceDean.setPassword(passwordEncoder.encode(viceDeanRequest.getPassword()));
        ViceDean savedViceDean = viceDeanRepository.save(viceDean);
        return ResponseMessage.<ViceDeanResponse>builder()
                .message(SuccessMessages.VICE_DEAN_SAVE)
                .httpStatus(HttpStatus.CREATED)
                .object(viceDeanMapper.mapViceDeanToViceDeanResponse(savedViceDean))
                .build();

    }


    public ResponseMessage<ViceDeanResponse> updateViceDeanById(Long userId, ViceDeanRequest viceDeanRequest) {
        ViceDean viceDean = isViceDeanExist(userId);
        uniquePropertyValidator.checkUniqueProperties(viceDean, viceDeanRequest );
        ViceDean updatedViceDean = viceDeanMapper.mapViceDeanRequestToUpdatedViceDean(viceDeanRequest,userId);
        ViceDean savedViceDean = viceDeanRepository.save(updatedViceDean);
        return ResponseMessage.<ViceDeanResponse>builder()
                .message(SuccessMessages.VICE_DEAN_UPDATE)
                .httpStatus(HttpStatus.OK)
                .object(viceDeanMapper.mapViceDeanToViceDeanResponse(savedViceDean))
                .build();
    }


    public ResponseMessage<ViceDeanResponse> getViceDeanByViceDeanId(Long userId) {
        return new ResponseMessage<>(viceDeanMapper.mapViceDeanToViceDeanResponse(isViceDeanExist(userId)
        ), SuccessMessages.VICE_DEAN_FOUND, HttpStatus.OK);
    }

    public List<ViceDeanResponse> getAllViceDeans() {
        return viceDeanRepository.findAll().stream().map(viceDeanMapper::mapViceDeanToViceDeanResponse)
                .collect(Collectors.toList());
    }

    public Page<ViceDeanResponse> getAllViceDeanByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page,size,sort,type);
        return viceDeanRepository.findAll(pageable).map(viceDeanMapper::mapViceDeanToViceDeanResponse);
    }

    public ResponseMessage<?> deleteViceDeanByUserId(Long viceDeanId) {
        isViceDeanExist(viceDeanId);
        viceDeanRepository.deleteById(viceDeanId);
        return ResponseMessage.builder()
                    .message(SuccessMessages.VICE_DEAN_DELETE)
                    .httpStatus(HttpStatus.OK)
                    .build();
        }
}
