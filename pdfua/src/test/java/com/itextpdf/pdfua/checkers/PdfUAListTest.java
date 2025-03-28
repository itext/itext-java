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

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUAListTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUAListTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    public static List<PdfUAConformance> testSources() {
        return Arrays.asList(PdfUAConformance.PDF_UA_1, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void validListTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List(ListNumberingType.DECIMAL);
                list.add(new ListItem("item1"));
                list.add(new ListItem("item2"));
                list.add(new ListItem("item3"));
                list.setFont(loadFont());
                return list;
            }
        });

        framework.assertBothValid("validListTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void lblAndLBodyInListItemTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Div list = new Div();
                list.getAccessibilityProperties().setRole(StandardRoles.L);
                Div item = new Div();
                item.getAccessibilityProperties().setRole(StandardRoles.LI);

                Paragraph lbl = new Paragraph("label");
                lbl.getAccessibilityProperties().setRole(StandardRoles.LBL);

                Paragraph lBody = new Paragraph("body");
                lBody.getAccessibilityProperties().setRole(StandardRoles.LBODY);

                item.add(lbl);
                item.add(lBody);
                list.add(item);

                list.setFont(loadFont());
                return list;
            }
        });

        framework.assertBothValid("lblAndLBodyInListItemTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void invalidListItemRoleTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List(ListNumberingType.DECIMAL);
                ListItem item1 = new ListItem("item1");
                item1.getAccessibilityProperties().setRole(StandardRoles.P);
                list.add(item1);
                list.add(new ListItem("item2"));
                list.add(new ListItem("item3"));
                list.setFont(loadFont());
                return list;
            }
        });

        framework.assertBothFail("invalidListItemRoleTest",
                PdfUAExceptionMessageConstants.LIST_ITEM_CONTENT_HAS_INVALID_TAG, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void artifactInListItemTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List(ListNumberingType.DECIMAL);
                ListItem item1 = new ListItem("item1");
                item1.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
                list.add(item1);
                list.add(new ListItem("item2"));
                list.add(new ListItem("item3"));
                list.setFont(loadFont());
                return list;
            }
        });

        framework.assertBothValid("artifactInListItemTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void noListNumberingTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            pdfDoc.getTagStructureContext().normalizeDocumentRootTag();
            PdfStructElem list = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                    pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.L)) :
                    ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0))
                            .addKid(new PdfStructElem(pdfDoc, PdfName.L));
            PdfStructElem listItem1 = list.addKid(new PdfStructElem(pdfDoc, PdfName.LI));
            listItem1.addKid(new PdfStructElem(pdfDoc, PdfName.Lbl));
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("noListNumberingTest", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("noListNumberingTest",
                    PdfUAExceptionMessageConstants.LIST_NUMBERING_IS_NOT_SPECIFIED, pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void noneListNumberingTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            pdfDoc.getTagStructureContext().normalizeDocumentRootTag();
            PdfStructElem list = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                    pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.L)) :
                    ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0))
                            .addKid(new PdfStructElem(pdfDoc, PdfName.L));

            PdfDictionary attributes = new PdfDictionary();
            attributes.put(PdfName.O, PdfName.List);
            attributes.put(PdfName.ListNumbering, PdfName.None);
            list.setAttributes(attributes);

            PdfStructElem listItem1 = list.addKid(new PdfStructElem(pdfDoc, PdfName.LI));
            listItem1.addKid(new PdfStructElem(pdfDoc, PdfName.Lbl));
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("noneListNumberingTest", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("noneListNumberingTest",
                    PdfUAExceptionMessageConstants.LIST_NUMBERING_IS_NOT_SPECIFIED, pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void noListNumberingNoLblTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            pdfDoc.getTagStructureContext().normalizeDocumentRootTag();
            PdfStructElem list = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                    pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.L)) :
                    ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0))
                            .addKid(new PdfStructElem(pdfDoc, PdfName.L));
            PdfStructElem listItem1 = list.addKid(new PdfStructElem(pdfDoc, PdfName.LI));
            listItem1.addKid(new PdfStructElem(pdfDoc, PdfName.LBody));
        });

        framework.assertBothValid("noListNumberingNoLblTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void invalidNestedListTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Div list = new Div();
                list.getAccessibilityProperties().setRole(StandardRoles.L);
                PdfDictionary attributes = new PdfDictionary();
                attributes.put(PdfName.O, PdfName.List);
                attributes.put(PdfName.ListNumbering, PdfName.Unordered);
                list.getAccessibilityProperties().addAttributes(new PdfStructureAttributes(attributes));

                com.itextpdf.layout.element.List nestedList = new com.itextpdf.layout.element.List(ListNumberingType.DECIMAL);
                ListItem nestedItem = new ListItem("item4");
                nestedItem.getAccessibilityProperties().setRole(StandardRoles.P);
                nestedList.add(nestedItem);

                list.add(new ListItem("item1"));
                list.add(new ListItem("item2"));
                list.add(new ListItem("item3"));
                list.add(nestedList);
                list.setFont(loadFont());
                return list;
            }
        });

        framework.assertBothFail("invalidNestedListTest",
                PdfUAExceptionMessageConstants.LIST_ITEM_CONTENT_HAS_INVALID_TAG, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void severalListNumberingsFirstValidTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List(ListNumberingType.DECIMAL);
                list.add(new ListItem("item1"));
                list.add(new ListItem("item2"));
                list.add(new ListItem("item3"));
                list.setFont(loadFont());

                PdfDictionary attributes = new PdfDictionary();
                attributes.put(PdfName.O, PdfName.List);
                attributes.put(PdfName.ListNumbering, PdfName.None);
                // ListNumbering Decimal will be added to the beginning when processing List layout element.
                list.getAccessibilityProperties().addAttributes(new PdfStructureAttributes(attributes));
                list.getAccessibilityProperties().addAttributes(new PdfStructureAttributes(attributes));
                list.getAccessibilityProperties().addAttributes(new PdfStructureAttributes(attributes));
                return list;
            }
        });

        framework.assertBothValid("severalListNumberingsFirstValidTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void severalListNumberingsFirstInvalidTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook((pdfDoc) -> {
            pdfDoc.getTagStructureContext().normalizeDocumentRootTag();
            PdfStructElem list = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                    pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.L)) :
                    ((PdfStructElem) pdfDoc.getStructTreeRoot().getKids().get(0))
                            .addKid(new PdfStructElem(pdfDoc, PdfName.L));

            PdfDictionary validAttributes = new PdfDictionary();
            validAttributes.put(PdfName.O, PdfName.List);
            validAttributes.put(PdfName.ListNumbering, PdfName.Ordered);

            PdfDictionary invalidAttributes = new PdfDictionary();
            invalidAttributes.put(PdfName.O, PdfName.List);
            invalidAttributes.put(PdfName.ListNumbering, PdfName.None);

            PdfArray attributes = new PdfArray();
            attributes.add(invalidAttributes);
            attributes.add(validAttributes);
            attributes.add(invalidAttributes);
            list.setAttributes(attributes);

            PdfStructElem listItem1 = list.addKid(new PdfStructElem(pdfDoc, PdfName.LI));
            listItem1.addKid(new PdfStructElem(pdfDoc, PdfName.Lbl));
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothValid("severalListNumberingsFirstInvalidTest", pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("severalListNumberingsFirstInvalidTest",
                    PdfUAExceptionMessageConstants.LIST_NUMBERING_IS_NOT_SPECIFIED, pdfUAConformance);
        }
    }

    private static PdfFont loadFont() {
        try {
            return PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
