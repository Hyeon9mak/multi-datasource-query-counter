package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ClassInfoFinder(
    private val classRoomRepository: ClassRoomRepository,
) {
    fun getClassInfo(classRoomId: Long): ClassInfo {
        val classRoom = findClassRoomByClassRoomId(classRoomId = classRoomId)
        return ClassInfo.fromClassRoom(classRoom = classRoom)
    }

    private fun findClassRoomByClassRoomId(classRoomId: Long): ClassRoom =
        classRoomRepository.findByIdOrNull(classRoomId)
            ?: throw IllegalArgumentException("Class not found with id $classRoomId")
}
