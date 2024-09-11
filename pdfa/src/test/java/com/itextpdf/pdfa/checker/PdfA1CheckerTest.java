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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfA1CheckerTest extends ExtendedITextTest {

    private PdfA1Checker pdfA1Checker = new PdfA1Checker(PdfAConformance.PDF_A_1B);

    @BeforeEach
    public void before() {
        pdfA1Checker.setFullCheckMode(true);
    }

    @Test
    public void checkCatalogDictionaryWithoutAAEntry() {
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.AA, new PdfDictionary());

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY, e.getMessage());
    }

    @Test
    public void checkCatalogDictionaryWithoutOCPropertiesEntry() {
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, new PdfDictionary());

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_OCPROPERTIES_KEY, e.getMessage());
    }

    @Test
    public void checkCatalogDictionaryWithoutEmbeddedFiles() {
        PdfDictionary names = new PdfDictionary();
        names.put(PdfName.EmbeddedFiles, new PdfDictionary());

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Names, names);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_NAME_DICTIONARY_SHALL_NOT_CONTAIN_THE_EMBEDDED_FILES_KEY, e.getMessage());
    }

    @Test
    public void checkValidCatalog() {
        pdfA1Checker.checkCatalogValidEntries(new PdfDictionary());

        // checkCatalogValidEntries doesn't change the state of any object
        // and doesn't return any value. The only result is exception which
        // was or wasn't thrown. Successful scenario is tested here therefore
        // no assertion is provided
    }

    @Test
    public void deprecatedCheckColorShadingTest() {
        PdfDictionary patternDict = new PdfDictionary();
        patternDict.put(PdfName.ExtGState, new PdfDictionary());
        PdfPattern.Shading pattern = new PdfPattern.Shading(patternDict);

        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.ColorSpace, PdfName.DeviceCMYK);
        pattern.setShading(dictionary);

        Color color = new PatternColor(pattern);

        AssertUtil.doesNotThrow(() -> {
            pdfA1Checker.checkColor(null, color, new PdfDictionary(), true, null);
        });
    }

    @Test
    public void checkSignatureTest() {
        PdfDictionary dict = new PdfDictionary();
        pdfA1Checker.checkSignature(dict);
        Assertions.assertTrue(pdfA1Checker.objectIsChecked(dict));
    }

    @Test
    public void checkSignatureTypeTest() {
        pdfA1Checker.checkSignatureType(true);
        //nothing to check, only for coverage
    }

    @Test
    public void checkLZWDecodeInInlineImage() {
        PdfStream stream = new PdfStream();
        stream.put(PdfName.Filter, PdfName.LZWDecode);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkInlineImage(stream, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.LZWDECODE_FILTER_IS_NOT_PERMITTED, e.getMessage());
    }

    @Test
    public void checkLZWDecodeArrayInInlineImage() {
        PdfStream stream = new PdfStream();
        PdfArray array = new PdfArray();
        array.add(PdfName.LZWDecode);
        stream.put(PdfName.Filter, array);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkInlineImage(stream, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.LZWDECODE_FILTER_IS_NOT_PERMITTED, e.getMessage());
    }

    @Test
    public void checkEmptyImageTwiceTest() {
        PdfStream image = new PdfStream();
        pdfA1Checker.checkImage(image, null);
        pdfA1Checker.checkImage(image, null);
        //nothing to check, expecting that no error is thrown
    }

    @Test
    public void checkImageWithAlternateTest() {
        PdfStream image = new PdfStream();
        image.put(PdfName.Alternates, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkImage(image, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.AN_IMAGE_DICTIONARY_SHALL_NOT_CONTAIN_ALTERNATES_KEY, e.getMessage());
    }

    @Test
    public void checkImageWithOPITest() {
        PdfStream image = new PdfStream();
        image.put(PdfName.OPI, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkImage(image, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.AN_IMAGE_DICTIONARY_SHALL_NOT_CONTAIN_OPI_KEY, e.getMessage());
    }

    @Test
    public void checkImageWithInterpolateTest() {
        PdfStream image = new PdfStream();
        image.put(PdfName.Interpolate, new PdfBoolean(true));
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkImage(image, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_VALUE_OF_INTERPOLATE_KEY_SHALL_BE_FALSE, e.getMessage());
    }

    @Test
    public void checkImageWithSMaskTest() {
        PdfStream image = new PdfStream();
        image.put(PdfName.SMask, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkImage(image, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_SMASK_KEY_IS_NOT_ALLOWED_IN_XOBJECTS, e.getMessage());
    }

    @Test
    public void checkFormXObjectWithOPITest() {
        PdfStream form = new PdfStream();
        form.put(PdfName.OPI, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkFormXObject(form)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_OPI_KEY, e.getMessage());
    }

    @Test
    public void checkFormXObjectWithPSTest() {
        PdfStream form = new PdfStream();
        form.put(PdfName.PS, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkFormXObject(form)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_PS_KEY, e.getMessage());
    }

    @Test
    public void checkFormXObjectWithSubtype2PSTest() {
        PdfStream form = new PdfStream();
        form.put(PdfName.Subtype2, PdfName.PS);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkFormXObject(form)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_SUBTYPE2_KEY_WITH_A_VALUE_OF_PS, e.getMessage());
    }

    @Test
    public void checkFormXObjectWithSMaskTest() {
        PdfStream form = new PdfStream();
        form.put(PdfName.SMask, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkFormXObject(form)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_SMASK_KEY_IS_NOT_ALLOWED_IN_XOBJECTS, e.getMessage());
    }

    @Test
    public void checkCatalogContainsMetadataTest() {
        PdfDictionary catalog = new PdfDictionary();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkMetaData(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_CATALOG_DICTIONARY_SHALL_CONTAIN_METADATA_ENTRY, e.getMessage());
    }

    @Test
    public void checkOutputIntentsTest() {
        PdfDictionary catalog = new PdfDictionary();
        PdfArray array = new PdfArray();
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.DestOutputProfile, PdfName.Identity);
        PdfDictionary dictionary2 = new PdfDictionary();
        dictionary2.put(PdfName.DestOutputProfile, PdfName.Crypt);
        array.add(dictionary);
        array.add(dictionary2);
        catalog.put(PdfName.OutputIntents, array);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkOutputIntents(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.IF_OUTPUTINTENTS_ARRAY_HAS_MORE_THAN_ONE_ENTRY_WITH_DESTOUTPUTPROFILE_KEY_THE_SAME_INDIRECT_OBJECT_SHALL_BE_USED_AS_THE_VALUE_OF_THAT_OBJECT, e.getMessage());
        //nothing to check, expecting that no error is thrown
    }

    @Test
    public void checkLZWDecodeInPdfStreamTest() {
        PdfStream stream = new PdfStream();
        stream.put(PdfName.Filter, PdfName.LZWDecode);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkPdfStream(stream)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.LZWDECODE_FILTER_IS_NOT_PERMITTED, e.getMessage());
    }

    @Test
    public void checkLZWDecodeInPdfStreamArrayTest() {
        PdfStream stream = new PdfStream();
        PdfArray array = new PdfArray();
        array.add(PdfName.LZWDecode);
        stream.put(PdfName.Filter, array);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkPdfStream(stream)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.LZWDECODE_FILTER_IS_NOT_PERMITTED, e.getMessage());
    }

    @Test
    public void checkFileSpecTest() {
        pdfA1Checker.checkFileSpec(new PdfDictionary());
        //nothing to check, only for coverage
    }

    @Test
    public void checkEmptyAnnotationTest() {
        PdfDictionary annotation = new PdfDictionary();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkAnnotation(annotation)
        );
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED, "null"), e.getMessage());
    }

    @Test
    public void checkAnnotationWithoutFKeyTest() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA1Checker.checkAnnotation(annotation)
        );
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.AN_ANNOTATION_DICTIONARY_SHALL_CONTAIN_THE_F_KEY, "null"), e.getMessage());
    }
}
