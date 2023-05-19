package com.example.motivational.qoutes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash)

        object : CountDownTimer(2000, 1000) {
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
}