package com.example.multidatasourcequerycounter.learningmanagementservice

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Table(name = "classroom")
@Entity
class Classroom(
    @Column(nullable = false, length = 50)
    val name: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = INITIAL_ID

    @OneToMany(
        mappedBy = "classroom",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.REMOVE],
        orphanRemoval = true,
    )
    val students: MutableList<Student> = mutableListOf()

    fun enrollStudent(student: Student) {
        students.add(student)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Classroom

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
