package com.bernaferrari.emojislider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public final class CircleHandle_C5190i extends Drawable {
    private final int f20904d;
    private final Paint myPaint = new Paint(1);
    float radius_f20901a;
    int color_f20902b = -1;
    int color_f20903c = 0;

    public CircleHandle_C5190i(Context context) {
        this.f20904d = context.getResources().getDimensionPixelSize(R.dimen.slider_handle_ring_thickness);
    }

    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final void draw(Canvas canvas) {
        float exactCenterX = getBounds().exactCenterX();
        float exactCenterY = getBounds().exactCenterY();
        this.myPaint.setColor(this.color_f20903c);
        canvas.drawCircle(exactCenterX, exactCenterY, this.radius_f20901a, this.myPaint);
        this.myPaint.setColor(this.color_f20902b);
        canvas.drawCircle(exactCenterX, exactCenterY, this.radius_f20901a - ((float) this.f20904d), this.myPaint);
    }

    public final int getIntrinsicHeight() {
        return (int) (this.radius_f20901a * 2.0f);
    }

    public final int getIntrinsicWidth() {
        return (int) (this.radius_f20901a * 2.0f);
    }

    public final void setAlpha(int i) {
        this.myPaint.setAlpha(i);
    }

    public final void setColorFilter(ColorFilter colorFilter) {
        this.myPaint.setColorFilter(colorFilter);
    }
}