package com.example.multidatasourcequerycounter.learningmanagementservice.classroom.application

import com.example.multidatasourcequerycounter.learningmanagementservice.classroom.domain.Classroom

data class ClassInfo(
    val className: String,
    val students: List<ClassStudentInfo>,
) {
    companion object {
        fun fromClassroom(classroom: Classroom): ClassInfo = ClassInfo(
            className = classroom.name,
            students = classroom.students.map { ClassStudentInfo.fromStudent(it) }
        )
    }
}
