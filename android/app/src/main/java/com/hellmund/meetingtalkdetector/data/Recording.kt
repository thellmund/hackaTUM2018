package com.hellmund.meetingtalkdetector.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Recording(
    val title: String,
    val timestamp: Long,
    val personStates: List<PersonState>
) : Parcelable
