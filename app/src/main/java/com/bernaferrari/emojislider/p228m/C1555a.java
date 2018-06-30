package com.bernaferrari.emojislider.p228m;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class C1555a {
    public final CopyOnWriteArraySet<C1563j> f6927c = new CopyOnWriteArraySet();
    final Map<String, C1558e> f6925a = new HashMap();
    final Set<C1558e> f6926b = new CopyOnWriteArraySet();
    private final C1562i f6929e;
    public boolean f6928d = true;

    public C1555a(C1562i c1562i) {
        if (c1562i != null) {
            this.f6929e = c1562i;
            this.f6929e.f6956a = this;
            return;
        }
        throw new IllegalArgumentException("springLooper is required");
    }

    public final C1558e m4019a() {
        C1558e c1558e = new C1558e(this);
        if (this.f6925a.containsKey(c1558e.f6939c)) {
            throw new IllegalArgumentException("spring is already registered");
        }
        this.f6925a.put(c1558e.f6939c, c1558e);
        return c1558e;
    }

    public final void m4020a(double d) {
        Iterator it;
        Iterator it2 = this.f6927c.iterator();
        while (it2.hasNext()) {
            ((C1563j) it2.next()).mo1813a();
        }
        for (C1558e c1558e : this.f6926b) {
            Object obj;
            double d2;
            boolean c;
            C1558e c1558e2;
            double d3;
            double d4;
            double d5;
            double d6;
            double d7;
            double d8;
            C1558e c1558e3;
            double d9;
            boolean z;
            boolean z2;
            C1561h c1561h;
            if (c1558e.m4034c()) {
                if (c1558e.f6945i) {
                    obj = null;
                    if (obj == null) {
                        d2 = d / 1000.0d;
                        c = c1558e.m4034c();
                        if (c) {
                            if (c1558e.f6945i) {
                            }
                        }
                        if (d2 > 0.064d) {
                            d2 = 0.064d;
                        }
                        c1558e2 = c1558e;
                        c1558e2.f6948l += d2;
                        d3 = c1558e2.f6937a.f6953b;
                        d4 = c1558e.f6937a.f6952a;
                        d5 = c1558e.f6940d.f6934a;
                        d6 = c1558e.f6940d.f6935b;
                        d7 = c1558e.f6942f.f6934a;
                        d8 = c1558e.f6942f.f6935b;
                        while (c1558e.f6948l >= 0.001d) {
                            c1558e3 = c1558e;
                            c1558e3.f6948l = c1558e.f6948l - 0.001d;
                            if (c1558e3.f6948l < 0.001d) {
                                c1558e3.f6941e.f6934a = d5;
                                c1558e3.f6941e.f6935b = d6;
                            }
                            double d10 = (d3 * (c1558e3.f6944h - d7)) - (d4 * d6);
                            double d11 = d6 + ((d10 * 0.001d) * 0.5d);
                            double d12 = (d3 * (c1558e.f6944h - (((d6 * 0.001d) * 0.5d) + d5))) - (d4 * d11);
                            double d13 = d6 + ((d12 * 0.001d) * 0.5d);
                            d9 = ((c1558e.f6944h - (((d11 * 0.001d) * 0.5d) + d5)) * d3) - (d4 * d13);
                            d7 = (d13 * 0.001d) + d5;
                            d8 = d6 + (d9 * 0.001d);
                            d5 += (((d6 + ((d11 + d13) * 2.0d)) + d8) * 0.16666666666666666d) * 0.001d;
                            d6 += (0.16666666666666666d * ((d10 + (2.0d * (d12 + d9))) + (((c1558e.f6944h - d7) * d3) - (d4 * d8)))) * 0.001d;
                        }
                        c1558e.f6942f.f6934a = d7;
                        c1558e.f6942f.f6935b = d8;
                        c1558e.f6940d.f6934a = d5;
                        c1558e.f6940d.f6935b = d6;
                        if (c1558e.f6948l > 0.0d) {
                            c1558e2 = c1558e;
                            double d14 = c1558e2.f6948l / 0.001d;
                            d9 = 1.0d - d14;
                            c1558e2.f6940d.f6934a = (c1558e2.f6940d.f6934a * d14) + (c1558e.f6941e.f6934a * d9);
                            c1558e2 = c1558e;
                            c1558e2.f6940d.f6935b = (c1558e2.f6940d.f6935b * d14) + (c1558e.f6941e.f6935b * d9);
                        }
                        if (c1558e.m4034c() || (c1558e.f6938b && c1558e.m4032b())) {
                            if (d3 <= 0.0d) {
                                c1558e2 = c1558e;
                                c1558e2.f6943g = c1558e2.f6944h;
                                c1558e2.f6940d.f6934a = c1558e2.f6944h;
                            } else {
                                c1558e2 = c1558e;
                                c1558e2.f6944h = c1558e.f6940d.f6934a;
                                c1558e2.f6943g = c1558e2.f6944h;
                            }
                            c1558e.m4033c(0.0d);
                            c = true;
                        }
                        if (c1558e.f6945i) {
                            z = false;
                            z2 = false;
                        } else {
                            z = false;
                            c1558e.f6945i = false;
                            z2 = true;
                        }
                        if (c) {
                            c1558e.f6945i = true;
                            z = true;
                        }
                        it = c1558e.f6949m.iterator();
                        while (it.hasNext()) {
                            c1561h = (C1561h) it.next();
                            if (z2) {
                                c1561h.mo966c(c1558e);
                            }
                            c1558e2 = c1558e;
                            c1561h.mo964a(c1558e2);
                            if (z) {
                                c1561h.mo965b(c1558e2);
                            }
                        }
                    } else {
                        this.f6926b.remove(c1558e);
                    }
                }
            }
            obj = 1;
            if (obj == null) {
                this.f6926b.remove(c1558e);
            } else {
                d2 = d / 1000.0d;
                c = c1558e.m4034c();
                if (c) {
                    if (c1558e.f6945i) {
                    }
                }
                if (d2 > 0.064d) {
                    d2 = 0.064d;
                }
                c1558e2 = c1558e;
                c1558e2.f6948l += d2;
                d3 = c1558e2.f6937a.f6953b;
                d4 = c1558e.f6937a.f6952a;
                d5 = c1558e.f6940d.f6934a;
                d6 = c1558e.f6940d.f6935b;
                d7 = c1558e.f6942f.f6934a;
                d8 = c1558e.f6942f.f6935b;
                while (c1558e.f6948l >= 0.001d) {
                    c1558e3 = c1558e;
                    c1558e3.f6948l = c1558e.f6948l - 0.001d;
                    if (c1558e3.f6948l < 0.001d) {
                        c1558e3.f6941e.f6934a = d5;
                        c1558e3.f6941e.f6935b = d6;
                    }
                    double d102 = (d3 * (c1558e3.f6944h - d7)) - (d4 * d6);
                    double d112 = d6 + ((d102 * 0.001d) * 0.5d);
                    double d122 = (d3 * (c1558e.f6944h - (((d6 * 0.001d) * 0.5d) + d5))) - (d4 * d112);
                    double d132 = d6 + ((d122 * 0.001d) * 0.5d);
                    d9 = ((c1558e.f6944h - (((d112 * 0.001d) * 0.5d) + d5)) * d3) - (d4 * d132);
                    d7 = (d132 * 0.001d) + d5;
                    d8 = d6 + (d9 * 0.001d);
                    d5 += (((d6 + ((d112 + d132) * 2.0d)) + d8) * 0.16666666666666666d) * 0.001d;
                    d6 += (0.16666666666666666d * ((d102 + (2.0d * (d122 + d9))) + (((c1558e.f6944h - d7) * d3) - (d4 * d8)))) * 0.001d;
                }
                c1558e.f6942f.f6934a = d7;
                c1558e.f6942f.f6935b = d8;
                c1558e.f6940d.f6934a = d5;
                c1558e.f6940d.f6935b = d6;
                if (c1558e.f6948l > 0.0d) {
                    c1558e2 = c1558e;
                    double d142 = c1558e2.f6948l / 0.001d;
                    d9 = 1.0d - d142;
                    c1558e2.f6940d.f6934a = (c1558e2.f6940d.f6934a * d142) + (c1558e.f6941e.f6934a * d9);
                    c1558e2 = c1558e;
                    c1558e2.f6940d.f6935b = (c1558e2.f6940d.f6935b * d142) + (c1558e.f6941e.f6935b * d9);
                }
                if (d3 <= 0.0d) {
                    c1558e2 = c1558e;
                    c1558e2.f6944h = c1558e.f6940d.f6934a;
                    c1558e2.f6943g = c1558e2.f6944h;
                } else {
                    c1558e2 = c1558e;
                    c1558e2.f6943g = c1558e2.f6944h;
                    c1558e2.f6940d.f6934a = c1558e2.f6944h;
                }
                c1558e.m4033c(0.0d);
                c = true;
                if (c1558e.f6945i) {
                    z = false;
                    z2 = false;
                } else {
                    z = false;
                    c1558e.f6945i = false;
                    z2 = true;
                }
                if (c) {
                    c1558e.f6945i = true;
                    z = true;
                }
                it = c1558e.f6949m.iterator();
                while (it.hasNext()) {
                    c1561h = (C1561h) it.next();
                    if (z2) {
                        c1561h.mo966c(c1558e);
                    }
                    c1558e2 = c1558e;
                    c1561h.mo964a(c1558e2);
                    if (z) {
                        c1561h.mo965b(c1558e2);
                    }
                }
            }
        }
        if (this.f6926b.isEmpty()) {
            this.f6928d = true;
        }
        it = this.f6927c.iterator();
        while (it.hasNext()) {
            ((C1563j) it.next()).mo1814a(this);
        }
        if (this.f6928d) {
            this.f6929e.mo969b();
        }
    }

    public final void m4021a(C1563j c1563j) {
        if (c1563j != null) {
            this.f6927c.add(c1563j);
            return;
        }
        throw new IllegalArgumentException("newListener is required");
    }

    final void m4022a(String str) {
        C1558e c1558e = this.f6925a.get(str);
        if (c1558e != null) {
            this.f6926b.add(c1558e);
            if (this.f6928d) {
                this.f6928d = false;
                this.f6929e.mo968a();
                return;
            }
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("springId ");
        stringBuilder.append(str);
        stringBuilder.append(" does not reference a registered spring");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public final List<C1558e> m4023b() {
        List list;
        Collection values = this.f6925a.values();
        if (values instanceof List) {
            list = (List) values;
        } else {
            list = new ArrayList(values);
        }
        return Collections.unmodifiableList(list);
    }

    public final void m4024b(C1563j c1563j) {
        if (c1563j != null) {
            this.f6927c.remove(c1563j);
            return;
        }
        throw new IllegalArgumentException("listenerToRemove is required");
    }
}
