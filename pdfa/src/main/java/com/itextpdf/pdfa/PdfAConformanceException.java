package com.itextpdf.pdfa;

import com.itextpdf.basics.PdfException;

public class PdfAConformanceException extends PdfException {

    public static final String AnAnnotationDictionaryShallContainTheFKey = "an.annotation.dictionary.shall.contain.the.f.key";
    public static final String AnAnnotationDictionaryShallNotContainTheCaKeyWithAValueOtherThan1 = "an.annotation.dictionary.shall.not.contain.the.ca.key.with.a.value.other.than.1";
    public static final String AnnotationOfType1ShouldHaveContentsKey = "annotation.of.type.1.should.have.contents.key";
    public static final String AnnotationType1IsNotPermitted = "annotation.type.1.is.not.permitted";
    public static final String AnnotationShallContainKeyF = "annotation.shall.contain.key.F";
    public static final String AppearanceDictionaryOfWidgetSubtypeAndBtnFieldTypeShallContainOnlyTheNKeyWithDictionaryValue = "appearance.dictionary.of.widget.subtype.and.btn.field.type.shall.contain.only.the.n.key.with.dictionary.value";
    public static final String AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue = "appearance.dictionary.shall.contain.only.the.n.key.with.stream.value";
    public static final String DestoutputprofileInThePdfa1OutputintentDictionaryShallBeRgb = "destoutputprofile.in.the.pdfa1.outputintent.dictionary.shall.be.rgb";
    public static final String EveryAnnotationShallHaveAtLeastOneAppearanceDictionary = "every.annotation.shall.have.at.least.one.appearance.dictionary";
    public static final String TextAnnotationsShouldSetTheNozoomAndNorotateFlagBitsOfTheFKeyTo1 = "text.annotations.should.set.the.nozoom.and.norotate.flag.bits.of.the.f.key.to.1";
    public static final String TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleAndNoviewFlagBitsShallBeSetTo0 = "the.f.keys.print.flag.bit.shall.be.set.to.1.and.its.hidden.invisible.and.noview.flag.bits.shall.be.set.to.0";
    public static final String TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleNoviewAndTogglenoviewFlagBitsShallBeSetTo0 = "the.f.keys.print.flag.bit.shall.be.set.to.1.and.its.hidden.invisible.noview.and.togglenoview.flag.bits.shall.be.set.to.0";
    public static final String WidgetAnnotationDictionaryOrFieldDictionaryShallNotIncludeAOrAAEntry = "widget.annotation.dictionary.or.field.dictionary.shall.not.include.a.or.aa.entry";

    public PdfAConformanceException(String message) {
        super(message);
    }

    public PdfAConformanceException(String message, Object object) {
        super(message, object);
    }
}

