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
package com.itextpdf.io.font;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.commons.utils.FileUtil;

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
            throw new IOException(IoExceptionMessageConstant.FONT_FILE_NOT_FOUND).setMessageParams(ttcPath);
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
     * @throws java.io.IOException in case TTC index does not exist in this TTC file
     */
    public FontProgram getFontByTccIndex(int ttcIndex) throws java.io.IOException {
        if (ttcIndex > TTCSize - 1) {
            throw new IOException(IoExceptionMessageConstant.TTC_INDEX_DOESNT_EXIST_IN_THIS_TTC_FILE);
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
            throw new IOException(IoExceptionMessageConstant.INVALID_TTC_FILE);
        }
        raf.skipBytes(4);
        TTCSize = raf.readInt();
    }
}
