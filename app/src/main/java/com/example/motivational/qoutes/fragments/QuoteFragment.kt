package com.example.motivational.qoutes.fragments

import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Environment
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
import com.example.motivational.qoutes.activities.FullViewActivity
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.FragmentQuoteBinding
import com.example.motivational.qoutes.interfaces.InterfaceMisClick
import com.example.motivational.qoutes.utils.CustomDialog
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.Misc
import com.example.motivational.qoutes.utils.Misc.getName
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class QuoteFragment : Fragment() {
    private var param1: QuotModel? = null
    private var param2: Int? = null
    private lateinit var binding: FragmentQuoteBinding
    private lateinit var vMdl: QuotViewModel
    private lateinit var infc: InterfaceMisClick
    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeechEngine.language = Locale.ENGLISH
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
            param2 = it.getInt(ARG_PARAM2)
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
        textToSpeechEngine.speak("", TextToSpeech.QUEUE_FLUSH, null, "tts1")

        BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).apply {
            peekHeight = 0
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.mView.visibility = View.VISIBLE
        binding.adFrameLayout.visibility = View.GONE
        binding.quotLayout.qoutData.text = param1?.Quote
        if (param1 == null) {
            binding.quotLayout.qoutData.text = "Either you run the day or the day runs you"
        }
        binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.wallpapers[param1!!.wall])
        updateUi()
        btnClicks()

        binding.root.setOnClickListener {
            if (!infc.onMisTouch(param1)) {
                if (BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state == BottomSheetBehavior.STATE_EXPANDED) {
                    BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state =
                        BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    binding.quotLayout.qoutWallpaper.setImageResource(
                        UtilLists.wallpapers[Misc.getNextWallpaper(
                            param1!!.wall
                        )]
                    )
                    binding.quotLayout.qoutWallpaper.destroyDrawingCache()
                    param1?.wall = Misc.getNextWallpaper(param1!!.wall)
                    CoroutineScope(Dispatchers.IO).launch {
                        vMdl.updateQoute(param1!!)
                    }
                }
            }
        }
        binding.root.setOnLongClickListener {
            if (!infc.onMisTouch(param1)) {
                Misc.copyToClip(requireContext(), param1?.Quote ?: "")
                Misc.showSnackBar(binding.constraintLayoutOptions, "Quote Copied!")
            }
            return@setOnLongClickListener true
        }
        return binding.root
    }

    private fun btnClicks() {
        binding.btnFullScreen.setOnClickListener {
            UtilSharedPerefs.setIsFullQuote(requireContext(), true)
            startActivity(
                Intent(requireActivity(), FullViewActivity::class.java).putExtra(
                    "cat",
                    param1?.Category
                ).putExtra("pos", param2 ?: 0)
            )
            activity?.finish()
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
            Misc.copyToClip(requireContext(), param1?.Quote ?: "")
            Misc.showSnackBar(binding.constraintLayoutOptions, "Quote Copied!")
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
                CoroutineScope(Dispatchers.IO).launch {
                    var dlg: CustomDialog? = null
                    requireActivity().runOnUiThread {
                        dlg = Misc.showProgressD(requireContext())
                    }
                    WallpaperManager.getInstance(requireContext())
                        .setBitmap(binding.quotLayout.root.drawToBitmap())
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Wallpaper Updated!", Toast.LENGTH_SHORT)
                            .show()
                        dlg?.dismiss()
                    }
                }
            }
            binding.bottomSheetQualities.btnDownload.setOnClickListener {
                BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state =
                    BottomSheetBehavior.STATE_COLLAPSED
                Misc.downloadImg(
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
                BottomSheetBehavior.from(binding.bottomSheetQualities.frameLayout).state =
                    BottomSheetBehavior.STATE_COLLAPSED
                Misc.downloadImg(
                    requireContext(),
                    binding.quotLayout.root.drawToBitmap(),
                    param1
                )
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

            }
            binding.bottomSheetQualities.btnRed.setOnClickListener {
                Misc.downloadImg(
                    requireContext(),
                    binding.quotLayout.root.drawToBitmap(),
                    param1
                )
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
                    Toast.makeText(context, "Instagram is not installed", Toast.LENGTH_SHORT).show()
                }
            }
            binding.bottomSheetQualities.btnBlack.setOnClickListener {
                Misc.downloadImg(
                    requireContext(),
                    binding.quotLayout.root.drawToBitmap(),
                    param1
                )

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
                    Toast.makeText(context, "Tiktok is not installed", Toast.LENGTH_SHORT).show()
                }
            }
            binding.bottomSheetQualities.btnBlue.setOnClickListener {
                Misc.downloadImg(
                    requireContext(),
                    binding.quotLayout.root.drawToBitmap(),
                    param1
                )
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
                    Toast.makeText(context, "Facebook is not installed", Toast.LENGTH_SHORT).show()
                }
            }
            binding.bottomSheetQualities.btnGreen.setOnClickListener {
                Misc.downloadImg(
                    requireContext(),
                    binding.quotLayout.root.drawToBitmap(),
                    param1
                )
                val model =
                    File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "${getName(param1?.Category, param1?.id)}.jpg"
                    )
                Misc.onShare(requireContext(), model)
            }
            binding.bottomSheetQualities.btnSparrow.setOnClickListener {
                Misc.downloadImg(
                    requireContext(),
                    binding.quotLayout.root.drawToBitmap(),
                    param1
                )
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
                    Toast.makeText(context, "Twitter is not installed", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }


    private fun downloadImg() {
        Misc.saveMediaToStorage(
            requireContext(),
            binding.quotLayout.root.drawToBitmap(),
            "${getName(param1?.Category, param1?.id)}"
        )

    }

    private fun updateUi() {
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
        binding.constraintLayoutOptions.visibility = View.VISIBLE
        binding.btnFullScreen.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: QuotModel?, param2: Int) =
            QuoteFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                }
            }
    }
}