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

import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleTextExtractionStrategy implements ITextExtractionStrategy {

    private Vector lastStart;
    private Vector lastEnd;

    /** used to store the resulting String. */
    private final StringBuilder result = new StringBuilder();

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo)data;
            boolean firstRender = result.length() == 0;
            boolean hardReturn = false;

            LineSegment segment = renderInfo.getBaseline();
            Vector start = segment.getStartPoint();
            Vector end = segment.getEndPoint();

            if (!firstRender){
                Vector x1 = lastStart;
                Vector x2 = lastEnd;

                // see http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
                float dist = (x2.subtract(x1)).cross((x1.subtract(start))).lengthSquared() / x2.subtract(x1).lengthSquared();

                // we should probably base this on the current font metrics, but 1 pt seems to be sufficient for the time being
                float sameLineThreshold = 1f;
                if (dist > sameLineThreshold)
                    hardReturn = true;

                // Note:  Technically, we should check both the start and end positions, in case the angle of the text changed without any displacement
                // but this sort of thing probably doesn't happen much in reality, so we'll leave it alone for now
            }

            if (hardReturn){
                //System.out.println("<< Hard Return >>");
                appendTextChunk("\n");
            } else if (!firstRender){
                // we only insert a blank space if the trailing character of the previous string wasn't a space, and the leading character of the current string isn't a space
                if (result.charAt(result.length()-1) != ' ' && renderInfo.getText().length() > 0 && renderInfo.getText().charAt(0) != ' '){
                    float spacing = lastEnd.subtract(start).length();
                    if (spacing > renderInfo.getSingleSpaceWidth()/2f){
                        appendTextChunk(" ");
                        //System.out.println("Inserting implied space before '" + renderInfo.getText() + "'");
                    }
                }
            } else {
                //System.out.println("Displaying first string of content '" + text + "' :: x1 = " + x1);
            }

            //System.out.println("[" + renderInfo.getStartPoint() + "]->[" + renderInfo.getEndPoint() + "] " + renderInfo.getText());
            appendTextChunk(renderInfo.getText());

            lastStart = start;
            lastEnd = end;
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT)));
    }

    /**
     * Returns the result so far.
     * @return	a String with the resulting text.
     */
    @Override
    public String getResultantText() {
        return result.toString();
    }

    /**
     * Used to actually append text to the text results.  Subclasses can use this to insert
     * text that wouldn't normally be included in text parsing (e.g. result of OCR performed against
     * image content)
     * @param text the text to append to the text results accumulated so far
     */
    protected final void appendTextChunk(CharSequence text){
        result.append(text);
    }

}
