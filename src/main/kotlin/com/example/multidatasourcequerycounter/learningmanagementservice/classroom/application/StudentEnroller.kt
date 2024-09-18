package com.example.multidatasourcequerycounter.learningmanagementservice.classroom.application

import com.example.multidatasourcequerycounter.learningmanagementservice.classroom.domain.Classroom
import com.example.multidatasourcequerycounter.learningmanagementservice.classroom.domain.ClassroomRepository
import com.example.multidatasourcequerycounter.learningmanagementservice.classroom.domain.Student
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
