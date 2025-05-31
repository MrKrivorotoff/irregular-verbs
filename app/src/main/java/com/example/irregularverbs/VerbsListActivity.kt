package com.example.irregularverbs

import android.os.Bundle
import android.widget.TableLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // verbsTable.addView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("IrregularVerbsList", mIrregularVerbs)
        super.onSaveInstanceState(outState)
    }
}