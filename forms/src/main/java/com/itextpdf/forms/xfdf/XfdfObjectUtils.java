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
package com.itextpdf.forms.xfdf;

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFreeTextAnnotation;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

final class XfdfObjectUtils {

    private XfdfObjectUtils() {
    }

    /**
     * Converts a string containing 2 or 4 float values into a {@link Rectangle}.
     *
     * @param rectString the annotation rectangle, defining the location of the annotation on the page
     *                   in default user space units. The value is four comma separated real numbers
     *                   which may be positive or negative: (xLeft, yBottom, xRight, yTop). If only two coordinates
     *                   are present, they should represent {@link Rectangle} width and height.
     */
    static Rectangle convertRectFromString(String rectString) {
        String delims = ",";
        StringTokenizer st = new StringTokenizer(rectString, delims);
        List<String> coordsList = new ArrayList<>();

        while (st.hasMoreTokens()) {
            coordsList.add(st.nextToken());
        }

        if (coordsList.size() == 2) {
            return new Rectangle(Float.parseFloat(coordsList.get(0)), Float.parseFloat(coordsList.get(1)));
        } else if (coordsList.size() == 4) {
            float xLeft = Float.parseFloat(coordsList.get(0));
            float yBottom = Float.parseFloat(coordsList.get(1));
            float width = Float.parseFloat(coordsList.get(2)) - xLeft;
            float height = Float.parseFloat(coordsList.get(3)) - yBottom;
            return new Rectangle(xLeft, yBottom, width, height);
        }

        return null;
    }

    /**
     * Converts a string containing 4 float values into a PdfArray, representing rectangle fringe.
     * If the number of floats in the string is not equal to 4, returns and PdfArray with empty values.
     */
    static PdfArray convertFringeFromString(String fringeString) {
        String[] fringeList = fringeString.split(",");
        float[] fringe = new float[4];
        if (fringeList.length == 4) {
            for (int i = 0; i < 4; i++) {
                fringe[i] = Float.parseFloat(fringeList[i]);
            }
        }
        return new PdfArray(fringe);
    }

    /**
     * Converts a string containing float values into a PdfArray, representing a pattern of dashes and gaps to be used
     * in drawing a dashed border.
     */
    static PdfArray convertDashesFromString(String dashesString) {
        String[] dashesList = dashesString.split(",");
        float[] dashes = new float[dashesList.length];
        for (int i = 0; i < dashesList.length; i++) {
            dashes[i] = Float.parseFloat(dashesList[i]);
        }
        return new PdfArray(dashes);
    }

    /**
     * Converts a PdfArray, representing a pattern of dashes and gaps to be used in drawing a dashed border,
     * into a string containing float values.
     */
    static PdfString convertDashesFromArray(PdfArray dashesArray) {
        if (dashesArray == null) {
            return null;
        }
        String delims = ",";
        StringBuilder dashes = new StringBuilder();
        for (int i = 0; i < dashesArray.size() - 1; i++) {
            dashes.append(convertFloatToString(((PdfNumber) dashesArray.get(i)).floatValue())).append(delims);
        }
        dashes.append(convertFloatToString(((PdfNumber) dashesArray.get(dashesArray.size() - 1)).floatValue()));

        return new PdfString(dashes.toString());
    }

    /**
     * Converts a string containing justification value into an integer value representing a code specifying
     * the form of quadding (justification).
     */
    static int convertJustificationFromStringToInteger(String attributeValue) {
        if ("centered".equalsIgnoreCase(attributeValue)) {
            return PdfFreeTextAnnotation.CENTERED;
        }
        if ("right".equalsIgnoreCase(attributeValue)) {
            return PdfFreeTextAnnotation.RIGHT_JUSTIFIED;
        }
        return PdfFreeTextAnnotation.LEFT_JUSTIFIED;
    }

    /**
     * Converts an integer value representing a code specifying the form of quadding (justification) into a string
     * containing justification value.
     */
    static String convertJustificationFromIntegerToString(int justification) {
        if (PdfFreeTextAnnotation.CENTERED == justification) {
            return "centered";
        }
        if (PdfFreeTextAnnotation.RIGHT_JUSTIFIED == justification) {
            return "right";
        }
        return "left";
    }

    /**
     * Converts H key value in the link annotation dictionary to Highlight value of xfdf link annotation attribute.
     */
    static PdfName getHighlightFullValue(PdfName highlightMode) {
        if (highlightMode == null) {
            return null;
        }
        switch (highlightMode.toString().substring(1)) {
            case "N":
                return new PdfName("None");
            case "I":
                return new PdfName("Invert");
            case "O":
                return new PdfName("Outline");
            case "P":
                return new PdfName("Push");
            default:
                return null;
        }
    }

    /**
     * Converts style (S key value) in the pdf annotation dictionary to style value of xfdf annotation attribute.
     */
    static PdfName getStyleFullValue(PdfName style) {
        if (style == null) {
            return null;
        }
        switch (style.toString().substring(1)) {
            case "S":
                return new PdfName("solid");
            case "D":
                return new PdfName("dash");
            case "B":
                return new PdfName("bevelled");
            case "I":
                return new PdfName("inset");
            case "U":
                return new PdfName("underline");
            case "C":
                return new PdfName("cloudy");
            default:
                return null;
        }
    }

