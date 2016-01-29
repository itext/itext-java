package com.itextpdf.basics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IOException extends RuntimeException {

    public static final String _1BitSamplesAreNotSupportedForHorizontalDifferencingPredictor = "1.bit.samples.are.not.supported.for.horizontal.differencing.predictor";
    public static final String _1CorruptedJfifMarker = "1.corrupted.jfif.marker";
    public static final String _1IsNotAValidJpegFile = "1.is.not.a.valid.jpeg.file";
    public static final String _1MustHave8BitsPerComponent = "1.must.have.8.bits.per.component";
    public static final String _1UnsupportedJpegMarker2 = "1.unsupported.jpeg.marker.2";
    public static final String _1IsNotAnAFMorPfmFontFile = "1.is.not.an.afm.or.pfm.font.file";

    public static final String AllFillBitsPrecedingEolCodeMustBe0 = "all.fill.bits.preceding.eol.code.must.be.0";
    public static final String BadEndiannessTagNot0x4949Or0x4d4d = "bad.endianness.tag.not.0x4949.or.0x4d4d";
    public static final String BadMagicNumberShouldBe42 = "bad.magic.number.should.be.42";
    public static final String BitsPerComponentMustBe1_2_4or8 = "bits.per.component.must.be.1.2.4.or.8";
    public static final String BitsPerSample1IsNotSupported = "bits.per.sample.1.is.not.supported";
    public static final String BmpImageException = "bmp.image.exception";
    public static final String BytesCanBeAssignedToByteArrayOutputStreamOnly = "bytes.can.be.assigned.to.bytearrayoutputstream.only";
    public static final String BytesCanBeResetInByteArrayOutputStreamOnly = "bytes.can.be.reset.in.bytearrayoutputstream.only";

    public static final String CannotGetTiffImageColor = "cannot.get.tiff.image.color";
    public static final String CannotFind1Frame = "cannot.find.1.frame";
    public static final String CannotHandleBoxSizesHigherThan2_32 = "cannot.handle.box.sizes.higher.than.2.32";
    public static final String CannotInflateTiffImage = "cannot.inflate.tiff.image";
    public static final String CannotReadTiffImage = "cannot.read.tiff.image";
    public static final String CannotWriteByte = "cannot.write.byte";
    public static final String CannotWriteBytes = "cannot.write.bytes";
    public static final String CannotWriteFloatNumber = "cannot.write.float.number";
    public static final String CannotWriteIntNumber = "cannot.write.int.number";
    public static final String CcittCompressionTypeMustBeCcittg4Ccittg3_1dOrCcittg3_2d = "ccitt.compression.type.must.be.ccittg4.ccittg3.1d.or.ccittg3.2d";
    public static final String ComponentsMustBe1_3Or4 = "components.must.be.1.3.or.4";
    public static final String Compression1IsNotSupported = "compression.1.is.not.supported";
    public static final String CompressionJpegIsOnlySupportedWithASingleStripThisImageHas1Strips = "compression.jpeg.is.only.supported.with.a.single.strip.this.image.has.1.strips";
    public static final String DirectoryNumberTooLarge = "directory.number.too.large";
    public static final String EolCodeWordEncounteredInBlackRun = "eol.code.word.encountered.in.black.run";
    public static final String EolCodeWordEncounteredInWhiteRun = "eol.code.word.encountered.in.white.run";
    public static final String ErrorAtFilePointer1 = "error.at.file.pointer.1";
    public static final String ErrorReadingString = "error.reading.string";
    public static final String ErrorWithJpMarker = "error.with.jp.marker";

    public static final String ExpectedFtypMarker = "expected.ftyp.marker";
    public static final String ExpectedIhdrMarker = "expected.ihdr.marker";
    public static final String ExpectedJpMarker = "expected.jp.marker";
    public static final String ExpectedJp2hMarker = "expected.jp2h.marker";

    public static final String ExtraSamplesAreNotSupported = "extra.samples.are.not.supported";
    public static final String FdfStartxrefNotFound = "fdf.startxref.not.found";

    public static final String FirstScanlineMustBe1dEncoded = "first.scanline.must.be.1d.encoded";
    // TODO parametrized message
    public static final String FontFileNotFound = "font.file.not.found";
    public static final String ImageFormatCannotBeRecognized = "image.format.cannot.be.recognized";
    public static final String GifImageException = "gif.image.exception";
    public static final String GtNotExpected = "gt.not.expected";
    public static final String GifSignatureNotFound = "gif.signature.not.found";
    public static final String IllegalValueForPredictorInTiffFile = "illegal.value.for.predictor.in.tiff.file";
    public static final String Font1IsNotRecognized = "font.1.is.not.recognized";
    public static final String FontIsNotRecognized = "font.is.not.recognized";

    public static final String ImageCanNotBeAnImageMask = "image.can.not.be.an.image.mask";
    public static final String ImageMaskCannotContainAnotherImageMask = "image.mask.cannot.contain.another.image.mask";
    public static final String ImageMaskIsNotAMaskDidYouDoMakeMask = "image.mask.is.not.a.mask.did.you.do.makemask";
    public static final String IncompletePalette = "incomplete.palette";

    public static final String InvalidTTCFile = "1.is.not.a.valid.ttc.file";
    public static final String InvalidBmpFileCompression = "invalid.bmp.file.compression";
    public static final String InvalidCodeEncountered = "invalid.code.encountered";
    public static final String InvalidCodeEncounteredWhileDecoding2dGroup3CompressedData = "invalid.code.encountered.while.decoding.2d.group.3.compressed.data";
    public static final String InvalidCodeEncounteredWhileDecoding2dGroup4CompressedData = "invalid.code.encountered.while.decoding.2d.group.4.compressed.data";
    public static final String InvalidIccProfile = "invalid.icc.profile";
    public static final String InvalidJpeg2000File = "invalid.jpeg2000.file";
    public static final String InvalidMagicValueForBmpFile = "invalid.magic.value.for.bmp.file";
    public static final String IoException = "io.exception";
    public static final String Jbig2ImageException = "jbig2.image.exception";
    public static final String JpegImageException = "jpeg.image.exception";
    public static final String Jpeg2000ImageException = "jpeg2000.image.exception";
    public static final String MissingTagSForOjpegCompression = "missing.tag.s.for.ojpeg.compression";
    public static final String PageNumberMustBeGtEq1 = "page.number.must.be.gt.eq.1";
    public static final String PdfEncodings = "pdf.encodings";
    public static final String PdfHeaderNotFound = "pdf.header.not.found";
    public static final String PdfStartxrefNotFound = "pdf.startxref.not.found";
    public static final String Photometric1IsNotSupported = "photometric.1.is.not.supported";
    public static final String PlanarImagesAreNotSupported = "planar.images.are.not.supported";
    public static final String PngFilterUnknown = "png.filter.unknown";
    public static final String PngImageException = "png.image.exception";
    public static final String PrematureEofWhileReadingJpg = "premature.eof.while.reading.jpg";
    public static final String ScanlineMustBeginWithEolCodeWord = "scanline.must.begin.with.eol.code.word";
    public static final String Tiff50StyleLzwCodesAreNotSupported = "tiff.5.0.style.lzw.codes.are.not.supported";
    public static final String TiffFillOrderTagMustBeEither1Or2 = "tiff.fill.order.tag.must.be.either.1.or.2";
    public static final String TiffImageException = "tiff.image.exception";
    public static final String TTCIndexDoesNotExistInFile = "ttc.index.doesn't.exist.in.ttc.file";
    public static final String TilesAreNotSupported = "tiles.are.not.supported";
    public static final String TransparencyLengthMustBeEqualTo2WithCcittImages = "transparency.length.must.be.equal.to.2.with.ccitt.images";
    public static final String UnexpectedCloseBracket = "unexpected.close.bracket";
    public static final String UnexpectedGtGt = "unexpected.gt.gt";
    public static final String UnknownCompressionType1 = "unknown.compression.type.1";
    public static final String UnsupportedBoxSizeEqEq0 = "unsupported.box.size.eq.eq.0";
    public static final String WrongNumberOfComponentsInIccProfile = "icc.profile.contains.1.components.the.image.data.contains.2.components";

    protected Object object;
    private List<Object> messageParams;

    private final String ERROR_MESSAGE_LOCALE_PATH = "com.itextpdf.basics.l10n.error.exception_messages";

    public IOException(String message) {
        super(message);
    }

    public IOException(Throwable cause) {
        super(cause);
    }

    public IOException(String message, Object object) {
        this(message);
        this.object = object;
    }

    public IOException(String message, Throwable cause) {
        super(message, cause);
    }

    public IOException(String message, Throwable cause, Object object) {
        this(message, cause);
        this.object = object;
    }

    @Override
    public String getMessage() {
        return ResourceLocaleBundle.getMessage(ERROR_MESSAGE_LOCALE_PATH, super.getMessage(), messageParams);
    }

    public IOException setMessageParams(Object... messageParams) {
        this.messageParams = new ArrayList<>();
        Collections.addAll(this.messageParams, messageParams);
        return this;
    }
}
