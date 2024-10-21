package com.example.electric.view;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

// Transformer cho ViewPager2 với hiệu ứng Depth (chiều sâu)
public class DepthPageTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1) {
            // Nếu trang này đã nằm quá xa bên trái (không còn hiển thị)
            page.setAlpha(0f);  // Làm cho trang này hoàn toàn trong suốt
        } else if (position <= 0) {
            // Trang hiện tại đang trượt về bên trái hoặc đang đứng yên
            page.setAlpha(1f);  // Hiển thị trang với độ trong suốt 100%
            page.setTranslationX(0f);  // Đặt vị trí X về 0 (không di chuyển)
            page.setTranslationZ(0f);  // Đặt trục Z về 0 (không hiệu ứng chiều sâu)
            page.setScaleX(1f);  // Đặt tỷ lệ X về kích thước bình thường
            page.setScaleY(1f);  // Đặt tỷ lệ Y về kích thước bình thường
        } else if (position <= 1) {
            // Trang này đang trượt về bên phải
            page.setAlpha(1 - position);  // Giảm độ trong suốt khi trượt
            page.setTranslationX(page.getWidth() * -position);  // Dịch chuyển theo chiều ngang
            page.setTranslationZ(-1f);  // Đẩy trang về phía sau (chiều sâu âm)

            // Tạo hiệu ứng thu nhỏ trang
            float scaleFactor = 0.75f + (1 - Math.abs(position)) * 0.25f;
            page.setScaleX(scaleFactor);  // Thu nhỏ theo trục X
            page.setScaleY(scaleFactor);  // Thu nhỏ theo trục Y
        } else {
            // Nếu trang đã nằm quá xa bên phải (không còn hiển thị)
            page.setAlpha(0f);  // Làm cho trang này hoàn toàn trong suốt
        }
    }
}
