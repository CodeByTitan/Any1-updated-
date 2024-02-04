package com.example.any1.feature_group.domain.model

data class GroupInfoModel(val name : String,
                          val adminList: ArrayList<String>,
                          val approval : Boolean,
                          val tag : String,
                          val hidetags : Boolean,
                          val imageurl : String,
                          val namelock : Boolean,
                          val nosimping : Boolean,
                          val rankupdates : Boolean,
                          val searchTags : ArrayList<String>,
                          val videocall : Boolean,
                          val requestList : ArrayList<String>,
                          val membersList : ArrayList<String>,
                          val membercount : Long,
                          val accessibility : String)
