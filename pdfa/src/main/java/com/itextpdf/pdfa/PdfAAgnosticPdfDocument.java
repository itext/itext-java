package com.itextpdf.pdfa;

import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;

/**
 * This class extends {@link PdfADocument} and serves as {@link PdfADocument} for
 * PDF/A compliant documents and as {@link com.itextpdf.kernel.pdf.PdfDocument}
 * for non PDF/A documents.
 *
 * <p>
 * This class can throw various exceptions like {@link com.itextpdf.kernel.exceptions.PdfException}
 * as well as {@link com.itextpdf.pdfa.exceptions.PdfAConformanceException} for PDF/A documents.
 */
public class PdfAAgnosticPdfDocument extends PdfADocument {

    /**
     * Opens a PDF/A document in stamping mode.
     *
     * @param reader the {@link PdfReader}
     * @param writer the {@link PdfWriter} object to write to
     */
    public PdfAAgnosticPdfDocument (PdfReader reader, PdfWriter writer) {
        this(reader, writer, new StampingProperties());
    }

    /**
     * Opens a PDF/A document in stamping mode.
     *
     * @param reader the {@link PdfReader}
     * @param writer the {@link PdfWriter} object to write to
     * @param properties {@link StampingProperties} of the stamping process
     */
    public PdfAAgnosticPdfDocument (PdfReader reader, PdfWriter writer, StampingProperties properties) {
        super(reader, writer, properties, true);
    }
}
