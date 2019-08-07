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
package com.itextpdf.barcodes.qrcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements Reed-Solomon encoding, as the name implies.
 *
 * @author Sean Owen
 * @author William Rucklidge
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
