package com.bernaferrari.emojislider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Build.VERSION;
import android.support.v4.content.ContextCompat;
import android.text.Layout.Alignment;

public final class C5189h extends Drawable implements Callback {
    final textDrawable f20900a;

    public C5189h(Context context) {
        Resources resources = context.getResources();
        int b = ContextCompat.getColor(context, R.color.slider_sticker_question_text);
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.slider_sticker_question_text_size);
        this.f20900a = new textDrawable(context, resources.getDimensionPixelSize(R.dimen.slider_sticker_question_width));
        this.f20900a.m10646a(Alignment.ALIGN_CENTER);
        this.f20900a.setCallback(this);
        this.f20900a.setColor(b);
        this.f20900a.setTextSize((float) dimensionPixelSize);
        if (VERSION.SDK_INT >= 21) {
//            this.f20900a.m10650c();
//            this.f20900a.m10644a(ac.m7427a());
//            return;
        }
        this.f20900a.m10645a(Typeface.SANS_SERIF, 0);
    }

    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
    }

    public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
    }

    public final void draw(Canvas canvas) {
        float min = Math.min(1.0f, ((float) getBounds().height()) / ((float) this.f20900a.getIntrinsicHeight()));
        canvas.save();
        canvas.scale(min, min, getBounds().exactCenterX(), getBounds().exactCenterY());
        try {
            this.f20900a.draw(canvas);
        } catch (Exception e) {

        }
        canvas.restore();
    }

    public final int getIntrinsicHeight() {
        return this.f20900a.getIntrinsicHeight();
    }

    public final int getIntrinsicWidth() {
        return this.f20900a.getIntrinsicWidth();
    }

    public final void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    protected final void onBoundsChange(Rect rect) {
        this.f20900a.setBounds((int) (rect.exactCenterX() - (((float) getIntrinsicWidth()) / 2.0f)), (int) (rect.exactCenterY() - (((float) getIntrinsicHeight()) / 2.0f)), (int) (rect.exactCenterX() + (((float) getIntrinsicWidth()) / 2.0f)), (int) (rect.exactCenterY() + (((float) getIntrinsicHeight()) / 2.0f)));
    }

    public final void setAlpha(int i) {
        this.f20900a.setAlpha(i);
    }

    public final void setColorFilter(ColorFilter colorFilter) {
        this.f20900a.setColorFilter(colorFilter);
    }
}
