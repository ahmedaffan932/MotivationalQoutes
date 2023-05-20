package com.example.motivational.qoutes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivitySplashBinding
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilMiscs
import com.example.motivational.qoutes.utils.UtilMiscs.readJsonFromFile
import com.example.motivational.qoutes.utils.UtilMiscs.unzipFromAssets
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash)

        setup()

        object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // This method is called every second during the countdown
                // You can update UI or perform any task here if needed
            }

            override fun onFinish() {
                // This method is called after the countdown finishes
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }.start()
    }

    private fun setup() {
        val outputDirectory = filesDir.absolutePath
        if (!File(outputDirectory, "quotes.json").exists()) {
            val zipFileName = "archive.zip"
            Log.d("logkey", "PTH: $outputDirectory")
            unzipFromAssets(this, zipFileName, outputDirectory)

            val gson = Gson()
            val quotsJson = readJsonFromFile(filesDir.absolutePath + "/quotes.json")
            val quots = gson.fromJson(quotsJson, Array<QuotModel>::class.java).toList()
            QuotViewModel(application).insertUsers(quots)
            Log.d("logkey", "SZ: ${quots.size}")
        }



    }
}