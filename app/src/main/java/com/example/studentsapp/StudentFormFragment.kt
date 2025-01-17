package com.example.studentsapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.studentsapp.data.Student
import com.example.studentsapp.data.StudentRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import kotlin.properties.Delegates

class StudentFormFragment : Fragment() {

    private lateinit var nameField: TextInputEditText
    private lateinit var idField: TextInputEditText
    private lateinit var phoneField: TextInputEditText
    private lateinit var addressField: TextInputEditText
    private lateinit var isCheckedBox: CheckBox

    private lateinit var birthDateField: TextInputEditText
    private lateinit var birthTimeField: TextInputEditText

    private lateinit var saveButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private lateinit var updateButton: MaterialButton
    private lateinit var deleteButton: MaterialButton

    private var mode: FormMode by Delegates.observable(FormMode.VIEW) { _, _, newValue ->
        updateButtonsVisibility(newValue)
        setFieldsEnabled(newValue != FormMode.VIEW)
        setActionBar(newValue)
    }
    private var currentStudent: Student? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_form, container, false)

        nameField = view.findViewById(R.id.nameField)
        idField = view.findViewById(R.id.idField)
        phoneField = view.findViewById(R.id.phoneField)
        addressField = view.findViewById(R.id.addressField)
        isCheckedBox = view.findViewById(R.id.isCheckedBox)
        birthDateField = view.findViewById(R.id.birthDateField)
        birthTimeField = view.findViewById(R.id.birthTimeField)

        saveButton = view.findViewById(R.id.saveButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        deleteButton = view.findViewById(R.id.deleteButton)
        updateButton = view.findViewById(R.id.updateButton)

        val args = StudentFormFragmentArgs.fromBundle(requireArguments())
        mode = if (args.studentId != null) FormMode.VIEW else FormMode.ADD

        if (mode == FormMode.VIEW) {
            currentStudent = StudentRepository.students.find { it.id == args.studentId }
            populateFields(currentStudent)
        }

        saveButton.setOnClickListener { updateStudent() }
        cancelButton.setOnClickListener {
            populateFields(currentStudent)
            mode = FormMode.VIEW
        }
        updateButton.setOnClickListener {
            updateStudent()
            mode = FormMode.VIEW
        }
        deleteButton.setOnClickListener { deleteStudent() }

        birthDateField.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val formattedDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    birthDateField.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        birthTimeField.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val formattedTime = "%02d:%02d".format(hourOfDay, minute)
                    birthTimeField.setText(formattedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // Use 24-hour format
            ).show()
        }

        if (mode != FormMode.ADD) initActionBar()
        return view
    }

    private fun updateStudent() {
        val name = nameField.text.toString()
        val id = idField.text.toString()
        val phone = phoneField.text.toString()
        val address = addressField.text.toString()
        val isChecked = isCheckedBox.isChecked
        val birthDate = birthDateField.text.toString()
        val birthTime = birthTimeField.text.toString()

        if (name.isEmpty() || id.isEmpty() || phone.isEmpty() || address.isEmpty() || birthDate.isEmpty() || birthTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val dialogActionString = if (currentStudent != null) "Update" else "Add"
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm $dialogActionString")
            .setMessage("Are you sure you want to $dialogActionString this student?")
            .setPositiveButton(dialogActionString) { _, _ ->
                if (currentStudent != null) {
                    currentStudent?.let {
                        it.name = name
                        it.id = id
                        it.phone = phone
                        it.isChecked = isChecked
                        it.address = address
                        it.birthDate = birthDate
                        it.birthTime = birthTime

                    }
                } else {
                    StudentRepository.students.add(
                        Student(
                            id,
                            name,
                            isChecked,
                            phone,
                            address,
                            birthDate,
                            birthTime
                        )
                    )
                }
                Toast.makeText(
                    requireContext(),
                    "Student $dialogActionString successful",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp() // Go back to the list
            }
            .setNegativeButton("Cancel", null) // Do nothing on cancel
            .create().show()
    }

    private fun deleteStudent() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this student?")
            .setPositiveButton("Delete") { _, _ ->
                currentStudent?.let {
                    StudentRepository.students.remove(it)
                    Toast.makeText(
                        requireContext(),
                        "${it.name} has been deleted.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                findNavController().navigateUp()

            }
            .setNegativeButton("Cancel", null)
            .create().show()

    }

    private fun populateFields(student: Student?) {
        if (student != null) {
            nameField.setText(student.name)
            idField.setText(student.id)
            phoneField.setText(student.phone)
            addressField.setText(student.address)
            isCheckedBox.isChecked = student.isChecked
            birthDateField.setText(student.birthDate)
            birthTimeField.setText(student.birthTime)
        }
    }

    private fun setFieldsEnabled(isEnabled: Boolean) {
        nameField.isEnabled = isEnabled
        idField.isEnabled = isEnabled
        phoneField.isEnabled = isEnabled
        addressField.isEnabled = isEnabled
        isCheckedBox.isEnabled = isEnabled
        birthDateField.isEnabled = isEnabled
        birthTimeField.isEnabled = isEnabled
    }

    private fun updateButtonsVisibility(mode: FormMode) {
        saveButton.visibility = if (mode == FormMode.ADD) View.VISIBLE else View.GONE
        updateButton.visibility = if (mode == FormMode.EDIT) View.VISIBLE else View.GONE
        cancelButton.visibility = if (mode == FormMode.EDIT) View.VISIBLE else View.GONE
        deleteButton.visibility = if (mode == FormMode.EDIT) View.VISIBLE else View.GONE
    }

    private fun initActionBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_student_form, menu)
                menu.findItem(R.id.action_edit).isVisible = mode == FormMode.VIEW
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit -> {
                        mode = FormMode.EDIT
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun setActionBar(mode: FormMode) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = when (mode) {
            FormMode.ADD -> "New Student"
            FormMode.VIEW -> "Student's Details"
            FormMode.EDIT -> "Edit Student"
        }
        requireActivity().invalidateOptionsMenu()
    }
}