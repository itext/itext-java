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
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.kernel.crypto.OID.X509Extensions;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDeveloperExtension;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.logs.SignLogMessageConstant;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add verification according to PAdES-LTV (part 4).
 */
public class LtvVerification {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final Logger LOGGER = LoggerFactory.getLogger(LtvVerification.class);

    private final PdfDocument document;
    private final SignatureUtil sgnUtil;
    private final Map<PdfName, ValidationData> validated = new HashMap<>();
    private boolean used = false;
    private String securityProviderCode = null;
    private RevocationDataNecessity revocationDataNecessity = RevocationDataNecessity.OPTIONAL;
    private IIssuingCertificateRetriever issuingCertificateRetriever = new DefaultIssuingCertificateRetriever();

    /**
     * What type of verification to include.
     */
    public enum Level {
        /**
         * Include only OCSP.
         */
        OCSP,
        /**
         * Include only CRL.
         */
        CRL,
        /**
         * Include both OCSP and CRL.
         */
        OCSP_CRL,
        /**
         * Include CRL only if OCSP can't be read.
         */
        OCSP_OPTIONAL_CRL
    }

    /**
     * Options for how many certificates to include.
     */
    public enum CertificateOption {
        /**
         * Include verification just for the signing certificate.
         */
        SIGNING_CERTIFICATE,
        /**
         * Include verification for the whole chain of certificates.
         */
        WHOLE_CHAIN,
        /**
         * Include verification for the whole certificates chain, certificates used to create OCSP responses,
         * CRL response certificates and timestamp certificates included in the signatures.
         */
        ALL_CERTIFICATES
    }

    /**
     * Certificate inclusion in the DSS and VRI dictionaries in the CERT and CERTS
     * keys.
     */
    public enum CertificateInclusion {
        /**
         * Include certificates in the DSS and VRI dictionaries.
         */
        YES,
        /**
         * Do not include certificates in the DSS and VRI dictionaries.
         */
        NO
    }

    /**
     * Option to determine whether revocation information is required for the signing certificate.
     */
    public enum RevocationDataNecessity {
        /**
         * Require revocation information for the signing certificate.
         */
        REQUIRED_FOR_SIGNING_CERTIFICATE,
        /**
         * Revocation data for the signing certificate may be optional.
         */
        OPTIONAL
    }

    /**
     * The verification constructor. This class should only be created with
     * PdfStamper.getLtvVerification() otherwise the information will not be
     * added to the Pdf.
     *
     * @param document The {@link PdfDocument} to apply the validation to.
     */
    public LtvVerification(PdfDocument document) {
        this.document = document;
        this.sgnUtil = new SignatureUtil(document);
    }

    /**
     * The verification constructor. This class should only be created with
     * PdfStamper.getLtvVerification() otherwise the information will not be
     * added to the Pdf.
     *
     * @param document             The {@link PdfDocument} to apply the validation to.
     * @param securityProviderCode Security provider to use
     */
    public LtvVerification(PdfDocument document, String securityProviderCode) {
        this(document);
        this.securityProviderCode = securityProviderCode;
    }

    /**
     * Sets {@link RevocationDataNecessity} option to specify the necessity of revocation data.
     *
     * <p>
     * Default value is {@link RevocationDataNecessity#OPTIONAL}.
     *
     * @param revocationDataNecessity {@link RevocationDataNecessity} value to set
     *
     * @return this {@link LtvVerification} instance.
     */
    public LtvVerification setRevocationDataNecessity(RevocationDataNecessity revocationDataNecessity) {
        this.revocationDataNecessity = revocationDataNecessity;
        return this;
    }

