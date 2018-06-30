package com.bernaferrari.emojislider;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class SpinnerImageView extends ColorFilterAlphaImageView {
    private ObjectAnimator c;
    private boolean d = true;
    private boolean e;

    public SpinnerImageView(Context paramContext) {
        super(paramContext);
        c();
    }

    public SpinnerImageView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        c();
    }

    public SpinnerImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        c();
    }

    private void c() {
        setScaleType(ImageView.ScaleType.CENTER);
        c = ObjectAnimator.ofFloat(this, "rotation", 0.0F, 360.0F).setDuration(850L);
        c.setRepeatMode(ValueAnimator.RESTART);
        c.setRepeatCount(-1);
        c.setInterpolator(new LinearInterpolator());
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if ((isShown()) && (d)) {
            c.start();
        }
    }

    protected void onDetachedFromWindow() {
        e = false;
        c.cancel();
        super.onDetachedFromWindow();
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, paramInt2);
        if ((e) && (d)) {
            c.start();
            e = false;
        }
    }

    protected void onVisibilityChanged(@NonNull View paramView, int paramInt) {
        super.onVisibilityChanged(paramView, paramInt);
        if (getWindowToken() != null) {
            if ((paramInt == 0) && (getVisibility() == View.VISIBLE) && (d)) {
                if (getAnimation() == null) {
                    if (getMeasuredWidth() != 0) {
                        c.start();
                        return;
                    }
                    e = true;
                }
            } else {
                c.cancel();
                e = false;
            }
        }
    }

    public void setLoadingStatus(int paramInt) {
        switch (paramInt) {
            default:
                break;
            case 3:
                setVisibility(View.GONE);
                break;
            case 2:
                d = false;
                c.end();
//      setBackgroundResource(2131231446);
                setVisibility(VISIBLE);
                return;
            case 1:
                d = true;
                c.start();
//      setBackgroundResource(2131231990);
                setVisibility(VISIBLE);
        }
    }
}

/* Location:
 * Qualified Name:     com.instagram.ui.widget.spinner.SpinnerImageView
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */