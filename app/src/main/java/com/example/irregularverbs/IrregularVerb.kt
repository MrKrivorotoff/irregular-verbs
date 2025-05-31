package com.example.irregularverbs

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import org.apache.commons.csv.CSVFormat

data class IrregularVerb(
    val infinitive: String,
    val pastSimple: String,
    val pastParticiple: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(infinitive)
        parcel.writeString(pastSimple)
        parcel.writeString(pastParticiple)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<IrregularVerb> {
        override fun createFromParcel(parcel: Parcel): IrregularVerb {
            return IrregularVerb(parcel)
        }

        override fun newArray(size: Int): Array<IrregularVerb?> {
            return arrayOfNulls(size)
        }
    }
}

fun Activity.readIrregularVerbs(): ArrayList<IrregularVerb> = CSVFormat.DEFAULT
    .parse(assets.open("IrregularVerbs.csv").reader())
    .asSequence()
    .drop(1)
    .mapTo(ArrayList()) { IrregularVerb(it[0], it[1], it[2]) }