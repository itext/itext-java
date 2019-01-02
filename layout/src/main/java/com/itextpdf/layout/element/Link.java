/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.property.Property;
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
