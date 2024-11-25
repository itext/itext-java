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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.TrustedCertificatesStore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(IssuingCertificateRetriever.class);

    private final TrustedCertificatesStore trustedCertificatesStore = new TrustedCertificatesStore();
    private final Map<String, List<Certificate>> knownCertificates = new HashMap<>();

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
                if (certificatesFromAIA != null) {
                    addKnownCertificates(certificatesFromAIA);
                }
                // Retrieve Issuer from the certificate store
                Certificate issuer = getIssuerFromCertificateSet(lastAddedCert, trustedCertificatesStore
                        .getKnownCertificates(lastAddedCert.getIssuerX500Principal().getName()));
                if (issuer == null || !isSignedBy(lastAddedCert, issuer)) {
                    issuer = getIssuerFromCertificateSet(lastAddedCert, knownCertificates.get(
                            lastAddedCert.getIssuerX500Principal().getName()));
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
            }
            lastAddedCert = (X509Certificate) fullChain.get(fullChain.size() - 1);
        }

        return fullChain.toArray(new Certificate[0]);
    }

    /**
     * This method tries to rebuild certificate issuer chain. The result contains all possible chains
     * starting with the given certificate based on issuer names and public keys.
     *
     * @param certificate {@link X509Certificate} for which issuer chains shall be built
     *
     * @return all possible issuer chains
     */
    public List<X509Certificate[]> buildCertificateChains(X509Certificate certificate) {
        return buildCertificateChains(new X509Certificate[] {certificate});
    }

    /**
     * This method tries to rebuild certificate issuer chain. The result contains all possible chains
     * starting with the given certificate array based on issuer names and public keys.
     *
     * @param certificate {@link X509Certificate} array for which issuer chains shall be built
     *
     * @return all possible issuer chains
     */
    public List<X509Certificate[]> buildCertificateChains(X509Certificate[] certificate) {
        List<List<X509Certificate>> allCertificateChains = buildCertificateChainsList(certificate);
        List<X509Certificate[]> result = new ArrayList<>(allCertificateChains.size() * 5);
        for (List<X509Certificate> chain : allCertificateChains) {
            Collections.reverse(chain);
            result.add(chain.toArray(new X509Certificate[0]));
        }
        return result;
    }

    private List<List<X509Certificate>> buildCertificateChainsList(X509Certificate[] certificates) {
        List<List<X509Certificate>> allChains =
                new ArrayList<>(buildCertificateChainsList(certificates[certificates.length - 1]));
        for (List<X509Certificate> issuerChain : allChains) {
            for (int i = certificates.length - 2; i >= 0; --i) {
                issuerChain.add(certificates[i]);
            }
        }
        return allChains;
    }

    private List<List<X509Certificate>> buildCertificateChainsList(X509Certificate certificate) {
        if (CertificateUtil.isSelfSigned(certificate)) {
            List<List<X509Certificate>> singleChain = new ArrayList<>();
            List<X509Certificate> chain = new ArrayList<>();
            chain.add(certificate);
            singleChain.add(chain);
            return singleChain;
        }

        List<List<X509Certificate>> allChains = new ArrayList<>();
        // Get missing certificates using AIA Extensions
        String url = CertificateUtil.getIssuerCertURL(certificate);
        Collection<Certificate> certificatesFromAIA = processCertificatesFromAIA(url);
        if (certificatesFromAIA != null) {
            addKnownCertificates(certificatesFromAIA);
        }
        Set<Certificate> possibleIssuers = trustedCertificatesStore
                .getKnownCertificates(certificate.getIssuerX500Principal().getName());
        if (knownCertificates.get(
                certificate.getIssuerX500Principal().getName()) != null) {
            possibleIssuers.addAll(knownCertificates.get(certificate.getIssuerX500Principal().getName()));
        }
        if (possibleIssuers.isEmpty()) {
            List<List<X509Certificate>> singleChain = new ArrayList<>();
            List<X509Certificate> chain = new ArrayList<>();
            chain.add(certificate);
            singleChain.add(chain);
            return singleChain;
        }
        for (Certificate possibleIssuer : possibleIssuers) {
            List<List<X509Certificate>> issuerChains = buildCertificateChainsList((X509Certificate) possibleIssuer);
            for (List<X509Certificate> issuerChain : issuerChains) {
                issuerChain.add(certificate);
                allChains.add(issuerChain);
            }
        }
        return allChains;
    }

    /**
     * Retrieve issuer certificate for the provided certificate.
     *
     * @param certificate {@link Certificate} for which issuer certificate shall be retrieved
     *
     * @return issuer certificate. {@code null} if there is no issuer certificate, or it cannot be retrieved.
     */
    public List<X509Certificate> retrieveIssuerCertificate(Certificate certificate) {
        List<X509Certificate> result = new ArrayList<>();
        for (X509Certificate[] certificateChain : buildCertificateChains((X509Certificate) certificate)) {
            if (certificateChain.length > 1) {
                result.add(certificateChain[1]);
            }
        }
        return result;
    }

    /**
     * Retrieves OCSP responder certificate candidates either from the response certs or
     * trusted store in case responder certificate isn't found in /Certs.
     *
     * @param ocspResp basic OCSP response to get responder certificate for
     *
     * @return retrieved OCSP responder candidates or an empty set in case none were found.
     */
    public Set<Certificate> retrieveOCSPResponderByNameCertificate(IBasicOCSPResp ocspResp) {

        String name = null;
        name = FACTORY.createX500Name( FACTORY.createASN1Sequence(
                ocspResp.getResponderId().toASN1Primitive().getName().toASN1Primitive())).getName();

        // Look for the existence of an Authorized OCSP responder inside the cert chain in the ocsp response.
        Iterable<X509Certificate> certs = SignUtils.getCertsFromOcspResponse(ocspResp);
        for (X509Certificate cert : certs) {
            try {
                if (name.equals(cert.getSubjectX500Principal().getName())) {
                    return Collections.singleton(cert);
                }
            } catch (Exception ignored) {
                // Ignore.
            }
        }
        // Certificate chain is not present in the response, or is does not contain the responder.
        return trustedCertificatesStore.getKnownCertificates(name);
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
        Certificate[][] result = getCrlIssuerCertificatesGeneric(crl, true);
        if (result.length == 0) {
            return new Certificate[0];
        }
        return result[0];
    }

    /**
     * {@inheritDoc}
     *
     * @param crl {@inheritDoc}
     *
     * @return {@inheritDoc}
     */

    @Override
    public Certificate[][] getCrlIssuerCertificatesByName(CRL crl) {
        return getCrlIssuerCertificatesGeneric(crl, false);
    }

    private Certificate[][] getCrlIssuerCertificatesGeneric(CRL crl, boolean verify) {
        // Usually CRLs are signed using CA certificate, so we don’t need to do anything extra and the revocation data
        // is already collected. However, it is possible to sign it with any other certificate.

        // IssuingDistributionPoint extension: https://datatracker.ietf.org/doc/html/rfc5280#section-5.2.5
        // Nothing special for the indirect CRLs.

        // AIA Extension
        ArrayList<Certificate[]> matches = new ArrayList<Certificate[]>();
        String url = CertificateUtil.getIssuerCertURL(crl);
        List<Certificate> certificatesFromAIA = (List<Certificate>) processCertificatesFromAIA(url);
        if (certificatesFromAIA != null) {
            addKnownCertificates(certificatesFromAIA);
        }
        // Retrieve Issuer from the certificate store
        Set<Certificate> issuers = trustedCertificatesStore
                .getKnownCertificates(((X509CRL) crl).getIssuerX500Principal().getName());
        if (issuers == null) {
            issuers = new HashSet<>();
        }
        List<Certificate> localIssuers = getCrlIssuersFromKnownCertificates((X509CRL) crl);
        if (localIssuers != null) {
            issuers.addAll(localIssuers);
        }
        if (issuers.isEmpty()) {
            // Unable to retrieve CRL issuer
            return new Certificate[0][];
        }
        for (Certificate i: issuers) {
            if (!verify || isSignedBy((X509CRL) crl, i)) {
                matches.addAll(buildCertificateChains((X509Certificate) i));
            }
        }
        return matches.toArray(new Certificate[][]{});
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
            String name = ((X509Certificate) certificate).getSubjectX500Principal().getName();
            List<Certificate> certs = knownCertificates.computeIfAbsent(name,k -> new ArrayList<>());
            certs.add(certificate);
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

    private static boolean isSignedBy(X509Certificate certificate, Certificate issuer) {
        try {
            certificate.verify(issuer.getPublicKey());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isSignedBy(X509CRL crl, Certificate issuer) {
        try {
            crl.verify(issuer.getPublicKey());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Certificate getIssuerFromCertificateSet(X509Certificate lastAddedCert,
            Collection<Certificate> certs) {
        if (certs != null) {
            for (Certificate cert : certs) {
                if (isSignedBy(lastAddedCert, cert)) {
                    return cert;
                }
            }
        }
        return null;
    }

    private List<Certificate> getCrlIssuersFromKnownCertificates(X509CRL crl) {
        return knownCertificates.get(crl.getIssuerX500Principal().getName());
    }
}
