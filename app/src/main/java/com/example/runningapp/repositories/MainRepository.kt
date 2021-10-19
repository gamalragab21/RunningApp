package com.example.runningapp.repositories

import com.example.runningapp.db.Run
import com.example.runningapp.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(val runDao: RunDao) {


    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistanceInMeters()

    fun getAllRunsSortedByTimeInMillis() = runDao.getAllTimeInMillis()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeedInKMH()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getTotalAvgSpeed() = runDao.getAllAvgSpeedInKMH()

    fun getTotalDistance() = runDao.getAllDistanceInMeters()

    fun getTotalCaloriesBurned() = runDao.getAllCaloriesBurned()

    fun getTotalTimeInMillis() = runDao.getAllTimeInMillis()


}