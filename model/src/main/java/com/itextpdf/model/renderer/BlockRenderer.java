package com.itextpdf.model.renderer;

import com.itextpdf.basics.geom.AffineTransform;
import com.itextpdf.basics.geom.Point2D;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.BlockElement;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutPosition;
import com.itextpdf.model.layout.LayoutResult;

import java.util.*;

public class BlockRenderer extends AbstractRenderer {

    public BlockRenderer(BlockElement modelElement) {
        super(modelElement);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        int pageNumber = layoutContext.getArea().getPageNumber();

        Rectangle parentBBox = layoutContext.getArea().getBBox().clone();
        if (getProperty(Property.ROTATION_ANGLE) != null) {
            parentBBox.moveDown(AbstractRenderer.INF - parentBBox.getHeight()).setHeight(AbstractRenderer.INF);
        }
        applyMargins(parentBBox, false);
        applyBorderBox(parentBBox, false);

        if (isPositioned()) {
            float x = getPropertyAsFloat(Property.X);
            float relativeX = isFixedLayout() ? 0 : parentBBox.getX();
            parentBBox.setX(relativeX + x);
        }

        Float blockWidth = retrieveWidth(parentBBox.getWidth());
        if (blockWidth != null && (blockWidth < parentBBox.getWidth() || isPositioned())) {
            parentBBox.setWidth(blockWidth);
        }
        applyPaddings(parentBBox, false);

        List<Rectangle> areas;
        if (isPositioned()) {
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
            while ((result = childRenderer.layout(new LayoutContext(new LayoutArea(pageNumber, layoutBox)))).getStatus() != LayoutResult.FULL) {
                occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
                layoutBox.setHeight(layoutBox.getHeight() - result.getOccupiedArea().getBBox().getHeight());

                if (childRenderer.getProperty(Property.WIDTH) != null) {
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
                            BlockRenderer splitRenderer = createSplitRenderer(LayoutResult.PARTIAL);
                            splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                            splitRenderer.childRenderers.add(result.getSplitRenderer());
                            splitRenderer.occupiedArea = occupiedArea;

                            BlockRenderer overflowRenderer = createOverflowRenderer(LayoutResult.PARTIAL);
                            List<IRenderer> overflowRendererChildren = new ArrayList<>();
                            overflowRendererChildren.add(result.getOverflowRenderer());
                            overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                            overflowRenderer.childRenderers = overflowRendererChildren;

                            applyPaddings(occupiedArea.getBBox(), true);
                            applyBorderBox(occupiedArea.getBBox(), true);
                            applyMargins(occupiedArea.getBBox(), true);
                            return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, splitRenderer, overflowRenderer);
                        } else {
                            childRenderers.set(childPos, result.getSplitRenderer());
                            childRenderers.add(childPos + 1, result.getOverflowRenderer());
                            layoutBox = areas.get(++currentAreaPos).clone();
                            break;
                        }
                    } else if (result.getStatus() == LayoutResult.NOTHING) {
                        boolean keepTogether = Boolean.valueOf(true).equals(getProperty(Property.KEEP_TOGETHER));
                        int layoutResult = anythingPlaced && !keepTogether ? LayoutResult.PARTIAL : LayoutResult.NOTHING;

                        BlockRenderer splitRenderer = createSplitRenderer(layoutResult);
                        splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));

                        BlockRenderer overflowRenderer = createOverflowRenderer(layoutResult);
                        List<IRenderer> overflowRendererChildren = new ArrayList<>();
                        overflowRendererChildren.add(result.getOverflowRenderer());
                        overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                        overflowRenderer.childRenderers = overflowRendererChildren;
                        if (getProperty(Property.KEEP_TOGETHER)) {
                            splitRenderer = null;
                            overflowRenderer.childRenderers.clear();
                            overflowRenderer.childRenderers = new ArrayList<>(childRenderers);
                        }

