package com.hellmund.meetingtalkdetector.util

import android.graphics.*
import android.util.Base64
import io.fotoapparat.preview.Frame
import java.io.ByteArrayOutputStream

fun Bitmap.rotatedBy(degree: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, false)
}

fun Bitmap.encoded(): String {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val bytes = stream.toByteArray()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

fun Frame.copyToBitmap(dstBitmap: Bitmap, dstWidth: Int) {
    // Get a ByteArray from the Frame
    val yuvImage = YuvImage(image, ImageFormat.NV21, size.width, size.height, null)
    val outputStream = ByteArrayOutputStream()
    val rect = Rect(0, 0, yuvImage.width, yuvImage.height)
    yuvImage.compressToJpeg(rect, 80, outputStream)
    val byteArray = outputStream.toByteArray()

    // Convert to a Bitmap
    ImageConverter.scaleByteArrayIntoBitmap(
        byteArray,
        dstBitmap,
        dstWidth
    )
}
