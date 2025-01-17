package com.example.studentsapp

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.studentsapp.data.Student
import com.example.studentsapp.data.StudentRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlin.properties.Delegates

class StudentFormActivity : AppCompatActivity() {

    private lateinit var nameField: TextInputEditText
    private lateinit var idField: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var editButton: MaterialButton
    private lateinit var phoneField: TextInputEditText
    private lateinit var addressField: TextInputEditText
    private lateinit var isCheckedBox: CheckBox
    private lateinit var cancelButton: MaterialButton
    private lateinit var updateButton: MaterialButton
    private lateinit var deleteButton: MaterialButton

    private var mode: FormMode by Delegates.observable(FormMode.VIEW) { _, _, newValue ->
        updateButtonsVisibility(newValue)
        setFieldsEnabled(newValue != FormMode.VIEW)
    }
    private var currentStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_form)

        nameField = findViewById(R.id.nameField)
        idField = findViewById(R.id.idField)
        phoneField = findViewById(R.id.phoneField)
        addressField = findViewById(R.id.addressField)
        isCheckedBox = findViewById(R.id.isCheckedBox)

        saveButton = findViewById(R.id.saveButton)
        editButton = findViewById(R.id.editButton)
        cancelButton = findViewById(R.id.cancelButton)
        deleteButton = findViewById(R.id.deleteButton)
        updateButton = findViewById(R.id.updateButton)

        mode = if (intent.hasExtra("STUDENT_ID")) FormMode.VIEW else FormMode.ADD

        if (mode == FormMode.VIEW) {
            val studentId = intent.getStringExtra("STUDENT_ID")
            currentStudent = StudentRepository.students.find { it.id == studentId }
            populateFields(currentStudent)
        }

        saveButton.setOnClickListener { updateStudent() }
        editButton.setOnClickListener { mode = FormMode.EDIT }

        cancelButton.setOnClickListener {
            populateFields(currentStudent)
            mode = FormMode.VIEW
        }

        updateButton.setOnClickListener {
            updateStudent()
            mode = FormMode.VIEW
        }

        deleteButton.setOnClickListener {
            currentStudent?.let {
            StudentRepository.students.remove(it)
            Toast.makeText(this, "${it.name} has been deleted.", Toast.LENGTH_SHORT).show()
            finish()
        }}
    }

    private fun updateStudent() {
        val name = nameField.text.toString()
        val id = idField.text.toString()
        val phone = phoneField.text.toString()
        val address = addressField.text.toString()
        val isChecked = isCheckedBox.isChecked

        if (name.isEmpty() || id.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if(currentStudent != null) {
            currentStudent?.let {
                it.name = name
                it.id = id
                it.phone = phone
                it.isChecked = isChecked
                it.address = address
            }
        } else {
            StudentRepository.students.add(Student(id, name, isChecked, phone, address))
            finish()
        }

        Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show()
    }

    private fun populateFields(student: Student?) {
        if (student != null) {
            nameField.setText(student.name)
            idField.setText(student.id)
            phoneField.setText(student.phone)
            addressField.setText(student.address)
            isCheckedBox.isChecked = student.isChecked
        }
    }

    private fun setFieldsEnabled(isEnabled: Boolean) {
        nameField.isEnabled = isEnabled
        idField.isEnabled = isEnabled
        phoneField.isEnabled = isEnabled
        addressField.isEnabled = isEnabled
        isCheckedBox.isEnabled = isEnabled
    }

    private fun updateButtonsVisibility(mode: FormMode) {
        saveButton.visibility = if (mode == FormMode.ADD) View.VISIBLE else View.GONE
        editButton.visibility = if (mode == FormMode.VIEW) View.VISIBLE else View.GONE
        updateButton.visibility = if (mode == FormMode.EDIT) View.VISIBLE else View.GONE
        cancelButton.visibility = if (mode == FormMode.EDIT) View.VISIBLE else View.GONE
        deleteButton.visibility = if (mode == FormMode.EDIT) View.VISIBLE else View.GONE
    }

}