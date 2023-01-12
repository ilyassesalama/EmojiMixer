package com.emojimixer

import android.app.Application
import android.content.Intent
import com.emojimixer.activities.DebugActivity
import com.google.android.material.color.DynamicColors
import kotlin.system.exitProcess

class ApplicationLoader : Application() {
    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)
        Thread.setDefaultUncaughtExceptionHandler {
                _, throwable ->
            val intent = Intent(this, DebugActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("error", throwable.stackTraceToString())
            throwable.printStackTrace()
            startActivity(intent)
            exitProcess(0)
        }

        super.onCreate()
    }
}