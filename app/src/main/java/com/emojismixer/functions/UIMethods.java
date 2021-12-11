package com.emojismixer.functions;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
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

    public static void colorAnimator(final View view, final String color1, final String color2, final double duration) {
        android.animation.ValueAnimator colorAnimation = android.animation.ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor(color1), Color.parseColor(color2));
        colorAnimation.addUpdateListener(animator -> {
            int color = (int) animator.getAnimatedValue();
            view.setBackgroundColor(color);
        });
        colorAnimation.setDuration((int) duration);
        colorAnimation.start();
    }

}
