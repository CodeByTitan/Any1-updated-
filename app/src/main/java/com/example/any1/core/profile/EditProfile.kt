package com.example.any1.core.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.any1.R
import com.example.any1.core.main.MainActivity

class EditProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)
    }

    private fun slideleft(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("gotoprofilefragment", "gotoprofilefragment")
        startActivity(intent)
        overridePendingTransition(
            R.anim.slideleft2slow,
            R.anim.slideleftslow
        )
    }
}
