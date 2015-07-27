package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

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
        this(doc, inputStream, doc != null
                ? doc.getWriter().getCompressionLevel()
                : PdfWriter.DEFAULT_COMPRESSION);
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

    public PdfStream() {
        this(null);
    }

    //NOTE This constructor only for PdfReader.
    PdfStream(long offset, PdfDictionary keys) {
        super();
        //TODO review
//        if (keys.get(PdfName.Filter) != null) {
//            setCompressionLevel(PdfOutputStream.DEFAULT_COMPRESSION);
//        } else {
//            setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
//        }
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
    public byte getType() {
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
