package com.example.any1.core.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.any1.R
import com.example.any1.core.profile.Profile
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BottomNavFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class BottomNavFrag : Fragment() {
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
    private lateinit var bottomnav : BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences2 = requireContext().getSharedPreferences(requireContext().packageName+"theme", AppCompatActivity.MODE_PRIVATE)
        val view = inflater.inflate(R.layout.fragment_testfrag, container, false)
        // Inflate the layout for this fragment
        bottomnav = view.findViewById(R.id.bottomnavigationview)
//        val nestedNavHostFragment = childFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
//
//        val navController = nestedNavHostFragment?.navController
//
//        if (navController != null) {
//            bottomnav.setupWithNavController(navController)
//        } else {
//            throw RuntimeException("Controller not found")
//        }

        parentFragment?.activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView, Home())
            ?.commit() // Setting home as default fragment
        if(sharedPreferences2.getString("theme","")=="dark"){
            bottomnav.menu.getItem(0).setIcon(R.drawable.bnaviconbg)
            bottomnav.menu.getItem(1).setIcon(R.drawable.bnavprofile)
//            menu?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.blackhome);
        }else{
            bottomnav.menu.getItem(0).setIcon(R.drawable.iconbghome)
            bottomnav.menu.getItem(1).setIcon(R.drawable.iconbgprofile)
//            menu?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.bnaviconbg);
        }
        bottomnav.selectedItemId = R.id.home
        bottomnav.itemIconTintList = null;
        bottomnav.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.home -> {
                    fragment = Home()
                    val fragmentManager: FragmentManager = childFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)
                        .commit()
                    true
                }
                R.id.profile -> {
                    fragment = Profile()
                    val fragmentManager: FragmentManager = childFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment, "profile")
                        .commit()
                    true
                }
                else -> {
                    true
                }
            }

            parentFragment?.activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, fragment!!)?.commit()
            true
        })
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment testfrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BottomNavFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}