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
    protected PdfOutputStream outputStream;
    protected long offset;

    public PdfStream(PdfDocument doc) {
        super();
        makeIndirect(doc);
        this.outputStream = new PdfOutputStream(new ByteArrayOutputStream());
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
    protected PdfStream(long offset) {
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

    public InputStream getInputStream() throws IOException, PdfException {
        return getInputStream(true);
    }

    public byte[] getInputStreamBytes(boolean decoded) throws IOException, PdfException {
        if (offset > 0) {
            return getIndirectReference().getDocument().getReader().readStreamBytes(this, decoded);
        }
        return null;
    }

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
        if(length == null)
            return 0;
        return length.getIntValue();
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
        if (stream.outputStream != null && stream.outputStream.getOutputStream() != null && stream.outputStream.getOutputStream() instanceof ByteArrayOutputStream) {
            try {
                stream.outputStream.getOutputStream().flush();
                byte[] bytes = ((ByteArrayOutputStream) stream.outputStream.getOutputStream()).toByteArray();
                outputStream.write(bytes);
            } catch (IOException ioe) {
                throw new PdfException(PdfException.CannotCopyObjectContent, ioe);
            }
        } else if (stream.getReader() != null) {
            try {
                InputStream is = stream.getInputStream(false);
                byte[] buffer = new byte[stream.getLength()];
                is.read(buffer, 0, stream.getLength());
                getOutputStream().write(buffer);
                is.close();
            } catch (IOException ioe) {
                throw new PdfException(PdfException.CannotCopyObjectContent, ioe);
            }
        }
    }

    protected void initOutputStream() {
        if (getOutputStream() == null)
            outputStream = new PdfOutputStream(new ByteArrayOutputStream());
    }

}
