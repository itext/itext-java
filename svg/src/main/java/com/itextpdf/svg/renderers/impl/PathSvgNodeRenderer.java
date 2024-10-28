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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.MarkerVertexType;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.IMarkerCapable;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.svg.renderers.path.SvgPathShapeFactory;
import com.itextpdf.svg.renderers.path.impl.AbstractPathShape;
import com.itextpdf.svg.renderers.path.impl.ClosePath;
import com.itextpdf.svg.renderers.path.impl.IControlPointCurve;
import com.itextpdf.svg.renderers.path.impl.MoveTo;
import com.itextpdf.svg.renderers.path.impl.QuadraticSmoothCurveTo;
import com.itextpdf.svg.renderers.path.impl.SmoothSCurveTo;
import com.itextpdf.svg.utils.SvgCoordinateUtils;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;path&gt; tag.
 */
public class PathSvgNodeRenderer extends AbstractSvgNodeRenderer implements IMarkerCapable {

    private static final String SPACE_CHAR = " ";

    /**
     * The regular expression to find invalid operators in the <a href="https://www.w3.org/TR/SVG/paths.html#PathData">PathData
     * attribute of the &lt;path&gt; element</a>
     * <p>
     * Find any occurrence of a letter that is not an operator
     */
    private static final String INVALID_OPERATOR_REGEX = "(?:(?![mzlhvcsqtae])\\p{L})";
    private static final Pattern INVALID_REGEX_PATTERN = Pattern.compile(INVALID_OPERATOR_REGEX, Pattern.CASE_INSENSITIVE);

    /**
     * The regular expression to split the <a href="https://www.w3.org/TR/SVG/paths.html#PathData">PathData attribute of
     * the &lt;path&gt; element</a>
     * <p>
     * Since {@link PathSvgNodeRenderer#containsInvalidAttributes(String)} is called before the use of this expression
     * in {@link PathSvgNodeRenderer#parsePathOperations()} the attribute to be split is valid.
     * <p>
     * SVG defines 6 types of path commands, for a total of 20 commands:
     * <p>
     * MoveTo: M, m
     * LineTo: L, l, H, h, V, v
     * Cubic Bezier Curve: C, c, S, s
     * Quadratic Bezier Curve: Q, q, T, t
     * Elliptical Arc Curve: A, a
     * ClosePath: Z, z
     */
    private static final Pattern SPLIT_PATTERN = Pattern.compile("(?=[mlhvcsqtaz])", Pattern.CASE_INSENSITIVE);

    /**
     * The {@link ClosePath} shape keeping track of the initial point set by a {@link MoveTo} operation.
     * The original value is {@code null}, and must be set via a {@link MoveTo} operation before it may be drawn.
     */
    private ClosePath zOperator = null;

    @Override
    public void doDraw(SvgDrawContext context) {
        PdfCanvas canvas = context.getCurrentCanvas();
        canvas.writeLiteral("% path\n");
        for (IPathShape item : getShapes()) {
            item.draw(canvas);
        }
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        PathSvgNodeRenderer copy = new PathSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        Point lastPoint = null;
        Rectangle commonRectangle = null;
        for (IPathShape item : getShapes()) {
            if (lastPoint == null) {
                lastPoint = item.getEndingPoint();
            }
            Rectangle rectangle = item.getPathShapeRectangle(lastPoint);
            commonRectangle = Rectangle.getCommonRectangle(commonRectangle, rectangle);

            lastPoint = item.getEndingPoint();
        }
        return commonRectangle;
    }

