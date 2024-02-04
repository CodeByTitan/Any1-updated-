package com.example.any1.feature_login.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.any1.R

class EmailRecovery : AppCompatActivity() {
    var censorusername = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emailrecovery)
        val ss:String = intent.getStringExtra("email").toString()
        val tv = findViewById<TextView>(R.id.textView3)
        var tempostring = ""
        if(intent.getStringExtra("username")=="username"){
            censorusername = true
            val indexofatsym = ss.indexOf("@")
            tempostring = ss.replaceRange(1,indexofatsym-1,"*".repeat(indexofatsym-2))
        }
        val backbutton = findViewById<ImageView>(R.id.backbutton)
        backbutton.setOnClickListener{slideleft()}
        window.statusBarColor = ContextCompat.getColor(this, R.color.app_background)
        if(!censorusername){
            tv.text = getString(R.string.forgotemail,ss)
        }else{
            tv.text = getString(R.string.forgotemail,tempostring)
        }
//        val sharedPreferences2 = getSharedPreferences("theme", MODE_PRIVATE)
//        if(sharedPreferences2.getString("theme","")=="dark"){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }
        val sharedPreferences2 = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(sharedPreferences2.getString("theme","")=="dark"){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        val openmail = findViewById<Button>(R.id.openmail)
        openmail.setOnClickListener {
//            next.visibility = View.VISIBLE
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
//                viewModel.isVerified.value = true
        }

    }

    private fun slideleft(){
        val intent = Intent(this, ForgotPass::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slideleft2,
            R.anim.slideleft1
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        slideleft()
    }

}