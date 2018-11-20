package com.hellmund.meetingtalkdetector.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ImageConverter {

    companion object {

        fun scaleByteArrayIntoBitmap(byteArray: ByteArray, dstBitmap: Bitmap, dstWidth: Int) {
            // Get bitmap width
            val bitmapOptions =
                getScaledBitmapFromByteArray(byteArray)
            val srcWidth = bitmapOptions.outWidth

            // Calculate bitmap inSampleSize
            bitmapOptions.inSampleSize =
                    getRequiredInSampleSize(
                        srcWidth,
                        dstWidth
                    )

            // Resize inSampleSize bitmap to required width
            bitmapOptions.inScaled = true
            bitmapOptions.inDensity = srcWidth
            bitmapOptions.inTargetDensity = dstWidth * bitmapOptions.inSampleSize

            // Decode bitmap with determined size
            bitmapOptions.inJustDecodeBounds = false
            bitmapOptions.inBitmap = dstBitmap

            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, bitmapOptions)
        }

        private fun getScaledBitmapFromByteArray(byteArray: ByteArray): BitmapFactory.Options {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
            return options
        }

        private fun getRequiredInSampleSize(srcWidth: Int, dstWidth: Int): Int {
            var inSampleSize = 1

            if (srcWidth > dstWidth) {
                val halfWidth = srcWidth / 2

                while (halfWidth / inSampleSize >= dstWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

    }

}