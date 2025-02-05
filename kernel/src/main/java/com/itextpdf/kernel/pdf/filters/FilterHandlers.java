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
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.pdf.PdfName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates filter behavior for PDF streams.  Classes generally interace with this
 * using the static getDefaultFilterHandlers() method, then obtain the desired {@link IFilterHandler}
 * via a lookup.
 */
// Dev note:  we eventually want to refactor PdfReader so all of the existing filter functionality is moved into this class
// it may also be better to split the sub-classes out into a separate package
public final class FilterHandlers {

    /**
     * The default {@link IFilterHandler}s used by iText
     */
    private static final Map<PdfName, IFilterHandler> defaults;

    static {
        Map<PdfName, IFilterHandler> map = new HashMap<>();

        map.put(PdfName.FlateDecode, new FlateDecodeFilter());
        map.put(PdfName.Fl, new FlateDecodeFilter());
        map.put(PdfName.ASCIIHexDecode, new ASCIIHexDecodeFilter());
        map.put(PdfName.AHx, new ASCIIHexDecodeFilter());
        map.put(PdfName.ASCII85Decode, new ASCII85DecodeFilter());
        map.put(PdfName.A85, new ASCII85DecodeFilter());
        map.put(PdfName.LZWDecode, new LZWDecodeFilter());
        map.put(PdfName.CCITTFaxDecode, new CCITTFaxDecodeFilter());
        map.put(PdfName.Crypt, new DoNothingFilter());
        map.put(PdfName.RunLengthDecode, new RunLengthDecodeFilter());
        map.put(PdfName.DCTDecode, new DctDecodeFilter());
        map.put(PdfName.JPXDecode, new JpxDecodeFilter());

        defaults = Collections.unmodifiableMap(map);
    }

    /**
     * @return the default {@link IFilterHandler}s used by iText
     */
    public static Map<PdfName, IFilterHandler> getDefaultFilterHandlers() {
        return defaults;
    }
}
