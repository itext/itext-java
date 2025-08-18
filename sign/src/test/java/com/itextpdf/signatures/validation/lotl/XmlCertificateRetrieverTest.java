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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.List;

@Tag("UnitTest")
public class XmlCertificateRetrieverTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/lotl"
            + "/XmlCertificateRetrieverTest/";

    @Test
    public void readSingleCertificateTest() throws CertificateException, IOException {
        String xmlPath = SOURCE_FOLDER + "certificate.xml";
        String certPath = SOURCE_FOLDER + "certificate.pem";
        Certificate actualCertificate = new XmlCertificateRetriever(new XmlDefaultCertificateHandler())
                .getCertificates(Files.newInputStream(Paths.get(xmlPath))).get(0);
        Certificate expectedCertificate = PemFileHelper.readFirstChain(certPath)[0];
        Assertions.assertEquals(expectedCertificate, actualCertificate);
    }

    @Test
    public void readLotlCertificatesTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "eu-lotl.xml";
        List<Certificate> certificateList = new XmlCertificateRetriever(new XmlDefaultCertificateHandler())
                .getCertificates(Files.newInputStream(Paths.get(xmlPath)));

        Assertions.assertEquals(142, certificateList.size());
    }

    @Test
    public void readPivotCertificatesTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "eu-lotl-pivot-282.xml";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(
                new XmlDefaultCertificateHandler());
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(
                Files.newInputStream(Paths.get(xmlPath)));

        Assertions.assertEquals(126, certificateList.size());
        IServiceContext context = xmlCertificateRetriever.getServiceContexts().get(0);
        Assertions.assertNotNull(context);
        Assertions.assertTrue(SimpleServiceContext.class == context.getClass());
    }

    @Test
    public void readAustriaCertificatesTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "austriaTrustedList.xml";
        List<Certificate> certificateList = new XmlCertificateRetriever(new XmlDefaultCertificateHandler())
                .getCertificates(Files.newInputStream(Paths.get(xmlPath)));

        Assertions.assertEquals(104, certificateList.size());
    }

    @Test
    public void readAustriaCertificatesWithDefinedServiceTypeTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "austriaTrustedList.xml";
        List<Certificate> certificateList = new XmlCertificateRetriever(new XmlCountryCertificateHandler(
                new HashSet<>(Arrays.asList("http://uri.etsi.org/TrstSvc/Svctype/CA/PKC",
                        "http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC"))))
                .getCertificates(Files.newInputStream(Paths.get(xmlPath)));

        Assertions.assertEquals(38, certificateList.size());
    }

    @Test
    public void readAustriaCertificatesWithInvalidServiceTypeTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "austriaTrustedList.xml";
        List<Certificate> certificateList = new XmlCertificateRetriever(new XmlCountryCertificateHandler(
                new HashSet<>(Collections.singletonList("Invalid"))))
                .getCertificates(Files.newInputStream(Paths.get(xmlPath)));

        Assertions.assertEquals(0, certificateList.size());
    }

    @Test
    public void readBulgariaCertificatesTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "BulgariaTrustedList.xml";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(
                new XmlCountryCertificateHandler(Collections.<String>emptySet()));
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(
                Files.newInputStream(Paths.get(xmlPath)));

        Assertions.assertEquals(104, certificateList.size());
    }

    @Test
    public void readCzechiaCertificatesTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "CzechiaTrustedList.txt";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(
                new XmlCountryCertificateHandler(Collections.<String>emptySet()));
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(
                Files.newInputStream(Paths.get(xmlPath)));

        Assertions.assertEquals(441, certificateList.size());
    }

    @Test
    public void readCyrpusCertificateContextTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "cyprusTrustedList.xml";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(
                new XmlCountryCertificateHandler(Collections.<String>emptySet()));
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(
                Files.newInputStream(Paths.get(xmlPath)));
        Assertions.assertEquals(8, certificateList.size());
        CountryServiceContext serviceContext = (CountryServiceContext) xmlCertificateRetriever.getServiceContexts()
                .get(0);
        Assertions.assertEquals(2, serviceContext.getServiceChronologicalInfosSize());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/withdrawn",
                serviceContext.getCurrentChronologicalInfo().getServiceStatus());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/Svctype/CA/QC",
                serviceContext.getServiceType());
        Assertions.assertEquals(LocalDateTime.of(2021, 12, 16, 6, 0, 18),
                serviceContext.getCurrentChronologicalInfo().getServiceStatusStartingTime());
    }

    @Test
    public void readEstoniaCertificateContextTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "estoniaTrustedList.xml";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(
                new XmlCountryCertificateHandler(Collections.<String>emptySet()));
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(
                Files.newInputStream(Paths.get(xmlPath)));

        Assertions.assertEquals(64, certificateList.size());
        CountryServiceContext serviceContext = (CountryServiceContext) xmlCertificateRetriever.getServiceContexts()
                .get(0);
        Assertions.assertEquals(3, serviceContext.getServiceChronologicalInfosSize());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/withdrawn",
                serviceContext.getCurrentChronologicalInfo().getServiceStatus());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/Svctype/CA/QC",
                serviceContext.getServiceType());
        Assertions.assertEquals(LocalDateTime.of(2017, 6, 30, 22, 0),
                serviceContext.getCurrentChronologicalInfo().getServiceStatusStartingTime());
        LocalDateTime previousStatusTime = LocalDateTime.of(2016, 6, 30, 22, 0);
        String previousStatus = serviceContext.getServiceChronologicalInfoByDate(previousStatusTime).getServiceStatus();
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision",
                previousStatus);
    }

    @Test
    public void readHungaryCertificateContextTest() throws IOException {
        String xmlPath = SOURCE_FOLDER + "HungaryTrustedList.xml";
        XmlCertificateRetriever xmlCertificateRetriever = new XmlCertificateRetriever(
                new XmlCountryCertificateHandler(Collections.<String>emptySet()));
        List<Certificate> certificateList = xmlCertificateRetriever.getCertificates(
                Files.newInputStream(Paths.get(xmlPath)));

        Assertions.assertEquals(346, certificateList.size());
        CountryServiceContext serviceContext = (CountryServiceContext) xmlCertificateRetriever.getServiceContexts()
                .get(0);
        Assertions.assertEquals(3, serviceContext.getServiceChronologicalInfosSize());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/withdrawn",
                serviceContext.getCurrentChronologicalInfo().getServiceStatus());
        Assertions.assertEquals("http://uri.etsi.org/TrstSvc/Svctype/CA/QC",
                serviceContext.getServiceType());
        Assertions.assertEquals(LocalDateTime.of(2016, 6, 30, 22, 0),
                serviceContext.getCurrentChronologicalInfo().getServiceStatusStartingTime());
    }


    @Test
    public void emptyXmlTest() {
        String xmlPath = SOURCE_FOLDER + "emptyXml.xml";
        XmlCertificateRetriever certificateRetriever = new XmlCertificateRetriever(new XmlDefaultCertificateHandler());

        //No checking for message as it is different for C# and java because of differences in library
        Assertions.assertThrows(PdfException.class,
                () -> certificateRetriever.getCertificates(Files.newInputStream(Paths.get(xmlPath))));
    }

    @Test
    public void invalidCertificateTest() {
        String xmlPath = SOURCE_FOLDER + "invalidCertificate.xml";
        XmlCertificateRetriever certificateRetriever = new XmlCertificateRetriever(new XmlDefaultCertificateHandler());

        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> certificateRetriever.getCertificates(Files.newInputStream(Paths.get(xmlPath))));
        Assertions.assertEquals(SignExceptionMessageConstant.FAILED_TO_RETRIEVE_CERTIFICATE, exception.getMessage());
    }
}
