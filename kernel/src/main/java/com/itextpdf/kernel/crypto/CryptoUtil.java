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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encoding;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * This file is a helper class for internal usage only.
 * Be aware that it's API and functionality may be changed in the future.
 */
public class CryptoUtil {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private CryptoUtil() {
        // Empty constructor.
    }

    public static Certificate readPublicCertificate(InputStream is) throws CertificateException {
        return CertificateFactory.getInstance("X.509").generateCertificate(is);
    }

    /**
     * Creates {@link IASN1OutputStream} instance and asserts for unexpected ASN1 encodings.
     *
     * @param outputStream the underlying stream
     * @param asn1Encoding ASN1 encoding that will be used for writing. Only DER and BER are allowed as values.
     *                     See also {@link IASN1Encoding}.
     *
     * @return an {@link IASN1OutputStream} instance. Exact stream implementation is chosen based on passed encoding.
     */
    public static IASN1OutputStream createAsn1OutputStream(OutputStream outputStream, String asn1Encoding) {
        if (!BOUNCY_CASTLE_FACTORY.createASN1Encoding().getDer().equals(asn1Encoding) &&
                !BOUNCY_CASTLE_FACTORY.createASN1Encoding().getBer().equals(asn1Encoding)) {
            throw new UnsupportedOperationException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.UNSUPPORTED_ASN1_ENCODING, asn1Encoding)
            );
        }
        return BOUNCY_CASTLE_FACTORY.createASN1OutputStream(outputStream, asn1Encoding);
    }

    static MessageDigest getMessageDigest(String hashAlgorithm, String provider)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.startsWith("SunPKCS11") || provider.startsWith("SunMSCAPI")) {
            return MessageDigest.getInstance(DigestAlgorithms.normalizeDigestName(hashAlgorithm));
        } else {
            return MessageDigest.getInstance(hashAlgorithm, provider);
        }
    }
}
