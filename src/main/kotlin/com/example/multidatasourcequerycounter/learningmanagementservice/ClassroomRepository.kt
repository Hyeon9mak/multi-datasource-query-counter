package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.data.jpa.repository.JpaRepository

interface ClassroomRepository : JpaRepository<Classroom, Long> {
}
