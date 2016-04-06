/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.io;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IOException extends RuntimeException {

    public static final String _1BitSamplesAreNotSupportedForHorizontalDifferencingPredictor = "{0} bit.samples.are.not.supported.for.horizontal.differencing.predictor";
    public static final String _1CorruptedJfifMarker = "{0} corrupted.jfif.marker";
    public static final String _1IsNotAValidJpegFile = "{0} is.not.a.valid.jpeg.file";
    public static final String _1MustHave8BitsPerComponent = "{0} must.have.8.bits.per.component";
    public static final String _1UnsupportedJpegMarker2 = "{0} unsupported.jpeg.marker {1}";
    public static final String _1IsNotAnAfmOrPfmFontFile = "{0} is.not.an.afm.or.pfm.font.file";
    public static final String _1NotFoundAsFileOrResource = "{0} not found as file or resource.";

    public static final String AllFillBitsPrecedingEolCodeMustBe0 = "all.fill.bits.preceding.eol.code.must.be.0";
    public static final String BadEndiannessTagNot0x4949Or0x4d4d = "bad.endianness.tag.not.0x4949.or.0x4d4d";
    public static final String BadMagicNumberShouldBe42 = "bad.magic.number.should.be.42";
    public static final String BitsPerComponentMustBe1_2_4or8 = "bits.per.component.must.be.1.2.4.or.8";
    public static final String BitsPerSample1IsNotSupported = "bits.per.sample {0} is.not.supported";
    public static final String BmpImageException = "bmp.image.exception";
    public static final String BytesCanBeAssignedToByteArrayOutputStreamOnly = "bytes.can.be.assigned.to.bytearrayoutputstream.only";
    public static final String BytesCanBeResetInByteArrayOutputStreamOnly = "bytes.can.be.reset.in.bytearrayoutputstream.only";

    public static final String CannotGetTiffImageColor = "cannot.get.tiff.image.color";
    public static final String CannotFind1Frame = "cannot.find {0} frame";
    public static final String CannotHandleBoxSizesHigherThan2_32 = "cannot.handle.box.sizes.higher.than.2.32";
    public static final String CannotInflateTiffImage = "cannot.inflate.tiff.image";
    public static final String CannotReadTiffImage = "cannot.read.tiff.image";
    public static final String CannotWriteByte = "cannot.write.byte";
    public static final String CannotWriteBytes = "cannot.write.bytes";
    public static final String CannotWriteFloatNumber = "cannot.write.float.number";
    public static final String CannotWriteIntNumber = "cannot.write.int.number";
    public static final String CcittCompressionTypeMustBeCcittg4Ccittg3_1dOrCcittg3_2d = "ccitt.compression.type.must.be.ccittg4.ccittg3.1d.or.ccittg3.2d";
    public static final String ComponentsMustBe1_3Or4 = "components.must.be.1.3.or.4";
    public static final String Compression1IsNotSupported = "compression {0} is.not.supported";
    public static final String ColorDepthIsNotSupported = "the.color.depth {0} is.not.supported";
    public static final String ColorSpaceIsNotSupported = "the.color.space {0} is.not.supported";
    public static final String CompressionJpegIsOnlySupportedWithASingleStripThisImageHas1Strips = "compression.jpeg.is.only.supported.with.a.single.strip.this.image.has {0} strips";
    public static final String DirectoryNumberTooLarge = "directory.number.too.large";
    public static final String EolCodeWordEncounteredInBlackRun = "eol.code.word.encountered.in.black.run";
    public static final String EolCodeWordEncounteredInWhiteRun = "eol.code.word.encountered.in.white.run";
    public static final String ErrorAtFilePointer1 = "error.at.file.pointer {0}";
    public static final String ErrorReadingString = "error.reading.string";
    public static final String ErrorWithJpMarker = "error.with.jp.marker";

    public static final String ExpectedFtypMarker = "expected.ftyp.marker";
    public static final String ExpectedIhdrMarker = "expected.ihdr.marker";
    public static final String ExpectedJpMarker = "expected.jp.marker";
    public static final String ExpectedJp2hMarker = "expected.jp2h.marker";

    public static final String ExtraSamplesAreNotSupported = "extra.samples.are.not.supported";
    public static final String FdfStartxrefNotFound = "fdf.startxref.not.found";

    public static final String FirstScanlineMustBe1dEncoded = "first.scanline.must.be.1d.encoded";
    public static final String FontFile1NotFound = "font.file {0} not.found";
    public static final String ImageFormatCannotBeRecognized = "image.format.cannot.be.recognized";
    public static final String GifImageException = "gif.image.exception";
    public static final String GtNotExpected = "gt.not.expected";
    public static final String GifSignatureNotFound = "gif.signature.not.found";
    public static final String IllegalValueForPredictorInTiffFile = "illegal.value.for.predictor.in.tiff.file";
    public static final String Font1IsNotRecognized = "font {0} is.not.recognized";
    public static final String FontIsNotRecognized = "font.is.not.recognized";

    public static final String ImageCanNotBeAnImageMask = "image.can.not.be.an.image.mask";
    public static final String ImageMaskCannotContainAnotherImageMask = "image.mask.cannot.contain.another.image.mask";
    public static final String ImageMaskIsNotAMaskDidYouDoMakeMask = "image.mask.is.not.a.mask.did.you.do.makemask";
    public static final String IncompletePalette = "incomplete.palette";

    public static final String InvalidTTCFile = "{0} is.not.a.valid.ttc.file";
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
    public static final String NValueIsNotSupported = "N.value.1.is.not.supported";
    public static final String PageNumberMustBeGtEq1 = "page.number.must.be.gt.eq {0}";
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
    public static final String UnknownCompressionType1 = "unknown.compression.type {0}";
    public static final String UnsupportedBoxSizeEqEq0 = "unsupported.box.size.eq.eq.0";
    public static final String WrongNumberOfComponentsInIccProfile = "icc.profile.contains {0} components.the.image.data.contains {2} components";

    protected Object obj;
    private List<Object> messageParams;

    public IOException(String message) {
        super(message);
    }

    public IOException(Throwable cause) {
        super(cause);
    }

    public IOException(String message, Object obj) {
        this(message);
        this.obj = obj;
    }

    public IOException(String message, Throwable cause) {
        super(message, cause);
    }

    public IOException(String message, Throwable cause, Object obj) {
        this(message, cause);
        this.obj = obj;
    }

    @Override
    public String getMessage() {
        if (messageParams == null || messageParams.isEmpty()) {
            return super.getMessage();
        } else {
            return MessageFormat.format(super.getMessage(), messageParams.toArray());
        }
    }

    public IOException setMessageParams(Object... messageParams) {
        this.messageParams = new ArrayList<>();
        Collections.addAll(this.messageParams, messageParams);
        return this;
    }
}
