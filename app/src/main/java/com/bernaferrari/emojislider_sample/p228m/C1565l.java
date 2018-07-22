package com.bernaferrari.emojislider_sample.p228m;

import android.os.SystemClock;
import android.view.Choreographer.FrameCallback;

final class C1565l implements FrameCallback {
    final /* synthetic */ C6721m f6957a;

    C1565l(C6721m c6721m) {
        this.f6957a = c6721m;
    }

    public final void doFrame(long j) {
        if (this.f6957a.f26842d) {
            if (this.f6957a.f6956a != null) {
                long uptimeMillis = SystemClock.uptimeMillis();
                this.f6957a.f6956a.m4020a((double) (uptimeMillis - this.f6957a.f26843e));
                this.f6957a.f26843e = uptimeMillis;
                this.f6957a.f26840b.postFrameCallback(this.f6957a.f26841c);
            }
        }
    }
}
