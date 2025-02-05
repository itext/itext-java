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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.utils.ICopyFilter;
import com.itextpdf.kernel.utils.NullCopyFilter;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Representation of a stream as described in the PDF Specification.
 */
public class PdfStream extends PdfDictionary {


    protected int compressionLevel;
    // Output stream associated with PDF stream.
    protected PdfOutputStream outputStream;
    private InputStream inputStream;
    private long offset;
    private int length = -1;

    /**
     * Constructs a {@code PdfStream}-object.
     *
     * @param bytes            initial content of {@link PdfOutputStream}.
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     */
    public PdfStream(byte[] bytes, int compressionLevel) {
        super();
        setState(MUST_BE_INDIRECT);
        this.compressionLevel = compressionLevel;
        if (bytes != null && bytes.length > 0) {
            this.outputStream = new PdfOutputStream(new ByteArrayOutputStream(bytes.length));
            this.outputStream.writeBytes(bytes);
        } else {
            this.outputStream = new PdfOutputStream(new ByteArrayOutputStream());
        }
    }

    /**
     * Creates a PdfStream instance.
     *
     * @param bytes bytes to write to the PdfStream
     */
    public PdfStream(byte[] bytes) {
        this(bytes, CompressionConstants.UNDEFINED_COMPRESSION);
    }

    /**
     * Creates an efficient stream. No temporary array is ever created. The {@code InputStream}
     * is totally consumed but is not closed. The general usage is:
     * <br>
     * <pre>
     * PdfDocument document = ?;
     * InputStream in = ?;
     * PdfStream stream = new PdfStream(document, in, PdfOutputStream.DEFAULT_COMPRESSION);
     * ?
     * stream.flush();
     * in.close();
     * </pre>
     *
     * @param doc              the {@link PdfDocument pdf document} in which this stream lies
     * @param inputStream      the data to write to this stream
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     */
    public PdfStream(PdfDocument doc, InputStream inputStream, int compressionLevel) {
        super();
        if (doc == null) {
            throw new PdfException(
                    KernelExceptionMessageConstant.CANNOT_CREATE_PDFSTREAM_BY_INPUT_STREAM_WITHOUT_PDF_DOCUMENT);
        }
        makeIndirect(doc);
        if (inputStream == null) {
            throw new IllegalArgumentException("The input stream in PdfStream constructor can not be null.");
        }
        this.inputStream = inputStream;
        this.compressionLevel = compressionLevel;
        put(PdfName.Length, new PdfNumber(-1).makeIndirect(doc));
    }

    /**
     * Creates an efficient stream. No temporary array is ever created. The {@code InputStream}
     * is totally consumed but is not closed. The general usage is:
     * <br>
     * <pre>
     * PdfDocument document = ?;
     * InputStream in = ?;
     * PdfStream stream = new PdfStream(document, in);
     * stream.flush();
     * in.close();
     * </pre>
     *
     * @param doc         the {@link PdfDocument pdf document} in which this stream lies
     * @param inputStream the data to write to this stream
     */
    public PdfStream(PdfDocument doc, InputStream inputStream) {
        this(doc, inputStream, CompressionConstants.UNDEFINED_COMPRESSION);
    }

    /**
     * Constructs a {@code PdfStream}-object.
     *
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     */
    public PdfStream(int compressionLevel) {
        this(null, compressionLevel);
    }

    /**
     * Creates an empty PdfStream instance.
     */
    public PdfStream() {
        this((byte[]) null);
    }

    protected PdfStream(java.io.OutputStream outputStream) {
        this.outputStream = new PdfOutputStream(outputStream);
        this.compressionLevel = CompressionConstants.UNDEFINED_COMPRESSION;
        setState(MUST_BE_INDIRECT);
    }

    //NOTE This constructor only for PdfReader.
    PdfStream(long offset, PdfDictionary keys) {
        super();
        this.compressionLevel = CompressionConstants.UNDEFINED_COMPRESSION;
        this.offset = offset;
        putAll(keys);
        PdfNumber length = getAsNumber(PdfName.Length);
        if (length == null) {
            this.length = 0;
        } else {
            this.length = length.intValue();
        }
    }

    /**
     * Gets output stream.
     *
     * @return output stream
     */
    public PdfOutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Gets compression level of this PdfStream.
     * For more details @see {@link com.itextpdf.io.source.DeflaterOutputStream}.
     *
     * @return compression level.
     */
    public int getCompressionLevel() {
        return compressionLevel;
    }

    /**
     * Sets compression level of this PdfStream.
     * For more details @see {@link com.itextpdf.io.source.DeflaterOutputStream}.
     *
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     */
    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    @Override
    public byte getType() {
        return STREAM;
    }

    public int getLength() {
        return length;
    }

    /**
     * Gets decoded stream bytes.
     * Note, {@link PdfName#DCTDecode} and {@link PdfName#JPXDecode} filters will be ignored.
     *
     * @return byte content of the {@code PdfStream}. Byte content will be {@code null},
     * if the {@code PdfStream} was created by {@code InputStream}.
     */
    public byte[] getBytes() {
        return getBytes(true);
    }

