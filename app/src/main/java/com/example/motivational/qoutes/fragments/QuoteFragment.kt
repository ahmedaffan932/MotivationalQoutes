package com.example.motivational.qoutes.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.activities.FullViewActivity
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.NativeAd
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.FragmentQuoteBinding
import com.example.motivational.qoutes.interfaces.InterfaceMisClick
import com.example.motivational.qoutes.utils.BottomSheetDialog
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilMiscs
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

private const val ARG_PARAM1 = "param1"

class QuoteFragment : Fragment() {
    private var param1: QuotModel? = null
    private lateinit var binding: FragmentQuoteBinding
    private lateinit var vMdl: QuotViewModel
    private lateinit var infc: InterfaceMisClick


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onPause() {
        Log.d("logkey", "onPause: ${param1?.Popularity}")
        binding.constraintLayoutOptions.visibility = View.GONE
        binding.btnFullScreen.visibility = View.GONE
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuoteBinding.inflate(inflater, container, false)
        vMdl = ViewModelProvider(this)[QuotViewModel::class.java]
        infc = activity as InterfaceMisClick

        Log.d("logkey", "param1: $param1")
        if (param1 == null) {
            binding.mView.visibility = View.GONE
            binding.adFrameLayout.visibility = View.VISIBLE
            NativeAd.showPreFetch(
                requireContext(),
                Ads.inBetweenQuotesNativeAm,
                binding.adFrameLayout,
                null
            )
        }
        else {
            binding.mView.visibility = View.VISIBLE
            binding.adFrameLayout.visibility = View.GONE
            binding.quotLayout.qoutData.text = param1?.Quote
            binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.wallpapers[param1!!.wall])
            updateUi()
            btnClicks()

            binding.root.setOnClickListener {
                if (!infc.onMisTouch(param1)) {
                    binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.wallpapers[UtilMiscs.getNextWallpaper(param1!!.wall)])
                    binding.quotLayout.qoutWallpaper.destroyDrawingCache()
                    param1?.wall=UtilMiscs.getNextWallpaper(param1!!.wall)
                    CoroutineScope(Dispatchers.IO).launch {
                        vMdl.updateQoute(param1!!)
                    }
                }
            }
            binding.root.setOnLongClickListener {
                if (!infc.onMisTouch(param1)) {
                    UtilMiscs.copyToClip(requireContext(), param1?.Quote ?: "")
                    UtilMiscs.showSnackBar(binding.constraintLayoutOptions, "Quote Copied!")
                }
                return@setOnLongClickListener true
            }
        }
        return binding.root
    }
    private fun btnClicks() {
        binding.btnFullScreen.setOnClickListener {
            UtilSharedPerefs.setIsFullQuote(requireContext(),true)
            startActivity(
                Intent(requireActivity(), FullViewActivity::class.java).putExtra(
                    "cat",
                    param1?.Category
                )
            )
            activity?.finish()
        }
        binding.btnGreen.setOnClickListener {
            downloadImg()
            val model=
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"${param1?.Category} ${param1?.id}.jpg")
            UtilMiscs.onShare(requireContext(),model)
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
            UtilMiscs.copyToClip(requireContext(), param1?.Quote ?: "")
            UtilMiscs.showSnackBar(binding.constraintLayoutOptions, "Quote Copied!")
        }


        binding.btnShare.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireActivity(), param1!!,binding.quotLayout.root.drawToBitmap())
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "bottomSheet")


        }

    }


    private fun downloadImg() {
        UtilMiscs.saveMediaToStorage(requireContext(),binding.quotLayout.root.drawToBitmap(),"${param1?.Category} ${param1?.id}")

    }

    private fun updateUi(){
        if (param1?.isFav==1){
            binding.btnFav.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.clr_blue,requireContext().theme))
        }
        else{
            binding.btnFav.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.white,requireContext().theme))
        }
    }

    override fun onResume() {
        super.onResume()
        updateUi()
        binding.constraintLayoutOptions.visibility = View.VISIBLE
        binding.btnFullScreen.visibility=View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: QuotModel?) =
            QuoteFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}