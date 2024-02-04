package com.example.any1.util.sharedpref

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.key

object SharedPreferenceHelper {
    fun putString(context : Context, sharedPref : String, key : String, value : String){
        val sharedPreferences = context.getSharedPreferences(context.packageName+sharedPref,MODE_PRIVATE)
        sharedPreferences.edit().putString(key,value).apply()
    }
    fun putMultipleStrings(context : Context, sharedPref : String, hashMap: HashMap<String,String>){
        val sharedPreferences = context.getSharedPreferences(context.packageName+sharedPref,MODE_PRIVATE)
        hashMap.forEach {
            sharedPreferences.edit().putString(it.key,it.value).apply()
        }
    }

    fun getString(context : Context, sharedPref : String, key : String): String{
        val sharedPreferences = context.getSharedPreferences(context.packageName+sharedPref,MODE_PRIVATE)
        return sharedPreferences.getString(key,"").toString()
    }

    fun getMultipleString(context : Context, sharedPref : String, listOfKeys : List<String>): HashMap<String,String>{
        val hashMap = HashMap<String,String>()
        val sharedPreferences = context.getSharedPreferences(context.packageName+sharedPref,MODE_PRIVATE)
        listOfKeys.forEach { key ->
            hashMap[key] = sharedPreferences.getString(key, "").toString()
        }
        return hashMap
    }
    fun clearPref(context: Context, sharedPref: String){
        val sharedPreferences = context.getSharedPreferences(context.packageName+sharedPref,MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

}