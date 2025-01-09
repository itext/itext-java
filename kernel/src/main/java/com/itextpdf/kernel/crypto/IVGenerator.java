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

import com.itextpdf.commons.utils.SystemUtil;

import java.nio.charset.StandardCharsets;

/**
 * An initialization vector generator for a CBC block encryption. It's a random generator based on ARCFOUR.
 */
public final class IVGenerator {

    private static final ARCFOUREncryption arcfour;

    static {
        arcfour = new ARCFOUREncryption();
        long time = SystemUtil.getTimeBasedSeed();
        long mem = SystemUtil.getFreeMemory();
        String s = time + "+" + mem;
        arcfour.prepareARCFOURKey(s.getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     * Creates a new instance of IVGenerator
     */
    private IVGenerator() {
    }

    /**
     * Gets a 16 byte random initialization vector.
     *
     * @return a 16 byte random initialization vector
     */
    public static byte[] getIV() {
        return getIV(16);
    }

    /**
     * Gets a random initialization vector.
     *
     * @param len the length of the initialization vector
     * @return a random initialization vector
     */
    public static byte[] getIV(int len) {
        byte[] b = new byte[len];
        synchronized (arcfour) {
            arcfour.encryptARCFOUR(b);
        }
        return b;
    }
}
