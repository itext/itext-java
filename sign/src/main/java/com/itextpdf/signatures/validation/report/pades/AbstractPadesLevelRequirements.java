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
package com.itextpdf.signatures.validation.report.pades;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class gathers all information needed to establish the achieved PAdES level of a signature.
 * It also holds the rules in common for both common signatures and document timestamps.
 * <p>
 * Specific rules are delegated to implementors of this class.
 * <p>
 * It also executes all those rules to define the achieved level.
 */
abstract class AbstractPadesLevelRequirements {

    public static final String COMMITMENT_TYPE_AND_REASON_SHALL_NOT_BE_USED_TOGETHER = "commitment-type-indication "
            + "or the reason dictionary item cannot be present together";
    public static final String SIGNED_DATA_CERTIFICATES_MUST_BE_INCLUDED = "SignedData.certificates must be included";
    public static final String SIGNED_DATA_CERTIFICATES_MUST_INCLUDE_SIGNING_CERTIFICATE = "SignedData.certificates "
            + "must include signing certificate";
    public static final String SIGNED_DATA_CERTIFICATES_SHOULD_INCLUDE_THE_ENTIRE_CERTIFICATE_CHAIN = "SignedData"
            + ".certificates should include the entire certificate chain";
    public static final String SIGNED_DATA_CERTIFICATES_SHOULD_INCLUDE_THE_ENTIRE_CERTIFICATE_CHAIN_AND_INCLUDE_CA =
            SIGNED_DATA_CERTIFICATES_SHOULD_INCLUDE_THE_ENTIRE_CERTIFICATE_CHAIN
                    + " including the certificate authority";
    public static final String CMS_CONTENT_TYPE_MUST_BE_ID_DATA = "CMS content-type entry value is not id-data";
    public static final String CMS_MESSAGE_DIGEST_IS_MISSING = "CMS message-digest is missing";
    public static final String CLAIMED_TIME_OF_SIGNING_SHALL_NOT_BE_INCLUDED_IN_THE_CMS = "Claimed time of signing "
            + "shall not be included in the CMS";
    public static final String DICTIONARY_ENTRY_M_IS_MISSING = "Dictionary entry M is missing";
    public static final String DICTIONARY_ENTRY_M_IS_NOT_IN_THE_CORRECT_FORMAT = "Dictionary entry M is not in the "
            + "correct format";
    public static final String CONTENTS_ENTRY_IS_MISSING_FROM_THE_SIGNATURE_DICTIONARY = "Contents entry is missing "
            + "from the signature dictionary";
    public static final String FILTER_ENTRY_IS_MISSING_FROM_THE_SIGNATURE_DICTIONARY = "Filter entry is missing from "
            + "the signature dictionary";
    public static final String BYTE_RANGE_ENTRY_IS_MISSING_FROM_THE_SIGNATURE_DICTIONARY = "ByteRange entry is "
            + "missing from the signature dictionary";
    public static final String FILTER_ENTRY_IS_MISSING_FROM_THE_SIGNATURE_DICTIONARY1 = "SubFilter entry is missing "
            + "from the signature dictionary";
    public static final String CERT_ENTRY_IS_ADDED_TO_THE_SIGNATURE_DICTIONARY = "Cert entry is added to the "
            + "signature dictionary";
    public static final String A_DISCOURAGED_HASH_OR_SIGNING_ALGORITHM_WAS_USED = "A hash or signing "
            + "algorithm was used that is not allowed according to ETSI 119 312 :\n";
    public static final String A_FORBIDDEN_HASH_OR_SIGNING_ALGORITHM_WAS_USED = "A hash or signing "
            + "algorithm was used that is not allowed according to ETSI 319 142-1 :\n";
    public static final String SIGNED_ATTRIBUTES_MUST_CONTAIN_SINGING_CERTIFICATE =
            "The singing certificate must be added as a signed attribute";
    public static final String SIGNED_ATTRIBUTES_SHOULD_CONTAIN_SIGNING_CERTIFICATE_V2 =
            "The singing certificate should be added as a singing-certificate-v2 signed attribute";
    public static final String THERE_MUST_BE_A_SIGNATURE_OR_DOCUMENT_TIMESTAMP_AVAILABLE = "There must be a signature "
            + "or document timestamp available";
    public static final String ISSUER_FOR_THESE_CERTIFICATES_IS_MISSING = "Issuer for the following certificates is "
            + "missing:\n";
    public static final String ISSUER_FOR_THESE_CERTIFICATES_IS_NOT_IN_DSS = "Issuer for the following certificates is "
            + "missing from the DSS dictionary:\n";
    public static final String REVOCATION_DATA_FOR_THESE_CERTIFICATES_IS_MISSING = "Revocation data for the "
            + "following certificates is missing:\n";
    public static final String REVOCATION_DATA_FOR_THESE_CERTIFICATES_NOT_TIMESTAMPED = "Revocation data for the "
            + "following certificates is not timestamped:\n";
    public static final String DOCUMENT_TIMESTAMP_IS_MISSING = "A document timestamp is missing";
    public static final String DSS_IS_NOT_COVERED_BY_TIMESTAMP = "The DSS entry is not covered by a document timestamp";

