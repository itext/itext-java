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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfNamedDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStructureDestination;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Tag("IntegrationTest")
public class PdfUA2LinkAnnotationTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUA2LinkAnnotationTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfName> testSources() {
        return Arrays.asList(PdfName.Dest, PdfName.SD, PdfName.D);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void linkAnnotationIsNotTaggedTest(PdfName destLocation) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfStructElem structElem = getPdfStructElem(pdfDoc);

            PdfStructureDestination destination = PdfStructureDestination.createFit(structElem);
            PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
            addDestination(destLocation, linkAnnotation, destination);

            pdfDoc.getPage(1).addAnnotation(-1, linkAnnotation, false);
        });
        framework.assertBothFail("linkAnnotationIsNotTagged_" + destLocation.getValue(), PdfUAExceptionMessageConstants
                .LINK_ANNOT_IS_NOT_NESTED_WITHIN_LINK_OR_REFERENCE, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void linkAnnotationWithInvalidTagTest(PdfName destLocation) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfStructElem structElem = getPdfStructElem(pdfDoc);

            PdfStructureDestination destination = PdfStructureDestination.createFit(structElem);
            PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
            linkAnnotation.setContents("Some text");
            addDestination(destLocation, linkAnnotation, destination);

            pdfDoc.getPage(1).addAnnotation(-1, linkAnnotation, false);
            TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer()
                    .addTag(StandardRoles.ANNOT);
            tagPointer.addAnnotationTag(linkAnnotation);
        });
        framework.assertBothFail("linkAnnotationWithInvalidTag_" + destLocation.getValue(), PdfUAExceptionMessageConstants
                .LINK_ANNOT_IS_NOT_NESTED_WITHIN_LINK_OR_REFERENCE, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void linkAnnotationWithReferenceTagTest(PdfName destLocation) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfStructElem structElem = getPdfStructElem(pdfDoc);

            PdfStructureDestination destination = PdfStructureDestination.createFit(structElem);
            PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
            linkAnnotation.setContents("Some text");
            addDestination(destLocation, linkAnnotation, destination);

            pdfDoc.getPage(1).addAnnotation(-1, linkAnnotation, false);
            TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer()
                    .moveToKid(StandardRoles.P).setNamespaceForNewTags(new PdfNamespace(StandardNamespaces.PDF_1_7))
                    .addTag(StandardRoles.REFERENCE);
            tagPointer.addAnnotationTag(linkAnnotation);
        });
        if (PdfName.D.equals(destLocation)) {
            // VeraPDF doesn't allow actions with structure destination being placed in D entry. Instead, it requires
            // structure destination to be added into special SD entry. There is no such requirement in released
            // PDF 2.0 spec. Although it is already mentioned in errata version.
            framework.assertOnlyVeraPdfFail("linkAnnotationWithReferenceTag_" + destLocation.getValue(),
                    PdfUAConformance.PDF_UA_2);
        } else {
            framework.assertBothValid("linkAnnotationWithReferenceTag_" + destLocation.getValue(),
                    PdfUAConformance.PDF_UA_2);
        }
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void differentStructureDestinationsInSameStructureElementTest(PdfName destLocation) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfStructElem structElem = getPdfStructElem(pdfDoc);
            PdfStructElem structElem2 = structElem.addKid(new PdfStructElem(pdfDoc, PdfName.P));

            PdfStructureDestination dest1 = PdfStructureDestination.createFit(structElem);
            PdfStructureDestination dest2 = PdfStructureDestination.createFit(structElem2);

            addLinkAnnotations(destLocation, pdfDoc, dest1, dest2, false);
        });
        String filename = "differentStructureDestinations_";
        framework.assertBothFail(filename + destLocation.getValue(), PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void differentNamedDestinationsInSameStructureElementTest(PdfName destLocation) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfStructElem structElem = getPdfStructElem(pdfDoc);
            PdfStructElem structElem2 = structElem.addKid(new PdfStructElem(pdfDoc, PdfName.P));

            PdfNamedDestination namedDestination1 = getNamedDestination(pdfDoc, structElem, "dest");
            PdfNamedDestination namedDestination2 = getNamedDestination(pdfDoc, structElem2, "dest2");

            addLinkAnnotations(destLocation, pdfDoc, namedDestination1, namedDestination2, false);
        });
        framework.assertBothFail("differentNamedDestinations_" + destLocation.getValue(),
                PdfUAExceptionMessageConstants.DIFFERENT_LINKS_IN_SINGLE_STRUCT_ELEM, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    public void differentStringDestinationsInSameStructureElementTest(PdfName destLocation) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfStructElem structElem = getPdfStructElem(pdfDoc);
            PdfStructElem structElem2 = structElem.addKid(new PdfStructElem(pdfDoc, PdfName.P));

            PdfStringDestination namedDestination1 = getStringDestination(pdfDoc, structElem, "dest");
            PdfStringDestination namedDestination2 = getStringDestination(pdfDoc, structElem2, "dest2");

            addLinkAnnotations(destLocation, pdfDoc, namedDestination1, namedDestination2, false);
        });
        framework.assertBothFail("differentStringDestinations_" + destLocation.getValue(),
                PdfUAExceptionMessageConstants.DIFFERENT_LINKS_IN_SINGLE_STRUCT_ELEM, PdfUAConformance.PDF_UA_2);
    }

    private static void addLinkAnnotations(PdfName destLocation, PdfDocument pdfDoc, PdfDestination destination1,
                                           PdfDestination destination2, boolean isSeparateAnnots) {
        PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        linkAnnotation.setContents("Some text");
        addDestination(destLocation, linkAnnotation, destination1);

        PdfLinkAnnotation linkAnnotation2 = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        linkAnnotation2.setContents("Some text2");
        addDestination(destLocation, linkAnnotation2, destination2);

        if (isSeparateAnnots) {
            pdfDoc.getPage(1).addAnnotation(linkAnnotation).addAnnotation(linkAnnotation2);
        } else {
            pdfDoc.getPage(1).addAnnotation(-1, linkAnnotation, false).addAnnotation(-1, linkAnnotation2, false);
            TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer()
                    .addTag(StandardRoles.LINK);
            tagPointer.addAnnotationTag(linkAnnotation);
            tagPointer.addAnnotationTag(linkAnnotation2);
        }
    }

    private static PdfNamedDestination getNamedDestination(PdfDocument pdfDoc, PdfStructElem structElem, String name) {
        // Named destination is referred to indirectly by means of a name object in PDF 1.1. In PDF 1.1, the 
        // correspondence between name objects and destinations shall be defined by the Dests entry in the catalog.
        PdfStructureDestination dest = PdfStructureDestination.createFit(structElem);
        PdfDictionary dests = pdfDoc.getCatalog().getPdfObject().getAsDictionary(PdfName.Dests);
        if (dests == null) {
            dests = new PdfDictionary();
        }
        PdfName destName = new PdfName(name);
        dests.put(destName, dest.getPdfObject());
        pdfDoc.getCatalog().put(PdfName.Dests, dests);
        return new PdfNamedDestination(destName);
    }

    private static PdfStringDestination getStringDestination(PdfDocument pdfDoc, PdfStructElem structElem, String name) {
        PdfStructureDestination dest = PdfStructureDestination.createFit(structElem);
        pdfDoc.addNamedDestination(name, dest.getPdfObject());
        return new PdfStringDestination(name);
    }

    private static PdfStructElem getPdfStructElem(PdfDocument pdfDoc) {
        Document document = new Document(pdfDoc);
        document.setFont(loadFont());
        Paragraph paragraph = new Paragraph("Some text");
        document.add(paragraph);
        TagStructureContext context = pdfDoc.getTagStructureContext();
        TagTreePointer tagPointer = context.getAutoTaggingPointer();
        return context.getPointerStructElem(tagPointer);
    }

    private static void addDestination(PdfName destLocation, PdfLinkAnnotation link, PdfDestination dest) {
        if (PdfName.Dest.equals(destLocation)) {
            link.setDestination(dest);
        } else {
            PdfAction gotoStructAction = PdfAction.createGoTo(dest);
            gotoStructAction.put(destLocation, dest.getPdfObject());
            link.setAction(gotoStructAction);
        }
    }

    private static PdfFont loadFont() {
        try {
            return PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI,
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validate(String filename, String expectedMessage, PdfName destLocation, UaValidationTestFramework framework) throws IOException {
        // TODO DEVSIX-9580. VeraPDF claims the document to be valid, although it's not.
        //  We will need to update this test when veraPDF behavior is fixed and veraPDF version is updated.
        if (PdfName.D.equals(destLocation)) {
            // In case PdfName.D equals destLocation, VeraPDF doesn't allow actions with structure destination being
            // placed in D entry. Instead, it requires structure destination to be added into special SD entry. There is
            // no such requirement in released PDF 2.0 spec. Although it is already mentioned in errata version.
            framework.assertBothFail(filename + destLocation.getValue(), PdfUAConformance.PDF_UA_2);
        } else {
            framework.assertOnlyITextFail(filename + destLocation.getValue(), expectedMessage, PdfUAConformance.PDF_UA_2);
        }
    }
}
