/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.io.codec;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

//TODO DEVSIX-6406: add support for indeterminate-segment-size value of dataLength
/**
 * Class to read a JBIG2 file at a basic level: understand all the segments,
 * understand what segments belong to which pages, how many pages there are,
 * what the width and height of each page is, and global segments if there
 * are any.  Or: the minimum required to be able to take a normal sequential
 * or random-access organized file, and be able to embed JBIG2 pages as images
 * in a PDF.
 */
public class Jbig2SegmentReader {
    //see 7.4.2.
    public static final int SYMBOL_DICTIONARY = 0;

    //see 7.4.3.
    public static final int INTERMEDIATE_TEXT_REGION = 4;
    //see 7.4.3.//see 7.4.3.
    public static final int IMMEDIATE_TEXT_REGION = 6;
    //see 7.4.3.
    public static final int IMMEDIATE_LOSSLESS_TEXT_REGION = 7;
    //see 7.4.4.
    public static final int PATTERN_DICTIONARY = 16;
    //see 7.4.5.
    public static final int INTERMEDIATE_HALFTONE_REGION = 20;
    //see 7.4.5.
    public static final int IMMEDIATE_HALFTONE_REGION = 22;
    //see 7.4.5.
    public static final int IMMEDIATE_LOSSLESS_HALFTONE_REGION = 23;
    //see 7.4.6.
    public static final int INTERMEDIATE_GENERIC_REGION = 36;
    //see 7.4.6.
    public static final int IMMEDIATE_GENERIC_REGION = 38;
    //see 7.4.6.
    public static final int IMMEDIATE_LOSSLESS_GENERIC_REGION = 39;
    //see 7.4.7.
    public static final int INTERMEDIATE_GENERIC_REFINEMENT_REGION = 40;
    //see 7.4.7.
    public static final int IMMEDIATE_GENERIC_REFINEMENT_REGION = 42;
    //see 7.4.7.
    public static final int IMMEDIATE_LOSSLESS_GENERIC_REFINEMENT_REGION = 43;

    //see 7.4.8.
    public static final int PAGE_INFORMATION = 48;
    //see 7.4.9.
    public static final int END_OF_PAGE = 49;
    //see 7.4.10.
    public static final int END_OF_STRIPE = 50;
    //see 7.4.11.
    public static final int END_OF_FILE = 51;
    //see 7.4.12.
    public static final int PROFILES = 52;
    //see 7.4.13.
    public static final int TABLES = 53;
    //see 7.4.14.
    public static final int EXTENSION = 62;

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

        private final int segmentNumber;
        private long dataLength = -1;
        private int page = -1;
        private int[] referredToSegmentNumbers = null;
        private boolean[] segmentRetentionFlags = null;
        private int type = -1;
        private boolean deferredNonRetain = false;
        private int countOfReferredToSegments = -1;
        private byte[] data = null;
        private byte[] headerData = null;
        private boolean pageAssociationSize = false;
        private int pageAssociationOffset = -1;

        public Jbig2Segment(int segment_number) {
            this.segmentNumber = segment_number;
        }

        public int compareTo(Jbig2Segment s) {
            return this.segmentNumber - s.segmentNumber;
        }

        /**
         * Retrieves the data length of a JBig2Segment object.
         *
         * @return data length value
         */
        public long getDataLength() {
            return dataLength;
        }

        /**
         * Sets the data length of a JBig2Segment object.
         *
         * @param dataLength data length value
         */
        public void setDataLength(long dataLength) {
            this.dataLength = dataLength;
        }

        /**
         * Retrieves the page number of a JBig2Segment object.
         *
         * @return page number
         */
        public int getPage() {
            return page;
        }

        /**
         * Sets the page number of a JBig2Segment object.
         *
         * @param page page number
         */
        public void setPage(int page) {
            this.page = page;
        }

        /**
         * Retrieves the referred-to segment numbers of a JBig2Segment object.
         *
         * @return Every referred-to segment number
         */
        public int[] getReferredToSegmentNumbers() {
            return referredToSegmentNumbers;
        }

