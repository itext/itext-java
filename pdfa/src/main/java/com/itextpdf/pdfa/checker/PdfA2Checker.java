package com.itextpdf.pdfa.checker;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.pdfa.PdfAConformanceLevel;

import java.util.Arrays;
import java.util.HashSet;

public class PdfA2Checker extends PdfA1Checker{

    protected static final HashSet<PdfName> forbiddenAnnotations = new HashSet<>(Arrays.asList(PdfName._3D, PdfName.Sound, PdfName.Screen, PdfName.Movie));

    public PdfA2Checker(PdfAConformanceLevel conformanceLevel) {
        super(conformanceLevel);
    }

    @Override
    public void checkAnnotations(PdfArray annotations) {
        for (PdfObject annotation : annotations) {
            PdfDictionary annotDic = (PdfDictionary) annotation;
            PdfName subtype = annotDic.getAsName(PdfName.Subtype);

            if (subtype == null) {
                throw new PdfAConformanceException("annotation.type.1.is.not.permitted").setMessageParams("null");
            }
            if (forbiddenAnnotations.contains(subtype)) {
                throw new PdfAConformanceException("annotation.type.1.is.not.permitted").setMessageParams(subtype.getValue());
            }

            if (!subtype.equals(PdfName.Popup)) {
                PdfNumber f = annotDic.getAsNumber(PdfName.F);
                if (f == null) {
                    throw new PdfAConformanceException("an.annotation.dictionary.shall.contain.the.f.key");
                }
                int flags = f.getIntValue();
                if (!checkFlag(flags, PdfAnnotation.Print)
                        || checkFlag(flags, PdfAnnotation.Hidden)
                        || checkFlag(flags, PdfAnnotation.Invisible)
                        || checkFlag(flags, PdfAnnotation.NoView)
                        || checkFlag(flags, PdfAnnotation.ToggleNoView)) {
                    throw new PdfAConformanceException("the.f.keys.print.flag.bit.shall.be.set.to.1.and.its.hidden.invisible.noview.and.togglenoview.flag.bits.shall.be.set.to.0");
                }
                if (subtype.equals(PdfName.Text)) {
                    if (!checkFlag(flags, PdfAnnotation.NoZoom) || !checkFlag(flags, PdfAnnotation.NoRotate)) {
                        throw new PdfAConformanceException("text.annotations.should.set.the.nozoom.and.norotate.flag.bits.of.the.f.key.to.1");
                    }
                }
            }

            if (PdfName.Widget.equals(subtype) && (annotDic.containsKey(PdfName.AA) || annotDic.containsKey(PdfName.A))) {
                throw new PdfAConformanceException("widget.annotation.dictionary.or.field.dictionary.shall.not.include.a.or.aa.entry");
            }

            if (checkStructure(conformanceLevel)) {
                if (contentAnnotations.contains(subtype) && !annotDic.containsKey(PdfName.Contents)) {
                    throw new PdfAConformanceException("annotation.of.type.1.should.have.contents.key").setMessageParams(subtype);
                }
            }

            PdfDictionary ap = annotDic.getAsDictionary(PdfName.AP);
            if (ap != null) {
                if (ap.containsKey(PdfName.R) || ap.containsKey(PdfName.D)) {
                    throw new PdfAConformanceException("appearance.dictionary.shall.contain.only.the.n.key.with.stream.value");
                }
                PdfObject n = ap.get(PdfName.N);
                if (PdfName.Widget.equals(subtype) && PdfName.Btn.equals(annotDic.getAsName(PdfName.FT))) {
                    if (n == null || !n.isDictionary())
                        throw new PdfAConformanceException("appearance.dictionary.of.widget.subtype.and.btn.field.type.shall.contain.only.the.n.key.with.dictionary.value");
                } else {
                    if (n == null || !n.isStream())
                        throw new PdfAConformanceException("appearance.dictionary.shall.contain.only.the.n.key.with.stream.value");
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
                    throw new PdfAConformanceException("every.annotation.shall.have.at.least.one.appearance.dictionary");
            }
        }
    }
}
