package com.mrkrivorotoff.irregularverbs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get

class VerbsListActivity : AppCompatActivity() {
    private lateinit var mVerbsTable: TableLayout

    private lateinit var mIrregularVerbs: ArrayList<IrregularVerb>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verbs_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val verbsTable = findViewById<TableLayout>(R.id.verbs_table)
        mVerbsTable = verbsTable

        val irregularVerbs = savedInstanceState?.getParcelableArrayList(
            "IrregularVerbsList",
            IrregularVerb::class.java,
        ) ?: intent.getParcelableArrayListExtra(
            "IrregularVerbsList",
            IrregularVerb::class.java,
        ) ?: throw IllegalArgumentException()
        mIrregularVerbs = irregularVerbs

        var currentFirstChar = Char.MIN_VALUE
        for (irregularVerb in irregularVerbs) {
            val firstChar: Char = irregularVerb.infinitive[0]
            if (firstChar != currentFirstChar) {
                currentFirstChar = firstChar
                verbsTable.addCharRow(firstChar)
            }
            verbsTable.addVerbRow(irregularVerb)
        }
    }

    private fun TableLayout.addCharRow(char: Char) {
        @SuppressLint("InflateParams")
        val row = LayoutInflater.from(this@VerbsListActivity)
            .inflate(R.layout.char_row_view, null) as TableRow
        (row[0] as TextView).text = char.toString()
        (row[1] as TextView).text = ""
        (row[2] as TextView).text = ""
        addView(row)
    }

    private fun TableLayout.addVerbRow(irregularVerb: IrregularVerb) {
        @SuppressLint("InflateParams")
        val row = LayoutInflater.from(this@VerbsListActivity)
            .inflate(R.layout.verb_row_view, null) as TableRow
        (row[0] as TextView).text = irregularVerb.infinitive
        (row[1] as TextView).text = irregularVerb.pastSimple
        (row[2] as TextView).text = irregularVerb.pastParticiple
        addView(row)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("IrregularVerbsList", mIrregularVerbs)
        super.onSaveInstanceState(outState)
    }
}