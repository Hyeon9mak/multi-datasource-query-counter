package com.example.multidatasourcequerycounter.learningmanagementservice

data class ClassStudentInfo(
    val studentName: String,
) {
    companion object {
        fun fromStudent(student: Student): ClassStudentInfo = ClassStudentInfo(studentName = student.name)
    }
}
