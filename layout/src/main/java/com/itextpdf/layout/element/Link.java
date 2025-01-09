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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LinkRenderer;

/**
 * A clickable piece of {@link Text} which contains a {@link PdfLinkAnnotation
 * link annotation dictionary}. The concept is largely similar to that of the
 * HTML anchor tag.
 */
public class Link extends Text {

    /**
     * Creates a Link with a fully constructed link annotation dictionary.
     *
     * @param text the textual contents of the link
     * @param linkAnnotation a {@link PdfLinkAnnotation}
     */
    public Link(String text, PdfLinkAnnotation linkAnnotation) {
        super(text);
        setProperty(Property.LINK_ANNOTATION, linkAnnotation);
    }

    /**
     * Creates a Link which can execute an action.
     *
     * @param text the textual contents of the link
     * @param action a {@link PdfAction}
     */
    public Link(String text, PdfAction action) {
        this(text, (PdfLinkAnnotation) new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0)).setAction(action).setFlags(PdfAnnotation.PRINT));
    }

    /**
     * Creates a Link to another location in the document.
     *
     * @param text the textual contents of the link
     * @param destination a {@link PdfDestination}
     */
    public Link(String text, PdfDestination destination) {
        this(text, (PdfLinkAnnotation) new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0)).setDestination(destination).setFlags(PdfAnnotation.PRINT));
    }

    /**
     * Gets the link annotation dictionary associated with this link.
     * @return a {@link PdfLinkAnnotation}
     */
    public PdfLinkAnnotation getLinkAnnotation() {
        return this.<PdfLinkAnnotation>getProperty(Property.LINK_ANNOTATION);
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.LINK);
        }
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new LinkRenderer(this, text);
    }
}
