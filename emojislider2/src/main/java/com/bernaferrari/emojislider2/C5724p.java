package com.bernaferrari.emojislider2;

import android.os.Build.VERSION;
import android.text.Layout;
import android.text.StaticLayout;

public final class C5724p {
    public static int m10696a(Layout layout) {
        int i = 0;
        if (layout == null) {
            return 0;
        }
        int lineCount = layout.getLineCount();
        int i2 = 0;
        while (i < lineCount) {
            i2 = Math.max(i2, Math.round(layout.getLineRight(i) - layout.getLineLeft(i)));
            i++;
        }
        return i2;
    }

    public static int m10697b(Layout layout) {
        int i = 0;
        if (layout == null) {
            return 0;
        }
        if (VERSION.SDK_INT <= 19 && (layout instanceof StaticLayout)) {
            float lineDescent = (float) (layout.getLineDescent(layout.getLineCount() - 1) - layout.getLineAscent(layout.getLineCount() - 1));
            lineDescent -= (lineDescent - layout.getSpacingAdd()) / layout.getSpacingMultiplier();
            i = lineDescent >= 0.0f ? (int) (((double) lineDescent) + 0.5d) : -((int) (((double) (-lineDescent)) + 0.5d));
        }
        return layout.getHeight() - i;
    }

    public static int m10698c(Layout layout) {
        int i = 0;
        if (layout != null) {
            if (layout.getLineCount() != 0) {
                int i2 = Integer.MAX_VALUE;
                while (i < layout.getLineCount()) {
                    i2 = Math.min(i2, (int) layout.getLineLeft(i));
                    i++;
                }
                return i2;
            }
        }
        return 0;
    }
}