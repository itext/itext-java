/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfTrueTypeFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfXrefTable;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfTransparencyGroup;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfACheckerTest extends ExtendedITextTest {

    private PdfAChecker pdfAChecker;

    @Before
    public void before() {
        pdfAChecker = new EmptyPdfAChecker();
        pdfAChecker.setFullCheckMode(true);
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

    private static class EmptyPdfAChecker extends PdfAChecker {

        protected EmptyPdfAChecker() {
            super(null);
        }

        @Override
        public void checkCanvasStack(char stackOperation) {

        }

        @Override
        public void checkInlineImage(PdfStream inlineImage, PdfDictionary currentColorSpaces) {

        }

        @Override
        public void checkColor(Color color, PdfDictionary currentColorSpaces, Boolean fill) {

        }

        @Override
        public void checkColorSpace(PdfColorSpace colorSpace, PdfDictionary currentColorSpaces, boolean checkAlternate,
                Boolean fill) {

        }

        @Override
        public void checkRenderingIntent(PdfName intent) {

        }

        @Override
        public void checkExtGState(CanvasGraphicsState extGState) {

        }

        @Override
        public void checkFont(PdfFont pdfFont) {

        }

        @Override
        public void checkXrefTable(PdfXrefTable xrefTable) {

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
        protected void checkColorsUsages() {

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
    }
}
