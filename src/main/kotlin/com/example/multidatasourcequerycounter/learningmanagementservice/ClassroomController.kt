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
) {
    @PostMapping
    fun createClassroom(@RequestBody request: ClassroomCreateRequest): Classroom {
        return classroomCreator.createClassroom(name = request.name)
    }

    @GetMapping("/{classroomId}")
    fun getClassInfo(@PathVariable classroomId: Long): ClassInfo {
        return classInfoFinder.getClassInfo(classroomId = classroomId)
    }
}
