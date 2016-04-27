/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.property.Leading;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * A layout element that represents a self-contained block of textual and
 * grpahical information.
 * It is a {@link BlockElement} which essentially acts as a container for
 * {@link ILeafElement leaf elements}.
 */
public class Paragraph extends BlockElement<Paragraph> {

    protected PdfName role = PdfName.P;
    protected AccessibilityProperties tagProperties;

    /**
     * Creates a Paragraph.
     */
    public Paragraph() {
    }

    /**
     * Creates a Paragraph, initialized with a piece of text.
     * @param text the initial textual content, as a {@link String}
     */
    public Paragraph(String text) {
        this(new Text(text));
    }

    /**
     * Creates a Paragraph, initialized with a piece of text.
     * @param text the initial textual content, as a {@link Text}
     */
    public Paragraph(Text text) {
        add(text);
    }

    /**
     * Adds a piece of text to the Paragraph
     * @param <T> the runtime type of this object
     * @param text the content to be added, as a {@link String}
     * @return this Paragraph
     */
    public <T extends Paragraph> T add(String text) {
        return add(new Text(text));
    }

    /**
     * Adds a layout element to the Paragraph.
     * @param <T> the runtime type of this object
     * @param element the content to be added, any {@link ILeafElement}
     * @return this Paragraph
     */
    public <T extends Paragraph> T add(ILeafElement element) {
        childElements.add(element);
        return (T) this;
    }

    /**
     * Adds a {@link java.util.List} of layout elements to the Paragraph.
     * @param <T> the runtime type of this object
     * @param elements the content to be added, any {@link ILeafElement}
     * @return this Paragraph
     */
    public <T extends Paragraph> T addAll(java.util.List<? extends ILeafElement> elements) {
        for (ILeafElement element : elements) {
            add(element);
        }
        return (T) this;
    }

    /**
     * Adds an unspecified amount of tabstop elements as properties to the Paragraph.
     * @param <T> the runtime type of this object
     * @param tabStops the {@link TabStop tabstop(s)} to be added as properties
     * @return this Paragraph
     * @see TabStop
     */
    public <T extends Paragraph> T addTabStops(TabStop ... tabStops) {
        addTabStopsAsProperty(Arrays.asList(tabStops));
        return (T) this;
    }

    /**
     * Adds a {@link java.util.List} of tabstop elements as properties to the Paragraph.
     * @param <T> the runtime type of this object
     * @param tabStops the list of {@link TabStop}s to be added as properties
     * @return this Paragraph
     * @see TabStop
     */
    public <T extends Paragraph> T addTabStops(java.util.List<TabStop> tabStops) {
        addTabStopsAsProperty(tabStops);
        return (T) this;
    }

    /**
     * Removes a tabstop position from the Paragraph, if it is present in the
     * {@link Property#TAB_STOPS} property.
     * 
     * @param <T> the runtime type of this object
     * @param tabStopPosition the {@link TabStop} position to be removed.
     * @return this Paragraph
     * @see TabStop
     */
    public <T extends Paragraph> T removeTabStop(float tabStopPosition) {
        Map<Float, TabStop> tabStops = getProperty(Property.TAB_STOPS);
        if (tabStops != null) {
            tabStops.remove(tabStopPosition);
        }
        return (T) this;
    }

    @Override
    public <T> T getDefaultProperty(int property) {
        switch (property) {
            case Property.LEADING:
                return (T) new Leading(Leading.MULTIPLIED, childElements.size() == 1 && childElements.get(0) instanceof Image ? 1 : 1.35f);
            case Property.FIRST_LINE_INDENT:
                return (T) Float.valueOf(0);
            case Property.MARGIN_TOP:
            case Property.MARGIN_BOTTOM:
                return (T) Float.valueOf(4);
            case Property.TAB_DEFAULT:
                return (T) Float.valueOf(50);
            default:
                return super.getDefaultProperty(property);
        }
    }

    /**
     * Sets the indent value for the first line of the {@link Paragraph}.
     * 
     * @param <T> the runtime type of this object
     * @param indent the indent value that must be applied to the first line of
     * the Paragraph, as a <code>float</code>
     * @return this Paragraph
     */
    public <T extends Paragraph> T setFirstLineIndent(float indent) {
        setProperty(Property.FIRST_LINE_INDENT, indent);
        return (T) this;
    }

    /**
     * Sets the leading value, using the {@link Leading#FIXED} strategy.
     * 
     * @param <T> the runtime type of this object
     * @param leading the new leading value
     * @return this Paragraph
     * @see Leading
     */
    public <T extends Paragraph> T setFixedLeading(float leading) {
        setProperty(Property.LEADING, new Leading(Leading.FIXED, leading));
        return (T) this;
    }

    /**
     * Sets the leading value, using the {@link Leading#MULTIPLIED} strategy.
     * 
     * @param <T> the runtime type of this object
     * @param leading the new leading value
     * @return this Paragraph
     * @see Leading
     */
    public <T extends Paragraph> T setMultipliedLeading(float leading) {
        setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, leading));
        return (T) this;
    }


    @Override
    protected ParagraphRenderer makeNewRenderer() {
        return new ParagraphRenderer(this);
    }

    private void addTabStopsAsProperty(java.util.List<TabStop> newTabStops) {
        Map<Float, TabStop> tabStops = getProperty(Property.TAB_STOPS);
        if (tabStops == null) {
            tabStops = new TreeMap<>();
            setProperty(Property.TAB_STOPS, tabStops);
        }
        for (TabStop tabStop : newTabStops) {
            tabStops.put(tabStop.getTabPosition(), tabStop);
        }
    }

    @Override
    public PdfName getRole() {
        return role;
    }

    @Override
    public void setRole(PdfName role) {
        this.role = role;
        if (PdfName.Artifact.equals(role)) {
            propagateArtifactRoleToChildElements();
        }
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new AccessibilityProperties();
        }
        return tagProperties;
    }
}
