package com.example.any1.feature_login.presentation

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.any1.R
import com.example.any1.core.main.MainActivity

class Splash : AppCompatActivity() {
    lateinit var shp:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences2 = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(sharedPreferences2.getString("theme","")=="dark"){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        super.onCreate(savedInstanceState)
        shp = applicationContext.getSharedPreferences(packageName+"login", MODE_PRIVATE)
        val isLoggedIn: String = shp.getString("login", "").toString()
        setContentView(R.layout.activity_splash)
        if (isLoggedIn == "true") {
//            val intent = Intent(this@Splash, MainActivity::class.java)
//            startActivity(intent)
//            finish()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@Splash, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@Splash, LoginChoice::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        }
    }
}