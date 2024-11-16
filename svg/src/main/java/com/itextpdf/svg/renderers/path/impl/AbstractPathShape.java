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
package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.svg.utils.SvgCoordinateUtils;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.util.Map;

/**
 * This class handles common behaviour in IPathShape implementations
 */
public abstract class AbstractPathShape implements IPathShape {

    private PathSvgNodeRenderer parent;
    /**
     * The properties of this shape.
     */
    protected Map<String, String> properties;
    /**
     * Whether this is a relative operator or not.
     */
    protected boolean relative;
    protected final IOperatorConverter copier;
    // Original coordinates from path instruction, according to the (x1 y1 x2 y2 x y)+ spec
    protected String[] coordinates;
    protected SvgDrawContext context;

    public AbstractPathShape() {
        this(false);
    }

    public AbstractPathShape(boolean relative) {
        this(relative, new DefaultOperatorConverter());
    }

    public AbstractPathShape(boolean relative, IOperatorConverter copier) {
        this.relative = relative;
        this.copier = copier;
    }

    @Override
    public boolean isRelative() {
        return this.relative;
    }

    protected Point createPoint(String coordX, String coordY) {
        return new Point((double) CssDimensionParsingUtils.parseDouble(coordX), (double) CssDimensionParsingUtils.parseDouble(coordY));
    }

    @Override
    public Point getEndingPoint() {
        return createPoint(coordinates[coordinates.length - 2], coordinates[coordinates.length - 1]);
    }

    /**
     * Get bounding rectangle of the current path shape.
     *
     * @param lastPoint start point for this shape
     * @return calculated rectangle
     */
    @Override
    public Rectangle getPathShapeRectangle(Point lastPoint) {
        return new Rectangle((float) CssUtils.convertPxToPts(getEndingPoint().getX()),
                (float) CssUtils.convertPxToPts(getEndingPoint().getY()), 0,
                0);
    }

    @Override
    public void draw(PdfCanvas canvas) {
        draw();
    }

    /**
     * Draws this instruction to a canvas object.
     */
    public abstract void draw();

    /**
     * Set parent path for this shape.
     *
     * @param parent {@link PathSvgNodeRenderer} instance
     */
    public void setParent(PathSvgNodeRenderer parent) {
        this.parent = parent;
    }

    /**
     * Set svg draw context for this shape.
     *
     * @param context {@link SvgDrawContext} instance.
     */
    public void setContext(SvgDrawContext context) {
        this.context = context;
    }

    /**
     * Parse x axis length value.
     *
     * @param length {@link String} length for parsing
     * @return absolute length in points
     */
    protected float parseHorizontalLength(String length) {
        return SvgCssUtils.parseAbsoluteLength(parent, length,
                SvgCoordinateUtils.calculatePercentBaseValueIfNeeded(context, length, true), 0.0F, context);
    }

    /**
     * Parse y axis length value.
     *
     * @param length {@link String} length for parsing
     * @return absolute length in points
     */
    protected float parseVerticalLength(String length) {
        return SvgCssUtils.parseAbsoluteLength(parent, length,
                SvgCoordinateUtils.calculatePercentBaseValueIfNeeded(context, length, false), 0.0F, context);
    }
}
