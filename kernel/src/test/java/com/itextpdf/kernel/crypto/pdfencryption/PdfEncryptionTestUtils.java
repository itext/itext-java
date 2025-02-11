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
package com.itextpdf.kernel.crypto.pdfencryption;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.utils.CompareTool;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PdfEncryptionTestUtils {

    private final String destinationFolder;

    private final String sourceFolder;

    public static final String PAGE_TEXT_CONTENT = "Hello world!";

    public static final String CUSTOM_INFO_ENTRY_KEY = "Custom";

    public static final String CUSTOM_INFO_ENTRY_VALUE = "String";

    /**
     * User password.
     */
    public static byte[] USER = "Hello".getBytes(StandardCharsets.ISO_8859_1);

    /**
     * Owner password.
     */
    public static byte[] OWNER = "World".getBytes(StandardCharsets.ISO_8859_1);


    public PdfEncryptionTestUtils(String destinationFolder, String sourceFolder) {
        this.destinationFolder = destinationFolder;
        this.sourceFolder = sourceFolder;
    }

    public void compareEncryptedPdf(String filename) throws IOException, InterruptedException {
        checkDecryptedWithPasswordContent(destinationFolder + filename, OWNER, PAGE_TEXT_CONTENT);
        checkDecryptedWithPasswordContent(destinationFolder + filename, USER, PAGE_TEXT_CONTENT);

        CompareTool compareTool = new CompareTool().enableEncryptionCompare(false);
        String compareResult = compareTool.compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder, "diff_", USER, USER);
        if (compareResult != null) {
            Assertions.fail(compareResult);
        }
    }

    public void checkDecryptedWithPasswordContent(String src, byte[] password, String pageContent)
            throws IOException {
        checkDecryptedWithPasswordContent(src, password, pageContent, false);
    }

    public void checkDecryptedWithPasswordContent(String src, byte[] password, String pageContent,
                                                     boolean expectError) throws IOException {
        PdfReader reader = CompareTool.createOutputReader(src, new ReaderProperties().setPassword(password));
        PdfDocument document = new PdfDocument(reader);
        PdfPage page = document.getPage(1);

        boolean expectedContentFound = new String(page.getStreamBytes(0)).contains(pageContent);
        String actualCustomInfoEntry = document.getTrailer().getAsDictionary(PdfName.Info)
                .getAsString(new PdfName(CUSTOM_INFO_ENTRY_KEY)).toUnicodeString();

        if (!expectError) {
            Assertions.assertTrue(expectedContentFound, "Expected content: \n" + pageContent);
            Assertions.assertEquals( CUSTOM_INFO_ENTRY_VALUE, actualCustomInfoEntry, "Encrypted custom");
        } else {
            Assertions.assertFalse(expectedContentFound, "Expected content: \n" + pageContent);
            Assertions.assertNotEquals(CUSTOM_INFO_ENTRY_VALUE, actualCustomInfoEntry, "Encrypted custom");
        }

        document.close();
    }

    public static void writeTextBytesOnPageContent(PdfPage page, String text) throws IOException {
        page.getFirstContentStream().getOutputStream().writeBytes(("q\n" +
                "BT\n" +
                "36 706 Td\n" +
                "0 0 Td\n" +
                "/F1 24 Tf\n" +
                "(" + text + ")Tj\n" +
                "0 0 Td\n" +
                "ET\n" +
                "Q ").getBytes(StandardCharsets.ISO_8859_1));
        page.getResources().addFont(page.getDocument(), PdfFontFactory.createFont(StandardFonts.HELVETICA));
    }

}
