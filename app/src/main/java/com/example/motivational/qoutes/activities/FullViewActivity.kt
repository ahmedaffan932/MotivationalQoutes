package com.example.motivational.qoutes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.ads.*
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivityFullViewBinding
import com.example.motivational.qoutes.fragments.FullScreenQuoteFragment
import com.example.motivational.qoutes.interfaces.InterfaceMisClick
import com.example.motivational.qoutes.utils.CustomDialog
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.Misc
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FullViewActivity : AppCompatActivity(), InterfaceMisClick {
    private lateinit var binding: ActivityFullViewBinding
    private var cat = ""
    private var position = 0
    private lateinit var vMdl: QuotViewModel
    private var lstQuot = ArrayList<QuotModel?>()
    private var activeQoute: QuotModel? = null
    private var myLoader: CustomDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vMdl = QuotViewModel(application)
        cat = intent.getStringExtra("cat") ?: ""
        position = intent.getIntExtra("pos", 0)

        if (intent.getStringExtra("quote") != null) {
            val objQuote = Gson().fromJson(intent.getStringExtra("quote"), QuotModel::class.java)
            cat = objQuote.Category
            Log.d("logKeyQuote", objQuote.Quote)
            lstQuot.add(0, objQuote)
        }


        myLoader = Misc.showProgressD(this@FullViewActivity)
        if (UtilSharedPerefs.getIsGuideAllowedToShow(this@FullViewActivity)) {
            Misc.showGuide(
                this@FullViewActivity,
                "handClick.json",
                resources.getString(R.string.handClickString)
            ).setOnDismissListener {
                Misc.showGuide(
                    this@FullViewActivity,
                    "slide.json",
                    resources.getString(R.string.slideString)
                )
            }
            UtilSharedPerefs.setIsGuideAllowedToShow(this@FullViewActivity, false)
        }

        if (cat == "MFAV") {
            binding.tvHeading.text = "Favourite"
            CoroutineScope(Dispatchers.IO).launch {
                for (i in vMdl.readAllFav()) {
                    lstQuot.add(i)
                }
                if (lstQuot.isNotEmpty()) {
                    activeQoute = lstQuot[0]
                }
                //adding ads in list
                if (Ads.inBetweenQuotesNativeAdPosition > 1) {
                    for (i in 0 until lstQuot.size) {
                        if (i >= Ads.inBetweenQuotesNativeAdStartingIndex) {
                            if (i % Ads.inBetweenQuotesNativeAdPosition == 0) {
                                lstQuot.add(i, null)
                            }
                        }
                    }
                }
                runOnUiThread {
                    binding.quotesViewPager.adapter =
                        QuotesPagerAdapter(this@FullViewActivity)
                    binding.quotesViewPager.offscreenPageLimit = 1
                    myLoader?.dismiss()
                    binding.quotesViewPager.currentItem = position
                    if (lstQuot.size > 0) {
                        binding.emptyFlag.visibility = View.GONE
                    } else {
                        binding.emptyFlag.visibility = View.VISIBLE
                        binding.emptyLottie.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            binding.tvHeading.text = cat.toUpperCase()
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
                if (Ads.inBetweenQuotesNativeAdPosition > 1) {
                    for (i in 0 until lstQuot.size) {
                        if (i >= Ads.inBetweenQuotesNativeAdStartingIndex) {
                            if (i % Ads.inBetweenQuotesNativeAdPosition == 0) {
                                lstQuot.add(i, null)
                            }
                        }
                    }
                }
                runOnUiThread {
                    binding.quotesViewPager.adapter =
                        QuotesPagerAdapter(this@FullViewActivity)
                    binding.quotesViewPager.offscreenPageLimit = 1
                    myLoader?.dismiss()
                    binding.quotesViewPager.currentItem = position
                    if (lstQuot.size > 0) {
                        binding.emptyFlag.visibility = View.GONE
                    } else {
                        binding.emptyFlag.visibility = View.VISIBLE
                        binding.emptyLottie.visibility = View.VISIBLE
                    }
                }
            }
        }


        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        Ads.showInterstitial(this, Ads.backQuoteStudioIntAm, object : InterstitialCallBack{
            override fun onDismiss() {
                finish()
            }
        })
    }

    private inner class QuotesPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return lstQuot.size

        }

        override fun createFragment(position: Int): Fragment {
            return FullScreenQuoteFragment.newInstance(lstQuot[position], position)
        }
    }

    override fun onMisTouch(model: QuotModel?): Boolean {
        return if (binding.quotesViewPager.currentItem == lstQuot.indexOf(model)) {
            false
        } else {
            binding.quotesViewPager.currentItem = lstQuot.indexOf(model)
            true
        }
    }

    override fun onWallChange(wall: Int) {
        binding.qoutWallpaper.setImageResource(UtilLists.wallpapers[wall])
    }

}