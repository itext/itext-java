package com.itextpdf.kernel.crypto;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.crypto.securityhandler.UnsupportedSecurityHandlerException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfReaderCustomFilterTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/crypto/PdfReaderCustomFilterTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void encryptedDocumentCustomFilterStandartTest() throws IOException {
        junitExpectedException.expect(UnsupportedSecurityHandlerException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.
                format(UnsupportedSecurityHandlerException.UnsupportedSecurityHandler, "/Standart"));

        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "customSecurityHandler.pdf"));
        doc.close();
    }
}
