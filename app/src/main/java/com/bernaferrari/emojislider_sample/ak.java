package com.bernaferrari.emojislider_sample;


public final class ak {
    final /* synthetic */ int f31413a;

    ak(int i) {
        this.f31413a = i;
    }

    public final String m17404a(int i) {
        if (i % this.f31413a != 0) {
            return null;
        }
        int i2 = i / 60;
        return "ARRRR";
//        if (i2 == 0) {
//            return StringFormatUtil.formatStrLocaleSafe(":%02d", Integer.valueOf(i % 60));
//        }
//        if (i2 < 10) {
//            return StringFormatUtil.formatStrLocaleSafe("%01d:%02d", Integer.valueOf(i2), Integer.valueOf(i % 60));
//        }
//        return StringFormatUtil.formatStrLocaleSafe("%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i % 60));
    }
}
