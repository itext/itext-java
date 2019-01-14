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
package com.itextpdf.barcodes.qrcode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author dswitkin@google.com (Daniel Switkin) - ported from C++
 */
final class Encoder {

    // The original table is defined in the table 5 of JISX0510:2004 (p.19).
    private static final int[] ALPHANUMERIC_TABLE = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 0x00-0x0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 0x10-0x1f
            36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43,  // 0x20-0x2f
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 44, -1, -1, -1, -1, -1,  // 0x30-0x3f
            -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,  // 0x40-0x4f
            25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1,  // 0x50-0x5f
    };

    static final String DEFAULT_BYTE_MODE_ENCODING = "ISO-8859-1";

    private Encoder() {
    }

    // The mask penalty calculation is complicated.  See Table 21 of JISX0510:2004 (p.45) for details.
    // Basically it applies four rules and summate all penalties.
    private static int calculateMaskPenalty(ByteMatrix matrix) {
        int penalty = 0;
        penalty += MaskUtil.applyMaskPenaltyRule1(matrix);
        penalty += MaskUtil.applyMaskPenaltyRule2(matrix);
        penalty += MaskUtil.applyMaskPenaltyRule3(matrix);
        penalty += MaskUtil.applyMaskPenaltyRule4(matrix);
        return penalty;
    }

    /**
     * Encode "bytes" with the error correction level "ecLevel". The encoding mode will be chosen
     * internally by chooseMode(). On success, store the result in "qrCode".
     * <p>
     * We recommend you to use QRCode.EC_LEVEL_L (the lowest level) for
     * "getECLevel" since our primary use is to show QR code on desktop screens. We don't need very
     * strong error correction for this purpose.
     * <p>
     * Note that there is no way to encode bytes in MODE_KANJI. We might want to add EncodeWithMode()
     * with which clients can specify the encoding mode. For now, we don't need the functionality.
     *
     * @param content String to encode
     * @param ecLevel Error-correction level to use
     * @param qrCode QR code to store the result in
     * @throws WriterException
     */
    public static void encode(String content, ErrorCorrectionLevel ecLevel, QRCode qrCode)
            throws WriterException {
        encode(content, ecLevel, null, qrCode);
    }

    /**
     * Encode "bytes" with the error correction level "ecLevel". The encoding mode will be chosen
     * internally by chooseMode(). On success, store the result in "qrCode".
     * <p>
     * We recommend you to use QRCode.EC_LEVEL_L (the lowest level) for
     * "getECLevel" since our primary use is to show QR code on desktop screens. We don't need very
     * strong error correction for this purpose.
     * <p>
     * Note that there is no way to encode bytes in MODE_KANJI. We might want to add EncodeWithMode()
     * with which clients can specify the encoding mode. For now, we don't need the functionality.
     *
     * @param content String to encode
     * @param ecLevel Error-correction level to use
     * @param hints   Optional Map containing  encoding and suggested minimum version to use
     * @param qrCode QR code to store the result in
     * @throws WriterException
     */
    public static void encode(String content, ErrorCorrectionLevel ecLevel, Map<EncodeHintType, Object> hints,
                              QRCode qrCode) throws WriterException {

        String encoding = hints == null ? null : (String) hints.get(EncodeHintType.CHARACTER_SET);
        if (encoding == null) {
            encoding = DEFAULT_BYTE_MODE_ENCODING;
        }
        int desiredMinVersion = (hints == null || hints.get(EncodeHintType.MIN_VERSION_NR) == null) ? 1 : (int) hints.get(EncodeHintType.MIN_VERSION_NR);
        //Check if desired level is within bounds of [1,40]
        if (desiredMinVersion < 1) desiredMinVersion = 1;
        if (desiredMinVersion > 40) desiredMinVersion = 40;
        // Step 1: Choose the mode (encoding).
        Mode mode = chooseMode(content, encoding);

        // Step 2: Append "bytes" into "dataBits" in appropriate encoding.
        BitVector dataBits = new BitVector();
        appendBytes(content, mode, dataBits, encoding);
        // Step 3: Initialize QR code that can contain "dataBits".
        int numInputBytes = dataBits.sizeInBytes();
        initQRCode(numInputBytes, ecLevel, desiredMinVersion, mode, qrCode);

        // Step 4: Build another bit vector that contains header and data.
        BitVector headerAndDataBits = new BitVector();

        // Step 4.5: Append ECI message if applicable
        if (mode == Mode.BYTE && !DEFAULT_BYTE_MODE_ENCODING.equals(encoding)) {
            CharacterSetECI eci = CharacterSetECI.getCharacterSetECIByName(encoding);
            if (eci != null) {
                appendECI(eci, headerAndDataBits);
            }
        }

        appendModeInfo(mode, headerAndDataBits);

        int numLetters = mode.equals(Mode.BYTE) ? dataBits.sizeInBytes() : content.length();
        appendLengthInfo(numLetters, qrCode.getVersion(), mode, headerAndDataBits);
        headerAndDataBits.appendBitVector(dataBits);

        // Step 5: Terminate the bits properly.
        terminateBits(qrCode.getNumDataBytes(), headerAndDataBits);

        // Step 6: Interleave data bits with error correction code.
        BitVector finalBits = new BitVector();
        interleaveWithECBytes(headerAndDataBits, qrCode.getNumTotalBytes(), qrCode.getNumDataBytes(),
                qrCode.getNumRSBlocks(), finalBits);

        // Step 7: Choose the mask pattern and set to "qrCode".
        ByteMatrix matrix = new ByteMatrix(qrCode.getMatrixWidth(), qrCode.getMatrixWidth());
        qrCode.setMaskPattern(chooseMaskPattern(finalBits, qrCode.getECLevel(), qrCode.getVersion(),
                matrix));

        // Step 8.  Build the matrix and set it to "qrCode".
        MatrixUtil.buildMatrix(finalBits, qrCode.getECLevel(), qrCode.getVersion(),
                qrCode.getMaskPattern(), matrix);
        qrCode.setMatrix(matrix);
        // Step 9.  Make sure we have a valid QR Code.
        if (!qrCode.isValid()) {
            throw new WriterException("Invalid QR code: " + qrCode.toString());
        }
    }

    /**
     * @return the code point of the table used in alphanumeric mode or
     * -1 if there is no corresponding code in the table.
     */
    static int getAlphanumericCode(int code) {
        if (code < ALPHANUMERIC_TABLE.length) {
            return ALPHANUMERIC_TABLE[code];
        }
        return -1;
    }

    /**
     * Choose the best mode by examining the content.
     *
     * @param content content to examine
     * @return mode to use
     */
    public static Mode chooseMode(String content) {
        return chooseMode(content, null);
    }

    /**
     * Choose the best mode by examining the content. Note that 'encoding' is used as a hint;
     * if it is Shift_JIS, and the input is only double-byte Kanji, then we return {@link Mode#KANJI}
     *
     * @param content  content to examine
     * @param encoding hint for the encoding to use
     * @return mode to use
     */
    public static Mode chooseMode(String content, String encoding) {
        if ("Shift_JIS".equals(encoding)) {
            // Choose Kanji mode if all input are double-byte characters
            return isOnlyDoubleByteKanji(content) ? Mode.KANJI : Mode.BYTE;
        }
        boolean hasNumeric = false;
        boolean hasAlphanumeric = false;
        for (int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);
            if (c >= '0' && c <= '9') {
                hasNumeric = true;
            } else if (getAlphanumericCode(c) != -1) {
                hasAlphanumeric = true;
            } else {
                return Mode.BYTE;
            }
        }
        if (hasAlphanumeric) {
            return Mode.ALPHANUMERIC;
        } else if (hasNumeric) {
            return Mode.NUMERIC;
        }
        return Mode.BYTE;
    }

    private static boolean isOnlyDoubleByteKanji(String content) {
        byte[] bytes;
        try {
            bytes = content.getBytes("Shift_JIS");
        } catch (UnsupportedEncodingException uee) {
            return false;
        }
        int length = bytes.length;
        if (length % 2 != 0) {
            return false;
        }
        for (int i = 0; i < length; i += 2) {
            int byte1 = bytes[i] & 0xFF;
            if ((byte1 < 0x81 || byte1 > 0x9F) && (byte1 < 0xE0 || byte1 > 0xEB)) {
                return false;
            }
        }
        return true;
    }

    private static int chooseMaskPattern(BitVector bits, ErrorCorrectionLevel ecLevel, int version,
                                         ByteMatrix matrix) throws WriterException {

        int minPenalty = Integer.MAX_VALUE;  // Lower penalty is better.
        int bestMaskPattern = -1;
        // We try all mask patterns to choose the best one.
        for (int maskPattern = 0; maskPattern < QRCode.NUM_MASK_PATTERNS; maskPattern++) {
            MatrixUtil.buildMatrix(bits, ecLevel, version, maskPattern, matrix);
            int penalty = calculateMaskPenalty(matrix);
            if (penalty < minPenalty) {
                minPenalty = penalty;
                bestMaskPattern = maskPattern;
            }
        }
        return bestMaskPattern;
    }

    /**
     * Initialize "qrCode" according to "numInputBytes", "ecLevel", and "mode". On success,
     * modify "qrCode".
     */
    private static void initQRCode(int numInputBytes, ErrorCorrectionLevel ecLevel, int desiredMinVersion, Mode mode,
                                   QRCode qrCode) throws WriterException {
        qrCode.setECLevel(ecLevel);
        qrCode.setMode(mode);

        // In the following comments, we use numbers of Version 7-H.
        for (int versionNum = desiredMinVersion; versionNum <= 40; versionNum++) {
            Version version = Version.getVersionForNumber(versionNum);
            // numBytes = 196
            int numBytes = version.getTotalCodewords();
            // getNumECBytes = 130
            Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
            int numEcBytes = ecBlocks.getTotalECCodewords();
            // getNumRSBlocks = 5
            int numRSBlocks = ecBlocks.getNumBlocks();
            // getNumDataBytes = 196 - 130 = 66
            int numDataBytes = numBytes - numEcBytes;
            // We want to choose the smallest version which can contain data of "numInputBytes" + some
            // extra bits for the header (mode info and length info). The header can be three bytes
            // (precisely 4 + 16 bits) at most. Hence we do +3 here.
            if (numDataBytes >= numInputBytes + 3) {
                // Yay, we found the proper rs block info!
                qrCode.setVersion(versionNum);
                qrCode.setNumTotalBytes(numBytes);
                qrCode.setNumDataBytes(numDataBytes);
                qrCode.setNumRSBlocks(numRSBlocks);
                // getNumECBytes = 196 - 66 = 130
                qrCode.setNumECBytes(numEcBytes);
                // matrix width = 21 + 6 * 4 = 45
                qrCode.setMatrixWidth(version.getDimensionForVersion());
                return;
            }
        }
        throw new WriterException("Cannot find proper rs block info (input data too big?)");
    }

    /**
     * Terminate bits as described in 8.4.8 and 8.4.9 of JISX0510:2004 (p.24).
     */
    static void terminateBits(int numDataBytes, BitVector bits) throws WriterException {
        int capacity = numDataBytes << 3;
        if (bits.size() > capacity) {
            throw new WriterException("data bits cannot fit in the QR Code" + bits.size() + " > " +
                    capacity);
        }
        // Append termination bits. See 8.4.8 of JISX0510:2004 (p.24) for details.
        for (int i = 0; i < 4 && bits.size() < capacity; ++i) {
            bits.appendBit(0);
        }
        int numBitsInLastByte = bits.size() % 8;
        // If the last byte isn't 8-bit aligned, we'll add padding bits.
        if (numBitsInLastByte > 0) {
            int numPaddingBits = 8 - numBitsInLastByte;
            for (int i = 0; i < numPaddingBits; ++i) {
                bits.appendBit(0);
            }
        }
        // Should be 8-bit aligned here.
        if (bits.size() % 8 != 0) {
            throw new WriterException("Number of bits is not a multiple of 8");
        }
        // If we have more space, we'll fill the space with padding patterns defined in 8.4.9 (p.24).
        int numPaddingBytes = numDataBytes - bits.sizeInBytes();
        for (int i = 0; i < numPaddingBytes; ++i) {
            if (i % 2 == 0) {
                bits.appendBits(0xec, 8);
            } else {
                bits.appendBits(0x11, 8);
            }
        }
        if (bits.size() != capacity) {
            throw new WriterException("Bits size does not equal capacity");
        }
    }

    /**
     * Get number of data bytes and number of error correction bytes for block id "blockID". Store
     * the result in "numDataBytesInBlock", and "numECBytesInBlock". See table 12 in 8.5.1 of
     * JISX0510:2004 (p.30)
     */
    static void getNumDataBytesAndNumECBytesForBlockID(int numTotalBytes, int numDataBytes,
                                                       int numRSBlocks, int blockID, int[] numDataBytesInBlock,
                                                       int[] numECBytesInBlock) throws WriterException {
        if (blockID >= numRSBlocks) {
            throw new WriterException("Block ID too large");
        }
        // numRsBlocksInGroup2 = 196 % 5 = 1
        int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
        // numRsBlocksInGroup1 = 5 - 1 = 4
        int numRsBlocksInGroup1 = numRSBlocks - numRsBlocksInGroup2;
        // numTotalBytesInGroup1 = 196 / 5 = 39
        int numTotalBytesInGroup1 = numTotalBytes / numRSBlocks;
        // numTotalBytesInGroup2 = 39 + 1 = 40
        int numTotalBytesInGroup2 = numTotalBytesInGroup1 + 1;
        // numDataBytesInGroup1 = 66 / 5 = 13
        int numDataBytesInGroup1 = numDataBytes / numRSBlocks;
        // numDataBytesInGroup2 = 13 + 1 = 14
        int numDataBytesInGroup2 = numDataBytesInGroup1 + 1;
        // numEcBytesInGroup1 = 39 - 13 = 26
        int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
        // numEcBytesInGroup2 = 40 - 14 = 26
        int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
        // Sanity checks.
        // 26 = 26
        if (numEcBytesInGroup1 != numEcBytesInGroup2) {
            throw new WriterException("EC bytes mismatch");
        }
        // 5 = 4 + 1.
        if (numRSBlocks != numRsBlocksInGroup1 + numRsBlocksInGroup2) {
            throw new WriterException("RS blocks mismatch");
        }
        // 196 = (13 + 26) * 4 + (14 + 26) * 1
        if (numTotalBytes !=
                ((numDataBytesInGroup1 + numEcBytesInGroup1) *
                        numRsBlocksInGroup1) +
                        ((numDataBytesInGroup2 + numEcBytesInGroup2) *
                                numRsBlocksInGroup2)) {
            throw new WriterException("Total bytes mismatch");
        }

        if (blockID < numRsBlocksInGroup1) {
            numDataBytesInBlock[0] = numDataBytesInGroup1;
            numECBytesInBlock[0] = numEcBytesInGroup1;
        } else {
            numDataBytesInBlock[0] = numDataBytesInGroup2;
            numECBytesInBlock[0] = numEcBytesInGroup2;
        }
    }

    /**
     * Interleave "bits" with corresponding error correction bytes. On success, store the result in
     * "result". The interleave rule is complicated. See 8.6 of JISX0510:2004 (p.37) for details.
     */
    static void interleaveWithECBytes(BitVector bits, int numTotalBytes,
                                      int numDataBytes, int numRSBlocks, BitVector result) throws WriterException {

        // "bits" must have "getNumDataBytes" bytes of data.
        if (bits.sizeInBytes() != numDataBytes) {
            throw new WriterException("Number of bits and data bytes does not match");
        }

        // Step 1.  Divide data bytes into blocks and generate error correction bytes for them. We'll
        // store the divided data bytes blocks and error correction bytes blocks into "blocks".
        int dataBytesOffset = 0;
        int maxNumDataBytes = 0;
        int maxNumEcBytes = 0;

        // Since, we know the number of reedsolmon blocks, we can initialize the vector with the number.
        List<BlockPair> blocks = new ArrayList<>(numRSBlocks);

        for (int i = 0; i < numRSBlocks; ++i) {
            int[] numDataBytesInBlock = new int[1];
            int[] numEcBytesInBlock = new int[1];
            getNumDataBytesAndNumECBytesForBlockID(
                    numTotalBytes, numDataBytes, numRSBlocks, i,
                    numDataBytesInBlock, numEcBytesInBlock);

            ByteArray dataBytes = new ByteArray();
            dataBytes.set(bits.getArray(), dataBytesOffset, numDataBytesInBlock[0]);
            ByteArray ecBytes = generateECBytes(dataBytes, numEcBytesInBlock[0]);
            blocks.add(new BlockPair(dataBytes, ecBytes));

            maxNumDataBytes = Math.max(maxNumDataBytes, dataBytes.size());
            maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes.size());
            dataBytesOffset += numDataBytesInBlock[0];
        }
        if (numDataBytes != dataBytesOffset) {
            throw new WriterException("Data bytes does not match offset");
        }

        // First, place data blocks.
        for (int i = 0; i < maxNumDataBytes; ++i) {
            for (int j = 0; j < blocks.size(); ++j) {
                ByteArray dataBytes = blocks.get(j).getDataBytes();
                if (i < dataBytes.size()) {
                    result.appendBits(dataBytes.at(i), 8);
                }
            }
        }
        // Then, place error correction blocks.
        for (int i = 0; i < maxNumEcBytes; ++i) {
            for (int j = 0; j < blocks.size(); ++j) {
                ByteArray ecBytes = blocks.get(j).getErrorCorrectionBytes();
                if (i < ecBytes.size()) {
                    result.appendBits(ecBytes.at(i), 8);
                }
            }
        }
        if (numTotalBytes != result.sizeInBytes()) {  // Should be same.
            throw new WriterException("Interleaving error: " + numTotalBytes + " and " +
                    result.sizeInBytes() + " differ.");
        }
    }

    static ByteArray generateECBytes(ByteArray dataBytes, int numEcBytesInBlock) {
        int numDataBytes = dataBytes.size();
        int[] toEncode = new int[numDataBytes + numEcBytesInBlock];
        for (int i = 0; i < numDataBytes; i++) {
            toEncode[i] = dataBytes.at(i);
        }
        new ReedSolomonEncoder(GF256.QR_CODE_FIELD).encode(toEncode, numEcBytesInBlock);

        ByteArray ecBytes = new ByteArray(numEcBytesInBlock);
        for (int i = 0; i < numEcBytesInBlock; i++) {
            ecBytes.set(i, toEncode[numDataBytes + i]);
        }
        return ecBytes;
    }

    /**
     * Append mode info. On success, store the result in "bits".
     */
    static void appendModeInfo(Mode mode, BitVector bits) {
        bits.appendBits(mode.getBits(), 4);
    }


    /**
     * Append length info. On success, store the result in "bits".
     */
    static void appendLengthInfo(int numLetters, int version, Mode mode, BitVector bits)
            throws WriterException {
        int numBits = mode.getCharacterCountBits(Version.getVersionForNumber(version));
        if (numLetters > ((1 << numBits) - 1)) {
            throw new WriterException(numLetters + "is bigger than" + ((1 << numBits) - 1));
        }
        bits.appendBits(numLetters, numBits);
    }

    /**
     * Append "bytes" in "mode" mode (encoding) into "bits". On success, store the result in "bits".
     */
    static void appendBytes(String content, Mode mode, BitVector bits, String encoding)
            throws WriterException {
        if (mode.equals(Mode.NUMERIC)) {
            appendNumericBytes(content, bits);
        } else if (mode.equals(Mode.ALPHANUMERIC)) {
            appendAlphanumericBytes(content, bits);
        } else if (mode.equals(Mode.BYTE)) {
            append8BitBytes(content, bits, encoding);
        } else if (mode.equals(Mode.KANJI)) {
            appendKanjiBytes(content, bits);
        } else {
            throw new WriterException("Invalid mode: " + mode);
        }
    }

    static void appendNumericBytes(String content, BitVector bits) {
        int length = content.length();
        int i = 0;
        while (i < length) {
            int num1 = content.charAt(i) - '0';
            if (i + 2 < length) {
                // Encode three numeric letters in ten bits.
                int num2 = content.charAt(i + 1) - '0';
                int num3 = content.charAt(i + 2) - '0';
                bits.appendBits(num1 * 100 + num2 * 10 + num3, 10);
                i += 3;
            } else if (i + 1 < length) {
                // Encode two numeric letters in seven bits.
                int num2 = content.charAt(i + 1) - '0';
                bits.appendBits(num1 * 10 + num2, 7);
                i += 2;
            } else {
                // Encode one numeric letter in four bits.
                bits.appendBits(num1, 4);
                i++;
            }
        }
    }

    static void appendAlphanumericBytes(String content, BitVector bits) throws WriterException {
        int length = content.length();
        int i = 0;
        while (i < length) {
            int code1 = getAlphanumericCode(content.charAt(i));
            if (code1 == -1) {
                throw new WriterException();
            }
            if (i + 1 < length) {
                int code2 = getAlphanumericCode(content.charAt(i + 1));
                if (code2 == -1) {
                    throw new WriterException();
                }
                // Encode two alphanumeric letters in 11 bits.
                bits.appendBits(code1 * 45 + code2, 11);
                i += 2;
            } else {
                // Encode one alphanumeric letter in six bits.
                bits.appendBits(code1, 6);
                i++;
            }
        }
    }

    static void append8BitBytes(String content, BitVector bits, String encoding)
            throws WriterException {
        byte[] bytes;
        try {
            bytes = content.getBytes(encoding);
        } catch (UnsupportedEncodingException uee) {
            throw new WriterException(uee.toString());
        }
        for (int i = 0; i < bytes.length; ++i) {
            bits.appendBits(bytes[i], 8);
        }
    }

    static void appendKanjiBytes(String content, BitVector bits) throws WriterException {
        byte[] bytes;
        try {
            bytes = content.getBytes("Shift_JIS");
        } catch (UnsupportedEncodingException uee) {
            throw new WriterException(uee.toString());
        }
        int length = bytes.length;
        for (int i = 0; i < length; i += 2) {
            int byte1 = bytes[i] & 0xFF;
            int byte2 = bytes[i + 1] & 0xFF;
            int code = (byte1 << 8) | byte2;
            int subtracted = -1;
            if (code >= 0x8140 && code <= 0x9ffc) {
                subtracted = code - 0x8140;
            } else if (code >= 0xe040 && code <= 0xebbf) {
                subtracted = code - 0xc140;
            }
            if (subtracted == -1) {
                throw new WriterException("Invalid byte sequence");
            }
            int encoded = ((subtracted >> 8) * 0xc0) + (subtracted & 0xff);
            bits.appendBits(encoded, 13);
        }
    }

    private static void appendECI(CharacterSetECI eci, BitVector bits) {
        bits.appendBits(Mode.ECI.getBits(), 4);
        // This is correct for values up to 127, which is all we need now.
        bits.appendBits(eci.getValue(), 8);
    }

}
