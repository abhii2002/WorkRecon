package com.blissvine.workrecon.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeString

data class SelectedMembers (
        val id: String = "",
        val image: String = ""

): Parcelable{
        constructor(parcel: Parcel) : this(
                parcel.readString()!!,
                parcel.readString()!!
        ) {
        }

        override fun describeContents() = 0


        override fun writeToParcel(dest: Parcel, p1: Int) = with (dest){
                writeString(id)
                writeString(image)
        }

        companion object CREATOR : Parcelable.Creator<SelectedMembers> {
                override fun createFromParcel(parcel: Parcel): SelectedMembers {
                        return SelectedMembers(parcel)
                }

                override fun newArray(size: Int): Array<SelectedMembers?> {
                        return arrayOfNulls(size)
                }
        }

}