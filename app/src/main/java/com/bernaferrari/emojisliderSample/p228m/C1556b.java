package com.bernaferrari.emojisliderSample.p228m;

public class C1556b {
    final double f6930a;
    final double f6931b;
    private final double f6932d;
    private final double f6933e;

    public C1556b(double d, double d2) {
        this.f6932d = d;
        this.f6933e = d2;
        double d3 = 0.0d;
        d2 = ((((d2 / 1.7d) - 0.0d) / 20.0d) * 0.8d) + 0.0d;
        this.f6930a = 0.5d + ((((d / 1.7d) - 0.0d) / 20.0d) * 199.5d);
        double d4 = this.f6930a;
        if (d4 <= 18.0d) {
            d3 = (((7.0E-4d * Math.pow(d4, 3.0d)) - (0.031d * Math.pow(d4, 2.0d))) + (0.64d * d4)) + 1.28d;
        } else if (d4 > 18.0d && d4 <= 44.0d) {
            d3 = (((4.4E-5d * Math.pow(d4, 3.0d)) - (0.006d * Math.pow(d4, 2.0d))) + (0.36d * d4)) + 2.0d;
        } else if (d4 > 44.0d) {
            d3 = (((4.5E-7d * Math.pow(d4, 3.0d)) - (3.32E-4d * Math.pow(d4, 2.0d))) + (0.1078d * d4)) + 5.84d;
        } else if (1 == 0) {
            throw new AssertionError();
        }
        double d5 = (2.0d * d2) - (d2 * d2);
        this.f6931b = (0.01d * d5) + ((1.0d - d5) * d3);
    }
}
