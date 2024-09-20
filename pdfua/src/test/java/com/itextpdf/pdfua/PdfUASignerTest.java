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

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.function.Consumer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            signer.setSignerProperties(new SignerProperties().setFieldName("Signature12"));
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
            appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
            signer.getSignerProperties().setSignatureAppearance(appearance);
        });
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void visibleSignatureWithTUButNotAFont() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        String outPdf = generateSignature(inPdf, "visibleSignatureWithTUButNotAFont", (signer) -> {
            signer.setSignerProperties(new SignerProperties().setFieldName("Signature12"));
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
            appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
            try {
                appearance.setFont(PdfFontFactory.createFont(FONT));
            } catch (IOException e) {
                throw new RuntimeException();
            }
            appearance.setContent("Some signature content");
            signer.getSignerProperties()
                    .setPageNumber(1)
                    .setPageRect(new Rectangle(36, 648, 200, 100))
                    .setSignatureAppearance(appearance);
        });
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void visibleSignatureWithoutTUFont() {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            generateSignature(inPdf, "visibleSignatureWithoutTUFont", (signer) -> {
                signer.setSignerProperties(new SignerProperties().setFieldName("Signature12"));
                SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
                appearance.setContent(new SignedAppearanceText().setLocationLine("Dummy location").setReasonLine("Dummy reason").setSignedBy("Dummy"));

                try {
                    appearance.setFont(PdfFontFactory.createFont(FONT));
                } catch (IOException f) {
                    throw new RuntimeException();
                }

                signer.getSignerProperties()
                        .setPageNumber(1)
                        .setPageRect(new Rectangle(36, 648, 200, 100))
                        .setSignatureAppearance(appearance);
            });
        });
    }

    @Test
    public void visibleSignatureWithNoFontSelected() {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            generateSignature(inPdf, "visibleSignatureWithNoFontSelected", (signer) -> {
                signer.setSignerProperties(new SignerProperties().setFieldName("Signature12"));
                SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
                appearance.setContent("Some signature content");
                signer.getSignerProperties().setPageNumber(1).setPageRect(new Rectangle(36, 648, 200, 100));
                appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
                appearance.setContent(new SignedAppearanceText().setSignedBy("Dummy").setReasonLine("Dummy reason").setLocationLine("Dummy location"));
                signer.getSignerProperties().setSignatureAppearance(appearance);
            });
        });
    }


    @Test
    public void normalPdfSignerInvisibleSignatureWithTU() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        String outPdf = generateSignatureNormal(inPdf, "normalPdfSignerInvisibleSignatureWithTU", (signer) -> {
            signer.setSignerProperties(new SignerProperties().setFieldName("Signature12"));
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
            appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
            signer.getSignerProperties().setSignatureAppearance(appearance);
        });
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void normalPdfSignerInvisibleSignatureWithoutTU() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        String outPdf = generateSignatureNormal(inPdf, "normalPdfSignerInvisibleSignatureWithoutTU", (signer) -> {
            signer.setSignerProperties(new SignerProperties().setFieldName("Signature12"));
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
            signer.getSignerProperties().setSignatureAppearance(appearance);
        });
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    // TODO DEVSIX-8623 Spike: Get rid of PdfADocument, PdfUADocument, PdfAAgnosticDocument in favour of one PdfDocument
    public void normalPdfSignerVisibleSignatureWithoutFont() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        //This test should fail with the appropriate exception
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        String outPdf = generateSignatureNormal(inPdf, "normalPdfSignerVisibleSignatureWithoutFont", (signer) -> {
            signer.setSignerProperties(new SignerProperties().setFieldName("Signature12"));
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
            appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
            appearance.setContent(new SignedAppearanceText().setLocationLine("Dummy location").setReasonLine("Dummy reason").setSignedBy("Dummy"));
            signer.getSignerProperties()
                    .setPageNumber(1)
                    .setPageRect(new Rectangle(36, 648, 200, 100))
                    .setSignatureAppearance(appearance);
        });
        Assertions.assertNotNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }


    @Test
    public void normalPdfSignerVisibleSignatureWithFont() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        PdfFont font = PdfFontFactory.createFont(FONT);
        String outPdf = generateSignatureNormal(inPdf, "normalPdfSignerVisibleSignatureWithFont", (signer) -> {
            signer.setSignerProperties(new SignerProperties().setFieldName("Signature12"));
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
            appearance.getAccessibilityProperties().setAlternateDescription("Some alternate description");
            appearance.setContent(new SignedAppearanceText().setLocationLine("Dummy location").setReasonLine("Dummy reason").setSignedBy("Dummy"));
            appearance.setFont(font);
            signer.getSignerProperties()
                    .setPageNumber(1)
                    .setPageRect(new Rectangle(36, 648, 200, 100))
                    .setSignatureAppearance(appearance);
        });
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    // TODO DEVSIX-8623 Spike: Get rid of PdfADocument, PdfUADocument, PdfAAgnosticDocument in favour of one PdfDocument
    public void normalPdfSignerVisibleSignatureWithFontEmptyTU() throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        //Should throw the correct exception if the font is not set
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        PdfFont font = PdfFontFactory.createFont(FONT);
        String outPdf = generateSignatureNormal(inPdf, "normalPdfSignerVisibleSignatureWithFontEmptyTU", (signer) -> {
            signer.setSignerProperties(new SignerProperties().setFieldName("Signature12"));
            SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
            appearance.getAccessibilityProperties().setAlternateDescription("");
            appearance.setContent(new SignedAppearanceText().setLocationLine("Dummy location").setReasonLine("Dummy reason").setSignedBy("Dummy"));
            appearance.setFont(font);
            signer.getSignerProperties()
                    .setPageNumber(1)
                    .setPageRect(new Rectangle(36, 648, 200, 100))
                    .setSignatureAppearance(appearance);
        });
        Assertions.assertNotNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfSignerVisibleSignatureWithFontEmptyTU() throws IOException {
        //Should throw the correct exception if the font is not set
        ByteArrayInputStream inPdf = generateSimplePdfUA1Document();
        PdfFont font = PdfFontFactory.createFont(FONT);
        Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            generateSignature(inPdf, "pdfSignerVisibleSignatureWithFontEmptyTU", (signer) -> {
                signer.setSignerProperties(new SignerProperties().setFieldName("Signature12"));
                SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
                appearance.getAccessibilityProperties().setAlternateDescription("");
                appearance.setContent(new SignedAppearanceText().setLocationLine("Dummy location").setReasonLine("Dummy reason").setSignedBy("Dummy"));
                appearance.setFont(font);
                signer.getSignerProperties()
                        .setPageNumber(1)
                        .setPageRect(new Rectangle(36, 648, 200, 100))
                        .setSignatureAppearance(appearance);
            });
        });
    }


    private ByteArrayInputStream generateSimplePdfUA1Document() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfUADocument pdfUADocument = new PdfUADocument(new PdfWriter(out), new PdfUAConfig(PdfUAConformance.PDF_UA_1, "Title", "en-US"));
        pdfUADocument.addNewPage();
        pdfUADocument.close();
        return new ByteArrayInputStream(out.toByteArray());
    }


    private String generateSignature(ByteArrayInputStream inPdf, String name, Consumer<PdfSigner> signingAction) throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String certFileName = CERTIFICATE_FOLDER + "sign.pem";

        Security.addProvider(new BouncyCastleProvider());
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(certFileName, PASSWORD);
        IExternalSignature pks = new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        Certificate[] signChain = PemFileHelper.readFirstChain(certFileName);

        String outPdf = DESTINATION_FOLDER + name + ".pdf";
        PdfSigner signer = new PdfUaSigner(new PdfReader(inPdf), FileUtil.getFileOutputStream(outPdf), new StampingProperties());


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
        Certificate[] signChain = PemFileHelper.readFirstChain(certFileName);

        String outPdf = DESTINATION_FOLDER + name + ".pdf";
        PdfSigner signer = new PdfSigner(new PdfReader(inPdf), FileUtil.getFileOutputStream(outPdf), new StampingProperties());

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
            return new PdfUADocument(reader, writer, new PdfUAConfig(PdfUAConformance.PDF_UA_1, "Title", "en-US"));
        }
    }


}
