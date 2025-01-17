package com.example.studentsapp.data

data class Student(
    var id: String,
    var name: String,
    var isChecked: Boolean = false,
    var phone: String,
    var address: String,
    var birthDate: String,
    var birthTime: String,
)