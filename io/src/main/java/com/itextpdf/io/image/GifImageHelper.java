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
package com.itextpdf.io.image;

import com.itextpdf.io.IOException;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class GifImageHelper {

    static final int MAX_STACK_SIZE = 4096;   // max decoder pixel stack size

    private static class GifParameters {

        public GifParameters(GifImageData image) {
            this.image = image;
        }

        InputStream input;
        boolean gctFlag;      // global color table used

        int bgIndex;          // background color index
        int bgColor;          // background color
        int pixelAspect;      // pixel aspect ratio

        boolean lctFlag;      // local color table flag
        boolean interlace;    // interlace flag
        int lctSize;          // local color table size

        int ix, iy, iw, ih;   // current image rectangle

        byte[] block = new byte[256];  // current data block
        int blockSize = 0;    // block size

        // last graphic control extension info
        int dispose = 0;   // 0=no action; 1=leave in place; 2=restore to bg; 3=restore to prev
        boolean transparency = false;   // use transparent color
        int delay = 0;        // delay in milliseconds
        int transIndex;       // transparent color index

        // LZW decoder working arrays
        short[] prefix;
        byte[] suffix;
        byte[] pixelStack;
        byte[] pixels;

        byte[] m_out;
        int m_bpc;
        int m_gbpc;
        byte[] m_global_table;
        byte[] m_local_table;
        byte[] m_curr_table;
        int m_line_stride;
        byte[] fromData;
        URL fromUrl;
        int currentFrame;

        GifImageData image;
    }

    /**
     * Reads image source and fills GifImage object with parameters (frames, width, height)
     * @param image GifImage
     */
    public static void processImage(GifImageData image) {
        processImage(image, -1);
    }

    /**
     * Reads image source and fills GifImage object with parameters (frames, width, height)
     * @param image GifImage
     * @param lastFrameNumber the last frame of the gif image should be read
     */
    public static void processImage(GifImageData image, int lastFrameNumber) {
        GifParameters gif = new GifParameters(image);
        InputStream gifStream;
        try {
            if (image.getData() == null) {
                image.loadData();
            }
            gifStream = new ByteArrayInputStream(image.getData());
            process(gifStream, gif, lastFrameNumber);
        } catch (java.io.IOException e) {
            throw new IOException(IOException.GifImageException, e);
        }
    }

    private static void process(InputStream stream, GifParameters gif, int lastFrameNumber) throws java.io.IOException {
        gif.input = stream;
        readHeader(gif);
        readContents(gif, lastFrameNumber);
        if (gif.currentFrame <= lastFrameNumber)
            throw new IOException(IOException.CannotFind1Frame).setMessageParams(lastFrameNumber);
    }

    /**
     * Reads GIF file header information.
     */
    private static void readHeader(GifParameters gif) throws java.io.IOException {
        StringBuilder id = new StringBuilder("");
        for (int i = 0; i < 6; i++)
            id.append((char)gif.input.read());
        if (!id.toString().startsWith("GIF8")) {
            throw new IOException(IOException.GifSignatureNotFound);
        }

        readLSD(gif);
        if (gif.gctFlag) {
            gif.m_global_table = readColorTable(gif.m_gbpc, gif);
        }
    }

    /**
     * Reads Logical Screen Descriptor
     */
    private static void readLSD(GifParameters gif) throws java.io.IOException {

        // logical screen size
        gif.image.setLogicalWidth(readShort(gif));
        gif.image.setLogicalHeight(readShort(gif));

        // packed fields
        int packed = gif.input.read();
        gif.gctFlag = (packed & 0x80) != 0;      // 1   : global color table flag
        gif.m_gbpc = (packed & 7) + 1;
        gif.bgIndex = gif.input.read();        // background color index
        gif.pixelAspect = gif.input.read();    // pixel aspect ratio
    }

    /**
     * Reads next 16-bit value, LSB first
     */
    private static int readShort(GifParameters gif) throws java.io.IOException {
        // read 16-bit value, LSB first
        return gif.input.read() | gif.input.read() << 8;
    }

    /**
     * Reads next variable length block from input.
     *
     * @return number of bytes stored in "buffer"
     */
    private static int readBlock(GifParameters gif) throws java.io.IOException {
        gif.blockSize = gif.input.read();
        if (gif.blockSize <= 0)
            return gif.blockSize = 0;

        gif.blockSize = gif.input.read(gif.block, 0, gif.blockSize);

        return gif.blockSize;
    }

    private static byte[] readColorTable(int bpc, GifParameters gif) throws java.io.IOException {
        int ncolors = 1 << bpc;
        int nbytes = 3*ncolors;
        bpc = newBpc(bpc);
        byte[] table = new byte[(1 << bpc) * 3];
        StreamUtil.readFully(gif.input, table, 0, nbytes);
        return table;
    }


    private static int newBpc(int bpc) {
        switch (bpc) {
            case 1:
            case 2:
            case 4:
                break;
            case 3:
                return 4;
            default:
                return 8;
        }
        return bpc;
    }

    private static void readContents(GifParameters gif, int lastFrameNumber) throws java.io.IOException {
        // read GIF file content blocks
        boolean done = false;
        gif.currentFrame = 0;
        while (!done) {
            int code = gif.input.read();
            switch (code) {
                case 0x2C:    // image separator
                    readFrame(gif);
                    if (gif.currentFrame == lastFrameNumber) {
                        done = true;
                    }
                    gif.currentFrame++;
                    break;
                case 0x21:    // extension
                    code = gif.input.read();
                    switch (code) {

                        case 0xf9:    // graphics control extension
                            readGraphicControlExt(gif);
                            break;

                        case 0xff:    // application extension
                            readBlock(gif);
                            skip(gif);        // don't care
                            break;

                        default:    // uninteresting extension
                            skip(gif);
                    }
                    break;
                default:
                    done = true;
                    break;
            }
        }
    }

    /**
     * Reads next frame image
     */
    private static void readFrame(GifParameters gif) throws java.io.IOException {
        gif.ix = readShort(gif);    // (sub)image position & size
        gif.iy = readShort(gif);
        gif.iw = readShort(gif);
        gif.ih = readShort(gif);

        int packed = gif.input.read();
        gif.lctFlag = (packed & 0x80) != 0;     // 1 - local color table flag
        gif.interlace = (packed & 0x40) != 0;   // 2 - interlace flag
        // 3 - sort flag
        // 4-5 - reserved
        gif.lctSize = 2 << (packed & 7);        // 6-8 - local color table size
        gif.m_bpc = newBpc(gif.m_gbpc);
        if (gif.lctFlag) {
            gif.m_curr_table = readColorTable((packed & 7) + 1, gif);   // read table
            gif.m_bpc = newBpc((packed & 7) + 1);
        }
        else {
            gif.m_curr_table = gif.m_global_table;
        }
        if (gif.transparency && gif.transIndex >= gif.m_curr_table.length / 3)
            gif.transparency = false;
        if (gif.transparency && gif.m_bpc == 1) { // Acrobat 5.05 doesn't like this combination
            byte[] tp = new byte[12];
            System.arraycopy(gif.m_curr_table, 0, tp, 0, 6);
            gif.m_curr_table = tp;
            gif.m_bpc = 2;
        }
        boolean skipZero = decodeImageData(gif);   // decode pixel data
        if (!skipZero)
            skip(gif);

        try {
            Object[] colorspace = new Object[4];
            colorspace[0] = "/Indexed";
            colorspace[1] = "/DeviceRGB";
            int len = gif.m_curr_table.length;
            colorspace[2] = len / 3 - 1;
            colorspace[3] = PdfEncodings.convertToString(gif.m_curr_table, null);
            Map<String, Object> ad = new HashMap<>();
            ad.put("ColorSpace", colorspace);
            RawImageData img = new RawImageData(gif.m_out, ImageType.GIF);
            RawImageHelper.updateRawImageParameters(img, gif.iw, gif.ih, 1, gif.m_bpc, gif.m_out);
            RawImageHelper.updateImageAttributes(img, ad);
            gif.image.addFrame(img);
            if (gif.transparency) {
                img.setTransparency(new int[]{gif.transIndex, gif.transIndex});
            }
        } catch (Exception e) {
            throw new IOException(IOException.GifImageException, e);
        }
    }

    private static boolean decodeImageData(GifParameters gif) throws java.io.IOException {
        int NullCode = -1;
        int npix = gif.iw * gif.ih;
        int available, clear, code_mask, code_size, end_of_information, in_code, old_code,
                bits, code, count, i, datum, data_size, first, top, bi;
        boolean skipZero = false;

        if (gif.prefix == null)
            gif.prefix = new short[MAX_STACK_SIZE];
        if (gif.suffix == null)
            gif.suffix = new byte[MAX_STACK_SIZE];
        if (gif.pixelStack == null)
            gif.pixelStack = new byte[MAX_STACK_SIZE +1];

        gif.m_line_stride = (gif.iw * gif.m_bpc + 7) / 8;
        gif.m_out = new byte[gif.m_line_stride * gif.ih];
        int pass = 1;
        int inc = gif.interlace ? 8 : 1;
        int line = 0;
        int xpos = 0;

        //  Initialize GIF data stream decoder.

        data_size = gif.input.read();
        clear = 1 << data_size;
        end_of_information = clear + 1;
        available = clear + 2;
        old_code = NullCode;
        code_size = data_size + 1;
        code_mask = (1 << code_size) - 1;
        for (code = 0; code < clear; code++) {
            gif.prefix[code] = 0;
            gif.suffix[code] = (byte) code;
        }

        //  Decode GIF pixel stream.

        datum = bits = count = first = top = bi = 0;

        for (i = 0; i < npix; ) {
            if (top == 0) {
                if (bits < code_size) {
                    //  Load bytes until there are enough bits for a code.
                    if (count == 0) {
                        // Read a new data block.
                        count = readBlock(gif);
                        if (count <= 0) {
                            skipZero = true;
                            break;
                        }
                        bi = 0;
                    }
                    datum += (gif.block[bi] & 0xff) << bits;
                    bits += 8;
                    bi++;
                    count--;
                    continue;
                }

                //  Get the next code.

                code = datum & code_mask;
                datum >>= code_size;
                bits -= code_size;

                //  Interpret the code

                if (code > available || code == end_of_information)
                    break;
                if (code == clear) {
                    //  Reset decoder.
                    code_size = data_size + 1;
                    code_mask = (1 << code_size) - 1;
                    available = clear + 2;
                    old_code = NullCode;
                    continue;
                }
                if (old_code == NullCode) {
                    gif.pixelStack[top++] = gif.suffix[code];
                    old_code = code;
                    first = code;
                    continue;
                }
                in_code = code;
                if (code == available) {
                    gif.pixelStack[top++] = (byte) first;
                    code = old_code;
                }
                while (code > clear) {
                    gif.pixelStack[top++] = gif.suffix[code];
                    code = gif.prefix[code];
                }
                first = gif.suffix[code] & 0xff;

                //  Add a new string to the string table,

                if (available >= MAX_STACK_SIZE)
                    break;
                gif.pixelStack[top++] = (byte) first;
                gif.prefix[available] = (short) old_code;
                gif.suffix[available] = (byte) first;
                available++;
                if ((available & code_mask) == 0 && available < MAX_STACK_SIZE) {
                    code_size++;
                    code_mask += available;
                }
                old_code = in_code;
            }

            //  Pop a pixel off the pixel stack.

            top--;
            i++;

            setPixel(xpos, line, gif.pixelStack[top], gif);
            ++xpos;
            if (xpos >= gif.iw) {
                xpos = 0;
                line += inc;
                if (line >= gif.ih) {
                    if (gif.interlace) {
                        do {
                            pass++;
                            switch (pass) {
                                case 2:
                                    line = 4;
                                    break;
                                case 3:
                                    line = 2;
                                    inc = 4;
                                    break;
                                case 4:
                                    line = 1;
                                    inc = 2;
                                    break;
                                default: // this shouldn't happen
                                    line = gif.ih - 1;
                                    inc = 0;
                            }
                        } while (line >= gif.ih);
                    }
                    else {
                        line = gif.ih - 1; // this shouldn't happen
                        inc = 0;
                    }
                }
            }
        }
        return skipZero;
    }


    private static void setPixel(int x, int y, int v, GifParameters gif) {
        if (gif.m_bpc == 8) {
            int pos = x + gif.iw * y;
            gif.m_out[pos] = (byte)v;
        }
        else {
            int pos = gif.m_line_stride * y + x / (8 / gif.m_bpc);
            int vout = v << 8 - gif.m_bpc * (x % (8 / gif.m_bpc))- gif.m_bpc;
            gif.m_out[pos] |= (byte) vout;
        }
    }

    /**
     * Reads Graphics Control Extension values
     */
    private static void readGraphicControlExt(GifParameters gif) throws java.io.IOException {
        gif.input.read();    // block size
        int packed = gif.input.read();   // packed fields
        gif.dispose = (packed & 0x1c) >> 2;   // disposal method
        if (gif.dispose == 0)
            gif.dispose = 1;   // elect to keep old image if discretionary
        gif.transparency = (packed & 1) != 0;
        gif.delay = readShort(gif) * 10;   // delay in milliseconds
        gif.transIndex = gif.input.read();        // transparent color index
        gif.input.read();                     // block terminator
    }

    /**
     * Skips variable length blocks up to and including
     * next zero length block.
     */
    private static void skip(GifParameters gif) throws java.io.IOException {
        do {
            readBlock(gif);
        } while (gif.blockSize > 0);
    }
}
