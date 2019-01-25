package com.marksmile.utils;

public class UUIDUtil {

    public static String randomUUID() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        return (digits(mostSigBits >> 32, 8) + digits(mostSigBits >> 16, 4)
                + digits(mostSigBits, 4) + digits(leastSigBits >> 48, 4)
                + digits(leastSigBits, 12));
    }

    /** Returns val represented by the specified number of hex digits. */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

    public static void main(String[] args) {

        System.out.println(UUIDUtil.randomUUID());
        System.out.println(UUIDUtil.randomUUID().length());
    }

}
