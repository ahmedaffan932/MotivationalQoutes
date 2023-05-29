package com.example.motivational.qoutes.utils

import android.app.Application
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Locale
import java.util.Objects
import java.util.Random
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object UtilMiscs {
    var clipboard: ClipboardManager?=null
    var clip: ClipData?=null
    private var rndmNmbr=0

    fun showProgressD(context: Context):ProgressDialog{
        val progress = ProgressDialog(context)
        progress.setTitle("Loading")
        progress.setCancelable(false)
        progress.show()
        return progress
    }
    fun Context.unZipFolder() {
        val outputDirectory = filesDir.absolutePath
        if (!File(outputDirectory, "quotes.json").exists()) {
            val zipFileName = "archive.zip"
            Log.d("logkey", "PTH: $outputDirectory")
            unzipFromAssets(this, zipFileName, outputDirectory)
        }
    }
    fun Context.setupRoomDb(application:Application) {
        CoroutineScope(Dispatchers.IO).launch {
            if (QuotViewModel(application).getAllCats().isEmpty()) {
                val gson = Gson()
                val quotsJson = readJsonFromFile(filesDir.absolutePath + "/quotes.json")
                val quots = gson.fromJson(quotsJson, Array<QuotModel>::class.java).toList()
                QuotViewModel(application).insertUsers(quots)
                Log.d("logkey", "SZ: ${quots.size}")
            }
        }
    }
    fun getRootPath(){

    }
    fun unzipFromAssets(context: Context, zipFileName: String, outputDirectory: String) {
        try {
            val inputStream = context.assets.open(zipFileName)
            val zipInputStream = ZipInputStream(inputStream)
            var zipEntry: ZipEntry?

            val buffer = ByteArray(1024)
            var count: Int

            while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                val fileName = zipEntry?.name
                val outputFile = File(outputDirectory, fileName)

                if (zipEntry?.isDirectory == true) {
                    // Create the directory if it doesn't exist
                    outputFile.mkdirs()
                } else {
                    // Create any necessary parent directories for the file
                    outputFile.parentFile?.mkdirs()

                    // Extract the file
                    val fileOutputStream = FileOutputStream(outputFile)
                    while (zipInputStream.read(buffer).also { count = it } != -1) {
                        fileOutputStream.write(buffer, 0, count)
                    }
                    fileOutputStream.close()
                }

                zipInputStream.closeEntry()
            }

            zipInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun readJsonFromFile(filePath: String): String? {
        val file = File(filePath)
        if (!file.exists()) {
            // File doesn't exist
            return null
        }

        val bufferSize = 4096 // Adjust the buffer size according to your needs
        val reader = file.bufferedReader(Charsets.UTF_8)
        val stringBuilder = StringBuilder()
        val buffer = CharArray(bufferSize)
        var charsRead: Int

        try {
            while (reader.read(buffer).also { charsRead = it } != -1) {
                stringBuilder.append(buffer, 0, charsRead)
            }
            return stringBuilder.toString()
        } finally {
            reader.close()
        }
    }
    fun copyToClip(context:Context, text:String){
        clipboard=context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clip =ClipData.newPlainText("Qoutes", text)
        clipboard?.setPrimaryClip(clip!!)
    }
    fun showSnackBar(viw: View,text:String){
        val snack = Snackbar.make(
            viw,
            text,
            Snackbar.LENGTH_LONG
        )
        val view: View = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER
        view.layoutParams = params
        snack.duration=2000
        snack.show()
    }
    fun saveMediaToStorage(context: Context, bitmap: Bitmap?, name:String){
        //Generating a file name
        if (File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"$name.jpg").exists()){
            Log.d("logkey",File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"$name.jpg").delete().toString())
        }

        val filename = "${name}.jpg"

            //Output stream
            var fos: OutputStream? = null

            //For devices running android >= Q
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //getting the contentResolver
                context?.contentResolver?.also { resolver ->

                    //Content resolver will process the contentvalues
                    val contentValues = ContentValues().apply {

                        //putting file information in content values
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }

                    //Inserting the contentValues to contentResolver and getting the Uri
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    //Opening an outputstream with the Uri that we got
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                //These for devices running on android < Q
                //So I don't think an explanation is needed here
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }


    }

    fun appInstalledOrNot(context: Context, uri: String): Boolean {
        val pm = context.packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    fun onShare(context: Context, model: File?) {
        if (appInstalledOrNot(context, "com.whatsapp")) {
            directOpenWhatsapp(context, false, model!!)
        }

    }

    fun directOpenWhatsapp(context: Context, isBussiness: Boolean, model: File) {
        if (isBussiness) {
            val sendIntent = Intent()
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val files: java.util.ArrayList<Uri> = java.util.ArrayList<Uri>()

            for (file in model.listFiles()) {

                files.add(Objects.requireNonNull(context).let {
                    FileProvider.getUriForFile(
                        it,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    )
                }!!)
            }

            sendIntent.putParcelableArrayListExtra(
                "android.intent.extra.STREAM",
                files
            )
//            sendIntent.putExtra(
//                Intent.EXTRA_TEXT,
//                "This is downloaded by:-\nhttps://play.google.com/store/apps/details?id=" + applicationContext?.packageName
//
//            )
            sendIntent.action = Intent.ACTION_SEND_MULTIPLE
            sendIntent.setPackage("com.whatsapp.w4b")
            sendIntent.type = "video/*"
            context.startActivity(sendIntent)
        } else {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            sendIntent.putExtra(
                Intent.EXTRA_STREAM,
                Objects.requireNonNull(context)?.let {
                    FileProvider.getUriForFile(
                        it,
                        BuildConfig.APPLICATION_ID + ".provider",
                        model
                    )
                })
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "This is downloaded by:-\nhttps://play.google.com/store/apps/details?id=" + context.packageName

            )
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.setPackage("com.whatsapp")
            sendIntent.type = "video/*"
            context.startActivity(sendIntent)
        }
    }


    fun downloadImg(context: Context,bitmap: Bitmap?, param1:QuotModel?) {
        saveMediaToStorage(context,bitmap,"${param1?.Category} ${param1?.id}")
    }

    fun getRandom():Int{
        if (rndmNmbr>=33){
            rndmNmbr=0
        }
        else{
            rndmNmbr+=1
        }
        return rndmNmbr
    }
    fun getNextWallpaper(indx:Int):Int{
        if (indx>=32){
            return 0
        }
        else{
            return indx+1
        }
    }

}