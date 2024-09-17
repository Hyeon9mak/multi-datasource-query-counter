package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class StudentEnroller(
    private val classroomRepository: ClassroomRepository,
) {
    fun enrollStudent(studentName: String, classroomId: Long): ClassInfo {
        val classroom = findClassroomByClassroomId(classroomId = classroomId)
        val student = Student(name = studentName, classroom = classroom)
        classroom.enrollStudent(student = student)
        return ClassInfo.fromClassroom(classroom = classroom)
    }

    private fun findClassroomByClassroomId(classroomId: Long): Classroom =
        classroomRepository.findByIdOrNull(classroomId)
            ?: throw IllegalArgumentException("Classroom not found with id $classroomId")
}
