package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class StudentEnroller(
    private val studentRepository: StudentRepository,
    private val classroomRepository: ClassroomRepository,
) {
    fun enrollStudent(studentName: String, classroomId: Long): Student {
        val classroom = findClassroomByClassroomId(classroomId = classroomId)
        val student = Student(name = studentName, classroom = classroom)
        return studentRepository.save(student)
    }

    private fun findClassroomByClassroomId(classroomId: Long): Classroom =
        classroomRepository.findByIdOrNull(classroomId)
            ?: throw IllegalArgumentException("Classroom not found with id $classroomId")
}
