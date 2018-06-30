package com.bernaferrari.emojislider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class C7850j extends C5140a implements Callback, OnTouchListener {
    public final int paddingHorizontal;
    public final C7848b handleSize;
    private final int paddingTopWithQuestion;
    private final int paddingTopWithoutQuestion;
    private final int paddingBottomWithQuestion;
    private final int paddingBottomWithoutQuestion;
    private final int questionMargin;
    private final int shadowSize;
    private final int stickerWidth;
    private final Drawable sliderStickerBackground;
    private final Drawable stickerBackground;
    private final C5189h f32882p;
    public boolean f32869c;
    public int f32870d;
    public C5179d f32871e;
    public C5181f f32872f = new C5181f(0);

    public C7850j(Context context) {
        Resources resources = context.getResources();
        this.paddingTopWithQuestion = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_top_with_question);
        this.paddingTopWithoutQuestion = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_top_without_question);
        this.paddingBottomWithQuestion = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_bottom_with_question);
        this.paddingBottomWithoutQuestion = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_bottom_without_question);
        this.paddingHorizontal = resources.getDimensionPixelSize(R.dimen.slider_sticker_padding_horizontal);
        this.questionMargin = resources.getDimensionPixelSize(R.dimen.slider_sticker_question_margin);
        this.shadowSize = resources.getDimensionPixelSize(R.dimen.slider_sticker_shadow_size);
        this.stickerWidth = resources.getDimensionPixelSize(R.dimen.slider_sticker_width);
        this.sliderStickerBackground = ContextCompat.getDrawable(context, R.drawable.slider_sticker_background);
        this.sliderStickerBackground.setCallback(this);
        this.stickerBackground = ContextCompat.getDrawable(context, R.drawable.slider_sticker_background);//question_background_shadow);
        this.stickerBackground.setCallback(this);
        this.f32882p = new C5189h(context);
        this.f32882p.setCallback(this);
        C5189h c5189h = this.f32882p;
        c5189h.f20900a.setColor(ContextCompat.getColor(context, R.color.slider_sticker_question_text));
        c5189h.invalidateSelf();
        this.handleSize = new C7848b(context);
        this.handleSize.setCallback(this);
        this.handleSize.m18483a(resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_handle_size));
        C7848b c7848b = this.handleSize;
        c7848b.sliderHeight = resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_height);
        c7848b.invalidateSelf();
        this.handleSize.m18489b(resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_track_height));
        c7848b = this.handleSize;
        int voteAverageHandleSize = resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_vote_average_handle_size);
        CircleHandle_C5190i circleHandleC5190I = c7848b.circleHandle_f32835b;
        circleHandleC5190I.radius_f20901a = ((float) voteAverageHandleSize) / 2.0f;
        circleHandleC5190I.invalidateSelf();
    }

    public final boolean mo1856a() {
        return true;
    }

    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
    }

    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
    }

    public final int m18502b() {
        return m18500d() ? this.paddingBottomWithQuestion : this.paddingBottomWithoutQuestion;
    }

    public final void m18503c() {
        int color;
        String str;
        int i2;
        CharSequence charSequence;
        C5189h c5189h;
        C7848b c7848b;
        CircleHandle_C5190i circleHandleC5190I;
        int somecolor3;
        C7848b c7848b2;
        float f;
        C7848b c7848b3;
        float f2;
        if (this.f32871e == null) {
            color = -1;
        } else {
            str = this.f32871e.backgroundColorf20864e;
            if (str == null) {
                color = 0;
            } else {
                color = Color.parseColor(str);
            }
        }
        if (this.f32871e == null) {
            i2 = -16777216;
        } else {
            str = this.f32871e.f20868i;
            if (str == null) {
                i2 = 0;
            } else {
                i2 = Color.parseColor(str);
            }
        }
        this.sliderStickerBackground.mutate().setColorFilter(new PorterDuffColorFilter(color, Mode.SRC));
        C5189h c5189h2 = this.f32882p;
        if (this.f32871e != null) {
            if (this.f32871e.f20867h != null) {
                charSequence = this.f32871e.f20867h;
//                c5189h2.f20900a.m10647a(new SpannableString(charSequence));
                c5189h2.invalidateSelf();
                c5189h = this.f32882p;
                c5189h.f20900a.setColor(i2);
                c5189h.invalidateSelf();
                c7848b = this.handleSize;
                circleHandleC5190I = c7848b.circleHandle_f32835b;
                circleHandleC5190I.color_f20902b = color;
                circleHandleC5190I.invalidateSelf();
                if (color != -1) {
                    somecolor3 = c7848b.gradientBackground;
                } else {
                    somecolor3 = C3395a.m7509b(color);
                }
                c7848b.somecolor = somecolor3;
                c7848b.f32836c.setColor(c7848b.somecolor);
                if (i2 != -1) {
                    somecolor3 = i2;
                } else {
                    somecolor3 = c7848b.gradientStart;
                }
                c7848b.f32845l = somecolor3;
                if (i2 == -1) {
                    i2 = c7848b.gradientEnd;
                }
                c7848b.f32846m = i2;
                c7848b.m18484a(c7848b.getBounds());
                c7848b.invalidateSelf();
                if (this.f32872f == null) {
                    c7848b2 = this.handleSize;
//                    am amVar = this.f32872f.f20870b;
                    C7849d c7849d = c7848b2.f32834a;
                    C7852l c7852l = c7849d.f32862c;
//                    C3139c b = ab.f13242h.m6994b(amVar.f36630d);
//                    b.f13336b = new WeakReference(c7852l);
//                    ab.f13242h.m6992a(b.m7019a());
                    c7849d.invalidateSelf();
                    this.handleSize.m18491b(C5186e.USER);
                    c7848b = this.handleSize;
                    if (this.f32871e != null) {
                        if (this.f32871e.f20861b == -1) {
                            if (this.f32871e.m9938a()) {
                                i2 = this.f32871e.f20861b;
                                f = ((((float) i2) * this.f32871e.f20863d) + this.f32872f.f20869a) / ((float) (i2 + 1));
                            } else {
                                f = this.f32871e.f20863d;
                            }
                            c7848b.f32843j = !c7848b.f32842i;
                            c7848b.f32842i = true;
                            c7848b.f32848o = f;
                            if (c7848b.f32843j) {
//                                c7848b.f32853t.m4030b(1.0d);
                            }
                            c7848b.invalidateSelf();
                        }
                    }
                    f = f32872f.f20869a;
                    c7848b.f32843j = !c7848b.f32842i;
                    c7848b.f32842i = true;
                    c7848b.f32848o = f;
                    if (c7848b.f32843j) {
//                        c7848b.f32853t.m4030b(1.0d);
                    }
                    c7848b.invalidateSelf();
                } else {
                    c7848b2 = this.handleSize;
                    if (this.f32871e != null) {
                        if (this.f32871e.f20865f == null) {
                            str = f32871e.f20865f;
                            c7848b2.m18487a(str);
                            this.handleSize.m18486a(C5186e.EMOJI);
                            c7848b3 = this.handleSize;
                            c7848b3.f32843j = false;
                            c7848b3.f32842i = false;
                            c7848b3.invalidateSelf();
                        }
                    }
                    str = "😍";
                    c7848b2.m18487a(str);
                    this.handleSize.m18486a(C5186e.EMOJI);
                    c7848b3 = this.handleSize;
                    c7848b3.f32843j = false;
                    c7848b3.f32842i = false;
                    c7848b3.invalidateSelf();
                }
                if (this.f32872f == null) {
                    c7848b2 = this.handleSize;
                    f2 = this.f32872f.f20869a;
                } else {
                    c7848b2 = this.handleSize;
                    if (this.f32871e != null) {
                        if (!this.f32871e.m9938a()) {
                            f2 = this.f32871e.f20862c;
                        }
                    }
                    f2 = 0.1f;
                }
                c7848b2.m18488b(f2);
                invalidateSelf();
            }
        }
        charSequence = "";
//        c5189h2.f20900a.m10647a(new SpannableString(charSequence));
        c5189h2.invalidateSelf();
        c5189h = this.f32882p;
        c5189h.f20900a.setColor(i2);
        c5189h.invalidateSelf();
        c7848b = this.handleSize;
        circleHandleC5190I = c7848b.circleHandle_f32835b;
        circleHandleC5190I.color_f20902b = color;
        circleHandleC5190I.invalidateSelf();
        if (color != -1) {
            somecolor3 = C3395a.m7509b(color);
        } else {
            somecolor3 = c7848b.gradientBackground;
        }
        c7848b.somecolor = somecolor3;
        c7848b.f32836c.setColor(c7848b.somecolor);
        if (i2 != -1) {
            somecolor3 = c7848b.gradientStart;
        } else {
            somecolor3 = i2;
        }
        c7848b.f32845l = somecolor3;
        if (i2 == -1) {
            i2 = c7848b.gradientEnd;
        }
        c7848b.f32846m = i2;
        c7848b.m18484a(c7848b.getBounds());
        c7848b.invalidateSelf();
        if (this.f32872f == null) {
            c7848b2 = this.handleSize;
            if (this.f32871e != null) {
                if (this.f32871e.f20865f == null) {
                    str = this.f32871e.f20865f;
                    c7848b2.m18487a(str);
                    this.handleSize.m18486a(C5186e.EMOJI);
                    c7848b3 = this.handleSize;
                    c7848b3.f32843j = false;
                    c7848b3.f32842i = false;
                    c7848b3.invalidateSelf();
                }
            }
            str = "😍";
            c7848b2.m18487a(str);
            this.handleSize.m18486a(C5186e.EMOJI);
            c7848b3 = this.handleSize;
            c7848b3.f32843j = false;
            c7848b3.f32842i = false;
            c7848b3.invalidateSelf();
        } else {
            c7848b2 = this.handleSize;
//            am amVar2 = this.f32872f.f20870b;
            C7849d c7849d2 = c7848b2.f32834a;
            C7852l c7852l2 = c7849d2.f32862c;
//            C3139c b2 = ab.f13242h.m6994b(amVar2.f36630d);
//            b2.f13336b = new WeakReference(c7852l2);
//            ab.f13242h.m6992a(b2.m7019a());
            c7849d2.invalidateSelf();
            this.handleSize.m18491b(C5186e.USER);
            c7848b = this.handleSize;
            if (this.f32871e != null) {
                if (this.f32871e.f20861b == -1) {
                    if (this.f32871e.m9938a()) {
                        i2 = this.f32871e.f20861b;
                        f = ((((float) i2) * this.f32871e.f20863d) + this.f32872f.f20869a) / ((float) (i2 + 1));
                    } else {
                        f = this.f32871e.f20863d;
                    }
                    c7848b.f32843j = !c7848b.f32842i;
                    c7848b.f32842i = true;
                    c7848b.f32848o = f;
                    if (c7848b.f32843j) {
//                        c7848b.f32853t.m4030b(1.0d);
                    }
                    c7848b.invalidateSelf();
                }
            }
            f = this.f32872f.f20869a;
            c7848b.f32843j = !c7848b.f32842i;
            c7848b.f32842i = true;
            c7848b.f32848o = f;
            if (c7848b.f32843j) {
//                c7848b.f32853t.m4030b(1.0d);
            }
            c7848b.invalidateSelf();
        }
        if (this.f32872f == null) {
            c7848b2 = this.handleSize;
            if (this.f32871e != null) {
                if (!this.f32871e.m9938a()) {
                    f2 = this.f32871e.f20862c;
                }
            }
            f2 = 0.1f;
        } else {
            c7848b2 = this.handleSize;
            f2 = this.f32872f.f20869a;
        }
        c7848b2.m18488b(f2);
        invalidateSelf();
    }

    private boolean m18500d() {
        return this.f32871e != null && !TextUtils.isEmpty(this.f32871e.f20867h);
    }

    public void draw(Canvas canvas) {
        if (this.f32869c) {
            this.stickerBackground.draw(canvas);
        }
        this.sliderStickerBackground.draw(canvas);
        this.handleSize.draw(canvas);
        if (m18500d()) {
            this.f32882p.draw(canvas);
        }
    }

    public int getIntrinsicHeight() {
        if (this.f32870d > 0) {
            return this.f32870d;
        }
        int intrinsicHeight = this.handleSize.getIntrinsicHeight();
        if (!m18500d()) {
            return (this.paddingTopWithoutQuestion + intrinsicHeight) + this.paddingBottomWithoutQuestion;
        }
        return (((this.paddingTopWithQuestion + this.f32882p.getIntrinsicHeight()) + this.questionMargin) + intrinsicHeight) + this.paddingBottomWithQuestion;
    }

    public int getIntrinsicWidth() {
        return this.stickerWidth;
    }

    public void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.handleSize.onTouch(view, motionEvent);
    }

    public void setAlpha(int i) {
        this.sliderStickerBackground.setAlpha(i);
        this.handleSize.setAlpha(i);
        this.f32882p.setAlpha(i);
    }

    public void setBounds(int i, int i2, int i3, int i4) {
//        super.setBounds(i, i2, i3, i4);
        int i5 = (i + i3) / 2;
        int i6 = (i2 + i4) / 2;
        int intrinsicHeight = getIntrinsicHeight();
        int i7 = intrinsicHeight / 2;
        int i8 = i6 - i7;
        i6 += i7;
        this.sliderStickerBackground.setBounds(i, i8, i3, i6);
        this.stickerBackground.setBounds(i - this.shadowSize, i8 - this.shadowSize, this.shadowSize + i3, this.shadowSize + i6);
        this.handleSize.setBounds(i + this.paddingHorizontal, (i6 - this.handleSize.getIntrinsicHeight()) - m18502b(), i3 - this.paddingHorizontal, i6 - m18502b());
        if (m18500d()) {
            this.f32882p.setBounds(i5 - (this.f32882p.getIntrinsicWidth() / 2), this.paddingTopWithQuestion + i8, i5 + (this.f32882p.getIntrinsicWidth() / 2), (i8 + this.paddingTopWithQuestion) + ((((intrinsicHeight - this.paddingBottomWithQuestion) - this.questionMargin) - this.handleSize.getIntrinsicHeight()) - this.paddingBottomWithQuestion));
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.sliderStickerBackground.setColorFilter(colorFilter);
        this.handleSize.setColorFilter(colorFilter);
        this.f32882p.setColorFilter(colorFilter);
    }
}
