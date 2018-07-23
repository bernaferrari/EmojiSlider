package com.bernaferrari.emojisliderSample.p228m;

import java.util.concurrent.CopyOnWriteArrayList;

public final class C6723u implements C1561h {
    private static final C1560g f26848f = C1560g.f6954a;
    private static int f26849g;
    public final C6724v f26850a;
    public final CopyOnWriteArrayList<SPRING_C1558e> f26851b;
    public final C1559f f26853d;
    public final C1559f f26854e;
    private final CopyOnWriteArrayList<C1561h> f26855h;
    public int f26852c;

    public C6723u() {
        this((byte) 0);
    }

    private C6723u(byte b) {
        this.f26850a = C6724v.m13495c();
        this.f26855h = new CopyOnWriteArrayList();
        this.f26851b = new CopyOnWriteArrayList();
        this.f26852c = -1;
        this.f26853d = C1559f.m4037a(40.0d, 6.0d);
        this.f26854e = C1559f.m4037a(70.0d, 10.0d);
        C1560g c1560g = f26848f;
        C1559f c1559f = this.f26853d;
        StringBuilder stringBuilder = new StringBuilder("main spring ");
        int i = f26849g;
        f26849g = i + 1;
        stringBuilder.append(i);
        c1560g.m4039a(c1559f, stringBuilder.toString());
        c1560g = f26848f;
        c1559f = this.f26854e;
        stringBuilder = new StringBuilder("attachment spring ");
        i = f26849g;
        f26849g = i + 1;
        stringBuilder.append(i);
        c1560g.m4039a(c1559f, stringBuilder.toString());
    }

    public final C6723u m13490a(C1561h c1561h) {
        this.f26851b.add(this.f26850a.m4019a().m4028a(this).m4027a(this.f26854e));
        this.f26855h.add(c1561h);
        return this;
    }

    public final void mo964a(SPRING_C1558e SPRINGC1558E) {
        int indexOf = this.f26851b.indexOf(SPRINGC1558E);
        C1561h c1561h = this.f26855h.get(indexOf);
        int i = -1;
        if (indexOf == this.f26852c) {
            i = indexOf - 1;
            indexOf++;
        } else if (indexOf < this.f26852c) {
            int i2 = indexOf - 1;
            indexOf = -1;
            i = i2;
        } else {
            indexOf = indexOf > this.f26852c ? indexOf + 1 : -1;
        }
        if (indexOf >= 0 && indexOf < this.f26851b.size()) {
            this.f26851b.get(indexOf).m4030b(SPRINGC1558E.f6940d.f6934a);
        }
        if (i >= 0 && i < this.f26851b.size()) {
            this.f26851b.get(i).m4030b(SPRINGC1558E.f6940d.f6934a);
        }
        c1561h.mo964a(SPRINGC1558E);
    }

    public final void mo965b(SPRING_C1558e SPRINGC1558E) {
        this.f26855h.get(this.f26851b.indexOf(SPRINGC1558E)).mo965b(SPRINGC1558E);
    }

    public final void mo966c(SPRING_C1558e SPRINGC1558E) {
        this.f26855h.get(this.f26851b.indexOf(SPRINGC1558E)).mo966c(SPRINGC1558E);
    }

    public final void mo967d(SPRING_C1558e SPRINGC1558E) {
        this.f26855h.get(this.f26851b.indexOf(SPRINGC1558E)).mo967d(SPRINGC1558E);
    }
}
