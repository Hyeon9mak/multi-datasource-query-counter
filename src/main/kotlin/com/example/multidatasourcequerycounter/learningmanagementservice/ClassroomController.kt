package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/classrooms")
@RestController
class ClassroomController(
    private val classInfoFinder: ClassInfoFinder,
    private val classroomCreator: ClassroomCreator,
    private val studentEnroller: StudentEnroller,
) {
    @PostMapping
    fun createClassroom(@RequestBody request: ClassroomCreateRequest): Classroom {
        return classroomCreator.createClassroom(name = request.name)
    }

    @PostMapping("/{classroomId}/enroll-student")
    fun enrollStudent(@PathVariable classroomId: Long, @RequestBody request: StudentEnrollRequest): Student {
        return studentEnroller.enrollStudent(studentName = request.name, classroomId = classroomId)
    }

    @GetMapping("/{classroomId}")
    fun getClassInfo(@PathVariable classroomId: Long): ClassInfo {
        return classInfoFinder.getClassInfo(classroomId = classroomId)
    }
}
