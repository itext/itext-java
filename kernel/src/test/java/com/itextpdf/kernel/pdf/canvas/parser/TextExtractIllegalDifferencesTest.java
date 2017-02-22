/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 * @author benoit
 */
@Category(IntegrationTest.class)
public class TextExtractIllegalDifferencesTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/TextExtractIllegalDifferencesTest/";
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.DOCFONT_HAS_ILLEGAL_DIFFERENCES, count = 1))
    public void illegalDifference() throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfReader(sourceFolder + "illegalDifference.pdf"));
        PdfTextExtractor.getTextFromPage(pdf.getFirstPage());
    }
}
