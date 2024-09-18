package com.example.multidatasourcequerycounter.learningmanagementservice.classroom.application

import com.example.multidatasourcequerycounter.learningmanagementservice.classroom.domain.Classroom
import com.example.multidatasourcequerycounter.learningmanagementservice.classroom.domain.ClassroomRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Component
class ClassInfoFinder(
    private val classroomRepository: ClassroomRepository,
) {
    fun getClassInfo(classroomId: Long): ClassInfo {
        val classroom = findClassroomByClassroomId(classroomId = classroomId)
        return ClassInfo.fromClassroom(classroom = classroom)
    }

    private fun findClassroomByClassroomId(classroomId: Long): Classroom =
        classroomRepository.findByIdOrNull(classroomId)
            ?: throw IllegalArgumentException("Class not found with id $classroomId")
}
