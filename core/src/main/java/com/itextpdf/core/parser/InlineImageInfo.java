package com.itextpdf.core.parser;

import com.itextpdf.core.pdf.PdfDictionary;

/**
 * Represents an inline image from a PDF
 */
public class InlineImageInfo {
    private final byte[] samples;
    private final PdfDictionary imageDictionary;

    public InlineImageInfo(byte[] samples, PdfDictionary imageDictionary) {
        this.samples = samples;
        this.imageDictionary = imageDictionary;
    }

    /**
     * @return the image dictionary associated with this inline image
     */
    public PdfDictionary getImageDictionary() {
        return imageDictionary;
    }

    /**
     * @return the raw samples associated with this inline image
     */
    public byte[] getSamples() {
        return samples;
    }
}