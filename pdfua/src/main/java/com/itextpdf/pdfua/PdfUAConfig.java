package com.itextpdf.pdfua;

import com.itextpdf.kernel.pdf.PdfUAConformanceLevel;

/**
 * Class that holds the configuration for the PDF/UA document.
 */
public class PdfUAConfig {

    private final PdfUAConformanceLevel conformanceLevel;
    private final String title;
    private final String language;

    /**
     * Creates a new PdfUAConfig instance.
     *
     * @param conformanceLevel The conformance level of the PDF/UA document.
     * @param title            The title of the PDF/UA document.
     * @param language         The language of the PDF/UA document.
     */
    public PdfUAConfig(PdfUAConformanceLevel conformanceLevel, String title, String language) {
        this.conformanceLevel = conformanceLevel;
        this.title = title;
        this.language = language;
    }

    /**
     * Gets the conformance level.
     *
     * @return The {@link PdfUAConformanceLevel}.
     */
    public PdfUAConformanceLevel getConformanceLevel() {
        return conformanceLevel;
    }

    /**
     * Gets the title.
     *
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the language.
     *
     * @return The language.
     */
    public String getLanguage() {
        return language;
    }

}
