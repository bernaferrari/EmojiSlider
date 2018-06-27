package com.bernaferrari.emojislider;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class SliderView extends HorizontalScrollView {
    public C3573b f14771a;
    private float f14772b;
    private boolean f14773c;
    private float f14774d;
    private boolean f14775e = false;
    private boolean f14776f;
    private float f14777g;
    private boolean f14778h;
    private boolean f14779i;
    private int f14780j;
    private boolean f14781k = false;
    private float f14782l = -1.0f;
    private float f14783m = 1.0f;
    private float f14784n = Float.NaN;
    private RulerView f14785o;
    private float f14786p;

    public SliderView(Context context) {
        super(context);
    }

    public SliderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SliderView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void fling(int i) {
    }

    private float m7792a(int i) {
        return ((float) (2 * (((int) (((double) i) + (((double) getWidth()) / 2.0d))) - this.f14780j))) / this.f14772b;
    }

    public final void m7794a(float f, boolean z) {
        if (this.f14781k) {
            m7793b(f / 25.0f, z);
            return;
        }
        this.f14776f = true;
        this.f14777g = f;
        this.f14778h = z;
    }

    private void m7793b(float f, boolean z) {
        int max = (int) (this.f14772b * ((Math.max(this.f14782l, Math.min(this.f14783m, f)) + 1.0f) / 2.0f));
        if (z) {
            smoothScrollTo(max, 0);
        } else {
            scrollTo(max, 0);
        }
    }

    private float getCurrentScrollPercent() {
        return m7792a(getScrollX());
    }

    protected void onFinishInflate() {
//        int a = Logger.m348a(C0713a.f2679c, 44, -1882591759);
        super.onFinishInflate();
        this.f14785o = (RulerView) getChildAt(0);
        this.f14785o.f14767k = 0.05f;
        this.f14785o.f14768l = 0.2f;
        this.f14785o.f14769m = 0.85f;
        this.f14785o.setLeftRightMarginRatio(0.5f);
        this.f14785o.setNumIncrements(50);
//        Logger.m349a(C0713a.f2679c, 45, -1695533085, a);
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (!this.f14781k) {
            this.f14775e = false;
            scrollTo(((this.f14785o.getLeft() + this.f14785o.getRight()) - getWidth()) / 2, getScrollY());
            this.f14780j = getScrollX() + (getWidth() / 2);
            this.f14775e = true;
            this.f14772b = (float) (this.f14785o.getWidth() - getWidth());
            this.f14782l = -1.0f;
            this.f14783m = 1.0f;
            this.f14781k = true;
        }
        if (this.f14776f) {
            m7793b(this.f14777g / 25.0f, this.f14778h);
            this.f14776f = false;
            this.f14777g = 0.0f;
            this.f14778h = false;
        }
    }

    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (this.f14775e) {
            float currentScrollPercent = getCurrentScrollPercent();
            if (currentScrollPercent > this.f14783m) {
                m7793b(this.f14783m, false);
                currentScrollPercent = this.f14783m;
            } else if (currentScrollPercent < this.f14782l) {
                m7793b(this.f14782l, false);
                currentScrollPercent = this.f14782l;
            }
            if (this.f14771a != null) {
                float round = ((float) Math.round((25.0f * currentScrollPercent) * 10.0f)) / 10.0f;
                if (this.f14784n != round) {
                    this.f14771a.mo2046a(round);
                    this.f14784n = round;
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r13) {

        return true;
        /*
        r12 = this;
        r1 = com.facebook.profilo.provider.p103a.C0713a.f2679c;
        r8 = 1;
        r0 = 1383463963; // 0x5275fc1b float:2.64124154E11 double:6.835220164E-315;
        r4 = com.facebook.profilo.logger.Logger.m348a(r1, r8, r0);
        r5 = r13.getAction();
        r0 = r13.getActionMasked();
        r11 = 1061158912; // 0x3f400000 float:0.75 double:5.24282163E-315;
        r3 = 2;
        r6 = 0;
        r2 = 0;
        switch(r0) {
            case 0: goto L_0x00d4;
            case 1: goto L_0x007f;
            case 2: goto L_0x001c;
            case 3: goto L_0x00d1;
            default: goto L_0x001a;
        };
    L_0x001a:
        goto L_0x00e0;
    L_0x001c:
        r12.f14779i = r2;
        r0 = r12.getCurrentScrollPercent();
        r10 = 1103626240; // 0x41c80000 float:25.0 double:5.45263811E-315;
        r0 = r0 * r10;
        r9 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r0 = r0 * r9;
        r0 = java.lang.Math.round(r0);
        r7 = (float) r0;
        r7 = r7 / r9;
        r1 = r12.getScrollX();
        r0 = r12.f14774d;
        r0 = (int) r0;
        r1 = r1 + r0;
        r0 = r12.m7792a(r1);
        r10 = r10 * r0;
        r10 = r10 * r9;
        r0 = java.lang.Math.round(r10);
        r1 = (float) r0;
        r1 = r1 / r9;
        r0 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1));
        if (r0 != 0) goto L_0x006e;
    L_0x0046:
        r0 = java.lang.Math.abs(r1);
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 >= 0) goto L_0x006e;
    L_0x004e:
        r12.m7793b(r6, r2);
        r12.f14773c = r2;
        r2 = r12.f14774d;
        r1 = r12.f14786p;
        r0 = r13.getX();
        r1 = r1 - r0;
        r2 = r2 + r1;
        r12.f14774d = r2;
        r0 = r13.getX();
        r12.f14786p = r0;
        r1 = com.facebook.profilo.provider.p103a.C0713a.f2679c;
        r0 = 1906045351; // 0x719bf1a7 float:1.5443941E30 double:9.417115273E-315;
        com.facebook.profilo.logger.Logger.m349a(r1, r3, r0, r4);
        return r8;
    L_0x006e:
        r0 = r12.f14773c;
        if (r0 != 0) goto L_0x0078;
    L_0x0072:
        r13.setAction(r2);
        r12.f14773c = r8;
        goto L_0x00e2;
    L_0x0078:
        r0 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1));
        if (r0 == 0) goto L_0x00e2;
    L_0x007c:
        r12.f14774d = r6;
        goto L_0x00e2;
    L_0x007f:
        r0 = r12.f14771a;
        if (r0 == 0) goto L_0x0088;
    L_0x0083:
        r0 = r12.f14771a;
        r0.mo2047b();
    L_0x0088:
        r0 = r12.f14779i;
        if (r0 == 0) goto L_0x00cf;
    L_0x008c:
        r7 = r13.getX();
        r0 = r12.getWidth();
        r1 = (float) r0;
        r0 = 1048576000; // 0x3e800000 float:0.25 double:5.180653787E-315;
        r1 = r1 * r0;
        r0 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1));
        if (r0 >= 0) goto L_0x009e;
    L_0x009c:
        r0 = r8;
        goto L_0x009f;
    L_0x009e:
        r0 = r2;
    L_0x009f:
        r7 = 990057071; // 0x3b03126f float:0.002 double:4.89153186E-315;
        if (r0 == 0) goto L_0x00b2;
    L_0x00a4:
        r0 = r12.f14771a;
        if (r0 == 0) goto L_0x00cf;
    L_0x00a8:
        r1 = -1;
        r0 = r12.f14772b;
        r7 = r7 * r0;
        r0 = (int) r7;
        r1 = r1 * r0;
        r12.scrollBy(r1, r2);
        goto L_0x00cf;
    L_0x00b2:
        r1 = r13.getX();
        r0 = r12.getWidth();
        r0 = (float) r0;
        r0 = r0 * r11;
        r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
        if (r0 <= 0) goto L_0x00c1;
    L_0x00c0:
        goto L_0x00c2;
    L_0x00c1:
        r8 = r2;
    L_0x00c2:
        if (r8 == 0) goto L_0x00cf;
    L_0x00c4:
        r0 = r12.f14771a;
        if (r0 == 0) goto L_0x00cf;
    L_0x00c8:
        r0 = r12.f14772b;
        r7 = r7 * r0;
        r0 = (int) r7;
        r12.scrollBy(r0, r2);
    L_0x00cf:
        r12.f14779i = r2;
    L_0x00d1:
        r12.f14786p = r6;
        goto L_0x00e0;
    L_0x00d4:
        r0 = r12.f14771a;
        if (r0 == 0) goto L_0x00dd;
    L_0x00d8:
        r0 = r12.f14771a;
        r0.mo2045a();
    L_0x00dd:
        r12.f14779i = r8;
        goto L_0x00e2;
    L_0x00e0:
        r12.f14779i = r2;
    L_0x00e2:
        r0 = r13.getX();
        r12.f14786p = r0;
        r2 = super.onTouchEvent(r13);
        r13.setAction(r5);
        r1 = com.facebook.profilo.provider.p103a.C0713a.f2679c;
        r0 = -1988790495; // 0xffffffff89757721 float:-2.9546816E-33 double:NaN;
        com.facebook.profilo.logger.Logger.m349a(r1, r3, r0, r4);
        return r2;
        */
//        throw new UnsupportedOperationException("Method not decompiled: com.instagram.creation.base.ui.sliderview.SliderView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setOnSlideListener(C3573b c3573b) {
        this.f14771a = c3573b;
    }
}
