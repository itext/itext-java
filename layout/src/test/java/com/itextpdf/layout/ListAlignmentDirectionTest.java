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
package com.itextpdf.layout;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
@Category(IntegrationTest.class)
public class ListAlignmentDirectionTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/ListAlignmentDirectionTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/ListAlignmentDirectionTest/";

    private static final String PARAMETERS_NAME_PATTERN = "item-text-align: {0}; item-direction: {1}, "
            + "list-text-align: {2}; list-direction: {3}";
    private static final String RESULTANT_FILE_NAME_PATTERN
            = "item-text-align-{0}_item-dir-{1}_list-text-align-{2}_list-dir-{3}";

    private static final String HTML_PATTERN =
            "<ul style=\"background-color: green; width: 300pt; margin-left: 150pt; text-align: {2}; direction: {3}\">"
                    + "  <li style=\"background-color: blue;\">Usual line</li>"
                    + "  <li style=\"background-color: yellow; text-align: {0}; direction: {1}\">Specific line</li>"
                    + "</ul>";

    private TextAlignment itemTextAlignment;
    private BaseDirection itemBaseDirection;
    private TextAlignment listTextAlignment;
    private BaseDirection listBaseDirection;

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public ListAlignmentDirectionTest(Object itemTextAlignment, Object itemBaseDirection,
            Object listTextAlignment, Object listBaseDirection) throws IOException {
        this.itemTextAlignment = (TextAlignment) itemTextAlignment;
        this.itemBaseDirection = (BaseDirection) itemBaseDirection;
        this.listTextAlignment = (TextAlignment) listTextAlignment;
        this.listBaseDirection = (BaseDirection) listBaseDirection;
    }

    @Parameterized.Parameters(name = PARAMETERS_NAME_PATTERN)
    public static Iterable<Object[]> alignItemsAndJustifyContentProperties() {
        TextAlignment[] alignmentTestValues = new TextAlignment[] {TextAlignment.LEFT, TextAlignment.CENTER,
                TextAlignment.RIGHT, TextAlignment.JUSTIFIED, TextAlignment.JUSTIFIED_ALL};
        BaseDirection[] directionTestValues = new BaseDirection[] {BaseDirection.LEFT_TO_RIGHT,
                BaseDirection.RIGHT_TO_LEFT};
        java.util.List<Object[]> objectList = new ArrayList<Object[]>();
        for (TextAlignment itemTA : alignmentTestValues) {
            for (BaseDirection itemBA : directionTestValues) {
                for (TextAlignment listTA : alignmentTestValues) {
                    for (BaseDirection listBA : directionTestValues) {
                        objectList.add(new Object[] {itemTA, itemBA, listTA, listBA});
                    }
                }
            }
        }
        return objectList;
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.TYPOGRAPHY_NOT_FOUND, count = 8))
    // TODO DEVSIX-5727 direction of the first list-item should define the symbol indent's side. Once the issue
    // is fixed, the corresponding cmps should be updated.
    public void alignmentDirectionTest() throws Exception {
        // Create an HTML for this test
        createHtml();
        String fileName = MessageFormatUtil.format(
                RESULTANT_FILE_NAME_PATTERN,
                formatTextAlignment(itemTextAlignment),
                formatBaseDirection(itemBaseDirection),
                formatTextAlignment(listTextAlignment),
                formatBaseDirection(listBaseDirection));
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        Style style = new Style()
                .setTextAlignment(itemTextAlignment)
                .setBaseDirection(itemBaseDirection);
        List list = createTestList(style);
        list.setTextAlignment(listTextAlignment);
        list.setBaseDirection(listBaseDirection);

        document.add(list);
        document.close();

        System.out.println("HTML: " + UrlUtil.getNormalizedFileUriString(DESTINATION_FOLDER + fileName + ".html") + "\n");
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_"));
    }

    private static List createTestList(Style secondItemStyle) {
        List list = new List();
        list.setSymbolIndent(20);
        list.setListSymbol("\u2022");
        list.setBackgroundColor(ColorConstants.GREEN);
        list.setWidth(300);
        list.setMarginLeft(150);

        ListItem listItem1 = new ListItem();
        listItem1.add(new Paragraph("Usual item"));
        listItem1.setBackgroundColor(ColorConstants.BLUE);
        list.add(listItem1);

        ListItem listItem2 = new ListItem();
        listItem2.addStyle(secondItemStyle);
        listItem2.add(new Paragraph("Specific item"));
        listItem2.setBackgroundColor(ColorConstants.YELLOW);
        list.add(listItem2);

        return list;
    }

    private void createHtml() throws IOException {
        String fileName = MessageFormatUtil.format(
                RESULTANT_FILE_NAME_PATTERN,
                formatTextAlignment(itemTextAlignment),
                formatBaseDirection(itemBaseDirection),
                formatTextAlignment(listTextAlignment),
                formatBaseDirection(listBaseDirection));

        String htmlString = MessageFormatUtil.format(
                HTML_PATTERN,
                formatTextAlignment(itemTextAlignment, true),
                formatBaseDirection(itemBaseDirection),
                formatTextAlignment(listTextAlignment, true),
                formatBaseDirection(listBaseDirection));
        try (OutputStream htmlFile =
                FileUtil.getFileOutputStream(DESTINATION_FOLDER + fileName + ".html")) {
            byte[] htmlBytes = htmlString.getBytes(StandardCharsets.UTF_8);
            htmlFile.write(htmlBytes, 0, htmlBytes.length);
        }
    }

    private static String formatTextAlignment(TextAlignment alignment) {
        return formatTextAlignment(alignment, false);
    }

    private static String formatTextAlignment(TextAlignment alignment, boolean isHtml) {
        switch (alignment) {
            case LEFT:
                return "left";
            case RIGHT:
                return "right";
            case CENTER:
                return "center";
            case JUSTIFIED:
                return "justify";
            case JUSTIFIED_ALL:
                return isHtml ? "justify" : "justify-all";
            default:
                Assert.fail("Unexpected text alignment");
                return null;
        }
    }

    private static String formatBaseDirection(BaseDirection direction) {
        switch (direction) {
            case LEFT_TO_RIGHT:
                return "ltr";
            case RIGHT_TO_LEFT:
                return "rtl";
            default:
                Assert.fail("Unexpected base direction");
                return null;
        }
    }
}
