package com.bernaferrari.emojislider;

import android.graphics.Color;

public final class C3395a {
    public static int m7507a(int i, int i2) {
        float f = (float) i;
        if (f > 127.5f) {
            float f2 = 255.0f - f;
            return (int) ((((float) i2) * (f2 / 127.5f)) + (f - f2));
        }
        return (int) (((float) i2) * (f / 127.5f));
    }

    public static int m7506a(int i) {
        float[] fArr = new float[3];
        if (((double) (((float) Color.alpha(i)) / 255.0f)) >= 0.3d) {
            if (C3395a.m7510c(i) <= 0.85f) {
                return -1;
            }
        }
        Color.colorToHSV(i, fArr);
        if (fArr[0] == 0.0f) {
            return -16777216;
        }
        fArr[1] = 1.0f;
        fArr[2] = 0.4f;
        return Color.HSVToColor(fArr);
    }

    public static int m7508a(int i, int i2, float f) {
        int red = Color.red(i);
        int green = Color.green(i);
        int blue = Color.blue(i);
        return Color.rgb(red + ((int) (((float) (Color.red(i2) - red)) * f)), green + ((int) (((float) (Color.green(i2) - green)) * f)), blue + ((int) (((float) (Color.blue(i2) - blue)) * f)));
    }

    public static int m7509b(int i) {
        if (((double) C3395a.m7510c(i)) < 0.25d) {
            return 1291845631;
        }
        return Color.rgb(Math.max(Color.red(i) - 38, 0), Math.max(Color.green(i) - 38, 0), Math.max(Color.blue(i) - 38, 0));
    }

    private static float m7510c(int i) {
        return ((((((float) Color.red(i)) / 255.0f) * 299.0f) + ((((float) Color.green(i)) / 255.0f) * 587.0f)) + ((((float) Color.blue(i)) / 255.0f) * 114.0f)) / 1000.0f;
    }
}
