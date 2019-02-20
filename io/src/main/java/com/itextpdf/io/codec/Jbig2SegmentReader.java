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
package com.itextpdf.io.codec;

import com.itextpdf.io.source.RandomAccessFileOrArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Class to read a JBIG2 file at a basic level: understand all the segments,
 * understand what segments belong to which pages, how many pages there are,
 * what the width and height of each page is, and global segments if there
 * are any.  Or: the minimum required to be able to take a normal sequential
 * or random-access organized file, and be able to embed JBIG2 pages as images
 * in a PDF.
 *
 * TODO: the indeterminate-segment-size value of dataLength, else?
 *
 */

public class Jbig2SegmentReader {

    public static final int SYMBOL_DICTIONARY = 0; //see 7.4.2.

    public static final int INTERMEDIATE_TEXT_REGION = 4; //see 7.4.3.
    public static final int IMMEDIATE_TEXT_REGION = 6; //see 7.4.3.
    public static final int IMMEDIATE_LOSSLESS_TEXT_REGION = 7; //see 7.4.3.
    public static final int PATTERN_DICTIONARY = 16; //see 7.4.4.
    public static final int INTERMEDIATE_HALFTONE_REGION = 20; //see 7.4.5.
    public static final int IMMEDIATE_HALFTONE_REGION = 22; //see 7.4.5.
    public static final int IMMEDIATE_LOSSLESS_HALFTONE_REGION = 23; //see 7.4.5.
    public static final int INTERMEDIATE_GENERIC_REGION = 36; //see 7.4.6.
    public static final int IMMEDIATE_GENERIC_REGION = 38; //see 7.4.6.
    public static final int IMMEDIATE_LOSSLESS_GENERIC_REGION = 39; //see 7.4.6.
    public static final int INTERMEDIATE_GENERIC_REFINEMENT_REGION = 40; //see 7.4.7.
    public static final int IMMEDIATE_GENERIC_REFINEMENT_REGION = 42; //see 7.4.7.
    public static final int IMMEDIATE_LOSSLESS_GENERIC_REFINEMENT_REGION = 43; //see 7.4.7.

    public static final int PAGE_INFORMATION = 48; //see 7.4.8.
    public static final int END_OF_PAGE = 49; //see 7.4.9.
    public static final int END_OF_STRIPE = 50; //see 7.4.10.
    public static final int END_OF_FILE = 51; //see 7.4.11.
    public static final int PROFILES = 52; //see 7.4.12.
    public static final int TABLES = 53; //see 7.4.13.
    public static final int EXTENSION = 62; //see 7.4.14.

    private final Map<Integer, Jbig2Segment> segments = new TreeMap<>();
    private final Map<Integer, Jbig2Page> pages = new TreeMap<>();
    private final Set<Jbig2Segment> globals = new TreeSet<>();
    private RandomAccessFileOrArray ra;
    private boolean sequential;
    private boolean number_of_pages_known;
    private int number_of_pages = -1;
    private boolean read = false;

    /**
     * Inner class that holds information about a JBIG2 segment.
     */
    public static class Jbig2Segment implements Comparable<Jbig2Segment> {

        public final int segmentNumber;
        public long dataLength = -1;
        public int page = -1;
        public int[] referredToSegmentNumbers = null;
        public boolean[] segmentRetentionFlags = null;
        public int type = -1;
        public boolean deferredNonRetain = false;
        public int countOfReferredToSegments = -1;
        public byte[] data = null;
        public byte[] headerData = null;
        public boolean page_association_size = false;
        public int page_association_offset = -1;

        public Jbig2Segment(int segment_number) {
            this.segmentNumber = segment_number;
        }

        public int compareTo(Jbig2Segment s) {
            return this.segmentNumber - s.segmentNumber;
        }


    }

    /**
     * Inner class that holds information about a JBIG2 page.
     */
    public static class Jbig2Page {
        public final int page;
        private final Jbig2SegmentReader sr;
        private final Map<Integer, Jbig2Segment> segs = new TreeMap<>();
        public int pageBitmapWidth = -1;
        public int pageBitmapHeight = -1;

        public Jbig2Page(int page, Jbig2SegmentReader sr) {
            this.page = page;
            this.sr = sr;
        }

