@file:Suppress("DEPRECATION")

package com.example.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var narController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // khởi tạo
        val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        narController = navHostFrag.navController
    }

    override fun onBackPressed() {
        // kiểm tra ngăn xếp
        if (supportFragmentManager.backStackEntryCount > 0 ){
            super.onBackPressed()
        }else{
        // so sánh id với id homeFragment
            if (narController.currentDestination?.id == R.id.homeFragment){
                moveTaskToBack(true)
            }else{
                super.onBackPressed()
            }
        }

    }
}


