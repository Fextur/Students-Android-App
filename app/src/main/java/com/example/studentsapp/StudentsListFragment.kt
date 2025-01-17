package com.example.studentsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentsapp.data.StudentRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton


class StudentsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateMessage: TextView
    private lateinit var addStudentFab: FloatingActionButton
    private lateinit var adapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_students_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        emptyStateMessage = view.findViewById(R.id.emptyStateMessage)
        addStudentFab = view.findViewById(R.id.addStudentFab)

        setupRecyclerView()
        updateEmptyState()

        addStudentFab.setOnClickListener {
            val action = StudentsListFragmentDirections.actionStudentsListFragmentToStudentFormFragment(null)
            it.findNavController().navigate(action)
        }

        return view
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(StudentRepository.students, onRowClick = { student ->
            val action = StudentsListFragmentDirections.actionStudentsListFragmentToStudentFormFragment(student.id)
            view?.findNavController()?.navigate(action)

        }, onCheckChange = { student, isChecked ->
            student.isChecked = isChecked
        })

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun updateEmptyState() {
        if (StudentRepository.students.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateMessage.visibility = View.GONE
        }
    }
}