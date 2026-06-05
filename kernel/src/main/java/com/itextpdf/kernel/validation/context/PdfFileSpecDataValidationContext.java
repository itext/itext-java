package com.itextpdf.kernel.validation.context;

import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;

/**
 * Class for {@link com.itextpdf.kernel.pdf.filespec.PdfFileSpec} stream validation context.
 */
public class PdfFileSpecDataValidationContext implements IValidationContext {

    private final PdfStream fileSpecDataStream;

    /**
     * Creates a new {@link PdfFileSpecDataValidationContext} instance.
     *
     * @param fileSpecDataStream {@link PdfStream} which represents data for validation
     */
    public PdfFileSpecDataValidationContext(PdfStream fileSpecDataStream) {
        this.fileSpecDataStream = fileSpecDataStream;
    }


    /**
     * Gets {@link PdfStream} presentation of file spec data.
     *
     * @return file spec data stream object
     */
    public PdfStream getFileSpecDataStream() {
        return fileSpecDataStream;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public ValidationType getType() {
        return ValidationType.FILE_SPEC_DATA;
    }
}
