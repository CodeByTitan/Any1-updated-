package com.example.any1.feature_login.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.any1.feature_group.presentation.GroupInfo
import com.example.any1.R
import com.example.any1.core.main.CreateGroup
import com.example.any1.feature_tags.Model
import com.example.any1.feature_tags.TagsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class SearchTags : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var searchview : androidx.appcompat.widget.SearchView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var searchtick : ImageView
    private lateinit var infobefore : ImageView
    private lateinit var infoafter: ImageView
    var deletepref = true
    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        val themepreferences = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(themepreferences.getString("theme","")=="dark"){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        super.onCreate(savedInstanceState)
        var activity = ""
        val extras = intent.extras
        if(extras!=null && extras.get("donotdelete")=="donotdelete"){
            deletepref = false
        }

        if (extras!=null && extras.get("cg")=="cg"){
            activity = "cg"
        }

        val gctag = intent.getStringExtra("gctag").toString()

        if(extras!=null && extras.get("gc")=="gc"){
            activity = "gc"
        }

        sharedPreferences = getSharedPreferences(packageName+"tag", MODE_PRIVATE)
        val primarytag = sharedPreferences.getString("primarytag","").toString()
        val secondarytag = sharedPreferences.getString("secondarytag","").toString()
        val tertiarytag = sharedPreferences.getString("tertiarytag","").toString()
        setContentView(R.layout.activity_searchtags)
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.phonetoolbar)
        toolbar.setNavigationOnClickListener{
            if(intent.getStringExtra("cg")=="cg"){
                slideleft()
            }else{
                slideToGroupActivity()
            }
        }
        searchtick = findViewById(R.id.searchtick)
        val `is` = applicationContext.resources.openRawResource(R.raw.tags)
        var line: String?
        val arr : ArrayList<Model> = ArrayList()
        try {
            val r = BufferedReader(InputStreamReader(`is`))
            while (r.readLine().also { line = it } != null) {
                line?.let { arr.add(Model(it,false,true)) }
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        recyclerView  = findViewById(R.id.tagrecyclerview)
        searchview = findViewById(R.id.tagsearchview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val displaylist = ArrayList<Model>()
        var firsttag = Model(primarytag,true,true)
        var secondtag = Model(secondarytag,true,true)
        var thirdtag = Model(tertiarytag,true,true)
        displaylist.addAll(arr)

        val adapter = TagsAdapter(displaylist, this,recyclerView,activity)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        var count: String?

        if(primarytag!=""){
            displaylist.remove(Model(primarytag,false,true))
            displaylist.add(0,firsttag)
            recyclerView.adapter!!.notifyDataSetChanged()
        }
        if(secondarytag!=""){
            displaylist.remove(Model(secondarytag,false,true))
            displaylist.add(1,secondtag)
            recyclerView.adapter!!.notifyDataSetChanged()
        }
        if(tertiarytag!=""){
            displaylist.remove(Model(tertiarytag,false,true))
            displaylist.add(2,thirdtag)
            recyclerView.adapter!!.notifyDataSetChanged()
        }
        searchview.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.isNotEmpty()) {
                    displaylist.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    firsttag = Model(sharedPreferences.getString("primarytag","").toString(),true,true)
                    secondtag = Model(sharedPreferences.getString("secondarytag","").toString(),true,true)
                    thirdtag = Model(sharedPreferences.getString("tertiarytag","").toString(),true,true)
                    count = sharedPreferences.getString("counts","").toString()
                    for (i in arr) {
                        if (i.item.lowercase(Locale.getDefault()).contains(search)) {
                            displaylist.add(i)
                        }
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                    when (count){
                        "1"-> {
                            if(firsttag.item != "" && !displaylist.contains(firsttag)){
                                displaylist.remove(firsttag)
                                displaylist.add(0,firsttag)
                            }else{
                                displaylist.add(0,firsttag)
                            }
                        }
                        "2"  -> {
                            if (secondtag.item!= "" && secondtag.item != "" && !displaylist.contains(firsttag)  && !displaylist.contains(secondtag)) {
                                displaylist.add(0,firsttag)
                                displaylist.add(1,secondtag)
                            }
                        }
                        "3"-> {
                            if (firsttag.item!= "" && secondtag.item != ""  && thirdtag.item != "" && !displaylist.contains(firsttag)  && !displaylist.contains(secondtag) && !displaylist.contains(thirdtag)) {
                                displaylist.add(0,firsttag)
                                displaylist.add(1,secondtag)
                                displaylist.add(2,thirdtag)
                            }
                        }
                    }

                }else{
                    displaylist.clear()
                    displaylist.addAll(arr)
                    val primarytag = sharedPreferences.getString("primarytag","").toString()
                    val secondarytag = sharedPreferences.getString("secondarytag","").toString()
                    val tertiarytag = sharedPreferences.getString("tertiarytag","").toString()
                    firsttag = Model(primarytag,true,true)
                    val falsefirsttag = Model(primarytag,false,true)
                    secondtag = Model(secondarytag,true,true)
                    val falsesecondtag = Model(secondarytag,false,true)
                    thirdtag = Model(tertiarytag,true,true)
                    val falsethirdtag = Model(tertiarytag,false,true)
                    when (sharedPreferences.getString("counts","")){
                        "1"-> {
                            if (primarytag!="") {
                                if(displaylist.contains(firsttag)||displaylist.contains(falsefirsttag)){
                                    displaylist.remove(firsttag)
                                    displaylist.remove(falsesecondtag)
                                    displaylist.add(0,firsttag)
                                }else{
                                    displaylist.add(0,firsttag)
                                }
                            }
                        }
                        "2"  -> {
                            if (primarytag!= "" && secondarytag != "" ) {
                                if(displaylist.contains(firsttag)|| displaylist.contains(falsefirsttag)|| displaylist.contains(secondtag)||displaylist.contains(falsesecondtag)){
                                    displaylist.remove(firsttag)
                                    displaylist.remove(falsefirsttag)
                                    displaylist.remove(secondtag)
                                    displaylist.remove(falsesecondtag)
                                    displaylist.add(0,firsttag)
                                    displaylist.add(1,secondtag)
                                }else{
                                    displaylist.add(0,firsttag)
                                    displaylist.add(1,secondtag)
                                }
                            }
                        }
                        "3"-> {
                            if (primarytag!= "" && secondarytag != "" && tertiarytag != "") {
                                if(displaylist.contains(firsttag)|| displaylist.contains(falsefirsttag)|| displaylist.contains(secondtag)||displaylist.contains(falsesecondtag)||displaylist.contains(thirdtag)||displaylist.contains(falsethirdtag)){
                                    displaylist.remove(firsttag)
                                    displaylist.remove(falsefirsttag)
                                    displaylist.remove(secondtag)
                                    displaylist.remove(falsesecondtag)
                                    displaylist.remove(thirdtag)
                                    displaylist.remove(falsethirdtag)
                                    displaylist.add(0,firsttag)
                                    displaylist.add(1,secondtag)
                                    displaylist.add(1,thirdtag)
                                }else{
                                    displaylist.add(0,firsttag)
                                    displaylist.add(1,secondtag)
                                    displaylist.add(1,thirdtag)
                                }
                            }
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })



//        searchview.setOnCloseListener {
//            true
//        }
        recyclerView.addItemDecoration(
            SimpleDividerItemDecoration(
                this
            )
        )

        searchtick.isEnabled = true
        searchtick.setOnClickListener {
            val primarytag = sharedPreferences.getString("primarytag","").toString()
            val primaryincg = sharedPreferences.getString("primaryincg","").toString()
            if(primarytag!="") {
                if(intent.getStringExtra("cg")=="cg"){
                    val intent = Intent(this, CreateGroup::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra("tags", "tags")
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.slideleft2,
                        R.anim.slideleft1
                    )
                }else{
                    val intent = Intent(this, GroupInfo::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    val firestore = FirebaseFirestore.getInstance()
                    val checkedlist = ArrayList<String>()
                    val primarytag = sharedPreferences.getString("primarytag","").toString()
                    val secondarytag = sharedPreferences.getString("secondarytag","").toString()
                    val tertiarytag = sharedPreferences.getString("tertiarytag","").toString()
                    if(primarytag!="")checkedlist.add(primarytag)
                    if(secondarytag!="") checkedlist.add(secondarytag)
                    if(tertiarytag!="") checkedlist.add(tertiarytag)
                    firestore.collection("groups").document(gctag).update("searchtag",checkedlist)
                    intent.putExtra("tags", "tags")
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.slideleft2,
                        R.anim.slideleft1
                    )
                }
            }else if(primaryincg != primarytag&& primarytag == ""){
                if(intent.getStringExtra("cg")=="cg") {
                    val intent = Intent(this, CreateGroup::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra("tags", "tags")
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.slideleft2,
                        R.anim.slideleft1
                    )
                }
            }
        }
    }


    private fun displayItemInDefaultRecyclerView(){

    }
    override fun onStart() {
        super.onStart()
        val extras = intent.extras
        if(extras!=null && extras.get("donotdelete")=="donotdelete"){
            deletepref = false
        }
    }

    override fun onResume() {
        super.onResume()
        val extras = intent.extras
        if(extras!=null && extras.get("donotdelete")=="donotdelete"){
            deletepref = false
        }

    }

    private fun slideleft(){
        if(deletepref){
            sharedPreferences.edit().clear().apply()
        }
        val intent = Intent(this, CreateGroup::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("cancel","cancel")
        startActivity(intent)
        overridePendingTransition(
            R.anim.slideleft2,
            R.anim.slideleft1
        )
    }

    private fun slideToGroupActivity(){
        if(deletepref){
            sharedPreferences.edit().clear().apply()
        }
        val intent = Intent(this, GroupInfo::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("cancel","cancel")
        startActivity(intent)
        overridePendingTransition(
            R.anim.slideleft2,
            R.anim.slideleft1
        )
    }
    class SimpleDividerItemDecoration(context: Context?) :
        ItemDecoration() {
        private val mDivider: Drawable? = ContextCompat.getDrawable(context!!,
            R.drawable.line_divider
        )
        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight
            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + mDivider!!.intrinsicHeight
                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        if(intent.getStringExtra("cg")=="cg"){
            slideleft()
        }else{
            slideToGroupActivity()
        }
    }

}