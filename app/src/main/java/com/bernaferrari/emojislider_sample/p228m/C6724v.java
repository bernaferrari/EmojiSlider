package com.bernaferrari.emojislider_sample.p228m;

import android.os.Build.VERSION;
import android.os.Handler;
import android.view.Choreographer;

public final class C6724v extends C1555a {
    private C6724v(C1562i c1562i) {
        super(c1562i);
    }

    public static C6724v m13495c() {
        C1562i c6721m;
        if (VERSION.SDK_INT >= 16) {
            c6721m = new C6721m(Choreographer.getInstance());
        } else {
            c6721m = new C6722o(new Handler());
        }
        return new C6724v(c6721m);
    }
}
