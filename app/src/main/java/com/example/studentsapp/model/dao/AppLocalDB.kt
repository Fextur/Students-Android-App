package com.example.studentsapp.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studentsapp.base.MyApplication
import com.example.studentsapp.model.Student
import java.lang.IllegalStateException

@Database(entities = [Student::class], version = 2)
abstract class AppLocalDBRepository : RoomDatabase() {
    abstract fun studentDao(): StudentDao
}

object AppLocalDB {
    val db: AppLocalDBRepository by lazy {
        val context = MyApplication.Globals.appContext ?: throw IllegalStateException("Application context is missing")
            Room.databaseBuilder(
                context = context,
                klass = AppLocalDBRepository::class.java,
                name = "dbFileName.db"
            ).fallbackToDestructiveMigration().build()

    }
}