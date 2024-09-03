package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.data.jpa.repository.JpaRepository

interface ClassRoomRepository : JpaRepository<ClassRoom, Long> {
}
