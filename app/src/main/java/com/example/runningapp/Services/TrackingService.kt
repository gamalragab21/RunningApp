package com.example.runningapp.Services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.runningapp.R
import com.example.runningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.other.Constants.ACTION_STOP_SERVICE
import com.example.runningapp.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.runningapp.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runningapp.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runningapp.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runningapp.other.Constants.NOTIFICATION_ID
import com.example.runningapp.other.Constants.TIMER_UPDATE_INTERVAL
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

typealias polyLine = MutableList<LatLng>
typealias polyLines = MutableList<polyLine>

@AndroidEntryPoint
class TrackingService : LifecycleService() {
    var isFirstRun = true
    var serviceKilled=false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var curNotificationBuilder:NotificationCompat.Builder


    private val timeRunInSeconds=MutableLiveData<Long>()


    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoint = MutableLiveData<polyLines>()
        val timeRunInMillis=MutableLiveData<Long>()


    }

    private fun postInitialValue() {
        isTracking.postValue(false)
        pathPoint.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder=baseNotificationBuilder
        postInitialValue()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }

    private fun addEmptyPolyline() = pathPoint.value?.apply {
        add(mutableListOf())
        pathPoint.postValue(this)
    } ?: pathPoint.postValue(mutableListOf(mutableListOf()))

    private var isTimerEnabled=false
    private var lapTime=0L
    private var timeRun=0L
    private var timeStarted=0L
    private var lastSecondTimestamp=0L

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted=System.currentTimeMillis()
        isTimerEnabled=true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                // time difference between now and timeStart
                lapTime=System.currentTimeMillis() -timeStarted
                //
                timeRunInMillis.postValue(timeRun+lapTime)

                if (timeRunInMillis.value!!>=lastSecondTimestamp+1000L){
                   timeRunInSeconds.postValue(timeRunInSeconds.value!!+1)
                   lastSecondTimestamp+=1000L
                }
                delay(TIMER_UPDATE_INTERVAL)


            }

            timeRun+=lapTime
        }
    }
    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled=false
    }
private fun killService(){
    serviceKilled=true
    isFirstRun=true
    pauseService()
    postInitialValue()
    stopForeground(true)
    stopSelf()
}

    private fun updateNotificationTrackingState(isTracking: Boolean){

        val notificationActionText=if (isTracking) "Pause" else "Resume"

        val pendingIntent =if(isTracking){
            val pauseIntent=Intent(this,TrackingService::class.java).apply {
                action= ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this,1,pauseIntent, FLAG_UPDATE_CURRENT)
        }else{
            val resumeIntent =Intent(this,TrackingService::class.java).apply {
                action= ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this,2,resumeIntent, FLAG_UPDATE_CURRENT)

        }

        val notificationManger=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible=true
            set(curNotificationBuilder,ArrayList<NotificationCompat.Action>())

        }
        if (!serviceKilled){
            curNotificationBuilder=baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp,notificationActionText,pendingIntent)
            notificationManger.notify(NOTIFICATION_ID,curNotificationBuilder.build())
        }



    }
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request = com.google.android.gms.location.LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY

                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations.let { locations ->
                    locations?.forEach {
                        addPathPoint(it)
                        Timber.d("NEW LOCATION:${it.latitude} , ${it.longitude}")
//                        Toast.makeText(this@TrackingService, "NEW LOCATION:${it.latitude} , ${it.longitude}", Toast.LENGTH_SHORT).show()
                    }

                } ?: Toast.makeText(
                    applicationContext,
                    "Please , Open your Location to start running",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun addPathPoint(location: Location?) {

        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoint.value?.apply {
                last().add(pos)
                pathPoint.postValue(this)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("ACTION_RESUME_SERVICE")
                        startTimer()
                    }

                }

                ACTION_PAUSE_SERVICE -> {
                    Timber.d("ACTION_PAUSE_SERVICE")
                    pauseService()
                }

                ACTION_STOP_SERVICE -> {
                    Timber.d("ACTION_STOP_SERVICE")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
      startTimer()
        isTracking.postValue(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

//        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//            .apply {
//                setAutoCancel(false)
//                setOngoing(true)
//                setSmallIcon(R.drawable.ic_directions_run_black_24dp)
//                setContentTitle("Running App")
//                setContentText("00:00:00")
//                setContentIntent(getMainActivityPendingIntent())
//            }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
        timeRunInSeconds.observe(this,{
            if (!serviceKilled){
                val notification=curNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(it*1000L))
                notificationManager.notify(NOTIFICATION_ID,notification.build())
            }

        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {

        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
            )

        notificationManager.createNotificationChannel(channel)

    }

//    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
//        this,
//        0,
//        Intent(this, MainActivity::class.java)
//            .also {
//                it.action = ACTION_SHOW_TRACKING_FRAGMENT
//            },
//        FLAG_UPDATE_CURRENT
//    )
}