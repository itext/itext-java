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

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * The PageResizer class provides functionality to resize PDF pages to a specified
 * target page size using various resizing methods. It adjusts page dimensions,
 * content, annotations, and resources accordingly, also supports configuration
 * options for maintaining the aspect ratio during the resize operation.
 */
class PageResizer {
    /**
     * Represents the target page size for the {@code PageResizer} functionality.
     * This variable dictates the dimensions to which a page needs to be resized.
     * The value is immutable and initialised during the construction of the {@code PageResizer}.
     */
    private final PageSize size;
    /**
     * Represents the type of resize operation to be applied to a page.
     * This variable specifies the method by which the page resize is performed.
     */
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
     * Resizes a given PDF page based on the specified dimensions and resize type.
     * Depending on the resize type, the aspect ratio may be maintained during scaling.
     * Updates the page's content, annotations, and resources to reflect the new size.
     *
     * @param page the PDF page to be resized
     */
    public void resize(PdfPage page) {
        Rectangle originalPageSize = page.getMediaBox();
        double horizontalScale = size.getWidth() / originalPageSize.getWidth();
        double verticalScale = size.getHeight() / originalPageSize.getHeight();

        if (ResizeType.MAINTAIN_ASPECT_RATIO == type) {
            double scale = Math.min(horizontalScale, verticalScale);
            horizontalScale = scale;
            verticalScale = scale;
        }

        updateBoxes(page, originalPageSize);

        AffineTransform scalingMatrix = new AffineTransform();
        scalingMatrix.scale(horizontalScale, verticalScale);
        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), page.getDocument());
        pdfCanvas.concatMatrix(scalingMatrix);
        pdfCanvas.saveState();

        for (PdfName resName : page.getResources().getResourceNames())
        {
            PdfPattern pattern = page.getResources().getPattern(resName);
            if (pattern != null)
            {
                resizePattern(pattern, scalingMatrix);
            }
            PdfFormXObject form = page.getResources().getForm(resName);
            if (form != null) {
                resizeForm(form, scalingMatrix);
            }
        }

        for (PdfAnnotation annot : page.getAnnotations())
        {
            resizeAnnotation(annot, scalingMatrix);
        }
    }

    /**
     * Scales the transformation matrix of the provided PDF pattern using the given scaling matrix.
     *
     * @param pattern the PDF pattern whose transformation matrix is to be scaled
     * @param scalingMatrix the affine transformation matrix to be applied for scaling
     */
    private static void resizePattern(PdfPattern pattern, AffineTransform scalingMatrix) {
        AffineTransform origTrans;
        if (pattern.getMatrix() == null) {
            origTrans = new AffineTransform(scalingMatrix);
        } else {
            origTrans = new AffineTransform(pattern.getMatrix().toDoubleArray());
            AffineTransform newMatrix = new AffineTransform(scalingMatrix);
            newMatrix.concatenate(origTrans);
            origTrans = newMatrix;
        }
        double[] newMatrixArray = new double[6];
        origTrans.getMatrix(newMatrixArray);
        pattern.setMatrix(new PdfArray(newMatrixArray));
    }

    /**
     * Resizes the given PDF form XObject by applying the specified affine transformation matrix.
     * This method adjusts the content of the form XObject based on the scaling matrix to fit
     * the desired dimensions or proportions.
     *
     * @param form the PDF form XObject to be resized
     * @param scalingMatrix the affine transformation matrix representing the scaling to be applied
     */
    private static void resizeForm(PdfFormXObject form, AffineTransform scalingMatrix) {
        //TODO DEVSIX-9439 implement this method
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
        annot.setRectangle(scalePdfRect(annot.getRectangle(), scalingMatrix.getScaleY(), scalingMatrix.getScaleX()));
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
    private static Rectangle scalePageBox(Rectangle originalPageSize, PageSize newPageSize, Rectangle box) {
        float lfr = originalPageSize.getWidth() / box.getLeft();
        float wfr = originalPageSize.getWidth() / box.getWidth();
        float tfr = originalPageSize.getHeight() / box.getBottom();
        float hfr = originalPageSize.getHeight() / box.getHeight();
        return new Rectangle(newPageSize.getWidth() / lfr , newPageSize.getHeight() / tfr,
                newPageSize.getWidth() / wfr, newPageSize.getHeight() / hfr);
    }

    /**
     * Scales the dimensions of the given PDF rectangle by applying the specified vertical and horizontal scale factors.
     *
     * @param rect the PDF array representing the rectangular dimensions to be scaled
     * @param verticalScale the factor by which the vertical dimensions should be scaled
     * @param horizontalScale the factor by which the horizontal dimensions should be scaled
     *
     * @return the updated PDF array with the scaled dimensions
     */
    private static PdfArray scalePdfRect(PdfArray rect, double verticalScale, double horizontalScale) {
        rect.set(0, new PdfNumber(((PdfNumber)rect.get(0)).doubleValue() * horizontalScale));
        rect.set(1, new PdfNumber(((PdfNumber)rect.get(1)).doubleValue() * verticalScale));
        rect.set(2, new PdfNumber(((PdfNumber)rect.get(2)).doubleValue() * horizontalScale));
        rect.set(3, new PdfNumber(((PdfNumber)rect.get(3)).doubleValue() * verticalScale));
        return rect;
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


    /**
     * Enum representing the available types of resizing strategies when modifying the dimensions
     * of a PDF page. These strategies determine how the content is scaled relative to the new size.
     */
    enum ResizeType {
        MAINTAIN_ASPECT_RATIO,
        DEFAULT
    }
}
