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
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utility class responsible for converting Strings containing transformation declarations
 * into AffineTransform objects.
 * <p>
 * This class only supports the transformations as described in the SVG specification:
 * - matrix
 * - rotate
 * - scale
 * - skewX
 * - skewY
 * - translate
 */
public final class TransformUtils {

    /**
     * Keyword for matrix transformations. Accepts 6 values.
     * <p>
     * matrix(0 1 2 3 4 5)
     */
    private static final String MATRIX = "MATRIX";

    /**
     * Keyword for rotation transformation. Accepts either 1 or 3 values.
     * In the case of 1 value, x and y are assumed to be the origin of the user space.
     * <p>
     * rotate(angle x y)
     * rotate(angle)
     */
    private static final String ROTATE = "ROTATE";

    /**
     * Keyword for scale transformation. Accepts either 1 or 2 values.
     * In the case of 1 value, the second value is assumed to be the same as the first one.
     * <p>
     * scale(x y)
     * scale(x)
     */
    private static final String SCALE = "SCALE";

    /**
     * Keyword for skewX transformation. Accepts 1 value.
     * <p>
     * skewX(angle)
     */
    private static final String SKEWX = "SKEWX";

    /**
     * Keyword for skewY transformation. Accepts 1 value.
     * <p>
     * skewY(angle)
     */
    private static final String SKEWY = "SKEWY";

    /**
     * Keyword for translate transformation. Accepts either 1 or 2 values.
     * In the case of 1 value, the y value is assumed to be 0.
     * <p>
     * translate(x y)
     * translate(x)
     */
    private static final String TRANSLATE = "TRANSLATE";

    private TransformUtils() {
    }

    /**
     * Converts a string containing a transform declaration into an AffineTransform object.
     * This class only supports the transformations as described in the SVG specification:
     * - matrix
     * - translate
     * - skewx
     * - skewy
     * - rotate
     * - scale
     *
     * @param transform value to be parsed
     * @return the AffineTransform object
     */
    public static AffineTransform parseTransform(String transform) {
        if (transform == null) {
            throw new SvgProcessingException(SvgLogMessageConstant.TRANSFORM_NULL);
        }

        if (transform.isEmpty()) {
            throw new SvgProcessingException(SvgLogMessageConstant.TRANSFORM_EMPTY);
        }

        AffineTransform matrix = new AffineTransform();

        List<String> listWithTransformations = splitString(transform);

        for (String transformation : listWithTransformations) {
            AffineTransform newMatrix = transformationStringToMatrix(transformation);

            if (newMatrix != null) {
                matrix.concatenate(newMatrix);
            }
        }

        return matrix;
    }

    /**
     * A transformation attribute can encompass multiple transformation operation (e.g. "translate(10,20) scale(30,40)".
     * This method splits the original transformation string into multiple strings so that they can be handled separately.
     *
     * @param transform the transformation value
     * @return a list containing strings describing a single transformation operation
     */
    private static List<String> splitString(String transform) {
        ArrayList<String> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(transform, ")", false);

        while (tokenizer.hasMoreTokens()) {
            String trim = tokenizer.nextToken().trim();

            if (trim != null && !trim.isEmpty()) {
                list.add(trim + ")");
            }
        }

        return list;
    }

    /**
     * This method decides which transformation operation the given transformation strings maps onto.
     *
     * @param transformation string containing a transformation operation
     * @return the mapped AffineTransform object
     */
    private static AffineTransform transformationStringToMatrix(String transformation) {
        String name = getNameFromString(transformation).toUpperCase();

        if (name.isEmpty()) {
            throw new SvgProcessingException(SvgLogMessageConstant.INVALID_TRANSFORM_DECLARATION);
        }
        switch (name) {
        case MATRIX:
            return createMatrixTransformation(getValuesFromTransformationString(transformation));
        case TRANSLATE:
            return createTranslateTransformation(getValuesFromTransformationString(transformation));
        case SCALE:
            return createScaleTransformation(getValuesFromTransformationString(transformation));
        case ROTATE:
            return createRotationTransformation(getValuesFromTransformationString(transformation));
        case SKEWX:
            return createSkewXTransformation(getValuesFromTransformationString(transformation));
        case SKEWY:
            return createSkewYTransformation(getValuesFromTransformationString(transformation));
        default:
            throw new SvgProcessingException(SvgLogMessageConstant.UNKNOWN_TRANSFORMATION_TYPE);
        }
    }

