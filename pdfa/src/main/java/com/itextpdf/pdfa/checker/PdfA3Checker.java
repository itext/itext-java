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
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.pdfa.logs.PdfAConformanceLogMessageConstant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PdfA3Checker defines the requirements of the PDF/A-3 standard and contains a
 * number of methods that override the implementations of its superclass
 * {@link PdfA2Checker}.
 * 
 * The specification implemented by this class is ISO 19005-3
 */
public class PdfA3Checker extends PdfA2Checker{
    protected static final Set<PdfName> allowedAFRelationships = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.Source,
                    PdfName.Data,
                    PdfName.Alternative,
                    PdfName.Supplement,
                    PdfName.Unspecified)));

    /**
     * Creates a PdfA3Checker with the required conformance
     * 
     * @param aConformance the required conformance, <code>a</code> or
     * <code>u</code> or <code>b</code>
     */
    public PdfA3Checker(PdfAConformance aConformance) {
        super(aConformance);
    }

    @Override
    protected void checkFileSpec(PdfDictionary fileSpec) {
        PdfName relationship = fileSpec.getAsName(PdfName.AFRelationship);
        if (relationship == null || !allowedAFRelationships.contains(relationship)) {
            throw new PdfAConformanceException(PdfaExceptionMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_ONE_OF_THE_PREDEFINED_AFRELATIONSHIP_KEYS);
        }

        if (fileSpec.containsKey(PdfName.EF)) {
            if (!fileSpec.containsKey(PdfName.F) || !fileSpec.containsKey(PdfName.UF) || !fileSpec.containsKey(PdfName.Desc)) {
                throw new PdfAConformanceException(PdfaExceptionMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY);
            }


            PdfDictionary ef = fileSpec.getAsDictionary(PdfName.EF);
            PdfStream embeddedFile = ef.getAsStream(PdfName.F);
            if (embeddedFile == null) {
                throw new PdfAConformanceException(PdfaExceptionMessageConstant.EF_KEY_OF_FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_DICTIONARY_WITH_VALID_F_KEY);
            }

            if (!embeddedFile.containsKey(PdfName.Subtype)) {
                throw new PdfAConformanceException(PdfaExceptionMessageConstant.MIME_TYPE_SHALL_BE_SPECIFIED_USING_THE_SUBTYPE_KEY_OF_THE_FILE_SPECIFICATION_STREAM_DICTIONARY);
            }

            if (embeddedFile.containsKey(PdfName.Params)) {
                PdfObject params = embeddedFile.get(PdfName.Params);
                if (!params.isDictionary()) {
                    throw new PdfAConformanceException(PdfaExceptionMessageConstant.EMBEDDED_FILE_SHALL_CONTAIN_PARAMS_KEY_WITH_DICTIONARY_AS_VALUE);
                }
                if (((PdfDictionary)params).getAsString(PdfName.ModDate) == null) {
                    throw new PdfAConformanceException(PdfaExceptionMessageConstant.EMBEDDED_FILE_SHALL_CONTAIN_PARAMS_KEY_WITH_VALID_MODDATE_KEY);
                }
            } else {
                Logger logger = LoggerFactory.getLogger(PdfAChecker.class);
                logger.warn(PdfAConformanceLogMessageConstant.EMBEDDED_FILE_SHOULD_CONTAIN_PARAMS_KEY);
            }
        }
    }
}
