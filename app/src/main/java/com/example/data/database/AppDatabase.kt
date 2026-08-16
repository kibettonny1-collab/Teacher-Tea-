package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.model.*

@Database(
    entities = [
        ClassRoomEntity::class,
        StudentEntity::class,
        InboxMessageEntity::class,
        VocabWordEntity::class,
        AssessmentEntity::class,
        StudentScoreEntity::class,
        ActivityLogEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun classRoomDao(): ClassRoomDao
    abstract fun studentDao(): StudentDao
    abstract fun inboxMessageDao(): InboxMessageDao
    abstract fun vocabWordDao(): VocabWordDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun studentScoreDao(): StudentScoreDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "class_companion_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
