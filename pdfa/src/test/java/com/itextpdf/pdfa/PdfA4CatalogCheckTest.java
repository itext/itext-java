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
package com.itextpdf.pdfa;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.List;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfA4CatalogCheckTest  extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA4CatalogCheckTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA4CatalogCheckTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
        Security.addProvider(BouncyCastleFactoryCreator.getFactory().getProvider());
    }

    @Test
    public void createSimpleDocTest() throws IOException {
        String outPdf = destinationFolder + "pdfA4_catalogCheck01.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        doc.close();

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void createSimpleTaggedDocTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA4_tagged.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfA4CatalogCheckTest/cmp_pdfA4_tagged.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument pdfDoc = (PdfADocument) new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is)).setTagged();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        Document document = new Document(pdfDoc);
        document.setFont(font);
        List list = new List();
        list.add("123");

        document.add(list);
        Assertions.assertEquals(PdfVersion.PDF_2_0, pdfDoc.getTagStructureContext().getTagStructureTargetVersion());
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void invalidCatalogVersionCheckTest() throws IOException {
        String outPdf = destinationFolder + "pdfA4_catalogCheck02.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        doc.getCatalog().put(PdfName.Version, new PdfString("1.7"));

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_CATALOG_VERSION_SHALL_CONTAIN_RIGHT_PDF_VERSION, "2"),
                e.getMessage());
    }

    @Test
    public void encryptInTrailerTest() throws IOException {
        String outPdf = destinationFolder + "pdfA4_catalogCheck03.pdf";

        byte[] userPassword = "user".getBytes(StandardCharsets.ISO_8859_1);
        byte[] ownerPassword = "owner".getBytes(StandardCharsets.ISO_8859_1);
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS | EncryptionConstants.ALLOW_DEGRADED_PRINTING;
        PdfWriter writer = new PdfWriter(outPdf,
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                        .setStandardEncryption(userPassword, ownerPassword, permissions, EncryptionConstants.ENCRYPTION_AES_256).setFullCompressionMode(false));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.KEYWORD_ENCRYPT_SHALL_NOT_BE_USED_IN_THE_TRAILER_DICTIONARY,
                e.getMessage());
    }

    @Test
    public void encryptedDocumentTest() throws IOException {
        String outPdf = destinationFolder + "pdfA4_encryptedDocument.pdf";

        byte[] userPassword = "user".getBytes(StandardCharsets.ISO_8859_1);
        byte[] ownerPassword = "owner".getBytes(StandardCharsets.ISO_8859_1);
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS | EncryptionConstants.ALLOW_DEGRADED_PRINTING;
        PdfWriter writer = new PdfWriter(outPdf,
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                        .setStandardEncryption(userPassword, ownerPassword, permissions, EncryptionConstants.ENCRYPTION_AES_256).setFullCompressionMode(true));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.KEYWORD_ENCRYPT_SHALL_NOT_BE_USED_IN_THE_TRAILER_DICTIONARY,
                e.getMessage());
    }

    @Test
    public void absentPieceInfoTest() throws IOException {
        String outPdf = destinationFolder + "pdfA4_catalogCheck04.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        PdfDictionary info = new PdfDictionary();
        final String timeValue = "D:19860426012347+04'00'";
        info.put(PdfName.ModDate, new PdfDate(PdfDate.decode(timeValue)).getPdfObject());
        doc.getTrailer().put(PdfName.Info, info);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DOCUMENT_SHALL_NOT_CONTAIN_INFO_UNLESS_THERE_IS_PIECE_INFO,
                e.getMessage());
    }

    @Test
    public void validCatalogCheckTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA4_catalogCheck05.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfA4CatalogCheckTest/cmp_pdfA4_catalogCheck05.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        doc.getCatalog().put(PdfName.PieceInfo, new PdfDictionary());
        doc.close();

        // This is required to check if ModDate is inside Info dictionary
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void invalidInfoTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA4_catalogCheck05.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        doc.getTrailer().put(PdfName.Info, new PdfDictionary());

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DOCUMENT_INFO_DICTIONARY_SHALL_ONLY_CONTAIN_MOD_DATE,
                e.getMessage());
    }

    @Test
    public void invalidInfoWithFullCompression() throws IOException {
        String outPdf = destinationFolder + "invalidInfoWithFillCompression.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0).setFullCompressionMode(true));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        try (PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is))) {
            doc.addNewPage();
        }

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void readValidDocumentTest() throws IOException {
        String outPdf = destinationFolder + "simplePdfA4_output01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfReader reader = new PdfReader(sourceFolder + "pdfs/simplePdfA4.pdf");
        PdfDocument document = new PdfADocument(reader, writer);
        document.close();
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void invalidVersionInCatalogTest() throws IOException {
        String outPdf = destinationFolder + "pdfA4_catalogCheck06.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        doc.getCatalog().put(PdfName.Version, new PdfString("1.7"));

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_CATALOG_VERSION_SHALL_CONTAIN_RIGHT_PDF_VERSION, 2),
                e.getMessage());
    }

    @Test
    public void corruptedVersionInCatalogTest() throws IOException {
        String outPdf = destinationFolder + "pdfA4_catalogCheck07.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        doc.getCatalog().put(PdfName.Version, new PdfString("2ae"));

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_CATALOG_VERSION_SHALL_CONTAIN_RIGHT_PDF_VERSION, 2),
                e.getMessage());
    }

    @Test
    public void readDocumentWithInvalidVersionTest() throws IOException {
        PdfWriter writer = new PdfWriter(destinationFolder + "simplePdfA4_output02.pdf");
        PdfReader reader = new PdfReader(sourceFolder + "pdfs/pdfA4WithInvalidVersion.pdf");
        PdfDocument document = new PdfADocument(reader, writer);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> document.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_FILE_HEADER_SHALL_CONTAIN_RIGHT_PDF_VERSION, 2),
                e.getMessage());
    }

    @Test
    public void checkReferenceXObject() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(100, 100));
        xObject.put(PdfName.Ref, new PdfString("test.pdf"));
        PdfCanvas xObjCanvas = new PdfCanvas(xObject, doc);
        xObjCanvas.rectangle(30, 30, 10, 10).fill();
        canvas.addXObjectFittedIntoRectangle(xObject, new Rectangle(300, 300));

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_REF_KEY,
                e.getMessage());
    }

    @Test
    public void checkOpiInXObject() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(100, 100));
        xObject.put(PdfName.OPI, new PdfString("test.pdf"));
        PdfCanvas xObjCanvas = new PdfCanvas(xObject, doc);
        xObjCanvas.rectangle(30, 30, 10, 10).fill();
        canvas.addXObjectFittedIntoRectangle(xObject, new Rectangle(300, 300));

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_OPI_KEY,
                e.getMessage());
    }

    @Test
    public void validFormXObjectTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA4_catalogCheck08.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfA4CatalogCheckTest/cmp_pdfA4_catalogCheck08.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(100, 100));
        xObject.getPdfObject().put(PdfName.Subtype2, PdfName.PS);
        PdfCanvas xObjCanvas = new PdfCanvas(xObject, doc);
        xObjCanvas.rectangle(30, 30, 10, 10).fill();
        canvas.addXObjectFittedIntoRectangle(xObject, new Rectangle(300, 300));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void createInvalidPdfAVersionNumberWithPDFA4() throws IOException {
        PdfWriter writer = new PdfWriter(new PdfWriter(new ByteArrayOutputStream()),
                new WriterProperties());
        PdfDocumentCustomVersion doc = new PdfDocumentCustomVersion(writer, PdfAConformance.PDF_A_4,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        doc.setPdfVersion(PdfVersion.PDF_1_7);
        doc.addNewPage();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(
                MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_FILE_HEADER_SHALL_CONTAIN_RIGHT_PDF_VERSION,
                        "2"), e.getMessage());
    }

    @Test
    public void createInvalidPdfAVersionNumberWithPDFA4F() throws IOException {
        PdfWriter writer = new PdfWriter(new PdfWriter(new ByteArrayOutputStream()),
                new WriterProperties());
        PdfDocumentCustomVersion doc = new PdfDocumentCustomVersion(writer, PdfAConformance.PDF_A_4F,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        doc.setPdfVersion(PdfVersion.PDF_1_7);
        doc.addNewPage();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(
                MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_FILE_HEADER_SHALL_CONTAIN_RIGHT_PDF_VERSION,
                        "2"), e.getMessage());
    }

    @Test
    public void createInvalidPdfAVersionNumberWithPDFA4E() throws IOException {
        PdfWriter writer = new PdfWriter(new PdfWriter(new ByteArrayOutputStream()),
                new WriterProperties());
        PdfDocumentCustomVersion doc = new PdfDocumentCustomVersion(writer, PdfAConformance.PDF_A_4E,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        doc.setPdfVersion(PdfVersion.PDF_1_7);
        doc.addNewPage();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(
                MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_FILE_HEADER_SHALL_CONTAIN_RIGHT_PDF_VERSION,
                        "2"), e.getMessage());
    }

    @Test
    public void createInvalidPdfAVersion16NumberWithPDFA4() throws IOException {
        PdfWriter writer = new PdfWriter(new PdfWriter(new ByteArrayOutputStream()),
                new WriterProperties());
        PdfDocumentCustomVersion doc = new PdfDocumentCustomVersion(writer, PdfAConformance.PDF_A_4,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        doc.setPdfVersion(PdfVersion.PDF_1_6);
        doc.addNewPage();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(
                MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_FILE_HEADER_SHALL_CONTAIN_RIGHT_PDF_VERSION,
                        "2"), e.getMessage());
    }

    @Test
    public void createInvalidPdfAVersion13NumberWithPDFA4F() throws IOException {
        PdfWriter writer = new PdfWriter(new PdfWriter(new ByteArrayOutputStream()),
                new WriterProperties());
        PdfDocumentCustomVersion doc = new PdfDocumentCustomVersion(writer, PdfAConformance.PDF_A_4F,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        doc.setPdfVersion(PdfVersion.PDF_1_3);
        doc.addNewPage();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(
                MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_FILE_HEADER_SHALL_CONTAIN_RIGHT_PDF_VERSION,
                        "2"), e.getMessage());
    }

    @Test
    public void createInvalidPdfAVersionNumber17WithPDFA4E() throws IOException {
        PdfWriter writer = new PdfWriter(new PdfWriter(new ByteArrayOutputStream()),
                new WriterProperties());
        PdfDocumentCustomVersion doc = new PdfDocumentCustomVersion(writer, PdfAConformance.PDF_A_4E,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        doc.setPdfVersion(PdfVersion.PDF_1_7);
        doc.addNewPage();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(
                MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_FILE_HEADER_SHALL_CONTAIN_RIGHT_PDF_VERSION,
                        "2"), e.getMessage());
    }

    private static class PdfDocumentCustomVersion extends PdfADocument {

        public PdfDocumentCustomVersion(PdfWriter writer, PdfAConformance aConformance, PdfOutputIntent outputIntent) {
            super(writer, aConformance, outputIntent);
        }

        public void setPdfVersion(PdfVersion pdfVersion) {
            this.pdfVersion = pdfVersion;
        }
    }
}
