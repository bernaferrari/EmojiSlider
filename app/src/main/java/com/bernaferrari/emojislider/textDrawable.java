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

public class textDrawable extends Drawable {
    public final TextPaint text;
    private final Rect f22469h = new Rect();
    public Spannable spannable;
    public StaticLayout staticlayout;
    public Alignment align = Alignment.ALIGN_CENTER;
    public int width;
    public float f22467f;
    public float f22468g;
    private Bitmap bitmap;
    private CharSequence charsequence;
    private int rectLeft;
    private int rectTop;
    private int intrinsicWidth;
    private int intrinsicHeight;
    private int canvaTranslate;
    private int f22477p;
    private float spacingadd = 0.0f;
    private float spacingmult = 1.0f;
    private boolean f22480s;

    public textDrawable(Context context, int width) {
        this.width = width;
        this.text = new TextPaint();
        this.text.density = context.getResources().getDisplayMetrics().density;
        this.text.setAntiAlias(true);
        this.text.setDither(true);
        this.text.setFilterBitmap(true);
        this.text.setColor(-1);
    }

    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final void drawText() {
        if (this.spannable != null) {
            CharSequence charSequence = this.spannable;
            int i = 0;
            if (this.f22477p > 0) {
                charSequence = "RAWRR";//C3282b.m7274a((CharSequence) "", this.spannable, this.charsequence, this.f22477p, new C3288h(this.text, this.width, this.spacingadd, this.spacingmult, false), ((Boolean) C0542g.jz.m1705a(null)).booleanValue());
                if (!charSequence.equals(this.spannable)) {
                    charSequence = TextUtils.concat(charSequence, this.charsequence);
                }
            }
            this.staticlayout = new StaticLayout(charSequence, this.text, this.width, this.align, this.spacingmult, this.spacingadd, false);
            if (m10637g()) {
                this.text.getTextBounds(this.spannable.toString(), 0, this.spannable.length(), this.f22469h);
                i = Math.max(0, this.f22469h.height() - this.staticlayout.getLineBottom(0));
            }
            this.canvaTranslate = i;
            this.intrinsicWidth = C5724p.m10696a(this.staticlayout) + Math.round(this.f22467f * 2.0f);
            this.intrinsicHeight = (C5724p.m10697b(this.staticlayout) + Math.round(this.f22468g * 2.0f)) + this.canvaTranslate;
            m10648b();
        }
    }

    public final void setTextSize(float f) {
        this.text.setTextSize(f);
        drawText();
        invalidateSelf();
    }

    public final void m10640a(float f, float f2) {
        this.spacingadd = f;
        this.spacingmult = f2;
        drawText();
        invalidateSelf();
    }

    public final void setShadowLayer(float f, float f2, int i) {
        this.text.setShadowLayer(f, 0.0f, f2, i);
        drawText();
        invalidateSelf();
    }

    public final void setColor(int i) {
        this.text.setColor(i);
        drawText();
        invalidateSelf();
    }

    public final void m10643a(int i, CharSequence charSequence) {
        this.f22477p = i;
        this.charsequence = charSequence;
        drawText();
        invalidateSelf();
    }

    private void m10636a(Canvas canvas) {
        if (!TextUtils.isEmpty(this.spannable)) {
            Spanned spanned = this.spannable;
            int i = 0;
            OnPreDrawListener[] onPreDrawListenerArr = spanned.getSpans(0, spanned.length(), OnPreDrawListener.class);
            while (i < onPreDrawListenerArr.length) {
                onPreDrawListenerArr[i].onPreDraw();
                i++;
            }
        }
        canvas.translate(this.f22467f, this.f22468g + ((float) this.canvaTranslate));
        if (this.align != Alignment.ALIGN_NORMAL) {
            int c = C5724p.m10698c(this.staticlayout);
            canvas.save();
            canvas.translate((float) (-c), 0.0f);
            this.staticlayout.draw(canvas);
            canvas.restore();
            return;
        }
        this.staticlayout.draw(canvas);
    }

    public final void m10644a(Typeface typeface) {
        this.text.setTypeface(typeface);
        drawText();
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
            this.text.setTypeface(defaultFromStyle);
            int style = ((defaultFromStyle != null ? defaultFromStyle.getStyle() : 0) ^ -1) & i;
            TextPaint textPaint = this.text;
            if ((style & 1) != 0) {
                z = true;
            }
            textPaint.setFakeBoldText(z);
            textPaint = this.text;
            if ((style & 2) != 0) {
                f = -0.25f;
            }
            textPaint.setTextSkewX(f);
        } else {
            this.text.setFakeBoldText(false);
            this.text.setTextSkewX(0.0f);
            this.text.setTypeface(typeface);
        }
        drawText();
        invalidateSelf();
    }

    public final void m10646a(Alignment alignment) {
        if (this.align != alignment) {
            this.align = alignment;
            drawText();
            invalidateSelf();
        }
    }

    public final void setSpannable(Spannable spannable) {
        if (this.spannable == null || !this.spannable.equals(spannable)) {
            this.spannable = spannable;
            drawText();
            invalidateSelf();
        }
    }

    public final void m10648b() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (this.f22480s && this.intrinsicWidth > 0 && this.intrinsicHeight > 0 && m10637g()) {
            int i = 0;
            if (VERSION.SDK_INT >= 21) {
                i = Math.round((((float) this.text.getFontMetricsInt(null)) * (this.spacingmult - 1.0f)) + this.spacingadd);
            }
            bitmap = Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight + i, Config.ARGB_8888);
            m10636a(new Canvas(bitmap));
        }
    }

    public final void m10649b(float f, float f2) {
        this.f22467f = f;
        this.f22468g = f2;
        drawText();
        invalidateSelf();
    }

    @TargetApi(21)
    public final void setLetterSpacing() {
        this.text.setLetterSpacing(-0.03f);
        drawText();
        invalidateSelf();
    }

    public final void m10651d() {
        this.text.clearShadowLayer();
        drawText();
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate((float) this.rectLeft, (float) this.rectTop);
        if (bitmap != null) {
            canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, this.text);
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
//        return !TextUtils.isEmpty(this.spannable) ? ab.m7419b().matcher(this.spannable).find() : false;
    }

    public int getIntrinsicHeight() {
        return this.intrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.intrinsicWidth;
    }

    protected void onBoundsChange(Rect rect) {
        this.rectLeft = rect.left;
        this.rectTop = rect.top;
    }

    public void setAlpha(int i) {
        this.text.setAlpha(i);
        drawText();
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.text.setColorFilter(colorFilter);
        drawText();
        invalidateSelf();
    }
}
