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
package com.itextpdf.forms.xfdf;

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

final class XfdfObjectUtils {

    private XfdfObjectUtils() {
    }

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
            return new Rectangle(Float.parseFloat(coordsList.get(0)), Float.parseFloat(coordsList.get(1)),
                    Float.parseFloat(coordsList.get(2)), Float.parseFloat(coordsList.get(3)));
        }

        return null;
    }

    static PdfArray convertFringeFromString(String fringeString) {
        String delims = ",";
        StringTokenizer st = new StringTokenizer(fringeString, delims);
        List<String> fringeList = new ArrayList<>();

        while (st.hasMoreTokens()) {
            fringeList.add(st.nextToken());
        }
        float[] fringe = new float[4];

         if (fringeList.size() == 4) {
             for(int i = 0; i < 4; i++) {
                 fringe[i] = Float.parseFloat(fringeList.get(i));
             }
        }

        return new PdfArray(fringe);
    }

    static String convertRectToString(Rectangle rect) {
        return convertFloatToString(rect.getX()) + ", "  +
                convertFloatToString(rect.getY()) + ", " +
                convertFloatToString((rect.getX() + rect.getWidth())) + ", " +
                convertFloatToString((rect.getY() + rect.getHeight()));
    }

    static String convertFloatToString(float coord) {
        return new String(ByteUtils.getIsoBytes(coord));
    }

    static float [] convertQuadPointsFromCoordsString(String coordsString) {
        String delims = ",";
        StringTokenizer st = new StringTokenizer(coordsString, delims);
        List<String> quadPointsList = new ArrayList<>();

        while (st.hasMoreTokens()) {
            quadPointsList.add(st.nextToken());
        }

        if (quadPointsList.size() == 8) {
            float [] quadPoints = new float [8];
            for (int i = 0; i < 8; i++) {
                quadPoints[i] = Float.parseFloat(quadPointsList.get(i));
            }
            return quadPoints;
        }
        return new float[0];
    }

    static String convertQuadPointsToCoordsString(float [] quadPoints) {
        StringBuilder stb = new StringBuilder(floatToPaddedString(quadPoints[0]));

        for (int i = 1; i < 8; i++) {
            stb.append(", ").append(floatToPaddedString(quadPoints[i]));
        }
        return stb.toString();
    }

    private static String floatToPaddedString(float number) {
        return new String(ByteUtils.getIsoBytes(number));
    }

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

        for(String flag : flagsList) {
            if (flagMap.containsKey(flag)) {
                result += (int) flagMap.get(flag);//implicit cast  for autoporting
            }
        }
        return result;
    }

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

        for(String flag : flagsList) {
            stb.append(flag).append(",");
        }

        String result = stb.toString();
        return result.length() > 0 ? result.substring(0, result.length() - 1) : null;
    }

    static String convertColorToString(float[] colors) {
        if (colors.length == 3) {
            return "#" + convertColorFloatToHex(colors[0]) + convertColorFloatToHex(colors[1]) + convertColorFloatToHex(colors[2]);
        }
        return null;
    }

    static String convertColorToString(Color color) {
        float[] colors = color.getColorValue();
        if (colors != null &&colors.length == 3) {
            return "#" + convertColorFloatToHex(colors[0]) + convertColorFloatToHex(colors[1]) + convertColorFloatToHex(colors[2]);
        }
        return null;
    }

    private static String convertColorFloatToHex(float colorFloat) {
        String result = "0" + Integer.toHexString(((int)(colorFloat*255 + 0.5))).toUpperCase();
        return result.substring(result.length() - 2);
    }

    static String convertIdToHexString(String stringId) {
        StringBuilder stb=  new StringBuilder();
        char[] stringSymbols = stringId.toCharArray();
        for(char ch : stringSymbols) {
            stb.append(Integer.toHexString((int)ch).toUpperCase());
        }
        return stb.toString();
    }

    static Color convertColorFromString(String hexColor) {
        return Color.makeColor(new PdfDeviceCs.Rgb(), convertColorFloatsFromString(hexColor));
    }

    static float[] convertColorFloatsFromString(String colorHexString){
        float[] result = new float[3];
        String colorString = colorHexString.substring(colorHexString.indexOf('#') + 1);
        if (colorString.length() == 6) {
            for (int i = 0; i < 3; i++) {
                result[i] = Integer.parseInt(colorString.substring(i * 2, 2 + i * 2), 16);
            }
        }
        return result;
    }

