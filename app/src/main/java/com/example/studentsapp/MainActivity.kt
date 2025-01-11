package com.example.studentsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentsapp.data.StudentRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter
    private lateinit var emptyStateMessage: View

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
        emptyStateMessage = findViewById(R.id.emptyStateMessage)

        setupRecyclerView()
        updateEmptyState()

        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.addStudentFab).setOnClickListener {
            startActivity(Intent(this, StudentFormActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(StudentRepository.students, onRowClick = { student ->
            val intent = Intent(this, StudentFormActivity::class.java)
            intent.putExtra("STUDENT_ID", student.id)
            startActivity(intent)
        }, onCheckChange = { student, isChecked ->
            student.isChecked = isChecked
        })
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

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        updateEmptyState()
    }

}