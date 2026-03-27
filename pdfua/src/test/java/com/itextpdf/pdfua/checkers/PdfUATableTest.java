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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.pdfua.logs.PdfUALogMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUATableTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUATableTest/";

    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    public static Supplier<Cell> newHeaderCell(String id, String content, int colspan, int rowspan, String scope) {
        return () -> {
            try {
                Cell cell = new Cell(rowspan, colspan).add(
                        new Paragraph(content).setFont(PdfFontFactory.createFont(FONT)));
                cell.getAccessibilityProperties().setRole(StandardRoles.TH);
                if (scope != null) {
                    cell.getAccessibilityProperties()
                            .addAttributes(new PdfStructureAttributes("Table").addEnumAttribute("Scope", scope));
                }
                if (id != null) {
                    cell.getAccessibilityProperties().setStructureElementIdString(id);
                }
                return cell;
            } catch (IOException e) {
                throw new PdfException(e.getMessage());
            }
        };
    }

    public static Supplier<Cell> newDataCell(String content, int colspan, int rowspan, List<String> headers) {
        return () -> {
            try {
                Cell cell = new Cell(rowspan, colspan).add(
                        new Paragraph(content).setFont(PdfFontFactory.createFont(FONT)));
                if (headers != null) {
                    PdfArray list = new PdfArray();
                    for (String header : headers) {
                        list.add(new PdfString(header));
                    }
                    cell.getAccessibilityProperties().addAttributes(
                            new InternalPdfStructureAttributes("Table").addPdfObject(PdfName.Headers,
                                    new PdfArray(list)));
                }
                return cell;
            } catch (IOException e) {
                throw new PdfException(e.getMessage());
            }
        };
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithoutHeaders01(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        for (int i = 0; i < 16; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithoutHeaders01");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithoutHeaders02(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        for (int i = 0; i < 4; i++) {
            tableBuilder.addHeaderCell(newDataCell("Data 1", 1, 1, null));
        }
        for (int i = 0; i < 8; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }
        for (int i = 0; i < 4; i++) {
            tableBuilder.addFooterCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithoutHeaders02");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn01(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn01");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn02(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn02");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn03(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addFooterCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addFooterCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addFooterCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addFooterCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn03");
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 2)})
    public void tableWithHeaderScopeColumn04(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));
        // Notice, that body table is not completely filled up
        for (int i = 0; i < 10; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothFail("tableWithHeaderScopeColumn04");
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 8)})
    public void notRegularRowGroupingsInTableTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header 1", 2, 1, "Column"));
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header 2", 1, 2, "Column"));
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header 3", 2, 1, "Column"));
        // Table is not completely filled up
        for (int i = 0; i < 11; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }
        tableBuilder.addFooterCell(newDataCell("Footer 1", 3, 1, null));

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothFail("notRegularRowGroupingsInTable",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.ROWS_SPAN_DIFFERENT_NUMBER_OF_COLUMNS, 1, 2),
                false);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn05(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        //Colspan
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 2, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn05");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn06(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 2, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        //Colspan
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn06");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn07(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 4, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn07");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn08(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 2, 1, null));
        }
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn08");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn09(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 2, 2, null));
        for (int i = 0; i < 6; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn09");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn10(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 2, 1, "Column"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 2, 2, null));
        for (int i = 0; i < 6; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn10");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn11(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addFooterCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addFooterCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addFooterCell(newHeaderCell(null, "Header 3", 2, 1, "Column"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 2, 2, null));
        for (int i = 0; i < 6; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn11");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn12(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(5);
        for (int i = 0; i < 10; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 2, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 2, 1, "Column"));
        for (int i = 0; i < 10; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn12");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn13(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(() -> {
            Cell cell = new Cell();
            cell.setNeutralRole();
            return cell;
        });
        for (int i = 0; i < 9; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothFail("tableWithHeaderScopeColumn13");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn14(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1 )) {
            framework.assertBothFail("tableWithHeaderScopeColumn14");
        } else {
            // Rule 8.2.5.26-5 in VeraPDF passes since scope is resolved to default (see Table 384 in ISO 32000-2:2020)
            framework.assertBothValid("tableWithHeaderScopeColumn14");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn15(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn15");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn16(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newDataCell("Header 2", 1, 1, null));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderScopeColumn16");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope01(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addHeaderCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addHeaderCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addHeaderCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addHeaderCell(newDataCell("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));

        tableBuilder.addFooterCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addFooterCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addFooterCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addFooterCell(newDataCell("Data 1", 1, 1, null));

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderRowScope01");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope02(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 3, 1, null));

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderRowScope02");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope03(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addBodyCell(newDataCell("Data 1", 3, 1, null));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderRowScope03");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope04(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addBodyCell(newDataCell("Data 1", 2, 1, null));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderRowScope04");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope05(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 4, "Row"));

        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderRowScope05");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope06(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 4, "Row"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));

        TableBuilder tableBuilder1 = new TableBuilder(3);
        tableBuilder1.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(newHeaderCell(null, "Header 1", 1, 4, "Row"));
        tableBuilder1.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(newDataCell("Data 1", 1, 1, null));

        TableBuilder tableBuilder2 = new TableBuilder(3);
        tableBuilder2.addBodyCell(newHeaderCell(null, "Header 1", 1, 3, "Row"));
        tableBuilder2.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(newDataCell("Data 1", 1, 1, null));

        tableBuilder2.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(newDataCell("Data 1", 1, 1, null));

        tableBuilder2.addBodyCell(newHeaderCell(null, "Header 1", 1, 3, "Row"));
        tableBuilder2.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(newDataCell("Data 1", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc(), tableBuilder1.generateFunc(), tableBuilder2.generateFunc());
        framework.assertBothValid("tableWithHeaderRowScope06");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope07(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(() -> {
            Cell cell = new Cell();
            return cell.setNeutralRole();
        });

        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothFail("tableWithHeaderRowScope07");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope08(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderRowScope08");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope09(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "None"));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1 )) {
            framework.assertBothFail("tableWithHeaderRowScope09");
        } else {
            // Rule 8.2.5.26-5 in VeraPDF passes since scope is resolved to default (see Table 384 in ISO 32000-2:2020)
            framework.assertBothValid("tableWithHeaderRowScope09");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderBothScope01(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));

        tableBuilder.addBodyCell(newHeaderCell(null, "Header", 3, 1, "Both"));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderBothScope01");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderBothScope02(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header", 1, 1, "Both"));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderBothScope02");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderBothScope03(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newHeaderCell(null, "Header", 3, 1, "Both"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithHeaderBothScope03");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId01(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1 )) {
            framework.assertBothFail("tableWithId01");
        } else {
            // Rule 8.2.5.26-5 in VeraPDF passes since scope is resolved to default (see Table 384 in ISO 32000-2:2020)
            framework.assertBothValid("tableWithId01");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId02(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1 )) {
            framework.assertBothFail("tableWithId02");
        } else {
            framework.assertBothValid("tableWithId02");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId03(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId03");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId04(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addHeaderCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(newHeaderCell("id2", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId04");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId05(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addHeaderCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(newHeaderCell("id2", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        tableBuilder.addHeaderCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addHeaderCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addHeaderCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId05");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId06(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addHeaderCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(newHeaderCell("id2", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addFooterCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addFooterCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addFooterCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId06");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId07(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        tableBuilder.addFooterCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addFooterCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addFooterCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId07");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId08(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId08");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId09(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 3, 1, "None"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId09");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId10(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));

        tableBuilder.addFooterCell(newHeaderCell("id1", "Header", 3, 1, "None"));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId10");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId11(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 1, 3, "None"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId11");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId12(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("notexisting", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothFail("tableWithId12");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId13(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId13");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId14(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableWithId14");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId15(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(newHeaderCell("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("notexisting", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id3")));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        if (conformance.conformsTo(PdfUAConformance.PDF_UA_1 )) {
            framework.assertBothFail("tableWithId15");
        } else {
            framework.assertBothValid("tableWithId15");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination01(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newHeaderCell("id1", "Header1", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(newDataCell("Data2", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data3", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothFail("combination01");
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED, count = 2)})
    public void combination02(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        for (int i = 0; i < 201; i++) {
            tableBuilder.addBodyCell(newHeaderCell("id" + i, "Header1", 1, 1, "None"));
        }

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("combination02");
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED, count = 2)})
    public void combination04(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        for (int i = 0; i < 12; i++) {
            tableBuilder.addHeaderCell(newDataCell("Data1H", 1, 1, Collections.singletonList("id" + i)));
        }
        for (int i = 0; i < 201; i++) {
            tableBuilder.addBodyCell(newHeaderCell("id" + i, "Header1", 1, 1, "None"));
        }
        for (int i = 0; i < 201; i++) {
            tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id" + i)));
        }

        for (int i = 0; i < 12; i++) {
            tableBuilder.addFooterCell(newDataCell("Data1F", 1, 1, Collections.singletonList("id" + i)));
        }

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("combination04");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination05(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(newHeaderCell("id1", "Header1", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(newDataCell("Data2", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data3", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothFail("combination05");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination06(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addHeaderCell(newHeaderCell("id1", "Header1", 1, 1, "None"));
        tableBuilder.addHeaderCell(newHeaderCell("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(newDataCell("Data2", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data3", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothFail("combination06");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination07(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addFooterCell(newHeaderCell("id1", "Header1", 1, 1, "None"));
        tableBuilder.addFooterCell(newHeaderCell("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addFooterCell(newHeaderCell(null, "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(newDataCell("Data2", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data3", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothFail("combination07");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination08(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addHeaderCell(newHeaderCell("id1", "Header1", 1, 1, "None"));
        tableBuilder.addHeaderCell(newHeaderCell("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(newHeaderCell(null, "Header3", 1, 1, "Row"));

        tableBuilder.addFooterCell(newDataCell("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addFooterCell(newDataCell("Data2", 1, 1, null));
        tableBuilder.addFooterCell(newDataCell("Data3", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothFail("combination08");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination09(PdfConformance conformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header1", 1, 1, "None"));
        tableBuilder.addBodyCell(newHeaderCell("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell("id3", "Header3", 1, 1, "Column"));
        tableBuilder.addBodyCell(newDataCell("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(newDataCell("Data2", 1, 1, null));
        tableBuilder.addBodyCell(newDataCell("Data3", 1, 1, null));

        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("combination09");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void roleMapping01(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        TableBuilder tableBuilder = new TableBuilder(2);
        framework.addBeforeGenerationHook((pdfDocument -> {
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("FancyHeading", StandardRoles.TH);
            root.addRoleMapping("FancyTD", StandardRoles.TD);
            if (framework.isPdf2Based(conformance)) {
                PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0).addNamespaceRoleMapping(
                        "FancyHeading", StandardRoles.TH).addNamespaceRoleMapping("FancyTD", StandardRoles.TD);
                pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(namespace);
                pdfDocument.getStructTreeRoot().addNamespace(namespace);
            }
        }));
        tableBuilder.addBodyCell(() -> {
            Cell c = new Cell();
            c.add(new Paragraph("Heading 1").setFont(getFont()));
            c.getAccessibilityProperties().setRole("FancyHeading");
            return c;
        });

        tableBuilder.addBodyCell(() -> {
            Cell c = new Cell();
            c.add(new Paragraph("Heading 2").setFont(getFont()));
            c.getAccessibilityProperties().setRole("FancyHeading");
            return c;
        });

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("tableCustomRoles");
    }

    private static PdfFont getFont() {
        try {
            return PdfFontFactory.createFont(FONT);
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }

    public static class TableBuilder {
        private final int amountOfColumns;
        private final List<Supplier<Cell>> headerCells = new ArrayList<>();

        private final List<Supplier<Cell>> bodyCells = new ArrayList<>();

        private final List<Supplier<Cell>> footerCells = new ArrayList<>();

        public TableBuilder(int amountOfColumns) {
            this.amountOfColumns = amountOfColumns;
        }

        public TableBuilder addHeaderCell(Supplier<Cell> sup) {
            this.headerCells.add(sup);
            return this;
        }

        public TableBuilder addBodyCell(Supplier<Cell> sup) {
            this.bodyCells.add(sup);
            return this;
        }

        public TableBuilder addFooterCell(Supplier<Cell> sup) {
            this.footerCells.add(sup);
            return this;
        }

        public Function<PdfDocument, IBlockElement> generateFunc() {
            return (pdfDocument -> {
                Table table = new Table(amountOfColumns);
                for (Supplier<Cell> headerCell : headerCells) {
                    table.addHeaderCell(headerCell.get());
                }
                for (Supplier<Cell> bodyCell : bodyCells) {
                    table.addCell(bodyCell.get());
                }
                for (Supplier<Cell> supplier : footerCells) {
                    table.addFooterCell(supplier.get());
                }
                return table;
            });
        }

    }


    static class InternalPdfStructureAttributes extends PdfStructureAttributes {

        public InternalPdfStructureAttributes(String owner) {
            super(owner);
        }

        public PdfStructureAttributes addPdfObject(PdfName headers, PdfArray pdfObjects) {
            getPdfObject().put(headers, pdfObjects);
            setModified();
            return this;
        }
    }
}

