package com.example.messagingappmv

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.messagingappmv.databinding.ActivityMainBinding
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage
import kotlinx.android.synthetic.main.list_item_room_users_contact.view.*

class MainActivity : AppCompatActivity(), DrawerMenuLocker {


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout


        val navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun setDrawerLocked(shouldLock: Boolean, context: Context) {
        if(shouldLock){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }else{
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            val headerText = binding.navView.getHeaderView(0).user_name
            headerText.text = TokenStorage.load(context).username
        }
    }
}
