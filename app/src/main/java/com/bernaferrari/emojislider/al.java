package com.bernaferrari.emojislider;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.ViewTreeObserver.OnPreDrawListener;

public class al extends Drawable {
    public final TextPaint f22462a;
    private final Rect f22469h = new Rect();
    public Spannable f22463b;
    public StaticLayout f22464c;
    public Alignment f22465d = Alignment.ALIGN_CENTER;
    public int f22466e;
    public float f22467f;
    public float f22468g;
    private Bitmap f22470i;
    private CharSequence f22471j;
    private int f22472k;
    private int f22473l;
    private int f22474m;
    private int f22475n;
    private int f22476o;
    private int f22477p;
    private float f22478q = 0.0f;
    private float f22479r = 1.0f;
    private boolean f22480s;

    public al(Context context, int i) {
        this.f22466e = i;
        this.f22462a = new TextPaint();
        this.f22462a.density = context.getResources().getDisplayMetrics().density;
        this.f22462a.setAntiAlias(true);
        this.f22462a.setDither(true);
        this.f22462a.setFilterBitmap(true);
        this.f22462a.setColor(-1);
    }

    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final void m10638a() {
        if (this.f22463b != null) {
            CharSequence charSequence = this.f22463b;
            int i = 0;
            if (this.f22477p > 0) {
                charSequence = "RAWRR";//C3282b.m7274a((CharSequence) "", this.f22463b, this.f22471j, this.f22477p, new C3288h(this.f22462a, this.f22466e, this.f22478q, this.f22479r, false), ((Boolean) C0542g.jz.m1705a(null)).booleanValue());
                if (!charSequence.equals(this.f22463b)) {
                    charSequence = TextUtils.concat(charSequence, this.f22471j);
                }
            }
            this.f22464c = new StaticLayout(charSequence, this.f22462a, this.f22466e, this.f22465d, this.f22479r, this.f22478q, false);
            if (m10637g()) {
                this.f22462a.getTextBounds(this.f22463b.toString(), 0, this.f22463b.length(), this.f22469h);
                i = Math.max(0, this.f22469h.height() - this.f22464c.getLineBottom(0));
            }
            this.f22476o = i;
            this.f22474m = C5724p.m10696a(this.f22464c) + Math.round(this.f22467f * 2.0f);
            this.f22475n = (C5724p.m10697b(this.f22464c) + Math.round(this.f22468g * 2.0f)) + this.f22476o;
            m10648b();
        }
    }

    public final void m10639a(float f) {
        this.f22462a.setTextSize(f);
        m10638a();
        invalidateSelf();
    }

    public final void m10640a(float f, float f2) {
        this.f22478q = f;
        this.f22479r = f2;
        m10638a();
        invalidateSelf();
    }

    public final void m10641a(float f, float f2, int i) {
        this.f22462a.setShadowLayer(f, 0.0f, f2, i);
        m10638a();
        invalidateSelf();
    }

    public final void m10642a(int i) {
        this.f22462a.setColor(i);
        m10638a();
        invalidateSelf();
    }

    public final void m10643a(int i, CharSequence charSequence) {
        this.f22477p = i;
        this.f22471j = charSequence;
        m10638a();
        invalidateSelf();
    }

    private void m10636a(Canvas canvas) {
        if (!TextUtils.isEmpty(this.f22463b)) {
            Spanned spanned = this.f22463b;
            int i = 0;
            OnPreDrawListener[] onPreDrawListenerArr = spanned.getSpans(0, spanned.length(), OnPreDrawListener.class);
            while (i < onPreDrawListenerArr.length) {
                onPreDrawListenerArr[i].onPreDraw();
                i++;
            }
        }
        canvas.translate(this.f22467f, this.f22468g + ((float) this.f22476o));
        if (this.f22465d != Alignment.ALIGN_NORMAL) {
            int c = C5724p.m10698c(this.f22464c);
            canvas.save();
            canvas.translate((float) (-c), 0.0f);
            this.f22464c.draw(canvas);
            canvas.restore();
            return;
        }
        this.f22464c.draw(canvas);
    }

