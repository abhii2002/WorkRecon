package com.blissvine.workrecon.models

import android.os.Parcel
import android.os.Parcelable

data class User (
    val id: String = "",
    val name: String ="",
    val email: String ="",
    val image: String = "",
    val mobile: Long = 0,
    val fcmToken: String = "",
    var selected: Boolean = false
// Making this class parcelable with the help of parcelable plugin which generates parcelable code
//Parcelable is a serialization mechanism provided by Android to pass complex data from one activity
// to another activity.
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!
    ) {
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest)  {
       writeString(id)
       writeString(name)
        writeString(email)
        writeString(image)
        writeLong(mobile)
        writeString(fcmToken)
    }



    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}

