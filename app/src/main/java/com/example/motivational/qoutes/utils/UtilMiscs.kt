package com.example.motivational.qoutes.utils

import android.app.Application
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
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
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object UtilMiscs {
    var clipboard: ClipboardManager?=null
    var clip: ClipData?=null

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

        return try {
            val text = file.readText()
            text
        } catch (e: IOException) {
            e.printStackTrace()
            null
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
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snack.duration=2000
        snack.show()
    }
    fun saveMediaToStorage(context: Context, bitmap: Bitmap?, name:String){
        //Generating a file name
        if (File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"$name.jpg").exists()){
            return
        }
        else{
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
                Toast.makeText(context,"Saved!",Toast.LENGTH_SHORT).show()
            }

        }
    }
}