    /**
     * Sets {@link IIssuingCertificateRetriever} instance needed to get CRL issuer certificates (using AIA extension).
     *
     * <p>
     * Default value is {@link DefaultIssuingCertificateRetriever}.
     *
     * @param issuingCertificateRetriever {@link IIssuingCertificateRetriever} instance to set
     *
     * @return this {@link LtvVerification} instance.
     */
    public LtvVerification setIssuingCertificateRetriever(IIssuingCertificateRetriever issuingCertificateRetriever) {
        this.issuingCertificateRetriever = issuingCertificateRetriever;
        return this;
    }

    /**
     * Add verification for a particular signature.
     *
     * @param signatureName the signature to validate (it may be a timestamp)
     * @param ocsp          the interface to get the OCSP
     * @param crl           the interface to get the CRL
     * @param certOption    options as to how many certificates to include
     * @param level         the validation options to include
     * @param certInclude   certificate inclusion options
     *
     * @return true if a validation was generated, false otherwise
     *
     * @throws GeneralSecurityException when requested cryptographic algorithm or security provider
     *                                  is not available
     * @throws IOException              signals that an I/O exception has occurred
     */
    public boolean addVerification(String signatureName, IOcspClient ocsp, ICrlClient crl, CertificateOption certOption,
            Level level, CertificateInclusion certInclude)
            throws IOException, GeneralSecurityException {
        if (used) {
            throw new IllegalStateException(SignExceptionMessageConstant.VERIFICATION_ALREADY_OUTPUT);
        }
        PdfPKCS7 pk = sgnUtil.readSignatureData(signatureName, securityProviderCode);
        LOGGER.info("Adding verification for " + signatureName);
        Certificate[] certificateChain = pk.getCertificates();
        X509Certificate signingCert = pk.getSigningCertificate();
        ValidationData validationData = new ValidationData();
        Set<X509Certificate> processedCerts = new HashSet<>();
        addRevocationDataForChain(signingCert, certificateChain, ocsp, crl, level, certInclude, certOption,
                validationData, processedCerts);

        if (certOption == CertificateOption.ALL_CERTIFICATES) {
            Certificate[] timestampCertsChain = pk.getTimestampCertificates();
            addRevocationDataForChain(signingCert, timestampCertsChain, ocsp, crl, level, certInclude, certOption,
                    validationData, processedCerts);
        }
        if (certInclude == CertificateInclusion.YES) {
            for (X509Certificate processedCert : processedCerts) {
                List<byte[]> certs = validationData.getCerts();
                certs.add(processedCert.getEncoded());
                validationData.setCerts(certs);
            }
        }
        
        if (validationData.getCrls().isEmpty() && validationData.getOcsps().isEmpty()) {
            return false;
        }
        validated.put(getSignatureHashKey(signatureName), validationData);
        return true;
    }

    /**
     * Adds verification to the signature.
     *
     * @param signatureName name of the signature
     * @param ocsps         collection of DER-encoded BasicOCSPResponses
     * @param crls          collection of DER-encoded CRLs
     * @param certs         collection of DER-encoded certificates
     *
     * @return boolean
     *
     * @throws IOException              signals that an I/O exception has occurred
     * @throws GeneralSecurityException when requested cryptographic algorithm or security provider
     *                                  is not available
     */
    public boolean addVerification(String signatureName, Collection<byte[]> ocsps, Collection<byte[]> crls,
            Collection<byte[]> certs) throws IOException, GeneralSecurityException {
        if (used) {
            throw new IllegalStateException(SignExceptionMessageConstant.VERIFICATION_ALREADY_OUTPUT);
        }
        ValidationData vd = new ValidationData();
        if (ocsps != null) {
            for (byte[] ocsp : ocsps) {
                List<byte[]> ocspsArr = vd.getOcsps();
                ocspsArr.add(LtvVerification.buildOCSPResponse(ocsp));
                vd.setOcsps(ocspsArr);
            }
        }
        if (crls != null) {
            List<byte[]> crlsArr = vd.getCrls();
            crlsArr.addAll(crls);
            vd.setCrls(crlsArr);
        }
        if (certs != null) {
            List<byte[]> certsArr = vd.getCerts();
            certsArr.addAll(certs);
            vd.setCerts(certsArr);
        }
        validated.put(getSignatureHashKey(signatureName), vd);
        return true;
    }

