package com.example.studentsapp.model

import com.example.studentsapp.Constants
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings

class FirebaseModel {
    private val db = Firebase.firestore

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
        db.firestoreSettings = settings
    }

    fun getAllStudents(callback: (List<Student>) -> Unit) {
        db.collection(Constants.Collections.STUDENTS).get().addOnSuccessListener {
            val students: MutableList<Student> = mutableListOf()
            for (json in it) {
                students.add(Student.fromJSON(json.data))
            }

            callback(students)
        }
    }

    fun updateStudents(vararg students: Student, callback: () -> Unit = {}) {
        val batch = db.batch()
        students.forEach { student ->
            val docRef = db.collection(Constants.Collections.STUDENTS).document(student.id)
            batch.set(docRef, student.json)
        }

        batch.commit().addOnSuccessListener {
            callback()
        }
    }

    fun getStudent(studentId: String, callback: (Student) -> Unit) {
        val docRef = db.collection(Constants.Collections.STUDENTS).document(studentId)
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val student = Student.fromJSON(document.data!!)
                callback(student)
            }
        }
    }

    fun deleteStudent(student: Student, callback: () -> Unit = {}) {
        val docRef = db.collection(Constants.Collections.STUDENTS).document(student.id)
        docRef.delete().addOnSuccessListener {
            callback()
        }
    }

}