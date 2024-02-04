package com.example.any1.feature_search.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.any1.feature_search.domain.adapter.SearchUserAdapter
import com.example.any1.feature_search.domain.vm.SearchViewModel
import com.example.any1.R
import com.example.any1.core.main.MainActivity
import com.example.any1.feature_search.domain.adapter.ViewPagerFragmentAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class Search : AppCompatActivity() {
//    private lateinit var recyclerView: RecyclerView
    private lateinit var changesearch : ImageView
    private lateinit var viewModel: SearchViewModel
    private lateinit var searchView: SearchView
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var tablayout : TabLayout
    private lateinit var adapter : SearchUserAdapter
    private lateinit var viewPager2: ViewPager2
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewPagerFragmentAdapter: ViewPagerFragmentAdapter
    private val titles = arrayOf("tags","names","group tag")
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences2 = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(sharedPreferences2.getString("theme","")=="dark"){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchView = findViewById(R.id.searchsearchview)
        changesearch = findViewById(R.id.changesearch)
        viewPager2 = findViewById(R.id.searchviewpager)
        val recyclerView : RecyclerView = findViewById(R.id.searchuserrecyclerview)
        toolbar = findViewById(R.id.searchtoolbar)
        tablayout = findViewById(R.id.tablayout)
        adapter = SearchUserAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewPagerFragmentAdapter = ViewPagerFragmentAdapter(this)
        if(toolbar.title == "Search People"){
            recyclerView.visibility = View.VISIBLE
        }else{
            recyclerView.visibility = View.GONE
        }
        viewPager2.adapter = viewPagerFragmentAdapter
        TabLayoutMediator(
            tablayout, viewPager2
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = titles[position]
        }.attach()
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        sharedPreferences = getSharedPreferences(packageName+"search", MODE_PRIVATE)
        viewModel.searchstring.observe(this){
            if(recyclerView.visibility == View.VISIBLE){
                viewModel.getUsersByUsername()
                if(it==""){
                    adapter.clearUsersList()
                }
            }
        }

        viewModel.liveUserList.observe(this){
            if(recyclerView.visibility == View.VISIBLE){
                adapter.setAdapterList(it)
                recyclerView.adapter = adapter
            }
        }
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
//                viewModel.setString(query)
                return false
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    viewModel.setString(newText)
                }
                return true
            }
        })

        toolbar.setNavigationOnClickListener { slideleft() }
        changesearch.setOnClickListener {
            if(sharedPreferences.getString("groups","")!="false"){
                sharedPreferences.edit().putString("groups","false").apply()
                toolbar.title = "Search People"
                recyclerView.visibility = View.VISIBLE
                viewPager2.visibility = View.INVISIBLE
                viewPager2.isEnabled = false
                tablayout.visibility = View.INVISIBLE
                tablayout.isEnabled = false
            }else{
                sharedPreferences.edit().putString("groups","true").apply()
                viewPager2.visibility = View.VISIBLE
                viewPager2.isEnabled = true
                toolbar.title = "Search Groups"
                recyclerView.visibility = View.GONE
                tablayout.visibility = View.VISIBLE
                tablayout.isEnabled = true
            }
        }

    }

    private fun slideleft(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slideleft2,
            R.anim.slideleft1
        )
    }
    override fun onBackPressed() {
        super.onBackPressed()
        slideleft()
    }
}