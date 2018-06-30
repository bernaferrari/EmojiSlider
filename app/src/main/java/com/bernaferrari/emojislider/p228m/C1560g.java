package com.bernaferrari.emojislider.p228m;

import java.util.HashMap;
import java.util.Map;

public final class C1560g {
    public static final C1560g f6954a = new C1560g();
    private final Map<C1559f, String> f6955b = new HashMap();

    private C1560g() {
        m4039a(C1559f.f6951c, "default config");
    }

    public final boolean m4039a(C1559f c1559f, String str) {
        if (c1559f == null) {
            throw new IllegalArgumentException("springConfig is required");
        } else if (str == null) {
            throw new IllegalArgumentException("configName is required");
        } else if (this.f6955b.containsKey(c1559f)) {
            return false;
        } else {
            this.f6955b.put(c1559f, str);
            return true;
        }
    }
}
