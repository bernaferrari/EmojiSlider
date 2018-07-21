//package com.bernaferrari.emojislider;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.graphics.Canvas;
//import android.graphics.ColorFilter;
//import android.graphics.PixelFormat;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.Drawable.Callback;
//import android.os.Handler;
//import android.os.Looper;
//
//import com.facebook.rebound.BaseSpringSystem;
//import com.facebook.rebound.Spring;
//import com.facebook.rebound.SpringConfig;
//import com.facebook.rebound.SpringListener;
//import com.facebook.rebound.SpringSystem;
//
//public final class C7851k extends Drawable implements Callback, Runnable, SpringListener {
//    public final Handler f32883a = new Handler(Looper.getMainLooper());
//    private final Drawable f32886d;
//    private final DrawSeekBar f32887e;
//    private final int f32888f;
//    private final int f32889g;
//    public boolean f32885c;
//
//    public C7851k(Drawable f32886d, Context context) {
//        this.f32886d = f32886d;
//        Resources resources = context.getResources();
//        this.f32888f = resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_height);
//        this.f32889g = resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_slider_padding);
//
//        BaseSpringSystem c = SpringSystem.create();
//
//        Spring a = c.createSpring()
//                .setSpringConfig(
//                        SpringConfig.fromOrigamiTensionAndFriction(3.0d, 5.0d)
//                )
//                .addListener(this);
//
//        a.setOvershootClampingEnabled(true);
//
////        this.f32886d = C0835a.m2242a(context, (int) R.drawable.slider_sticker_tray_background);
//        this.f32886d.setCallback(this);
//        this.f32887e = new DrawSeekBar(context);
//        this.f32887e.setCallback(this);
//        DrawSeekBar drawSeekBar = this.f32887e;
//        drawSeekBar.setF32841h(true);
//        drawSeekBar.invalidateSelf();
//        this.f32887e.configureEmoji_m18487a("😍");
//        this.f32887e.m18483a(resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_slider_handle_size));
//        this.f32887e.m18486a(C5186e.EMOJI);
//        this.f32887e.configureHeight(resources.getDimensionPixelSize(R.dimen.slider_sticker_tray_track_height));
//    }
//
//    public final int getOpacity() {
//        return PixelFormat.TRANSLUCENT;
//    }
//
//    public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
//    }
//
//    public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
//    }
//
//    public final void draw(Canvas canvas) {
//        this.f32886d.draw(canvas);
//        this.f32887e.draw(canvas);
//    }
//
//    public final void invalidateDrawable(Drawable drawable) {
//        invalidateSelf();
//    }
//
//    public final void run() {
//        double d = 0.0d;
////        Object obj = this.f32884b.f6940d.f6934a == 0.0d ? 1 : null;
////        Spring c1558e = this.f32884b;
////        if (obj != null) {
////            d = 0.5d;
////        }
////        c1558e.m4030b(d);
//    }
//
//    public final void setAlpha(int i) {
//        this.f32886d.setAlpha(i);
//        this.f32887e.setAlpha(i);
//    }
//
//    public final void setBounds(int i, int i2, int i3, int i4) {
//        super.setBounds(i, i2, i3, i4);
//        int i5 = ((i4 - i2) / 2) + i2;
//        this.f32886d.setBounds(i, i5 - (this.f32888f / 2), i3, i5 + (this.f32888f / 2));
//        this.f32887e.setBounds(i + this.f32889g, i2, i3 - this.f32889g, i4);
//    }
//
//    public final void setColorFilter(ColorFilter colorFilter) {
//        this.f32886d.setColorFilter(colorFilter);
//        this.f32887e.setColorFilter(colorFilter);
//    }
//
//    @Override
//    public void onSpringUpdate(Spring spring) {
//        this.f32887e.updateColors((float) spring.getCurrentValue());
//    }
//
//    @Override
//    public void onSpringAtRest(Spring spring) {
//        if (this.f32885c) {
//            f32883a.postDelayed(this, 1750);
//        }
//    }
//
//    @Override
//    public void onSpringActivate(Spring spring) {
//
//    }
//
//    @Override
//    public void onSpringEndStateChange(Spring spring) {
//
//    }
//}
//
//
