package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.LoggerFactory;

/**
 * Representation of a stream as described in the PDF Specification.
 */
public class PdfStream extends PdfDictionary {

    protected int compressionLevel;
    // Output stream associated with PDF stream.
    private PdfOutputStream outputStream;
    private InputStream inputStream;
    private long offset;
    private int length = -1;

    /**
     * Constructs a {@code PdfStream}-object.
     *
     * @param bytes            initial content of {@see PdfOutputStream}.
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     * @throws PdfException on error.
     */
    public PdfStream(byte[] bytes, int compressionLevel) {
        super();
        setState(MustBeIndirect);
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
        this(bytes, PdfWriter.UNDEFINED_COMPRESSION);
    }

    /**
     * Creates an efficient stream. No temporary array is ever created. The {@code InputStream}
     * is totally consumed but is not closed. The general usage is:
     * <p/>
     * <pre>
     * PdfDocument document = …;
     * InputStream in = …;
     * PdfStream stream = new PdfStream(document, in, PdfOutputStream.DEFAULT_COMPRESSION);
     * …
     * stream.flush();
     * in.close();
     * </pre>
     *
     * @param inputStream      the data to write to this stream
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     * @throws PdfException on error.
     */
    public PdfStream(PdfDocument doc, InputStream inputStream, int compressionLevel) {
        super();
        if (doc == null) {
            throw new PdfException(PdfException.CannotCreatePdfStreamByInputStreamWithoutPdfDocument);
        }
        makeIndirect(doc);
        if (inputStream == null) {
            throw new NullPointerException("inputStream");
        }
        this.inputStream = inputStream;
        this.compressionLevel = compressionLevel;
        put(PdfName.Length, new PdfNumber(-1).makeIndirect(doc));
    }

    /**
     * Creates an efficient stream. No temporary array is ever created. The {@code InputStream}
     * is totally consumed but is not closed. The general usage is:
     * <p/>
     * <pre>
     * PdfDocument document = …;
     * InputStream in = …;
     * PdfStream stream = new PdfStream(document, in);
     * stream.flush();
     * in.close();
     * </pre>
     *
     * @param inputStream the data to write to this stream
     * @throws PdfException on error.
     */
    public PdfStream(PdfDocument doc, InputStream inputStream) {
        this(doc, inputStream, PdfWriter.UNDEFINED_COMPRESSION);
    }

    /**
     * Constructs a {@code PdfStream}-object.
     *
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     * @throws PdfException on error.
     */
    public PdfStream(int compressionLevel) {
        this(null, compressionLevel);
    }

    /**
     * Creates an empty PdfStream instance.
     */
    public PdfStream() {
        this(null);
    }

    //NOTE This constructor only for PdfReader.
    PdfStream(long offset, PdfDictionary keys) {
        super();
        this.compressionLevel = PdfOutputStream.UNDEFINED_COMPRESSION;
        this.offset = offset;
        putAll(keys);
        PdfNumber length = getAsNumber(PdfName.Length);
        if (length == null) {
            this.length = 0;
        } else {
            this.length = length.getIntValue();
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
     * For more details @see {@link java.util.zip.Deflater}.
     *
     * @return compression level.
     */
    public int getCompressionLevel() {
        return compressionLevel;
    }

    /**
     * Sets compression level of this PdfStream.
     * For more details @see {@link java.util.zip.Deflater}.
     *
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     */
    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    @Override
    public int getType() {
        return Stream;
    }

    public int getLength() {
        return length;
    }

    /**
     * Gets decoded stream bytes.
     *
     * @return byte[]
     * @throws PdfException
     */
    public byte[] getBytes() {
        return getBytes(true);
    }

    /**
     * Gets stream bytes.
     *
     * @param decoded true if to get decoded stream bytes, otherwise false.
     * @return byte content of the {@code PdfStream}. Byte content will be {@code null},
     *          if the {@code PdfStream} was created by {@code InputStream}.
     * @on error.
     */
    public byte[] getBytes(boolean decoded) {
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
            } catch (IOException ioe) {
                throw new PdfException(PdfException.CannotGetPdfStreamBytes, ioe, this);
            }
        } else if (getReader() != null) {
            try {
                bytes = getReader().readStreamBytes(this, decoded);
            } catch (IOException ioe) {
                throw new PdfException(PdfException.CannotGetPdfStreamBytes, ioe, this);
            }
        }
        return bytes;
    }

    /**
     * Sets <code>bytes</code> as stream's content.
     * Could not be used with streams which were created by <code>InputStream</code>.
     * @param bytes new content for stream; if <code>null</code> then stream's content will be discarded
     */
    public void setData(byte[] bytes) {
        setData(bytes, false);
    }

    /**
     * Sets or appends <code>bytes</code> to stream content.
     * Could not be used with streams which were created by <code>InputStream</code>.
     * @param bytes new content for stream; if <code>null</code> and <code>append</code> is false then
     *              stream's content will be discarded
     * @param append if set to true then <code>bytes</code> will be appended to the end,
     *               rather then replace original content
     */
    public void setData(byte[] bytes, boolean append) {
        if (inputStream != null) {
            throw new PdfException(PdfException.CannotSetDataToPdfstreamWhichWasCreatedByInputstream);
        }

        boolean outputStreamIsUninitialized = outputStream == null;
        if (outputStreamIsUninitialized) {
            outputStream = new PdfOutputStream(new ByteArrayOutputStream());
        }

        if (append) {
            if (outputStreamIsUninitialized && getReader() != null) {
                byte[] oldBytes;
                try {
                    oldBytes = getBytes();
                } catch (PdfException ex) {
                    throw new PdfException(PdfException.CannotReadAStreamInOrderToAppendNewBytes, ex);
                }
                offset = 0;
                outputStream.writeBytes(oldBytes);
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

        // Only when we remove old filter will the compression logic be triggered on flushing the stream
        remove(PdfName.Filter);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfStream makeIndirect(PdfDocument document) {
        return (PdfStream) super.makeIndirect(document);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfStream makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        return (PdfStream) super.makeIndirect(document, reference);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfStream copyToDocument(PdfDocument document) {
        return (PdfStream) super.copyToDocument(document, true);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfStream copyToDocument(PdfDocument document, boolean allowDuplicating) {
        return (PdfStream) super.copyToDocument(document, allowDuplicating);
    }

    @Override
    protected PdfStream newInstance() {
        return new PdfStream();
    }

    protected long getOffset() {
        return offset;
    }

    /**
     * Update length manually in case its correction. {@see PdfReader.checkPdfStreamLength()} method.
     * @on error.
     */
    protected void updateLength(int length) {
        this.length = length;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfStream stream = (PdfStream) from;
        assert inputStream == null : "Try to copy the PdfStream that has been just created.";
        byte[] bytes = stream.getBytes(false);
        try {
            outputStream.write(bytes);
        } catch (IOException ioe) {
            throw new PdfException(PdfException.CannotCopyObjectContent, ioe, stream);
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
            throw new PdfException(PdfException.IoException, e);
        }
    }

    protected InputStream getInputStream(){
        return inputStream;
    }
}
