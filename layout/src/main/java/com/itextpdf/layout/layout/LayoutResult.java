/*

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
package com.itextpdf.layout.layout;

import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.renderer.IRenderer;

public class LayoutResult {

    public static final int FULL = 1;
    public static final int PARTIAL = 2;
    public static final int NOTHING = 3;

    protected int status;
    protected LayoutArea occupiedArea;
    protected IRenderer splitRenderer;
    protected IRenderer overflowRenderer;
    protected AreaBreak areaBreak;

    protected IRenderer causeOfNothing;

    public LayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer) {
        this.status = status;
        this.occupiedArea = occupiedArea;
        this.splitRenderer = splitRenderer;
        this.overflowRenderer = overflowRenderer;
    }

    public LayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer, IRenderer cause) {
        this(status, occupiedArea, splitRenderer, overflowRenderer);
        if (LayoutResult.NOTHING == status) {
            causeOfNothing = cause;
        }
    }

    public int getStatus() {
        return status;
    }

    public LayoutArea getOccupiedArea() {
        return occupiedArea;
    }

    public IRenderer getSplitRenderer() {
        return splitRenderer;
    }

    public void setSplitRenderer(IRenderer splitRenderer) {
        this.splitRenderer = splitRenderer;
    }

    public IRenderer getOverflowRenderer() {
        return overflowRenderer;
    }

    public void setOverflowRenderer(IRenderer overflowRenderer) {
        this.overflowRenderer = overflowRenderer;
    }

    public AreaBreak getAreaBreak() {
        return areaBreak;
    }

    public LayoutResult setAreaBreak(AreaBreak areaBreak) {
        this.areaBreak = areaBreak;
        return this;
    }

    public IRenderer getCauseOfNothing() {
        return causeOfNothing;
    }

    @Override
    public String toString() {
        String status;
        switch (getStatus()) {
            case FULL:
                status = "Full";
                break;
            case NOTHING:
                status = "Nothing";
                break;
            case PARTIAL:
                status = "Partial";
                break;
            default:
                status = "None";
                break;
        }
        return "LayoutResult{" +
                status +
                ", areaBreak=" + areaBreak +
                ", occupiedArea=" + occupiedArea +
                '}';
    }
}
