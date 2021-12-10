package com.emojismixer.functions;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class UIMethods {

    public static void shadAnim(final View view, final String propertyName, final double value, final int duration) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(view);
        anim.setPropertyName(propertyName);
        anim.setFloatValues((float) value);
        anim.setDuration(duration);
        anim.start();
    }

    public static void rotateAnimation(final View view) {
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(900);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        view.startAnimation(rotate);
    }

}
