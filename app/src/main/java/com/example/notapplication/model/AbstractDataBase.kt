package com.example.notapplication.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [NoteBook::class], version = 3)
abstract class AbstractDataBase : RoomDatabase() {

    abstract fun abstractFunDataBase(): Interface

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val addColumnQuery: String =
                    "ALTER TABLE noteBook ADD COLUMN date TEXT NOT NULL DEFAULT undefined"
                db.execSQL(addColumnQuery)
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE noteBook ADD COLUMN timeInMillis INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE noteBook ADD COLUMN isEnabledTime INTEGER NOT NULL DEFAULT 1")
            }
        }

        fun buildDataBase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AbstractDataBase::class.java,
            name = "notes"
        ).allowMainThreadQueries().addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()


    }

}