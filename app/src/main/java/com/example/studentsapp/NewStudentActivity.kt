package com.example.studentsapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.studentsapp.data.Student
import com.example.studentsapp.data.StudentRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class NewStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_student)

        val nameField = findViewById<TextInputEditText>(R.id.nameField)
        val idField = findViewById<TextInputEditText>(R.id.idField)
        val saveButton = findViewById<MaterialButton>(R.id.saveButton)

        saveButton.setOnClickListener {
            val name = nameField.text.toString()
            val id = idField.text.toString()

            if (name.isNotEmpty() && id.isNotEmpty()) {
                StudentRepository.students.add(Student(id, name))
                finish()
            }
        }
    }
}