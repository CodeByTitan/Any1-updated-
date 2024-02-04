package com.example.any1.feature_signup.presentation

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.any1.R
import com.example.any1.feature_signup.domain.AddAccountViewModel
import com.example.any1.feature_signup.domain.EmailViewModel
import com.example.any1.feature_signup.domain.UserPassVM
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [userpass.newInstance] factory method to
 * create an instance of this fragment.
 */
class UsernamePassword : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var username: EditText
    lateinit var password: EditText
    private lateinit var uninput: TextInputLayout
    private lateinit var passinput: TextInputLayout
//    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_userpass, container, false)
        username = view.findViewById<EditText>(R.id.username)
        val firestore = FirebaseFirestore.getInstance()
        val next = view.findViewById<Button>(R.id.nxt)
//        linearLayout = view.findViewById<LinearLayout>(R.id.testlinear)
        uninput =view.findViewById(R.id.uninput)
        passinput = view.findViewById(R.id.passinput)
        password = view.findViewById(R.id.password)
        val addViewModel = ViewModelProvider(requireActivity())[AddAccountViewModel::class.java]
        var usernamestr = ""
        var passwordstr = ""
        val progressbar = view.findViewById<ProgressBar>(R.id.progressbarr)
        val tick = view.findViewById<ImageView>(R.id.tick)
        tick.visibility = View.INVISIBLE
        var count = 0
        var secondcount = 0
        if(usernamestr!="" && passwordstr!=""){
            next.isEnabled = true
            next.alpha = 1f
        }else{
            next.isEnabled = false
            next.alpha = 0.5f
        }
        if(requireActivity().intent.getStringExtra("add")=="add"){
            addViewModel.add
        }else{
            addViewModel.dontadd()
        }
        password.transformationMethod = PasswordTransformationMethod.getInstance();
        progressbar.visibility = View.INVISIBLE
        username.doAfterTextChanged { text: Editable? ->  usernamestr = text.toString()
            if(usernamestr!="" && passwordstr!=""){
                next.isEnabled = true
                next.alpha = 1f
            }else{
                next.isEnabled = false
                next.alpha = 0.5f
            }
            if(count==0){
                count++
            }else{
                //ToDo -  Put usernames in mongodb database and retrieve whole lists from them or try implementing username checker
                // through this method only
                tick.visibility = View.INVISIBLE
                progressbar.visibility = View.VISIBLE
                val timerObj = Timer()

//            requireActivity().runOnUiThread { Runnable {
//
//            }}
                val timerTaskObj: TimerTask = object : TimerTask() {
                    override fun run() {
                        if(usernamestr!=""){
                            firestore.collection("usernames").document(usernamestr).get().addOnSuccessListener {
                                if (it.exists()) {
                                    uninput.error = "This username already exists"
                                    progressbar.visibility = View.INVISIBLE
                                    next.isEnabled = false
                                } else {
                                    tick.visibility = View.VISIBLE
                                    progressbar.visibility = View.INVISIBLE
                                    next.isEnabled = true
                                }
                            }
                        }else{
                            progressbar.visibility = View.INVISIBLE
                        }
                    }
                }
                timerObj.schedule(timerTaskObj, 2000)
                if(usernamestr == ".") uninput.error = "Please enter a valid username"
                else if(usernamestr.startsWith("__")&& usernamestr.endsWith("__")){
                    uninput.error = "Please enter a valid username"
                    next.isEnabled = false
                }
                else if(usernamestr.contains("/")|| usernamestr.contains("..")){
                    uninput.error = "Please enter a valid username"
                    next.isEnabled = false
                }
                else if(usernamestr.length>=30) {
                    uninput.error = "Character limit exceeded"
                    next.isEnabled = false
                    Toast.makeText(context, "Character limit exceeded", Toast.LENGTH_SHORT).show()
                }
                else if (usernamestr == passwordstr) {
                    uninput.error=""
                    next.isEnabled = false
                }else{
                    uninput.error = null
                    username.error = null
                    next.isEnabled = true
                }
            }
        }
        password.doAfterTextChanged { text: Editable? ->
            passwordstr = text.toString()
            if(usernamestr!="" && passwordstr!=""){
                next.isEnabled = true
                next.alpha = 1f
            }else{
                next.isEnabled = false
                next.alpha = 0.5f
            }
            if(secondcount ==0){
                secondcount++
            }else{
                if (passwordstr.length < 6 ) {
                    passinput.error = "Password must have at least 6 characters"
                    next.isEnabled = false
                }else if (passwordstr == usernamestr) {
                    passinput.error = "Password cannot be same as username"
//                    Toast.makeText(context, "Password cannot be same as username", Toast.LENGTH_SHORT).show()
                    next.isEnabled = false
                }else{
                    passinput.error = null
                    next.isEnabled = true
                }
            }
        }

        next.setOnClickListener {
            if(usernamestr.equals("")&&passwordstr.equals("")){
                Toast.makeText(context, "Please enter a username and password", Toast.LENGTH_SHORT).show()
            }
            else if(uninput.error != null || passinput.error !=null){
                next.isEnabled =false
            }
            else if(usernamestr =="") Toast.makeText(context, "Please enter a username", Toast.LENGTH_SHORT).show()
            else if(passwordstr =="") Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
            else if(progressbar.visibility == View.VISIBLE){
                Toast.makeText(requireContext(), "Please Wait...", Toast.LENGTH_SHORT).show()
            }
            else{
                next.isEnabled = true
                firestore.collection("usernames").document(usernamestr).set({})
                val sharedPreferences = activity?.getSharedPreferences(requireContext().packageName+"temporaryuser",MODE_PRIVATE)
                sharedPreferences?.edit()?.putString("username",usernamestr)?.apply()
                sharedPreferences?.edit()?.putString("password",passwordstr)?.apply()
                val viewModel = ViewModelProvider(requireActivity())[UserPassVM::class.java]
                viewModel.username = usernamestr
                viewModel.password = passwordstr
                val navController = activity?.findNavController(R.id.fragmentContainerView2)
                navController?.navigate(R.id.email2)
            }
        }
        return view
    }

}