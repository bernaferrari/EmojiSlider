package com.bernaferrari.emojisliderSample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class MaskingFrameLayout extends FrameLayout {
    private final Paint c = new Paint(1);
    private final Rect d = new Rect();
    private final Rect e = new Rect();
    public float a;
    public Bitmap b;

    public MaskingFrameLayout(Context paramContext) {
        super(paramContext);
        a();
    }

    public MaskingFrameLayout(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        a();
    }

    public MaskingFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        a();
    }

    private void a() {
        c.setColor(getResources().getColor(R.color.black));
        c.setDither(true);
        c.setFilterBitmap(true);
        c.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
    }

    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        if (b != null) {
            getGlobalVisibleRect(e);
            e.set(Math.round(e.left / a), Math.round(e.top / a), Math.round(e.right / a), Math.round(e.bottom / a));
            d.set(0, 0, getWidth(), getHeight());
            paramCanvas.drawBitmap(b, e, d, c);
            c.setAlpha(51);
            paramCanvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), c);
            c.setAlpha(255);
        }
    }
}

/* Location:
 * Qualified Name:     com.instagram.ui.widget.tooltippopup.MaskingFrameLayout
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */