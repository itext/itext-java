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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OID;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class that allows you to fetch and validate revocation data for the certificate.
 */
public class RevocationDataValidator {
    static final String REVOCATION_DATA_CHECK = "Revocation data check.";

    static final String CRL_PARSING_ERROR = "CRL is incorrectly formatted.";
    static final String NO_REVOCATION_DATA = "Certificate revocation status cannot be checked: " +
            "no revocation data available or the status cannot be determined.";
    static final String SELF_SIGNED_CERTIFICATE = "Certificate is self-signed: it cannot be revoked.";
    static final String TRUSTED_OCSP_RESPONDER = "Authorized OCSP Responder certificate has id-pkix-ocsp-nocheck " +
            "extension so it is trusted by the definition and no revocation checking is performed.";
    static final String VALIDITY_ASSURED = "Certificate is trusted due to validity assured - short term extension.";

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final List<IOcspClient> ocspClients = new ArrayList<>();
    private final List<ICrlClient> crlClients = new ArrayList<>();
    private IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
    private OCSPValidator ocspValidator = new OCSPValidator();
    private CRLValidator crlValidator = new CRLValidator();

    private boolean checkOcspResponder = false;
    private OnlineFetching onlineFetching = OnlineFetching.ALWAYS_FETCH;

    /**
     * Creates new {@link RevocationDataValidator} instance to validate certificate revocation data.
     */
    public RevocationDataValidator() {
        // Empty constructor.
    }

    /**
     * Add {@link ICrlClient} to be used for CRL responses receiving.
     *
     * @param crlClient {@link ICrlClient} to be used for CRL responses receiving
     *
     * @return same instance of {@link RevocationDataValidator}.
     */
    public RevocationDataValidator addCrlClient(ICrlClient crlClient) {
        this.crlClients.add(crlClient);
        return this;
    }

    /**
     * Add {@link IOcspClient} to be used for OCSP responses receiving.
     *
     * @param ocspClient {@link IOcspClient} to be used for OCSP responses receiving
     *
     * @return same instance of {@link RevocationDataValidator}.
     */
    public RevocationDataValidator addOcspClient(IOcspClient ocspClient) {
        this.ocspClients.add(ocspClient);
        return this;
    }

    /**
     * Set {@link IssuingCertificateRetriever} to be used for certificate chain building.
     *
     * @param certificateRetriever {@link IssuingCertificateRetriever} to restore certificates chain
     *
     * @return same instance of {@link RevocationDataValidator}.
     */
    public RevocationDataValidator setIssuingCertificateRetriever(IssuingCertificateRetriever certificateRetriever) {
        this.certificateRetriever = certificateRetriever;
        ocspValidator.setIssuingCertificateRetriever(certificateRetriever);
        crlValidator.setIssuingCertificateRetriever(certificateRetriever);
        return this;
    }

    /**
     * Sets {@link OCSPValidator} for the OCSP responses validation.
     *
     * @param validator {@link OCSPValidator} to be a validator for the OCSP responses validation
     *
     * @return same instance of {@link RevocationDataValidator}.
     */
    public RevocationDataValidator setOCSPValidator(OCSPValidator validator) {
        this.ocspValidator = validator;
        return this;
    }

    /**
     * Sets {@link CRLValidator} for the CRL responses validation.
     *
     * @param validator {@link CRLValidator} to be a validator for the CRL responses validation
     *
     * @return same instance of {@link RevocationDataValidator}.
     */
    public RevocationDataValidator setCRLValidator(CRLValidator validator) {
        this.crlValidator = validator;
        return this;
    }

    /**
     * Sets the onlineFetching property representing possible online fetching permissions.
     *
     * @param onlineFetching onlineFetching property value to set
     *
     * @return this same {@link RevocationDataValidator} instance.
     */
    public RevocationDataValidator setOnlineFetching(OnlineFetching onlineFetching) {
        this.onlineFetching = onlineFetching;
        return this;
    }

    /**
     * Sets the revocation freshness value which is a time interval in milliseconds indicating that the validation
     * accepts revocation data responses that were emitted at a point in time after the validation time minus the
     * freshness: thisUpdate not before (validationTime - freshness).
     *
     * <p>
     * If the revocation freshness constraint is respected by a revocation data then it can be used.
     *
     * @param freshness time interval in milliseconds identifying revocation freshness constraint
     *
     * @return this same {@link RevocationDataValidator} instance.
     */
    public RevocationDataValidator setFreshness(long freshness) {
        this.ocspValidator.setFreshness(freshness);
        this.crlValidator.setFreshness(freshness);
        return this;
    }

