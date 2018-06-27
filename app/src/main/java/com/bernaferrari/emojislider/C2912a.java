package com.bernaferrari.emojislider;

import android.os.Handler;
import android.os.Looper;

public class C2912a {
    private static Handler f12651a;

    public static void m6550a() {
        C2912a.m6552a("This operation must be run on UI thread.");
    }

    public static void m6551a(Runnable runnable) {
        if ((Looper.getMainLooper().getThread() == Thread.currentThread() ? 1 : null) != null) {
            runnable.run();
        } else {
//            C0486e.m1104a(C2912a.m6556d(), runnable, -1345656438);
        }
    }

    public static void m6552a(String str) {
        if ((Looper.getMainLooper().getThread() == Thread.currentThread() ? 1 : null) == null) {
            throw new IllegalStateException(str);
        }
    }

    public static void m6553b() {
        if (((Looper.getMainLooper().getThread() == Thread.currentThread() ? 1 : 0) ^ 1) == 0) {
            throw new IllegalStateException("This operation can't be run on UI thread.");
        }
    }

    public static void m6554b(Runnable runnable) {
//        C0486e.m1104a(C2912a.m6556d(), runnable, -1469338351);
    }

    public static boolean m6555c() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    private static Handler m6556d() {
        if (f12651a == null) {
            synchronized (C2912a.class) {
                if (f12651a == null) {
                    f12651a = new Handler(Looper.getMainLooper());
                }
            }
        }
        return f12651a;
    }
}
