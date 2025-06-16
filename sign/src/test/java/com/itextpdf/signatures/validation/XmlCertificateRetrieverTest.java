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
package com.itextpdf.signatures.validation;

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
import java.time.LocalDateTime;
import java.util.List;

@Tag("UnitTest")
public class XmlCertificateRetrieverTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/XmlCertificateRetrieverTest/";

    @Test
    public void readSingleCertificateTest() throws CertificateException, IOException {
        String xmlPath = SOURCE_FOLDER + "certificate.xml";
        String certPath = SOURCE_FOLDER + "certificate.pem";
        Certificate actualCertificate = new XmlCertificateRetriever(new XmlDefaultCertificateHandler())
                .getCertificates(xmlPath).get(0);
        Certificate expectedCertificate = PemFileHelper.readFirstChain(certPath)[0];
        Assertions.assertEquals(expectedCertificate, actualCertificate);
    }

    @Test
    public void readLotlCertificatesTest() {
        String xmlPath = SOURCE_FOLDER + "eu-lotl.xml";
        List<Certificate> certificateList = new XmlCertificateRetriever(new XmlDefaultCertificateHandler())
                .getCertificates(xmlPath);

        Assertions.assertEquals(142, certificateList.size());
    }

    @Test
    public void readPivotCertificatesTest() {
        String xmlPath = SOURCE_FOLDER + "eu-lotl-pivot-282.xml";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(
                new XmlDefaultCertificateHandler());
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(xmlPath);

        Assertions.assertEquals(126, certificateList.size());
        IServiceContext context = xmlCertificateRetriever.getServiceContext(certificateList.get(0));
        Assertions.assertNotNull(context);
        Assertions.assertTrue(context instanceof SimpleServiceContext);
    }

    @Test
    public void readAustriaCertificatesTest() {
        String xmlPath = SOURCE_FOLDER + "austriaTrustedList.xml";
        List<Certificate> certificateList = new XmlCertificateRetriever(new XmlDefaultCertificateHandler())
                .getCertificates(xmlPath);

        Assertions.assertEquals(104, certificateList.size());
    }

    @Test
    public void readBulgariaCertificatesTest() {
        String xmlPath = SOURCE_FOLDER + "BulgariaTrustedList.xml";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(new XmlCountryCertificateHandler());
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(xmlPath);

        Assertions.assertEquals(104, certificateList.size());
    }

    @Test
    public void readCzechiaCertificatesTest() {
        String xmlPath = SOURCE_FOLDER + "CzechiaTrustedList.txt";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever( new XmlCountryCertificateHandler());
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(xmlPath);

        Assertions.assertEquals(441, certificateList.size());
    }

    @Test
    public void readCyrpusCertificateContextTest() {
        String xmlPath = SOURCE_FOLDER + "cyprusTrustedList.xml";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(new XmlCountryCertificateHandler());
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(xmlPath);

        Assertions.assertEquals(8, certificateList.size());
        CountryServiceContext serviceContext = (CountryServiceContext) xmlCertificateRetriever.getServiceContext(
                certificateList.get(0));
        Assertions.assertEquals(2, serviceContext.getServiceStatusInfosSize());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/withdrawn",
                serviceContext.getCurrentStatusInfo().getServiceStatus());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/Svctype/CA/QC",
                serviceContext.getServiceType());
        Assertions.assertEquals(LocalDateTime.of(2021, 12, 16, 6, 0, 18),
                serviceContext.getCurrentStatusInfo().getServiceStatusStartingTime());
    }

    @Test
    public void readEstoniaCertificateContextTest() {
        String xmlPath = SOURCE_FOLDER + "estoniaTrustedList.xml";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(new XmlCountryCertificateHandler());
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(xmlPath);

        Assertions.assertEquals(64, certificateList.size());
        CountryServiceContext serviceContext = (CountryServiceContext) xmlCertificateRetriever.getServiceContext(
                certificateList.get(0));
        Assertions.assertEquals(3, serviceContext.getServiceStatusInfosSize());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/withdrawn",
                serviceContext.getCurrentStatusInfo().getServiceStatus());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/Svctype/CA/QC",
                serviceContext.getServiceType());
        Assertions.assertEquals(LocalDateTime.of(2017, 6, 30, 22, 0),
                serviceContext.getCurrentStatusInfo().getServiceStatusStartingTime());
        LocalDateTime previousStatusTime = LocalDateTime.of(2016, 6, 30, 22, 0);
        String previousStatus = serviceContext.getServiceStatusByDate(previousStatusTime);
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision",
                previousStatus);
    }

    @Test
    public void readHungaryCertificateContextTest() {
        String xmlPath = SOURCE_FOLDER + "HungaryTrustedList.xml";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(new XmlCountryCertificateHandler());
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(xmlPath);

            Assertions.assertEquals(346, certificateList.size());
        CountryServiceContext serviceContext = (CountryServiceContext) xmlCertificateRetriever.getServiceContext(
                certificateList.get(0));
        Assertions.assertEquals(3, serviceContext.getServiceStatusInfosSize());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/withdrawn",
                serviceContext.getCurrentStatusInfo().getServiceStatus());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/Svctype/CA/QC",
                serviceContext.getServiceType());
        Assertions.assertEquals(LocalDateTime.of(2016, 6, 30, 22, 0),
                serviceContext.getCurrentStatusInfo().getServiceStatusStartingTime());
    }


    @Test
    public void emptyXmlTest() {
        String xmlPath = SOURCE_FOLDER + "emptyXml.xml";
        XmlCertificateRetriever certificateRetriever = new XmlCertificateRetriever(new XmlDefaultCertificateHandler());

        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> certificateRetriever.getCertificates(xmlPath));
        Assertions.assertEquals(MessageFormatUtil.format(
                SignExceptionMessageConstant.FAILED_TO_READ_CERTIFICATE_BYTES_FROM_XML, xmlPath), exception.getMessage());
    }

    @Test
    public void invalidCertificateTest() {
        String xmlPath = SOURCE_FOLDER + "invalidCertificate.xml";
        XmlCertificateRetriever certificateRetriever = new XmlCertificateRetriever(new XmlDefaultCertificateHandler());

        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> certificateRetriever.getCertificates(xmlPath));
        Assertions.assertEquals(SignExceptionMessageConstant.FAILED_TO_RETRIEVE_CERTIFICATE, exception.getMessage());
    }
}
