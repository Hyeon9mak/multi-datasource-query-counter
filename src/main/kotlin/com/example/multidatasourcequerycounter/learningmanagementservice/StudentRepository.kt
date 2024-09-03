package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.data.jpa.repository.JpaRepository

interface StudentRepository : JpaRepository<Student, Long> {
}
