package com.hellmund.meetingtalkdetector.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.util.Base64
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PersonState(
    val id: Int,
    val imageData: String?,
    val talkingTime: Long
) : Parcelable {

    val image: Bitmap?
        get() {
            return imageData?.let {
                val byteArray = Base64.decode(imageData, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }
        }

}
