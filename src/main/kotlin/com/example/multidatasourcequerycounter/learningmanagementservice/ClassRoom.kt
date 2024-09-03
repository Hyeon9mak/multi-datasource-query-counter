package com.example.multidatasourcequerycounter.learningmanagementservice

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class ClassRoom(
    val name: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = INITIAL_ID

    @OneToMany(
        mappedBy = "classRoom",
        fetch = FetchType.EAGER,
        cascade = [CascadeType.PERSIST, CascadeType.REMOVE],
        orphanRemoval = true,
    )
    val students: MutableList<Student> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassRoom

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}