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
    var photoUrl: String = ""
) {
    val json: Map<String, Any>
        get() = hashMapOf(
            "id" to id,
            "name" to name,
            "isChecked" to isChecked,
            "phone" to phone,
            "address" to address,
            "birthDate" to birthDate,
            "birthTime" to birthTime,
            "photoUrl" to photoUrl
        )

    companion object {
        fun fromJSON(json: Map<String, Any>): Student {
            val id = json["id"] as? String ?: ""
            val name = json["name"] as? String?: ""
            val isChecked = json["isChecked"] as? Boolean ?: false
            val phone = json["phone"] as? String?: ""
            val address = json["address"]as? String ?: ""
            val birthDate = json["birthDate"] as? String?: ""
            val birthTime = json["birthTime"] as? String?: ""
            val photoUrl = json["photoUrl"] as? String?: ""

            return Student(id, name, isChecked, phone, address, birthDate, birthTime, photoUrl)
        }
    }

}