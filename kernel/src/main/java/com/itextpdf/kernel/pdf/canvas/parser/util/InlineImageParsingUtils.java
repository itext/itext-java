/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf.canvas.parser.util;

import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.filters.DoNothingFilter;
import com.itextpdf.kernel.pdf.filters.FilterHandlers;
import com.itextpdf.kernel.pdf.filters.IFilterHandler;
import com.itextpdf.kernel.pdf.filters.FlateDecodeStrictFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods to help with processing of inline images
 */
public final class InlineImageParsingUtils {

    private static final byte[] EI = new byte[]{(byte)'E', (byte)'I'};

    private InlineImageParsingUtils() {
    }

    /**
     * Simple class in case users need to differentiate an exception from processing
     * inline images vs other exceptions
     */
    public static class InlineImageParseException extends PdfException {


        public InlineImageParseException(String message) {
            super(message);
        }
    }

    /**
     * Map between key abbreviations allowed in dictionary of inline images and their
     * equivalent image dictionary keys
     */
    private static final Map<PdfName, PdfName> inlineImageEntryAbbreviationMap;
    /**
     * Map between value abbreviations allowed in dictionary of inline images for COLORSPACE
     */
    private static final Map<PdfName, PdfName> inlineImageColorSpaceAbbreviationMap;
    /**
     * Map between value abbreviations allowed in dictionary of inline images for FILTER
     */
    private static final Map<PdfName, PdfName> inlineImageFilterAbbreviationMap;

    static {
        // Map between key abbreviations allowed in dictionary of inline images and their
        // equivalent image dictionary keys
        inlineImageEntryAbbreviationMap = new HashMap<>();

        // allowed entries - just pass these through
        inlineImageEntryAbbreviationMap.put(PdfName.BitsPerComponent, PdfName.BitsPerComponent);
        inlineImageEntryAbbreviationMap.put(PdfName.ColorSpace, PdfName.ColorSpace);
        inlineImageEntryAbbreviationMap.put(PdfName.Decode, PdfName.Decode);
        inlineImageEntryAbbreviationMap.put(PdfName.DecodeParms, PdfName.DecodeParms);
        inlineImageEntryAbbreviationMap.put(PdfName.Filter, PdfName.Filter);
        inlineImageEntryAbbreviationMap.put(PdfName.Height, PdfName.Height);
        inlineImageEntryAbbreviationMap.put(PdfName.ImageMask, PdfName.ImageMask);
        inlineImageEntryAbbreviationMap.put(PdfName.Intent, PdfName.Intent);
        inlineImageEntryAbbreviationMap.put(PdfName.Interpolate, PdfName.Interpolate);
        inlineImageEntryAbbreviationMap.put(PdfName.Width, PdfName.Width);

        // abbreviations - transform these to corresponding correct values
        inlineImageEntryAbbreviationMap.put(new PdfName("BPC"), PdfName.BitsPerComponent);
        inlineImageEntryAbbreviationMap.put(new PdfName("CS"), PdfName.ColorSpace);
        inlineImageEntryAbbreviationMap.put(new PdfName("D"), PdfName.Decode);
        inlineImageEntryAbbreviationMap.put(new PdfName("DP"), PdfName.DecodeParms);
        inlineImageEntryAbbreviationMap.put(new PdfName("F"), PdfName.Filter);
        inlineImageEntryAbbreviationMap.put(new PdfName("H"), PdfName.Height);
        inlineImageEntryAbbreviationMap.put(new PdfName("IM"), PdfName.ImageMask);
        inlineImageEntryAbbreviationMap.put(new PdfName("I"), PdfName.Interpolate);
        inlineImageEntryAbbreviationMap.put(new PdfName("W"), PdfName.Width);

        // Map between value abbreviations allowed in dictionary of inline images for COLORSPACE
        inlineImageColorSpaceAbbreviationMap = new HashMap<>();

        inlineImageColorSpaceAbbreviationMap.put(new PdfName("G"), PdfName.DeviceGray);
        inlineImageColorSpaceAbbreviationMap.put(new PdfName("RGB"), PdfName.DeviceRGB);
        inlineImageColorSpaceAbbreviationMap.put(new PdfName("CMYK"), PdfName.DeviceCMYK);
        inlineImageColorSpaceAbbreviationMap.put(new PdfName("I"), PdfName.Indexed);

        // Map between value abbreviations allowed in dictionary of inline images for FILTER
        inlineImageFilterAbbreviationMap = new HashMap<PdfName, PdfName>();

        inlineImageFilterAbbreviationMap.put(new PdfName("AHx"), PdfName.ASCIIHexDecode);
        inlineImageFilterAbbreviationMap.put(new PdfName("A85"), PdfName.ASCII85Decode);
        inlineImageFilterAbbreviationMap.put(new PdfName("LZW"), PdfName.LZWDecode);
        inlineImageFilterAbbreviationMap.put(new PdfName("Fl"), PdfName.FlateDecode);
        inlineImageFilterAbbreviationMap.put(new PdfName("RL"), PdfName.RunLengthDecode);
        inlineImageFilterAbbreviationMap.put(new PdfName("CCF"), PdfName.CCITTFaxDecode);
        inlineImageFilterAbbreviationMap.put(new PdfName("DCT"), PdfName.DCTDecode);
    }

