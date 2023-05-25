package com.example.motivational.qoutes.database

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class CustomObjectConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        Log.d("logkey", "c")
        if (value!=null){
            Log.d("logkey", "CIF")
            val listType: Type = object : TypeToken<List<String>>() {}.type
            return Gson().fromJson(value, listType)
        }
        else{
            Log.d("logkey", "CELSE")
            return emptyList()
        }
    }

    @TypeConverter
    fun fromArrayList(list: List<String>): String {
        if (list!=null) {
            val gson = Gson()
            return gson.toJson(list)
        }
        else{
            return ""
        }
    }
}