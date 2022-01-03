/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.kernel.crypto;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1OutputStream;

/**
 * This file is a helper class for internal usage only.
 * Be aware that it's API and functionality may be changed in the future.
 */
public class CryptoUtil {

    private CryptoUtil() {

    }

    public static Certificate readPublicCertificate(InputStream is) throws CertificateException {
        return CertificateFactory.getInstance("X.509").generateCertificate(is);
    }

    public static PrivateKey readPrivateKeyFromPKCS12KeyStore(InputStream keyStore, String pkAlias, char[] pkPassword) throws GeneralSecurityException, IOException {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(keyStore, pkPassword);
        return (PrivateKey) keystore.getKey(pkAlias, pkPassword);
    }

    /**
     * Creates {@link ASN1OutputStream} instance and asserts for unexpected ASN1 encodings.
     *
     * @param outputStream the underlying stream
     * @param asn1Encoding ASN1 encoding that will be used for writing. Only DER and BER are allowed as values.
     *                     See also {@link ASN1Encoding}.
     *
     * @return an {@link ASN1OutputStream} instance. Exact stream implementation is chosen based on passed encoding.
     */
    public static ASN1OutputStream createAsn1OutputStream(OutputStream outputStream, String asn1Encoding) {
        if (!ASN1Encoding.DER.equals(asn1Encoding) && !ASN1Encoding.BER.equals(asn1Encoding)) {
            throw new UnsupportedOperationException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.UNSUPPORTED_ASN1_ENCODING, asn1Encoding)
            );
        }
        return ASN1OutputStream.create(outputStream, asn1Encoding);
    }
}
