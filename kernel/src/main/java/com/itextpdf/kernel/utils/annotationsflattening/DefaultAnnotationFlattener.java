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
package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to flatten annotations.
 * The default implementation first tries to draw the normal appearance stream of the annotation.
 * If the normal appearance stream is not present, then it tries to draw the annotation using the fallback
 * implementation.
 */
public class DefaultAnnotationFlattener implements IAnnotationFlattener {
    private static final Logger LOGGER = LoggerFactory.getLogger(
            DefaultAnnotationFlattener.class);

    /**
     * Creates a new {@link DefaultAnnotationFlattener} instance.
     */
    public DefaultAnnotationFlattener() {
        //empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean flatten(PdfAnnotation annotation, PdfPage page) {
        if (annotation == null) {
            throw new PdfException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "annotation"));
        }
        if (page == null) {
            throw new PdfException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "page"));
        }
        final PdfArray pdfArrayRectangle = annotation.getRectangle();
        if (pdfArrayRectangle == null) {
            return false;
        }

        final PdfObject normalAppearance = annotation.getNormalAppearanceObject();
        if (normalAppearance instanceof PdfStream) {
            final Rectangle area = annotation.getRectangle().toRectangle();
            final PdfCanvas under = this.createCanvas(page);
            final PdfStream annotationNormalAppearanceStream = (PdfStream) normalAppearance;
            under.addXObjectFittedIntoRectangle(new PdfFormXObject(annotationNormalAppearanceStream), area);
            page.removeAnnotation(annotation);
            return true;
        }
        final boolean drawn = draw(annotation, page);
        if (drawn) {
            page.removeAnnotation(annotation);
            return true;
        }

        final String message = MessageFormatUtil.format(KernelLogMessageConstant.FLATTENING_IS_NOT_YET_SUPPORTED,
                annotation.getSubtype());
        LOGGER.warn(message);
        return false;
    }

    /**
     * Creates a canvas. It will draw above the other items on the canvas.
     *
     * @param page the page to draw the annotation on
     *
     * @return the {@link  PdfCanvas} the annotation will be drawn upon.
     */
    protected PdfCanvas createCanvas(PdfPage page) {
        return new PdfCanvas(page.newContentStreamAfter(), page.getResources(), page.getDocument());
    }

    /**
     * Draws annotation.
     * This method is called if the normal appearance stream of the annotation is not present.
     * The default implementation returns false.
     *
     * @param annotation annotation to draw
     * @param page       page to draw annotation on
     *
     * @return true if annotation was drawn, false otherwise
     */
    protected boolean draw(PdfAnnotation annotation, PdfPage page) {
        return false;
    }
}
