package com.mrkrivorotoff.irregularverbs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

private const val MAX_NUMBER_OF_PREVIOUS_VERBS = 10

private const val IRREGULAR_VERBS_LIST_KEY = "IrregularVerbsList"
private const val PREVIOUS_VERBS_INDEX_ARRAY_KEY = "PreviousVerbsIndexArray"
private const val CURRENT_VERB_INDEX_KEY = "CurrentVerbIndex"
private const val HAS_PAST_REVEALED_KEY = "HasPastRevealed"

class MainActivity : AppCompatActivity() {
    private lateinit var mInfinitiveText: TextView
    private lateinit var mPastSimpleText: TextView
    private lateinit var mPastParticipleText: TextView

    private lateinit var mDoMainActionButton: Button
    private lateinit var mOpenVerbsListButton: Button
    private lateinit var mTranslateButton: Button
    private lateinit var mPreviousVerbButton: Button

    private lateinit var mIrregularVerbs: ArrayList<IrregularVerb>
    private lateinit var mPreviousVerbs: ArrayDeque<IrregularVerb>

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

        mDoMainActionButton = findViewById<Button>(R.id.button_do_main_action).also {
            it.setOnClickListener(::onDoMainActionButtonClick)
        }
        mOpenVerbsListButton = findViewById<Button>(R.id.button_open_verbs_list).also {
            it.setOnClickListener(::onOpenVerbsListButtonClick)
        }
        mTranslateButton = findViewById<Button>(R.id.button_translate).also {
            it.setOnClickListener(::onTranslateButtonClick)
        }
        mPreviousVerbButton = findViewById<Button>(R.id.button_previous_verb).also {
            it.setOnClickListener(::onPreviousVerbButtonClick)
        }

        val irregularVerbs = savedInstanceState?.getParcelableArrayList(
            IRREGULAR_VERBS_LIST_KEY,
            IrregularVerb::class.java,
        ) ?: readIrregularVerbs()
        mIrregularVerbs = irregularVerbs
        val previousVerbs = ArrayDeque<IrregularVerb>(MAX_NUMBER_OF_PREVIOUS_VERBS)
        savedInstanceState?.getIntArray(PREVIOUS_VERBS_INDEX_ARRAY_KEY)?.forEach { index ->
            previousVerbs.addLast(irregularVerbs[index])
        }
        mPreviousVerbs = previousVerbs

        val currentVerbIndex = savedInstanceState?.getInt(CURRENT_VERB_INDEX_KEY, -1) ?: -1
        mCurrentVerb = if (currentVerbIndex < 0) null else irregularVerbs[currentVerbIndex]
        mHasPastRevealed = savedInstanceState?.getBoolean(HAS_PAST_REVEALED_KEY) == true
        updateVerbTensesText()
        updateMainActionButtonText()
        updateTranslateAvailability()
        updatePreviousVerbButton()
    }

    private fun onDoMainActionButtonClick(v: View) {
        if (isActionSelectNext()) {
            selectNextVerbAndReturnPrev()
                ?.storeAsPrevious()
            mHasPastRevealed = false
        } else {
            mHasPastRevealed = true
        }
        updateVerbTensesText()
        updateMainActionButtonText()
        updateTranslateAvailability()
        updatePreviousVerbButton()
    }

    private fun isActionSelectNext() = mCurrentVerb == null || mHasPastRevealed

    private fun selectNextVerbAndReturnPrev(): IrregularVerb? = mCurrentVerb.also { currentVerb ->
        mCurrentVerb = if (currentVerb == null)
            mIrregularVerbs.random()
        else
            mIrregularVerbs.copyAllExceptIndexToArray(currentVerb.index).random()
    }

    private fun IrregularVerb.storeAsPrevious() {
        val previousVerbs = mPreviousVerbs
        if (previousVerbs.size == MAX_NUMBER_OF_PREVIOUS_VERBS)
            previousVerbs.removeFirst()
        previousVerbs.addLast(this)
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

    private fun updateMainActionButtonText() {
        mDoMainActionButton.text = getText(
            if (isActionSelectNext())
                R.string.select_verb
            else
                R.string.show_past
        )
    }

    private fun updateTranslateAvailability() {
        mTranslateButton.setEnabled(mCurrentVerb != null)
    }

    private fun updatePreviousVerbButton() {
        val previousVerbButton = mPreviousVerbButton
        val previousVerb = mPreviousVerbs.lastOrNull()
        if (previousVerb == null) {
            previousVerbButton.setEnabled(false)
            previousVerbButton.text = getString(R.string.previous_verb)
        } else {
            previousVerbButton.setEnabled(true)
            previousVerbButton.text =
                getString(R.string.previous_verb_template, previousVerb.infinitive)
        }
    }

    private fun onOpenVerbsListButtonClick(v: View) {
        val intent = Intent(this, VerbsListActivity::class.java)
        intent.putParcelableArrayListExtra("IrregularVerbsList", mIrregularVerbs)
        startActivity(intent)
    }

    private fun getStringResIdByName(name: String): Int? = R.string::class.java.fields
        .firstOrNull { it.name == name }
        ?.let { it.getInt(it) }

    private fun onTranslateButtonClick(v: View) {
        val verb = mCurrentVerb?.infinitive
        if (verb != null) {
            val stringResId = getStringResIdByName("verb_$verb")
            if (stringResId != null) {
                val currentLocale = getCurrentLocale()
                val title = if (currentLocale == null)
                    getString(R.string.translation)
                else
                    getString(R.string.translation_template, currentLocale.language)
                AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(getText(stringResId))
                    .create()
                    .show()
            }
        }
    }

    private fun Context.getCurrentLocale(): Locale? =
        resources.configuration.getLocales().get(0)

    private fun onPreviousVerbButtonClick(v: View) {
        mCurrentVerb = mPreviousVerbs.removeLast()
        mHasPastRevealed = false
        updateVerbTensesText()
        updateMainActionButtonText()
        updatePreviousVerbButton()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        with(outState) {
            putParcelableArrayList(IRREGULAR_VERBS_LIST_KEY, mIrregularVerbs)
            putIntArray(PREVIOUS_VERBS_INDEX_ARRAY_KEY, mPreviousVerbs.mapToIntArray { it.index })
            putInt(CURRENT_VERB_INDEX_KEY, mCurrentVerb?.index ?: -1)
            putBoolean(HAS_PAST_REVEALED_KEY, mHasPastRevealed)
        }
        super.onSaveInstanceState(outState)
    }
}