    /**
     * Validates revocation data (Certificate Revocation List (CRL) Responses and OCSP Responses) of the certificate.
     *
     * @param report         to store all the verification results
     * @param certificate    the certificate to check revocation data for
     * @param validationDate validation date to check for
     */
    public void validate(ValidationReport report, X509Certificate certificate, Date validationDate) {
        if (CertificateUtil.isSelfSigned(certificate)) {
            report.addReportItem(new CertificateReportItem(certificate, REVOCATION_DATA_CHECK, SELF_SIGNED_CERTIFICATE,
                    ReportItemStatus.INFO));
            return;
        }
        // Check Validity Assured - Short Term extension which indicates that the validity of the certificate is assured
        // because the certificate is a "short-term certificate".
        if (CertificateUtil.getExtensionValueByOid(certificate,
                OID.X509Extensions.VALIDITY_ASSURED_SHORT_TERM) != null) {
            report.addReportItem(new CertificateReportItem(certificate, REVOCATION_DATA_CHECK, VALIDITY_ASSURED,
                    ReportItemStatus.INFO));
            return;
        }
        if (checkOcspResponder) {
            // Check if Authorised OCSP Responder certificate has id-pkix-ocsp-nocheck extension, in which case we
            // do not perform revocation check for it.
            if (CertificateUtil.getExtensionValueByOid(certificate, BOUNCY_CASTLE_FACTORY.createOCSPObjectIdentifiers()
                    .getIdPkixOcspNoCheck().getId()) != null) {
                report.addReportItem(new CertificateReportItem(certificate, REVOCATION_DATA_CHECK,
                        TRUSTED_OCSP_RESPONDER, ReportItemStatus.INFO));
                return;
            }
            checkOcspResponder = false;
        }

        // Collect revocation data.
        Map<ISingleResp, IBasicOCSPResp> ocspResponsesMap = retrieveAllOCSPResponses(certificate);
        // Sort all the OCSP responses available based on the most recent revocation data.
        List<ISingleResp> singleResponses = ocspResponsesMap.keySet().stream()
                .sorted((o1, o2) -> o2.getThisUpdate().compareTo(o1.getThisUpdate())).collect(Collectors.toList());
        List<X509CRL> crlResponses = retrieveAllCRLResponses(report, certificate);

        // Try to check responderCert for revocation using provided responder OCSP/CRL clients or
        // Authority Information Access for OCSP responses and CRL Distribution Points for CRL responses
        // using default clients.
        validateRevocationData(report, certificate, validationDate, singleResponses, ocspResponsesMap, crlResponses);
    }

    /**
     * Identifies that revocation data will be checked for the OCSP Authorised Responder certificate during the next
     * {@link this#validate(ValidationReport, X509Certificate, Date)} call.
     *
     * @return same instance of {@link RevocationDataValidator}.
     */
    // TODO DEVSIX-8213 Introduce a context aware validation parameter class: remove this method.
    RevocationDataValidator checkOcspResponder() {
        this.checkOcspResponder = true;
        return this;
    }

    private void validateRevocationData(ValidationReport report, X509Certificate certificate, Date validationDate,
                                        List<ISingleResp> singleResponses, Map<ISingleResp, IBasicOCSPResp>
                                                ocspResponsesMap, List<X509CRL> crlResponses) {
        int i = 0;
        int j = 0;
        while (i < singleResponses.size() || j < crlResponses.size()) {
            ValidationReport revDataValidationReport = new ValidationReport();
            if (i < singleResponses.size() && (j >= crlResponses.size() ||
                    singleResponses.get(i).getThisUpdate().after(crlResponses.get(j).getThisUpdate()))) {
                ocspValidator.validate(revDataValidationReport, certificate, singleResponses.get(i),
                        ocspResponsesMap.get(singleResponses.get(i)), validationDate);
                i++;
            } else {
                crlValidator.validate(revDataValidationReport, certificate, crlResponses.get(j), validationDate);
                j++;
            }

            if (ValidationReport.ValidationResult.INDETERMINATE != revDataValidationReport.getValidationResult()) {
                for (ReportItem reportItem : revDataValidationReport.getLogs()) {
                    report.addReportItem(reportItem);
                }
                return;
            } else {
                for (ReportItem reportItem : revDataValidationReport.getLogs()) {
                    report.addReportItem(reportItem.setStatus(ReportItemStatus.INFO));
                }
            }
        }

        report.addReportItem(new CertificateReportItem(certificate, REVOCATION_DATA_CHECK, NO_REVOCATION_DATA,
                ReportItemStatus.INDETERMINATE));
    }

