package com.sessac.myaitrip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

//class MainActivity : ViewBindingBaseActivity<ActivityMainBinding>({ActivityMainBinding.inflate(it)}) {
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        navController = navHostFragment.navController

        val bottomNav: BottomNavigationView = findViewById(R.id.bnv_home)

        bottomNav.setupWithNavController(navController)

//        binding.bnvHome.also { bottomNav ->
//            bottomNav.setupWithNavController(navController)
//        }
    }
}