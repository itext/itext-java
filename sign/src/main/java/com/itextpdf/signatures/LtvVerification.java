/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.kernel.PdfException;
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

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.OCSPRespBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add verification according to PAdES-LTV (part 4).
 *
 * @author Paulo Soares
 */
public class LtvVerification {

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
        this.acroForm = PdfAcroForm.getAcroForm(document, true);
        this.sgnUtil = new SignatureUtil(document);
    }

    /**
     * The verification constructor. This class should only be created with
     * PdfStamper.getLtvVerification() otherwise the information will not be
     * added to the Pdf.
     *
     * @param document The {@link PdfDocument} to apply the validation to.
     * @param securityProviderCode Security provider to use
     */
    public LtvVerification(PdfDocument document, String securityProviderCode){
        this(document);
        this.securityProviderCode = securityProviderCode;
    }

    /**
     * Add verification for a particular signature.
     *
     * @param signatureName the signature to validate (it may be a timestamp)
     * @param ocsp the interface to get the OCSP
     * @param crl the interface to get the CRL
     * @param certOption options as to how many certificates to include
     * @param level the validation options to include
     * @param certInclude certificate inclusion options
     * @return true if a validation was generated, false otherwise
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public boolean addVerification(String signatureName, IOcspClient ocsp, ICrlClient crl, CertificateOption certOption, Level level, CertificateInclusion certInclude) throws IOException, GeneralSecurityException {
        if (used)
            throw new IllegalStateException(PdfException.VerificationAlreadyOutput);
        PdfPKCS7 pk = sgnUtil.verifySignature(signatureName, securityProviderCode);
        LOGGER.info("Adding verification for " + signatureName);
        Certificate[] xc = pk.getCertificates();
        X509Certificate cert;
        X509Certificate signingCert = pk.getSigningCertificate();
        ValidationData vd = new ValidationData();
        for (int k = 0; k < xc.length; ++k) {
            cert = (X509Certificate)xc[k];
            LOGGER.info("Certificate: " + cert.getSubjectDN());
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
            if (crl != null && (level == Level.CRL || level == Level.OCSP_CRL || (level == Level.OCSP_OPTIONAL_CRL && ocspEnc == null))) {
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
        if (vd.crls.size() == 0 && vd.ocsps.size() == 0)
            return false;
        validated.put(getSignatureHashKey(signatureName), vd);
        return true;
    }

    /**
     * Get the issuing certificate for a child certificate.
     *
     * @param cert	the certificate for which we search the parent
     * @param certs	an array with certificates that contains the parent
     * @return the parent certificate
     */
    private X509Certificate getParent(X509Certificate cert, Certificate[] certs) {
        X509Certificate parent;
        for (int i = 0; i < certs.length; i++) {
            parent = (X509Certificate)certs[i];
            if (!cert.getIssuerDN().equals(parent.getSubjectDN()))
                continue;
            try {
                cert.verify(parent.getPublicKey());
                return parent;
            } catch (Exception e) {
                // do nothing
            }
        }
        return null;
    }

    /**
     * Adds verification to the signature.
     *
     * @param signatureName name of the signature
     * @param ocsps collection of ocsp responses
     * @param crls collection of crls
     * @param certs collection of certificates
     * @return boolean
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public boolean addVerification(String signatureName, Collection<byte[]> ocsps, Collection<byte[]> crls, Collection<byte[]> certs) throws IOException, GeneralSecurityException {
        if (used)
            throw new IllegalStateException(PdfException.VerificationAlreadyOutput);
        ValidationData vd = new ValidationData();
        if (ocsps != null) {
            for (byte[] ocsp : ocsps) {
                vd.ocsps.add(buildOCSPResponse(ocsp));
            }
        }
        if (crls != null) {
            for (byte[] crl : crls) {
                vd.crls.add(crl);
            }
        }
        if (certs != null) {
            for (byte[] cert : certs) {
                vd.certs.add(cert);
            }
        }
        validated.put(getSignatureHashKey(signatureName), vd);
        return true;
    }

    private static byte[] buildOCSPResponse(byte[] basicOcspResponse) throws IOException {
        DEROctetString doctet = new DEROctetString(basicOcspResponse);
        OCSPResponseStatus respStatus = new OCSPResponseStatus(OCSPRespBuilder.SUCCESSFUL);
        ResponseBytes responseBytes = new ResponseBytes(OCSPObjectIdentifiers.id_pkix_ocsp_basic, doctet);
        OCSPResponse ocspResponse = new OCSPResponse(respStatus, responseBytes);
        return new OCSPResp(ocspResponse).getEncoded();
    }

    private PdfName getSignatureHashKey(String signatureName) throws NoSuchAlgorithmException, IOException {
        PdfSignature sig = sgnUtil.getSignature(signatureName);
        PdfString contents = sig.getContents();
        byte[] bc = PdfEncodings.convertToBytes(contents.getValue(), null);
        byte[] bt = null;
        if (PdfName.ETSI_RFC3161.equals(sig.getSubFilter())) {
            ASN1InputStream din = new ASN1InputStream(new ByteArrayInputStream(bc));
            ASN1Primitive pkcs = din.readObject();
            bc = pkcs.getEncoded();
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
     * @throws IOException
     */
    public void merge() throws IOException {
        if (used || validated.size() == 0)
            return;
        used = true;
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfObject dss = catalog.get(PdfName.DSS);
        if (dss == null)
            createDss();
        else
            updateDss();
    }

    private void updateDss() throws IOException {
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
        //delete old validations
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
        if (all == null || toDelete == null)
            return;
        for (PdfObject pi : toDelete) {
            PdfIndirectReference pir = pi.getIndirectReference();

            if (pir == null) {
                continue;
            }

            for (int k = 0; k < all.size(); ++k) {
                PdfIndirectReference pod = all.get(k).getIndirectReference();

                if (pod == null) {
                    continue;
                }

                if (pir.getObjNumber() == pod.getObjNumber()) {
                    all.remove(k);
                    --k;
                }
            }
        }
    }

    private void createDss() throws IOException {
        outputDss(new PdfDictionary(), new PdfDictionary(), new PdfArray(), new PdfArray(), new PdfArray());
    }

    private void outputDss(PdfDictionary dss, PdfDictionary vrim, PdfArray ocsps, PdfArray crls, PdfArray certs) throws IOException {
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

    // TODO: Refactor. Copied from itext5 Utilities
    /**
     * Converts an array of bytes to a String of hexadecimal values
     *
     * @param bytes	a byte array
     * @return	the same bytes expressed as hexadecimal values
     */
    public static String convertToHex(byte[] bytes) {
        ByteBuffer buf = new ByteBuffer();
        for (byte b : bytes) {
            buf.appendHex(b);
        }
        return PdfEncodings.convertToString(buf.toByteArray(), null).toUpperCase();
    }
}
