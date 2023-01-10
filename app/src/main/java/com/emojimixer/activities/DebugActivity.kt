package com.emojimixer.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DebugActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AlertDialog.Builder(this)
            .setTitle("An error occurred")
            .setMessage(intent.getStringExtra("error"))
            .setPositiveButton("End Application") { _, _ -> finish() }
            .create()
            .show()
    }
}