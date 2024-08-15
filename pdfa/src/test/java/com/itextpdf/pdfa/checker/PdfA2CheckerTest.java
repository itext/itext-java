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
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.function.PdfType4Function;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfA2CheckerTest extends ExtendedITextTest {
    private PdfA2Checker pdfA2Checker = new PdfA2Checker(PdfAConformanceLevel.PDF_A_2B);

    @Test
    public void checkNameEntryShouldBeUniqueBetweenDefaultAndAdditionalConfigs() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName"));
        configs.add(config);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.VALUE_OF_NAME_ENTRY_SHALL_BE_UNIQUE_AMONG_ALL_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARIES,
                e.getMessage());
    }

    @Test
    public void checkAsKeyInContentConfigDictTest() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName"));
        config.put(PdfName.AS, new PdfArray());
        configs.add(config);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfA2Checker.checkCatalogValidEntries(catalog));
        Assertions.assertEquals(PdfaExceptionMessageConstant.
                        THE_AS_KEY_SHALL_NOT_APPEAR_IN_ANY_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARY, e.getMessage());
    }

    @Test
    public void checkNameEntryShouldBeUniqueBetweenAdditionalConfigs() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        configs.add(config);
        config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        configs.add(config);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.VALUE_OF_NAME_ENTRY_SHALL_BE_UNIQUE_AMONG_ALL_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARIES,
                e.getMessage());
    }

    @Test
    public void checkOCCDContainName() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        configs.add(config);
        config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName2"));
        configs.add(config);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.OPTIONAL_CONTENT_CONFIGURATION_DICTIONARY_SHALL_CONTAIN_NAME_ENTRY,
                e.getMessage());
    }

    @Test
    public void checkOrderArrayDoesNotContainRedundantReferences()  {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        PdfArray order = new PdfArray();
        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));
        order.add(orderItem);
        PdfDictionary orderItem1 = new PdfDictionary();
        orderItem1.put(PdfName.Name, new PdfString("CustomName3"));
        order.add(orderItem1);
        config.put(PdfName.Order, order);

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);

        ocProperties.put(PdfName.OCGs, ocgs);

        configs.add(config);


        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS, e.getMessage());
    }

    @Test
    public void checkOrderArrayContainsReferencesToAllOCGs() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        PdfArray order = new PdfArray();
        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));
        order.add(orderItem);
        PdfDictionary orderItem1 = new PdfDictionary();
        orderItem1.put(PdfName.Name, new PdfString("CustomName3"));
        config.put(PdfName.Order, order);

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);
        ocgs.add(orderItem1);

        ocProperties.put(PdfName.OCGs, ocgs);

        configs.add(config);

        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS, e.getMessage());
    }

    @Test
    public void checkOrderArrayAndOCGsMatch() {
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        PdfArray order = new PdfArray();
        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));
        order.add(orderItem);
        PdfDictionary orderItem1 = new PdfDictionary();
        orderItem1.put(PdfName.Name, new PdfString("CustomName3"));
        order.add(orderItem1);
        config.put(PdfName.Order, order);

        PdfArray ocgs = new PdfArray();
        PdfDictionary orderItem2 = new PdfDictionary();
        orderItem2.put(PdfName.Name, new PdfString("CustomName4"));
        ocgs.add(orderItem2);
        PdfDictionary orderItem3 = new PdfDictionary();
        orderItem3.put(PdfName.Name, new PdfString("CustomName5"));
        ocgs.add(orderItem3);

        ocProperties.put(PdfName.OCGs, ocgs);

        configs.add(config);

        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS, e.getMessage());
    }

    @Test
    public void checkAbsenceOfOptionalConfigEntryAllowed() {
        PdfDictionary ocProperties = new PdfDictionary();

        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));

        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);

        ocProperties.put(PdfName.OCGs, ocgs);
        ocProperties.put(PdfName.D, d);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        pdfA2Checker.checkCatalogValidEntries(catalog);

        // checkCatalogValidEntries doesn't change the state of any object
        // and doesn't return any value. The only result is exception which
        // was or wasn't thrown. Successful scenario is tested here therefore
        // no assertion is provided
    }

    @Test
    public void checkAbsenceOfOptionalOrderEntryAllowed() {
        PdfDictionary ocProperties = new PdfDictionary();

        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));

        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);

        PdfArray configs = new PdfArray();

        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));

        configs.add(config);

        ocProperties.put(PdfName.OCGs, ocgs);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.OCProperties, ocProperties);

        pdfA2Checker.checkCatalogValidEntries(catalog);


        // checkCatalogValidEntries doesn't change the state of any object
        // and doesn't return any value. The only result is exception which
        // was or wasn't thrown. Successful scenario is tested here therefore
        // no assertion is provided
    }

    @Test
    public void checkCatalogDictionaryWithoutAlternatePresentations() {
        PdfDictionary names = new PdfDictionary();
        names.put(PdfName.AlternatePresentations, new PdfDictionary());

        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Names, names);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_ALTERNATEPRESENTATIONS_NAMES_ENTRY,
                e.getMessage());
    }

    @Test
    public void checkCatalogDictionaryWithoutRequirements() {
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.Requirements, new PdfDictionary());

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_REQUIREMENTS_ENTRY, e.getMessage());
    }

    @Test
    public void deviceNColorspaceNoAttributesDictionary() {
        int numberOfComponents = 2;
        List<String> tmpArray = new ArrayList<String>(numberOfComponents);
        float[] transformArray = new float[numberOfComponents * 2];

        for (int i = 0; i < numberOfComponents; i++) {
            tmpArray.add("MyColor" + i + 1);
            transformArray[i * 2] = 0;
            transformArray[i * 2 + 1]  = 1;
        }
        PdfType4Function function = new PdfType4Function(transformArray, new float[]{0, 1, 0, 1, 0, 1},
                "{0}".getBytes(StandardCharsets.ISO_8859_1));

        PdfDictionary currentColorSpaces = new PdfDictionary();

        //TODO DEVSIX-4203 should not cause an IndexOutOfBoundException.
        // Should throw PdfAConformanceException as Colorants dictionary always must be present
        // for Pdf/A-2
        Assertions.assertThrows(RuntimeException.class,
                () -> pdfA2Checker.checkColorSpace(new PdfSpecialCs.DeviceN(tmpArray, new PdfDeviceCs.Rgb(), function),
                        null, currentColorSpaces, true, false)
        );
    }

    @Test
    public void checkColorShadingTest() {
        PdfDictionary patternDict = new PdfDictionary();
        patternDict.put(PdfName.ExtGState, new PdfDictionary());
        PdfPattern.Shading pattern = new PdfPattern.Shading(patternDict);

        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.ColorSpace, PdfName.DeviceCMYK);
        pattern.setShading(dictionary);

        Color color = new PatternColor(pattern);

        AssertUtil.doesNotThrow(() -> {
            pdfA2Checker.checkColor(null, color, new PdfDictionary(), true, null);
        });
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
            pdfA2Checker.checkColor(null, color, new PdfDictionary(), true, null);
        });
    }

    @Test
    public void checkColorShadingWithoutExtGStatePropertyInPatternDictTest() {
        PdfDictionary patternDict = new PdfDictionary();
        patternDict.put(PdfName.PatternType, new PdfNumber(2));
        PdfPattern.Shading pattern = new PdfPattern.Shading(patternDict);

        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.ColorSpace, PdfName.DeviceCMYK);
        pattern.setShading(dictionary);

        Color color = new PatternColor(pattern);

        AssertUtil.doesNotThrow(() -> {
            pdfA2Checker.checkColor(new UpdateCanvasGraphicsState(new PdfDictionary()),
                    color, new PdfDictionary(), true, null);
        });
    }

    @Test
    public void checkSignatureTest() {
        PdfDictionary signatureDict = createSignatureDict();
        pdfA2Checker.checkSignature(signatureDict);
        Assertions.assertTrue(pdfA2Checker.objectIsChecked(signatureDict));
    }

    @Test
    public void checkSignatureDigestMethodTest() {
        PdfDictionary signatureDict = createSignatureDict();
        PdfArray types = (PdfArray) signatureDict.get(PdfName.Reference);
        PdfDictionary reference = (PdfDictionary) types.get(0);
        PdfArray digestMethod = new PdfArray();
        digestMethod.add(new PdfName("SHA256"));
        reference.put(PdfName.DigestMethod, digestMethod);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkSignature(signatureDict));
        Assertions.assertEquals(PdfaExceptionMessageConstant.SIGNATURE_REFERENCES_DICTIONARY_SHALL_NOT_CONTAIN_DIGESTLOCATION_DIGESTMETHOD_DIGESTVALUE,
                e.getMessage());
    }

    @Test
    public void checkLZWDecodeInInlineImage() {
        PdfStream stream = new PdfStream();
        stream.put(PdfName.Filter, PdfName.LZWDecode);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkInlineImage(stream, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.LZWDECODE_FILTER_IS_NOT_PERMITTED, e.getMessage());
    }

    @Test
    public void checkImageWithAlternateTest() {
        PdfStream image = new PdfStream();
        image.put(PdfName.Alternates, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkImage(image, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.AN_IMAGE_DICTIONARY_SHALL_NOT_CONTAIN_ALTERNATES_KEY, e.getMessage());
    }

    @Test
    public void checkImageWithOPITest() {
        PdfStream image = new PdfStream();
        image.put(PdfName.OPI, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkImage(image, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.AN_IMAGE_DICTIONARY_SHALL_NOT_CONTAIN_OPI_KEY, e.getMessage());
    }

    @Test
    public void checkImageWithInterpolateTest() {
        PdfStream image = new PdfStream();
        image.put(PdfName.Interpolate, new PdfBoolean(true));
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkImage(image, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_VALUE_OF_INTERPOLATE_KEY_SHALL_BE_FALSE, e.getMessage());
    }

    @Test
    public void checkFormXObjectWithOPITest() {
        PdfStream form = new PdfStream();
        form.put(PdfName.OPI, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkFormXObject(form)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_OPI_KEY, e.getMessage());
    }

    @Test
    public void checkFormXObjectWithPSTest() {
        PdfStream form = new PdfStream();
        form.put(PdfName.PS, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkFormXObject(form)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_PS_KEY, e.getMessage());
    }

    @Test
    public void checkCryptInInlineImage() {
        PdfStream stream = new PdfStream();
        stream.put(PdfName.Filter, PdfName.Crypt);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkInlineImage(stream, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.CRYPT_FILTER_IS_NOT_PERMITTED_INLINE_IMAGE, e.getMessage());
    }


    @Test
    public void checkLZWDecodeArrayInInlineImage() {
        PdfStream stream = new PdfStream();
        PdfArray array = new PdfArray();
        array.add(PdfName.LZWDecode);
        stream.put(PdfName.Filter, array);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkInlineImage(stream, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.LZWDECODE_FILTER_IS_NOT_PERMITTED, e.getMessage());
    }

    @Test
    public void checkCryptArrayInInlineImage() {
        PdfStream stream = new PdfStream();
        PdfArray array = new PdfArray();
        array.add(PdfName.Crypt);
        stream.put(PdfName.Filter, array);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkInlineImage(stream, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.CRYPT_FILTER_IS_NOT_PERMITTED_INLINE_IMAGE, e.getMessage());
    }

    @Test
    public void checkAllowedArrayFilterInInlineImage() {
        PdfStream stream = new PdfStream();
        PdfArray array = new PdfArray();
        array.add(PdfName.Identity);
        stream.put(PdfName.Filter, array);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkInlineImage(stream, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.INVALID_INLINE_IMAGE_FILTER_USAGE, e.getMessage());
    }

    @Test
    public void checkAllowedFilterInInlineImage() {
        PdfStream stream = new PdfStream();
        stream.put(PdfName.Filter, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkInlineImage(stream, null)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.INVALID_INLINE_IMAGE_FILTER_USAGE, e.getMessage());
    }

    @Test
    public void checkEmptyAnnotationTest() {
        PdfDictionary annotation = new PdfDictionary();
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkAnnotation(annotation)
        );
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED, "null"), e.getMessage());
    }

    @Test
    public void checkAnnotationAgainstActionsWithATest() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.A, PdfName.Identity);
        annotation.put(PdfName.Subtype, PdfName.Widget);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkAnnotationAgainstActions(annotation)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.WIDGET_ANNOTATION_DICTIONARY_OR_FIELD_DICTIONARY_SHALL_NOT_INCLUDE_A_OR_AA_ENTRY, e.getMessage());
    }

    @Test
    public void checkAnnotationAgainstActionsWithAATest() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.AA, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkAnnotationAgainstActions(annotation)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.AN_ANNOTATION_DICTIONARY_SHALL_NOT_CONTAIN_AA_KEY, e.getMessage());
    }

    @Test
    public void checkNeedsRenderingCatalogTest() {
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.NeedsRendering, new PdfBoolean(true));
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_THE_NEEDSRENDERING_KEY, e.getMessage());
    }

    @Test
    public void checkCatalogContainsAATest() {
        PdfDictionary catalog = new PdfDictionary();
        catalog.put(PdfName.AA,  PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY, e.getMessage());
    }

    @Test
    public void checkCatalogContainsSignatureTest() {
        PdfDictionary catalog = new PdfDictionary();
        PdfDictionary perms = new PdfDictionary();
        PdfDictionary docMdp = new PdfDictionary();
        perms.put(PdfName.DocMDP, docMdp);
        catalog.put(PdfName.Perms,  perms);
        pdfA2Checker.checkCatalogValidEntries(catalog);
        //nothing to check, expecting that no error is thrown
    }

    @Test
    public void checkPageSizeTest() {
        PdfDictionary page = new PdfDictionary();
        PdfArray rect = new PdfArray();
        rect.add(new PdfNumber(0));
        rect.add(new PdfNumber(0));
        rect.add(new PdfNumber(0));
        rect.add(new PdfNumber(0));
        page.put(PdfName.CropBox, rect);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkPageSize(page)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_PAGE_LESS_3_UNITS_NO_GREATER_14400_IN_EITHER_DIRECTION, e.getMessage());
        //nothing to check, expecting that no error is thrown
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
                () -> pdfA2Checker.checkOutputIntents(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.IF_OUTPUTINTENTS_ARRAY_HAS_MORE_THAN_ONE_ENTRY_WITH_DESTOUTPUTPROFILE_KEY_THE_SAME_INDIRECT_OBJECT_SHALL_BE_USED_AS_THE_VALUE_OF_THAT_OBJECT, e.getMessage());
        //nothing to check, expecting that no error is thrown
    }
    @Test
    public void checkCatalogContainsInvalidPermsTest() {
        PdfDictionary catalog = new PdfDictionary();
        PdfDictionary perms = new PdfDictionary();
        perms.put(PdfName.Identity, PdfName.Identity);
        catalog.put(PdfName.Perms,  perms);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkCatalogValidEntries(catalog)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.NO_KEYS_OTHER_THAN_UR3_AND_DOC_MDP_SHALL_BE_PRESENT_IN_A_PERMISSIONS_DICTIONARY, e.getMessage());
    }

    @Test
    public void checkFileSpecNotContainsFKeyTest() {
        PdfDictionary fileSpec = new PdfDictionary();
        fileSpec.put(PdfName.EF, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkFileSpec(fileSpec)
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
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkFileSpec(fileSpec)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.EF_KEY_OF_FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_DICTIONARY_WITH_VALID_F_KEY, e.getMessage());
    }

    @Test
    public void checkPdfStreamContainsFKeyTest() {
        PdfStream pdfStream = new PdfStream();
        pdfStream.put(PdfName.F, PdfName.Identity);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkPdfStream(pdfStream)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.STREAM_OBJECT_DICTIONARY_SHALL_NOT_CONTAIN_THE_F_FFILTER_OR_FDECODEPARAMS_KEYS, e.getMessage());
    }

    @Test
    public void checkPdfStreamContainsLZWDecodeKeyTest() {
        PdfStream pdfStream = new PdfStream();
        pdfStream.put(PdfName.Filter, PdfName.LZWDecode);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkPdfStream(pdfStream)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.LZWDECODE_FILTER_IS_NOT_PERMITTED, e.getMessage());
    }

    @Test
    public void checkPdfStreamContainsLZWDecodeArrayKeyTest() {
        PdfStream pdfStream = new PdfStream();
        PdfArray array = new PdfArray();
        array.add(PdfName.LZWDecode);
        pdfStream.put(PdfName.Filter, array);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkPdfStream(pdfStream)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.LZWDECODE_FILTER_IS_NOT_PERMITTED, e.getMessage());
    }

    @Test
    public void checkPdfStreamContainsCryptKeyTest() {
        PdfStream pdfStream = new PdfStream();
        pdfStream.put(PdfName.Filter, PdfName.Crypt);
        PdfDictionary decodeParams = new PdfDictionary();
        decodeParams.put(PdfName.Name, PdfName.Crypt);
        pdfStream.put(PdfName.DecodeParms, decodeParams);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkPdfStream(pdfStream)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.NOT_IDENTITY_CRYPT_FILTER_IS_NOT_PERMITTED, e.getMessage());
    }

    @Test
    public void checkPdfStreamContainsCryptArrayKeyTest() {
        PdfStream pdfStream = new PdfStream();
        PdfArray array = new PdfArray();
        array.add(PdfName.Crypt);
        pdfStream.put(PdfName.Filter, array);
        PdfDictionary decodeParams = new PdfDictionary();
        PdfArray decodeArray = new PdfArray();
        decodeArray.add(decodeParams);
        decodeParams.put(PdfName.Name, PdfName.Crypt);
        pdfStream.put(PdfName.DecodeParms, decodeArray);
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkPdfStream(pdfStream)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.NOT_IDENTITY_CRYPT_FILTER_IS_NOT_PERMITTED, e.getMessage());
    }

    @Test
    public void checkColorSpaceWithDeviceNWithoutAttributes() {
        List<String> tmpArray = new ArrayList<String>(3);
        float[] transformArray = new float[6];
        tmpArray.add("Black");
        tmpArray.add("Magenta");
        tmpArray.add("White");

        for (int i = 0; i < 3; i++) {
            transformArray[i * 2] = 0;
            transformArray[i * 2 + 1]  = 1;
        }
        PdfType4Function function = new PdfType4Function(transformArray, new float[]{0, 1, 0, 1, 0, 1},
                "{0}".getBytes(StandardCharsets.ISO_8859_1));

        PdfArray deviceNAsArray = ((PdfArray)(new  PdfSpecialCs.DeviceN(tmpArray, new PdfDeviceCs.Rgb(), function)).getPdfObject());
        PdfDictionary currentColorSpaces = new PdfDictionary();


        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkColorSpace(new PdfSpecialCs.DeviceN(deviceNAsArray), null,
                        currentColorSpaces, true, false)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.COLORANTS_DICTIONARY_SHALL_NOT_BE_EMPTY_IN_DEVICE_N_COLORSPACE, e.getMessage());
    }


    @Test
    public void checkColorSpaceWithDeviceNWithoutColorants() {
        List<String> tmpArray = new ArrayList<String>(3);
        float[] transformArray = new float[6];
        tmpArray.add("Black");
        tmpArray.add("Magenta");
        tmpArray.add("White");

        for (int i = 0; i < 3; i++) {
            transformArray[i * 2] = 0;
            transformArray[i * 2 + 1]  = 1;
        }
        PdfType4Function function = new PdfType4Function(transformArray, new float[]{0, 1, 0, 1, 0, 1},
                "{0}".getBytes(StandardCharsets.ISO_8859_1));

        PdfArray deviceNAsArray = ((PdfArray)(new  PdfSpecialCs.DeviceN(tmpArray, new PdfDeviceCs.Rgb(), function)).getPdfObject());
        PdfDictionary currentColorSpaces = new PdfDictionary();
        PdfDictionary attributes = new PdfDictionary();
        deviceNAsArray.add(attributes);


        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> pdfA2Checker.checkColorSpace(new PdfSpecialCs.DeviceN(deviceNAsArray), null,
                        currentColorSpaces, true, false)
        );
        Assertions.assertEquals(PdfaExceptionMessageConstant.COLORANTS_DICTIONARY_SHALL_NOT_BE_EMPTY_IN_DEVICE_N_COLORSPACE, e.getMessage());
    }

    private static PdfDictionary createSignatureDict() {
        PdfDictionary signatureDict = new PdfDictionary();

        PdfDictionary reference = new PdfDictionary();
        PdfDictionary transformParams = new PdfDictionary();
        transformParams.put(PdfName.P, new PdfNumber(1));
        transformParams.put(PdfName.V, new PdfName("1.2"));
        transformParams.put(PdfName.Type, PdfName.TransformParams);
        reference.put(PdfName.TransformMethod, PdfName.DocMDP);
        reference.put(PdfName.Type, PdfName.SigRef);
        reference.put(PdfName.TransformParams, transformParams);

        PdfArray types = new PdfArray();
        types.add(reference);
        signatureDict.put(PdfName.Reference, types);

        return signatureDict;
    }

    private static final class UpdateCanvasGraphicsState extends CanvasGraphicsState {
        public UpdateCanvasGraphicsState(PdfDictionary extGStateDict) {
            updateFromExtGState(new PdfExtGState(extGStateDict));
        }
    }
}
