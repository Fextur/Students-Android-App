package com.example.studentsapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.studentsapp.data.Student
import com.example.studentsapp.data.StudentRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class StudentFormActivity : AppCompatActivity() {

    private lateinit var nameField: TextInputEditText
    private lateinit var idField: TextInputEditText
    private lateinit var saveButton: MaterialButton

    private lateinit var mode: FormMode
    private var currentStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_form)

        nameField = findViewById<TextInputEditText>(R.id.nameField)
        idField = findViewById<TextInputEditText>(R.id.idField)
        saveButton = findViewById<MaterialButton>(R.id.saveButton)

        mode = if (intent.hasExtra("STUDENT_ID")) FormMode.VIEW else FormMode.ADD

        if (mode == FormMode.VIEW) {
            val studentId = intent.getStringExtra("STUDENT_ID")
            currentStudent = StudentRepository.students.find { it.id == studentId }
            populateFields(currentStudent)
            setFieldsReadOnly()
        }

        saveButton.visibility = if (mode == FormMode.VIEW) View.GONE else View.VISIBLE
        saveButton.setOnClickListener { addNewStudent() }

    }

    private fun addNewStudent() {
        val name = nameField.text.toString()
        val id = idField.text.toString()

        if (name.isEmpty() || id.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        StudentRepository.students.add(Student(id, name))
        Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun populateFields(student: Student?) {
        if (student != null) {
            nameField.setText(student.name)
            idField.setText(student.id)
        }
    }

    private fun setFieldsReadOnly() {
        nameField.isEnabled = false
        idField.isEnabled = false
    }
}