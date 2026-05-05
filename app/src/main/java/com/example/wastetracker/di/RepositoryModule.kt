package com.example.wastetracker.di

import com.example.wastetracker.data.repository.FinanceRepository
import com.example.wastetracker.data.repository.OfflineFirstFinanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFinanceRepository(
        repository: OfflineFirstFinanceRepository
    ): FinanceRepository
}
