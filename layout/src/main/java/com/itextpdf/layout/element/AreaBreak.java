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

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.AreaBreakRenderer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * A layout object that terminates the current content area and creates a new
 * one. If no {@link PageSize} is given, the new content area will have the same
 * size as the current one.
 */
public class AreaBreak extends AbstractElement<AreaBreak> {

    protected PageSize pageSize;

    /**
     * Creates an AreaBreak. The new content area will have the same size as the
     * current one.
     */
    public AreaBreak() {
        this(AreaBreakType.NEXT_AREA);
    }

    /**
     * Creates an AreaBreak that terminates a specified area type.
     * @param areaBreakType an {@link AreaBreakType area break type}
     */
    public AreaBreak(AreaBreakType areaBreakType) {
        setProperty(Property.AREA_BREAK_TYPE, areaBreakType);
    }
    
    /**
     * Creates an AreaBreak. The new content area will have the specified page
     * size.
     * @param pageSize the size of the new content area
     */
    public AreaBreak(PageSize pageSize) {
        this(AreaBreakType.NEXT_PAGE);
        this.pageSize = pageSize;
    }

    /**
     * Gets the page size.
     * @return the {@link PageSize page size} of the next content area.
     */
    public PageSize getPageSize() {
        return pageSize;
    }

    /**
     * Sets the page size.
     * @param pageSize the new {@link PageSize page size} of the next content area.
     */
    public void setPageSize(PageSize pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the type of area that this AreaBreak will terminate.
     * @return the current {@link AreaBreakType area break type}
     */
    public AreaBreakType getType() {
        return this.<AreaBreakType>getProperty(Property.AREA_BREAK_TYPE);
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new AreaBreakRenderer(this);
    }
}
