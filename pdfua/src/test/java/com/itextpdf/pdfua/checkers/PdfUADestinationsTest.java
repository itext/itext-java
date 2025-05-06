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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitRemoteGoToDestination;
import com.itextpdf.kernel.pdf.navigation.PdfNamedDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStructureDestination;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUADestinationsTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUADestinationsTest/";
    private static final Rectangle RECTANGLE = new Rectangle(200, 200, 100, 100);

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<String> destinationWrapperType() {
        return Arrays.asList("GoTo", "Destination", "Outline", "OutlineWithAction", "GoToR", "Manual", "GoToInRandomPlace");
    }

    @BeforeEach
    public void setUp() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER, false);
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void pureStructureDestinationTest(String destinationWrapType) throws IOException {
        String filename = "pureStructureDestinationTest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType, createStructureDestination(document));
        });
        switch (destinationWrapType) {
            case "Destination":
            case "Manual":
            case "Outline":
            case "GoToR":
            case "GoToInRandomPlace":
                framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
                break;
            case "GoTo":
            case "OutlineWithAction":
                // Verapdf doesn't allow actions with structure destination being placed in D entry. Instead, it requires
                // structure destination to be added into special SD entry. There is no such requirement in released PDF 2.0 spec.
                // Although it is already mentioned in errata version.
                framework.assertOnlyVeraPdfFail(filename, PdfUAConformance.PDF_UA_2);
                break;
        }
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void pureExplicitDestinationTest(String destinationWrapType) throws IOException {
        String filename = "pureExplicitDestinationTest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType, createExplicitDestination(document));
        });
        switch (destinationWrapType) {
            case "Destination":
            case "Manual":
            case "Outline":
            case "OutlineWithAction":
            case "GoTo":
                framework.assertBothFail(filename, PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION, PdfUAConformance.PDF_UA_2);
                break;
            case "GoToR":
                framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
                break;
            case "GoToInRandomPlace":
                // iText fails because of the way we search for goto actions.
                // We traverse whole document looking for a dictionary, which can represent GoTo action.
                // That's why in this particular example we fail, however in reality GoTo action cannot be added directly to catalog.
                framework.assertOnlyITextFail(filename, PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION, PdfUAConformance.PDF_UA_2);
                break;
        }
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void namedDestinationWithStructureDestinationTest(String destinationWrapType) throws IOException {
        String filename = "namedDestWithStructureDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType, createNamedDestination(document, "destination",
                    createStructureDestination(document)));
        });
        framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void namedDestinationWithDictionaryWithStructureDestinationTest(String destinationWrapType) throws IOException {
        String filename = "namedDestWithDictWithStructDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType, createNamedDestinationWithDictionary(document, createStructureDestination(document)));
        });
        switch (destinationWrapType) {
            case "Destination":
            case "Manual":
            case "Outline":
            case "GoTo":
            case "OutlineWithAction":
                // Verapdf doesn't allow name destination to contain dictionary with structure destination in D entry.
                // Instead, it wants it to be in special SD entry.
                framework.assertOnlyVeraPdfFail(filename, PdfUAConformance.PDF_UA_2);
                break;
            case "GoToR":
            case "GoToInRandomPlace":
                framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
                break;
        }
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void namedDestinationWithDictionaryAndSDWithStructureDestinationTest(String destinationWrapType) throws IOException {
        String filename = "namedDestWithDictAndSDWithStructDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType,
                    createNamedDestinationWithDictionaryAndSD(document, createStructureDestination(document)));
        });
        framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void namedDestinationWithExplicitDestinationTest(String destinationWrapType) throws IOException {
        String filename = "namedDestWithExplicitDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType, createNamedDestination(document, "destination",
                    createExplicitDestination(document)));
        });
        switch (destinationWrapType) {
            case "Destination":
            case "Manual":
            case "Outline":
            case "GoTo":
            case "OutlineWithAction":
                framework.assertBothFail(filename, PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION, PdfUAConformance.PDF_UA_2);
                break;
            case "GoToR":
                framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
                break;
            case "GoToInRandomPlace":
                // iText fails because of the way we search for goto actions.
                // We traverse whole document looking for a dictionary, which can represent GoTo action.
                // That's why in this particular example we fail, however in reality GoTo action cannot be added directly to catalog.
                framework.assertOnlyITextFail(filename, PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION, PdfUAConformance.PDF_UA_2);
                break;
        }
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void namedDestinationWithDictionaryAndSDWithExplicitDestinationTest(String destinationWrapType) throws IOException {
        String filename = "namedDestWithDictAndSDWithExplicitDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType,
                    createNamedDestinationWithDictionaryAndSD(document, createExplicitDestination(document)));
        });
        switch (destinationWrapType) {
            case "Destination":
            case "Manual":
            case "Outline":
            case "GoTo":
            case "OutlineWithAction":
            case "GoToInRandomPlace":
                // Verapdf for some reason allows explicit destinations in SD entry.
                // SD is specifically reserved for structure destinations,
                // that's why placing not structure destination in there is wrong in the first place.
                // However, if one is placed there, UA-2 exception is expected.
                framework.assertOnlyITextFail(filename, PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION,
                        PdfUAConformance.PDF_UA_2);
                break;
            case "GoToR":
                framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
                break;
        }
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void namedDestinationWithNamedDestinationWithStructureDestinationTest(String destinationWrapType) throws IOException {
        String filename = "namedDestWithNamedDestWithStructDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType, createNamedDestination(document, "destination1",
                    createNamedDestination(document, "destination2", createStructureDestination(document))));
        });
        switch (destinationWrapType) {
            case "Destination":
            case "Manual":
            case "Outline":
            case "GoTo":
            case "OutlineWithAction":
                // Verapdf doesn't allow named destination inside named destination, because it contradicts PDF 2.0 spec.
                framework.assertOnlyVeraPdfFail(filename, PdfUAConformance.PDF_UA_2);
                break;
            case "GoToR":
            case "GoToInRandomPlace":
                framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
                break;
        }
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void namedDestinationWithCyclicReferenceTest(String destinationWrapType) throws IOException {
        String filename = "namedDestWithCyclicReference_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType, createNamedDestination(document, "destination",
                    createNamedDestination(document, "destination", createStructureDestination(document))));
        });
        switch (destinationWrapType) {
            case "Destination":
            case "Manual":
            case "Outline":
            case "GoTo":
            case "OutlineWithAction":
                framework.assertBothFail(filename, PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION,
                        PdfUAConformance.PDF_UA_2);
                break;
            case "GoToR":
                framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
                break;
            case "GoToInRandomPlace":
                // iText fails because of the way we search for goto actions.
                // We traverse whole document looking for a dictionary, which can represent GoTo action.
                // That's why in this particular example we fail, however in reality GoTo action cannot be added directly to catalog.
                framework.assertOnlyITextFail(filename, PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION, PdfUAConformance.PDF_UA_2);
                break;
        }
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void stringDestinationWithStructureDestinationTest(String destinationWrapType) throws IOException {
        String filename = "stringDestWithStructureDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType, createStringDestination(document,
                    createStructureDestination(document)));
        });
        framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void stringDestinationWithDictionaryWithStructureDestinationTest(String destinationWrapType) throws IOException {
        String filename = "stringDestWithDictWithStructDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType, createStringDestinationWithDictionary(document, createStructureDestination(document)));
        });
        switch (destinationWrapType) {
            case "Destination":
            case "Manual":
            case "Outline":
            case "GoTo":
            case "OutlineWithAction":
                // Verapdf doesn't allow name destination to contain dictionary with structure destination in D entry.
                // Instead, it wants it to be in special SD entry.
                framework.assertOnlyVeraPdfFail(filename, PdfUAConformance.PDF_UA_2);
                break;
            case "GoToR":
            case "GoToInRandomPlace":
                framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
                break;
        }
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void stringDestinationWithDictionaryAndSDWithStructureDestinationTest(String destinationWrapType) throws IOException {
        String filename = "stringDestWithDictAndSDWithStructDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType,
                    createStringDestinationWithDictionaryAndSD(document, createStructureDestination(document)));
        });
        framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void stringDestinationWithExplicitDestinationTest(String destinationWrapType) throws IOException {
        String filename = "stringDestWithExplicitDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType, createStringDestination(document,
                    createExplicitDestination(document)));
        });
        switch (destinationWrapType) {
            case "Destination":
            case "Manual":
            case "Outline":
            case "GoTo":
            case "OutlineWithAction":
                framework.assertBothFail(filename, PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION, PdfUAConformance.PDF_UA_2);
                break;
            case "GoToR":
                framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
                break;
            case "GoToInRandomPlace":
                // iText fails because of the way we search for goto actions.
                // We traverse whole document looking for a dictionary, which can represent GoTo action.
                // That's why in this particular example we fail, however in reality GoTo action cannot be added directly to catalog.
                framework.assertOnlyITextFail(filename, PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION, PdfUAConformance.PDF_UA_2);
                break;
        }
    }

    @ParameterizedTest
    @MethodSource("destinationWrapperType")
    public void stringDestinationWithDictionaryAndSDWithExplicitDestinationTest(String destinationWrapType) throws IOException {
        String filename = "stringDestWithDictAndSDWithExplicitDest_" + destinationWrapType;
        framework.addBeforeGenerationHook(document -> {
            document.addNewPage();
            addDestinationToDocument(document, destinationWrapType,
                    createStringDestinationWithDictionaryAndSD(document, createExplicitDestination(document)));
        });
        switch (destinationWrapType) {
            case "Destination":
            case "Manual":
            case "Outline":
            case "GoTo":
            case "OutlineWithAction":
            case "GoToInRandomPlace":
                // Verapdf for some reason allows explicit destinations in SD entry.
                // SD is specifically reserved for structure destinations,
                // that's why placing not structure destination in there is wrong in the first place.
                // However, if one is placed there, UA-2 exception is expected.
                framework.assertOnlyITextFail(filename, PdfUAExceptionMessageConstants.DESTINATION_NOT_STRUCTURE_DESTINATION,
                        PdfUAConformance.PDF_UA_2);
                break;
            case "GoToR":
                framework.assertBothValid(filename, PdfUAConformance.PDF_UA_2);
                break;
        }
    }

    private void addDestinationToDocument(PdfDocument document, String destinationWrapType, PdfDestination destination) {
        switch (destinationWrapType) {
            case "GoTo":
                PdfLinkAnnotation goToLinkAnnotation = new PdfLinkAnnotation(RECTANGLE);
                goToLinkAnnotation.setContents("GoTo");
                goToLinkAnnotation.setAction(PdfAction.createGoTo(destination));
                document.getPage(1).addAnnotation(goToLinkAnnotation);
                break;
            case "Destination":
                PdfLinkAnnotation destinationLinkAnnotation = new PdfLinkAnnotation(RECTANGLE);
                destinationLinkAnnotation.setContents("Destination");
                destinationLinkAnnotation.setDestination(destination);
                document.getPage(1).addAnnotation(destinationLinkAnnotation);
                break;
            case "Outline":
                PdfOutline outlineWithDestination = document.getOutlines(false);
                outlineWithDestination.addOutline("destination").addDestination(destination);
                break;
            case "OutlineWithAction":
                PdfOutline outlineWithAction = document.getOutlines(false);
                outlineWithAction.addOutline("destination").addAction(PdfAction.createGoTo(destination));
                break;
            case "GoToR":
                PdfLinkAnnotation goToRLinkAnnotation = new PdfLinkAnnotation(RECTANGLE);
                goToRLinkAnnotation.setContents("GoToR");
                goToRLinkAnnotation.setAction(PdfAction.createGoToR("filename", 1));
                document.getPage(1).addAnnotation(goToRLinkAnnotation);
                break;
            case "Manual":
                PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(RECTANGLE);
                linkAnnotation.setContents("Manual");
                linkAnnotation.setDestination(destination);

                PdfPage page = document.getPage(1);
                PdfArray annots = new PdfArray();
                annots.add(linkAnnotation.getPdfObject());
                page.getPdfObject().put(PdfName.Annots, annots);
                page.setModified();

                TagTreePointer tagPointer = document.getTagStructureContext().getAutoTaggingPointer();
                tagPointer.addTag(StandardRoles.LINK);
                PdfPage prevPage = tagPointer.getCurrentPage();
                tagPointer.setPageForTagging(page).addAnnotationTag(linkAnnotation);
                if (prevPage != null) {
                    tagPointer.setPageForTagging(prevPage);
                }
                tagPointer.moveToParent();
                page.setTabOrder(PdfName.S);
                break;
            case "GoToInRandomPlace":
                PdfAction action = PdfAction.createGoTo(destination);
                document.getCatalog().getPdfObject().put(PdfName.GoTo, action.getPdfObject());
                break;
            default:
                Assertions.fail("No implementation for " + destinationWrapType);
        }
    }

    private PdfDestination createStructureDestination(PdfDocument document) {
        TagTreePointer pointer = new TagTreePointer(document);
        PdfStructElem structElem = document.getTagStructureContext().getPointerStructElem(pointer);
        return PdfStructureDestination.createFit(structElem);
    }

    private PdfDestination createExplicitDestination(PdfDocument document) {
        return PdfExplicitDestination.createFit(document.getPage(1));
    }

    private PdfDestination createRemoteExplicitDestination() {
        return PdfExplicitRemoteGoToDestination.createFit(1);
    }

    private PdfDestination createNamedDestination(PdfDocument document, String name, PdfDestination destination) {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(new PdfName(name), destination.getPdfObject());
        document.getCatalog().put(PdfName.Dests, dictionary);
        return new PdfNamedDestination(name);
    }

    private PdfDestination createNamedDestinationWithDictionary(PdfDocument document, PdfDestination destination) {
        PdfDictionary destinationDictionary = new PdfDictionary();
        destinationDictionary.put(PdfName.D, destination.getPdfObject());
        PdfDictionary dests = new PdfDictionary();
        dests.put(new PdfName("destination_name"), destinationDictionary);
        document.getCatalog().put(PdfName.Dests, dests);
        return new PdfNamedDestination("destination_name");
    }

    private PdfDestination createNamedDestinationWithDictionaryAndSD(PdfDocument document, PdfDestination destination) {
        PdfDictionary destinationDictionary = new PdfDictionary();
        destinationDictionary.put(PdfName.SD, destination.getPdfObject());
        PdfDictionary dests = new PdfDictionary();
        dests.put(new PdfName("destination_name"), destinationDictionary);
        document.getCatalog().put(PdfName.Dests, dests);
        return new PdfNamedDestination("destination_name");
    }

    private PdfDestination createStringDestination(PdfDocument document, PdfDestination destination) {
        document.getCatalog().getNameTree(PdfName.Dests)
                .addEntry("destination_name", destination.getPdfObject());
        return new PdfStringDestination("destination_name");
    }

    private PdfDestination createStringDestinationWithDictionary(PdfDocument document, PdfDestination destination) {
        PdfDictionary destinationDictionary = new PdfDictionary();
        destinationDictionary.put(PdfName.D, destination.getPdfObject());
        document.getCatalog().getNameTree(PdfName.Dests)
                .addEntry("destination_name", destinationDictionary);
        return new PdfStringDestination("destination_name");
    }

    private PdfDestination createStringDestinationWithDictionaryAndSD(PdfDocument document, PdfDestination destination) {
        PdfDictionary destinationDictionary = new PdfDictionary();
        destinationDictionary.put(PdfName.SD, destination.getPdfObject());
        document.getCatalog().getNameTree(PdfName.Dests)
                .addEntry("destination_name", destinationDictionary);
        return new PdfStringDestination("destination_name");
    }
}
