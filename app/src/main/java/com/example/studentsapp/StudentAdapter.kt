package com.example.studentsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentsapp.model.Student
import com.squareup.picasso.Picasso

class StudentAdapter(
    private val students: List<Student>,
    private val onRowClick: (Student) -> Unit,
    private val onCheckChange: (Student, Boolean) -> Unit
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.student_name)
        val id: TextView = view.findViewById(R.id.student_id)
        val picture: ImageView = view.findViewById(R.id.studentPhoto)
        val checkBox: CheckBox = view.findViewById(R.id.student_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.name.text = student.name
        holder.id.text = student.id
        holder.checkBox.isChecked = student.isChecked

        holder.itemView.setOnClickListener { onRowClick(student) }
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckChange(student, isChecked)
        }

        if (student.photoUrl.isNotEmpty()) {
            Picasso.get()
                .load(student.photoUrl)
                .placeholder(R.drawable.student_avatar)
                .error(R.drawable.student_avatar)
                .into(holder.picture)
        } else {
            holder.picture.setImageResource(R.drawable.student_avatar)
        }
    }

    override fun getItemCount() = students.size
}