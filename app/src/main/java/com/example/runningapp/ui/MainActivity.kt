package com.example.runningapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runningapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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
}