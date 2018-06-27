package com.bernaferrari.emojislider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;

import java.util.ArrayList;
import java.util.List;

public final class C5188g extends Drawable implements FrameCallback {
    private final int f20887b;
    private final int f20888c;
    private final int f20889d;
    private final List<C5187f> f20890e = new ArrayList();
    private final List<C5187f> f20891f = new ArrayList();
    private final Rect f20892g = new Rect();
    private final TextPaint f20893h = new TextPaint(1);
    public String emoji = "😍";
    private float f20894i;
    private float f20895j;
    private float f20896k;
    private boolean f20897l;
    private long f20898m;
    private C5187f f20899n;

    public C5188g(Context context) {
        Resources resources = context.getResources();
        this.f20889d = resources.getDimensionPixelSize(R.dimen.slider_particle_system_anchor_offset);
        this.f20887b = resources.getDimensionPixelSize(R.dimen.slider_particle_system_particle_min_size);
        this.f20888c = resources.getDimensionPixelSize(R.dimen.slider_particle_system_particle_max_size);
    }

    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final void m9942a() {
        this.f20899n = new C5187f(this.emoji);
        this.f20899n.f20881b = this.f20894i;
        this.f20899n.f20882c = this.f20895j;
        this.f20899n.f20884e = this.f20896k;
        if (!this.f20897l) {
            this.f20897l = true;
            doFrame(System.currentTimeMillis());
        }
    }

    public final void m9943a(float f) {
        this.f20896k = ((float) this.f20887b) + (f * ((float) (this.f20888c - this.f20887b)));
        if (this.f20899n != null) {
            this.f20899n.f20884e = this.f20896k;
        }
        invalidateSelf();
    }

    public final void m9944a(float f, float f2) {
        this.f20894i = f;
        this.f20895j = f2;
        if (this.f20899n != null) {
            this.f20899n.f20881b = this.f20894i;
            this.f20899n.f20882c = this.f20895j;
        }
        invalidateSelf();
    }

    private void m9941a(Canvas canvas, C5187f c5187f) {
        this.f20893h.setTextSize(c5187f.f20884e);
        this.f20893h.getTextBounds(c5187f.f20880a, 0, c5187f.f20880a.length(), this.f20892g);
        canvas.drawText(c5187f.f20880a, c5187f.f20881b - (((float) this.f20892g.width()) / 2.0f), (c5187f.f20882c + c5187f.f20883d) - (((float) this.f20892g.height()) / 2.0f), this.f20893h);
    }

    public final void m9945b() {
        this.f20890e.add(0, this.f20899n);
        this.f20899n = null;
    }

    public final void doFrame(long j) {
        if (this.f20899n != null) {
            double toRadians = Math.toRadians((double) (System.currentTimeMillis() / 8));
            this.f20899n.f20883d = (float) ((Math.sin(toRadians) * 16.0d) - ((double) this.f20889d));
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (this.f20898m != 0) {
            float f = ((float) (currentTimeMillis - this.f20898m)) / 1000.0f;
            for (int i = 0; i < this.f20890e.size(); i++) {
                C5187f c5187f = this.f20890e.get(i);
                c5187f.f20885f += -1000.0f * f;
                c5187f.f20882c += c5187f.f20885f * f;
                if (c5187f.f20882c < ((float) getBounds().top) - (2.0f * c5187f.f20884e)) {
                    this.f20891f.add(c5187f);
                }
            }
            if (!this.f20891f.isEmpty()) {
                this.f20890e.removeAll(this.f20891f);
                this.f20891f.clear();
            }
        }
        this.f20898m = currentTimeMillis;
        if (this.f20899n == null && this.f20890e.isEmpty()) {
            this.f20897l = false;
        } else {
            Choreographer.getInstance().postFrameCallback(this);
        }
        invalidateSelf();
    }

    public final void draw(Canvas canvas) {
        if (this.f20899n != null) {
            m9941a(canvas, this.f20899n);
        }
        for (int i = 0; i < this.f20890e.size(); i++) {
            m9941a(canvas, this.f20890e.get(i));
        }
    }

    public final void setAlpha(int i) {
        this.f20893h.setAlpha(i);
        invalidateSelf();
    }

    public final void setColorFilter(ColorFilter colorFilter) {
        this.f20893h.setColorFilter(colorFilter);
        invalidateSelf();
    }
}