    /**
     * Gets the coordinates that shall be passed to {@link IPathShape#setCoordinates} for the current shape.
     *
     * @param shape          The current shape.
     * @param previousShape  The previous shape which can affect the coordinates of the current shape.
     * @param pathProperties The operator and all arguments as an array of {@link String String}s
     * @return a {@link String} array of coordinates that shall be passed to {@link IPathShape#setCoordinates}
     */
    private String[] getShapeCoordinates(IPathShape shape, IPathShape previousShape, String[] pathProperties) {
        if (shape instanceof ClosePath) {
            return null;
        }
        String[] shapeCoordinates = null;
        if (shape instanceof SmoothSCurveTo || shape instanceof QuadraticSmoothCurveTo) {
            String[] startingControlPoint = new String[2];
            if (previousShape != null) {
                Point previousEndPoint = previousShape.getEndingPoint();
                // If the previous command was a Bezier curve, use its last control point
                if (previousShape instanceof IControlPointCurve) {
                    Point lastControlPoint = ((IControlPointCurve) previousShape).getLastControlPoint();
                    float reflectedX = (float) (2 * previousEndPoint.getX() - lastControlPoint.getX());
                    float reflectedY = (float) (2 * previousEndPoint.getY() - lastControlPoint.getY());

                    startingControlPoint[0] = SvgCssUtils.convertFloatToString(reflectedX);
                    startingControlPoint[1] = SvgCssUtils.convertFloatToString(reflectedY);
                } else {
                    startingControlPoint[0] = SvgCssUtils.convertDoubleToString(previousEndPoint.getX());
                    startingControlPoint[1] = SvgCssUtils.convertDoubleToString(previousEndPoint.getY());
                }
            } else {
                throw new SvgProcessingException(SvgExceptionMessageConstant.INVALID_SMOOTH_CURVE_USE);
            }
            shapeCoordinates = concatenate(startingControlPoint, pathProperties);
        }
        if (shapeCoordinates == null) {
            shapeCoordinates = pathProperties;
        }
        return shapeCoordinates;
    }

    /**
     * Processes an individual pathing operator and all of its arguments, converting into one or more
     * {@link IPathShape} objects.
     *
     * @param pathProperties The property operator and all arguments as an array of {@link String}s
     * @param previousShape  The previous shape which can affect the positioning of the current shape. If no previous
     *                       shape exists {@code null} is passed.
     * @return a {@link List} of each {@link IPathShape} that should be drawn to represent the operator.
     */
    private List<IPathShape> processPathOperator(String[] pathProperties, IPathShape previousShape) {
        List<IPathShape> shapes = new ArrayList<>();
        if (pathProperties.length == 0 || pathProperties[0].isEmpty()
                || SvgPathShapeFactory.getArgumentCount(pathProperties[0]) < 0) {
            return shapes;
        }

        int argumentCount = SvgPathShapeFactory.getArgumentCount(pathProperties[0]);
        if (argumentCount == 0) { // closePath operator
            if (previousShape == null) {
                throw new SvgProcessingException(SvgExceptionMessageConstant.INVALID_CLOSEPATH_OPERATOR_USE);
            }
            shapes.add(zOperator);
            return shapes;
        }
        for (int index = 1; index < pathProperties.length; index += argumentCount) {
            if (index + argumentCount > pathProperties.length) {
                break;
            }
            IPathShape pathShape = SvgPathShapeFactory.createPathShape(pathProperties[0]);
            if (pathShape instanceof MoveTo) {
                shapes.addAll(addMoveToShapes(pathShape, pathProperties, previousShape));
                return shapes;
            }

            String[] shapeCoordinates = getShapeCoordinates(pathShape, previousShape,
                    Arrays.copyOfRange(pathProperties, index, index + argumentCount));
            if (pathShape != null) {
                if (shapeCoordinates != null) {
                    pathShape.setCoordinates(shapeCoordinates, getCurrentPoint(previousShape));
                }
                shapes.add(pathShape);
            }
            previousShape = pathShape;
        }
        return shapes;
    }

