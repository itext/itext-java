package com.itextpdf.pdfa.checker;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.pdfa.PdfAConformanceLevel;

import java.util.Arrays;
import java.util.HashSet;

public class PdfA1Checker extends PdfAChecker {

    protected static final HashSet<PdfName> forbiddenAnnotations = new HashSet<>(Arrays.asList(PdfName.Sound, PdfName.Movie, PdfName.FileAttachment));
    static public final HashSet<PdfName> contentAnnotations = new HashSet<PdfName>(Arrays.asList(PdfName.Text,
            PdfName.FreeText, PdfName.Line, PdfName.Square, PdfName.Circle, PdfName.Stamp, PdfName.Ink, PdfName.Popup));

    public PdfA1Checker(PdfAConformanceLevel conformanceLevel, String outputIntentColorSpace) {
        super(conformanceLevel, outputIntentColorSpace);
    }

    @Override
    public void checkCanvasStack(char stackOperation) {

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

    }

    @Override
    protected void checkPdfStream(PdfStream stream) {

    }

    @Override
    protected void checkPdfString(PdfString string) {

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

    }
}
