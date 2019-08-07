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

import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineSeparatorRenderer;

/**
 * This is a line separator element which is basically just a horizontal line with
 * a style specified by {@link ILineDrawer} custom drawing interface instance.
 * This might be thought of as an HTML's &lt;hr&gt; element alternative.
 */
public class LineSeparator extends BlockElement<LineSeparator> {

    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Creates a custom line separator with line style defined by custom {@link ILineDrawer} interface instance
     *
     * @param lineDrawer line drawer instance
     */
    public LineSeparator(ILineDrawer lineDrawer) {
        setProperty(Property.LINE_DRAWER, lineDrawer);
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.ARTIFACT);
        }
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new LineSeparatorRenderer(this);
    }
}
