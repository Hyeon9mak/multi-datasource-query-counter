package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class ClassroomCreator(
    private val classroomRepository: ClassroomRepository,
) {
    fun createClassroom(name: String): Classroom {
        val classroom = Classroom(name = name)
        return classroomRepository.save(classroom)
    }
}
