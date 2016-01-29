package com.itextpdf.kernel.pdf;

/**
 * Enum listing all official PDF versions.
 */
public enum PdfVersion {

    PDF_1_0("PDF-1.0"),
    PDF_1_1("PDF-1.1"),
    PDF_1_2("PDF-1.2"),
    PDF_1_3("PDF-1.3"),
    PDF_1_4("PDF-1.4"),
    PDF_1_5("PDF-1.5"),
    PDF_1_6("PDF-1.6"),
    PDF_1_7("PDF-1.7"),
    PDF_2_0("PDF-2.0");

    private String value;

    /**
     * Creates a PdfVersion enum.
     *
     * @param value version number
     */
    PdfVersion(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Creates a PdfVersion enum from a String object if the specified version
     * can be found.
     *
     * @param value version number
     * @return PdfVersion of the specified version
     */
    public static PdfVersion fromString(String value) {
        for (PdfVersion version : PdfVersion.values()) {
            if (version.value.equals(value)) {
                return version;
            }
        }
        throw new IllegalArgumentException("The provided pdf version was not found.");
    }
}
