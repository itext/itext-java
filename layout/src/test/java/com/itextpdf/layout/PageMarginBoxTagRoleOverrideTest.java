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
package com.itextpdf.layout;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.properties.margins.PageMarginContent;
import com.itextpdf.layout.tagging.IAccessibleElement;
import com.itextpdf.layout.testutil.PageMarginsTestUtil;
import com.itextpdf.layout.testutil.TestResourceUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

@Tag("IntegrationTest")
public class PageMarginBoxTagRoleOverrideTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/PageMarginBoxTagRoleOverrideTest/";
    private static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/layout/PageMarginBoxTagRoleOverrideTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void relativePositionWithPageMarginTagRoleOverrideGoldenTest()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String fileName = "relativePositionWithPageMarginTagRoleOverride";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            pdfDocument.setTagged();

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());

            SectionBreak sectionBreak = new SectionBreak(
                    new ParagraphRolePageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            div1.setRelativePosition(50, 50, 0, 0);

            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            document.add(div1)
                    .add(sectionBreak)
                    .add(div2);
        }

        CompareTool ct = new CompareTool();
        Assertions.assertNull(ct.compareByContent(outFileName, cmpFileName,
                DESTINATION_FOLDER, "diff_" + fileName));
        Assertions.assertNull(ct.compareTagStructures(outFileName, cmpFileName));
    }

    private static class ParagraphRolePageMarginBoxes extends PageMarginBoxes {

        ParagraphRolePageMarginBoxes(List<PageMarginContent> elements) {
            super(elements);
        }

        @Override
        protected void setPageMarginTagRole(IElement element) {
            if (element instanceof IAccessibleElement) {
                ((IAccessibleElement) element).getAccessibilityProperties()
                        .setRole(StandardRoles.CAPTION);
            }
        }
    }
}
