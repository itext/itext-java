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
package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CanvasTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/CanvasTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/CanvasTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNABLE_TO_APPLY_PAGE_DEPENDENT_PROP_UNKNOWN_PAGE_ON_WHICH_ELEMENT_IS_DRAWN))
    public void canvasNoPageLinkTest() throws IOException, InterruptedException {
        String testName = "canvasNoPageLinkTest";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(pdfCanvas, pdf, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    public void canvasWithPageLinkTest() throws IOException, InterruptedException {
        String testName = "canvasWithPageLinkTest";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    public void canvasWithPageEnableTaggingTest01() throws IOException, InterruptedException {
        String testName = "canvasWithPageEnableTaggingTest01";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));

        pdf.setTagged();

        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.UNABLE_TO_APPLY_PAGE_DEPENDENT_PROP_UNKNOWN_PAGE_ON_WHICH_ELEMENT_IS_DRAWN),
            @LogMessage(messageTemplate = LogMessageConstant.PASSED_PAGE_SHALL_BE_ON_WHICH_CANVAS_WILL_BE_RENDERED)})
    public void canvasWithPageEnableTaggingTest02() throws IOException, InterruptedException {
        String testName = "canvasWithPageEnableTaggingTest02";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));

        pdf.setTagged();

        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);

        // This will disable tagging and also prevent annotations addition. Created tagged document is invalid. Expected log message.
        canvas.enableAutoTagging(null);

        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }
}
