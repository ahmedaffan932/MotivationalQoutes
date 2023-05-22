package com.example.motivational.qoutes.activities

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.adapters.AdapterCategories
import com.example.motivational.qoutes.adapters.AdapterQouts
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.InterstitialAds
import com.example.motivational.qoutes.ads.NativeAd
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivityMainBinding
import com.example.motivational.qoutes.interfaces.InterfaceCatClick
import com.example.motivational.qoutes.interfaces.InterfaceQuotClick
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilSharedPerefs
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        InterstitialAds.showInterstitialAdmob(this,this, Ads.dashboardIntAm,null)
        NativeAd.showPreFetch(this,Ads.dashboardNativeAm,binding.adFrameLayout,null)
        vMdl=QuotViewModel(application)
        binding.btnFav.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, NewQuoteStudioActivity::class.java).putExtra(
                    "cat",
                    "MFAV"
                )
            )
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this,SettingsActivity::class.java))
        }
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
        binding.qoutOfDay.qoutData.text=UtilSharedPerefs.getQuote(this)
        binding.qoutOfDay.qoutWallpaper.setImageResource(UtilLists.getRandomWallpaper())
        binding.qoutOfDay.root.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, NewQuoteStudioActivity::class.java).putExtra(
                    "cat",
                    "QOD"
                )
            )
        }



//        binding.recyclerPopularCats.isNestedScrollingEnabled = false
        binding.recyclerPopularCats.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerTrending.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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

        Log.d(
            "logkey",
            "vMdl.readByCat(\"\") ${vMdl.readByCat("").size}"
        )
        binding.recyclerTrending.adapter = AdapterQouts(
            this@MainActivity,
            vMdl.readByCat(""),
            object : InterfaceQuotClick {
                override fun onClick(quot: QuotModel) {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            NewQuoteStudioActivity::class.java
                        ).putExtra("cat", "").putExtra("model", quot)
                    )
                }
            })


    }

    override fun onResume() {
        super.onResume()
        try {
            if (!vMdl.readAllFav().isNotEmpty()){
                binding.btnFav.visibility= View.GONE
            }
            else{
                binding.btnFav.visibility= View.VISIBLE
            }
        }catch (exc:Exception){
            exc.printStackTrace()
        }
    }
}