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

import java.util.ArrayList;
import java.util.List;

/**
 * Implements Reed-Solomon encoding, as the name implies.
 */
final class ReedSolomonEncoder {

    private final GF256 field;
    private final List<GF256Poly> cachedGenerators;

    /**
     * Creates a SolomonEncoder object based on a {@link com.itextpdf.barcodes.qrcode.GF256} object.
     * Only QR codes are supported at the moment.
     *
     * @param field the galois field
     */
    public ReedSolomonEncoder(GF256 field) {
        if (!GF256.QR_CODE_FIELD.equals(field)) {
            throw new UnsupportedOperationException("Only QR Code is supported at this time");
        }
        this.field = field;
        this.cachedGenerators = new ArrayList<>();
        cachedGenerators.add(new GF256Poly(field, new int[] { 1 }));
    }

    private GF256Poly buildGenerator(int degree) {
        if (degree >= cachedGenerators.size()) {
            GF256Poly lastGenerator = cachedGenerators.get(cachedGenerators.size() - 1);
            for (int d = cachedGenerators.size(); d <= degree; d++) {
                GF256Poly nextGenerator = lastGenerator.multiply(new GF256Poly(field, new int[] { 1, field.exp(d - 1) }));
                cachedGenerators.add(nextGenerator);
                lastGenerator = nextGenerator;
            }
        }
        return cachedGenerators.get(degree);
    }

    /**
     * Encodes the provided data.
     *
     * @param toEncode data to encode
     * @param ecBytes error correction bytes
     */
    public void encode(int[] toEncode, int ecBytes) {
        if (ecBytes == 0) {
            throw new IllegalArgumentException("No error correction bytes");
        }
        int dataBytes = toEncode.length - ecBytes;
        if (dataBytes <= 0) {
            throw new IllegalArgumentException("No data bytes provided");
        }
        GF256Poly generator = buildGenerator(ecBytes);
        int[] infoCoefficients = new int[dataBytes];
        System.arraycopy(toEncode, 0, infoCoefficients, 0, dataBytes);
        GF256Poly info = new GF256Poly(field, infoCoefficients);
        info = info.multiplyByMonomial(ecBytes, 1);
        GF256Poly remainder = info.divide(generator)[1];
        int[] coefficients = remainder.getCoefficients();
        int numZeroCoefficients = ecBytes - coefficients.length;
        for (int i = 0; i < numZeroCoefficients; i++) {
            toEncode[dataBytes + i] = 0;
        }
        System.arraycopy(coefficients, 0, toEncode, dataBytes + numZeroCoefficients, coefficients.length);
    }

}
