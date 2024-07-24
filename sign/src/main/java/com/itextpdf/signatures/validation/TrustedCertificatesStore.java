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
package com.itextpdf.signatures.validation;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trusted certificates storage class to be used to configure trusted certificates in a particular way.
 */
public class TrustedCertificatesStore {
    private final Map<String, Certificate> generallyTrustedCertificates = new HashMap<>();
    private final Map<String, Certificate> ocspTrustedCertificates = new HashMap<>();
    private final Map<String, Certificate> timestampTrustedCertificates = new HashMap<>();
    private final Map<String, Certificate> crlTrustedCertificates = new HashMap<>();
    private final Map<String, Certificate> caTrustedCertificates = new HashMap<>();

    /**
     * Add collection of certificates to be trusted for any possible usage.
     *
     * @param certificates {@link Collection} of {@link Certificate} instances
     */
    public void addGenerallyTrustedCertificates(Collection<Certificate> certificates) {
        for (Certificate certificate : certificates) {
            generallyTrustedCertificates.put(((X509Certificate) certificate)
                    .getSubjectX500Principal().getName(), certificate);
        }
    }

    /**
     * Add collection of certificates to be trusted for OCSP response signing.
     * These certificates are considered to be valid trust anchors for
     * arbitrarily long certificate chain responsible for OCSP response generation.
     *
     * @param certificates {@link Collection} of {@link Certificate} instances
     */
    public void addOcspTrustedCertificates(Collection<Certificate> certificates) {
        for (Certificate certificate : certificates) {
            ocspTrustedCertificates.put(((X509Certificate) certificate)
                    .getSubjectX500Principal().getName(), certificate);
        }
    }

    /**
     * Add collection of certificates to be trusted for CRL signing.
     * These certificates are considered to be valid trust anchors for
     * arbitrarily long certificate chain responsible for CRL generation.
     *
     * @param certificates {@link Collection} of {@link Certificate} instances
     */
    public void addCrlTrustedCertificates(Collection<Certificate> certificates) {
        for (Certificate certificate : certificates) {
            crlTrustedCertificates.put(((X509Certificate) certificate)
                    .getSubjectX500Principal().getName(), certificate);
        }
    }

    /**
     * Add collection of certificates to be trusted for timestamping.
     * These certificates are considered to be valid trust anchors for
     * arbitrarily long certificate chain responsible for timestamp generation.
     *
     * @param certificates {@link Collection} of {@link Certificate} instances
     */
    public void addTimestampTrustedCertificates(Collection<Certificate> certificates) {
        for (Certificate certificate : certificates) {
            timestampTrustedCertificates.put(((X509Certificate) certificate)
                    .getSubjectX500Principal().getName(), certificate);
        }
    }

    /**
     * Add collection of certificates to be trusted to be CA certificates.
     * These certificates are considered to be valid trust anchors for certificate generation.
     *
     * @param certificates {@link Collection} of {@link Certificate} instances
     */
    public void addCATrustedCertificates(Collection<Certificate> certificates) {
        for (Certificate certificate : certificates) {
            caTrustedCertificates.put(((X509Certificate) certificate).getSubjectX500Principal().getName(), certificate);
        }
    }

    /**
     * Check if provided certificate is configured to be trusted for any purpose.
     *
     * @param certificate {@link Certificate} to be checked
     *
     * @return {@code true} is provided certificate is generally trusted, {@code false} otherwise
     */
    public boolean isCertificateGenerallyTrusted(Certificate certificate) {
        return generallyTrustedCertificates.containsKey(
                ((X509Certificate) certificate).getSubjectX500Principal().getName());
    }

    /**
     * Check if provided certificate is configured to be trusted for OCSP response generation.
     *
     * @param certificate {@link Certificate} to be checked
     *
     * @return {@code true} is provided certificate is trusted for OCSP generation, {@code false} otherwise
     */
    public boolean isCertificateTrustedForOcsp(Certificate certificate) {
        return ocspTrustedCertificates.containsKey(((X509Certificate) certificate).getSubjectX500Principal().getName());
    }

