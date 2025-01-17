package com.example.studentsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentsapp.model.Model
import com.example.studentsapp.model.Student


class StudentsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateMessage: TextView
    private lateinit var adapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_students_list, container, false)
        initActionBar(view)

        recyclerView = view.findViewById(R.id.recyclerView)
        emptyStateMessage = view.findViewById(R.id.emptyStateMessage)

        Model.shared.getAllStudents { students ->
            setupRecyclerView(students)
            updateEmptyState(students.isEmpty())
        }


        return view
    }

    private fun setupRecyclerView(students: List<Student>) {
        adapter = StudentAdapter(students, onRowClick = { student ->
            val action =
                StudentsListFragmentDirections.actionStudentsListFragmentToStudentFormFragment(
                    student.id
                )
            view?.findNavController()?.navigate(action)

        }, onCheckChange = { student, isChecked ->
            student.isChecked = isChecked
        })

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            recyclerView.visibility = View.GONE
            emptyStateMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateMessage.visibility = View.GONE
        }
    }

    private fun initActionBar(view: View) {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_students_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_add -> {
                        val action =
                            StudentsListFragmentDirections.actionStudentsListFragmentToStudentFormFragment(
                                null
                            )
                        view.findNavController().navigate(action)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }
}