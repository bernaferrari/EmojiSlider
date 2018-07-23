package com.bernaferrari.emojisliderSample.arrowpopupwindow.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class Util {
    public static int DpToPx(Context context, float x) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (x * scale + 0.5f);
    }

    public static int PxToDp(Context context, int x) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (x / scale + 0.5f);
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap scaleBitmap(Bitmap source, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
