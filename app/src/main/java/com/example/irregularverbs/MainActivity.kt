package com.example.irregularverbs

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var mInfinitiveText: TextView
    private lateinit var mPastSimpleText: TextView
    private lateinit var mPastParticipleText: TextView
    private lateinit var mDoMainActionButton: Button
    private lateinit var mOpenVerbsListButton: Button

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

        val doMainActionButton = findViewById<Button>(R.id.button_do_main_action)
        doMainActionButton.setOnClickListener { onDoMainActionButtonClick() }
        mDoMainActionButton = doMainActionButton

        val openVerbsListButton = findViewById<Button>(R.id.button_open_verbs_list)
        openVerbsListButton.setOnClickListener { onOpenVerbsListButtonClick() }
        mOpenVerbsListButton = openVerbsListButton

        mIrregularVerbs = savedInstanceState?.getParcelableArrayList(
            "IrregularVerbsList",
            IrregularVerb::class.java
        ) ?: readIrregularVerbs()

        mCurrentVerb = savedInstanceState?.getParcelable("CurrentVerb", IrregularVerb::class.java)
        mHasPastRevealed = savedInstanceState?.getBoolean("HasPastRevealed") == true
        updateVerbTensesText()
        updateMainActionButtonText()
    }

    private fun onDoMainActionButtonClick() {
        if (isActionRoll()) {
            val currentVerb = mCurrentVerb
            val irregularVerbs = if (currentVerb == null)
                mIrregularVerbs
            else
                mIrregularVerbs.apply {
                    filterNotTo(ArrayList(size - 1)) { it.infinitive == currentVerb.infinitive }
                }
            mCurrentVerb = irregularVerbs.random()
            mHasPastRevealed = false
        } else {
            mHasPastRevealed = true
        }
        updateVerbTensesText()
        updateMainActionButtonText()
    }

    private fun onOpenVerbsListButtonClick() {
        val intent = Intent(this, VerbsListActivity::class.java)
        intent.putParcelableArrayListExtra("IrregularVerbsList", mIrregularVerbs)
        startActivity(intent)
    }

    private fun isActionRoll() = mCurrentVerb == null || mHasPastRevealed

    private fun updateMainActionButtonText() {
        mDoMainActionButton.text = getText(
            if (isActionRoll())
                R.string.roll_verb
            else
                R.string.show_past
        )
    }

    private fun updateVerbTensesText() {
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