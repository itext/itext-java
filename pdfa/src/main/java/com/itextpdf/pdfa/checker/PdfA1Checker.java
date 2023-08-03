/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfTrueTypeFont;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfXrefTable;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.parser.util.PdfCanvasParser;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.pdfa.logs.PdfAConformanceLogMessageConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PdfA1Checker defines the requirements of the PDF/A-1 standard and contains
 * method implementations from the abstract {@link PdfAChecker} class.
 * <p>
 * The specification implemented by this class is ISO 19005-1
 */
public class PdfA1Checker extends PdfAChecker {

    protected static final Set<PdfName> forbiddenAnnotations = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.Sound,
                    PdfName.Movie,
                    PdfName.FileAttachment)));
    protected static final Set<PdfName> contentAnnotations = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.Text,
                    PdfName.FreeText,
                    PdfName.Line,
                    PdfName.Square,
                    PdfName.Circle,
                    PdfName.Stamp,
                    PdfName.Ink,
                    PdfName.Popup)));
    protected static final Set<PdfName> forbiddenActions = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.Launch,
                    PdfName.Sound,
                    PdfName.Movie,
                    PdfName.ResetForm,
                    PdfName.ImportData,
                    PdfName.JavaScript,
                    PdfName.Hide)));
    protected static final Set<PdfName> allowedNamedActions = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.NextPage,
                    PdfName.PrevPage,
                    PdfName.FirstPage,
                    PdfName.LastPage)));
    protected static final Set<PdfName> allowedRenderingIntents = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.RelativeColorimetric,
                    PdfName.AbsoluteColorimetric,
                    PdfName.Perceptual,
                    PdfName.Saturation)));
    private static final int MAX_NUMBER_OF_DEVICEN_COLOR_COMPONENTS = 8;

    /**
     * Creates a PdfA1Checker with the required conformance level
     *
     * @param conformanceLevel the required conformance level, <code>a</code> or
     *                         <code>b</code>
     */
    public PdfA1Checker(PdfAConformanceLevel conformanceLevel) {
        super(conformanceLevel);
    }

    @Override
    public void checkCanvasStack(char stackOperation) {
        if ('q' == stackOperation) {
            if (++gsStackDepth > PdfA1Checker.maxGsStackDepth)
                throw new PdfAConformanceException(PdfAConformanceException.GRAPHICS_STATE_STACK_DEPTH_IS_GREATER_THAN_28);
        } else if ('Q' == stackOperation) {
            gsStackDepth--;
        }
    }

    @Override
    public void checkInlineImage(PdfStream inlineImage, PdfDictionary currentColorSpaces) {
        PdfObject filter = inlineImage.get(PdfName.Filter);
        if (filter instanceof PdfName) {
            if (filter.equals(PdfName.LZWDecode)) {
                throw new PdfAConformanceException(PdfAConformanceException.LZWDECODE_FILTER_IS_NOT_PERMITTED);
            }
        } else if (filter instanceof PdfArray) {
            for (int i = 0; i < ((PdfArray) filter).size(); i++) {
                PdfName f = ((PdfArray) filter).getAsName(i);
                if (f.equals(PdfName.LZWDecode)) {
                    throw new PdfAConformanceException(PdfAConformanceException.LZWDECODE_FILTER_IS_NOT_PERMITTED);
                }
            }
        }

        checkImage(inlineImage, currentColorSpaces);
    }

    @Override
    public void checkColor(Color color, PdfDictionary currentColorSpaces, Boolean fill, PdfStream stream) {
        checkColorSpace(color.getColorSpace(), currentColorSpaces, true, fill);
        if (color instanceof PatternColor) {
            PdfPattern pattern = ((PatternColor) color).getPattern();
            if (pattern instanceof PdfPattern.Tiling) {
                checkContentStream((PdfStream) pattern.getPdfObject());
            }
        }
    }

    @Override
    public void checkColorSpace(PdfColorSpace colorSpace, PdfDictionary currentColorSpaces, boolean checkAlternate, Boolean fill) {
        if (colorSpace instanceof PdfSpecialCs.Separation) {
            colorSpace = ((PdfSpecialCs.Separation) colorSpace).getBaseCs();
        } else if (colorSpace instanceof PdfSpecialCs.DeviceN) {
            PdfSpecialCs.DeviceN deviceNColorspace = (PdfSpecialCs.DeviceN) colorSpace;
            if (deviceNColorspace.getNumberOfComponents() > MAX_NUMBER_OF_DEVICEN_COLOR_COMPONENTS) {
                throw new PdfAConformanceException(PdfAConformanceException.
                        THE_NUMBER_OF_COLOR_COMPONENTS_IN_DEVICE_N_COLORSPACE_SHOULD_NOT_EXCEED,
                        MAX_NUMBER_OF_DEVICEN_COLOR_COMPONENTS);
            }
            colorSpace = deviceNColorspace.getBaseCs();
        }

        if (colorSpace instanceof PdfDeviceCs.Rgb) {
            if (cmykIsUsed) {
                throw new PdfAConformanceException(PdfAConformanceException.DEVICERGB_AND_DEVICECMYK_COLORSPACES_CANNOT_BE_USED_BOTH_IN_ONE_FILE);
            }
            rgbIsUsed = true;
        } else if (colorSpace instanceof PdfDeviceCs.Cmyk) {
            if (rgbIsUsed) {
                throw new PdfAConformanceException(PdfAConformanceException.DEVICERGB_AND_DEVICECMYK_COLORSPACES_CANNOT_BE_USED_BOTH_IN_ONE_FILE);
            }
            cmykIsUsed = true;
        } else if (colorSpace instanceof PdfDeviceCs.Gray) {
            grayIsUsed = true;
        }
    }


    @Override
    public void checkXrefTable(PdfXrefTable xrefTable) {
        if (xrefTable.getCountOfIndirectObjects() > getMaxNumberOfIndirectObjects()) {
            throw new PdfAConformanceException(PdfAConformanceException.MAXIMUM_NUMBER_OF_INDIRECT_OBJECTS_EXCEEDED);
        }
    }

    @Override
    protected Set<PdfName> getForbiddenActions() {
        return forbiddenActions;
    }

    @Override
    protected Set<PdfName> getAllowedNamedActions() {
        return allowedNamedActions;
    }

    @Override
    protected long getMaxNumberOfIndirectObjects() {
        return 8_388_607;
    }

    @Override
    protected void checkColorsUsages() {
        if ((rgbIsUsed || cmykIsUsed || grayIsUsed) && pdfAOutputIntentColorSpace == null) {
            throw new PdfAConformanceException(PdfAConformanceException.IF_DEVICE_RGB_CMYK_GRAY_USED_IN_FILE_THAT_FILE_SHALL_CONTAIN_PDFA_OUTPUTINTENT);
        }

        if (rgbIsUsed) {
            if (!ICC_COLOR_SPACE_RGB.equals(pdfAOutputIntentColorSpace)) {
                throw new PdfAConformanceException(PdfAConformanceException.DEVICERGB_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_RGB_PDFA_OUTPUT_INTENT);
            }
        }
        if (cmykIsUsed) {
            if (!ICC_COLOR_SPACE_CMYK.equals(pdfAOutputIntentColorSpace)) {
                throw new PdfAConformanceException(PdfAConformanceException.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT);
            }
        }
    }

    @Override
    public void checkExtGState(CanvasGraphicsState extGState, PdfStream contentStream) {
        if (extGState.getTransferFunction() != null) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_EXTGSTATE_DICTIONARY_SHALL_NOT_CONTAIN_THE_TR_KEY);
        }
        PdfObject transferFunction2 = extGState.getTransferFunction2();
        if (transferFunction2 != null && !PdfName.Default.equals(transferFunction2)) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_EXTGSTATE_DICTIONARY_SHALL_NOT_CONTAIN_THE_TR_2_KEY_WITH_A_VALUE_OTHER_THAN_DEFAULT);
        }

        checkRenderingIntent(extGState.getRenderingIntent());

        PdfObject softMask = extGState.getSoftMask();
        if (softMask != null && !PdfName.None.equals(softMask)) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_SMASK_KEY_IS_NOT_ALLOWED_IN_EXTGSTATE);
        }

        PdfObject bm = extGState.getBlendMode();
        if (bm != null && !PdfName.Normal.equals(bm) && !PdfName.Compatible.equals(bm)) {
            throw new PdfAConformanceException(PdfAConformanceException.BLEND_MODE_SHALL_HAVE_VALUE_NORMAL_OR_COMPATIBLE);
        }

        Float ca = extGState.getStrokeOpacity();
        if (ca != null && ca != 1) {
            throw new PdfAConformanceException(PdfAConformanceException.TRANSPARENCY_IS_NOT_ALLOWED_CA_SHALL_BE_EQUAL_TO_1);
        }

        ca = extGState.getFillOpacity();
        if (ca != null && ca != 1) {
            throw new PdfAConformanceException(PdfAConformanceException.TRANSPARENCY_IS_NOT_ALLOWED_AND_CA_SHALL_BE_EQUAL_TO_1);
        }
    }

    @Override
    public void checkFontGlyphs(PdfFont font, PdfStream contentStream) {
        // This check is irrelevant for the PdfA1 checker, so the body of the method is empty
    }

    @Override
    public void checkRenderingIntent(PdfName intent) {
        if (intent == null)
            return;

        if (!allowedRenderingIntents.contains(intent)) {
            throw new PdfAConformanceException(PdfAConformanceException.IF_SPECIFIED_RENDERING_SHALL_BE_ONE_OF_THE_FOLLOWING_RELATIVECOLORIMETRIC_ABSOLUTECOLORIMETRIC_PERCEPTUAL_OR_SATURATION);
        }
    }

    @Override
    public void checkFont(PdfFont pdfFont) {
        if (!pdfFont.isEmbedded()) {
            throw new PdfAConformanceException(PdfAConformanceException.ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0)
                    .setMessageParams(pdfFont.getFontProgram().getFontNames().getFontName());
        }

        if (pdfFont instanceof PdfTrueTypeFont) {
            PdfTrueTypeFont trueTypeFont = (PdfTrueTypeFont) pdfFont;
            boolean symbolic = trueTypeFont.getFontEncoding().isFontSpecific();
            if (symbolic) {
                checkSymbolicTrueTypeFont(trueTypeFont);
            } else {
                checkNonSymbolicTrueTypeFont(trueTypeFont);
            }
        }

        if (pdfFont instanceof PdfType3Font) {
            PdfDictionary charProcs = pdfFont.getPdfObject().getAsDictionary(PdfName.CharProcs);
            for (PdfName charName : charProcs.keySet()) {
                checkContentStream(charProcs.getAsStream(charName));
            }
        }
    }

    @Override
    protected void checkPageTransparency(PdfDictionary pageDict, PdfDictionary pageResources) {
        // This check is irrelevant for the PdfA1 checker, so the body of the method is empty
    }

    @Override
    protected void checkContentStream(PdfStream contentStream) {
        if (isFullCheckMode() || contentStream.isModified()) {
            byte[] contentBytes = contentStream.getBytes();
            PdfTokenizer tokenizer = new PdfTokenizer(
                    new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(contentBytes)));

            PdfCanvasParser parser = new PdfCanvasParser(tokenizer);
            List<PdfObject> operands = new ArrayList<>();
            try {
                while (parser.parse(operands).size() > 0) {
                    for (PdfObject operand : operands) {
                        checkContentStreamObject(operand);
                    }
                }
            } catch (IOException e) {
                throw new PdfException(PdfaExceptionMessageConstant.CANNOT_PARSE_CONTENT_STREAM, e);
            }
        }
    }

    @Override
    protected void checkContentStreamObject(PdfObject object) {
        byte type = object.getType();
        switch (type) {
            case PdfObject.NAME:
                checkPdfName((PdfName) object);
                break;
            case PdfObject.STRING:
                checkPdfString((PdfString) object);
                break;
            case PdfObject.NUMBER:
                checkPdfNumber((PdfNumber) object);
                break;
            case PdfObject.ARRAY:
                PdfArray array = (PdfArray) object;
                checkPdfArray(array);
                for (PdfObject obj : array) {
                    checkContentStreamObject(obj);
                }
                break;
            case PdfObject.DICTIONARY:
                PdfDictionary dictionary = (PdfDictionary) object;
                checkPdfDictionary(dictionary);
                for (final PdfName name: dictionary.keySet()) {
                    checkPdfName(name);
                    checkPdfObject(dictionary.get(name, false));
                }
                for (final PdfObject obj : dictionary.values()) {
                    checkContentStreamObject(obj);
                }
                break;
        }
    }

    @Override
    protected void checkNonSymbolicTrueTypeFont(PdfTrueTypeFont trueTypeFont) {
        String encoding = trueTypeFont.getFontEncoding().getBaseEncoding();
        // non-symbolic true type font will always has an encoding entry in font dictionary in itext
        if (!PdfEncodings.WINANSI.equals(encoding) && !PdfEncodings.MACROMAN.equals(encoding) || trueTypeFont.getFontEncoding().hasDifferences()) {
            throw new PdfAConformanceException(PdfAConformanceException.ALL_NON_SYMBOLIC_TRUE_TYPE_FONT_SHALL_SPECIFY_MAC_ROMAN_OR_WIN_ANSI_ENCODING_AS_THE_ENCODING_ENTRY, trueTypeFont);
        }
    }

    @Override
    protected void checkSymbolicTrueTypeFont(PdfTrueTypeFont trueTypeFont) {
        if (trueTypeFont.getFontEncoding().hasDifferences()) {
            throw new PdfAConformanceException(PdfAConformanceException.ALL_SYMBOLIC_TRUE_TYPE_FONTS_SHALL_NOT_SPECIFY_ENCODING);
        }

        // if symbolic font encoding doesn't have differences, itext won't write encoding for such font
    }

    @Override
    protected void checkImage(PdfStream image, PdfDictionary currentColorSpaces) {
        PdfColorSpace colorSpace = null;
        if (isAlreadyChecked(image)) {
            colorSpace = checkedObjectsColorspace.get(image);
            checkColorSpace(colorSpace, currentColorSpaces, true, null);
            return;
        }
        PdfObject colorSpaceObj = image.get(PdfName.ColorSpace);
        if (colorSpaceObj != null) {
            colorSpace = PdfColorSpace.makeColorSpace(colorSpaceObj);
            checkColorSpace(colorSpace, currentColorSpaces, true, null);
            checkedObjectsColorspace.put(image, colorSpace);
        }

        if (image.containsKey(PdfName.Alternates)) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_IMAGE_DICTIONARY_SHALL_NOT_CONTAIN_ALTERNATES_KEY);
        }
        if (image.containsKey(PdfName.OPI)) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_IMAGE_DICTIONARY_SHALL_NOT_CONTAIN_OPI_KEY);
        }

        if (image.containsKey(PdfName.Interpolate) && (boolean) image.getAsBool(PdfName.Interpolate)) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_VALUE_OF_INTERPOLATE_KEY_SHALL_BE_FALSE);
        }

        checkRenderingIntent(image.getAsName(PdfName.Intent));

        if (image.containsKey(PdfName.SMask) && !PdfName.None.equals(image.getAsName(PdfName.SMask))) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_SMASK_KEY_IS_NOT_ALLOWED_IN_XOBJECTS);
        }
    }

    @Override
    protected void checkFormXObject(PdfStream form) {
        if (isAlreadyChecked(form)) return;

        if (form.containsKey(PdfName.OPI)) {
            throw new PdfAConformanceException(PdfAConformanceException.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_OPI_KEY);
        }
        if (form.containsKey(PdfName.PS)) {
            throw new PdfAConformanceException(PdfAConformanceException.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_PS_KEY);
        }
        if (PdfName.PS.equals(form.getAsName(PdfName.Subtype2))) {
            throw new PdfAConformanceException(PdfAConformanceException.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_SUBTYPE2_KEY_WITH_A_VALUE_OF_PS);
        }

        if (form.containsKey(PdfName.SMask) && !PdfName.None.equals(form.getAsName(PdfName.SMask))) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_SMASK_KEY_IS_NOT_ALLOWED_IN_XOBJECTS);
        }

        if (isContainsTransparencyGroup(form)) {
            throw new PdfAConformanceException(PdfAConformanceException.A_GROUP_OBJECT_WITH_AN_S_KEY_WITH_A_VALUE_OF_TRANSPARENCY_SHALL_NOT_BE_INCLUDED_IN_A_FORM_XOBJECT);
        }

        checkResources(form.getAsDictionary(PdfName.Resources));
        checkContentStream(form);
    }

    @Override
    protected void checkLogicalStructure(PdfDictionary catalog) {
        if (checkStructure(conformanceLevel)) {
            PdfDictionary markInfo = catalog.getAsDictionary(PdfName.MarkInfo);
            if (markInfo == null || markInfo.getAsBoolean(PdfName.Marked) == null || !markInfo.getAsBoolean(PdfName.Marked).getValue()) {
                throw new PdfAConformanceException(PdfAConformanceException.A_CATALOG_SHALL_INCLUDE_MARK_INFO_DICTIONARY_WITH_MARKED_TRUE_VALUE);
            }
            if (!catalog.containsKey(PdfName.Lang)) {
                Logger logger = LoggerFactory.getLogger(PdfAChecker.class);
                logger.warn(PdfAConformanceLogMessageConstant.CATALOG_SHOULD_CONTAIN_LANG_ENTRY);
            }
        }
    }

    @Override
    protected void checkMetaData(PdfDictionary catalog) {
        if (!catalog.containsKey(PdfName.Metadata)) {
            throw new PdfAConformanceException(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_CONTAIN_METADATA_ENTRY);
        }
    }

    @Override
    protected void checkOutputIntents(PdfDictionary catalog) {
        PdfArray outputIntents = catalog.getAsArray(PdfName.OutputIntents);
        if (outputIntents == null)
            return;

        int i;
        PdfObject destOutputProfile = null;
        for (i = 0; i < outputIntents.size() && destOutputProfile == null; ++i) {
            destOutputProfile = outputIntents.getAsDictionary(i).get(PdfName.DestOutputProfile);
        }
        for (; i < outputIntents.size(); ++i) {
            PdfObject otherDestOutputProfile = outputIntents.getAsDictionary(i).get(PdfName.DestOutputProfile);
            if (otherDestOutputProfile != null && destOutputProfile != otherDestOutputProfile) {
                throw new PdfAConformanceException(PdfAConformanceException.IF_OUTPUTINTENTS_ARRAY_HAS_MORE_THAN_ONE_ENTRY_WITH_DESTOUTPUTPROFILE_KEY_THE_SAME_INDIRECT_OBJECT_SHALL_BE_USED_AS_THE_VALUE_OF_THAT_OBJECT);
            }
        }

    }

    @Override
    protected void checkPdfNumber(PdfNumber number) {
        if (number.hasDecimalPoint()) {
            if (Math.abs(number.longValue()) > getMaxRealValue()) {
                throw new PdfAConformanceException(PdfAConformanceException.REAL_NUMBER_IS_OUT_OF_RANGE);
            }
        } else {
            if (number.longValue() > getMaxIntegerValue() || number.longValue() < getMinIntegerValue()) {
                throw new PdfAConformanceException(PdfAConformanceException.INTEGER_NUMBER_IS_OUT_OF_RANGE);
            }
        }
    }

    /**
     * Retrieve maximum allowed real value.
     * @return maximum allowed real number
     */
    protected double getMaxRealValue() {
        return 32767;
    }

    /**
     * Retrieve maximal allowed integer value.
     * @return maximal allowed integer number
     */
    protected long getMaxIntegerValue() {
        return Integer.MAX_VALUE;
    }

    /**
     * Retrieve minimal allowed integer value.
     * @return minimal allowed integer number
     */
    protected long getMinIntegerValue() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected void checkPdfArray(PdfArray array) {
        if (array.size() > getMaxArrayCapacity()) {
            throw new PdfAConformanceException(PdfAConformanceException.MAXIMUM_ARRAY_CAPACITY_IS_EXCEEDED);
        }
    }

    @Override
    protected void checkPdfDictionary(PdfDictionary dictionary) {
        if (dictionary.size() > getMaxDictionaryCapacity()) {
            throw new PdfAConformanceException(PdfAConformanceException.MAXIMUM_DICTIONARY_CAPACITY_IS_EXCEEDED);
        }
    }

    @Override
    protected void checkPdfStream(PdfStream stream) {
        checkPdfDictionary(stream);

        if (stream.containsKey(PdfName.F) || stream.containsKey(PdfName.FFilter) || stream.containsKey(PdfName.FDecodeParams)) {
            throw new PdfAConformanceException(PdfAConformanceException.STREAM_OBJECT_DICTIONARY_SHALL_NOT_CONTAIN_THE_F_FFILTER_OR_FDECODEPARAMS_KEYS);
        }

        PdfObject filter = stream.get(PdfName.Filter);
        if (filter instanceof PdfName) {
            if (filter.equals(PdfName.LZWDecode))
                throw new PdfAConformanceException(PdfAConformanceException.LZWDECODE_FILTER_IS_NOT_PERMITTED);
        } else if (filter instanceof PdfArray) {
            for (PdfObject f : ((PdfArray) filter)) {
                if (f.equals(PdfName.LZWDecode))
                    throw new PdfAConformanceException(PdfAConformanceException.LZWDECODE_FILTER_IS_NOT_PERMITTED);
            }
        }
    }

    @Override
    protected void checkPdfName(PdfName name) {
        if (name.getValue().length() > getMaxNameLength()) {
            throw new PdfAConformanceException(PdfAConformanceException.PDF_NAME_IS_TOO_LONG);
        }
    }

    /**
     * Retrieve maximum allowed length of the name object.
     *
     * @return maximum allowed length of the name
     */
    protected int getMaxNameLength() {
        return 127;
    }

    @Override
    protected void checkPdfString(PdfString string) {
        if (string.getValueBytes().length > getMaxStringLength()) {
            throw new PdfAConformanceException(PdfAConformanceException.PDF_STRING_IS_TOO_LONG);
        }
    }

    /**
     * Returns maximum allowed bytes length of the string in a PDF document.
     *
     * @return maximum string length
     */
    protected int getMaxStringLength() {
        return 65535;
    }

    @Override
    protected void checkPageSize(PdfDictionary page) {

    }

    @Override
    protected void checkFileSpec(PdfDictionary fileSpec) {
        if (fileSpec.containsKey(PdfName.EF)) {
            throw new PdfAConformanceException(PdfAConformanceException.FILE_SPECIFICATION_DICTIONARY_SHALL_NOT_CONTAIN_THE_EF_KEY);
        }
    }

    @Override
    protected void checkAnnotation(PdfDictionary annotDic) {
        PdfName subtype = annotDic.getAsName(PdfName.Subtype);

        if (subtype == null) {
            throw new PdfAConformanceException(PdfAConformanceException.ANNOTATION_TYPE_0_IS_NOT_PERMITTED).setMessageParams("null");
        }
        if (forbiddenAnnotations.contains(subtype)) {
            throw new PdfAConformanceException(PdfAConformanceException.ANNOTATION_TYPE_0_IS_NOT_PERMITTED).setMessageParams(subtype.getValue());
        }
        PdfNumber ca = annotDic.getAsNumber(PdfName.CA);
        if (ca != null && ca.floatValue() != 1.0) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_ANNOTATION_DICTIONARY_SHALL_NOT_CONTAIN_THE_CA_KEY_WITH_A_VALUE_OTHER_THAN_1);
        }
        if (!annotDic.containsKey(PdfName.F)) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_ANNOTATION_DICTIONARY_SHALL_CONTAIN_THE_F_KEY);
        }

        int flags = (int) annotDic.getAsInt(PdfName.F);
        if (!checkFlag(flags, PdfAnnotation.PRINT) || checkFlag(flags, PdfAnnotation.HIDDEN) || checkFlag(flags, PdfAnnotation.INVISIBLE) ||
                checkFlag(flags, PdfAnnotation.NO_VIEW)) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_F_KEYS_PRINT_FLAG_BIT_SHALL_BE_SET_TO_1_AND_ITS_HIDDEN_INVISIBLE_AND_NOVIEW_FLAG_BITS_SHALL_BE_SET_TO_0);
        }
        if (subtype.equals(PdfName.Text) && (!checkFlag(flags, PdfAnnotation.NO_ZOOM) || !checkFlag(flags, PdfAnnotation.NO_ROTATE))) {
            throw new PdfAConformanceException(PdfAConformanceLogMessageConstant.TEXT_ANNOTATIONS_SHOULD_SET_THE_NOZOOM_AND_NOROTATE_FLAG_BITS_OF_THE_F_KEY_TO_1);
        }
        if (annotDic.containsKey(PdfName.C) || annotDic.containsKey(PdfName.IC)) {
            if (!ICC_COLOR_SPACE_RGB.equals(pdfAOutputIntentColorSpace)) {
                throw new PdfAConformanceException(PdfAConformanceException.DESTOUTPUTPROFILE_IN_THE_PDFA1_OUTPUTINTENT_DICTIONARY_SHALL_BE_RGB);
            }
        }

        PdfDictionary ap = annotDic.getAsDictionary(PdfName.AP);
        if (ap != null) {
            if (ap.containsKey(PdfName.D) || ap.containsKey(PdfName.R)) {
                throw new PdfAConformanceException(PdfAConformanceException.APPEARANCE_DICTIONARY_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_STREAM_VALUE);
            }
            if (PdfName.Widget.equals(annotDic.getAsName(PdfName.Subtype)) &&
                    (PdfName.Btn.equals(PdfFormField.getFormType(annotDic)))) {
                if (ap.getAsDictionary(PdfName.N) == null) {
                    throw new PdfAConformanceException(PdfAConformanceException.N_KEY_SHALL_BE_APPEARANCE_SUBDICTIONARY);
                }
            } else {
                if (ap.getAsStream(PdfName.N) == null) {
                    throw new PdfAConformanceException(PdfAConformanceException.APPEARANCE_DICTIONARY_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_STREAM_VALUE);
                }
            }
            checkResourcesOfAppearanceStreams(ap);
        }

        if (PdfName.Widget.equals(subtype) && (annotDic.containsKey(PdfName.AA) || annotDic.containsKey(PdfName.A))) {
            throw new PdfAConformanceException(PdfAConformanceException.WIDGET_ANNOTATION_DICTIONARY_OR_FIELD_DICTIONARY_SHALL_NOT_INCLUDE_A_OR_AA_ENTRY);
        }

        if (annotDic.containsKey(PdfName.AA)) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_ANNOTATION_DICTIONARY_SHALL_NOT_CONTAIN_AA_KEY);
        }

        if (checkStructure(conformanceLevel)) {
            if (contentAnnotations.contains(subtype) && !annotDic.containsKey(PdfName.Contents)) {
                throw new PdfAConformanceException(PdfAConformanceException.ANNOTATION_OF_TYPE_0_SHOULD_HAVE_CONTENTS_KEY).setMessageParams(subtype.getValue());
            }
        }
    }

    @Override
    protected void checkForm(PdfDictionary form) {
        if (form == null)
            return;

        PdfBoolean needAppearances = form.getAsBoolean(PdfName.NeedAppearances);
        if (needAppearances != null && needAppearances.getValue()) {
            throw new PdfAConformanceException(PdfAConformanceException.NEEDAPPEARANCES_FLAG_OF_THE_INTERACTIVE_FORM_DICTIONARY_SHALL_EITHER_NOT_BE_PRESENTED_OR_SHALL_BE_FALSE);
        }

        checkResources(form.getAsDictionary(PdfName.DR));

        PdfArray fields = form.getAsArray(PdfName.Fields);
        if (fields != null) {
            fields = getFormFields(fields);
            for (PdfObject field : fields) {
                PdfDictionary fieldDic = (PdfDictionary) field;
                if (fieldDic.containsKey(PdfName.A) || fieldDic.containsKey(PdfName.AA)) {
                    throw new PdfAConformanceException(PdfAConformanceException.WIDGET_ANNOTATION_DICTIONARY_OR_FIELD_DICTIONARY_SHALL_NOT_INCLUDE_A_OR_AA_ENTRY);
                }
                checkResources(fieldDic.getAsDictionary(PdfName.DR));
            }
        }
    }

    @Override
    protected void checkAction(PdfDictionary action) {
        if (isAlreadyChecked(action)) return;

        PdfName s = action.getAsName(PdfName.S);
        if (getForbiddenActions().contains(s)) {
            throw new PdfAConformanceException(PdfAConformanceException._0_ACTIONS_ARE_NOT_ALLOWED).setMessageParams(s.getValue());
        }
        if (s.equals(PdfName.Named)) {
            PdfName n = action.getAsName(PdfName.N);
            if (n != null && !getAllowedNamedActions().contains(n)) {
                throw new PdfAConformanceException(PdfAConformanceException.NAMED_ACTION_TYPE_0_IS_NOT_ALLOWED).setMessageParams(n.getValue());
            }
        }
        if (s.equals(PdfName.SetState) || s.equals(PdfName.NoOp)) {
            throw new PdfAConformanceException(PdfAConformanceException.DEPRECATED_SETSTATE_AND_NOOP_ACTIONS_ARE_NOT_ALLOWED);
        }
    }

    @Override
    protected void checkCatalogValidEntries(PdfDictionary catalogDict) {
        if (catalogDict.containsKey(PdfName.AA)) {
            throw new PdfAConformanceException(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY);
        }
        if (catalogDict.containsKey(PdfName.OCProperties)) {
            throw new PdfAConformanceException(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_OCPROPERTIES_KEY);
        }
        if (catalogDict.containsKey(PdfName.Names)) {
            if (catalogDict.getAsDictionary(PdfName.Names).containsKey(PdfName.EmbeddedFiles)) {
                throw new PdfAConformanceException(PdfAConformanceException.A_NAME_DICTIONARY_SHALL_NOT_CONTAIN_THE_EMBEDDED_FILES_KEY);
            }
        }
    }

    @Override
    protected void checkPageObject(PdfDictionary pageDict, PdfDictionary pageResources) {
        PdfDictionary actions = pageDict.getAsDictionary(PdfName.AA);
        if (actions != null) {
            for (PdfName key : actions.keySet()) {
                PdfDictionary action = actions.getAsDictionary(key);
                checkAction(action);
            }
        }
        if (isContainsTransparencyGroup(pageDict)) {
            throw new PdfAConformanceException(PdfAConformanceException.A_GROUP_OBJECT_WITH_AN_S_KEY_WITH_A_VALUE_OF_TRANSPARENCY_SHALL_NOT_BE_INCLUDED_IN_A_PAGE_XOBJECT);
        }
    }

    @Override
    protected void checkTrailer(PdfDictionary trailer) {
        if (trailer.containsKey(PdfName.Encrypt)) {
            throw new PdfAConformanceException(PdfAConformanceException.KEYWORD_ENCRYPT_SHALL_NOT_BE_USED_IN_THE_TRAILER_DICTIONARY);
        }
    }

    /**
     * Gets a {@link PdfArray} of fields with kids from a {@link PdfArray} of {@link PdfDictionary} objects.
     *
     * @param array the {@link PdfArray} of form fields {@link PdfDictionary} objects
     *
     * @return the {@link PdfArray} of form fields
     */
    protected PdfArray getFormFields(PdfArray array) {
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

    private int getMaxArrayCapacity() {
        return 8191;
    }

    private int getMaxDictionaryCapacity() {
        return 4095;
    }
}
