package com.example.any1.feature_login.presentation

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.example.any1.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPass : AppCompatActivity() {
    private lateinit var dialog : Dialog
    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpass)
        val submit = findViewById<Button>(R.id.submit)
        val email = findViewById<TextInputEditText>(R.id.forgotemail)
        val auth = FirebaseAuth.getInstance()
        val forgotemail = findViewById<TextView>(R.id.textView17)
        forgotemail.setOnClickListener {
            val intent = Intent(this, ForgotPassByUsername::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_bottom,R.anim.slide_out_top)
//            overridePendingTransition(R.anim.slideup,R.anim.no_change)
        }
        var emailid = ""
        window.statusBarColor = ContextCompat.getColor(this, R.color.app_background)
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
        val bb = findViewById<ImageView>(R.id.bb)
        bb.setOnClickListener{
            slideleft()
        }
        email.doAfterTextChanged { text: Editable? -> emailid = text.toString()}
        submit.setOnClickListener{
            if(emailid == ""){
                Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show()
            }else{
                if(emailid.isValidEmail()){
                    showProgressBarDialog()
                    auth.fetchSignInMethodsForEmail(emailid).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val isNewUser = task.result.signInMethods!!.isEmpty()
                                if (!isNewUser) {
                                    auth.sendPasswordResetEmail(emailid)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                dialog.dismiss()
                                                val intent = Intent(this, EmailRecovery::class.java)
                                                intent.putExtra("email", emailid)
                                                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                                                    this,
                                                    R.anim.anim_enter,
                                                    R.anim.anim_exit
                                                ).toBundle()
                                                startActivity(intent, bndlAnimation)
                                            }
                                        }
                                }else{
                                    dialog.dismiss()
                                    showEmailDialog()
                                }
                            }
                    }

                }else{
                    dialog.dismiss()
                    showInvalidEmailDialog()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        slideleft()
    }

    private fun showProgressBarDialog(){
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progressbardialog)
        dialog.setCancelable(false)
        dialog.show()
    }
    private fun showInvalidEmailDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.incorrectusernamedialog)
        dialog.findViewById<TextView>(R.id.incorrectps).text = "Invalid Email"
        dialog.findViewById<TextView>(R.id.incorrectusernametext).text = "The email address you entered is invalid. Please enter a valid email address"
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

    private fun slideleft(){
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slideleft2,
            R.anim.slideleft1
        )
    }
}