        /**
         * Sets the referred-to segment numbers of a JBig2Segment object.
         *
         * @param referredToSegmentNumbers Referred-to segment numbers
         */
        public void setReferredToSegmentNumbers(int[] referredToSegmentNumbers) {
            this.referredToSegmentNumbers = referredToSegmentNumbers;
        }

        /**
         * Retrieves segment retention flags of a JBig2Segment object.
         *
         * @return Every segment retention flag value
         */
        public boolean[] getSegmentRetentionFlags() {
            return segmentRetentionFlags;
        }

        /**
         * Sets segment retention flags of a JBig2Segment object.
         *
         * @param segmentRetentionFlags Segment retention flag values
         */
        public void setSegmentRetentionFlags(boolean[] segmentRetentionFlags) {
            this.segmentRetentionFlags = segmentRetentionFlags;
        }

        /**
         * Retrieves type of the JBig2Segment object.
         *
         * @return Type value
         */
        public int getType() {
            return type;
        }

        /**
         * Sets type of the JBig2Segment object.
         * @param type Type value
         */
        public void setType(int type) {
            this.type = type;
        }

        /**
         * Retrieves whether the object is deferred without retention.
         * Default value is false.
         *
         * @return true if deferred without retention, false otherwise
         */
        public boolean isDeferredNonRetain() {
            return deferredNonRetain;
        }

        /**
         * Sets whether the JBig2Segments object is deferred without retention.
         *
         * @param deferredNonRetain true for deferred without retention, false otherwise
         */
        public void setDeferredNonRetain(boolean deferredNonRetain) {
            this.deferredNonRetain = deferredNonRetain;
        }

        /**
         * Retrieves the count of the referred-to segments.
         *
         * @return count of referred-to segments
         */
        public int getCountOfReferredToSegments() {
            return countOfReferredToSegments;
        }

        /**
         * Sets the count of referred-to segments of the JBig2Segment object.
         *
         * @param countOfReferredToSegments count of referred segments
         */
        public void setCountOfReferredToSegments(int countOfReferredToSegments) {
            this.countOfReferredToSegments = countOfReferredToSegments;
        }

        /**
         * Retrieves data of the JBig2Segment object.
         *
         * @return data bytes
         */
        public byte[] getData() {
            return data;
        }

        /**
         * Sets data of the JBig2Segment object.
         *
         * @param data data bytes
         */
        public void setData(byte[] data) {
            this.data = data;
        }

        /**
         * Retrieves header data of the JBig2Segment object.
         *
         * @return header data bytes
         */
        public byte[] getHeaderData() {
            return headerData;
        }

        /**
         * Sets header data of the JBig2Segment object.
         *
         * @param headerData header date bytes
         */
        public void setHeaderData(byte[] headerData) {
            this.headerData = headerData;
        }

        /**
         * Retrieves page association size of the JBig2Segment object.
         *
         * @return page association size value
         */
        public boolean isPageAssociationSize() {
            return pageAssociationSize;
        }

        /**
         * Sets page association size of the JBig2Segment object.
         *
         * @param pageAssociationSize page association size
         */
        public void setPageAssociationSize(boolean pageAssociationSize) {
            this.pageAssociationSize = pageAssociationSize;
        }

        /**
         * Retrieves the page association offset of the JBig2Segment object.
         *
         * @return page association offset value
         */
        public int getPageAssociationOffset() {
            return pageAssociationOffset;
        }

        /**
         * Sets page association offset of the JBig2Segment object.
         *
         * @param pageAssociationOffset page association offset
         */
        public void setPageAssociationOffset(int pageAssociationOffset) {
            this.pageAssociationOffset = pageAssociationOffset;
        }

