package com.example.any1.core.main

import android.R.attr.height
import android.app.ActivityOptions
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.any1.feature_search.presentation.Search
import com.example.any1.feature_group.domain.model.GroupModel
import com.example.any1.feature_chat.presentation.Chat
import com.example.any1.R
import com.example.any1.feature_group.domain.adapter.GroupAdapter
import com.example.any1.databinding.FragmentHomeBinding
import com.example.any1.feature_group.domain.interfaces.OnGroupClickListener
import com.example.any1.feature_group.domain.vm.GroupVM
import com.example.any1.util.animation.MyCustomAnimation

class Home : Fragment(), OnGroupClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: GroupAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var view: FragmentHomeBinding
    private lateinit var viewModel: GroupVM

    private fun slideUp(view: View) {
        val animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            view.height.toFloat(),  // fromYDelta
            0F
        ) // toYDelta
        animate.duration = 500
//        animate.fillAfter = true
        view.startAnimation(animate)
        animate.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                view.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
    }

    // slide the view from its current position to below itself
    private fun slideDown(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            0F,  // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
        animate.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                slideUp(view)
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
    }

    private fun runAnimation(){
        if (view.switchcard.visibility == View.VISIBLE) {
            val a = MyCustomAnimation(view.switchcard, 1000, MyCustomAnimation.COLLAPSE)
            a.height = view.switchcard.height
            view.switchcard.startAnimation(a)
        } else {
            val a = MyCustomAnimation(view.switchcard, 1000, MyCustomAnimation.EXPAND)
            a.height = height
            view.switchimg.startAnimation(a)
            a.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    runAnimation()
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }

            })
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = FragmentHomeBinding.inflate(layoutInflater)
        view.switchcard.visibility = View.INVISIBLE
        view.gcrecyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = GroupAdapter(this,requireContext())
        viewModel = ViewModelProvider(requireActivity())[GroupVM::class.java]
        viewModel.getGroups.observe(viewLifecycleOwner
        ) {
            adapter.setGroupModelList(it)
            recyclerView.adapter = adapter
        }

        viewModel.liveuserid.observe(viewLifecycleOwner){
            viewModel.getGroups()
        }
//        ViewCompat.setNestedScrollingEnabled(recyclerView,true)
        view.creategroup.setOnClickListener {
            val intent = Intent(requireActivity(), CreateGroup::class.java)
            intent.putExtra("home","home")
            startActivity(intent)
        }
        val sharedPreferences2 = requireActivity().getSharedPreferences(requireContext().packageName+"theme", MODE_PRIVATE
        )
        view.search.setOnClickListener {
            val intent = Intent(requireActivity(), Search::class.java)
            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                context,
                R.anim.anim_enter,
                R.anim.anim_exit
            ).toBundle()
            startActivity(intent,bndlAnimation)
        }
        val vp = requireActivity().findViewById<View>(R.id.viewpager) as ViewPager2
        vp.isUserInputEnabled = true

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
        view.directmessage.setOnClickListener {
            vp.setCurrentItem(1,true)
        }

        if(sharedPreferences2.getString("theme","")=="dark"){
            view.directmessage.setColorFilter(Color.argb(255, 255, 255, 255));
        }else{
            view.directmessage.setColorFilter(Color.argb(0, 0, 0, 0));
        }
        return view.root
    }


    override fun OnGroupClicked(position: Int, groupModelList: ArrayList<GroupModel>) {
        val intent = Intent(requireActivity(), Chat::class.java)
        val groupModel = groupModelList[position]
        intent.putExtra("imageurl",groupModel.uri)
        intent.putExtra("gcname",groupModel.item)
        intent.putExtra("gctag",groupModel.tag)
        val bndlAnimation = ActivityOptions.makeCustomAnimation(
            context,
            R.anim.anim_enter,
            R.anim.anim_exit
        ).toBundle()
        startActivity(intent,bndlAnimation)
        requireActivity().overridePendingTransition(
            R.anim.anim_enter,
            R.anim.anim_exit
        )
    }
}