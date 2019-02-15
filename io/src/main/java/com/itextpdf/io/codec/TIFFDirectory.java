/*
* Copyright 2003-2012 by Paulo Soares.
*
* This code was originally released in 2001 by SUN (see class
* com.sun.media.imageio.plugins.tiff.TIFFDirectory.java)
* using the BSD license in a specific wording. In a mail dating from
* January 23, 2008, Brian Burkhalter (@sun.com) gave us permission
* to use the code under the following version of the BSD license:
*
* Copyright (c) 2006 Sun Microsystems, Inc. All  Rights Reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* - Redistribution of source code must retain the above copyright
*   notice, this  list of conditions and the following disclaimer.
*
* - Redistribution in binary form must reproduce the above copyright
*   notice, this list of conditions and the following disclaimer in
*   the documentation and/or other materials provided with the
*   distribution.
*
* Neither the name of Sun Microsystems, Inc. or the names of
* contributors may be used to endorse or promote products derived
* from this software without specific prior written permission.
*
* This software is provided "AS IS," without a warranty of any
* kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
* WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
* EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
* NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
* USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
* DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
* ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
* CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
* REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
* INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGES.
*
* You acknowledge that this software is not designed or intended for
* use in the design, construction, operation or maintenance of any
* nuclear facility.
*/
package com.itextpdf.io.codec;

import com.itextpdf.io.IOException;
import com.itextpdf.io.source.RandomAccessFileOrArray;

import java.io.EOFException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class representing an Image File Directory (IFD) from a TIFF 6.0
 * stream.  The TIFF file format is described in more detail in the
 * comments for the TIFFDescriptor class.
 * <br>
 * <p> A TIFF IFD consists of a set of TIFFField tags.  Methods are
 * provided to query the set of tags and to obtain the raw field
 * array.  In addition, convenience methods are provided for acquiring
 * the values of tags that contain a single value that fits into a
 * byte, int, long, float, or double.
 * <br>
 * <p> Every TIFF file is made up of one or more public IFDs that are
 * joined in a linked list, rooted in the file header.  A file may
 * also contain so-called private IFDs that are referenced from
 * tag data and do not appear in the main list.
 * <br>
 * <p><b> This class is not a committed part of the JAI API.  It may
 * be removed or changed in future releases of JAI.</b>
 *
 * @see TIFFField
 */
public class TIFFDirectory implements Serializable {

    private static final long serialVersionUID = -168636766193675380L;

    /**
     * A boolean storing the endianness of the stream.
     */
    boolean isBigEndian;

    /**
     * The number of entries in the IFD.
     */
    int numEntries;

    /**
     * An array of TIFFFields.
     */
    TIFFField[] fields;

    /**
     * A Hashtable indexing the fields by tag number.
     */
    Map<Integer, Integer> fieldIndex = new HashMap<>();

    /**
     * The offset of this IFD.
     */
    long IFDOffset = 8;

    /**
     * The offset of the next IFD.
     */
    long nextIFDOffset = 0;

    /**
     * The default constructor.
     */
    TIFFDirectory() {
    }

    private static boolean isValidEndianTag(int endian) {
        return endian == 0x4949 || endian == 0x4d4d;
    }

    /**
     * Constructs a TIFFDirectory from a SeekableStream.
     * The directory parameter specifies which directory to read from
     * the linked list present in the stream; directory 0 is normally
     * read but it is possible to store multiple images in a single
     * TIFF file by maintaining multiple directories.
     *
     * @param stream    a SeekableStream to read from.
     * @param directory the index of the directory to read.
     * @throws          java.io.IOException
     */
    public TIFFDirectory(RandomAccessFileOrArray stream, int directory)
            throws java.io.IOException {

        long global_save_offset = stream.getPosition();
        long ifd_offset;

        // Read the TIFF header
        stream.seek(0L);
        int endian = stream.readUnsignedShort();
        if (!isValidEndianTag(endian)) {
            throw new IOException(IOException.BadEndiannessTag0x4949Or0x4d4d);
        }
        isBigEndian = endian == 0x4d4d;

        int magic = readUnsignedShort(stream);
        if (magic != 42) {
            throw new IOException(IOException.BadMagicNumberShouldBe42);
        }

        // Get the initial ifd offset as an unsigned int (using a long)
        ifd_offset = readUnsignedInt(stream);

        for (int i = 0; i < directory; i++) {
            if (ifd_offset == 0L) {
                throw new IOException(IOException.DirectoryNumberIsTooLarge);
            }

            stream.seek(ifd_offset);
            int entries = readUnsignedShort(stream);
            stream.skip(12 * entries);

            ifd_offset = readUnsignedInt(stream);
        }

        stream.seek(ifd_offset);
        initialize(stream);
        stream.seek(global_save_offset);
    }

