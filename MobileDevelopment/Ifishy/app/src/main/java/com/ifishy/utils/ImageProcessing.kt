package com.ifishy.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageProcessing {

    fun resizeAndCompressImageFromUri(
        context: Context,
        imageUri: Uri,
        targetWidth: Int = 244,
        targetHeight: Int = 244,
        quality: Int = 100
    ): File? {
        try {

            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

            val file = File(context.cacheDir, "resize_and_compressed${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            resizedBitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}