    /**
     * Parses an inline image from the provided content parser.  The parser must be positioned immediately following the BI operator in the content stream.
     * The parser will be left with current position immediately following the EI operator that terminates the inline image
     *
     * @param ps            the content parser to use for reading the image.
     * @param colorSpaceDic a color space dictionary
     * @return the parsed image
     * @throws IOException               if anything goes wring with the parsing
     * @throws InlineImageParseException if parsing of the inline image failed due to issues specific to inline image processing
     */
    public static PdfStream parse(PdfCanvasParser ps, PdfDictionary colorSpaceDic) throws IOException {
        PdfDictionary inlineImageDict = parseDictionary(ps);
        byte[] samples = parseSamples(inlineImageDict, colorSpaceDic, ps);
        PdfStream inlineImageAsStreamObject = new PdfStream(samples);
        inlineImageAsStreamObject.putAll(inlineImageDict);
        return inlineImageAsStreamObject;
    }

    /**
     * @param colorSpaceName the name of the color space. If null, a bi-tonal (black and white) color space is assumed.
     * @return the components per pixel for the specified color space
     */
    static int getComponentsPerPixel(PdfName colorSpaceName, PdfDictionary colorSpaceDic) {
        if (colorSpaceName == null)
            return 1;
        if (colorSpaceName.equals(PdfName.DeviceGray))
            return 1;
        if (colorSpaceName.equals(PdfName.DeviceRGB))
            return 3;
        if (colorSpaceName.equals(PdfName.DeviceCMYK))
            return 4;

        if (colorSpaceDic != null) {
            PdfArray colorSpace = colorSpaceDic.getAsArray(colorSpaceName);
            if (colorSpace == null) {
                PdfName tempName = colorSpaceDic.getAsName(colorSpaceName);
                if (tempName != null) {
                    return getComponentsPerPixel(tempName, colorSpaceDic);
                }
            } else {
                if (PdfName.Indexed.equals(colorSpace.getAsName(0))) {
                    return 1;
                }
                if (PdfName.ICCBased.equals(colorSpace.getAsName(0))) {
                    return colorSpace.getAsStream(1).getAsNumber(PdfName.N).intValue();
                }
            }
        }

        throw new InlineImageParseException(KernelExceptionMessageConstant.UNEXPECTED_COLOR_SPACE).setMessageParams(colorSpaceName);
    }

    /**
     * Parses the next inline image dictionary from the parser.  The parser must be positioned immediately following the BI operator.
     * The parser will be left with position immediately following the whitespace character that follows the ID operator that ends the inline image dictionary.
     *
     * @param ps the parser to extract the embedded image information from
     * @return the dictionary for the inline image, with any abbreviations converted to regular image dictionary keys and values
     * @throws IOException if the parse fails
     */
    private static PdfDictionary parseDictionary(PdfCanvasParser ps) throws IOException {
        // by the time we get to here, we have already parsed the BI operator
        PdfDictionary dict = new PdfDictionary();

        for (PdfObject key = ps.readObject(); key != null && !"ID".equals(key.toString()); key = ps.readObject()) {
            PdfObject value = ps.readObject();
            PdfName resolvedKey = inlineImageEntryAbbreviationMap.get((PdfName) key);
            if (resolvedKey == null) {
                resolvedKey = (PdfName) key;
            }
            dict.put(resolvedKey, getAlternateValue(resolvedKey, value));
        }

        int ch = ps.getTokeniser().read();
        if (!PdfTokenizer.isWhitespace(ch))
            throw new InlineImageParseException(
                    KernelExceptionMessageConstant.UNEXPECTED_CHARACTER_FOUND_AFTER_ID_IN_INLINE_IMAGE
            ).setMessageParams(ch);

        return dict;
    }

