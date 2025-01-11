package com.example.studentsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentsapp.data.Student
import com.example.studentsapp.data.StudentRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        StudentRepository.students.add(Student("1", "Alice"))
        StudentRepository.students.add(Student("2", "Bob"))

        adapter = StudentAdapter(StudentRepository.students,
            onRowClick = { student ->
                // TODO: Navigate to StudentDetailsActivity (to be implemented)
            },
            onCheckChange = { student, isChecked ->
                student.isChecked = isChecked
            }
        )

        recyclerView.adapter = adapter

        // TODO: Add FloatingActionButton click listener (to be implemented)
    }
}