package com.example.multidatasourcequerycounter.learningmanagementservice.classroom.domain

import org.springframework.data.jpa.repository.JpaRepository

interface ClassroomRepository : JpaRepository<Classroom, Long> {
}
