package com.example.runningapp.di

import android.content.Context
import androidx.room.Room
import com.example.runningapp.db.RunDao
import com.example.runningapp.db.RunningDataBase
import com.example.runningapp.other.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesRunningDataBase(@ApplicationContext app: Context): RunningDataBase =
        Room.databaseBuilder(
            app,
            RunningDataBase::class.java,
            RUNNING_DATABASE_NAME
        ).build()


    @Singleton
    @Provides
    fun providesRunDao(dataBase: RunningDataBase): RunDao =dataBase.getRunDao()

}