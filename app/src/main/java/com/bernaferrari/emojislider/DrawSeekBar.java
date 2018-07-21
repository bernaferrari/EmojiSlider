package com.bernaferrari.emojislider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

public final class DrawSeekBar extends Drawable implements Callback {
    public final C7849d bigCircleThumb_f32834a;
    public final CircleHandle_C5190i averageCircleHandle_f32835b;
    public final Spring f32853t;
    final Paint paint_f32836c = new Paint(1);
    private final Spring f32851r;
    private final Spring f32852s;
    private final Paint paint_f32854u = new Paint(1);
    private final RectF rect_f32855v = new RectF();
    public boolean f32841h;
    public boolean f32842i;
    public boolean f32843j;
    public float percentage_progress_f32847n = (float) 0.90;
    public float percentage_f32848o = (float) 0.50;
    int colorStart;
    int sliderHeight;
    int colorEnd;
    private int gradientBackground;
    private float rectRx_f32858y;
    private float f32859z;

    public DrawSeekBar(Context context) {

        // Add a spring to the system.
        BaseSpringSystem c = SpringSystem.create();

        this.f32851r = c.createSpring()
                .setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(10.0d, 20.0d))
                .setCurrentValue(1)
                .addListener(new SimpleSpringListener() {
                    @Override
                    public void onSpringUpdate(Spring spring) {
                        // Just tell the UI to update based on the springs current state.
                        invalidateSelf();
                    }
                });

        this.f32852s = c.createSpring()
                .setSpringConfig(
                        SpringConfig.fromOrigamiTensionAndFriction(10.0d, 20.0d)
                )
                .setCurrentValue(1)
                .addListener(new SimpleSpringListener() {
                    @Override
                    public void onSpringUpdate(Spring spring) {
                        // Just tell the UI to update based on the springs current state.
                        invalidateSelf();
                    }
                });


        this.bigCircleThumb_f32834a = new C7849d(context);
        this.bigCircleThumb_f32834a.setCallback(this);
        this.averageCircleHandle_f32835b = new CircleHandle_C5190i(context);
        this.averageCircleHandle_f32835b.setCallback(this);

        this.f32853t = c.createSpring()
                .addListener(new SimpleSpringListener() {
                    @Override
                    public void onSpringUpdate(Spring spring) {
                        // Just tell the UI to update based on the springs current state.
                        invalidateSelf();
                    }
                })
                .setCurrentValue(0)
                .setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 7));

