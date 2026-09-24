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
package com.itextpdf.layout.element;

import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.WritingMode;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

/**
 * A {@link Paragraph} that is laid out vertically, with text flowing from top to bottom.
 */
public class VerticalParagraph extends AbstractParagraph<VerticalParagraph> {

    /**
     * Creates a new {@link VerticalParagraph} instance.
     *
     * @param rightToLeftProgression {@code true} for vertical right-to-left
     * lines writing progression (see {@link WritingMode#VERTICAL_RL}), or
     * {@code false} for vertical left-to-right lines writing progression (see {@link WritingMode#VERTICAL_LR})
     */
    public VerticalParagraph(boolean rightToLeftProgression) {
        super();
        if (rightToLeftProgression) {
            super.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_RL);
        } else {
            super.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        }
        super.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
        super.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        super.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        super.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
    }

    /**
     * Creates a {@link VerticalParagraph}, initialized with a piece of text.
     *
     * @param text the initial textual content, as a {@link String}
     * @param rightToLeftProgression {@code true} for vertical right-to-left lines writing progression,
     * {@code false} for vertical left-to-right lines writing progression
     */
    public VerticalParagraph(String text, boolean rightToLeftProgression) {
        this(new Text(text), rightToLeftProgression);
    }

    /**
     * Creates a {@link VerticalParagraph}, initialized with a piece of text.
     *
     * @param text the initial textual content, as a {@link Text}
     * @param rightToLeftProgression {@code true} for vertical right-to-left lines writing progression,
     * {@code false} for vertical left-to-right lines writing progression
     */
    public VerticalParagraph(Text text, boolean rightToLeftProgression) {
        this(rightToLeftProgression);
        super.add(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new ParagraphRenderer(this);
    }
}
