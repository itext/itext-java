package com.itextpdf.pdfa;

import com.itextpdf.kernel.PdfException;
/**
 * Exception that is thrown when the PDF Document doesn't adhere to the PDF/A specification.
 */
public class PdfAConformanceException extends PdfException {

    public static final String _1ActionsIsNotAllowed = "1.actions.is.not.allowed";
    public static final String AllFontsMustBeEmbeddedThisOneIsnt1 = "all.the.fonts.must.be.embedded.this.one.isn.t.1";
    public static final String AFormXobjectDictionaryShallNotContainOpiKey = "a.form.xobject.dictionary.shall.not.contain.opi.key";
    public static final String AFormXobjectDictionaryShallNotContainPSKey = "a.form.xobject.dictionary.shall.not.contain.PS.key";
    public static final String AFormXobjectDictionaryShallNotContainSubtype2KeyWithAValueOfPS = "a.form.xobject.dictionary.shall.not.contain.subtype2.key.with.a.value.of.PS";
    public static final String AGroupObjectWithAnSKeyWithAValueOfTransparencyShallNotBeIncludedInAFormXobject = "a.group.object.with.an.s.key.with.a.value.of.transparency.shall.not.be.included.in.a.form.xobject";
    public static final String AGroupObjectWithAnSKeyWithAValueOfTransparencyShallNotBeIncludedInAPageObject = "a.group.object.with.an.s.key.with.a.value.of.transparency.shall.not.be.included.in.a.form.xobject";
    public static final String AllColourChannelsInTheJpeg2000DataShallHaveTheSameBitDepth = "all.colour.channels.in.the.jpeg2000.data.shall.have.the.same.bit-depth";
    public static final String AllHalftonesShallHaveHalftonetype1Or5 = "all.halftones.shall.have.halftonetype.1.or.5";
    public static final String AnAnnotationDictionaryShallContainTheFKey = "an.annotation.dictionary.shall.contain.the.f.key";
    public static final String AnAnnotationDictionaryShallNotContainTheCaKeyWithAValueOtherThan1 = "an.annotation.dictionary.shall.not.contain.the.ca.key.with.a.value.other.than.1";
    public static final String AnExtgstateDictionaryShallNotContainTheHTPKey = "an.extgstate.dictionary.shall.not.contain.the.HTP.key";
    public static final String AnExtgstateDictionaryShallNotContainTheTR2KeyWithAValueOtherThanDefault = "an.extgstate.dictionary.shall.not.contain.the.TR2.key.with.a.value.other.than.default";
    public static final String AnExtgstateDictionaryShallNotContainTheTrKey = "an.extgstate.dictionary.shall.not.contain.the.tr.key";
    public static final String AnImageDictionaryShallNotContainAlternatesKey = "an.image.dictionary.shall.not.contain.alternates.key";
    public static final String AnImageDictionaryShallNotContainOpiKey = "an.image.dictionary.shall.not.contain.opi.key";
    public static final String AnnotationOfType1ShouldHaveContentsKey = "annotation.of.type.1.should.have.contents.key";
    public static final String AnnotationType1IsNotPermitted = "annotation.type.1.is.not.permitted";
    public static final String AnnotationShallContainKeyF = "annotation.shall.contain.key.F";
    public static final String AppearanceDictionaryOfWidgetSubtypeAndBtnFieldTypeShallContainOnlyTheNKeyWithDictionaryValue = "appearance.dictionary.of.widget.subtype.and.btn.field.type.shall.contain.only.the.n.key.with.dictionary.value";
    public static final String AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue = "appearance.dictionary.shall.contain.only.the.n.key.with.stream.value";
    public static final String BlendModeShallHhaveValueNormalOrCompatible = "blend.mode.shall.have.value.normal.or.compatible";
    public static final String CatalogDictionaryShallNotContainAAEntry = "catalog.dictionary.shall.not.contain.aa.entry";
    public static final String CatalogDictionaryShallNotContainAlternatepresentationsNamesEntry = "catalog.dictionary.shall.not.contain.alternatepresentations.names.entry";
    public static final String CatalogDictionaryShallNotContainOCPropertiesKey = "catalog.dictionary.shall.not.contain.the.ocproperties.key";
    public static final String CatalogDictionaryShallNotContainRequirementsEntry = "catalog.dictionary.shall.not.contain.a.requirements.entry";
    public static final String CatalogShallIncludeMarkInfoDictionaryWithMarkedTrueValue = "catalog.dictionary.shall.include.a.markinfo.dictionary.whose.entry.marked.shall.have.a.value.of.true";
    public static final String CatalogShallContainLangEntry = "catalog.dictionary.shall.contain.lang.entry";
    public static final String CatalogShallContainMetadataEntry = "catalog.dictionary.shall.contain.metadata.entry";
    public static final String ColorSpace1ShallBeDeviceIndependent = "color.space.1.shall.be.device.independent";
    public static final String ColorSpace1ShallHave2Components = "color.space.1.shall.have.2.components";
    public static final String CryptFilterIsNotPermitted = "crypt.filter.is.not.permitted.inline.image";
    public static final String DeprecatedSetStateAndNoOpActionsAreNotAllowed = "deprecated.setstate.and.noop.actions.are.not.allowed";
    public static final String DestoutputprofileInThePdfa1OutputintentDictionaryShallBeRgb = "destoutputprofile.in.the.pdfa1.outputintent.dictionary.shall.be.rgb";
    public static final String DevicergbAndDevicecmykColorspacesCannotBeUsedBothInOneFile = "devicergb.and.devicecmyk.colorspaces.cannot.be.used.both.in.one.file";
    public static final String DevicergbMayBeUsedOnlyIfTheFileHasARgbPdfAOutputIntent = "devicergb.may.be.used.only.if.the.file.has.a.rgb.pdfa.outputIntent";
    public static final String DevicergbMayBeUsedOnlyIfTheFileHasARgbPdfAOutputIntentOrDefaultRgbInUsageContext = "devicergb.may.be.used.only.if.the.file.has.a.rgb.pdfa.outputIntent.or.defaultrgb.in.usage.context";
    public static final String DevicecmykMayBeUsedOnlyIfTheFileHasACmykPdfAOutputIntent = "devicecmyk.may.be.used.only.if.the.file.has.a.cmyk.pdfa.outputIntent";
    public static final String DevicecmykMayBeUsedOnlyIfTheFileHasACmykPdfAOutputIntentOrDefaultCmykInUsageContext = "devicecmyk.may.be.used.only.if.the.file.has.a.cmyk.pdfa.outputIntent.or.defaultcmyk.in.usage.context";
    public static final String DocumentToReadFromShallBeAPdfAConformantFileWithValidXmpMetadata = "document.to.read.from.shall.be.a.pdfa.conformant.file.with.valid.xmp.metadata";
    public static final String EFKeyOfFileSpecificationDictionaryShallContainDictionaryWithValidFKey = "ef.key.of.file.specification.dictionary.shall.contain.dictionary.with.valid.f.key";
    public static final String EmbeddedFileShallBeOfPdfMimeType = "embedded.file.shall.be.of.pdf.mime.type";
    public static final String EmbeddedFileShallContainParamsKeyWithDictionaryAsValue = "embedded.file.shall.contain.params.key.with.dictionary.as.value";
    public static final String EmbeddedFileShallContainParamsKeyWithValidModdateKey = "embedded.file.shall.contain.params.key.with.valid.moddate.key";
    public static final String EncryptShallNotBeUsedInTrailerDictionary = "keyword.encrypt.shall.not.be.used.in.the.trailer.dictionary";
    public static final String EveryAnnotationShallHaveAtLeastOneAppearanceDictionary = "every.annotation.shall.have.at.least.one.appearance.dictionary";
    public static final String ExactlyOneColourSpaceSpecificationShallHaveTheValue0x01InTheApproxField = "exactly.one.colour.space.specification.shall.have.the.value.0x01.in.the.approx.field";
    public static final String FileSpecificationDictionaryShallContainFKeyUFKeyAndDescKey = "file.specification.dictionary.shall.contain.f.key.uf.key.and.desc.key";
    public static final String FileSpecificationDictionaryShallContainOneOfThePredefinedAFRelationshipKeys = "file.specification.dictionary.shall.contain.one.of.the.predefined.afrelationship.keys";
    public static final String FileSpecificationDictionaryShallNotContainTheEFKey = "file.specification.dictionary.shall.not.contain.the.EF.key";
    public static final String GraphicStateStackDepthIsGreaterThan28 = "graphics.state.stack.depth.is.greater.than.28";
    public static final String HalftonesShallNotContainHalftonename = "halftones.shall.not.contain.halftonename";
    public static final String IfDeviceRgbCmykGrayUsedInFileThatFileShallContainPdfaOutputIntent = "if.device.rgb.cmyk.gray.used.in.file.that.file.shall.contain.pdfa.outputintent";
    public static final String IfDeviceRgbCmykGrayUsedInFileThatFileShallContainPdfaOutputIntentOrDefaultRgbCmykGrayInUsageContext = "if.device.rgb.cmyk.gray.used.in.file.that.file.shall.contain.pdfa.outputintent.orDefaultRgb.Cmyk.Gray.in.usage.context";
    public static final String IfOutputintentsArrayHasMoreThanOneEntryWithDestoutputprofileKeyTheSameIndirectObjectShallBeUsedAsTheValueOfThatObject = "if.outputintents.array.has.more.than.one.entry.with.destoutputprofile.key.the.same.indirect.object.shall.be.used.as.the.value.of.that.object";
    public static final String IfSpecifiedRenderingShallBeOneOfTheFollowingRelativecolorimetricAbsolutecolorimetricPerceptualOrSaturation = "if.specified.rendering.shall.be.one.of.the.following.relativecolorimetric.absolutecolorimetric.perceptual.or.saturation";
    public static final String IfTheDocumentDoesNotContainAPdfAOutputIntentTransparencyIsForbidden = "if.the.document.does.not.contain.a.pdfa.outputintent.transparency.is.forbidden";
    public static final String Jpeg2000EnumeratedColourSpace19CIEJabShallNotBeUsed = "jpeg2000.enumerated.colour.space.19.(CIEJab).shall.not.be.used";
    public static final String LZWDecodeFilterIsNotPermitted = "lzwdecode.filter.is.not.permitted";
    public static final String NamedActionType1IsNotAllowed = "named.action.type.1.not.allowed";
    public static final String NameDictionaryShallNotContainTheEmbeddedFilesKey = "name.dictionary.shall.not.contain.the.EmbeddedFiles.key";
    public static final String NeedAppearancesFlagOfTheInteractiveFormDictionaryShallEitherNotBePresentedOrShallBeFalse = "needappearances.flag.of.the.interactive.form.dictionary.shall.either.not.be.presented.or.shall.be.false";
    public static final String NoKeysOtherUr3andDocMdpShallBePresentInPerDict = "no.keys.other.than.UR3.and.DocMDP.shall.be.present.in.a.permissions.dictionary";
    public static final String NotIdentityCryptFilterIsNotPermitted = "not.identity.crypt.filter.is.not.permitted";
    public static final String OnlyJpxBaselineSetOfFeaturesShallBeUsed = "only.jpx.baseline.set.of.features.shall.be.used";
    public static final String OnlyStandardBlendModesShallBeusedForTheValueOfTheBMKeyOnAnExtendedGraphicStateDictionary = "only.standard.blend.modes.shall.be.used.for.the.value.of.the.BM.key.in.an.extended.graphic.state.dictionary";
    public static final String OptionalContentConfigurationDictionaryShallContainNameEntry = "optional.content.configuration.dictionary.shall.contain.name.entry";
    public static final String OrderArrayShallContainReferencesToAllOcgs = "order.array.shall.contain.references.to.all.ocgs";
    public static final String OutputIntentColorSpaceShallBeEitherGrayRgbOrCmyk = "output.intent.color.space.shall.be.either.gray.rgb.or.cmyk";
    public static final String OverprintModeShallNotBeOneWhenAnICCBasedCMYKColourSpaceIsUsedAndWhenOverprintingIsSetToTrue = "overprint.mode.shall.not.be.one.when.an.ICCBased.CMYK.colour.space.is.used.and.when.overprinting.is.set.to.true";
    public static final String PageDictionaryShallNotContainAAEntry = "page.dictionary.shall.not.contain.aa.entry";
    public static final String PageDictionaryShallNotContainPressstepsEntry = "page.dictionary.shall.not.contain.pressteps.entry";
    public static final String PageLess3UnitsNoGreater14400InEitherDirection = "the.page.less.3.units.no.greater.14400.in.either.direction";
    public static final String PdfStringIsTooLong = "pdf.string.is.too.long";
    public static final String ProfileStreamOfOutputintentShallBeOutputProfilePrtrOrMonitorProfileMntr = "profile.stream.of.outputintent.shall.be.output.profile.(prtr).or.monitor.profile.(mntr)";
    public static final String RealNumberIsOutOfRange = "real.number.is.out.of.range";
    public static final String SigRefDicShallNotContDigestParam ="signature.references.dictionary.shall.not.contain.digestlocation.digestmethod.digestvalue";
    public static final String StreamObjDictShallNotContainForFFilterOrFDecodeParams = "stream.object.dictionary.shall.not.contain.the.f.ffilter.or.fdecodeparams.keys";
    public static final String TextAnnotationsShouldSetTheNozoomAndNorotateFlagBitsOfTheFKeyTo1 = "text.annotations.should.set.the.nozoom.and.norotate.flag.bits.of.the.f.key.to.1";
    public static final String TheAsKeyShallNotAppearInAnyOptionalContentConfigurationDictionary = "the.as.key.shall.not.appear.in.any.optional.content.configuration.dictionary";
    public static final String TheBitDepthOfTheJpeg2000DataShallHaveAValueInTheRange1To38 = "the.bit-depth.of.the.jpeg2000.data.shall.have.a.value.in.the.range.1to38";
    public static final String TheCatalogDictionaryShallNotContainTheNeedsrenderingKey = "the.catalog.dictionary.shall.not.contain.the.needsrendering.key";
    public static final String TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleAndNoviewFlagBitsShallBeSetTo0 = "the.f.keys.print.flag.bit.shall.be.set.to.1.and.its.hidden.invisible.and.noview.flag.bits.shall.be.set.to.0";
    public static final String TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleNoviewAndTogglenoviewFlagBitsShallBeSetTo0 = "the.f.keys.print.flag.bit.shall.be.set.to.1.and.its.hidden.invisible.noview.and.togglenoview.flag.bits.shall.be.set.to.0";
    public static final String TheInteractiveFormDictionaryShallNotContainTheXfaKey = "the.interactive.form.dictionary.shall.not.contain.the.xfa.key";
    public static final String TheNumberOfColourChannelsInTheJpeg2000DataShallBe123 = "the.number.of.colour.channels.in.the.jpeg2000.data.shall.be.123";
    public static final String TheSmaskKeyIsNotAllowedInExtgstate = "the.smask.key.is.not.allowed.in.extgstate";
    public static final String TheSmaskKeyIsNotAllowedInXobjects = "the.smask.key.is.not.allowed.in.xobjects";
    public static final String TheValueOfInterpolateKeyShallNotBeTrue = "the.value.of.interpolate.key.shall.not.be.true";
    public static final String TheValueOfTheMethEntryInColrBoxShallBe123 = "the.value.of.the.meth.entry.in.colr.box.shall.be.123";
    public static final String TintTransformAndAlternateSpaceOfSeparationArraysInTheColorantsOfDeviceNShallBeConsistentWithSameAttributesOfDeviceN = "tintTransform.and.alternateSpace.of.separation.arrays.in.the.colorants.of.deviceN.shall.be.consistent.with.same.attributes.of.deviceN";
    public static final String TintTransformAndAlternateSpaceShallBeTheSameForTheAllSeparationCSWithTheSameName = "tintTransform.and.alternateSpace.shall.be.the.same.for.the.all.separation.cs.with.the.same.name";
    public static final String TransparencyIsNotAllowedCAShallBeEqualTo1 = "transparency.is.not.allowed.CA.shall.be.equal.to.1";
    public static final String TransparencyIsNotAllowedCaShallBeEqualTo1 = "transparency.is.not.allowed.ca.shall.be.equal.to.1";
    public static final String ValueOfNameEntryShallBeUniqueAmongAllOptionalContentConfigurationDictionaries = "value.of.name.entry.shall.be.unique.among.all.optional.content.configuration.dictionaries";
    public static final String WidgetAnnotationDictionaryOrFieldDictionaryShallNotIncludeAOrAAEntry = "widget.annotation.dictionary.or.field.dictionary.shall.not.include.a.or.aa.entry";

    /**
     * Creates a PdfAConformanceException.
     *
     * @param message the error message
     */
    public PdfAConformanceException(String message) {
        super(message);
    }

    /**
     * Creates a PdfAConformanceException.
     *
     * @param message the error message
     * @param object an object
     */
    public PdfAConformanceException(String message, Object object) {
        super(message, object);
    }
}

