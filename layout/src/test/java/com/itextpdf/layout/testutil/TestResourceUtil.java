/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.testutil;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;

public final class TestResourceUtil {

    private TestResourceUtil() {
    }

    /**
     * Returns a Byron stanza string.
     */
    public static String getByronStanza() {
        return "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n" +
                        "\n" +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted.";
    }

    /**
     * Returns a narrow Byron stanza string.
     */
    public static String getByronStanzaNarrow() {
        return "When a man hath no freedom to fight for at home, " +
                "Let him combat for that of his neighbours; " +
                "Let him think of the glories of Greece and of Rome, " +
                "And get knocked on the head for his labours. " +
                "\n" +
                "To do good to Mankind is the chivalrous plan, " +
                "And is always as nobly requited; " +
                "Then battle for Freedom wherever you can, " +
                "And, if not shot or hanged, you'll get knighted.";
    }

    /**
     * Returns a tall div element.
     *
     * @param paragraphCount count of paragraphs
     * @return resulting div element
     */
    public static Div getTallDiv(int paragraphCount) {
        Div div = new Div().setBackgroundColor(new DeviceRgb(78, 151, 205));
        for (int i = 0; i < paragraphCount; i++) {
            div.add(new Paragraph("BLOCK " + i + "\n" + TestResourceUtil.getByronStanza()));
        }
        return div;
    }

    /**
     * Repeats a string N times and returns result
     *
     * @param s string
     * @param n number of repeats
     * @return resulting string
     */
    public static String repeatString(String s, int n) {
        StringBuilder sb = new StringBuilder(s.length() * n);
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Calculates the available content rectangle after subtracting the document margins
     * and the given offsets on each side.
     *
     * @param docHeight the full page height
     * @param docWidth the full page width
     * @param docMargin the margin for the doc
     * @param top the offset to subtract from the top
     * @param bottom the offset to subtract from the bottom
     * @param left the offset to subtract from the left
     * @param right the offset to subtract from the right
     * @return the remaining Rectangle available for content layout
     */
    public static Rectangle getAvailableRect(float docHeight, float docWidth, float docMargin, float top, float bottom,
            float left, float right) {
        float x = docMargin + left;
        float y = docMargin + bottom;
        float w = docWidth  - 2 * docMargin - left  - right;
        float h = docHeight - 2 * docMargin - top   - bottom;
        return new Rectangle(x, y, Math.max(w, 1f), Math.max(h, 1f));
    }
}
