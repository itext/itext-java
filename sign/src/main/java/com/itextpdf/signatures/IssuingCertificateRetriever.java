/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.v1.TrustedCertificatesStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link IIssuingCertificateRetriever} default implementation.
 */
public class IssuingCertificateRetriever implements IIssuingCertificateRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssuingCertificateRetriever.class);

    private final TrustedCertificatesStore trustedCertificatesStore = new TrustedCertificatesStore();
    private final Map<String, Certificate> knownCertificates = new HashMap<>();

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
        X509Certificate lastAddedCert = signingCertificate;
        while (!CertificateUtil.isSelfSigned(lastAddedCert)) {
            // Check if there are any missing certificates with isSignedByNext
            if (i < chain.length &&
                    CertificateUtil.isIssuerCertificate(lastAddedCert, (X509Certificate) chain[i])) {
                fullChain.add(chain[i]);
                i++;
            } else {
                // Get missing certificates using AIA Extensions
                String url = CertificateUtil.getIssuerCertURL(lastAddedCert);
                Collection<Certificate> certificatesFromAIA = processCertificatesFromAIA(url);
                if (certificatesFromAIA == null || certificatesFromAIA.isEmpty()) {
                    // Retrieve Issuer from the certificate store
                    Certificate issuer = trustedCertificatesStore
                            .getKnownCertificate(lastAddedCert.getIssuerX500Principal().getName());
                    if (issuer == null) {
                        issuer = knownCertificates.get(lastAddedCert.getIssuerX500Principal().getName());
                        if (issuer == null) {
                            // Unable to retrieve missing certificates
                            while (i < chain.length) {
                                fullChain.add(chain[i]);
                                i++;
                            }
                            return fullChain.toArray(new Certificate[0]);
                        }
                    }
                    fullChain.add(issuer);
                } else {
                    fullChain.addAll(certificatesFromAIA);
                }
            }
            lastAddedCert = (X509Certificate) fullChain.get(fullChain.size() - 1);
        }

        return fullChain.toArray(new Certificate[0]);
    }

    /**
     * Retrieve issuer certificate for the provided certificate.
     *
     * @param certificate {@link Certificate} for which issuer certificate shall be retrieved
     *
     * @return issuer certificate. {@code null} if there is no issuer certificate, or it cannot be retrieved.
     */
    public Certificate retrieveIssuerCertificate(Certificate certificate) {
        Certificate[] certificateChain = retrieveMissingCertificates(new Certificate[]{certificate});
        if (certificateChain.length > 1) {
            return certificateChain[1];
        }
        return null;
    }

    /**
     * Retrieves OCSP responder certificate either from the response certs or
     * trusted store in case responder certificate isn't found in /Certs.
     *
     * @param ocspResp basic OCSP response to get responder certificate for
     *
     * @return retrieved OCSP responder certificate or null in case it wasn't found.
     */
    public Certificate retrieveOCSPResponderCertificate(IBasicOCSPResp ocspResp) {
        // Look for the existence of an Authorized OCSP responder inside the cert chain in the ocsp response.
        Iterable<X509Certificate> certs = SignUtils.getCertsFromOcspResponse(ocspResp);
        for (X509Certificate cert : certs) {
            try {
                if (CertificateUtil.isSignatureValid(ocspResp, cert)) {
                    return cert;
                }
            } catch (Exception ignored) {
                // Ignore.
            }
        }
        // Certificate chain is not present in the response.
        // Try to verify using trusted store according to RFC 6960 2.2. Response:
        // "The key used to sign the response MUST belong to one of the following:
        // - ...
        // - a Trusted Responder whose public key is trusted by the requester;
        // - ..."
        try {
            for (Certificate anchor : trustedCertificatesStore.getAllTrustedCertificates()) {
                if (CertificateUtil.isSignatureValid(ocspResp, anchor)) {
                    // Certificate from the root store is considered trusted and valid by this method.
                    return anchor;
                }
            }
        } catch (Exception ignored) {
            // Ignore.
        }
        return null;
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
        if (certificatesFromAIA == null) {
            // Retrieve Issuer from the certificate store
            Certificate issuer = trustedCertificatesStore
                    .getKnownCertificate(((X509CRL) crl).getIssuerX500Principal().getName());
            if (issuer == null) {
                issuer = knownCertificates.get(((X509CRL) crl).getIssuerX500Principal().getName());
                if (issuer == null) {
                    // Unable to retrieve CRL issuer
                    return new Certificate[0];
                }
            }
            return retrieveMissingCertificates(new Certificate[]{issuer});
        }
        return retrieveMissingCertificates(certificatesFromAIA.toArray(new Certificate[0]));
    }

    /**
     * Sets trusted certificate list to be used as certificates trusted for any possible usage.
     * In case more specific trusted is desired to be configured
     * {@link IssuingCertificateRetriever#getTrustedCertificatesStore()} method is expected to be used.
     *
     * @param certificates certificate list to be used as certificates trusted for any possible usage.
     */
    @Override
    public void setTrustedCertificates(Collection<Certificate> certificates) {
        addTrustedCertificates(certificates);
    }

    /**
     * Add trusted certificates collection to trusted certificates storage.
     *
     * @param certificates certificates {@link Collection} to be added
     */
    public void addTrustedCertificates(Collection<Certificate> certificates) {
        trustedCertificatesStore.addGenerallyTrustedCertificates(certificates);
    }

    /**
     * Add certificates collection to known certificates storage, which is used for issuer certificates retrieval.
     *
     * @param certificates certificates {@link Collection} to be added
     */
    public void addKnownCertificates(Collection<Certificate> certificates) {
        for (Certificate certificate : certificates) {
            knownCertificates.put(((X509Certificate) certificate).getSubjectX500Principal().getName(), certificate);
        }
    }

    /**
     * Gets {@link TrustedCertificatesStore} to be used to provide more complex trusted certificates configuration.
     *
     * @return {@link TrustedCertificatesStore} storage
     */
    public TrustedCertificatesStore getTrustedCertificatesStore() {
        return trustedCertificatesStore;
    }

    /**
     * Check if provided certificate is present in trusted certificates storage.
     *
     * @param certificate {@link Certificate} to be checked
     *
     * @return {@code true} if certificate is present in trusted certificates storage, {@code false} otherwise
     */
    public boolean isCertificateTrusted(Certificate certificate) {
        return trustedCertificatesStore.isCertificateGenerallyTrusted(certificate);
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
