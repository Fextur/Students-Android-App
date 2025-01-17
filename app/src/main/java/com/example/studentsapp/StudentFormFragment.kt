package com.example.studentsapp

import android.os.Bundle
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
import kotlin.properties.Delegates

class StudentFormFragment : Fragment() {

    private lateinit var nameField: TextInputEditText
    private lateinit var idField: TextInputEditText
    private lateinit var phoneField: TextInputEditText
    private lateinit var addressField: TextInputEditText
    private lateinit var isCheckedBox: CheckBox
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


        if (mode != FormMode.ADD) initActionBar(view)
        return view
    }

    private fun updateStudent() {
        val name = nameField.text.toString()
        val id = idField.text.toString()
        val phone = phoneField.text.toString()
        val address = addressField.text.toString()
        val isChecked = isCheckedBox.isChecked

        if (name.isEmpty() || id.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val dialogActionString = if (currentStudent != null) "Update" else "Add"
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm $dialogActionString")
            .setMessage("Are you sure you want to $dialogActionString this student?")
            .setPositiveButton(dialogActionString) { _, _ ->
                currentStudent?.apply {
                        this.name = name
                        this.id = id
                        this.phone = phone
                        this.isChecked = isChecked
                        this.address = address

                } ?: {
                    StudentRepository.students.add(Student(id, name, isChecked, phone, address))
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
        updateButton.visibility = if (mode == FormMode.EDIT) View.VISIBLE else View.GONE
        cancelButton.visibility = if (mode == FormMode.EDIT) View.VISIBLE else View.GONE
        deleteButton.visibility = if (mode == FormMode.EDIT) View.VISIBLE else View.GONE
    }

    private fun initActionBar(view: View) {
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