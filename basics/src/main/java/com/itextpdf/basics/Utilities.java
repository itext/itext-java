package com.itextpdf.basics;

import java.io.IOException;
import java.io.InputStream;

public class Utilities {

    /**
     * This method is an alternative for the <CODE>InputStream.skip()</CODE>
     * -method that doesn't seem to work properly for big values of <CODE>size
     * </CODE>.
     *
     * @param is   the <CODE>InputStream</CODE>
     * @param size the number of bytes to skip
     * @throws java.io.IOException
     */
    static public void skip(final InputStream is, int size) throws IOException {
        long n;
        while (size > 0) {
            n = is.skip(size);
            if (n <= 0)
                break;
            size -= n;
        }
    }


}
