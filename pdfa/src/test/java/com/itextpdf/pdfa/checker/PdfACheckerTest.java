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
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfTrueTypeFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfXrefTable;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfACheckerTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/pdfs/";

    private PdfAChecker pdfAChecker;

    @BeforeEach
    public void before() {
        pdfAChecker = new EmptyPdfAChecker();
        pdfAChecker.setFullCheckMode(true);
    }

    @Test
    public void checkPdfWithHugeAmountOfOutlinesTest() throws IOException {
        try(PdfDocument pdf = new PdfDocument(new PdfReader(SOURCE_FOLDER + "outlineStackOverflowTest01.pdf"))) {
            AssertUtil.doesNotThrow(() -> pdfAChecker.checkDocument(pdf.getCatalog()));
        }
    }

    @Test
    public void checkAppearanceStreamsWithCycle() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {

            PdfDictionary normalAppearance = new PdfDictionary();
            normalAppearance.put(PdfName.ON, normalAppearance);

            normalAppearance.makeIndirect(document);

            PdfAnnotation annotation = new PdfPopupAnnotation(new Rectangle(0f, 0f));
            annotation.setAppearance(PdfName.N, normalAppearance);

            PdfPage pageToCheck = document.addNewPage();
            pageToCheck.addAnnotation(annotation);

            // no assertions as we want to check that no exceptions would be thrown
            pdfAChecker.checkResourcesOfAppearanceStreams(annotation.getAppearanceDictionary());
        }
    }

    @Test()
    //TODO adapt after DEVSIX-5759 is fixed
    public void checkContentStreamPdfAText() throws IOException {
        PdfA1Checker testChecker = new PdfA1Checker(PdfAConformance.PDF_A_1B);
        PdfADocument pdfa = new PdfADocument(new PdfReader(new File(SOURCE_FOLDER + "InlineImagesPdfA.pdf")), new PdfWriter(new ByteArrayOutputStream()).setSmartMode(true));
        PdfStream firstContentStream = pdfa.getPage(1).getFirstContentStream();
        testChecker.setFullCheckMode(true);
        Assertions.assertThrows(NullPointerException.class, () -> testChecker.checkContentStream(firstContentStream),
                "NullPointer was not thrown on inline image.");
    }

    private static class EmptyPdfAChecker extends PdfAChecker {

        protected EmptyPdfAChecker() {
            super(null);
        }

        @Override
        public void checkSignatureType(boolean isCAdES) {

        }

        @Override
        public void checkCanvasStack(char stackOperation) {

        }

        @Override
        public void checkInlineImage(PdfStream inlineImage, PdfDictionary currentColorSpaces) {

        }

        @Override
        public void checkColor(CanvasGraphicsState gState, Color color, PdfDictionary currentColorSpaces, Boolean fill, PdfStream contentStream) {

        }

        @Override
        public void checkColorSpace(PdfColorSpace colorSpace, PdfObject object, PdfDictionary currentColorSpaces, boolean checkAlternate,
                Boolean fill) {

        }

        @Override
        public void checkRenderingIntent(PdfName intent) {

        }

        @Override
        public void checkFontGlyphs(PdfFont font, PdfStream contentStream) {

        }

        @Override
        public void checkExtGState(CanvasGraphicsState extGState, PdfStream contentStream) {

        }

        @Override
        public void checkFont(PdfFont pdfFont) {

        }

        @Override
        public void checkXrefTable(PdfXrefTable xrefTable) {

        }

        @Override
        public void checkCrypto(PdfObject crypto) {

        }

        @Override
        public void checkText(String text, PdfFont font) {

        }

        @Override
        protected void checkContentStream(PdfStream contentStream) {

        }

        @Override
        protected void checkContentStreamObject(PdfObject object) {

        }

        @Override
        protected long getMaxNumberOfIndirectObjects() {
            return 0;
        }

        @Override
        protected Set<PdfName> getForbiddenActions() {
            return null;
        }

        @Override
        protected Set<PdfName> getAllowedNamedActions() {
            return null;
        }

        @Override
        protected void checkAction(PdfDictionary action) {

        }

        @Override
        protected void checkAnnotation(PdfDictionary annotDic) {

        }

        @Override
        protected void checkCatalogValidEntries(PdfDictionary catalogDict) {

        }

        @Override
        protected void checkPageColorsUsages(PdfDictionary pageDict, PdfDictionary pageResources) {

        }

        @Override
        protected void checkImage(PdfStream image, PdfDictionary currentColorSpaces) {

        }

        @Override
        protected void checkFileSpec(PdfDictionary fileSpec) {

        }

        @Override
        protected void checkForm(PdfDictionary form) {

        }

        @Override
        protected void checkFormXObject(PdfStream form) {

        }

        @Override
        protected void checkLogicalStructure(PdfDictionary catalog) {

        }

        @Override
        protected void checkMetaData(PdfDictionary catalog) {

        }

        @Override
        protected void checkNonSymbolicTrueTypeFont(PdfTrueTypeFont trueTypeFont) {

        }

        @Override
        protected void checkOutputIntents(PdfDictionary catalog) {

        }

        @Override
        protected void checkPageObject(PdfDictionary page, PdfDictionary pageResources) {

        }

        @Override
        protected void checkPageSize(PdfDictionary page) {

        }

        @Override
        protected void checkPdfArray(PdfArray array) {

        }

        @Override
        protected void checkPdfDictionary(PdfDictionary dictionary) {

        }

        @Override
        protected void checkPdfName(PdfName name) {

        }

        @Override
        protected void checkPdfNumber(PdfNumber number) {

        }

        @Override
        protected void checkPdfStream(PdfStream stream) {

        }

        @Override
        protected void checkPdfString(PdfString string) {

        }

        @Override
        protected void checkSymbolicTrueTypeFont(PdfTrueTypeFont trueTypeFont) {

        }

        @Override
        protected void checkTrailer(PdfDictionary trailer) {

        }

        @Override
        protected void checkCatalog(PdfCatalog catalog) {

        }

        @Override
        protected void checkPageTransparency(PdfDictionary pageDict, PdfDictionary pageResources) {

        }
    }
}
