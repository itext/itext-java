/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.logs.IoLogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class VersionConforming {

    public static final String DEPRECATED_AES256_REVISION = "It seems that PDF 1.7 document encrypted with AES256 was updated to PDF 2.0 version and StampingProperties#preserveEncryption flag was set: encryption shall be updated via WriterProperties#setStandardEncryption method. Standard security handler was found with revision 5, which is deprecated and shall not be used in PDF 2.0 documents.";
    public static final String DEPRECATED_ENCRYPTION_ALGORITHMS = "Encryption algorithms STANDARD_ENCRYPTION_40, STANDARD_ENCRYPTION_128 and ENCRYPTION_AES_128 (see com.itextpdf.kernel.pdf.EncryptionConstants) are deprecated in PDF 2.0. It is highly recommended not to use it.";
    public static final String DEPRECATED_NEED_APPEARANCES_IN_ACROFORM = "NeedAppearances has been deprecated in PDF 2.0. Appearance streams are required in PDF 2.0.";
    public static final String DEPRECATED_XFA_FORMS = "XFA is deprecated in PDF 2.0. The XFA form will not be written to the document";
    public static final String NOT_SUPPORTED_AES_GCM = "Advanced Encryption Standard-Galois/Counter Mode " +
            "(AES-GCM) encryption algorithm is supported starting from PDF 2.0.";

    private static final Logger logger = LoggerFactory.getLogger(VersionConforming.class);

    public static boolean validatePdfVersionForDictEntry(PdfDocument document, PdfVersion expectedVersion, PdfName entryKey, PdfName dictType) {
        if (document != null && document.getPdfVersion().compareTo(expectedVersion) < 0) {
            logger.warn(
                    MessageFormat.format(IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY, entryKey,
                            dictType, expectedVersion, document.getPdfVersion()));
            return true;
        } else {
            return false;
        }
    }

    public static boolean validatePdfVersionForDeprecatedFeatureLogWarn(PdfDocument document, PdfVersion expectedVersion, String deprecatedFeatureLogMessage) {
        if (document.getPdfVersion().compareTo(expectedVersion) >= 0) {
            logger.warn(deprecatedFeatureLogMessage);
            return true;
        } else {
            return false;
        }
    }

    public static boolean validatePdfVersionForDeprecatedFeatureLogError(PdfDocument document, PdfVersion expectedVersion, String deprecatedFeatureLogMessage) {
        if (document.getPdfVersion().compareTo(expectedVersion) >= 0) {
            logger.error(deprecatedFeatureLogMessage);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Logs error message in case provided PDF document version is earlier than specified expected starting version.
     *
     * @param document PDF document to check version for
     * @param expectedStartVersion starting version since which new feature is supported
     * @param notSupportedFeatureLogMessage error message to log
     *
     * @return boolean value specifying whether validation passed ({@code true}) or failed ({@code false})
     */
    public static boolean validatePdfVersionForNotSupportedFeatureLogError(PdfDocument document,
                                                                           PdfVersion expectedStartVersion,
                                                                           String notSupportedFeatureLogMessage) {
        if (document.getPdfVersion().compareTo(expectedStartVersion) >= 0) {
            return true;
        }
        logger.error(notSupportedFeatureLogMessage);
        return false;
    }

}
