package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.payload.request.user.TeacherRequest;
import com.project.schoolmanagment.payload.response.message.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.TeacherResponse;
import com.project.schoolmanagment.repository.user.TeacherRepository;
import com.project.schoolmanagment.service.business.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

	private final TeacherRepository teacherRepository;
	private final LessonService lessonService;

	public ResponseMessage<TeacherResponse> saveTeacher(TeacherRequest teacherRequest) {
		//we need to get lessons according to ID.s
		return null;
	}
}
