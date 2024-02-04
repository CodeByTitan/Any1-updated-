package com.example.any1.feature_search.presentation

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.R
import com.example.any1.feature_chat.presentation.Chat
import com.example.any1.feature_search.domain.adapter.SearchAdapter
import com.example.any1.feature_search.domain.interfaces.OnSearchGroupClickListener
import com.example.any1.feature_search.domain.model.SearchModel
import com.example.any1.feature_search.domain.vm.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [gcsearchtabfrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchByTags : Fragment() , OnSearchGroupClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SearchViewModel
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var gctag : String
    private lateinit var gcname : String
    private var membercount : Long = 0
    private lateinit var imageurl : String
    private lateinit var adapter : SearchAdapter
    private lateinit var memberList : ArrayList<String>
    private lateinit var requestList : ArrayList<String>
    private val auth = FirebaseAuth.getInstance()
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
        val view = inflater.inflate(R.layout.fragment_searchbygctags, container, false)
        recyclerView = view.findViewById(R.id.searchgrouprecyclerview)
        adapter = SearchAdapter(this,requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)
        viewModel.searchstring.observe(viewLifecycleOwner){
            viewModel.getGroupsByTags()
            if(it==""){
                adapter.clearGroups()
                recyclerView.adapter = adapter
            }
        }
        viewModel.liveData.observe(viewLifecycleOwner){
            adapter.setFilteredGroups(it)
            recyclerView.adapter = adapter
        }
        return view
    }

    override fun OnGroupClicked(position: Int, groupModelList: ArrayList<SearchModel>) {
        gctag = groupModelList[position].tag
        gcname = groupModelList[position].item
        imageurl = groupModelList[position].uri
        membercount = groupModelList[position].membercount
        memberList = groupModelList[position].memberList
        requestList = groupModelList[position].requestList
        if(groupModelList[position].isApprovalRequired){
            showDialog(groupModelList[position].tag,groupModelList[position].requestList)
        }else{
            if(groupModelList[position].membercount < 30){
                if(!memberList.contains(auth.currentUser!!.uid)) {
                    memberList.add(auth.currentUser!!.uid)
                    firestore.collection("groups").document(gctag).update(
                        "members", memberList,
                        "membercount", membercount + 1
                    ).addOnSuccessListener {
                        startIntentToGroupChat()
                    }
                }else{
                    Toast.makeText(context, "You are already a member", Toast.LENGTH_SHORT).show()
                    startIntentToGroupChat()
                }
            }
        }
    }

    private fun startIntentToGroupChat(){
        val intent = Intent(requireActivity(), Chat::class.java)
        intent.putExtra("gctag", gctag)
        intent.putExtra("gcname", gcname)
        intent.putExtra("imageurl", imageurl)
        val bndlAnimation = ActivityOptions.makeCustomAnimation(
            context,
            R.anim.anim_enter,
            R.anim.anim_exit
        ).toBundle()
        startActivity(intent, bndlAnimation)
    }
    private fun showDialog(tag: String, list: ArrayList<String>) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.approvaldialog)
        val approvaltext = dialog.findViewById<TextView>(R.id.approvaltext)
        approvaltext.text = getString(R.string.approvaldialogtext,gcname)
        val sendrequest = dialog.findViewById<TextView>(R.id.sendrequest)
        val cancel  = dialog.findViewById<TextView>(R.id.approvalcancel)
        sendrequest.setOnClickListener {
            if (!memberList.contains(auth.currentUser!!.uid)) {
                if(!list.contains(auth.currentUser!!.uid)) {
                    list.add(auth.currentUser!!.uid)
                    firestore.collection("groups").document(tag).update("requests",
                        requestList
                    ).addOnSuccessListener {
                            Toast.makeText(context, "Request has been sent", Toast.LENGTH_SHORT)
                                .show()
                            dialog.dismiss()
                        }
                }else{
                    Toast.makeText(context, "You have already sent request to this group : $tag", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, list.toString(), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }else{
                Toast.makeText(context, "You are already a member", Toast.LENGTH_SHORT).show()
                startIntentToGroupChat()
                dialog.dismiss()
            }
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}