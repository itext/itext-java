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
import com.itextpdf.io.colors.IccProfile;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.Jpeg2000ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfTrueTypeFont;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.font.Type3Glyph;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.logs.PdfAConformanceLogMessageConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PdfA2Checker defines the requirements of the PDF/A-2 standard and contains a
 * number of methods that override the implementations of its superclass
 * {@link PdfA1Checker}.
 * <p>
 * The specification implemented by this class is ISO 19005-2
 */
public class PdfA2Checker extends PdfA1Checker {

    protected static final Set<PdfName> forbiddenAnnotations = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName._3D,
                    PdfName.Sound,
                    PdfName.Screen,
                    PdfName.Movie)));
    protected static final Set<PdfName> forbiddenActions = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.Launch,
                    PdfName.Sound,
                    PdfName.Movie,
                    PdfName.ResetForm,
                    PdfName.ImportData,
                    PdfName.JavaScript,
                    PdfName.Hide,
                    PdfName.SetOCGState,
                    PdfName.Rendition,
                    PdfName.Trans,
                    PdfName.GoTo3DView)));
    protected static final Set<PdfName> allowedBlendModes = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.Normal,
                    PdfName.Compatible,
                    PdfName.Multiply,
                    PdfName.Screen,
                    PdfName.Overlay,
                    PdfName.Darken,
                    PdfName.Lighten,
                    PdfName.ColorDodge,
                    PdfName.ColorBurn,
                    PdfName.HardLight,
                    PdfName.SoftLight,
                    PdfName.Difference,
                    PdfName.Exclusion,
                    PdfName.Hue,
                    PdfName.Saturation,
                    PdfName.Color,
                    PdfName.Luminosity)));

    static final int MAX_PAGE_SIZE = 14400;
    static final int MIN_PAGE_SIZE = 3;
    private static final int MAX_NUMBER_OF_DEVICEN_COLOR_COMPONENTS = 32;

    private boolean currentFillCsIsIccBasedCMYK = false;
    private boolean currentStrokeCsIsIccBasedCMYK = false;

    private Map<PdfName, PdfArray> separationColorSpaces = new HashMap<>();

    private Set<PdfObject> transparencyObjects = new HashSet<>();

    /**
     * Creates a PdfA2Checker with the required conformance level
     *
     * @param conformanceLevel the required conformance level, <code>a</code> or
     *                         <code>u</code> or <code>b</code>
     */
    public PdfA2Checker(PdfAConformanceLevel conformanceLevel) {
        super(conformanceLevel);
    }

    @Override
    public void checkInlineImage(PdfStream inlineImage, PdfDictionary currentColorSpaces) {
        PdfObject filter = inlineImage.get(PdfName.Filter);
        if (filter instanceof PdfName) {
            if (filter.equals(PdfName.LZWDecode))
                throw new PdfAConformanceException(PdfAConformanceException.LZWDECODE_FILTER_IS_NOT_PERMITTED);
            if (filter.equals(PdfName.Crypt)) {
                throw new PdfAConformanceException(PdfAConformanceException.CRYPT_FILTER_IS_NOT_PERMITTED_INLINE_IMAGE);
            }
        } else if (filter instanceof PdfArray) {
            for (int i = 0; i < ((PdfArray) filter).size(); i++) {
                PdfName f = ((PdfArray) filter).getAsName(i);
                if (f.equals(PdfName.LZWDecode))
                    throw new PdfAConformanceException(PdfAConformanceException.LZWDECODE_FILTER_IS_NOT_PERMITTED);
                if (f.equals(PdfName.Crypt)) {
                    throw new PdfAConformanceException(PdfAConformanceException.CRYPT_FILTER_IS_NOT_PERMITTED_INLINE_IMAGE);
                }
            }
        }

        checkImage(inlineImage, currentColorSpaces);
    }

    @Override
    public void checkColor(Color color, PdfDictionary currentColorSpaces, Boolean fill, PdfStream contentStream) {
        if (color instanceof PatternColor) {
            PdfPattern pattern = ((PatternColor) color).getPattern();
            if (pattern instanceof PdfPattern.Shading) {
                PdfDictionary shadingDictionary = ((PdfPattern.Shading) pattern).getShading();
                PdfObject colorSpace = shadingDictionary.get(PdfName.ColorSpace);
                checkColorSpace(PdfColorSpace.makeColorSpace(colorSpace), currentColorSpaces, true, true);
                final PdfDictionary extGStateDict = ((PdfDictionary) pattern.getPdfObject()).getAsDictionary(PdfName.ExtGState);
                CanvasGraphicsState gState = new UpdateCanvasGraphicsState(extGStateDict);
                checkExtGState(gState, contentStream);
            } else if (pattern instanceof PdfPattern.Tiling) {
                checkContentStream((PdfStream) pattern.getPdfObject());
            }
        }

        super.checkColor(color, currentColorSpaces, fill, contentStream);
    }

    @Override
    public void checkColorSpace(PdfColorSpace colorSpace, PdfDictionary currentColorSpaces, boolean checkAlternate, Boolean fill) {
        if (fill != null) {
            if ((boolean) fill) {
                currentFillCsIsIccBasedCMYK = false;
            } else {
                currentStrokeCsIsIccBasedCMYK = false;
            }
        }

        if (colorSpace instanceof PdfSpecialCs.Separation) {

            PdfSpecialCs.Separation separation = (PdfSpecialCs.Separation) colorSpace;
            checkSeparationCS((PdfArray) separation.getPdfObject());
            if (checkAlternate) {
                checkColorSpace(separation.getBaseCs(), currentColorSpaces, false, fill);
            }

        } else if (colorSpace instanceof PdfSpecialCs.DeviceN) {

            PdfSpecialCs.DeviceN deviceN = (PdfSpecialCs.DeviceN) colorSpace;
            if (deviceN.getNumberOfComponents() > MAX_NUMBER_OF_DEVICEN_COLOR_COMPONENTS) {
                throw new PdfAConformanceException(PdfAConformanceException.
                        THE_NUMBER_OF_COLOR_COMPONENTS_IN_DEVICE_N_COLORSPACE_SHOULD_NOT_EXCEED,
                        MAX_NUMBER_OF_DEVICEN_COLOR_COMPONENTS);
            }
            //TODO DEVSIX-4203 Fix IndexOutOfBounds exception being thrown for DeviceN (not NChannel) colorspace without
            // attributes. According to the spec PdfAConformanceException should be thrown.
            PdfDictionary attributes = ((PdfArray) deviceN.getPdfObject()).getAsDictionary(4);
            PdfDictionary colorants = attributes.getAsDictionary(PdfName.Colorants);
            //TODO DEVSIX-4203 Colorants dictionary is mandatory in PDF/A-2 spec. Need to throw an appropriate exception
            // if it is not present.
            if (colorants != null) {
                for (Map.Entry<PdfName, PdfObject> entry : colorants.entrySet()) {
                    PdfArray separation = (PdfArray) entry.getValue();
                    checkSeparationInsideDeviceN(separation, ((PdfArray) deviceN.getPdfObject()).get(2), ((PdfArray) deviceN.getPdfObject()).get(3));
                }
            }

            if (checkAlternate) {
                checkColorSpace(deviceN.getBaseCs(), currentColorSpaces, false, fill);
            }

        } else if (colorSpace instanceof PdfSpecialCs.Indexed) {
            if (checkAlternate) {
                checkColorSpace(((PdfSpecialCs.Indexed) colorSpace).getBaseCs(), currentColorSpaces, true, fill);
            }
        } else if (colorSpace instanceof PdfSpecialCs.UncoloredTilingPattern) {
            if (checkAlternate) {
                checkColorSpace(((PdfSpecialCs.UncoloredTilingPattern) colorSpace).getUnderlyingColorSpace(), currentColorSpaces, true, fill);
            }
        } else {

            if (colorSpace instanceof PdfDeviceCs.Rgb) {
                if (!checkDefaultCS(currentColorSpaces, fill, PdfName.DefaultRGB, 3)) {
                    rgbIsUsed = true;
                }
            } else if (colorSpace instanceof PdfDeviceCs.Cmyk) {
                if (!checkDefaultCS(currentColorSpaces, fill, PdfName.DefaultCMYK, 4)) {
                    cmykIsUsed = true;
                }
            } else if (colorSpace instanceof PdfDeviceCs.Gray) {
                if (!checkDefaultCS(currentColorSpaces, fill, PdfName.DefaultGray, 1)) {
                    grayIsUsed = true;
                }
            }
        }

        if (fill != null && colorSpace instanceof PdfCieBasedCs.IccBased) {
            byte[] iccBytes = ((PdfArray) colorSpace.getPdfObject()).getAsStream(1).getBytes();
            if (ICC_COLOR_SPACE_CMYK.equals(IccProfile.getIccColorSpaceName(iccBytes))) {
                if ((boolean) fill) {
                    currentFillCsIsIccBasedCMYK = true;
                } else {
                    currentStrokeCsIsIccBasedCMYK = true;
                }
            }
        }
    }

    @Override
    public void checkExtGState(CanvasGraphicsState extGState, PdfStream contentStream) {
        if (Integer.valueOf(1).equals(extGState.getOverprintMode())) {
            if (extGState.getFillOverprint() && currentFillCsIsIccBasedCMYK) {
                throw new PdfAConformanceException(PdfAConformanceException.OVERPRINT_MODE_SHALL_NOT_BE_ONE_WHEN_AN_ICCBASED_CMYK_COLOUR_SPACE_IS_USED_AND_WHEN_OVERPRINTING_IS_SET_TO_TRUE);
            }
            if (extGState.getStrokeOverprint() && currentStrokeCsIsIccBasedCMYK) {
                throw new PdfAConformanceException(PdfAConformanceException.OVERPRINT_MODE_SHALL_NOT_BE_ONE_WHEN_AN_ICCBASED_CMYK_COLOUR_SPACE_IS_USED_AND_WHEN_OVERPRINTING_IS_SET_TO_TRUE);
            }
        }

        if (extGState.getTransferFunction() != null) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_EXTGSTATE_DICTIONARY_SHALL_NOT_CONTAIN_THE_TR_KEY);
        }
        if (extGState.getHTP() != null) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_EXTGSTATE_DICTIONARY_SHALL_NOT_CONTAIN_THE_HTP_KEY);
        }

        PdfObject transferFunction2 = extGState.getTransferFunction2();
        if (transferFunction2 != null && !PdfName.Default.equals(transferFunction2)) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_EXTGSTATE_DICTIONARY_SHALL_NOT_CONTAIN_THE_TR_2_KEY_WITH_A_VALUE_OTHER_THAN_DEFAULT);
        }

        if (extGState.getHalftone() instanceof PdfDictionary) {
            PdfDictionary halftoneDict = (PdfDictionary) extGState.getHalftone();
            Integer halftoneType = halftoneDict.getAsInt(PdfName.HalftoneType);
            if (halftoneType != 1 && halftoneType != 5) {
                throw new PdfAConformanceException(PdfAConformanceException.ALL_HALFTONES_SHALL_HAVE_HALFTONETYPE_1_OR_5);
            }

            if (halftoneDict.containsKey(PdfName.HalftoneName)) {
                throw new PdfAConformanceException(PdfAConformanceException.HALFTONES_SHALL_NOT_CONTAIN_HALFTONENAME);
            }
        }

        checkRenderingIntent(extGState.getRenderingIntent());

        if (extGState.getSoftMask() != null && extGState.getSoftMask() instanceof PdfDictionary) {
            transparencyObjects.add(contentStream);
        }
        if (extGState.getStrokeOpacity() < 1) {
            transparencyObjects.add(contentStream);
        }
        if (extGState.getFillOpacity() < 1) {
            transparencyObjects.add(contentStream);
        }

        PdfObject bm = extGState.getBlendMode();
        if (bm != null) {
            if (!PdfName.Normal.equals(bm)) {
                transparencyObjects.add(contentStream);
            }
            if (bm instanceof PdfArray) {
                for (PdfObject b : (PdfArray) bm) {
                    checkBlendMode((PdfName) b);
                }
            } else if (bm instanceof PdfName) {
                checkBlendMode((PdfName) bm);
            }
        }
    }

    @Override
    public void checkSignature(PdfDictionary signatureDict) {
        if (isAlreadyChecked(signatureDict)) {
            return;
        }

        PdfArray references = signatureDict.getAsArray(PdfName.Reference);
        if (references != null) {
            for (int i = 0; i < references.size(); i++) {
                PdfDictionary referenceDict = references.getAsDictionary(i);
                if (referenceDict.containsKey(PdfName.DigestLocation)
                        || referenceDict.containsKey(PdfName.DigestMethod)
                        || referenceDict.containsKey(PdfName.DigestValue)) {
                    throw new PdfAConformanceException(
                            PdfAConformanceException.SIGNATURE_REFERENCES_DICTIONARY_SHALL_NOT_CONTAIN_DIGESTLOCATION_DIGESTMETHOD_DIGESTVALUE);
                }
            }
        }
    }

    @Override
    protected void checkNonSymbolicTrueTypeFont(PdfTrueTypeFont trueTypeFont) {
        String encoding = trueTypeFont.getFontEncoding().getBaseEncoding();
        // non-symbolic true type font will always has an encoding entry in font dictionary in itext
        if (!PdfEncodings.WINANSI.equals(encoding) && !PdfEncodings.MACROMAN.equals(encoding)) {
            throw new PdfAConformanceException(PdfAConformanceException.ALL_NON_SYMBOLIC_TRUE_TYPE_FONT_SHALL_SPECIFY_MAC_ROMAN_ENCODING_OR_WIN_ANSI_ENCODING, trueTypeFont);
        }

        // if font has differences array, itext ensures that all names in it are listed in AdobeGlyphList
    }

    @Override
    protected double getMaxRealValue() {
        return Float.MAX_VALUE;
    }

    @Override
    protected int getMaxStringLength() {
        return 32767;
    }
    @Override
    protected void checkPdfArray(PdfArray array) {
        // currently no validation for arrays is implemented for PDF/A 2
    }

    @Override
    protected void checkPdfDictionary(PdfDictionary dictionary) {
        // currently no validation for dictionaries is implemented for PDF/A 2
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

        if (!subtype.equals(PdfName.Popup)) {
            PdfNumber f = annotDic.getAsNumber(PdfName.F);
            if (f == null) {
                throw new PdfAConformanceException(PdfAConformanceException.AN_ANNOTATION_DICTIONARY_SHALL_CONTAIN_THE_F_KEY);
            }
            int flags = f.intValue();
            if (!checkFlag(flags, PdfAnnotation.PRINT)
                    || checkFlag(flags, PdfAnnotation.HIDDEN)
                    || checkFlag(flags, PdfAnnotation.INVISIBLE)
                    || checkFlag(flags, PdfAnnotation.NO_VIEW)
                    || checkFlag(flags, PdfAnnotation.TOGGLE_NO_VIEW)) {
                throw new PdfAConformanceException(PdfAConformanceException.THE_F_KEYS_PRINT_FLAG_BIT_SHALL_BE_SET_TO_1_AND_ITS_HIDDEN_INVISIBLE_NOVIEW_AND_TOGGLENOVIEW_FLAG_BITS_SHALL_BE_SET_TO_0);
            }
            if (subtype.equals(PdfName.Text)) {
                if (!checkFlag(flags, PdfAnnotation.NO_ZOOM) || !checkFlag(flags, PdfAnnotation.NO_ROTATE)) {
                    throw new PdfAConformanceException(PdfAConformanceLogMessageConstant.TEXT_ANNOTATIONS_SHOULD_SET_THE_NOZOOM_AND_NOROTATE_FLAG_BITS_OF_THE_F_KEY_TO_1);
                }
            }
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

        PdfDictionary ap = annotDic.getAsDictionary(PdfName.AP);
        if (ap != null) {
            if (ap.containsKey(PdfName.R) || ap.containsKey(PdfName.D)) {
                throw new PdfAConformanceException(PdfAConformanceException.APPEARANCE_DICTIONARY_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_STREAM_VALUE);
            }
            PdfObject n = ap.get(PdfName.N);
            if (PdfName.Widget.equals(subtype) && PdfName.Btn.equals(PdfFormField.getFormType(annotDic))) {
                if (n == null || !n.isDictionary())
                    throw new PdfAConformanceException(PdfAConformanceException.APPEARANCE_DICTIONARY_OF_WIDGET_SUBTYPE_AND_BTN_FIELD_TYPE_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_DICTIONARY_VALUE);
            } else {
                if (n == null || !n.isStream())
                    throw new PdfAConformanceException(PdfAConformanceException.APPEARANCE_DICTIONARY_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_STREAM_VALUE);
            }

            checkResourcesOfAppearanceStreams(ap);
        } else {
            boolean isCorrectRect = false;
            PdfArray rect = annotDic.getAsArray(PdfName.Rect);
            if (rect != null && rect.size() == 4) {
                PdfNumber index0 = rect.getAsNumber(0);
                PdfNumber index1 = rect.getAsNumber(1);
                PdfNumber index2 = rect.getAsNumber(2);
                PdfNumber index3 = rect.getAsNumber(3);
                if (index0 != null && index1 != null && index2 != null && index3 != null &&
                        index0.floatValue() == index2.floatValue() && index1.floatValue() == index3.floatValue())
                    isCorrectRect = true;
            }
            if (!PdfName.Popup.equals(subtype) &&
                    !PdfName.Link.equals(subtype) &&
                    !isCorrectRect)
                throw new PdfAConformanceException(PdfAConformanceException.EVERY_ANNOTATION_SHALL_HAVE_AT_LEAST_ONE_APPEARANCE_DICTIONARY);
        }
    }

    @Override
    protected void checkAppearanceStream(PdfStream appearanceStream) {
        if (isAlreadyChecked(appearanceStream)) {
            return;
        }

        if (isContainsTransparencyGroup(appearanceStream)) {
            this.transparencyObjects.add(appearanceStream);
        }
        checkResources(appearanceStream.getAsDictionary(PdfName.Resources));
    }

    @Override
    protected void checkForm(PdfDictionary form) {
        if (form != null) {
            PdfBoolean needAppearances = form.getAsBoolean(PdfName.NeedAppearances);
            if (needAppearances != null && needAppearances.getValue()) {
                throw new PdfAConformanceException(PdfAConformanceException.NEEDAPPEARANCES_FLAG_OF_THE_INTERACTIVE_FORM_DICTIONARY_SHALL_EITHER_NOT_BE_PRESENTED_OR_SHALL_BE_FALSE);
            }
            if (form.containsKey(PdfName.XFA)) {
                throw new PdfAConformanceException(PdfAConformanceException.THE_INTERACTIVE_FORM_DICTIONARY_SHALL_NOT_CONTAIN_THE_XFA_KEY);
            }
            checkResources(form.getAsDictionary(PdfName.DR));

            PdfArray fields = form.getAsArray(PdfName.Fields);
            if (fields != null) {
                fields = getFormFields(fields);
                for (PdfObject field : fields) {
                    PdfDictionary fieldDic = (PdfDictionary) field;
                    checkResources(fieldDic.getAsDictionary(PdfName.DR));
                }
            }
        }
    }

    @Override
    protected void checkCatalogValidEntries(PdfDictionary catalogDict) {
        if (catalogDict.containsKey(PdfName.NeedsRendering)) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_THE_NEEDSRENDERING_KEY);
        }

        if (catalogDict.containsKey(PdfName.AA)) {
            throw new PdfAConformanceException(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY);
        }

        if (catalogDict.containsKey(PdfName.Requirements)) {
            throw new PdfAConformanceException(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_REQUIREMENTS_ENTRY);
        }

        PdfDictionary permissions = catalogDict.getAsDictionary(PdfName.Perms);
        if (permissions != null) {
            for (PdfName dictKey : permissions.keySet()) {
                if (PdfName.DocMDP.equals(dictKey)) {
                    PdfDictionary signatureDict = permissions.getAsDictionary(PdfName.DocMDP);
                    if (signatureDict != null) {
                        checkSignature(signatureDict);
                    }
                } else if (PdfName.UR3.equals(dictKey)) {
                } else {
                    throw new PdfAConformanceException(PdfAConformanceException.NO_KEYS_OTHER_THAN_UR3_AND_DOC_MDP_SHALL_BE_PRESENT_IN_A_PERMISSIONS_DICTIONARY);
                }
            }
        }

        PdfDictionary namesDictionary = catalogDict.getAsDictionary(PdfName.Names);
        if (namesDictionary != null && namesDictionary.containsKey(PdfName.AlternatePresentations)) {
            throw new PdfAConformanceException(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_ALTERNATEPRESENTATIONS_NAMES_ENTRY);
        }

        PdfDictionary oCProperties = catalogDict.getAsDictionary(PdfName.OCProperties);
        if (oCProperties != null) {
            List<PdfDictionary> configList = new ArrayList<>();
            PdfDictionary d = oCProperties.getAsDictionary(PdfName.D);
            if (d != null) {
                configList.add(d);
            }
            PdfArray configs = oCProperties.getAsArray(PdfName.Configs);
            if (configs != null) {
                for (PdfObject config : configs) {
                    configList.add((PdfDictionary) config);
                }
            }

            HashSet<PdfObject> ocgs = new HashSet<>();
            PdfArray ocgsArray = oCProperties.getAsArray(PdfName.OCGs);
            if (ocgsArray != null) {
                for (PdfObject ocg : ocgsArray) {
                    ocgs.add(ocg);
                }
            }

            HashSet<String> names = new HashSet<>();

            for (PdfDictionary config : configList) {
                checkCatalogConfig(config, ocgs, names);
            }
        }
    }

    @Override
    protected void checkPageSize(PdfDictionary page) {
        PdfName[] boxNames = new PdfName[]{PdfName.MediaBox, PdfName.CropBox, PdfName.TrimBox, PdfName.ArtBox, PdfName.BleedBox};
        for (PdfName boxName : boxNames) {
            Rectangle box = page.getAsRectangle(boxName);
            if (box != null) {
                float width = box.getWidth();
                float height = box.getHeight();
                if (width < MIN_PAGE_SIZE || width > MAX_PAGE_SIZE || height < MIN_PAGE_SIZE || height > MAX_PAGE_SIZE)
                    throw new PdfAConformanceException(PdfAConformanceException.THE_PAGE_LESS_3_UNITS_NO_GREATER_14400_IN_EITHER_DIRECTION);
            }
        }
    }

    @Override
    protected void checkFileSpec(PdfDictionary fileSpec) {
        if (fileSpec.containsKey(PdfName.EF)) {
            if (!fileSpec.containsKey(PdfName.F) || !fileSpec.containsKey(PdfName.UF)) {
                throw new PdfAConformanceException(PdfAConformanceException.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY);
            }
            if (!fileSpec.containsKey(PdfName.Desc)) {
                Logger logger = LoggerFactory.getLogger(PdfAChecker.class);
                logger.warn(PdfAConformanceLogMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHOULD_CONTAIN_DESC_KEY);
            }

            PdfDictionary ef = fileSpec.getAsDictionary(PdfName.EF);
            PdfStream embeddedFile = ef.getAsStream(PdfName.F);
            if (embeddedFile == null) {
                throw new PdfAConformanceException(PdfAConformanceException.EF_KEY_OF_FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_DICTIONARY_WITH_VALID_F_KEY);
            }
            // iText doesn't check whether provided file is compliant to PDF-A specs.
            Logger logger = LoggerFactory.getLogger(PdfAChecker.class);
            logger.warn(PdfAConformanceLogMessageConstant.EMBEDDED_FILE_SHALL_BE_COMPLIANT_WITH_SPEC);
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
            if (filter.equals(PdfName.Crypt)) {
                PdfDictionary decodeParams = stream.getAsDictionary(PdfName.DecodeParms);
                if (decodeParams != null) {
                    PdfName cryptFilterName = decodeParams.getAsName(PdfName.Name);
                    if (cryptFilterName != null && !cryptFilterName.equals(PdfName.Identity)) {
                        throw new PdfAConformanceException(PdfAConformanceException.NOT_IDENTITY_CRYPT_FILTER_IS_NOT_PERMITTED);
                    }
                }
            }
        } else if (filter instanceof PdfArray) {
            for (int i = 0; i < ((PdfArray) filter).size(); i++) {
                PdfName f = ((PdfArray) filter).getAsName(i);
                if (f.equals(PdfName.LZWDecode))
                    throw new PdfAConformanceException(PdfAConformanceException.LZWDECODE_FILTER_IS_NOT_PERMITTED);
                if (f.equals(PdfName.Crypt)) {
                    PdfArray decodeParams = stream.getAsArray(PdfName.DecodeParms);
                    if (decodeParams != null && i < decodeParams.size()) {
                        PdfDictionary decodeParam = decodeParams.getAsDictionary(i);
                        PdfName cryptFilterName = decodeParam.getAsName(PdfName.Name);
                        if (cryptFilterName != null && !cryptFilterName.equals(PdfName.Identity)) {
                            throw new PdfAConformanceException(PdfAConformanceException.NOT_IDENTITY_CRYPT_FILTER_IS_NOT_PERMITTED);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void checkPageObject(PdfDictionary pageDict, PdfDictionary pageResources) {
        if (pageDict.containsKey(PdfName.AA)) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_PAGE_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY);
        }

        if (pageDict.containsKey(PdfName.PresSteps)) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_PAGE_DICTIONARY_SHALL_NOT_CONTAIN_PRESSTEPS_ENTRY);
        }

        if (isContainsTransparencyGroup(pageDict)) {
            PdfObject cs = pageDict.getAsDictionary(PdfName.Group).get(PdfName.CS);
            if (cs != null) {
                PdfDictionary currentColorSpaces = pageResources.getAsDictionary(PdfName.ColorSpace);
                checkColorSpace(PdfColorSpace.makeColorSpace(cs), currentColorSpaces, true, null);
            }
        }
    }

    @Override
    protected void checkPageTransparency(PdfDictionary pageDict, PdfDictionary pageResources) {
        if (pdfAOutputIntentColorSpace == null
                && transparencyObjects.size() > 0
                && (pageDict.getAsDictionary(PdfName.Group) == null || pageDict.getAsDictionary(PdfName.Group).get(PdfName.CS) == null)) {
            if (transparencyObjects.contains(pageDict)) {
                throw new PdfAConformanceException(PdfAConformanceException.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE);
            }
            checkContentsForTransparency(pageDict);
            checkAnnotationsForTransparency(pageDict.getAsArray(PdfName.Annots));
            checkResourcesForTransparency(pageResources, new HashSet<PdfObject>());
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

        if (destOutputProfile != null) {
            String deviceClass = IccProfile.getIccDeviceClass(((PdfStream) destOutputProfile).getBytes());
            if (!ICC_DEVICE_CLASS_OUTPUT_PROFILE.equals(deviceClass) && !ICC_DEVICE_CLASS_MONITOR_PROFILE.equals(deviceClass)) {
                throw new PdfAConformanceException(PdfAConformanceException.PROFILE_STREAM_OF_OUTPUTINTENT_SHALL_BE_OUTPUT_PROFILE_PRTR_OR_MONITOR_PROFILE_MNTR);
            }

            String cs = IccProfile.getIccColorSpaceName(((PdfStream) destOutputProfile).getBytes());
            if (!ICC_COLOR_SPACE_RGB.equals(cs) && !ICC_COLOR_SPACE_CMYK.equals(cs) && !ICC_COLOR_SPACE_GRAY.equals(cs)) {
                throw new PdfAConformanceException(PdfAConformanceException.OUTPUT_INTENT_COLOR_SPACE_SHALL_BE_EITHER_GRAY_RGB_OR_CMYK);
            }
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
    protected void checkColorsUsages() {
        if ((rgbIsUsed || cmykIsUsed || grayIsUsed) && pdfAOutputIntentColorSpace == null) {
            throw new PdfAConformanceException(PdfAConformanceException.IF_DEVICE_RGB_CMYK_GRAY_USED_IN_FILE_THAT_FILE_SHALL_CONTAIN_PDFA_OUTPUTINTENT_OR_DEFAULT_RGB_CMYK_GRAY_IN_USAGE_CONTEXT);
        }

        if (rgbIsUsed) {
            if (!ICC_COLOR_SPACE_RGB.equals(pdfAOutputIntentColorSpace)) {
                throw new PdfAConformanceException(PdfAConformanceException.DEVICERGB_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_RGB_PDFA_OUTPUT_INTENT_OR_DEFAULTRGB_IN_USAGE_CONTEXT);
            }
        }
        if (cmykIsUsed) {
            if (!ICC_COLOR_SPACE_CMYK.equals(pdfAOutputIntentColorSpace)) {
                throw new PdfAConformanceException(PdfAConformanceException.DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT);
            }
        }
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

        if (image.getAsStream(PdfName.SMask) != null) {
            transparencyObjects.add(image);
        }

        if (image.containsKey(PdfName.SMaskInData) && image.getAsInt(PdfName.SMaskInData) > 0) {
            transparencyObjects.add(image);
        }

        if (PdfName.JPXDecode.equals(image.get(PdfName.Filter))) {
            Jpeg2000ImageData jpgImage = (Jpeg2000ImageData) ImageDataFactory.createJpeg2000(image.getBytes(false));
            Jpeg2000ImageData.Parameters params = jpgImage.getParameters();

            /* Concerning !params.isJpxBaseline check
             *
             * In pdf/a-2 ISO (ISO 19005-2:2011  6.2.8.3 JPEG2000) is stated that:
             * "Only the JPX baseline set of features, ... , shall be used."
             *
             * Also in jpeg2000 ISO (ISO/IEC 15444-2:2004   Annex M: M.9.2 Support for JPX feature set) is stated that:
             * "In general, a JPX reader is not required to support the entire set of features defined within this Recommendation |International Standard.
             * However, to promote interoperability, the following baseline set of features is defined. Files that
             * are written in such a way as to allow a reader that supports only this JPX baseline set of features to properly open the
             * file shall contain a CLi field in the File Type box with the value 'jpxb' (0x6a70 7862); all JPX baseline readers are
             * required to properly support all files with this code in the compatibility list in the File Type box."
             *
             * Therefore, I assumed that a file, which doesn't has the jpxb flag (which can be checked with the isJpxBaseline flag)
             * uses not only JPX baseline set of features.
             *
             * But, all the test files used in iText5 failed on this check, so may be my assumption is wrong.
             */
            if (!params.isJp2 /*|| !params.isJpxBaseline*/) {
                throw new PdfAConformanceException(PdfAConformanceException.ONLY_JPX_BASELINE_SET_OF_FEATURES_SHALL_BE_USED);
            }

            if (params.numOfComps != 1 && params.numOfComps != 3 && params.numOfComps != 4) {
                throw new PdfAConformanceException(PdfAConformanceException.THE_NUMBER_OF_COLOUR_CHANNELS_IN_THE_JPEG2000_DATA_SHALL_BE_1_3_OR_4);
            }

            if (params.colorSpecBoxes != null && params.colorSpecBoxes.size() > 1) {
                int numOfApprox0x01 = 0;
                for (Jpeg2000ImageData.ColorSpecBox colorSpecBox : params.colorSpecBoxes) {
                    if (colorSpecBox.getApprox() == 1) {
                        ++numOfApprox0x01;
                        if (numOfApprox0x01 == 1 &&
                                colorSpecBox.getMeth() != 1 && colorSpecBox.getMeth() != 2 && colorSpecBox.getMeth() != 3) {
                            throw new PdfAConformanceException(PdfAConformanceException.THE_VALUE_OF_THE_METH_ENTRY_IN_COLR_BOX_SHALL_BE_1_2_OR_3);
                        }

                        if (image.get(PdfName.ColorSpace) == null) {
                            switch (colorSpecBox.getEnumCs()) {
                                case 1:
                                    PdfDeviceCs.Gray deviceGrayCs = new PdfDeviceCs.Gray();
                                    checkColorSpace(deviceGrayCs, currentColorSpaces, true, null);
                                    checkedObjectsColorspace.put(image, deviceGrayCs);
                                    break;
                                case 3:
                                    PdfDeviceCs.Rgb deviceRgbCs = new PdfDeviceCs.Rgb();
                                    checkColorSpace(deviceRgbCs, currentColorSpaces, true, null);
                                    checkedObjectsColorspace.put(image, deviceRgbCs);
                                    break;
                                case 12:
                                    PdfDeviceCs.Cmyk deviceCmykCs = new PdfDeviceCs.Cmyk();
                                    checkColorSpace(deviceCmykCs, currentColorSpaces, true, null);
                                    checkedObjectsColorspace.put(image, deviceCmykCs);
                                    break;
                            }
                        }
                    }
                    if (colorSpecBox.getEnumCs() == 19) {
                        throw new PdfAConformanceException(PdfAConformanceException.JPEG2000_ENUMERATED_COLOUR_SPACE_19_CIEJAB_SHALL_NOT_BE_USED);
                    }
                }
                if (numOfApprox0x01 != 1) {
                    throw new PdfAConformanceException(PdfAConformanceException.EXACTLY_ONE_COLOUR_SPACE_SPECIFICATION_SHALL_HAVE_THE_VALUE_0X01_IN_THE_APPROX_FIELD);
                }
            }

            if (jpgImage.getBpc() < 1 || jpgImage.getBpc() > 38) {
                throw new PdfAConformanceException(PdfAConformanceException.THE_BIT_DEPTH_OF_THE_JPEG2000_DATA_SHALL_HAVE_A_VALUE_IN_THE_RANGE_1_TO_38);
            }

            // The Bits Per Component box specifies the bit depth of each component.
            // If the bit depth of all components in the codestream is the same (in both sign and precision),
            // then this box shall not be found. Otherwise, this box specifies the bit depth of each individual component.
            if (params.bpcBoxData != null) {
                throw new PdfAConformanceException(PdfAConformanceException.ALL_COLOUR_CHANNELS_IN_THE_JPEG2000_DATA_SHALL_HAVE_THE_SAME_BIT_DEPTH);
            }
        }
    }

    @Override
    public void checkFontGlyphs(PdfFont font, PdfStream contentStream) {
        if (font instanceof PdfType3Font) {
            checkType3FontGlyphs((PdfType3Font) font, contentStream);
        }
    }

    @Override
    protected void checkFormXObject(PdfStream form) {
        checkFormXObject(form, null);
    }

    /**
     * Verify the conformity of the Form XObject with appropriate
     * specification. Throws PdfAConformanceException if any discrepancy was found
     *
     * @param form the {@link PdfStream} to be checked
     * @param contentStream the {@link PdfStream} current content stream
     */
    protected void checkFormXObject(PdfStream form, PdfStream contentStream) {
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

        if (isContainsTransparencyGroup(form)) {
            if (contentStream != null) {
                transparencyObjects.add(contentStream);
            } else {
                transparencyObjects.add(form);
            }
            PdfObject cs = form.getAsDictionary(PdfName.Group).get(PdfName.CS);
            PdfDictionary resources = form.getAsDictionary(PdfName.Resources);
            if (cs != null && resources != null) {
                PdfDictionary currentColorSpaces = resources.getAsDictionary(PdfName.ColorSpace);
                checkColorSpace(PdfColorSpace.makeColorSpace(cs), currentColorSpaces, true, null);
            }
        }

        checkResources(form.getAsDictionary(PdfName.Resources));
        checkContentStream(form);
    }

    private void checkContentsForTransparency(PdfDictionary pageDict) {
        PdfStream contentStream = pageDict.getAsStream(PdfName.Contents);
        if (contentStream != null && transparencyObjects.contains(contentStream)) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE);
        } else {
            PdfArray contentSteamArray = pageDict.getAsArray(PdfName.Contents);
            if (contentSteamArray != null) {
                for (int i = 0; i < contentSteamArray.size(); i++) {
                    if (transparencyObjects.contains(contentSteamArray.get(i))) {
                        throw new PdfAConformanceException(PdfAConformanceException.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE);
                    }
                }
            }
        }
    }

    private void checkAnnotationsForTransparency(PdfArray annotations) {
        if (annotations == null) {
            return;
        }
        for (int i = 0; i < annotations.size(); ++i) {
            PdfDictionary annot = annotations.getAsDictionary(i);
            PdfDictionary ap = annot.getAsDictionary(PdfName.AP);
            if (ap != null) {
                checkAppearanceStreamForTransparency(ap, new HashSet<PdfObject>());
            }
        }
    }

    private void checkAppearanceStreamForTransparency(PdfDictionary ap, Set<PdfObject> checkedObjects) {
        if (checkedObjects.contains(ap)) {
            return;
        } else {
            checkedObjects.add(ap);
        }
        for (final PdfObject val : ap.values()) {
            if (this.transparencyObjects.contains(val)) {
                throw new PdfAConformanceException(PdfAConformanceException.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE);
            } else if (val.isDictionary()) {
                checkAppearanceStreamForTransparency((PdfDictionary) val, checkedObjects);
            } else if (val.isStream()) {
                checkObjectWithResourcesForTransparency(val, checkedObjects);
            }
        }
    }

    private void checkObjectWithResourcesForTransparency(PdfObject objectWithResources, Set<PdfObject> checkedObjects) {
        if (checkedObjects.contains(objectWithResources)) {
            return;
        } else {
            checkedObjects.add(objectWithResources);
        }

        if (this.transparencyObjects.contains(objectWithResources)) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE);
        }
        if (objectWithResources instanceof PdfDictionary) {
            checkResourcesForTransparency(((PdfDictionary) objectWithResources).getAsDictionary(PdfName.Resources), checkedObjects);
        }
    }

    private void checkResourcesForTransparency(PdfDictionary resources, Set<PdfObject> checkedObjects) {
        if (resources != null) {
            checkSingleResourceTypeForTransparency(resources.getAsDictionary(PdfName.XObject), checkedObjects);
            checkSingleResourceTypeForTransparency(resources.getAsDictionary(PdfName.Pattern), checkedObjects);
        }
    }

    private void checkSingleResourceTypeForTransparency(PdfDictionary singleResourceDict, Set<PdfObject> checkedObjects) {
        if (singleResourceDict != null) {
            for (PdfObject resource : singleResourceDict.values()) {
                checkObjectWithResourcesForTransparency(resource, checkedObjects);
            }
        }
    }

    private void checkBlendMode(PdfName blendMode) {
        if (!allowedBlendModes.contains(blendMode)) {
            throw new PdfAConformanceException(PdfAConformanceException.ONLY_STANDARD_BLEND_MODES_SHALL_BE_USED_FOR_THE_VALUE_OF_THE_BM_KEY_IN_AN_EXTENDED_GRAPHIC_STATE_DICTIONARY);
        }
    }

    private void checkSeparationInsideDeviceN(PdfArray separation, PdfObject deviceNColorSpace, PdfObject deviceNTintTransform) {
        if (!isAltCSIsTheSame(separation.get(2), deviceNColorSpace) ||
                !deviceNTintTransform.equals(separation.get(3))) {
            Logger logger = LoggerFactory.getLogger(PdfAChecker.class);
            logger.warn(PdfAConformanceLogMessageConstant.TINT_TRANSFORM_AND_ALTERNATE_SPACE_OF_SEPARATION_ARRAYS_IN_THE_COLORANTS_OF_DEVICE_N_SHOULD_BE_CONSISTENT_WITH_SAME_ATTRIBUTES_OF_DEVICE_N);
        }
        checkSeparationCS(separation);
    }

    private void checkSeparationCS(PdfArray separation) {
        if (separationColorSpaces.containsKey(separation.getAsName(0))) {
            boolean altCSIsTheSame;
            boolean tintTransformIsTheSame;

            PdfArray sameNameSeparation = separationColorSpaces.get(separation.getAsName(0));
            PdfObject cs1 = separation.get(2);
            PdfObject cs2 = sameNameSeparation.get(2);
            altCSIsTheSame = isAltCSIsTheSame(cs1, cs2);
            // TODO(DEVSIX-1672) in fact need to check if objects content is equal. ISO 19005-2, 6.2.4.4 "Separation and DeviceN colour spaces":
            // In evaluating equivalence, the PDF objects shall be compared, rather than the computational
            // result of the use of those PDF objects. Compression and whether or not an object is direct or indirect shall be ignored.
            PdfObject f1Obj = separation.get(3);
            PdfObject f2Obj = sameNameSeparation.get(3);
            //Can be a stream or dict
            boolean bothAllowedType = (f1Obj.getType() == f2Obj.getType()) && (f1Obj.isDictionary() || f1Obj.isStream());
            //Check if the indirect references are equal
            tintTransformIsTheSame = bothAllowedType && f1Obj.equals(f2Obj);


            if (!altCSIsTheSame || !tintTransformIsTheSame) {
                throw new PdfAConformanceException(PdfAConformanceException.TINT_TRANSFORM_AND_ALTERNATE_SPACE_SHALL_BE_THE_SAME_FOR_THE_ALL_SEPARATION_CS_WITH_THE_SAME_NAME);
            }
        } else {
            separationColorSpaces.put(separation.getAsName(0), separation);
        }

    }

    private boolean isAltCSIsTheSame(PdfObject cs1, PdfObject cs2) {
        boolean altCSIsTheSame = false;
        if (cs1 instanceof PdfName) {
            altCSIsTheSame = cs1.equals(cs2);
        } else if (cs1 instanceof PdfArray && cs2 instanceof PdfArray) {
            // TODO(DEVSIX-1672) in fact need to check if objects content is equal. ISO 19005-2, 6.2.4.4 "Separation and DeviceN colour spaces":
            // In evaluating equivalence, the PDF objects shall be compared, rather than the computational
            // result of the use of those PDF objects. Compression and whether or not an object is direct or indirect shall be ignored.
            altCSIsTheSame = ((PdfArray) cs1).get(0).equals(((PdfArray) cs1).get(0));
        }
        return altCSIsTheSame;
    }

    private void checkCatalogConfig(PdfDictionary config, HashSet<PdfObject> ocgs, HashSet<String> names)  {
        PdfString name = config.getAsString(PdfName.Name);
        if (name == null) {
            throw new PdfAConformanceException(PdfAConformanceException.OPTIONAL_CONTENT_CONFIGURATION_DICTIONARY_SHALL_CONTAIN_NAME_ENTRY);
        }
        if (!names.add(name.toUnicodeString())) {
            throw new PdfAConformanceException(PdfAConformanceException.VALUE_OF_NAME_ENTRY_SHALL_BE_UNIQUE_AMONG_ALL_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARIES);
        }
        if (config.containsKey(PdfName.AS)) {
            throw new PdfAConformanceException(PdfAConformanceException.THE_AS_KEY_SHALL_NOT_APPEAR_IN_ANY_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARY);
        }
        PdfArray orderArray = config.getAsArray(PdfName.Order);
        if (orderArray != null) {
            HashSet<PdfObject> order = new HashSet<>();
            fillOrderRecursively(orderArray, order);
            if (!order.equals(ocgs)) {
                throw new PdfAConformanceException(
                        PdfAConformanceException.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS);
            }
        }
    }

    private void fillOrderRecursively(PdfArray orderArray, Set<PdfObject> order) {
        for (PdfObject orderItem : orderArray) {
            if (!orderItem.isArray()) {
                order.add(orderItem);
            } else {
                fillOrderRecursively((PdfArray) orderItem, order);
            }
        }
    }

    private boolean checkDefaultCS(PdfDictionary currentColorSpaces, Boolean fill, PdfName defaultCsName, int numOfComponents) {
        if (currentColorSpaces == null)
            return false;
        if (!currentColorSpaces.containsKey(defaultCsName))
            return false;

        PdfObject defaultCsObj = currentColorSpaces.get(defaultCsName);
        PdfColorSpace defaultCs = PdfColorSpace.makeColorSpace(defaultCsObj);
        if (defaultCs instanceof PdfDeviceCs)
            throw new PdfAConformanceException(PdfAConformanceException.COLOR_SPACE_0_SHALL_BE_DEVICE_INDEPENDENT).setMessageParams(defaultCsName.toString());

        if (defaultCs.getNumberOfComponents() != numOfComponents)
            throw new PdfAConformanceException(PdfAConformanceException.COLOR_SPACE_0_SHALL_HAVE_1_COMPONENTS).setMessageParams(defaultCsName.getValue(), numOfComponents);

        checkColorSpace(defaultCs, currentColorSpaces, false, fill);
        return true;
    }

    private void checkType3FontGlyphs(PdfType3Font font, PdfStream contentStream) {
        for (int i = 0; i <= PdfFont.SIMPLE_FONT_MAX_CHAR_CODE_VALUE; ++i) {
            FontEncoding fontEncoding = font.getFontEncoding();
            if (fontEncoding.canDecode(i)) {
                Type3Glyph type3Glyph = font.getType3Glyph(fontEncoding.getUnicode(i));
                if (type3Glyph != null) {
                    checkFormXObject(type3Glyph.getContentStream(), contentStream);
                }
            }
        }
    }

    private static final class UpdateCanvasGraphicsState extends CanvasGraphicsState {
        public UpdateCanvasGraphicsState(PdfDictionary extGStateDict) {
            updateFromExtGState(new PdfExtGState(extGStateDict));
        }
    }
}