    private List<IPathShape> addMoveToShapes(IPathShape pathShape, String[] pathProperties,
            IPathShape beforeMoveShape) {
        List<IPathShape> shapes = new ArrayList<>();
        int argumentCount = 2;
        String[] shapeCoordinates = getShapeCoordinates(pathShape, beforeMoveShape,
                Arrays.copyOfRange(pathProperties, 1, 3));
        zOperator = new ClosePath(pathShape.isRelative());
        final Point currentPointBeforeMove = getCurrentPoint(beforeMoveShape);
        zOperator.setCoordinates(shapeCoordinates, currentPointBeforeMove);
        pathShape.setCoordinates(shapeCoordinates, currentPointBeforeMove);
        shapes.add(pathShape);
        IPathShape previousShape = pathShape;
        if (pathProperties.length > 3) {
            for (int index = 3; index < pathProperties.length; index += argumentCount) {
                if (index + 2 > pathProperties.length) {
                    break;
                }
                pathShape = pathShape.isRelative() ? SvgPathShapeFactory.createPathShape("l")
                        : SvgPathShapeFactory.createPathShape("L");
                shapeCoordinates = getShapeCoordinates(pathShape, previousShape,
                        Arrays.copyOfRange(pathProperties, index, index + 2));
                pathShape.setCoordinates(shapeCoordinates, previousShape.getEndingPoint());
                shapes.add(pathShape);
                previousShape = pathShape;
            }
        }
        return shapes;
    }

    /**
     * Processes the {@link SvgConstants.Attributes#D} {@link PathSvgNodeRenderer#attributesAndStyles} and converts them
     * into one or more {@link IPathShape} objects to be drawn on the canvas.
     * <p>
     * Each individual operator is passed to {@link PathSvgNodeRenderer#processPathOperator(String[], IPathShape)} to be
     * processed individually.
     *
     * @return a {@link Collection} of each {@link IPathShape} that should be drawn to represent the path.
     */
    Collection<IPathShape> getShapes() {
        Collection<String> parsedResults = parsePathOperations();
        List<IPathShape> shapes = new ArrayList<>();

        for (String parsedResult : parsedResults) {
            String[] pathProperties = parsedResult.split(" +");
            IPathShape previousShape = shapes.size() == 0 ? null : shapes.get(shapes.size() - 1);
            List<IPathShape> operatorShapes = processPathOperator(pathProperties, previousShape);
            shapes.addAll(operatorShapes);
        }
        return shapes;
    }

    private static String[] concatenate(String[] first, String[] second) {
        String[] arr = new String[first.length + second.length];
        System.arraycopy(first, 0, arr, 0, first.length);
        System.arraycopy(second, 0, arr, first.length, second.length);
        return arr;
    }


    boolean containsInvalidAttributes(String attributes) {
        return INVALID_REGEX_PATTERN.matcher(attributes).find();
    }

    Collection<String> parsePathOperations() {
        Collection<String> result = new ArrayList<>();
        String pathString = attributesAndStyles.get(SvgConstants.Attributes.D);
        if (pathString == null) {
            pathString = "";
        }
        if (containsInvalidAttributes(pathString)) {
            throw new SvgProcessingException(SvgExceptionMessageConstant.INVALID_PATH_D_ATTRIBUTE_OPERATORS)
                    .setMessageParams(pathString);
        }

        pathString = pathString.replaceAll("\\s+", " ").trim();
        String[] operators = splitPathStringIntoOperators(pathString);

        for (String inst : operators) {
            String instTrim = inst.trim();
            if (!instTrim.isEmpty()) {
                char instruction = instTrim.charAt(0);
                String temp = instruction + SPACE_CHAR + instTrim.substring(1).replace(",", SPACE_CHAR).trim();
                // Do a run-through for decimal point separation
                temp = separateDecimalPoints(temp);
                result.add(temp);
            }
        }

        return result;
    }