    private static final Map<PAdESLevel, LevelChecks> CHECKS = new HashMap<>();
    private static final PAdESLevel[] PADES_LEVELS =
            new PAdESLevel[] {PAdESLevel.B_B, PAdESLevel.B_T, PAdESLevel.B_LT, PAdESLevel.B_LTA};
    private final Map<PAdESLevel, List<String>> nonConformaties = new HashMap<>();
    private final Map<PAdESLevel, List<String>> warnings = new HashMap<>();
    //section 6.2.1
    protected List<String> discouragedAlgorithmUsage = new ArrayList<String>();
    protected List<String> forbiddenAlgorithmUsage = new ArrayList<String>();
    // Table 1 row 1
    protected boolean signedDataCertificatesPresent;
    // Table 1 row 1 note a
    protected boolean signatureCertificatesContainsSigningCertificate;
    // Table 1 row 1 note b
    protected boolean signatureCertificatesContainsCertificatePathIncludingCA;
    // Table 1 row 1 note b
    protected boolean signatureCertificatesContainsCertificatePath;
    // Table 1 row 2 note c
    protected boolean contentTypeValueIsIdData;
    // Table 1 row 3
    protected boolean messageDigestPresent;
    // Table 1 row 7
    protected boolean commitmentTypeIndicationPresent;
    // Table 1 row 9
    protected boolean essSigningCertificateV1Present;
    // Table 1 row 10
    protected boolean essSigningCertificateV2Present;
    // Table 1 row 12
    protected boolean dictionaryEntryMPresent;
    // Table 1 row 12 note g
    protected boolean dictionaryEntryMHasCorrectFormat;
    // Table 1 row 13
    protected boolean cmsSigningTimeAttributePresent;
    // Table 1 row 14
    protected boolean dictionaryEntryContentsPresent;
    // Table 1 row 15
    protected boolean dictionaryEntryFilterPresent;
    // Table 1 row 16
    protected boolean dictionaryEntryByteRangePresent;
    // Table 1 row 17
    protected boolean dictionaryEntrySubFilterPresent;
    // Table 1 row 17 note l
    protected boolean signatureDictionaryEntrySubFilterValueIsETSICadesDetached;
    // Table 1 row 19
    protected boolean dictionaryEntryReasonPresent;
    // Table 1 row 22
    protected boolean dictionaryEntryCertPresent;
    // Table 1 row 23n
    protected boolean poeSignaturePresent;
    // Table 1 row 25
    protected boolean documentTimestampPresent;
    // Table 1 row 27
    protected boolean isDSSPresent;
    // Table 1 row 29
    protected boolean poeDssPresent;
    // Table 1 row 30 note x
    protected List<X509Certificate> certificateIssuerMissing = new ArrayList<>();
    protected List<X509Certificate> certificateIssuerNotInDss = new ArrayList<>();
    protected List<X509Certificate> revocationDataNotInDSS = new ArrayList<>();
    protected List<X509Certificate> revocationDataNotTimestamped = new ArrayList<>();
    // Table 1 row 30 note y
    protected boolean timestampDictionaryEntrySubFilterValueEtsiRfc3161;
    protected boolean signatureIsValid = false;

