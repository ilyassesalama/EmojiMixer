package com.emojimixer.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DebugActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val error = intent.getStringExtra("error")
        MaterialAlertDialogBuilder(this).apply {
            setTitle("An error occurred")
            setMessage(error)
            setPositiveButton("OK") { _, _ -> finish() }
            setNegativeButton("Copy") { _, _ ->
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("error", error)
                clipboard.setPrimaryClip(clip)
                finish()
            }
            show()
        }
    }
}