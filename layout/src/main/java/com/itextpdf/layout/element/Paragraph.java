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

import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.LineHeight;
import com.itextpdf.layout.properties.ParagraphOrphansControl;
import com.itextpdf.layout.properties.ParagraphWidowsControl;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.util.Arrays;
import java.util.Map;

/**
 * A layout element that represents a self-contained block of textual and
 * graphical information.
 * It is a {@link BlockElement} which essentially acts as a container for
 * {@link ILeafElement leaf elements}.
 */
public class Paragraph extends AbstractParagraph<Paragraph> {

    /**
     * Creates a new {@link Paragraph} instance.
     */
    public Paragraph() {
        super();
    }

    /**
     * Creates a Paragraph, initialized with a piece of text.
     *
     * @param text the initial textual content, as a {@link String}
     */
    public Paragraph(String text) {
        this(new Text(text));
    }

    /**
     * Creates a Paragraph, initialized with a piece of text.
     *
     * @param text the initial textual content, as a {@link Text}
     */
    public Paragraph(Text text) {
        add(text);
    }

    /**
     * Adds a piece of text to this {@link Paragraph}.
     *
     * @param text the content to be added, as a {@link String}
     *
     * @return this {@link Paragraph}
     */
    @Override
    public Paragraph add(String text) {
        return super.add(new Text(text));
    }

    /**
     * Adds a {@link ILeafElement element} to this {@link Paragraph}.
     *
     * @param element the content to be added, any {@link ILeafElement}
     *
     * @return (T)this {@link Paragraph}
     */
    @Override
    public Paragraph add(ILeafElement element) {
        return super.add(element);
    }

    /**
     * Adds an {@link IBlockElement element} to this {@link Paragraph}.
     *
     * @param element the content to be added, any {@link IBlockElement}
     *
     * @return (T)this {@link Paragraph}
     */
    public Paragraph add(IBlockElement element) {
        childElements.add(element);
        return this;
    }

    /**
     * Adds the {@link FootnoteAnchor} element to this {@link Paragraph}.
     *
     * @param footnoteAnchor the footnote anchor to be added
     *
     * @return this same {@link Paragraph} instance
     */
    public Paragraph add(FootnoteAnchor footnoteAnchor) {
        childElements.add(footnoteAnchor);
        return this;
    }

    /**
     * Adds a {@link java.util.List} of layout elements to this {@link Paragraph}.
     *
     * @param elements the content to be added
     * @param <T2> any {@link ILeafElement}
     *
     * @return this {@link Paragraph}
     */
    public <T2 extends ILeafElement> Paragraph addAll(java.util.List<T2> elements) {
        for (ILeafElement element : elements) {
            add(element);
        }
        return this;
    }

    /**
     * Adds an unspecified amount of tabstop elements as properties to this {@link Paragraph}.
     *
     * @param tabStops the {@link TabStop tabstop(s)} to be added as properties
     *
     * @return this {@link Paragraph}
     *
     * @see TabStop
     */
    public Paragraph addTabStops(TabStop... tabStops) {
        addTabStopsAsProperty(Arrays.asList(tabStops));
        return this;
    }

    /**
     * Adds a {@link java.util.List} of tabstop elements as properties to this {@link Paragraph}.
     *
     * @param tabStops the list of {@link TabStop}s to be added as properties
     *
     * @return this {@link Paragraph}
     *
     * @see TabStop
     */
    public Paragraph addTabStops(java.util.List<TabStop> tabStops) {
        addTabStopsAsProperty(tabStops);
        return this;
    }

    /**
     * Removes a tabstop position from the Paragraph, if it is present in the
     * {@link Property#TAB_STOPS} property.
     *
     * @param tabStopPosition the {@link TabStop} position to be removed.
     *
     * @return this Paragraph
     *
     * @see TabStop
     */
    public Paragraph removeTabStop(float tabStopPosition) {
        Map<Float, TabStop> tabStops = this.<Map<Float, TabStop>>getProperty(Property.TAB_STOPS);
        if (tabStops != null) {
            tabStops.remove(tabStopPosition);
        }
        return this;
    }

    /**
     * Sets the indent value for the first line of the {@link Paragraph}.
     *
     * @param indent the indent value that must be applied to the first line of
     * the Paragraph, as a <code>float</code>
     *
     * @return this Paragraph
     */
    @Override
    public Paragraph setFirstLineIndent(float indent) {
        return super.setFirstLineIndent(indent);
    }

    /**
     * Sets orphans restriction on a {@link Paragraph}.
     *
     * @param orphansControl an instance of {@link ParagraphOrphansControl}
     *
     * @return this {@link Paragraph} instance
     */
    @Override
    public Paragraph setOrphansControl(ParagraphOrphansControl orphansControl) {
        return super.setOrphansControl(orphansControl);
    }

    /**
     * Sets widows restriction on a {@link Paragraph}.
     *
     * @param widowsControl an instance of {@link ParagraphWidowsControl}
     *
     * @return this {@link Paragraph} instance
     */
    @Override
    public Paragraph setWidowsControl(ParagraphWidowsControl widowsControl) {
        return super.setWidowsControl(widowsControl);
    }

    /**
     * Sets the leading value, using the {@link Leading#FIXED} strategy.
     * <p>
     * If for the element {@link RenderingMode#HTML_MODE} is enabled, than {@link Property#LINE_HEIGHT}
     * property will be set instead of default layout {@link Property#LEADING}.
     *
     * @param leading the new leading value
     *
     * @return this Paragraph
     *
     * @see Leading
     * @see LineHeight
     */
    public Paragraph setFixedLeading(float leading) {
        if (RenderingMode.HTML_MODE.equals(this.<RenderingMode>getProperty(Property.RENDERING_MODE))) {
            setProperty(Property.LINE_HEIGHT, LineHeight.createFixedValue(leading));
        } else {
            setProperty(Property.LEADING, new Leading(Leading.FIXED, leading));
        }
        return this;
    }

    /**
     * Sets the leading value, using the {@link Leading#MULTIPLIED} strategy.
     * <p>
     * If for the element {@link RenderingMode#HTML_MODE} is enabled, than {@link Property#LINE_HEIGHT}
     * property will be set instead of default layout {@link Property#LEADING}.
     *
     * @param leading the new leading value
     *
     * @return this Paragraph
     *
     * @see Leading
     * @see LineHeight
     */
    public Paragraph setMultipliedLeading(float leading) {
        if (RenderingMode.HTML_MODE.equals(this.<RenderingMode>getProperty(Property.RENDERING_MODE))) {
            setProperty(Property.LINE_HEIGHT, LineHeight.createMultipliedValue(leading));
        } else {
            setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, leading));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new ParagraphRenderer(this);
    }
}
