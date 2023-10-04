package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to warn user that annotation will not be flattened.
 */
public class WarnFormfieldFlattener implements IAnnotationFlattener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarnFormfieldFlattener.class);

    /**
     * Creates a new {@link WarnFormfieldFlattener} instance.
     */
    public WarnFormfieldFlattener() {
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
        LOGGER.warn(KernelLogMessageConstant.FORMFIELD_ANNOTATION_WILL_NOT_BE_FLATTENED);
        return false;
    }
}
