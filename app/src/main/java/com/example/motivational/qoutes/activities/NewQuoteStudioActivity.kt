package com.example.motivational.qoutes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivityNewQuoteStudioBinding
import com.example.motivational.qoutes.fragments.QuoteFragment
import com.example.motivational.qoutes.utils.HorizontalMarginItemDecoration
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import java.lang.Math.abs

class NewQuoteStudioActivity : AppCompatActivity() {
    private lateinit var binding:ActivityNewQuoteStudioBinding
    private var cat = ""
    private lateinit var vMdl: QuotViewModel
    private var lstQuot= listOf<QuotModel>()
    private var activeQoute:QuotModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNewQuoteStudioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vMdl = QuotViewModel(application)
        cat = intent.getStringExtra("cat") ?: ""
        if (cat=="QOD"){
            activeQoute=QuotModel(0,"","",0.0, UtilSharedPerefs.getQuote(this), listOf<String>(),0)
            lstQuot+=activeQoute!!
        }
        else if (cat=="MFAV"){
            lstQuot=vMdl.readAllFav()
            if (lstQuot.isNotEmpty()){
                activeQoute=lstQuot[0]
            }
        }
        else{
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
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.quotesViewPager.adapter=QuotesPagerAdapter(this)
        binding.quotesViewPager.offscreenPageLimit = 1

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
            Log.d("logkey","createFragment")
            return QuoteFragment.newInstance(lstQuot[position])
        }
    }

}