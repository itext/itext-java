/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.util;

import com.itextpdf.io.IOException;
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
            throw new IOException(IOException.CannotInflateTiffImage);
        }
    }

    public static InputStream getInflaterInputStream(InputStream input) {
        return new InflaterInputStream(input, new Inflater());
    }
}
