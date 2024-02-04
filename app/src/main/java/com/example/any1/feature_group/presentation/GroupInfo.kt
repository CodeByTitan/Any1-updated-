package com.example.any1.feature_group.presentation

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.feature_group.domain.model.MemberModel
import com.example.any1.feature_search.domain.model.RequestModel
import com.example.any1.feature_chat.presentation.Chat
import com.example.any1.feature_group.domain.vm.GroupInfoVM
import com.example.any1.feature_group.domain.vm.MembersVM
import com.bumptech.glide.Glide
import com.example.any1.R
import com.example.any1.databinding.ActivityGroupinfoBinding
import com.example.any1.databinding.MemberoptionsbottomsheetBinding
import com.example.any1.feature_group.domain.adapter.MembersAdapter
import com.example.any1.feature_group.domain.adapter.RequestAdapter
import com.example.any1.feature_group.domain.interfaces.OnConnectClickListener
import com.example.any1.feature_group.domain.interfaces.OnMemberClickListener
import com.example.any1.feature_group.domain.interfaces.OnMenuClickListener
import com.example.any1.feature_group.domain.interfaces.OnRequestClickListener
import com.example.any1.feature_group.domain.interfaces.RequestRemovedListener
import com.example.any1.feature_login.presentation.SearchTags
import com.example.any1.feature_profile.ViewProfile
import com.example.any1.feature_tags.SearchTagsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikhaellopez.circularimageview.CircularImageView
import java.util.*


