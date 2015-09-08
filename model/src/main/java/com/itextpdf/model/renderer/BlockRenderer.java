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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            applyRotationLayout();
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
            bBox.setWidth(getWidthBeforeRotation(rotationAngle));
            bBox.setHeight(getHeightBeforeRotation());
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

    protected void applyRotationLayout() {
        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);

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

        double cos = Math.abs(Math.cos(angle));
        double sin = Math.abs(Math.sin(angle));
        float newHeight = (float) (height*cos + width*sin);
        float newWidth = (float) (height*sin + width*cos);

        occupiedArea.getBBox().setWidth(newWidth);
        occupiedArea.getBBox().setHeight(newHeight);

        float heightDiff = height - newHeight;
        move(0, heightDiff);
        setProperty(Property.ROTATION_LAYOUT_SHIFT, heightDiff);
    }

    protected float[] applyRotation() {
        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        AffineTransform transform = new AffineTransform();
        transform.rotate(angle);

        float dx = 0, dy = 0;
        if (!isPositioned()) {
            float x = occupiedArea.getBBox().getX();
            float y = occupiedArea.getBBox().getY();
            float actualWidth = getWidthBeforeRotation(angle);
            float actualHeight = getHeightBeforeRotation();

            Point2D p00 = transform.transform(new Point2D.Float(x, y), new Point2D.Float());
            Point2D p01 = transform.transform(new Point2D.Float(x + actualWidth, y), new Point2D.Float());
            Point2D p10 = transform.transform(new Point2D.Float(x + actualWidth, y + actualHeight), new Point2D.Float());
            Point2D p11 = transform.transform(new Point2D.Float(x, y + actualHeight), new Point2D.Float());

            List<Double> xValues = Arrays.asList(p00.getX(), p01.getX(), p10.getX(), p11.getX());
            List<Double> yValues = Arrays.asList(p00.getY(), p01.getY(), p10.getY(), p11.getY());

            double minX = Collections.min(xValues);
            double maxY = Collections.max(yValues);

            dy = (float) ((y + actualHeight) - maxY);
            dx = (float) (x - minX);
        }

        float rotationPointX = getPropertyAsFloat(Property.ROTATION_POINT_X);
        float rotationPointY = getPropertyAsFloat(Property.ROTATION_POINT_Y);

        float[] ctm = new float[6];
        transform.getMatrix(ctm);
        ctm[4] = rotationPointX + dx;
        ctm[5] = rotationPointY + dy;
        return ctm;
    }



    private void beginRotationIfApplied(PdfCanvas canvas) {
        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            float heightDiff = getProperty(Property.ROTATION_LAYOUT_SHIFT);

            float rotationPointX = getProperty(Property.ROTATION_POINT_X);
            float rotationPointY = getProperty(Property.ROTATION_POINT_Y);

            float shiftX = rotationPointX;
            float shiftY = rotationPointY + heightDiff;

            move(-shiftX, -shiftY);
            float[] ctm = applyRotation();
            canvas.saveState().concatMatrix(ctm[0], ctm[1], ctm[2], ctm[3], ctm[4], ctm[5]);
        }
    }

    private void endRotationIfApplied(PdfCanvas canvas) {
        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            float heightDiff = getProperty(Property.ROTATION_LAYOUT_SHIFT);

            float shiftX = getPropertyAsFloat(Property.ROTATION_POINT_X);
            float shiftY = getPropertyAsFloat(Property.ROTATION_POINT_Y) + heightDiff;

            canvas.restoreState();
            move(shiftX, shiftY);
        }
    }

    private float getWidthBeforeRotation(float angle) {
        float rotatedWidth = occupiedArea.getBBox().getWidth();
        float rotatedHeight = occupiedArea.getBBox().getHeight();

        float pi4 = (float) (Math.PI / 4);
        float pi2 = (float) (Math.PI / 2);
        if (checkIfMultiple(angle, pi4) && !checkIfMultiple(angle, pi2))
            return (float) (rotatedWidth*Math.sqrt(2) - getHeightBeforeRotation());

        double cos = Math.abs(Math.cos(angle));
        double sin = Math.abs(Math.sin(angle));

        return (float) ((rotatedHeight*sin - rotatedWidth*cos)/(sin*sin - cos*cos));
    }

    private float getHeightBeforeRotation() {
        float rotatedHeight = occupiedArea.getBBox().getHeight();
        float heightDiff = getProperty(Property.ROTATION_LAYOUT_SHIFT);
        return rotatedHeight + heightDiff;
    }

    private boolean checkIfMultiple(float multipleOfNumber, float number) {
        float remainder = Math.abs(multipleOfNumber % number);
        return remainder < EPS || number - remainder < EPS;
    }
}
