package com.bernaferrari.emojisliderSample;

import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.ImageView;

public final class b {
    private static final TypedValue a = new TypedValue();

    public static void a(Resources.Theme paramTheme, ImageView paramImageView, int paramInt) {
        paramTheme.resolveAttribute(paramInt, a, true);
        paramImageView.setColorFilter(a.data);
    }
}

/* Location:
 * Qualified Name:     com.instagram.common.ui.colorfilter.b
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */