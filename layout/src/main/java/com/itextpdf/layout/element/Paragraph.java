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
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.ParagraphOrphansControl;
import com.itextpdf.layout.properties.ParagraphWidowsControl;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * A layout element that represents a self-contained block of textual and
 * graphical information.
 * It is a {@link BlockElement} which essentially acts as a container for
 * {@link ILeafElement leaf elements}.
 */
public class Paragraph extends BlockElement<Paragraph> {

    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Creates a Paragraph.
     */
    public Paragraph() {
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
     * @return this {@link Paragraph}
     */
    public Paragraph add(String text) {
        return add(new Text(text));
    }

    /**
     * Adds a {@link ILeafElement element} to this {@link Paragraph}.
     *
     * @param element the content to be added, any {@link ILeafElement}
     * @return this {@link Paragraph}
     */
    public Paragraph add(ILeafElement element) {
        childElements.add(element);
        return this;
    }

    /**
     * Adds an {@link IBlockElement element} to this {@link Paragraph}.
     *
     * @param element the content to be added, any {@link IBlockElement}
     * @return this {@link Paragraph}
     */
    public Paragraph add(IBlockElement element) {
        childElements.add(element);
        return this;
    }

    /**
     * Adds a {@link java.util.List} of layout elements to this {@link Paragraph}.
     *
     * @param elements the content to be added
     * @param <T2>      any {@link ILeafElement}
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
     * @return this {@link Paragraph}
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
     * @return this {@link Paragraph}
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
     * @return this Paragraph
     * @see TabStop
     */
    public Paragraph removeTabStop(float tabStopPosition) {
        Map<Float, TabStop> tabStops = this.<Map<Float, TabStop>>getProperty(Property.TAB_STOPS);
        if (tabStops != null) {
            tabStops.remove(tabStopPosition);
        }
        return this;
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.LEADING:
                return (T1) (Object) new Leading(Leading.MULTIPLIED, childElements.size() == 1 && childElements.get(0) instanceof Image ? 1 : 1.35f);
            case Property.FIRST_LINE_INDENT:
                return (T1) (Object) 0f;
            case Property.MARGIN_TOP:
            case Property.MARGIN_BOTTOM:
                return (T1) (Object) UnitValue.createPointValue(4f);
            case Property.TAB_DEFAULT:
                return (T1) (Object) 50f;
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    /**
     * Sets the indent value for the first line of the {@link Paragraph}.
     *
     * @param indent the indent value that must be applied to the first line of
     *               the Paragraph, as a <code>float</code>
     * @return this Paragraph
     */
    public Paragraph setFirstLineIndent(float indent) {
        setProperty(Property.FIRST_LINE_INDENT, indent);
        return this;
    }

    /**
     * Sets orphans restriction on a {@link Paragraph}.
     *
     * @param orphansControl an instance of {@link ParagraphOrphansControl}.
     * @return this {@link Paragraph} instance.
     */
    public Paragraph setOrphansControl(ParagraphOrphansControl orphansControl) {
        setProperty(Property.ORPHANS_CONTROL, orphansControl);
        return this;
    }

    /**
     * Sets widows restriction on a {@link Paragraph}.
     *
     * @param widowsControl an instance of {@link ParagraphWidowsControl}.
     * @return this {@link Paragraph} instance.
     */
    public Paragraph setWidowsControl(ParagraphWidowsControl widowsControl) {
        setProperty(Property.WIDOWS_CONTROL, widowsControl);
        return this;
    }

    /**
     * Sets the leading value, using the {@link Leading#FIXED} strategy.
     *
     * @param leading the new leading value
     * @return this Paragraph
     * @see Leading
     */
    public Paragraph setFixedLeading(float leading) {
        setProperty(Property.LEADING, new Leading(Leading.FIXED, leading));
        return this;
    }

    /**
     * Sets the leading value, using the {@link Leading#MULTIPLIED} strategy.
     *
     * @param leading the new leading value
     * @return this Paragraph
     * @see Leading
     */
    public Paragraph setMultipliedLeading(float leading) {
        setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, leading));
        return this;
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.P);
        }
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new ParagraphRenderer(this);
    }

    private void addTabStopsAsProperty(java.util.List<TabStop> newTabStops) {
        Map<Float, TabStop> tabStops = this.<Map<Float, TabStop>>getProperty(Property.TAB_STOPS);
        if (tabStops == null) {
            tabStops = new TreeMap<>();
            setProperty(Property.TAB_STOPS, tabStops);
        }
        for (TabStop tabStop : newTabStops) {
            tabStops.put(tabStop.getTabPosition(), tabStop);
        }
    }
}
