package com.example.runningapp.ui.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.other.Constants.REQUEST_CODE_LOCATION_PERMISSIONS
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class RunFragment:Fragment(R.layout.fragment_run),EasyPermissions.PermissionCallbacks {

     private val viewModel: MainViewModel by viewModels()

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          requestPermissions()
          fab.setOnClickListener {
               findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
          }
     }

     private fun requestPermissions(){

          if (TrackingUtility.hasLocationPermissions(requireContext())){
               return
          }

          if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
               EasyPermissions.requestPermissions(
                    this,
                    "you need to accept location permissions to use this app.",
                    REQUEST_CODE_LOCATION_PERMISSIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
               )
          }else{
               EasyPermissions.requestPermissions(
                    this,
                    "you need to accept location permissions to use this app.",
                    REQUEST_CODE_LOCATION_PERMISSIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
               )
          }
     }

     override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
          Toast.makeText(requireContext(), "Accepted", Toast.LENGTH_SHORT).show()
     }

     override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
         if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
           AppSettingsDialog.Builder(this).build().show()
         }else{
              requestPermissions()
         }
     }


     override fun onRequestPermissionsResult(
          requestCode: Int,
          permissions: Array<out String>,
          grantResults: IntArray
     ) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults)
          EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
     }
}