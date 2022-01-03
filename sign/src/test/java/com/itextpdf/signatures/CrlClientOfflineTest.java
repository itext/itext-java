/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import com.itextpdf.test.signutils.Pkcs12FileHelper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CrlClientOfflineTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/CrlClientOfflineTest/";
    private static final char[] PASSWORD = "password".toCharArray();
    private static final String CRL_DISTRIBUTION_POINT = "http://www.example.com/";

    private static X509Certificate checkCert;
    private static Collection<byte[]> listOfByteArrays;

    @Test
    public void checkUnknownPdfExceptionWhenCrlIsNull() {
        Exception e = Assert.assertThrows(PdfException.class,
                () -> listOfByteArrays = new CrlClientOffline((CRL) null).getEncoded(null, ""));
        Assert.assertEquals(KernelExceptionMessageConstant.UNKNOWN_PDF_EXCEPTION, e.getMessage());
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayRealArgsTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        validateCrlBytes(null, checkCert, CRL_DISTRIBUTION_POINT);
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayWithoutArgsTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        validateCrlBytes(null, null, "");
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayUrlIsEmptyTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        validateCrlBytes(null, checkCert, "");
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayNonExistingUrlTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        validateCrlBytes(null, checkCert, "http://nonexistingurl.com");
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayCertIsNullNonExistingUrlTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        validateCrlBytes(null, null, "http://nonexistingurl.com");
    }

    @Test
    public void getEncodedFromCrlEmptyByteArrayCertIsNullUrlIsRealTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        validateCrlBytes(null, null, CRL_DISTRIBUTION_POINT);
    }

    @Test
    public void getEncodedFromCrlObjectRealArgsTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, checkCert, CRL_DISTRIBUTION_POINT);
    }

    @Test
    public void getEncodedFromCrlObjectWithoutCertAndUrlTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, null, "");
    }

    @Test
    public void getEncodedFromCrlObjectUrlIsEmptyTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, checkCert, "");
    }

    @Test
    public void getEncodedFromCrlObjectNonExistingUrlTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, checkCert, "http://nonexistingurl.com");
    }

    @Test
    public void getEncodedFromCrlObjectCertIsNullNonExistingUrlTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, null, "http://nonexistingurl.com");
    }

    @Test
    public void getEncodedFromCrlObjectCertIsNullUrlIsRealTest() throws GeneralSecurityException, IOException {
        String fileName = SOURCE_FOLDER + "pdfWithCrl.pdf";
        byte[] testBytes = obtainCrlFromPdf(fileName);
        validateCrlBytes(testBytes, null, CRL_DISTRIBUTION_POINT);
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

    private static Collection<byte[]> validateCrlBytes(byte[] testBytes, X509Certificate checkCert, String crlDistPoint)
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        CrlClientOffline crlClientOffline = new CrlClientOffline(testBytes);
        checkCert = (X509Certificate) Pkcs12FileHelper
                .readFirstChain(SOURCE_FOLDER + "crlDistPoint.p12", PASSWORD)[0];

        listOfByteArrays = crlClientOffline.getEncoded(checkCert, crlDistPoint);

        //These checks are enough, because there is exactly one element in the collection,
        //and these are the same test bytes 
        Assert.assertEquals(1, listOfByteArrays.size());
        Assert.assertTrue(listOfByteArrays.contains(testBytes));
        return listOfByteArrays;
    }
}
