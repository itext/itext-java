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
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MapUtil;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class TestSignUtils {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    public static void assertDssDict(InputStream inputStream, Map<String, Integer> expectedNumberOfCrls,
            Map<String, Integer> expectedNumberOfOcsp, List<String> expectedCerts)
            throws IOException, CertificateException, CRLException, AbstractOCSPException {
        try (PdfDocument outDocument = new PdfDocument(new PdfReader(inputStream))) {
            PdfDictionary dss = outDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
            
            PdfArray certs = dss.getAsArray(PdfName.Certs) == null ? new PdfArray() : dss.getAsArray(PdfName.Certs);
            List<String> realCertificates = createCertsList(certs);

            PdfArray crls = dss.getAsArray(PdfName.CRLs) == null ? new PdfArray() : dss.getAsArray(PdfName.CRLs);
            Map<String, Integer> realNumberOfCrls = createCrlMap(crls);

            PdfArray ocsps = dss.getAsArray(PdfName.OCSPs) == null ? new PdfArray() : dss.getAsArray(PdfName.OCSPs);
            Map<String, Integer> realNumberOfOcsp = createOcspMap(ocsps);

            if (expectedCerts != null) {
                Assertions.assertEquals(realCertificates.size(), expectedCerts.size(),
                        "Certs entry in DSS dictionary isn't correct");
                // Order agnostic list comparison.
                for (String expectedCert : expectedCerts) {
                    Assertions.assertTrue(realCertificates.stream().anyMatch(cert -> cert.equals(expectedCert)));
                }
            }
            Assertions.assertTrue(MapUtil.equals(expectedNumberOfCrls, realNumberOfCrls),
                    "CRLs entry in DSS dictionary isn't correct");
            Assertions.assertTrue(MapUtil.equals(expectedNumberOfOcsp, realNumberOfOcsp),
                    "OCSPs entry in DSS dictionary isn't correct");
        }
    }

    public static void assertDssDict(InputStream inputStream, Map<String, Integer> expectedNumberOfCrls,
            Map<String, Integer> expectedNumberOfOcsp)
            throws IOException, CertificateException, CRLException, AbstractOCSPException {
        assertDssDict(inputStream, expectedNumberOfCrls, expectedNumberOfOcsp, null);
    }
    
    public static void basicCheckSignedDoc(String filePath, String signatureName) throws GeneralSecurityException, IOException {
        try (InputStream inputStream = FileUtil.getInputStreamForFile(filePath)) {
            basicCheckSignedDoc(inputStream, signatureName);
        }
    }

    public static void basicCheckSignedDoc(InputStream inputStream, String signatureName) throws GeneralSecurityException, IOException {
        try (PdfDocument outDocument = new PdfDocument(new PdfReader(inputStream))) {
            SignatureUtil sigUtil = new SignatureUtil(outDocument);
            PdfPKCS7 signatureData = sigUtil.readSignatureData(signatureName);
            Assertions.assertTrue(signatureData.verifySignatureIntegrityAndAuthenticity());
        }
    }
    
    public static void signedDocumentContainsCerts(InputStream inputStream, List<X509Certificate> expectedCertificates,
            String signatureName)
            throws IOException {
        try (PdfDocument outDocument = new PdfDocument(new PdfReader(inputStream))) {
            SignatureUtil sigUtil = new SignatureUtil(outDocument);
            List<Certificate> actualCertificates = Arrays.asList(sigUtil.readSignatureData(signatureName).getCertificates());
            // Searching for every certificate we expect should be in the resulting document.
            Assertions.assertEquals(expectedCertificates.size(), actualCertificates.size());
            for (X509Certificate expectedCert : expectedCertificates) {
                Assertions.assertTrue(actualCertificates.stream().anyMatch(cert ->
                        ((X509Certificate) cert).getSubjectX500Principal().equals(expectedCert.getSubjectX500Principal())));
            }
        }
    }
    
    private static List<String> createCertsList(PdfArray certs) throws CertificateException {
        List<String> certsNames = new ArrayList<>();
        for (PdfObject cert : certs) {
            PdfStream certStream = (PdfStream) cert;
            byte[] certBytes = certStream.getBytes();
            X509Certificate certificate = (X509Certificate) SignUtils.readAllCerts(certBytes).toArray(new Certificate[0])[0];
            certsNames.add(certificate.getSubjectX500Principal().getName());
        }
        return certsNames;
    }

    private static Map<String, Integer> createCrlMap(PdfArray crls) throws CertificateException, CRLException {
        Map<String, Integer> realNumberOfCrls = new HashMap<>();
        for (PdfObject crl : crls) {
            PdfStream crlStream = (PdfStream) crl;
            byte[] crlBytes = crlStream.getBytes(true);
            X509CRL crlObj = (X509CRL) SignUtils.parseCrlFromStream(new ByteArrayInputStream(crlBytes));
            String x500Principal = crlObj.getIssuerX500Principal().getName();
            Integer currentAmount =
                    realNumberOfCrls.get(x500Principal) == null ? 0 : realNumberOfCrls.get(x500Principal);
            realNumberOfCrls.put(x500Principal, currentAmount + 1);
        }
        return realNumberOfCrls;
    }

    private static Map<String, Integer> createOcspMap(PdfArray ocsps) throws IOException, AbstractOCSPException {
        Map<String, Integer> realNumberOfOcsp = new HashMap<>();
        for (PdfObject ocsp : ocsps) {
            PdfStream ocspStream = (PdfStream) ocsp;
            byte[] ocspBytes = ocspStream.getBytes(true);
            IOCSPResp ocspResp = FACTORY.createOCSPResp(ocspBytes);
            IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(ocspResp.getResponseObject());
            Iterable<X509Certificate> certs = SignUtils.getCertsFromOcspResponse(basicOCSPResp);
            for (X509Certificate cert : certs) {
                String x500Principal = cert.getSubjectX500Principal().getName();
                Integer currentAmount =
                        realNumberOfOcsp.get(x500Principal) == null ? 0 : realNumberOfOcsp.get(x500Principal);
                realNumberOfOcsp.put(x500Principal, currentAmount + 1);
            }
        }
        return realNumberOfOcsp;
    }
}
