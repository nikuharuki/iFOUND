package com.example.ifound

import android.os.Parcel
import android.os.Parcelable

data class LostItemData(
    val name: String? = null,
    val location: String? = null,
    val description: String? = null,
    val date: String? = null,
    val submittedBy: String? = null,
    val contact: String? = null,
    val image: String? = null,
    val childUid: String? = null): Parcelable {
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
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeString(submittedBy)
        parcel.writeString(contact)
        parcel.writeString(image)
        parcel.writeString(childUid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LostItemData> {
        override fun createFromParcel(parcel: Parcel): LostItemData {
            return LostItemData(parcel)
        }

        override fun newArray(size: Int): Array<LostItemData?> {
            return arrayOfNulls(size)
        }
    }
}
