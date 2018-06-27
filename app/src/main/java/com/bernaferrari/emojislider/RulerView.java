package com.bernaferrari.emojislider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
//import com.bernaferrari.emojislider.R;

public class RulerView extends View {
    private final Paint f14757a;
    private final Paint f14758b;
    //    private final int f14759c;
    public ak f14760d;
    public int f14763g;
    public int f14765i;
    public float f14767k;
    public float f14768l;
    public float f14769m;
    public int f14770n;
    private Rect f14761e;
    private int f14762f;
    private int f14764h;
    private boolean f14766j;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RulerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f14757a = new Paint();
        this.f14758b = new Paint();
//        this.f14759c = ContextCompat.getColor(getContext().getTheme(), (int) R.attr.creationTextColor);
        Resources resources = getResources();
        this.f14763g = Math.round(20);         // TODO DP TO PIXELS
        this.f14764h = 2;
        this.f14770n = 5;
//        this.f14758b.setColor(this.f14759c);
//        this.f14758b.setTextSize((float) resources.getDimensionPixelSize(R.dimen.trim_ruler_text_size));
        this.f14758b.setTextAlign(Align.CENTER);
        this.f14758b.setAntiAlias(true);
        this.f14761e = new Rect();
//        if (this.f14764h % 2 != getResources().getDimensionPixelSize(R.dimen.sliderview_pointer_width) % 2) {
//            this.f14764h++;
//        }
    }

    private void m7791a(Canvas canvas, float f, float f2, int i) {
        if (this.f14760d != null) {
            String a = this.f14760d.m17404a(i);
            if (a != null) {
                this.f14758b.getTextBounds(a, 0, a.length(), this.f14761e);
                canvas.drawText(a, f, f2 + ((float) this.f14761e.centerY()), this.f14758b);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        float height = (float) getHeight();
        float f = height * this.f14767k;
        height *= (1.0f - this.f14767k) - this.f14768l;
        float f2 = f + ((1.0f - this.f14769m) * height);
        float f3 = height * this.f14769m;

        // GRADIENT COLOR
//        for (int i = 0; i <= r4.f14762f; i++) {
//            float f4 = (float) ((r4.f14763g * i) + r4.f14765i);
//            Canvas canvas2 = canvas;
//            if (i % r4.f14770n == 0) {
//                r4.f14757a.setColor(r4.f14759c);
//                canvas2.drawRect(f4 - (((float) r4.f14764h) / 2.0f), f, f4 + (((float) r4.f14764h) / 2.0f), f + height, r4.f14757a);
//                m7791a(canvas2, f4, f, i);
//            } else {
//                r4.f14757a.setColor(r4.f14759c);
//                canvas2.drawRect(f4, f2, f4 + 1.0f, f2 + f3, r4.f14757a);
//                m7791a(canvas2, f4, f2, i);
//            }
//        }
    }

    public void onMeasure(int i, int i2) {
        if (!this.f14766j) {
            this.f14762f = (((MeasureSpec.getSize(i) - this.f14765i) + this.f14763g) - 1) / this.f14763g;
        }
        setMeasuredDimension((this.f14762f * this.f14763g) + (2 * this.f14765i), MeasureSpec.getSize(i2));
    }

    public void setIncrementWidthPx(int i) {
        this.f14763g = i;
    }

    public void setLeftRightMarginPx(int i) {
        this.f14765i = i;
    }

    public void setLeftRightMarginRatio(float f) {
        this.f14765i = Math.round(f * ((float) getResources().getDisplayMetrics().widthPixels));
    }

    public void setLineLabeler(ak akVar) {
        this.f14760d = akVar;
    }

    public void setNumIncrements(int i) {
        this.f14762f = i;
        this.f14766j = true;
    }

    public void setPaddingBottomRatio(float f) {
        this.f14768l = f;
    }

    public void setPaddingTopRatio(float f) {
        this.f14767k = f;
    }

    public void setSmallLineRatio(float f) {
        this.f14769m = f;
    }

    public void setSmallToLargeLineFrequency(int i) {
        this.f14770n = i;
    }
}
