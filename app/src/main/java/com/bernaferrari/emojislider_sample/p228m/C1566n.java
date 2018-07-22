package com.bernaferrari.emojislider_sample.p228m;

import android.os.SystemClock;

final class C1566n implements Runnable {
    final /* synthetic */ C6722o f6958a;

    C1566n(C6722o c6722o) {
        this.f6958a = c6722o;
    }

    public final void run() {
        if (this.f6958a.f26846d) {
            if (this.f6958a.f6956a != null) {
                long uptimeMillis = SystemClock.uptimeMillis();
                this.f6958a.f6956a.m4020a((double) (uptimeMillis - this.f6958a.f26847e));
                this.f6958a.f26847e = uptimeMillis;
//                C0486e.m1104a(this.f6958a.f26844b, this.f6958a.f26845c, -257725303);
            }
        }
    }
}
