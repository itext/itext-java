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
    public static final String RealNumberIsOutOfRange = "real.number.is.out.of.range";
    public static final String PdfStringIsTooLong = "pdf.string.is.too.long";
    public static final String StreamObjDictShallNotContainForFFilterOrFDecodeParams = "stream.object.dictionary.shall.not.contain.the.f.ffilter.or.fdecodeparams.keys";
    public static final String LZWDecodeFilterIsNotPermitted = "lzwdecode.filter.is.not.permitted";
    public static final String NotIdentityCryptFilterIsNotPermitted = "not.identity.crypt.filter.is.not.permitted";
    public static final String CatalogDictionaryShallNotContainOCPropertiesKey = "the.catalog.dictionary.shall.not.contain.the.ocproperties.key";
    public static final String CryptFilterIsNotPermitted = "crypt.filter.is.not.permitted.inline.imag";
    public static final String GraphicStateStackDepthIsGreaterThan28 = "graphics.state.stack.depth.is.greater.than.28";
    public static final String SigRefDicShallNotContDigestParam ="signature.references.dictionary.shall.not.contain.digestlocation.digestmethod.digestvalue";
    public static final String NoKeysOtherUr3andDocMdpShallBePresentInPerDict = "no.keys.other.than.UR3.and.DocMDP.shall.be.present.in.a.permissions.dictionary";
    public static final String EncryptShallNotBeUsedInTrailerDictionary = "keyword.encrypt.shall.not.be.used.in.the.trailer.dictionary";

    public PdfAConformanceException(String message) {
        super(message);
    }

    public PdfAConformanceException(String message, Object object) {
        super(message, object);
    }
}

