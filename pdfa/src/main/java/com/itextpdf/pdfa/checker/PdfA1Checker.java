package com.itextpdf.pdfa.checker;

import com.itextpdf.core.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.pdf.PdfAConformanceLevel;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfBoolean;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;
import com.itextpdf.core.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.core.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.pdfa.PdfAConformanceException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PdfA1Checker extends PdfAChecker {

    protected static final Set<PdfName> forbiddenAnnotations = new HashSet<>(Arrays.asList(PdfName.Sound, PdfName.Movie, PdfName.FileAttachment));
    protected static final Set<PdfName> contentAnnotations = new HashSet<>(Arrays.asList(PdfName.Text,
            PdfName.FreeText, PdfName.Line, PdfName.Square, PdfName.Circle, PdfName.Stamp, PdfName.Ink, PdfName.Popup));
    protected static final Set<PdfName> forbiddenActions = new HashSet<>(Arrays.asList(PdfName.Launch, PdfName.Sound, PdfName.Movie,
            PdfName.ResetForm, PdfName.ImportData, PdfName.JavaScript, PdfName.Hide));
    protected static final Set<PdfName> allowedNamedActions = new HashSet<>(Arrays.asList(PdfName.NextPage,
            PdfName.PrevPage, PdfName.FirstPage, PdfName.LastPage));
    protected static final Set<PdfName> allowedRenderingIntents = new HashSet<>(Arrays.asList(PdfName.RelativeColorimetric,
            PdfName.AbsoluteColorimetric, PdfName.Perceptual, PdfName.Saturation));

    public PdfA1Checker(PdfAConformanceLevel conformanceLevel) {
        super(conformanceLevel);
    }

    @Override
    public void checkCanvasStack(char stackOperation) {
        if ('q' == stackOperation) {
            if (++gsStackDepth > PdfA1Checker.maxGsStackDepth)
                throw new PdfAConformanceException(PdfAConformanceException.GraphicStateStackDepthIsGreaterThan28);
        } else if ('Q' == stackOperation) {
            gsStackDepth--;
        }
    }

    @Override
    public void checkInlineImage(PdfStream inlineImage, PdfDictionary currentColorSpaces) {
        PdfObject filter = inlineImage.get(PdfName.Filter);
        if (filter instanceof PdfName) {
            if (filter.equals(PdfName.LZWDecode)) {
                throw new PdfAConformanceException(PdfAConformanceException.LZWDecodeFilterIsNotPermitted);
            }
        } else if (filter instanceof PdfArray) {
            for (int i = 0; i < ((PdfArray) filter).size(); i++) {
                PdfName f = ((PdfArray) filter).getAsName(i);
                if (f.equals(PdfName.LZWDecode)) {
                    throw new PdfAConformanceException(PdfAConformanceException.LZWDecodeFilterIsNotPermitted);
                }
            }
        }

        checkImage(inlineImage, currentColorSpaces);
    }

    @Override
    public void checkColor(Color color, PdfDictionary currentColorSpaces, Boolean fill) {
        checkColorSpace(color.getColorSpace(), currentColorSpaces, true, fill);
    }

    @Override
    public void checkColorSpace(PdfColorSpace colorSpace, PdfDictionary currentColorSpaces, boolean checkAlternate, Boolean fill) {
        if (colorSpace instanceof PdfSpecialCs.Separation) {
            colorSpace = ((PdfSpecialCs.Separation) colorSpace).getBaseCs();
        } else if (colorSpace instanceof PdfSpecialCs.DeviceN) {
            colorSpace = ((PdfSpecialCs.DeviceN) colorSpace).getBaseCs();
        }

        if (colorSpace instanceof PdfDeviceCs.Rgb) {
            if (cmykIsUsed) {
                throw new PdfAConformanceException(PdfAConformanceException.DevicergbAndDevicecmykColorspacesCannotBeUsedBothInOneFile);
            }
            rgbIsUsed = true;
        } else if (colorSpace instanceof PdfDeviceCs.Cmyk) {
            if (rgbIsUsed) {
                throw new PdfAConformanceException(PdfAConformanceException.DevicergbAndDevicecmykColorspacesCannotBeUsedBothInOneFile);
            }
            cmykIsUsed = true;
        } else if (colorSpace instanceof PdfDeviceCs.Gray) {
            grayIsUsed = true;
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
            throw new PdfAConformanceException(PdfAConformanceException.IfDeviceRgbCmykGrayUsedInFileThatFileShallContainPdfaOutputIntent);
        }

        if (rgbIsUsed) {
            if (!ICC_COLOR_SPACE_RGB.equals(pdfAOutputIntentColorSpace)) {
                throw new PdfAConformanceException(PdfAConformanceException.DevicergbMayBeUsedOnlyIfTheFileHasARgbPdfAOutputIntent);
            }
        }
        if (cmykIsUsed) {
            if (!ICC_COLOR_SPACE_CMYK.equals(pdfAOutputIntentColorSpace)) {
                throw new PdfAConformanceException(PdfAConformanceException.DevicecmykMayBeUsedOnlyIfTheFileHasACmykPdfAOutputIntent);
            }
        }
    }

    @Override
    public void checkExtGState(CanvasGraphicsState extGState) {
        if (extGState.getTransferFunction() != null) {
            throw new PdfAConformanceException(PdfAConformanceException.AnExtgstateDictionaryShallNotContainTheTrKey);
        }
        PdfObject transferFunction2 = extGState.getTransferFunction2();
        if (transferFunction2 != null && !PdfName.Default.equals(transferFunction2)) {
            throw new PdfAConformanceException(PdfAConformanceException.AnExtgstateDictionaryShallNotContainTheTR2KeyWithAValueOtherThanDefault);
        }

        checkRenderingIntent(extGState.getRenderingIntent());

        PdfObject softMask = extGState.getSoftMask();
        if (softMask != null && !PdfName.None.equals(softMask)) {
            throw new PdfAConformanceException(PdfAConformanceException.TheSmaskKeyIsNotAllowedInExtgstate);
        }

        PdfObject bm = extGState.getBlendMode();
        if (bm != null && !PdfName.Normal.equals(bm) && !PdfName.Compatible.equals(bm)) {
            throw new PdfAConformanceException(PdfAConformanceException.BlendModeShallHhaveValueNormalOrCompatible);
        }

        Float ca = extGState.getStrokeOpacity();
        if (ca != null && ca != 1) {
            throw new PdfAConformanceException(PdfAConformanceException.TransparencyIsNotAllowedCAShallBeEqualTo1);
        }

        ca = extGState.getFillOpacity();
        if (ca != null && ca != 1) {
            throw new PdfAConformanceException(PdfAConformanceException.TransparencyIsNotAllowedCaShallBeEqualTo1);
        }
    }

    @Override
    public void checkRenderingIntent(PdfName intent) {
        if (intent == null)
            return;

        if (!allowedRenderingIntents.contains(intent)) {
            throw new PdfAConformanceException(PdfAConformanceException.IfSpecifiedRenderingShallBeOneOfTheFollowingRelativecolorimetricAbsolutecolorimetricPerceptualOrSaturation);
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
            colorSpace = PdfColorSpace.makeColorSpace(colorSpaceObj, null);
            checkColorSpace(colorSpace, currentColorSpaces, true, null);
            checkedObjectsColorspace.put(image, colorSpace);
        }

        if (image.containsKey(PdfName.Alternates)) {
            throw new PdfAConformanceException(PdfAConformanceException.AnImageDictionaryShallNotContainAlternatesKey);
        }
        if (image.containsKey(PdfName.OPI)) {
            throw new PdfAConformanceException(PdfAConformanceException.AnImageDictionaryShallNotContainOpiKey);
        }

        if (image.containsKey(PdfName.Interpolate) && image.getAsBool(PdfName.Interpolate)) {
            throw new PdfAConformanceException(PdfAConformanceException.TheValueOfInterpolateKeyShallNotBeTrue);
        }

        checkRenderingIntent(image.getAsName(PdfName.Intent));

        if (image.containsKey(PdfName.SMask) && !PdfName.None.equals(image.getAsName(PdfName.SMask))) {
            throw new PdfAConformanceException(PdfAConformanceException.TheSmaskKeyIsNotAllowedInXobjects);
        }
    }

    @Override
    protected void checkFormXObject(PdfStream form) {
        if (isAlreadyChecked(form)) return;

        if (form.containsKey(PdfName.OPI)) {
            throw new PdfAConformanceException(PdfAConformanceException.AFormXobjectDictionaryShallNotContainOpiKey);
        }
        if (form.containsKey(PdfName.PS)) {
            throw new PdfAConformanceException(PdfAConformanceException.AFormXobjectDictionaryShallNotContainPSKey);
        }
        if (PdfName.PS.equals(form.getAsName(PdfName.Subtype2))) {
            throw new PdfAConformanceException(PdfAConformanceException.AFormXobjectDictionaryShallNotContainSubtype2KeyWithAValueOfPS);
        }

        if (form.containsKey(PdfName.SMask)) {
            throw new PdfAConformanceException(PdfAConformanceException.TheSmaskKeyIsNotAllowedInXobjects);
        }

        if (form.containsKey(PdfName.Group) && PdfName.Transparency.equals(form.getAsDictionary(PdfName.Group).getAsName(PdfName.S))) {
            throw new PdfAConformanceException(PdfAConformanceException.AGroupObjectWithAnSKeyWithAValueOfTransparencyShallNotBeIncludedInAFormXobject);
        }
    }

    @Override
    protected void checkLogicalStructure(PdfDictionary catalog) {
        if (checkStructure(conformanceLevel)) {
            PdfDictionary markInfo = catalog.getAsDictionary(PdfName.MarkInfo);
            if (markInfo == null || markInfo.getAsBoolean(PdfName.Marked) == null || !markInfo.getAsBoolean(PdfName.Marked).getValue()) {
                throw new PdfAConformanceException(PdfAConformanceException.CatalogShallIncludeMarkInfoDictionaryWithMarkedTrueValue);
            }
            if (!catalog.containsKey(PdfName.Lang)) {
                throw new PdfAConformanceException(PdfAConformanceException.CatalogShallContainLangEntry);
            }
        }
    }

    @Override
    protected  void checkMetaData(PdfDictionary catalog){
        if (!catalog.containsKey(PdfName.Metadata)) {
            throw new PdfAConformanceException(PdfAConformanceException.CatalogShallContainMetadataEntry);
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
                throw new PdfAConformanceException(PdfAConformanceException.IfOutputintentsArrayHasMoreThanOneEntryWithDestoutputprofileKeyTheSameIndirectObjectShallBeUsedAsTheValueOfThatObject);
            }
        }

    }

    @Override
    protected void checkPdfNumber(PdfNumber number) {
        if (Math.abs(number.getLongValue()) > getMaxRealValue() && number.toString().contains(".")) {
            throw new PdfAConformanceException(PdfAConformanceException.RealNumberIsOutOfRange);
        }
    }

    protected double getMaxRealValue() {
        return 32767;
    }

    @Override
    protected void checkPdfStream(PdfStream stream) {
        if (stream.containsKey(PdfName.F) || stream.containsKey(PdfName.FFilter) || stream.containsKey(PdfName.FDecodeParams)) {
            throw new PdfAConformanceException(PdfAConformanceException.StreamObjDictShallNotContainForFFilterOrFDecodeParams);
        }

        PdfObject filter = stream.get(PdfName.Filter);
        if (filter instanceof PdfName) {
            if (filter.equals(PdfName.LZWDecode))
                throw new PdfAConformanceException(PdfAConformanceException.LZWDecodeFilterIsNotPermitted);
        } else if (filter instanceof PdfArray) {
            for (PdfObject f : ((PdfArray) filter)) {
                if (f.equals(PdfName.LZWDecode))
                    throw new PdfAConformanceException(PdfAConformanceException.LZWDecodeFilterIsNotPermitted);
            }
        }
    }

    @Override
    protected void checkPdfString(PdfString string) {
        if (string.getValue().getBytes().length > getMaxStringLength()) {
            throw new PdfAConformanceException(PdfAConformanceException.PdfStringIsTooLong);
        }
    }

    protected int getMaxStringLength() {
        return 65535;
    }

    @Override
    protected  void checkPageSize(PdfDictionary  page){

    }

    @Override
    protected void checkFileSpec(PdfDictionary fileSpec) {
        if (fileSpec.containsKey(PdfName.EF)) {
            throw new PdfAConformanceException(PdfAConformanceException.FileSpecificationDictionaryShallNotContainTheEFKey);
        }
    }

    @Override
    protected void checkAnnotation(PdfDictionary annotDic) {
        PdfName subtype = annotDic.getAsName(PdfName.Subtype);

        if (subtype == null) {
            throw new PdfAConformanceException(PdfAConformanceException.AnnotationType1IsNotPermitted).setMessageParams("null");
        }
        if (forbiddenAnnotations.contains(subtype)) {
            throw new PdfAConformanceException(PdfAConformanceException.AnnotationType1IsNotPermitted).setMessageParams(subtype.getValue());
        }
        PdfNumber ca = annotDic.getAsNumber(PdfName.CA);
        if (ca != null && ca.getFloatValue() != 1.0) {
            throw new PdfAConformanceException(PdfAConformanceException.AnAnnotationDictionaryShallNotContainTheCaKeyWithAValueOtherThan1);
        }
        if (!annotDic.containsKey(PdfName.F)) {
            throw new PdfAConformanceException(PdfAConformanceException.AnnotationShallContainKeyF);
        }

        int flags = annotDic.getAsInt(PdfName.F);
        if (!checkFlag(flags, PdfAnnotation.Print) || checkFlag(flags, PdfAnnotation.Hidden) || checkFlag(flags, PdfAnnotation.Invisible) ||
                checkFlag(flags, PdfAnnotation.NoView)) {
            throw new PdfAConformanceException(PdfAConformanceException.TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleAndNoviewFlagBitsShallBeSetTo0);
        }
        if (subtype.equals(PdfName.Text) && (!checkFlag(flags, PdfAnnotation.NoZoom) || !checkFlag(flags, PdfAnnotation.NoRotate))) {
            throw new PdfAConformanceException(PdfAConformanceException.TextAnnotationsShouldSetTheNozoomAndNorotateFlagBitsOfTheFKeyTo1);
        }
        if (annotDic.containsKey(PdfName.C) || annotDic.containsKey(PdfName.IC)) {
            if (!ICC_COLOR_SPACE_RGB.equals(pdfAOutputIntentColorSpace)) {
                throw new PdfAConformanceException(PdfAConformanceException.DestoutputprofileInThePdfa1OutputintentDictionaryShallBeRgb);
            }
        }

        PdfDictionary ap = annotDic.getAsDictionary(PdfName.AP);
        if (ap != null) {
            if (ap.containsKey(PdfName.D) || ap.containsKey(PdfName.R)) {
                throw new PdfAConformanceException(PdfAConformanceException.AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue);
            }
            PdfStream n = ap.getAsStream(PdfName.N);
            if (n == null) {
                throw new PdfAConformanceException(PdfAConformanceException.AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue);
            }
        }

        if (PdfName.Widget.equals(subtype) && (annotDic.containsKey(PdfName.AA) || annotDic.containsKey(PdfName.A))) {
            throw new PdfAConformanceException(PdfAConformanceException.WidgetAnnotationDictionaryOrFieldDictionaryShallNotIncludeAOrAAEntry);
        }

        if (checkStructure(conformanceLevel)) {
            if (contentAnnotations.contains(subtype) && !annotDic.containsKey(PdfName.Contents)) {
                throw new PdfAConformanceException(PdfAConformanceException.AnnotationOfType1ShouldHaveContentsKey).setMessageParams(subtype);
            }
        }
    }

    @Override
    protected void checkForm(PdfDictionary form) {
        if (form == null)
            return;

        PdfBoolean needAppearances = form.getAsBoolean(PdfName.NeedAppearances);
        if (needAppearances != null && needAppearances.getValue()) {
            throw new PdfAConformanceException(PdfAConformanceException.NeedAppearancesFlagOfTheInteractiveFormDictionaryShallEitherNotBePresentedOrShallBeFalse);
        }

        PdfArray fields = form.getAsArray(PdfName.Fields);
        if (fields != null) {
            fields = getFormFields(fields);
            for (PdfObject field : fields) {
                PdfDictionary fieldDic = (PdfDictionary) field;
                if (fieldDic.containsKey(PdfName.A) || fieldDic.containsKey(PdfName.AA)) {
                    throw new PdfAConformanceException(PdfAConformanceException.WidgetAnnotationDictionaryOrFieldDictionaryShallNotIncludeAOrAAEntry);
                }
            }
        }
    }

    protected void checkAction(PdfDictionary action) {
        if (isAlreadyChecked(action)) return;

        PdfName s = action.getAsName(PdfName.S);
        if (getForbiddenActions().contains(s)) {
            throw new PdfAConformanceException(PdfAConformanceException._1ActionsIsNotAllowed).setMessageParams(s.getValue());
        }
        if (s.equals(PdfName.Named)) {
            PdfName n = action.getAsName(PdfName.N);
            if (n != null && !getAllowedNamedActions().contains(n)) {
                throw new PdfAConformanceException(PdfAConformanceException.NamedActionType1IsNotAllowed).setMessageParams(n.getValue());
            }
        }
        if (s.equals(PdfName.SetState) || s.equals(PdfName.NoOp)) {
            throw new PdfAConformanceException(PdfAConformanceException.DeprecatedSetStateAndNoOpActionsAreNotAllowed);
        }
    }

    @Override
    protected void checkCatalogValidEntries(PdfDictionary catalogDict) {
        if (catalogDict.containsKey(PdfName.AA)) {
            throw new PdfAConformanceException(PdfAConformanceException.CatalogDictionaryShallNotContainAAEntry);
        }
        if (catalogDict.containsKey(PdfName.OCProperties)) {
            throw new PdfAConformanceException(PdfAConformanceException.CatalogDictionaryShallNotContainOCPropertiesKey);
        }
        if (catalogDict.containsKey(PdfName.Names)) {
            if (catalogDict.getAsDictionary(PdfName.Names).containsKey(PdfName.EmbeddedFiles)) {
                throw new PdfAConformanceException(PdfAConformanceException.NameDictionaryShallNotContainTheEmbeddedFilesKey);
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
        if (pageDict.containsKey(PdfName.Group) && PdfName.Transparency.equals(pageDict.getAsDictionary(PdfName.Group).getAsName(PdfName.S))) {
            throw new PdfAConformanceException(PdfAConformanceException.AGroupObjectWithAnSKeyWithAValueOfTransparencyShallNotBeIncludedInAPageObject);
        }
    }

    @Override
    protected void checkTrailer(PdfDictionary trailer) {
        if (trailer.containsKey(PdfName.Encrypt)) {
            throw new PdfAConformanceException(PdfAConformanceException.EncryptShallNotBeUsedInTrailerDictionary);
        }
    }

    private PdfArray getFormFields(PdfArray array) {
        PdfArray fields = new PdfArray();
        for (PdfObject field : array) {
            PdfDictionary fieldDic = (PdfDictionary) field;
            PdfArray kids = fieldDic.getAsArray(PdfName.Kids);
            fields.add(field);
            if (kids != null) {
                fields.addAll(getFormFields(kids));
            }
        }
        return fields;
    }
}