                        applyPaddings(occupiedArea.getBBox(), true);
                        applyBorderBox(occupiedArea.getBBox(), true);
                        applyMargins(occupiedArea.getBBox(), true);
                        return new LayoutResult(layoutResult, occupiedArea, splitRenderer, overflowRenderer);
                    }
                }
            }

            anythingPlaced = true;

            occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
            if (result.getStatus() == LayoutResult.FULL) {
                layoutBox.setHeight(layoutBox.getHeight() - result.getOccupiedArea().getBBox().getHeight());
                if (childRenderer.getProperty(Property.WIDTH) != null) {
                    alignChildHorizontally(childRenderer, layoutBox.getWidth());
                }
            }
        }

        Float blockHeight = getPropertyAsFloat(Property.HEIGHT);
        applyPaddings(occupiedArea.getBBox(), true);
        if (blockHeight != null && blockHeight > occupiedArea.getBBox().getHeight()) {
            occupiedArea.getBBox().moveDown(blockHeight - occupiedArea.getBBox().getHeight()).setHeight(blockHeight);
            applyVerticalAlignment();
        }
        if (isPositioned()) {
            float y = getPropertyAsFloat(Property.Y);
            float relativeY = isFixedLayout() ? 0 : layoutBox.getY();
            move(0, relativeY + y - occupiedArea.getBBox().getY());
        }

        applyBorderBox(occupiedArea.getBBox(), true);
        applyMargins(occupiedArea.getBBox(), true);
        if (getProperty(Property.ROTATION_ANGLE) != null) {
            applyRotationLayout(layoutContext.getArea().getBBox().clone());
            if (isNotFittingHeight(layoutContext.getArea())) {
                if (!layoutContext.getArea().isEmptyArea()) {
                    return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
                }
            }
        }
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    protected BlockRenderer createSplitRenderer(int layoutResult) {
        BlockRenderer splitRenderer = new BlockRenderer((BlockElement) modelElement);
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        return splitRenderer;
    }

    protected BlockRenderer createOverflowRenderer(int layoutResult) {
        BlockRenderer overflowRenderer = new BlockRenderer((BlockElement) modelElement);
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        return overflowRenderer;
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        int position = getPropertyAsInteger(Property.POSITION);
        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }

        beginRotationIfApplied(canvas);

        drawBackground(document, canvas);
        drawBorder(document, canvas);
        drawChildren(document, canvas);

        endRotationIfApplied(canvas);

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(true);
        }

        flushed = true;
    }

    @Override
    protected Rectangle getOccupiedAreaBBox() {
        Rectangle bBox = occupiedArea.getBBox().clone();
        Float rotationAngle = getProperty(Property.ROTATION_ANGLE);
        if (rotationAngle != null) {
            bBox.setWidth(getPropertyAsFloat(Property.ROTATION_INITIAL_WIDTH));
            bBox.setHeight(getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT));
        }
        return bBox;
    }

    protected void applyVerticalAlignment() {
        Property.VerticalAlignment verticalAlignment = getProperty(Property.VERTICAL_ALIGNMENT);
        if (verticalAlignment != null && verticalAlignment != Property.VerticalAlignment.TOP && childRenderers.size() > 0) {
            float deltaY = childRenderers.get(childRenderers.size() - 1).getOccupiedArea().getBBox().getY() - occupiedArea.getBBox().getY();
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
            List<Point2D.Float> rotatedPoints = new ArrayList<>();
            getLayoutShiftAndRotatedPoints(rotatedPoints, rotationPointX, rotationPointY);

            Point2D clipLineBeg = new Point2D.Float(layoutBox.getRight(), layoutBox.getTop());
            Point2D clipLineEnd = new Point2D.Float(layoutBox.getRight(), layoutBox.getBottom());
            List<Point2D> newOccupiedBox = clipBBox(rotatedPoints, clipLineBeg, clipLineEnd);

            double maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            for (Point2D point : newOccupiedBox) {
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
            Point2D shift = getLayoutShiftAndRotatedPoints(new ArrayList<Point2D.Float>(), 0, 0);

            dy = (float) shift.getY();
            dx = (float) shift.getX();
        }

        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        AffineTransform transform = new AffineTransform();
        transform.rotate(angle);
        float[] ctm = new float[6];
        transform.getMatrix(ctm);

        ctm[4] = getPropertyAsFloat(Property.ROTATION_POINT_X) + dx;
        ctm[5] = getPropertyAsFloat(Property.ROTATION_POINT_Y) + dy;
        return ctm;
    }

    private Point2D.Float getLayoutShiftAndRotatedPoints(List<Point2D.Float> rotatedPoints, float shiftX, float shiftY) {
        float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        float width = getPropertyAsFloat(Property.ROTATION_INITIAL_WIDTH);
        float height = getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT);

        float left = occupiedArea.getBBox().getX() - shiftX;
        float bottom = occupiedArea.getBBox().getY() - shiftY;
        float right = left + width;
        float top = bottom + height;

        AffineTransform rotateTransform = new AffineTransform();
        rotateTransform.rotate(angle);

        transformBBox(left, bottom, right, top, rotateTransform, rotatedPoints);

        double minX = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (Point2D point : rotatedPoints) {
            if (point.getX() < minX)  minX = point.getX();
            if (point.getY() > maxY)  maxY = point.getY();
        }

        float dx = (float) (left - minX);
        float dy = (float) (top - maxY);

        for (Point2D point : rotatedPoints) {
            point.setLocation(point.getX() + dx + shiftX, point.getY() + dy + shiftY);
        }

        return new Point2D.Float(dx, dy);
    }



    private void beginRotationIfApplied(PdfCanvas canvas) {
        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            float heightDiff = getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT) - occupiedArea.getBBox().getHeight();

            float shiftX = getPropertyAsFloat(Property.ROTATION_POINT_X);
            float shiftY = getPropertyAsFloat(Property.ROTATION_POINT_Y) + heightDiff;

            move(-shiftX, -shiftY);
            float[] ctm = applyRotation();
            canvas.saveState().concatMatrix(ctm[0], ctm[1], ctm[2], ctm[3], ctm[4], ctm[5]);
        }
    }

    private void endRotationIfApplied(PdfCanvas canvas) {
        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            float heightDiff = getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT) - occupiedArea.getBBox().getHeight();

            float shiftX = getPropertyAsFloat(Property.ROTATION_POINT_X);
            float shiftY = getPropertyAsFloat(Property.ROTATION_POINT_Y) + heightDiff;

            canvas.restoreState();
            move(shiftX, shiftY);
        }
    }

    private List<Point2D.Float> transformBBox(float left, float bottom, float right, float top, AffineTransform transform, List<Point2D.Float> bBoxPoints) {
        bBoxPoints.addAll(Arrays.asList(new Point2D.Float(left, bottom), new Point2D.Float(right, bottom),
                new Point2D.Float(right, top), new Point2D.Float(left, top)));

        for (Point2D.Float point : bBoxPoints) {
            transform.transform(point, point);
        }

        return bBoxPoints;
    }

    private List<Point2D> clipBBox(List<Point2D.Float> points, Point2D clipLineBeg, Point2D clipLineEnd) {
        List<Point2D> filteredPoints = new ArrayList<>();

        boolean prevOnRightSide = false;
        Point2D filteringPoint = points.get(0);
        if (checkPointSide(filteringPoint, clipLineBeg, clipLineEnd) >= 0) {
            filteredPoints.add(filteringPoint);
            prevOnRightSide = true;
        }

        Point2D prevPoint = filteringPoint;
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

    private int checkPointSide(Point2D filteredPoint, Point2D clipLineBeg, Point2D clipLineEnd) {
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

    private Point2D getIntersectionPoint(Point2D lineBeg, Point2D lineEnd, Point2D clipLineBeg, Point2D clipLineEnd) {
        double A1 = lineBeg.getY() - lineEnd.getY(), A2 = clipLineBeg.getY() - clipLineEnd.getY();
        double B1 = lineEnd.getX() - lineBeg.getX(), B2 = clipLineEnd.getX() - clipLineBeg.getX();
        double C1 = lineBeg.getX() * lineEnd.getY() - lineBeg.getY() * lineEnd.getX();
        double C2 = clipLineBeg.getX() * clipLineEnd.getY() - clipLineBeg.getY() * clipLineEnd.getX();

        double M = B1 * A2 - B2 * A1;

        return new Point2D.Double((B2 * C1 - B1 * C2) / M, (C2 * A1 - C1 * A2) / M);
    }
}
