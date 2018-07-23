package com.bernaferrari.emojisliderSample.p228m;

import android.os.Handler;
import android.os.SystemClock;

final class C6722o extends C1562i {
    final Handler f26844b;
    final Runnable f26845c = new C1566n(this);
    boolean f26846d;
    long f26847e;

    public C6722o(Handler handler) {
        this.f26844b = handler;
    }

    public final void mo968a() {
        if (!this.f26846d) {
            this.f26846d = true;
            this.f26847e = SystemClock.uptimeMillis();
        }
    }

    public final void mo969b() {
        this.f26846d = false;
    }
}
