/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.util;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class FilterUtil {

    /** The Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterUtil.class);

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
        }finally {
            try {
                zip.close();
                output.close();
            }catch(Exception e){
                //Log the error
                LOGGER.error(e.getMessage(),e);
            }
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
            throw new IOException(IoExceptionMessageConstant.CANNOT_INFLATE_TIFF_IMAGE);
        }
    }

    public static InputStream getInflaterInputStream(InputStream input) {
        return new InflaterInputStream(input, new Inflater());
    }
}
