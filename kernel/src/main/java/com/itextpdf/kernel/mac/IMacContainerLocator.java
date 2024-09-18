package com.itextpdf.kernel.mac;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Strategy interface, which is responsible for {@link AbstractMacIntegrityProtector} container location.
 * Expected to be used in {@link com.itextpdf.commons.utils.DIContainer}.
 */
public interface IMacContainerLocator {
    /**
     * Locates {@link AbstractMacIntegrityProtector} container.
     *
     * @param macIntegrityProtector {@link AbstractMacIntegrityProtector} container to be located
     */
    void locateMacContainer(AbstractMacIntegrityProtector macIntegrityProtector);

    /**
     * Creates {@link AbstractMacIntegrityProtector} from explicitly provided MAC properties.
     *
     * @param document {@link PdfDocument} for which MAC container shall be created
     * @param macProperties {@link MacProperties} to be used for MAC container creation
     *
     * @return {@link AbstractMacIntegrityProtector} which specific implementation depends on interface implementation.
     */
    AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document, MacProperties macProperties);

    /**
     * Creates {@link AbstractMacIntegrityProtector} from already existing AuthCode dictionary.
     *
     * @param document {@link PdfDocument} for which MAC container shall be created
     * @param authDictionary AuthCode {@link PdfDictionary} which contains MAC related information
     *
     * @return {@link AbstractMacIntegrityProtector} which specific implementation depends on interface implementation.
     */
    AbstractMacIntegrityProtector createMacIntegrityProtector(PdfDocument document, PdfDictionary authDictionary);
}
