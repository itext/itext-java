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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfA4MiscCheckTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String CMP_FOLDER = SOURCE_FOLDER + "cmp/PdfA4MiscCheckTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfA4MiscCheckTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void pdfA4CheckThatAsKeyIsAllowedTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4CheckThatAsKeyIsAllowedTest.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4CheckThatAsKeyIsAllowedTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        try (PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_4, createOutputIntent())) {
            doc.addNewPage();

            PdfArray configs = new PdfArray();
            PdfDictionary config = new PdfDictionary();
            config.put(PdfName.Name, new PdfString("CustomName"));

            PdfDictionary usageAppDict = new PdfDictionary();
            usageAppDict.put(PdfName.Event, PdfName.View);
            PdfArray categoryArray = new PdfArray();
            categoryArray.add(PdfName.Zoom);
            usageAppDict.put(PdfName.Category, categoryArray);
            config.put(PdfName.AS, usageAppDict);

            configs.add(config);

            PdfDictionary ocProperties = new PdfDictionary();
            ocProperties.put(PdfName.Configs, configs);
            doc.getCatalog().put(PdfName.OCProperties, ocProperties);
        }

        compareResult(outPdf, cmpPdf);
    }

    private PdfOutputIntent createOutputIntent() throws IOException {
        return new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm"));
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            Assertions.fail(result);
        }
    }
}
