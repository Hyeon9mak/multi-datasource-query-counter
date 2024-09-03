package com.example.multidatasourcequerycounter.learningmanagementservice

data class ClassInfo(
    val className: String,
    val students: List<ClassStudentInfo>,
) {
    companion object {
        fun fromClassRoom(classRoom: ClassRoom): ClassInfo = ClassInfo(
            className = classRoom.name,
            students = classRoom.students.map { ClassStudentInfo.fromStudent(it) }
        )
    }
}
