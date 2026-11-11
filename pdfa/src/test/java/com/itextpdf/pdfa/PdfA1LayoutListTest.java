/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfA1LayoutListTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfA1LayoutListTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    private static final String CMP_FOLDER = SOURCE_FOLDER + "cmp/PdfA1LayoutListTest/";
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/pdfa/fonts/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void listTest01() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA1b_listTest01.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA1b_listTest01.pdf";

        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(new PdfWriter(outPdf), PdfAConformance.PDF_A_1B, outputIntent);

        Document doc = new Document(pdfDocument);
        pdfDocument.setTagged();

        PdfFont textfont = PdfFontFactory.createFont(FONTS_FOLDER + "FreeSans.ttf",
                PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        textfont.setSubset(true);


        List list = new List();

        ListItem listItem = new ListItem();
        listItem.add(new Paragraph().add("foobar"));
        list.add(listItem);
        listItem.setFont(textfont);

        doc.add(list);

        doc.close();

        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            Assertions.fail(result);
        }
    }
}
