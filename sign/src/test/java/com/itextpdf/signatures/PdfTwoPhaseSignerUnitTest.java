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
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class PdfTwoPhaseSignerUnitTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final byte[] OWNER = "owner".getBytes(StandardCharsets.UTF_8);
    private static final byte[] USER = "user".getBytes(StandardCharsets.UTF_8);

    private static final String PDFA_RESOURCES = "./src/test/resources/com/itextpdf/signatures/pdfa/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/Pdf2PhaseSignerUnitTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();


    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void prepareDocumentTestWithSHA256() throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createSimpleDocument()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);

        int estimatedSize = 8079;
        SignerProperties signerProperties = new SignerProperties();
        byte[] digest = signer.prepareDocumentForSignature(signerProperties, DigestAlgorithms.SHA256, PdfName.Adobe_PPKLite,
                PdfName.Adbe_pkcs7_detached, estimatedSize, false);
        String fieldName = signerProperties.getFieldName();

        PdfReader resultReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()));
        PdfDocument resultDoc = new PdfDocument(resultReader);
        SignatureUtil signatureUtil = new SignatureUtil(resultDoc);
        PdfSignature signature = signatureUtil.getSignature(fieldName);
        Assertions.assertEquals(estimatedSize, signature.getContents().getValueBytes().length);
    }


    @Test
    public void prepareDocumentTestWithExternalDigest() throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createSimpleDocument()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);


        int estimatedSize = 8079;
        SignerProperties signerProperties = new SignerProperties();
        signer.setExternalDigest(new BouncyCastleDigest());
        byte[] digest = signer.prepareDocumentForSignature(signerProperties,  DigestAlgorithms.SHA256, PdfName.Adobe_PPKLite,
                PdfName.Adbe_pkcs7_detached, estimatedSize, false);

        String fieldName = signerProperties.getFieldName();
        PdfReader resultReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()));
        PdfDocument resultDoc = new PdfDocument(resultReader);
        SignatureUtil signatureUtil = new SignatureUtil(resultDoc);
        PdfSignature signature = signatureUtil.getSignature(fieldName);
        Assertions.assertEquals(estimatedSize, signature.getContents().getValueBytes().length);
    }

    @Test
    public void addSignatureToPreparedDocumentTest() throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createSimpleDocument()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfTwoPhaseSigner signer = new PdfTwoPhaseSigner(reader, outputStream);


        int estimatedSize = 8079;
        SignerProperties signerProperties = new SignerProperties();
        byte[] digest = signer.prepareDocumentForSignature(signerProperties, DigestAlgorithms.SHA256, PdfName.Adobe_PPKLite,
                PdfName.Adbe_pkcs7_detached, estimatedSize, false);
        String fieldName = signerProperties.getFieldName();

        PdfReader resultReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()));
        PdfDocument resultDoc = new PdfDocument(resultReader);

        ByteArrayOutputStream completedOutputStream = new ByteArrayOutputStream();
        byte[] testData = ByteUtils.getIsoBytes("Some data to test the signature addition with");
        PdfTwoPhaseSigner.addSignatureToPreparedDocument(resultDoc, fieldName, completedOutputStream, testData);

        resultReader = new PdfReader(new ByteArrayInputStream(completedOutputStream.toByteArray()));
        resultDoc = new PdfDocument(resultReader);

        SignatureUtil signatureUtil = new SignatureUtil(resultDoc);
        PdfSignature signature = signatureUtil.getSignature(fieldName);
        byte[] content = signature.getContents().getValueBytes();
        for (int i = 0; i < testData.length; i++) {
            Assertions.assertEquals(testData[i], content[i]);
        }
    }

    private static byte[] createSimpleDocument() {
        return createSimpleDocument(PdfVersion.PDF_1_7);
    }

    private static byte[] createSimpleDocument(PdfVersion version) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        WriterProperties writerProperties = new WriterProperties();
        if (null != version) {
            writerProperties.setPdfVersion(version);
        }
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream, writerProperties));
        document.addNewPage();
        document.close();
        return outputStream.toByteArray();
    }
}
