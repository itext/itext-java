package com.itextpdf.kernel.font;

import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class FontUtilTest extends ExtendedITextTest {
    @Test
    public void parseUniversalNotExistedCMapTest() {
        Assert.assertNull(FontUtil.parseUniversalToUnicodeCMap("NotExisted"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.UNKNOWN_ERROR_WHILE_PROCESSING_CMAP, logLevel = LogLevelConstants.ERROR)
    })
    public void processInvalidToUnicodeTest() {
        PdfStream toUnicode = new PdfStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            toUnicode.makeIndirect(pdfDocument);
            toUnicode.flush();
            final CMapToUnicode cmap = FontUtil.processToUnicode(toUnicode);
            Assert.assertNotNull(cmap);
            Assert.assertFalse(cmap.hasByteMappings());
        }
    }
}
