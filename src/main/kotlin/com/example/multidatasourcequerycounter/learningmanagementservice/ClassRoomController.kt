package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/class-rooms")
@RestController
class ClassRoomController(
    private val classInfoFinder: ClassInfoFinder,
) {
    @GetMapping("/{classRoomId}")
    fun getClassInfo(@PathVariable classRoomId: Long): ClassInfo {
        return classInfoFinder.getClassInfo(classRoomId = classRoomId)
    }
}
