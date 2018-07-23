package com.bernaferrari.emojisliderSample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;

public final class C7852l extends Drawable implements Callback {
    float f32890a;
    private Drawable f32891b;

    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
    }

    public final void setAlpha(int i) {
    }

    public final void setColorFilter(ColorFilter colorFilter) {
    }

    public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
    }

    public final void mo1692a(Bitmap bitmap) {
//        this.f32891b = new BitmapDrawable(getResources(), bitmap)
        this.f32891b.setCallback(this);
        this.f32891b.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        invalidateSelf();
    }

    public final void draw(Canvas canvas) {
        if (this.f32891b != null && this.f32890a > 0.0f) {
            float max = Math.max(this.f32890a / ((float) this.f32891b.getIntrinsicWidth()), this.f32890a / ((float) this.f32891b.getIntrinsicHeight()));
            canvas.save();
            canvas.translate((float) getBounds().left, (float) getBounds().top);
            canvas.scale(max, max);
            this.f32891b.draw(canvas);
            canvas.restore();
        }
    }

    public final int getIntrinsicHeight() {
        return (int) this.f32890a;
    }

    public final int getIntrinsicWidth() {
        return (int) this.f32890a;
    }

    public final void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }
}