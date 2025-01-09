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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CrlClientOfflineTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/CrlClientOfflineTest/";
    private static final String CRL_DISTRIBUTION_POINT = "http://www.example.com/";
    
    private static Collection<byte[]> listOfByteArrays;

    @Test
    public void checkUnknownPdfExceptionWhenCrlIsNull() {
        Exception e = Assertions.assertThrows(PdfException.class,
                () -> listOfByteArrays = new CrlClientOffline(
                        BouncyCastleFactoryCreator.getFactory().createNullCrl()).getEncoded(null, ""));
        Assertions.assertEquals(KernelExceptionMessageConstant.UNKNOWN_PDF_EXCEPTION, e.getMessage());
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayRealArgsTest() throws CertificateException, IOException {
        validateCrlBytes(null, CRL_DISTRIBUTION_POINT);
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayWithoutArgsTest() throws CertificateException, IOException {
        validateCrlBytes(null, "");
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayUrlIsEmptyTest() throws CertificateException, IOException {
        validateCrlBytes(null, "");
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayNonExistingUrlTest() throws CertificateException, IOException {
        validateCrlBytes(null, "http://nonexistingurl.com");
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayCertIsNullNonExistingUrlTest() throws CertificateException, IOException {
        validateCrlBytes(null, "http://nonexistingurl.com");
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayCertIsNullUrlIsRealTest() throws CertificateException, IOException {
        validateCrlBytes(null, CRL_DISTRIBUTION_POINT);
    }

    @Test
    public void getEncodedFromCrlObjectRealArgsTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, CRL_DISTRIBUTION_POINT);
    }

    @Test
    public void getEncodedFromCrlObjectWithoutCertAndUrlTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, "");
    }

    @Test
    public void getEncodedFromCrlObjectUrlIsEmptyTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, "");
    }

    @Test
    public void getEncodedFromCrlObjectNonExistingUrlTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, "http://nonexistingurl.com");
    }

    @Test
    public void getEncodedFromCrlObjectCertIsNullNonExistingUrlTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, "http://nonexistingurl.com");
    }

    @Test
    public void getEncodedFromCrlObjectCertIsNullUrlIsRealTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, CRL_DISTRIBUTION_POINT);
    }

    //Get CRL from PDF. We expect the PDF to contain an array of CRLs from which we only take the first
    private static byte[] obtainCrlFromPdf(String fileName)
            throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(fileName));
        PdfDictionary pdfDictionary = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        PdfArray crlArray = pdfDictionary.getAsArray(PdfName.CRLs);
        PdfStream stream = crlArray.getAsStream(0);
        return stream.getBytes();
    }

    private static void validateCrlBytes(byte[] testBytes, String crlDistPoint)
            throws CertificateException, IOException {
        CrlClientOffline crlClientOffline = new CrlClientOffline(testBytes);
        X509Certificate checkCert =
                (X509Certificate) PemFileHelper.readFirstChain(SOURCE_FOLDER + "crlDistPoint.pem")[0];

        listOfByteArrays = crlClientOffline.getEncoded(checkCert, crlDistPoint);

        //These checks are enough, because there is exactly one element in the collection,
        //and these are the same test bytes 
        Assertions.assertEquals(1, listOfByteArrays.size());
        Assertions.assertTrue(listOfByteArrays.contains(testBytes));
    }
}