    /**
     * Merges the validation with any validation already in the document or creates a new one.
     */
    public void merge() {
        if (used || validated.isEmpty()) {
            return;
        }
        used = true;
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfObject dss = catalog.get(PdfName.DSS);
        if (dss == null) {
            createDss();
        } else {
            updateDss();
        }
    }

    /**
     * Converts an array of bytes to a String of hexadecimal values
     *
     * @param bytes a byte array
     *
     * @return the same bytes expressed as hexadecimal values
     */
    public static String convertToHex(byte[] bytes) {
        ByteBuffer buf = new ByteBuffer();
        for (byte b : bytes) {
            buf.appendHex(b);
        }
        return PdfEncodings.convertToString(buf.toByteArray(), null).toUpperCase();
    }

    /**
     * Get the issuing certificate for a child certificate.
     *
     * @param cert  the certificate for which we search the parent
     * @param certs an array with certificates that contains the parent
     *
     * @return the parent certificate
     */
    X509Certificate getParent(X509Certificate cert, Certificate[] certs) {
        X509Certificate parent;
        for (Certificate certificate : certs) {
            parent = (X509Certificate) certificate;
            if (!cert.getIssuerX500Principal().equals(parent.getSubjectX500Principal())) {
                continue;
            }
            try {
                cert.verify(parent.getPublicKey());
                return parent;
            } catch (Exception e) {
                // do nothing
            }
        }
        return null;
    }

    private void addRevocationDataForChain(X509Certificate signingCert, Certificate[] certChain, IOcspClient ocsp,
            ICrlClient crl, Level level, CertificateInclusion certInclude, CertificateOption certOption,
            ValidationData validationData, Set<X509Certificate> processedCerts)
            throws CertificateException, IOException, CRLException {
        Certificate[] fullChain = certOption == CertificateOption.ALL_CERTIFICATES ?
                retrieveMissingCertificates(certChain) : certChain;
        for (Certificate certificate : fullChain) {
            X509Certificate cert = (X509Certificate) certificate;
            LOGGER.info(MessageFormatUtil.format("Certificate: {0}", BOUNCY_CASTLE_FACTORY.createX500Name(cert)));
            if ((certOption == CertificateOption.SIGNING_CERTIFICATE && !cert.equals(signingCert))
                    || processedCerts.contains(cert)) {
                continue;
            }
            addRevocationDataForCertificate(signingCert, fullChain, cert, ocsp, crl, level, certInclude, certOption,
                    validationData, processedCerts);
        }
    }

