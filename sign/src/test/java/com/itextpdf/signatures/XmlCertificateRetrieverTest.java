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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;

@Tag("UnitTest")
public class XmlCertificateRetrieverTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/XmlCertificateRetrieverTest/";

    @Test
    public void readSingleCertificateTest() throws CertificateException, IOException {
        String xmlPath = SOURCE_FOLDER + "certificate.xml";
        String certPath = SOURCE_FOLDER + "certificate.pem";
        Certificate actualCertificate = XmlCertificateRetriever.getCertificates(xmlPath).get(0);
        Certificate expectedCertificate = PemFileHelper.readFirstChain(certPath)[0];
        Assertions.assertEquals(expectedCertificate, actualCertificate);
    }

    @Test
    public void readLotlCertificatesTest() {
        String xmlPath = SOURCE_FOLDER + "eu-lotl.xml";
        List<Certificate> certificateList = XmlCertificateRetriever.getCertificates(xmlPath);
        Assertions.assertEquals(142, certificateList.size());
    }

    @Test
    public void readPivotCertificatesTest() {
        String xmlPath = SOURCE_FOLDER + "eu-lotl-pivot-282.xml";
        List<Certificate> certificateList = XmlCertificateRetriever.getCertificates(xmlPath);
        Assertions.assertEquals(126, certificateList.size());
    }

    @Test
    public void readAustriaCertificatesTest() {
        String xmlPath = SOURCE_FOLDER + "ttlAustria.xml";
        List<Certificate> certificateList = XmlCertificateRetriever.getCertificates(xmlPath);
        Assertions.assertEquals(103, certificateList.size());
    }

    @Test
    public void emptyXmlTest() {
        String xmlPath = SOURCE_FOLDER + "emptyXml.xml";
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> XmlCertificateRetriever.getCertificates(xmlPath));
        Assertions.assertEquals(MessageFormatUtil.format(
                SignExceptionMessageConstant.FAILED_TO_READ_CERTIFICATE_BYTES_FROM_XML, xmlPath), exception.getMessage());
    }

    @Test
    public void invalidCertificateTest() {
        String xmlPath = SOURCE_FOLDER + "invalidCertificate.xml";
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> XmlCertificateRetriever.getCertificates(xmlPath));
        Assertions.assertEquals(SignExceptionMessageConstant.FAILED_TO_RETRIEVE_CERTIFICATE, exception.getMessage());
    }
}
