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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
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

	@BeforeAll
	public static void beforeClass() {
		createOrClearDestinationFolder(DESTINATION_FOLDER);
	}

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

	@ParameterizedTest(name = PARAMETERS_NAME_PATTERN)
	@MethodSource("baseDirectionAndSymbolAlignmentProperties")
	@LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPOGRAPHY_NOT_FOUND, count = 8)})
	public void defaultListIemPositionAlignmentTest(BaseDirection listBaseDirection, BaseDirection listItemBaseDirection,
			ListSymbolAlignment listSymbolAlignment, ListSymbolPosition listSymbolPosition, Integer comparisonPdfId)
			throws IOException, InterruptedException {
		// Create an HTML for this test
		createHtml(listBaseDirection, listItemBaseDirection, listSymbolAlignment, listSymbolPosition);
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
 		List list = createTestList(listBaseDirection, listItemBaseDirection, listSymbolAlignment, listSymbolPosition);
		document.add(list);

		document.close();

		System.out.println("HTML: " + UrlUtil.getNormalizedFileUriString(DESTINATION_FOLDER + fileName + ".html") + "\n");
		Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
	}

	private List createTestList(BaseDirection listBaseDirection, BaseDirection listItemBaseDirection,
			ListSymbolAlignment listSymbolAlignment, ListSymbolPosition listSymbolPosition) {
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

	private void createHtml(BaseDirection listBaseDirection, BaseDirection listItemBaseDirection,
			ListSymbolAlignment listSymbolAlignment, ListSymbolPosition listSymbolPosition) throws IOException {
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
				Assertions.fail("Unexpected base direction");
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
				Assertions.fail("Unexpected symbol alignment");
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
				Assertions.fail("Unexpected symbol position");
				return null;
		}
	}
}
