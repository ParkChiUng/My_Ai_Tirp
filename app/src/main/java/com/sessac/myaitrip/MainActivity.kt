package com.sessac.myaitrip

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sessac.myaitrip.databinding.ActivityMainBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseActivity

class MainActivity : ViewBindingBaseActivity<ActivityMainBinding>({ActivityMainBinding.inflate(it)}) {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        navController = navHostFragment.navController

        binding.bnvHome.also { bottomNav ->
            bottomNav.setupWithNavController(navController)
        }
    }
}