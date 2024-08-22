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
package com.itextpdf.pdfua;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.openssl.IPEMParser;
import com.itextpdf.commons.bouncycastle.openssl.jcajce.IJcaPEMKeyConverter;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IInputDecryptorProvider;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.bouncycastle.pkcs.IPKCS8EncryptedPrivateKeyInfo;
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.signatures.*;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.util.function.Consumer;

@Tag("IntegrationTest")
public class PdfUASignerTest extends ExtendedITextTest {


    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUASignerTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final Logger logger = LoggerFactory.getLogger(PdfUASignerTest.class);

    public static final String CERTIFICATE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/certificates/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();



    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }


    @Test
    public void invisibleSignatureWithNoTU() {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            generateSignature(inPdf, "invisibleSignatureWithNoTU", (signer) -> {
            });
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.MISSING_FORM_FIELD_DESCRIPTION, e.getMessage());
    }

    @Test
    public void invisibleSignatureWithTU() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        String outPdf = generateSignature(inPdf, "invisibleSignatureWithTU", (signer) -> {
            signer.setFieldName("Signature12");
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName());
            appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
            signer.setSignatureAppearance(appearance);
        });
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf));
    }

    @Test
    public void visibleSignatureWithTUButNotAFont() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        String outPdf = generateSignature(inPdf, "visibleSignatureWithTUButNotAFont", (signer) -> {
            signer.setFieldName("Signature12");
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName());
            appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
            try {
                appearance.setFont(PdfFontFactory.createFont(FONT));
            } catch (IOException e) {
                throw new RuntimeException();
            }
            appearance.setContent("Some signature content");
            signer.setPageNumber(1).setPageRect(new Rectangle(36, 648, 200, 100));
            signer.setSignatureAppearance(appearance);
        });
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf));
    }

    @Test
    public void visibleSignatureWithoutTUFont() {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            generateSignature(inPdf, "visibleSignatureWithoutTUFont", (signer) -> {
                signer.setFieldName("Signature12");
                SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName());
                appearance.setContent(new SignedAppearanceText().setLocationLine("Dummy location").setReasonLine("Dummy reason").setSignedBy("Dummy"));

                try {
                    appearance.setFont(PdfFontFactory.createFont(FONT));
                } catch (IOException f) {
                    throw new RuntimeException();
                }

                signer.setPageNumber(1).setPageRect(new Rectangle(36, 648, 200, 100));
                signer.setSignatureAppearance(appearance);
            });
        });
    }

    @Test
    public void visibleSignatureWithNoFontSelected() {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            generateSignature(inPdf, "visibleSignatureWithNoFontSelected", (signer) -> {
                signer.setFieldName("Signature12");
                SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName());
                appearance.setContent("Some signature content");
                signer.setPageNumber(1).setPageRect(new Rectangle(36, 648, 200, 100));
                appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
                appearance.setContent(new SignedAppearanceText().setSignedBy("Dummy").setReasonLine("Dummy reason").setLocationLine("Dummy location"));
                signer.setSignatureAppearance(appearance);
            });
        });
    }


    @Test
    public void normalPdfSignerInvisibleSignatureWithTU() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        String outPdf = generateSignatureNormal(inPdf, "normalPdfSignerInvisibleSignatureWithTU", (signer) -> {
            signer.setFieldName("Signature12");
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName());
            appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
            signer.setSignatureAppearance(appearance);
        });
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf));
    }

    @Test
    public void normalPdfSignerInvisibleSignatureWithoutTU() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        String outPdf = generateSignatureNormal(inPdf, "normalPdfSignerInvisibleSignatureWithoutTU", (signer) -> {
            signer.setFieldName("Signature12");
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName());
            signer.setSignatureAppearance(appearance);
        });
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf));
    }

    @Test
    @Disabled("DEVSIX-8571")
    public void normalPdfSignerVisibleSignatureWithoutFont() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        //This test should fail with the appropriate exception
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        String outPdf = generateSignatureNormal(inPdf, "normalPdfSignerVisibleSignatureWithoutFont", (signer) -> {
            signer.setFieldName("Signature12");
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName());
            appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
            appearance.setContent(new SignedAppearanceText().setLocationLine("Dummy location").setReasonLine("Dummy reason").setSignedBy("Dummy"));
            signer.setPageNumber(1).setPageRect(new Rectangle(36, 648, 200, 100));
            signer.setSignatureAppearance(appearance);
        });
        Assertions.assertNotNull(new VeraPdfValidator().validate(outPdf));
    }


    @Test
    public void normalPdfSignerVisibleSignatureWithFont() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        PdfFont font = PdfFontFactory.createFont(FONT);
        String outPdf = generateSignatureNormal(inPdf, "normalPdfSignerVisibleSignatureWithFont", (signer) -> {
            signer.setFieldName("Signature12");
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName());
            appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
            appearance.setContent(new SignedAppearanceText().setLocationLine("Dummy location").setReasonLine("Dummy reason").setSignedBy("Dummy"));
            appearance.setFont(font);
            signer.setPageNumber(1).setPageRect(new Rectangle(36, 648, 200, 100));
            signer.setSignatureAppearance(appearance);
        });
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf));
    }

    @Test
    @Disabled("DEVSIX-8571")
    public void normalPdfSignerVisibleSignatureWithFontEmptyTU() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        //Should throw the correct exception if the font is not set
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        PdfFont font = PdfFontFactory.createFont(FONT);
        String outPdf = generateSignatureNormal(inPdf, "normalPdfSignerVisibleSignatureWithFontEmptyTU", (signer) -> {
            signer.setFieldName("Signature12");
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName());
            appearance.getAccessibilityProperties().setAlternateDescription("");
            appearance.setContent(new SignedAppearanceText().setLocationLine("Dummy location").setReasonLine("Dummy reason").setSignedBy("Dummy"));
            appearance.setFont(font);
            signer.setPageNumber(1).setPageRect(new Rectangle(36, 648, 200, 100));
            signer.setSignatureAppearance(appearance);
        });
        Assertions.assertNotNull(new VeraPdfValidator().validate(outPdf));
    }

    @Test
    public void pdfSignerVisibleSignatureWithFontEmptyTU() throws IOException {
        //Should throw the correct exception if the font is not set
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        PdfFont font = PdfFontFactory.createFont(FONT);
        Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            generateSignature(inPdf, "pdfSignerVisibleSignatureWithFontEmptyTU", (signer) -> {
                signer.setFieldName("Signature12");
                SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName());
                appearance.getAccessibilityProperties().setAlternateDescription("");
                appearance.setContent(new SignedAppearanceText().setLocationLine("Dummy location").setReasonLine("Dummy reason").setSignedBy("Dummy"));
                appearance.setFont(font);
                signer.setPageNumber(1).setPageRect(new Rectangle(36, 648, 200, 100));
                signer.setSignatureAppearance(appearance);
            });
        });
    }


    private ByteArrayInputStream generateSimplePdfUA1Document() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfUADocument pdfUADocument = new PdfUADocument(new PdfWriter(out), new PdfUAConfig(PdfUAConformanceLevel.PDFUA_1, "Title", "en-US"));
        pdfUADocument.addNewPage();
        pdfUADocument.close();
        return new ByteArrayInputStream(out.toByteArray());
    }


    private String generateSignature(ByteArrayInputStream inPdf, String name, Consumer<PdfSigner> signingAction) throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String certFileName = CERTIFICATE_FOLDER + "sign.pem";

        Security.addProvider(new BouncyCastleProvider());
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certFileName , PASSWORD);
        IExternalSignature pks = new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        Certificate[] signChain = PemFileHelper.readFirstChain(certFileName);

        String outPdf = DESTINATION_FOLDER + name + ".pdf";
        PdfSigner signer = new PdfUaSigner(new PdfReader(inPdf), new FileOutputStream(outPdf), new StampingProperties());


        signingAction.accept(signer);
        signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);
        logger.info("Out pdf: " + UrlUtil.getNormalizedFileUriString(outPdf));
        return outPdf;
    }


    private String generateSignatureNormal(ByteArrayInputStream inPdf, String name, Consumer<PdfSigner> signingAction) throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String certFileName = CERTIFICATE_FOLDER + "sign.pem";

        Security.addProvider(new BouncyCastleProvider());
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certFileName, PASSWORD);
        IExternalSignature pks = new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        Certificate[] signChain = PemFileHelper.readFirstChain(certFileName );

        String outPdf = DESTINATION_FOLDER + name + ".pdf";
        PdfSigner signer = new PdfSigner(new PdfReader(inPdf), new FileOutputStream(outPdf), new StampingProperties());


        signingAction.accept(signer);
        signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        logger.info("Out pdf: " + UrlUtil.getNormalizedFileUriString(outPdf));
        return outPdf;
    }


    static class PdfUaSigner extends PdfSigner {

        public PdfUaSigner(PdfReader reader, OutputStream outputStream, StampingProperties properties) throws IOException {
            super(reader, outputStream, properties);
        }

        public PdfUaSigner(PdfReader reader, OutputStream outputStream, String path, StampingProperties stampingProperties, SignerProperties signerProperties) throws IOException {
            super(reader, outputStream, path, stampingProperties, signerProperties);
        }

        public PdfUaSigner(PdfReader reader, OutputStream outputStream, String path, StampingProperties properties) throws IOException {
            super(reader, outputStream, path, properties);
        }

        @Override
        protected PdfDocument initDocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
            return new PdfUADocument(reader, writer, new PdfUAConfig(PdfUAConformanceLevel.PDFUA_1, "Title", "en-US"));
        }
    }


}
