package com.example.any1.feature_chat.presentation

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.*
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.core.widget.doAfterTextChanged
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.feature_chat.domain.adapter.ChatAdapter
import com.example.any1.feature_group.domain.vm.GroupInfoVM
import com.bumptech.glide.Glide
import com.example.any1.R
import com.example.any1.core.main.MainActivity
import com.example.any1.feature_chat.domain.interfaces.BasicClickListener
import com.example.any1.feature_chat.domain.model.ChatModel
import com.example.any1.feature_chat.domain.vm.ChatViewModel
import com.example.any1.feature_group.presentation.GroupInfo
import com.example.any1.feature_profile.ViewProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.lang.Runnable
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Chat.newInstance] factory method to
 * create an instance of this fragment.
 */
class Chat : AppCompatActivity() , BasicClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private var temporaryMessageList = ArrayList<ChatModel>()
    private var mediaRecorder : MediaRecorder? = null
    private lateinit var gcpic: CircularImageView
    private lateinit var videocall: ImageView
    private lateinit var mic : ImageView
    private lateinit var gallery: ImageView
    private lateinit var gif: ImageView
    private lateinit var backbutton: ImageView
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var camera: ImageView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var groupinfo: ImageView
    private lateinit var gctag : String
    private lateinit var grouptitle: TextView
    private lateinit var send: TextView
    private lateinit var editText: EditText
    private lateinit var smoothScroller: RecyclerView.SmoothScroller
    private var str = ""
    private lateinit var membersList : ArrayList<String>
    private  lateinit var groupInfoViewModel : GroupInfoVM
    private lateinit var viewModel: ChatViewModel
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        val themepreferences = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(themepreferences.getString("theme","")=="dark"){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_chat)
        val imageuri = intent.getStringExtra("imageurl")
        val gcname = intent.getStringExtra("gcname")
        gctag = intent.getStringExtra("gctag").toString()
        gcpic = findViewById(R.id.gcpic)
        grouptitle = findViewById(R.id.grouptitle)
        videocall = findViewById(R.id.videocall)
        groupinfo = findViewById(R.id.groupinfo)
        recyclerView = findViewById(R.id.groupchatrecyclerview)
        groupInfoViewModel = ViewModelProvider(this).get(GroupInfoVM::class.java)
        groupInfoViewModel.getGroupInfo(gctag)

        val emojiConfig = BundledEmojiCompatConfig(this)
        emojiConfig.setReplaceAll(true)
            .registerInitCallback(object : EmojiCompat.InitCallback() {
                override fun onInitialized() {
                }

                override fun onFailed(throwable: Throwable?) {
                }
            })
        EmojiCompat.init(emojiConfig)
//        groupInfoViewModel.liveGroupInfoModelList.observe(Groupinfo()){
//        }

//        groupInfoViewModel.liveGroupInfoModelList.observe(this){
//            membersList = it.membersList
//
//        }
        val linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true)
