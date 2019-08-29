/*

  This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.kernel;

import com.itextpdf.io.util.MessageFormatUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception class for exceptions in kernel module.
 */
public class PdfException extends RuntimeException {
    private static final long serialVersionUID = 4348832109324449091L;

    public static final String _1IsAnUnknownGraphicsStateDictionary = "{0} is an unknown graphics state dictionary.";
    public static final String _1IsNotAnAcceptableValueForTheField2 = "{0} is not an acceptable value for the field {1}.";
    public static final String _1IsNotAValidPlaceableWindowsMetafile = "{0} is not a valid placeable windows metafile.";

    public static final String AnnotationShallHaveReferenceToPage = "Annotation shall have reference to page.";
    public static final String AppendModeRequiresADocumentWithoutErrorsEvenIfRecoveryWasPossible = "Append mode requires a document without errors, even if recovery is possible.";
    public static final String AuthenticatedAttributeIsMissingTheDigest = "Authenticated attribute is missing the digest.";
    public static final String AvailableSpaceIsNotEnoughForSignature = "Available space is not enough for signature.";
    public static final String BadCertificateAndKey = "Bad public key certificate and/or private key.";
    public static final String BadUserPassword = "Bad user password. Password is not provided or wrong password provided. Correct password should be passed to PdfReader constructor with properties. See ReaderProperties#setPassword() method.";

