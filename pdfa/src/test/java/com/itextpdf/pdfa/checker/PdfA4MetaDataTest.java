package com.itextpdf.pdfa.checker;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfA4MetaDataTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfA4ActionCheckTest/";

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void pdfA4DocumentShallContainMetaDataKey() {
        PdfDictionary dictionary = new PdfDictionary();

        PdfA4Checker checker = new PdfA4Checker(PdfAConformanceLevel.PDF_A_4);
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            new PdfA4Checker(PdfAConformanceLevel.PDF_A_4).checkMetaData(dictionary);
        });
        Assert.assertEquals(e.getMessage(), PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_CONTAIN_METADATA_ENTRY);
    }


    @Test
    public void pdfA4DocumentMetaDataDocumentShallNotContainBytes() {

        String startHeader = "<?xpacket begin=\"\" id=\"W5M0MpCehiHzreSzNTczkc9d\" bytes=\"1234567890\"?>\n";

        byte[] bytes = startHeader.getBytes();
        PdfA4Checker checker = new PdfA4Checker(PdfAConformanceLevel.PDF_A_4);
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Metadata, new PdfStream(bytes));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            checker.checkMetaData(catalog);
        });
        Assert.assertEquals(
                PdfaExceptionMessageConstant.XMP_METADATA_HEADER_PACKET_MAY_NOT_CONTAIN_BYTES_OR_ENCODING_ATTRIBUTE,
                e.getMessage());
    }

    @Test
    public void pdfA4DocumentMetaDataDocumentShallNotContainEncoding() {
        String startHeader = "<?xpacket begin=\"\" id=\"W5M0MpCehiHzreSzNTczkc9d\" encoding=\"UTF-8\"?>\n";

        byte[] bytes = startHeader.getBytes();

        PdfA4Checker checker = new PdfA4Checker(PdfAConformanceLevel.PDF_A_4);
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Metadata, new PdfStream(bytes));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            checker.checkMetaData(catalog);
        });
        Assert.assertEquals(
                PdfaExceptionMessageConstant.XMP_METADATA_HEADER_PACKET_MAY_NOT_CONTAIN_BYTES_OR_ENCODING_ATTRIBUTE,
                e.getMessage());

    }

    @Test
    public void pdfA4DocumentMetaDataDocumentShallNotContainEncodingInAnyPacket() {
        String startHeader = "<?xpacket begin=\"\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n";
        startHeader += "<?xpacket begin=\"\" id=\"W5M0MpCehiHzreSzNTczkc9d\" encoding=\"UTF-8\"?>\n";
        byte[] bytes = startHeader.getBytes();

        PdfA4Checker checker = new PdfA4Checker(PdfAConformanceLevel.PDF_A_4);
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Metadata, new PdfStream(bytes));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            checker.checkMetaData(catalog);
        });
        Assert.assertEquals(
                PdfaExceptionMessageConstant.XMP_METADATA_HEADER_PACKET_MAY_NOT_CONTAIN_BYTES_OR_ENCODING_ATTRIBUTE,
                e.getMessage());
    }

    @Test
    public void pdfA4DocumentMetaDataDocumentShallNotThrowInAnyPacket() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "xmp/xmpWithMultipleXmpHeaders.xmp"));
        PdfA4Checker checker = new PdfA4Checker(PdfAConformanceLevel.PDF_A_4);
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Metadata, new PdfStream(bytes));
        AssertUtil.doesNotThrow(() -> {
            checker.checkMetaData(catalog);
        });
    }

    @Test
    public void pdfA4DocumentMetaDataDocumentShallThrowInSecondPacket() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "xmp/xmpWithMultipleXmpHeadersWithEnconding.xmp"));
        PdfA4Checker checker = new PdfA4Checker(PdfAConformanceLevel.PDF_A_4);
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Metadata, new PdfStream(bytes));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            checker.checkMetaData(catalog);
        });
        Assert.assertEquals(
                PdfaExceptionMessageConstant.XMP_METADATA_HEADER_PACKET_MAY_NOT_CONTAIN_BYTES_OR_ENCODING_ATTRIBUTE,
                e.getMessage());

    }

    @Test
    public void testAbsentPartPropertyPDFA4() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testAbsentPartPropertyPDFA4.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
            xmpMeta.deleteProperty(XMPConst.NS_PDFA_ID, XMPConst.PART);
        }));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
        Assert.assertEquals(MessageFormatUtil.format(
                        PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_PART, "4"),
                e.getMessage());
    }

    @Test
    public void testInvalidPartPropertyPDFA4() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testInvalidPartPropertyPDFA4.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
            try {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "1");
            } catch (XMPException e) {
                throw new PdfException(e);
            }
        }));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
        Assert.assertEquals(MessageFormatUtil.format(
                        PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_PART, "4"),
                e.getMessage());
    }

    @Test
    public void testNullPartPropertyPDFA4() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testNullPartPropertyPDFA4.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
            try {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, null);
            } catch (XMPException e) {
                throw new PdfException(e);
            }
        }));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
        Assert.assertEquals(MessageFormatUtil.format(
                        PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_PART, "4"),
                e.getMessage());
    }

    @Test
    public void testAbsentRevisionPropertyPDFA4() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testNullRevisionPropertyPDFA4.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
            xmpMeta.deleteProperty(XMPConst.NS_PDFA_ID, XMPConst.REV);
        }));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
        Assert.assertEquals(PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV,
                e.getMessage());
    }

    @Test
    public void testInvalidValueNotNumberRevisionPropertyPDFA4() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testInvalidValueNotNumberRevisionPropertyPDFA4.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
            try {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.REV, "test");
            } catch (XMPException e) {
                throw new PdfException(e);
            }
        }));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
        Assert.assertEquals(PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV,
                e.getMessage());
    }


    @Test
    public void testInvalidValueNotLength4RevisionPropertyPDFA4() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testInvalidValueNotLength4RevisionPropertyPDFA4.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
            try {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.REV, "200");
            } catch (XMPException e) {
                throw new PdfException(e);
            }
        }));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
        Assert.assertEquals(PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV,
                e.getMessage());
    }

    @Test
    public void testInvalidValueLength4ButContainsLettersRevisionPropertyPDFA4() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testInvalidValueLength4ButContainsLettersRevisionPropertyPDFA4.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
            try {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.REV, "200A");
            } catch (XMPException e) {
                throw new PdfException(e);
            }
        }));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
        Assert.assertEquals(PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV,
                e.getMessage());
    }

    @Test
    public void testValidPropertiesPDFA4() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testValidPropertiesPDFA4.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
        }));
        AssertUtil.doesNotThrow(() -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
    }


    @Test
    public void testValidPropertiesPDFA4F() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testValidPropertiesPDFA4F.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4F;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(doc, "file".getBytes(), "description", "file.txt", null,
                    null, null);
            doc.addFileAttachment("file.txt", fs);

        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
        }));

        AssertUtil.doesNotThrow(() -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
    }


    @Test
    public void testValidPropertiesPDFA4E() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testValidPropertiesPDFA4E.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4E;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {

        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
        }));
        AssertUtil.doesNotThrow(() -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
    }

    @Test
    public void testAbsentConformancePropertyPDFA4F() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testAbsentConformancePropertyPDFA4F.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4F;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(doc, "file".getBytes(), "description", "file.txt", null,
                    null, null);
            doc.addFileAttachment("file.txt", fs);
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
            xmpMeta.deleteProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE);
        }));

        AssertUtil.doesNotThrow(() -> new PdfA4Checker(conformanceLevel).checkMetaData(catalog));
    }

    @Test
    public void testInvalidConformancePropertyPDFA4F() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testInvalidConformancePropertyPDFA4F.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4F;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(doc, "file".getBytes(), "description", "file.txt", null,
                    null, null);
            doc.addFileAttachment("file.txt", fs);
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
            try {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, "1");
            } catch (XMPException e) {
                throw new PdfException(e);
            }
        }));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
        Assert.assertEquals(
                PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_CONFORMANCE,
                e.getMessage());
    }

    @Test
    public void historyWithXmpMetaData() throws IOException, XMPException {
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "xmp/xmpWithCorrectHistory.xmp"));
        XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
        PdfDictionary catalog = new PdfDictionary();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMPMetaFactory.serialize(xmpMeta, baos);
        catalog.put(PdfName.Metadata, new PdfStream(baos.toByteArray()));
        AssertUtil.doesNotThrow(() -> new PdfA4Checker(conformanceLevel).checkMetaData(catalog));
    }

    @Test
    public void historyWithInvalidWhenXmpMetaData() throws IOException, XMPException {
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "xmp/xmpWithInvalidWhen.xmp"));
        XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
        PdfDictionary catalog = new PdfDictionary();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMPMetaFactory.serialize(xmpMeta, baos);
        catalog.put(PdfName.Metadata, new PdfStream(baos.toByteArray()));
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> new PdfA4Checker(conformanceLevel).checkMetaData(catalog));
        Assert.assertEquals(
                MessageFormatUtil.format(PdfaExceptionMessageConstant.XMP_METADATA_HISTORY_ENTRY_SHALL_CONTAIN_KEY,
                        "stEvt:when"), e.getMessage());
    }

    @Test
    public void historyWithInvalidActionXmpMetaData() throws IOException, XMPException {
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "xmp/xmpWithInvalidAction.xmp"));
        XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
        PdfDictionary catalog = new PdfDictionary();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMPMetaFactory.serialize(xmpMeta, baos);
        catalog.put(PdfName.Metadata, new PdfStream(baos.toByteArray()));
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> new PdfA4Checker(conformanceLevel).checkMetaData(catalog));
        Assert.assertEquals(
                MessageFormatUtil.format(PdfaExceptionMessageConstant.XMP_METADATA_HISTORY_ENTRY_SHALL_CONTAIN_KEY,
                        "stEvt:action"), e.getMessage());
    }

    @Test
    public void historyWithEmptyEntryXmpMetaData() throws IOException, XMPException {
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4;
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "xmp/xmpWithEmpty.xmp"));
        XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
        PdfDictionary catalog = new PdfDictionary();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMPMetaFactory.serialize(xmpMeta, baos);
        catalog.put(PdfName.Metadata, new PdfStream(baos.toByteArray()));
        new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        Assert.assertTrue(true);
    }

    @Test
    public void testNullConformancePropertyPDFA4F() throws IOException, XMPException {
        String outPdf = DESTINATION_FOLDER + "testNullConformancePropertyPDFA4F.pdf";
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_4F;
        generatePdfADocument(conformanceLevel, outPdf, (doc) -> {
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(doc, "file".getBytes(), "description", "file.txt", null,
                    null, null);
            doc.addFileAttachment("file.txt", fs);
        });
        PdfADocument pdfADocument = new PdfADocument(new PdfReader(outPdf), new PdfWriter(new ByteArrayOutputStream()));
        PdfDictionary catalog = generateCustomXmpCatalog(pdfADocument, (xmpMeta -> {
            try {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, null);
            } catch (XMPException e) {
                throw new PdfException(e);
            }
        }));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> {
            new PdfA4Checker(conformanceLevel).checkMetaData(catalog);
        });
        Assert.assertEquals(
                PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_CONFORMANCE,
                e.getMessage());
    }


    private void generatePdfADocument(PdfAConformanceLevel conformanceLevel, String outPdf,
            Consumer<PdfDocument> consumer) throws FileNotFoundException {
        if (outPdf == null) {
            Assert.fail();
        }
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, conformanceLevel,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        new FileInputStream(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));
        doc.addNewPage();
        consumer.accept(doc);
        doc.close();
    }

    private static PdfDictionary generateCustomXmpCatalog(PdfADocument pdfADocument, Consumer<XMPMeta> action)
            throws XMPException {
        XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(pdfADocument.getXmpMetadata()));
        PdfDictionary catalog = pdfADocument.getCatalog().getPdfObject();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        action.accept(xmpMeta);
        XMPMetaFactory.serialize(xmpMeta, baos);
        catalog.put(PdfName.Metadata, new PdfStream(baos.toByteArray()));
        return catalog;
    }

}