    /**
     * Converts a Rectangle to a string containing 4 float values.
     */
    static String convertRectToString(Rectangle rect) {
        return convertFloatToString(rect.getX()) + ", " +
                convertFloatToString(rect.getY()) + ", " +
                convertFloatToString((rect.getX() + rect.getWidth())) + ", " +
                convertFloatToString((rect.getY() + rect.getHeight()));
    }

    /**
     * Converts float value to string with UTF-8 encoding.
     */
    static String convertFloatToString(float coord) {
        return new String(ByteUtils.getIsoBytes(coord), StandardCharsets.UTF_8);
    }

    /**
     * Converts a string containing 8*n float values into a float array, representing quadPoints.
     * If the number of floats in the string is not a multiple of 8, returns an empty float array.
     */
    static float[] convertQuadPointsFromCoordsString(String coordsString) {
        String delims = ",";
        StringTokenizer st = new StringTokenizer(coordsString, delims);
        List<String> quadPointsList = new ArrayList<>();

        while (st.hasMoreTokens()) {
            quadPointsList.add(st.nextToken());
        }

        if (quadPointsList.size() % 8 == 0) {
            float[] quadPoints = new float[quadPointsList.size()];
            for (int i = 0; i < quadPointsList.size(); i++) {
                quadPoints[i] = Float.parseFloat(quadPointsList.get(i));
            }
            return quadPoints;
        }
        return new float[0];
    }

    /**
     * Converts a float array, representing quadPoints into a string containing 8*n float values.
     */
    static String convertQuadPointsToCoordsString(float[] quadPoints) {
        StringBuilder stb = new StringBuilder(convertFloatToString(quadPoints[0]));

        for (int i = 1; i < quadPoints.length; i++) {
            stb.append(", ").append(convertFloatToString(quadPoints[i]));
        }
        return stb.toString();
    }

    /**
     * Converts a string containing a comma separated list of names of the flags into an integer representation
     * of the flags.
     */
    static int convertFlagsFromString(String flagsString) {
        int result = 0;

        String delims = ",";
        StringTokenizer st = new StringTokenizer(flagsString, delims);
        List<String> flagsList = new ArrayList<>();
        while (st.hasMoreTokens()) {
            flagsList.add(st.nextToken().toLowerCase());
        }

        Map<String, Integer> flagMap = new HashMap<>();
        flagMap.put(XfdfConstants.INVISIBLE, PdfAnnotation.INVISIBLE);
        flagMap.put(XfdfConstants.HIDDEN, PdfAnnotation.HIDDEN);
        flagMap.put(XfdfConstants.PRINT, PdfAnnotation.PRINT);
        flagMap.put(XfdfConstants.NO_ZOOM, PdfAnnotation.NO_ZOOM);
        flagMap.put(XfdfConstants.NO_ROTATE, PdfAnnotation.NO_ROTATE);
        flagMap.put(XfdfConstants.NO_VIEW, PdfAnnotation.NO_VIEW);
        flagMap.put(XfdfConstants.READ_ONLY, PdfAnnotation.READ_ONLY);
        flagMap.put(XfdfConstants.LOCKED, PdfAnnotation.LOCKED);
        flagMap.put(XfdfConstants.TOGGLE_NO_VIEW, PdfAnnotation.TOGGLE_NO_VIEW);

        for (String flag : flagsList) {
            if (flagMap.containsKey(flag)) {
                //implicit cast  for autoporting
                result += (int) flagMap.get(flag);
            }
        }
        return result;
    }

    /**
     * Converts an integer representation of the flags into a string with a comma separated list of names of the flags.
     */
    static String convertFlagsToString(PdfAnnotation pdfAnnotation) {
        List<String> flagsList = new ArrayList<>();
        StringBuilder stb = new StringBuilder();

        if (pdfAnnotation.hasFlag(PdfAnnotation.INVISIBLE)) {
            flagsList.add(XfdfConstants.INVISIBLE);
        }
        if (pdfAnnotation.hasFlag(PdfAnnotation.HIDDEN)) {
            flagsList.add(XfdfConstants.HIDDEN);
        }
        if (pdfAnnotation.hasFlag(PdfAnnotation.PRINT)) {
            flagsList.add(XfdfConstants.PRINT);
        }
        if (pdfAnnotation.hasFlag(PdfAnnotation.NO_ZOOM)) {
            flagsList.add(XfdfConstants.NO_ZOOM);
        }
        if (pdfAnnotation.hasFlag(PdfAnnotation.NO_ROTATE)) {
            flagsList.add(XfdfConstants.NO_ROTATE);
        }
        if (pdfAnnotation.hasFlag(PdfAnnotation.NO_VIEW)) {
            flagsList.add(XfdfConstants.NO_VIEW);
        }
        if (pdfAnnotation.hasFlag(PdfAnnotation.READ_ONLY)) {
            flagsList.add(XfdfConstants.READ_ONLY);
        }
        if (pdfAnnotation.hasFlag(PdfAnnotation.LOCKED)) {
            flagsList.add(XfdfConstants.LOCKED);
        }
        if (pdfAnnotation.hasFlag(PdfAnnotation.TOGGLE_NO_VIEW)) {
            flagsList.add(XfdfConstants.TOGGLE_NO_VIEW);
        }

        for (String flag : flagsList) {
            stb.append(flag).append(",");
        }

        String result = stb.toString();
        return result.length() > 0 ? result.substring(0, result.length() - 1) : null;
    }

