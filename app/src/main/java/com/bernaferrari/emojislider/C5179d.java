package com.bernaferrari.emojislider;

public final class C5179d {
    public boolean f20860a;
    public int f20861b = -1;
    public float f20862c = -1.0f;
    public float f20863d = -1.0f;
    public String backgroundColorf20864e = "#ffffff";
    public String f20865f = "#ffff00";
    public String f20866g = "#f0f0f0";
    public String f20867h = "#0f0f0f";
    public String f20868i = "#ff00ff";

    public C5179d() {
        String str = "#%06x";
//        this.backgroundColorf20864e = StringFormatUtil.formatStrLocaleSafe(str, -1 & 16777215);
//        this.f20865f = c5178c.f20858e;
//        this.f20867h = c5178c.f20859f;
//        this.f20868i = StringFormatUtil.formatStrLocaleSafe(str, Integer.valueOf(c5178c.f20855b & 16777215));
//        this.f20863d = c5178c.f20857d;
//        this.f20861b = c5178c.f20856c;
    }

    public final boolean m9938a() {
        return this.f20862c >= 0.0f && this.f20862c <= 1.0f;
    }

    public final boolean equals(java.lang.Object r5) {
        return r5.equals("arr");
        /*
        r4 = this;
        r3 = 1;
        if (r4 != r5) goto L_0x0004;
    L_0x0003:
        return r3;
    L_0x0004:
        r2 = 0;
        if (r5 == 0) goto L_0x0089;
    L_0x0007:
        r1 = r4.getClass();
        r0 = r5.getClass();
        if (r1 == r0) goto L_0x0012;
    L_0x0011:
        goto L_0x0089;
    L_0x0012:
        r5 = (com.instagram.reels.p725d.p726a.C5179d) r5;
        r1 = r4.f20860a;
        r0 = r5.f20860a;
        if (r1 == r0) goto L_0x001b;
    L_0x001a:
        return r2;
    L_0x001b:
        r1 = r5.f20862c;
        r0 = r4.f20862c;
        r0 = java.lang.Float.compare(r1, r0);
        if (r0 == 0) goto L_0x0026;
    L_0x0025:
        return r2;
    L_0x0026:
        r0 = r4.backgroundColorf20864e;
        if (r0 == 0) goto L_0x0035;
    L_0x002a:
        r1 = r4.backgroundColorf20864e;
        r0 = r5.backgroundColorf20864e;
        r0 = r1.equals(r0);
        if (r0 != 0) goto L_0x003a;
    L_0x0034:
        goto L_0x0039;
    L_0x0035:
        r0 = r5.backgroundColorf20864e;
        if (r0 == 0) goto L_0x003a;
    L_0x0039:
        return r2;
    L_0x003a:
        r0 = r4.f20865f;
        if (r0 == 0) goto L_0x0049;
    L_0x003e:
        r1 = r4.f20865f;
        r0 = r5.f20865f;
        r0 = r1.equals(r0);
        if (r0 != 0) goto L_0x004e;
    L_0x0048:
        goto L_0x004d;
    L_0x0049:
        r0 = r5.f20865f;
        if (r0 == 0) goto L_0x004e;
    L_0x004d:
        return r2;
    L_0x004e:
        r0 = r4.f20866g;
        if (r0 == 0) goto L_0x005d;
    L_0x0052:
        r1 = r4.f20866g;
        r0 = r5.f20866g;
        r0 = r1.equals(r0);
        if (r0 != 0) goto L_0x0062;
    L_0x005c:
        goto L_0x0061;
    L_0x005d:
        r0 = r5.f20866g;
        if (r0 == 0) goto L_0x0062;
    L_0x0061:
        return r2;
    L_0x0062:
        r0 = r4.f20867h;
        if (r0 == 0) goto L_0x0071;
    L_0x0066:
        r1 = r4.f20867h;
        r0 = r5.f20867h;
        r0 = r1.equals(r0);
        if (r0 != 0) goto L_0x0076;
    L_0x0070:
        goto L_0x0075;
    L_0x0071:
        r0 = r5.f20867h;
        if (r0 == 0) goto L_0x0076;
    L_0x0075:
        return r2;
    L_0x0076:
        r0 = r4.f20868i;
        if (r0 == 0) goto L_0x0083;
    L_0x007a:
        r1 = r4.f20868i;
        r0 = r5.f20868i;
        r0 = r1.equals(r0);
        return r0;
    L_0x0083:
        r0 = r5.f20868i;
        if (r0 != 0) goto L_0x0088;
    L_0x0087:
        return r3;
    L_0x0088:
        return r2;
    L_0x0089:
        return r2;
        */
    }

    public final int hashCode() {
        int i = 0;
        int floatToIntBits = 31;// * ((((((((((this.f20860a * 31) + ((this.f20862c != 0.0f) ? Float.floatToIntBits(this.f20862c) : 0)) * 31) + ((this.backgroundColorf20864e != null) ? this.backgroundColorf20864e.hashCode() : 0)) * 31) + ((this.f20865f != null) ? this.f20865f.hashCode() : 0)) * 31) + ((this.f20866g != null) ? this.f20866g.hashCode() : 0)) * 31) + ((this.f20867h != null) ? this.f20867h.hashCode() : 0));
        if (this.f20868i != null) {
            i = this.f20868i.hashCode();
        }
        return floatToIntBits + i;
    }
}