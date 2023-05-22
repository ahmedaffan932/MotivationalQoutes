package com.example.motivational.qoutes.activities

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.lifecycle.ViewModelProvider
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivityFullViewBinding
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilMiscs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FullViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullViewBinding
    private var param1: QuotModel? = null
    private lateinit var vMdl: QuotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFullViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vMdl = ViewModelProvider(this)[QuotViewModel::class.java]
        param1=intent.getParcelableExtra("mdl")
        binding.quotLayout.qoutData.text=param1?.Quote
        binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.getRandomWallpaper())
        updateUi()
        btnClicks()

        binding.quotLayout.root.setOnClickListener {
            binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.getRandomWallpaper())
        }
    }

    private fun btnClicks() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnCloseFullScreen.setOnClickListener {
            onBackPressed()
        }
        binding.btnFav.setOnClickListener {
            if (param1?.isFav==1){
                param1?.isFav=0
            }
            else{
                param1?.isFav=1
            }
            CoroutineScope(Dispatchers.IO).launch {
                vMdl.updateQoute(param1!!)
            }
            updateUi()
        }

        binding.btnCopy.setOnClickListener {
            UtilMiscs.copyToClip(this,param1?.Quote?:"")
            UtilMiscs.showSnackBar(binding.root,"Quote Copied!")
        }

        binding.btnDown.setOnClickListener {
            downloadImg()
        }

        binding.btnShare.setOnClickListener {
            downloadImg()
            val model=
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"${param1?.Category} ${param1?.id}.jpg")

            val intentBuilder: ShareCompat.IntentBuilder =
                ShareCompat.IntentBuilder.from(this)
                    .setType("image/*")
            intentBuilder.addStream(
                FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    model
                )
            )
            val intent = intentBuilder.intent.setAction(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "Send to "))
        }

    }


    private fun downloadImg() {
        UtilMiscs.saveMediaToStorage(this,binding.quotLayout.root.drawToBitmap(),"${param1?.Category} ${param1?.id}")

    }

    private fun updateUi(){
        if (param1?.isFav==1){
            binding.btnFav.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.clr_blue,theme))
        }
        else{
            binding.btnFav.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.white,theme))
        }
    }
}