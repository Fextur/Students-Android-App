package com.example.studentsapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import com.example.studentsapp.model.Model
import com.example.studentsapp.model.Student
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import kotlin.properties.Delegates

class StudentFormFragment : Fragment() {

    private lateinit var progressBar: ProgressBar

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

    private lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    private var studentPhotoBitmap: Bitmap? = null

    private var mode: FormMode by Delegates.observable(FormMode.VIEW) { _, _, newValue ->
        updateButtonsVisibility(newValue)
        setFieldsEnabled(newValue != FormMode.VIEW)
        idField.isEnabled = newValue == FormMode.ADD

        setActionBar(newValue)
    }
    private var currentStudent: Student? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_form, container, false)

        progressBar = view.findViewById(R.id.progressBar)

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

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                studentPhotoBitmap = it
                view.findViewById<ImageView>(R.id.studentPhoto)?.setImageBitmap(bitmap)
            } ?: Toast.makeText(requireContext(), "Failed to capture photo", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<MaterialButton>(R.id.takePhotoButton).setOnClickListener {
            cameraLauncher.launch(null)
        }

        val args = StudentFormFragmentArgs.fromBundle(requireArguments())
        mode = if (args.studentId != null) FormMode.VIEW else FormMode.ADD

        if (mode == FormMode.VIEW) {
            progressBar.visibility = View.VISIBLE
            Model.shared.getStudent(args.studentId!!) { student ->
                currentStudent = student
                populateFields(currentStudent)
                progressBar.visibility = View.GONE
            }
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

        if (currentStudent != null && currentStudent!!.id != id) {
            Toast.makeText(requireContext(), "You can't change the ID of a student", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (name.isEmpty() || id.isEmpty() || phone.isEmpty() || address.isEmpty() || birthDate.isEmpty() || birthTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        progressBar.visibility = View.VISIBLE

        if (studentPhotoBitmap != null) {
            Model.shared.uploadImage(studentPhotoBitmap!!, id) { photoUrl ->
                if (photoUrl.isNotEmpty()) {
                    saveStudent(name, id, phone, address, isChecked, birthDate, birthTime, photoUrl)
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            saveStudent(name, id, phone, address, isChecked, birthDate, birthTime, currentStudent?.photoUrl ?: "")
        }
    }

    private fun saveStudent(
        name: String,
        id: String,
        phone: String,
        address: String,
        isChecked: Boolean,
        birthDate: String,
        birthTime: String,
        photoUrl: String
    ) {
        val student = Student(id, name, isChecked, phone, address, birthDate, birthTime, photoUrl)
        Model.shared.updateStudents(student) {
            Toast.makeText(requireContext(), "Student saved successfully!", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            findNavController().navigateUp()
        }
    }

    private fun deleteStudent() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this student?")
            .setPositiveButton("Delete") { _, _ ->
                currentStudent?.let {
                    progressBar.visibility = View.VISIBLE
                    Model.shared.deleteStudent(currentStudent!!) {
                        Toast.makeText(
                            requireContext(),
                            "${it.name} has been deleted.",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressBar.visibility = View.GONE
                        findNavController().navigateUp()
                    }
                }
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
