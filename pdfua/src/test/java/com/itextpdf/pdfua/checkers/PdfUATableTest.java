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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.UaValidationTestFramework.Generator;
import com.itextpdf.pdfua.exceptions.PdfUALogMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfUATableTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUATableTest/";

    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    private UaValidationTestFramework framework;
    @Before
    public void initializeFramework(){
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    @Test
    public void tableWithoutHeaders01() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        for (int i = 0; i < 16; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithoutHeaders01");
    }

    @Test
    public void tableWithoutHeaders02() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithoutHeaders02");
    }

    @Test
    public void tableWithHeaderScopeColumn01() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn01");
    }

    @Test
    public void tableWithHeaderScopeColumn02() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn02");
    }

    @Test
    public void tableWithHeaderScopeColumn03() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn03");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 2)
    })
    public void tableWithHeaderScopeColumn04() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        //notice body table is not completly filled up
        for (int i = 0; i < 10; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("tableWithHeaderScopeColumn04");
    }


    @Test
    public void tableWithHeaderScopeColumn05() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        //Colspan
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 2, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn05");
    }

    @Test
    public void tableWithHeaderScopeColumn06() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 2, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        //Colspan
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn06");
    }

    @Test
    public void tableWithHeaderScopeColumn07() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 4, 1, "Column"));
        for (int i = 0; i < 12; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn07");
    }

    @Test
    public void tableWithHeaderScopeColumn08() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithHeaderScopeColumn08");
    }

    @Test
    public void tableWithHeaderScopeColumn09() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithHeaderScopeColumn09");
    }

    @Test
    public void tableWithHeaderScopeColumn10() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithHeaderScopeColumn10");
    }

    @Test
    public void tableWithHeaderScopeColumn11() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithHeaderScopeColumn11");
    }


    @Test
    public void tableWithHeaderScopeColumn12() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithHeaderScopeColumn12");
    }


    @Test
    public void tableWithHeaderScopeColumn13() throws FileNotFoundException {
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
        framework.assertBothFail("tableWithHeaderScopeColumn13");
    }

    @Test
    public void tableWithHeaderScopeColumn14() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 2", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("tableWithHeaderScopeColumn14");
    }


    @Test
    public void tableWithHeaderScopeColumn15() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn15");
    }

    @Test
    public void tableWithHeaderScopeColumn16() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        }

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new DataCellSupplier("Header 2", 1, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderScopeColumn16");
    }

    @Test
    public void tableWithHeaderRowScope01() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithHeaderRowScope01");

    }

    @Test
    public void tableWithHeaderRowScope02() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 3, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderRowScope02");

    }

    @Test
    public void tableWithHeaderRowScope03() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 3, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderRowScope03");

    }

    @Test
    public void tableWithHeaderRowScope04() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(4);

        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 2, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderRowScope04");

    }

    @Test
    public void tableWithHeaderRowScope05() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithHeaderRowScope05");

    }

    @Test
    public void tableWithHeaderRowScope06() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithHeaderRowScope06");

    }

    @Test
    public void tableWithHeaderRowScope07() throws FileNotFoundException {
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
        framework.assertBothFail("tableWithHeaderRowScope07");

    }

    @Test
    public void tableWithHeaderRowScope08() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderRowScope08");

    }

    @Test
    public void tableWithHeaderRowScope09() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "Row"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header 1", 1, 1, "None"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data 1", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("tableWithHeaderRowScope09");

    }

    @Test
    public void tableWithHeaderBothScope01() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header", 3, 1, "Both"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderBothScope01");

    }


    @Test
    public void tableWithHeaderBothScope02() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithHeaderBothScope02");

    }

    @Test
    public void tableWithHeaderBothScope03() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header", 3, 1, "Both"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithHeaderBothScope03");
    }


    @Test
    public void tableWithId01() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("tableWithId01");
    }


    @Test
    public void tableWithId02() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("tableWithId02");
    }


    @Test
    public void tableWithId03() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId03");
    }

    @Test
    public void tableWithId04() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId04");
    }

    @Test
    public void tableWithId05() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));

        tableBuilder.addHeaderCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addHeaderCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addHeaderCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId05");
    }


    @Test
    public void tableWithId06() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithId06");
    }


    @Test
    public void tableWithId07() throws FileNotFoundException {
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
        framework.assertBothValid("tableWithId07");
    }

    @Test
    public void tableWithId08() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId08");
    }


    @Test
    public void tableWithId09() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 3, 1, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId09");
    }

    @Test
    public void tableWithId10() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));

        tableBuilder.addFooterCell(new HeaderCellSupplier("id1", "Header", 3, 1, "None"));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId10");
    }


    @Test
    public void tableWithId11() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 3, "None"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId11");
    }

    @Test
    public void tableWithId12() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("notexisting", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));


        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("tableWithId12");
    }

    @Test
    public void tableWithId13() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id1")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId13");
    }

    @Test
    public void tableWithId14() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id3")));

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header", 1, 1, "None"));


        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("tableWithId14");
    }

    @Test
    public void combination01() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header1", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data3", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("combination01");

    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED, count = 2)})
    public void combination02() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);
        for (int i = 0; i < 201; i++) {
            tableBuilder.addBodyCell(new HeaderCellSupplier("id" + i, "Header1", 1, 1, "None"));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("combination02");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED, count = 2)})
    public void combination04() throws FileNotFoundException {
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
        framework.assertBothValid("combination04");
    }


    @Test
    public void combination05() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addBodyCell(new HeaderCellSupplier("id1", "Header1", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data3", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("combination05");

    }

    @Test
    public void combination06() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addHeaderCell(new HeaderCellSupplier("id1", "Header1", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data3", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("combination06");

    }

    @Test
    public void combination07() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addFooterCell(new HeaderCellSupplier("id1", "Header1", 1, 1, "None"));
        tableBuilder.addFooterCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addFooterCell(new HeaderCellSupplier(null, "Header3", 1, 1, "Row"));

        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data3", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("combination07");

    }

    @Test
    public void combination08() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);

        tableBuilder.addHeaderCell(new HeaderCellSupplier("id1", "Header1", 1, 1, "None"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addHeaderCell(new HeaderCellSupplier(null, "Header3", 1, 1, "Row"));

        tableBuilder.addFooterCell(new DataCellSupplier("Data1", 1, 1, Arrays.asList("id1", "id2")));
        tableBuilder.addFooterCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addFooterCell(new DataCellSupplier("Data3", 1, 1, null));


        framework.addSuppliers(tableBuilder);
        framework.assertBothFail("combination08");

    }

    @Test
    public void combination09() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(3);
        tableBuilder.addBodyCell(new HeaderCellSupplier(null, "Header1", 1, 1, "None"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id2", "Header2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new HeaderCellSupplier("id3", "Header3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new DataCellSupplier("Data1", 1, 1, Collections.singletonList("id2")));
        tableBuilder.addBodyCell(new DataCellSupplier("Data2", 1, 1, null));
        tableBuilder.addBodyCell(new DataCellSupplier("Data3", 1, 1, null));

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("combination09");
    }

    @Test
    public void roleMapping01() throws FileNotFoundException {
        TableBuilder tableBuilder = new TableBuilder(2);
        framework.addBeforeGenerationHook((pdfDocument -> {
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("FancyHeading", StandardRoles.TH);
            root.addRoleMapping("FancyTD", StandardRoles.TD);
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
        framework.assertBothValid("tableCustomRoles");
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

    private static PdfFont getFont(){
        try {
            return PdfFontFactory.createFont(FONT);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}

