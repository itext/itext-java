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
package com.itextpdf.kernel.crypto;

public class StandardDecryptor implements IDecryptor {

    protected ARCFOUREncryption arcfour;

    /**
     * Creates a new instance of StandardDecryption
     *
     * @param key data to be written
     * @param off the start offset in data
     * @param len number of bytes to write
     */
    public StandardDecryptor(byte[] key, int off, int len) {
        arcfour = new ARCFOUREncryption();
        arcfour.prepareARCFOURKey(key, off, len);
    }

    public byte[] update(byte[] b, int off, int len) {
        byte[] b2 = new byte[len];
        arcfour.encryptARCFOUR(b, off, len, b2, 0);
        return b2;
    }

    public byte[] finish() {
        return null;
    }
}
