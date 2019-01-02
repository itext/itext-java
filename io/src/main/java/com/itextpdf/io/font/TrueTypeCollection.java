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
package com.itextpdf.io.font;

import com.itextpdf.io.IOException;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.util.FileUtil;

/**
 * Use this class for working with true type collection font (*.ttc)
 */
public class TrueTypeCollection {

    protected RandomAccessFileOrArray raf;
    private int TTCSize = 0;
    private String ttcPath;
    private byte[] ttc;
    private boolean cached = true;

    /**
     * Creates a new {@link TrueTypeCollection} instance by its bytes.
     *
     * @param ttc the byte contents of the collection
     * @throws java.io.IOException in case the input in mal-formatted
     */
    public TrueTypeCollection(byte[] ttc) throws java.io.IOException {
        raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(ttc));
        this.ttc = ttc;
        initFontSize();
    }

    /**
     * Creates a new {@link TrueTypeCollection} instance by its file path.
     *
     * @param ttcPath the path of the collection
     * @throws java.io.IOException in case the input in mal-formatted
     */
    public TrueTypeCollection(String ttcPath) throws java.io.IOException {
        if (!FileUtil.fileExists(ttcPath)) {
            throw new IOException(IOException.FontFile1NotFound).setMessageParams(ttcPath);
        }
        raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(ttcPath));
        this.ttcPath = ttcPath;
        initFontSize();
    }

    /**
     * method return TrueTypeFont by ttc index
     *
     * @param ttcIndex the index for the TTC font
     * @return TrueTypeFont
     */
    public FontProgram getFontByTccIndex(int ttcIndex) throws java.io.IOException {
        if (ttcIndex > TTCSize - 1) {
            throw new IOException(IOException.TtcIndexDoesNotExistInThisTtcFile);
        }

        if (ttcPath != null) {
            return FontProgramFactory.createFont(ttcPath, ttcIndex, cached);
        } else {
            return FontProgramFactory.createFont(ttc, ttcIndex, cached);
        }
    }

    /**
     * returns the number of fonts in True Type Collection (file or bytes array)
     *
     * @return returns the number of fonts
     */
    public int getTTCSize() {
        return TTCSize;
    }

    /**
     * Indicates if fonts created by the call to {@link #getFontByTccIndex(int)} will be cached or not.
     *
     * @return <code>true</code> if the created fonts will be cached, <code>false</code> otherwise
     */
    public boolean isCached() {
        return cached;
    }

    /**
     * Sets if fonts created by the call to {@link #getFontByTccIndex(int)} will be cached or not.
     *
     * @param cached <code>true</code> if the created fonts will be cached, <code>false</code> otherwise
     */
    public void setCached(boolean cached) {
        this.cached = cached;
    }

    private void initFontSize() throws java.io.IOException {
        String mainTag = raf.readString(4, PdfEncodings.WINANSI);
        if (!mainTag.equals("ttcf")) {
            throw new IOException(IOException.InvalidTtcFile);
        }
        raf.skipBytes(4);
        TTCSize = raf.readInt();
    }
}
