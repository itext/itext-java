package com.itextpdf.pdfa.checker;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.pdfa.PdfAConformanceLevel;

import java.util.Arrays;
import java.util.HashSet;

public class PdfA1Checker extends PdfAChecker {


    static public final int maxGsStackDepth = 28;
    protected int gsStackDepth = 0;

    protected static final HashSet<PdfName> forbiddenAnnotations = new HashSet<>(Arrays.asList(PdfName.Sound, PdfName.Movie, PdfName.FileAttachment));
    static public final HashSet<PdfName> contentAnnotations = new HashSet<PdfName>(Arrays.asList(PdfName.Text,
            PdfName.FreeText, PdfName.Line, PdfName.Square, PdfName.Circle, PdfName.Stamp, PdfName.Ink, PdfName.Popup));

    public PdfA1Checker(PdfAConformanceLevel conformanceLevel, String outputIntentColorSpace) {
        super(conformanceLevel, outputIntentColorSpace);
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
    public void checkInlineImage(PdfImageXObject inlineImage) {

    }

    @Override
    protected void checkAction(PdfDictionary action) {

    }

    @Override
    protected void checkExtGState(PdfDictionary extGState) {

    }

    @Override
    protected void checkImageXObject(PdfStream image) {

    }

    @Override
    protected void checkFormXObject(PdfStream form) {

    }

    @Override
    protected void checkFont(PdfDictionary font) {

    }

    @Override
    protected void checkPdfNumber(PdfNumber number) {
        if (Math.abs(number.getLongValue()) > getMaxRealValue() && number.toString().contains(".")) {
            throw new PdfAConformanceException(PdfAConformanceException.RealNumberIsOutOfRange);
        }
    }

    protected double getMaxRealValue(){
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

    protected int getMaxStringLength(){
        return  65535;
    }

    @Override
    protected void checkAnnotations(PdfArray annotations) {
        for (PdfObject annotation : annotations) {
            PdfDictionary annotDic = (PdfDictionary) annotation;
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
                if (!ICC_COLOR_SPACE_RGB.equalsIgnoreCase(pdfAOutputIntentColorSpace)) {
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
    }

    @Override
    protected void checkForm(PdfDictionary form) {
        PdfBoolean needAppearances = form.getAsBoolean(PdfName.NeedAppearances);
        if (needAppearances != null && needAppearances.getValue()) {
            throw new PdfAConformanceException("needappearances.flag.of.the.interactive.form.dictionary.shall.either.not.be.present.or.shall.be.false");
        }
    }

    @Override
    protected void checkCatalog(PdfDictionary catalog) {
        if (catalog.containsKey(PdfName.OCProperties)) {
            throw new PdfAConformanceException(PdfAConformanceException.CatalogDictionaryShallNotContainOCPropertiesKey);
        }
    }

    @Override
    protected void checkTrailer(PdfDictionary trailer){
        if (trailer.get(PdfName.Encrypt) != null) {
            throw new PdfAConformanceException(PdfAConformanceException.EncryptShallNotBeUsedInTrailerDictionary);
        }
    }

}
