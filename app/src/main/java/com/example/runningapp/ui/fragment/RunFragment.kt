package com.example.runningapp.ui.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.runningapp.R
import com.example.runningapp.adapters.RunAdapter
import com.example.runningapp.other.Constants.REQUEST_CODE_LOCATION_PERMISSIONS
import com.example.runningapp.other.SortType
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


@AndroidEntryPoint
class RunFragment:Fragment(R.layout.fragment_run),EasyPermissions.PermissionCallbacks {

     private val viewModel: MainViewModel by viewModels()
     @Inject
     lateinit var glide: RequestManager


     @Inject
     lateinit var runAdapter: RunAdapter

     @RequiresApi(Build.VERSION_CODES.Q)
     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)

          requestPermissions()
          setupRecyclerView()


          when(viewModel.sortType){
               SortType.DATE->spFilter.setSelection(0)
               SortType.RUNNING_TIME->spFilter.setSelection(1)
               SortType.DISTANCE->spFilter.setSelection(2)
               SortType.AVG_SPEED->spFilter.setSelection(3)
               SortType.CALORIES_BURNED->spFilter.setSelection(4)
          }

          spFilter.onItemSelectedListener= object :AdapterView.OnItemSelectedListener{
               override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    when(pos){
                         0-> viewModel.sortRuns(SortType.DATE)
                         1-> viewModel.sortRuns(SortType.RUNNING_TIME)
                         2-> viewModel.sortRuns(SortType.DISTANCE)
                         3-> viewModel.sortRuns(SortType.AVG_SPEED)
                         4-> viewModel.sortRuns(SortType.CALORIES_BURNED)
                    }
               }

               override fun onNothingSelected(p0: AdapterView<*>?) {
               }

          }


          viewModel.runs.observe(viewLifecycleOwner,{
               runAdapter.submitList(it)
          })



          fab.setOnClickListener {
               findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
          }

     }

     private fun setupRecyclerView()=rvRuns.apply {
          adapter=runAdapter
          layoutManager=LinearLayoutManager(requireContext())
     }
     @RequiresApi(Build.VERSION_CODES.Q)
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

     @RequiresApi(Build.VERSION_CODES.Q)
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