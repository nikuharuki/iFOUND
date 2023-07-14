package com.example.ifound

import android.os.Parcel
import android.os.Parcelable

data class ClaimRequestData(
    val claimId: String? = null,
    val claimer: String? = null,
    val location: String? = null,
    val date: String? = null,
    val description: String? = null,
    val foundItemId: String? = null,
    val foundItemSubmitter: String? = null,
    val foundItemImage: String? = null): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(claimId)
        parcel.writeString(claimer)
        parcel.writeString(location)
        parcel.writeString(date)
        parcel.writeString(description)
        parcel.writeString(foundItemId)
        parcel.writeString(foundItemSubmitter)
        parcel.writeString(foundItemImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClaimRequestData> {
        override fun createFromParcel(parcel: Parcel): ClaimRequestData {
            return ClaimRequestData(parcel)
        }

        override fun newArray(size: Int): Array<ClaimRequestData?> {
            return arrayOfNulls(size)
        }
    }
}
