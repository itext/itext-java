package com.itextpdf.pdfua.checkers.utils.ua2;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Utility class which performs UA-2 checks related to PdfString objects.
 */
public final class PdfUA2StringChecker {

    private PdfUA2StringChecker() {
        // Private constructor will prevent the instantiation of this class directly.
    }

    /**
     * Checks PdfString object to be UA-2 compatible.
     *
     * @param string {@link PdfString} to be checked
     */
    public static void checkPdfString(PdfString string) {
        // Only perform this check if PdfString is text string (intended to be human-readable).
        if (PdfEncodings.PDF_DOC_ENCODING.equals(string.getEncoding()) ||
                PdfEncodings.UTF8.equals(string.getEncoding()) ||
                PdfEncodings.UNICODE_BIG.equals(string.getEncoding())) {
            for (int i = 0; i < string.getValue().length(); ++i) {
                int code = string.getValue().codePointAt(i);
                boolean isPrivateArea = code >= 0xE000 && code <= 0xF8FF;
                boolean isSupplementaryPrivateAreaA = code >= 0xF0000 && code <= 0xFFFFD;
                boolean isSupplementaryPrivateAreaB = code >= 0x100000 && code <= 0x10FFFD;
                if (isPrivateArea || isSupplementaryPrivateAreaA || isSupplementaryPrivateAreaB) {
                    throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.TEXT_STRING_USES_UNICODE_PUA);
                }
            }
        }
    }
}
