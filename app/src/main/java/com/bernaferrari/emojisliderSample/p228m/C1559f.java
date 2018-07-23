package com.bernaferrari.emojisliderSample.p228m;

public final class C1559f {
    public static C1559f f6951c = C1559f.m4037a(40.0d, 7.0d);
    public double friction_f6952a;
    public double tension_f6953b;

    public C1559f(double d, double d2) {
        this.tension_f6953b = d;
        this.friction_f6952a = d2;
    }

    public static C1559f m4037a(double d, double d2) {
        double d3 = 0.0d;
        d = d == 0.0d ? 0.0d : ((d - 30.0d) * 3.62d) + 194.0d;
        if (d2 != 0.0d) {
            d3 = 25.0d + ((d2 - 8.0d) * 3.0d);
        }
        return new C1559f(d, d3);
    }

    public static C1559f m4038b(double d, double d2) {
        C1556b c1556b = new C1556b(d2, d);
        return C1559f.m4037a(c1556b.f6930a, c1556b.f6931b);
    }
}
