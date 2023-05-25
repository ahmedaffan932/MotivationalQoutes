package com.example.motivational.qoutes.activities

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.adapters.AdapterCategories
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.InterstitialAds
import com.example.motivational.qoutes.ads.NativeAd
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivityMainBinding
import com.example.motivational.qoutes.databinding.DialogRateBinding
import com.example.motivational.qoutes.fragments.TrendingFragment
import com.example.motivational.qoutes.interfaces.InterfaceCatClick
import com.example.motivational.qoutes.interfaces.InterfaceUserInterfere
import com.example.motivational.qoutes.utils.UtilMiscs
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), InterfaceUserInterfere {
    private lateinit var binding: ActivityMainBinding
    private lateinit var vMdl:QuotViewModel
    private var arrListTrendingKerosil=ArrayList<QuotModel>()
    private var kerosilSpinnerHandler :Handler?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        InterstitialAds.showInterstitialAdmob(this,this, Ads.dashboardIntAm,null)
        NativeAd.showPreFetch(this,Ads.dashboardNativeAm,binding.adFrameLayout,null)
        vMdl=QuotViewModel(application)

        //initialize kerosil list
        arrListTrendingKerosil.clear()
        CoroutineScope(Dispatchers.IO).launch {
            val trndLst = vMdl.readByCat("")
            if (trndLst.size>0){
                binding.quotesViewPager.visibility=View.VISIBLE
                for(i in 0 until 6){
                    arrListTrendingKerosil.add(trndLst[i])
                }
            }
            else{
                binding.quotesViewPager.visibility=View.GONE
            }

            runOnUiThread { binding.quotesViewPager.adapter=QuotesPagerAdapter(this@MainActivity)
                TabLayoutMediator(binding.tabLayoutOnBoardingScreen, binding.quotesViewPager) { tab, position ->
                }.attach() }


        }


        binding.btnMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerVisible(
                    GravityCompat.START
                )
            ) binding.drawerLayout.closeDrawer(GravityCompat.START) else binding.drawerLayout.openDrawer(
                GravityCompat.START
            )
        }

        binding.btnPro.setOnClickListener {
            startActivity(Intent(this,InAppActivity::class.java))
        }

        binding.btnShare.setOnClickListener {
            binding.drawerLayout.closeDrawers()
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
            binding.drawerLayout.closeDrawers()
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/daily-positive-quotes/home")
            )
            startActivity(browserIntent)
        }

        binding.btnMoreApps.setOnClickListener {
            binding.drawerLayout.closeDrawers()
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
            binding.drawerLayout.closeDrawers()
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

        binding.btnCloseMenu.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        binding.btnFav.setOnClickListener {
            binding.drawerLayout.closeDrawers()
            startActivity(
                Intent(this@MainActivity, NewQuoteStudioActivity::class.java).putExtra(
                    "cat",
                    "MFAV"
                )
            )
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
//        binding.btnSettings.setOnClickListener {
//            startActivity(Intent(this,SettingsActivity::class.java))
//        }

//        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//        if (UtilSharedPerefs.getDate(this)!=currentDate){
//            UtilSharedPerefs.setDate(this,currentDate)
//            try {
//                UtilSharedPerefs.setQuote(this,vMdl.getRandomObject().Quote)
//            }
//            catch (exc:Exception){
//                exc.printStackTrace()
//            }
//        }

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



        //kerosil spinner
        kerosilSpinnerHandler= Handler(mainLooper)
        kerosilSpinnerHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    // Call your function here
                    kerosilSpinner()
                    // Schedule the next execution after 2 seconds
                    kerosilSpinnerHandler?.postDelayed(this, 6000)
                }
            }
            ,6000)

    }

    private fun kerosilSpinner() {
        if (binding.quotesViewPager.currentItem>=5){
            binding.quotesViewPager.currentItem=0
        }
        else{
            binding.quotesViewPager.currentItem+=1
        }
    }

    override fun onResume() {
        super.onResume()
//        Log.d("logkey","Total Size Of DB: ${vMdl.readAllData.value?.size}")

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

    override fun onInterfere() {
        Log.d("logkey","Interference found")
        resetHandler()
    }


    private fun resetHandler(){
        kerosilSpinnerHandler?.removeCallbacksAndMessages(null)
        kerosilSpinnerHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    // Call your function here
                    kerosilSpinner()
                    // Schedule the next execution after 2 seconds
                    kerosilSpinnerHandler?.postDelayed(this, 6000)
                }
            }
            ,6000)
    }


}