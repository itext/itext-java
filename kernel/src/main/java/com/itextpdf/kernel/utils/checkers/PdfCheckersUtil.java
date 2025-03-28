/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.utils.checkers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.properties.XMPProperty;

import java.io.ByteArrayInputStream;
import java.util.function.Function;

/**
 * Utility class that contains common checks used in both PDF/A and PDF/UA modules.
 */
public final class PdfCheckersUtil {

    private PdfCheckersUtil() {
        // Private constructor will prevent the instantiation of this class directly.
    }

    /**
     * Checks that natural language is declared using the methods described in ISO 32000-2:2020, 14.9.2 or
     * ISO 32000-1:2008, 14.9.2 (same requirements).
     *
     * @param catalogDict {@link PdfDictionary} document catalog dictionary containing {@code Lang} entry to check
     * @param exceptionSupplier {@code Function<String, PdfException>} in order to provide correct exception
     */
    public static void validateLang(PdfDictionary catalogDict, Function<String, PdfException> exceptionSupplier) {
        if (!BCP47Validator.validate(catalogDict.get(PdfName.Lang).toString())) {
            throw exceptionSupplier.apply(KernelExceptionMessageConstant.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY);
        }
    }

    /**
     * Checks that the {@code Catalog} dictionary of a conforming file contains the {@code Metadata} key whose value is
     * a metadata stream as defined in ISO 32000-2:2020. Also checks that the value of either {@code pdfuaid:part} or
     * {@code pdfaid:part} is the provided one for conforming PDF files and validates required {@code pdfuaid:rev} or
     * {@code pdfaid:rev} value.
     *
     * <p>
     * For PDF/UA, checks that the {@code Metadata} stream as specified in ISO 32000-2:2020, 14.3 in the document
     * catalog dictionary includes a {@code dc:title} entry reflecting the title of the document.
     *
     * <p>
     * For PDF/A, checks that {@code pdfa:conformance} value is correct if specified.
     *
     * @param catalog {@link PdfDictionary} document catalog dictionary
     * @param conformance either PDF/A or PDF/UA conformance to check
     * @param exceptionSupplier {@code Function<String, PdfException>} in order to provide correct exception
     */
    public static void checkMetadata(PdfDictionary catalog, PdfConformance conformance,
                                     Function<String, PdfException> exceptionSupplier) {
        if (!catalog.containsKey(PdfName.Metadata)) {
            throw exceptionSupplier.apply(
                    KernelExceptionMessageConstant.METADATA_SHALL_BE_PRESENT_IN_THE_CATALOG_DICTIONARY);
        }
        try {
            final PdfStream xmpMetadata = catalog.getAsStream(PdfName.Metadata);
            if (xmpMetadata == null) {
                throw exceptionSupplier.apply(KernelExceptionMessageConstant.INVALID_METADATA_VALUE);
            }
            final XMPMeta metadata = XMPMetaFactory.parse(new ByteArrayInputStream(xmpMetadata.getBytes()));

            final String NS_ID = conformance.isPdfA() ? XMPConst.NS_PDFA_ID : XMPConst.NS_PDFUA_ID;

            XMPProperty actualPart = metadata.getProperty(NS_ID, XMPConst.PART);
            String expectedPart = conformance.isPdfA() ? conformance.getAConformance().getPart() :
                    conformance.getUAConformance().getPart();
            if (actualPart == null || !expectedPart.equals(actualPart.getValue())) {
                throw exceptionSupplier.apply(MessageFormatUtil.format(KernelExceptionMessageConstant
                        .XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_PART, expectedPart,
                        (actualPart != null && actualPart.getValue().isEmpty()) ? null : actualPart));
            }

            XMPProperty rev = metadata.getProperty(NS_ID, XMPConst.REV);
            if (rev == null || !isValidXmpRevision(rev.getValue())) {
                throw exceptionSupplier.apply(KernelExceptionMessageConstant
                        .XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV);
            }
        } catch (XMPException e) {
            throw exceptionSupplier.apply(KernelExceptionMessageConstant.INVALID_METADATA_VALUE);
        }
    }

    /**
     * Gets all the descending kids including widgets for a given {@link PdfArray} representing array of form fields.
     *
     * @param array the {@link PdfArray} of form fields {@link PdfDictionary} objects
     *
     * @return the {@link PdfArray} of all form fields
     */
    public static PdfArray getFormFields(PdfArray array) {
        PdfArray fields = new PdfArray();
        for (PdfObject field : array) {
            PdfArray kids = ((PdfDictionary) field).getAsArray(PdfName.Kids);
            fields.add(field);
            if (kids != null) {
                fields.addAll(getFormFields(kids));
            }
        }
        return fields;
    }

    /**
     * Validates {@code pdfuaid:rev} value which is four-digit year of the date of publication or revision.
     *
     * @param value {@code pdfuaid:rev} value to check
     *
     * @return {@code true} if {@code pdfuaid:rev} value is valid, {@code false} otherwise
     */
    private static boolean isValidXmpRevision(String value) {
        if (value == null || value.length() != 4) {
            return false;
        }
        for (final char c : value.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