    private void addRevocationDataForCertificate(X509Certificate signingCert, Certificate[] certificateChain,
            X509Certificate cert, IOcspClient ocsp, ICrlClient crl, Level level, CertificateInclusion certInclude,
            CertificateOption certOption, ValidationData validationData, Set<X509Certificate> processedCerts)
            throws IOException, CertificateException, CRLException {
        processedCerts.add(cert);
        byte[] validityAssured = SignUtils.getExtensionValueByOid(cert, X509Extensions.VALIDITY_ASSURED_SHORT_TERM);
        if (validityAssured != null) {
            LOGGER.info(MessageFormatUtil.format(SignLogMessageConstant.REVOCATION_DATA_NOT_ADDED_VALIDITY_ASSURED,
                    cert.getSubjectX500Principal()));
            return;
        }
        byte[] ocspEnc = null;
        boolean revocationDataAdded = false;
        if (ocsp != null && level != Level.CRL) {
            ocspEnc = ocsp.getEncoded(cert, getParent(cert, certificateChain), null);
            if (ocspEnc != null && BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood().equals(
                    OcspClientBouncyCastle.getCertificateStatus(ocspEnc))) {
                List<byte[]> ocsps = validationData.getOcsps();
                ocsps.add(LtvVerification.buildOCSPResponse(ocspEnc));
                validationData.setOcsps(ocsps);
                revocationDataAdded = true;
                LOGGER.info("OCSP added");
                if (certOption == CertificateOption.ALL_CERTIFICATES) {
                    addRevocationDataForOcspCert(ocspEnc, signingCert, ocsp, crl, level, certInclude, certOption,
                            validationData, processedCerts);
                }
            } else {
                ocspEnc = null;
            }
        }
        if (crl != null
                && (level == Level.CRL || level == Level.OCSP_CRL
                || (level == Level.OCSP_OPTIONAL_CRL && ocspEnc == null))) {
            Collection<byte[]> cims = crl.getEncoded(cert, null);
            if (cims != null) {
                for (byte[] cim : cims) {
                    revocationDataAdded = true;
                    boolean dup = false;
                    for (byte[] b : validationData.getCrls()) {
                        if (Arrays.equals(b, cim)) {
                            dup = true;
                            break;
                        }
                    }
                    if (!dup) {
                        List<byte[]> crls = validationData.getCrls();
                        crls.add(cim);
                        validationData.setCrls(crls);
                        LOGGER.info("CRL added");
                        if (certOption == CertificateOption.ALL_CERTIFICATES) {
                            Certificate[] certsList = issuingCertificateRetriever.getCrlIssuerCertificates(
                                    SignUtils.parseCrlFromStream(new ByteArrayInputStream(cim)));
                            addRevocationDataForChain(signingCert, certsList, ocsp, crl,
                                    level, certInclude, certOption, validationData, processedCerts);
                        }
                    }
                }
            }
        }
        if (revocationDataNecessity == RevocationDataNecessity.REQUIRED_FOR_SIGNING_CERTIFICATE &&
                signingCert.equals(cert) && !revocationDataAdded) {
            throw new PdfException(SignExceptionMessageConstant.NO_REVOCATION_DATA_FOR_SIGNING_CERTIFICATE);
        }
    }

    private void addRevocationDataForOcspCert(byte[] ocspEnc, X509Certificate signingCert, IOcspClient ocsp,
            ICrlClient crl, Level level, CertificateInclusion certInclude, CertificateOption certOption,
            ValidationData validationData, Set<X509Certificate> processedCerts)
            throws CertificateException, IOException, CRLException {
        IBasicOCSPResp ocspResp = BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(
                BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(ocspEnc));
        Iterable<X509Certificate> certs = SignUtils.getCertsFromOcspResponse(ocspResp);
        List<X509Certificate> ocspCertsList = iterableToList(certs);
        X509Certificate ocspSigningCert = null;
        for (X509Certificate ocspCert : ocspCertsList) {
            try {
                if (SignUtils.isSignatureValid(ocspResp, ocspCert, BOUNCY_CASTLE_FACTORY.getProviderName())) {
                    ocspSigningCert = ocspCert;
                    break;
                }
            } catch (AbstractOperatorCreationException | AbstractOCSPException ignored) {
                // Wasn't possible to check if this cert is signing one, skip.
            }
        }
        if (ocspSigningCert != null && SignUtils.getExtensionValueByOid(
                ocspSigningCert, X509Extensions.ID_PKIX_OCSP_NOCHECK) != null) {
            // If ocsp_no_check extension is set on OCSP signing cert we shan't collect revocation data for this cert.
            ocspCertsList.remove(ocspSigningCert);
            processedCerts.add(ocspSigningCert);
        }
        addRevocationDataForChain(signingCert, ocspCertsList.toArray(new X509Certificate[0]),
                ocsp, crl, level, certInclude, certOption, validationData, processedCerts);
    }
    
    private static List<X509Certificate> iterableToList(Iterable<X509Certificate> iterable) {
        List<X509Certificate> list = new ArrayList<>();
        for (X509Certificate certificate : iterable) {
            list.add(certificate);
        }
        return list;
    }