//        this.gradientBackground = ContextCompat.getColor(context, R.color.slider_gradient_background);
//        colorStart = ContextCompat.getColor(context, R.color.slider_gradient_start);
//        colorEnd = ContextCompat.getColor(context, R.color.slider_gradient_end);

    }

    public void setGradientBackground(int gradientBackground) {
        this.gradientBackground = gradientBackground;
        this.paint_f32836c.setColor(this.gradientBackground);
    }

    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
    }

    public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
    }

    public final void m18483a(int i) {
        C7849d c7849d = this.bigCircleThumb_f32834a;
        c7849d.f32864e = (float) i;
//        c7849d.f32860a.m10639a(c7849d.f32864e);
        CircleHandle_C5190i circleHandleC5190I = c7849d.imageHandle_f32861b;
        circleHandleC5190I.radius_f20901a = c7849d.f32864e / 2.0f;
        circleHandleC5190I.invalidateSelf();
        C7852l c7852l = c7849d.f32862c;
        c7852l.f32890a = c7849d.f32864e;
        c7852l.invalidateSelf();
        c7849d.invalidateSelf();
    }

    final void m18484a(Rect rect) {
        this.paint_f32854u.setShader(new LinearGradient(0.0f, rect.exactCenterY(), (float) rect.width(), rect.exactCenterY(), this.colorStart, this.colorEnd, TileMode.CLAMP));
    }

    public final void m18486a(C5186e c5186e) {
        this.bigCircleThumb_f32834a.m18496a(c5186e);
    }

    public final void configureEmoji_m18487a(String str) {
        C7849d c7849d = this.bigCircleThumb_f32834a;
//        c7849d.f32860a.m10647a(new SpannableString(str));
        c7849d.invalidateSelf();
    }

    public final void m18488b(float f) {
//        this.percentage_progress_f32847n = f;
        C7849d c7849d = this.bigCircleThumb_f32834a;
        CircleHandle_C5190i circleHandleC5190I = c7849d.imageHandle_f32861b;
        circleHandleC5190I.color_f20903c = C3395a.getCorrectColor_m7508a(this.colorStart, this.colorEnd, this.percentage_progress_f32847n);
        circleHandleC5190I.invalidateSelf();
        c7849d.invalidateSelf();
        invalidateSelf();
    }

    public final void m18489b(int i) {
        float f = (float) i;
        this.rectRx_f32858y = f / 2.0f;
        this.f32859z = f;
        invalidateSelf();
    }

    public final void m18491b(C5186e c5186e) {
        C7849d c7849d = this.bigCircleThumb_f32834a;
        if (c7849d.f32865f == null) {
            c7849d.m18496a(c5186e);
            return;
        }
        if (c7849d.f32865f != c5186e) {
            c7849d.f32866g = c7849d.f32865f;
            c7849d.f32865f = c5186e;
//            c7849d.f328163d.setCurrentValuem4026a(0.0d, true).m4030b(1.0d);
            c7849d.invalidateSelf();
        }
    }

    public final void draw(Canvas canvas) {
        float width;
        float sxsy;
        Rect bounds;
        float intrinsicWidth;
        float intrinsicHeight;
        float height;
        Rect bounds2 = getBounds();
        canvas.save();
        canvas.translate((float) bounds2.left, (float) bounds2.top);
        Rect bounds3 = getBounds();
        float height2 = ((float) bounds3.height()) / 2.0f;
        this.rect_f32855v.set(0.0f, height2 - (this.f32859z / 2.0f), (float) bounds3.width(), height2 + (this.f32859z / 2.0f));

        // draw grey rect (__________)
        canvas.drawRoundRect(this.rect_f32855v, this.rectRx_f32858y, this.rectRx_f32858y, this.paint_f32836c);

        bounds2 = getBounds();
        float intrinsicWidth2 = (float) this.bigCircleThumb_f32834a.getIntrinsicWidth();
        height2 = ((float) bounds2.height()) / 2.0f;
        if (this.f32841h) {
            width = (this.percentage_progress_f32847n * (((float) bounds2.width()) - intrinsicWidth2)) + (intrinsicWidth2 / 2.0f);
        } else {
            width = this.percentage_progress_f32847n * ((float) bounds2.width());
        }
        this.rect_f32855v.set(0.0f, height2 - (this.f32859z / 2.0f), width, height2 + (this.f32859z / 2.0f));
//        this.paint_f32854u.setShader(new LinearGradient(0.0f, bounds2.centerY(), width, bounds2.centerY(), this.colorStart, this.colorEnd, TileMode.CLAMP));

        canvas.drawRoundRect(this.rect_f32855v, this.rectRx_f32858y, this.rectRx_f32858y, this.paint_f32854u);

        if (this.f32842i) {
            int correctColor_a = C3395a.getCorrectColor_m7508a(this.colorStart, this.colorEnd, this.percentage_f32848o);
            CircleHandle_C5190i circleHandleC5190I = this.averageCircleHandle_f32835b;
            circleHandleC5190I.color_f20903c = correctColor_a;
            circleHandleC5190I.invalidateSelf();
            if (this.f32843j) {
                sxsy = (float) this.f32853t.getCurrentValue();
            } else {
                sxsy = (float) this.f32852s.getCurrentValue();
            }
            bounds = getBounds();
            intrinsicWidth = (float) this.averageCircleHandle_f32835b.getIntrinsicWidth();
            intrinsicHeight = (float) this.averageCircleHandle_f32835b.getIntrinsicHeight();
            if (this.f32841h) {
                intrinsicWidth2 = (this.percentage_f32848o * (((float) bounds.width()) - intrinsicWidth)) + (intrinsicWidth / 2.0f);
            } else {
                intrinsicWidth2 = this.percentage_f32848o * ((float) bounds.width());
            }
            height = (float) (bounds.height() / 2);
            canvas.save();
            canvas.scale(sxsy, sxsy, intrinsicWidth2, height);
            intrinsicWidth /= 2.0f;
            intrinsicHeight /= 2.0f;
            this.averageCircleHandle_f32835b.setBounds((int) (intrinsicWidth2 - intrinsicWidth), (int) (height - intrinsicHeight), (int) (intrinsicWidth2 + intrinsicWidth), (int) (height + intrinsicHeight));

            // draw circle ()
            this.averageCircleHandle_f32835b.draw(canvas);

            canvas.restore();
        }
        bounds = getBounds();
        intrinsicWidth = (float) this.bigCircleThumb_f32834a.getIntrinsicWidth();
        intrinsicHeight = (float) this.bigCircleThumb_f32834a.getIntrinsicHeight();
        sxsy = (float) this.f32851r.getCurrentValue();
        if (this.f32841h) {
            intrinsicWidth2 = (this.percentage_progress_f32847n * (((float) bounds.width()) - intrinsicWidth)) + (intrinsicWidth / 2.0f);
        } else {
            intrinsicWidth2 = this.percentage_progress_f32847n * ((float) bounds.width());
        }
        height = (float) (bounds.height() / 2);
        canvas.save();
        canvas.scale(sxsy, sxsy, intrinsicWidth2, height);
        intrinsicWidth /= 2.0f;

        this.bigCircleThumb_f32834a.setBounds((int) (intrinsicWidth2 - intrinsicWidth), (int) (height - intrinsicHeight), (int) (intrinsicWidth2 + intrinsicWidth), (int) (height + intrinsicHeight));

        this.bigCircleThumb_f32834a.draw(canvas);
        canvas.restore();
        canvas.restore();
    }

    public final int getIntrinsicHeight() {
        return this.sliderHeight > 0 ? this.sliderHeight : this.bigCircleThumb_f32834a.getIntrinsicHeight();
    }

    public final void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    protected final void onBoundsChange(Rect rect) {
        m18484a(rect);
    }

    public final void setAlpha(int i) {
        this.bigCircleThumb_f32834a.setAlpha(i);
        this.paint_f32836c.setAlpha(i);
        this.paint_f32854u.setAlpha(i);
    }

    public final void setColorFilter(ColorFilter colorFilter) {
        this.bigCircleThumb_f32834a.setColorFilter(colorFilter);
        this.paint_f32836c.setColorFilter(colorFilter);
        this.paint_f32854u.setColorFilter(colorFilter);
    }
}