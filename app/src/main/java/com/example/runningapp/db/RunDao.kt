package com.example.runningapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface RunDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("select * from running_table order by timestamp DESC")
    fun getAllRunsSortedByDate():LiveData<List<Run>>

    @Query("select * from running_table order by timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis():LiveData<List<Run>>

    @Query("select * from running_table order by caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned():LiveData<List<Run>>

    @Query("select * from running_table order by avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeedInKMH():LiveData<List<Run>>

    @Query("select * from running_table order by distanceInMeters DESC")
    fun getAllRunsSortedByDistanceInMeters():LiveData<List<Run>>

    @Query("select sum (timeInMillis) from running_table order by distanceInMeters DESC")
    fun getAllTimeInMillis():LiveData<Long>

    @Query("select sum (caloriesBurned) from running_table order by distanceInMeters DESC")
    fun getAllCaloriesBurned():LiveData<Int>

    @Query("select sum (distanceInMeters) from running_table order by distanceInMeters DESC")
    fun getAllDistanceInMeters():LiveData<Int>

    @Query("select sum (avgSpeedInKMH) from running_table order by distanceInMeters DESC")
    fun getAllAvgSpeedInKMH():LiveData<Float>





}