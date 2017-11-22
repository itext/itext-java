// Copyright 2016 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// This is part of java port of project hosted at https://github.com/google/woff2
package com.itextpdf.io.font.woff2;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.io.codec.brotli.dec.BrotliInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.itextpdf.io.font.woff2.JavaUnsignedUtil.asU16;
import static com.itextpdf.io.font.woff2.JavaUnsignedUtil.asU8;
import static com.itextpdf.io.font.woff2.Round.round4;
import static com.itextpdf.io.font.woff2.StoreBytes.storeU16;
import static com.itextpdf.io.font.woff2.StoreBytes.storeU32;
import static com.itextpdf.io.font.woff2.TableTags.kGlyfTableTag;
import static com.itextpdf.io.font.woff2.TableTags.kHeadTableTag;
import static com.itextpdf.io.font.woff2.TableTags.kHheaTableTag;
import static com.itextpdf.io.font.woff2.TableTags.kHmtxTableTag;
import static com.itextpdf.io.font.woff2.TableTags.kKnownTags;
import static com.itextpdf.io.font.woff2.TableTags.kLocaTableTag;
import static com.itextpdf.io.font.woff2.VariableLength.read255UShort;
import static com.itextpdf.io.font.woff2.VariableLength.readBase128;
import static com.itextpdf.io.font.woff2.Woff2Common.collectionHeaderSize;
import static com.itextpdf.io.font.woff2.Woff2Common.computeULongSum;
import static com.itextpdf.io.font.woff2.Woff2Common.kSfntEntrySize;
import static com.itextpdf.io.font.woff2.Woff2Common.kSfntHeaderSize;
import static com.itextpdf.io.font.woff2.Woff2Common.kTtcFontFlavor;
import static com.itextpdf.io.font.woff2.Woff2Common.kWoff2FlagsTransform;
import static com.itextpdf.io.font.woff2.Woff2Common.kWoff2Signature;

// Library for converting WOFF2 format font files to their TTF versions.
class Woff2Dec {
    // simple glyph flags
    private final static int kGlyfOnCurve = 1 << 0;
    private final static int kGlyfXShort = 1 << 1;
    private final static int kGlyfYShort = 1 << 2;
    private final static int kGlyfRepeat = 1 << 3;
    private final static int kGlyfThisXIsSame = 1 << 4;
    private final static int kGlyfThisYIsSame = 1 << 5;

    // composite glyph flags
    // See CompositeGlyph.java in sfntly for full definitions
    private final static int FLAG_ARG_1_AND_2_ARE_WORDS = 1 << 0;
    private final static int FLAG_WE_HAVE_A_SCALE = 1 << 3;
    private final static int FLAG_MORE_COMPONENTS = 1 << 5;
    private final static int FLAG_WE_HAVE_AN_X_AND_Y_SCALE = 1 << 6;
    private final static int FLAG_WE_HAVE_A_TWO_BY_TWO = 1 << 7;
    private final static int FLAG_WE_HAVE_INSTRUCTIONS = 1 << 8;

    private final static int kCheckSumAdjustmentOffset = 8;

    private final static int kEndPtsOfContoursOffset = 10;
    private final static int kCompositeGlyphBegin = 10;

    // 98% of Google Fonts have no glyph above 5k bytes
    // Largest glyph ever observed was 72k bytes
    private final static int kDefaultGlyphBuf = 5120;

    // Over 14k test fonts the max compression ratio seen to date was ~20.
    // >100 suggests you wrote a bad uncompressed size.
    private final static float kMaxPlausibleCompressionRatio = 100.0f;


    // metadata for a TTC font entry
    private static class TtcFont {
        public int flavor;
        public int dst_offset;
        public int header_checksum;
        public short[] table_indices;
    }

    private static class Woff2Header {
        public int flavor;
        public int header_version;
        public short num_tables;
        //TODO do we need it to be long?
        public long compressed_offset;
        public int compressed_length;
        public int uncompressed_size;
        public Woff2Common.Table[] tables;  // num_tables unique tables
        public TtcFont[] ttc_fonts;  // metadata to help rebuild font
    }

    /**
     * Accumulates data we may need to reconstruct a single font. One per font
     * created for a TTC.
     */
    private static class Woff2FontInfo {
        public short num_glyphs;
        public short index_format;
        public short num_hmetrics;
        public short[] x_mins;
        public Map<Integer, Integer> table_entry_by_tag = new HashMap<>();
    }

    // Accumulates metadata as we rebuild the font
    private static class RebuildMetadata {
        int header_checksum;  // set by writeHeaders
        Woff2FontInfo[] font_infos;
        // checksums for tables that have been written.
        // (tag, src_offset) => checksum. Need both because 0-length loca.
        Map<TableChecksumInfo, Integer> checksums = new HashMap<>();
    }

    private static class TableChecksumInfo {
        public int tag;
        public int offset;

        public TableChecksumInfo(int tag, int offset) {
            this.tag = tag;
            this.offset = offset;
        }

