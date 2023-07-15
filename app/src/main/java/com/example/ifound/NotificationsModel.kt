package com.example.ifound

import android.os.Parcel
import android.os.Parcelable

data class NotificationsModel(
    val childUid: String? = null,
    val claimReqUid: String? = null,
    val image: String? = null,
    val message: String? = null,
    val timestamp: String? = null,
    val sentTo: String? = null
)