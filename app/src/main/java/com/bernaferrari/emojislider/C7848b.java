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
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.bernaferrari.emojislider.p228m.C1555a;
import com.bernaferrari.emojislider.p228m.C1558e;
import com.bernaferrari.emojislider.p228m.C1559f;
import com.bernaferrari.emojislider.p228m.C1561h;
import com.bernaferrari.emojislider.p228m.C6724v;

public final class C7848b extends Drawable implements Callback, OnTouchListener, C1561h {
    public final C7849d f32834a;
    public final CircleHandle_C5190i circleHandle_f32835b;
    public final C1558e f32853t;
    final Paint f32836c = new Paint(1);
    private final C1558e f32851r;
    private final C1558e f32852s;
    private final Paint f32854u = new Paint(1);
    private final RectF f32855v = new RectF();
    public boolean f32840g;
    public boolean f32841h;
    public boolean f32842i;
    public boolean f32843j;
    public float f32847n;
    public float f32848o;
    public cj f32850q;
    int gradientBackground;
    int gradientStart;
    int gradientEnd;
    int sliderHeight;
    int somecolor;
    int f32845l;
    int f32846m;
    private boolean f32856w;
    private boolean f32857x;
    private float f32858y;
    private float f32859z;

    public C7848b(Context context) {

        // Add a spring to the system.
        C1555a c = C6724v.m13495c();
        this.f32834a = new C7849d(context);
        this.f32834a.setCallback(this);
        this.f32851r = c.m4019a().m4027a(C1559f.m4038b(10.0d, 20.0d)).m4025a(1.0d).m4028a(this);
        this.circleHandle_f32835b = new CircleHandle_C5190i(context);
        this.circleHandle_f32835b.setCallback(this);
        this.f32852s = c.m4019a().m4027a(C1559f.m4038b(10.0d, 20.0d)).m4025a(1.0d).m4028a(this);
        this.f32853t = c.m4019a().m4028a(this).m4025a(0.0d).m4027a(C1559f.m4037a(40.0d, 7.0d));
        this.gradientBackground = ContextCompat.getColor(context, R.color.slider_gradient_background);
        this.gradientStart = ContextCompat.getColor(context, R.color.slider_gradient_start);
        this.gradientEnd = ContextCompat.getColor(context, R.color.slider_gradient_end);
        this.f32836c.setColor(this.gradientBackground);
    }

    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
    }

    public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
    }

    public final void m18483a(int i) {
        C7849d c7849d = this.f32834a;
        c7849d.f32864e = (float) i;
//        c7849d.f32860a.m10639a(c7849d.f32864e);
        CircleHandle_C5190i circleHandleC5190I = c7849d.f32861b;
        circleHandleC5190I.radius_f20901a = c7849d.f32864e / 2.0f;
        circleHandleC5190I.invalidateSelf();
        C7852l c7852l = c7849d.f32862c;
        c7852l.f32890a = c7849d.f32864e;
        c7852l.invalidateSelf();
        c7849d.invalidateSelf();
    }

    final void m18484a(Rect rect) {
        this.f32854u.setShader(new LinearGradient(0.0f, rect.exactCenterY(), (float) rect.width(), rect.exactCenterY(), this.f32845l, this.f32846m, TileMode.CLAMP));
    }

    public final void mo964a() {
        invalidateSelf();
    }

    public final void m18486a(C5186e c5186e) {
        this.f32834a.m18496a(c5186e);
    }

    public final void m18487a(String str) {
        C7849d c7849d = this.f32834a;
//        c7849d.f32860a.m10647a(new SpannableString(str));
        c7849d.invalidateSelf();
    }

    public final void m18488b(float f) {
        this.f32847n = f;
        C7849d c7849d = this.f32834a;
        int a = C3395a.m7508a(this.f32845l, this.f32846m, this.f32847n);
        CircleHandle_C5190i circleHandleC5190I = c7849d.f32861b;
        circleHandleC5190I.color_f20903c = a;
        circleHandleC5190I.invalidateSelf();
        c7849d.invalidateSelf();
        invalidateSelf();
    }

    public final void m18489b(int i) {
        float f = (float) i;
        this.f32858y = f / 2.0f;
        this.f32859z = f;
        invalidateSelf();
    }

    public final void m18491b(C5186e c5186e) {
        C7849d c7849d = this.f32834a;
        if (c7849d.f32865f == null) {
            c7849d.m18496a(c5186e);
            return;
        }
        if (c7849d.f32865f != c5186e) {
            c7849d.f32866g = c7849d.f32865f;
            c7849d.f32865f = c5186e;
//            c7849d.f328163d.m4026a(0.0d, true).m4030b(1.0d);
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
        this.f32855v.set(0.0f, height2 - (this.f32859z / 2.0f), (float) bounds3.width(), height2 + (this.f32859z / 2.0f));
        canvas.drawRoundRect(this.f32855v, this.f32858y, this.f32858y, this.f32836c);
        bounds2 = getBounds();
        float intrinsicWidth2 = (float) this.f32834a.getIntrinsicWidth();
        height2 = ((float) bounds2.height()) / 2.0f;
        if (this.f32841h) {
            width = (this.f32847n * (((float) bounds2.width()) - intrinsicWidth2)) + (intrinsicWidth2 / 2.0f);
        } else {
            width = this.f32847n * ((float) bounds2.width());
        }
        this.f32855v.set(0.0f, height2 - (this.f32859z / 2.0f), width, height2 + (this.f32859z / 2.0f));
        canvas.drawRoundRect(this.f32855v, this.f32858y, this.f32858y, this.f32854u);
        if (this.f32842i) {
            int a = C3395a.m7508a(this.f32845l, this.f32846m, this.f32848o);
            CircleHandle_C5190i circleHandleC5190I = this.circleHandle_f32835b;
            circleHandleC5190I.color_f20903c = a;
            circleHandleC5190I.invalidateSelf();
//            if (this.f32843j) {
//                sxsy = (float) this.f32853t.f6940d.f6934a;
//            } else {
//                sxsy = (float) this.f32852s.f6940d.f6934a;
//            }
            bounds = getBounds();
            intrinsicWidth = (float) this.circleHandle_f32835b.getIntrinsicWidth();
            intrinsicHeight = (float) this.circleHandle_f32835b.getIntrinsicHeight();
            if (this.f32841h) {
                intrinsicWidth2 = (this.f32848o * (((float) bounds.width()) - intrinsicWidth)) + (intrinsicWidth / 2.0f);
            } else {
                intrinsicWidth2 = this.f32848o * ((float) bounds.width());
            }
            height = (float) (bounds.height() / 2);
            canvas.save();
//            canvas.scale(sxsy, sxsy, intrinsicWidth2, height);
            intrinsicWidth /= 2.0f;
            intrinsicHeight /= 2.0f;
            this.circleHandle_f32835b.setBounds((int) (intrinsicWidth2 - intrinsicWidth), (int) (height - intrinsicHeight), (int) (intrinsicWidth2 + intrinsicWidth), (int) (height + intrinsicHeight));
            this.circleHandle_f32835b.draw(canvas);
            canvas.restore();
        }
        bounds = getBounds();
        intrinsicWidth = (float) this.f32834a.getIntrinsicWidth();
        intrinsicHeight = (float) this.f32834a.getIntrinsicHeight();
//        sxsy = (float) this.f32851r.f6940d.f6934a;
        if (this.f32841h) {
            intrinsicWidth2 = (this.f32847n * (((float) bounds.width()) - intrinsicWidth)) + (intrinsicWidth / 2.0f);
        } else {
            intrinsicWidth2 = this.f32847n * ((float) bounds.width());
        }
        height = (float) (bounds.height() / 2);
        canvas.save();
//        canvas.scale(sxsy, sxsy, intrinsicWidth2, height);
        intrinsicWidth /= 2.0f;
        intrinsicHeight /= 2.0f;
        this.f32834a.setBounds((int) (intrinsicWidth2 - intrinsicWidth), (int) (height - intrinsicHeight), (int) (intrinsicWidth2 + intrinsicWidth), (int) (height + intrinsicHeight));
        this.f32834a.draw(canvas);
        canvas.restore();
        canvas.restore();
    }

    public final int getIntrinsicHeight() {
        return this.sliderHeight > 0 ? this.sliderHeight : this.f32834a.getIntrinsicHeight();
    }

    public final void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    protected final void onBoundsChange(Rect rect) {
        m18484a(rect);
    }

    @Override
    public final boolean onTouch(View view, MotionEvent motionEvent) {
        Rect bounds = getBounds();
        int x = ((int) motionEvent.getX()) - bounds.left;
        int y = ((int) motionEvent.getY()) - bounds.top;

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                this.f32856w = this.f32834a.getBounds().contains(x, y);
                if (this.f32856w) {
                    this.f32851r.m4030b(0.8999999761581421d);
                }
                this.f32857x = this.circleHandle_f32835b.getBounds().contains(x, y);
                if (this.f32857x) {
                    this.f32852s.m4030b(0.8999999761581421d);
                }
                if (this.f32850q != null) {
                    float f;
                    if (!this.f32840g || !this.f32856w) {
                        boolean z = this.f32856w;
                        f = this.f32847n;
                        if (z) {
                            break;
                        }
                    }
                    cj cjVar2 = this.f32850q;
                    f = this.f32847n;
                    break;
                }
                break;

            case MotionEvent.ACTION_UP:
                view.performClick();
                break;

            case MotionEvent.ACTION_CANCEL:

                if (this.f32850q != null) {
                    cj cjVar3;
                    if (this.f32840g && this.f32856w) {
                        cjVar3 = this.f32850q;
                        float f2 = this.f32847n;
                        C5181f c5181f = new C5181f(f2);
                    } else {
                        cjVar3 = this.f32850q;
                        boolean z2 = this.f32856w;
                        boolean z3 = this.f32857x;
                        float f3 = this.f32847n;
                    }
                }
                this.f32856w = false;
                this.f32857x = false;
                this.f32851r.m4030b(1.0d);
                this.f32852s.m4030b(1.0d);

                if (this.f32840g && this.f32856w) {
                    m18488b((float) Math.min(Math.max((double) (((float) x) / ((float) bounds.width())), 0.0d), 1.0d));
                    if (f32850q != null) {
//                        cj.d(this.f32850q, this.f32847n);
                        break;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                break;
        }

//        switch (motionEvent.getActionMasked()) {
//            case 0:
//                this.f32856w = this.f32834a.getBounds().contains(x, y);
//                if (this.f32856w) {
//                    this.f32851r.m4030b(0.8999999761581421d);
//                }
//                this.f32857x = this.circleHandle_f32835b.getBounds().contains(x, y);
//                if (this.f32857x) {
//                    this.f32852s.m4030b(0.8999999761581421d);
//                }
//                if (this.f32850q != null) {
//                    float f;
//                    if (!this.f32840g || !this.f32856w) {
//                        cj cjVar = this.f32850q;
//                        boolean z = this.f32856w;
//                        f = this.f32847n;
//                        cjVar.b.J();
//                        if (z) {
//                            cj.d(cjVar, f);
//                            cjVar.f.g.m9942a();
//                            break;
//                        }
//                    }
//                    cj cjVar2 = this.f32850q;
//                    f = this.f32847n;
//                    cjVar2.b.I();
//                    cj.d(cjVar2, f);
//                    cjVar2.f.g.m9942a();
//                    break;
//                }
//                break;
//            case 1:
//            case 3:
//                if (this.f32850q != null) {
//                    cj cjVar3;
//                    if (this.f32840g && this.f32856w) {
//                        cjVar3 = this.f32850q;
        float f2 = this.f32847n;
//                        String str = cjVar3.c.f32285b.f35979j;
//                        String str2 = cjVar3.d.f20866g;
        C5181f c5181f = new C5181f(f2);
//                        cjVar3.b.a(new C5176a(str, str2, c5181f), cjVar3.f);
//                        cjVar3.f.f.handleSize.f32840g = false;
//                        C7850j c7850j = cjVar3.f.f;
//                        c7850j.f32872f = c5181f;
//                        c7850j.m18503c();
//                        cj.d(cjVar3, f2);
//                        cjVar3.f.g.m9945b();
//                    } else {
//                        cjVar3 = this.f32850q;
//                        boolean z2 = this.f32856w;
//                        boolean z3 = this.f32857x;
//                        float f3 = this.f32847n;
//                        cjVar3.b.a(z3, cjVar3.f);
//                        if (z2) {
//                            cj.d(cjVar3, f3);
//                            cjVar3.f.g.m9945b();
//                        }
//                    }
//                }
//                this.f32856w = false;
//                this.f32857x = false;
//                this.f32851r.m4030b(1.0d);
//                this.f32852s.m4030b(1.0d);
//                break;
//            case 2:

//                break;
//            default:
//                break;
//        }
        return true;
    }

    public final void setAlpha(int i) {
        this.f32834a.setAlpha(i);
        this.f32836c.setAlpha(i);
        this.f32854u.setAlpha(i);
    }

    public final void setColorFilter(ColorFilter colorFilter) {
        this.f32834a.setColorFilter(colorFilter);
        this.f32836c.setColorFilter(colorFilter);
        this.f32854u.setColorFilter(colorFilter);
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