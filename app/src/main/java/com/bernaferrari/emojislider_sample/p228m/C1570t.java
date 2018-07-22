package com.bernaferrari.emojislider_sample.p228m;

import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;

public final class C1570t {
    public static final boolean f6963a = (VERSION.SDK_INT >= 16);
    public static final C1570t f6964d = new C1570t();
    public Handler f6965b;
    public Choreographer f6966c;

    private C1570t() {
        if (f6963a) {
            this.f6966c = Choreographer.getInstance();
        } else {
            this.f6965b = new Handler(Looper.getMainLooper());
        }
    }

    public final void m4052a(C1569s c1569s) {
        if (f6963a) {
            this.f6966c.postFrameCallback(c1569s.m4049a());
            return;
        }
//        C0486e.m1108b(this.f6965b, c1569s.m4050b(), 0, -299697730);
    }

    public final void m4053b(C1569s c1569s) {
        if (f6963a) {
            this.f6966c.removeFrameCallback(c1569s.m4049a());
            return;
        }
//        C0486e.m1103a(this.f6965b, c1569s.m4050b());
    }
}
