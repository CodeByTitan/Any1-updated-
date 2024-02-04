package com.example.any1.core.settings

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.any1.feature_signup.presentation.Setup
import com.example.any1.R
import com.example.any1.core.main.MainActivity
import com.example.any1.databinding.ActivitySettingsBinding
import com.example.any1.feature_login.presentation.Login
import com.example.any1.feature_login.presentation.LoginChoice
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import kotlin.collections.ArrayList


class Settings : AppCompatActivity() {
    private lateinit var view: ActivitySettingsBinding
    private lateinit var addedusernames : MutableMap<Int,String>
    override fun onCreate(savedInstanceState: Bundle?) {
        view = ActivitySettingsBinding.inflate(layoutInflater)
        val sharedPreferences = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(sharedPreferences.getString("theme","")=="dark"){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES;
//            menu?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.blackhome);
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
//            menu?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.bnaviconbg);
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        view.addaccount.setOnClickListener {
            showAddAccountDialog()
        }
        val addaccountpreferences = getSharedPreferences(packageName+"addedaccounts", MODE_PRIVATE)
        var addedaccounts = 0
        if(addaccountpreferences.getString("count","")!=""){
            addedaccounts = addaccountpreferences.getString("count","")!!.toInt()
        }
        val userpref = getSharedPreferences(packageName+"user", MODE_PRIVATE)
        val username = userpref.getString("username","")
        if(addedaccounts>1){
            view.signout.text = "Sign out $username"
           view.signoutallaccounts.visibility = View.VISIBLE
        }else{
            view.signout.text = "Sign out"
            view.signoutallaccounts.visibility = View.GONE
        }
        view.settingstoolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            if(sharedPreferences.getString("theme","")=="dark"){
                intent.putExtra("theme","dark")
            }else{
                intent.putExtra("theme","light")
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        view.changetheme.isChecked = sharedPreferences.getString("theme","")== "dark"
        view.changetheme.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{ compoundButton, ischecked->
            if(ischecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPreferences.edit().putString("theme","dark").apply()

            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPreferences.edit().putString("theme","light").apply()
            }
        })

        view.signoutallaccounts.setOnClickListener {
            checkSaveInfoBoolForAllAccounts()
        }
        view.signout.setOnClickListener {
            checkSaveInfoBool()
        }
    }

    private fun checkSaveInfoBool(){
        val savedaccounts = getSharedPreferences(packageName+"savedaccounts", MODE_PRIVATE)
        var numberofaccountssaved = 0
        if(savedaccounts.getString("count","")!=""){
            numberofaccountssaved = savedaccounts.getString("count","")!!.toInt()
        }
        var username = ""
        val arrayList = ArrayList<String>()
        for(i in 1..numberofaccountssaved) {
            username = savedaccounts.getString("username$i", "").toString()
            arrayList.add(username)
        }
        val sharedPreferences = getSharedPreferences(packageName+"user", MODE_PRIVATE)
        if(arrayList.contains(sharedPreferences.getString("username",""))){
            showSignOutDialog()
        }else{
            showSaveInfoDialog(numberofaccountssaved)
        }

    }
    private fun checkSaveInfoBoolForAllAccounts(){
        val savedaccounts = getSharedPreferences(packageName+"savedaccounts", MODE_PRIVATE)
        var numberofaccountssaved = 0
        if(savedaccounts.getString("count","")!=""){
            numberofaccountssaved = savedaccounts.getString("count","")!!.toInt()
        }
        var username = ""
        var addedusername = ""
        val addedaccounts = getSharedPreferences(packageName+"addedaccounts", MODE_PRIVATE)
        var addedaccountscount = 0
        if(addedaccounts.getString("count","")!="")addedaccountscount = addedaccounts.getString("count","")!!.toInt()
        val addedaccountlist = ArrayList<String>()
        for(i in 1..addedaccountscount){
            addedusername = addedaccounts.getString("username$i","").toString()
            addedaccountlist.add(addedusername)
        }
        val arrayList = ArrayList<String>()
        for(i in 1..numberofaccountssaved) {
            username = savedaccounts.getString("username$i", "").toString()
            arrayList.add(username)
        }
        if(arrayList.containsAll(addedaccountlist)){
            showSignOutDialog()
        } else{
            showSaveInfoDialogForAllAccounts(numberofaccountssaved)
        }
    }

    private fun showSaveInfoDialog( numberofaccountssaved : Int){
        var numberofaccountssaved = numberofaccountssaved
        val savedaccounts = getSharedPreferences(packageName+"savedaccounts", MODE_PRIVATE)
        val saveinfodialog = Dialog(this)
        val sharedPreferences = getSharedPreferences(packageName+"user", MODE_PRIVATE)
        saveinfodialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        saveinfodialog.setCancelable(false)
        saveinfodialog.setContentView(R.layout.saveinfo)
        saveinfodialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val saveinfo = saveinfodialog.findViewById<TextView>(R.id.saveinfo)
        val notnow = saveinfodialog.findViewById<TextView>(R.id.notnow)
        val editor = savedaccounts.edit()
        saveinfo.setOnClickListener{
//                    Toast.makeText(this, arrayList.toString(), Toast.LENGTH_SHORT).show()
//                    Toast.makeText(this,sharedPreferences.getString("username","") , Toast.LENGTH_SHORT).show()
//                    if(arrayList.contains(sharedPreferences.getString("username",""))){
//                        showSignOutDialog()
//                    }else{
            val accountnumber = numberofaccountssaved+1
            editor.putString("count",accountnumber.toString()).apply()
            editor.putString("username$accountnumber",sharedPreferences.getString("username",""))
            editor.putString("saveinfo$accountnumber","true").apply()
            editor.putString("email$accountnumber",sharedPreferences.getString("email",""))
            editor.putString("imgurl$accountnumber",sharedPreferences.getString("imgurl",""))
            editor.putString("displayname$accountnumber",sharedPreferences.getString("displayname",""))
            editor.putString("age$accountnumber",sharedPreferences.getString("age",""))
            editor.putString("gender$accountnumber",sharedPreferences.getString("gender",""))
            editor.putString("password$accountnumber",sharedPreferences.getString("password",""))
            editor.apply()
            saveinfodialog.dismiss()
            showSignOutDialog()
//                    }
//                    Toast.makeText(this, sharedPreferences.getString("username","").toString(), Toast.LENGTH_SHORT).show()
//                    Toast.makeText(this, accountnumber.toString(), Toast.LENGTH_SHORT).show()
//                    Toast.makeText(this, savedaccounts.getString("username1",""), Toast.LENGTH_SHORT).show()
        }
        notnow.setOnClickListener{
            if(savedaccounts.getString("count","")!=""){
                numberofaccountssaved = savedaccounts.getString("count","")!!.toInt()
            }
            val accountnumber = numberofaccountssaved+1
            editor.putString("saveinfo$accountnumber","false").apply()
            saveinfodialog.dismiss()
            showSignOutDialog()
        }
        saveinfodialog.setOnKeyListener(DialogInterface.OnKeyListener { arg0, keyCode, event -> // TODO Auto-generated method stub
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                saveinfodialog.dismiss()
            }
            true
        })
        saveinfodialog.show()
    }

