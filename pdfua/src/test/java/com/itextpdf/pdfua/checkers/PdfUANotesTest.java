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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUANotesTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUANotesTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    
    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfUAConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    private static String getRoleBasedOnConformance(PdfUAConformance pdfUAConformance) {
        return pdfUAConformance == PdfUAConformance.PDF_UA_1 ? StandardRoles.NOTE : StandardRoles.FENOTE;
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addNoteForUA2AndFENoteForUA1Test(PdfUAConformance pdfUAConformance) {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("FENote");
                PdfFont font;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                        StandardRoles.FENOTE : StandardRoles.NOTE);
                return note;
            }
        });
        String message = Assertions.assertThrows(PdfException.class,
                // It doesn't matter what we call here.
                // Test fails on document creation and verapdf validation isn't triggered anyway.
                () -> framework.assertOnlyVeraPdfFail("addNoteForUA2AndFENoteForUA1", pdfUAConformance)).getMessage();
        String expectedExceptionMessage = pdfUAConformance == PdfUAConformance.PDF_UA_1 ?
                MessageFormatUtil.format(
                        KernelExceptionMessageConstant.ROLE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, StandardRoles.FENOTE) :
                MessageFormatUtil.format(
                        KernelExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, StandardRoles.NOTE, StandardNamespaces.PDF_2_0);
        Assertions.assertEquals(expectedExceptionMessage, message);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addFENoteWithoutReferencesTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("FENote");
                PdfFont font;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));
                return note;
            }
        });
        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("addFENoteWithoutReferences",
                    PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("addFENoteWithoutReferences", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addFENoteWithValidNoteTypeTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("FENote");
                PdfFont font;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));

                PdfDictionary attribute = new PdfDictionary();
                attribute.put(PdfName.O, PdfName.FENote);
                attribute.put(PdfName.NoteType, PdfName.Endnote);
                note.getAccessibilityProperties().addAttributes(new PdfStructureAttributes(attribute));

                return note;
            }
        });
        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("addFENoteWithValidNoteTypeTest",
                    PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("addFENoteWithValidNoteTypeTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addFENoteWithInvalidNoteTypeTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework        .addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("FENote");
                PdfFont font;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));

                PdfDictionary attribute = new PdfDictionary();
                attribute.put(PdfName.O, PdfName.FENote);
                attribute.put(PdfName.NoteType, PdfName.End);
                note.getAccessibilityProperties().addAttributes(new PdfStructureAttributes(attribute));

                return note;
            }
        });
        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("addFENoteWithInvalidNoteTypeTest",
                    PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("addFENoteWithInvalidNoteTypeTest",
                    PdfUAExceptionMessageConstants.INCORRECT_NOTE_TYPE_VALUE, pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY, count = 4), ignore = true)
    public void realContentDoesntHaveReferenceTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
            pdfDocument.addNewPage();
            PdfAnnotation annotation = new PdfTextAnnotation(new Rectangle(100, 100)).setContents("Real content");
            pdfDocument.getPage(1).addAnnotation(annotation);
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("FENote");
                PdfFont font;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                            EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));

                return note;
            }
        });
        framework.addAfterGenerationHook(pdfDocument -> {
            TagTreePointer pointer = new TagTreePointer(pdfDocument);
            pointer.moveToKid(getRoleBasedOnConformance(pdfUAConformance));
            TagTreePointer feNotePointer = new TagTreePointer(pointer);
            feNotePointer.applyProperties(new DefaultAccessibilityProperties(pointer.getRole())
                    .addRef(pointer.moveToRoot().moveToKid(StandardRoles.ANNOT)));
            pointer.moveToRoot();
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("realContentDoesntHaveReferenceTest",
                    PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("realContentDoesntHaveReferenceTest",
                    PdfUAExceptionMessageConstants.CONTENT_NOT_REFERENCING_FE_NOTE, pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY, count = 4), ignore = true)
    public void noteDoesntHaveReferenceTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
            pdfDocument.addNewPage();
            PdfAnnotation annotation = new PdfTextAnnotation(new Rectangle(100, 100)).setContents("Real content");
            pdfDocument.getPage(1).addAnnotation(annotation);
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("FENote");
                PdfFont font;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                            EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));

                return note;
            }
        });
        framework.addAfterGenerationHook(pdfDocument -> {
            TagTreePointer pointer = new TagTreePointer(pdfDocument);
            pointer.moveToKid(StandardRoles.ANNOT);
            TagTreePointer realContentPointer = new TagTreePointer(pointer);
            realContentPointer.applyProperties(new DefaultAccessibilityProperties(pointer.getRole())
                    .addRef(pointer.moveToRoot().moveToKid(getRoleBasedOnConformance(pdfUAConformance))));
            pointer.moveToRoot();
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("noteDoesntHaveReferenceTest",
                    PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothFail("noteDoesntHaveReferenceTest",
                    PdfUAExceptionMessageConstants.FE_NOTE_NOT_REFERENCING_CONTENT, pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY, count = 4), ignore = true)
    public void feNoteWithValidReferencesTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
            pdfDocument.addNewPage();
            PdfAnnotation annotation = new PdfTextAnnotation(new Rectangle(100, 100)).setContents("Real content");
            pdfDocument.getPage(1).addAnnotation(annotation);
        });
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("FENote");
                PdfFont font;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                            EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));

                return note;
            }
        });
        framework.addAfterGenerationHook(pdfDocument -> {
            TagTreePointer pointer = new TagTreePointer(pdfDocument);
            pointer.moveToKid(StandardRoles.ANNOT);
            TagTreePointer realContentPointer = new TagTreePointer(pointer);
            realContentPointer.applyProperties(new DefaultAccessibilityProperties(pointer.getRole())
                    .addRef(pointer.moveToRoot().moveToKid(getRoleBasedOnConformance(pdfUAConformance))));

            TagTreePointer notePointer = new TagTreePointer(pointer);
            notePointer.applyProperties(new DefaultAccessibilityProperties(pointer.getRole())
                    .addRef(pointer.moveToRoot().moveToKid(StandardRoles.ANNOT)));

            pointer.moveToRoot();
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("feNoteWithValidReferencesTest",
                    PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("feNoteWithValidReferencesTest", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addNoteWithoutIdTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("note");
                PdfFont font = null;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));
                return note;
            }
        });
        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("noteWithoutID",
                    PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("noteWithoutID", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, ignore = true)})
    public void addTwoNotesWithSameIdTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addSuppliers(
                new UaValidationTestFramework.Generator<IBlockElement>() {
                    @Override
                    public IBlockElement generate() {
                        Paragraph note = new Paragraph("note 1");
                        PdfFont font = null;
                        try {
                            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        note.setFont(font);
                        note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));
                        note.getAccessibilityProperties().setStructureElementIdString("123");
                        return note;
                    }
                },
                new UaValidationTestFramework.Generator<IBlockElement>() {
                    @Override
                    public IBlockElement generate() {
                        Paragraph note = new Paragraph("note 2");
                        PdfFont font = null;
                        try {
                            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        note.setFont(font);
                        note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));
                        note.getAccessibilityProperties().setStructureElementIdString("123");
                        return note;
                    }
                });
        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("twoNotesWithSameId", MessageFormatUtil.format(
                            PdfUAExceptionMessageConstants.NON_UNIQUE_ID_ENTRY_IN_STRUCT_TREE_ROOT, "123"),
                    false, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("twoNotesWithSameId", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addNoteWithValidIdTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addSuppliers(new UaValidationTestFramework.Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph note = new Paragraph("note");
                PdfFont font = null;
                try {
                    font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                note.setFont(font);
                note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));
                note.getAccessibilityProperties().setStructureElementIdString("123");
                return note;
            }
        });
        framework.assertBothValid("noteWithValidID", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void addTwoNotesWithDifferentIdTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addSuppliers(
                new UaValidationTestFramework.Generator<IBlockElement>() {
                    @Override
                    public IBlockElement generate() {
                        Paragraph note = new Paragraph("note 1");
                        PdfFont font = null;
                        try {
                            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        note.setFont(font);
                        note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));
                        note.getAccessibilityProperties().setStructureElementIdString("123");
                        return note;
                    }
                },
                new UaValidationTestFramework.Generator<IBlockElement>() {
                    @Override
                    public IBlockElement generate() {
                        Paragraph note = new Paragraph("note 2");
                        PdfFont font = null;
                        try {
                            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        note.setFont(font);
                        note.getAccessibilityProperties().setRole(getRoleBasedOnConformance(pdfUAConformance));
                        note.getAccessibilityProperties().setStructureElementIdString("234");
                        return note;
                    }
                });
        framework.assertBothValid("twoNotesWithDifferentId", pdfUAConformance);
    }
}
