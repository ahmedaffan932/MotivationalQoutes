package com.example.motivational.qoutes.activities

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.adapters.AdapterCategories
import com.example.motivational.qoutes.ads.*
import com.example.motivational.qoutes.database.Category
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivityMainBinding
import com.example.motivational.qoutes.databinding.DialogExitBinding
import com.example.motivational.qoutes.databinding.DialogRateBinding
import com.example.motivational.qoutes.fragments.TrendingFragment
import com.example.motivational.qoutes.interfaces.InterfaceCatClick
import com.example.motivational.qoutes.interfaces.InterfaceUserInterfere
import com.example.motivational.qoutes.utils.NotificationScheduler
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilMiscs
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), InterfaceUserInterfere {
    private lateinit var binding: ActivityMainBinding
    private lateinit var vMdl: QuotViewModel
    private var arrListTrendingKerosil = ArrayList<QuotModel>()
    private var kerosilSpinnerHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCategoriesRecyclerView()

        KeyboardVisibilityEvent.setEventListener(
            this
        ) { isOpen ->
            if (isOpen) {
                binding.clViewPager.visibility = View.GONE
            } else {
                binding.clViewPager.visibility = View.VISIBLE
            }
        }

        binding.etSearch.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    // Check if no view has focus:
                    val view = this.currentFocus
                    if (view != null) {
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    return@OnEditorActionListener true
                }
                false
            })
        NotificationScheduler().scheduleNotification(application)

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int,
                before: Int, count: Int
            ) {
                setCategoriesRecyclerView(binding.etSearch.text.toString())
            }
        })


        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        vMdl = QuotViewModel(application)

        //initialize kerosil list
        arrListTrendingKerosil.clear()
        CoroutineScope(Dispatchers.IO).launch {
            val trndLst = vMdl.readByCat("")
            if (trndLst.size > 0) {
                binding.quotesViewPager.visibility = View.VISIBLE
                for (i in 0 until 6) {
                    arrListTrendingKerosil.add(trndLst[i])
                }
            } else {
                binding.quotesViewPager.visibility = View.GONE
            }

            runOnUiThread {
                binding.quotesViewPager.adapter = QuotesPagerAdapter(this@MainActivity)
                TabLayoutMediator(
                    binding.tabLayoutOnBoardingScreen,
                    binding.quotesViewPager
                ) { tab, position ->
                }.attach()
            }
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
            startActivity(Intent(this, InAppActivity::class.java))
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
                goToStore()
            }
        }

        binding.btnCloseMenu.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        binding.btnFav.setOnClickListener {
            binding.drawerLayout.closeDrawers()
            startActivity(
                Intent(this@MainActivity, FullViewActivity::class.java).putExtra(
                    "cat",
                    "MFAV"
                ).putExtra("pos", 0)
            )
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.recyclerPopularCats.layoutManager = GridLayoutManager(this, 2)


        //kerosil spinner
        kerosilSpinnerHandler = Handler(mainLooper)
        kerosilSpinnerHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    // Call your function here
                    kerosilSpinner()
                    // Schedule the next execution after 2 seconds
                    kerosilSpinnerHandler?.postDelayed(this, 6000)
                }
            }, 6000
        )

    }

    private fun kerosilSpinner() {
        if (binding.quotesViewPager.currentItem >= 5) {
            binding.quotesViewPager.currentItem = 0
        } else {
            binding.quotesViewPager.currentItem += 1
        }
    }

    private inner class QuotesPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return arrListTrendingKerosil.size
        }

        override fun createFragment(position: Int): Fragment {
            return TrendingFragment.newInstance(arrListTrendingKerosil[position])
        }
    }

    private fun goToStore() {
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
        Log.d("logkey", "Interference found")
        resetHandler()
    }


    private fun resetHandler() {
        kerosilSpinnerHandler?.removeCallbacksAndMessages(null)
        kerosilSpinnerHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    // Call your function here
                    kerosilSpinner()
                    // Schedule the next execution after 2 seconds
                    kerosilSpinnerHandler?.postDelayed(this, 6000)
                }
            }, 6000
        )
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(binding.navView)) {
            binding.drawerLayout.closeDrawers()
        } else {
            val alert = Dialog(this)
            val customLayoutBinding =
                DialogExitBinding.bind(layoutInflater.inflate(R.layout.dialog_exit, null))
            alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alert.setCancelable(true)
            alert.setCanceledOnTouchOutside(true)
            alert.setContentView(customLayoutBinding.root)
            alert.show()
            customLayoutBinding.btnNotNow.setOnClickListener {
                alert.dismiss()
            }
            customLayoutBinding.btnNo.setOnClickListener {
                alert.dismiss()
            }
            customLayoutBinding.btnYes.setOnClickListener {
                alert.dismiss()
                finishAffinity()
            }
        }
    }

    private fun setCategoriesRecyclerView(search: String = "") {
        val arrCategories = ArrayList<Category>()
        for (i in 0 until UtilLists.cats.size) {
            if (UtilLists.cats[i].contains(search)) {
                arrCategories.add(Category(UtilLists.cats[i], UtilLists.catWallpapers[i]))
            }
        }

        binding.recyclerPopularCats.adapter =
            AdapterCategories(this, arrCategories, object : InterfaceCatClick {
                override fun onClick(catName: String) {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            FullViewActivity::class.java
                        ).putExtra("cat", catName).putExtra("pos", 0)
                    )
                }
            })

    }

}