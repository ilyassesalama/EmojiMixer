package com.emojimixer.functions;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;

public class UIMethods {

    public static void shadAnim(final View view, final String propertyName, final double value, final int duration) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(view);
        anim.setPropertyName(propertyName);
        anim.setFloatValues((float) value);
        anim.setDuration(duration);
        anim.start();
    }


    public static void colorAnimator(final View view, final String color1, final String color2, final double duration) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor(color1), Color.parseColor(color2));
        colorAnimation.addUpdateListener(animator -> {
            int color = (int) animator.getAnimatedValue();
            view.setBackgroundColor(color);
        });
        colorAnimation.setDuration((int) duration);
        colorAnimation.start();
    }

}