    static {
        LevelChecks bbChecks = new LevelChecks();
        CHECKS.put(PAdESLevel.B_B, bbChecks);

        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.signedDataCertificatesPresent,
                SIGNED_DATA_CERTIFICATES_MUST_BE_INCLUDED));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.signatureCertificatesContainsSigningCertificate,
                SIGNED_DATA_CERTIFICATES_MUST_INCLUDE_SIGNING_CERTIFICATE));

        bbChecks.shoulds.add(new CheckAndMessage(
                r -> true,
                r -> SIGNED_DATA_CERTIFICATES_SHOULD_INCLUDE_THE_ENTIRE_CERTIFICATE_CHAIN));

        bbChecks.shoulds.add(new CheckAndMessage(
                r -> r.signatureCertificatesContainsCertificatePathIncludingCA,
                SIGNED_DATA_CERTIFICATES_SHOULD_INCLUDE_THE_ENTIRE_CERTIFICATE_CHAIN_AND_INCLUDE_CA));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.messageDigestPresent,
                CMS_MESSAGE_DIGEST_IS_MISSING));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> !(r.commitmentTypeIndicationPresent && r.dictionaryEntryReasonPresent),
                COMMITMENT_TYPE_AND_REASON_SHALL_NOT_BE_USED_TOGETHER));
        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.dictionaryEntryContentsPresent,
                CONTENTS_ENTRY_IS_MISSING_FROM_THE_SIGNATURE_DICTIONARY));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.dictionaryEntryFilterPresent,
                FILTER_ENTRY_IS_MISSING_FROM_THE_SIGNATURE_DICTIONARY));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.dictionaryEntryByteRangePresent,
                BYTE_RANGE_ENTRY_IS_MISSING_FROM_THE_SIGNATURE_DICTIONARY));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.dictionaryEntrySubFilterPresent,
                FILTER_ENTRY_IS_MISSING_FROM_THE_SIGNATURE_DICTIONARY1));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> !r.dictionaryEntryCertPresent,
                CERT_ENTRY_IS_ADDED_TO_THE_SIGNATURE_DICTIONARY));

        bbChecks.shoulds.add(new CheckAndMessage(
                r -> r.discouragedAlgorithmUsage.isEmpty(),
                r -> {
                    StringBuilder message = new StringBuilder(A_DISCOURAGED_HASH_OR_SIGNING_ALGORITHM_WAS_USED);
                    for (String usage : r.discouragedAlgorithmUsage) {
                        message.append('\t').append(usage).append('\n');
                    }
                    return message.toString();
                }));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.forbiddenAlgorithmUsage.isEmpty(),
                r -> {
                    StringBuilder message = new StringBuilder(A_FORBIDDEN_HASH_OR_SIGNING_ALGORITHM_WAS_USED);
                    for (String usage : r.forbiddenAlgorithmUsage) {
                        message.append('\t').append(usage).append('\n');
                    }
                    return message.toString();
                }));


        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.essSigningCertificateV1Present
                        || r.essSigningCertificateV2Present,
                SIGNED_ATTRIBUTES_MUST_CONTAIN_SINGING_CERTIFICATE));

        bbChecks.shoulds.add(new CheckAndMessage(
                r -> r.essSigningCertificateV2Present,
                SIGNED_ATTRIBUTES_SHOULD_CONTAIN_SIGNING_CERTIFICATE_V2));

        LevelChecks BTChecks = new LevelChecks();
        CHECKS.put(PAdESLevel.B_T, BTChecks);

        LevelChecks bltChecks = new LevelChecks();
        CHECKS.put(PAdESLevel.B_LT, bltChecks);

        bltChecks.shalls.add(new CheckAndMessage(
                r -> r.certificateIssuerMissing.isEmpty()
                        && r.revocationDataNotInDSS.isEmpty(),
                r -> {
                    StringBuilder message = new StringBuilder();
                    if (!r.certificateIssuerMissing.isEmpty()) {
                        message.append(ISSUER_FOR_THESE_CERTIFICATES_IS_MISSING);
                        for (X509Certificate cert : r.certificateIssuerMissing) {
                            message.append('\t').append(cert).append('\n');
                        }
                    }
                    if (!r.revocationDataNotInDSS.isEmpty()) {
                        message.append(REVOCATION_DATA_FOR_THESE_CERTIFICATES_IS_MISSING);
                        for (X509Certificate cert : r.revocationDataNotInDSS) {
                            message.append('\t').append(cert).append('\n');
                        }
                    }
                    return message.toString();
                }));
        bltChecks.shoulds.add(new CheckAndMessage(
                r -> r.certificateIssuerNotInDss.isEmpty(),
                r -> {
                    StringBuilder message = new StringBuilder();
                    message.append(ISSUER_FOR_THESE_CERTIFICATES_IS_NOT_IN_DSS);
                    for (X509Certificate cert : r.certificateIssuerNotInDss) {
                        message.append('\t').append(cert).append('\n');
                    }
                    return message.toString();
                }));

        LevelChecks bltaChecks = new LevelChecks();
        bltaChecks.shalls.add(new CheckAndMessage(r -> r.revocationDataNotTimestamped.isEmpty(), r -> {
            StringBuilder message = new StringBuilder();
            if (!r.revocationDataNotTimestamped.isEmpty()) {
                message.append(REVOCATION_DATA_FOR_THESE_CERTIFICATES_NOT_TIMESTAMPED);
                for (X509Certificate cert : r.revocationDataNotTimestamped) {
                    message.append('\t').append(cert).append('\n');
                }
            }
            return message.toString();
        }));

        CHECKS.put(PAdESLevel.B_LTA, bltaChecks);
    }

    /**
     * Calculates the highest achieved PAdES level for the signature being checked.
     *
     * @param timestampReports PAdES level reports for already checked timestamp signatures
     *
     * @return the highest achieved level
     */
    public PAdESLevel getHighestAchievedPadesLevel(Iterable<PAdESLevelReport> timestampReports) {
        for (PAdESLevel level : PADES_LEVELS) {
            ArrayList<String> messages = new ArrayList<>();
            this.nonConformaties.put(level, messages);
            for (CheckAndMessage check : CHECKS.get(level).shalls) {
                if (!check.getCheck().test(this)) {
                    messages.add(check.getMessageGenerator().apply(this));
                }
            }
            for (CheckAndMessage check : getChecks().get(level).shalls) {
                if (!check.getCheck().test(this)) {
                    messages.add(check.getMessageGenerator().apply(this));
                }
            }
            for (PAdESLevelReport tsReport : timestampReports) {
                messages.addAll(tsReport.getNonConformaties().get(level));
            }

            messages = new ArrayList<>();
            this.warnings.put(level, messages);
            for (CheckAndMessage check : CHECKS.get(level).shoulds) {
                if (!check.getCheck().test(this)) {
                    messages.add(check.getMessageGenerator().apply(this));
                }
            }
            for (CheckAndMessage check : getChecks().get(level).shoulds) {
                if (!check.getCheck().test(this)) {
                    messages.add(check.getMessageGenerator().apply(this));
                }
            }
            for (PAdESLevelReport tsReport : timestampReports) {
                messages.addAll(tsReport.getWarnings().get(level));
            }
        }
        PAdESLevel achieved = PAdESLevel.NONE;
        for (PAdESLevel level : PADES_LEVELS) {
            if (this.nonConformaties.containsKey(level) && !this.nonConformaties.get(level).isEmpty()) {
                break;
            }
            achieved = level;
        }
        if (signatureIsValid) {
            return achieved;
        }

        return PAdESLevel.INDETERMINATE;
    }

    /**
     * Adds a message about a non-approved algorithm, according to ETSI TS 119 312,  being used.
     *
     * @param message a message about a non-approved, according to ETSI TS 119 312, algorithm being used
     */
    public void addDiscouragedAlgorithmUsage(String message) {
        this.discouragedAlgorithmUsage.add(message);
    }

    /**
     * Adds a message about a forbidden algorithm, according to ETSI TS 319 142,  being used.
     *
     * @param message a message about a forbidden, according to ETSI TS 319 142, algorithm being used
     */
    public void addForbiddenAlgorithmUsage(String message) {
        this.forbiddenAlgorithmUsage.add(message);
    }


    /**
     * Sets whether the signatures container contains the certificate entry.
     *
     * @param signedDataCertificatesPresent whether the signatures container contains the certificate entry
     */
    public void setSignedDataCertificatesPresent(boolean signedDataCertificatesPresent) {
        this.signedDataCertificatesPresent = signedDataCertificatesPresent;
    }

    /**
     * Sets whether the signatures container contains the certificate chain includes the signing certificate.
     *
     * @param signatureCertificatesContainsSigningCertificate whether the signatures container contains
     *                                                        the certificate chain includes the signing certificate
     */
    public void setSignatureCertificatesContainsSigningCertificate(
            boolean signatureCertificatesContainsSigningCertificate) {
        this.signatureCertificatesContainsSigningCertificate = signatureCertificatesContainsSigningCertificate;
    }

    /**
     * Sets whether the signatures container contains the certificate chain.
     *
     * @param signedDataCertificatesContainsCertificatePath whether the signatures container contains
     *                                                      the certificate chain
     */
    public void setSignedDataCertificatesContainsCertificatePath(
            boolean signedDataCertificatesContainsCertificatePath) {
        this.signatureCertificatesContainsCertificatePath = signedDataCertificatesContainsCertificatePath;
    }

    /**
     * Sets whether the signatures container contains the certificate chain includes the CA.
     *
     * @param signatureCertificatesContainsCertificatePathIncludingCA whether the signatures containers contains the
     *                                                                certificate chain includes the CA
     */
    public void setSignatureCertificatesContainsCertificatePathIncludingCA(
            boolean signatureCertificatesContainsCertificatePathIncludingCA) {
        this.signatureCertificatesContainsCertificatePathIncludingCA =
                signatureCertificatesContainsCertificatePathIncludingCA;
    }

    /**
     * Sets whether the signatures container contains the certificate path.
     *
     * @param signatureCertificatesContainsCertificatePath whether the signatures container
     *                                                     contains the certificate path
     */
    public void setSignatureCertificatesContainsCertificatePath(boolean signatureCertificatesContainsCertificatePath) {
        this.signatureCertificatesContainsCertificatePath = signatureCertificatesContainsCertificatePath;
    }

    /**
     * Sets whether the CMS signed attributes contain the content type and whether that content type is IdData.
     *
     * @param contentTypeValueIsIdData whether the CMS signed attributes contain the content type and
     *                                 whether that content type is IdData.
     */
    public void setContentTypeValueIsIdData(boolean contentTypeValueIsIdData) {
        this.contentTypeValueIsIdData = contentTypeValueIsIdData;
    }

    /**
     * Sets whether the CMS signed attributes contain the MessageDigest.
     *
     * @param messageDigestPresent whether the CMS signed attributes contain the MessageDigest
     */
    public void setMessageDigestPresent(boolean messageDigestPresent) {
        this.messageDigestPresent = messageDigestPresent;
    }

    /**
     * Sets whether the CMS signed attributes contain the commitment type indication.
     *
     * @param commitmentTypeIndicationPresent whether the CMS signed attributes contain the commitment type indication
     */
    public void setCommitmentTypeIndicationPresent(boolean commitmentTypeIndicationPresent) {
        this.commitmentTypeIndicationPresent = commitmentTypeIndicationPresent;
    }

    /**
     * Sets whether the CMS signed attributes contain the signing certificate in V1 format.
     *
     * @param essSigningCertificatePresent whether the CMS signed attributes contain the
     *                                     signing certificate in V1 format
     */
    public void setEssSigningCertificateV1Present(boolean essSigningCertificatePresent) {
        this.essSigningCertificateV1Present = essSigningCertificatePresent;
    }

    /**
     * Sets whether the CMS signed attributes contain the signing certificate in Vs format.
     *
     * @param essSigningCertificateV2Present  whether the CMS signed attributes contain the
     *                                        signing certificate in V2 format
     */
    public void setEssSigningCertificateV2Present(boolean essSigningCertificateV2Present) {
        this.essSigningCertificateV2Present = essSigningCertificateV2Present;
    }

    /**
     * Sets whether the signature dictionary contains the M entry.
     *
     * @param dictionaryEntryMPresent  whether the signature dictionary contains the M entry
     */
    public void setDictionaryEntryMPresent(boolean dictionaryEntryMPresent) {
        this.dictionaryEntryMPresent = dictionaryEntryMPresent;
    }

    /**
     * Sets whether the signature dictionary entry M is correctly formatted.
     *
     * @param dictionaryEntryMHasCorrectFormat whether the signature dictionary entry M is correctly formatted
     */
    public void setDictionaryEntryMHasCorrectFormat(boolean dictionaryEntryMHasCorrectFormat) {
        this.dictionaryEntryMHasCorrectFormat = dictionaryEntryMHasCorrectFormat;
    }

    /**
     * Sets whether the CMS signed attributes contains the signing time attribute.
     *
     * @param cmsSigningTimeAttributePresent whether the CMS signed attributes contains the signing time attribute
     */
    public void setCmsSigningTimeAttributePresent(boolean cmsSigningTimeAttributePresent) {
        this.cmsSigningTimeAttributePresent = cmsSigningTimeAttributePresent;
    }

    /**
     * Sets whether the signature dictionary contains the Contents entry.
     *
     * @param dictionaryEntryContentsPresent  whether the signature dictionary contains the Contents entry
     */
    public void setDictionaryEntryContentsPresent(boolean dictionaryEntryContentsPresent) {
        this.dictionaryEntryContentsPresent = dictionaryEntryContentsPresent;
    }

    /**
     *  Sets whether the signature dictionary contains the Filter entry.
     *
     * @param dictionaryEntryFilterPresent whether the signature dictionary contains the Filter entry
     */
    public void setDictionaryEntryFilterPresent(boolean dictionaryEntryFilterPresent) {
        this.dictionaryEntryFilterPresent = dictionaryEntryFilterPresent;
    }

    /**
     * Sets whether the signature dictionary contains the Byte range entry.
     *
     * @param dictionaryEntryByteRangePresent whether the signature dictionary contains the byte range entry
     */
    public void setDictionaryEntryByteRangePresent(boolean dictionaryEntryByteRangePresent) {
        this.dictionaryEntryByteRangePresent = dictionaryEntryByteRangePresent;
    }

    /**
     * Sets whether the signature dictionary contains the Subfilter entry.
     *
     * @param dictionaryEntrySubFilterPresent whether the signature dictionary contains the Subfilter entry
     */
    public void setDictionaryEntrySubFilterPresent(boolean dictionaryEntrySubFilterPresent) {
        this.dictionaryEntrySubFilterPresent = dictionaryEntrySubFilterPresent;
    }

    /**
     * Sets whether the signature dictionary entry SubFilter has value ETSI.CAdES.detached.
     *
     * @param signatureDictionaryEntrySubFilterValueIsETSICadesDetached  whether the signature dictionary entry
     *                                                                   SubFilter has value ETSI.CAdES.detached
     */
    public void setSignatureDictionaryEntrySubFilterValueIsETSICadesDetached(
            boolean signatureDictionaryEntrySubFilterValueIsETSICadesDetached) {
        this.signatureDictionaryEntrySubFilterValueIsETSICadesDetached =
                signatureDictionaryEntrySubFilterValueIsETSICadesDetached;
    }

    /**
     * Sets whether the signature dictionary entry SubFilter has value ETSI.RFC3161.
     *
     * @param timestampDictionaryEntrySubFilterValueEtsiRfc3161 whether the signature dictionary entry SubFilter
     *                                                          has value ETSI.RFC3161
     */
    public void setTimestampDictionaryEntrySubFilterValueEtsiRfc3161(
            boolean timestampDictionaryEntrySubFilterValueEtsiRfc3161) {
        this.timestampDictionaryEntrySubFilterValueEtsiRfc3161 = timestampDictionaryEntrySubFilterValueEtsiRfc3161;
    }

    /**
     * Sets whether the signature dictionary contains the entry Reason.
     *
     * @param dictionaryEntryReasonPresent whether the signature dictionary contains the entry Reason
     */
    public void setDictionaryEntryReasonPresent(boolean dictionaryEntryReasonPresent) {
        this.dictionaryEntryReasonPresent = dictionaryEntryReasonPresent;
    }

    /**
     * Sets whether the signature dictionary contains the entry Cert.
     *
     * @param dictionaryEntryCertPresent whether the signature dictionary contains the entry Cert
     */
    public void setDictionaryEntryCertPresent(boolean dictionaryEntryCertPresent) {
        this.dictionaryEntryCertPresent = dictionaryEntryCertPresent;
    }

    /**
     * Sets whether there is a Proof of existence covering the signature.
     *
     * @param poeSignaturePresent whether there is a Proof of existence covering the signature
     */
    public void setPoeSignaturePresent(boolean poeSignaturePresent) {
        this.poeSignaturePresent = poeSignaturePresent;
    }

    /**
     * Sets whether there is a document timestamp covering the signature.
     *
     * @param documentTimestampPresent  whether there is a document timestamp covering the signature
     */
    public void setDocumentTimestampPresent(boolean documentTimestampPresent) {
        this.documentTimestampPresent = documentTimestampPresent;
    }

    /**
     * Sets whether there is a DSS covering the signature.
     *
     * @param isDSSPresent whether there is a DSS covering the signature
     */
    public void setDSSPresent(boolean isDSSPresent) {
        this.isDSSPresent = isDSSPresent;
    }

    /**
     * Adds a certificate for which the issuer cannot be found anywhere in the document.
     *
     * @param certificateUnderInvestigation a certificate for which the issuer cannot be found anywhere in the document
     */
    public void addCertificateIssuerMissing(X509Certificate certificateUnderInvestigation) {
        certificateIssuerMissing.add(certificateUnderInvestigation);
    }

    /**
     * Adds a certificate for which the issuer missing in the DSS.
     *
     * @param certificateUnderInvestigation a certificate for which the issuer missing in the DSS
     */
    public void addCertificateIssuerNotInDSS(X509Certificate certificateUnderInvestigation) {
        certificateIssuerNotInDss.add(certificateUnderInvestigation);
    }

    /**
     * Adds a certificate for which no revocation data was available in the DSS.
     *
     * @param certificateUnderInvestigation a certificate for which no revocation data was available in the DSS
     */
    public void addRevocationDataNotInDSS(X509Certificate certificateUnderInvestigation) {
        revocationDataNotInDSS.add(certificateUnderInvestigation);
    }

    /**
     * Adds a certificate for which no revocation data was available in a timestamped DSS.
     *
     * @param certificateUnderInvestigation a certificate for which no revocation data was available in the DSS
     */
    public void addRevocationDataNotTimestamped(X509Certificate certificateUnderInvestigation) {
        revocationDataNotTimestamped.add(certificateUnderInvestigation);
    }

    /**
     * Sets whether there is a Proof of Existence covering the DSS.
     *
     * @param poeDssPresent whether there is a Proof of Existence covering the DSS
     */
    public void setPoeDssPresent(boolean poeDssPresent) {
        this.poeDssPresent = poeDssPresent;
    }

    /**
     * Sets whether the signature validation was successful.
     *
     * @param validationSucceeded whether the signature validation was successful
     */
    public void setValidationSucceeded(boolean validationSucceeded) {
        signatureIsValid = validationSucceeded;
    }

    /**
     * Returns all non conformaties per level, the SHALL HAVE rules that were broken, per PAdES level.
     *
     * @return all non conformaties per level, the SHALL HAVE rules that were broken, per PAdES level
     */
    public Map<PAdESLevel, List<String>> getNonConformaties() {
        return nonConformaties;
    }

    /**
     * Returns all warnings, the SHOULD HAVE rules that were broken, per PAdES level.
     *
     * @return all warnings, the SHOULD HAVE rules that were broken, per PAdES level
     */
    public Map<PAdESLevel, List<String>> getWarnings() {
        return warnings;
    }

    /**
     * Abstract method to retrieve the specific rules sets from the implementors.
     *
     * @return the specific rules sets from the implementors
     */
    protected abstract Map<PAdESLevel, LevelChecks> getChecks();

    /**
     * A class to hold all rules for a level
     */
    protected static class LevelChecks {
        protected List<CheckAndMessage> shalls = new ArrayList<>();
        protected List<CheckAndMessage> shoulds = new ArrayList<>();

        protected LevelChecks() {
            // Empty constructor
        }
    }

    /**
     * A class containing a check executor and message generator
     */
    public static class CheckAndMessage {
        private final Function<AbstractPadesLevelRequirements, String> messageGenerator;
        private final Predicate<AbstractPadesLevelRequirements> check;

        /**
         * Instantiates a new check with a static message.
         *
         * @param check   the check executor, taking in the AbstractPadesLevelRequirements holding the information
         * @param message the static message for when the rule check failed
         */
        public CheckAndMessage(Predicate<AbstractPadesLevelRequirements> check, String message) {
            this.check = check;
            this.messageGenerator = r -> message;
        }

        /**
         * Instantiates a new check with a message generator.
         *
         * @param check            the check executor, taking in the
         *                         AbstractPadesLevelRequirements holding the information
         * @param messageGenerator the message generator for when the rule check failed,
         *                         taking the AbstractPadesLevelRequirements holding the information
         */
        public CheckAndMessage(Predicate<AbstractPadesLevelRequirements> check,
                Function<AbstractPadesLevelRequirements, String> messageGenerator) {
            this.check = check;
            this.messageGenerator = messageGenerator;
        }

        /**
         * Returns the message generator.
         *
         * @return the message generator
         */
        public Function<AbstractPadesLevelRequirements, String> getMessageGenerator() {
            return messageGenerator;
        }

        /**
         * Returns the check executor.
         *
         * @return the check executor
         */
        public Predicate<AbstractPadesLevelRequirements> getCheck() {
            return check;
        }
    }
}
