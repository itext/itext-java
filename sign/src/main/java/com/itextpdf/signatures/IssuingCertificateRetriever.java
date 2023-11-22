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
package com.itextpdf.signatures;

import com.itextpdf.signatures.logs.SignLogMessageConstant;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IIssuingCertificateRetriever} default implementation.
 */
public class IssuingCertificateRetriever implements IIssuingCertificateRetriever {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(IssuingCertificateRetriever.class);

    /**
     * Creates {@link IssuingCertificateRetriever} instance.
     */
    public IssuingCertificateRetriever() {
        // Empty constructor.
    }

    /**
     * {@inheritDoc}
     *
     * @param chain {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Certificate[] retrieveMissingCertificates(Certificate[] chain) {
        List<Certificate> fullChain = new ArrayList<>();
        X509Certificate signingCertificate = (X509Certificate) chain[0];
        fullChain.add(signingCertificate);

        int i = 1;
        Certificate lastAddedCert = signingCertificate;
        while (!CertificateUtil.isSelfSigned((X509Certificate) lastAddedCert)) {
            //  Check if there are any missing certificates with isSignedByNext
            if (i < chain.length &&
                    CertificateUtil.isIssuerCertificate((X509Certificate) lastAddedCert, (X509Certificate) chain[i])) {
                fullChain.add(chain[i]);
                i++;
            } else {
                // Get missing certificates using AIA Extensions
                String url = CertificateUtil.getIssuerCertURL((X509Certificate) lastAddedCert);
                Collection<Certificate> certificatesFromAIA = processCertificatesFromAIA(url);
                if (certificatesFromAIA == null || certificatesFromAIA.isEmpty()) {
                    // Unable to retrieve missing certificates
                    while (i < chain.length) {
                        fullChain.add(chain[i]);
                        i++;
                    }
                    return fullChain.toArray(new Certificate[0]);
                }
                fullChain.addAll(certificatesFromAIA);
            }
            lastAddedCert = fullChain.get(fullChain.size() - 1);
        }

        return fullChain.toArray(new Certificate[0]);
    }

    /**
     * {@inheritDoc}
     *
     * @param crl {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Certificate[] getCrlIssuerCertificates(CRL crl) {
        // Usually CRLs are signed using CA certificate, so we don’t need to do anything extra and the revocation data
        // is already collected. However, it is possible to sign it with any other certificate.

        // IssuingDistributionPoint extension: https://datatracker.ietf.org/doc/html/rfc5280#section-5.2.5
        // Nothing special for the indirect CRLs.

        // AIA Extension
        String url = CertificateUtil.getIssuerCertURL(crl);
        List<Certificate> certificatesFromAIA = (List<Certificate>) processCertificatesFromAIA(url);
        return certificatesFromAIA == null ? new Certificate[0] :
                retrieveMissingCertificates(certificatesFromAIA.toArray(new Certificate[0]));
    }

    /**
     * Get CA issuers certificates represented as {@link InputStream}.
     *
     * @param uri {@link URL} URI, which is expected to be used to get issuer certificates from. Usually
     *            CA Issuers value from Authority Information Access (AIA) certificate extension.
     *
     * @return CA issuer certificate (or chain) bytes, represented as {@link InputStream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    protected InputStream getIssuerCertByURI(String uri) throws IOException {
        return SignUtils.getHttpResponse(new URL(uri));
    }

    /**
     * Parses certificates represented as byte array.
     *
     * @param certsData stream which contains one or more X509 certificates.
     *
     * @return a (possibly empty) collection of the certificates read from the given byte array.
     *
     * @throws CertificateException if parsing error occurs.
     */
    protected Collection<Certificate> parseCertificates(InputStream certsData) throws CertificateException {
        return SignUtils.readAllCerts(certsData, null);
    }

    private Collection<Certificate> processCertificatesFromAIA(String url) {
        if (url == null) {
            // We don't have any URIs to the issuer certificates in AuthorityInfoAccess extension
            return null;
        }
        try (InputStream missingCertsData = getIssuerCertByURI(url)) {
            return parseCertificates(missingCertsData);
        } catch (Exception e) {
            LOGGER.warn(SignLogMessageConstant.UNABLE_TO_PARSE_AIA_CERT);
            return null;
        }
    }
}
