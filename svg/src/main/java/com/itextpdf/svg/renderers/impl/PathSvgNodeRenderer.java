/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.svg.renderers.path.SvgPathShapeFactory;
import com.itextpdf.svg.renderers.path.impl.ClosePath;
import com.itextpdf.svg.renderers.path.impl.IControlPointCurve;
import com.itextpdf.svg.renderers.path.impl.MoveTo;
import com.itextpdf.svg.renderers.path.impl.QuadraticSmoothCurveTo;
import com.itextpdf.svg.renderers.path.impl.SmoothSCurveTo;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.SvgRegexUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;path&gt; tag.
 */
public class PathSvgNodeRenderer extends AbstractSvgNodeRenderer {

    private static final String SPACE_CHAR = " ";

    /**
     * The regular expression to find invalid operators in the <a href="https://www.w3.org/TR/SVG/paths.html#PathData">PathData attribute of the &ltpath&gt element</a>
     * <p>
     * Find any occurrence of a letter that is not an operator
     */
    private static final String INVALID_OPERATOR_REGEX = "(?:(?![mzlhvcsqtae])\\p{L})";
    private static Pattern invalidRegexPattern = Pattern.compile(INVALID_OPERATOR_REGEX, Pattern.CASE_INSENSITIVE);

    /**
     * The regular expression to split the <a href="https://www.w3.org/TR/SVG/paths.html#PathData">PathData attribute of the &ltpath&gt element</a>
     * <p>
     * Since {@link PathSvgNodeRenderer#containsInvalidAttributes(String)} is called before the use of this expression in {@link PathSvgNodeRenderer#parsePathOperations()} the attribute to be split is valid.
     *
     * SVG defines 6 types of path commands, for a total of 20 commands:
     *
     * MoveTo: M, m
     * LineTo: L, l, H, h, V, v
     * Cubic Bezier Curve: C, c, S, s
     * Quadratic Bezier Curve: Q, q, T, t
     * Elliptical Arc Curve: A, a
     * ClosePath: Z, z
     */
    private static final Pattern SPLIT_PATTERN = Pattern.compile("(?=[mlhvcsqtaz])", Pattern.CASE_INSENSITIVE);


    /**
     * The {@link Point} representing the current point in the path to be used for relative pathing operations.
     * The original value is the origin, and should be set via a {@link MoveTo} operation before it may be referenced.
     */
    private Point currentPoint = new Point(0, 0);

    /**
     * The {@link ClosePath} shape keeping track of the initial point set by a {@link MoveTo} operation.
     * The original value is {@code null}, and must be set via a {@link MoveTo} operation before it may be drawn.
     */
    private ClosePath zOperator = null;

