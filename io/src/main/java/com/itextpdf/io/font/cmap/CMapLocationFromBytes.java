/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.io.font.cmap;

import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;

/**
 * Supplies a CMap tokenizer backed by an in-memory byte array.
 */
public class CMapLocationFromBytes implements ICMapLocation {

    private byte[] data;

    /**
     * Creates a location backed by the provided CMap bytes.
     *
     * @param data the CMap source bytes; retained without copying
     */
    public CMapLocationFromBytes(byte[] data) {
        this.data = data;
    }

    /**
     * Creates a tokenizer for the retained CMap bytes.
     *
     * @param location ignored because this implementation has one in-memory source
     *
     * @return a new tokenizer over the retained bytes
     */
    public PdfTokenizer getLocation(String location) {
        return new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(data)));
    }
}
