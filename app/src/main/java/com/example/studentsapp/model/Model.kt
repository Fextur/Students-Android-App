package com.example.studentsapp.model

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.studentsapp.model.dao.AppLocalDB
import com.example.studentsapp.model.dao.AppLocalDBRepository
import java.util.concurrent.Executors

class Model private  constructor() {

    private val db: AppLocalDBRepository = AppLocalDB.db
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val shared = Model()
    }

    fun getAllStudents(callback: (List<Student>) -> Unit) {
        executor.execute {
            val students = db.studentDao().getAll()
            mainHandler.post {
                callback(students)
            }
        }
    }

    fun addStudents(vararg students: Student, callback: () -> Unit = {}) {
        executor.execute {
            db.studentDao().insertStudents(*students)
            mainHandler.post {
                callback()
            }
        }
    }
}