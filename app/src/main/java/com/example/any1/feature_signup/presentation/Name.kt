package com.example.any1.feature_signup.presentation

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.any1.R
import com.example.any1.feature_signup.domain.AddAccountViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Name.newInstance] factory method to
 * create an instance of this fragment.
 */
class Name : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_setname, container, false)
        val displayname = view.findViewById<EditText>(R.id.displayname)
        val next = view.findViewById<Button>(R.id.namenext)
        val auth = FirebaseAuth.getInstance()
        val addAccountViewModel = ViewModelProvider(requireActivity())[AddAccountViewModel::class.java]
//        val firestore = FirebaseFirestore.getInstance()
        val sharedPreferences2 = requireActivity().getSharedPreferences(requireContext().packageName+"theme", MODE_PRIVATE)
//        if(sharedPreferences2.getString("theme","")=="dark"){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }
//        val stickyService = Intent(requireActivity(), StickyService::class.java)
//        requireActivity().startService(stickyService)
//        val handler = ApplicationLifeCyclerHandler()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            requireActivity().registerActivityLifecycleCallbacks(handler)
//        }
//        requireActivity().registerComponentCallbacks(handler)
        next.visibility = View.INVISIBLE
        var name = ""
        displayname.doAfterTextChanged { text: Editable? ->
            if (text.toString().length <= 30) {
                name = text.toString()
                next.visibility = View.VISIBLE
            }
        }
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

//        .setOnKeyListener(object : View.OnKeyListener {
//            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
//                return keyCode == KeyEvent.KEYCODE_BACK
//            }
//        })
        val themeswitch = view.findViewById<SwitchCompat>(R.id.themeswitch)
        if(sharedPreferences2.getString("theme","")== "dark"){
            themeswitch.isChecked = true
        }
        themeswitch.setOnCheckedChangeListener { _, ischecked ->
            if (ischecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                sharedPreferences2.edit().putString("theme", "dark").apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                sharedPreferences2.edit().putString("theme", "light").apply()
            }
        }

        next.setOnClickListener {
//            val shp = requireContext().getSharedPreferences("login", MODE_PRIVATE)
            val sharedPreferences = requireActivity().getSharedPreferences(requireContext().packageName+"temporaryuser",MODE_PRIVATE)
            sharedPreferences.edit().putString("displayname",name).apply()
            val navController = requireActivity().findNavController(R.id.fragmentContainerView2)
            navController.navigate(R.id.datepicker)
//            val imgurl = sharedPreferences?.getString("imgurl","")
//            val username = sharedPreferences?.getString("username","")
//            val emailid = sharedPreferences!!.getString("email","")
//
//            firestore.collection("users").document(auth.currentUser!!.uid).set(hashMapOf(
//                name to "displayname",
//                imgurl to "imageurl",
//                sharedPreferences?.getString("username","") to "username"
//            ))
//            val documentReference: DocumentReference = firestore.collection("users").document(auth.currentUser!!.uid)
//            val userdata = hashMapOf(
//                "displayname" to name,
//                "imageurl" to imgurl.toString(),
//            )
//            val usernameref = firestore.collection("usernames").document(username.toString())
//            usernameref.set(hashMapOf(
//                "email" to emailid)).addOnSuccessListener { Toast.makeText(context, "username created", Toast.LENGTH_SHORT).show()}
//                .addOnFailureListener{ Toast.makeText(context, "username not created", Toast.LENGTH_SHORT).show()}
//            documentReference.set(userdata).addOnSuccessListener {
//            }.addOnFailureListener{
//            }
//            sharedPreferences?.edit()?.putString("displayname",name)?.apply()
//            startActivity(Intent(requireActivity(),MainActivity::class.java).setFlags (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
//            shp.edit().putString("login","true").apply()
//            requireActivity().finish()
        }
        return view
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        val sharedPreferences =
//            requireContext().getSharedPreferences("user", MODE_PRIVATE)
//        val username = sharedPreferences?.getString("username", "")
//        if (username != null) {
//            val firestore = FirebaseFirestore.getInstance()
//            firestore.collection("usernames").document(username).get()
//                .addOnSuccessListener {
//                    firestore.collection("usernames").document(username).delete()
//                    val auth = FirebaseAuth.getInstance()
//                    auth.currentUser?.delete()
//                }
//        }
//    }

    //    override fun onStart() {
//        super.onStart()
//        requireView().isFocusableInTouchMode = true
//        requireView().requestFocus()
//        requireView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
//                val dialog = Dialog(requireContext())
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                dialog.setCancelable(false)
//                dialog.setContentView(R.layout.profiledialog)
//                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                val stopcreation = dialog.findViewById<TextView>(R.id.stopcreating)
//                val continuee = dialog.findViewById<TextView>(R.id.continuee)
//                continuee.setOnClickListener { dialog.dismiss() }
//                stopcreation.setOnClickListener {
//                    dialog.dismiss()
//                    val sharedPreferences = requireContext().getSharedPreferences("user", MODE_PRIVATE)
//                    val username = sharedPreferences?.getString("username","")
//                    if (username != null) {
//                        val firestore = FirebaseFirestore.getInstance()
//                        firestore.collection("usernames").document(username).get().addOnSuccessListener{
//                            firestore.collection("usernames").document(username).delete()
//                        }
//                    }
//                    requireActivity().finish()
//                }
//                dialog.show()
//            }
//            false
//        })
//    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment setname.
         */

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Name().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}