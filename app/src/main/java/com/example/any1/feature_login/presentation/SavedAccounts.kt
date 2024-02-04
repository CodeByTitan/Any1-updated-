package com.example.any1.feature_login.presentation

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.R
import com.example.any1.core.main.MainActivity
import com.example.any1.feature_group.domain.vm.GroupVM
import com.example.any1.feature_login.domain.adapters.SavedAccountsAdapter
import com.example.any1.feature_login.domain.interfaces.AccountClickListener
import com.example.any1.feature_login.domain.interfaces.RemoveClickListener
import com.example.any1.feature_login.domain.model.SavedAccountModel
import com.google.firebase.auth.FirebaseAuth

class SavedAccounts : AppCompatActivity(), AccountClickListener, RemoveClickListener {
    val modelList = ArrayList<SavedAccountModel>()
    private lateinit var email : String
    private val auth = FirebaseAuth.getInstance()
    private lateinit var password : String
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences2 = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(sharedPreferences2.getString("theme","")=="dark"){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savedaccounts)
        val sharedPreferences = getSharedPreferences(packageName+"savedaccounts", MODE_PRIVATE)
        val numberofaccounts = sharedPreferences.getString("count","")!!.toInt()
        val arrow = findViewById<ImageView>(R.id.arrowup)
        for(i in 1..numberofaccounts){
            val j =i-1
//            Toast.makeText(this, numberofaccounts.toString(), Toast.LENGTH_SHORT).show()
            val username = sharedPreferences.getString("username$i","").toString()
//            Toast.makeText(this, username, Toast.LENGTH_SHORT).show()
            val imageurl = sharedPreferences.getString("imgurl$i","").toString()
            val displayname = sharedPreferences.getString("displayname$i","").toString()
            val age = sharedPreferences.getString("age$i","").toString()
            val gender = sharedPreferences.getString("gender$i","").toString()
            email = sharedPreferences.getString("email$i","").toString()
            password = sharedPreferences.getString("password$i","").toString()
            val model = SavedAccountModel(username,imageurl,email,password,displayname,age,gender)
            modelList.add(index = j,model)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.savedaccountrecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SavedAccountsAdapter(this,this,this)
        adapter.setModelList(modelList)
        recyclerView.adapter = adapter
        val animation = AnimationUtils.loadAnimation(this,R.anim.arrowanimation)
        val animationreverse = AnimationUtils.loadAnimation(this,R.anim.arrowanimationreverse)
        arrow.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                arrow.startAnimation(animationreverse)
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
        animationreverse.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                arrow.startAnimation(animation)
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })

        arrow.setOnClickListener {
            startActivity(Intent(this, Login::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            overridePendingTransition(R.anim.slide_in_top,R.anim.slideoutbottom)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_top,R.anim.slideoutbottom)
    }

    override fun onAccountClick(savedAccountModel: SavedAccountModel) {
//        Toast.makeText(this, savedAccountModel.username, Toast.LENGTH_SHORT).show()
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.signingin)
        dialog.setCancelable(false)
        dialog.show()
        if(savedAccountModel.email != "" && savedAccountModel.password!= ""){
            auth.signInWithEmailAndPassword(savedAccountModel.email, savedAccountModel.password).addOnSuccessListener {
                val sharedPreferences = getSharedPreferences(
                    packageName+"login",
                    MODE_PRIVATE
                )
                sharedPreferences.edit().putString("login", "true")
                    .apply()
                if(intent.getStringExtra("add")=="add"){
                    val sharedPreferences = getSharedPreferences(packageName+"addedaccounts", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    var numberofaddedaccounts = 0
                    if(sharedPreferences.getString("count","")!="") numberofaddedaccounts = sharedPreferences.getString("count","")!!.toInt()
                    var isAccountAdded = false
                    if(numberofaddedaccounts!=0){
                        for(i in 1..numberofaddedaccounts){
                            val username = sharedPreferences.getString("username$i","")
                            if(username == savedAccountModel.username){
                                isAccountAdded = true
                            }
                        }
                    }
                    if(!isAccountAdded) {
                        val accountnumber = numberofaddedaccounts + 1
                        editor.putString("count", accountnumber.toString()).apply()
                        editor.putString("username$accountnumber", savedAccountModel.username)
                        editor.putString("email$accountnumber", savedAccountModel.email)
                        editor.putString("imgurl$accountnumber", savedAccountModel.imageurl)
                        editor.putString("displayname$accountnumber", savedAccountModel.displayname)
                        editor.putString("age$accountnumber", savedAccountModel.age)
                        editor.putString("gender$accountnumber", savedAccountModel.gender)
                        editor.putString("password$accountnumber", savedAccountModel.password)
                        editor.apply()
                        val viewModel = ViewModelProvider(this).get(GroupVM::class.java)
                        dialog.dismiss()
                        viewModel.updateAuthId(auth.currentUser!!.uid)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }else{
                        dialog.dismiss()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }else{
                    dialog.dismiss()
                    startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION))
                }
                val userpref = getSharedPreferences(packageName+"user", MODE_PRIVATE)
                val editor = userpref.edit()
                editor.putString("username",savedAccountModel.username)
                editor.putString("displayname",savedAccountModel.displayname)
                editor.putString("imgurl",savedAccountModel.imageurl)
                editor.putString("age",savedAccountModel.age)
                editor.putString("gender",savedAccountModel.gender)
                editor.putString("email",savedAccountModel.email)
                editor.putString("password",savedAccountModel.password)
                editor.apply()

            }
        }else{
            dialog.dismiss()
            Toast.makeText(this, "Email or Password is null", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTrashClicked() {
        startActivity(Intent(this, Login::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        overridePendingTransition(R.anim.slide_in_top,R.anim.slideoutbottom)
    }
}
