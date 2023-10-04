package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

/**
 * This class is used to Remove annotations without drawing them on the page content stream.
 */
public class RemoveWithoutDrawingFlattener implements IAnnotationFlattener {


    /**
     * Creates a new {@link RemoveWithoutDrawingFlattener} instance.
     */
    public RemoveWithoutDrawingFlattener() {
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
        page.removeAnnotation(annotation);
        return true;
    }
}