    /**
     * Constructs a TIFFDirectory by reading a SeekableStream.
     * The ifd_offset parameter specifies the stream offset from which
     * to begin reading; this mechanism is sometimes used to store
     * private IFDs within a TIFF file that are not part of the normal
     * sequence of IFDs.
     *
     * @param stream     a SeekableStream to read from.
     * @param ifd_offset the long byte offset of the directory.
     * @param directory  the index of the directory to read beyond the
     *                   one at the current stream offset; zero indicates the IFD
     *                   at the current offset.
     * @throws           java.io.IOException
     */
    public TIFFDirectory(RandomAccessFileOrArray stream, long ifd_offset, int directory)
            throws java.io.IOException {

        long global_save_offset = stream.getPosition();
        stream.seek(0L);
        int endian = stream.readUnsignedShort();
        if (!isValidEndianTag(endian)) {
            throw new IOException(IOException.BadEndiannessTag0x4949Or0x4d4d);
        }
        isBigEndian = endian == 0x4d4d;

        // Seek to the first IFD.
        stream.seek(ifd_offset);

        // Seek to desired IFD if necessary.
        int dirNum = 0;
        while (dirNum < directory) {
            // Get the number of fields in the current IFD.
            int numEntries = readUnsignedShort(stream);

            // Skip to the next IFD offset value field.
            stream.seek(ifd_offset + 12 * numEntries);

            // Read the offset to the next IFD beyond this one.
            ifd_offset = readUnsignedInt(stream);

            // Seek to the next IFD.
            stream.seek(ifd_offset);

            // Increment the directory.
            dirNum++;
        }

        initialize(stream);
        stream.seek(global_save_offset);
    }

    private static final int[] sizeOfType = {
            0, //  0 = n/a
            1, //  1 = byte
            1, //  2 = ascii
            2, //  3 = short
            4, //  4 = long
            8, //  5 = rational
            1, //  6 = sbyte
            1, //  7 = undefined
            2, //  8 = sshort
            4, //  9 = slong
            8, // 10 = srational
            4, // 11 = float
            8  // 12 = double
    };

