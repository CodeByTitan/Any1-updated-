package com.example.any1.feature_signup.presentation

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.any1.R
import com.example.any1.feature_signup.domain.AddAccountViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.Period
import java.util.*
import kotlin.properties.Delegates


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [Date.newInstance] factory method to
 * create an instance of this fragment.
 */
class Date : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var period by Delegates.notNull<Int>()
    private lateinit var picker: DatePicker
    private var age:Int = 0
    private lateinit var birthdate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_datepicker, container, false)
        picker = view.findViewById<DatePicker>(R.id.datePicker)
        val date = view.findViewById<TextView>(R.id.date)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR).toString()
        val addAccountViewModel = ViewModelProvider(requireActivity())[AddAccountViewModel::class.java]
        val month = calendar.get(Calendar.MONTH).toString()
        val day :String= calendar.get(Calendar.DAY_OF_MONTH).toString()
        val auth = FirebaseAuth.getInstance()
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
//        LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault())
        val yearint = calendar.get(Calendar.YEAR)
        val monthint = (calendar.get(Calendar.MONTH))
        val next = view.findViewById<Button>(R.id.agenext)
        val dayint = calendar.get(Calendar.DAY_OF_MONTH)
        var clickcount = 0
        val sharedPreferences = requireContext().getSharedPreferences(requireContext().packageName+"temporaryuser",MODE_PRIVATE)
        next.setOnClickListener {
            if(age < 13){
                clickcount +=1
                Toast.makeText(context, "Sorry but you cannot access the app", Toast.LENGTH_SHORT).show()
                if(clickcount>=2){
                    val username = sharedPreferences.getString("username","")
                    val auth = FirebaseAuth.getInstance()
                    val firestore = FirebaseFirestore.getInstance()
                    if (username != null) {
                        firestore.collection("usernames").document(username).get()
                            .addOnSuccessListener {
                                firestore.collection("usernames").document(username).delete()
                                auth.currentUser?.delete()
                            }
                    }
                    requireActivity().finish()

                }
            }else{
                if(birthdate!=null){
                    sharedPreferences.edit().putString("birthdate", birthdate).apply()
                    sharedPreferences.edit().putString("age", age.toString()).apply()
                    val navController = requireActivity().findNavController(R.id.fragmentContainerView2)
                    navController.navigate(R.id.gender)
                }
            }
        }
//        val yearint = LocalDate.now().year
//        val monthint = (LocalDate.now().monthValue)
//        val dayint = (LocalDate.now().dayOfMonth)
        picker.maxDate = System.currentTimeMillis();

        date.text = "You are ${getAge(yearint, monthint, dayint)} years old"
//        val birthday = day + "/"+ month + "/" + year
//        date.setText(birthday)

        picker.init(
            yearint, monthint, dayint
        ) { view, year, monthOfYear, dayOfMonth ->
            val tempmonth = monthOfYear+1
            age = getAge(year,tempmonth,dayOfMonth)
            birthdate = "$dayOfMonth/$tempmonth/$year"
            date.text = "You are $age years old"
        }
//        picker.init(yearint,monthint,dayint, DatePicker.OnDateChangedListener(){
//
//        })
//        date.text = getCurrentDate()
//        datepicker.setOnDateChangedListener(){
//
//        }
        return view
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAge(year: Int, month: Int, dayOfMonth: Int): Int {
        period = Period.between(
            LocalDate.of(year, month, dayOfMonth),
            LocalDate.now()

        ).years
//        if(month ==0){
//            val tempmonth = month+1
//             period = Period.between(
//                LocalDate.of(year, tempmonth, dayOfMonth),
//                LocalDate.now()
//
//            ).years
//        }else{
//            period = 0
//        }
           return period
    }
    fun getCurrentDate(): String? {
        val builder = StringBuilder()
        builder.append((picker.getMonth() + 1).toString() + "/") //month is 0 based
        builder.append(picker.dayOfMonth.toString() + "/")
        builder.append(picker.getYear())
        return builder.toString()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment datepicker.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Date().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}