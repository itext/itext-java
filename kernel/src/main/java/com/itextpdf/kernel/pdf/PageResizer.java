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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The PageResizer class provides functionality to resize PDF pages to a specified
 * target page size using various resizing methods. It adjusts page dimensions,
 * content, annotations, and resources accordingly, also supports configuration
 * options for maintaining the aspect ratio during the resize operation.
 */
class PageResizer {
    private static final float EPSILON = 1e-6f;

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
    PageResizer(PageSize size, ResizeType type) {
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

    static String scaleDaString(String daString, double scale) {
        if (daString == null || daString.trim().isEmpty()) {
            return daString;
        }

        // Optimization for identity scaling. Use an epsilon for robust float comparison.
        if (Math.abs(scale - 1.0) < 1e-9) {
            return daString;
        }

        java.util.List<String> tokens = new ArrayList<>(Arrays.asList(daString.trim().split("\\s+")));

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
        final java.util.Set<String> scalableOperators = new java.util.HashSet<>(java.util.Arrays.asList(
                "Tf", "TL", "Tc", "Tw", "Ts"));

        for (int i = 0; i < tokens.size(); i++) {
            if (scalableOperators.contains(tokens.get(i))) {
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
     * Resizes the appearance streams of a given PDF annotation by applying the specified affine transformation matrix.
     * This involves scaling the content of the appearance streams in the annotation's
     * appearance dictionary and adjusting their transformation matrices to reflect the scaling.
     *
     * @param annot the PDF annotation whose appearance streams are to be resized
     * @param scalingMatrix the affine transformation matrix used to scale the appearance streams
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
     * Scales the transformation matrix of the provided pattern using the given scaling matrix,
     * without mutating the original pattern.
     *
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
        if (annotDict.getAsString(PdfName.DA) != null) {
            String da = annotDict.getAsString(PdfName.DA).toUnicodeString();
            annotDict.put(PdfName.DA, new PdfString(scaleDaString(da, lengthScale)));
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
        // Round to 4 decimal places.
        long scaled = (long) Math.round(v * 10000.0);
        if (scaled == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        if (scaled < 0) {
            sb.append('-');
            scaled = -scaled;
        }
        long wholePart = scaled / 10000;
        long fractionalPart = scaled % 10000;
        sb.append(wholePart);
        if (fractionalPart > 0) {
            sb.append('.');
            String fractionalStr = String.valueOf(10000 + fractionalPart).substring(1);
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
     * Enum representing the available types of resizing strategies when modifying the dimensions
     * of a PDF page. These strategies determine how the content is scaled relative to the new size.
     */
    enum ResizeType {
        MAINTAIN_ASPECT_RATIO,
        DEFAULT
    }

    enum VerticalAnchorPoint {
        TOP,
        CENTER,
        BOTTOM
    }

    enum HorizontalAnchorPoint {
        LEFT,
        CENTER,
        RIGHT
    }
}
