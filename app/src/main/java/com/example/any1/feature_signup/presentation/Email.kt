package com.example.any1.feature_signup.presentation

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.any1.R
import android.text.Editable
import android.util.Patterns
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.any1.feature_login.presentation.Login
import com.example.any1.feature_signup.domain.AddAccountViewModel
import com.example.any1.feature_signup.domain.EmailViewModel
import com.example.any1.feature_signup.domain.UserPassVM
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [email.newInstance] factory method to
 * create an instance of this fragment.
 */
class Email : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private val FIREBASE_DEFAULT_DOMAIN = "any1-347401.firebaseapp.com"
    private lateinit var dialog :Dialog
    var emailid = ""
    var password = ""
    var checkemail:Boolean = false
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    //ToDo - backbutton does not shows stop creating account dialog

    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_email, container, false)
            val editText = view.findViewById<EditText>(R.id.email)
            val addAccountViewModel = ViewModelProvider(requireActivity())[AddAccountViewModel::class.java]
            auth = FirebaseAuth.getInstance()
            val callback = object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    val dialog = Dialog(requireContext())
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.profiledialog)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val stopcreation = dialog.findViewById<TextView>(R.id.stopcreating)
                    val continuee = dialog.findViewById<TextView>(R.id.continuee)
                    continuee.setOnClickListener { dialog.dismiss() }
                    stopcreation.setOnClickListener {
                        dialog.dismiss()
                        val sharedPreferences =
                            requireContext().getSharedPreferences(requireContext().packageName+"temporaryuser", MODE_PRIVATE)
                        val username = sharedPreferences?.getString("username", "")
                        if (username != null) {
                            val firestore = FirebaseFirestore.getInstance()
                            firestore.collection("usernames").document(username).get()
                                .addOnSuccessListener {
                                    firestore.collection("usernames").document(username).delete()
                                }
                        }
                        requireActivity().finish()

                    }
                    dialog.show()
                }
            }
            val next = view.findViewById<Button>(R.id.next)
            if(emailid == ""){
                next.isEnabled = false
                next.alpha = 0.5f
            }else{
                next.isEnabled = true
                next.alpha = 1f
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
            editText.doAfterTextChanged { text: Editable? ->  emailid =text.toString()
                checkemail = emailid.isValidEmail()
                if(emailid == ""){
                    next.isEnabled = false
                    next.alpha = 0.5f
                }else{
                    next.isEnabled = true
                    next.alpha = 1f
                }
            }
            val viewModel = ViewModelProvider(requireActivity())[UserPassVM::class.java]
            password = viewModel.password
            next.setOnClickListener {
                if(checkemail){
                    val dialog = Dialog(requireContext())
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.progressbardialog)
                    dialog.setCancelable(false)
                    dialog.show()
                    auth.fetchSignInMethodsForEmail(emailid)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                val isNewUser = task1.result.signInMethods!!.isEmpty()
                                if (isNewUser) {
                                    if (emailid != "") {
                                        auth.createUserWithEmailAndPassword(emailid, password)
                                            .addOnCompleteListener { task2 ->
                                                if (task2.isSuccessful) {
                                                    val sharedPreferences =
                                                        activity?.getSharedPreferences(
                                                            requireContext().packageName+"temporaryuser",
                                                            MODE_PRIVATE
                                                        )
                                                    sharedPreferences?.edit()
                                                        ?.putString("email", emailid)?.apply()
                                                    sharedPreferences?.edit()
                                                        ?.putString("password", password)?.apply()
                                                    val user = auth.currentUser!!
                                                    user.sendEmailVerification()
                                                        .addOnCompleteListener { task3 ->
                                                            if (task3.isSuccessful) {
                                                                dialog.dismiss()
                                                                val viewModel =
                                                                    ViewModelProvider(
                                                                        requireActivity()
                                                                    ).get(
                                                                        EmailViewModel::class.java
                                                                    )
                                                                viewModel.email = emailid
                                                                val navController =
                                                                    activity?.findNavController(R.id.fragmentContainerView2)
                                                                navController?.navigate(R.id.emailverification)
                                                            }
                                                        }
                                                }
                                            }
                                    } else {
                                        dialog.dismiss()
                                        showSignedUpEmailDialog()
                                    }
                                } else {
                                    dialog.dismiss()
                                   showSignedUpEmailDialog()
                                }
                            }
                        }
                }else if(emailid == ""){
                    Toast.makeText(context, "Please enter an email address", Toast.LENGTH_SHORT).show()
                }else{
                    showEmailDialog()
                }
            }
            return view
        }

    private fun showSignedUpEmailDialog(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.signedupemaildialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TextView>(R.id.logintoexistingaccount).setOnClickListener {
            val intent = Intent(requireActivity(), Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("email",emailid)
            startActivity(intent)
        }
        dialog.findViewById<TextView>(R.id.createnewaccount).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun showEmailDialog(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.incorrectusernamedialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TextView>(R.id.incorrectps).text = "Incorrect Email"
        dialog.findViewById<TextView>(R.id.incorrectusernametext).text = "The email you entered seems to be invalid. Please re-check the email and try again "
        dialog.findViewById<TextView>(R.id.tryagain).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}