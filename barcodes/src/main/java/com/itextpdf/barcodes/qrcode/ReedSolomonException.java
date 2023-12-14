/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
 * Thrown when an exception occurs during Reed-Solomon decoding, such as when
 * there are too many errors to correct.
 *
 * @author Sean Owen
 */
final class ReedSolomonException extends Exception {

    private static final long serialVersionUID = 2168232776886684292L;

    /**
     * Creates a ReedSolomonException with a message.
     *
     * @param message the message of the exception
     */
    public ReedSolomonException(String message) {
        super(message);
    }

}
