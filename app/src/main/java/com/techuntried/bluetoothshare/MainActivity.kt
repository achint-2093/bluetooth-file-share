package com.techuntried.bluetoothshare

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

import com.techuntried.bluetoothshare.R
import com.techuntried.bluetoothshare.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()


    }
}