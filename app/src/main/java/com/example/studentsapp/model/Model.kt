package com.example.studentsapp.model

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.studentsapp.model.dao.AppLocalDB
import com.example.studentsapp.model.dao.AppLocalDBRepository
import java.util.concurrent.Executors

class Model private constructor() {

    private val db: AppLocalDBRepository = AppLocalDB.db
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val firebaseModel = FirebaseModel()

    companion object {
        val shared = Model()
    }

    fun getAllStudents(callback: (List<Student>) -> Unit) {
        executor.execute {
            firebaseModel.getAllStudents(
                successCallback = { students ->
                    executor.execute {
                        db.studentDao().insertStudents(*students.toTypedArray())
                        mainHandler.post {
                            callback(students)
                        }
                    }
                },
                failureCallback = {
                    executor.execute {
                        val students = db.studentDao().getAll()
                        mainHandler.post {
                            callback(students)
                        }
                    }
                }
            )
        }
    }

    fun updateStudents(vararg students: Student, callback: () -> Unit = {}) {
        executor.execute {
            firebaseModel.updateStudents(*students) {
                executor.execute {
                    db.studentDao().insertStudents(*students)
                    mainHandler.post {
                        callback()
                    }
                }
            }
        }
    }

    fun getStudent(studentId: String, callback: (Student) -> Unit) {
        executor.execute {
            firebaseModel.getStudent(studentId, { firebaseStudent ->
                executor.execute { // Ensures Room operation is on a background thread
                    db.studentDao().insertStudents(firebaseStudent) // Cache Firebase data
                    mainHandler.post { callback(firebaseStudent) } // Notify UI
                }
            }, {
                executor.execute { // Ensures fallback Room operation is on a background thread
                    val localStudent = db.studentDao().getStudentById(studentId)
                    mainHandler.post { callback(localStudent) } // Notify UI
                }
            })
        }
    }

    fun deleteStudent(student: Student, callback: () -> Unit = {}) {
        executor.execute {
            firebaseModel.deleteStudent(student) {
                executor.execute {
                    db.studentDao().delete(student)
                    mainHandler.post {
                        callback()
                    }
                }
            }
        }
    }
}