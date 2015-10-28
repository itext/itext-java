package com.itextpdf.basics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PdfException extends RuntimeException {

    public static final String _1BitSamplesAreNotSupportedForHorizontalDifferencingPredictor = "1.bit.samples.are.not.supported.for.horizontal.differencing.predictor";


    public static final String _1CorruptedJfifMarker = "1.corrupted.jfif.marker";
    public static final String _1IsAnUnknownImageFormat = "1.is.an.unknown.image.format";
    public static final String _1IsNotAValidJpegFile = "1.is.not.a.valid.jpeg.file";
    public static final String _1IsNotAValidPlaceableWindowsMetafile = "1.is.not.a.valid.placeable.windows.metafile";
    public static final String _1MustHave8BitsPerComponent = "1.must.have.8.bits.per.component";
    public static final String _1UnsupportedJpegMarker2 = "1.unsupported.jpeg.marker.2";


    public static final String AllFillBitsPrecedingEolCodeMustBe0 = "all.fill.bits.preceding.eol.code.must.be.0";
    public static final String AnnotShallHaveReferenceToPage = "annot.shall.have.reference.to.page";
    public static final String AppendModeRequiresADocumentWithoutErrorsEvenIfRecoveryWasPossible = "append.mode.requires.a.document.without.errors.even.if.recovery.was.possible";
    public static final String BadEndiannessTagNot0x4949Or0x4d4d = "bad.endianness.tag.not.0x4949.or.0x4d4d";
    public static final String BadMagicNumberShouldBe42 = "bad.magic.number.should.be.42";
    public static final String BadCertificateAndKey = "bad.certificate.and.key";
    public static final String BadUserPassword = "bad.user.password";
    public static final String BitsPerComponentMustBe1_2_4or8 = "bits.per.component.must.be.1.2.4.or.8";
    public static final String BitsPerSample1IsNotSupported = "bits.per.sample.1.is.not.supported";
    public static final String BmpImageException = "bmp.image.exception";
    public static final String BytesCanBeAssignedToByteArrayOutputStreamOnly = "bytes.can.be.assigned.to.bytearrayoutputstream.only";
    public static final String BytesCanBeResetInByteArrayOutputStreamOnly = "bytes.can.be.reset.in.bytearrayoutputstream.only";

    public static final String CannotAddObjectToObjectstream = "cannot.add.object.to.objectstream";
    public static final String CannotAddStructElemToContent = "cannot.add.structelem.to.content";
    public static final String CannotCloseDocument = "cannot.close.document";
    public static final String CannotCloseDocumentWithAlreadyFlushedPdfCatalog = "cannot.close.document.with.already.flushed.pdf.catalog";
    public static final String CannotConvertPdfArrayToRectanle = "cannot.convert.pdfarray.to.rectangle";
    public static final String CannotCopyFlushedObject = "cannot.copy.flushed.object";
    public static final String CannotCopyObjectContent = "cannot.copy.object.content";
    public static final String CannotCopyIndirectObjectFromTheDocumentThatIsBeingWritten = "cannot.copy.indirect.object.from.the.document.that.is.being.written";
    public static final String CannotCopyToDocumentOpenedInReadingMode = "cannot.copy.to.document.opened.in.reading.mode";
    public static final String CannotCreatePdfStreamByInputStreamWithoutPdfDocument = "cannot.create.pdfstream.by.inputstream.without.pdfdocument";
    public static final String CannotGetContentBytes = "cannot.get.content.bytes";
    public static final String CannotGetPdfStreamBytes = "cannot.get.pdfstream.bytes";
    public static final String CannotGetTiffImageColor = "cannot.get.tiff.image.color";
    public static final String CannotGetTiffNumberOfPages = "cannot.get.tiff.number.of.pages";
    public static final String CannotFind1Frame = "cannot.find.1.frame";
    public static final String CannotFlushObject = "cannot.flush.object";
    public static final String CannotHandleBoxSizesHigherThan2_32 = "cannot.handle.box.sizes.higher.than.2.32";
    public static final String CannotInflateTiffImage = "cannot.inflate.tiff.image";
    public static final String CannotOpenDocument = "cannot.open.document";
    public static final String CannotReadAStreamInOrderToAppendNewBytesReason1 = "cannot.read.a.stream.in.order.to.append.new.bytes.reason.1";
    public static final String CannotReadPdfObject = "cannot.read.pdf.object";
    public static final String CannotReadTiffImage = "cannot.read.tiff.image";
    public static final String CannotSetDataToPdfstreamWhichWasCreatedByInputstream = "cannot.set.data.to.pdfstream.which.was.created.by.inputstream";
    public static final String CannotSplitDocumentThatIsBeingWritten = "cannot.split.document.that.is.being.written";
    public static final String CannotWriteByte = "cannot.write.byte";
    public static final String CannotWriteBytes = "cannot.write.bytes";
    public static final String CannotWriteFloatNumber = "cannot.write.float.number";
    public static final String CannotWriteIntNumber = "cannot.write.int.number";
    public static final String CannotWritePdfStream = "cannot.write.pdf.stream";
    public static final String CcittCompressionTypeMustBeCcittg4Ccittg3_1dOrCcittg3_2d = "ccitt.compression.type.must.be.ccittg4.ccittg3.1d.or.ccittg3.2d";
    public static final String CfNotFoundEncryption = "cf.not.found.encryption";
    public static final String CodabarCharacterOneIsIllegal = "the.character.1.is.illegal.in.codabar";
    public static final String CodabarMustHaveAtLeastAStartAndStopCharacter = "codabar.must.have.at.least.a.start.and.stop.character";
    public static final String CodabarMustHaveOneAbcdAsStartStopCharacter  = "codabar.must.have.one.of.abcd.as.start.stop.character";
    public static final String CodabarStartStopCharacterAreOnlyExtremes =  "in.codabar.start.stop.characters.are.only.allowed.at.the.extremes";
    public static final String ComponentsMustBe1_3Or4 = "components.must.be.1.3.or.4";
    public static final String Compression1IsNotSupported = "compression.1.is.not.supported";
    public static final String CompressionJpegIsOnlySupportedWithASingleStripThisImageHas1Strips = "compression.jpeg.is.only.supported.with.a.single.strip.this.image.has.1.strips";
    public static final String ContentStreamMustNotInvokeOperatorsThatSpecifyColorsOrOtherColorRelatedParameters = "content.stream.must.not.invoke.operators.that.specify.colors.or.other.color.related.parameters.in.the.graphics.state";
    public static final String DecodeParameterType1IsNotSupported = "decode.parameter.type.1.is.not.supported";
    public static final String DefaultcryptfilterNotFoundEncryption = "defaultcryptfilter.not.found.encryption";
    public static final String DictionaryKey1IsNotAName = "dictionary.key.1.is.not.a.name";
    public static final String DictionaryNotContainFontData = "dict.not.contain.font.data";
    public static final String DirectoryNumberTooLarge = "directory.number.too.large";
    public static final String DocumentDoesntContainStructTreeRoot = "document.doesn't.contain.structtreeroot";
    public static final String DocumentHasNoPages = "document.has.no.pages";
    public static final String DocumentHasNoCatalogObject = "document.has.no.catalog.object";
    public static final String DocumentToCopyToCannotBeNull = "document.to.copy.to.cannot.be.null";
    public static final String ElementCannotFitAnyArea = "element.cannot.fit.any.area";
    public static final String EncryptionCanOnlyBeAddedBeforeOpeningDocument = "encryption.can.only.be.added.before.opening.the.document";
    public static final String EolCodeWordEncounteredInBlackRun = "eol.code.word.encountered.in.black.run";
    public static final String EolCodeWordEncounteredInWhiteRun = "eol.code.word.encountered.in.white.run";
    public static final String ErrorAtFilePointer1 = "error.at.file.pointer.1";
    public static final String ErrorReadingObjectStream = "error.reading.objstm";
    public static final String ErrorReadingString = "error.reading.string";
    public static final String ErrorWithJpMarker = "error.with.jp.marker";

    public static final String ExpectedFtypMarker = "expected.ftyp.marker";
    public static final String ExpectedIhdrMarker = "expected.ihdr.marker";
    public static final String ExpectedJpMarker = "expected.jp.marker";
    public static final String ExpectedJp2hMarker = "expected.jp2h.marker";

    public static final String ExtraSamplesAreNotSupported = "extra.samples.are.not.supported";
    public static final String FdfStartxrefNotFound = "fdf.startxref.not.found";
    public static final String FieldFlatteningIsNotSupportedInAppendMode = "field.flattening.is.not.supported.in.append.mode";
    public static final String Filter1IsNotSupported = "filter.1.is.not.supported";
    public static final String FilePosition0CrossReferenceEntryInThisXrefSubsection = "file.position.0.cross.reference.entry.in.this.xref.subsection";
    public static final String FilterCcittfaxdecodeIsOnlySupportedForImages = "filter.ccittfaxdecode.is.only.supported.for.images";
    public static final String FirstScanlineMustBe1dEncoded = "first.scanline.must.be.1d.encoded";
    public static final String FlateCompressException = "flate.compress.exception";
    public static final String FlushedPageCannotBeAddedOrInserted = "flushed.page.cannot.be.added.or.inserted";
    public static final String FontAndSizeMustBeSetBeforeWritingAnyText = "font.and.size.must.be.set.before.writing.any.text";
    public static final String FontSizeTooSmall = "font.size.too.small";
    public static final String FormXObjectMustHaveBbox = "form.xobject.must.have.bbox";
    public static final String FunctionIsNotCompatibleWitColorSpace = "function.is.not.compatible.with.color.space";
    public static final String ImageFormatCannotBeRecognized = "image.format.cannot.be.recognized";
    public static final String GifImageException = "gif.image.exception";
    public static final String GtNotExpected = "gt.not.expected";
    public static final String GifSignatureNotFound = "gif.signature.not.found";
    public static final String IllegalValueForPredictorInTiffFile = "illegal.value.for.predictor.in.tiff.file";
    public static final String IllegalCharacterInAsciihexdecode = "illegal.character.in.asciihexdecode";
    public static final String IllegalCharacterInAscii85decode = "illegal.character.in.ascii85decode";
    public static final String IllegalLengthInAscii85decode = "illegal.length.in.ascii85decode";
    public static final String IllegalLengthValue = "illegal.length.value";
    public static final String IllegalPValue = "illegal.p.value";
    public static final String IllegalResourceTree = "illegal.resources.tree";
    public static final String IllegalRValue = "illegal.r.value";
    public static final String IllegalVValue = "illegal.v.value";
    public static final String Font1With2IsNotRecognized = "font.1.with.2.is.not.recognized";
    public static final String FontIsNotRecognized = "font.is.not.recognized";

    public static final String ImageCanNotBeAnImageMask = "image.can.not.be.an.image.mask";
    public static final String ImageMaskCannotContainAnotherImageMask = "image.mask.cannot.contain.another.image.mask";
    public static final String ImageMaskIsNotAMaskDidYouDoMakeMask = "image.mask.is.not.a.mask.did.you.do.makemask";
    public static final String IncompletePalette = "incomplete.palette";
    public static final String IncorrectMcrParent = "incorrect.mcr.parent";
    public static final String IncorrectNumberOfComponents = "incorrect.number.of.components";

    public static final String InfiniteIndirectReferenceChain = "infinite.indirect.reference.chain";
    public static final String InlineLevelOrIllustrationElementCannotContainKids = "inline.level.or.illustration.element.cannot.contain.kids";
    public static final String InvalidTTCFile = "1.is.not.a.valid.ttc.file";
    public static final String InvalidBmpFileCompression = "invalid.bmp.file.compression";
    public static final String InvalidCodeEncountered = "invalid.code.encountered";
    public static final String InvalidCodeEncounteredWhileDecoding2dGroup3CompressedData = "invalid.code.encountered.while.decoding.2d.group.3.compressed.data";
    public static final String InvalidCodeEncounteredWhileDecoding2dGroup4CompressedData = "invalid.code.encountered.while.decoding.2d.group.4.compressed.data";
    public static final String InvalidCodewordSize = "invalid.codeword.size";
    public static final String InvalidCrossReferenceEntryInThisXrefSubsection = "invalid.cross.reference.entry.in.this.xref.subsection";
    public static final String InvalidIndirectReference1 = "invalid.indirect.reference.1";
    public static final String InvalidIccProfile = "invalid.icc.profile";
    public static final String InvalidJpeg2000File = "invalid.jpeg2000.file";
    public static final String InvalidMagicValueForBmpFile = "invalid.magic.value.for.bmp.file";
    public static final String InvalidPageStructure1 = "invalid.page.structure.1";
    public static final String InvalidPageStructurePagesKidsMustBePdfArray = "invalid.page.structure.pages.kids.must.be.pdfarray";
    public static final String InvalidPageStructurePagesPagesMustBePdfDictionary = "invalid.page.structure.pages.must.be.pdfdictionary";
    public static final String InvalidRangeArray = "invalid.range.array";
    public static final String InvalidOffsetForObject1 = "invalid.offset.for.object.1";
    public static final String InvalidXrefSection = "invalid.xref.section";
    public static final String InvalidXrefStream = "invalid.xref.stream";
    public static final String IoException = "io.exception";
    public static final String IsNotWmfImage = "is.not.wmf.image";
    public static final String Jbig2ImageException = "jbig2.image.exception";
    public static final String JpegImageException = "jpeg.image.exception";
    public static final String Jpeg2000ImageException = "jpeg2000.image.exception";
    public static final String LzwDecoderException = "lzw.decoder.exception";
    public static final String LzwFlavourNotSupported = "lzw.flavour.not.supported";
    public static final String MacroSegmentIdMustBeGtOrEqZero = "macrosegmentid.must.be.gt.eq.0";
    public static final String MacroSegmentIdMustBeGtZero = "macrosegmentid.must.be.gt.0";
    public static final String MacroSegmentIdMustBeLtMacroSegmentCount = "macrosegmentid.must.be.lt.macrosegmentcount";
    public static final String MissingTagSForOjpegCompression = "missing.tag.s.for.ojpeg.compression";
    public static final String NumberOfEntriesInThisXrefSubsectionNotFound = "number.of.entries.in.this.xref.subsection.not.found";
    public static final String NameAlreadyExistsInTheNameTree = "name.already.exist.in.the.name.tree";
    public static final String NoCompatibleEncryptionFound = "no.compatible.encryption.found";
    public static final String NotAPlaceableWindowsMetafile = "not.a.placeable.windows.metafile";
    public static final String NoValidEncryptionMode = "no.valid.encryption.mode";
    public static final String ObjectNumberOfTheFirstObjectInThisXrefSubsectionNotFound = "object.number.of.the.first.object.in.this.xref.subsection.not.found";
    public static final String OnlyBmpCanBeWrappedInWmf = "only.bmp.can.be.wrapped.in.wmf";
    public static final String PageWasAlreadyFlushedUseAddFieldAppearanceToPageMethodInstead = "the.page.was.already.flushed.use.add.field.appearance.to.page.method.instead";
    public static final String Page1CannotBeAddedToDocument2BecauseItBelongsToDocument3 = "page.1.cannot.be.added.to.document.2.because.it.belongs.to.document.3";
    public static final String PageNumberMustBeGtEq1 = "page.number.must.be.gt.eq.1";
    public static final String PdfEncodings = "pdf.encodings";
    public static final String PdfEncryption = "pdf.encryption";
    public static final String PdfDecryption = "pdf.decryption";
    public static final String PdfFormXobjectHasInvalidBbox = "pdf.form.xobject.has.invalid.bbox";
    public static final String PdfHeaderNotFound = "pdf.header.not.found";
    public static final String PdfObjectStreamReachMaxSize = "pdf.object.stream.reach.max.size";
    public static final String PdfPageShallHaveContent = "pdf.page.shall.have.content";
    public static final String PdfPagesTreeCouldBeGeneratedOnlyOnce = "pdf.pages.tree.could.be.generated.only.once";
    public static final String PdfStartxrefIsNotFollowedByANumber = "pdf.startxref.is.not.followed.by.a.number";
    public static final String PdfStartxrefNotFound = "pdf.startxref.not.found";
    public static final String PdfStringHasNoContentOrHasAlreadyDecrypted = "pdf.string.has.no.content.or.has.already.decrypted";
    public static final String PdfVersionNotValid = "pdf.version.not.valid";
    public static final String Photometric1IsNotSupported = "photometric.1.is.not.supported";
    public static final String PlanarImagesAreNotSupported = "planar.images.are.not.supported";
    public static final String PngFilterUnknown = "png.filter.unknown";
    public static final String PngImageException = "png.image.exception";
    public static final String PrematureEofWhileReadingJpg = "premature.eof.while.reading.jpg";
    public static final String ScanlineMustBeginWithEolCodeWord = "scanline.must.begin.with.eol.code.word";
    public static final String StdcfNotFoundEncryption = "stdcf.not.found.encryption";
    public static final String FilterIsNotANameOrArray = "filter.is.not.a.name.or.array";
    public static final String StructureElementIsNotLinkedToStructParent = "structure.element.is.not.linked.to.structparent";
    public static final String StructureElementShallContainParentObject = "structure.element.shall.contain.parent.object";
    public static final String StructureElementShallContainPgKey = "structure.element.shall.contain.pg.key";
    public static final String TextCannotBeNull = "text.cannot.be.null";
    public static final String TextIsTooBig = "text.is.too.big";
    public static final String TextMustBeEven = "the.text.length.must.be.even";
    public static final String TwoBarcodeMustBeExternally = "the.two.barcodes.must.be.composed.externally";
    public static final String ThereAreIllegalCharactersForBarcode128In1 = "there.are.illegal.characters.for.barcode.128.in.1";
    public static final String ThereIsNoAssociatePdfWriterForMakingIndirects = "there.is.no.associate.pdf.writer.for.making.indirects";
    public static final String Tiff50StyleLzwCodesAreNotSupported = "tiff.5.0.style.lzw.codes.are.not.supported";
    public static final String TiffFillOrderTagMustBeEither1Or2 = "tiff.fill.order.tag.must.be.either.1.or.2";
    public static final String TiffImageException = "tiff.image.exception";
    public static final String TTCIndexDoesNotExistInFile = "ttc.index.doesn't.exist.in.ttc.file";
    public static final String TilesAreNotSupported = "tiles.are.not.supported";
    public static final String TrailerNotFound = "trailer.not.found";
    public static final String TrailerPrevEntryPointsToItsOwnCrossReferenceSection = "trailer.prev.entry.points.to.its.own.cross.reference.section";
    public static final String TransparencyLengthMustBeEqualTo2WithCcittImages = "transparency.length.must.be.equal.to.2.with.ccitt.images";
    public static final String UnbalancedBeginEndMarkedContentOperators = "unbalanced.begin.end.marked.content.operators";
    public static final String UnbalancedLayerOperators = "unbalanced.layer.operators";
    public static final String UnexpectedCloseBracket = "unexpected.close.bracket";
    public static final String UnexpectedEndOfFile = "unexpected.end.of.file";
    public static final String UnexpectedGtGt = "unexpected.gt.gt";
    public static final String UnknownCompressionType1 = "unknown.compression.type.1";
    public static final String UnknownEncryptionTypeREq1 = "unknown.encryption.type.r.eq.1";
    public static final String UnknownEncryptionTypeVEq1 = "unknown.encryption.type.v.eq.1";
    public static final String UnsupportedBoxSizeEqEq0 = "unsupported.box.size.eq.eq.0";
    public static final String WhitePointIsIncorrectlySpecified = "white.point.is.incorrectly.specified";
    public static final String WmfImageException = "wmf.image.exception";
    public static final String WrongFormFieldAddAnnotationToTheField = "wrong.form.field.add.annotation.to.the.field";
    public static final String WrongNumberOfComponentsInIccProfile = "icc.profile.contains.1.components.the.image.data.contains.2.components";
    public static final String XrefSubsectionNotFound = "xref.subsection.not.found";
    public static final String XrefTableDoesntHaveSuitableItemForObject1 = "xref.table.doesn't.have.suitable.item.for.object.1";
    public static final String YouCannotFlushPdfCatalogManually = "you.cannot.flush.pdf.catalog.manually";
    public static final String YouCannotFlushStructTreeRootManually = "you.cannot.flush.structtreeroot.manually";

    protected Object object;
    protected String composedMessage;
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
        if (messageParams != null) {
            StringBuilder builder = new StringBuilder(super.getMessage());
            builder.append('+');
            for (Object obj : messageParams) {
                builder.append(obj.toString()).append('+');
            }
            return builder.substring(0, builder.length() - 1);
        }
        return super.getMessage();
    }

    public PdfException setMessageParams(Object... messageParams) { // TODO: make it to be constructor parameter
        this.messageParams = new ArrayList<Object>();
        Collections.addAll(this.messageParams, messageParams);
        return this;
    }

    public String getComposedMessage() {
        return composedMessage;
    }

    public Object getObject() {
        return object;
    }
}
