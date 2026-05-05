package com.example.wastetracker.di

import android.content.Context
import androidx.room.Room
import com.example.wastetracker.data.local.AppDatabase
import com.example.wastetracker.data.local.dao.OperationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "waste_tracker_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideOperationDao(database: AppDatabase): OperationDao {
        return database.operationDao()
    }
}