    /**
     * Transforms value abbreviations into their corresponding real value
     *
     * @param key   the key that the value is for
     * @param value the value that might be an abbreviation
     * @return if value is an allowed abbreviation for the key, the expanded value for that abbreviation.  Otherwise, value is returned without modification
     */
    private static PdfObject getAlternateValue(PdfName key, PdfObject value) {
        if (key == PdfName.Filter) {
            if (value instanceof PdfName) {
                PdfName altValue = inlineImageFilterAbbreviationMap.get((PdfName) value);
                if (altValue != null) {
                    return altValue;
                }
            } else if (value instanceof PdfArray) {
                PdfArray array = ((PdfArray) value);
                PdfArray altArray = new PdfArray();
                int count = array.size();
                for (int i = 0; i < count; i++) {
                    altArray.add(getAlternateValue(key, array.get(i)));
                }
                return altArray;
            }
        } else if (key == PdfName.ColorSpace && value instanceof PdfName) {
            PdfName altValue = inlineImageColorSpaceAbbreviationMap.get((PdfName) value);
            if (altValue != null) {
                return altValue;
            }
        }
        return value;
    }

    /**
     * Computes the number of unfiltered bytes that each row of the image will contain.
     * If the number of bytes results in a partial terminating byte, this number is rounded up
     * per the PDF specification
     *
     * @param imageDictionary the dictionary of the inline image
     * @return the number of bytes per row of the image
     */
    private static int computeBytesPerRow(PdfDictionary imageDictionary, PdfDictionary colorSpaceDic) {
        PdfNumber wObj = imageDictionary.getAsNumber(PdfName.Width);
        PdfNumber bpcObj = imageDictionary.getAsNumber(PdfName.BitsPerComponent);
        int cpp = getComponentsPerPixel(imageDictionary.getAsName(PdfName.ColorSpace), colorSpaceDic);

        int w = wObj.intValue();
        int bpc = bpcObj != null ? bpcObj.intValue() : 1;

        return (w * bpc * cpp + 7) / 8;
    }

    /**
     * Parses the samples of the image from the underlying content parser, ignoring all filters.
     * The parser must be positioned immediately after the ID operator that ends the inline image's dictionary.
     * The parser will be left positioned immediately following the EI operator.
     * This is primarily useful if no filters have been applied.
     *
     * @param imageDictionary the dictionary of the inline image
     * @param ps              the content parser
     * @return the samples of the image
     * @throws IOException if anything bad happens during parsing
     */
    private static byte[] parseUnfilteredSamples(PdfDictionary imageDictionary, PdfDictionary colorSpaceDic, PdfCanvasParser ps) throws IOException {
        // special case:  when no filter is specified, we just read the number of bits
        // per component, multiplied by the width and height.
        if (imageDictionary.containsKey(PdfName.Filter))
            throw new IllegalArgumentException("Dictionary contains filters");

        PdfNumber h = imageDictionary.getAsNumber(PdfName.Height);

        int bytesToRead = computeBytesPerRow(imageDictionary, colorSpaceDic) * h.intValue();
        byte[] bytes = new byte[bytesToRead];
        PdfTokenizer tokeniser = ps.getTokeniser();

        // skip next character (which better be a whitespace character - I suppose we could check for this)
        int shouldBeWhiteSpace = tokeniser.read();
        // from the PDF spec:  Unless the image uses ASCIIHexDecode or ASCII85Decode as one of its filters, the ID operator shall be followed by a single white-space character, and the next character shall be interpreted as the first byte of image data.
        // unfortunately, we've seen some PDFs where there is no space following the ID, so we have to capture this case and handle it
        int startIndex = 0;
        if (!PdfTokenizer.isWhitespace(shouldBeWhiteSpace) || shouldBeWhiteSpace == 0) {
            // tokeniser treats 0 as whitespace, but for our purposes, we shouldn't
            bytes[0] = (byte) shouldBeWhiteSpace;
            startIndex++;
        }
        for (int i = startIndex; i < bytesToRead; i++) {
            int ch = tokeniser.read();
            if (ch == -1)
                throw new InlineImageParseException(
                        KernelExceptionMessageConstant.END_OF_CONTENT_STREAM_REACHED_BEFORE_END_OF_IMAGE_DATA);

            bytes[i] = (byte) ch;
        }
        PdfObject ei = ps.readObject();
        if (!"EI".equals(ei.toString())) {
            // Some PDF producers seem to add another non-whitespace character after the image data.
            // Let's try to handle that case here.
            PdfObject ei2 = ps.readObject();
            if (!"EI".equals(ei2.toString()))
                throw new InlineImageParseException(
                        KernelExceptionMessageConstant.OPERATOR_EI_NOT_FOUND_AFTER_END_OF_IMAGE_DATA);
        }
        return bytes;
    }

