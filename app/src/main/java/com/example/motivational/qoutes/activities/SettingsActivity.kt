package com.example.motivational.qoutes.activities

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.databinding.ActivitySettingsBinding
import com.example.motivational.qoutes.databinding.DialogRateBinding
import com.example.motivational.qoutes.utils.UtilMiscs

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnShare.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Download " + getString(R.string.app_name) + " now: https://play.google.com/store/apps/details?id=" + packageName
                )
                shareIntent.type = "text/plain"
                startActivity(shareIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.btnPrivacy.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/daily-positive-quotes/home")
            )
            startActivity(browserIntent)
        }

        binding.btnMoreApps.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=Elite+Translator")
                    )
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Link is Down", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRateUs.setOnClickListener {
            val alert = Dialog(this)
            val customLayoutBinding =
                DialogRateBinding.bind(layoutInflater.inflate(R.layout.dialog_rate, null))
            alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alert.setCancelable(true)
            alert.setCanceledOnTouchOutside(true)
            alert.setContentView(customLayoutBinding.root)
            alert.show()
            customLayoutBinding.btnNotNow.setOnClickListener {
                alert.dismiss()
            }
            customLayoutBinding.btnThumbDown.setOnClickListener {
                alert.dismiss()
                UtilMiscs.showSnackBar(binding.root, "Thank you for your rating!")
            }
            customLayoutBinding.btnThumbUp.setOnClickListener {
                alert.dismiss()
                goToStrore()
            }
        }
    }


    private fun goToStrore() {
        val uri =
            Uri.parse("market://details?id=" + applicationContext.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)

        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + applicationContext.packageName)
                )
            )
        }
    }


}