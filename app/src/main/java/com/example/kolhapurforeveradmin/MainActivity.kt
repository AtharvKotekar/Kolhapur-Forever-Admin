package com.example.kolhapurforeveradmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var listner: NavController.OnDestinationChangedListener
    lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController,appBarConfiguration)


        listner = NavController.OnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.addCompitionFragment){
                supportActionBar?.setTitle("Add New Compitition")
            }else if(destination.id == R.id.editCompitionFragment){
                supportActionBar?.setTitle("Edit Compititions")
            }else if(destination.id == R.id.addSponsorFragment){
                supportActionBar?.setTitle("Add New Sponsor")
            }else if(destination.id == R.id.editSponsorFragment){
                supportActionBar?.setTitle("Edit Sponsors")
            }
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
