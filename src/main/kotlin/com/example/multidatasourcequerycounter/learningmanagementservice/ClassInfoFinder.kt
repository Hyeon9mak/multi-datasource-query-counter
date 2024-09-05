package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

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
