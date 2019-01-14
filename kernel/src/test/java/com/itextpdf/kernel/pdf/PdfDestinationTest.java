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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfStringFS;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitRemoteGoToDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStructureDestination;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNull;

@Category(IntegrationTest.class)
public class PdfDestinationTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDestinationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfDestinationTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void destTest01() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleNoLinks.pdf";
        String outFile = destinationFolder + "destTest01.pdf";
        String cmpFile = sourceFolder + "cmp_destTest01.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcFile), new PdfWriter(outFile));
        PdfPage firstPage = document.getPage(1);
        
        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        linkExplicitDest.setAction(PdfAction.createGoTo(PdfExplicitDestination.createFit(document.getPage(2))));
        firstPage.addAnnotation(linkExplicitDest);
        
        PdfLinkAnnotation linkStringDest = new PdfLinkAnnotation(new Rectangle(35, 760, 160, 15));
        PdfExplicitDestination destToPage3 = PdfExplicitDestination.createFit(document.getPage(3));
        String stringDest = "thirdPageDest";
        document.addNamedDestination(stringDest, destToPage3.getPdfObject());
        linkStringDest.setAction(PdfAction.createGoTo(new PdfStringDestination(stringDest)));
        firstPage.addAnnotation(linkStringDest);
        
        document.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest01() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest01.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest01.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2, 3), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest02() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest02.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest02.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest03() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest03.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest03.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest04() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest04.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest04.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 3), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest05() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest05.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest05.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2, 3, 1), destDoc);
        destDoc.close();

        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest06() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "sourceWithNamedDestination.pdf";
        String outFile = destinationFolder + "destCopyingTest06.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest06.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2, 1), destDoc);
        destDoc.close();

        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest07() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "sourceStringDestWithPageNumber.pdf";
        String outFile = destinationFolder + "destCopyingTest07.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest07.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(new PdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2, 1), destDoc);
        destDoc.close();

        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void structureDestination01Test() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "customRolesMappingPdf2.pdf";
        String outFile = destinationFolder + "structureDestination01Test.pdf";
        String cmpFile = sourceFolder + "cmp_structureDestination01Test.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcFile), new PdfWriter(outFile));

        PdfStructElem imgElement = new PdfStructElem((PdfDictionary) document.getPdfObject(13));
        PdfStructureDestination dest = PdfStructureDestination.createFit(imgElement);

        PdfPage secondPage = document.addNewPage();

        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        linkExplicitDest.setAction(PdfAction.createGoTo(dest));
        secondPage.addAnnotation(linkExplicitDest);

        document.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void structureDestination02Test() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "customRolesMappingPdf2.pdf";
        String outFile = destinationFolder + "structureDestination02Test.pdf";
        String cmpFile = sourceFolder + "cmp_structureDestination02Test.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcFile), new PdfWriter(outFile));

        PdfStructElem imgElement = new PdfStructElem((PdfDictionary) document.getPdfObject(13));
        PdfStructureDestination dest = PdfStructureDestination.createFit(imgElement);

        PdfPage secondPage = document.addNewPage();
        PdfPage thirdPage = document.addNewPage();

        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        PdfAction gotoStructAction = PdfAction.createGoTo(PdfExplicitDestination.createFit(thirdPage));
        gotoStructAction.put(PdfName.SD, dest.getPdfObject());
        linkExplicitDest.setAction(gotoStructAction);
        secondPage.addAnnotation(linkExplicitDest);

        document.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void makeDestination01Test() throws IOException {
        String srcFile = sourceFolder + "cmp_structureDestination01Test.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(srcFile));
        PdfObject destObj = ((PdfLinkAnnotation) pdfDocument.getPage(2).getAnnotations().get(0)).getAction().get(PdfName.D);
        PdfDestination destWrapper = PdfDestination.makeDestination(destObj);
        Assert.assertEquals(PdfStructureDestination.class, destWrapper.getClass());
    }

    @Test
    public void remoteGoToDestinationTest() throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_remoteGoToDestinationTest.pdf";
        String outFile = destinationFolder + "remoteGoToDestinationTest.pdf";

        PdfDocument out = new PdfDocument(new PdfWriter(outFile));
        out.addNewPage();

        List<PdfDestination> destinations = new ArrayList<>(7);
        destinations.add(PdfExplicitRemoteGoToDestination.createFit(1));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitH(1, 10));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitV(1, 10));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitR(1, 10, 10, 10, 10));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitB(1));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitBH(1, 10));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitBV(1, 10));

        int y = 785;
        for (PdfDestination destination : destinations) {
            PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, y, 160, 15));
            PdfAction action = PdfAction.createGoToR(new PdfStringFS("Some fake destination"), destination);
            linkExplicitDest.setAction(action);
            out.getFirstPage().addAnnotation(linkExplicitDest);
            y -= 20;
        }
        out.close();
        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }
}