        @Override
        public int hashCode() {
            return new Integer(tag).hashCode() * 13 + new Integer(offset).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof TableChecksumInfo) {
                TableChecksumInfo info = (TableChecksumInfo) o;
                return tag == info.tag && offset == info.offset;
            }
            return false;
        }
    }

    private static int withSign(int flag, int baseval) {
        // Precondition: 0 <= baseval < 65536 (to avoid integer overflow)
        return (flag & 1) != 0 ? baseval : -baseval;
    }

    private static int tripletDecode(byte[] data, int flags_in_offset, int in_offset, int in_size, int n_points, Woff2Common.Point[] result) {
        int x = 0;
        int y = 0;

        if (n_points > in_size) {
            throw new FontCompressionException(FontCompressionException.RECONSTRUCT_GLYPH_FAILED);
        }
        int triplet_index = 0;

        for (int i = 0; i < n_points; ++i) {
            int flag = asU8(data[i + flags_in_offset]);
            boolean on_curve = (flag >> 7) == 0;
            flag &= 0x7f;
            int n_data_bytes;
            if (flag < 84) {
                n_data_bytes = 1;
            } else if (flag < 120) {
                n_data_bytes = 2;
            } else if (flag < 124) {
                n_data_bytes = 3;
            } else {
                n_data_bytes = 4;
            }
            if (triplet_index + n_data_bytes > in_size ||
                    triplet_index + n_data_bytes < triplet_index) {
                throw new FontCompressionException(FontCompressionException.RECONSTRUCT_GLYPH_FAILED);
            }
            int dx, dy;
            if (flag < 10) {
                dx = 0;
                dy = withSign(flag, ((flag & 14) << 7) + asU8(data[in_offset + triplet_index]));
            } else if (flag < 20) {
                dx = withSign(flag, (((flag - 10) & 14) << 7) + asU8(data[in_offset + triplet_index]));
                dy = 0;
            } else if (flag < 84) {
                int b0 = flag - 20;
                int b1 = asU8(data[in_offset + triplet_index]);
                dx = withSign(flag, 1 + (b0 & 0x30) + (b1 >> 4));
                dy = withSign(flag >> 1, 1 + ((b0 & 0x0c) << 2) + (b1 & 0x0f));
            } else if (flag < 120) {
                int b0 = flag - 84;
                dx = withSign(flag, 1 + ((b0 / 12) << 8) + asU8(data[in_offset + triplet_index]));
                dy = withSign(flag >> 1,
                        1 + (((b0 % 12) >> 2) << 8) + asU8(data[in_offset + triplet_index + 1]));
            } else if (flag < 124) {
                int b2 = asU8(data[in_offset + triplet_index + 1]);
                dx = withSign(flag, (asU8(data[in_offset + triplet_index]) << 4) + (b2 >> 4));
                dy = withSign(flag >> 1, ((b2 & 0x0f) << 8) + asU8(data[in_offset + triplet_index + 2]));
            } else {
                dx = withSign(flag, (asU8(data[in_offset + triplet_index]) << 8) + asU8(data[in_offset + triplet_index + 1]));
                dy = withSign(flag >> 1,
                        (asU8(data[in_offset + triplet_index + 2]) << 8) + asU8(data[in_offset + triplet_index + 3]));
            }
            triplet_index += n_data_bytes;
            // Possible overflow but coordinate values are not security sensitive
            x += dx;
            y += dy;
            result[i] = new Woff2Common.Point(x, y, on_curve);
        }
        return triplet_index;
    }

    // This function stores just the point data. On entry, dst points to the
    // beginning of a simple glyph. Returns total glyph size on success.
    private static int storePoints(int n_points, Woff2Common.Point[] points,
                                   int n_contours, int instruction_length,
                                   byte[] dst, int dst_size) {
        // I believe that n_contours < 65536, in which case this is safe. However, a
        // comment and/or an assert would be good.
        int flag_offset = kEndPtsOfContoursOffset + 2 * n_contours + 2 +
                instruction_length;
        int last_flag = -1;
        int repeat_count = 0;
        int last_x = 0;
        int last_y = 0;
        int x_bytes = 0;
        int y_bytes = 0;

        for (int i = 0; i < n_points; ++i) {
            Woff2Common.Point point = points[i];
            int flag = point.on_curve ? kGlyfOnCurve : 0;
            int dx = point.x - last_x;
            int dy = point.y - last_y;
            if (dx == 0) {
                flag |= kGlyfThisXIsSame;
            } else if (dx > -256 && dx < 256) {
                flag |= kGlyfXShort | (dx > 0 ? kGlyfThisXIsSame : 0);
                x_bytes += 1;
            } else {
                x_bytes += 2;
            }
            if (dy == 0) {
                flag |= kGlyfThisYIsSame;
            } else if (dy > -256 && dy < 256) {
                flag |= kGlyfYShort | (dy > 0 ? kGlyfThisYIsSame : 0);
                y_bytes += 1;
            } else {
                y_bytes += 2;
            }

            if (flag == last_flag && repeat_count != 255) {
                dst[flag_offset - 1] |= kGlyfRepeat;
                repeat_count++;
            } else {
                if (repeat_count != 0) {
                    if (flag_offset >= dst_size) {
                        throw new FontCompressionException(FontCompressionException.RECONSTRUCT_POINT_FAILED);
                    }
                    dst[flag_offset++] = (byte) repeat_count;
                }
                if (flag_offset >= dst_size) {
                    throw new FontCompressionException(FontCompressionException.RECONSTRUCT_POINT_FAILED);
                }
                dst[flag_offset++] = (byte) flag;
                repeat_count = 0;
            }
            last_x = point.x;
            last_y = point.y;
            last_flag = flag;
        }

        if (repeat_count != 0) {
            if (flag_offset >= dst_size) {
                throw new FontCompressionException(FontCompressionException.RECONSTRUCT_POINT_FAILED);
            }
            dst[flag_offset++] = (byte) repeat_count;
        }
        int xy_bytes = x_bytes + y_bytes;
        if (xy_bytes < x_bytes ||
                flag_offset + xy_bytes < flag_offset ||
                flag_offset + xy_bytes > dst_size) {
            throw new FontCompressionException(FontCompressionException.RECONSTRUCT_POINT_FAILED);
        }

        int x_offset = flag_offset;
        int y_offset = flag_offset + x_bytes;
        last_x = 0;
        last_y = 0;
        for (int i = 0; i < n_points; ++i) {
            int dx = points[i].x - last_x;
            if (dx == 0) {
                // pass
            } else if (dx > -256 && dx < 256) {
                dst[x_offset++] = (byte) Math.abs(dx);
            } else {
                // will always fit for valid input, but overflow is harmless
                x_offset = storeU16(dst, x_offset, dx);
            }
            last_x += dx;
            int dy = points[i].y - last_y;
            if (dy == 0) {
                // pass
            } else if (dy > -256 && dy < 256) {
                dst[y_offset++] = (byte) Math.abs(dy);
            } else {
                y_offset = storeU16(dst, y_offset, dy);
            }
            last_y += dy;
        }
        int glyph_size = y_offset;
        return glyph_size;
    }

    // Compute the bounding box of the coordinates, and store into a glyf buffer.
    // A precondition is that there are at least 10 bytes available.
    // dst should point to the beginning of a 'glyf' record.
    private static void computeBbox(int n_points, Woff2Common.Point[] points, byte[] dst) {
        int x_min = 0;
        int y_min = 0;
        int x_max = 0;
        int y_max = 0;

        if (n_points > 0) {
            x_min = points[0].x;
            x_max = points[0].x;
            y_min = points[0].y;
            y_max = points[0].y;
        }
        for (int i = 1; i < n_points; ++i) {
            int x = points[i].x;
            int y = points[i].y;
            x_min = Math.min(x, x_min);
            x_max = Math.max(x, x_max);
            y_min = Math.min(y, y_min);
            y_max = Math.max(y, y_max);
        }
        int offset = 2;
        offset = storeU16(dst, offset, x_min);
        offset = storeU16(dst, offset, y_min);
        offset = storeU16(dst, offset, x_max);
        offset = storeU16(dst, offset, y_max);
    }

    private static CompositeGlyphInfo sizeOfComposite(Buffer composite_stream) {
        //In c++ code the composite_stream is transferred by value so we need to recreate it in oder to not mess it up
        composite_stream = new Buffer(composite_stream);
        int start_offset = composite_stream.getOffset();
        boolean we_have_instructions = false;

        int flags = FLAG_MORE_COMPONENTS;
        while ((flags & FLAG_MORE_COMPONENTS) != 0) {
            flags = asU16(composite_stream.readShort());
            we_have_instructions |= (flags & FLAG_WE_HAVE_INSTRUCTIONS) != 0;
            int arg_size = 2;  // glyph index
            if ((flags & FLAG_ARG_1_AND_2_ARE_WORDS) != 0) {
                arg_size += 4;
            } else {
                arg_size += 2;
            }
            if ((flags & FLAG_WE_HAVE_A_SCALE) != 0) {
                arg_size += 2;
            } else if ((flags & FLAG_WE_HAVE_AN_X_AND_Y_SCALE) != 0) {
                arg_size += 4;
            } else if ((flags & FLAG_WE_HAVE_A_TWO_BY_TWO) != 0) {
                arg_size += 8;
            }
            composite_stream.skip(arg_size);
        }

        int size = composite_stream.getOffset() - start_offset;
        boolean have_instructions = we_have_instructions;

        return new CompositeGlyphInfo(size, have_instructions);
    }

    private static class CompositeGlyphInfo {
        public int size;
        public boolean have_instructions;

        public CompositeGlyphInfo(int size, boolean have_instructions) {
            this.size = size;
            this.have_instructions = have_instructions;
        }
    }

    private static void pad4(Woff2Out out) {
        byte[] zeroes = {0, 0, 0};
        if (out.size() + 3 < out.size()) {
            throw new FontCompressionException(FontCompressionException.PADDING_OVERFLOW);
        }
        int pad_bytes = Round.round4(out.size()) - out.size();
        if (pad_bytes > 0) {
            out.write(zeroes, 0, pad_bytes);
        }
    }

    // Build TrueType loca table. Returns loca_checksum
    private static int storeLoca(int[] loca_values, int index_format, Woff2Out out) {
        // TODO(user) figure out what index format to use based on whether max
        // offset fits into uint16_t or not
        long loca_size = loca_values.length;
        long offset_size = index_format != 0 ? 4 : 2;
        if ((loca_size << 2) >> 2 != loca_size) {
            throw new FontCompressionException(FontCompressionException.LOCA_SIZE_OVERFLOW);
        }
        byte[] loca_content = new byte[(int) (loca_size * offset_size)];
        int offset = 0;
        for (int i = 0; i < loca_values.length; ++i) {
            int value = loca_values[i];
            if (index_format != 0) {
                offset = storeU32(loca_content, offset, value);
            } else {
                offset = storeU16(loca_content, offset, value >> 1);
            }
        }
        int checksum = computeULongSum(loca_content, 0, loca_content.length);
        out.write(loca_content, 0, loca_content.length);
        return checksum;
    }

    // Reconstruct entire glyf table based on transformed original
    private static Checksums reconstructGlyf(byte[] data, int data_offset,
                                             Woff2Common.Table glyf_table, int glyph_checksum,
                                             Woff2Common.Table loca_table, int loca_checksum,
                                             Woff2FontInfo info, Woff2Out out) {
        final int kNumSubStreams = 7;
        Buffer file = new Buffer(data, data_offset, glyf_table.transform_length);
        int version;
        ArrayList<StreamInfo> substreams = new ArrayList<>(kNumSubStreams);
        final int glyf_start = out.size();

        // TODO: check version on 0?
        version = file.readInt();
        info.num_glyphs = file.readShort();
        info.index_format = file.readShort();

        int offset = (2 + kNumSubStreams) * 4;
        if (offset > glyf_table.transform_length) {
            throw new FontCompressionException(FontCompressionException.RECONSTRUCT_GLYF_TABLE_FAILED);
        }
        // Invariant from here on: data_size >= offset
        for (int i = 0; i < kNumSubStreams; ++i) {
            int substream_size;
            substream_size = file.readInt();
            if (substream_size > glyf_table.transform_length - offset) {
                throw new FontCompressionException(FontCompressionException.RECONSTRUCT_GLYF_TABLE_FAILED);
            }
            substreams.add(new StreamInfo(data_offset + offset, substream_size));
            offset += substream_size;
        }
        Buffer n_contour_stream = new Buffer(data, substreams.get(0).offset, substreams.get(0).length);
        Buffer n_points_stream = new Buffer(data, substreams.get(1).offset, substreams.get(1).length);
        Buffer flag_stream = new Buffer(data, substreams.get(2).offset, substreams.get(2).length);
        Buffer glyph_stream = new Buffer(data, substreams.get(3).offset, substreams.get(3).length);
        Buffer composite_stream = new Buffer(data, substreams.get(4).offset, substreams.get(4).length);
        Buffer bbox_stream = new Buffer(data, substreams.get(5).offset, substreams.get(5).length);
        Buffer instruction_stream = new Buffer(data, substreams.get(6).offset, substreams.get(6).length);

        int[] loca_values = new int[asU16(info.num_glyphs) + 1];
        ArrayList<Integer> n_points_vec = new ArrayList<>();
        Woff2Common.Point[] points = new Woff2Common.Point[0];
        int points_size = 0;
        int bbox_bitmap_offset = bbox_stream.getInitialOffset();
        // Safe because num_glyphs is bounded
        int bitmap_length = ((asU16(info.num_glyphs) + 31) >> 5) << 2;
        bbox_stream.skip(bitmap_length);

        // Temp buffer for glyph's.
        int glyph_buf_size = kDefaultGlyphBuf;
        byte[] glyph_buf = new byte[glyph_buf_size];

        info.x_mins = new short[asU16(info.num_glyphs)];
        for (int i = 0; i < asU16(info.num_glyphs); ++i) {
            int glyph_size = 0;
            int n_contours = 0;
            boolean have_bbox = false;
            byte[] bitmap = new byte[bitmap_length];
            System.arraycopy(data, bbox_bitmap_offset, bitmap, 0, bitmap_length);
            if ((data[bbox_bitmap_offset + (i >> 3)] & (0x80 >> (i & 7))) != 0) {
                have_bbox = true;
            }
            n_contours = asU16(n_contour_stream.readShort());

            if (n_contours == 0xffff) {
                // composite glyph
                boolean have_instructions = false;
                int instruction_size = 0;
                if (!have_bbox) {
                    // composite glyphs must have an explicit bbox
                    throw new FontCompressionException(FontCompressionException.RECONSTRUCT_GLYF_TABLE_FAILED);
                }

                int composite_size;
                CompositeGlyphInfo compositeGlyphInfo = sizeOfComposite(composite_stream);
                have_instructions = compositeGlyphInfo.have_instructions;
                composite_size = compositeGlyphInfo.size;
                if (have_instructions) {
                    instruction_size = read255UShort(glyph_stream);
                }

                int size_needed = 12 + composite_size + instruction_size;
                if (glyph_buf_size < size_needed) {
                    glyph_buf = new byte[size_needed];
                    glyph_buf_size = size_needed;
                }

                glyph_size = storeU16(glyph_buf, glyph_size, n_contours);
                bbox_stream.read(glyph_buf, glyph_size, 8);
                glyph_size += 8;

                composite_stream.read(glyph_buf, glyph_size, composite_size);
                glyph_size += composite_size;
                if (have_instructions) {
                    glyph_size = storeU16(glyph_buf, glyph_size, instruction_size);
                    instruction_stream.read(glyph_buf, glyph_size, instruction_size);
                    glyph_size += instruction_size;
                }
            } else if (n_contours > 0) {
                // simple glyph
                n_points_vec.clear();
                int total_n_points = 0;
                int n_points_contour;
                //Read numberOfContours 255UInt16 values from the nPoints stream. Each of these is the number of points of that contour.
                //Convert this into the endPtsOfContours[] array by computing the cumulative sum, then subtracting one.
                //For example, if the values in the stream are [2, 4], then the endPtsOfContours array is [1, 5].
                //Also, the sum of all the values in the array is the total number of points in the glyph, nPoints. In the example given, the value of nPoints is 6.
                for (int j = 0; j < n_contours; ++j) {
                    n_points_contour = read255UShort(n_points_stream);
                    n_points_vec.add(n_points_contour);
                    if (total_n_points + n_points_contour < total_n_points) {
                        throw new FontCompressionException(FontCompressionException.RECONSTRUCT_GLYF_TABLE_FAILED);
                    }
                    total_n_points += n_points_contour;
                }
                int flag_size = total_n_points;
                if (flag_size > flag_stream.getLength() - flag_stream.getOffset()) {
                    throw new FontCompressionException(FontCompressionException.RECONSTRUCT_GLYF_TABLE_FAILED);
                }
                int flags_buf_offset = flag_stream.getInitialOffset() + flag_stream.getOffset();
                int triplet_buf_offset = glyph_stream.getInitialOffset() + glyph_stream.getOffset();
                int triplet_size = glyph_stream.getLength() - glyph_stream.getOffset();
                int triplet_bytes_consumed = 0;
                if (points_size < total_n_points) {
                    points_size = total_n_points;
                    points = new Woff2Common.Point[points_size];
                }
                triplet_bytes_consumed = tripletDecode(data, flags_buf_offset, triplet_buf_offset, triplet_size, total_n_points, points);
                //Read nPoints UInt8 values from the flags stream. Each corresponds to one point in the reconstructed glyph outline.
                //The interpretation of the flag byte is described in details in subclause 5.2.
                flag_stream.skip(flag_size);
                glyph_stream.skip(triplet_bytes_consumed);
                int instruction_size;
                instruction_size = read255UShort(glyph_stream);

                if (total_n_points >= (1 << 27) || instruction_size >= (1 << 30)) {
                    throw new FontCompressionException(FontCompressionException.RECONSTRUCT_GLYF_TABLE_FAILED);
                }
                int size_needed = 12 + 2 * n_contours + 5 * total_n_points
                        + instruction_size;
                if (glyph_buf_size < size_needed) {
                    glyph_buf = new byte[size_needed];
                    glyph_buf_size = size_needed;
                }

                glyph_size = storeU16(glyph_buf, glyph_size, n_contours);
                if (have_bbox) {
                    bbox_stream.read(glyph_buf, glyph_size, 8);
                } else {
                    computeBbox(total_n_points, points, glyph_buf);
                }
                glyph_size = kEndPtsOfContoursOffset;
                int end_point = -1;
                for (int contour_ix = 0; contour_ix < n_contours; ++contour_ix) {
                    end_point += n_points_vec.get(contour_ix);
                    if (end_point >= 65536) {
                        throw new FontCompressionException(FontCompressionException.RECONSTRUCT_GLYF_TABLE_FAILED);
                    }
                    glyph_size = storeU16(glyph_buf, glyph_size, end_point);
                }

                glyph_size = storeU16(glyph_buf, glyph_size, instruction_size);
                instruction_stream.read(glyph_buf, glyph_size, instruction_size);
                glyph_size += instruction_size;

                glyph_size = storePoints(total_n_points, points, n_contours, instruction_size, glyph_buf, glyph_buf_size);
            }

            loca_values[i] = out.size() - glyf_start;
            out.write(glyph_buf, 0, glyph_size);

            // TODO(user) Old code aligned glyphs ... but do we actually need to?
            pad4(out);

            glyph_checksum += computeULongSum(glyph_buf, 0, glyph_size);

            // We may need x_min to reconstruct 'hmtx'
            if (n_contours > 0) {
                Buffer x_min_buf = new Buffer(glyph_buf, 2, 2);
                info.x_mins[i] = x_min_buf.readShort();
            }
        }

        // glyf_table dst_offset was set by reconstructFont
        glyf_table.dst_length = out.size() - glyf_table.dst_offset;
        loca_table.dst_offset = out.size();
        // loca[n] will be equal the length of the glyph data ('glyf') table
        loca_values[asU16(info.num_glyphs)] = glyf_table.dst_length;
        loca_checksum = storeLoca(loca_values, info.index_format, out);
        loca_table.dst_length = out.size() - loca_table.dst_offset;

        return new Checksums(loca_checksum, glyph_checksum);
    }

    private static class Checksums {
        public int loca_checksum;
        public int glyph_checksum;

        public Checksums(int loca_checksum, int glyph_checksum) {
            this.loca_checksum = loca_checksum;
            this.glyph_checksum = glyph_checksum;
        }
    }

    private static class StreamInfo {
        public int offset;
        public int length;

        public StreamInfo(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
    }

    private static Woff2Common.Table findTable(ArrayList<Woff2Common.Table> tables, int tag) {
        for (Woff2Common.Table table : tables) {
            if (table.tag == tag) {
                return table;
            }
        }
        return null;
    }

    // Get numberOfHMetrics, https://www.microsoft.com/typography/otspec/hhea.htm
    private static short readNumHMetrics(byte[] data, int offset, int data_length) {
        // Skip 34 to reach 'hhea' numberOfHMetrics
        Buffer buffer = new Buffer(data, offset, data_length);
        buffer.skip(34);
        short result = buffer.readShort();
        return result;
    }

    private static int reconstructTransformedHmtx(byte[] transformed_buf,
                                                  int transformed_offset,
                                                  int transformed_size,
                                                  int num_glyphs, //uint16
                                                  int num_hmetrics, //uint16
                                                  short[] x_mins,
                                                  Woff2Out out) {
        Buffer hmtx_buff_in = new Buffer(transformed_buf, transformed_offset, transformed_size);

        int hmtx_flags = asU8(hmtx_buff_in.readByte());

        short[] advance_widths;
        short[] lsbs;
        boolean has_proportional_lsbs = (hmtx_flags & 1) == 0;
        boolean has_monospace_lsbs = (hmtx_flags & 2) == 0;

        // you say you transformed but there is little evidence of it
        if (has_proportional_lsbs && has_monospace_lsbs) {
            throw new FontCompressionException(FontCompressionException.RECONSTRUCT_HMTX_TABLE_FAILED);
        }

        if (x_mins == null || x_mins.length != num_glyphs) {
            throw new FontCompressionException(FontCompressionException.RECONSTRUCT_HMTX_TABLE_FAILED);
        }

        // num_glyphs 0 is OK if there is no 'glyf' but cannot then xform 'hmtx'.
        if (num_hmetrics > num_glyphs) {
            throw new FontCompressionException(FontCompressionException.RECONSTRUCT_HMTX_TABLE_FAILED);
        }

        // https://www.microsoft.com/typography/otspec/hmtx.htm
        // "...only one entry need be in the array, but that entry is required."
        if (num_hmetrics < 1) {
            throw new FontCompressionException(FontCompressionException.RECONSTRUCT_HMTX_TABLE_FAILED);
        }

        advance_widths = new short[num_hmetrics];
        for (int i = 0; i < num_hmetrics; i++) {
            short advance_width;
            advance_width = hmtx_buff_in.readShort(); //u16, but it doesn't meter since we only store them
            advance_widths[i] = advance_width;
        }

        lsbs = new short[num_glyphs];
        for (int i = 0; i < num_hmetrics; i++) {
            short lsb;
            if (has_proportional_lsbs) {
                lsb = hmtx_buff_in.readShort(); //u16, but it doesn't meter since we only store them
            } else {
                lsb = x_mins[i];
            }
            lsbs[i] = lsb;
        }

        for (int i = num_hmetrics; i < num_glyphs; i++) {
            short lsb;
            if (has_monospace_lsbs) {
                lsb = hmtx_buff_in.readShort(); //u16, but it doesn't meter since we only store them
            } else {
                lsb = x_mins[i];
            }
            lsbs[i] = lsb;
        }

        // bake me a shiny new hmtx table
        int hmtx_output_size = 2 * num_glyphs + 2 * num_hmetrics;
        byte[] hmtx_table = new byte[hmtx_output_size];
        int dst_offset = 0;
        for (int i = 0; i < num_glyphs; i++) {
            if (i < num_hmetrics) {
                dst_offset = storeU16(hmtx_table, dst_offset, advance_widths[i]);
            }
            dst_offset = storeU16(hmtx_table, dst_offset, lsbs[i]);
        }

        int checksum = computeULongSum(hmtx_table, 0, hmtx_output_size);
        out.write(hmtx_table, 0, hmtx_output_size);

        return checksum;
    }

    private static void woff2Uncompress(byte[] dst_buf, int dst_offset, int dst_length, byte[] src_buf, int src_offset, int src_length) {
        int remain = dst_length;
        try {
            BrotliInputStream stream = new BrotliInputStream(new ByteArrayInputStream(src_buf, src_offset, src_length));
            while (remain > 0) {
                int read = stream.read(dst_buf, dst_offset, dst_length);
                if (read < 0) {
                    throw new FontCompressionException(FontCompressionException.BROTLI_DECODING_FAILED);
                }
                remain -= read;
            }
            //check that we read stream fully
            if (stream.read() != -1) {
                throw new FontCompressionException(FontCompressionException.BROTLI_DECODING_FAILED);
            }
        } catch (IOException any) {
            throw new FontCompressionException(FontCompressionException.BROTLI_DECODING_FAILED);
        }
        if (remain != 0) {
            throw new FontCompressionException(FontCompressionException.BROTLI_DECODING_FAILED);
        }
    }

    private static void readTableDirectory(Buffer file, Woff2Common.Table[] tables, int num_tables) {
        int src_offset = 0;
        for (int i = 0; i < num_tables; ++i) {
            Woff2Common.Table table = new Woff2Common.Table();
            tables[i] = table;
            int flag_byte = asU8(file.readByte());
            int tag;
            if ((flag_byte & 0x3f) == 0x3f) {
                tag = file.readInt();
            } else {
                tag = kKnownTags[flag_byte & 0x3f];
            }
            int flags = 0;
            int xform_version = ((flag_byte >> 6) & 0x03);

            // 0 means xform for glyph/loca, non-0 for others
            if (tag == kGlyfTableTag || tag == kLocaTableTag) {
                if (xform_version == 0) {
                    flags |= kWoff2FlagsTransform;
                }
            } else if (xform_version != 0) {
                flags |= kWoff2FlagsTransform;
            }
            flags |= xform_version;

            int dst_length = readBase128(file);
            int transform_length = dst_length;
            if ((flags & kWoff2FlagsTransform) != 0) {
                transform_length = readBase128(file);
                if (tag == kLocaTableTag && transform_length != 0) {
                    throw new FontCompressionException(FontCompressionException.READ_TABLE_DIRECTORY_FAILED);
                }
            }
            if (src_offset + transform_length < src_offset) {
                throw new FontCompressionException(FontCompressionException.READ_TABLE_DIRECTORY_FAILED);
            }
            table.src_offset = src_offset;
            table.src_length = transform_length;
            src_offset += transform_length;

            table.tag = tag;
            table.flags = flags;
            table.transform_length = transform_length;
            table.dst_length = dst_length;
        }
    }

    // Writes a single Offset Table entry
    private static int storeOffsetTable(byte[] result, int offset, int flavor, int num_tables) {
        offset = storeU32(result, offset, flavor);  // sfnt version
        offset = storeU16(result, offset, num_tables);  // num_tables
        int max_pow2 = 0;
        while (1 << (max_pow2 + 1) <= num_tables) {
            max_pow2++;
        }
        int output_search_range = (1 << max_pow2) << 4;
        offset = storeU16(result, offset, output_search_range);  // searchRange
        offset = storeU16(result, offset, max_pow2);  // entrySelector
        // rangeShift
        offset = storeU16(result, offset, (num_tables << 4) - output_search_range);
        return offset;
    }

    private static int storeTableEntry(byte[] result, int offset, int tag) {
        offset = storeU32(result, offset, tag);
        offset = storeU32(result, offset, 0);
        offset = storeU32(result, offset, 0);
        offset = storeU32(result, offset, 0);
        return offset;
    }

    //TODO do we realy need long here?
    // First table goes after all the headers, table directory, etc
    private static long computeOffsetToFirstTable(Woff2Header hdr) {
        long offset = kSfntHeaderSize +
                kSfntEntrySize * hdr.num_tables;
        if (hdr.header_version != 0) {
            offset = collectionHeaderSize(hdr.header_version, hdr.ttc_fonts.length)
                    + kSfntHeaderSize * hdr.ttc_fonts.length;
            for (TtcFont ttc_font : hdr.ttc_fonts) {
                offset += kSfntEntrySize * ttc_font.table_indices.length;
            }
        }
        return offset;
    }

    private static ArrayList<Woff2Common.Table> tables(Woff2Header hdr, int font_index) {
        ArrayList<Woff2Common.Table> tables = new ArrayList<>();
        if (hdr.header_version != 0) {
            for (short index : hdr.ttc_fonts[font_index].table_indices) {
                tables.add(hdr.tables[asU16(index)]);
            }
        } else {
            for (Woff2Common.Table table : hdr.tables) {
                tables.add(table);
            }
        }
        return tables;
    }

    private static void reconstructFont(byte[] transformed_buf,
                                        int transformed_buf_offset,
                                        int transformed_buf_size,
                                        RebuildMetadata metadata,
                                        Woff2Header hdr,
                                        int font_index,
                                        Woff2Out out) {
        int dest_offset = out.size();
        byte[] table_entry = new byte[12];
        Woff2FontInfo info = metadata.font_infos[font_index];
        ArrayList<Woff2Common.Table> tables = tables(hdr, font_index);

        // 'glyf' without 'loca' doesn't make sense
        if ((findTable(tables, kGlyfTableTag) != null) != (findTable(tables, kLocaTableTag) != null)) {
            throw new FontCompressionException(FontCompressionException.RECONSTRUCT_TABLE_DIRECTORY_FAILED);
        }

        int font_checksum = metadata.header_checksum;
        if (hdr.header_version != 0) {
            font_checksum = hdr.ttc_fonts[font_index].header_checksum;
        }

        int loca_checksum = 0;
        for (int i = 0; i < tables.size(); i++) {
            Woff2Common.Table table = tables.get(i);

            TableChecksumInfo checksum_key = new TableChecksumInfo(table.tag, table.src_offset);
            boolean reused = metadata.checksums.containsKey(checksum_key);
            if (font_index == 0 && reused) {
                throw new FontCompressionException(FontCompressionException.RECONSTRUCT_TABLE_DIRECTORY_FAILED);
            }

            // TODO(user) a collection with optimized hmtx that reused glyf/loca
            // would fail. We don't optimize hmtx for collections yet.
            if (((long)table.src_offset) + table.src_length > transformed_buf_size) {
                 throw new FontCompressionException(FontCompressionException.RECONSTRUCT_TABLE_DIRECTORY_FAILED);
            }

            if (table.tag == kHheaTableTag) {
                info.num_hmetrics = readNumHMetrics(transformed_buf, transformed_buf_offset + table.src_offset, table.src_length);
            }

            int checksum = 0;
            if (!reused) {
                if ((table.flags & kWoff2FlagsTransform) != kWoff2FlagsTransform) {
                    if (table.tag == kHeadTableTag) {
                        if (table.src_length < 12) {
                            throw new FontCompressionException(FontCompressionException.RECONSTRUCT_TABLE_DIRECTORY_FAILED);
                        }
                        // checkSumAdjustment = 0
                        storeU32(transformed_buf, transformed_buf_offset + table.src_offset + 8, 0);
                    }
                    table.dst_offset = dest_offset;
                    checksum = computeULongSum(transformed_buf, transformed_buf_offset + table.src_offset, table.src_length);
                    out.write(transformed_buf, transformed_buf_offset + table.src_offset, table.src_length);
                } else {
                    if (table.tag == kGlyfTableTag) {
                        table.dst_offset = dest_offset;

                        Woff2Common.Table loca_table = findTable(tables, kLocaTableTag);

                        Checksums resultChecksum = reconstructGlyf(transformed_buf, transformed_buf_offset + table.src_offset, table, checksum, loca_table, loca_checksum, info, out);
                        checksum = resultChecksum.glyph_checksum;
                        loca_checksum = resultChecksum.loca_checksum;
                    } else if (table.tag == kLocaTableTag) {
                        // All the work was done by reconstructGlyf. We already know checksum.
                        checksum = loca_checksum;
                    } else if (table.tag == kHmtxTableTag) {
                        table.dst_offset = dest_offset;
                        // Tables are sorted so all the info we need has been gathered.
                        checksum = reconstructTransformedHmtx(transformed_buf,
                                transformed_buf_offset + table.src_offset, table.src_length,
                                asU16(info.num_glyphs), asU16(info.num_hmetrics), info.x_mins,
                                out);
                    } else {
                        throw new FontCompressionException(FontCompressionException.RECONSTRUCT_TABLE_DIRECTORY_FAILED);  // transform unknown
                    }
                }
                metadata.checksums.put(checksum_key, checksum);
            } else {
                checksum = metadata.checksums.get(checksum_key).intValue();
            }
            font_checksum += checksum;

            // update the table entry with real values.
            storeU32(table_entry, 0, checksum);
            storeU32(table_entry, 4, table.dst_offset);
            storeU32(table_entry, 8, table.dst_length);
            out.write(table_entry, 0, info.table_entry_by_tag.get(table.tag).intValue() + 4, 12);

            // We replaced 0's. Update overall checksum.
            font_checksum += computeULongSum(table_entry, 0, 12);

            pad4(out);

            if (((long) table.dst_offset) + table.dst_length > out.size()) {
                throw new FontCompressionException(FontCompressionException.RECONSTRUCT_TABLE_DIRECTORY_FAILED);
            }
            dest_offset = out.size();
        }

        // Update 'head' checkSumAdjustment. We already set it to 0 and summed font.
        Woff2Common.Table head_table = findTable(tables, kHeadTableTag);
        if (head_table != null) {
            if (head_table.dst_length < 12) {
                throw new FontCompressionException(FontCompressionException.RECONSTRUCT_TABLE_DIRECTORY_FAILED);
            }
            byte[] checksum_adjustment = new byte[4];
            storeU32(checksum_adjustment, 0, (int) (0xB1B0AFBA - font_checksum));
            out.write(checksum_adjustment, 0, head_table.dst_offset + 8, 4);
        }
    }

    private static void readWoff2Header(byte[] data, int length, Woff2Header hdr) {
        Buffer file = new Buffer(data, 0, length);

        int signature;
        signature = file.readInt();
        if (signature != kWoff2Signature) {
            throw new FontCompressionException(FontCompressionException.INCORRECT_SIGNATURE);
        }
        hdr.flavor = file.readInt();

        // TODO(user): Should call IsValidVersionTag() here.

        //assuming we won't deal with font files > 2Gb
        int reported_length = file.readInt();
        assert reported_length > 0;

        if (length != reported_length) {
            throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
        }

        hdr.num_tables = file.readShort();
        if (hdr.num_tables == 0) {
            throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
        }

        // We don't care about these fields of the header:
        //   uint16_t reserved
        //   uint32_t total_sfnt_size, we don't believe this, will compute later
        file.skip(6);

        hdr.compressed_length = file.readInt();
        assert hdr.compressed_length >= 0;

        // We don't care about these fields of the header:
        //   uint16_t major_version, minor_version
        file.skip(2 * 2);

        int meta_offset;
        int meta_length;
        int meta_length_orig;
        meta_offset = file.readInt();
        assert meta_offset >= 0;
        meta_length = file.readInt();
        assert meta_length >= 0;
        meta_length_orig = file.readInt();
        assert meta_length_orig >= 0;
        if (meta_offset != 0) {
            if (meta_offset >= length || length - meta_offset < meta_length) {
                throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
            }
        }
        int priv_offset;
        int priv_length;
        priv_offset = file.readInt();
        assert priv_offset >= 0;
        priv_length = file.readInt();
        assert priv_length >= 0;

        if (priv_offset != 0) {
            if (priv_offset >= length || length - priv_offset < priv_length) {
                throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
            }
        }
        hdr.tables = new Woff2Common.Table[hdr.num_tables];
        readTableDirectory(file, hdr.tables, hdr.num_tables);

        // Before we sort for output the last table end is the uncompressed size.
        Woff2Common.Table last_table = hdr.tables[hdr.tables.length - 1];
        hdr.uncompressed_size = last_table.src_offset + last_table.src_length;
        assert hdr.uncompressed_size > 0;
        if (hdr.uncompressed_size < last_table.src_offset) {
            throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
        }

        hdr.header_version = 0;

        if (hdr.flavor == kTtcFontFlavor) {
            hdr.header_version = file.readInt();
            if (hdr.header_version != 0x00010000 && hdr.header_version != 0x00020000) {
                throw new FontCompressionException(FontCompressionException.READ_COLLECTION_HEADER_FAILED);
            }
            int num_fonts;
            num_fonts = read255UShort(file);
            hdr.ttc_fonts = new TtcFont[num_fonts];

            for (int i = 0; i < num_fonts; i++) {
                TtcFont ttc_font = new TtcFont();
                hdr.ttc_fonts[i] = ttc_font;
                int num_tables;
                num_tables = read255UShort(file);
                ttc_font.flavor = file.readInt();

                ttc_font.table_indices = new short[num_tables];

                Woff2Common.Table glyf_table = null;
                Woff2Common.Table loca_table = null;

                for (int j = 0; j < num_tables; j++) {
                    int table_idx;
                    table_idx = read255UShort(file);
                    if (table_idx >= hdr.tables.length) {
                        throw new FontCompressionException(FontCompressionException.READ_COLLECTION_HEADER_FAILED);
                    }
                    ttc_font.table_indices[j] = (short) table_idx;

                    Woff2Common.Table table = hdr.tables[table_idx];
                    if (table.tag == kLocaTableTag) {
                        loca_table = table;
                    }
                    if (table.tag == kGlyfTableTag) {
                        glyf_table = table;
                    }

                }

                if ((glyf_table == null) != (loca_table == null)) {
                    throw new FontCompressionException(FontCompressionException.READ_COLLECTION_HEADER_FAILED);
                }
            }
        }

        final long first_table_offset = computeOffsetToFirstTable(hdr);

        hdr.compressed_offset = file.getOffset();
        //TODO literary can't happen
        if (hdr.compressed_offset > Integer.MAX_VALUE) {
            throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
        }
        long src_offset = round4(hdr.compressed_offset + hdr.compressed_length);
        long dst_offset = first_table_offset;

        if (src_offset > length) {
            throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
        }
        if (meta_offset != 0) {
            if (src_offset != meta_offset) {
                throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
            }
            src_offset = Round.round4(meta_offset + meta_length);
            //TODO literary can't happen
            if (src_offset > Integer.MAX_VALUE) {
                throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
            }
        }

        if (priv_offset != 0) {
            if (src_offset != priv_offset) {
                throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
            }
            src_offset = Round.round4(priv_offset + priv_length);
            //TODO literary can't happen
            if (src_offset > Integer.MAX_VALUE) {
                throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
            }
        }

        if (src_offset != Round.round4(length)) {
            throw new FontCompressionException(FontCompressionException.READ_HEADER_FAILED);
        }
    }

    // Write everything before the actual table data
    private static void writeHeaders(byte[] data, int length, RebuildMetadata metadata,
                                     Woff2Header hdr, Woff2Out out) {
        long firstTableOffset = computeOffsetToFirstTable(hdr);
        assert firstTableOffset <= Integer.MAX_VALUE;
        byte[] output = new byte[(int) firstTableOffset];

        // Re-order tables in output (OTSpec) order
        List<Woff2Common.Table> sorted_tables = Arrays.asList(hdr.tables);
        if (hdr.header_version != 0) {
            // collection font; we have to sort the table offset vector in each font
            for (TtcFont ttc_font : hdr.ttc_fonts) {
                Map<Integer, Short> sorted_index_by_tag = new TreeMap<>();
                for (short table_index : ttc_font.table_indices) {
                    sorted_index_by_tag.put(hdr.tables[table_index].tag, table_index);
                }
                short index = 0;
                for (Map.Entry<Integer, Short> i : sorted_index_by_tag.entrySet()) {
                    ttc_font.table_indices[index++] = i.getValue();
                }
            }
        } else {
            // non-collection font; we can just sort the tables
            Collections.sort(sorted_tables);
        }

        // Start building the font
        byte[] result = output;
        int offset = 0;
        if (hdr.header_version != 0) {
            // TTC header
            offset = storeU32(result, offset, hdr.flavor);  // TAG TTCTag
            offset = storeU32(result, offset, hdr.header_version);  // FIXED Version
            offset = storeU32(result, offset, hdr.ttc_fonts.length);  // ULONG numFonts
            // Space for ULONG OffsetTable[numFonts] (zeroed initially)
            int offset_table = offset;  // keep start of offset table for later
            for (int i = 0; i < hdr.ttc_fonts.length; ++i) {
                offset = storeU32(result, offset, 0);  // will fill real values in later
            }
            // space for DSIG fields for header v2
            if (hdr.header_version == 0x00020000) {
                offset = storeU32(result, offset, 0);  // ULONG ulDsigTag
                offset = storeU32(result, offset, 0);  // ULONG ulDsigLength
                offset = storeU32(result, offset, 0);  // ULONG ulDsigOffset
            }

            // write Offset Tables and store the location of each in TTC Header
            metadata.font_infos = new Woff2FontInfo[hdr.ttc_fonts.length];
            for (int i = 0; i < hdr.ttc_fonts.length; ++i) {
                TtcFont ttc_font = hdr.ttc_fonts[i];

                // write Offset Table location into TTC Header
                offset_table = storeU32(result, offset_table, offset);

                // write the actual offset table so our header doesn't lie
                ttc_font.dst_offset = offset;
                offset = storeOffsetTable(result, offset, ttc_font.flavor, ttc_font.table_indices.length);

                metadata.font_infos[i] = new Woff2FontInfo();
                for (short table_index : ttc_font.table_indices) {
                    int tag = hdr.tables[table_index].tag;
                    metadata.font_infos[i].table_entry_by_tag.put(tag, offset);
                    offset = storeTableEntry(result, offset, tag);
                }

                ttc_font.header_checksum = computeULongSum(output, ttc_font.dst_offset, offset - ttc_font.dst_offset);
            }
        } else {
            metadata.font_infos = new Woff2FontInfo[1];
            offset = storeOffsetTable(result, offset, hdr.flavor, hdr.num_tables);
            metadata.font_infos[0] = new Woff2FontInfo();
            for (int i = 0; i < hdr.num_tables; ++i) {
                metadata.font_infos[0].table_entry_by_tag.put(sorted_tables.get(i).tag, offset);
                offset = storeTableEntry(result, offset, sorted_tables.get(i).tag);
            }
        }

        out.write(output, 0, output.length);
        metadata.header_checksum = computeULongSum(output, 0, output.length);
    }

    // Compute the size of the final uncompressed font, or throws exception on error.
    public static int computeWoff2FinalSize(byte[] data, int length) {
        Buffer file = new Buffer(data, 0, length);
        file.skip(16);
        return file.readInt();
    }

    // Decompresses the font into out. Returns true on success.
    // Works even if WOFF2Header totalSfntSize is wrong.
    // Please prefer this API.
    public static void convertWoff2ToTtf(byte[] data, int length, Woff2Out out) {
        RebuildMetadata metadata = new RebuildMetadata();
        Woff2Header hdr = new Woff2Header();
        readWoff2Header(data, length, hdr);

        writeHeaders(data, length, metadata, hdr, out);

        final float compression_ratio = (float) hdr.uncompressed_size / length;
        if (compression_ratio > kMaxPlausibleCompressionRatio) {
            throw new FontCompressionException(MessageFormatUtil.format("Implausible compression ratio {0}", compression_ratio));
        }

        byte[] uncompressed_buf = new byte[hdr.uncompressed_size];
        woff2Uncompress(uncompressed_buf, 0, hdr.uncompressed_size, data, (int) hdr.compressed_offset, hdr.compressed_length);

        for (int i = 0; i < metadata.font_infos.length; i++) {
            reconstructFont(uncompressed_buf, 0, hdr.uncompressed_size, metadata, hdr, i, out);
        }
    }
}
