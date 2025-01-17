package com.example.studentsapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Student(
    @PrimaryKey var id: String,
    var name: String,
    var isChecked: Boolean = false,
    var phone: String,
    var address: String,
    var birthDate: String,
    var birthTime: String,
)