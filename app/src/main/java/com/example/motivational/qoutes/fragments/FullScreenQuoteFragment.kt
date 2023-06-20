package com.example.motivational.qoutes.fragments

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.NativeAd
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.FragmentFullScreenQuoteBinding
import com.example.motivational.qoutes.interfaces.InterfaceMisClick
import com.example.motivational.qoutes.utils.*
import com.example.motivational.qoutes.utils.UtilMiscs.getName
import com.example.motivational.qoutes.utils.UtilMiscs.wallpaperInt
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FullScreenQuoteFragment : Fragment() {
    private lateinit var binding: FragmentFullScreenQuoteBinding
    private var param1: QuotModel? = null
    private var param2: Int? = null
    private lateinit var vMdl: QuotViewModel
    private lateinit var infc: InterfaceMisClick
    private val textToSpeechEngine: TextToSpeech by lazy {
        // Pass in context and the listener.
        TextToSpeech(requireContext(),
            TextToSpeech.OnInitListener { status ->
                // set our locale only if init was success.
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.ENGLISH
                }
            })
    }


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
            param2 = it.getInt(ARG_PARAM2)
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFullScreenQuoteBinding.inflate(inflater, container, false)
        textToSpeechEngine.speak("", TextToSpeech.QUEUE_FLUSH, null, "tts1")
        BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).apply {
            peekHeight = 0
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout)
            .addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
