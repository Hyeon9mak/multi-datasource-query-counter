package com.example.multidatasourcequerycounter.learningmanagementservice

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Student(
    @Column(nullable = false, length = 10)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val classroom: Classroom,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = INITIAL_ID

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Student

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
