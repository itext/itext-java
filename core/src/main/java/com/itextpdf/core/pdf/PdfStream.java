package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.basics.io.OutputStream;

import java.io.IOException;
import java.io.InputStream;

public class PdfStream extends PdfDictionary {

    /**
     * Output stream associated with PDF stream.
     */
    private PdfOutputStream outputStream;
    private long offset;

    public PdfStream(PdfDocument doc) {
        super();
        makeIndirect(doc);
        this.outputStream = new PdfOutputStream(new ByteArrayOutputStream());
        this.outputStream.document = doc;
    }

    public PdfStream(PdfDocument doc, byte[] bytes) throws IOException {
        super();
        makeIndirect(doc);
        if (bytes != null && bytes.length > 0) {
            this.outputStream = new PdfOutputStream(new ByteArrayOutputStream(bytes.length));
            this.outputStream.write(bytes);
        } else {
            this.outputStream = new PdfOutputStream(new ByteArrayOutputStream());
        }
    }

    public PdfStream(PdfDocument doc, OutputStream stream) {
        this(doc);
        this.outputStream = new PdfOutputStream(stream);
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

    public byte[] getInputStreamBytes() throws IOException, PdfException {
        return getInputStreamBytes(true);
    }

    /**
     * Gets the decoded input stream associated with PdfStream.
     * User is responsible for closing returned stream.
     *
     * @return InputStream
     * @throws IOException
     * @throws PdfException
     */
    public InputStream getInputStream() throws IOException, PdfException {
        return getInputStream(true);
    }

    /**
     * Reads and gets stream bytes.
     *
     * @param decoded true if to get decoded stream bytes, false if to leave it originally encoded.
     * @return byte[]
     * @throws IOException
     * @throws PdfException
     */
    public byte[] getInputStreamBytes(boolean decoded) throws IOException, PdfException {
        if (offset > 0) {
            return getIndirectReference().getDocument().getReader().readStreamBytes(this, decoded);
        }
        return null;
    }

    /**
     * Gets the input stream associated with PdfStream.
     * User is responsible for closing returned stream.
     *
     * @param decoded true if to get decoded stream, false if to leave it originally encoded.
     * @return InputStream
     * @throws IOException
     * @throws PdfException
     */
    public InputStream getInputStream(boolean decoded) throws IOException, PdfException {
        if (offset > 0) {
            return getIndirectReference().getDocument().getReader().readStream(this, decoded);
        }
        return null;
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
                InputStream is = getInputStream(decoded);
                bytes = new byte[getLength()];
                is.read(bytes, 0, getLength());
                is.close();
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

    protected void initOutputStream() {
        if (getOutputStream() == null)
            outputStream = new PdfOutputStream(new ByteArrayOutputStream());
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
