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
import com.itextpdf.layout.properties.ListSymbolAlignment;
import com.itextpdf.layout.properties.ListSymbolPosition;
import com.itextpdf.layout.properties.Property;
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
public class ListItemPositionAlignmentTest extends ExtendedITextTest {

	public static final String SOURCE_FOLDER  = "./src/test/resources/com/itextpdf/layout/ListItemPositionAlignmentTest/";
	public static final String DESTINATION_FOLDER  = "./target/test/com/itextpdf/layout/ListItemPositionAlignmentTest/";

	private static final String PARAMETERS_NAME_PATTERN = "{index}: list-base-direction: {0}; list-item-base-direction: {1};" +
	                                                      " list-symbol-alignment: {2}; list-symbol-position: {3};";
	private static final String RESULTANT_FILE_NAME_PATTERN
			= "list-dir-{0}_item-dir-{1}_symbol-align-{2}_symbol-position-{3}";

	private static final String HTML_PATTERN =
			"<ul style=\"background-color: green; direction: {3}\">"
			+ "  <li style=\"background-color: blue;\">Usual item</li>"
			+ "  <li style=\"background-color: yellow; direction: {2}; symbol-alignment:{1}; symbol-position: {0}\">Specific item</li>"
			+ "</ul>";

	private BaseDirection listBaseDirection;
	private BaseDirection listItemBaseDirection;
	private ListSymbolAlignment listSymbolAlignment;
	private ListSymbolPosition listSymbolPosition;
	private Integer comparisonPdfId;

	@BeforeClass
	public static void beforeClass() {
		createOrClearDestinationFolder(DESTINATION_FOLDER);
	}

	public ListItemPositionAlignmentTest(Object listBaseDirection, Object listItemBaseDirection,
	                                     Object listSymbolAlignment, Object listSymbolPosition, Object comparisonPdfId) throws IOException {
		this.listBaseDirection = (BaseDirection) listBaseDirection;
		this.listItemBaseDirection = (BaseDirection) listItemBaseDirection;
		this.listSymbolAlignment = (ListSymbolAlignment) listSymbolAlignment;
		this.listSymbolPosition = (ListSymbolPosition) listSymbolPosition;
		this.comparisonPdfId = (Integer) comparisonPdfId;
	}

	@Parameterized.Parameters(name = PARAMETERS_NAME_PATTERN)
	public static Iterable<Object[]> baseDirectionAndSymbolAlignmentProperties() {
		BaseDirection[] directionTestValues = new BaseDirection[]{BaseDirection.LEFT_TO_RIGHT,
		                                                          BaseDirection.RIGHT_TO_LEFT};
		ListSymbolAlignment[] listSymbolAlignmentTestValues = new ListSymbolAlignment[]{ListSymbolAlignment.LEFT,
		                                                                                ListSymbolAlignment.RIGHT};
		ListSymbolPosition[] listSymbolPositionTestValues = new ListSymbolPosition[]{ListSymbolPosition.OUTSIDE,
		                                                                             ListSymbolPosition.INSIDE};
		java.util.List<Object[]> objectList = new ArrayList<>();
		int count = 0;
		for (BaseDirection listBA : directionTestValues) {
			for (BaseDirection itemBA : directionTestValues) {
				for (ListSymbolAlignment listSA : listSymbolAlignmentTestValues) {
					for (ListSymbolPosition listSP : listSymbolPositionTestValues) {
						objectList.add(new Object[]{listBA, itemBA, listSA, listSP, count});
						count++;
					}
				}
			}
		}
		return objectList;
	}

	@Test
	@LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPOGRAPHY_NOT_FOUND, count = 8)})
	public void defaultListIemPositionAlignmentTest() throws IOException, InterruptedException {
		// Create an HTML for this test
		createHtml();
		String fileName = MessageFormatUtil.format(
				RESULTANT_FILE_NAME_PATTERN,
				formatSymbolPosition(listSymbolPosition),
				formatSymbolAlignment(listSymbolAlignment),
				formatBaseDirection(listItemBaseDirection),
				formatBaseDirection(listBaseDirection));

		String outFileName = DESTINATION_FOLDER + "defaultListItemTest" + comparisonPdfId + ".pdf";
		String cmpFileName = SOURCE_FOLDER + "cmp_defaultListItemTest" + comparisonPdfId + ".pdf";
		PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

		Document document = new Document(pdfDocument);
 		List list = createTestList();
		document.add(list);

		document.close();

		System.out.println("HTML: " + UrlUtil.getNormalizedFileUriString(DESTINATION_FOLDER + fileName + ".html") + "\n");
		Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
	}

	private List createTestList() {
		List list = new List();
		list.setSymbolIndent(20);
		list.setListSymbol("\u2022");
		list.setBackgroundColor(ColorConstants.GREEN);

		ListItem listItem1 = new ListItem();
		listItem1.add(new Paragraph("Usual item"));
		listItem1.setBackgroundColor(ColorConstants.BLUE);

		list.add(listItem1);

		ListItem listItem2 = new ListItem();
		listItem2.add(new Paragraph("Specific item"));
		listItem2.setBackgroundColor(ColorConstants.YELLOW);
		listItem2.setProperty(Property.BASE_DIRECTION, listItemBaseDirection);
		listItem2.setProperty(Property.LIST_SYMBOL_ALIGNMENT, listSymbolAlignment);
		listItem2.setProperty(Property.LIST_SYMBOL_POSITION, listSymbolPosition);
		list.add(listItem2);
		list.setProperty(Property.BASE_DIRECTION, listBaseDirection);
		return list;
	}

	private void createHtml() throws IOException {
		String fileName = MessageFormatUtil.format(
				RESULTANT_FILE_NAME_PATTERN,
				formatSymbolPosition(listSymbolPosition),
				formatSymbolAlignment(listSymbolAlignment),
				formatBaseDirection(listItemBaseDirection),
				formatBaseDirection(listBaseDirection));

		String htmlString = MessageFormatUtil.format(
				HTML_PATTERN,
				formatSymbolPosition(listSymbolPosition),
				formatSymbolAlignment(listSymbolAlignment),
				formatBaseDirection(listItemBaseDirection),
				formatBaseDirection(listBaseDirection));
		try (OutputStream htmlFile =
				     FileUtil.getFileOutputStream(DESTINATION_FOLDER + fileName + ".html")) {
			byte[] htmlBytes = htmlString.getBytes(StandardCharsets.UTF_8);
			htmlFile.write(htmlBytes, 0, htmlBytes.length);
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

	private static String formatSymbolAlignment(ListSymbolAlignment alignment) {
		switch (alignment) {
			case LEFT:
				return "left";
			case RIGHT:
				return "right";
			default:
				Assert.fail("Unexpected symbol alignment");
				return null;
		}
	}

	private static String formatSymbolPosition(ListSymbolPosition position) {
		switch (position) {
			case OUTSIDE:
				return "outside";
			case INSIDE:
				return "inside";
			default:
				Assert.fail("Unexpected symbol position");
				return null;
		}
	}
}