    private fun showSaveInfoDialogForAllAccounts( numberofaccountssaved : Int){
        var numberofaccountssaved = numberofaccountssaved
        val savedaccounts = getSharedPreferences(packageName+"savedaccounts", MODE_PRIVATE)
        val saveinfodialog = Dialog(this)
        saveinfodialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        saveinfodialog.setCancelable(false)
        saveinfodialog.setContentView(R.layout.saveinfoforallaccountsdialog)
        saveinfodialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val saveinfo = saveinfodialog.findViewById<TextView>(R.id.saveinfo)
        val notnow = saveinfodialog.findViewById<TextView>(R.id.notnow)
        val editor = savedaccounts.edit()
        val addedaccounts = getSharedPreferences(packageName+"addedaccounts", MODE_PRIVATE)
        var addedaccountscount = 0
        var savedaccountscount= 0
        var username = ""
        var savedusername = ""
        val savedusernames = ArrayList<String>()
        saveinfo.setOnClickListener{
            if(addedaccounts.getString("count","")!=""){
                addedaccountscount = addedaccounts.getString("count","")!!.toInt()
            }
            addedusernames = mutableMapOf()
            for(i in 1..addedaccountscount){
                username = addedaccounts.getString("username$i","").toString()
                addedusernames.put(i,username)
            }
            if(savedaccounts.getString("count","")!=""){
                savedaccountscount= savedaccounts.getString("count","")!!.toInt()
            }
            if(savedaccountscount!=0){
                for(i in 1..savedaccountscount){
                    savedusername = savedaccounts.getString("username$i","").toString()
                    savedusernames.add(savedusername)
                }
                for(i in 1..addedusernames.size){
                    if(!savedusernames.contains(addedusernames.get(i))){
                        shareAddedAccountInfoWithSavedAccount(i)
                    }
                }
            }else{
                for(i in 1..addedusernames.size){
                    shareAddedAccountInfoWithSavedAccount(i)
                }
            }
            saveinfodialog.dismiss()
            showSignOutDialog()
//                    }

        }
        notnow.setOnClickListener{
            if(savedaccounts.getString("count","")!=""){
                numberofaccountssaved = savedaccounts.getString("count","")!!.toInt()
            }
            val accountnumber = numberofaccountssaved+1
            editor.putString("saveinfo$accountnumber","false").apply()
            saveinfodialog.dismiss()
            showSignOutDialog()
        }
        saveinfodialog.setOnKeyListener(DialogInterface.OnKeyListener { arg0, keyCode, event -> // TODO Auto-generated method stub
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                saveinfodialog.dismiss()
            }
            true
        })
        saveinfodialog.show()
    }

    private fun shareAddedAccountInfoWithSavedAccount(get: Int) {
        val savedaccounts = getSharedPreferences(packageName+"savedaccounts", MODE_PRIVATE)
        val addedaccoungts = getSharedPreferences(packageName+"addedaccounts", MODE_PRIVATE)
        val savededitor = savedaccounts.edit()
        var savedaccountscount = 0
        if(savedaccounts.getString("count","")!=""){
            savedaccountscount = savedaccounts.getString("count","")!!.toInt()
        }
        val accountnumber = savedaccountscount+1
        savededitor.putString("username$accountnumber",addedaccoungts.getString("username$get",""))
        savededitor.putString("age$accountnumber",addedaccoungts.getString("age$get",""))
        savededitor.putString("displayname$accountnumber",addedaccoungts.getString("displayname$get",""))
        savededitor.putString("imgurl$accountnumber",addedaccoungts.getString("imgurl$get",""))
        savededitor.putString("gender$accountnumber",addedaccoungts.getString("gender$get",""))
        savededitor.putString("email$accountnumber",addedaccoungts.getString("email$get",""))
        savededitor.putString("password$accountnumber",addedaccoungts.getString("password$get",""))
        savededitor.putString("count",accountnumber.toString())
        savededitor.putString("saveinfo$accountnumber",addedaccoungts.getString("username$get",""))
        savededitor.apply()
    }

    private fun showSignOutDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.signout)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val signout = dialog.findViewById<TextView>(R.id.signoutsettings)
        val cancel = dialog.findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener { dialog.dismiss() }
        dialog.setOnKeyListener(DialogInterface.OnKeyListener { arg0, keyCode, event -> // TODO Auto-generated method stub
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss()
            }
            true
        })
        signout.setOnClickListener{
            dialog.dismiss()
            val dialog1 = Dialog(this)
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog1.setContentView(R.layout.signingin)
            val text = dialog1.findViewById<TextView>(R.id.signintext)
            text.text = "Signing Out...."
            dialog1.show()
            val addaccountpreferences = getSharedPreferences(packageName+"addedaccounts", MODE_PRIVATE)
//            addaccountpreferences.edit().clear().apply()
            var numberofaddedaccounts = 0
            if(addaccountpreferences.getString("count","")!=""){
                numberofaddedaccounts = addaccountpreferences.getString("count","")!!.toInt()
            }
            val userpref = getSharedPreferences(packageName+"user", MODE_PRIVATE)
            val editor = addaccountpreferences.edit()
            for (i in 1..numberofaddedaccounts){
                if(userpref.getString("username","") == addaccountpreferences.getString("username$i","")){
                    editor.putString("count",(numberofaddedaccounts-1).toString()).apply()
                    editor.putString("username$i","")
                    editor.putString("email$i","")
                    editor.putString("imgurl$i","")
                    editor.putString("displayname$i","")
                    editor.putString("age$i","")
                    editor.putString("gender$i","")
                    editor.putString("password$i","")
                    editor.apply()
                }
            }
            FirebaseAuth.getInstance().signOut()
            val sharedPreferences = getSharedPreferences(
                packageName+"login",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor2 = sharedPreferences.edit()
            editor2.putString("login", "false")
            editor2.apply()
            val intent = Intent(this, LoginChoice::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
            dialog1.dismiss()
            addaccountpreferences.edit().clear().apply()
            startActivity(intent)
            finish()
        }
        dialog.show()
    }
    private fun showAddAccountDialog(){
        val sharedPreferences = getSharedPreferences(packageName+"addedaccounts", MODE_PRIVATE)
        var numberofaddedaccounts = 0
        if(sharedPreferences.getString("count","")!=""){
            numberofaddedaccounts = sharedPreferences.getString("count","")!!.toInt()}
        if(numberofaddedaccounts == 0) {
            val accountnumber = numberofaddedaccounts + 1
            val editor = sharedPreferences.edit()
            val sp = getSharedPreferences(packageName+"user", MODE_PRIVATE)
            editor.putString("count", accountnumber.toString()).apply()
            editor.putString("username$accountnumber", sp.getString("username", ""))
            editor.putString("email$accountnumber", sp.getString("email", ""))
            editor.putString("imgurl$accountnumber", sp.getString("imgurl", ""))
            editor.putString("displayname$accountnumber", sp.getString("displayname", ""))
            editor.putString("age$accountnumber", sp.getString("age", ""))
            editor.putString("gender$accountnumber", sp.getString("gender", ""))
            editor.putString("password$accountnumber", sp.getString("password", ""))
            editor.apply()
        }
        val addaccountdialog = BottomSheetDialog(this,R.style.BottomSheetDialog)
        addaccountdialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addaccountdialog.setContentView(R.layout.addaccountdialog)
        addaccountdialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addaccountdialog.window!!.attributes.windowAnimations = R.style.Dialoganimation
        addaccountdialog.window!!.setGravity(Gravity.BOTTOM)
        val button = findViewById<MaterialButton>(R.id.logintoexistingaccount)
        val createnewaccount = findViewById<TextView>(R.id.createnewaccount)
        button.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            intent.putExtra("add","add")
            startActivity(intent)
        }
        createnewaccount.setOnClickListener {
            val intent = Intent(this, Setup::class.java)
            intent.putExtra("add","add")
            startActivity(intent)
        }
        addaccountdialog.show()
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        val sharedPreferences = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(sharedPreferences.getString("theme","")=="dark"){
            intent.putExtra("theme","dark")
        }else{
            intent.putExtra("theme","light")
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        finish()
        super.onBackPressed()
    }
}