//        linearLayoutManager.stackFromEnd = true
//        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        adapter = ChatAdapter(this,this)
        groupinfo.setOnClickListener {
            val intent = Intent(this, GroupInfo::class.java)
            intent.putExtra("tag",gctag)
            intent.putExtra("chat","chat")
            intent.putExtra("gcname",gcname)
            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.anim_enter,
                R.anim.anim_exit
            ).toBundle()
            startActivity(intent,bndlAnimation)
        }
        backbutton = findViewById(R.id.chatbackbutton)
        gif = findViewById(R.id.gif)
        gallery = findViewById(R.id.gallery)
        smoothScroller = LinearSmoothScroller(this)
        onBackPressedDispatcher.onBackPressed()
        mic= findViewById(R.id.vn)
        editText = findViewById(R.id.gcedittext)
        camera= findViewById(R.id.chatcamera)
        send = findViewById(R.id.send)
        if(imageuri!="") Glide.with(this).load(imageuri).circleCrop().into(gcpic)
        grouptitle.text = gcname
        gif.setOnClickListener {  }
        gallery.setOnClickListener {  }
        camera.setOnClickListener {  }
        mic.setOnClickListener {  }
        editText.doAfterTextChanged{
                text: Editable? ->
            str= text.toString()
            if(str!=""){
                gif.visibility = View.INVISIBLE
                mic.visibility = View.INVISIBLE
                gallery.visibility = View.INVISIBLE
                send.visibility = View.VISIBLE
            }else{
                gif.visibility = View.VISIBLE
                mic.visibility = View.VISIBLE
                gallery.visibility = View.VISIBLE
                send.visibility = View.INVISIBLE
            }
        }
        send.setOnClickListener{
            sendMessage(str.trim())
            editText.setText("")
            str = ""
        }
        backbutton.setOnClickListener {
          slideleft()
        }
        viewModel = ViewModelProvider(this@Chat).get(ChatViewModel::class.java)
        viewModel.getMessages(gctag)
        viewModel.getGroupMessages.observe(this){
            if (it != null) {
                adapter.clearChatModelList()
                adapter.setChatModelList(it)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun sortMessagesByTime(arrayList: ArrayList<ChatModel>): List<ChatModel> {
        for (message in arrayList){
            val timeWithoutColon = message.time.replace(":","")
            message.time = timeWithoutColon
        }
        arrayList.sortBy { it.time.substring(it.time.length-2,it.time.length)}
        arrayList.sortBy { it.time.substring(2,4) }
        arrayList.sortBy { it.time.substring(0,2) }
        for (i in arrayList){
            val stringBuilder = StringBuilder(i.time)
            stringBuilder.insert(2,":")
            stringBuilder.insert(4,":")
        }
        val finalList : List<ChatModel> = arrayList.reversed()
        return finalList
    }

    private fun sendMessage(message : String){
        val formatter = SimpleDateFormat("hh:mm:ss")
        val date = Date(System.currentTimeMillis())
        val time = formatter.format(date)
        val hashmap = hashMapOf(
            "sender" to auth.currentUser!!.uid,
            "message" to message,
            "senderpfpuri" to getSharedPreferences(packageName+"user", MODE_PRIVATE).getString("imgurl",""),
            "timestamp" to FieldValue.serverTimestamp()
        )
        val formatter1= SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy")
        val formatter2 = SimpleDateFormat("dd.MM.yyyy")
        val date1 = formatter1.parse(date.toString())?.let { formatter2.format(it) }
        if (date1 != null) {
            firestore.collection("groups").document(gctag).collection("messages").document(date1).get().addOnSuccessListener {
                if(it.exists()){
                    firestore.collection("groups").document(gctag).collection("messages").document(date1).collection("messages").document(time).set(hashmap)
                }else{
                    firestore.collection("groups").document(gctag).collection("messages").document(date1).set(hashMapOf("timestamp" to FieldValue.serverTimestamp()))
                    firestore.collection("groups").document(gctag).collection("messages").document(date1).collection("messages").document(time).set(hashmap)
                }
            }
        }
    }

    override fun onStop() {
        viewModel.stopReceiveingMessages()
        super.onStop()
    }


    private fun slideleft(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val bndlAnimation = ActivityOptions.makeCustomAnimation(
//            applicationContext,
//        ).toBundle()
        startActivity(intent)
        overridePendingTransition(
            R.anim.slideleft2,
            R.anim.slideleft1
        )
    }


    private var playableSeconds = 0
    private val executorService = Executors.newSingleThreadExecutor()
    private var seconds = 0
    private var isRecording = false
    private var dummySeconds = 0
    @SuppressLint("RestrictedApi")
    private fun recordAudio(){
        if(!isRecording){
            isRecording = true
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mediaRecorder?.setOutputFile(getRecordingFilePath())
                }
                try {
                    prepare()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "prepare() failed")
                }
                val path = getRecordingFilePath()
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                start()
                runOnUiThread {
                    playableSeconds = 0
                    seconds = 0
                    dummySeconds = 0
                    runTimer()
                }
            }
        }else{
            executorService.execute {
                mediaRecorder!!.stop()
                mediaRecorder!!.release()
                mediaRecorder = null
                playableSeconds = seconds
                dummySeconds = seconds
                seconds = 0
                isRecording = false
            }
        }
    }

    private var isPlaying = false
    private fun runTimer(){
        Handler(Looper.getMainLooper()).post(Runnable {
            val minutes = (seconds%3600)/60
            val secs = seconds%60
            val time = String.format(Locale.getDefault(),"%02d:%02d",minutes,secs)
            if(isRecording || (isPlaying && playableSeconds != -1)){
                seconds++
                playableSeconds--
                if(playableSeconds == -1 && isPlaying){
                    mediaRecorder?.stop()
                    mediaRecorder?.release()
                    mediaRecorder = null
//                    mediaRecorder = MediaPlayer()
                }
            }
        })
    }

    private fun getRecordingFilePath(): String {
        val contextWrapper = ContextWrapper(applicationContext)
        val music =  contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file  = File(music,"testfile.mp3")
        return file.path
    }

    private fun stopRecording() {
        mediaRecorder.apply {
            this?.stop()
            this?.release()
        }
        mediaRecorder = null
    }


    private val askMicrophonePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            recordAudio()
        } else {
            Toast.makeText(
                this,
                "We require permission to access microphone of your device in order to set your profile photo." +
                        " To setup your profile photo, go to Settings-> Apps -> Any1 -> Permissions -> grant microphone permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getMicrophonePermission(){
        Dexter.withContext(this).withPermission(android.Manifest.permission.RECORD_AUDIO).withListener(
            object : PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    recordAudio()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                   askMicrophonePermission.launch(android.Manifest.permission.RECORD_AUDIO)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }
            }
        ).check()
    }
    override fun onClick() {
        val intent = Intent(this, ViewProfile::class.java)
        val bndlAnimation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.animenterslow,
            R.anim.animexitslow
        ).toBundle()
        startActivity(intent,bndlAnimation)
    }
}