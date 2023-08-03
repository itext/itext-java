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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteBuffer;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add verification according to PAdES-LTV (part 4).
 *
 * @author Paulo Soares
 */
public class LtvVerification {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private Logger LOGGER = LoggerFactory.getLogger(LtvVerification.class);

    private PdfDocument document;
    private SignatureUtil sgnUtil;
    private PdfAcroForm acroForm;
    private Map<PdfName, ValidationData> validated = new HashMap<>();
    private boolean used = false;
    private String securityProviderCode = null;

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
        WHOLE_CHAIN
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
     * The verification constructor. This class should only be created with
     * PdfStamper.getLtvVerification() otherwise the information will not be
     * added to the Pdf.
     *
     * @param document The {@link PdfDocument} to apply the validation to.
     */
    public LtvVerification(PdfDocument document) {
        this.document = document;
        this.acroForm = PdfFormCreator.getAcroForm(document, true);
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
            Level level, CertificateInclusion certInclude) throws IOException, GeneralSecurityException {
        if (used) {
            throw new IllegalStateException(SignExceptionMessageConstant.VERIFICATION_ALREADY_OUTPUT);
        }
        PdfPKCS7 pk = sgnUtil.readSignatureData(signatureName, securityProviderCode);
        LOGGER.info("Adding verification for " + signatureName);
        Certificate[] xc = pk.getCertificates();
        X509Certificate cert;
        X509Certificate signingCert = pk.getSigningCertificate();
        ValidationData vd = new ValidationData();
        for (Certificate certificate : xc) {
            cert = (X509Certificate) certificate;
            LOGGER.info(MessageFormatUtil.format("Certificate: {0}", BOUNCY_CASTLE_FACTORY.createX500Name(cert)));
            if (certOption == CertificateOption.SIGNING_CERTIFICATE
                    && !cert.equals(signingCert)) {
                continue;
            }
            byte[] ocspEnc = null;
            if (ocsp != null && level != Level.CRL) {
                ocspEnc = ocsp.getEncoded(cert, getParent(cert, xc), null);
                if (ocspEnc != null) {
                    vd.ocsps.add(buildOCSPResponse(ocspEnc));
                    LOGGER.info("OCSP added");
                }
            }
            if (crl != null
                    && (level == Level.CRL || level == Level.OCSP_CRL
                        || (level == Level.OCSP_OPTIONAL_CRL && ocspEnc == null))) {
                Collection<byte[]> cims = crl.getEncoded(cert, null);
                if (cims != null) {
                    for (byte[] cim : cims) {
                        boolean dup = false;
                        for (byte[] b : vd.crls) {
                            if (Arrays.equals(b, cim)) {
                                dup = true;
                                break;
                            }
                        }
                        if (!dup) {
                            vd.crls.add(cim);
                            LOGGER.info("CRL added");
                        }
                    }
                }
            }
            if (certInclude == CertificateInclusion.YES) {
                vd.certs.add(cert.getEncoded());
            }
        }
        if (vd.crls.size() == 0 && vd.ocsps.size() == 0) {
            return false;
        }
        validated.put(getSignatureHashKey(signatureName), vd);
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
                vd.ocsps.add(buildOCSPResponse(ocsp));
            }
        }
        if (crls != null) {
            vd.crls.addAll(crls);
        }
        if (certs != null) {
            vd.certs.addAll(certs);
        }
        validated.put(getSignatureHashKey(signatureName), vd);
        return true;
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
            if (!cert.getIssuerDN().equals(parent.getSubjectDN())) {
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

    private static byte[] buildOCSPResponse(byte[] basicOcspResponse) throws IOException {
        IDEROctetString doctet = BOUNCY_CASTLE_FACTORY.createDEROctetString(basicOcspResponse);
        IOCSPResponseStatus respStatus = BOUNCY_CASTLE_FACTORY.createOCSPResponseStatus(
                BOUNCY_CASTLE_FACTORY.createOCSPRespBuilderInstance().getSuccessful());
        IResponseBytes responseBytes = BOUNCY_CASTLE_FACTORY.createResponseBytes(
                BOUNCY_CASTLE_FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspBasic(), doctet);
        IOCSPResponse ocspResponse = BOUNCY_CASTLE_FACTORY.createOCSPResponse(respStatus, responseBytes);
        return BOUNCY_CASTLE_FACTORY.createOCSPResp(ocspResponse).getEncoded();
    }

    private PdfName getSignatureHashKey(String signatureName) throws NoSuchAlgorithmException, IOException {
        PdfSignature sig = sgnUtil.getSignature(signatureName);
        PdfString contents = sig.getContents();
        byte[] bc = PdfEncodings.convertToBytes(contents.getValue(), null);
        byte[] bt = null;
        if (PdfName.ETSI_RFC3161.equals(sig.getSubFilter())) {
            try (IASN1InputStream din = BOUNCY_CASTLE_FACTORY.createASN1InputStream(new ByteArrayInputStream(bc))) {
                IASN1Primitive pkcs = din.readObject();
                bc = pkcs.getEncoded();
            }
        }
        bt = hashBytesSha1(bc);
        return new PdfName(convertToHex(bt));
    }

    private static byte[] hashBytesSha1(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest sh = MessageDigest.getInstance("SHA1");
        return sh.digest(b);
    }

    /**
     * Merges the validation with any validation already in the document or creates a new one.
     */
    public void merge() {
        if (used || validated.size() == 0) {
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
            for (byte[] b : validated.get(vkey).crls) {
                PdfStream ps = new PdfStream(b);
                ps.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
                ps.makeIndirect(document);
                crl.add(ps);
                crls.add(ps);
                crls.setModified();
            }
            for (byte[] b : validated.get(vkey).ocsps) {
                PdfStream ps = new PdfStream(b);
                ps.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
                ocsp.add(ps);
                ocsps.add(ps);
                ocsps.setModified();
            }
            for (byte[] b : validated.get(vkey).certs) {
                PdfStream ps = new PdfStream(b);
                ps.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
                ps.makeIndirect(document);
                cert.add(ps);
                certs.add(ps);
                certs.setModified();
            }
            if (ocsp.size() > 0) {
                ocsp.makeIndirect(document);
                vri.put(PdfName.OCSP, ocsp);
            }
            if (crl.size() > 0) {
                crl.makeIndirect(document);
                vri.put(PdfName.CRL, crl);
            }
            if (cert.size() > 0) {
                cert.makeIndirect(document);
                vri.put(PdfName.Cert, cert);
            }
            vri.makeIndirect(document);
            vrim.put(vkey, vri);
        }
        vrim.makeIndirect(document);
        vrim.setModified();
        dss.put(PdfName.VRI, vrim);
        if (ocsps.size() > 0) {
            ocsps.makeIndirect(document);
            dss.put(PdfName.OCSPs, ocsps);
        }
        if (crls.size() > 0) {
            crls.makeIndirect(document);
            dss.put(PdfName.CRLs, crls);
        }
        if (certs.size() > 0) {
            certs.makeIndirect(document);
            dss.put(PdfName.Certs, certs);
        }

        dss.makeIndirect(document);
        dss.setModified();
        catalog.put(PdfName.DSS, dss);
    }

    private static class ValidationData {
        public List<byte[]> crls = new ArrayList<>();
        public List<byte[]> ocsps = new ArrayList<>();
        public List<byte[]> certs = new ArrayList<>();
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
}
