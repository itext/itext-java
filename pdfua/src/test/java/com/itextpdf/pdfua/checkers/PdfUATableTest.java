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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfArray;
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
import com.itextpdf.pdfua.UaValidationTestFramework.Generator;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUATableTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUATableTest/";

    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    private UaValidationTestFramework framework;
    @BeforeEach
    public void initializeFramework(){
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    public static List<PdfUAConformance> data() {
        return Arrays.asList(PdfUAConformance.PDF_UA_1, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithoutHeaders01(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        for (int i = 0; i < 16; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithoutHeaders01", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithoutHeaders02(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        for (int i = 0; i < 4; i++) {
            tableBuilder.addHeaderCell(new DataCellSupplier("Data 1", 1, 1, null));
        }
        for (int i = 0; i < 8; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }
        for (int i = 0; i < 4; i++) {
            tableBuilder.addFooterCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithoutHeaders02", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn01(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn01", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn02(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn02", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn03(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn03", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 2)
    })
    public void tableWithHeaderScopeColumn04(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        // Notice, that body table is not completely filled up
        for (int i = 0; i < 10; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("tableWithHeaderScopeColumn04", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 4)
    })
    public void notRegularRowGroupingsInTableTest(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 1", 2, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 2", 1, 2, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 3", 2, 1, "Column"));
        // Table is not completely filled up
        for (int i = 0; i < 11; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }
        tableBuilder.addFooterCell(new DataCellSupplier("Footer 1", 3, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertVeraPdfFail("tableWithHeaderScopeColumn04", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn05(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        //Colspan
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 2, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn05", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn06(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 2, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        //Colspan
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn06", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn07(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 4, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn07", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn08(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 2, 1, null));
        }
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn08", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn09(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 2, 2, null));
        for (int i = 0; i < 6; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn09", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn10(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 2, 1, "Column"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 2, 2, null));
        for (int i = 0; i < 6; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn10", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn11(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 3", 2, 1, "Column"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 2, 2, null));
        for (int i = 0; i < 6; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn11", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn12(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(5);
        for (int i = 0; i < 10; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 2, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 2, 1, "Column"));
        for (int i = 0; i < 10; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn12", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn13(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(
                new Generator<Cell>() {
                    @Override
                    public Cell generate() {
                        Cell cell = new Cell();
                        cell.setNeutralRole();
                        return cell;
                    }
                });
        for (int i = 0; i < 9; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("tableWithHeaderScopeColumn13", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn14(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        if (PdfUAConformance.PDF_UA_1 == pdfUAConformance) {
            framework.assertBothFail("tableWithHeaderScopeColumn14", pdfUAConformance);
        }
        if (PdfUAConformance.PDF_UA_2 == pdfUAConformance) {
            // Rule 8.2.5.26-5 in VeraPDF passes since scope is resolved to default (see Table 384 in ISO 32000-2:2020)
            framework.assertBothValid("tableWithHeaderScopeColumn14", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn15(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn15", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderScopeColumn16(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new DataCellSupplier("Header 2", 1, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn16", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope01(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addHeaderCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addHeaderCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addHeaderCell(new DataCellSupplier("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addFooterCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addFooterCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addFooterCell(new DataCellSupplier("Data 1", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderRowScope01", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope02(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 3, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderRowScope02", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope03(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 3, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderRowScope03", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope04(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 2, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderRowScope04", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope05(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 4, "Row"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderRowScope05", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope06(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 4, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        TableBuilder tableBuilder1 = new TableBuilder(3);
        tableBuilder1.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 4, "Row"));
        tableBuilder1.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder1.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        TableBuilder tableBuilder2 = new TableBuilder(3);
        tableBuilder2.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 3, "Row"));
        tableBuilder2.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        tableBuilder2.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        tableBuilder2.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 3, "Row"));
        tableBuilder2.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder2.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        framework.addSuppliers(tableBuilder, tableBuilder1, tableBuilder2);
        framework.assertBothValid("tableWithHeaderRowScope06", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope07(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new Generator<Cell>() {
            @Override
            public Cell generate() {
                Cell cell = new Cell();
                return cell.setNeutralRole();
            }
        });

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("tableWithHeaderRowScope07", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope08(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderRowScope08", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderRowScope09(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "None"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        if (PdfUAConformance.PDF_UA_1 == pdfUAConformance) {
            framework.assertBothFail("tableWithHeaderRowScope09", pdfUAConformance);
        }
        if (PdfUAConformance.PDF_UA_2 == pdfUAConformance) {
            // Rule 8.2.5.26-5 in VeraPDF passes since scope is resolved to default (see Table 384 in ISO 32000-2:2020)
            framework.assertBothValid("tableWithHeaderRowScope09", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderBothScope01(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header", 3, 1, "Both"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderBothScope01", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderBothScope02(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header", 1, 1, "Both"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderBothScope02", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithHeaderBothScope03(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header", 3, 1, "Both"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderBothScope03", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId01(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        if (PdfUAConformance.PDF_UA_1 == pdfUAConformance) {
            framework.assertBothFail("tableWithId01", pdfUAConformance);
        }
        if (PdfUAConformance.PDF_UA_2 == pdfUAConformance) {
            // Rule 8.2.5.26-5 in VeraPDF passes since scope is resolved to default (see Table 384 in ISO 32000-2:2020)
            framework.assertBothValid("tableWithId01", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId02(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        if (PdfUAConformance.PDF_UA_1 == pdfUAConformance) {
            framework.assertBothFail("tableWithId02", pdfUAConformance);
        }
        if (PdfUAConformance.PDF_UA_2 == pdfUAConformance) {
            // Rule 8.2.5.26-5 in VeraPDF passes since scope is resolved to default (see Table 384 in ISO 32000-2:2020)
            framework.assertBothValid("tableWithId02", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId03(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId03", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId04(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId04", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId05(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addHeaderCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addHeaderCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addHeaderCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId05", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId06(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addFooterCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addFooterCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addFooterCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId06", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId07(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addFooterCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addFooterCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addFooterCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId07", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId08(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId08", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId09(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 3, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId09", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId10(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));

        tableBuilder.addFooterCell(new HeaderCellSupplier("id1", "Header", 3, 1, "None"));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId10", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId11(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 3, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId11", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId12(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("notexisting", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("tableWithId12", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId13(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId13", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId14(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId14", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tableWithId15(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("notexisting", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        framework.addSuppliers(tableBuilder);
        if (PdfUAConformance.PDF_UA_1 == pdfUAConformance) {
            framework.assertBothFail("tableWithId15", pdfUAConformance);
        }
        if (PdfUAConformance.PDF_UA_2 == pdfUAConformance) {
            framework.assertBothValid("tableWithId15", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination01(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header1", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data3", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("combination01", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED, count = 2)})
    public void combination02(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        for (int i = 0; i < 201; i++) {
            tableBuilder.addBodyCell(new HeaderCellSupplier("id" + i, "Header1", 1, 1, "None"));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("combination02", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED, count = 2)})
    public void combination04(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        for (int i = 0; i < 12; i++) {
            tableBuilder.addHeaderCell(new DataCellSupplier("Data1H", 1, 1, Collections.singletonList("id" + i)));
        }
        for (int i = 0; i < 201; i++) {
            tableBuilder.addBodyCell(new HeaderCellSupplier("id" + i, "Header1", 1, 1, "None"));
        }
        for (int i = 0; i < 201; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id" + i)));
        }

        for (int i = 0; i < 12; i++) {
            tableBuilder.addFooterCell(new DataCellSupplier("Data1F", 1, 1, Collections.singletonList("id" + i)));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("combination04", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination05(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header1", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data3", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("combination05", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination06(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addHeaderCell(new HeaderCellSupplier("id1", "Header1", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data3", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("combination06", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination07(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addFooterCell(new HeaderCellSupplier("id1", "Header1", 1, 1, "None"));
        tableBuilder.addFooterCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data3", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("combination07", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination08(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addHeaderCell(new HeaderCellSupplier("id1", "Header1", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header3", 1, 1, "Row"));

        tableBuilder.addFooterCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addFooterCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addFooterCell(new DataCellSupplier("Data3", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("combination08", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void combination09(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header1", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data3", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("combination09", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void roleMapping01(PdfUAConformance pdfUAConformance) throws IOException {
        TableBuilder tableBuilder = new TableBuilder(2);
        framework.addBeforeGenerationHook((pdfDocument -> {
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("FancyHeading", StandardRoles.TH);
            root.addRoleMapping("FancyTD", StandardRoles.TD);
            if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
                PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0)
                        .addNamespaceRoleMapping("FancyHeading", StandardRoles.TH)
                        .addNamespaceRoleMapping("FancyTD", StandardRoles.TD);
                pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(namespace);
                pdfDocument.getStructTreeRoot().addNamespace(namespace);
            }
        }));
        tableBuilder.addBodyCell(new Generator<Cell>() {
            @Override
            public Cell generate() {
                Cell c = new Cell();
                c.add(new Paragraph("Heading 1").setFont(getFont()));
                c.getAccessibilityProperties().setRole("FancyHeading");
                return c;
            }
        });

        tableBuilder.addBodyCell(new Generator<Cell>() {
            @Override
            public Cell generate() {
                Cell c = new Cell();
                c.add(new Paragraph("Heading 2").setFont(getFont()));
                c.getAccessibilityProperties().setRole("FancyHeading");
                return c;
            }
        });

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableCustomRoles", pdfUAConformance);
    }

    static class TableBuilder implements Generator<IBlockElement> {
        private final int amountOfColumns;
        private final List<Generator<Cell>> headerCells = new ArrayList<>();

        private final List<Generator<Cell>> bodyCells = new ArrayList<>();

        private final List<Generator<Cell>> footerCells = new ArrayList<>();

        TableBuilder(int amountOfColumns) {
            this.amountOfColumns = amountOfColumns;
        }

        public TableBuilder addHeaderCell(Generator<Cell> sup) {
            this.headerCells.add(sup);
            return this;
        }

        public TableBuilder addBodyCell(Generator<Cell> sup) {
            this.bodyCells.add(sup);
            return this;
        }

        public TableBuilder addFooterCell(Generator<Cell> sup) {
            this.footerCells.add(sup);
            return this;
        }

        @Override
        public IBlockElement generate() {
            Table table = new Table(amountOfColumns);
            for (Generator<Cell> headerCell : headerCells) {
                table.addHeaderCell(headerCell.generate());
            }
            for (Generator<Cell> bodyCell : bodyCells) {
                table.addCell(bodyCell.generate());
            }
            for (Generator<Cell> supplier : footerCells) {
                table.addFooterCell(supplier.generate());
            }
            return table;
        }
    }

    static class DataCellSupplier implements Generator<Cell> {

        private final String content;

        private final int colspan;

        private final int rowspan;

        private final List<String> headers;

        public DataCellSupplier(String content, int colspan, int rowspan, List<String> headers) {
            this.content = content;
            this.colspan = colspan;
            this.rowspan = rowspan;
            this.headers = headers;
        }

        @Override
        public Cell generate() {
            try {
                Cell cell = new Cell(rowspan, colspan).add(
                        new Paragraph(content).setFont(PdfFontFactory.createFont(FONT)));
                if (headers != null) {
                    PdfArray headers = new PdfArray();
                    for (String header : this.headers) {
                        headers.add(new PdfString(header));
                    }
                    cell.getAccessibilityProperties()
                            .addAttributes(new InternalPdfStructureAttributes("Table")
                                    .addPdfObject(PdfName.Headers, new PdfArray(headers)));
                }
                return cell;
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
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

    static class HeaderCellSupplier implements Generator<Cell> {

        private final String id;

        private final String content;

        private final int colspan;

        private final int rowspan;

        private final String scope;

        public HeaderCellSupplier(String id, String content, int colspan, int rowspan, String scope) {
            this.id = id;
            this.content = content;
            this.colspan = colspan;
            this.rowspan = rowspan;
            this.scope = scope;
        }

        @Override
        public Cell generate() {
            try {
                Cell cell = new Cell(rowspan, colspan).add(
                        new Paragraph(content).setFont(PdfFontFactory.createFont(FONT)));
                cell.getAccessibilityProperties().setRole(StandardRoles.TH);
                if (scope != null) {
                    cell.getAccessibilityProperties().addAttributes(new PdfStructureAttributes("Table")
                            .addEnumAttribute("Scope", scope));
                }
                if (id != null) {
                    cell.getAccessibilityProperties().setStructureElementIdString(id);
                }
                return cell;
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private static PdfFont getFont() {
        try {
            return PdfFontFactory.createFont(FONT);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}

