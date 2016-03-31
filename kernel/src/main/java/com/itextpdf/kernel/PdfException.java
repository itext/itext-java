package com.itextpdf.kernel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PdfException extends RuntimeException {

    public static final String _1IsAnUnknownGraphicsStateDictionary = "{0} is.an.unknown.graphics.state.dictionary";
    public static final String _1IsNotAValidPlaceableWindowsMetafile = "{0} is.not.a.valid.placeable.windows.metafile";

    public static final String AnnotationHasInvalidStructParentValue = "annotation.has.invalid.struct.parent.value";
    public static final String AnnotShallHaveReferenceToPage = "annot.shall.have.reference.to.page";
    public static final String AppendModeRequiresADocumentWithoutErrorsEvenIfRecoveryWasPossible = "append.mode.requires.a.document.without.errors.even.if.recovery.was.possible";
    public static final String AuthenticatedAttributeIsMissingTheDigest = "authenticated.attribute.is.missing.the.digest";
    public static final String AvailableSpaceIsNotEnoughForSignature = "available.space.is.not.enough.for.signature";
    public static final String BadCertificateAndKey = "bad.certificate.and.key";
    public static final String BadUserPassword = "bad.user.password";

    public static final String CannotAddKidToTheFlushedElement = "cannot.add.kid.to.the.flushed.element";
    public static final String CannotCloseDocument = "cannot.close.document";
    public static final String CannotCloseDocumentWithAlreadyFlushedPdfCatalog = "cannot.close.document.with.already.flushed.pdf.catalog";
    public static final String CannotConvertPdfArrayToRectanle = "cannot.convert.pdfarray.to.rectangle";
    public static final String CannotCopyFlushedObject = "cannot.copy.flushed.object";
    public static final String CannotCopyObjectContent = "cannot.copy.object.content";
    public static final String CannotCopyIndirectObjectFromTheDocumentThatIsBeingWritten = "cannot.copy.indirect.object.from.the.document.that.is.being.written";
    public static final String CannotCopyToDocumentOpenedInReadingMode = "cannot.copy.to.document.opened.in.reading.mode";
    public static final String CannotCreateLayoutImageByWmfImage = "Cannot create layout image by WmfImage instance. First convert the image into FormXObject and then use the corresponding layout image constructor";
    public static final String CannotCreatePdfImageXObjectByWmfImage = "Cannot create PdfImageXObject instance by WmfImage. Use PdfFormXObject constructor instead.";
    public static final String CannotCreatePdfStreamByInputStreamWithoutPdfDocument = "cannot.create.pdfstream.by.inputstream.without.pdfdocument";
    public static final String CannotGetContentBytes = "cannot.get.content.bytes";
    public static final String CannotGetPdfStreamBytes = "cannot.get.pdfstream.bytes";
    public static final String CannotFindImageDataOrEI = "cannot.find.image.data.or.EI";
    public static final String CannotFlushDocumentRootTagBeforeDocumentIsClosed = "cannot.flush.document.root.tag.before.document.is.closed";
    public static final String CannotFlushObject = "cannot.flush.object";
    public static final String CannotMoveToFlushedKid = "cannot.move.to.flushed.kid";
    public static final String CannotMoveToMarkedContentReference = "cannot.move.to.marked.content.reference";
    public static final String CannotMoveToParentCurrentElementIsRoot = "cannot.move.to.parent.current.element.is.root";
    public static final String CannotOpenDocument = "cannot.open.document";
    public static final String CannotParseContentStream = "could.not.parse.content.stream";
    public static final String CannotReadAStreamInOrderToAppendNewBytes = "cannot.read.a.stream.in.order.to.append.new.bytes.reason {0}";
    public static final String CannotReadPdfObject = "cannot.read.pdf.object";
    public static final String CannotModifyTagStructureWhenItWasPartlyFlushed = "cannot.modify.tag.structure.when.it.was.partly.flushed";
    public static final String CannotRemoveDocumentRootTag = "cannot.remove.document.root.tag";
    public static final String CannotRemoveTagStructureElementsIfTagStructureWasPartiallyFlushed = "cannot.remove.tag.structure.elements.if.tag.structure.was.partially.flushed";
    public static final String CannotSetDataToPdfstreamWhichWasCreatedByInputstream = "cannot.set.data.to.pdfstream.which.was.created.by.inputstream";
    public static final String CannotSplitDocumentThatIsBeingWritten = "cannot.split.document.that.is.being.written";
    public static final String CannotWritePdfStream = "cannot.write.pdf.stream";
    public static final String CannotWriteObjectAfterItWasReleased = "Cannot write object after it was released. In normal situation the object must be read once again before being written";
    public static final String CantDecodePkcs7SigneddataObject = "can.t.decode.pkcs7signeddata.object";
    public static final String CantFindSigningCertificateWithSerial1 = "can.t.find.signing.certificate.with.serial {0}";
    public static final String CfNotFoundEncryption = "cf.not.found.encryption";
    public static final String CodabarCharacterOneIsIllegal = "the.character {0} is.illegal.in.codabar";
    public static final String CodabarMustHaveAtLeastAStartAndStopCharacter = "codabar.must.have.at.least.a.start.and.stop.character";
    public static final String CodabarMustHaveOneAbcdAsStartStopCharacter = "codabar.must.have.one.of.abcd.as.start.stop.character";
    public static final String CodabarStartStopCharacterAreOnlyExtremes = "in.codabar.start.stop.characters.are.only.allowed.at.the.extremes";
    public static final String ColorNotFound = "color.not.found";
    public static final String ContentStreamMustNotInvokeOperatorsThatSpecifyColorsOrOtherColorRelatedParameters = "content.stream.must.not.invoke.operators.that.specify.colors.or.other.color.related.parameters.in.the.graphics.state";
    public static final String DecodeParameterType1IsNotSupported = "decode.parameter.type {0} is.not.supported";
    public static final String DefaultcryptfilterNotFoundEncryption = "defaultcryptfilter.not.found.encryption";
    public static final String DictionaryKey1IsNotAName = "dictionary.key {0} is.not.a.name";
    public static final String DictionaryNotContainFontData = "dict.not.contain.font.data";
    public static final String DocumentAlreadyPreClosed = "document.already.pre.closed";
    public static final String DocumentClosedImpossibleExecuteAction = "document.was.closed.it.is.impossible.execute.action";
    public static final String DocumentDoesntContainStructTreeRoot = "document.doesn't.contain.structtreeroot";
    public static final String DocumentHasNoPages = "document.has.no.pages";
    public static final String DocumentHasNoCatalogObject = "document.has.no.catalog.object";
    public static final String DocumentMustBePreclosed = "document.must.be.preclosed";
    public static final String DocumentToCopyToCannotBeNull = "document.to.copy.to.cannot.be.null";
    public static final String ElementCannotFitAnyArea = "element.cannot.fit.any.area";
    public static final String EncryptionCanOnlyBeAddedBeforeOpeningDocument = "encryption.can.only.be.added.before.opening.the.document";
    public static final String EndOfContentStreamReachedBeforeEndOfImageData = "end.of.content.stream.reached.before.end.of.image.data";
    public static final String ErrorReadingObjectStream = "error.reading.objstm";

    public static final String FailedToGetTsaResponseFrom1 = "failed.to.get.tsa.response.from {0}";
    public static final String FieldFlatteningIsNotSupportedInAppendMode = "field.flattening.is.not.supported.in.append.mode";
    public static final String FieldIsAlreadySigned = "field.flattening.is.not.supported.in.append.mode";

    public static final String FieldNamesCannotContainADot = "field.names.cannot.contain.a.dot";
    public static final String FieldTypeIsNotASignatureFieldType = "the.field.type.is.not.a.signature.field.type";
    public static final String Filter1IsNotSupported = "filter {0} is.not.supported";
    public static final String FilePosition0CrossReferenceEntryInThisXrefSubsection = "file.position {0} cross.reference.entry.in.this.xref.subsection";
    public static final String FilterCcittfaxdecodeIsOnlySupportedForImages = "filter.ccittfaxdecode.is.only.supported.for.images";
    public static final String FilterIsNotANameOrArray = "filter.is.not.a.name.or.array";
    public static final String FlushedPageCannotBeAddedOrInserted = "flushed.page.cannot.be.added.or.inserted";
    public static final String FontAndSizeMustBeSetBeforeWritingAnyText = "font.and.size.must.be.set.before.writing.any.text";
    public static final String FontEmbeddingIssue = "font.embedding.issue";
    public static final String FontSizeTooSmall = "font.size.too.small";
    public static final String FormXObjectMustHaveBbox = "form.xobject.must.have.bbox";
    public static final String FunctionIsNotCompatibleWitColorSpace = "function.is.not.compatible.with.color.space";
    public static final String GivenAccessibleElementIsNotConnectedToAnyTag = "given.accessible.element.is.not.connected.to.any.tag";
    public static final String IllegalCharacterInAsciihexdecode = "illegal.character.in.asciihexdecode";
    public static final String IllegalCharacterInAscii85decode = "illegal.character.in.ascii85decode";
    public static final String IllegalLengthValue = "illegal.length.value";
    public static final String IllegalPValue = "illegal.p.value";
    public static final String IllegalRValue = "illegal.r.value";
    public static final String IllegalVValue = "illegal.v.value";
    public static final String InAPageLabelThePageNumbersMustBeGreaterOrEqualTo1 = "in.a.page.label.the.page.numbers.must.be.greater.or.equal.to.1";
    public static final String InvalidHttpResponse1 = "invalid.http.response {0}";
    public static final String InvalidTsa1ResponseCode2 = "invalid.tsa {0} response.code {1}";
    public static final String IncorrectNumberOfComponents = "incorrect.number.of.components";
    public static final String InlineLevelOrIllustrationElementCannotContainKids = "inline.level.or.illustration.element.cannot.contain.kids";
    public static final String InvalidCodewordSize = "invalid.codeword.size";
    public static final String InvalidCrossReferenceEntryInThisXrefSubsection = "invalid.cross.reference.entry.in.this.xref.subsection";
    public static final String InvalidIndirectReference1 = "invalid.indirect.reference {0}";
    public static final String InvalidPageStructure1 = "invalid.page.structure {0}";
    public static final String InvalidPageStructurePagesPagesMustBePdfDictionary = "invalid.page.structure.pages.must.be.pdfdictionary";
    public static final String InvalidRangeArray = "invalid.range.array";
    public static final String InvalidOffsetForObject1 = "invalid.offset.for.object {0}";
    public static final String InvalidXrefSection = "invalid.xref.section";
    public static final String InvalidXrefStream = "invalid.xref.stream";
    public static final String IoException = "io.exception";
    public static final String IsNotAnAcceptableValueForTheField = "{0}.is.not.an.acceptable.value.for.the.field.{1}";
    public static final String IsNotWmfImage = "is.not.wmf.image";
    public static final String LzwDecoderException = "lzw.decoder.exception";
    public static final String LzwFlavourNotSupported = "lzw.flavour.not.supported";
    public static final String MacroSegmentIdMustBeGtOrEqZero = "macrosegmentid.must.be.gt.eq.0";
    public static final String MacroSegmentIdMustBeGtZero = "macrosegmentid.must.be.gt.0";
    public static final String MacroSegmentIdMustBeLtMacroSegmentCount = "macrosegmentid.must.be.lt.macrosegmentcount";
    public static final String MustBeATaggedDocument = "must.be.a.tagged.document";
    public static final String NumberOfEntriesInThisXrefSubsectionNotFound = "number.of.entries.in.this.xref.subsection.not.found";
    public static final String NameAlreadyExistsInTheNameTree = "name.already.exist.in.the.name.tree";
    public static final String NoCompatibleEncryptionFound = "no.compatible.encryption.found";
    public static final String NoCryptoDictionaryDefined = "no.crypto.dictionary.defined";
    public static final String NoKidWithSuchRole = "no.kid.with.such.role";
    public static final String NotAPlaceableWindowsMetafile = "not.a.placeable.windows.metafile";
    public static final String NotAValidPkcs7ObjectNotASequence = "not.a.valid.pkcs.7.object.not.a.sequence";
    public static final String NotAValidPkcs7ObjectNotSignedData = "not.a.valid.pkcs.7.object.not.signed.data";
    public static final String NoValidEncryptionMode = "no.valid.encryption.mode";
    public static final String ObjectMustBeIndirectToWorkWithThisWrapper = "object.must.be.indirect.to.work.with.this.wrapper";
    public static final String ObjectNumberOfTheFirstObjectInThisXrefSubsectionNotFound = "object.number.of.the.first.object.in.this.xref.subsection.not.found";
    public static final String OcspStatusIsRevoked = "ocsp.status.is.revoked";
    public static final String OcspStatusIsUnknown = "ocsp.status.is.unknown";
    public static final String OnlyBmpCanBeWrappedInWmf = "only.bmp.can.be.wrapped.in.wmf";
    public static final String OperatorEINotFoundAfterEndOfImageData = "operator.EI.not.found.after.end.of.image.data";
    public static final String Page1CannotBeAddedToDocument2BecauseItBelongsToDocument3 = "page {0} cannot.be.added.to.document {1} because.it.belongs.to.document {2}";
    public static final String PageIsNotSetForThePdfTagStructure = "page.is.not.set.for.the.pdf.tag.structure";
    public static final String PageWasAlreadyFlushed = "the.page.was.already.flushed";
    public static final String PageWasAlreadyFlushedUseAddFieldAppearanceToPageMethodBeforePageFlushing = "the.page.was.already.flushed.use.add.field.appearance.to.page.method.before.page.flushing";
    public static final String PdfEncodings = "pdf.encodings";
    public static final String PdfEncryption = "pdf.encryption";
    public static final String PdfDecryption = "pdf.decryption";
    public static final String PdfDocumentMustBeOpenedInStampingMode = "pdf.document.must.be.opened.in.stamping.mode";
    public static final String PdfFormXobjectHasInvalidBbox = "pdf.form.xobject.has.invalid.bbox";
    public static final String PdfObjectStreamReachMaxSize = "pdf.object.stream.reach.max.size";
    public static final String PdfPageShallHaveContent = "pdf.page.shall.have.content";
    public static final String PdfPagesTreeCouldBeGeneratedOnlyOnce = "pdf.pages.tree.could.be.generated.only.once";
    public static final String PdfStartxrefIsNotFollowedByANumber = "pdf.startxref.is.not.followed.by.a.number";
    public static final String PdfStartxrefNotFound = "pdf.startxref.not.found";
    public static final String PdfInderectObjectBelongToOtherPdfDocument = "pdf.inderect.object.belong.to.other.pdf.document.Copy.object.to.current.pdf.document";
    public static final String PdfVersionNotValid = "pdf.version.not.valid";
    public static final String PngFilterUnknown = "png.filter.unknown";
    public static final String ResourcesCannotBeNull = "resources.cannot.be.null";
    public static final String ResourcesDoNotContainExtgstateEntryUnableToProcessOperator1 = "resources.do.not.contain.extgstate.entry.unable.to.process.operator {0}";
    public static final String RoleIsNotMappedWithAnyStandardRole = "role.is.not.mapped.with.any.standard.role";
    public static final String SignatureWithName1IsNotTheLastItDoesntCoverWholeDocument = "signature.with.name.1.is.not.the.last.it.doesnt.cover.whole.document";
    public static final String StdcfNotFoundEncryption = "stdcf.not.found.encryption";
    public static final String StructureElementShallContainParentObject = "structure.element.shall.contain.parent.object";
    public static final String TagCannotBeMovedToTheAnotherDocumentsTagStructure = "tag.cannot.be.moved.to.the.another.documents.tag.structure";
    public static final String TagTreePointerIsInInvalidStateItPointsAtFlushedElementUseMoveToRoot = "tagtreepointer.is.in.invalid.state.it.points.at.flushed.element.use.movetoroot";
    public static final String TagTreePointerIsInInvalidStateItPointsAtRemovedElementUseMoveToRoot = "tagtreepointer.is.in.invalid.state.it.points.at.removed.element.use.movetoroot";
    public static final String TextCannotBeNull = "text.cannot.be.null";
    public static final String TextIsTooBig = "text.is.too.big";
    public static final String TextMustBeEven = "the.text.length.must.be.even";
    public static final String TwoBarcodeMustBeExternally = "the.two.barcodes.must.be.composed.externally";
    public static final String TheNumberOfBooleansInTheArrayDoesntCorrespondWithTheNumberOfFields = "the.number.of.booleans.in.the.array.doesn.t.correspond.with.the.number.of.fields";
    public static final String ThereAreIllegalCharactersForBarcode128In1 = "there.are.illegal.characters.for.barcode.128.in {0}";
    public static final String ThereIsNoAssociatePdfWriterForMakingIndirects = "there.is.no.associate.pdf.writer.for.making.indirects";
    public static final String ThereIsNoFieldInTheDocumentWithSuchName1 = "there.is.no.field.in.the.document.with.such.name {0}";
    public static final String ThisPkcs7ObjectHasMultipleSignerinfosOnlyOneIsSupportedAtThisTime = "this.pkcs.7.object.has.multiple.signerinfos.only.one.is.supported.at.this.time";
    public static final String ThisInstanceOfPdfSignerIsAlreadyClosed = "this.instance.of.PdfSigner.is.already.closed";
    public static final String Tsa1FailedToReturnTimeStampToken2 = "tsa {0} failed.to.return.time.stamp.token {1}";
    public static final String TrailerNotFound = "trailer.not.found";
    public static final String TrailerPrevEntryPointsToItsOwnCrossReferenceSection = "trailer.prev.entry.points.to.its.own.cross.reference.section";
    public static final String UnbalancedBeginEndMarkedContentOperators = "unbalanced.begin.end.marked.content.operators";
    public static final String UnbalancedLayerOperators = "unbalanced.layer.operators";
    public static final String UnexpectedCharacter1FoundAfterIDInInlineImage = "unexpected.character.1.found.after.ID.in.inline.image";
    public static final String UnexpectedCloseBracket = "unexpected.close.bracket";
    public static final String UnexpectedColorSpace1 = "unexpected.color.space {0}";
    public static final String UnexpectedEndOfFile = "unexpected.end.of.file";
    public static final String UnexpectedGtGt = "unexpected.gt.gt";
    public static final String UnknownEncryptionTypeREq1 = "unknown.encryption.type.r.eq {0}";
    public static final String UnknownEncryptionTypeVEq1 = "unknown.encryption.type.v.eq {0}";
    public static final String UnknownHashAlgorithm1 = "unknown.hash.algorithm {0}";
    public static final String UnknownKeyAlgorithm1 = "unknown.key.algorithm {0}";
    public static final String UnknownColorFormatMustBeRGBorRRGGBB = "unknown.color.format.must.be.rgb.or.rrggbb";
    public static final String VerificationAlreadyOutput = "verification.already.output";
    public static final String WhitePointIsIncorrectlySpecified = "white.point.is.incorrectly.specified";
    public static final String WmfImageException = "wmf.image.exception";
    public static final String WrongFormFieldAddAnnotationToTheField = "wrong.form.field.add.annotation.to.the.field";
    public static final String XrefSubsectionNotFound = "xref.subsection.not.found";
    public static final String YouCannotFlushPdfCatalogManually = "you.cannot.flush.pdf.catalog.manually";
    public static final String YouHaveToDefineABooleanArrayForThisCollectionSortDictionary = "you.have.to.define.a.boolean.array.for.this.collection.sort.dictionary";
    public static final String YouMustSetAValueBeforeAddingAPrefix = "you.must.set.a.value.before.adding.a.prefix";
    public static final String YouNeedASingleBooleanForThisCollectionSortDictionary = "you.need.a.single.boolean.for.this.collection.sort.dictionary";

    protected Object object;
    private List<Object> messageParams;

    public PdfException(String message) {
        super(message);
    }

    public PdfException(Throwable cause) {
        super(cause);
    }

    public PdfException(String message, Object object) {
        this(message);
        this.object = object;
    }

    public PdfException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfException(String message, Throwable cause, Object object) {
        this(message, cause);
        this.object = object;
    }

    @Override
    public String getMessage() {
        if (messageParams == null || messageParams.isEmpty()) {
            return super.getMessage();
        } else {
            return MessageFormat.format(super.getMessage(), messageParams.toArray());
        }
    }

    public PdfException setMessageParams(Object... messageParams) {
        this.messageParams = new ArrayList<>();
        Collections.addAll(this.messageParams, messageParams);
        return this;
    }
}
