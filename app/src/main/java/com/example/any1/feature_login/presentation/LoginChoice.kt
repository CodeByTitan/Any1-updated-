package com.example.any1.feature_login.presentation

import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.any1.R
import com.example.any1.feature_signup.presentation.Setup
import com.google.firebase.auth.FirebaseAuth


class LoginChoice : AppCompatActivity() {
    // ...
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    private lateinit var auth: FirebaseAuth
    private lateinit var videoView:VideoView
    private var stopPosition:Int = 0
    private lateinit var shp:SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addorlogin)
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        val addme = findViewById<Button>(R.id.addme)
        val login = findViewById<Button>(R.id.login)
        shp = applicationContext.getSharedPreferences(packageName+"login", MODE_PRIVATE)
        editor = shp.edit()
        auth = FirebaseAuth.getInstance()
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        videoView = findViewById<VideoView>(R.id.videoview)
        val uri = Uri.parse("android.resource://com.example.any1/"+ R.raw.loginvideo1)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()
        videoView.setOnPreparedListener(OnPreparedListener { mp -> mp.isLooping = true })
        videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);

//        createRequest()
        addme.setOnClickListener {
            startActivity(Intent(this, Setup::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
//            resultLauncher.launch(Intent(mGoogleSignInClient.signInIntent))
        }

        login.setOnClickListener {
            startActivity(Intent(this, Login::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
    }


    override fun onPause() {
        super.onPause()
        stopPosition = videoView.getCurrentPosition() //stopPosition is an int
        videoView.pause()
    }

    override fun onResume() {
        super.onResume()
        videoView.seekTo(stopPosition)
        videoView.start() //Or use resume() if it doesn't work. I'm not sure
    }

}