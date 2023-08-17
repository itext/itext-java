package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to warn that annotation flattening is not supported for the given annotation.
 */
public class NotSupportedFlattener implements IAnnotationFlattener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotSupportedFlattener.class);

    /**
     * Creates a new {@link NotSupportedFlattener} instance.
     */
    public NotSupportedFlattener() {
        //empty constructor
    }

    /**
     * Logs a warning that annotation flattening is not supported for the given annotation.
     *
     * @param annotation annotation to flatten
     * @param page       page to flatten annotation on
     *
     * @return true if annotation was flattened, false otherwise
     */
    @Override
    public boolean flatten(PdfAnnotation annotation, PdfPage page) {
        final String message = MessageFormatUtil.format(KernelLogMessageConstant.FLATTENING_IS_NOT_YET_SUPPORTED,
                annotation.getSubtype());
        LOGGER.warn(message);
        return false;
    }
}
