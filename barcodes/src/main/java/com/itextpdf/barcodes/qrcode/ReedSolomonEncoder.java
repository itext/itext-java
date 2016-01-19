package com.itextpdf.barcodes.qrcode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Implements Reed-Solomon encoding, as the name implies.</p>
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
