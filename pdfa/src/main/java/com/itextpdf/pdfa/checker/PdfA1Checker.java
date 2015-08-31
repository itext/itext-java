package com.itextpdf.pdfa.checker;

import com.itextpdf.basics.color.IccProfile;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.pdfa.PdfAConformanceLevel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class PdfA1Checker extends PdfAChecker {

    protected static final HashSet<PdfName> forbiddenAnnotations = new HashSet<>(Arrays.asList(PdfName.Sound, PdfName.Movie, PdfName.FileAttachment));
    static public final HashSet<PdfName> contentAnnotations = new HashSet<PdfName>(Arrays.asList(PdfName.Text,
            PdfName.FreeText, PdfName.Line, PdfName.Square, PdfName.Circle, PdfName.Stamp, PdfName.Ink, PdfName.Popup));

    public PdfA1Checker(PdfAConformanceLevel conformanceLevel) {
        super(conformanceLevel);
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
                throw new PdfAConformanceException("annotation.type.1.is.not.permitted").setMessageParams("null");
            }
            if (forbiddenAnnotations.contains(subtype)) {
                throw new PdfAConformanceException("annotation.type.1.is.not.permitted").setMessageParams(subtype.getValue());
            }
            PdfNumber ca = annotDic.getAsNumber(PdfName.CA);
            if (ca != null && ca.getFloatValue() != 1.0) {
                throw new PdfAConformanceException("an.annotation.dictionary.shall.not.contain.the.ca.key.with.a.value.other.than.1");
            }
            if (!annotDic.containsKey(PdfName.F)) {
                throw new PdfAConformanceException("annotation.shall.contain.key.F");
            }

            int flags = annotDic.getAsInt(PdfName.F);
            if (!checkFlag(flags, PdfAnnotation.Print) || checkFlag(flags, PdfAnnotation.Hidden) || checkFlag(flags, PdfAnnotation.Invisible) ||
                    checkFlag(flags, PdfAnnotation.NoView)) {
                throw new PdfAConformanceException("the.f.keys.print.flag.bit.shall.be.set.to.1.and.its.hidden.invisible.and.noview.flag.bits.shall.be.set.to.0");
            }
            if (subtype.equals(PdfName.Text) && (!checkFlag(flags, PdfAnnotation.NoZoom) || !checkFlag(flags, PdfAnnotation.NoRotate))) {
                throw new PdfAConformanceException("text.annotations.should.set.the.nozoom.and.norotate.flag.bits.of.the.f.key.to.1");
            }
            if (annotDic.containsKey(PdfName.C) || annotDic.containsKey(PdfName.IC)) {
                //Checks if color space of the DestOutputProfile is RGB
                //@TODO Postpone this until DEVSIX-254 is implemented.
            }

            PdfDictionary ap = annotDic.getAsDictionary(PdfName.AP);
            if (ap != null) {
                if (ap.containsKey(PdfName.D) || ap.containsKey(PdfName.R)) {
                    throw new PdfAConformanceException("appearance.dictionary.shall.contain.only.the.n.key.with.stream.value");
                }
                PdfStream n = ap.getAsStream(PdfName.N);
                if (n == null) {
                    throw new PdfAConformanceException("appearance.dictionary.shall.contain.only.the.n.key.with.stream.value");
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
        }
    }
}
