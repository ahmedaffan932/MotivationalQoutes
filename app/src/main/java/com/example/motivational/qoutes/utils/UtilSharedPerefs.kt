package com.example.motivational.qoutes.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.motivational.qoutes.R

object UtilSharedPerefs {
    fun sharedPreferencesVar(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.resources.getString(R.string.shared_pref_name),
            AppCompatActivity.MODE_PRIVATE
        )
    }
    fun sharedPreferancesEditorVar(context: Context): SharedPreferences.Editor{
        return sharedPreferencesVar(context).edit()
    }

    fun getDate(context: Context):String{
        return sharedPreferencesVar(context).getString("storedDateKey", "")?:""
    }

    fun setDate(context: Context, date:String){
        sharedPreferancesEditorVar(context).putString("storedDateKey",date).apply()
    }

    fun getQuote(context: Context):Int{
        return sharedPreferencesVar(context).getInt("storedQuote",0)
    }

    fun setQuote(context: Context, quote:Int){
        Log.d("logkey","quoteIt: $quote")
        sharedPreferancesEditorVar(context).putInt("storedQuote",quote).apply()
    }

    fun setPurchasedStatus(context: Context, b: Boolean) {
        sharedPreferancesEditorVar(context).putBoolean("isPurchased",b).apply()
    }
    fun getPurchasedStatus(context: Context):Boolean{
        return sharedPreferencesVar(context).getBoolean("isPurchased",false)
    }
    fun setIsFullQuote(context: Context, b: Boolean) {
        sharedPreferancesEditorVar(context).putBoolean("isFullQuote",b).apply()
    }
    fun getIsFullQuote(context: Context):Boolean{
        return sharedPreferencesVar(context).getBoolean("isFullQuote",true)
    }

}