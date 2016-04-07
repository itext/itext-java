package com.itextpdf.io.util;

import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

public final class EncodingUtil {

    private EncodingUtil() {
    }

    public static byte[] convertToBytes(char[] chars, String encoding) throws CharacterCodingException {
        Charset cc = Charset.forName(encoding);
        CharsetEncoder ce = cc.newEncoder();
        ce.onUnmappableCharacter(CodingErrorAction.IGNORE);
        java.nio.ByteBuffer bb = ce.encode(CharBuffer.wrap(chars));
        bb.rewind();
        int lim = bb.limit();
        byte[] br = new byte[lim];
        bb.get(br);
        return br;
    }
}
