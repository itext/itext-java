package com.itextpdf.io.util;

import com.itextpdf.io.IOException;
import com.itextpdf.io.codec.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public final class FilterUtil {

    private FilterUtil() {
    }

    /**
     * A helper to FlateDecode.
     *
     * @param input     the input data
     * @param strict <CODE>true</CODE> to read a correct stream. <CODE>false</CODE>
     *               to try to read a corrupted stream
     * @return the decoded data
     */
    public static byte[] flateDecode(byte[] input, boolean strict) {
        ByteArrayInputStream stream = new ByteArrayInputStream(input);
        InflaterInputStream zip = new InflaterInputStream(stream);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] b = new byte[strict ? 4092 : 1];
        try {
            int n;
            while ((n = zip.read(b)) >= 0) {
                output.write(b, 0, n);
            }
            zip.close();
            output.close();
            return output.toByteArray();
        } catch (Exception e) {
            return strict ? null : output.toByteArray();
        }
    }

    /**
     * Decodes a stream that has the FlateDecode filter.
     *
     * @param input the input data
     * @return the decoded data
     */
    public static byte[] flateDecode(byte[] input) {
        byte[] b = flateDecode(input, true);
        if (b == null)
            return flateDecode(input, false);
        return b;
    }

    /**
     * This method provides support for general purpose decompression using the
     * popular ZLIB compression library.
     * @param deflated the input data bytes
     * @param inflated the buffer for the uncompressed data
     */
    public static void inflateData(byte[] deflated, byte[] inflated) {
        Inflater inflater = new Inflater();
        inflater.setInput(deflated);
        try {
            inflater.inflate(inflated);
        } catch (DataFormatException dfe) {
            throw new IOException(IOException.CannotInflateTiffImage);
        }
    }

    public static InputStream getInflaterInputStream(InputStream input) {
        return new InflaterInputStream(input, new Inflater());
    }
}