    /**
     * Gets stream bytes.
     * Note, {@link PdfName#DCTDecode} and {@link PdfName#JPXDecode} filters will be ignored.
     *
     * @param decoded true if to get decoded stream bytes, otherwise false.
     * @return byte content of the {@code PdfStream}. Byte content will be {@code null},
     * if the {@code PdfStream} was created by {@code InputStream}.
     */
    public byte[] getBytes(boolean decoded) {
        if (isFlushed()) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_OPERATE_WITH_FLUSHED_PDF_STREAM);
        }
        if (inputStream != null) {
            LoggerFactory.getLogger(PdfStream.class).warn("PdfStream was created by InputStream." +
                    "getBytes() always returns null in this case");
            return null;
        }
        byte[] bytes = null;
        if (outputStream != null && outputStream.getOutputStream() != null) {
            assert outputStream.getOutputStream() instanceof ByteArrayOutputStream
                    : "Invalid OutputStream: ByteArrayByteArrayOutputStream expected";
            try {
                outputStream.getOutputStream().flush();
                bytes = ((ByteArrayOutputStream) outputStream.getOutputStream()).toByteArray();
                if (decoded && containsKey(PdfName.Filter)) {
                    bytes = PdfReader.decodeBytes(bytes, this);
                }
            } catch (IOException ioe) {
                throw new PdfException(KernelExceptionMessageConstant.CANNOT_GET_PDF_STREAM_BYTES, ioe, this);
            }
        } else if (getIndirectReference() != null) {
            // This logic makes sense only for the case when PdfStream was created by reader and in this
            // case PdfStream instance always has indirect reference and is never in the MustBeIndirect state
            PdfReader reader = getIndirectReference().getReader();
            if (reader != null) {
                try {
                    bytes = reader.readStreamBytes(this, decoded);
                } catch (IOException ioe) {
                    throw new PdfException(KernelExceptionMessageConstant.CANNOT_GET_PDF_STREAM_BYTES, ioe, this);
                }
            }
        }
        return bytes;
    }

    /**
     * Sets <code>bytes</code> as stream's content.
     * Could not be used with streams which were created by <code>InputStream</code>.
     *
     * @param bytes new content for stream; if <code>null</code> then stream's content will be discarded
     */
    public void setData(byte[] bytes) {
        setData(bytes, false);
    }

    /**
     * Sets or appends <code>bytes</code> to stream content.
     * Could not be used with streams which were created by <code>InputStream</code>.
     *
     * @param bytes  New content for stream. These bytes are considered to be a raw data (i.e. not encoded/compressed/encrypted)
     *               and if it's not true, the corresponding filters shall be set to the PdfStream object manually. 
     *               Data compression generally should be configured via {@link PdfStream#setCompressionLevel} and 
     *               is handled on stream writing to the output document.
     *               If <code>null</code> and <code>append</code> is false then stream's content will be discarded.
     * @param append If set to true then <code>bytes</code> will be appended to the end,
     *               rather then replace original content. The original content will be decoded if needed.  
     */
    public void setData(byte[] bytes, boolean append) {
        if (isFlushed()) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_OPERATE_WITH_FLUSHED_PDF_STREAM);
        }
        if (inputStream != null) {
            throw new PdfException(
                    KernelExceptionMessageConstant.CANNOT_SET_DATA_TO_PDF_STREAM_WHICH_WAS_CREATED_BY_INPUT_STREAM);
        }

        boolean outputStreamIsUninitialized = outputStream == null;
        if (outputStreamIsUninitialized) {
            outputStream = new PdfOutputStream(new ByteArrayOutputStream());
        }

        if (append) {
            if (outputStreamIsUninitialized && getIndirectReference() != null && getIndirectReference().getReader() != null
                    || !outputStreamIsUninitialized && containsKey(PdfName.Filter)) {
                // here is the same as in the getBytes() method: this logic makes sense only when stream is created
                // by reader and in this case indirect reference won't be null and stream is not in the MustBeIndirect state.

                byte[] oldBytes;
                try {
                    oldBytes = getBytes();
                } catch (PdfException ex) {
                    throw new PdfException(
                            KernelExceptionMessageConstant.CANNOT_READ_A_STREAM_IN_ORDER_TO_APPEND_NEW_BYTES, ex);
                }
                outputStream.assignBytes(oldBytes, oldBytes.length);
            }

            if (bytes != null) {
                outputStream.writeBytes(bytes);
            }
        } else {
            if (bytes != null) {
                outputStream.assignBytes(bytes, bytes.length);
            } else {
                outputStream.reset();
            }
        }

        offset = 0;
        // Bytes that are set shall be not encoded, and moreover the existing bytes in cases of the appending are decoded,
        // therefore all filters shall be removed. Compression will be handled on stream flushing.
        remove(PdfName.Filter);
        remove(PdfName.DecodeParms);
    }

    @Override
    protected PdfObject newInstance() {
        return new PdfStream();
    }

    protected long getOffset() {
        return offset;
    }

    /**
     * Update length manually in case its correction.
     *
     * @param length the new length
     * @see com.itextpdf.kernel.pdf.PdfReader#checkPdfStreamLength(PdfStream)
     */
    protected void updateLength(int length) {
        this.length = length;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        copyContent(from, document, NullCopyFilter.getInstance());
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document, ICopyFilter copyFilter) {
        super.copyContent(from, document, copyFilter);
        PdfStream stream = (PdfStream) from;
        assert inputStream == null : "Try to copy the PdfStream that has been just created.";
        byte[] bytes = stream.getBytes(false);
        try {
            outputStream.write(bytes);
        } catch (IOException ioe) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_COPY_OBJECT_CONTENT, ioe, stream);
        }
    }

    protected void initOutputStream(java.io.OutputStream stream) {
        if (getOutputStream() == null && inputStream == null)
            outputStream = new PdfOutputStream(stream != null ? stream : new ByteArrayOutputStream());
    }

    /**
     * Release content of PdfStream.
     */
    protected void releaseContent() {
        super.releaseContent();
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.IO_EXCEPTION, e);
        }
    }

    protected InputStream getInputStream() {
        return inputStream;
    }
}
