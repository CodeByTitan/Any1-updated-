package com.example.any1.feature_signup.presentation

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.any1.R
import com.example.any1.feature_signup.domain.VerifiedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EmailVerification.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmailVerification : Fragment(){
    private var param1: String? = null
//    private val FIREBASE_DEFAULT_DOMAIN = "any1-347401.firebaseapp.com"
    private var param2: String? = null
    private var bool = false
    private lateinit var handler: Handler
    private lateinit var openmail : Button
    private lateinit var verifiedViewModel: VerifiedViewModel
    private lateinit var runnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_emailverification, container, false)
        val resendlink = view.findViewById<TextView>(R.id.resendemail)
        val auth = FirebaseAuth.getInstance()
        val sharedPreferences = requireContext().getSharedPreferences(requireContext().packageName+"temporaryuser",MODE_PRIVATE)
        val email = sharedPreferences.getString("email","")
        handler = Handler(Looper.getMainLooper())
        verifiedViewModel = ViewModelProvider(requireActivity()).get(VerifiedViewModel::class.java)
        handler.postDelayed({
            runnable = Runnable {
                verifiedViewModel.checkEmailVerification()
                handler.postDelayed(runnable,1000)
            }
            runnable.run()
        },1000)

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
                                auth.currentUser?.delete()
                            }
                    }
                    requireActivity().finish()
                }
                dialog.show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
        resendlink.setOnClickListener{
            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                task -> if(task.isSuccessful){
                Toast.makeText(context, "Verification link has been sent to $email", Toast.LENGTH_SHORT).show()
                }
            }
        }
        verifiedViewModel.liveIsVerified.observe(viewLifecycleOwner){
            if(it){
                val navController = requireActivity().findNavController(R.id.fragmentContainerView2)
                navController.navigate(R.id.setname)
                bool = false
                openmail.text = "Continue"
            }
        }
        openmail = view.findViewById<Button>(R.id.openmailin)
        openmail.setOnClickListener {
//            next.visibility = View.VISIBLE
            if (openmail.text == "open mail app") {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                requireActivity().startActivity(intent)
//                viewModel.isVerified.value = true
            } else {
                checkforverfiication()
            }
        }
//
        return view
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        verifiedViewModel.liveIsVerified.removeObservers(viewLifecycleOwner)
        super.onDestroy()
    }

    private fun checkforverfiication(){
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            user.reload()
            if(user.isEmailVerified){
                val navController = requireActivity().findNavController(R.id.fragmentContainerView2)
                navController.navigate(R.id.setname)
                bool = false
            }else{
                Toast.makeText(context, "Please verify your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
//        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.progressbardialog)
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.findViewById<TextView>(R.id.pleasewait).text = "Checking Verification"
//        val auth = FirebaseAuth.getInstance()
//        val user = auth.currentUser
//        user!!.reload()
//        if(user.isEmailVerified){
//            dialog.dismiss()
//            val navController = requireActivity().findNavController(R.id.fragmentContainerView2)
//            navController.navigate(R.id.setname)
//            bool = false
//            openmail.text = "Continue"
//        }else{
//            dialog.dismiss()
//        }
//        handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            runnable = Runnable {
//                verifiedViewModel.checkEmailVerification()
//                verifiedViewModel.liveIsVerified.observe(viewLifecycleOwner){
//                    if(it) {
//                        dialog.dismiss()
//                        val navController =
//                            requireActivity().findNavController(R.id.fragmentContainerView2)
//                        navController.navigate(R.id.setname)
//                        bool = false
//                        openmail.text = "Continue"
//                    }else{
//                        dialog.dismiss()
//                    }
//                }
//                handler.postDelayed(runnable,1000)
//            }
//            runnable.run()
//        },1000)
//        dialog.show()
        super.onResume()
//        if(bool){
//            checkforverfiication()
//            openmail.text = "Continue"
//        }else{
//            bool = true
//        }
//        Handler(Looper.getMainLooper()).postDelayed({
//            if(bool){
//                checkforverfiication()
//                openmail.text = "Continue"
//            }else{
//                bool = true
//            }
//        },1500)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Emailverification.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmailVerification().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}