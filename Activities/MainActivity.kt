package com.example.yashwanth.echo.Activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.yashwanth.echo.Fragments.*
import com.example.yashwanth.echo.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    object Staticated{
        val mainScreenFragment= MainScreenFragment()
        val favouritesFragment= FavouritesFragment()
        var notificationManager:NotificationManager?=null

    }
    var trackNotificationBuilder:Notification?=null


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragment,Staticated.mainScreenFragment)
                .addToBackStack("MainScreenFragment")
                .commit()

        var intent= Intent(this@MainActivity,MainActivity::class.java)
        val pIntent=PendingIntent.getActivity(this@MainActivity,System.currentTimeMillis().toInt(),
                intent,0)
        trackNotificationBuilder=Notification.Builder(this@MainActivity)
                .setContentTitle("Now Playing")
                .setContentText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                .setSmallIcon(R.drawable.echo_logo)
                .setContentIntent(pIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build()
        Staticated.notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    }

    override fun onDestroy() {
        super.onDestroy()
        try{
            Staticated.notificationManager?.cancel(1978)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        try{
            Staticated.notificationManager?.cancel(1978)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun onResume() {
        super.onResume()
        try{
            Staticated.notificationManager?.cancel(1978)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun onStop() {
        super.onStop()
        try{
            if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying){
                Staticated.notificationManager?.notify(1978,trackNotificationBuilder)
            }

        }catch(e:Exception){
         e.printStackTrace()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

            R.id.nav_Songs -> {


                this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,Staticated.mainScreenFragment)
                        .addToBackStack("MainScreenFragment")
                        .commit()


            }
            R.id.nav_aboutUs -> {
                val aboutUsFragment= AboutUsFragment()
                this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,aboutUsFragment)
                        .commit()


            }
            R.id.nav_settings -> {
                val settingsFragment= SettingsFragment()
                this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,settingsFragment)
                        .commit()


            }
            R.id.nav_favourites -> {

                this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,Staticated.favouritesFragment)
                        .addToBackStack("FavouritesFragment")
                        .commit()



            }

            
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