    private static byte[] buildOCSPResponse(byte[] basicOcspResponse) throws IOException {
        IDEROctetString doctet = BOUNCY_CASTLE_FACTORY.createDEROctetString(basicOcspResponse);
        IOCSPResponseStatus respStatus = BOUNCY_CASTLE_FACTORY.createOCSPResponseStatus(
                BOUNCY_CASTLE_FACTORY.createOCSPRespBuilderInstance().getSuccessful());
        IResponseBytes responseBytes = BOUNCY_CASTLE_FACTORY.createResponseBytes(
                BOUNCY_CASTLE_FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspBasic(), doctet);
        IOCSPResponse ocspResponse = BOUNCY_CASTLE_FACTORY.createOCSPResponse(respStatus, responseBytes);
        return BOUNCY_CASTLE_FACTORY.createOCSPResp(ocspResponse).getEncoded();
    }

    private PdfName getSignatureHashKey(String signatureName) throws NoSuchAlgorithmException {
        PdfSignature sig = sgnUtil.getSignature(signatureName);
        PdfString contents = sig.getContents();
        byte[] bc = PdfEncodings.convertToBytes(contents.getValue(), null);
        byte[] bt = hashBytesSha1(bc);
        return new PdfName(convertToHex(bt));
    }

    private static byte[] hashBytesSha1(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest sh = MessageDigest.getInstance("SHA1");
        return sh.digest(b);
    }

    private void updateDss() {
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        catalog.setModified();
        PdfDictionary dss = catalog.getAsDictionary(PdfName.DSS);
        PdfArray ocsps = dss.getAsArray(PdfName.OCSPs);
        PdfArray crls = dss.getAsArray(PdfName.CRLs);
        PdfArray certs = dss.getAsArray(PdfName.Certs);
        dss.remove(PdfName.OCSPs);
        dss.remove(PdfName.CRLs);
        dss.remove(PdfName.Certs);
        PdfDictionary vrim = dss.getAsDictionary(PdfName.VRI);
        // delete old validations
        if (vrim != null) {
            for (PdfName n : vrim.keySet()) {
                if (validated.containsKey(n)) {
                    PdfDictionary vri = vrim.getAsDictionary(n);
                    if (vri != null) {
                        deleteOldReferences(ocsps, vri.getAsArray(PdfName.OCSP));
                        deleteOldReferences(crls, vri.getAsArray(PdfName.CRL));
                        deleteOldReferences(certs, vri.getAsArray(PdfName.Cert));
                    }
                }
            }
        }
        if (ocsps == null) {
            ocsps = new PdfArray();
        }
        if (crls == null) {
            crls = new PdfArray();
        }
        if (certs == null) {
            certs = new PdfArray();
        }
        if (vrim == null) {
            vrim = new PdfDictionary();
        }
        outputDss(dss, vrim, ocsps, crls, certs);
    }

    private static void deleteOldReferences(PdfArray all, PdfArray toDelete) {
        if (all == null || toDelete == null) {
            return;
        }

        for (PdfObject pi : toDelete) {
            final PdfIndirectReference pir = pi.getIndirectReference();

            for (int i = 0; i < all.size(); i++) {
                final PdfIndirectReference pod = all.get(i).getIndirectReference();

                if (Objects.equals(pir, pod)) {
                    all.remove(i);
                    i--;
                }
            }
        }
    }

    private void createDss() {
        outputDss(new PdfDictionary(), new PdfDictionary(), new PdfArray(), new PdfArray(), new PdfArray());
    }

