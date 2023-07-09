package com.project.schoolmanagment.service.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.LessonMapper;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.LessonRequest;
import com.project.schoolmanagment.payload.response.LessonResponse;
import com.project.schoolmanagment.payload.response.ResponseMessage;
import com.project.schoolmanagment.repository.businnes.LessonRepository;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LessonService {

	private final LessonMapper lessonMapper;

	private final LessonRepository lessonRepository;

	private final PageableHelper pageableHelper;


	public ResponseMessage<LessonResponse>saveLesson(LessonRequest lessonRequest){
		isLessonExistByLessonName(lessonRequest.getLessonName());

		Lesson savedLesson = lessonRepository.save(lessonMapper.mapLessonRequestToLesson(lessonRequest));

		return ResponseMessage.<LessonResponse>builder()
				.object(lessonMapper.mapLessonToLessonResponse(savedLesson))
				.message(SuccessMessages.LESSON_SAVE)
				.httpStatus(HttpStatus.CREATED)
				.build();
	}


	public ResponseMessage deleteLessonById(Long id){
		isLessonExistById(id);
		lessonRepository.deleteById(id);

		return ResponseMessage.builder()
				.message(SuccessMessages.LESSON_DELETE)
				.httpStatus(HttpStatus.OK)
				.build();
	}

	public ResponseMessage<LessonResponse>getLessonByLessonName(String lessonName){
		if(lessonRepository.getLessonByLessonName(lessonName).isPresent()){
			return ResponseMessage.<LessonResponse>builder()
					.message(SuccessMessages.LESSON_FOUND)
					.object(lessonMapper.mapLessonToLessonResponse(lessonRepository.getLessonByLessonName(lessonName).get()))
					.build();
		}else {
			return ResponseMessage.<LessonResponse>builder()
					.message(ErrorMessages.NOT_FOUND_LESSON_MESSAGE)
					.build();
		}
	}

	public Page<LessonResponse> findLessonByPage (int page, int size, String sort, String type){
		Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
		return lessonRepository.findAll(pageable).map(lessonMapper::mapLessonToLessonResponse);
	}

	//TODO in case of returning empty collection, exception handling may be implemented
	public Set<Lesson> getLessonByLessonIdSet(Set<Long> lessons){
		return lessonRepository.getLessonByLessonIdList(lessons);
	}

	private boolean isLessonExistByLessonName(String lessonName){
		boolean lessonExist = lessonRepository.existsLessonByLessonNameEqualsIgnoreCase(lessonName);
		if(lessonExist){
			throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE, lessonName));
		} else {
			return false;
		}
	}

	Lesson isLessonExistById(Long id){
		return lessonRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE, id)));
	}
}