    public static final String CannotAddCellToCompletedLargeTable = "The large table was completed. It's prohibited to use it anymore. Created different Table instance instead.";
    public static final String CannotAddKidToTheFlushedElement = "Cannot add kid to the flushed element.";
    public static final String CannotAddNonDictionaryExtGStateToResources1 = "Cannot add graphic state to resources. The PdfObject type is {0}, but should be PdfDictionary.";
    public static final String CannotAddNonDictionaryPatternToResources1 = "Cannot add pattern to resources. The PdfObject type is {0}, but should be PdfDictionary or PdfStream.";
    public static final String CannotAddNonDictionaryPropertiesToResources1 = "Cannot add properties to resources. The PdfObject type is {0}, but should be PdfDictionary.";
    public static final String CannotAddNonDictionaryShadingToResources1 = "Cannot add shading to resources. The PdfObject type is {0}, but should be PdfDictionary or PdfStream.";
    public static final String CannotAddNonStreamFormToResources1 = "Cannot add form to resources. The PdfObject type is {0}, but should be PdfStream.";
    public static final String CannotAddNonStreamImageToResources1 = "Cannot add image to resources. The PdfObject type is {0}, but should be PdfStream.";
    public static final String CannotBeEmbeddedDueToLicensingRestrictions = "{0} cannot be embedded due to licensing restrictions.";
    public static final String CannotCloseDocument = "Cannot close document.";
    public static final String CannotCloseDocumentWithAlreadyFlushedPdfCatalog = "Cannot close document with already flushed PDF Catalog.";
    public static final String CannotConvertPdfArrayToBooleanArray = "Cannot convert PdfArray to an array of booleans";
    public static final String CannotConvertPdfArrayToDoubleArray = "Cannot convert PdfArray to an array of doubles.";
    public static final String CannotConvertPdfArrayToIntArray = "Cannot convert PdfArray to an array of integers.";
    public static final String CannotConvertPdfArrayToFloatArray = "Cannot convert PdfArray to an array of floats.";
    public static final String CannotConvertPdfArrayToLongArray = "Cannot convert PdfArray to an array of longs.";
    public static final String CannotConvertPdfArrayToRectanle = "Cannot convert PdfArray to Rectangle.";
    public static final String CannotCopyFlushedObject = "Cannot copy flushed object.";
    public static final String CannotCopyFlushedTag = "Cannot copy flushed tag.";
    public static final String CannotCopyObjectContent = "Cannot copy object content.";
    public static final String CannotCopyIndirectObjectFromTheDocumentThatIsBeingWritten = "Cannot copy indirect object from the document that is being written.";
    public static final String CannotCopyToDocumentOpenedInReadingMode = "Cannot copy to document opened in reading mode.";
    public static final String CannotCreateLayoutImageByWmfImage = "Cannot create layout image by WmfImage instance. First convert the image into FormXObject and then use the corresponding layout image constructor.";
    public static final String CannotCreatePdfImageXObjectByWmfImage = "Cannot create PdfImageXObject instance by WmfImage. Use PdfFormXObject constructor instead.";
    public static final String CannotCreatePdfStreamByInputStreamWithoutPdfDocument = "Cannot create pdfstream by InputStream without PdfDocument.";
    public static final String CannotDrawElementsOnAlreadyFlushedPages = "Cannot draw elements on already flushed pages.";
    public static final String CannotGetContentBytes = "Cannot get content bytes.";
    public static final String CannotGetPdfStreamBytes = "Cannot get PdfStream bytes.";
    public static final String CannotOperateWithFlushedPdfStream = "Cannot operate with the flushed PdfStream.";
    public static final String CannotRetrieveMediaBoxAttribute = "Invalid PDF. There is no media box attribute for page or its parents.";
    public static final String CannotFindImageDataOrEI = "Cannot find image data or EI.";
    public static final String CannotFlushDocumentRootTagBeforeDocumentIsClosed = "Cannot flush document root tag before document is closed.";
    public static final String CannotFlushObject = "Cannot flush object.";
    public static final String CannotMoveFlushedTag = "Cannot move flushed tag";
    public static final String CannotMoveToFlushedKid = "Cannot move to flushed kid.";
    public static final String CannotMoveToMarkedContentReference = "Cannot move to marked content reference.";
    public static final String CannotMoveToParentCurrentElementIsRoot = "Cannot move to parent current element is root.";
    public static final String CannotMovePagesInPartlyFlushedDocument = "Cannot move pages in partly flushed document. Page number {0} is already flushed.";
    public static final String CannotOpenDocument = "Cannot open document.";
    public static final String CannotParseContentStream = "Cannot parse content stream.";
    public static final String CannotReadAStreamInOrderToAppendNewBytes = "Cannot read a stream in order to append new bytes.";
    public static final String CannotReadPdfObject = "Cannot read PdfObject.";
    public static final String CannotRecogniseDocumentFontWithEncoding = "Cannot recognise document font {0} with {1} encoding";
    public static final String CannotRelocateRootTag = "Cannot relocate root tag.";
    public static final String CannotRelocateTagWhichIsAlreadyFlushed = "Cannot relocate tag which is already flushed.";
    public static final String CannotRelocateTagWhichParentIsAlreadyFlushed = "Cannot relocate tag which parent is already flushed.";
    public static final String CannotRemoveDocumentRootTag = "Cannot remove document root tag.";
    public static final String CannotRemoveMarkedContentReferenceBecauseItsPageWasAlreadyFlushed = "Cannot remove marked content reference, because its page has been already flushed.";
    public static final String CannotRemoveTagBecauseItsParentIsFlushed = "Cannot remove tag, because its parent is flushed.";
    @Deprecated
    public static final String CannotSetDataToPdfstreamWhichWasCreatedByInputStream = "Cannot set data to PdfStream which was created by InputStream.";
    public static final String CannotSetDataToPdfStreamWhichWasCreatedByInputStream = "Cannot set data to PdfStream which was created by InputStream.";
    public static final String CannotSetEncryptedPayloadToDocumentOpenedInReadingMode = "Cannot set encrypted payload to a document opened in read only mode.";
    public static final String CannotSetEncryptedPayloadToEncryptedDocument = "Cannot set encrypted payload to an encrypted document.";
    public static final String CannotSplitDocumentThatIsBeingWritten = "Cannot split document that is being written.";
    public static final String CannotWriteToPdfStream = "Cannot write to PdfStream.";
    public static final String CannotWriteObjectAfterItWasReleased = "Cannot write object after it was released. In normal situation the object must be read once again before being written.";
    public static final String CannotDecodePkcs7SigneddataObject = "Cannot decode PKCS#7 SignedData object.";
    public static final String CannotFindSigningCertificateWithSerial1 = "Cannot find signing certificate with serial {0}.";
    public static final String CertificateIsNotProvidedDocumentIsEncryptedWithPublicKeyCertificate = "Certificate is not provided. Document is encrypted with public key certificate, it should be passed to PdfReader constructor with properties. See ReaderProperties#setPublicKeySecurityParams() method.";
    public static final String CertificationSignatureCreationFailedDocShallNotContainSigs = "Certification signature creation failed. Document shall not contain any certification or approval signatures before signing with certification signature.";
    public static final String CfNotFoundEncryption = "/CF not found (encryption)";
    public static final String CodabarMustHaveAtLeastStartAndStopCharacter = "Codabar must have at least start and stop character.";
    public static final String CodabarMustHaveOneAbcdAsStartStopCharacter = "Codabar must have one of 'ABCD' as start/stop character.";
    public static final String ColorSpaceNotFound = "ColorSpace not found.";
    public static final String ContentStreamMustNotInvokeOperatorsThatSpecifyColorsOrOtherColorRelatedParameters = "Content stream must not invoke operators that specify colors or other color related parameters in the graphics state.";
    public static final String DecodeParameterType1IsNotSupported = "Decode parameter type {0} is not supported.";
    public static final String DefaultAppearanceNotFound = "DefaultAppearance is required but not found";
    public static final String DefaultcryptfilterNotFoundEncryption = "/DefaultCryptFilter not found (encryption).";
    public static final String DictionaryKey1IsNotAName = "Dictionary key {0} is not a name.";
    public static final String DictionaryDoesntHave1FontData = "Dictionary doesn't have {0} font data.";
    public static final String DictionaryDoesntHaveSupportedFontData = "Dictionary doesn't have supported font data.";
    public static final String DocumentAlreadyPreClosed = "Document has been already pre closed.";
    public static final String DocumentClosedItIsImpossibleToExecuteAction = "Document was closed. It is impossible to execute action.";
    public static final String DocumentDoesntContainStructTreeRoot = "Document doesn't contain StructTreeRoot.";
    public static final String DocumentHasNoPages = "Document has no pages.";
    public static final String DocumentHasNoPdfCatalogObject = "Document has no PDF Catalog object.";
    public static final String DocumentMustBePreClosed = "Document must be preClosed.";
    public static final String DocumentForCopyToCannotBeNull = "Document for copyTo cannot be null.";
    public static final String DuringDecompressionMultipleStreamsInSumOccupiedMoreMemoryThanAllowed = "During decompression multiple streams in sum occupied more memory than allowed. Please either check your pdf or increase the allowed single decompressed pdf stream maximum size value by setting the appropriate parameter of ReaderProperties's MemoryLimitsAwareHandler.";
    public static final String DuringDecompressionSingleStreamOccupiedMoreMemoryThanAllowed = "During decompression a single stream occupied more memory than allowed. Please either check your pdf or increase the allowed multiple decompressed pdf streams maximum size value by setting the appropriate parameter of ReaderProperties's MemoryLimitsAwareHandler.";
    public static final String DuringDecompressionSingleStreamOccupiedMoreThanMaxIntegerValue = "During decompression a single stream occupied more than a maximum integer value. Please check your pdf.";
    public static final String EndOfContentStreamReachedBeforeEndOfImageData = "End of content stream reached before end of image data.";
    public static final String ErrorWhileReadingObjectStream = "Error while reading Object Stream.";
    public static final String EncryptedPayloadFileSpecDoesntHaveEncryptedPayloadDictionary = "Encrypted payload file spec shall have encrypted payload dictionary.";
    public static final String EncryptedPayloadFileSpecShallBeIndirect = "Encrypted payload file spec shall be indirect.";
    public static final String EncryptedPayloadFileSpecShallHaveEFDictionary = "Encrypted payload file spec shall have 'EF' key. The value of such key shall be a dictionary that contains embedded file stream.";
    public static final String EncryptedPayloadFileSpecShallHaveTypeEqualToFilespec = "Encrypted payload file spec shall have 'Type' key. The value of such key shall be 'Filespec'.";
    public static final String EncryptedPayloadShallHaveTypeEqualsToEncryptedPayloadIfPresent = "Encrypted payload dictionary shall have field 'Type' equal to 'EncryptedPayload' if present";
    public static final String EncryptedPayloadShallHaveSubtype = "Encrypted payload shall have 'Subtype' field specifying crypto filter";

