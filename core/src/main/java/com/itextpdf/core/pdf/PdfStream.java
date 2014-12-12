package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;

import java.io.IOException;

public class PdfStream extends PdfDictionary {

    protected int compressionLevel;
    // Output stream associated with PDF stream.
    private PdfOutputStream outputStream;
    private long offset;

    /**
     *
     * @param doc PdfDocument.
     * @param bytes initial content of {@link PdfOutputStream}.
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     * @throws PdfException
     */
    public PdfStream(PdfDocument doc, byte[] bytes, int compressionLevel) throws PdfException {
        super();
        makeIndirect(doc);
        this.compressionLevel = compressionLevel;
        if (bytes != null && bytes.length > 0) {
            this.outputStream = new PdfOutputStream(new ByteArrayOutputStream(bytes.length));
            this.outputStream.writeBytes(bytes);
        } else {
            this.outputStream = new PdfOutputStream(new ByteArrayOutputStream());
        }
        this.outputStream.document = doc;
    }

    public PdfStream(PdfDocument doc, byte[] bytes) throws PdfException {
        this(doc, bytes, PdfWriter.DEFAULT_COMPRESSION);
        if (doc != null) {
            this.compressionLevel = doc.getWriter().getCompressionLevel();
        }
    }

    /**
     *
     * @param doc PdfDocument.
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     */
    public PdfStream(PdfDocument doc, int compressionLevel) throws PdfException {
        this(doc, null, compressionLevel);
    }

    public PdfStream(PdfDocument doc) throws PdfException {
        this(doc, null);
    }

    //NOTE This constructor only for PdfReader.
    PdfStream(long offset) {
        super();
        this.offset = offset;
    }

    private PdfStream() {
        super();
        outputStream = new PdfOutputStream(new ByteArrayOutputStream());
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
     * @return compression level.
     */
    public int getCompressionLevel() {
        return compressionLevel;
    }

    @Override
    public byte getType() {
        return Stream;
    }

    public int getLength() throws PdfException {
        PdfNumber length = getAsNumber(PdfName.Length);
        if (length == null)
            return 0;
        return length.getIntValue();
    }

    /**
     * Gets decoded stream bytes.
     *
     * @return byte[]
     * @throws PdfException
     */
    public byte[] getBytes() throws PdfException {
        return getBytes(true);
    }

    /**
     * Gets stream bytes.
     *
     * @param decoded true if to get decoded stream bytes, otherwise false.
     * @return byte[]
     * @throws PdfException
     */
    public byte[] getBytes(boolean decoded) throws PdfException {
        byte[] bytes = null;
        if (outputStream != null && outputStream.getOutputStream() != null && outputStream.getOutputStream() instanceof ByteArrayOutputStream) {
            try {
                outputStream.getOutputStream().flush();
                bytes = ((ByteArrayOutputStream) outputStream.getOutputStream()).toByteArray();
            } catch (IOException ioe) {
                throw new PdfException(PdfException.CannotGetPdfStreamBytes, ioe, this);
            }
        } else if (getReader() != null) {
            try {
                bytes = getIndirectReference().getDocument().getReader().readStreamBytes(this, decoded);
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

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {
        super.copyContent(from, document);
        PdfStream stream = (PdfStream) from;
        byte[] bytes = stream.getBytes(false);
        try {
            outputStream.write(bytes);
        } catch (IOException ioe) {
            throw new PdfException(PdfException.CannotCopyObjectContent, ioe, stream);
        }
    }

    protected void initOutputStream(java.io.OutputStream stream) {
        if (getOutputStream() == null)
            outputStream = new PdfOutputStream(stream != null ? stream : new ByteArrayOutputStream());
    }

    /**
     * Release content of PdfStream.
     */
    protected void releaseContent() throws PdfException {
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new PdfException(PdfException.IoException, e);
        }
        outputStream = null;
    }
}
