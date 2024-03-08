package com.sessac.myaitrip.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sessac.myaitrip.data.entities.TourItem

@Database(
    entities = [TourItem::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tourDao(): TourDao

    companion object {
        private lateinit var instance : AppDatabase
        fun getDatabase(context: Context): AppDatabase {
            if(!Companion::instance.isInitialized){
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myAiTrip.db"
                ).fallbackToDestructiveMigrationFrom(1, 2)
                    .build()
            }
            return instance
        }
    }
}
