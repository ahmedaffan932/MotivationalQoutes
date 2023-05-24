package com.example.motivational.qoutes.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.adapters.AdapterQouts
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivityQoutStudioBinding
import com.example.motivational.qoutes.interfaces.InterfaceCatClick
import com.example.motivational.qoutes.interfaces.InterfaceQuotClick
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilMiscs
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

class QoutStudioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQoutStudioBinding
    private var cat = ""
    private lateinit var vMdl: QuotViewModel
    private var lstQuot= listOf<QuotModel>()
    private var activeQoute:QuotModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQoutStudioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vMdl = QuotViewModel(application)
        cat = intent.getStringExtra("cat") ?: ""
        binding.recyclerQuots.layoutManager =LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        if (cat=="MFAV"){
            binding.recyclerQuots.visibility= View.VISIBLE
            lstQuot=vMdl.readAllFav()
            if (lstQuot.isNotEmpty()){
                activeQoute=lstQuot[0]
            }
        }
        else{
            binding.recyclerQuots.visibility= View.VISIBLE
            lstQuot=vMdl.readByCat(cat)
            if (cat==""){
                activeQoute=intent.getParcelableExtra("model")
            }
            else{
                if (lstQuot.isNotEmpty()){
                    activeQoute=lstQuot[0]
                }
            }
        }

        Log.d("logkey","List size: ${lstQuot.size}")

        binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.getRandomWallpaper())
        binding.quotLayout.qoutData.text=activeQoute?.Quote

        updateUi()

        btnClicks()

        binding.quotLayout.qoutData.text=activeQoute?.Quote
        binding.recyclerQuots.adapter = AdapterQouts(
            this@QoutStudioActivity,
            lstQuot,
            object : InterfaceQuotClick {
                override fun onClick(quot: QuotModel) {
                    activeQoute=quot
                    binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.getRandomWallpaper())
                    binding.quotLayout.qoutData.text=quot.Quote
                    if (quot.isFav==1){
                        binding.btnFav.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.clr_blue,theme))
                    }
                    else{
                        binding.btnFav.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.white,theme))
                    }

                    updateUi()
                }
            })
    }

    private fun btnClicks() {
        binding.btnFav.setOnClickListener {
            if (activeQoute?.isFav==1){
                activeQoute?.isFav=0
            }
            else{
                activeQoute?.isFav=1
            }
            CoroutineScope(Dispatchers.IO).launch {
                vMdl.updateQoute(activeQoute!!)
            }
            updateUi()
        }

        binding.btnCopy.setOnClickListener {
            UtilMiscs.copyToClip(this,activeQoute?.Quote?:"")
            UtilMiscs.showSnackBar(binding.root,"Quote Copied!")
        }

        binding.btnDown.setOnClickListener {
            downloadImg()
        }

        binding.btnShare.setOnClickListener {
            downloadImg()
            val model=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"$cat ${activeQoute?.id}.jpg")

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

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun downloadImg() {
        UtilMiscs.saveMediaToStorage(this,binding.quotLayout.root.drawToBitmap(),"$cat ${activeQoute?.id}")

    }

    private fun updateUi(){
        if (activeQoute?.isFav==1){
            binding.btnFav.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.clr_blue,theme))
        }
        else{
            binding.btnFav.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.white,theme))
        }
    }
}