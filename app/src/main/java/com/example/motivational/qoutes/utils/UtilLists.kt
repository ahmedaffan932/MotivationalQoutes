package com.example.motivational.qoutes.utils

import com.example.motivational.qoutes.R
import java.util.Random

object UtilLists {
    fun getRandomWallpaper():Int{
        return wallpapers[Random().nextInt(wallpapers.size)]
    }
    val wallpapers = intArrayOf(
        R.drawable.rectangle1,
        R.drawable.rectangle2,
        R.drawable.rectangle3,
        R.drawable.rectangle4,
        R.drawable.rectangle5,
        R.drawable.rectangle6,
        R.drawable.rectangle7,
        R.drawable.rectangle8,
        R.drawable.rectangle9,
        R.drawable.rectangle10,
        R.drawable.rectangle11,
        R.drawable.rectangle12,
        R.drawable.rectangle13,
        R.drawable.rectangle14,
        R.drawable.rectangle15,
        R.drawable.rectangle16,
        R.drawable.rectangle17,
        R.drawable.rectangle18,
        R.drawable.rectangle19,
        R.drawable.rectangle20,
        R.drawable.rectangle21,
        R.drawable.rectangle22,
        R.drawable.rectangle23,
        R.drawable.rectangle24,
        R.drawable.rectangle25,
        R.drawable.rectangle26,
        R.drawable.rectangle27,
        R.drawable.rectangle28,
        R.drawable.rectangle29,
        R.drawable.rectangle30
    )
    val cats = arrayOf(
        "life",
        "happiness",
        "love",
        "truth",
        "inspiration",
        "humor",
        "philosophy",
        "science",
        "",
        "soul",
        "books",
        "wisdom",
        "knowledge",
        "education",
        "poetry",
        "hope",
        "friendship",
        "writing",
        "religion",
        "death",
        "romance",
        "success",
        "arts",
        "relationship",
        "motivation",
        "faith",
        "mind",
        "god",
        "funny",
        "quotes",
        "positive",
        "purpose"
    )
}