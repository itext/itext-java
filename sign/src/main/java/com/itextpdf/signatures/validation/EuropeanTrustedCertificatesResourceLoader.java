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
package com.itextpdf.signatures.validation;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.eutrustedlistsresources.EuropeanTrustedListConfiguration;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Loads the certificates as published as part of the european journal publication "Information related to data on
 * Member States' trusted lists as notified under Commission Implementing Decision (EU) 2015/1505"
 */
class EuropeanTrustedCertificatesResourceLoader {

    private final EuropeanTrustedListConfiguration configuration;

    /**
     * Creates a new instance of {@link  EuropeanTrustedCertificatesResourceLoader}
     */
    EuropeanTrustedCertificatesResourceLoader(EuropeanTrustedListConfiguration config) {
        this.configuration = config;
    }

    static void verifyCertificate(
            String hashB64Encoded, Certificate certificate)
            throws CertificateException, NoSuchAlgorithmException {
        if (hashB64Encoded == null) {
            throw new PdfException(SignExceptionMessageConstant.CERTIFICATE_HASH_NULL);
        }
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] certHash = digest.digest(certificate.getEncoded());
        if (!MessageDigest.isEqual(certHash, Base64.getDecoder().decode(hashB64Encoded))) {
            if (certificate instanceof X509Certificate) {
                X509Certificate x509Certificate = (X509Certificate) certificate;
                throw new PdfException(MessageFormatUtil.format(SignExceptionMessageConstant.CERTIFICATE_HASH_MISMATCH,
                        x509Certificate.getIssuerX500Principal().getName()));
            }
            throw new PdfException(MessageFormatUtil.format(
                    SignExceptionMessageConstant.CERTIFICATE_HASH_MISMATCH, hashB64Encoded));
        }
    }

    /**
     * Loads the certificates from the European Trusted List configuration.
     * And verifies if they match the expected SHA-256 hash.
     *
     * @return A list of X509Certificates.
     */
    public List<Certificate> loadCertificates() {
        final ArrayList<Certificate> result = new ArrayList<>();
        for (EuropeanTrustedListConfiguration.PemCertificateWithHash pemContainer : configuration.getCertificates()) {
            Certificate certificate = CertificateUtil.readCertificatesFromPem(
                    new ByteArrayInputStream(pemContainer.getPemCertificate().getBytes(
                            StandardCharsets.UTF_8)))[0];
            result.add(certificate);
            try {
                verifyCertificate(pemContainer.getHash(), certificate);
            } catch (CertificateException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);

            }
        }
        return result;
    }
}


