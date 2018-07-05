package com.bernaferrari.emojislider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Handler;
import android.os.Looper;

public final class C7851k extends Drawable implements Callback, Runnable {
    public final Handler f32883a = new Handler(Looper.getMainLooper());
    private final Drawable f32886d;
    private final DrawSeekBar f32887e;
    private final int f32888f;
    private final int f32889g;
    public boolean f32885c;

    public C7851k(Drawable f32886d, Context context) {
        this.f32886d = f32886d;
        Resources resources = context.getResources();
        this.f32888f = resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_height);
        this.f32889g = resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_slider_padding);
//        SPRING_C1558e a = C6724v.m13495c().m4019a();
//        a.f6938b = true;
//        this.f32884b = a.m4027a(C1559f.m4038b(3.0d, 5.0d)).m4028a((C1561h) this);
//        this.f32886d = C0835a.m2242a(context, (int) R.drawable.slider_sticker_tray_background);
//        this.f32886d.setCallback(this);
        this.f32887e = new DrawSeekBar(context);
        this.f32887e.setCallback(this);
        DrawSeekBar drawSeekBar = this.f32887e;
        drawSeekBar.f32841h = true;
        drawSeekBar.invalidateSelf();
        this.f32887e.m18487a("😍");
        this.f32887e.m18483a(resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_slider_handle_size));
        this.f32887e.m18486a(C5186e.EMOJI);
        this.f32887e.m18489b(resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_track_height));
    }

    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
    }

    public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
    }

//    public final void mo964a(SPRING_C1558e c1558e) {
//        this.f32887e.m18488b((float) c1558e.f6940d.f6934a);
//    }

//    public final void mo965b(SPRING_C1558e c1558e) {
//        if (this.f32885c) {
//            C0486e.m1108b(this.f32883a, this, 1750, 1182779826);
//        }
//    }

    public final void draw(Canvas canvas) {
        this.f32886d.draw(canvas);
        this.f32887e.draw(canvas);
    }

    public final void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    public final void run() {
        double d = 0.0d;
//        Object obj = this.f32884b.f6940d.f6934a == 0.0d ? 1 : null;
//        SPRING_C1558e c1558e = this.f32884b;
//        if (obj != null) {
//            d = 0.5d;
//        }
//        c1558e.m4030b(d);
    }

    public final void setAlpha(int i) {
        this.f32886d.setAlpha(i);
        this.f32887e.setAlpha(i);
    }

    public final void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        int i5 = ((i4 - i2) / 2) + i2;
        this.f32886d.setBounds(i, i5 - (this.f32888f / 2), i3, i5 + (this.f32888f / 2));
        this.f32887e.setBounds(i + this.f32889g, i2, i3 - this.f32889g, i4);
    }

    public final void setColorFilter(ColorFilter colorFilter) {
        this.f32886d.setColorFilter(colorFilter);
        this.f32887e.setColorFilter(colorFilter);
    }
}


