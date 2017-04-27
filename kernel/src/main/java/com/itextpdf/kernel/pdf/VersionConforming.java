package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO consider naming?
// TODO consider this class access level and package
public class VersionConforming {
    // TODO consider constants location
    static final String DEPRECATED_AES256_REVISION = "It seems that PDF 1.7 document encrypted with AES256 was updated to PDF 2.0 version and StampingProperties#preserveEncryption flag was set: encryption shall be updated via WriterProperties#setStandardEncryption method. Standard security handler with revision 5";
    static final String DEPRECATED_ENCRYPTION_ALGORITHMS = "Encryption algorithms STANDARD_ENCRYPTION_40, STANDARD_ENCRYPTION_128 and ENCRYPTION_AES_128 (see com.itextpdf.kernel.pdf.EncryptionConstants) usage";

    private static final Logger logger = LoggerFactory.getLogger(VersionConforming.class);

    public static void validatePdfVersionForDictEntry(PdfDocument document, PdfVersion expectedVersion, PdfName entryKey, PdfName dictType) {
        if (document != null && document.getPdfVersion().compareTo(expectedVersion) < 0) {
            logger.warn(MessageFormat.format(LogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY, entryKey, dictType, expectedVersion, document.getPdfVersion()));
        }


    }

    public static void validatePdfVersionForDeprecatedFeature(PdfDocument document, PdfVersion expectedVersion, String deprecatedFeatureDescription) {
        if (document.getPdfVersion().compareTo(expectedVersion) >= 0) {
            logger.warn(MessageFormat.format(LogMessageConstant.FEATURE_IS_DEPRECATED, deprecatedFeatureDescription, expectedVersion));
        }
    }
}