    /**
     * Check if provided certificate is configured to be trusted for CRL generation.
     *
     * @param certificate {@link Certificate} to be checked
     *
     * @return {@code true} is provided certificate is trusted for CRL generation, {@code false} otherwise
     */
    public boolean isCertificateTrustedForCrl(Certificate certificate) {
        return crlTrustedCertificates.containsKey(((X509Certificate) certificate).getSubjectX500Principal().getName());
    }

    /**
     * Check if provided certificate is configured to be trusted for timestamp generation.
     *
     * @param certificate {@link Certificate} to be checked
     *
     * @return {@code true} is provided certificate is trusted for timestamp generation, {@code false} otherwise
     */
    public boolean isCertificateTrustedForTimestamp(Certificate certificate) {
        return timestampTrustedCertificates.containsKey(
                ((X509Certificate) certificate).getSubjectX500Principal().getName());
    }

    /**
     * Check if provided certificate is configured to be trusted to be CA.
     *
     * @param certificate {@link Certificate} to be checked
     *
     * @return {@code true} is provided certificate is trusted for certificates generation, {@code false} otherwise
     */
    public boolean isCertificateTrustedForCA(Certificate certificate) {
        return caTrustedCertificates.containsKey(((X509Certificate) certificate).getSubjectX500Principal().getName());
    }

    /**
     * Get certificate, if any, which is trusted for any usage, which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return {@link Certificate} which corresponds to the provided certificate name
     */
    public Certificate getGenerallyTrustedCertificate(String certificateName) {
        return generallyTrustedCertificates.get(certificateName);
    }

    /**
     * Get certificate, if any, which is trusted for OCSP response generation,
     * which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return {@link Certificate} which corresponds to the provided certificate name
     */
    public Certificate getCertificateTrustedForOcsp(String certificateName) {
        return ocspTrustedCertificates.get(certificateName);
    }

    /**
     * Get certificate, if any, which is trusted for CRL generation, which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return {@link Certificate} which corresponds to the provided certificate name
     */
    public Certificate getCertificateTrustedForCrl(String certificateName) {
        return crlTrustedCertificates.get(certificateName);
    }

    /**
     * Get certificate, if any, which is trusted for timestamp generation,
     * which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return {@link Certificate} which corresponds to the provided certificate name
     */
    public Certificate getCertificateTrustedForTimestamp(String certificateName) {
        return timestampTrustedCertificates.get(certificateName);
    }

    /**
     * Get certificate, if any, which is trusted to be a CA, which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return {@link Certificate} which corresponds to the provided certificate name
     */
    public Certificate getCertificateTrustedForCA(String certificateName) {
        return caTrustedCertificates.get(certificateName);
    }

    /**
     * Get certificate, if any, which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return {@link Certificate} which corresponds to the provided certificate name
     */
    public Certificate getKnownCertificate(String certificateName) {
        if (generallyTrustedCertificates.containsKey(certificateName)) {
            return generallyTrustedCertificates.get(certificateName);
        }
        if (ocspTrustedCertificates.containsKey(certificateName)) {
            return ocspTrustedCertificates.get(certificateName);
        }
        if (crlTrustedCertificates.containsKey(certificateName)) {
            return crlTrustedCertificates.get(certificateName);
        }
        if (timestampTrustedCertificates.containsKey(certificateName)) {
            return timestampTrustedCertificates.get(certificateName);
        }
        return caTrustedCertificates.get(certificateName);
    }

    /**
     * Get all the certificates, which where provided to this storage as trusted certificate.
     *
     * @return {@link Collection} of {@link Certificate} instances
     */
    public Collection<Certificate> getAllTrustedCertificates() {
        List<Certificate> certificates = new ArrayList<>();
        certificates.addAll(generallyTrustedCertificates.values());
        certificates.addAll(ocspTrustedCertificates.values());
        certificates.addAll(crlTrustedCertificates.values());
        certificates.addAll(timestampTrustedCertificates.values());
        certificates.addAll(caTrustedCertificates.values());
        return certificates;
    }
}