    private void outputDss(PdfDictionary dss, PdfDictionary vrim, PdfArray ocsps, PdfArray crls, PdfArray certs) {
        PdfCatalog catalog = document.getCatalog();
        if (document.getPdfVersion().compareTo(PdfVersion.PDF_2_0) < 0) {
            catalog.addDeveloperExtension(PdfDeveloperExtension.ESIC_1_7_EXTENSIONLEVEL5);
        }
        for (PdfName vkey : validated.keySet()) {
            PdfArray ocsp = new PdfArray();
            PdfArray crl = new PdfArray();
            PdfArray cert = new PdfArray();
            PdfDictionary vri = new PdfDictionary();
            for (byte[] b : validated.get(vkey).getCrls()) {
                PdfStream ps = new PdfStream(b);
                ps.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
                ps.makeIndirect(document);
                crl.add(ps);
                crls.add(ps);
                crls.setModified();
            }
            for (byte[] b : validated.get(vkey).getOcsps()) {
                PdfStream ps = new PdfStream(b);
                ps.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
                ocsp.add(ps);
                ocsps.add(ps);
                ocsps.setModified();
            }
            for (byte[] b : validated.get(vkey).getCerts()) {
                PdfStream ps = new PdfStream(b);
                ps.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
                ps.makeIndirect(document);
                cert.add(ps);
                certs.add(ps);
                certs.setModified();
            }
            if (!ocsp.isEmpty()) {
                ocsp.makeIndirect(document);
                vri.put(PdfName.OCSP, ocsp);
            }
            if (!crl.isEmpty()) {
                crl.makeIndirect(document);
                vri.put(PdfName.CRL, crl);
            }
            if (!cert.isEmpty()) {
                cert.makeIndirect(document);
                vri.put(PdfName.Cert, cert);
            }
            vri.makeIndirect(document);
            vrim.put(vkey, vri);
        }
        vrim.makeIndirect(document);
        vrim.setModified();
        dss.put(PdfName.VRI, vrim);
        if (!ocsps.isEmpty()) {
            ocsps.makeIndirect(document);
            dss.put(PdfName.OCSPs, ocsps);
        }
        if (!crls.isEmpty()) {
            crls.makeIndirect(document);
            dss.put(PdfName.CRLs, crls);
        }
        if (!certs.isEmpty()) {
            certs.makeIndirect(document);
            dss.put(PdfName.Certs, certs);
        }

        dss.makeIndirect(document);
        dss.setModified();
        catalog.put(PdfName.DSS, dss);
    }

    private static class ValidationData {
        private List<byte[]> crls = new ArrayList<>();
        private List<byte[]> ocsps = new ArrayList<>();
        private List<byte[]> certs = new ArrayList<>();

        /**
         * Sets the crls byte array.
         *
         * @param crls crls
         */
        public void setCrls(List<byte[]> crls) {
            this.crls = crls;
        }

        /**
         * Retrieves Crls byte array.
         *
         * @return crls
         */
        public List<byte[]> getCrls() {
            return crls;
        }

        /**
         * Sets the ocsps array.
         *
         * @param ocsps ocsps
         */
        public void setOcsps(List<byte[]> ocsps) {
            this.ocsps = ocsps;
        }

        /**
         * Retrieves ocsps byte array.
         *
         * @return ocsps
         */
        public List<byte[]> getOcsps() {
            return ocsps;
        }

        /**
         * Sets the certs byte array.
         *
         * @param certs certs
         */
        public void setCerts(List<byte[]> certs) {
            this.certs = certs;
        }

        /**
         * Retrieves cert byte array.
         *
         * @return cert
         */
        public List<byte[]> getCerts() {
            return certs;
        }
    }

    private Certificate[] retrieveMissingCertificates(Certificate[] certChain) {
        Map<String, Certificate> restoredChain = new LinkedHashMap<>();
        Certificate[] subChain;
        for (Certificate certificate : certChain) {
            subChain = issuingCertificateRetriever.retrieveMissingCertificates(new Certificate[]{certificate});
            for (Certificate cert : subChain) {
                restoredChain.put(((X509Certificate) cert).getSubjectX500Principal().getName(), cert);
            }
        }
        return restoredChain.values().toArray(new Certificate[0]);
    }


}
