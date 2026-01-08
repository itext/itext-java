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
package com.itextpdf.kernel.contrast;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Path;

/**
 * Represents rendering information for a text element in contrast analysis.
 */
public class TextColorInfo extends ColorInfo {
    private final String text;
    private final String parent;
    private final float fontSize;

    /**
     * Constructs a new {@link TextColorInfo} with the specified text properties.
     *
     * @param text     the text content (typically a single character) being rendered
     * @param parent   the parent text string that this text is part of, providing context
     * @param color    the fill color of the text
     * @param path     the geometric path defining the shape and position of the text element
     * @param fontSize the font size in points.
     */
    public TextColorInfo(String text, String parent, Color color, Path path, float fontSize) {
        super(color, path);
        this.text = text;
        this.parent = parent;
        this.fontSize = fontSize;
    }

    /**
     * Gets the text content being rendered.
     * <p>
     * This typically represents a single character or glyph from the PDF content stream.
     *
     * @return the text content as a String
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the parent text string that this text is part of.
     * <p>
     * This provides context about the larger text block or string that contains this
     * individual text element, which can be useful for debugging and analysis.
     *
     * @return the parent text string
     */
    public String getParent() {
        return parent;
    }

    /**
     * Gets the font size of the text in points.
     *
     * @return the font size in points
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * Returns a string representation of this text render information.
     *
     * @return a string containing the text, parent, color, and path information
     */
    @Override
    public String toString() {
        return "TextRenderInfo{" +
                "character=" + text +
                ", parent=" + parent +
                ", color=" + getColor() +
                ", path=" + getPath() +
                '}';
    }
}
