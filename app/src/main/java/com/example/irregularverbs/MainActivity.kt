package com.example.irregularverbs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

private const val MAX_NUMBER_OF_PREVIOUS_VERBS = 5

private const val IRREGULAR_VERBS_LIST_KEY = "IrregularVerbsList"
private const val PREVIOUS_VERBS_ARRAY_KEY = "PreviousVerbsArray"
private const val CURRENT_VERB_KEY = "CurrentVerb"
private const val HAS_PAST_REVEALED_KEY = "HasPastRevealed"

class MainActivity : AppCompatActivity() {
    private lateinit var mInfinitiveText: TextView
    private lateinit var mPastSimpleText: TextView
    private lateinit var mPastParticipleText: TextView

    private lateinit var mDoMainActionButton: Button
    private lateinit var mOpenVerbsListButton: Button
    private lateinit var mTranslateButton: Button

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

        val doMainActionButton = findViewById<Button>(R.id.button_do_main_action)
        doMainActionButton.setOnClickListener { onDoMainActionButtonClick() }
        mDoMainActionButton = doMainActionButton

        val openVerbsListButton = findViewById<Button>(R.id.button_open_verbs_list)
        openVerbsListButton.setOnClickListener { onOpenVerbsListButtonClick() }
        mOpenVerbsListButton = openVerbsListButton

        val translateButton = findViewById<Button>(R.id.button_translate)
        translateButton.setOnClickListener { onTranslateButtonClick() }
        mTranslateButton = translateButton

        mIrregularVerbs = savedInstanceState?.getParcelableArrayList(
            IRREGULAR_VERBS_LIST_KEY,
            IrregularVerb::class.java,
        ) ?: readIrregularVerbs()
        mPreviousVerbs = savedInstanceState?.getParcelableArray(
            PREVIOUS_VERBS_ARRAY_KEY,
            IrregularVerb::class.java,
        )?.let { ArrayDeque(it.asList()) } ?: ArrayDeque(MAX_NUMBER_OF_PREVIOUS_VERBS)

        mCurrentVerb =
            savedInstanceState?.getParcelable(CURRENT_VERB_KEY, IrregularVerb::class.java)
        mHasPastRevealed = savedInstanceState?.getBoolean(HAS_PAST_REVEALED_KEY) == true
        updateVerbTensesText()
        updateMainActionButtonText()
        updateTranslateAvailability()
    }

    private fun onDoMainActionButtonClick() {
        if (isActionRoll()) {
            rollNextVerbAndReturnPrevious()
                ?.storeAsPrevious()
            mHasPastRevealed = false
        } else {
            mHasPastRevealed = true
        }
        updateVerbTensesText()
        updateMainActionButtonText()
        updateTranslateAvailability()
    }

    private fun isActionRoll() = mCurrentVerb == null || mHasPastRevealed

    private fun rollNextVerbAndReturnPrevious(): IrregularVerb? {
        val currentVerb = mCurrentVerb
        val irregularVerbs = if (currentVerb == null)
            mIrregularVerbs
        else
            mIrregularVerbs.run {
                filterNotTo(ArrayList(size - 1)) { it.infinitive == currentVerb.infinitive }
            }
        mCurrentVerb = irregularVerbs.random()
        return currentVerb
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
            if (isActionRoll())
                R.string.roll_verb
            else
                R.string.show_past
        )
    }

    private fun updateTranslateAvailability() {
        mTranslateButton.setEnabled(mCurrentVerb != null)
    }

    private fun onOpenVerbsListButtonClick() {
        val intent = Intent(this, VerbsListActivity::class.java)
        intent.putParcelableArrayListExtra("IrregularVerbsList", mIrregularVerbs)
        startActivity(intent)
    }

    private fun getStringResIdByName(name: String): Int? = R.string::class.java.fields
        .firstOrNull { it.name == name }
        ?.let { it.getInt(it) }

    private fun onTranslateButtonClick() {
        val verb = mCurrentVerb?.infinitive
        if (verb != null) {
            val stringResId = getStringResIdByName("verb_$verb")
            if (stringResId != null) {
                val currentLocale = getCurrentLocale()
                val title = if (currentLocale == null)
                    resources.getString(R.string.translation)
                else
                    "${resources.getString(R.string.translation)} ${currentLocale.language}"
                AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(resources.getText(stringResId))
                    .create()
                    .show()
            }
        }
    }

    private fun Context.getCurrentLocale(): Locale? =
        resources.configuration.getLocales().get(0)

    override fun onSaveInstanceState(outState: Bundle) {
        with(outState) {
            putParcelableArrayList(IRREGULAR_VERBS_LIST_KEY, mIrregularVerbs)
            putParcelableArray(PREVIOUS_VERBS_ARRAY_KEY, mPreviousVerbs.toTypedArray())
            putParcelable(CURRENT_VERB_KEY, mCurrentVerb)
            putBoolean(HAS_PAST_REVEALED_KEY, mHasPastRevealed)
        }
        super.onSaveInstanceState(outState)
    }
}