    public static final String FailedToGetTsaResponseFrom1 = "Failed to get TSA response from {0}.";
    public static final String FieldFlatteningIsNotSupportedInAppendMode = "Field flattening is not supported in append mode.";
    public static final String FieldAlreadySigned = "Field has been already signed.";

    public static final String FieldNamesCannotContainADot = "Field names cannot contain a dot.";
    public static final String FieldTypeIsNotASignatureFieldType = "Field type is not a signature field type.";
    public static final String Filter1IsNotSupported = "Filter {0} is not supported.";
    public static final String FilePosition1CrossReferenceEntryInThisXrefSubsection = "file position {0} cross reference entry in this xref subsection.";
    public static final String FilterCcittfaxdecodeIsOnlySupportedForImages = "Filter CCITTFaxDecode is only supported for images";
    public static final String FilterIsNotANameOrArray = "filter is not a name or array.";
    public static final String FlushedPageCannotBeAddedOrInserted = "Flushed page cannot be added or inserted.";
    public static final String FlushingHelperFLushingModeIsNotForDocReadingMode = "Flushing writes the object to the output stream and releases it from memory. It is only possible for documents that have a PdfWriter associated with them. Use PageFlushingHelper#releaseDeep method instead.";
    public static final String FontAndSizeMustBeSetBeforeWritingAnyText = "Font and size must be set before writing any text.";
    public static final String FontEmbeddingIssue = "Font embedding issue.";
    public static final String FontProviderNotSetFontFamilyNotResolved = "FontProvider and FontSet are empty. Cannot resolve font family name (see ElementPropertyContainer#setFontFamily) without initialized FontProvider (see RootElement#setFontProvider).";
    @Deprecated
    public static final String FontSizeIsTooSmall = "Font size is too small.";
    public static final String FormXObjectMustHaveBbox = "Form XObject must have BBox.";
    public static final String FunctionIsNotCompatibleWitColorSpace = "Function is not compatible with ColorSpace.";
    public static final String GivenAccessibleElementIsNotConnectedToAnyTag = "Given accessible element is not connected to any tag.";
    public static final String IllegalCharacterInAsciihexdecode = "illegal character in ASCIIHexDecode.";
    public static final String IllegalCharacterInAscii85decode = "Illegal character in ASCII85Decode.";
    public static final String IllegalCharacterInCodabarBarcode = "Illegal character in Codabar Barcode.";
    public static final String IllegalLengthValue = "Illegal length value.";
    public static final String IllegalRValue = "Illegal R value.";
    public static final String IllegalVValue = "Illegal V value.";
    public static final String InAPageLabelThePageNumbersMustBeGreaterOrEqualTo1 = "In a page label the page numbers must be greater or equal to 1.";
    public static final String InCodabarStartStopCharactersAreOnlyAllowedAtTheExtremes = "In Codabar, start/stop characters are only allowed at the extremes.";
    public static final String InvalidHttpResponse1 = "Invalid http response {0}.";
    public static final String InvalidTsa1ResponseCode2 = "Invalid TSA {0} response code {1}.";
    public static final String IncorrectNumberOfComponents = "Incorrect number of components.";
    public static final String InvalidCodewordSize = "Invalid codeword size.";
    public static final String InvalidCrossReferenceEntryInThisXrefSubsection = "Invalid cross reference entry in this xref subsection.";
    public static final String InvalidIndirectReference1 = "Invalid indirect reference {0}.";
    public static final String InvalidMediaBoxValue = "Tne media box object has incorrect values.";
    public static final String InvalidPageStructure1 = "Invalid page structure {0}.";
    public static final String InvalidPageStructurePagesPagesMustBePdfDictionary = "Invalid page structure. /Pages must be PdfDictionary.";
    public static final String InvalidRangeArray = "Invalid range array.";
    public static final String InvalidOffsetForObject1 = "Invalid offset for object {0}.";
    public static final String InvalidXrefStream = "Invalid xref stream.";
    public static final String InvalidXrefTable = "Invalid xref table.";
    public static final String IoException = "I/O exception.";
    public static final String IoExceptionWhileCreatingFont = "I/O exception while creating Font";
    public static final String LzwDecoderException = "LZW decoder exception.";
    public static final String LzwFlavourNotSupported = "LZW flavour not supported.";
    public static final String MacroSegmentIdMustBeGtOrEqZero = "macroSegmentId must be >= 0";
    public static final String MacroSegmentIdMustBeGtZero = "macroSegmentId must be > 0";
    public static final String MacroSegmentIdMustBeLtMacroSegmentCount = "macroSegmentId must be < macroSemgentCount";
    public static final String MustBeATaggedDocument = "Must be a tagged document.";
    public static final String NumberOfEntriesInThisXrefSubsectionNotFound = "Number of entries in this xref subsection not found.";
    public static final String NoCompatibleEncryptionFound = "No compatible encryption found.";
    public static final String NoCryptoDictionaryDefined = "No crypto dictionary defined.";
    public static final String NoKidWithSuchRole = "No kid with such role.";
    /**
     * @deprecated Now we log a warning rather than throw an exception.
     */
    @Deprecated
    public static final String NoMaxLenPresent = "No /MaxLen has been set even though the Comb flag has been set.";
    public static final String NoninvertibleMatrixCannotBeProcessed = "A noninvertible matrix has been parsed. The behaviour is unpredictable.";
    public static final String NotAPlaceableWindowsMetafile = "Not a placeable windows metafile.";
    public static final String NotAValidPkcs7ObjectNotASequence = "Not a valid PKCS#7 object - not a sequence";
    public static final String NotAValidPkcs7ObjectNotSignedData = "Not a valid PKCS#7 object - not signed data.";
    public static final String NotAWmfImage = "Not a WMF image.";
    public static final String NoValidEncryptionMode = "No valid encryption mode.";
    public static final String NumberOfBooleansInTheArrayDoesntCorrespondWithTheNumberOfFields = "The number of booleans in the array doesn't correspond with the number of fields.";
    public static final String ObjectMustBeIndirectToWorkWithThisWrapper = "Object must be indirect to work with this wrapper.";
    public static final String ObjectNumberOfTheFirstObjectInThisXrefSubsectionNotFound = "Object number of the first object in this xref subsection not found.";
    public static final String OnlyIdentityCMapsSupportsWithTrueType = "Only Identity CMaps supports with truetype";
    public static final String OnlyBmpCanBeWrappedInWmf = "Only BMP can be wrapped in WMF.";
    public static final String OperatorEINotFoundAfterEndOfImageData = "Operator EI not found after the end of image data.";
    public static final String Page1CannotBeAddedToDocument2BecauseItBelongsToDocument3 = "Page {0} cannot be added to document {1}, because it belongs to document {2}.";
    public static final String PageIsNotSetForThePdfTagStructure = "Page is not set for the pdf tag structure.";
    public static final String PageAlreadyFlushed = "The page has been already flushed.";
    public static final String PageAlreadyFlushedUseAddFieldAppearanceToPageMethodBeforePageFlushing = "The page has been already flushed. Use PdfAcroForm#addFieldAppearanceToPage() method before page flushing.";
    public static final String PdfEncodings = "PdfEncodings exception.";
    public static final String PdfEncryption = "PdfEncryption exception.";
    public static final String PdfDecryption = "Exception occurred with PDF document decryption. One of the possible reasons is wrong password or wrong public key certificate and private key.";
    public static final String PdfDocumentMustBeOpenedInStampingMode = "PdfDocument must be opened in stamping mode.";
    public static final String PdfFormXobjectHasInvalidBbox = "PdfFormXObject has invalid BBox.";
    public static final String PdfObjectStreamReachMaxSize = "PdfObjectStream reach max size.";
    public static final String PdfPagesTreeCouldBeGeneratedOnlyOnce = "PdfPages tree could be generated only once.";
    public static final String PdfReaderHasBeenAlreadyUtilized = "Given PdfReader instance has already been utilized. The PdfReader cannot be reused, please create a new instance.";
    public static final String PdfStartxrefIsNotFollowedByANumber = "PDF startxref is not followed by a number.";
    public static final String PdfStartxrefNotFound = "PDF startxref not found.";
    public static final String PdfIndirectObjectBelongsToOtherPdfDocument = "Pdf indirect object belongs to other PDF document. Copy object to current pdf document.";
    public static final String PdfVersionNotValid = "PDF version is not valid.";
    public static final String RefArrayItemsInStructureElementDictionaryShallBeIndirectObjects = "Ref array items in structure element dictionary shall be indirect objects.";
    public static final String RequestedPageNumberIsOutOfBounds = "Requested page number {0} is out of bounds.";
    public static final String PngFilterUnknown = "PNG filter unknown.";
    public static final String PrintScalingEnforceEntryInvalid = "/PrintScaling shall may appear in the Enforce array only if the corresponding entry in the viewer preferences dictionary specifies a valid value other than AppDefault";
    public static final String ResourcesCannotBeNull = "Resources cannot be null.";
    public static final String ResourcesDoNotContainExtgstateEntryUnableToProcessOperator1 = "Resources do not contain ExtGState entry. Unable to process operator {0}.";
    public static final String RoleIsNotMappedToAnyStandardRole = "Role \"{0}\" is not mapped to any standard role.";
    public static final String RoleInNamespaceIsNotMappedToAnyStandardRole = "Role \"{0}\" in namespace {1} is not mapped to any standard role.";
    public static final String ShadingTypeNotFound = "Shading type not found.";
    public static final String SignatureWithName1IsNotTheLastItDoesntCoverWholeDocument = "Signature with name {0} is not the last. It doesn't cover the whole document.";
    public static final String StdcfNotFoundEncryption = "/StdCF not found (encryption)";
    public static final String StructParentIndexNotFoundInTaggedObject = "StructParent index not found in tagged object.";
    public static final String StructureElementInStructureDestinationShallBeAnIndirectObject = "Structure element referenced by a structure destination shall be an indirect object.";
    public static final String StructureElementShallContainParentObject = "StructureElement shall contain parent object.";
    public static final String StructureElementDictionaryShallBeAnIndirectObjectInOrderToHaveChildren = "Structure element dictionary shall be an indirect object in order to have children.";
    public static final String TagCannotBeMovedToTheAnotherDocumentsTagStructure = "Tag cannot be moved to the another document's tag structure.";
    public static final String TagFromTheExistingTagStructureIsFlushedCannotAddCopiedPageTags = "Tag from the existing tag structure is flushed. Cannot add copied page tags.";
    public static final String TagStructureCopyingFailedItMightBeCorruptedInOneOfTheDocuments = "Tag structure copying failed: it might be corrupted in one of the documents.";
    public static final String TagStructureFlushingFailedItMightBeCorrupted = "Tag structure flushing failed: it might be corrupted.";
    public static final String TagTreePointerIsInInvalidStateItPointsAtFlushedElementUseMoveToRoot = "TagTreePointer is in invalid state: it points at flushed element. Use TagTreePointer#moveToRoot.";
    public static final String TagTreePointerIsInInvalidStateItPointsAtRemovedElementUseMoveToRoot = "TagTreePointer is in invalid state: it points at removed element use TagTreePointer#moveToRoot.";
    public static final String TextCannotBeNull = "Text cannot be null.";
    public static final String TextIsTooBig = "Text is too big.";
    public static final String TextMustBeEven = "The text length must be even.";
    public static final String TwoBarcodeMustBeExternally = "The two barcodes must be composed externally.";
    public static final String ThereAreIllegalCharactersForBarcode128In1 = "There are illegal characters for barcode 128 in {0}.";
    public static final String ThereIsNoAssociatePdfWriterForMakingIndirects = "There is no associate PdfWriter for making indirects.";
    public static final String ThereIsNoFieldInTheDocumentWithSuchName1 = "There is no field in the document with such name: {0}.";
    public static final String ThisPkcs7ObjectHasMultipleSignerinfosOnlyOneIsSupportedAtThisTime = "This PKCS#7 object has multiple SignerInfos. Only one is supported at this time.";
    public static final String ThisInstanceOfPdfSignerAlreadyClosed = "This instance of PdfSigner has been already closed.";
    public static final String ToFlushThisWrapperUnderlyingObjectMustBeAddedToDocument = "To manually flush this wrapper, you have to ensure that the object behind this wrapper is added to the document, i.e. it has an indirect reference.";
    public static final String Tsa1FailedToReturnTimeStampToken2 = "TSA {0} failed to return time stamp token: {1}.";
    public static final String TrailerNotFound = "Trailer not found.";
    public static final String TrailerPrevEntryPointsToItsOwnCrossReferenceSection = "Trailer prev entry points to its own cross reference section.";
    public static final String UnbalancedBeginEndMarkedContentOperators = "Unbalanced begin/end marked content operators.";
    public static final String UnbalancedLayerOperators = "Unbalanced layer operators.";
    public static final String UnbalancedSaveRestoreStateOperators = "Unbalanced save restore state operators.";
    public static final String UnexpectedCharacter1FoundAfterIDInInlineImage = "Unexpected character {0} found after ID in inline image.";
    public static final String UnexpectedCloseBracket = "Unexpected close bracket.";
    public static final String UnexpectedColorSpace1 = "Unexpected ColorSpace: {0}.";
    public static final String UnexpectedEndOfFile = "Unexpected end of file.";
    public static final String UnexpectedGtGt = "unexpected >>.";
    public static final String UnexpectedShadingType = "Unexpected shading type.";
    public static final String UnknownEncryptionTypeREq1 = "Unknown encryption type R == {0}.";
    public static final String UnknownEncryptionTypeVEq1 = "Unknown encryption type V == {0}.";
    public static final String UnknownPdfException = "Unknown PdfException.";
    public static final String UnknownHashAlgorithm1 = "Unknown hash algorithm: {0}.";
    public static final String UnknownKeyAlgorithm1 = "Unknown key algorithm: {0}.";
    @Deprecated
    public static final String UnsupportedDefaultColorSpaceName1 = "Unsupported default color space name. Was {0}, but should be DefaultCMYK, DefaultGray or DefaultRGB";
    public static final String UnsupportedXObjectType = "Unsupported XObject type.";
    public static final String VerificationAlreadyOutput = "Verification already output.";
    public static final String WhenAddingObjectReferenceToTheTagTreeItMustBeConnectedToNotFlushedObject = "When adding object reference to the tag tree, it must be connected to not flushed object.";
    public static final String WhitePointIsIncorrectlySpecified = "White point is incorrectly specified.";
    public static final String WmfImageException = "WMF image exception.";
    public static final String WrongFormFieldAddAnnotationToTheField = "Wrong form field. Add annotation to the field.";
    /**
     * @deprecated in favour of more informative named constant
     */
    @Deprecated
    public static final String WrongMediaBoxSize1= "Wrong media box size: {0}.";
    public static final String WRONGMEDIABOXSIZETOOFEWARGUMENTS = "Wrong media box size: {0}. Need at least 4 arguments";
    public static final String XrefSubsectionNotFound = "xref subsection not found.";
    public static final String YouHaveToDefineABooleanArrayForThisCollectionSortDictionary = "You have to define a boolean array for this collection sort dictionary.";
    public static final String YouMustSetAValueBeforeAddingAPrefix = "You must set a value before adding a prefix.";
    public static final String YouNeedASingleBooleanForThisCollectionSortDictionary = "You need a single boolean for this collection sort dictionary.";
    public static final String QuadPointArrayLengthIsNotAMultipleOfEight = "The QuadPoint Array length is not a multiple of 8.";

