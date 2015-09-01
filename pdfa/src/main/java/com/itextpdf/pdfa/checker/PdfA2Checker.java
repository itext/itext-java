package com.itextpdf.pdfa.checker;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.pdfa.PdfAConformanceLevel;

import java.util.Arrays;
import java.util.HashSet;

public class PdfA2Checker extends PdfA1Checker{

    protected static final HashSet<PdfName> forbiddenAnnotations = new HashSet<>(Arrays.asList(PdfName._3D, PdfName.Sound, PdfName.Screen, PdfName.Movie));

    public PdfA2Checker(PdfAConformanceLevel conformanceLevel, String outputIntentColorSpace) {
        super(conformanceLevel, outputIntentColorSpace);
    }

    @Override
    public void checkAnnotations(PdfArray annotations) {
        for (PdfObject annotation : annotations) {
            PdfDictionary annotDic = (PdfDictionary) annotation;
            PdfName subtype = annotDic.getAsName(PdfName.Subtype);

            if (subtype == null) {
                throw new PdfAConformanceException(PdfAConformanceException.AnnotationType1IsNotPermitted).setMessageParams("null");
            }
            if (forbiddenAnnotations.contains(subtype)) {
                throw new PdfAConformanceException(PdfAConformanceException.AnnotationType1IsNotPermitted).setMessageParams(subtype.getValue());
            }

            if (!subtype.equals(PdfName.Popup)) {
                PdfNumber f = annotDic.getAsNumber(PdfName.F);
                if (f == null) {
                    throw new PdfAConformanceException(PdfAConformanceException.AnAnnotationDictionaryShallContainTheFKey);
                }
                int flags = f.getIntValue();
                if (!checkFlag(flags, PdfAnnotation.Print)
                        || checkFlag(flags, PdfAnnotation.Hidden)
                        || checkFlag(flags, PdfAnnotation.Invisible)
                        || checkFlag(flags, PdfAnnotation.NoView)
                        || checkFlag(flags, PdfAnnotation.ToggleNoView)) {
                    throw new PdfAConformanceException(PdfAConformanceException.TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleNoviewAndTogglenoviewFlagBitsShallBeSetTo0);
                }
                if (subtype.equals(PdfName.Text)) {
                    if (!checkFlag(flags, PdfAnnotation.NoZoom) || !checkFlag(flags, PdfAnnotation.NoRotate)) {
                        throw new PdfAConformanceException(PdfAConformanceException.TextAnnotationsShouldSetTheNozoomAndNorotateFlagBitsOfTheFKeyTo1);
                    }
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

            PdfDictionary ap = annotDic.getAsDictionary(PdfName.AP);
            if (ap != null) {
                if (ap.containsKey(PdfName.R) || ap.containsKey(PdfName.D)) {
                    throw new PdfAConformanceException(PdfAConformanceException.AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue);
                }
                PdfObject n = ap.get(PdfName.N);
                if (PdfName.Widget.equals(subtype) && PdfName.Btn.equals(annotDic.getAsName(PdfName.FT))) {
                    if (n == null || !n.isDictionary())
                        throw new PdfAConformanceException(PdfAConformanceException.AppearanceDictionaryOfWidgetSubtypeAndBtnFieldTypeShallContainOnlyTheNKeyWithDictionaryValue);
                } else {
                    if (n == null || !n.isStream())
                        throw new PdfAConformanceException(PdfAConformanceException.AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue);
                }
            } else {
                boolean isCorrectRect = false;
                PdfArray rect = annotDic.getAsArray(PdfName.Rect);
                if (rect != null && rect.size() == 4) {
                    PdfNumber index0 = rect.getAsNumber(0);
                    PdfNumber index1 = rect.getAsNumber(1);
                    PdfNumber index2 = rect.getAsNumber(2);
                    PdfNumber index3 = rect.getAsNumber(3);
                    if (index0 != null && index1 != null && index2 != null && index3 != null &&
                            index0.getFloatValue() == index2.getFloatValue() && index1.getFloatValue() == index3.getFloatValue())
                        isCorrectRect = true;
                }
                if (!PdfName.Popup.equals(subtype) &&
                        !PdfName.Link.equals(subtype) &&
                        !isCorrectRect)
                    throw new PdfAConformanceException(PdfAConformanceException.EveryAnnotationShallHaveAtLeastOneAppearanceDictionary);
            }
        }
    }
}