//                        binding.quotLayout.qoutWallpaper.visibility = View.VISIBLE
//                    } else {
//                        binding.quotLayout.qoutWallpaper.visibility = View.GONE
//                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

            })
        vMdl = ViewModelProvider(this)[QuotViewModel::class.java]
        infc = activity as InterfaceMisClick

        Log.d("logkey", "param1: $param1")
        if (param1 == null && NativeAd.amInner != null) {
            binding.mView.visibility = View.GONE
            binding.adFrameLayout.visibility = View.VISIBLE
            NativeAd.showNativeAd(
                requireContext(),
                Ads.inBetweenQuotesNativeAm,
                binding.adFrameLayout,
                null
            )
        } else {
            binding.mView.visibility = View.VISIBLE
            binding.adFrameLayout.visibility = View.GONE
            binding.quotLayout.qoutData.text = param1?.Quote
            if (param1 == null) {
                binding.quotLayout.qoutData.text = "Either you run the day or the day runs you"
            }
            binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.wallpapers[wallpaperInt])

            updateUi()
            btnClicks()

            binding.root.setOnClickListener {
                if (!infc.onMisTouch(param1)) {
                    if (BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state == BottomSheetBehavior.STATE_EXPANDED) {
                        BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state =
                            BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                        wallpaperInt = UtilMiscs.getNextWallpaper(wallpaperInt)
                        binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.wallpapers[wallpaperInt])
                        binding.quotLayout.qoutWallpaper.destroyDrawingCache()
                        infc.onWallChange(wallpaperInt)
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
        binding.btnFullScreenClose.setOnClickListener {
        }
        binding.btnSpk.setOnClickListener {
            textToSpeechEngine.speak(param1?.Quote, TextToSpeech.QUEUE_FLUSH, null, "tts1")
        }
        binding.btnFav.setOnClickListener {
            if (param1?.isFav == 1) {
                param1?.isFav = 0
            } else {
                param1?.isFav = 1
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

            BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state =
                BottomSheetBehavior.STATE_EXPANDED

            binding.bottomSheetQualities.btnClose.setOnClickListener {
                BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state =
                    BottomSheetBehavior.STATE_COLLAPSED
            }
            binding.bottomSheetQualities.btnSetWallpaper.setOnClickListener {
                BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state =
                    BottomSheetBehavior.STATE_COLLAPSED
                binding.quotLayout.qoutWallpaper.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    CoroutineScope(Dispatchers.IO).launch {
                        var dlg: CustomDialog? = null
                        requireActivity().runOnUiThread {
                            dlg = UtilMiscs.showProgressD(requireContext())
                        }
                        WallpaperManager.getInstance(requireContext())
                            .setBitmap(binding.quotLayout.root.drawToBitmap())
                        requireActivity().runOnUiThread {
                            binding.quotLayout.qoutWallpaper.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Wallpaper Updated!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            dlg?.dismiss()
                        }
                    }
                }, 10)
            }
            binding.bottomSheetQualities.btnDownload.setOnClickListener {
                BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state =
                    BottomSheetBehavior.STATE_COLLAPSED
                UtilMiscs.downloadImg(
                    requireContext(),
                    binding.quotLayout.root.drawToBitmap(),
                    param1
                )
                Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
            }
            binding.bottomSheetQualities.btnPlaySound.setOnClickListener {
                BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state =
                    BottomSheetBehavior.STATE_COLLAPSED
                textToSpeechEngine.speak(param1?.Quote, TextToSpeech.QUEUE_FLUSH, null, "tts1")

            }
            binding.bottomSheetQualities.btnMoreOpts.setOnClickListener {
                binding.quotLayout.qoutWallpaper.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state =
                        BottomSheetBehavior.STATE_COLLAPSED
                    UtilMiscs.downloadImg(
                        requireContext(),
                        binding.quotLayout.root.drawToBitmap(),
                        param1
                    )
                    binding.quotLayout.qoutWallpaper.visibility = View.GONE
                    val model =
                        File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            "${getName(param1?.Category, param1?.id)}.jpg"
                        )

                    Log.d("logkey", "Model Path: ${model.absolutePath}")
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
                }, 100)

            }
            binding.bottomSheetQualities.btnRed.setOnClickListener {
                binding.quotLayout.qoutWallpaper.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    UtilMiscs.downloadImg(
                        requireContext(),
                        binding.quotLayout.root.drawToBitmap(),
                        param1
                    )
                    binding.quotLayout.qoutWallpaper.visibility = View.GONE
                    val model = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "${getName(param1?.Category, param1?.id)}.jpg"
                    )
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/*"
                    intent.putExtra(
                        Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                            requireContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            model
                        )
                    )
                    intent.setPackage("com.instagram.android") // Specify Instagram's package name

                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        // Instagram not installed on the device
                        Toast.makeText(context, "Instagram is not installed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, 100)
            }
            binding.bottomSheetQualities.btnBlack.setOnClickListener {
                binding.quotLayout.qoutWallpaper.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    UtilMiscs.downloadImg(
                        requireContext(),
                        binding.quotLayout.root.drawToBitmap(),
                        param1
                    )
                    binding.quotLayout.qoutWallpaper.visibility = View.GONE

                    val model = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "${getName(param1?.Category, param1?.id)}.jpg"
                    )
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/*"
                    intent.putExtra(
                        Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                            requireContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            model
                        )
                    )
                    intent.setPackage("com.zhiliaoapp.musically") // Specify Instagram's package name

                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        // Instagram not installed on the device
                        Toast.makeText(context, "Tiktok is not installed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, 100)
            }
            binding.bottomSheetQualities.btnBlue.setOnClickListener {
                binding.quotLayout.qoutWallpaper.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    UtilMiscs.downloadImg(
                        requireContext(),
                        binding.quotLayout.root.drawToBitmap(),
                        param1
                    )
                    binding.quotLayout.qoutWallpaper.visibility = View.GONE

                    val model = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "${getName(param1?.Category, param1?.id)}.jpg"
                    )
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/*"
                    intent.putExtra(
                        Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                            requireContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            model
                        )
                    )
                    intent.setPackage("com.facebook.katana") // Specify Instagram's package name

                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        // Instagram not installed on the device
                        Toast.makeText(context, "Facebook is not installed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, 100)
            }
            binding.bottomSheetQualities.btnGreen.setOnClickListener {
                binding.quotLayout.qoutWallpaper.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    UtilMiscs.downloadImg(
                        requireContext(),
                        binding.quotLayout.root.drawToBitmap(),
                        param1
                    )
                    binding.quotLayout.qoutWallpaper.visibility = View.GONE
                    val model =
                        File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            "${getName(param1?.Category, param1?.id)}.jpg"
                        )
                    UtilMiscs.onShare(requireContext(), model)
                }, 100)
            }
            binding.bottomSheetQualities.btnSparrow.setOnClickListener {
                binding.quotLayout.qoutWallpaper.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    UtilMiscs.downloadImg(
                        requireContext(),
                        binding.quotLayout.root.drawToBitmap(),
                        param1
                    )
                    binding.quotLayout.qoutWallpaper.visibility = View.GONE

                    val model = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "${getName(param1?.Category, param1?.id)}.jpg"
                    )
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/*"
                    intent.putExtra(
                        Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                            requireContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            model
                        )
                    )
                    intent.setPackage("com.twitter.android") // Specify Instagram's package name

                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        // Instagram not installed on the device
                        Toast.makeText(context, "Twitter is not installed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, 100)
            }

//                val bottomSheetDialog = BottomSheetDialogD(requireActivity(), param1!!,binding.quotLayout.root.drawToBitmap())
//                bottomSheetDialog.show(requireActivity().supportFragmentManager, "bottomSheet")
//            binding.quotLayout.qoutWallpaper.visibility=View.VISIBLE


        }

    }


    private fun updateUi() {
        binding.constraintLayoutOptions.visibility = View.VISIBLE
//        binding.btnFullScreenClose.visibility = View.VISIBLE
        if (param1?.isFav == 1) {
            binding.btnFav.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.ic_my_fav,
                    requireContext().theme
                )
            )
            binding.btnFav.imageTintList = ColorStateList.valueOf(
                ResourcesCompat.getColor(
                    resources,
                    R.color.clr_blue,
                    requireContext().theme
                )
            )
        } else {
            binding.btnFav.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.ic_fav,
                    requireContext().theme
                )
            )
            binding.btnFav.imageTintList = ColorStateList.valueOf(
                ResourcesCompat.getColor(
                    resources,
                    R.color.white,
                    requireContext().theme
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: QuotModel?, position: Int) =
            FullScreenQuoteFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, position)
                }
            }
    }
}