        /**
         * return as a single byte array the header-data for each segment in segment number
         * order, EMBEDDED organization, but I am putting the needed segments in SEQUENTIAL organization.
         * if for_embedding, skip the segment types that are known to be not for acrobat.
         *
         * @param for_embedding         True if the bytes represents embedded data, false otherwise
         * @throws java.io.IOException
         * @return a byte array
         */
        public byte[] getData(boolean for_embedding) throws java.io.IOException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (int sn : segs.keySet()) {
                Jbig2Segment s = segs.get(sn);

                // pdf reference 1.4, section 3.3.6 Jbig2Decode Filter
                // D.3 Embedded organisation
                if (for_embedding &&
                        (s.type == END_OF_FILE || s.type == END_OF_PAGE)) {
                    continue;
                }

                if (for_embedding) {
                    // change the page association to page 1
                    byte[] headerData_emb = copyByteArray(s.headerData);
                    if (s.page_association_size) {
                        headerData_emb[s.page_association_offset] = 0x0;
                        headerData_emb[s.page_association_offset + 1] = 0x0;
                        headerData_emb[s.page_association_offset + 2] = 0x0;
                        headerData_emb[s.page_association_offset + 3] = 0x1;
                    } else {
                        headerData_emb[s.page_association_offset] = 0x1;
                    }
                    os.write(headerData_emb);
                } else {
                    os.write(s.headerData);
                }
                os.write(s.data);
            }
            os.close();
            return os.toByteArray();
        }

