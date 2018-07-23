package com.bernaferrari.emojisliderSample.p228m;

import android.annotation.TargetApi;
import android.view.Choreographer.FrameCallback;

public abstract class C1569s {
    private Runnable f6961a;
    private FrameCallback f6962b;

    public abstract void mo1850c();

    @TargetApi(16)
    public final FrameCallback m4049a() {
        if (this.f6962b == null) {
            this.f6962b = new C1567q(this);
        }
        return this.f6962b;
    }

    public final Runnable m4050b() {
        if (this.f6961a == null) {
            this.f6961a = new C1568r(this);
        }
        return this.f6961a;
    }
}