    /**
     * Iterate over the input string and separate numbers from each other with space chars
     */
    String separateDecimalPoints(String input) {
        // If a space or minus sign is found reset
        // If a another point is found, add an extra space on before the point
        StringBuilder res = new StringBuilder();
        // We are now among the digits to the right of the decimal point
        boolean fractionalPartAfterDecimalPoint = false;
        // We are now among the exponent magnitude part
        boolean exponentSignMagnitude = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            // Resetting flags
            if (c == '-' || Character.isWhitespace(c)) {
                fractionalPartAfterDecimalPoint = false;
            }
            if (Character.isWhitespace(c)) {
                exponentSignMagnitude = false;
            }

            // Add extra space before the next number starting from '.', or before the next number starting with '-'
            if (endsWithNonWhitespace(res) && (c == '.' && fractionalPartAfterDecimalPoint ||
                    c == '-' && !exponentSignMagnitude)) {
                res.append(" ");
            }

            if (c == '.') {
                fractionalPartAfterDecimalPoint = true;
            } else if (Character.toLowerCase(c) == 'e') {
                exponentSignMagnitude = true;
            }

            res.append(c);
        }
        return res.toString();
    }

    /**
     * Gets an array of strings representing operators with their arguments, e.g. {"M 100 100", "L 300 100", "L200,
     * 300", "z"}
     */
    static String[] splitPathStringIntoOperators(String path) {
        return SPLIT_PATTERN.split(path);
    }

    private static boolean endsWithNonWhitespace(StringBuilder sb) {
        return sb.length() > 0 && !Character.isWhitespace(sb.charAt(sb.length() - 1));
    }

    @Override
    public void drawMarker(SvgDrawContext context, final MarkerVertexType markerVertexType) {
        Object[] allShapesOrdered = getShapes().toArray();
        Point point = null;
        if (MarkerVertexType.MARKER_START.equals(markerVertexType)) {
            point = ((AbstractPathShape) allShapesOrdered[0]).getEndingPoint();
        } else if (MarkerVertexType.MARKER_END.equals(markerVertexType)) {
            point = ((AbstractPathShape) allShapesOrdered[allShapesOrdered.length - 1])
                    .getEndingPoint();
        }
        if (point != null) {
            String moveX = SvgCssUtils.convertDoubleToString(point.getX());
            String moveY = SvgCssUtils.convertDoubleToString(point.getY());
            MarkerSvgNodeRenderer.drawMarker(context, moveX, moveY, markerVertexType, this);
        }
    }

    @Override
    public double getAutoOrientAngle(MarkerSvgNodeRenderer marker, boolean reverse) {
        Object[] pathShapes = getShapes().toArray();
        if (pathShapes.length > 1) {
            Vector v = new Vector(0, 0, 0);
            if (SvgConstants.Attributes.MARKER_END.equals(marker.attributesAndStyles.get(SvgConstants.Tags.MARKER))) {
                // Create vector from the last two shapes
                IPathShape lastShape = (IPathShape) pathShapes[pathShapes.length - 1];
                IPathShape secondToLastShape = (IPathShape) pathShapes[pathShapes.length - 2];
                v = new Vector((float) (lastShape.getEndingPoint().getX() - secondToLastShape.getEndingPoint().getX()),
                        (float) (lastShape.getEndingPoint().getY() - secondToLastShape.getEndingPoint().getY()),
                        0f);
            } else if (SvgConstants.Attributes.MARKER_START
                    .equals(marker.attributesAndStyles.get(SvgConstants.Tags.MARKER))) {
                // Create vector from the first two shapes
                IPathShape firstShape = (IPathShape) pathShapes[0];
                IPathShape secondShape = (IPathShape) pathShapes[1];
                v = new Vector((float) (secondShape.getEndingPoint().getX() - firstShape.getEndingPoint().getX()),
                        (float) (secondShape.getEndingPoint().getY() - firstShape.getEndingPoint().getY()),
                        0f);
            }
            // Get angle from this vector and the horizontal axis
            Vector xAxis = new Vector(1, 0, 0);
            double rotAngle = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(xAxis, v);
            return v.get(1) >= 0 && !reverse ? rotAngle : rotAngle * -1f;
        }
        return 0;
    }

    private static Point getCurrentPoint(IPathShape previousShape) {
        return previousShape == null ? new Point(0, 0) : previousShape.getEndingPoint();
    }
}