    /**
     * Converts an array of 3 floats into a hex string representing the rgb color.
     */
    static String convertColorToString(float[] colors) {
        if (colors.length == 3) {
            return "#" + convertColorFloatToHex(colors[0]) + convertColorFloatToHex(colors[1]) + convertColorFloatToHex(colors[2]);
        }
        return null;
    }

    /**
     * Converts a Color object into a hex string representing the rgb color.
     */
    static String convertColorToString(Color color) {
        float[] colors = color.getColorValue();
        if (colors != null && colors.length == 3) {
            return "#" + convertColorFloatToHex(colors[0]) + convertColorFloatToHex(colors[1]) + convertColorFloatToHex(colors[2]);
        }
        return null;
    }

    /**
     * Converts float representation of the rgb color into a hex string representing the rgb color.
     */
    private static String convertColorFloatToHex(float colorFloat) {
        String result = "0" + Integer.toHexString(((int) (colorFloat * 255 + 0.5))).toUpperCase();
        return result.substring(result.length() - 2);
    }

    /**
     * Converts string containing id from decimal to hexadecimal format.
     */
    static String convertIdToHexString(String idString) {
        StringBuilder stb = new StringBuilder();
        char[] stringSymbols = idString.toCharArray();
        for (char ch : stringSymbols) {
            stb.append(Integer.toHexString((int) ch).toUpperCase());
        }
        return stb.toString();
    }

    /**
     * Converts string containing hex color code to Color object.
     */
    static Color convertColorFromString(String hexColor) {
        return Color.makeColor(new PdfDeviceCs.Rgb(), convertColorFloatsFromString(hexColor));
    }

    /**
     * Converts string containing hex color code into an array of 3 integer values representing rgb color.
     */
    static float[] convertColorFloatsFromString(String colorHexString) {
        float[] result = new float[3];
        String colorString = colorHexString.substring(colorHexString.indexOf('#') + 1);
        if (colorString.length() == 6) {
            for (int i = 0; i < 3; i++) {
                result[i] = Integer.parseInt(colorString.substring(i * 2, 2 + i * 2), 16) / 255f;
            }
        }
        return result;
    }

    /**
     * Converts an array of float vertices to string.
     */
    static String convertVerticesToString(float[] vertices) {
        if (vertices.length <= 0) {
            return null;
        }
        StringBuilder stb = new StringBuilder();
        stb.append(convertFloatToString(vertices[0]));
        for (int i = 1; i < vertices.length; i++) {
            stb.append(", ").append(convertFloatToString(vertices[i]));
        }
        return stb.toString();
    }

    /**
     * Converts to string an array of floats representing the fringe.
     * If the number of floats doesn't equal 4, an empty string is returned.
     */
    static String convertFringeToString(float[] fringeArray) {
        if (fringeArray.length != 4) {
            return null;
        }
        StringBuilder stb = new StringBuilder();
        stb.append(convertFloatToString(fringeArray[0]));
        for (int i = 1; i < 4; i++) {
            stb.append(", ").append(convertFloatToString(fringeArray[i]));
        }
        return stb.toString();
    }

    /**
     * Converts a string into an array of floats representing vertices.
     */
    static float[] convertVerticesFromString(String verticesString) {
        String delims = ",;";
        StringTokenizer st = new StringTokenizer(verticesString, delims);
        List<String> verticesList = new ArrayList<>();

        while (st.hasMoreTokens()) {
            verticesList.add(st.nextToken());
        }
        float[] vertices = new float[verticesList.size()];
        for (int i = 0; i < verticesList.size(); i++) {
            vertices[i] = Float.parseFloat(verticesList.get(i));
        }
        return vertices;
    }

    /**
     * Returns a string representation of the start point of the line (x_1, y_1) based on given line array.
     * If the line array doesn't contain 4 floats, returns an empty string.
     *
     * @param line an array of 4 floats representing the line (x_1, y_1, x_2, y_2)
     */
    static String convertLineStartToString(float[] line) {
        if (line.length == 4) {
            return convertFloatToString(line[0]) + "," + convertFloatToString(line[1]);
        }
        return null;
    }

    /**
     * Returns a string representation of the end point of the line (x_2, y_2) based on given line array.
     * If the line array doesn't contain 4 floats, returns an empty string.
     *
     * @param line an array of 4 floats representing the line (x_1, y_1, x_2, y_2)
     */
    static String convertLineEndToString(float[] line) {
        if (line.length == 4) {
            return convertFloatToString(line[2]) + "," + convertFloatToString(line[3]);
        }
        return null;
    }
}
