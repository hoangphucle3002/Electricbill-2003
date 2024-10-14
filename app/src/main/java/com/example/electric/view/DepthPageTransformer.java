package com.example.electric.view;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class DepthPageTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1) {
            page.setAlpha(0f);
        } else if (position <= 0) {
            page.setAlpha(1f);
            page.setTranslationX(0f);
            page.setTranslationZ(0f);
            page.setScaleX(1f);
            page.setScaleY(1f);
        } else if (position <= 1) {
            page.setAlpha(1 - position);
            page.setTranslationX(page.getWidth() * -position);
            page.setTranslationZ(-1f);
            float scaleFactor = 0.75f + (1 - Math.abs(position)) * 0.25f;
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
        } else {
            page.setAlpha(0f);
        }
    }
}
