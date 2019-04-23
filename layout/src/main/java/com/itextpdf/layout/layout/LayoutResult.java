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

import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Represents the result of content {@link IRenderer#layout(LayoutContext) layouting}.
 */
public class LayoutResult {

    /**
     * The status of {@link IRenderer#layout(LayoutContext)}
     * which indicates that the content was fully placed.
     */
    public static final int FULL = 1;
    /**
     * The status of {@link IRenderer#layout(LayoutContext)}
     * which indicates that the content was placed partially.
     */
    public static final int PARTIAL = 2;
    /**
     * The status of {@link IRenderer#layout(LayoutContext)}
     * which indicates that the content was not placed.
     */
    public static final int NOTHING = 3;

    /**
     * The status of {@link IRenderer#layout(LayoutContext)}
     * which indicates whether the content was added or not
     * and, if yes, was it added fully or partially.
     */
    protected int status;
    /**
     * The area occupied by the content during its {@link IRenderer#layout(LayoutContext) layouting}.
     * which indicates whether the content was added or not and, if yes, was it added fully or partially.
     */
    protected LayoutArea occupiedArea;
    /**
     * The split renderer created during {@link IRenderer#layout(LayoutContext) layouting}.
     * This renderer will be used to draw the splitted part of content.
     */
    protected IRenderer splitRenderer;
    /**
     * The overflow renderer created during {@link IRenderer#layout(LayoutContext) layouting}.
     * This renderer will be used to draw the overflowed part of content.
     */
    protected IRenderer overflowRenderer;
    protected AreaBreak areaBreak;

    /**
     * The first renderer to produce {@link LayoutResult#NOTHING} during {@link IRenderer#layout(LayoutContext)}.
     */
    protected IRenderer causeOfNothing;

    /**
     * Creates the {@link LayoutResult result of {@link IRenderer#layout(LayoutContext) layouting}}.
     * The {@link LayoutResult#causeOfNothing} will be set as null.
     *
     * @param status the status of {@link IRenderer#layout(LayoutContext)}
     * @param occupiedArea the area occupied by the content
     * @param splitRenderer the renderer to draw the splitted part of the content
     * @param overflowRenderer the renderer to draw the overflowed part of the content
     */
    public LayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer) {
        this(status, occupiedArea, splitRenderer, overflowRenderer, null);
    }

    /**
     * Creates the {@link LayoutResult result of {@link IRenderer#layout(LayoutContext) layouting}}.
     *
     * @param status the status of {@link IRenderer#layout(LayoutContext)}
     * @param occupiedArea the area occupied by the content
     * @param splitRenderer the renderer to draw the splitted part of the content
     * @param overflowRenderer the renderer to draw the overflowed part of the content
     * @param cause the first renderer to produce {@link LayoutResult#NOTHING}
     */

    public LayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer, IRenderer cause) {
        this.status = status;
        this.occupiedArea = occupiedArea;
        this.splitRenderer = splitRenderer;
        this.overflowRenderer = overflowRenderer;
        this.causeOfNothing = cause;
    }

    /**
     * Gets the status of {@link IRenderer#layout(LayoutContext)}.
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status of {@link IRenderer#layout(LayoutContext)}.
     *
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the {@link LayoutArea layout area} occupied by the content during {@link IRenderer#layout(LayoutContext) layouting}.
     *
     * @return the {@link LayoutArea layout area} occupied by the content
     */
    public LayoutArea getOccupiedArea() {
        return occupiedArea;
    }

    /**
     * Gets the split {@link IRenderer renderer} created during {@link IRenderer#layout(LayoutContext) layouting}.
     *
     * @return the {@link IRenderer renderer}
     */
    public IRenderer getSplitRenderer() {
        return splitRenderer;
    }

    /**
     * Sets the split {@link IRenderer renderer}.
     *
     * @param splitRenderer
     */
    public void setSplitRenderer(IRenderer splitRenderer) {
        this.splitRenderer = splitRenderer;
    }

    /**
     * Gets the overflow renderer created during {@link IRenderer#layout(LayoutContext) layouting}.
     *
     * @return the {@link IRenderer renderer}
     */
    public IRenderer getOverflowRenderer() {
        return overflowRenderer;
    }

    /**
     * Sets the overflow {@link IRenderer renderer}.
     *
     * @param overflowRenderer
     */
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

    /**
     * Gets the first renderer to produce {@link LayoutResult#NOTHING} during {@link IRenderer#layout(LayoutContext)}
     *
     * @return the {@link IRenderer renderer}
     */
    public IRenderer getCauseOfNothing() {
        return causeOfNothing;
    }

    /**
     * {@inheritDoc}
     */
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
