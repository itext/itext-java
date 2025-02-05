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
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is designed to search for the occurrences of a regular expression and return the resultant rectangles.
 * Do note that this class holds all text locations and can't be used for processing multiple pages.
 * If you want to extract text from several pages of pdf document you have to create a new instance
 * of {@link RegexBasedLocationExtractionStrategy} for each page.
 * <p>
 * Here is an example of usage with new instance per each page:
 * <code>
 *         PdfDocument document = new PdfDocument(new PdfReader("..."));
 *         for (int i = 1; i &lt;= document.getNumberOfPages(); ++i) {
 *             RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy("");
 *             PdfCanvasProcessor processor = new PdfCanvasProcessor(extractionStrategy);
 *             processor.processPageContent(document.getPage(i));
 *             for (IPdfTextLocation location : extractionStrategy.getResultantLocations()) {
 *                 //process locations ...
 *              }
 *         }
 * </code>
 */
public class RegexBasedLocationExtractionStrategy implements ILocationExtractionStrategy {
    private static final float EPS = 1.0E-4F;
    private final Pattern pattern;
    private final List<CharacterRenderInfo> parseResult = new ArrayList<>();

    public RegexBasedLocationExtractionStrategy(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public RegexBasedLocationExtractionStrategy(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IPdfTextLocation> getResultantLocations() {
        // align characters in "logical" order
        Collections.sort(parseResult, new TextChunkLocationBasedComparator(new DefaultTextChunkLocationComparator()));

        // process parse results
        List<IPdfTextLocation> retval = new ArrayList<>();

        CharacterRenderInfo.StringConversionInfo txt = CharacterRenderInfo.mapString(parseResult);

        Matcher mat = pattern.matcher(txt.text);
        while (mat.find()) {
            Integer startIndex = getStartIndex(txt.indexMap, mat.start(), txt.text);
            Integer endIndex = getEndIndex(txt.indexMap, mat.end() - 1);
            if (startIndex != null && endIndex != null && startIndex <= endIndex) {
                for (Rectangle r : toRectangles(parseResult.subList(startIndex.intValue(), endIndex.intValue() + 1))) {
                    retval.add(new DefaultPdfTextLocation(r, mat.group(0)));
                }
            }
        }

        /* sort
         * even though the return type is Collection<Rectangle>, we apply a sorting algorithm here
         * This is to ensure that tests that use this functionality (for instance to generate pdf with
         * areas of interest highlighted) will not break when compared.
         */
        Collections.sort(retval, new PdfTextLocationComparator());

        // ligatures can produces same rectangle
        removeDuplicates(retval);

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eventOccurred(IEventData data, EventType type) {
        parseResult.addAll(toCRI((TextRenderInfo) data));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<EventType> getSupportedEvents() {
        return Collections.singleton(EventType.RENDER_TEXT);
    }

    /**
     * Convert {@link TextRenderInfo} to {@link CharacterRenderInfo}
     * This method is public and not final so that custom implementations can choose to override it.
     * Other implementations of {@code CharacterRenderInfo} may choose to store different properties than
     * merely the {@link Rectangle} describing the bounding box. E.g. a custom implementation might choose to
     * store {@link Color} information as well, to better match the content surrounding the redaction {@link Rectangle}.
     *
     * @param tri {@link TextRenderInfo} object
     *
     * @return a list of {@link CharacterRenderInfo}s which represents the passed {@link TextRenderInfo} ?
     */
    protected List<CharacterRenderInfo> toCRI(TextRenderInfo tri) {
        List<CharacterRenderInfo> cris = new ArrayList<>();
        for (TextRenderInfo subTri : tri.getCharacterRenderInfos()) {
            cris.add(new CharacterRenderInfo(subTri));
        }
        return cris;
    }

    /**
     * Converts {@link CharacterRenderInfo} objects to {@link Rectangle}s
     * This method is protected and not final so that custom implementations can choose to override it.
     * E.g. other implementations may choose to add padding/margin to the Rectangles.
     * This method also offers a convenient access point to the mapping of {@link CharacterRenderInfo} to {@link Rectangle}.
     * This mapping enables (custom implementations) to match color of text in redacted Rectangles,
     * or match color of background, by the mere virtue of offering access to the {@link CharacterRenderInfo} objects
     * that generated the {@link Rectangle}.
     *
     * @param cris list of {@link CharacterRenderInfo} objects
     *
     * @return an array containing the elements of this list
     */
    protected List<Rectangle> toRectangles(List<CharacterRenderInfo> cris) {
        List<Rectangle> retval = new ArrayList<>();
        if (cris.isEmpty()) {
            return retval;
        }
        int prev = 0;
        int curr = 0;
        while (curr < cris.size()) {
            while (curr < cris.size() && cris.get(curr).sameLine(cris.get(prev))) {
                curr++;
            }
            Rectangle resultRectangle = null;
            for (CharacterRenderInfo cri : cris.subList(prev, curr)) {
                // in case letters are rotated (imagine text being written with an angle of 90 degrees)
                resultRectangle = Rectangle.getCommonRectangle(resultRectangle, cri.getBoundingBox());
            }
            retval.add(resultRectangle);
            prev = curr;
        }

        // return
        return retval;
    }

    private void removeDuplicates(List<IPdfTextLocation> sortedList) {
        IPdfTextLocation lastItem = null;
        int orgSize = sortedList.size();
        for (int i = orgSize - 1; i >= 0; i--) {
            IPdfTextLocation currItem = sortedList.get(i);
            Rectangle currRect = currItem.getRectangle();
            if (lastItem != null && currRect.equalsWithEpsilon(lastItem.getRectangle())) {
                sortedList.remove(currItem);
            }
            lastItem = currItem;
        }
    }
    
    private static Integer getStartIndex(Map<Integer, Integer> indexMap, int index,
            String txt) {
        while (!indexMap.containsKey(index) && index < txt.length()) {
            index++;
        }
        return indexMap.get(index);
    }

    private static Integer getEndIndex(Map<Integer, Integer> indexMap, int index) {
        while (!indexMap.containsKey(index) && index >= 0) {
            index--;
        }
        return indexMap.get(index);
    }

    private static final class PdfTextLocationComparator
            implements Comparator<com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation> {
        @Override
        public int compare(com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation l1,
                           com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation l2) {
            Rectangle o1 = l1.getRectangle();
            Rectangle o2 = l2.getRectangle();
            if (Math.abs(o1.getY() - o2.getY()) < EPS) {
                return Math.abs(o1.getX() - o2.getX()) < EPS ? 0 : ((o2.getX() - o1.getX()) > EPS ? -1 : 1);
            } else {
                return (o2.getY() - o1.getY()) > EPS ? -1 : 1;
            }
        }
    }
}
