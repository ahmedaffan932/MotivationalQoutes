package com.example.motivational.qoutes.activities

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.InterstitialAds
import com.example.motivational.qoutes.ads.InterstitialCallback
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivityNewQuoteStudioBinding
import com.example.motivational.qoutes.fragments.QuoteFragment
import com.example.motivational.qoutes.utils.HorizontalMarginItemDecoration
import com.example.motivational.qoutes.utils.UtilMiscs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.abs

class NewQuoteStudioActivity : AppCompatActivity() {
    private lateinit var binding:ActivityNewQuoteStudioBinding
    private var cat = ""
    private lateinit var vMdl: QuotViewModel
    private var lstQuot= ArrayList<QuotModel?>()
    private var activeQoute:QuotModel?=null
    private var myLoader:ProgressDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNewQuoteStudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        InterstitialAds.showInterstitialAdmob(this,this, Ads.quoteStudioIntAm,null)

        myLoader=UtilMiscs.showProgressD(this)

        vMdl = QuotViewModel(application)
        cat = intent.getStringExtra("cat") ?: ""
        if (cat=="MFAV") {
            CoroutineScope(Dispatchers.IO).launch {
                for (i in vMdl.readAllFav()) {
                    lstQuot.add(i)
                }
                if (lstQuot.isNotEmpty()) {
                    activeQoute = lstQuot[0]
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
                runOnUiThread { binding.quotesViewPager.adapter=QuotesPagerAdapter(this@NewQuoteStudioActivity)
                    binding.quotesViewPager.offscreenPageLimit = 1
                myLoader?.dismiss()}

            }
        }


        binding.btnBack.setOnClickListener {
            onBackPressed()
        }


// Add a PageTransformer that translates the next and previous items horizontally
// towards the center of the screen, which makes them visible
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        binding.quotesViewPager.setPageTransformer { page, position ->
            page.translationY = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleX = 1 - (0.10f * abs(position))
            // If you want a fading effect uncomment the next line:
             page.alpha = 0.45f + (1 - abs(position))
        }

// The ItemDecoration gives the current (centered) item horizontal margin so that
// it doesn't occupy the whole screen width. Without it the items overlap
        val itemDecoration = HorizontalMarginItemDecoration(
            this,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding.quotesViewPager.addItemDecoration(itemDecoration)

        
    }



    private inner class QuotesPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return lstQuot.size

        }
        override fun createFragment(position: Int): Fragment {
            return QuoteFragment.newInstance(lstQuot[position])
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

}