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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.testutils.report.xml.XmlReportTestTool;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.NodeList;

import java.io.StringWriter;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

@Tag("BouncyCastleIntegrationTest")
class XmlReportGeneratorTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/signatures/validation/SignatureValidatorTest/";
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private ValidatorChainBuilder builder;

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @BeforeEach
    public void setUp() {
        builder = new ValidatorChainBuilder();
    }

    @Test
    public void baseXmlReportGenerationTest() throws Exception {
        try (PdfDocument document = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "docWithMultipleSignaturesAndTimeStamp.pdf"))) {
            AdESReportAggregator reportAggregator = new DefaultAdESReportAggregator();
            builder.withAdESReportAggregator(reportAggregator).buildSignatureValidator(document).validateSignatures();

            XmlReportGenerator reportGenerator = new XmlReportGenerator(new XmlReportOptions());
            StringWriter stringWriter = new StringWriter();
            reportGenerator.generate(reportAggregator.getReport(), stringWriter);

            XmlReportTestTool testTool = new XmlReportTestTool(stringWriter.toString());

            Assertions.assertEquals("ValidationReport", testTool.getDocumentNode().getNodeName());
            // There are 5 signatures, but 3 are timestamps
            Assertions.assertEquals(2, testTool.countElements("//r:SignatureValidationReport"));

            NodeList signatureValueNodes =
                    testTool.executeXpathAsNodeList("//r:SignatureValidationReport//ds:SignatureValue");
            List<String> b64ReportedSignatures = new ArrayList<>(signatureValueNodes.getLength());
            for (int i = 0; i < signatureValueNodes.getLength(); i++) {
                b64ReportedSignatures.add(signatureValueNodes.item(i).getTextContent());
            }

            SignatureUtil sigUtil = new SignatureUtil(document);
            for (String sigName : sigUtil.getSignatureNames()) {
                PdfSignature signature = sigUtil.getSignature(sigName);
                if (!PdfName.ETSI_RFC3161.equals(signature.getSubFilter())) {
                    CMSContainer cms = new CMSContainer(sigUtil.getSignature(sigName).getContents().getValueBytes());
                    String b64signature = Base64.encodeBytes(cms.getSignerInfo().getSignatureData());
                    Assertions.assertTrue(b64ReportedSignatures.contains(b64signature));
                }
            }

            // For each reported signature the certificate is added to the validation objects
            // We don't use something like
            // testTool.countElements("//r:ValidationObject[r:ObjectType=\"urn:etsi:019102:validationObject:certificate\"]");
            // here because it fails in native by not clear reason
            NodeList objectTypesNodes = testTool.executeXpathAsNodeList("//r:ValidationObject//r:ObjectType");
            int requiredObjectTypesCount = 0;
            for (int i = 0; i < objectTypesNodes.getLength(); i++) {
                if ("urn:etsi:019102:validationObject:certificate".equals(objectTypesNodes.item(i).getTextContent())) {
                    ++requiredObjectTypesCount;
                }
            }
            Assertions.assertEquals(2, requiredObjectTypesCount);

            Assertions.assertNull(testTool.validateXMLSchema());
        }
    }
}
