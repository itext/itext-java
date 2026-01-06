/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The PageResizer class provides functionality to resize PDF pages to a specified
 * target page size using various resizing methods. It adjusts page dimensions,
 * content, annotations, and resources accordingly, also supports configuration
 * options for maintaining the aspect ratio during the resize operation.
 */
public class PageResizer {
    private static final float EPSILON = 1e-6f;
    private static final int NUMBER_FORMAT_PRECISION = 10000;
    // Operators we care about in DA:
    //   Tf  => operands: /FontName <fontSize>  (scale the <fontSize> only)
    //   TL  => operand: <leading>              (scale)
    //   Tc  => operand: <charSpacing>          (scale)
    //   Tw  => operand: <wordSpacing>          (scale)
    //   Ts  => operand: <textRise>             (scale)
    //
    // Operators we intentionally do NOT scale:
    //   Tz (horizontal scaling, percentage), Tr (rendering mode),
    //   color ops (g/rg/k/G/RG/K), etc.
    private static final Set<String> SCALABLE_DA_OPERATORS = new HashSet<>(Arrays.asList(
            "Tf", "TL", "Tc", "Tw", "Ts"));
    // A list of single-value CSS properties with length values that should be scaled.
    // Based on a PDF spec for Rich Text strings 12.7.3.4 (Table 255) plus other common properties from CSS 1/2.
    // Shorthand properties like "padding" or "margin" seem to be unsupported by Acrobat.
    private static final Set<String> SCALABLE_RC_PROPERTIES = new HashSet<>(Arrays.asList(
            "font-size", "line-height", "text-indent",
            "padding-top", "padding-right", "padding-bottom", "padding-left",
            "margin-top", "margin-right", "margin-bottom", "margin-left",
            "border-top-width", "border-right-width", "border-bottom-width", "border-left-width",
            "width", "height", "letter-spacing", "word-spacing"));
    private static final Set<String> SCALABLE_UNITS = new HashSet<>(Arrays.asList("pt", "pc", "in", "cm", "mm", "px"));

    private final PageSize size;
    private VerticalAnchorPoint verticalAnchorPoint = VerticalAnchorPoint.CENTER;
    private HorizontalAnchorPoint horizontalAnchorPoint = HorizontalAnchorPoint.CENTER;
    private final ResizeType type;

    /**
     * Constructs a new PageResizer instance with the specified page size and resize type.
     *
     * @param size the target page size to which the content should be resized
     * @param type the resizing method to be applied, such as maintaining the aspect ratio
     */
    public PageResizer(PageSize size, ResizeType type) {
        this.size = size;
        this.type = type;
    }

    /**
     * Retrieves the horizontal anchor point of the PageResizer.
     *
     * @return the horizontal anchor point, which determines the horizontal alignment (e.g., LEFT, CENTER, RIGHT).
     */
    public HorizontalAnchorPoint getHorizontalAnchorPoint() {
        return horizontalAnchorPoint;
    }

    /**
     * Sets the horizontal anchor point, which determines how the horizontal alignment is handled
     * (e.g., LEFT, CENTER, RIGHT).
     *
     * @param anchorPoint the horizontal anchor point to set; it specifies the horizontal alignment type
     *                    for resizing operations
     */
    public void setHorizontalAnchorPoint(HorizontalAnchorPoint anchorPoint) {
        this.horizontalAnchorPoint = anchorPoint;
    }

    /**
     * Retrieves the vertical anchor point of the PageResizer.
     *
     * @return the vertical anchor point, which determines the vertical alignment (e.g., TOP, CENTER, BOTTOM).
     */
    public VerticalAnchorPoint getVerticalAnchorPoint() {
        return verticalAnchorPoint;
    }

    /**
     * Sets the vertical anchor point, which determines how the vertical alignment is handled
     * (e.g., TOP, CENTER, BOTTOM).
     *
     * @param anchorPoint the vertical anchor point to set; it specifies the vertical alignment type
     *                    for resizing operations
     */
    public void setVerticalAnchorPoint(VerticalAnchorPoint anchorPoint) {
        this.verticalAnchorPoint = anchorPoint;
    }