    /**
     * Parses the samples of the image from the underlying content parser, accounting for filters
     * The parser must be positioned immediately after the ID operator that ends the inline image's dictionary.
     * The parser will be left positioned immediately following the EI operator.
     * <b>Note:</b>This implementation does not actually apply the filters at this time
     *
     * @param imageDictionary the dictionary of the inline image
     * @param ps              the content parser
     * @return the samples of the image
     * @throws IOException if anything bad happens during parsing
     */
    private static byte[] parseSamples(PdfDictionary imageDictionary, PdfDictionary colorSpaceDic, PdfCanvasParser ps) throws IOException {
        // by the time we get to here, we have already parsed the ID operator

        if (!imageDictionary.containsKey(PdfName.Filter) && imageColorSpaceIsKnown(imageDictionary, colorSpaceDic)) {
            return parseUnfilteredSamples(imageDictionary, colorSpaceDic, ps);
        }


        // read all content until we reach an EI operator followed by whitespace.
        // then decode the content stream to check that bytes that were parsed are really all image bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int ch;
        int found = 0;
        PdfTokenizer tokeniser = ps.getTokeniser();
        while ((ch = tokeniser.read()) != -1) {
            if (ch == 'E') {
                // probably some bytes were preserved so write them
                baos.write(EI, 0, found);
                // just preserve 'E' and do not write it immediately
                found = 1;
            } else if (found == 1 && ch == 'I') {
                // just preserve 'EI' and do not write it immediately
                found = 2;
            } else {
                if (found == 2 && PdfTokenizer.isWhitespace(ch)) {
                    byte[] tmp = baos.toByteArray();
                    if (inlineImageStreamBytesAreComplete(tmp, imageDictionary)) {
                        return tmp;
                    }
                }
                // probably some bytes were preserved so write them
                baos.write(EI, 0, found);
                baos.write(ch);
                found = 0;
            }

        }
        throw new InlineImageParseException(KernelExceptionMessageConstant.CANNOT_FIND_IMAGE_DATA_OR_EI);
    }

    private static boolean imageColorSpaceIsKnown(PdfDictionary imageDictionary, PdfDictionary colorSpaceDic) {
        PdfName cs = imageDictionary.getAsName(PdfName.ColorSpace);
        if (cs == null || cs.equals(PdfName.DeviceGray) || cs.equals(PdfName.DeviceRGB) || cs.equals(PdfName.DeviceCMYK))
            return true;

        return colorSpaceDic != null && colorSpaceDic.containsKey(cs);
    }

    /**
     * This method acts like a check that bytes that were parsed are really all image bytes. If it's true,
     * then decoding will succeed, but if not all image bytes were read and "&lt;ws&gt;EI&lt;ws&gt;" bytes were just a part of the image,
     * then decoding should fail.
     * Not the best solution, but probably there is no better and more reliable way to check this.
     * <p>
     * Drawbacks: slow; images with DCTDecode, JBIG2Decode and JPXDecode filters couldn't be checked as iText doesn't
     * support these filters; what if decoding will succeed eventhough it's not all bytes?; also I'm not sure that all
     * filters throw an exception in case data is corrupted (For example, FlateDecodeFilter seems not to throw an exception).
     **/
    private static boolean inlineImageStreamBytesAreComplete(byte[] samples, PdfDictionary imageDictionary) {
        try {
            Map<PdfName, IFilterHandler> filters = new HashMap<>(FilterHandlers.getDefaultFilterHandlers());
            filters.put(PdfName.JBIG2Decode, new DoNothingFilter());
            filters.put(PdfName.FlateDecode, new FlateDecodeStrictFilter());
            PdfReader.decodeBytes(samples, imageDictionary, filters);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