        /**
         * Retrieves the segment number of the JBig2Segment object.
         *
         * @return segment number
         */
        public int getSegmentNumber() {
            return segmentNumber;
        }
    }

    /**
     * Inner class that holds information about a JBIG2 page.
     */
    public static class Jbig2Page {
        private final int page;
        private final Jbig2SegmentReader sr;
        private final Map<Integer, Jbig2Segment> segs = new TreeMap<>();
        private int pageBitmapWidth = -1;
        private int pageBitmapHeight = -1;

        public Jbig2Page(int page, Jbig2SegmentReader sr) {
            this.page = page;
            this.sr = sr;
        }

        /**
         * Retrieves the page number of the Jbig2Page object.
         *
         * @return page number
         */
        public int getPage() {
            return page;
        }

        /**
         * Retrieves page bitmap width of the Jbig2Page object.
         *
         * @return width of page bitmap
         */
        public int getPageBitmapWidth() {
            return pageBitmapWidth;
        }

        /**
         * Sets page bitmap width of the JBig2Page object.
         *
         * @param pageBitmapWidth page bitmap width
         */
        public void setPageBitmapWidth(int pageBitmapWidth) {
            this.pageBitmapWidth = pageBitmapWidth;
        }

        /**
         * Retrieves page bitmap height of the JBig2Page object.
         *
         * @return height of the page bitmap
         */
        public int getPageBitmapHeight() {
            return pageBitmapHeight;
        }

        /**
         * Sets the height of the page bitmap of a Jbig2Page object.
         *
         * @param pageBitmapHeight height of the page bitmap
         */
        public void setPageBitmapHeight(int pageBitmapHeight) {
            this.pageBitmapHeight = pageBitmapHeight;
        }

        /**
         * return as a single byte array the header-data for each segment in segment number
         * order, EMBEDDED organization, but I am putting the needed segments in SEQUENTIAL organization.
         * if for_embedding, skip the segment types that are known to be not for acrobat.
         *
         * @param for_embedding         True if the bytes represents embedded data, false otherwise
         * @throws java.io.IOException if an I/O error occurs.
         * @return a byte array
         */
        public byte[] getData(boolean for_embedding) throws java.io.IOException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (int sn : segs.keySet()) {
                Jbig2Segment s = segs.get(sn);

                // pdf reference 1.4, section 3.3.6 Jbig2Decode Filter
                // D.3 Embedded organisation
                if (for_embedding &&
                        (s.getType() == END_OF_FILE || s.getType() == END_OF_PAGE)) {
                    continue;
                }

                if (for_embedding) {
                    // change the page association to page 1
                    byte[] headerDataEmb = copyByteArray(s.getHeaderData());
                    if (s.isPageAssociationSize()) {
                        headerDataEmb[s.getPageAssociationOffset()] = 0x0;
                        headerDataEmb[s.getPageAssociationOffset() + 1] = 0x0;
                        headerDataEmb[s.getPageAssociationOffset() + 2] = 0x0;
                        headerDataEmb[s.getPageAssociationOffset() + 3] = 0x1;
                    } else {
                        headerDataEmb[s.getPageAssociationOffset()] = 0x1;
                    }
                    os.write(headerDataEmb);
                } else {
                    os.write(s.getHeaderData());
                }
                os.write(s.getData());
            }
            os.close();
            return os.toByteArray();
        }

        public void addSegment(Jbig2Segment s) {
            segs.put(s.getSegmentNumber(), s);
        }

    }

    public Jbig2SegmentReader(RandomAccessFileOrArray ra) {
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
                segments.put(tmp.getSegmentNumber(), tmp);
            } while (this.ra.getPosition() < this.ra.length());
        } else {
            // D.2
            Jbig2Segment tmp;
            do {
                tmp = readHeader();
                segments.put(tmp.getSegmentNumber(), tmp);
            } while (tmp.getType() != END_OF_FILE);
            for (int integer : segments.keySet()) {
                readSegment(segments.get(integer));
            }
        }
    }

    void readSegment(Jbig2Segment s) throws java.io.IOException {
        int ptr = (int) ra.getPosition();

        //TODO DEVSIX-6406 7.2.7 not supported
        if (s.getDataLength() == 0xffffffffl) {
            return;
        }

        byte[] data = new byte[(int) s.getDataLength()];
        ra.read(data);
        s.setData(data);

        if (s.getType() == PAGE_INFORMATION) {
            int last = (int) ra.getPosition();
            ra.seek(ptr);
            int page_bitmap_width = ra.readInt();
            int page_bitmap_height = ra.readInt();
            ra.seek(last);
            Jbig2Page p = pages.get(s.getPage());
            if (p == null) {
                throw new IOException("Referring to widht or height of a page we haven't seen yet: {0}")
                        .setMessageParams(s.getPage());
            }

            p.setPageBitmapWidth(page_bitmap_width);
            p.setPageBitmapHeight(page_bitmap_height);
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
        s.setDeferredNonRetain(deferred_non_retain);
        boolean page_association_size = (segment_header_flags & 0x40) == 0x40;
        int segment_type = segment_header_flags & 0x3f;
        s.setType(segment_type);

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
            throw new IOException("Count of referred-to segments has forbidden value in the header for segment {0} starting at {1}")
                    .setMessageParams(segment_number, ptr);

        }
        s.setSegmentRetentionFlags(segment_retention_flags);
        s.setCountOfReferredToSegments(count_of_referred_to_segments);

        // 7.2.5
        referred_to_segment_numbers = new int[count_of_referred_to_segments + 1];
        for (int i = 1; i <= count_of_referred_to_segments; i++) {
            if (segment_number <= 256) {
                referred_to_segment_numbers[i] = ra.read();
            } else if (segment_number <= 65536) {
                referred_to_segment_numbers[i] = ra.readUnsignedShort();
            } else {
                referred_to_segment_numbers[i] = (int) ra.readUnsignedInt();
            }
        }
        s.setReferredToSegmentNumbers(referred_to_segment_numbers);

        // 7.2.6
        int segment_page_association;
        int page_association_offset = (int) ra.getPosition() - ptr;
        if (page_association_size) {
            segment_page_association = ra.readInt();
        } else {
            segment_page_association = ra.read();
        }
        if (segment_page_association < 0) {
            throw new IOException("Page {0} is invalid for segment {1} starting at {2}")
                    .setMessageParams(segment_page_association, segment_number, ptr);
        }
        s.setPage(segment_page_association);
        // so we can change the page association at embedding time.
        s.setPageAssociationSize(page_association_size);
        s.setPageAssociationOffset(page_association_offset);

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
        //TODO DEVSIX-6406 the 0xffffffff value that might be here, and how to understand those afflicted segments
        s.setDataLength(segment_data_length);

        int end_ptr = (int) ra.getPosition();
        ra.seek(ptr);
        byte[] header_data = new byte[end_ptr - ptr];
        ra.read(header_data);
        s.setHeaderData(header_data);

        return s;
    }

    void readFileHeader() throws java.io.IOException {
        ra.seek(0);
        byte[] idstring = new byte[8];
        ra.read(idstring);

        byte[] refidstring = {(byte) 0x97, 0x4A, 0x42, 0x32, 0x0D, 0x0A, 0x1A, 0x0A};

        for (int i = 0; i < idstring.length; i++) {
            if (idstring[i] != refidstring[i]) {
                throw new IOException("File header idstring is not good at byte {0}").setMessageParams(i);
            }
        }

        int fileheaderflags = ra.read();

        this.sequential = (fileheaderflags & 0x1) == 0x1;
        this.number_of_pages_known = (fileheaderflags & 0x2) == 0x0;

        if ((fileheaderflags & 0xfc) != 0x0) {
            throw new IOException("File header flags bits from 2 to 7 should be 0, some not");
        }

        if (this.number_of_pages_known) {
            this.number_of_pages = ra.readInt();
        }
    }

    public int numberOfPages() {
        return pages.size();
    }

    public int getPageHeight(int i) {
        return pages.get(i).getPageBitmapHeight();
    }

    public int getPageWidth(int i) {
        return pages.get(i).getPageBitmapWidth();
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
                        (s.getType() == END_OF_FILE || s.getType() == END_OF_PAGE)) {
                    continue;
                }
                os.write(s.getHeaderData());
                os.write(s.getData());
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
