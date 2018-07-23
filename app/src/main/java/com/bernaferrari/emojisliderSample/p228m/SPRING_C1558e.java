package com.bernaferrari.emojisliderSample.p228m;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

public final class SPRING_C1558e {
    private static int f6936n;
    public final C1557d f6940d = new C1557d();
    public final CopyOnWriteArraySet<C1561h> f6949m = new CopyOnWriteArraySet();
    final String f6939c;
    final C1557d f6941e = new C1557d();
    final C1557d f6942f = new C1557d();
    private final C1555a f6950o;
    public C1559f f6937a;
    public boolean f6938b;
    public double f6943g;
    public double f6944h;
    public double f6946j = 0.005d;
    public double f6947k = 0.005d;
    boolean f6945i = true;
    double f6948l = 0.0d;

    SPRING_C1558e(C1555a c1555a) {
        if (c1555a != null) {
            this.f6950o = c1555a;
            StringBuilder stringBuilder = new StringBuilder("spring:");
            int i = f6936n;
            f6936n = i + 1;
            stringBuilder.append(i);
            this.f6939c = stringBuilder.toString();
            m4027a(C1559f.f6951c);
            return;
        }
        throw new IllegalArgumentException("Spring cannot be created outside of a BaseSpringSystem");
    }

    public final SPRING_C1558e setCurrentValuem4025a(double d) {
        return setCurrentValuem4026a(d, true);
    }

    public final SPRING_C1558e setCurrentValuem4026a(double d, boolean z) {
        this.f6943g = d;
        this.f6940d.f6934a = d;
        this.f6950o.m4022a(this.f6939c);
        Iterator it = this.f6949m.iterator();
        while (it.hasNext()) {
            ((C1561h) it.next()).mo964a(this);
        }
        if (z) {
            m4035d();
        }
        return this;
    }

    public final SPRING_C1558e m4027a(C1559f c1559f) {
        if (c1559f != null) {
            this.f6937a = c1559f;
            return this;
        }
        throw new IllegalArgumentException("springConfig is required");
    }

    public final SPRING_C1558e m4028a(C1561h c1561h) {
        if (c1561h != null) {
            this.f6949m.add(c1561h);
            return this;
        }
        throw new IllegalArgumentException("newListener is required");
    }

    public final void m4029a() {
        this.f6949m.clear();
        C1555a c1555a = this.f6950o;
        c1555a.f6926b.remove(this);
        c1555a.f6925a.remove(this.f6939c);
    }

    public final SPRING_C1558e m4030b(double d) {
        if (this.f6944h == d && m4034c()) {
            return this;
        }
        this.f6943g = this.f6940d.f6934a;
        this.f6944h = d;
        this.f6950o.m4022a(this.f6939c);
        Iterator it = this.f6949m.iterator();
        while (it.hasNext()) {
            ((C1561h) it.next()).mo967d(this);
        }
        return this;
    }

    public final SPRING_C1558e m4031b(C1561h c1561h) {
        if (c1561h != null) {
            this.f6949m.remove(c1561h);
            return this;
        }
        throw new IllegalArgumentException("listenerToRemove is required");
    }

    public final boolean m4032b() {
        return this.f6937a.tension_f6953b > 0.0d && ((this.f6943g < this.f6944h && this.f6940d.f6934a > this.f6944h) || (this.f6943g > this.f6944h && this.f6940d.f6934a < this.f6944h));
    }

    public final SPRING_C1558e m4033c(double d) {
        if (d == this.f6940d.f6935b) {
            return this;
        }
        this.f6940d.f6935b = d;
        this.f6950o.m4022a(this.f6939c);
        return this;
    }

    public final boolean m4034c() {
        if (Math.abs(this.f6940d.f6935b) <= this.f6946j) {
            return Math.abs(this.f6944h - this.f6940d.f6934a) <= this.f6947k || this.f6937a.tension_f6953b == 0.0d;
        }
        return false;
    }

    public final SPRING_C1558e m4035d() {
        this.f6944h = this.f6940d.f6934a;
        this.f6942f.f6934a = this.f6940d.f6934a;
        this.f6940d.f6935b = 0.0d;
        return this;
    }

    public final boolean m4036d(double d) {
        return Math.abs(this.f6940d.f6934a - d) <= this.f6947k;
    }
}
