package com.mrkrivorotoff.irregularverbs

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import org.apache.commons.csv.CSVFormat

data class IrregularVerb(
    val index: Int,
    val infinitive: String,
    val pastSimple: String,
    val pastParticiple: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        index = parcel.readInt(),
        infinitive = parcel.readString().orEmpty(),
        pastSimple = parcel.readString().orEmpty(),
        pastParticiple = parcel.readString().orEmpty(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
        parcel.writeString(infinitive)
        parcel.writeString(pastSimple)
        parcel.writeString(pastParticiple)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<IrregularVerb> {
        override fun createFromParcel(parcel: Parcel): IrregularVerb =
            IrregularVerb(parcel)

        override fun newArray(size: Int): Array<IrregularVerb?> =
            arrayOfNulls(size)
    }
}

fun Activity.readIrregularVerbs(): ArrayList<IrregularVerb> = CSVFormat.DEFAULT
    .parse(assets.open("IrregularVerbs.csv").reader())
    .asSequence()
    .drop(1)
    .mapIndexedTo(ArrayList()) { i, it -> IrregularVerb(i, it[0], it[1], it[2]) }