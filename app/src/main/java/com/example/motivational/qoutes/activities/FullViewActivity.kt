package com.example.motivational.qoutes.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.InterstitialAds
import com.example.motivational.qoutes.ads.InterstitialCallback
import com.example.motivational.qoutes.ads.NativeAd
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivityFullViewBinding
import com.example.motivational.qoutes.fragments.FullScreenQuoteFragment
import com.example.motivational.qoutes.fragments.QuoteFragment
import com.example.motivational.qoutes.interfaces.InterfaceMisClick
import com.example.motivational.qoutes.utils.CustomDialog
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilMiscs
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FullViewActivity : AppCompatActivity(),InterfaceMisClick {
    private lateinit var binding: ActivityFullViewBinding
    private var cat = ""
    private var position = 0
    private lateinit var vMdl: QuotViewModel
    private var lstQuot= ArrayList<QuotModel?>()
    private var activeQoute:QuotModel?=null
    private var myLoader: CustomDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFullViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        vMdl = QuotViewModel(application)
        cat = intent.getStringExtra("cat") ?: ""
        position=intent.getIntExtra("pos",0)
        NativeAd.showPreFetch(this,Ads.quoteStudioNativeAm,binding.adFrameLayout,null)

        InterstitialAds.showInterstitialAdmob(this,this, Ads.quoteStudioIntAm,object :InterstitialCallback{
            override fun onResult() {
                myLoader=UtilMiscs.showProgressD(this@FullViewActivity)
                if (UtilSharedPerefs.getIsGuideAllowedToShow(this@FullViewActivity)){
                    UtilMiscs.showGuide(this@FullViewActivity,"handClick.json",resources.getString(R.string.handClickString)).setOnDismissListener {
                        UtilMiscs.showGuide(this@FullViewActivity,"slide.json",resources.getString(R.string.slideString))
                    }
                    UtilSharedPerefs.setIsGuideAllowedToShow(this@FullViewActivity,false)
                }

                if (cat=="MFAV") {
                    CoroutineScope(Dispatchers.IO).launch {
                        for (i in vMdl.readAllFav()) {
                            lstQuot.add(i)
                        }
                        if (lstQuot.isNotEmpty()) {
                            activeQoute = lstQuot[0]
                        }
                        //adding ads in list
                        if (Ads.inBetweenQuotesNativeAdPosition>1) {
                            for (i in 0 until lstQuot.size) {
                                if (i >= Ads.inBetweenQuotesNativeAdStartingIndex) {
                                    if (i % Ads.inBetweenQuotesNativeAdPosition == 0) {
                                        lstQuot.add(i, null)
                                    }
                                }
                            }
                        }
                        runOnUiThread { binding.quotesViewPager.adapter=QuotesPagerAdapter(this@FullViewActivity)
                            binding.quotesViewPager.offscreenPageLimit = 1
                            myLoader?.dismiss()
                            binding.quotesViewPager.currentItem=position
                            if (lstQuot.size>0){
                                binding.emptyFlag.visibility= View.GONE
                            }
                            else{
                                binding.emptyFlag.visibility= View.VISIBLE
                            }
                        }

                    }

                }
                else {
                    CoroutineScope(Dispatchers.IO).launch {
                        for (i in vMdl.readByCat(cat)) {
                            lstQuot.add(i)
                        }
                        if (cat == "") {
                            activeQoute = intent.getParcelableExtra("model")
                        } else {
                            if (lstQuot.isNotEmpty()) {
                                activeQoute = lstQuot[0]
                            }
                        }

                        //adding ads in list
                        if (Ads.inBetweenQuotesNativeAdPosition>1) {
                            for (i in 0 until lstQuot.size) {
                                if (i >= Ads.inBetweenQuotesNativeAdStartingIndex) {
                                    if (i % Ads.inBetweenQuotesNativeAdPosition == 0) {
                                        lstQuot.add(i, null)
                                    }
                                }
                            }
                        }
                        runOnUiThread { binding.quotesViewPager.adapter=QuotesPagerAdapter(this@FullViewActivity)
                            binding.quotesViewPager.offscreenPageLimit = 1
                            myLoader?.dismiss()
                            binding.quotesViewPager.currentItem=position
                            if (lstQuot.size>0){
                                binding.emptyFlag.visibility= View.GONE
                            }
                            else{
                                binding.emptyFlag.visibility= View.VISIBLE
                            }
                        }

                    }
                }
            }

        })


        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
    private inner class QuotesPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return lstQuot.size

        }
        override fun createFragment(position: Int): Fragment {
            return FullScreenQuoteFragment.newInstance(lstQuot[position],position)
        }
    }

    override fun onBackPressed() {
        InterstitialAds.showInterstitialAdmob(this,this, Ads.backQuoteStudioIntAm,object :
            InterstitialCallback {
            override fun onResult() {
                finish()
            }
        })
    }

    override fun onMisTouch(model: QuotModel?): Boolean {
        if (binding.quotesViewPager.currentItem==lstQuot.indexOf(model)){
            return false
        }
        else{
            binding.quotesViewPager.currentItem=lstQuot.indexOf(model)
            return true
        }
    }

    override fun onWallChange(wall: Int) {
        binding.qoutWallpaper.setImageResource(UtilLists.wallpapers[wall])
    }


}