package com.itextpdf.io.font.woff2;

public class Woff2Converter {

    public static boolean isWoff2Font(byte[] woff2Bytes) {
        if (woff2Bytes.length < 4) {
            return false;
        }
        Buffer file = new Buffer(woff2Bytes, 0,4);
        try {
            return file.readInt() == Woff2Common.kWoff2Signature;
        } catch (Exception any) {
            return false;
        }
    }

    public static byte[] convert(byte[] woff2Bytes) {
        byte[] out = new byte[Woff2Dec.computeWOFF2FinalSize(woff2Bytes, woff2Bytes.length)];
        Woff2Dec.convertWOFF2ToTTF(out, out.length, woff2Bytes, woff2Bytes.length);
        return out;
    }
}
