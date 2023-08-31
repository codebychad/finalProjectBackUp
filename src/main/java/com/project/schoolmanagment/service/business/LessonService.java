package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.exception.ConflictException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.business.LessonMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.LessonRequest;
import com.project.schoolmanagment.payload.response.business.LessonResponse;
import com.project.schoolmanagment.payload.response.message.ResponseMessage;
import com.project.schoolmanagment.repository.business.LessonRepository;
import com.project.schoolmanagment.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

	private final LessonRepository lessonRepository;
	private final PageableHelper pageableHelper;
	private final LessonMapper lessonMapper;

	public ResponseMessage<LessonResponse> saveLesson(LessonRequest lessonRequest) {
		//we need to validate that we do not have any existing lesson name as requested.
		isLessonExistByName(lessonRequest.getLessonName());
		Lesson savedLesson = lessonMapper.mapLessonRequestToLesson(lessonRequest);
		lessonRepository.save(savedLesson);
		return ResponseMessage.<LessonResponse>builder()
				.object(lessonMapper.mapLessonToLessonResponse(savedLesson))
				.message(SuccessMessages.LESSON_SAVE)
				.httpStatus(HttpStatus.CREATED)
				.build();
	}

	private boolean isLessonExistByName(String lessonName){
		boolean isLessonExist = lessonRepository.existsLessonByLessonNameEqualsIgnoreCase(lessonName);
		if(isLessonExist){
			throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_LESSON_MESSAGE,lessonName));
		}else {
			return false;
		}
	}
	private Lesson isLessonExistById(Long id){
		return lessonRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE,id)));
	}

	public ResponseMessage deleteLessonById(Long id) {
		isLessonExistById(id);
		lessonRepository.deleteById(id);
		return ResponseMessage.builder()
				.message(SuccessMessages.LESSON_DELETE)
				.httpStatus(HttpStatus.OK)
				.build();
	}

	public ResponseMessage<LessonResponse> getLessonByLessonName(String lessonName) {
		Lesson lesson = lessonRepository
				.getLessonByLessonName(lessonName)
				.orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE,lessonName)));
		return ResponseMessage.<LessonResponse>builder()
				.object(lessonMapper.mapLessonToLessonResponse(lesson))
				.httpStatus(HttpStatus.OK)
				.message(SuccessMessages.LESSON_FOUND)
				.build();
	}

	public Set<Lesson> getAllLessonByLessonId(Set<Long> idSet) {
		//validate if id exist in DB
		idSet.forEach(this::isLessonExistById);
		return lessonRepository.getLessonByLessonIdIList(idSet);
	}

    public Page<LessonResponse> findLessonByPage(int page, int size, String sort, String type) {
			Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
			return lessonRepository.findAll(pageable).map(lessonMapper::mapLessonToLessonResponse);
		}

	public ResponseMessage<List<LessonResponse>> getLessonsByCreditScoreGreaterThan(Integer givenValue) {
		List<LessonResponse> lessonResponse = lessonRepository.getLessonsByCreditScoreGreaterThanEqual(givenValue).
												stream().
												map(lessonMapper::mapLessonToLessonResponse).
												collect(Collectors.toList());

		if(lessonResponse.isEmpty()) {
			return ResponseMessage.<List<LessonResponse>>builder()
					.message(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE, givenValue))
					.httpStatus(HttpStatus.BAD_REQUEST)
					.object(lessonResponse)
					.build();
		}

		return ResponseMessage.<List<LessonResponse>>builder()
				.message(String.format(SuccessMessages.LESSON_FOUND, givenValue))
				.httpStatus(HttpStatus.FOUND)
				.object(lessonResponse)
				.build();
	}

	public ResponseMessage<LessonResponse> updateLessonById(Long id, LessonRequest lessonRequest) {
		Lesson lesson = isLessonExistById(id);
		if(!lesson.getLessonName().equalsIgnoreCase(lessonRequest.getLessonName())){
			if(isLessonExistByName(lessonRequest.getLessonName())){
				throw  new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_LESSON_MESSAGE,lessonRequest.getLessonName()));
			}
		}
		Lesson updatedLesson = lessonMapper.mapLessonRequestToUpdateLesson(lessonRequest, id);
		Lesson savedLesson = lessonRepository.save(updatedLesson);
		return ResponseMessage.<LessonResponse>builder()
				.message(SuccessMessages.LESSON_UPDATE)
				.httpStatus(HttpStatus.OK)
				.object(lessonMapper.mapLessonToLessonResponse(savedLesson))
				.build();
	}

}