        public void addSegment(Jbig2Segment s) {
            segs.put(s.segmentNumber, s);
        }

    }

    public Jbig2SegmentReader(RandomAccessFileOrArray ra) throws java.io.IOException {
        this.ra = ra;
    }

    public static byte[] copyByteArray(byte[] b) {
        byte[] bc = new byte[b.length];
        System.arraycopy(b, 0, bc, 0, b.length);
        return bc;
    }

    public void read() throws java.io.IOException {
        if (this.read) {
            throw new IllegalStateException("already.attempted.a.read.on.this.jbig2.file");
        }
        this.read = true;

        readFileHeader();
        // Annex D
        if (this.sequential) {
            // D.1
            do {
                Jbig2Segment tmp = readHeader();
                readSegment(tmp);
                segments.put(tmp.segmentNumber, tmp);
            } while (this.ra.getPosition() < this.ra.length());
        } else {
            // D.2
            Jbig2Segment tmp;
            do {
                tmp = readHeader();
                segments.put(tmp.segmentNumber, tmp);
            } while (tmp.type != END_OF_FILE);
            for (int integer : segments.keySet()) {
                readSegment(segments.get(integer));
            }
        }
    }

    void readSegment(Jbig2Segment s) throws java.io.IOException {
        int ptr = (int) ra.getPosition();

        if (s.dataLength == 0xffffffffl) {
            // TODO figure this bit out, 7.2.7
            return;
        }

        byte[] data = new byte[(int) s.dataLength];
        ra.read(data);
        s.data = data;

        if (s.type == PAGE_INFORMATION) {
            int last = (int) ra.getPosition();
            ra.seek(ptr);
            int page_bitmap_width = ra.readInt();
            int page_bitmap_height = ra.readInt();
            ra.seek(last);
            Jbig2Page p = pages.get(s.page);
            if (p == null) {
                throw new com.itextpdf.io.IOException("Referring to widht or height of a page we haven't seen yet: {0}").setMessageParams(s.page);
            }

            p.pageBitmapWidth = page_bitmap_width;
            p.pageBitmapHeight = page_bitmap_height;
        }
    }

    Jbig2Segment readHeader() throws java.io.IOException {
        int ptr = (int) ra.getPosition();
        // 7.2.1
        int segment_number = ra.readInt();
        Jbig2Segment s = new Jbig2Segment(segment_number);

        // 7.2.3
        int segment_header_flags = ra.read();
        boolean deferred_non_retain = (segment_header_flags & 0x80) == 0x80;
        s.deferredNonRetain = deferred_non_retain;
        boolean page_association_size = (segment_header_flags & 0x40) == 0x40;
        int segment_type = segment_header_flags & 0x3f;
        s.type = segment_type;

        //7.2.4
        int referred_to_byte0 = ra.read();
        int count_of_referred_to_segments = (referred_to_byte0 & 0xE0) >> 5;
        int[] referred_to_segment_numbers = null;
        boolean[] segment_retention_flags = null;

        if (count_of_referred_to_segments == 7) {
            // at least five bytes
            ra.seek(ra.getPosition() - 1);
            count_of_referred_to_segments = ra.readInt() & 0x1fffffff;
            segment_retention_flags = new boolean[count_of_referred_to_segments + 1];
            int i = 0;
            int referred_to_current_byte = 0;
            do {
                int j = i % 8;
                if (j == 0) {
                    referred_to_current_byte = ra.read();
                }
                segment_retention_flags[i] = (0x1 << j & referred_to_current_byte) >> j == 0x1;
                i++;
            } while (i <= count_of_referred_to_segments);

        } else if (count_of_referred_to_segments <= 4) {
            // only one byte
            segment_retention_flags = new boolean[count_of_referred_to_segments + 1];
            referred_to_byte0 &= 0x1f;
            for (int i = 0; i <= count_of_referred_to_segments; i++) {
                segment_retention_flags[i] = (0x1 << i & referred_to_byte0) >> i == 0x1;
            }

        } else if (count_of_referred_to_segments == 5 || count_of_referred_to_segments == 6) {
            throw new com.itextpdf.io.IOException("Count of referred-to segments has forbidden value in the header for segment {0} starting at {1}")
                    .setMessageParams(segment_number, ptr);

        }
        s.segmentRetentionFlags = segment_retention_flags;
        s.countOfReferredToSegments = count_of_referred_to_segments;

        // 7.2.5
        referred_to_segment_numbers = new int[count_of_referred_to_segments + 1];
        for (int i = 1; i <= count_of_referred_to_segments; i++) {
            if (segment_number <= 256) {
                referred_to_segment_numbers[i] = ra.read();
            } else if (segment_number <= 65536) {
                referred_to_segment_numbers[i] = ra.readUnsignedShort();
            } else {
                referred_to_segment_numbers[i] = (int) ra.readUnsignedInt(); // TODO wtf ack
            }
        }
        s.referredToSegmentNumbers = referred_to_segment_numbers;

        // 7.2.6
        int segment_page_association;
        int page_association_offset = (int) ra.getPosition() - ptr;
        if (page_association_size) {
            segment_page_association = ra.readInt();
        } else {
            segment_page_association = ra.read();
        }
        if (segment_page_association < 0) {
            throw new com.itextpdf.io.IOException("Page {0} is invalid for segment {1} starting at {2}")
                    .setMessageParams(segment_page_association, segment_number, ptr);
        }
        s.page = segment_page_association;
        // so we can change the page association at embedding time.
        s.page_association_size = page_association_size;
        s.page_association_offset = page_association_offset;

        if (segment_page_association > 0 && !pages.containsKey(segment_page_association)) {
            pages.put(segment_page_association, new Jbig2Page(segment_page_association, this));
        }
        if (segment_page_association > 0) {
            pages.get(segment_page_association).addSegment(s);
        } else {
            globals.add(s);
        }

        // 7.2.7
        long segment_data_length = ra.readUnsignedInt();
        // TODO the 0xffffffff value that might be here, and how to understand those afflicted segments
        s.dataLength = segment_data_length;

        int end_ptr = (int) ra.getPosition();
        ra.seek(ptr);
        byte[] header_data = new byte[end_ptr - ptr];
        ra.read(header_data);
        s.headerData = header_data;

        return s;
    }

    void readFileHeader() throws java.io.IOException {
        ra.seek(0);
        byte[] idstring = new byte[8];
        ra.read(idstring);

        byte[] refidstring = {(byte) 0x97, 0x4A, 0x42, 0x32, 0x0D, 0x0A, 0x1A, 0x0A};

        for (int i = 0; i < idstring.length; i++) {
            if (idstring[i] != refidstring[i]) {
                throw new com.itextpdf.io.IOException("File header idstring is not good at byte {0}").setMessageParams(i);
            }
        }

        int fileheaderflags = ra.read();

        this.sequential = (fileheaderflags & 0x1) == 0x1;
        this.number_of_pages_known = (fileheaderflags & 0x2) == 0x0;

        if ((fileheaderflags & 0xfc) != 0x0) {
            throw new com.itextpdf.io.IOException("File header flags bits from 2 to 7 should be 0, some not");
        }

        if (this.number_of_pages_known) {
            this.number_of_pages = ra.readInt();
        }
    }

    public int numberOfPages() {
        return pages.size();
    }

    public int getPageHeight(int i) {
        return pages.get(i).pageBitmapHeight;
    }

    public int getPageWidth(int i) {
        return pages.get(i).pageBitmapWidth;
    }

    public Jbig2Page getPage(int page) {
        return pages.get(page);
    }

    public byte[] getGlobal(boolean for_embedding){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] streamBytes = null;
        try {
            for (Object element : globals) {
                Jbig2Segment s = (Jbig2Segment) element;
                if (for_embedding &&
                        (s.type == END_OF_FILE || s.type == END_OF_PAGE)) {
                    continue;
                }
                os.write(s.headerData);
                os.write(s.data);
            }

            if (os.size() > 0) {
                streamBytes = os.toByteArray();
            }
            os.close();
        } catch (java.io.IOException e) {
            Logger logger = LoggerFactory.getLogger(Jbig2SegmentReader.class);
            logger.debug(e.getMessage());
        }

        return streamBytes;
    }

    @Override
    public String toString() {
        if (this.read) {
            return "Jbig2SegmentReader: number of pages: " + this.numberOfPages();
        } else {
            return "Jbig2SegmentReader in indeterminate state.";
        }
    }
}
