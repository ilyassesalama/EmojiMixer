package com.emojimixer.functions

import android.animation.ObjectAnimator
import android.view.View

object UIMethods {
    @JvmStatic
    fun shadAnim(view: View?, propertyName: String?, value: Double, duration: Int) {
        val anim = ObjectAnimator()
        anim.target = view
        anim.setPropertyName(propertyName!!)
        anim.setFloatValues(value.toFloat())
        anim.duration = duration.toLong()
        anim.start()
    }

}