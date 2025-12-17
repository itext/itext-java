/* Copyright 2016 Google Inc. All Rights Reserved.

   Distributed under MIT license.
   See file LICENSE for detail or copy at https://opensource.org/licenses/MIT
*/

package com.itextpdf.io.codec.brotli.dec;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

/**
 * Common utility methods.
 */
public final class TestUtils {

    public static InputStream newBrotliInputStream(InputStream input) throws IOException {
        String brotliClass = System.getProperty("BROTLI_INPUT_STREAM");
        if (brotliClass == null) {
            return new BrotliInputStream(input);
        }
        try {
            Class<?> clazz = Class.forName(brotliClass);
            Constructor<?> ctor = clazz.getConstructor(InputStream.class);
            return (InputStream) ctor.newInstance(new Object[] { input });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] readUniBytes(String uniBytes) {
        byte[] result = new byte[uniBytes.length()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (byte) uniBytes.charAt(i);
        }
        return result;
    }

    private TestUtils() {}
}