    public final void m10644a(Typeface typeface) {
        this.f22462a.setTypeface(typeface);
        m10638a();
        invalidateSelf();
    }

    public final void m10645a(Typeface typeface, int i) {
        float f = 0.0f;
        boolean z = false;
        if (i > 0) {
            Typeface defaultFromStyle;
            if (typeface == null) {
                defaultFromStyle = Typeface.defaultFromStyle(i);
            } else {
                defaultFromStyle = Typeface.create(typeface, i);
            }
            this.f22462a.setTypeface(defaultFromStyle);
            int style = ((defaultFromStyle != null ? defaultFromStyle.getStyle() : 0) ^ -1) & i;
            TextPaint textPaint = this.f22462a;
            if ((style & 1) != 0) {
                z = true;
            }
            textPaint.setFakeBoldText(z);
            textPaint = this.f22462a;
            if ((style & 2) != 0) {
                f = -0.25f;
            }
            textPaint.setTextSkewX(f);
        } else {
            this.f22462a.setFakeBoldText(false);
            this.f22462a.setTextSkewX(0.0f);
            this.f22462a.setTypeface(typeface);
        }
        m10638a();
        invalidateSelf();
    }

    public final void m10646a(Alignment alignment) {
        if (this.f22465d != alignment) {
            this.f22465d = alignment;
            m10638a();
            invalidateSelf();
        }
    }

    public final void m10647a(Spannable spannable) {
        if (this.f22463b == null || !this.f22463b.equals(spannable)) {
            this.f22463b = spannable;
            m10638a();
            invalidateSelf();
        }
    }

    public final void m10648b() {
        if (this.f22470i != null) {
            this.f22470i.recycle();
            this.f22470i = null;
        }
        if (this.f22480s && this.f22474m > 0 && this.f22475n > 0 && m10637g()) {
            int i = 0;
            if (VERSION.SDK_INT >= 21) {
                i = Math.round((((float) this.f22462a.getFontMetricsInt(null)) * (this.f22479r - 1.0f)) + this.f22478q);
            }
            this.f22470i = Bitmap.createBitmap(this.f22474m, this.f22475n + i, Config.ARGB_8888);
            m10636a(new Canvas(this.f22470i));
        }
    }

    public final void m10649b(float f, float f2) {
        this.f22467f = f;
        this.f22468g = f2;
        m10638a();
        invalidateSelf();
    }

    @TargetApi(21)
    public final void m10650c() {
        this.f22462a.setLetterSpacing(-0.03f);
        m10638a();
        invalidateSelf();
    }

    public final void m10651d() {
        this.f22462a.clearShadowLayer();
        m10638a();
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate((float) this.f22472k, (float) this.f22473l);
        if (this.f22470i != null) {
            canvas.drawBitmap(this.f22470i, 0.0f, 0.0f, this.f22462a);
        } else {
            m10636a(canvas);
        }
        canvas.restore();
    }

    public final void m10652e() {
        if (!this.f22480s) {
            this.f22480s = true;
            m10648b();
            invalidateSelf();
        }
    }

    private boolean m10637g() {
        return false;
//        return !TextUtils.isEmpty(this.f22463b) ? ab.m7419b().matcher(this.f22463b).find() : false;
    }

    public int getIntrinsicHeight() {
        return this.f22475n;
    }

    public int getIntrinsicWidth() {
        return this.f22474m;
    }

    protected void onBoundsChange(Rect rect) {
        this.f22472k = rect.left;
        this.f22473l = rect.top;
    }

    public void setAlpha(int i) {
        this.f22462a.setAlpha(i);
        m10638a();
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.f22462a.setColorFilter(colorFilter);
        m10638a();
        invalidateSelf();
    }
}
