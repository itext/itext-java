package com.itextpdf.kernel.mac;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Default {@link AbstractMacIntegrityProtector} location strategy, which locates MAC container in document's trailer.
 */
public class StandaloneMacContainerLocator implements IMacContainerLocator {
    /**
     * {@inheritDoc}.
     */
    @Override
    public void locateMacContainer(AbstractMacIntegrityProtector macIntegrityProtector) {
        ((StandaloneMacIntegrityProtector) macIntegrityProtector).prepareDocument();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document,
            MacProperties macProperties) {
        return new StandaloneMacIntegrityProtector(document, macProperties);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document,
            PdfDictionary authDictionary) {
        return new StandaloneMacIntegrityProtector(document, authDictionary);
    }
}
