/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.kernel.pdf;

import static org.junit.Assert.assertTrue;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcrDictionary;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.CompareTool.CompareResult;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class ParentTreeTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/ParentTreeTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/ParentTreeTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void test01() throws IOException, InterruptedException {
        String outFile = destinationFolder + "parentTreeTest01.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest01.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage firstPage = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(firstPage);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, firstPage));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(firstPage, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(firstPage, span1))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfPage secondPage = document.addNewPage();

        document.close();
        assertTrue(checkParentTree(outFile, cmpFile));
    }


    @Test
    public void test02() throws IOException, InterruptedException {
        String outFile = destinationFolder + "parentTreeTest02.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest02.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage firstPage = document.addNewPage();

        PdfPage secondPage = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(secondPage);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, secondPage));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(secondPage, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(secondPage, span1))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        document.close();

        assertTrue(checkParentTree(outFile, cmpFile));
    }

    @Test
    public void test03() throws IOException, InterruptedException {
        String outFile = destinationFolder + "parentTreeTest03.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest03.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage firstPage = document.addNewPage();

        for (int i = 0; i < 51; i++) {
        	PdfPage anotherPage = document.addNewPage();
        	PdfCanvas canvas = new PdfCanvas(anotherPage);
        	canvas.beginText();
        	canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        	canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        	PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        	PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, anotherPage));
        	canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(anotherPage, span1))));
        	canvas.showText("Hello ");
        	canvas.closeTag();
        	canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(anotherPage, span1))));
        	canvas.showText("World");
        	canvas.closeTag();
        	canvas.endText();
        	canvas.release();
        }
        document.close();

        assertTrue(checkParentTree(outFile, cmpFile));
    }

    @Test
    public void test04() throws IOException, InterruptedException {
        String outFile = destinationFolder + "parentTreeTest04.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest04.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        for (int i = 0; i < 51; i++) {
        	PdfPage anotherPage = document.addNewPage();
        	PdfCanvas canvas = new PdfCanvas(anotherPage);
        	canvas.beginText();
        	canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        	canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        	PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        	PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, anotherPage));
        	canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(anotherPage, span1))));
        	canvas.showText("Hello ");
        	canvas.closeTag();
        	canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(anotherPage, span1))));
        	canvas.showText("World");
        	canvas.closeTag();
        	canvas.endText();
        	canvas.release();
        }
        document.close();

        assertTrue(checkParentTree(outFile, cmpFile));
    }

    @Test
    public void test05() throws IOException, InterruptedException {
        String outFile = destinationFolder + "parentTreeTest05.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest05.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

    	PdfPage page1 = document.addNewPage();
    	PdfCanvas canvas = new PdfCanvas(page1);
    	canvas.beginText();
    	canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
    	canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
    	PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
    	PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page1));

    	canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page1, span1))));
    	canvas.showText("Hello ");
    	canvas.closeTag();
    	canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(page1, span1))));
    	canvas.showText("World");
    	canvas.closeTag();
    	canvas.endText();
    	canvas.release();

    	PdfPage page2 = document.addNewPage();
    	canvas = new PdfCanvas(page2);
    	canvas.beginText();
    	canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 24);
    	canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
    	paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
    	span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page2));
    	canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(page2, span1))));
    	canvas.showText("Hello ");
    	canvas.closeTag();
    	PdfStructElem span2 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, page2));
    	canvas.openTag(new CanvasTag(span2.addKid(new PdfMcrNumber(page2, span2))));
    	canvas.showText("World");
    	canvas.closeTag();
    	canvas.endText();
    	canvas.release();

    	document.close();

    	assertTrue(checkParentTree(outFile, cmpFile));
    }

    @Test
    public void test06() throws IOException, InterruptedException {
        String outFile = destinationFolder + "parentTreeTest06.pdf";
        String cmpFile = sourceFolder + "cmp_parentTreeTest06.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(outFile));
        document.setTagged();

        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));

        PdfPage firstPage = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(firstPage);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 24);
        canvas.setTextMatrix(1, 0, 0, 1, 32, 512);
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P));
        PdfStructElem span1 = paragraph.addKid(new PdfStructElem(document, PdfName.Span, firstPage));
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrNumber(firstPage, span1))));
        canvas.showText("Hello ");
        canvas.closeTag();
        canvas.openTag(new CanvasTag(span1.addKid(new PdfMcrDictionary(firstPage, span1))));
        canvas.showText("World");
        canvas.closeTag();
        canvas.endText();
        canvas.release();

        PdfPage secondPage = document.addNewPage();
        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        secondPage.addAnnotation(linkExplicitDest);

        document.close();
        assertTrue(checkParentTree(outFile, cmpFile));
    }

    private boolean checkParentTree(String outFileName, String cmpFileName) throws IOException {
    	PdfReader outReader = new PdfReader(outFileName);
    	PdfDocument outDocument = new PdfDocument(outReader);
    	PdfReader cmpReader = new PdfReader(cmpFileName);
    	PdfDocument cmpDocument = new PdfDocument(cmpReader);
    	CompareResult result = new CompareTool().compareByCatalog(outDocument, cmpDocument);
    	if (!result.isOk()) {
            System.out.println(result.getReport());
        }
    	return result.isOk();
    }
}
