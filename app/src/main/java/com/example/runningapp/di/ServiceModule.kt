package com.example.runningapp.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.runningapp.R
import com.example.runningapp.other.Constants
import com.example.runningapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton


@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {
    @Provides
    @ServiceScoped
    fun provideFusedLocationProviderClient(
        @ApplicationContext app:Context
    )=FusedLocationProviderClient(app)


    @SuppressLint("UnspecifiedImmutableFlag")
    @Provides
    @ServiceScoped
    fun provideMainActivityPendingIntent(
        @ApplicationContext app: Context
    )= PendingIntent.getActivity(
        app,
        0,
        Intent(app, MainActivity::class.java)
            .also {
                it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
            },
        FLAG_UPDATE_CURRENT
    )

    @Provides
    @ServiceScoped
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    )=
        NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANNEL_ID)
        .apply {
            setAutoCancel(false)
            setOngoing(true)
            setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            setContentTitle("Running App")
            setContentText("00:00:00")
            setContentIntent(pendingIntent)
        }

}