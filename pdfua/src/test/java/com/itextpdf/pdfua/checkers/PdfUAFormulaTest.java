package com.itextpdf.pdfua.checkers;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.TestFramework;
import com.itextpdf.pdfua.TestFramework.Generator;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfUAFormulaTest extends ExtendedITextTest {


    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAFormulaTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private TestFramework framework;

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void initializeFramework() {
        framework = new TestFramework(DESTINATION_FOLDER);
    }

    @Test
    public void layoutTest01() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                return p;
            }
        });
        framework.assertBothFail("layout01");
    }

    @Test
    public void layoutTest02() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setActualText("Einstein smart boy formula");
                return p;
            }
        });
        framework.assertBothValid("layout02");
    }


    @Test
    public void layoutTest03() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setAlternateDescription("Einstein smart boy " + "formula");
                return p;
            }
        });
        framework.assertBothValid("layout03");
    }


    @Test
    public void layoutTest04() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setAlternateDescription("");
                return p;
            }
        });
        framework.assertBothFail("layout04");
    }

    @Test
    public void layoutTest05() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("E=mc²").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setActualText("");
                return p;
            }
        });
        framework.assertBothValid("layout05");
    }

    @Test
    public void layoutTest06() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("⫊").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setActualText("Some character that is not embeded in the font");
                return p;
            }
        });
        framework.assertBothFail("layout06");
    }

    @Test
    public void layoutTest07() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph p = new Paragraph("⫊").setFont(loadFont(FONT));
                p.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
                p.getAccessibilityProperties().setAlternateDescription("Alternate " + "description");
                return p;
            }
        });
        framework.assertBothFail("layout07");
    }

    @Test
    public void canvasTest01() throws IOException {
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        PdfFont font = PdfFontFactory.createFont(FONT);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(document.getFirstPage());
        tagPointer.addTag(StandardRoles.FORMULA);
        canvas.openTag(tagPointer.getTagReference()).saveState().beginText().setFontAndSize(font, 12).showText("E=mc²")
                .endText().closeTag();
        Assert.assertThrows(PdfUAConformanceException.class, () -> {
            document.close();
        });

    }

    @Test
    public void canvasTest02() throws IOException {
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        PdfFont font = PdfFontFactory.createFont(FONT);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(document.getFirstPage());
        tagPointer.addTag(StandardRoles.FORMULA);

        tagPointer.getProperties().setActualText("Einstein smart boy");
        canvas.openTag(tagPointer.getTagReference()).saveState().beginText().setFontAndSize(font, 12).showText("E=mc²")
                .endText().closeTag();
        AssertUtil.doesNotThrow(() -> {
            document.close();
        });
    }

    @Test
    public void canvasTest03() throws IOException {
        PdfDocument document = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        PdfFont font = PdfFontFactory.createFont(FONT);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(document.getFirstPage());
        tagPointer.addTag(StandardRoles.FORMULA);
        canvas.openTag(tagPointer.getTagReference()).saveState().beginText().setFontAndSize(font, 12);
        Assert.assertThrows(PdfUAConformanceException.class, () -> {
            canvas.showText("⫊");
        });
    }

    private static PdfFont loadFont(String fontPath) {
        try {
            return PdfFontFactory.createFont(fontPath);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
