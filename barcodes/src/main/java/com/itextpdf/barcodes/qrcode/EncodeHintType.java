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
package com.itextpdf.barcodes.qrcode;

/**
 * These are a set of hints that you may pass to Writers to specify their behavior.
 */
public final class EncodeHintType {

    /**
     * Specifies what degree of error correction to use, for example in QR Codes (type Integer).
     */
    public static final EncodeHintType ERROR_CORRECTION = new EncodeHintType();

    /**
     * Specifies what character encoding to use where applicable (type String)
     */
    public static final EncodeHintType CHARACTER_SET = new EncodeHintType();

    /**
    * Specifies the minimal version level to use, for example in QR Codes (type Integer).
    */
    public static final EncodeHintType MIN_VERSION_NR = new EncodeHintType();

    private EncodeHintType() {
    }

}
