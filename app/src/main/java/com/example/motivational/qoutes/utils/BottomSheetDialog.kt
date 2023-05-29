package com.example.motivational.qoutes.utils

import android.app.Activity
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.databinding.BottomSheetOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale


class BottomSheetDialog(val activityD: Activity, val model: QuotModel, val bitmap: Bitmap): BottomSheetDialogFragment()  {
    private lateinit var binding:BottomSheetOptionsBinding
    private val textToSpeechEngine: TextToSpeech by lazy {
        // Pass in context and the listener.
        TextToSpeech(activityD,
            TextToSpeech.OnInitListener { status ->
                // set our locale only if init was success.
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.ENGLISH
                }
            })
    }

    init {
        textToSpeechEngine.speak("", TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= BottomSheetOptionsBinding.inflate(inflater,container,false)
        binding.btnClose.setOnClickListener {
            this.dismiss()
        }
        binding.btnSetWallpaper.setOnClickListener {
            this.dismiss()
            CoroutineScope(Dispatchers.IO).launch {
                var dlg:ProgressDialog?=null
                activityD.runOnUiThread{dlg=UtilMiscs.showProgressD(activityD)}
                WallpaperManager.getInstance(activityD).setBitmap(bitmap)
                activityD.runOnUiThread{
                    Toast.makeText(activityD,"Wallpaper Updated!",Toast.LENGTH_SHORT).show()
                    dlg?.dismiss()
                }
            }
        }
        binding.btnDownload.setOnClickListener {
            this.dismiss()
            UtilMiscs.downloadImg(activityD,bitmap, model)
            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
        }
        binding.btnPlaySound.setOnClickListener {
            this.dismiss()
            textToSpeechEngine.speak(model.Quote, TextToSpeech.QUEUE_FLUSH, null, "tts1")

        }
        binding.btnMoreOpts.setOnClickListener {
            this.dismiss()
            UtilMiscs.downloadImg(activityD,bitmap, model)
            val model=
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"${model?.Category} ${model?.id}.jpg")

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
        binding.btnRed.setOnClickListener {
            UtilMiscs.downloadImg(activityD,bitmap, model)
            val model=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"${model?.Category} ${model?.id}.jpg")
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                model
            ))
            intent.setPackage("com.instagram.android") // Specify Instagram's package name

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Instagram not installed on the device
                Toast.makeText(context, "Instagram is not installed", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnBlack.setOnClickListener {
//            UtilMiscs.downloadImg(activityD,bitmap, model)

            val model=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"${model?.Category} ${model?.id}.jpg")
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                model
            ))
            intent.setPackage("com.zhiliaoapp.musically") // Specify Instagram's package name

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Instagram not installed on the device
                Toast.makeText(context, "Tiktok is not installed", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnBlue.setOnClickListener {
            UtilMiscs.downloadImg(activityD,bitmap, model)
            val model=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"${model?.Category} ${model?.id}.jpg")
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                model
            ))
            intent.setPackage("com.facebook.katana") // Specify Instagram's package name

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Instagram not installed on the device
                Toast.makeText(context, "Facebook is not installed", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnGreen.setOnClickListener {
            UtilMiscs.downloadImg(activityD,bitmap, model)
            val model=
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"${model.Category} ${model.id}.jpg")
            UtilMiscs.onShare(requireContext(),model)
        }
        binding.btnSparrow.setOnClickListener {
            UtilMiscs.downloadImg(activityD,bitmap, model)
            val model=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"${model?.Category} ${model?.id}.jpg")
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                model
            ))
            intent.setPackage("com.twitter.android") // Specify Instagram's package name

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Instagram not installed on the device
                Toast.makeText(context, "Twitter is not installed", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root


    }


}