    private void initialize(RandomAccessFileOrArray stream) throws java.io.IOException {
        long nextTagOffset = 0L;
        long maxOffset = stream.length();
        int i, j;

        IFDOffset = stream.getPosition();

        numEntries = readUnsignedShort(stream);
        fields = new TIFFField[numEntries];

        for (i = 0; i < numEntries && nextTagOffset < maxOffset; i++) {
            int tag = readUnsignedShort(stream);
            int type = readUnsignedShort(stream);
            int count = (int) readUnsignedInt(stream);
            boolean processTag = true;

            // The place to return to to read the next tag
            nextTagOffset = stream.getPosition() + 4;

            try {
                // If the tag data can't fit in 4 bytes, the next 4 bytes
                // contain the starting offset of the data
                if (count * sizeOfType[type] > 4) {
                    long valueOffset = readUnsignedInt(stream);

                    // bounds check offset for EOF
                    if (valueOffset < maxOffset) {
                        stream.seek(valueOffset);
                    } else {
                        // bad offset pointer .. skip tag
                        processTag = false;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException ae) {
                // if the data type is unknown we should skip this TIFF Field
                processTag = false;
            }

            if (processTag) {
                fieldIndex.put(tag, i);
                Object obj = null;

                switch (type) {
                    case TIFFField.TIFF_BYTE:
                    case TIFFField.TIFF_SBYTE:
                    case TIFFField.TIFF_UNDEFINED:
                    case TIFFField.TIFF_ASCII:
                        byte[] bvalues = new byte[count];
                        stream.readFully(bvalues, 0, count);

                        if (type == TIFFField.TIFF_ASCII) {

                            // Can be multiple strings
                            int index = 0, prevIndex = 0;
                            List<String> v = new ArrayList<>();

                            while (index < count) {

                                while (index < count && bvalues[index++] != 0) ;

                                // When we encountered zero, means one string has ended
                                v.add(new String(bvalues, prevIndex,
                                        (index - prevIndex)));
                                prevIndex = index;
                            }

                            count = v.size();
                            String[] strings = new String[count];
                            for (int c = 0; c < count; c++) {
                                strings[c] = v.get(c);
                            }

                            obj = strings;
                        } else {
                            obj = bvalues;
                        }

                        break;

                    case TIFFField.TIFF_SHORT:
                        char[] cvalues = new char[count];
                        for (j = 0; j < count; j++) {
                            cvalues[j] = (char) readUnsignedShort(stream);
                        }
                        obj = cvalues;
                        break;

                    case TIFFField.TIFF_LONG:
                        long[] lvalues = new long[count];
                        for (j = 0; j < count; j++) {
                            lvalues[j] = readUnsignedInt(stream);
                        }
                        obj = lvalues;
                        break;

                    case TIFFField.TIFF_RATIONAL:
                        long[][] llvalues = new long[count][];
                        for (j = 0; j < count; j++) {
                            llvalues[j] = new long[2];
                            llvalues[j][0] = readUnsignedInt(stream);
                            llvalues[j][1] = readUnsignedInt(stream);
                        }
                        obj = llvalues;
                        break;

                    case TIFFField.TIFF_SSHORT:
                        short[] svalues = new short[count];
                        for (j = 0; j < count; j++) {
                            svalues[j] = readShort(stream);
                        }
                        obj = svalues;
                        break;

                    case TIFFField.TIFF_SLONG:
                        int[] ivalues = new int[count];
                        for (j = 0; j < count; j++) {
                            ivalues[j] = readInt(stream);
                        }
                        obj = ivalues;
                        break;

                    case TIFFField.TIFF_SRATIONAL:
                        int[][] iivalues = new int[count][];
                        for (j = 0; j < count; j++) {
                            iivalues[j] = new int[2];
                            iivalues[j][0] = readInt(stream);
                            iivalues[j][1] = readInt(stream);
                        }
                        obj = iivalues;
                        break;

                    case TIFFField.TIFF_FLOAT:
                        float[] fvalues = new float[count];
                        for (j = 0; j < count; j++) {
                            fvalues[j] = readFloat(stream);
                        }
                        obj = fvalues;
                        break;

                    case TIFFField.TIFF_DOUBLE:
                        double[] dvalues = new double[count];
                        for (j = 0; j < count; j++) {
                            dvalues[j] = readDouble(stream);
                        }
                        obj = dvalues;
                        break;

                    default:
                        break;
                }

                fields[i] = new TIFFField(tag, type, count, obj);
            }

            stream.seek(nextTagOffset);
        }

        // Read the offset of the next IFD.
        try {
            nextIFDOffset = readUnsignedInt(stream);
        } catch (Exception e) {
            // broken tiffs may not have this pointer
            nextIFDOffset = 0;
        }
    }

    /**
     * Returns the number of directory entries.
     * @return The number of directory entries
     */
    public int getNumEntries() {
        return numEntries;
    }

    /**
     * Returns the value of a given tag as a TIFFField,
     * or null if the tag is not present.
     * @param tag   The tag
     * @return      The value of the given tag as a TIFFField or null
     */
    public TIFFField getField(int tag) {
        Integer i = fieldIndex.get(tag);
        if (i == null) {
            return null;
        } else {
            return fields[(int) i];
        }
    }

    /**
     * Returns true if a tag appears in the directory.
     * @param tag   The tag
     * @return      True if the tag appears in the directory, false otherwise
     */
    public boolean isTagPresent(int tag) {
        return fieldIndex.containsKey(tag);
    }

    /**
     * Returns an ordered array of integers indicating the tags
     * values.
     * @return an ordered array of integers indicating the tags
     */
    public int[] getTags() {
        int[] tags = new int[fieldIndex.size()];
        int i = 0;

        for (Integer integer : fieldIndex.keySet()) {
            tags[i++] = (int) integer;
        }

        return tags;
    }

    /**
     * Returns an array of TIFFFields containing all the fields
     * in this directory.
     * @return an array of TIFFFields containing all the fields in this directory
     */
    public TIFFField[] getFields() {
        return fields;
    }

    /**
     * Returns the value of a particular index of a given tag as a
     * byte.  The caller is responsible for ensuring that the tag is
     * present and has type TIFFField.TIFF_SBYTE, TIFF_BYTE, or
     * TIFF_UNDEFINED.
     * @param tag       The tag
     * @param index     The index
     * @return the value of a particular index of a given tag as a byte
     */
    public byte getFieldAsByte(int tag, int index) {
        Integer i = fieldIndex.get(tag);
        byte[] b = fields[(int) i].getAsBytes();
        return b[index];
    }

    /**
     * Returns the value of index 0 of a given tag as a
     * byte.  The caller is responsible for ensuring that the tag is
     * present and has  type TIFFField.TIFF_SBYTE, TIFF_BYTE, or
     * TIFF_UNDEFINED.
     * @param tag   The tag
     * @return      The value of index 0 of the given tag as a byte
     */
    public byte getFieldAsByte(int tag) {
        return getFieldAsByte(tag, 0);
    }

    /**
     * Returns the value of a particular index of a given tag as a
     * long.  The caller is responsible for ensuring that the tag is
     * present and has type TIFF_BYTE, TIFF_SBYTE, TIFF_UNDEFINED,
     * TIFF_SHORT, TIFF_SSHORT, TIFF_SLONG or TIFF_LONG.
     *
     * @param tag       The tag
     * @param index     The index
     * @return          The value of the given index of the given tag as a long
     */
    public long getFieldAsLong(int tag, int index) {
        Integer i = fieldIndex.get(tag);
        return fields[(int) i].getAsLong(index);
    }

    /**
     * Returns the value of index 0 of a given tag as a
     * long.  The caller is responsible for ensuring that the tag is
     * present and has type TIFF_BYTE, TIFF_SBYTE, TIFF_UNDEFINED,
     * TIFF_SHORT, TIFF_SSHORT, TIFF_SLONG or TIFF_LONG.
     *
     * @param tag       The tag
     * @return          The value of index 0 of the given tag as a long
     */
    public long getFieldAsLong(int tag) {
        return getFieldAsLong(tag, 0);
    }

    /**
     * Returns the value of a particular index of a given tag as a
     * float.  The caller is responsible for ensuring that the tag is
     * present and has numeric type (all but TIFF_UNDEFINED and
     * TIFF_ASCII).
     *
     * @param tag       The tag
     * @param index     The index
     * @return          The value of the given index of the given tag as a float
     */
    public float getFieldAsFloat(int tag, int index) {
        Integer i = fieldIndex.get(tag);
        return fields[(int) i].getAsFloat(index);
    }

    /**
     * Returns the value of index 0 of a given tag as a float.  The
     * caller is responsible for ensuring that the tag is present and
     * has numeric type (all but TIFF_UNDEFINED and TIFF_ASCII).
     *
     * @param tag       The tag
     * @return          The value of index 0 of the given tag as a float
     */
    public float getFieldAsFloat(int tag) {
        return getFieldAsFloat(tag, 0);
    }

    /**
     * Returns the value of a particular index of a given tag as a
     * double.  The caller is responsible for ensuring that the tag is
     * present and has numeric type (all but TIFF_UNDEFINED and
     * TIFF_ASCII).
     *
     * @param tag       The tag
     * @param index     The index
     * @return          The value of the given index of the given tag as a double
     */
    public double getFieldAsDouble(int tag, int index) {
        Integer i = fieldIndex.get(tag);
        return fields[(int) i].getAsDouble(index);
    }

    /**
     * Returns the value of index 0 of a given tag as a double.  The
     * caller is responsible for ensuring that the tag is present and
     * has numeric type (all but TIFF_UNDEFINED and TIFF_ASCII).
     *
     * @param tag       The tag
     * @return          The value of index 0 of the given tag as a double
     */
    public double getFieldAsDouble(int tag) {
        return getFieldAsDouble(tag, 0);
    }

    // Methods to read primitive data types from the stream

    private short readShort(RandomAccessFileOrArray stream)
            throws java.io.IOException {
        if (isBigEndian) {
            return stream.readShort();
        } else {
            return stream.readShortLE();
        }
    }

    private int readUnsignedShort(RandomAccessFileOrArray stream)
            throws java.io.IOException {
        if (isBigEndian) {
            return stream.readUnsignedShort();
        } else {
            return stream.readUnsignedShortLE();
        }
    }

    private int readInt(RandomAccessFileOrArray stream)
            throws java.io.IOException {
        if (isBigEndian) {
            return stream.readInt();
        } else {
            return stream.readIntLE();
        }
    }

    private long readUnsignedInt(RandomAccessFileOrArray stream)
            throws java.io.IOException {
        if (isBigEndian) {
            return stream.readUnsignedInt();
        } else {
            return stream.readUnsignedIntLE();
        }
    }

    private long readLong(RandomAccessFileOrArray stream)
            throws java.io.IOException {
        if (isBigEndian) {
            return stream.readLong();
        } else {
            return stream.readLongLE();
        }
    }

    private float readFloat(RandomAccessFileOrArray stream)
            throws java.io.IOException {
        if (isBigEndian) {
            return stream.readFloat();
        } else {
            return stream.readFloatLE();
        }
    }

    private double readDouble(RandomAccessFileOrArray stream)
            throws java.io.IOException {
        if (isBigEndian) {
            return stream.readDouble();
        } else {
            return stream.readDoubleLE();
        }
    }

    private static int readUnsignedShort(RandomAccessFileOrArray stream,
                                         boolean isBigEndian)
            throws java.io.IOException {
        if (isBigEndian) {
            return stream.readUnsignedShort();
        } else {
            return stream.readUnsignedShortLE();
        }
    }

    private static long readUnsignedInt(RandomAccessFileOrArray stream,
                                        boolean isBigEndian)
            throws java.io.IOException {
        if (isBigEndian) {
            return stream.readUnsignedInt();
        } else {
            return stream.readUnsignedIntLE();
        }
    }

    // Utilities

    /**
     * Returns the number of image directories (subimages) stored in a
     * given TIFF file, represented by a <code>SeekableStream</code>.
     *
     * @param stream       RandomAccessFileOrArray
     * @return             The number of image directories (subimages) stored
     *                     in a given TIFF file
     * @throws java.io.IOException
     */
    public static int getNumDirectories(RandomAccessFileOrArray stream)
            throws java.io.IOException {
        long pointer = stream.getPosition(); // Save stream pointer

        stream.seek(0L);
        int endian = stream.readUnsignedShort();
        if (!isValidEndianTag(endian)) {
            throw new IOException(IOException.BadEndiannessTag0x4949Or0x4d4d);
        }
        boolean isBigEndian = endian == 0x4d4d;
        int magic = readUnsignedShort(stream, isBigEndian);
        if (magic != 42) {
            throw new IOException(IOException.BadMagicNumberShouldBe42);
        }

        stream.seek(4L);
        long offset = readUnsignedInt(stream, isBigEndian);

        int numDirectories = 0;
        while (offset != 0L) {
            ++numDirectories;

            // EOFException means IFD was probably not properly terminated.
            try {
                stream.seek(offset);
                int entries = readUnsignedShort(stream, isBigEndian);
                stream.skip(12 * entries);
                offset = readUnsignedInt(stream, isBigEndian);
            } catch (EOFException eof) {
                numDirectories--;
                break;
            }
        }

        stream.seek(pointer); // Reset stream pointer
        return numDirectories;
    }

    /**
     * Returns a boolean indicating whether the byte order used in the
     * the TIFF file is big-endian (i.e. whether the byte order is from
     * the most significant to the least significant)
     */
    public boolean isBigEndian() {
        return isBigEndian;
    }

    /**
     * Returns the offset of the IFD corresponding to this
     * <code>TIFFDirectory</code>.
     */
    public long getIFDOffset() {
        return IFDOffset;
    }

    /**
     * Returns the offset of the next IFD after the IFD corresponding to this
     * <code>TIFFDirectory</code>.
     */
    public long getNextIFDOffset() {
        return nextIFDOffset;
    }
}
