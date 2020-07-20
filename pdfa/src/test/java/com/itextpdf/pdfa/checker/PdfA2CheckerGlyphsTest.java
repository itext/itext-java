package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfA2CheckerGlyphsTest extends ExtendedITextTest {

    private final PdfA2Checker pdfA2Checker = new PdfA2Checker(PdfAConformanceLevel.PDF_A_2B);

    @Before
    public void before() {
        pdfA2Checker.setFullCheckMode(true);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void checkValidFontGlyphsTest() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {
            document.addNewPage();

            PdfDictionary charProcs = new PdfDictionary();
            charProcs.put(PdfName.A, new PdfStream());
            charProcs.put(PdfName.B, new PdfStream());

            PdfArray differences = new PdfArray();
            differences.add(new PdfNumber(41));
            differences.add(PdfName.A);
            differences.add(new PdfNumber(82));
            differences.add(PdfName.B);

            PdfFont font = createFontWithCharProcsAndEncodingDifferences(document, charProcs, differences);

            // no assertions as we want to ensure that in this case the next method won't throw an exception
            pdfA2Checker.checkFontGlyphs(font, null);
        }
    }

    @Test
    public void checkInvalidFontGlyphsTest() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {
            document.addNewPage();

            PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0f, 0f));
            formXObject.getPdfObject().put(PdfName.Subtype2, PdfName.PS);

            PdfDictionary charProcs = new PdfDictionary();
            charProcs.put(PdfName.A, new PdfStream());
            charProcs.put(PdfName.B, formXObject.getPdfObject());

            PdfArray differences = new PdfArray();
            differences.add(new PdfNumber(41));
            differences.add(PdfName.A);
            differences.add(new PdfNumber(82));
            differences.add(PdfName.B);

            PdfFont font = createFontWithCharProcsAndEncodingDifferences(document, charProcs, differences);

            junitExpectedException.expect(PdfAConformanceException.class);
            junitExpectedException.expectMessage(
                    PdfAConformanceException.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_SUBTYPE2_KEY_WITH_A_VALUE_OF_PS);

            pdfA2Checker.checkFontGlyphs(font, null);
        }
    }

    private PdfFont createFontWithCharProcsAndEncodingDifferences(PdfDocument document,
            PdfDictionary charProcs, PdfArray differences) {
        PdfDictionary encoding = new PdfDictionary();
        encoding.put(PdfName.Type, PdfName.Encoding);
        encoding.put(PdfName.Differences, differences);

        PdfDictionary fontDictionary = new PdfDictionary();
        fontDictionary.put(PdfName.Type, PdfName.Font);
        fontDictionary.put(PdfName.Subtype, PdfName.Type3);
        fontDictionary.put(PdfName.Encoding, encoding);
        fontDictionary.put(PdfName.CharProcs, charProcs);
        fontDictionary.put(PdfName.FontMatrix, new PdfArray(new float[]{0f, 0f, 0f, 0f, 0f, 0f}));
        fontDictionary.put(PdfName.Widths, new PdfArray(new float[0]));

        fontDictionary.makeIndirect(document);

        return PdfFontFactory.createFont(fontDictionary);
    }
}
