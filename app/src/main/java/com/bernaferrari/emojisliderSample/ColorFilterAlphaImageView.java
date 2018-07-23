package com.bernaferrari.emojisliderSample;

import android.content.Context;
import android.graphics.ColorFilter;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class ColorFilterAlphaImageView extends AppCompatImageView {
    public ColorFilter c;
    public ColorFilter d;
    protected int a = 255;
    protected int b = 255;
    private int e = 255;
    private int f = 255;

    public ColorFilterAlphaImageView(Context paramContext) {
        super(paramContext);
    }

    public ColorFilterAlphaImageView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        a(paramContext, paramAttributeSet);
    }

    public ColorFilterAlphaImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        a(paramContext, paramAttributeSet);
    }

    private static boolean a(int[] paramArrayOfInt, int paramInt) {
        int i = paramArrayOfInt.length;
        for (int j = 0; j < i; j++) {
            if (paramInt == paramArrayOfInt[j]) {
                return true;
            }
        }
        return false;
    }

    public static void c(ColorFilterAlphaImageView paramColorFilterAlphaImageView) {
//    boolean bool1 = paramColorFilterAlphaImageView.isDuplicateParentStateEnabled();
//    int i = 1;
//    int j;
//    Object localObject;
//    if (!bool1)
//    {
//      bool1 = paramColorFilterAlphaImageView.isEnabled();
//      if ((bool1) && (paramColorFilterAlphaImageView.b())) {
//        j = i;
//      } else {
//        j = 0;
//      }
//    }
//    else
//    {
//      localObject = paramColorFilterAlphaImageView.getDrawableState();
//      boolean bool2 = a((int[])localObject, 16842910);
//      if (bool2)
//      {
//        bool1 = true;
//        j = i;
//        if (a((int[])localObject, 16842913)) {
//          break label95;
//        }
//        if (a((int[])localObject, 16842919))
//        {
//          bool1 = bool2;
//          j = i;
//          break label95;
//        }
//      }
//      j = 0;
//      bool1 = bool2;
//    }
//    if (bool1) {
//      if (j != 0) {
//        localObject = d;
//        j = e;
//      } else {
//        localObject = c;
//        j = a;
//      }
//    } else {
//      j = b;
//      localObject = c;
//    }
//    paramColorFilterAlphaImageView.setColorFilter((ColorFilter)localObject);
//    paramColorFilterAlphaImageView.setImageAlpha(Math.round(j / 255.0F * f));
    }

    private void a(Context paramContext, AttributeSet paramAttributeSet) {
//    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, ad.ColorFilterAwareImageView);
//    if (paramContext.hasValue(3)) {
//      c = a.a(paramContext.getColor(3, 0));
//    }
//    if (paramContext.hasValue(4)) {
//      a = paramContext.getInt(4, 255);
//    }
//    if (paramContext.hasValue(0)) {
//      d = a.a(paramContext.getColor(0, 0));
//    } else {
//      d = c;
//    }
//    if (paramContext.hasValue(1)) {
//      e = paramContext.getInt(1, 255);
//    }
//    if (paramContext.hasValue(2)) {
//      b = paramContext.getInt(2, 255);
//    }
//    paramContext.recycle();
        c(this);
    }

    protected boolean b() {
        return (isSelected()) || (isPressed());
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        c(this);
    }

    protected ColorFilter getActiveColorFilter() {
        return d;
    }

    public void setActiveColorFilter(int paramInt) {
//    d = a.a(paramInt);
        c(this);
    }

    public int getDisabledAlpha() {
        return b;
    }

    public void setDisabledAlpha(int paramInt) {
        b = paramInt;
        c(this);
    }

    protected ColorFilter getNormalColorFilter() {
        return c;
    }

    public void setNormalColorFilter(int paramInt) {
//    c = a.a(paramInt);
        c(this);
    }

    public void setActiveAlpha(int paramInt) {
        e = paramInt;
        c(this);
    }

    public void setImageAlpha(int paramInt) {
        f = paramInt;
        c(this);
    }

    public void setImageResource(int paramInt) {
        super.setImageResource(paramInt);
        c(this);
    }

    public void setNormalAlpha(int paramInt) {
        a = paramInt;
        c(this);
    }
}

/* Location:
 * Qualified Name:     com.instagram.common.ui.colorfilter.ColorFilterAlphaImageView
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */