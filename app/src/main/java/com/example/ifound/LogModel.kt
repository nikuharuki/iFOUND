package com.example.ifound

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LogModel(

    val message : String? = null

) : Parcelable
