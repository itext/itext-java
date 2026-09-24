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

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.ParagraphOrphansControl;
import com.itextpdf.layout.properties.ParagraphWidowsControl;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.Map;
import java.util.TreeMap;

/**
 * An abstract class that represents a paragraph of text in a document. It provides methods for adding text
 * and other elements to the paragraph, as well as managing properties such as leading, indentation, and margins.
 *
 * @param <T> the type of the concrete subclass extending this abstract class
 */
public abstract class AbstractParagraph<T extends AbstractParagraph<T>> extends BlockElement<T> {
    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Creates a new instance of {@link AbstractParagraph}. This constructor is protected to allow subclassing.
     */
    protected AbstractParagraph() {
        super();
    }

    /**
     * Adds a piece of text to this {@link AbstractParagraph}.
     *
     * @param text the content to be added, as a {@link String}
     *
     * @return this Element
     */
    public T add(String text) {
        return add(new Text(text));
    }

    /**
     * Adds a {@link ILeafElement} element to this {@link AbstractParagraph}.
     *
     * @param element the content to be added, any {@link ILeafElement}
     *
     * @return this Element
     */
    public T add(ILeafElement element) {
        childElements.add(element);
        return (T) this;
    }

    /**
     * Adds a {@link Paragraph} element.
     *
     * @param element the content to be added, any {@link Paragraph}
     *
     * @return this Element
     */
    public T add(Paragraph element) {
        childElements.add(element);
        return (T) this;
    }

    /**
     * Adds a {@link VerticalParagraph} element.
     *
     * @param element the content to be added, any {@link VerticalParagraph}
     *
     * @return this Element
     */
    public T add(VerticalParagraph element) {
        childElements.add(element);
        return (T) this;
    }

    /**
     * Adds a {@link java.util.List} of layout elements to this {@link AbstractParagraph}.
     *
     * @param elements the content to be added
     * @param <T2> any {@link ILeafElement}
     *
     * @return this Element
     */
    public <T2 extends ILeafElement> T addAll(java.util.List<T2> elements) {
        for (ILeafElement element : elements) {
            add(element);
        }
        return (T) this;
    }

    /**
     * Sets the indent value for the first line of the {@link AbstractParagraph}.
     *
     * @param indent the indent value that must be applied to the first line of
     * the {@link AbstractParagraph}, as a <code>float</code>
     *
     * @return this Element
     */
    public T setFirstLineIndent(float indent) {
        setProperty(Property.FIRST_LINE_INDENT, indent);
        return (T) this;
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.LEADING:
                return (T1) (Object) new Leading(Leading.MULTIPLIED,
                        childElements.size() == 1 && childElements.get(0) instanceof Image ? 1 : 1.35f);
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
     * Sets orphans restriction on a {@link AbstractParagraph}.
     *
     * @param orphansControl an instance of {@link ParagraphOrphansControl}
     *
     * @return this Element
     */
    public T setOrphansControl(ParagraphOrphansControl orphansControl) {
        setProperty(Property.ORPHANS_CONTROL, orphansControl);
        return (T) this;
    }

    /**
     * Sets widows restriction on a {@link AbstractParagraph}.
     *
     * @param widowsControl an instance of {@link ParagraphWidowsControl}
     *
     * @return this Element
     */
    public T setWidowsControl(ParagraphWidowsControl widowsControl) {
        setProperty(Property.WIDOWS_CONTROL, widowsControl);
        return (T) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.P);
        }
        return tagProperties;
    }

    /**
     * Adds a list of {@link TabStop} objects to the paragraph's properties.
     *
     * @param newTabStops the list of {@link TabStop} objects to be added
     */
    protected void addTabStopsAsProperty(java.util.List<TabStop> newTabStops) {
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
