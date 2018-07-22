package com.bernaferrari.emojislider_sample.p228m;

import android.annotation.TargetApi;
import android.os.SystemClock;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;

@TargetApi(16)
final class C6721m extends C1562i {
    final Choreographer f26840b;
    final FrameCallback f26841c = new C1565l(this);
    boolean f26842d;
    long f26843e;

    public C6721m(Choreographer choreographer) {
        this.f26840b = choreographer;
    }

    public final void mo968a() {
        if (!this.f26842d) {
            this.f26842d = true;
            this.f26843e = SystemClock.uptimeMillis();
            this.f26840b.removeFrameCallback(this.f26841c);
            this.f26840b.postFrameCallback(this.f26841c);
        }
    }

    public final void mo969b() {
        this.f26842d = false;
        this.f26840b.removeFrameCallback(this.f26841c);
    }
}