class GroupInfo : AppCompatActivity(), OnMemberClickListener, OnMenuClickListener,
    OnConnectClickListener,
    RequestRemovedListener {
    private lateinit var toolbar: Toolbar
    private lateinit var groupphoto: CircularImageView
    private lateinit var gcname :  EditText
    private lateinit var edittags: ImageView
    private lateinit var tagrecyclerview: RecyclerView
    private val sortedMemberList = ArrayList<MemberModel>()
    private lateinit var memberRecyclerView: RecyclerView
    var isMemberAdmin = false
    var reqsize = 0
    private val listFromFirestore = ArrayList<String>()
    private val listFromRepository = ArrayList<String>()
    private var requestModelList = ArrayList<RequestModel>()
    private var isAdmin = false
    private var model = RequestModel("","","",false,"")
    private lateinit var requestsRecyclerView: RecyclerView
    private lateinit var requestAdapter : RequestAdapter
    private lateinit var mutemessages: SwitchMaterial
    private lateinit var mutecalls: SwitchMaterial
    private val adminList = ArrayList<String>()
    private lateinit var nosimping: SwitchMaterial
    private lateinit var videocall: SwitchMaterial
    private val memberModeList = ArrayList<MemberModel>()
    private lateinit var approval: SwitchMaterial
    private lateinit var rankupdates: CheckBox
    private var currentUserMemberModel : MemberModel = MemberModel("","","",false,false,"")
    private lateinit var tagadapter: SearchTagsAdapter
    private lateinit var memberAdapter: MembersAdapter
    private lateinit var addmembers: TextView
    private lateinit var tagPreferences : SharedPreferences
    private lateinit var addmemberimageview: ImageView
    private var requestList = ArrayList<String>()
    private lateinit var requestsText : TextView
    private lateinit var addtogroup : Button
    private var imageurl = ""
    private lateinit var gctag : String
    private var memberList = ArrayList<String>()
    private lateinit var leavechat: TextView
    var groupname = ""
    private lateinit var membersViewModel: MembersVM
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var groupInfoViewModel : GroupInfoVM
    private var nosimpingbool : Boolean = false
    private var videocallbool : Boolean = false
    private lateinit var view : ActivityGroupinfoBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val themepreferences = getSharedPreferences(packageName+"theme", MODE_PRIVATE)
        if(themepreferences.getString("theme","")=="dark"){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        super.onCreate(savedInstanceState)
        tagPreferences = getSharedPreferences(packageName+"tag", MODE_PRIVATE)
        if(intent!=null && intent.getStringExtra("chat")=="chat"){
            tagPreferences.edit().clear().apply()
        }
        setContentView(R.layout.activity_groupinfo)
        view = ActivityGroupinfoBinding.inflate(layoutInflater)
        sharedPreferences = getSharedPreferences(packageName + "user", MODE_PRIVATE)
        gcname = findViewById(R.id.groupname)
        edittags = findViewById(R.id.edittags)
        val changephoto = findViewById<TextView>(R.id.changegroupphoto)
        tagrecyclerview = findViewById(R.id.tagrecyclerview)
        memberRecyclerView= findViewById(R.id.memberrecyclerview)
        mutecalls = findViewById(R.id.mutecall)
        mutemessages = findViewById(R.id.mutemessages)
        nosimping = findViewById(R.id.nosimping)
        approval = findViewById(R.id.approval)
        val tags = findViewById<TextView>(R.id.tags)
        val lock = findViewById<ImageView>(R.id.lock)
        var originalGroupName = intent.getStringExtra("gcname").toString()
        rankupdates = findViewById(R.id.rankingupdates)
        addmemberimageview = findViewById(R.id.addbutton)
        addmembers = findViewById(R.id.addmember)
        requestsText = findViewById<TextView>(R.id.requeststext)
        addtogroup = findViewById<Button>(R.id.addtogc)
        leavechat = findViewById(R.id.leavegroup)
        requestsRecyclerView = findViewById(R.id.requests)
        gcname.setText(originalGroupName)
        var hidetags = false
        var namelock = true
        requestsRecyclerView.layoutManager = LinearLayoutManager(this)
        videocall = findViewById(R.id.videocallenable)
        val accessibility = findViewById<AppCompatSpinner>(R.id.accessibility)
        toolbar = findViewById(R.id.gcinfotoolbar)
        toolbar.setNavigationOnClickListener { slideleft() }
        val edittagssettings = findViewById<ImageView>(R.id.edittagssettings)
        val accessibilitytext = findViewById<TextView>(R.id.textView16)
        val go = findViewById<ImageView>(R.id.go)
        val membercount = findViewById<TextView>(R.id.membercount)
        groupphoto = findViewById(R.id.gcphoto)
        gctag = intent.getStringExtra("tag").toString()
        requestList.clear()
        go.visibility = View.GONE
        membersViewModel = ViewModelProvider(this).get(MembersVM::class.java)
        groupInfoViewModel = ViewModelProvider(this).get(GroupInfoVM::class.java)
        requestAdapter = RequestAdapter(this, object : OnRequestClickListener {
            override fun onRequestClicked(position: Int, requestList: ArrayList<RequestModel>) {
                val intent = Intent(this@GroupInfo, ViewProfile::class.java)
                intent.putExtra("name",requestList[position].name)
                intent.putExtra("username",requestList[position].username)
                intent.putExtra("uri", requestList[position].uri)
                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    this@GroupInfo,
                    R.anim.animenterslow,
                    R.anim.animexitslow
                ).toBundle()
                startActivity(intent,bndlAnimation)
            }
        },gctag,this)
        groupInfoViewModel.getGroupInfo(gctag)
        groupInfoViewModel.liveGroupInfoModelList.observe(this){
            adminList.clear()
            adminList.addAll(it.adminList)
            for (i in adminList){
                if(i == auth.currentUser!!.uid){
                    isAdmin = true
                }
            }
            if(it.membercount.toString()== "1"){
                membercount.text = "${it.membercount} member"
            }else{
                membercount.text = "${it.membercount} members"
            }
            groupname = it.name
            memberList = it.membersList
            namelock = it.namelock
            imageurl = it.imageurl
            if(it.imageurl!="" && it.imageurl!="male" && it.imageurl!="female")Glide.with(applicationContext).load(it.imageurl).circleCrop().into(groupphoto)
            else groupphoto.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.wojakgc))
            if(isAdmin){
                lock.visibility = View.VISIBLE
                accessibility.visibility = View.VISIBLE
                accessibilitytext.visibility = View.VISIBLE
                videocall.visibility = View.VISIBLE
                approval.visibility = View.VISIBLE
                if(!namelock){
                    lock.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_cil_lock_unlocked))
                    val dialog = Dialog(this)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.setContentView(R.layout.namelockdialog)
                    dialog.findViewById<TextView>(R.id.ok).setOnClickListener{dialog.dismiss()}
                    dialog.show()
                }else{
                    lock.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_cil_lock_locked))
                }
                nosimping.isChecked = it.nosimping
                videocall.isChecked = it.videocall
                rankupdates.isChecked = it.rankupdates
                gcname.setText(it.name)
                originalGroupName = it.name
                if(it.accessibility.lowercase() == "public"){
                    accessibility.setSelection(0)
                }else{
                    accessibility.setSelection(1)
                }
                approval.isChecked = it.approval
            }else{
                gcname.isEnabled = !namelock
                accessibility.visibility = View.GONE
                accessibilitytext.visibility = View.GONE
                videocall.visibility = View.GONE
                approval.visibility = View.GONE
                rankupdates.visibility = View.GONE
                nosimping.visibility = View.GONE
                requestsText.visibility = View.GONE
                requestsRecyclerView.visibility = View.GONE
                addtogroup.visibility = View.GONE
                lock.visibility = View.GONE
            }

            if (it.hidetags && !isAdmin) {
                tags.visibility = View.GONE
                tagrecyclerview.visibility = View.GONE
                requestsText.visibility = View.GONE
                addtogroup.visibility = View.GONE
            } else {
                tagadapter = SearchTagsAdapter(this, isAdmin,gctag)
                tagrecyclerview.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                tagadapter.setTags(it.searchTags)
                when (it.searchTags.size) {
                    1 -> {
                        tagPreferences.edit().putString("primarytag", it.searchTags[0]).apply()
                        tagPreferences.edit().putString("counts", "1").apply()
                    }
                    2 -> {
                        tagPreferences.edit().putString("primarytag", it.searchTags[0]).apply()
                        tagPreferences.edit().putString("secondarytag", it.searchTags[1])
                            .apply()
                        tagPreferences.edit().putString("counts", "2").apply()
                    }
                    3 -> {
                        tagPreferences.edit().putString("primarytag", it.searchTags[0]).apply()
                        tagPreferences.edit().putString("secondarytag", it.searchTags[1])
                            .apply()
                        tagPreferences.edit().putString("tertiarytag", it.searchTags[2])
                            .apply()
                        tagPreferences.edit().putString("counts", "3").apply()
                    }
                }
                tagrecyclerview.adapter = tagadapter
            }
        }


        val accessibilityOptions = arrayListOf("Public", "Private")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, accessibilityOptions)
        accessibility.adapter = adapter
        approval.setOnCheckedChangeListener { button, bool ->
            firestore.collection("groups").document(gctag).update(
                "approval", bool
            )
        }


        edittagssettings.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.edittagssettings)
            val hide = dialog.findViewById<TextView>(R.id.hide)
            val cancel = dialog.findViewById<TextView>(R.id.cancel)
            hide.setOnClickListener {
                firestore.collection("groups").document(gctag).update("hidetags", true)
                    .addOnSuccessListener { dialog.dismiss() }
            }
            cancel.setOnClickListener { dialog.dismiss() }
        }

        nosimping.setOnCheckedChangeListener { button, bool ->
            firestore.collection("groups").document(gctag).update("nosimping", bool)
        }

        accessibility.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    p1: View?,
                    i: Int,
                    p3: Long
                ) {
                    firestore.collection("groups").document(gctag)
                        .update("accessibility", accessibilityOptions[i])
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }

        videocall.setOnCheckedChangeListener { button, bool ->
            firestore.collection("groups").document(gctag).update(
                "videocall", bool
            )
        }

        edittags.setOnClickListener {
            val intent = Intent(this, SearchTags::class.java)
            intent.putExtra("donotdelete", "donotdelete")
            intent.putExtra("gc", "gc")
            intent.putExtra("gctag",gctag)
            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.anim_enter,
                R.anim.anim_exit
            ).toBundle()
            startActivity(intent, bndlAnimation)
        }

        rankupdates.setOnCheckedChangeListener { button, bool ->
            firestore.collection("groups").document(gctag).update("rankupdates", bool)
        }


        membersViewModel.getRequests(gctag)
        if(membersViewModel.liveRequestList.value == null) {
            requestsText.visibility = View.GONE
            addtogroup.visibility = View.GONE
        }
        membersViewModel.liveRequestList.observe(this){
            if(isAdmin){
                if(it == null || it.isEmpty()){
                    requestsText.visibility = View.GONE
                    addtogroup.visibility = View.GONE
                    addtogroup.visibility = View.GONE
                    requestsRecyclerView.visibility = View.GONE
                }else{
                    if(requestModelList.isNotEmpty()){
                        Log.d("rqsts", "from if:$it")
                        Log.d("rqsts","requestmodellist from if : $requestModelList")
                        requestsText.visibility = View.VISIBLE
                        Toast.makeText(this@GroupInfo, "upar", Toast.LENGTH_SHORT).show()
                        addtogroup.visibility = View.VISIBLE
                        requestsRecyclerView.visibility = View.VISIBLE
                        requestAdapter.clearList()
                        requestAdapter.setRequestList(requestModelList)
                        requestsRecyclerView.adapter = requestAdapter
                    }else{
                        Toast.makeText(this, "neeche", Toast.LENGTH_SHORT).show()
                        Log.d("rqsts",it.toString())
                        Log.d("rqsts","requestmodellist : $requestModelList")
                        requestsText.visibility = View.VISIBLE
                        addtogroup.visibility = View.VISIBLE
                        requestsRecyclerView.visibility = View.VISIBLE
                        requestAdapter.clearList()
                        requestModelList.clear()
                        requestModelList.addAll(it)
                        requestAdapter.setRequestList(requestModelList.distinct())
                        requestsRecyclerView.adapter = requestAdapter
                        requestList.clear()
                        for(i in it){
                            requestList.add(i.id)
                        }
                    }
                }
            }
        }

        var str = ""


        addtogroup.setOnClickListener {
            val arrayList = requestAdapter.getUserIdOfCheckedRequests()
            if(arrayList.isNotEmpty()){
                for(i in arrayList){
                    if(memberList.isNotEmpty()){
                        memberList.add(i)
                        requestList.remove(i)
                        for(j in requestModelList){
                            if(j.id == i){
                                model = j
                            }
                        }
                        if(model.username!="" && model.id!="")  requestModelList.remove(model)
                        firestore.collection("groups").document(gctag).update("members",memberList,"membercount",memberList.size,"requests",requestList).addOnSuccessListener {
                            memberAdapter.notifyDataSetChanged()
                            requestAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        gcname.addTextChangedListener(
            object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                private var timer = Timer()
                private val DELAY: Long = 1000 // Milliseconds
                override fun afterTextChanged(s: Editable) {
                    str = s.toString()
//                    Toast.makeText(this, str + "yoyo$originalGroupName", Toast.LENGTH_SHORT).show()
//            firestore.collection("groups").document(gctag).update("name",str)
                    if(str == originalGroupName) go.visibility = View.GONE
                    else go.visibility = View.VISIBLE
                }
            }
        )

        go.setOnClickListener {
            firestore.collection("groups").document(gctag).update("name",str).addOnSuccessListener {
                Toast.makeText(this, "Group name has been changed successfully", Toast.LENGTH_SHORT).show()
                go.visibility = View.GONE
            }
        }

        mutemessages.setOnCheckedChangeListener{
            button , bool ->
            firestore.collection("users").document(auth.currentUser!!.uid).collection("groups").document(gctag).update(
                "messagemuted", bool
            )
        }

        lock.setOnClickListener {
            if(namelock){
                namelock = false
                firestore.collection("groups").document(gctag).update("namelock",false).addOnSuccessListener {
                    lock.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_cil_lock_unlocked))
                }
            }else{
                namelock = true
                firestore.collection("groups").document(gctag).update("namelock",true).addOnSuccessListener {
                    lock.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_cil_lock_locked))
                }
            }
        }


        mutecalls.setOnCheckedChangeListener{
                button , bool ->
            firestore.collection("users").document(auth.currentUser!!.uid).collection("groups").document(gctag).update(
                "callmuted", bool
            )
        }


        memberRecyclerView.layoutManager = LinearLayoutManager(this)
        memberAdapter = MembersAdapter(this,this,this,this,isAdmin)
        membersViewModel.getMembers(gctag)
        membersViewModel.liveData.observe(this){
//            Log.d("memberss",it.toString())
//            Log.d("memberss","membermodellist : $memberModeList")
            if(memberModeList.sortBy {it.memberusername } == it.sortBy { it.memberusername } && memberModeList.isNotEmpty() && it.isNotEmpty()){
                firestore.collection("groups").document(gctag).get().addOnSuccessListener {
                    value ->
                    if(value.get("members") != null && value.get("admins") !=""){
                        var membername = ""
                        var username= ""
                        var uri = ""
                        var isConnected = false
                        val membersArrayList = ArrayList<MemberModel>()
                        val adminlist = value.get("admins") as ArrayList<String>
                        val memberlist =  value.get("members") as ArrayList<String>
                        for(i in memberlist){
                            firestore.collection("users").document(i).get().addOnSuccessListener {
                                membername = it.getString("displayname").toString()
                                username = it.getString("username").toString()
                                uri = it.getString("imageurl").toString()
                                firestore.collection("users").document(auth.currentUser!!.uid).collection("connections").document(i).get().addOnSuccessListener {
                                        userdoc ->
                                    isConnected = userdoc.exists()
                                }.addOnFailureListener{isConnected = false}
                                isMemberAdmin = adminlist.contains(i)
                                val model = MemberModel(membername,username,uri,isConnected,isMemberAdmin,i)
                                membersArrayList.add(model)
                                val filteredList = membersArrayList.distinct()
                                val filteredArrayList = ArrayList(filteredList)
                                for(i in filteredArrayList){
                                    if(i.memberusername == sharedPreferences.getString("username","")){
                                        currentUserMemberModel = i
                                    }
                                }
                                if(currentUserMemberModel.memberusername!="" && currentUserMemberModel.membername!=""){
                                    filteredArrayList.remove(currentUserMemberModel)
                                    filteredArrayList.add(0,currentUserMemberModel)
                                }
                                memberModeList.clear()
                                memberModeList.addAll(filteredArrayList)
                                memberAdapter.clearList()
                                memberAdapter.setMembersList(filteredArrayList)
                            }
                        }
                    }
                }
            }else{
                memberAdapter.clearList()
                for(i in it){
                    if(i.memberusername == sharedPreferences.getString("username","")){
                        currentUserMemberModel = i
                    }
                }
                if(currentUserMemberModel.memberusername!="" && currentUserMemberModel.membername!=""){
                    it.remove(currentUserMemberModel)
                    it.add(0,currentUserMemberModel)
                }
                memberModeList.clear()
                memberModeList.addAll(it)
                memberAdapter.setMembersList(it)
                memberRecyclerView.adapter = memberAdapter
            }
        }
    }

    override fun onDestroy() {
        membersViewModel.liveRequestList.removeObserver { lifecycle }
        groupInfoViewModel.stopListening()
        membersViewModel.stopListeningForGroupInfo()
        super.onDestroy()
    }

    private suspend fun getSortedMemberList(it : ArrayList<MemberModel>):ArrayList<MemberModel>{
        val suspendModel = getCurrentUserMemberModel(it)
        it.remove(suspendModel)
        it.add(0,suspendModel)
        return it
    }

    private suspend fun getCurrentUserMemberModel(it : ArrayList<MemberModel>): MemberModel {
        for(i in it){
            if(i.memberusername == sharedPreferences.getString("username","")){
                currentUserMemberModel = i
            }
        }
        return currentUserMemberModel
    }
    override fun onStop() {
        groupInfoViewModel.stopListening()
        super.onStop()
    }

    override fun onPause() {
        groupInfoViewModel.stopListening()
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {

        super.onNewIntent(intent)
    }

    private fun slideleft(){
        tagPreferences.edit().clear().apply()
        val intent = Intent(this, Chat::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("gctag",gctag)
        intent.putExtra("imageurl",imageurl)
        intent.putExtra("gcname",groupname)
//        val bndlAnimation = ActivityOptions.makeCustomAnimation(
//            applicationContext,
//        ).toBundle()
        startActivity(intent)
        overridePendingTransition(
            R.anim.slideleft2,
            R.anim.slideleft1
        )
    }

    override fun onConnectClicked(position: Int, membersArrayList: ArrayList<MemberModel>) {

    }

    override fun onGroupMemberClicked(position: Int, memberArrayList: ArrayList<MemberModel>) {
        val intent = Intent(this, ViewProfile::class.java)
        intent.putExtra("groupinfo", "groupinfo")
        intent.putExtra("memberusername",memberArrayList[position].memberusername)
        val bndlAnimation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.anim_enter,
            R.anim.anim_exit
        ).toBundle()
        startActivity(intent,bndlAnimation)
    }

    override fun onMenuClicked(position: Int, memberModeList: ArrayList<MemberModel>) {
        val dialog = BottomSheetDialog(this)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = MemberoptionsbottomsheetBinding.inflate(inflater)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Dialoganimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
        binding.displaynameofmember.text = memberModeList[position].membername
        if(isAdmin){
            binding.removeuser.visibility = View.VISIBLE
            binding.makeadmin.visibility = View.VISIBLE
            if(memberModeList[position].isAdmin){
                binding.makeadmin.text = "Remove admin"
                binding.makeadmin.setTextColor(AppCompatResources.getColorStateList(this,R.color.black))
            }else{
                binding.makeadmin.text = "Make admin"
                binding.makeadmin.setTextColor(AppCompatResources.getColorStateList(this,R.color.fblue))
            }
        }else{
            binding.removeuser.visibility = View.GONE
            binding.makeadmin.visibility = View.GONE
        }
        binding.removeuser.setOnClickListener {
            if (memberList.isNotEmpty()) {
                if(memberModeList[position].isAdmin){
                    memberList.remove(memberModeList[position].id)
                    adminList.remove(memberModeList[position].id)
                    firestore.collection("groups").document(gctag)
                        .update("members", memberList, "membercount", memberList.size,"admins",adminList)
                        .addOnSuccessListener {
//                            memberModeList.removeAt(position)
//                            memberAdapter.clearList()
//                            memberAdapter.setMembersList(memberModeList)
                            memberAdapter.notifyDataSetChanged()
//                        memberAdapter.notifychange()
                            dialog.dismiss()
                        }
                }else {
                    memberList.remove(memberModeList[position].id)
                    firestore.collection("groups").document(gctag)
                        .update("members", memberList, "membercount", memberList.size)
                        .addOnSuccessListener {
//                            memberModeList.removeAt(position)
//                            memberAdapter.clearList()
//                            memberAdapter.setMembersList(memberModeList)
                            memberAdapter.notifyDataSetChanged()
//                        memberAdapter.notifychange()
                            dialog.dismiss()
                        }
                }
            }
        }
        binding.makeadmin.setOnClickListener {
            if(binding.makeadmin.text == "Make admin") {
                if (adminList.isNotEmpty()) {
                    adminList.add(memberModeList[position].id)
                    firestore.collection("groups").document(gctag).update("admins", adminList)
                        .addOnSuccessListener {
                            memberAdapter.notifyDataSetChanged()
                            dialog.dismiss()
                        }
                }
            }else{
                if (adminList.isNotEmpty()) {
                    adminList.remove(memberModeList[position].id)
                    firestore.collection("groups").document(gctag).update("admins", adminList)
                        .addOnSuccessListener {
                            memberAdapter.notifyDataSetChanged()
                            dialog.dismiss()
                        }
                }
            }
        }
        dialog.show()
    }

    override fun onRequestRemoved() {
        requestsText.visibility = View.GONE
        addtogroup.visibility = View.GONE
        requestsRecyclerView.visibility = View.GONE
    }
}
