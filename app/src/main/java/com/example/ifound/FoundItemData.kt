package com.example.ifound

import android.os.Parcel
import android.os.Parcelable

data class FoundItemData(val item: String? = null,
                        val addInfo: String? = null,
                        val whereFound: String? = null,
                        val whatRoom: String? = null,
                        val submittedBy: String? = null,
                         val phoneNo: String? = null,
                         val email: String? = null,
                         val image: String? = null,
                         val childUid: String? = null): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
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
        parcel.writeString(item)
        parcel.writeString(addInfo)
        parcel.writeString(whereFound)
        parcel.writeString(whatRoom)
        parcel.writeString(submittedBy)
        parcel.writeString(phoneNo)
        parcel.writeString(email)
        parcel.writeString(image)
        parcel.writeString(childUid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoundItemData> {
        override fun createFromParcel(parcel: Parcel): FoundItemData {
            return FoundItemData(parcel)
        }

        override fun newArray(size: Int): Array<FoundItemData?> {
            return arrayOfNulls(size)
        }
    }

}
