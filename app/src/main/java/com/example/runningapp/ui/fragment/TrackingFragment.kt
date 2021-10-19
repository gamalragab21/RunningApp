package com.example.runningapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningapp.R
import com.example.runningapp.Services.TrackingService
import com.example.runningapp.Services.polyLine
import com.example.runningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.other.Constants.MAP_ZOOM
import com.example.runningapp.other.Constants.POLYLINE_WITHED
import com.example.runningapp.other.Constants.POLYLINE__COLOR
import com.example.runningapp.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*


@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {


    private val viewModel: MainViewModel by viewModels()
    private var map: GoogleMap? = null

    private var isTracking = false
    private var pathPoints = mutableListOf<polyLine>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnToggleRun.setOnClickListener {
            toggleRun()
        }
        mapView?.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
            addAllPolyLines()
        }
        subscribeToObservers()
    }


    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )

        }
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, {
            updateTracking(it)
        })
        TrackingService.pathPoint.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyLine()
            moveCameraToUser()
        })
    }

    private fun toggleRun() {
        if (isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking) {
            btnToggleRun.text = "Start"
            btnFinishRun.isVisible = true
        } else {
            btnToggleRun.text = "Stop"
            btnFinishRun.isVisible = false
        }
    }

    private fun addAllPolyLines() {
        for (polyLine in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE__COLOR)
                .width(POLYLINE_WITHED)
                .addAll(polyLine)
            map?.addPolyline(polylineOptions)


        }
    }

    private fun addLatestPolyLine() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {

            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE__COLOR)
                .width(POLYLINE_WITHED)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)

        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mapView?.onDestroy()
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }


}