    /**
     * Resizes a given PDF page based on the specified dimensions and resize type.
     * Depending on the resize type, the aspect ratio may be maintained during scaling.
     * Updates the page's content, annotations, and resources to reflect the new size.
     *
     * @param page the PDF page to be resized
     */
    public void resize(PdfPage page) {
        if (size == null || size.getWidth() < EPSILON || size.getHeight() < EPSILON) {
            throw new IllegalArgumentException(MessageFormatUtil.format(KernelExceptionMessageConstant
                    .CANNOT_RESIZE_PAGE_WITH_NEGATIVE_OR_INFINITE_SCALE, size));
        }
        if (page == null) {
            return;
        }
        Rectangle originalPageSize = page.getMediaBox();
        double horizontalScale = size.getWidth() / originalPageSize.getWidth();
        double verticalScale = size.getHeight() / originalPageSize.getHeight();
        double horizontalFreeSpace = 0;
        double verticalFreeSpace = 0;
        if (ResizeType.MAINTAIN_ASPECT_RATIO == type) {
            double scale = Math.min(horizontalScale, verticalScale);
            horizontalScale = scale;
            verticalScale = scale;
            horizontalFreeSpace = size.getWidth() - originalPageSize.getWidth()*scale;
            verticalFreeSpace = size.getHeight() - originalPageSize.getHeight()*scale;
        }

        updateBoxes(page, originalPageSize);

        AffineTransform scalingMatrix = calculateAffineTransform(horizontalScale, verticalScale,
                horizontalFreeSpace, verticalFreeSpace);

        // Ensure resources exist to avoid NPEs when creating PdfCanvas or iterating resources
        PdfResources resources = page.getResources();
        if (resources == null) {
            resources = new PdfResources();
            page.getPdfObject().put(PdfName.Resources, resources.getPdfObject());
        }

        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), page.getDocument());
        pdfCanvas.concatMatrix(scalingMatrix);

        for (PdfName resName : page.getResources().getResourceNames()) {
            PdfPattern pattern = page.getResources().getPattern(resName);
            if (pattern != null) {
                resizePattern(page.getResources(), resName, scalingMatrix);
            }
        }

        for (PdfAnnotation annot : page.getAnnotations()) {
            resizeAnnotation(annot, scalingMatrix);
        }
    }

    /**
     * Enum representing the available types of resizing strategies when modifying the dimensions
     * of a PDF page. These strategies determine how the content is scaled relative to the new size.
     */
    public enum ResizeType {
        MAINTAIN_ASPECT_RATIO,
        DEFAULT
    }

    /**
     * Represents the vertical alignment points used for resizing or aligning elements,
     * particularly in the context of page rescaling.
     * <p>
     * The available anchor points are:
     * - TOP: The top edge of the element serves as the reference point for alignment.
     * - CENTER: The center of the element is used as the alignment reference.
     * - BOTTOM: The bottom edge of the element serves as the reference point for alignment.
     * <p>
     * This enumeration is employed by the PageResizer class to determine the vertical
     * alignment of content during resizing operations.
     */
    public enum VerticalAnchorPoint {
        TOP,
        CENTER,
        BOTTOM
    }

    /**
     * Enum representing the horizontal anchor point used in the resizing and alignment
     * of a page or content.
     * <p>
     * The horizontal anchor point specifies the horizontal alignment,
     * determining the reference point for positioning during resizing operations.
     * Possible values include:
     * - LEFT*/
    public enum HorizontalAnchorPoint {
        LEFT,
        CENTER,
        RIGHT
    }

    static String scaleDaString(String daString, double scale) {
        if (daString == null || daString.trim().isEmpty() || Math.abs(scale - 1.0) < EPSILON) {
            return daString;
        }

        List<String> tokens = new ArrayList<>(Arrays.asList(daString.trim().split("\\s+")));

        for (int i = 0; i < tokens.size(); i++) {
            if (SCALABLE_DA_OPERATORS.contains(tokens.get(i))) {
                int operandIdx = i - 1;
                while (operandIdx >= 0) {
                    String token = tokens.get(operandIdx);
                    if (token.startsWith("/")) {
                        // Skip resource names, e.g., /Helv
                        operandIdx--;
                        continue;
                    }
                    try {
                        double value = Double.parseDouble(token);
                        tokens.set(operandIdx, formatNumber(value * scale));
                        break;
                    } catch (NumberFormatException ignore) {
                        // Not a number, continue searching backwards.
                        operandIdx--;
                    }
                }
            }
        }
        return String.join(" ", tokens);
    }

    static String scaleRcString(String rcString, double scale) {
        if (isEmpty(rcString) || Math.abs(scale - 1.0) < EPSILON) {
            return rcString;
        }

        // Quick pre-check: if none of the property names appear at all, skip additional work.
        // This is a fast substring scan; false positives are fine.
        boolean containsScalable = false;
        for (String p : SCALABLE_RC_PROPERTIES) {
            if (rcString.contains(p)) {
                containsScalable = true;
                break;
            }
        }
        if (!containsScalable) {
            return rcString;
        }

        RcPropertyParser parser = new RcPropertyParser(rcString);
        StringBuilder out = new StringBuilder();
        int lastWrite = 0;

        while (parser.findNext()) {
            RcPropertyParserResult result = parser.getResult();
            double newValue = result.getParsedValue() * scale;
            String formatted = formatNumber(newValue);

            out.append(rcString.substring(lastWrite, result.getValueStart())).append(formatted);
            lastWrite = result.getValueEnd();
        }


        if (lastWrite == 0) {
            // No replacements
            return rcString;
        }
        // Append the remainder
        out.append(rcString.substring(lastWrite, rcString.length()));
        return out.toString();
    }

    /**
     * Scales the given page box dimensions from the original page size to the new page size.
     *
     * @param originalPageSize the size of the original page
     * @param newPageSize the size of the new page to scale to
     * @param box the rectangular box representing the dimensions to be scaled
     *
     * @return a new Rectangle representing the scaled dimensions of the page box
     */
    static Rectangle scalePageBox(Rectangle originalPageSize, PageSize newPageSize, Rectangle box) {
        if (originalPageSize == null || newPageSize == null || box == null) {
            return box;
        }

        float origW = originalPageSize.getWidth();
        float origH = originalPageSize.getHeight();
        float newW = newPageSize.getWidth();
        float newH = newPageSize.getHeight();

        if (origW < EPSILON || origH < EPSILON) {
            return box;
        }

        float left = box.getLeft() * newW / origW;
        float bottom = box.getBottom() * newH / origH;
        float width = box.getWidth() * newW / origW;
        float height = box.getHeight() * newH / origH;

        return new Rectangle(left, bottom, width, height);
    }

    /**
     * Resizes the appearance streams of the given PDF annotation by applying the specified affine transformation.
     * The method traverses through the annotation's appearance dictionary, locating and resizing the
     * streams or nested streams within, based on the scaling and transformation defined by the
     * affine transformation matrix.
     *
     * @param annot the PDF annotation whose appearance streams are to be resized
     * @param scalingMatrix the affine transformation matrix representing the scaling and translation
     *                      to be applied to the appearance streams
     */
    static void resizeAppearanceStreams(PdfAnnotation annot, AffineTransform scalingMatrix) {
        PdfDictionary ap = annot.getAppearanceDictionary();
        if (ap == null) {
            return;
        }
        for (PdfName key : ap.keySet()) {
            PdfObject apState = ap.get(key);
            if (apState.isStream()) {
                resizeAppearanceStream((PdfStream) apState, scalingMatrix);
            } else if (apState.isDictionary()) {
                PdfDictionary apStateDict = (PdfDictionary) apState;
                for (PdfName subKeyState : apStateDict.keySet()) {
                    PdfObject subApState = apStateDict.get(subKeyState);
                    if (subApState.isStream()) {
                        resizeAppearanceStream((PdfStream) subApState, scalingMatrix);
                    }
                }
            }
        }
    }

    /**
     * Checks if a given string is null, empty, or contains only whitespace characters.
     *
     * @param str the string to check for emptiness
     * @return true if the string is null, empty, or contains only whitespace; false otherwise
     */
    private static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Scales the transformation matrix of the provided pattern using the given scaling matrix,
     * without mutating the original pattern.
     * <p>
     * The method:
     *  - Locates the pattern object in the provided resources by name.
     *  - Deep-copies the pattern object into the same document.
     *  - Updates the /Matrix of the copied pattern.
     *  - Replaces the entry in the page's /Pattern resources with the copied (resized) pattern,
     *    so other pages that reference the original pattern remain unaffected.
     *
     * @param resources      the resource dictionary that holds the pattern
     * @param resName        the name of the pattern resource to resize
     * @param scalingMatrix  the affine transformation matrix to be applied for scaling
     */
    private static void resizePattern(PdfResources resources, PdfName resName, AffineTransform scalingMatrix) {
        if (resources == null || resName == null || scalingMatrix == null) {
            return;
        }

        PdfDictionary patternDictContainer = resources.getResource(PdfName.Pattern);
        if (patternDictContainer == null) {
            return;
        }

        PdfObject patternObj = resources.getResourceObject(PdfName.Pattern, resName);
        if (patternObj == null) {
            return;
        }

        PdfObject clonedObj = (PdfObject) patternObj.clone();
        PdfDictionary clonedPatternDict = (PdfDictionary) clonedObj;

        PdfArray existingMatrix = clonedPatternDict.getAsArray(PdfName.Matrix);
        AffineTransform newTransform;
        if (existingMatrix == null) {
            newTransform = new AffineTransform(scalingMatrix);
        } else {
            newTransform = new AffineTransform(existingMatrix.toDoubleArray());
            newTransform.preConcatenate(scalingMatrix);
        }
        double[] newMatrixArray = new double[6];
        newTransform.getMatrix(newMatrixArray);
        clonedPatternDict.put(PdfName.Matrix, new PdfArray(newMatrixArray));

        patternDictContainer.put(resName, clonedPatternDict);
    }

    /**
     * Resizes the given PDF annotation by applying the specified affine transformation.
     * This method adjusts the annotation's properties, such as its bounding box,
     * to reflect the transformation based on the scaling and translation defined
     * in the affine transformation matrix.
     *
     * @param annot the PDF annotation to be resized
     * @param scalingMatrix the affine transformation matrix representing the scaling
     *                      and translation to be applied
     */
    private static void resizeAnnotation(PdfAnnotation annot, AffineTransform scalingMatrix) {
        // Transform all geometric coordinate-based properties of the annotation.
        PdfArray rectArray = annot.getRectangle();
        if (rectArray != null) {
            double[] rectPoints = {rectArray.getAsNumber(0).doubleValue(), rectArray.getAsNumber(1).doubleValue(),
                    rectArray.getAsNumber(2).doubleValue(), rectArray.getAsNumber(3).doubleValue()};
            // Transform ll
            scalingMatrix.transform(rectPoints, 0, rectPoints, 0, 1);
            // Transform ur
            scalingMatrix.transform(rectPoints, 2, rectPoints, 2, 1);
            annot.setRectangle(new PdfArray(rectPoints));
        }

        PdfDictionary annotDict = annot.getPdfObject();
        transformCoordinateArray(annotDict.getAsArray(PdfName.L), scalingMatrix);
        transformCoordinateArray(annotDict.getAsArray(PdfName.Vertices), scalingMatrix);
        transformCoordinateArray(annotDict.getAsArray(PdfName.QuadPoints), scalingMatrix);
        transformCoordinateArray(annotDict.getAsArray(PdfName.CL), scalingMatrix);

        PdfArray inkList = annotDict.getAsArray(PdfName.InkList);
        if (inkList != null) {
            for (int i = 0; i < inkList.size(); i++) {
                transformCoordinateArray(inkList.getAsArray(i), scalingMatrix);
            }
        }

        // Scale all scalar properties of the annotation, such as border widths and font sizes.
        scaleAnnotationScalarProperties(annot, scalingMatrix.getScaleX(), scalingMatrix.getScaleY());

        // Resize the appearance streams, which define the annotation's visual representation.
        resizeAppearanceStreams(annot, scalingMatrix);
    }


    /**
     * Scales an array of coordinates (x1, y1, x2, y2, ...) by applying horizontal and vertical scale factors.
     *
     * @param coordinateArray the array of coordinates to scale
     * @param transform the transformation to be applied
     */
    private static void transformCoordinateArray(PdfArray coordinateArray, AffineTransform transform) {
        if (coordinateArray == null) {
            return;
        }

        // Only transform complete pairs.
        if (coordinateArray.size() % 2 != 0) {
            return;
        }

        double[] points = new double[coordinateArray.size()];
        for (int i = 0; i < coordinateArray.size(); i++) {
            points[i] = coordinateArray.getAsNumber(i).doubleValue();
        }

        transform.transform(points, 0, points, 0, points.length / 2);

        for (int i = 0; i < coordinateArray.size(); i++) {
            coordinateArray.set(i, new PdfNumber(points[i]));
        }
    }

    /**
     * Scales the scalar properties of an annotation based on the provided horizontal and vertical scaling factors.
     *
     * @param annot the annotation whose scalar properties are to be scaled
     * @param horizontalScale the factor by which the horizontal dimensions should be scaled
     * @param verticalScale the factor by which the vertical dimensions should be scaled
     */
    private static void scaleAnnotationScalarProperties(PdfAnnotation annot,
                                                        double horizontalScale, double verticalScale) {
        PdfDictionary annotDict = annot.getPdfObject();

        // Scale border width in a Border array [horizontal_radius vertical_radius width ...]
        PdfArray border = annotDict.getAsArray(PdfName.Border);
        if (border != null && border.size() >= 3) {
            border.set(0, new PdfNumber(border.getAsNumber(0).doubleValue() * horizontalScale));
            border.set(1, new PdfNumber(border.getAsNumber(1).doubleValue() * verticalScale));
            border.set(2, new PdfNumber(border.getAsNumber(2).doubleValue()
                    * Math.min(horizontalScale, verticalScale)));
        }

        // Scale border width in a BS (Border Style) dictionary
        PdfDictionary bs = annotDict.getAsDictionary(PdfName.BS);
        if (bs != null) {
            PdfNumber width = bs.getAsNumber(PdfName.W);
            if (width != null) {
                bs.put(PdfName.W, new PdfNumber(width.doubleValue() * Math.min(horizontalScale, verticalScale)));
            }
        }

        // Scale RD (Rectangle Differences) - defines differences between Rect and actual drawing area
        // These are lengths/insets, so they should be scaled, not transformed.
        PdfArray rd = annotDict.getAsArray(PdfName.RD);
        if (rd != null && rd.size() == 4) {
            rd.set(0, new PdfNumber(rd.getAsNumber(0).doubleValue() * horizontalScale));
            rd.set(1, new PdfNumber(rd.getAsNumber(1).doubleValue() * verticalScale));
            rd.set(2, new PdfNumber(rd.getAsNumber(2).doubleValue() * horizontalScale));
            rd.set(3, new PdfNumber(rd.getAsNumber(3).doubleValue() * verticalScale));
        }

        // Scale LeaderLine-related lengths for Line annotations
        double lengthScale = Math.min(horizontalScale, verticalScale);
        if (annotDict.getAsNumber(PdfName.LL) != null) {
            annotDict.put(PdfName.LL, new PdfNumber(annotDict.getAsNumber(PdfName.LL).doubleValue() * lengthScale));
        }
        if (annotDict.getAsNumber(PdfName.LLE) != null) {
            annotDict.put(PdfName.LLE, new PdfNumber(annotDict.getAsNumber(PdfName.LLE).doubleValue() * lengthScale));
        }
        if (annotDict.getAsNumber(PdfName.LLO) != null) {
            annotDict.put(PdfName.LLO, new PdfNumber(annotDict.getAsNumber(PdfName.LLO).doubleValue() * lengthScale));
        }

        // Scale font size in the Default Appearance string
        String da = null;
        if (annotDict.getAsString(PdfName.DA) != null) {
            da = annotDict.getAsString(PdfName.DA).toUnicodeString();
        } else {
            if (PdfName.Widget.equals(annotDict.getAsName(PdfName.Subtype))) {
                // For widget annotation we should also check parents
                da = getDaFromParent(annotDict);
                if (da == null) {
                    // Nothing in parents - check Acroform
                    PdfDictionary acroFormDictionary = annot.getPage().getDocument()
                            .getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
                    if (acroFormDictionary != null && acroFormDictionary.getAsString(PdfName.DA) != null) {
                        da = acroFormDictionary.getAsString(PdfName.DA).toUnicodeString();
                    }
                }
            }
        }
        if (da != null) {
            annotDict.put(PdfName.DA, new PdfString(scaleDaString(da, lengthScale)));
        }
        // Scale font size in Rich Content string
        if (annotDict.getAsString(PdfName.RC) != null) {
            String rc = annotDict.getAsString(PdfName.RC).toUnicodeString();
            annotDict.put(PdfName.RC, new PdfString(scaleRcString(rc, lengthScale)));
        }

        if (annotDict.getAsString(PdfName.DS) != null) {
            String ds = annotDict.getAsString(PdfName.DS).toUnicodeString();
            annotDict.put(PdfName.DS, new PdfString(scaleRcString(ds, lengthScale)));
        }
    }

    /**
     * Formats a given double value to a string representation with reasonable precision
     *
     * @param v the double value to be formatted
     * @return string representation of the formatted number
     */
    private static String formatNumber(double v) {
        if (Double.isNaN(v)) {
            return Double.toString(v);
        }
        if (Math.abs(v) < EPSILON) {
            return "0";
        }
        // Round to 4 decimal places.
        long scaled = (long) Math.round(v * NUMBER_FORMAT_PRECISION);
        if (scaled == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        if (scaled < 0) {
            sb.append('-');
            scaled = -scaled;
        }
        long wholePart = scaled / NUMBER_FORMAT_PRECISION;
        long fractionalPart = scaled % NUMBER_FORMAT_PRECISION;
        sb.append(wholePart);
        if (fractionalPart > 0) {
            sb.append('.');
            String fractionalStr = String.valueOf(NUMBER_FORMAT_PRECISION + fractionalPart).substring(1);
            sb.append(fractionalStr);
            while (sb.length() > 0 && sb.charAt(sb.length() - 1) == '0') {
                sb.setLength(sb.length() - 1);
            }
        }
        return sb.toString();
    }


    /**
     * Resizes a single appearance stream by scaling its bounding box and adjusting its transformation matrix.
     * The method takes into consideration the existing matrix if present, and applies the scaling transformation.
     *
     * @param appearanceStream the appearance stream to be resized
     * @param scalingMatrix the affine transformation matrix representing the scaling to be applied
     */
    private static void resizeAppearanceStream(PdfStream appearanceStream, AffineTransform scalingMatrix) {
        // The appearance stream's transformation matrix should only handle scaling.
        // Page-level translations are handled by the annotation's /Rect entry.
        // We create a new AffineTransform containing only the scaling components.
        AffineTransform scaleOnlyMatrix = new AffineTransform();
        scaleOnlyMatrix.scale(scalingMatrix.getScaleX(), scalingMatrix.getScaleY());

        PdfArray existingMatrix = appearanceStream.getAsArray(PdfName.Matrix);
        AffineTransform newMatrix;

        if (existingMatrix == null) {
            newMatrix = scaleOnlyMatrix;
        } else {
            newMatrix = new AffineTransform(existingMatrix.toDoubleArray());
            newMatrix.preConcatenate(scaleOnlyMatrix);
        }

        double[] newMatrixArray = new double[6];
        newMatrix.getMatrix(newMatrixArray);
        appearanceStream.put(PdfName.Matrix, new PdfArray(newMatrixArray));
    }

    private static String getDaFromParent(PdfDictionary dict) {
        PdfDictionary parentDict = dict.getAsDictionary(PdfName.Parent);
        if (parentDict == null) {
            return null;
        } else {
            PdfString da = parentDict.getAsString(PdfName.DA);
            if (da != null) {
                return da.toUnicodeString();
            } else {
                return getDaFromParent(parentDict);
            }
        }
    }

    /**
     * Updates the page boxes (MediaBox, CropBox, TrimBox, BleedBox, ArtBox) of the given PDF page
     * based on the specified original page size and the size associated with the current instance.
     * The page boxes are scaled proportionally to fit the new page size.
     *
     * @param page the PDF page whose boxes are to be updated
     * @param originalPageSize the dimensions of the original page size
     */
    private void updateBoxes(PdfPage page, Rectangle originalPageSize) {
        Rectangle newCP = scalePageBox(originalPageSize, size, page.getCropBox());
        Rectangle newTB = scalePageBox(originalPageSize, size, page.getTrimBox());
        Rectangle newBB =  scalePageBox(originalPageSize, size, page.getBleedBox());
        Rectangle newAB = scalePageBox(originalPageSize, size, page.getArtBox());

        page.setMediaBox(size);

        if (page.getPdfObject().getAsArray(PdfName.CropBox) != null) {
            page.setCropBox(newCP);
        }
        if (page.getPdfObject().getAsArray(PdfName.TrimBox) != null) {
            page.setTrimBox(newTB);
        }
        if (page.getPdfObject().getAsArray(PdfName.BleedBox) != null) {
            page.setBleedBox(newBB);
        }
        if (page.getPdfObject().getAsArray(PdfName.ArtBox) != null) {
            page.setArtBox(newAB);
        }
    }

    private AffineTransform calculateAffineTransform(double horizontalScale, double verticalScale,
                                                     double horizontalFreeSpace, double verticalFreeSpace) {
        AffineTransform scalingMatrix = new AffineTransform();
        scalingMatrix.scale(horizontalScale, verticalScale);
        AffineTransform transformMatrix = new AffineTransform();
        switch (horizontalAnchorPoint) {
            case CENTER:
                transformMatrix.translate(horizontalFreeSpace / 2, 0);
                break;
            case RIGHT:
                transformMatrix.translate(horizontalFreeSpace, 0);
                break;
            case LEFT:
            default:
                // PDF default nothing to do here
                break;
        }
        switch (verticalAnchorPoint) {
            case CENTER:
                transformMatrix.translate(0, verticalFreeSpace / 2);
                break;
            case TOP:
                transformMatrix.translate(0, verticalFreeSpace);
                break;
            case BOTTOM:
            default:
                // PDF default nothing to do here
                break;
        }
        transformMatrix.concatenate(scalingMatrix);
        scalingMatrix = transformMatrix;
        return scalingMatrix;
    }

    /**
     * Represents the result of parsing a property in a scalable RC string. This class encapsulates
     * the starting and ending indices of the parsed value within the RC property string, along with
     * the parsed numerical value itself.
     * <p>
     * This is a utility class used internally within the PageResizer to help process scalable RC
     * properties by extracting and interpreting numerical values in the context of resizing operations.
     */
    private static class RcPropertyParserResult {
        private int valueStart;
        private int valueEnd;
        private double parsedValue;

        public RcPropertyParserResult(int valueStart, int valueEnd, double parsedValue) {
            this.valueStart = valueStart;
            this.valueEnd = valueEnd;
            this.parsedValue = parsedValue;
        }

        public int getValueStart() {
            return valueStart;
        }

        public int getValueEnd() {
            return valueEnd;
        }

        public double getParsedValue() {
            return parsedValue;
        }
    }

    /**
     * RcPropertyParser is a utility class designed to parse scalable properties
     * from a given CSS-like source string. Its primary function is to locate a specific
     * property, extract its numeric value, and verify the associated unit for scaling purposes.
     * It iterates over the source, attempting to match and parse specific property patterns.
     */
    private static class RcPropertyParser {
        private final String source;
        private final int length;

        private int cursor;
        private RcPropertyParserResult result;


        RcPropertyParser(String source) {
            this.source = source;
            this.length = source.length();
            this.cursor = 0;
        }

        /**
         * Attempts to find the next matching property in the source string and parse its scalable value.
         * If a match is found, the method updates the cursor position and associated parsed value details.
         *
         * @return {@code true} if a matching scalable property is found and its value is successfully parsed,
         *         {@code false} if no more matching properties can be found in the source string.
         */
        public boolean findNext() {
            while (cursor < length) {
                int matchedPropEnd = findAndMatchProperty(cursor);
                if (matchedPropEnd != -1) {
                    int newCursor = parseAndSetScalableValue(matchedPropEnd);
                    if (newCursor != -1) {
                        cursor = newCursor;
                        return true;
                    }
                }
                cursor++;
            }
            return false;
        }

        public RcPropertyParserResult getResult() {
            return result;
        }

        private int findAndMatchProperty(int i) {
            // Micro-optimization: quick reject by checking the first char is a letter commonly starting properties.
            char c = source.charAt(i);
            // Check that the character before (if exists) is not a property name character
            // E.g., this ensures we don't match "height" in "text-height"
            boolean validLeadingBoundary = (i == 0) || !isPropertyNameChar(source.charAt(i - 1));
            if (((c >= 'a' && c <= 'z') || c == '-') && validLeadingBoundary) {
                for (String p : SCALABLE_RC_PROPERTIES) {
                    int len = p.length();
                    if (i + len <= length && compareRegions(source, i, p, 0, len)) {
                        return i + len;
                    }
                }
            }
            return -1;
        }

        private static boolean compareRegions(String first, int firstOffset, String second, int secondOffset, int len) {
            if (firstOffset < 0 || secondOffset < 0 || len < 0 ||
                    (firstOffset + len) > first.length() ||
                    (secondOffset + len) > second.length()) {
                return false;
            }
            for (int i = 0; i < len; i++) {
                char c1 = first.charAt(firstOffset + i);
                char c2 = second.charAt(secondOffset + i);
                if (c1 == c2) {
                    continue;
                }
                return false;
            }
            return true;
        }

        private int parseAndSetScalableValue(int matchedPropEnd) {
            int k = skipWhitespace(source, matchedPropEnd);
            if (k >= length || source.charAt(k) != ':') {
                // Not a property assignment;
                return -1;
            }
            // skip ':'
            k++;
            k = skipWhitespace(source, k);

            // Parse number
            int numStart = k;
            if (numStart >= length) {
                // no value;
                return -1;
            }

            int numEnd = parseCssNumber(source, numStart);
            if (numEnd <= numStart) {
                // Not a number here (e.g., 'inherit' or other values);
                return -1;
            }

            // Skip spaces between number and unit
            int unitStart = skipWhitespace(source, numEnd);

            // Parse unit (letters)
            int unitEnd = parseCssUnit(source, unitStart);

            if (unitEnd <= unitStart) {
                // Missing number or unit; not a match we scale.
                return -1;
            }

            String unit = source.substring(unitStart, unitEnd).toLowerCase();
            if (!SCALABLE_UNITS.contains(unit)) {
                // Do not scale relative or unsupported units
                return -1;
            }

            // At this point we have: property matched, colon, number [numStart..numEnd), unit [unitStart..unitEnd)
            String numStr = source.substring(numStart, numEnd);
            double value;
            try {
                value = Double.parseDouble(numStr);
            } catch (NumberFormatException ex) {
                // Shouldn't happen given our parser, but to be safe
                return -1;
            }

            this.result = new RcPropertyParserResult(numStart, numEnd, value);
            return unitEnd;
        }


        private static boolean isPropertyNameChar(char c) {
            return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '-' || Character.isDigit(c);
        }

        private static int skipWhitespace(String str, int index) {
            while (index < str.length() && Character.isWhitespace(str.charAt(index))) {
                index++;
            }
            return index;
        }

        private static int parseCssNumber(String str, int startIndex) {
            int n = str.length();
            if (startIndex >= n) {
                return startIndex;
            }

            int i = startIndex;

            // optional sign
            if (str.charAt(i) == '+' || str.charAt(i) == '-') {
                i++;
            }

            int digitsBefore = 0;
            int digitsAfter = 0;

            // digits before decimal
            int d = i;
            while (d < n && Character.isDigit(str.charAt(d))) {
                d++;
            }
            digitsBefore = d - i;
            i = d;

            // optional decimal part
            if (i < n && str.charAt(i) == '.') {
                i++;
                int a = i;
                while (a < n && Character.isDigit(str.charAt(a))) {
                    a++;
                }
                digitsAfter = a - i;
                i = a;
            }

            if (digitsBefore == 0 && digitsAfter == 0) {
                return startIndex;
            }

            i = parseExponent(str, i, n);

            return i;
        }

        private static int parseExponent(String str, int i, int n) {
            if (i < n && (str.charAt(i) == 'e' || str.charAt(i) == 'E')) {
                int expPos = i + 1;
                if (expPos < n && (str.charAt(expPos) == '+' || str.charAt(expPos) == '-')) {
                    expPos++;
                }
                int expDigitsStart = expPos;
                while (expPos < n && Character.isDigit(str.charAt(expPos))) {
                    expPos++;
                }
                if (expPos > expDigitsStart) {
                    // accept exponent only if digits present
                    return expPos;
                }
            }
            return i;
        }

        private static int parseCssUnit(String str, int startIndex) {
            int i = startIndex;
            while (i < str.length()) {
                char ch = str.charAt(i);
                if ((ch >= 'a' && ch <= 'z')  || (ch >= 'A' && ch <= 'Z')) {
                    i++;
                } else {
                    break;
                }
            }
            return i;
        }
    }

}
