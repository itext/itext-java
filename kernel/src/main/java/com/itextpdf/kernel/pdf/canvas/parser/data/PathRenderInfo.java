/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
package com.itextpdf.kernel.pdf.canvas.parser.data;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.FillingRule;

/**
 * Contains information relating to painting current path.
 */
public class PathRenderInfo implements IEventData {

    /**
     * End the path object without filling or stroking it. This operator shall be a path-painting no-op,
     * used primarily for the side effect of changing the current clipping path
     */
    public static final int NO_OP = 0;

    /**
     * Value specifying stroke operation to perform on the current path.
     */
    public static final int STROKE = 1;

    /**
     * Value specifying fill operation to perform on the current path. When the fill operation
     * is performed it should use either nonzero winding or even-odd rule.
     */
    public static final int FILL = 2;

    private Path path;
    private int operation;
    private int rule;
    private boolean isClip;
    private int clippingRule;
    private CanvasGraphicsState gs;
    private boolean graphicsStateIsPreserved;

    /**
     * @param path      The path to be rendered.
     * @param operation One of the possible combinations of {@link #STROKE} and {@link #FILL} values or {@link #NO_OP}
     * @param rule      Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}.
     * @param isClip    True indicates that current path modifies the clipping path, false - if not.
     * @param clipRule  Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}.
     * @param gs        The graphics state.
     */
    public PathRenderInfo(Path path, int operation, int rule, boolean isClip, int clipRule, CanvasGraphicsState gs) {
        this.path = path;
        this.operation = operation;
        this.rule = rule;
        this.gs = gs;
        this.isClip = isClip;
        this.clippingRule = clipRule;
    }

    /**
     * If the operation is {@link #NO_OP} then the rule is ignored,
     * otherwise {@link FillingRule#NONZERO_WINDING} is used by default.
     * With this constructor path is considered as not modifying clipping path.
     * <p>
     * See {@link #PathRenderInfo(Path, int, int, boolean, int, CanvasGraphicsState)}
     */
    public PathRenderInfo(Path path, int operation, CanvasGraphicsState gs) {
        this(path, operation, FillingRule.NONZERO_WINDING, false, FillingRule.NONZERO_WINDING, gs);
    }

    /**
     * @return The {@link Path} to be rendered.
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return <CODE>int</CODE> value which is either {@link #NO_OP} or one of possible
     * combinations of {@link #STROKE} and {@link #FILL}
     */
    public int getOperation() {
        return operation;
    }

    /**
     * @return Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}.
     */
    public int getRule() {
        return rule;
    }

    /**
     * @return true indicates that current path modifies the clipping path, false - if not.
     */
    public boolean isPathModifiesClippingPath() {
        return isClip;
    }

    /**
     * @return Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}.
     */
    public int getClippingRule() {
        return clippingRule;
    }

    /**
     * @return Current transformation matrix.
     */
    public Matrix getCtm() {
        return gs.getCtm();
    }

    public float getLineWidth() {
        return gs.getLineWidth();
    }

    public int getLineCapStyle() {
        return gs.getLineCapStyle();
    }

    public int getLineJoinStyle() {
        return gs.getLineJoinStyle();
    }

    public float getMiterLimit() {
        return gs.getMiterLimit();
    }

    public PdfArray getLineDashPattern() {
        return gs.getDashPattern();
    }

    public Color getStrokeColor() {
        return gs.getStrokeColor();
    }

    public Color getFillColor() {
        return gs.getFillColor();
    }

    public boolean isGraphicsStatePreserved() {
        return graphicsStateIsPreserved;
    }

    public void preserveGraphicsState() {
        this.graphicsStateIsPreserved = true;
        gs = new CanvasGraphicsState(gs);
    }

    public void releaseGraphicsState() {
        if (!graphicsStateIsPreserved) {
            gs = null;
        }
    }
}
