package com.example.motivational.qoutes.utils

import android.content.Context
import android.content.SharedPreferences
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

    fun getQuote(context: Context):String{
        return sharedPreferencesVar(context).getString("storedQuote", "The best and most beautiful things in the world cannot be seen or even touched - they must be felt with the heart.")?:"The best and most beautiful things in the world cannot be seen or even touched - they must be felt with the heart."
    }

    fun setQuote(context: Context, quote:String){
        sharedPreferancesEditorVar(context).putString("storedQuote",quote).apply()
    }
}