/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.pdf.canvas.parser;


import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.geom.ShapeTransformUtil;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.FillingRule;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.ClipperBridge;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.DefaultClipper;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.PolyTree;

/**
 * Internal class which is essentially a {@link CanvasGraphicsState} which supports tracking of
 * clipping path state and changes.
 */
public class ParserGraphicsState extends CanvasGraphicsState {
    // NOTE: From the spec default value of this field should be the boundary of the entire imageable portion of the output page.
    private Path clippingPath;

    /**
     * Internal empty and default constructor.
     */
    ParserGraphicsState() {

    }

    /**
     * Copy constructor.
     *
     * @param source the Graphics State to copy from
     */
    ParserGraphicsState(ParserGraphicsState source) {
        super(source);

        if (source.clippingPath != null) {
            clippingPath = new Path(source.clippingPath);
        }
    }

    @Override
    public void updateCtm(Matrix newCtm) {
        super.updateCtm(newCtm);
        if (clippingPath != null) {
            transformClippingPath(newCtm);
        }
    }

    /**
     * Intersects the current clipping path with the given path.
     *
     * <p>
     * <strong>Note:</strong> Coordinates of the given path should be in
     * the transformed user space.
     *
     * @param path        The path to be intersected with the current clipping path.
     * @param fillingRule The filling rule which should be applied to the given path.
     *                    It should be either {@link FillingRule#EVEN_ODD} or
     *                    {@link FillingRule#NONZERO_WINDING}
     */
    public void clip(Path path, int fillingRule) {
        if (clippingPath == null || clippingPath.isEmpty()) {
            return;
        }

        Path pathCopy = new Path(path);
        pathCopy.closeAllSubpaths();

        IClipper clipper = new DefaultClipper();
        ClipperBridge clipperBridge = new ClipperBridge(clippingPath, pathCopy);
        clipperBridge.addPath(clipper, clippingPath, IClipper.PolyType.SUBJECT);
        clipperBridge.addPath(clipper, pathCopy, IClipper.PolyType.CLIP);

        PolyTree resultTree = new PolyTree();
        clipper.execute(IClipper.ClipType.INTERSECTION, resultTree, IClipper.PolyFillType.NON_ZERO, ClipperBridge.getFillType(fillingRule));

        clippingPath = clipperBridge.convertToPath(resultTree);
    }

    /**
     * Getter for the current clipping path.
     *
     * <p>
     * <strong>Note:</strong> The returned clipping path is in the transformed user space, so
     * if you want to get it in default user space, apply transformation matrix ({@link CanvasGraphicsState#getCtm()}).
     *
     * @return The current clipping path.
     */
    public Path getClippingPath() {
        return clippingPath;
    }

    /**
     * Sets the current clipping path to the specified path.
     *
     * <p>
     * <strong>Note:</strong>This method doesn't modify existing clipping path,
     * it simply replaces it with the new one instead.
     *
     * @param clippingPath New clipping path.
     */
    public void setClippingPath(Path clippingPath) {
        Path pathCopy = new Path(clippingPath);
        pathCopy.closeAllSubpaths();
        this.clippingPath = pathCopy;
    }

    private void transformClippingPath(Matrix newCtm) {
        clippingPath = ShapeTransformUtil.transformPath(clippingPath, newCtm);
    }
}