    /**
     * Object for more details
     */
    protected Object object;

    private List<Object> messageParams;

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     */
    public PdfException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param cause the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public PdfException(Throwable cause) {
        this(UnknownPdfException, cause);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param obj     an object for more details.
     */
    public PdfException(String message, Object obj) {
        this(message);
        this.object = obj;
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public PdfException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     * @param obj     an object for more details.
     */
    public PdfException(String message, Throwable cause, Object obj) {
        this(message, cause);
        this.object = obj;
    }

    @Override
    public String getMessage() {
        if (messageParams == null || messageParams.size() == 0) {
            return super.getMessage();
        } else {
            return MessageFormatUtil.format(super.getMessage(), getMessageParams());
        }
    }

    /**
     * Sets additional params for Exception message.
     *
     * @param messageParams additional params.
     * @return object itself.
     */
    public PdfException setMessageParams(Object... messageParams) {
        this.messageParams = new ArrayList<>();
        Collections.addAll(this.messageParams, messageParams);
        return this;
    }

    /**
     * Gets additional params for Exception message.
     * @return array of additional params
     */
    protected Object[] getMessageParams() {
        Object[] parameters = new Object[messageParams.size()];
        for (int i = 0; i < messageParams.size(); i++) {
            parameters[i] = messageParams.get(i);
        }
        return parameters;
    }
}
