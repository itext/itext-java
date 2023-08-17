package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

/**
 * Interface for annotation flatten workers.
 * This interface is then used in {@link com.itextpdf.kernel.utils.PdfAnnotationFlattener}
 * to flatten annotations.
 */
public interface IAnnotationFlattener {

    /**
     * Flatten annotation.
     *
     * @param annotation annotation to flatten
     * @param page       page to flatten annotation on
     *
     * @return true if annotation was flattened, false otherwise
     */
    boolean flatten(PdfAnnotation annotation, PdfPage page);
}

