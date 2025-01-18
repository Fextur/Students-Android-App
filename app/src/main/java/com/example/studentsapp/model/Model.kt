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
    private val firebaseModel = FirebaseModel()

    companion object {
        val shared = Model()
    }

    fun getAllStudents(callback: (List<Student>) -> Unit) {
        executor.execute {
            firebaseModel.getAllStudents({ students ->
                db.studentDao().insertStudents(*students.toTypedArray())

                mainHandler.post {
                    callback(students)
                }
            }, {
                val students = db.studentDao().getAll()
                mainHandler.post {
                    callback(students)
                }
            })

        }
    }

    fun updateStudents(vararg students: Student, callback: () -> Unit = {}) {
        executor.execute {
            firebaseModel.updateStudents(*students) {
                db.studentDao().insertStudents(*students)
                mainHandler.post {
                    callback()
                }
            }
        }
    }

    fun getStudent(studentId: String, callback: (Student) -> Unit) {
        executor.execute {
            firebaseModel.getStudent(studentId, { firebaseStudent ->
                db.studentDao().insertStudents(firebaseStudent)
                mainHandler.post {
                    callback(firebaseStudent)
                }
            }, {
                val localStudent = db.studentDao().getStudentById(studentId)
                mainHandler.post {
                    callback(localStudent)
                }
            })
        }
    }

    fun deleteStudent(student: Student, callback: () -> Unit = {}) {
        executor.execute {
            firebaseModel.deleteStudent(student)  {
                db.studentDao().delete(student)
                mainHandler.post {
                    callback()
                }
            }
        }
    }
}