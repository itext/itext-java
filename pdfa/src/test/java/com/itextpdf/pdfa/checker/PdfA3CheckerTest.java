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
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfA3CheckerTest extends ExtendedITextTest {
    private PdfA1Checker pdfA3Checker = new PdfA3Checker(PdfAConformance.PDF_A_3B);

    @BeforeEach
    public void before() {
        pdfA3Checker.setFullCheckMode(true);
    }

    @Test
    public void checkFileSpecNotContainsAFRelationshipKeyTest() {
        PdfDictionary fileSpec = new PdfDictionary();
        fileSpec.put(PdfName.EF, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA3Checker.checkFileSpec(fileSpec)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_ONE_OF_THE_PREDEFINED_AFRELATIONSHIP_KEYS, e.getMessage());
    }

    @Test
    public void checkFileSpecNotContainsFKeyTest() {
        PdfDictionary fileSpec = new PdfDictionary();
        fileSpec.put(PdfName.EF, PdfName.Identity);
        fileSpec.put(PdfName.AFRelationship, PdfName.Data);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA3Checker.checkFileSpec(fileSpec)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY, e.getMessage());
    }

    @Test
    public void checkFileSpecContainsNullFKeyTest() {
        PdfDictionary fileSpec = new PdfDictionary();
        fileSpec.put(PdfName.EF, new PdfDictionary());
        fileSpec.put(PdfName.F, PdfName.Identity);
        fileSpec.put(PdfName.UF, PdfName.Identity);
        fileSpec.put(PdfName.Desc, PdfName.Identity);
        fileSpec.put(PdfName.AFRelationship, PdfName.Data);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA3Checker.checkFileSpec(fileSpec)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.EF_KEY_OF_FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_DICTIONARY_WITH_VALID_F_KEY, e.getMessage());
    }

    @Test
    public void checkFileSpecContainsEmptyFKeyTest() {
        PdfDictionary fileSpec = new PdfDictionary();
        PdfDictionary ef = new PdfDictionary();
        ef.put(PdfName.F, new PdfStream());
        fileSpec.put(PdfName.EF, ef);
        fileSpec.put(PdfName.F, new PdfDictionary());
        fileSpec.put(PdfName.UF, PdfName.Identity);
        fileSpec.put(PdfName.Desc, PdfName.Identity);
        fileSpec.put(PdfName.AFRelationship, PdfName.Data);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA3Checker.checkFileSpec(fileSpec)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.MIME_TYPE_SHALL_BE_SPECIFIED_USING_THE_SUBTYPE_KEY_OF_THE_FILE_SPECIFICATION_STREAM_DICTIONARY, e.getMessage());
    }

    @Test
    public void checkFileSpecContainsFKeyWithParamsTest() {
        PdfDictionary fileSpec = new PdfDictionary();
        PdfDictionary ef = new PdfDictionary();
        PdfStream stream = new PdfStream();
        stream.put(PdfName.Params, PdfName.Identity);
        stream.put(PdfName.Subtype, PdfName.Identity);
        ef.put(PdfName.F, stream);
        fileSpec.put(PdfName.EF, ef);
        fileSpec.put(PdfName.F, new PdfDictionary());
        fileSpec.put(PdfName.UF, PdfName.Identity);
        fileSpec.put(PdfName.Desc, PdfName.Identity);
        fileSpec.put(PdfName.AFRelationship, PdfName.Data);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA3Checker.checkFileSpec(fileSpec)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.EMBEDDED_FILE_SHALL_CONTAIN_PARAMS_KEY_WITH_DICTIONARY_AS_VALUE, e.getMessage());
    }
}
