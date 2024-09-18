package com.itextpdf.signatures.mac;

import com.itextpdf.kernel.mac.IMacContainerLocator;
import com.itextpdf.kernel.mac.AbstractMacIntegrityProtector;
import com.itextpdf.kernel.mac.MacProperties;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * {@link IMacContainerLocator} strategy, which should be used specifically in case of signature creation.
 * This strategy locates MAC container in signature unsigned attributes.
 */
public class SignatureMacContainerLocator implements IMacContainerLocator {
    /**
     * {@inheritDoc}.
     */
    @Override
    public void locateMacContainer(AbstractMacIntegrityProtector macIntegrityProtector) {
        ((SignatureMacIntegrityProtector) macIntegrityProtector).prepareDocument();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document,
            MacProperties macProperties) {
        return new SignatureMacIntegrityProtector(document, macProperties);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document,
            PdfDictionary authDictionary) {
        return new SignatureMacIntegrityProtector(document, authDictionary);
    }
}
