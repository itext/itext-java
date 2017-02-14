/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.pdfa;

import com.itextpdf.kernel.PdfException;
/**
 * Exception that is thrown when the PDF Document doesn't adhere to the PDF/A specification.
 */
public class PdfAConformanceException extends PdfException {

    private static final long serialVersionUID = -5951503441486657717L;

    public static final String _1ActionsAreNotAllowed = "{0} actions are not allowed";
    public static final String AFormXobjectDictionaryShallNotContainOpiKey = "A form xobject dictionary shall not contain opi key";
    public static final String AFormXobjectDictionaryShallNotContainPSKey = "A form xobject dictionary shall not contain PS key";
    public static final String AFormXobjectDictionaryShallNotContainSubtype2KeyWithAValueOfPS = "A form xobject dictionary shall not contain subtype2 key with a value of PS";
    public static final String AGroupObjectWithAnSKeyWithAValueOfTransparencyShallNotBeIncludedInAFormXobject = "A group object with an s key with a value of transparency shall not be included in a form xobject";
    public static final String AGroupObjectWithAnSKeyWithAValueOfTransparencyShallNotBeIncludedInAPageObject = "A group object with an s key with a value of transparency shall not be included in a form xobject";
    public static final String AllColourChannelsInTheJpeg2000DataShallHaveTheSameBitDepth = "All colour channels in the jpeg2000 data shall have the same bit-depth";
    public static final String AllFontsMustBeEmbeddedThisOneIsnt1 = "All the fonts must be embedded. This one is not: {0}";
    public static final String AllHalftonesShallHaveHalftonetype1Or5 = "All halftones shall have halftonetype 1 or 5";
    public static final String AllNonSymbolicTrueTypeFontShallSpecifyMacRomanEncodingOrWinAnsiEncoding = "All non-symbolic TrueType fonts shall specify MacRomanEncoding or WinAnsiEncoding as the value of the Encoding entry in the font dictionary ";
    public static final String AllNonSymbolicTrueTypeFontShallSpecifyMacRomanOrWinAnsiEncodingAsTheEncodingEntry = "All non-symbolic TrueType fonts shall specify MacRomanEncoding or WinAnsiEncoding as the value of the Encoding entry in the font dictionary  This also means that Encoding entry in the font dictionary shall not be an encoding dictionary ";
    public static final String AllSymbolicTrueTypeFontsShallNotSpecifyEncoding = "All symbolic TrueType fonts shall not specify an Encoding entry in the font dictionary ";
    public static final String AnAnnotationDictionaryShallContainTheFKey = "An annotation dictionary shall contain the f key";
    public static final String AnAnnotationDictionaryShallNotContainAAKey = "An annotation dictionary shall not contain aa key";
    public static final String AnAnnotationDictionaryShallNotContainTheCaKeyWithAValueOtherThan1 = "An annotation dictionary shall not contain the ca key with a value other than 1";
    public static final String AnExtgstateDictionaryShallNotContainTheHTPKey = "An extgstate dictionary shall not contain the HTP key";
    public static final String AnExtgstateDictionaryShallNotContainTheTR2KeyWithAValueOtherThanDefault = "An extgstate dictionary shall not contain the TR2 key with a value other than default";
    public static final String AnExtgstateDictionaryShallNotContainTheTrKey = "An extgstate dictionary shall not contain the tr key";
    public static final String AnImageDictionaryShallNotContainAlternatesKey = "An image dictionary shall not contain alternates key";
    public static final String AnImageDictionaryShallNotContainOpiKey = "An image dictionary shall not contain opi key";
    public static final String AnnotationOfType1ShouldHaveContentsKey = "Annotation of type 1 should have contents key";
    public static final String AnnotationShallContainKeyF = "Annotation shall contain key F";
    public static final String AnnotationType1IsNotPermitted = "Annotation type 1 is not permitted";
    public static final String AppearanceDictionaryOfWidgetSubtypeAndBtnFieldTypeShallContainOnlyTheNKeyWithDictionaryValue = "Appearance dictionary of widget subtype and btn field type shall contain only the n key with dictionary value";
    public static final String AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue = "Appearance dictionary shall contain only the n key with stream value";
    public static final String BlendModeShallHhaveValueNormalOrCompatible = "Blend mode shall have value normal or compatible";
    public static final String CatalogDictionaryShallNotContainAAEntry = "Catalog dictionary shall not contain aa entry";
    public static final String CatalogDictionaryShallNotContainAlternatepresentationsNamesEntry = "Catalog dictionary shall not contain alternatepresentations names entry";
    public static final String CatalogDictionaryShallNotContainOCPropertiesKey = "Catalog dictionary shall not contain the ocproperties key";
    public static final String CatalogDictionaryShallNotContainRequirementsEntry = "Catalog dictionary shall not contain a requirements entry";
    public static final String CatalogShallContainLangEntry = "Catalog dictionary should contain lang entry";
    public static final String CatalogShallContainMetadataEntry = "Catalog dictionary shall contain metadata entry";
    public static final String CatalogShallIncludeMarkInfoDictionaryWithMarkedTrueValue = "Catalog dictionary shall include a markinfo dictionary whose entry marked shall have a value of true";
    public static final String ColorSpace1ShallBeDeviceIndependent = "Color space 1 shall be device independent";
    public static final String ColorSpace1ShallHave2Components = "Color space 1 shall have 2 components";
    public static final String CryptFilterIsNotPermitted = "Crypt filter is not permitted inline image";
    public static final String DeprecatedSetStateAndNoOpActionsAreNotAllowed = "Deprecated setstate and noop actions are not allowed";
    public static final String DestoutputprofileInThePdfa1OutputintentDictionaryShallBeRgb = "Destoutputprofile in the pdfa1 outputintent dictionary shall be rgb";
    public static final String DevicecmykMayBeUsedOnlyIfTheFileHasACmykPdfAOutputIntent = "Devicecmyk may be used only if the file has a cmyk pdfa outputIntent";
    public static final String DevicecmykMayBeUsedOnlyIfTheFileHasACmykPdfAOutputIntentOrDefaultCmykInUsageContext = "Devicecmyk may be used only if the file has a cmyk pdfa outputIntent or defaultcmyk in usage context";
    public static final String DevicergbAndDevicecmykColorspacesCannotBeUsedBothInOneFile = "Devicergb and devicecmyk colorspaces cannot be used both in one file";
    public static final String DevicergbMayBeUsedOnlyIfTheFileHasARgbPdfAOutputIntent = "Devicergb may be used only if the file has a rgb pdfa outputIntent";
    public static final String DevicergbMayBeUsedOnlyIfTheFileHasARgbPdfAOutputIntentOrDefaultRgbInUsageContext = "Devicergb may be used only if the file has a rgb pdfa outputIntent or defaultrgb in usage context";
    public static final String DocumentToReadFromShallBeAPdfAConformantFileWithValidXmpMetadata = "Document to read from shall be a pdfa conformant file with valid xmp metadata";
    public static final String EFKeyOfFileSpecificationDictionaryShallContainDictionaryWithValidFKey = "Ef key of file specification dictionary shall contain dictionary with valid f key";
    public static final String EmbeddedFileShallBeOfPdfMimeType = "Embedded file shall be of pdf mime type";
    public static final String EmbeddedFileShallContainParamsKeyWithDictionaryAsValue = "Embedded file shall contain params key with dictionary as value";
    public static final String EmbeddedFileShallContainParamsKeyWithValidModdateKey = "Embedded file shall contain params key with valid moddate key";
    public static final String EncryptShallNotBeUsedInTrailerDictionary = "Keyword encrypt shall not be used in the trailer dictionary";
    public static final String EveryAnnotationShallHaveAtLeastOneAppearanceDictionary = "Every annotation shall have at least one appearance dictionary";
    public static final String ExactlyOneColourSpaceSpecificationShallHaveTheValue0x01InTheApproxField = "Exactly one colour space specification shall have the value 0x01 in the approx field";
    public static final String FileSpecificationDictionaryShallContainFKeyUFKeyAndDescKey = "File specification dictionary shall contain f key uf key and desc key";
    public static final String FileSpecificationDictionaryShallContainOneOfThePredefinedAFRelationshipKeys = "File specification dictionary shall contain one of the predefined afrelationship keys";
    public static final String FileSpecificationDictionaryShallNotContainTheEFKey = "File specification dictionary shall not contain the EF key";
    public static final String GraphicStateStackDepthIsGreaterThan28 = "Graphics state stack depth is greater than 28";
    public static final String HalftonesShallNotContainHalftonename = "Halftones shall not contain halftonename";
    public static final String IfDeviceRgbCmykGrayUsedInFileThatFileShallContainPdfaOutputIntent = "If device rgb cmyk gray used in file, that file shall contain pdfa outputintent";
    public static final String IfDeviceRgbCmykGrayUsedInFileThatFileShallContainPdfaOutputIntentOrDefaultRgbCmykGrayInUsageContext = "If device rgb cmyk gray used in file that file shall contain pdfa outputintent orDefaultRgb Cmyk Gray in usage context";
    public static final String IfOutputintentsArrayHasMoreThanOneEntryWithDestoutputprofileKeyTheSameIndirectObjectShallBeUsedAsTheValueOfThatObject = "If outputintents array has more than one entry with destoutputprofile key the same indirect object shall be used as the value of that object";
    public static final String IfSpecifiedRenderingShallBeOneOfTheFollowingRelativecolorimetricAbsolutecolorimetricPerceptualOrSaturation = "If specified rendering shall be one of the following relativecolorimetric absolutecolorimetric perceptual or saturation";
    public static final String IfTheDocumentDoesNotContainAPdfAOutputIntentTransparencyIsForbidden = "If the document does not contain a pdfa outputintent transparency is forbidden";
    public static final String Jpeg2000EnumeratedColourSpace19CIEJabShallNotBeUsed = "jpeg2000 enumerated colour space 19 (CIEJab) shall not be used";
    public static final String LZWDecodeFilterIsNotPermitted = "lzwdecode filter is not permitted";
    public static final String MimeTypeShallBeSpecifiedUsingTheSubtypeKeyOfTheFileSpecificationStreamDictionary = "Mime type shall be specified using the subtype key of the file specification stream dictionary";
    public static final String NameDictionaryShallNotContainTheEmbeddedFilesKey = "Name dictionary shall not contain the EmbeddedFiles key";
    public static final String NamedActionType1IsNotAllowed = "Named action type 1 not allowed";
    public static final String NeedAppearancesFlagOfTheInteractiveFormDictionaryShallEitherNotBePresentedOrShallBeFalse = "Needappearances flag of the interactive form dictionary shall either not be presented or shall be false";
    public static final String NoKeysOtherUr3andDocMdpShallBePresentInPerDict = "No keys other than UR3 and DocMDP shall be present in a permissions dictionary";
    public static final String NotIdentityCryptFilterIsNotPermitted = "Not identity crypt filter is not permitted";
    public static final String OnlyJpxBaselineSetOfFeaturesShallBeUsed = "Only jpx baseline set of features shall be used";
    public static final String OnlyStandardBlendModesShallBeusedForTheValueOfTheBMKeyOnAnExtendedGraphicStateDictionary = "Only standard blend modes shall be used for the value of the BM key in an extended graphic state dictionary";
    public static final String OptionalContentConfigurationDictionaryShallContainNameEntry = "Optional content configuration dictionary shall contain name entry";
    public static final String OrderArrayShallContainReferencesToAllOcgs = "Order array shall contain references to all ocgs";
    public static final String OutputIntentColorSpaceShallBeEitherGrayRgbOrCmyk = "Output intent color space shall be either gray rgb or cmyk";
    public static final String OverprintModeShallNotBeOneWhenAnICCBasedCMYKColourSpaceIsUsedAndWhenOverprintingIsSetToTrue = "Overprint mode shall not be one when an ICCBased CMYK colour space is used and when overprinting is set to true";
    public static final String PageDictionaryShallNotContainAAEntry = "Page dictionary shall not contain aa entry";
    public static final String PageDictionaryShallNotContainPressstepsEntry = "Page dictionary shall not contain pressteps entry";
    public static final String PageLess3UnitsNoGreater14400InEitherDirection = "The page less 3 units no greater 14400 in either direction";
    public static final String PdfStringIsTooLong = "PdfString is too long";
    public static final String ProfileStreamOfOutputintentShallBeOutputProfilePrtrOrMonitorProfileMntr = "Profile stream of outputintent shall be output profile (prtr) or monitor profile (mntr)";
    public static final String RealNumberIsOutOfRange = "Real number is out of range";
    public static final String SigRefDicShallNotContDigestParam ="Signature references dictionary shall not contain digestlocation digestmethod digestvalue";
    public static final String StreamObjDictShallNotContainForFFilterOrFDecodeParams = "Stream object dictionary shall not contain the f ffilter or fdecodeparams keys";
    public static final String TextAnnotationsShouldSetTheNozoomAndNorotateFlagBitsOfTheFKeyTo1 = "Text annotations should set the nozoom and norotate flag bits of the f key to 1";
    public static final String TheAsKeyShallNotAppearInAnyOptionalContentConfigurationDictionary = "The as key shall not appear in any optional content configuration dictionary";
    public static final String TheBitDepthOfTheJpeg2000DataShallHaveAValueInTheRange1To38 = "The bit-depth of the jpeg2000 data shall have a value in the range 1to38";
    public static final String TheCatalogDictionaryShallNotContainTheNeedsrenderingKey = "The catalog dictionary shall not contain the needsrendering key";
    public static final String TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleAndNoviewFlagBitsShallBeSetTo0 = "The f keys print flag bit shall be set to 1 and its hidden invisible and noview flag bits shall be set to 0";
    public static final String TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleNoviewAndTogglenoviewFlagBitsShallBeSetTo0 = "The f keys print flag bit shall be set to 1 and its hidden invisible noview and togglenoview flag bits shall be set to 0";
    public static final String TheInteractiveFormDictionaryShallNotContainTheXfaKey = "The interactive form dictionary shall not contain the xfa key";
    public static final String TheNumberOfColourChannelsInTheJpeg2000DataShallBe123 = "The number of colour channels in the jpeg2000 data shall be 123";
    public static final String TheSmaskKeyIsNotAllowedInExtgstate = "The smask key is not allowed in extgstate";
    public static final String TheSmaskKeyIsNotAllowedInXobjects = "The smask key is not allowed in xobjects";
    public static final String TheValueOfInterpolateKeyShallNotBeTrue = "The value of interpolate key shall not be true";
    public static final String TheValueOfTheMethEntryInColrBoxShallBe123 = "The value of the meth entry in colr box shall be 123";
    public static final String TintTransformAndAlternateSpaceOfSeparationArraysInTheColorantsOfDeviceNShallBeConsistentWithSameAttributesOfDeviceN = "TintTransform and alternateSpace of separation arrays in the colorants of deviceN shall be consistent with same attributes of deviceN";
    public static final String TintTransformAndAlternateSpaceShallBeTheSameForTheAllSeparationCSWithTheSameName = "TintTransform and alternateSpace shall be the same for the all separation cs with the same name";
    public static final String TransparencyIsNotAllowedCAShallBeEqualTo1 = "Transparency is not allowed. CA shall be equal to 1";
    public static final String TransparencyIsNotAllowedCaShallBeEqualTo1 = "Transparency is not allowed. ca shall be equal to 1";
    public static final String ValueOfNameEntryShallBeUniqueAmongAllOptionalContentConfigurationDictionaries = "Value of name entry shall be unique among all optional content configuration dictionaries";
    public static final String WidgetAnnotationDictionaryOrFieldDictionaryShallNotIncludeAOrAAEntry = "Widget annotation dictionary or field dictionary shall not include a or aa entry";

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

