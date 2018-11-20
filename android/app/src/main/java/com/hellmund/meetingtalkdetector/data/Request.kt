package com.hellmund.meetingtalkdetector.data

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import com.hellmund.meetingtalkdetector.util.encoded

data class Request(
    val timestamp: Long,
    @SerializedName("image_data") val imageData: String,
    @SerializedName("requires_thumbnails") val requiresThumbnails: Boolean
) {

    companion object {

        fun create(bitmap: Bitmap, requiresThumbnails: Boolean): Request {
            return Request(
                System.currentTimeMillis(),
                bitmap.encoded(),
                requiresThumbnails
            )
        }

    }

}
