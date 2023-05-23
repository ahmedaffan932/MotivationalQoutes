package com.example.motivational.qoutes.activities

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.adapters.AdapterCategories
import com.example.motivational.qoutes.adapters.AdapterQouts
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.InterstitialAds
import com.example.motivational.qoutes.ads.NativeAd
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivityMainBinding
import com.example.motivational.qoutes.fragments.QuoteFragment
import com.example.motivational.qoutes.fragments.TrendingFragment
import com.example.motivational.qoutes.interfaces.InterfaceCatClick
import com.example.motivational.qoutes.interfaces.InterfaceQuotClick
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var vMdl:QuotViewModel
    private var arrListTrendingKerosil=ArrayList<QuotModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        InterstitialAds.showInterstitialAdmob(this,this, Ads.dashboardIntAm,null)
        NativeAd.showPreFetch(this,Ads.dashboardNativeAm,binding.adFrameLayout,null)
        vMdl=QuotViewModel(application)

        //initialize kerosil list
        arrListTrendingKerosil.clear()
        val trndLst=vMdl.readByCat("")
        for(i in 0 until 6){
            arrListTrendingKerosil.add(trndLst[i])
        }

//        binding.btnFav.setOnClickListener {
//            startActivity(
//                Intent(this@MainActivity, NewQuoteStudioActivity::class.java).putExtra(
//                    "cat",
//                    "MFAV"
//                )
//            )
//        }
//        binding.btnSettings.setOnClickListener {
//            startActivity(Intent(this,SettingsActivity::class.java))
//        }
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (UtilSharedPerefs.getDate(this)!=currentDate){
            UtilSharedPerefs.setDate(this,currentDate)
            try {
                UtilSharedPerefs.setQuote(this,vMdl.getRandomObject().Quote)
            }
            catch (exc:Exception){
                exc.printStackTrace()
            }
        }
//        binding.qoutOfDay.qoutData.text=UtilSharedPerefs.getQuote(this)
//        binding.qoutOfDay.qoutWallpaper.setImageResource(UtilLists.getRandomWallpaper())
//        binding.qoutOfDay.root.setOnClickListener {
//            startActivity(
//                Intent(this@MainActivity, NewQuoteStudioActivity::class.java).putExtra(
//                    "cat",
//                    "QOD"
//                )
//            )
//        }



//        binding.recyclerPopularCats.isNestedScrollingEnabled = false
        binding.recyclerPopularCats.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerPopularCats.adapter = AdapterCategories(this, object : InterfaceCatClick {
            override fun onClick(catName: String) {
                startActivity(
                    Intent(
                        this@MainActivity,
                        NewQuoteStudioActivity::class.java
                    ).putExtra("cat", catName)
                )
            }

        })
        binding.quotesViewPager.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    NewQuoteStudioActivity::class.java
                ).putExtra("cat", "").putExtra("model", arrListTrendingKerosil[binding.quotesViewPager.currentItem])
            )
        }

        binding.quotesViewPager.adapter=QuotesPagerAdapter(this)
        TabLayoutMediator(binding.tabLayoutOnBoardingScreen, binding.quotesViewPager) { tab, position ->
        }.attach()

    }

    override fun onResume() {
        super.onResume()
//        try {
//            if (!vMdl.readAllFav().isNotEmpty()){
//                binding.btnFav.visibility= View.GONE
//            }
//            else{
//                binding.btnFav.visibility= View.VISIBLE
//            }
//        }catch (exc:Exception){
//            exc.printStackTrace()
//        }
    }


    private inner class QuotesPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return arrListTrendingKerosil.size
        }
        override fun createFragment(position: Int): Fragment {
            Log.d("logkey","createFragment")
            return TrendingFragment.newInstance(arrListTrendingKerosil[position])
        }
    }

}