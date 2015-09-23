package com.itextpdf.pdfa;

import com.itextpdf.basics.PdfException;

public class PdfAConformanceException extends PdfException {

    public static final String _1ActionsIsNotAllowed = "1.actions.is.not.allowed";
    public static final String AllFontsMustBeEmbeddedThisOneIsnt1 = "all.the.fonts.must.be.embedded.this.one.isn.t.1";
    public static final String AnAnnotationDictionaryShallContainTheFKey = "an.annotation.dictionary.shall.contain.the.f.key";
    public static final String AnAnnotationDictionaryShallNotContainTheCaKeyWithAValueOtherThan1 = "an.annotation.dictionary.shall.not.contain.the.ca.key.with.a.value.other.than.1";
    public static final String AnnotationOfType1ShouldHaveContentsKey = "annotation.of.type.1.should.have.contents.key";
    public static final String AnnotationType1IsNotPermitted = "annotation.type.1.is.not.permitted";
    public static final String AnnotationShallContainKeyF = "annotation.shall.contain.key.F";
    public static final String AppearanceDictionaryOfWidgetSubtypeAndBtnFieldTypeShallContainOnlyTheNKeyWithDictionaryValue = "appearance.dictionary.of.widget.subtype.and.btn.field.type.shall.contain.only.the.n.key.with.dictionary.value";
    public static final String AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue = "appearance.dictionary.shall.contain.only.the.n.key.with.stream.value";
    public static final String CatalogDictionaryShallNotContainAAEntry = "catalog.dictionary.shall.not.contain.aa.entry";
    public static final String CatalogDictionaryShallNotContainAlternatepresentationsNamesEntry = "catalog.dictionary.shall.not.contain.alternatepresentations.names.entry";
    public static final String CatalogDictionaryShallNotContainOCPropertiesKey = "catalog.dictionary.shall.not.contain.the.ocproperties.key";
    public static final String CatalogDictionaryShallNotContainRequirementsEntry = "catalog.dictionary.shall.not.contain.a.requirements.entry";
    public static final String CatalogShallIncludeMarkInfoDictionaryWithMarkedTrueValue = "catalog.dictionary.shall.include.a.markinfo.dictionary.whose.entry.marked.shall.have.a.value.of.true";
    public static final String CatalogShallContainLangEntry = "catalog.dictionary.shall.contain.lang.entry";
    public static final String CatalogShallContainMetadataEntry = "catalog.dictionary.shall.contain.metadata.entry";
    public static final String CryptFilterIsNotPermitted = "crypt.filter.is.not.permitted.inline.image";
    public static final String DeprecatedSetStateAndNoOpActionsAreNotAllowed = "deprecated.setstate.and.noop.actions.are.not.allowed";
    public static final String DestoutputprofileInThePdfa1OutputintentDictionaryShallBeRgb = "destoutputprofile.in.the.pdfa1.outputintent.dictionary.shall.be.rgb";
    public static final String EncryptShallNotBeUsedInTrailerDictionary = "keyword.encrypt.shall.not.be.used.in.the.trailer.dictionary";
    public static final String EveryAnnotationShallHaveAtLeastOneAppearanceDictionary = "every.annotation.shall.have.at.least.one.appearance.dictionary";
    public static final String GraphicStateStackDepthIsGreaterThan28 = "graphics.state.stack.depth.is.greater.than.28";
    public static final String LZWDecodeFilterIsNotPermitted = "lzwdecode.filter.is.not.permitted";
    public static final String NamedActionType1IsNotAllowed = "named.action.type.1.not.allowed";
    public static final String NeedAppearancesFlagOfTheInteractiveFormDictionaryShallEitherNotBePresentedOrShallBeFalse = "needappearances.flag.of.the.interactive.form.dictionary.shall.either.not.be.presented.or.shall.be.false";
    public static final String NoKeysOtherUr3andDocMdpShallBePresentInPerDict = "no.keys.other.than.UR3.and.DocMDP.shall.be.present.in.a.permissions.dictionary";
    public static final String NotIdentityCryptFilterIsNotPermitted = "not.identity.crypt.filter.is.not.permitted";
    public static final String OptionalContentConfigurationDictionaryShallContainNameEntry = "optional.content.configuration.dictionary.shall.contain.name.entry";
    public static final String OrderArrayShallContainReferencesToAllOcgs = "order.array.shall.contain.references.to.all.ocgs";
    public static final String PageDictionaryShallNotContainAAEntry = "page.dictionary.shall.not.contain.aa.entry";
    public static final String PageDictionaryShallNotContainPressstepsEntry = "page.dictionary.shall.not.contain.pressteps.entry";
    public static final String PageLess3UnitsNoGreater14400InEitherDirection = "the.page.less.3.units.no.greater.14400.in.either.direction";
    public static final String PdfStringIsTooLong = "pdf.string.is.too.long";
    public static final String RealNumberIsOutOfRange = "real.number.is.out.of.range";
    public static final String SigRefDicShallNotContDigestParam ="signature.references.dictionary.shall.not.contain.digestlocation.digestmethod.digestvalue";
    public static final String StreamObjDictShallNotContainForFFilterOrFDecodeParams = "stream.object.dictionary.shall.not.contain.the.f.ffilter.or.fdecodeparams.keys";
    public static final String TextAnnotationsShouldSetTheNozoomAndNorotateFlagBitsOfTheFKeyTo1 = "text.annotations.should.set.the.nozoom.and.norotate.flag.bits.of.the.f.key.to.1";
    public static final String TheAsKeyShallNotAppearInAnyOptionalContentConfigurationDictionary = "the.as.key.shall.not.appear.in.any.optional.content.configuration.dictionary";
    public static final String TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleAndNoviewFlagBitsShallBeSetTo0 = "the.f.keys.print.flag.bit.shall.be.set.to.1.and.its.hidden.invisible.and.noview.flag.bits.shall.be.set.to.0";
    public static final String TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleNoviewAndTogglenoviewFlagBitsShallBeSetTo0 = "the.f.keys.print.flag.bit.shall.be.set.to.1.and.its.hidden.invisible.noview.and.togglenoview.flag.bits.shall.be.set.to.0";
    public static final String ValueOfNameEntryShallBeUniqueAmongAllOptionalContentConfigurationDictionaries = "value.of.name.entry.shall.be.unique.among.all.optional.content.configuration.dictionaries";
    public static final String WidgetAnnotationDictionaryOrFieldDictionaryShallNotIncludeAOrAAEntry = "widget.annotation.dictionary.or.field.dictionary.shall.not.include.a.or.aa.entry";

    public PdfAConformanceException(String message) {
        super(message);
    }

    public PdfAConformanceException(String message, Object object) {
        super(message, object);
    }
}

