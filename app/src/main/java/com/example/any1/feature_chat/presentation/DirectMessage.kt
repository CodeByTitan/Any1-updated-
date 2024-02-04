package com.example.any1.feature_chat.presentation

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.any1.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DirectMessage.newInstance] factory method to
 * create an instance of this fragment.
 */
class DirectMessage : Fragment() {
    // TODO: Rename and change types of parameters
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
        val view = inflater.inflate(R.layout.fragment_directmessage, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.dmtoolbar)
        val vp = requireActivity().findViewById<View>(R.id.viewpager) as ViewPager2
        val sharedPreferences = requireContext().getSharedPreferences(requireContext().packageName+"user",MODE_PRIVATE)
        val tv = view.findViewById<TextView>(R.id.textView14)
        val gender = sharedPreferences.getString("gender","")
        if(gender == "male"){
            tv.text = getString(R.string.emptydmmale)
        }else{
            tv.text = getString(R.string.emptydmfemale)
        }
        toolbar.setNavigationOnClickListener{
            vp.setCurrentItem(0,true)
//            val fragment = home()
//            val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
//            ft.replace(R.id.fragmentContainerView, fragment)
//            ft.addToBackStack(null)
//            ft.commit()
////            Navigation.findNavController(it).navigate(R.id.action_directmessage2_to_home2)
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                vp.setCurrentItem(0,true)
                isEnabled = false
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment directmessage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DirectMessage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}