    private Map<ISingleResp, IBasicOCSPResp> retrieveAllOCSPResponses(X509Certificate certificate) {
        Map<ISingleResp, IBasicOCSPResp> ocspResponsesMap = new HashMap<>();
        for (IOcspClient ocspClient : ocspClients) {
            byte[] basicOcspRespBytes = ocspClient.getEncoded(certificate,
                    (X509Certificate) certificateRetriever.retrieveIssuerCertificate(certificate), null);
            if (basicOcspRespBytes != null) {
                try {
                    IBasicOCSPResp basicOCSPResp = BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(
                            BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(BOUNCY_CASTLE_FACTORY.createASN1Primitive(
                                    basicOcspRespBytes)));
                    fillOcspResponsesMap(ocspResponsesMap, basicOCSPResp);
                } catch (IOException ignored) {
                    // Ignore exception.
                }
            }
        }
        if (OnlineFetching.ALWAYS_FETCH == onlineFetching ||
                (OnlineFetching.FETCH_IF_NO_OTHER_DATA_AVAILABLE == onlineFetching && ocspResponsesMap.isEmpty())) {
            IBasicOCSPResp basicOCSPResp = new OcspClientBouncyCastle(null).getBasicOCSPResp(certificate,
                    (X509Certificate) certificateRetriever.retrieveIssuerCertificate(certificate), null);
            fillOcspResponsesMap(ocspResponsesMap, basicOCSPResp);
        }
        return ocspResponsesMap;
    }

    private void fillOcspResponsesMap(Map<ISingleResp, IBasicOCSPResp> ocspResponsesMap, IBasicOCSPResp basicOCSPResp) {
        if (basicOCSPResp != null) {
            // Getting the responses.
            ISingleResp[] singleResponses = basicOCSPResp.getResponses();
            for (ISingleResp singleResponse : singleResponses) {
                ocspResponsesMap.put(singleResponse, basicOCSPResp);
            }
        }
    }

    private List<X509CRL> retrieveAllCRLResponses(ValidationReport report, X509Certificate certificate) {
        List<X509CRL> crlResponses = new ArrayList<>();
        for (ICrlClient crlClient : crlClients) {
            crlResponses.addAll(retrieveAllCRLResponsesUsingClient(report, certificate, crlClient));
        }
        if (OnlineFetching.ALWAYS_FETCH == onlineFetching ||
                (OnlineFetching.FETCH_IF_NO_OTHER_DATA_AVAILABLE == onlineFetching && crlResponses.isEmpty())) {
            crlResponses.addAll(retrieveAllCRLResponsesUsingClient(report, certificate, new CrlClientOnline()));
        }
        // Sort all the CRL responses available based on the most recent revocation data.
        return crlResponses.stream().sorted((o1, o2) -> o2.getThisUpdate().compareTo(o1.getThisUpdate()))
                .collect(Collectors.toList());
    }

    private List<X509CRL> retrieveAllCRLResponsesUsingClient(ValidationReport report, X509Certificate certificate,
                                                             ICrlClient crlClient) {
        List<X509CRL> crlResponses = new ArrayList<>();
        try {
            Collection<byte[]> crlBytesCollection = crlClient.getEncoded(certificate, null);
            for (byte[] crlBytes : crlBytesCollection) {
                try {
                    crlResponses.add((X509CRL) CertificateUtil.parseCrlFromStream(
                            new ByteArrayInputStream(crlBytes)));
                } catch (Exception ignored) {
                    // CRL parsing error.
                    report.addReportItem(new CertificateReportItem(certificate, REVOCATION_DATA_CHECK,
                            CRL_PARSING_ERROR, ReportItemStatus.INFO));
                }
            }
        } catch (GeneralSecurityException ignored) {
            // Ignore exception.
        }
        return crlResponses;
    }

    /**
     * Enum representing possible online fetching permissions.
     */
    public enum OnlineFetching {
        /**
         * Permission to always fetch revocation data online.
         */
        ALWAYS_FETCH,
        /**
         * Permission to fetch revocation data online if no other revocation data available.
         */
        FETCH_IF_NO_OTHER_DATA_AVAILABLE,
        /**
         * Forbids fetching revocation data online.
         */
        NEVER_FETCH
    }
}
