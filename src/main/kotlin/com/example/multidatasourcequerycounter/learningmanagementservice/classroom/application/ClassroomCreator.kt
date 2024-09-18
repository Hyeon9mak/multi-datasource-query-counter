package com.example.multidatasourcequerycounter.learningmanagementservice.classroom.application

import com.example.multidatasourcequerycounter.learningmanagementservice.classroom.domain.Classroom
import com.example.multidatasourcequerycounter.learningmanagementservice.classroom.domain.ClassroomRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class ClassroomCreator(
    private val classroomRepository: ClassroomRepository,
) {
    fun createClassroom(name: String): ClassInfo {
        val classroom = classroomRepository.save(Classroom(name = name))
        return ClassInfo.fromClassroom(classroom = classroom)
    }
}
