/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Trusted certificates storage class to be used to configure trusted certificates in a particular way.
 */
public class TrustedCertificatesStore {
    private final Map<String, Set<Certificate>> generallyTrustedCertificates = new HashMap<>();
    private final Map<String, Set<Certificate>> ocspTrustedCertificates = new HashMap<>();
    private final Map<String, Set<Certificate>> timestampTrustedCertificates = new HashMap<>();
    private final Map<String, Set<Certificate>> crlTrustedCertificates = new HashMap<>();
    private final Map<String, Set<Certificate>> caTrustedCertificates = new HashMap<>();

    /**
     * Add collection of certificates to be trusted for any possible usage.
     *
     * @param certificates {@link Collection} of {@link Certificate} instances
     */
    public void addGenerallyTrustedCertificates(Collection<Certificate> certificates) {
        for (Certificate certificate : certificates) {
            addCertificateToMap(certificate, generallyTrustedCertificates);
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
            addCertificateToMap(certificate, ocspTrustedCertificates);
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
            addCertificateToMap(certificate, crlTrustedCertificates);
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
            addCertificateToMap(certificate, timestampTrustedCertificates);
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
            addCertificateToMap(certificate, caTrustedCertificates);
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
        return mapContainsCertificate(certificate, generallyTrustedCertificates);
    }


    /**
     * Check if provided certificate is configured to be trusted for OCSP response generation.
     *
     * @param certificate {@link Certificate} to be checked
     *
     * @return {@code true} is provided certificate is trusted for OCSP generation, {@code false} otherwise
     */
    public boolean isCertificateTrustedForOcsp(Certificate certificate) {
        return mapContainsCertificate(certificate, ocspTrustedCertificates);
    }

    /**
     * Check if provided certificate is configured to be trusted for CRL generation.
     *
     * @param certificate {@link Certificate} to be checked
     *
     * @return {@code true} is provided certificate is trusted for CRL generation, {@code false} otherwise
     */
    public boolean isCertificateTrustedForCrl(Certificate certificate) {
        return mapContainsCertificate(certificate, crlTrustedCertificates);
    }

    /**
     * Check if provided certificate is configured to be trusted for timestamp generation.
     *
     * @param certificate {@link Certificate} to be checked
     *
     * @return {@code true} is provided certificate is trusted for timestamp generation, {@code false} otherwise
     */
    public boolean isCertificateTrustedForTimestamp(Certificate certificate) {
        return mapContainsCertificate(certificate, timestampTrustedCertificates);
    }

    /**
     * Check if provided certificate is configured to be trusted to be CA.
     *
     * @param certificate {@link Certificate} to be checked
     *
     * @return {@code true} is provided certificate is trusted for certificates generation, {@code false} otherwise
     */
    public boolean isCertificateTrustedForCA(Certificate certificate) {
        return mapContainsCertificate(certificate, caTrustedCertificates);
    }

    /**
     * Get certificates, if any, which is trusted for any usage, which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return set of {@link Certificate} which correspond to the provided certificate name
     */
    public Set<Certificate> getGenerallyTrustedCertificates(String certificateName) {
        return generallyTrustedCertificates.getOrDefault(certificateName, Collections.<Certificate>emptySet());
    }

    /**
     * Get certificates, if any, which is trusted for OCSP response generation,
     * which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return set of {@link Certificate} which correspond to the provided certificate name
     */
    public Set<Certificate> getCertificatesTrustedForOcsp(String certificateName) {
        return ocspTrustedCertificates.getOrDefault(certificateName, Collections.<Certificate>emptySet());
    }

    /**
     * Get certificates, if any, which is trusted for CRL generation,
     * which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return set of {@link Certificate} which correspond to the provided certificate name
     */
    public Set<Certificate> getCertificatesTrustedForCrl(String certificateName) {
        return crlTrustedCertificates.getOrDefault(certificateName, Collections.<Certificate>emptySet());
    }

    /**
     * Get certificate, if any, which is trusted for timestamp generation,
     * which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return set of {@link Certificate} which correspond to the provided certificate name
     */
    public Set<Certificate> getCertificatesTrustedForTimestamp(String certificateName) {
        return timestampTrustedCertificates.getOrDefault(certificateName, Collections.<Certificate>emptySet());
    }

    /**
     * Get certificates, if any,
     * which is trusted to be a CA, which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return set of {@link Certificate} which correspond to the provided certificate name
     */
    public Set<Certificate> getCertificatesTrustedForCA(String certificateName) {
        return caTrustedCertificates.getOrDefault(certificateName, Collections.<Certificate>emptySet());
    }

    /**
     * Get certificates, if any, which corresponds to the provided certificate name.
     *
     * @param certificateName {@link String} certificate name
     *
     * @return set of {@link Certificate} which correspond to the provided certificate name
     */
    public Set<Certificate> getKnownCertificates(String certificateName) {
        Set<Certificate> result = new HashSet<>();
        addMatched(result, generallyTrustedCertificates, certificateName);
        addMatched(result, ocspTrustedCertificates, certificateName);
        addMatched(result, crlTrustedCertificates, certificateName);
        addMatched(result, timestampTrustedCertificates, certificateName);
        addMatched(result, caTrustedCertificates, certificateName);
        return result;
    }

    /**
     * Get all the certificates, which where provided to this storage as trusted certificate.
     *
     * @return {@link Collection} of {@link Certificate} instances
     */
    public Collection<Certificate> getAllTrustedCertificates() {
        Set<Certificate> certificates = new HashSet<>();
        for (Set<Certificate> set : generallyTrustedCertificates.values()) {
            certificates.addAll(set);
        }
        for (Set<Certificate> set : ocspTrustedCertificates.values()) {
            certificates.addAll(set);
        }
        for (Set<Certificate> set : crlTrustedCertificates.values()) {
            certificates.addAll(set);
        }
        for (Set<Certificate> set : timestampTrustedCertificates.values()) {
            certificates.addAll(set);
        }
        for (Set<Certificate> set : caTrustedCertificates.values()) {
            certificates.addAll(set);
        }
        return certificates;
    }

    /**
     * Get all the certificates having name as subject, which where provided to this storage as trusted certificate.
     *
     * @param name the subject name value for which to retrieve all trusted certificate
     *
     * @return set of {@link Certificate} which correspond to the provided certificate name
     */
    public Set<Certificate> getAllTrustedCertificates(String name) {
        Set<Certificate> certificates = new HashSet<>();
        Set<Certificate> set = generallyTrustedCertificates.get(name);
        if (set != null) {
            certificates.addAll(set);
        }
        set = ocspTrustedCertificates.get(name);
        if (set != null) {
            certificates.addAll(set);
        }
        set = crlTrustedCertificates.get(name);
        if (set != null) {
            certificates.addAll(set);
        }
        set = timestampTrustedCertificates.get(name);
        if (set != null) {
            certificates.addAll(set);
        }
        set = caTrustedCertificates.get(name);
        if (set != null) {
            certificates.addAll(set);
        }
        return certificates;
    }

    boolean checkIfCertIsTrusted(ValidationReport result, ValidationContext context,
                                         X509Certificate certificate) {
        if (CertificateSource.TRUSTED == context.getCertificateSource()) {
            result.addReportItem(new CertificateReportItem(certificate, CertificateChainValidator.CERTIFICATE_CHECK,
                    MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                            certificate.getSubjectX500Principal()), ReportItem.ReportItemStatus.INFO));
            return true;
        }


        if (this.isCertificateGenerallyTrusted(certificate)) {
            // Certificate is trusted for everything.
            result.addReportItem(new CertificateReportItem(certificate, CertificateChainValidator.CERTIFICATE_CHECK,
                    MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                            certificate.getSubjectX500Principal()), ReportItem.ReportItemStatus.INFO));
            return true;
        }
        if (this.isCertificateTrustedForCA(certificate)) {
            // Certificate is trusted to be CA, we need to make sure it wasn't used to directly sign anything else.
            if (CertificateSource.CERT_ISSUER == context.getCertificateSource()) {
                result.addReportItem(new CertificateReportItem(certificate, CertificateChainValidator.CERTIFICATE_CHECK,
                        MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                certificate.getSubjectX500Principal()), ReportItem.ReportItemStatus.INFO));
                return true;
            }
            // Certificate is trusted to be CA, but is not used in CA context.
            result.addReportItem(new CertificateReportItem(certificate, CertificateChainValidator.CERTIFICATE_CHECK,
                    MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                            certificate.getSubjectX500Principal(),
                    "certificates generation"), ReportItem.ReportItemStatus.INFO));
        }
        if (this.isCertificateTrustedForTimestamp(certificate)) {
            // Certificate is trusted for timestamp signing,
            // we need to make sure this chain is responsible for timestamping.
            if (ValidationContext.checkIfContextChainContainsCertificateSource(context, CertificateSource.TIMESTAMP)) {
                result.addReportItem(new CertificateReportItem(certificate, CertificateChainValidator.CERTIFICATE_CHECK,
                        MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                certificate.getSubjectX500Principal()), ReportItem.ReportItemStatus.INFO));
                return true;
            }
            // Certificate is trusted for timestamps generation, but is not used in timestamp generation context.
            result.addReportItem(new CertificateReportItem(certificate, CertificateChainValidator.CERTIFICATE_CHECK,
                    MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                            certificate.getSubjectX500Principal(),
                    "timestamp generation"), ReportItem.ReportItemStatus.INFO));
        }
        if (this.isCertificateTrustedForOcsp(certificate)) {
            // Certificate is trusted for OCSP response signing,
            // we need to make sure this chain is responsible for OCSP response generation.
            if (ValidationContext.checkIfContextChainContainsCertificateSource(
                    context, CertificateSource.OCSP_ISSUER)) {
                result.addReportItem(new CertificateReportItem(certificate, CertificateChainValidator.CERTIFICATE_CHECK,
                        MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                certificate.getSubjectX500Principal()), ReportItem.ReportItemStatus.INFO));
                return true;
            }
            // Certificate is trusted for OCSP response generation, but is not used in OCSP response generation context.
            result.addReportItem(new CertificateReportItem(certificate, CertificateChainValidator.CERTIFICATE_CHECK,
                    MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                            certificate.getSubjectX500Principal(),
                    "OCSP response generation"), ReportItem.ReportItemStatus.INFO));
        }
        if (this.isCertificateTrustedForCrl(certificate)) {
            // Certificate is trusted for CRL signing,
            // we need to make sure this chain is responsible for CRL generation.
            if (ValidationContext.checkIfContextChainContainsCertificateSource(context, CertificateSource.CRL_ISSUER)) {
                result.addReportItem(new CertificateReportItem(certificate, CertificateChainValidator.CERTIFICATE_CHECK,
                        MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                                certificate.getSubjectX500Principal()), ReportItem.ReportItemStatus.INFO));
                return true;
            }
            // Certificate is trusted for CRL generation, but is not used in CRL generation context.
            result.addReportItem(new CertificateReportItem(certificate, CertificateChainValidator.CERTIFICATE_CHECK,
                    MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                            certificate.getSubjectX500Principal(),
                    "CRL generation"), ReportItem.ReportItemStatus.INFO));
        }
        return false;
    }


    private static void addCertificateToMap(Certificate certificate, Map<String, Set<Certificate>> map) {
        String name = ((X509Certificate) certificate).getSubjectX500Principal().getName();

        Set<Certificate> set = map.computeIfAbsent(name, k -> new HashSet<>());
        set.add(certificate);
    }

    private static boolean mapContainsCertificate(Certificate certificate, Map<String, Set<Certificate>> map) {
        Set<Certificate> set = map.get(((X509Certificate) certificate).getSubjectX500Principal().getName());
        if (set == null) {
            return false;
        }
        return set.contains(certificate);
    }

    private static void addMatched(Set<Certificate> target, Map<String, Set<Certificate>> source,
            String certificateName) {
        Set<Certificate> subset = source.get(certificateName);
        if (subset != null) {
            target.addAll(subset);
        }
    }
}
