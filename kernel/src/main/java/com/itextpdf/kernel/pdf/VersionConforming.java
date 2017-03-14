package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO consider naming?
// TODO consider this class access level and package
public class VersionConforming {
    private static final Logger logger = LoggerFactory.getLogger(VersionConforming.class);

    public static void validatePdfVersionForDictEntry(PdfDocument document, PdfVersion expectedVersion, PdfName entryKey, PdfName dictType) {
        if (document.getPdfVersion().compareTo(expectedVersion) < 0) {
            logger.warn(MessageFormat.format(LogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY, entryKey, dictType, expectedVersion, document.getPdfVersion()));
        }


    }
}
