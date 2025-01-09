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

/**
 * Classes implementing this interface can create custom encodings or
 * replace existing ones. It is used in the context of <code>PdfEncoding</code>.
 */
public interface IExtraEncoding {

    /**
     * Converts an Unicode string to a byte array according to some encoding.
     * @param text the Unicode string
     * @param encoding the requested encoding. It's mainly of use if the same class
     * supports more than one encoding.
     * @return the conversion or <CODE>null</CODE> if no conversion is supported
     */
    byte[] charToByte(String text, String encoding);

    /**
     * Converts an Unicode char to a byte array according to some encoding.
     * @param char1 the Unicode char
     * @param encoding the requested encoding. It's mainly of use if the same class
     * supports more than one encoding.
     * @return the conversion or <CODE>null</CODE> if no conversion is supported
     */
    byte[] charToByte(char char1, String encoding);

    /**
     * Converts a byte array to an Unicode string according to some encoding.
     * @param b the input byte array
     * @param encoding the requested encoding. It's mainly of use if the same class
     * supports more than one encoding.
     * @return the conversion or <CODE>null</CODE> if no conversion is supported
     */
    String byteToChar(byte[] b, String encoding);
}
