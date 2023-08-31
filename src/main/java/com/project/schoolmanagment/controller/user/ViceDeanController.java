package com.project.schoolmanagment.controller.user;

import com.project.schoolmanagment.payload.request.user.ViceDeanRequest;
import com.project.schoolmanagment.payload.response.message.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.ViceDeanResponse;
import com.project.schoolmanagment.service.user.ViceDeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/viceDean")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
public class ViceDeanController {

    public final ViceDeanService viceDeanService;



    @PostMapping("/save")
    public ResponseMessage<ViceDeanResponse> saveViceDean(@Valid @RequestBody ViceDeanRequest viceDeanRequest){
        return viceDeanService.saveViceDean(viceDeanRequest);
    }

    @PutMapping("/update/{userId}")
    public ResponseMessage<ViceDeanResponse> updateViceDeanById (@PathVariable Long userId, @RequestBody @Valid ViceDeanRequest viceDeanRequest){
        return viceDeanService.updateViceDeanById(userId, viceDeanRequest);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("/delete/{userId}")
    public ResponseMessage deleteViceDeanByAdmin(@PathVariable Long userId){
        return viceDeanService.deleteViceDeanByUserId(userId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("getViceDeanById/{userId}")
    public ResponseMessage<ViceDeanResponse> findViceDeanByViceDeanId(@PathVariable Long userId){
        return viceDeanService.getViceDeanByViceDeanId(userId);
    }

    //ZIYA
    //@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("/getAll")
    public List<ViceDeanResponse> getAllViceDeans(){
        return viceDeanService.getAllViceDeans();
    }

    //ENES
    //PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("/getAllViceDeanByPage")
    public Page<ViceDeanResponse> getAllViceDeanByPage(
            @RequestParam(value = "page",defaultValue = "0",required = false) int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type){
        return viceDeanService.getAllViceDeanByPage(page,size,sort,type);
    }

}
