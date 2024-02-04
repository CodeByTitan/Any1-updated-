package com.example.any1.feature_login.presentation

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.any1.R
import com.example.any1.core.main.MainActivity
import com.example.any1.feature_group.domain.vm.GroupVM
import com.example.any1.feature_login.domain.vm.AuthVM
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream


class Login : AppCompatActivity() {

    private lateinit var loginbutton : Button
    private lateinit var loginprogress : ProgressBar
    private var usernamestr = ""
    private var passwordstr = ""
    private lateinit var animation: Animation
    private lateinit var animation2: Animation
    private lateinit var arrow : ImageView
    private lateinit var username : EditText
    private lateinit var password : EditText
    private lateinit var uninput : TextInputLayout
    private lateinit var passinput : TextInputLayout
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences2 = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        if(sharedPreferences2.getString("theme","")=="dark"){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        username = findViewById(R.id.loginusername)
        val savedaccounts = getSharedPreferences(packageName+"savedaccounts", MODE_PRIVATE)
        password = findViewById<EditText>(R.id.loginpassword)
        val gethelp = findViewById<TextView>(R.id.gethelp)
        val text : TextView = findViewById(R.id.text)
        loginbutton = findViewById<Button>(R.id.loginbutton)
        arrow = findViewById<ImageView>(R.id.arrow)
        val numberofsavedaccounts = savedaccounts.getString("count","")
        if(intent.getStringExtra("email")!="") {
            username.setText(intent.getStringExtra("email"))
            usernamestr = username.text.toString()
        }
        animation = AnimationUtils.loadAnimation(this,R.anim.bouncetestanimation)
        animation2 = AnimationUtils.loadAnimation(this,R.anim.sampleanimation)
        arrow.setOnClickListener {
            val intent1 = Intent(this, SavedAccounts::class.java)
            if(intent.getStringExtra("add")=="add")intent1.putExtra("add","add")
            startActivity(intent1)
            overridePendingTransition(R.anim.slide_in_bottom,R.anim.slide_out_top)
        }
        if(numberofsavedaccounts=="" || numberofsavedaccounts=="0"){
            arrow.visibility = View.INVISIBLE
            text.visibility = View.INVISIBLE
        }else{
            arrow.visibility = View.VISIBLE
            text.visibility = View.VISIBLE
            animateArrow()
        }
        if(passwordstr == "" || usernamestr ==""){
            loginbutton.isEnabled = false
            loginbutton.alpha = 0.5f
        }else {loginbutton.isEnabled = true
            loginbutton.alpha = 1f
        }
        username.viewTreeObserver.addOnGlobalLayoutListener(OnGlobalLayoutListener {
            val r = Rect()
            arrow.getWindowVisibleDisplayFrame(r)
            val heightDiff: Int = arrow.rootView.height - (r.bottom - r.top)
            if (heightDiff > 100) {
                arrow.visibility = View.GONE
                text.visibility = View.GONE
                arrow.clearAnimation()
            } else {
                if(numberofsavedaccounts=="" || numberofsavedaccounts=="0"){
                    arrow.visibility = View.INVISIBLE
                    text.visibility = View.INVISIBLE
                }else{
                    arrow.visibility = View.VISIBLE
                    text.visibility = View.VISIBLE
                    animateArrow()
                }
            }
        })
        uninput = findViewById(R.id.uninput)
        passinput = findViewById(R.id.passinput)
        loginbutton.setOnClickListener {
                loginUser() }
        password.transformationMethod = PasswordTransformationMethod.getInstance()
        username.doAfterTextChanged {
                text: Editable? -> usernamestr = text.toString()
            uninput.error=null
            if(passwordstr == "" || usernamestr ==""){
                loginbutton.isEnabled = false
                loginbutton.alpha = 0.5f
            }else {loginbutton.isEnabled = true
                loginbutton.alpha = 1f
            }
        }
        password.doAfterTextChanged { text: Editable? -> passwordstr = text.toString()
        passinput.error = null
        if(passwordstr == "" || usernamestr ==""){
            loginbutton.isEnabled = false
            loginbutton.alpha = 0.5f
        }else {loginbutton.isEnabled = true
            loginbutton.alpha = 1f
        }
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.app_background)
        loginprogress = findViewById<ProgressBar>(R.id.loginprogress)
        loginprogress.visibility = View.INVISIBLE

        gethelp.setOnClickListener{
            val intent = Intent(this, ForgotPass::class.java)
            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.anim_enter,
                R.anim.anim_exit
            ).toBundle()
            startActivity(intent, bndlAnimation)
            loginprogress.visibility = View.INVISIBLE
            loginbutton.text = "Login"
        }

    }
    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun showUsernameDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.incorrectusernamedialog)
        dialog.findViewById<TextView>(R.id.tryagain).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showEmailDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.incorrectusernamedialog)
        dialog.findViewById<TextView>(R.id.incorrectps).text = "Incorrect Email"
        dialog.findViewById<TextView>(R.id.incorrectusernametext).text = "The email you entered does not appear to belong to an account. Please re-check the email and try again "
        dialog.findViewById<TextView>(R.id.tryagain).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun showPasswordDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.incorrectpassworddialog)
        dialog.findViewById<TextView>(R.id.ok).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun loginUser(){
        var email = ""
        if(usernamestr != ""|| passwordstr != "") {
            loginbutton.text = ""
            loginprogress.visibility = View.VISIBLE
            firestore.collection("usernames").document(usernamestr).get()
                .addOnSuccessListener { coll ->
                    if (coll.exists()) {
                        email = coll.getString("email").toString()
                        val dialog = Dialog(this)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setContentView(R.layout.signingin)
                        dialog.setCancelable(false)
                        dialog.show()
                        auth.signInWithEmailAndPassword(email, passwordstr)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    firestore.collection("users")
                                        .document(auth.currentUser!!.uid).get()
                                        .addOnSuccessListener { document ->
                                            saveInformation(document,email,dialog)
                                        }

                                } else {
                                    dialog.dismiss()
                                   showPasswordDialog()
                                    loginprogress.visibility = View.INVISIBLE
                                    loginbutton.text = "Login"
                                }
                            }.addOnFailureListener {
                                dialog.dismiss()
                                showPasswordDialog()
                                loginprogress.visibility = View.INVISIBLE
                                loginbutton.text = "Login"
                            }
                    } else {
                        if (usernamestr.isValidEmail()) {
                            auth.fetchSignInMethodsForEmail(usernamestr).addOnSuccessListener {
                                auth.signInWithEmailAndPassword(usernamestr, passwordstr)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val dialog = Dialog(this)
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                            dialog.setContentView(R.layout.signingin)
                                            dialog.setCancelable(false)
                                            dialog.show()
                                            firestore.collection("users")
                                                .document(auth.currentUser!!.uid).get()
                                                .addOnSuccessListener { document ->
                                                    saveInformation(document,email,dialog)
                                                }.addOnFailureListener {
                                                    loginprogress.visibility = View.INVISIBLE
                                                    loginbutton.text = "Login"
                                                    dialog.dismiss()
                                                    Toast.makeText(
                                                        this,
                                                        "Sign in failed",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                }

                                        } else {
                                            loginprogress.visibility = View.INVISIBLE
                                            loginbutton.text = "Login"
//                                            passinput.error = "Password is incorrect"
                                            showPasswordDialog()
                                        }
                                    }

                            }.addOnFailureListener {
                                loginprogress.visibility = View.INVISIBLE
                                loginbutton.text = "Login"
                                showEmailDialog()
                            }
                        }else{
                            loginprogress.visibility = View.INVISIBLE
                            loginbutton.text = "Login"
//                            uninput.error = "Username is Incorrect"
                            showUsernameDialog()
                        }
                    }
                }
        }else{
            loginprogress.visibility = View.INVISIBLE
            loginbutton.text = "Login"
            Toast.makeText(this, "Please enter a username and password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveInformation(document : DocumentSnapshot,email : String, dialog : Dialog ){
        if(intent.getStringExtra("add")=="add") {
            val sharedPreferences = getSharedPreferences(packageName+"addedaccounts", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            var numberofaddedaccounts = 0
            if (sharedPreferences.getString("count", "") != "") numberofaddedaccounts =
                sharedPreferences.getString("count", "")!!.toInt()
            var isAccountAdded = false
            if (numberofaddedaccounts != 0) {
                for (i in 1..numberofaddedaccounts) {
                    val username = sharedPreferences.getString("username$i", "")
                    if (username == document.getString("username")) {
                        isAccountAdded = true
                    }
                }
            }
            if (!isAccountAdded) {
                val accountnumber = numberofaddedaccounts + 1
                editor.putString("count", accountnumber.toString()).apply()
                editor.putString("username$accountnumber", document.getString("username"))
                editor.putString("email$accountnumber", document.getString("email"))
                editor.putString("imgurl$accountnumber", document.getString("imageurl"))
                editor.putString("displayname$accountnumber", document.getString("displayname"))
                editor.putString("age$accountnumber", document.get("age").toString())
                editor.putString("gender$accountnumber", document.getString("gender"))
                editor.putString("password$accountnumber", passwordstr)
                editor.apply()
                val viewModel = ViewModelProvider(this)[AuthVM::class.java]
                viewModel.updateAuthId(auth.currentUser!!.uid)
            }
        }
        val sharedPreferences = getSharedPreferences(packageName+"user", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(
            "displayname",
            document.getString("displayname")
        ).apply()
        editor.putString(
            "imgurl",
            document.getString("imageurl")
        ).apply()
        editor.putString(
            "username",
            document.getString("username")
        ).apply()
        editor.putString(
            "birthdate",
            document.getString("birthdate")
        ).apply()
        editor.putString(
            "age",
            document.get("age").toString()
        ).apply()
        editor.putString(
            "gender",
            document.getString("gender")
        ).apply()
        editor.putString("email", email).apply()
        editor.putString("password",passwordstr).apply()
        editor.apply()
        dialog.dismiss()
        if(intent.getStringExtra("add")=="add") {
            val viewModel = ViewModelProvider(this).get(GroupVM::class.java)
            viewModel.updateAuthId(auth.currentUser!!.uid)
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
            finish()
        }else{
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
        }
        val sp = getSharedPreferences(
            packageName+"login",
            MODE_PRIVATE
        )
        sp.edit().putString("login", "true")
            .apply()
    }
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun animateArrow(){
        if(arrow.visibility !=View.INVISIBLE && arrow.visibility!= View.GONE){
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(arg0: Animation) {}
                override fun onAnimationRepeat(arg0: Animation) {}
                override fun onAnimationEnd(arg0: Animation) {
                    arrow.startAnimation(animation2)
                }
            })
            animation2.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(arg0: Animation) {}
                override fun onAnimationRepeat(arg0: Animation) {}
                override fun onAnimationEnd(arg0: Animation) {
                    arrow.startAnimation(animation)}
            })
            arrow.startAnimation(animation)
        }
    }
}