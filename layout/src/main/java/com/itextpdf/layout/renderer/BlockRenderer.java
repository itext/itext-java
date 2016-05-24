/*
    $Id$

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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.VerticalAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class BlockRenderer extends AbstractRenderer {

    protected BlockRenderer(IElement modelElement) {
        super(modelElement);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        int pageNumber = layoutContext.getArea().getPageNumber();

        Rectangle parentBBox = layoutContext.getArea().getBBox().clone();
        if (this.<Float>getProperty(Property.ROTATION_ANGLE) != null) {
            parentBBox.moveDown(AbstractRenderer.INF - parentBBox.getHeight()).setHeight(AbstractRenderer.INF);
        }

        Float blockHeight = retrieveHeight();
        if (!isFixedLayout() && blockHeight != null && blockHeight > parentBBox.getHeight()) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, this);
        }

        float[] margins = getMargins();
        applyMargins(parentBBox, margins, false);
        Border[] borders = getBorders();
        applyBorderBox(parentBBox, borders, false);

        boolean isPositioned = isPositioned();
        if (isPositioned) {
            float x = (float) getPropertyAsFloat(Property.X);
            float relativeX = isFixedLayout() ? 0 : parentBBox.getX();
            parentBBox.setX(relativeX + x);
        }

        Float blockWidth = retrieveWidth(parentBBox.getWidth());
        if (blockWidth != null && (blockWidth < parentBBox.getWidth() || isPositioned)) {
            parentBBox.setWidth((float) blockWidth);
        }
        float[] paddings = getPaddings();
        applyPaddings(parentBBox, paddings, false);

        List<Rectangle> areas;
        if (isPositioned) {
            areas = Collections.singletonList(parentBBox);
        } else {
            areas = initElementAreas(new LayoutArea(pageNumber, parentBBox));
        }

        occupiedArea = new LayoutArea(pageNumber, new Rectangle(parentBBox.getX(), parentBBox.getY() + parentBBox.getHeight(), parentBBox.getWidth(), 0));
        int currentAreaPos = 0;

        Rectangle layoutBox = areas.get(0).clone();

        boolean anythingPlaced = false;

        for (int childPos = 0; childPos < childRenderers.size(); childPos++) {
            IRenderer childRenderer = childRenderers.get(childPos);
            LayoutResult result;
            childRenderer.setParent(this);
            while ((result = childRenderer.setParent(this).layout(new LayoutContext(new LayoutArea(pageNumber, layoutBox)))).getStatus() != LayoutResult.FULL) {
                if (result.getOccupiedArea() != null) {
                    occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
                    layoutBox.setHeight(layoutBox.getHeight() - result.getOccupiedArea().getBBox().getHeight());
                }

                if (childRenderer.<Object>getProperty(Property.WIDTH) != null) {
                    alignChildHorizontally(childRenderer, layoutBox.getWidth());
                }

                // have more areas
                if (currentAreaPos + 1 < areas.size()) {
                    if (result.getStatus() == LayoutResult.PARTIAL) {
                        childRenderers.set(childPos, result.getSplitRenderer());
                        // TODO linkedList would make it faster
                        childRenderers.add(childPos + 1, result.getOverflowRenderer());
                    } else {
                        childRenderers.set(childPos, result.getOverflowRenderer());
                        childPos--;
                    }
                    layoutBox = areas.get(++currentAreaPos).clone();
                    break;
                } else {
                    if (result.getStatus() == LayoutResult.PARTIAL) {

                        layoutBox.setHeight(layoutBox.getHeight() - result.getOccupiedArea().getBBox().getHeight());

                        if (currentAreaPos + 1 == areas.size()) {
                            AbstractRenderer splitRenderer = createSplitRenderer(LayoutResult.PARTIAL);
                            splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                            splitRenderer.childRenderers.add(result.getSplitRenderer());
                            splitRenderer.occupiedArea = occupiedArea;

                            AbstractRenderer overflowRenderer = createOverflowRenderer(LayoutResult.PARTIAL);
                            List<IRenderer> overflowRendererChildren = new ArrayList<>();
                            overflowRendererChildren.add(result.getOverflowRenderer());
                            overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                            overflowRenderer.childRenderers = overflowRendererChildren;

                            applyPaddings(occupiedArea.getBBox(), paddings, true);
                            applyBorderBox(occupiedArea.getBBox(), borders, true);
                            applyMargins(occupiedArea.getBBox(), margins, true);
                            return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, splitRenderer, overflowRenderer);
                        } else {
                            childRenderers.set(childPos, result.getSplitRenderer());
                            childRenderers.add(childPos + 1, result.getOverflowRenderer());
                            layoutBox = areas.get(++currentAreaPos).clone();
                            break;
                        }
                    } else if (result.getStatus() == LayoutResult.NOTHING) {
                        boolean keepTogether = isKeepTogether();
                        int layoutResult = anythingPlaced && !keepTogether ? LayoutResult.PARTIAL : LayoutResult.NOTHING;

                        AbstractRenderer splitRenderer = createSplitRenderer(layoutResult);
                        splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                        for (IRenderer renderer : splitRenderer.childRenderers) {
                            renderer.setParent(splitRenderer);
                        }

                        AbstractRenderer overflowRenderer = createOverflowRenderer(layoutResult);
                        List<IRenderer> overflowRendererChildren = new ArrayList<>();
                        overflowRendererChildren.add(result.getOverflowRenderer());
                        overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));

                        overflowRenderer.childRenderers = overflowRendererChildren;
                        if (keepTogether) {
                            splitRenderer = null;
                            overflowRenderer.childRenderers.clear();
                            overflowRenderer.childRenderers = new ArrayList<>(childRenderers);
                        }

                        applyPaddings(occupiedArea.getBBox(), paddings, true);
                        applyBorderBox(occupiedArea.getBBox(), borders, true);
                        applyMargins(occupiedArea.getBBox(), margins, true);

                        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
                        } else {
                            return new LayoutResult(layoutResult, occupiedArea, splitRenderer, overflowRenderer);
                        }
                    }
                }
            }

            anythingPlaced = true;

            occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
            if (result.getStatus() == LayoutResult.FULL) {
                layoutBox.setHeight(layoutBox.getHeight() - result.getOccupiedArea().getBBox().getHeight());
                if (childRenderer.<Object>getProperty(Property.WIDTH) != null) {
                    alignChildHorizontally(childRenderer, layoutBox.getWidth());
                }
            }
        }

        applyPaddings(occupiedArea.getBBox(), paddings, true);
        if (blockHeight != null && blockHeight > occupiedArea.getBBox().getHeight()) {
            occupiedArea.getBBox().moveDown((float) blockHeight - occupiedArea.getBBox().getHeight()).setHeight((float) blockHeight);
        }
        if (isPositioned) {
            float y = (float) getPropertyAsFloat(Property.Y);
            float relativeY = isFixedLayout() ? 0 : layoutBox.getY();
            move(0, relativeY + y - occupiedArea.getBBox().getY());
        }

        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), margins, true);
        if (this.<Float>getProperty(Property.ROTATION_ANGLE) != null) {
            applyRotationLayout(layoutContext.getArea().getBBox().clone());
            if (isNotFittingHeight(layoutContext.getArea())) {
                if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                    return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
                }
            }
        }
        applyVerticalAlignment();
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    protected AbstractRenderer createSplitRenderer(int layoutResult) {
        AbstractRenderer splitRenderer = (AbstractRenderer) getNextRenderer();
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        splitRenderer.isLastRendererForModelElement = false;
        return splitRenderer;
    }

    protected AbstractRenderer createOverflowRenderer(int layoutResult) {
        AbstractRenderer overflowRenderer = (AbstractRenderer) getNextRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.properties = properties;
        return overflowRenderer;
    }

    @Override
    public void draw(DrawContext drawContext) {
        PdfDocument document = drawContext.getDocument();
        applyDestination(document);
        applyAction(document);

        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
        TagTreePointer tagPointer = null;
        IAccessibleElement accessibleElement = null;
        if (isTagged) {
            accessibleElement = (IAccessibleElement) getModelElement();
            PdfName role = accessibleElement.getRole();
            if (role != null && !PdfName.Artifact.equals(role)) {
                tagPointer = document.getTagStructureContext().getAutoTaggingPointer();
                if (!tagPointer.isElementConnectedToTag(accessibleElement)) {
                    AccessibleAttributesApplier.applyLayoutAttributes(role, this, document);

                    if (role.equals(PdfName.TD)) {
                        AccessibleAttributesApplier.applyTableAttributes(this);
                    }

                    if (role.equals(PdfName.List)) {
                        AccessibleAttributesApplier.applyListAttributes(this);
                    }

                }
                tagPointer.addTag(accessibleElement, true);
            } else {
                isTagged = false;
            }
        }

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyAbsolutePositioningTranslation(false);
        }

        beginRotationIfApplied(drawContext.getCanvas());

        drawBackground(drawContext);
        drawBorder(drawContext);
        drawChildren(drawContext);

        endRotationIfApplied(drawContext.getCanvas());

        if (isRelativePosition) {
            applyAbsolutePositioningTranslation(true);
        }

        if (isTagged) {
            tagPointer.moveToParent();
            if (isLastRendererForModelElement) {
                document.getTagStructureContext().removeElementConnectionToTag(accessibleElement);
            }
        }

        flushed = true;
    }

    @Override
    public Rectangle getOccupiedAreaBBox() {
        Rectangle bBox = occupiedArea.getBBox().clone();
        Float rotationAngle = this.<Float>getProperty(Property.ROTATION_ANGLE);
        if (rotationAngle != null) {
            bBox.setWidth((float) getPropertyAsFloat(Property.ROTATION_INITIAL_WIDTH));
            bBox.setHeight((float) getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT));
        }
        return bBox;
    }

    protected void applyVerticalAlignment() {
        VerticalAlignment verticalAlignment = this.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT);
        if (verticalAlignment != null && verticalAlignment != VerticalAlignment.TOP && childRenderers.size() > 0) {
            float deltaY = childRenderers.get(childRenderers.size() - 1).getOccupiedArea().getBBox().getY() - getInnerAreaBBox().getY();
            switch (verticalAlignment) {
                case BOTTOM:
                    for (IRenderer child : childRenderers) {
                        child.move(0, -deltaY);
                    }
                    break;
                case MIDDLE:
                    for (IRenderer child : childRenderers) {
                        child.move(0, -deltaY / 2);
                    }
                    break;
            }
        }
    }

    protected void applyRotationLayout(Rectangle layoutBox) {
        Float rotationPointX = getPropertyAsFloat(Property.ROTATION_POINT_X);
        Float rotationPointY = getPropertyAsFloat(Property.ROTATION_POINT_Y);

        if (rotationPointX == null || rotationPointY == null) {
            // if rotation point was not specified, the most bottom-left point is used
            rotationPointX = occupiedArea.getBBox().getX();
            rotationPointY = occupiedArea.getBBox().getY();
            setProperty(Property.ROTATION_POINT_X, rotationPointX);
            setProperty(Property.ROTATION_POINT_Y, rotationPointY);
        }

        float height = occupiedArea.getBBox().getHeight();
        float width = occupiedArea.getBBox().getWidth();

        setProperty(Property.ROTATION_INITIAL_WIDTH, width);
        setProperty(Property.ROTATION_INITIAL_HEIGHT, height);


        if (!isPositioned()) {
            List<Point> rotatedPoints = new ArrayList<>();
            getLayoutShiftAndRotatedPoints(rotatedPoints, (float) rotationPointX, rotationPointY);

            Point clipLineBeg = new Point(layoutBox.getRight(), layoutBox.getTop());
            Point clipLineEnd = new Point(layoutBox.getRight(), layoutBox.getBottom());
            List<Point> newOccupiedBox = clipBBox(rotatedPoints, clipLineBeg, clipLineEnd);

            double maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            for (Point point : newOccupiedBox) {
                if (point.getX() > maxX)  maxX = point.getX();
                if (point.getY() < minY)  minY = point.getY();
            }

            float newHeight = (float) (occupiedArea.getBBox().getTop() - minY);
            float newWidth = (float) (maxX - occupiedArea.getBBox().getLeft());

            occupiedArea.getBBox().setWidth(newWidth);
            occupiedArea.getBBox().setHeight(newHeight);

            move(0, height - newHeight);
        }
    }

    protected float[] applyRotation() {
        float dx = 0, dy = 0;
        if (!isPositioned()) {
            Point shift = getLayoutShiftAndRotatedPoints(new ArrayList<Point>(), 0, 0);

            dy = (float) shift.getY();
            dx = (float) shift.getX();
        }

        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        AffineTransform transform = new AffineTransform();
        transform.rotate((float) angle);
        float[] ctm = new float[6];
        transform.getMatrix(ctm);

        ctm[4] = (float) getPropertyAsFloat(Property.ROTATION_POINT_X) + dx;
        ctm[5] = (float) getPropertyAsFloat(Property.ROTATION_POINT_Y) + dy;
        return ctm;
    }

    private Point getLayoutShiftAndRotatedPoints(List<Point> rotatedPoints, float shiftX, float shiftY) {
        float angle = (float) getPropertyAsFloat(Property.ROTATION_ANGLE);
        float width = (float) getPropertyAsFloat(Property.ROTATION_INITIAL_WIDTH);
        float height = (float) getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT);

        float left = occupiedArea.getBBox().getX() - shiftX;
        float bottom = occupiedArea.getBBox().getY() - shiftY;
        float right = left + width;
        float top = bottom + height;

        AffineTransform rotateTransform = new AffineTransform();
        rotateTransform.rotate(angle);

        transformBBox(left, bottom, right, top, rotateTransform, rotatedPoints);

        double minX = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (Point point : rotatedPoints) {
            if (point.getX() < minX)  minX = point.getX();
            if (point.getY() > maxY)  maxY = point.getY();
        }

        float dx = (float) (left - minX);
        float dy = (float) (top - maxY);

        for (Point point : rotatedPoints) {
            point.setLocation(point.getX() + dx + shiftX, point.getY() + dy + shiftY);
        }

        return new Point(dx, dy);
    }

    protected void beginRotationIfApplied(PdfCanvas canvas) {
        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            float heightDiff = (float) getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT) - occupiedArea.getBBox().getHeight();

            float shiftX = (float) getPropertyAsFloat(Property.ROTATION_POINT_X);
            float shiftY = (float) getPropertyAsFloat(Property.ROTATION_POINT_Y) + heightDiff;

            move(-shiftX, -shiftY);
            float[] ctm = applyRotation();
            canvas.saveState().concatMatrix(ctm[0], ctm[1], ctm[2], ctm[3], ctm[4], ctm[5]);
        }
    }

    protected void endRotationIfApplied(PdfCanvas canvas) {
        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            float heightDiff = (float) getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT) - occupiedArea.getBBox().getHeight();

            float shiftX = (float) getPropertyAsFloat(Property.ROTATION_POINT_X);
            float shiftY = (float) getPropertyAsFloat(Property.ROTATION_POINT_Y) + heightDiff;

            canvas.restoreState();
            move(shiftX, shiftY);
        }
    }

    private List<Point> transformBBox(float left, float bottom, float right, float top, AffineTransform transform, List<Point> bBoxPoints) {
        bBoxPoints.addAll(Arrays.asList(new Point(left, bottom), new Point(right, bottom),
                new Point(right, top), new Point(left, top)));

        for (Point point : bBoxPoints) {
            transform.transform(point, point);
        }

        return bBoxPoints;
    }

    private List<Point> clipBBox(List<Point> points, Point clipLineBeg, Point clipLineEnd) {
        List<Point> filteredPoints = new ArrayList<>();

        boolean prevOnRightSide = false;
        Point filteringPoint = points.get(0);
        if (checkPointSide(filteringPoint, clipLineBeg, clipLineEnd) >= 0) {
            filteredPoints.add(filteringPoint);
            prevOnRightSide = true;
        }

        Point prevPoint = filteringPoint;
        for (int i = 1; i < points.size() + 1; ++i) {
            filteringPoint = points.get(i % points.size());
            if (checkPointSide(filteringPoint, clipLineBeg, clipLineEnd) >= 0) {
                if (!prevOnRightSide) {
                    filteredPoints.add(getIntersectionPoint(prevPoint, filteringPoint, clipLineBeg, clipLineEnd));
                }
                filteredPoints.add(filteringPoint);
                prevOnRightSide = true;
            } else if (prevOnRightSide) {
                filteredPoints.add(getIntersectionPoint(prevPoint, filteringPoint, clipLineBeg, clipLineEnd));
            }

            prevPoint = filteringPoint;
        }

        return filteredPoints;
    }

    private int checkPointSide(Point filteredPoint, Point clipLineBeg, Point clipLineEnd) {
        double x1, x2, y1, y2;
        x1 = filteredPoint.getX() - clipLineBeg.getX();
        y2 = clipLineEnd.getY() - clipLineBeg.getY();

        x2 = clipLineEnd.getX() - clipLineBeg.getX();
        y1 = filteredPoint.getY() - clipLineBeg.getY();

        double sgn = x1*y2 - x2*y1;

        if (Math.abs(sgn) < 0.001) return 0;
        if (sgn > 0) return 1;
        if (sgn < 0) return -1;

        return 0;
    }

    private Point getIntersectionPoint(Point lineBeg, Point lineEnd, Point clipLineBeg, Point clipLineEnd) {
        double A1 = lineBeg.getY() - lineEnd.getY(), A2 = clipLineBeg.getY() - clipLineEnd.getY();
        double B1 = lineEnd.getX() - lineBeg.getX(), B2 = clipLineEnd.getX() - clipLineBeg.getX();
        double C1 = lineBeg.getX() * lineEnd.getY() - lineBeg.getY() * lineEnd.getX();
        double C2 = clipLineBeg.getX() * clipLineEnd.getY() - clipLineBeg.getY() * clipLineEnd.getX();

        double M = B1 * A2 - B2 * A1;

        return new Point((B2 * C1 - B1 * C2) / M, (C2 * A1 - C1 * A2) / M);
    }
}
