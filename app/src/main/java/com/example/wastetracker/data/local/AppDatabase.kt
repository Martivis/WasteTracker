package com.example.wastetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wastetracker.data.local.dao.OperationDao
import com.example.wastetracker.data.local.entity.OperationEntity

@Database(entities = [OperationEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun operationDao(): OperationDao
}
