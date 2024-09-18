package com.example.multidatasourcequerycounter.learningmanagementservice.classroom.application

import com.example.multidatasourcequerycounter.learningmanagementservice.classroom.domain.Student

data class ClassStudentInfo(
    val studentName: String,
) {
    companion object {
        fun fromStudent(student: Student): ClassStudentInfo = ClassStudentInfo(studentName = student.name)
    }
}
