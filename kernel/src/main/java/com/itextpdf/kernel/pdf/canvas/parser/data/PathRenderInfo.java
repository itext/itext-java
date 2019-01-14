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
package com.itextpdf.kernel.pdf.canvas.parser.data;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.FillingRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Contains information relating to painting current path.
 */
public class PathRenderInfo extends AbstractRenderInfo {

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

    /**
     * Hierarchy of nested canvas tags for the text from the most inner (nearest to text) tag to the most outer.
     */
    private List<CanvasTag> canvasTagHierarchy;

    /**
     * @param gs        The graphics state.
     * @param path      The path to be rendered.
     * @param operation One of the possible combinations of {@link #STROKE} and {@link #FILL} values or {@link #NO_OP}
     * @param rule      Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}.
     * @param isClip    True indicates that current path modifies the clipping path, false - if not.
     * @param clipRule  Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}.
     */
    public PathRenderInfo(Stack<CanvasTag> canvasTagHierarchy, CanvasGraphicsState gs, Path path, int operation, int rule, boolean isClip, int clipRule) {
        super(gs);
        this.canvasTagHierarchy = Collections.<CanvasTag>unmodifiableList(new ArrayList<>(canvasTagHierarchy));
        this.path = path;
        this.operation = operation;
        this.rule = rule;
        this.isClip = isClip;
        this.clippingRule = clipRule;
    }

    /**
     * If the operation is {@link #NO_OP} then the rule is ignored,
     * otherwise {@link FillingRule#NONZERO_WINDING} is used by default.
     * With this constructor path is considered as not modifying clipping path.
     * <p>
     * See {@link #PathRenderInfo(Stack, CanvasGraphicsState, Path, int, int, boolean, int)}
     */
    public PathRenderInfo(Stack<CanvasTag> canvasTagHierarchy, CanvasGraphicsState gs, Path path, int operation) {
        this(canvasTagHierarchy, gs, path, operation, FillingRule.NONZERO_WINDING, false, FillingRule.NONZERO_WINDING);
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
        checkGraphicsState();
        return gs.getCtm();
    }

    public float getLineWidth() {
        checkGraphicsState();
        return gs.getLineWidth();
    }

    public int getLineCapStyle() {
        checkGraphicsState();
        return gs.getLineCapStyle();
    }

    public int getLineJoinStyle() {
        checkGraphicsState();
        return gs.getLineJoinStyle();
    }

    public float getMiterLimit() {
        checkGraphicsState();
        return gs.getMiterLimit();
    }

    public PdfArray getLineDashPattern() {
        checkGraphicsState();
        return gs.getDashPattern();
    }

    public Color getStrokeColor() {
        checkGraphicsState();
        return gs.getStrokeColor();
    }

    public Color getFillColor() {
        checkGraphicsState();
        return gs.getFillColor();
    }

    /**
     * Gets hierarchy of the canvas tags that wraps given text.
     *
     * @return list of the wrapping canvas tags. The first tag is the innermost (nearest to the text).
     */
    public List<CanvasTag> getCanvasTagHierarchy() {
        return canvasTagHierarchy;
    }

    /**
     * @return the marked content associated with the TextRenderInfo instance.
     */
    public int getMcid() {
        for (CanvasTag tag : canvasTagHierarchy) {
            if (tag.hasMcid()) {
                return tag.getMcid();
            }
        }
        return -1;
    }

    /**
     * Checks if the text belongs to a marked content sequence
     * with a given mcid.
     *
     * @param mcid a marked content id
     * @return true if the text is marked with this id
     */
    public boolean hasMcid(int mcid) {
        return hasMcid(mcid, false);
    }

    /**
     * Checks if the text belongs to a marked content sequence
     * with a given mcid.
     *
     * @param mcid                     a marked content id
     * @param checkTheTopmostLevelOnly indicates whether to check the topmost level of marked content stack only
     * @return true if the text is marked with this id
     */
    public boolean hasMcid(int mcid, boolean checkTheTopmostLevelOnly) {
        if (checkTheTopmostLevelOnly) {
            if (canvasTagHierarchy != null) {
                int infoMcid = getMcid();
                return infoMcid != -1 && infoMcid == mcid;
            }
        } else {
            for (CanvasTag tag : canvasTagHierarchy) {
                if (tag.hasMcid())
                    if (tag.getMcid() == mcid)
                        return true;
            }
        }
        return false;
    }
}
