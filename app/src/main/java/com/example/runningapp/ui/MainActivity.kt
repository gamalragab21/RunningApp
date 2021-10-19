package com.example.runningapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runningapp.R
import com.example.runningapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

          navigateToTrackingFragmentIfNeeded(intent)


        setSupportActionBar(toolbar)

        // her bottomNavigation is visible in all time ( means in all fragments )
        bottomNavigationView.setupWithNavController(
            navHostFragment.findNavController()
        )

        // to hide or show bottomNavigation in some Fragments

        navHostFragment.findNavController().addOnDestinationChangedListener { controller, destination, arguments ->

            when(destination.id){
                R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment->{
                    bottomNavigationView.isVisible=true
                }
                R.id.trackingFragment,R.id.setupFragment->{
                    bottomNavigationView.isVisible=false
                }
                else ->  bottomNavigationView.isVisible=false

            }
        }

    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if (intent?.action==ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global__trackingFragment)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }
}