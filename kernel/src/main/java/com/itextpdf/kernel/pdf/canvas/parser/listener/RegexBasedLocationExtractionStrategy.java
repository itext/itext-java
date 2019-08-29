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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is designed to search for the occurrences of a regular expression and return the resultant rectangles.
 */
public class RegexBasedLocationExtractionStrategy implements ILocationExtractionStrategy {

    private Pattern pattern;
    private List<CharacterRenderInfo> parseResult = new ArrayList<>();

    public RegexBasedLocationExtractionStrategy(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public RegexBasedLocationExtractionStrategy(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Collection<IPdfTextLocation> getResultantLocations() {
        // align characters in "logical" order
        Collections.sort(parseResult, new TextChunkLocationBasedComparator(new DefaultTextChunkLocationComparator()));

        // process parse results
        List<IPdfTextLocation> retval = new ArrayList<>();

        CharacterRenderInfo.StringConversionInfo txt = CharacterRenderInfo.mapString(parseResult);

        Matcher mat = pattern.matcher(txt.text);
        while (mat.find()) {
            int startIndex = txt.indexMap.get(mat.start());
            int endIndex = txt.indexMap.get(mat.end() - 1);
            for (Rectangle r : toRectangles(parseResult.subList(startIndex, endIndex + 1))) {
                retval.add(new DefaultPdfTextLocation(0, r, mat.group(0)));
            }
        }

        /* sort
         * even though the return type is Collection<Rectangle>, we apply a sorting algorithm here
         * This is to ensure that tests that use this functionality (for instance to generate pdf with
         * areas of interest highlighted) will not break when compared.
         */
        java.util.Collections.sort(retval, new Comparator<IPdfTextLocation>() {
            @Override
            public int compare(IPdfTextLocation l1, IPdfTextLocation l2) {
                Rectangle o1 = l1.getRectangle();
                Rectangle o2 = l2.getRectangle();
                if (o1.getY() == o2.getY()) {
                    return o1.getX() == o2.getX() ? 0 : (o1.getX() < o2.getX() ? -1 : 1);
                } else {
                    return o1.getY() < o2.getY() ? -1 : 1;
                }
            }
        });

        // ligatures can produces same rectangle
        removeDuplicates(retval);

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

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof TextRenderInfo) {
            parseResult.addAll(toCRI((TextRenderInfo) data));
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }

    /**
     * Convert {@link TextRenderInfo} to {@link CharacterRenderInfo}
     * This method is public and not final so that custom implementations can choose to override it.
     * Other implementations of {@code CharacterRenderInfo} may choose to store different properties than
     * merely the {@link Rectangle} describing the bounding box. E.g. a custom implementation might choose to
     * store {@link Color} information as well, to better match the content surrounding the redaction {@link Rectangle}.
     *
     * @param tri
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
     * @param cris
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

}
