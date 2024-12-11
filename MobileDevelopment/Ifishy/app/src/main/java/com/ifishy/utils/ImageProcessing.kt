package com.ifishy.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx. exifinterface. media. ExifInterface
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageProcessing {

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun compressImageFromUri(
        context: Context,
        imageUri: Uri,
        quality: Int = 80
    ): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val exifInterface = ExifInterface(context.contentResolver.openInputStream(imageUri)!!)
            val rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

            val rotatedBitmap = when (rotation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(originalBitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(originalBitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(originalBitmap, 270f)
                else -> originalBitmap
            }

            val file = File(context.cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

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