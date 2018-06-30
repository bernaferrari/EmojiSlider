package com.bernaferrari.emojislider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;

import com.bernaferrari.emojislider.p228m.C1558e;
import com.bernaferrari.emojislider.p228m.C1559f;
import com.bernaferrari.emojislider.p228m.C1561h;
import com.bernaferrari.emojislider.p228m.C6724v;
import com.orhanobut.logger.Logger;

public final class C7849d extends Drawable implements Callback, C1561h {
    final al f32860a;
    final CircleHandle_C5190i f32861b;
    final C7852l f32862c;
    final C1558e f32863d = C6724v.m13495c().m4019a().m4028a(this).m4025a(1.0d).m4027a(C1559f.m4037a(40.0d, 7.0d));
    float f32864e;
    C5186e f32865f;
    C5186e f32866g;

    public C7849d(Context context) {
        this.f32860a = new al(context, getWidthPixels(context));
        this.f32860a.setCallback(this);
        this.f32861b = new CircleHandle_C5190i(context);
        this.f32861b.setCallback(this);
        this.f32862c = new C7852l();
        this.f32862c.setCallback(this);
    }

    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
    }

    public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
    }

    public int getWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    private void m18494a(Canvas canvas, C5186e c5186e, float f) {
        Drawable drawable;

        final /* synthetic */ int[] f20875a = new int[C5186e.values().length];

        Logger.d("f20875a ordinal value is: " + f20875a[c5186e.ordinal()]);
        switch (f20875a[c5186e.ordinal()]) {
            case 1:
                drawable = this.f32860a;
                break;
            case 2:
                drawable = this.f32861b;
                break;
            case 3:
                drawable = this.f32862c;
                break;
            default:
                drawable = this.f32862c;

//                StringBuilder stringBuilder = new StringBuilder("Unsupported handle type: ");
//                stringBuilder.append(this.f32865f);
//                throw new IllegalStateException(stringBuilder.toString());
        }
        float intrinsicWidth = (this.f32864e - ((float) drawable.getIntrinsicWidth())) / 2.0f;
        float intrinsicHeight = (this.f32864e - ((float) drawable.getIntrinsicHeight())) / 2.0f;
        canvas.save();
        canvas.translate(intrinsicWidth, intrinsicHeight);
        canvas.scale(f, f, getBounds().exactCenterX(), getBounds().exactCenterY());
        drawable.draw(canvas);
        canvas.restore();
    }

    public final void mo964a() {
        invalidateSelf();
    }

    public final void m18496a(C5186e c5186e) {
        this.f32865f = c5186e;
        this.f32866g = null;
        this.f32863d.m4026a(1.0d, true);
        invalidateSelf();
    }

    public final void draw(Canvas canvas) {
        float f = (float) this.f32863d.f6940d.f6934a;
        if (this.f32866g != null && f < 1.0f) {
            m18494a(canvas, this.f32866g, 1.0f - f);
        }
        if (this.f32865f != null && f > 0.0f) {
            m18494a(canvas, this.f32865f, f);
        }
    }

    public final int getIntrinsicHeight() {
        return (int) this.f32864e;
    }

    public final int getIntrinsicWidth() {
        return (int) this.f32864e;
    }

    public final void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    public final void setAlpha(int i) {
        this.f32860a.setAlpha(i);
        this.f32861b.setAlpha(i);
        this.f32862c.setAlpha(i);
    }

    public final void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.f32860a.setBounds(i, i2, i3, i4);
        this.f32861b.setBounds(i, i2, i3, i4);
        this.f32862c.setBounds(i, i2, i3, i4);
    }

    public final void setColorFilter(ColorFilter colorFilter) {
        this.f32860a.setColorFilter(colorFilter);
        this.f32861b.setColorFilter(colorFilter);
        this.f32862c.setColorFilter(colorFilter);
    }

    @Override
    public void mo964a(C1558e c1558e) {
        invalidateSelf();
    }

    @Override
    public void mo965b(C1558e c1558e) {

    }

    @Override
    public void mo966c(C1558e c1558e) {

    }

    @Override
    public void mo967d(C1558e c1558e) {

    }
}