    @Override
    public void doDraw(SvgDrawContext context) {
        PdfCanvas canvas = context.getCurrentCanvas();
        canvas.writeLiteral("% path\n");
        currentPoint = new Point(0, 0);
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

    /**
     * Gets the coordinates that shall be passed to {@link IPathShape#setCoordinates} for the current shape.
     *
     * @param shape          The current shape.
     * @param previousShape  The previous shape which can affect the coordinates of the current shape.
     * @param pathProperties The operator and all arguments as a {@link String[]}
     * @return a {@link String[]} of coordinates that shall be passed to {@link IPathShape#setCoordinates}
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
                //if the previous command was a Bezier curve, use its last control point
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
                // TODO RND-951
                startingControlPoint[0] = pathProperties[0];
                startingControlPoint[1] = pathProperties[1];
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
     * @param pathProperties The property operator and all arguments as a {@link String[]}
     * @param previousShape  The previous shape which can affect the positioning of the current shape. If no previous
     *                       shape exists {@code null} is passed.
     * @return a {@link List} of each {@link IPathShape} that should be drawn to represent the operator.
     */
    private List<IPathShape> processPathOperator(String[] pathProperties, IPathShape previousShape) {
        List<IPathShape> shapes = new ArrayList<>();
        if (pathProperties.length == 0 || pathProperties[0].isEmpty() || SvgPathShapeFactory.getArgumentCount(pathProperties[0]) < 0) {
            return shapes;
        }

        int argumentCount = SvgPathShapeFactory.getArgumentCount(pathProperties[0]);
        if (argumentCount == 0) { // closePath operator
            if (previousShape == null) {
                throw new SvgProcessingException(SvgLogMessageConstant.INVALID_CLOSEPATH_OPERATOR_USE);
            }
            shapes.add(zOperator);
            currentPoint = zOperator.getEndingPoint();
            return shapes;
        }
        for (int index = 1; index < pathProperties.length; index += argumentCount) {
            if (index + argumentCount > pathProperties.length) {
                break;
            }
            IPathShape pathShape = SvgPathShapeFactory.createPathShape(pathProperties[0]);
            if (pathShape instanceof MoveTo) {
                shapes.addAll(addMoveToShapes(pathShape, pathProperties));
                return shapes;
            }

            String[] shapeCoordinates = getShapeCoordinates(pathShape, previousShape, Arrays.copyOfRange(pathProperties, index, index+argumentCount));
            if (pathShape != null) {
                if (shapeCoordinates != null) {
                    pathShape.setCoordinates(shapeCoordinates, currentPoint);
                }
                currentPoint = pathShape.getEndingPoint(); // unsupported operators are ignored.
                shapes.add(pathShape);
            }
            previousShape = pathShape;
        }
        return shapes;
    }

    private List<IPathShape> addMoveToShapes(IPathShape pathShape, String[] pathProperties) {
        List<IPathShape> shapes = new ArrayList<>();
        int argumentCount = 2;
        String[] shapeCoordinates = getShapeCoordinates(pathShape, null, Arrays.copyOfRange(pathProperties, 1, 3));
        zOperator = new ClosePath(pathShape.isRelative());
        zOperator.setCoordinates(shapeCoordinates, currentPoint);
        pathShape.setCoordinates(shapeCoordinates, currentPoint);
        currentPoint = pathShape.getEndingPoint();
        shapes.add(pathShape);
        IPathShape previousShape = pathShape;
        if (pathProperties.length > 3) {
            for (int index = 3; index < pathProperties.length; index += argumentCount) {
                if (index + 2 > pathProperties.length) {
                    break;
                }
                pathShape = pathShape.isRelative() ? SvgPathShapeFactory.createPathShape("l") : SvgPathShapeFactory.createPathShape("L");
                shapeCoordinates = getShapeCoordinates(pathShape, previousShape, Arrays.copyOfRange(pathProperties, index, index + 2));
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
     * Each individual operator is passed to {@link PathSvgNodeRenderer#processPathOperator(String[], IPathShape)} to be processed individually.
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
        return SvgRegexUtils.containsAtLeastOneMatch(invalidRegexPattern,attributes);
    }

    Collection<String> parsePathOperations() {
        Collection<String> result = new ArrayList<>();
        String attributes = attributesAndStyles.get(SvgConstants.Attributes.D);
        if (attributes == null) {
            throw new SvgProcessingException(SvgExceptionMessageConstant.PATH_OBJECT_MUST_HAVE_D_ATTRIBUTE);
        }
        if (containsInvalidAttributes(attributes)) {
            throw new SvgProcessingException(SvgLogMessageConstant.INVALID_PATH_D_ATTRIBUTE_OPERATORS).setMessageParams(attributes);
        }

        String[] operators = splitPathStringIntoOperators(attributes);

        for (String inst : operators) {
            String instTrim = inst.trim();
            if (!instTrim.isEmpty()) {
                char instruction = instTrim.charAt(0);
                String temp = instruction + SPACE_CHAR + instTrim.substring(1).replace(",", SPACE_CHAR).trim();
                //Do a run-through for decimal point separation
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
        //If a space or minus sign is found reset
        //If a another point is found, add an extra space on before the point
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
            } else if (c == 'e') {
                exponentSignMagnitude = true;
            }

            res.append(c);
        }
        return res.toString();
    }

    /**
     * Gets an array of strings representing operators with their arguments, e.g. {"M 100 100", "L 300 100", "L200, 300", "z"}
     */
    static String[] splitPathStringIntoOperators(String path) {
        return SPLIT_PATTERN.split(path);
    }

    private static boolean endsWithNonWhitespace(StringBuilder sb) {
        return sb.length() > 0 && !Character.isWhitespace(sb.charAt(sb.length() - 1));
    }
}