//    public static Map<Stream, Stream>  preprocessXfdf(String destFolder, String outPdf, String cmpFolder, String cmpPdf, String outTag, String cmpTag) throws ParserConfigurationException, IOException, SAXException, TransformerException {
//
//        InputStream outXfdfStream = new FileInputStream(destFolder + outPdf);
//        Document outDoc = XfdfFileUtils.createXfdfDocumentFromStream(outXfdfStream);
//
//        InputStream cmpXfdfStream = new FileInputStream(cmpFolder + cmpPdf);
//        Document cmpDoc = XfdfFileUtils.createXfdfDocumentFromStream(cmpXfdfStream);
//
//        NodeList excludedNodes = cmpDoc.getElementsByTagName(cmpTag);
//        int length = excludedNodes.getLength();
//        List<Node> parentNodes = new ArrayList<>();
//
//        for (int i = length - 1; i >= 0; i--) {
//            Node parentNode = excludedNodes.item(i).getParentNode();
//            parentNodes.add(parentNode);
//            parentNode.removeChild(excludedNodes.item(i));
//        }
//
//        //can just implement contents-richtext and forget about this piece of code
//        NodeList nodesToRemove = outDoc.getElementsByTagName(outTag);
//
//        for (int i = nodesToRemove.getLength() - 1; i >= 0; i--) {
//            Node parentNode = nodesToRemove.item(i).getParentNode();
//            for (Node node : parentNodes) {
//                if (node.getNodeName().equalsIgnoreCase(parentNode.getNodeName())) {
//                    parentNode.removeChild(nodesToRemove.item(i));
//                    break;
//                }
//            }
//        }
//
//        //write xmls
//        Map<OutputStream, OutputStream> cmpMap = new HashMap<>();
//        cmpMap.put(new FileOutputStream(destFolder + outPdf.substring(0, outPdf.indexOf('.')) + "_preprocessed.xfdf"),
//                new FileOutputStream(cmpFolder + cmpPdf.substring(0, cmpPdf.indexOf('.')) + "_preprocessed.xfdf"));
//        XfdfFileUtils.saveXfdfDocumentToFile(outDoc, );
//        XfdfFileUtils.saveXfdfDocumentToFile(cmpDoc, );
//        return cmpMap;
//    }

    static String convertVerticesToString(float[] vertices) {
        if (vertices.length <= 0) {
            return null;
        }
        StringBuilder stb = new StringBuilder();
        stb.append(vertices[0]);
        for (int i = 1; i < vertices.length; i++) {
            stb.append(", ").append(vertices[i]);
        }
        return stb.toString();
    }

    static String convertFringeToString(float[] fringeArray) {
        if (fringeArray.length != 4) {
            return null;
        }
        StringBuilder stb = new StringBuilder();
        stb.append(fringeArray[0]);
        for (int i = 1; i < 4; i++) {
            stb.append(", ").append(fringeArray[i]);
        }
        return stb.toString();
    }

    static float[] convertVerticesFromString(String verticesString) {
        String delims = ",;";
        StringTokenizer st = new StringTokenizer(verticesString, delims);
        List<String> verticesList = new ArrayList<>();

        while (st.hasMoreTokens()) {
            verticesList.add(st.nextToken());
        }
        float [] vertices = new float[verticesList.size()] ;
        for (int i = 0; i < verticesList.size(); i++) {
            vertices[i] = Float.parseFloat(verticesList.get(i));
        }
        return vertices;
    }

    static String convertLineStartToString(float [] line) {
        if (line.length == 4) {
            return line[0] + "," + line[1];
        }
        return null;
    }

    static String convertLineEndToString(float [] line) {
        if (line.length == 4) {
            return line[2] + "," + line[3];
        }
        return null;
    }

//    static float [] convertLineFromStrings(String start, String end) {
//        if (start == null || end == null) {
//           return new float[0];
//        }
//        float [] resultLine = new float [4];
//        String delims = ",";
//        List<String> verticesList = new ArrayList<>();
//        StringTokenizer stStart = new StringTokenizer(start, delims);
//        StringTokenizer stEnd = new StringTokenizer(end, delims);
//
//        while (stStart.hasMoreTokens()) {
//            verticesList.add(stStart.nextToken());
//        }
//        while (stEnd.hasMoreTokens()) {
//            verticesList.add(stEnd.nextToken());
//        }
//        if (verticesList.size() != 4) {
//            return new float[0];
//        } else {
//            for(int i = 0; i < 4; i++) {
//                resultLine[i] = Float.parseFloat(verticesList.get(i));
//            }
//            return resultLine;
//        }
//    }
}
