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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ILocationExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.RegexBasedLocationExtractionStrategy;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.*;

/**
 * This class tests the LocationExtractionStrategy framework.
 * It uses RegexBasedLocationExtractionStrategy, and searches for the word "Alice" in the book
 * "Alice in Wonderland" by Lewis Caroll on page 1.
 */
@Category(IntegrationTest.class)
public class LocationExtractTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/LocationExtractionTest/";

    @Test
    public void testLocationExtraction() throws IOException {
        String inputFile = sourceFolder + "aliceInWonderland.pdf";

        PdfReader reader = new PdfReader(inputFile);
        PdfDocument pdfDocument = new PdfDocument(reader);

        // calculate marked areas
        PdfPage page = pdfDocument.getPage(1);
        Collection<Rectangle> rectangleCollection = processPage(new RegexBasedLocationExtractionStrategy("Alice"), page);

        // close document
        pdfDocument.close();

        // compare rectangles
        Set<Rectangle> expectedRectangles = new HashSet<>();
        expectedRectangles.add(new Rectangle(174.67166f, 150.19658f, 29.191528f, 14.982529f));
        expectedRectangles.add(new Rectangle(200.95114f, 326.95657f, 29.297531f, 14.982544f));
        expectedRectangles.add(new Rectangle(250.17247f, 376.51657f, 29.191544f, 14.982544f));
        expectedRectangles.add(new Rectangle(434.33588f, 457.1566f, 29.191467f, 14.982544f));
        expectedRectangles.add(new Rectangle(374.3493f, 519.1966f, 29.191528f, 14.982483f));
        expectedRectangles.add(new Rectangle(510.3833f, 618.4366f, 29.380737f, 14.982483f));
        expectedRectangles.add(new Rectangle(84.0f, 649.3966f, 29.297523f, 14.982483f));

        Assert.assertTrue(expectedRectangles.size() == rectangleCollection.size());
        Assert.assertTrue(fuzzyContainsAll(rectangleCollection, expectedRectangles));

    }

    private Collection<Rectangle> processPage(ILocationExtractionStrategy strategy, PdfPage page) {
        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        parser.processPageContent(page);
        List<Rectangle> retval = new ArrayList<>();
        for(IPdfTextLocation l : strategy.getResultantLocations())
            retval.add(l.getRectangle());
        return retval;
    }

    /**
     * Comparing floats does not usually yield proper results for equality.
     * This function exists specifically to overcome that obstacle.
     *
     * @param rs
     * @param r
     * @return
     */
    private boolean fuzzyContains(Collection<Rectangle> rs, Rectangle r) {
        int x = (int) r.getX();
        int y = (int) r.getY();
        int w = (int) r.getWidth();
        int h = (int) r.getHeight();
        for (Rectangle r0 : rs) {
            int x0 = (int) r0.getX();
            int y0 = (int) r0.getY();
            int w0 = (int) r0.getWidth();
            int h0 = (int) r0.getHeight();
            if (x0 == x && y0 == y && w0 == w && h0 == h)
                return true;
        }
        return false;
    }

    /**
     * This function tests whether a first collection contains all elements of a second collection.
     * This method does not perform its job fast, but is only used for testing.
     *
     * @param rs0
     * @param rs1
     * @return true iff rs0 contains all elements of rs1
     */
    private boolean fuzzyContainsAll(Collection<Rectangle> rs0, Collection<Rectangle> rs1) {
        for (Rectangle r1 : rs1) {
            if (!fuzzyContains(rs0, r1))
                return false;
        }
        return true;
    }

}
