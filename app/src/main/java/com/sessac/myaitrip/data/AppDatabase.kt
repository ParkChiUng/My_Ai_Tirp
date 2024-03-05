package com.sessac.myaitrip.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sessac.myaitrip.data.tour.TourDao
import com.sessac.myaitrip.data.tour.TourItem

@Database(
    entities = [TourItem::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tourDao(): TourDao

    companion object {
        private lateinit var instance : AppDatabase
        fun getDatabase(context: Context): AppDatabase {
            if(!::instance.isInitialized){
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