    /**
     * Creates a skewY transformation.
     *
     * @param values values of the transformation
     * @return AffineTransform for the skew operation
     */
    private static AffineTransform createSkewYTransformation(List<String> values) {
        if (values.size() != 1) {
            throw new SvgProcessingException(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        double tan = Math.tan(Math.toRadians((float) CssUtils.parseFloat(values.get(0))));

        //Differs from the notation in the PDF-spec for skews
        return new AffineTransform(1, tan, 0, 1, 0, 0);
    }

    /**
     * Creates a skewX transformation.
     *
     * @param values values of the transformation
     * @return AffineTransform for the skew operation
     */
    private static AffineTransform createSkewXTransformation(List<String> values) {
        if (values.size() != 1) {
            throw new SvgProcessingException(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        double tan = Math.tan(Math.toRadians((float) CssUtils.parseFloat(values.get(0))));

        //Differs from the notation in the PDF-spec for skews
        return new AffineTransform(1, 0, tan, 1, 0, 0);
    }

    /**
     * Creates a rotate transformation.
     *
     * @param values values of the transformation
     * @return AffineTransform for the rotate operation
     */
    private static AffineTransform createRotationTransformation(List<String> values) {
        if (values.size() != 1 && values.size() != 3) {
            throw new SvgProcessingException(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        double angle = Math.toRadians((float) CssUtils.parseFloat(values.get(0)));

        if (values.size() == 3) {
            float centerX = CssUtils.parseAbsoluteLength(values.get(1));
            float centerY = CssUtils.parseAbsoluteLength(values.get(2));
            return AffineTransform.getRotateInstance(angle, centerX, centerY);
        }

        return AffineTransform.getRotateInstance(angle);
    }

    /**
     * Creates a scale transformation.
     *
     * @param values values of the transformation
     * @return AffineTransform for the scale operation
     */
    private static AffineTransform createScaleTransformation(List<String> values) {
        if (values.size() == 0 || values.size() > 2) {
            throw new SvgProcessingException(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        float scaleX = CssUtils.parseRelativeValue(values.get(0), 1);
        float scaleY = values.size() == 2 ? CssUtils.parseRelativeValue(values.get(1), 1) : scaleX;

        return AffineTransform.getScaleInstance(scaleX, scaleY);
    }

    /**
     * Creates a translate transformation.
     *
     * @param values values of the transformation
     * @return AffineTransform for the translate operation
     */
    private static AffineTransform createTranslateTransformation(List<String> values) {
        if (values.size() == 0 || values.size() > 2) {
            throw new SvgProcessingException(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        float translateX = CssUtils.parseAbsoluteLength(values.get(0));
        float translateY = values.size() == 2 ? CssUtils.parseAbsoluteLength(values.get(1)) : 0;

        return AffineTransform.getTranslateInstance(translateX, translateY);
    }

    /**
     * Creates a matrix transformation.
     *
     * @param values values of the transformation
     * @return AffineTransform for the matrix keyword
     */
    private static AffineTransform createMatrixTransformation(List<String> values) {
        if (values.size() != 6) {
            throw new SvgProcessingException(SvgLogMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        float a = CssUtils.parseAbsoluteLength(values.get(0));
        float b = CssUtils.parseAbsoluteLength(values.get(1));
        float c = CssUtils.parseAbsoluteLength(values.get(2));
        float d = CssUtils.parseAbsoluteLength(values.get(3));
        float e = CssUtils.parseAbsoluteLength(values.get(4));
        float f = CssUtils.parseAbsoluteLength(values.get(5));

        return new AffineTransform(a, b, c, d, e, f);
    }

    /**
     * This method extracts the transformation name given a transformation.
     *
     * @param transformation the transformation
     * @return the name of the transformation
     */
    private static String getNameFromString(String transformation) {
        int indexOfParenthesis = transformation.indexOf("(");

        if (indexOfParenthesis == -1) {
            throw new SvgProcessingException(SvgLogMessageConstant.INVALID_TRANSFORM_DECLARATION);
        }

        return transformation.substring(0, transformation.indexOf("("));
    }

    /**
     * This method extracts the values from a transformation.
     *
     * @param transformation the transformation
     * @return values of the transformation
     */
    private static List<String> getValuesFromTransformationString(String transformation) {
        String numbers = transformation.substring(transformation.indexOf('(') + 1, transformation.indexOf(')'));

        return SvgCssUtils.splitValueList(numbers);
    }
}
