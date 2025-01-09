/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
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
            throw new SvgProcessingException(SvgExceptionMessageConstant.TRANSFORM_NULL);
        }

        if (transform.isEmpty()) {
            throw new SvgProcessingException(SvgExceptionMessageConstant.TRANSFORM_EMPTY);
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
                if (trim.startsWith(",")) {
                    trim = trim.substring(1).trim();
                }
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
            throw new SvgProcessingException(SvgExceptionMessageConstant.INVALID_TRANSFORM_DECLARATION);
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
                throw new SvgProcessingException(SvgExceptionMessageConstant.UNKNOWN_TRANSFORMATION_TYPE);
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
            throw new SvgProcessingException(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        double tan = Math.tan(Math.toRadians((float) CssDimensionParsingUtils.parseFloat(values.get(0))));

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
            throw new SvgProcessingException(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        double tan = Math.tan(Math.toRadians((float) CssDimensionParsingUtils.parseFloat(values.get(0))));

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
            throw new SvgProcessingException(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        double angle = Math.toRadians((float) CssDimensionParsingUtils.parseFloat(values.get(0)));

        if (values.size() == 3) {
            float centerX = CssDimensionParsingUtils.parseAbsoluteLength(values.get(1));
            float centerY = CssDimensionParsingUtils.parseAbsoluteLength(values.get(2));
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
            throw new SvgProcessingException(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        float scaleX = CssDimensionParsingUtils.parseRelativeValue(values.get(0), 1);
        float scaleY = values.size() == 2 ? CssDimensionParsingUtils.parseRelativeValue(values.get(1), 1) : scaleX;

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
            throw new SvgProcessingException(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        float translateX = CssDimensionParsingUtils.parseAbsoluteLength(values.get(0));
        float translateY = values.size() == 2 ? CssDimensionParsingUtils.parseAbsoluteLength(values.get(1)) : 0;

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
            throw new SvgProcessingException(SvgExceptionMessageConstant.TRANSFORM_INCORRECT_NUMBER_OF_VALUES);
        }

        float a = (float) Float.parseFloat(values.get(0));
        float b = (float) Float.parseFloat(values.get(1));
        float c = (float) Float.parseFloat(values.get(2));
        float d = (float) Float.parseFloat(values.get(3));
        float e = CssDimensionParsingUtils.parseAbsoluteLength(values.get(4));
        float f = CssDimensionParsingUtils.parseAbsoluteLength(values.get(5));

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
            throw new SvgProcessingException(SvgExceptionMessageConstant.INVALID_TRANSFORM_DECLARATION);
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
