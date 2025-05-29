package com.example.irregularverbs

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.apache.commons.csv.CSVFormat

class MainActivity : AppCompatActivity() {
    private lateinit var mInfinitiveText: TextView
    private lateinit var mPastSimpleText: TextView
    private lateinit var mPastParticipleText: TextView
    private lateinit var mActionButton: Button

    private lateinit var mIrregularVerbs: ArrayList<IrregularVerb>

    private var mCurrentVerb: IrregularVerb? = null
    private var mHasPastRevealed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mInfinitiveText = findViewById(R.id.infinitive)
        mPastSimpleText = findViewById(R.id.past_simple)
        mPastParticipleText = findViewById(R.id.past_participle)

        val actionButton = findViewById<Button>(R.id.button_action)
        actionButton.setOnClickListener { onActionButtonClick() }
        mActionButton = actionButton

        mIrregularVerbs = savedInstanceState?.getParcelableArrayList(
            "IrregularVerbsList",
            IrregularVerb::class.java
        ) ?: readIrregularVerbs()

        mCurrentVerb = savedInstanceState?.getParcelable("CurrentVerb", IrregularVerb::class.java)
        mHasPastRevealed = savedInstanceState?.getBoolean("HasPastRevealed") == true
        updateVerbTexts()
        updateButtonText()
    }

    private fun readIrregularVerbs(): ArrayList<IrregularVerb> = CSVFormat.DEFAULT
        .parse(assets.open("IrregularVerbs.csv").reader())
        .asSequence()
        .drop(1)
        .mapTo(ArrayList()) { IrregularVerb(it[0], it[1], it[2]) }

    private fun onActionButtonClick() {
        if (isActionRoll()) {
            mCurrentVerb = mIrregularVerbs.random()
            mHasPastRevealed = false
        } else {
            mHasPastRevealed = true
        }
        updateVerbTexts()
        updateButtonText()
    }

    private fun isActionRoll() = mCurrentVerb == null || mHasPastRevealed

    private fun updateButtonText() {
        mActionButton.text = getText(
            if (isActionRoll())
                R.string.roll_verb
            else
                R.string.show_past
        )
    }

    private fun updateVerbTexts() {
        val currentVerb = mCurrentVerb
        mInfinitiveText.text = currentVerb?.infinitive.orEmpty()
        if (mHasPastRevealed && currentVerb != null) {
            mPastSimpleText.text = currentVerb.pastSimple
            mPastParticipleText.text = currentVerb.pastParticiple
        } else {
            mPastSimpleText.text = ""
            mPastParticipleText.text = ""
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("IrregularVerbsList", mIrregularVerbs)
        outState.putParcelable("CurrentVerb", mCurrentVerb)
        outState.putBoolean("HasPastRevealed", mHasPastRevealed)
        super.onSaveInstanceState(outState)
    }
}

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