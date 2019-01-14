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
package com.itextpdf.layout.layout;

import com.itextpdf.io.util.HashCode;
import com.itextpdf.kernel.geom.Rectangle;

import com.itextpdf.io.util.MessageFormatUtil;

/**
 * Represents the area for content {@link com.itextpdf.layout.renderer.IRenderer#layout(LayoutContext) layouting}.
 */
public class LayoutArea {

    /**
     * The number of page on which the area is located.
     */
    protected int pageNumber;
    /**
     * The area's bounding box
     */
    protected Rectangle bBox;

    /**
     * Creates the area for content {@link com.itextpdf.layout.renderer.IRenderer#layout(LayoutContext) layouting}.
     *
     * @param pageNumber the number of page on which the area is located.
     * @param bBox the area's bounding box
     */
    public LayoutArea(int pageNumber, Rectangle bBox) {
        this.pageNumber = pageNumber;
        this.bBox = bBox;
    }

    /**
     * Gets the number of page on which the area is located.
     *
     * @return page number
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Gets the {@link Rectangle box} which bounds the area.
     *
     * @return the bounding box
     */
    public Rectangle getBBox() {
        return bBox;
    }

    /**
     * Sets the {@link Rectangle box} which bounds the area.
     */
    public void setBBox(Rectangle bbox) {
        this.bBox = bbox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutArea clone() {
        LayoutArea area = new LayoutArea(pageNumber, bBox.clone());
        return area;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        LayoutArea that = (LayoutArea) obj;
        return pageNumber == that.pageNumber && bBox.equalsWithEpsilon(that.bBox);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCode hashCode = new HashCode();
        hashCode.append(pageNumber).
                append(bBox.hashCode());
        return hashCode.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return MessageFormatUtil.format("{0}, page {1}", bBox.toString(), pageNumber);
    }
}
