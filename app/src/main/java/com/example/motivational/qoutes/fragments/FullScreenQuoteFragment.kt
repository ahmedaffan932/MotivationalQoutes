package com.example.motivational.qoutes.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.lifecycle.ViewModelProvider
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.activities.FullViewActivity
import com.example.motivational.qoutes.activities.NewQuoteStudioActivity
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.NativeAd
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.FragmentFullScreenQuoteBinding
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

class FullScreenQuoteFragment : Fragment() {
    private lateinit var binding: FragmentFullScreenQuoteBinding
    private var param1: QuotModel? = null
    private lateinit var vMdl: QuotViewModel
    private lateinit var infc: InterfaceMisClick

    override fun onPause() {
        Log.d("logkey", "onPause: ${param1?.Popularity}")
        binding.constraintLayoutOptions.visibility = View.GONE
        binding.btnFullScreenClose.visibility = View.GONE
        super.onPause()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentFullScreenQuoteBinding.inflate(inflater,container,false)
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
            binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.getRandomWallpaper())
            updateUi()
            btnClicks()

            binding.root.setOnClickListener {
                if (!infc.onMisTouch(param1)) {
                    binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.getRandomWallpaper())
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
        binding.btnFullScreenClose.setOnClickListener {
            UtilSharedPerefs.setIsFullQuote(requireContext(),false)
            startActivity(
                Intent(requireActivity(), NewQuoteStudioActivity::class.java).putExtra(
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

//        binding.btnDown.setOnClickListener {
//            downloadImg()
//        }

        binding.btnShare.setOnClickListener {
//            val bottomSheetDialog = BottomSheetDialog(requireContext(), param1!!)
//            bottomSheetDialog.show(requireActivity().supportFragmentManager, "bottomSheet")
            downloadImg()
            val model=
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"${param1?.Category} ${param1?.id}.jpg")

            Log.d("logkey","Model Path: ${model.absolutePath}")
            val intentBuilder: ShareCompat.IntentBuilder =
                ShareCompat.IntentBuilder.from(requireActivity())
                    .setType("image/*")
            intentBuilder.addStream(
                FileProvider.getUriForFile(
                    requireContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    model
                )
            )
            val intent = intentBuilder.intent.setAction(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "Send to "))
        }

    }


    private fun downloadImg() {
        UtilMiscs.saveMediaToStorage(requireContext(),binding.quotLayout.root.drawToBitmap(),"${param1?.Category} ${param1?.id}")

    }

    private fun updateUi(){
        binding.constraintLayoutOptions.visibility = View.VISIBLE
        binding.btnFullScreenClose.visibility = View.VISIBLE
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
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: QuotModel?) =
            FullScreenQuoteFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}