package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.basics.io.OutputStream;
import com.itextpdf.core.crypto.OutputStreamEncryption;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PdfOutputStream extends OutputStream {

    /**
     * Type of encryption.
     */
    public static final int STANDARD_ENCRYPTION_40 = 0;
    /**
     * Type of encryption.
     */
    public static final int STANDARD_ENCRYPTION_128 = 1;
    /**
     * Type of encryption.
     */
    public static final int ENCRYPTION_AES_128 = 2;
    /**
     * Type of encryption.
     */
    public static final int ENCRYPTION_AES_256 = 3;
    /**
     * Mask to separate the encryption type from the encryption mode.
     */
    static final int ENCRYPTION_MASK = 7;
    /**
     * Add this to the mode to keep the metadata in clear text.
     */
    public static final int DO_NOT_ENCRYPT_METADATA = 8;
    /**
     * Add this to the mode to keep encrypt only the embedded files.
     */
    public static final int EMBEDDED_FILES_ONLY = 24;

    // permissions
    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_PRINTING = 4 + 2048;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_MODIFY_CONTENTS = 8;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_COPY = 16;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_MODIFY_ANNOTATIONS = 32;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_FILL_IN = 256;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_SCREENREADERS = 512;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_ASSEMBLY = 1024;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_DEGRADED_PRINTING = 4;


    // compression constants
    /**
     * A possible compression level.
     */
    public static final int DEFAULT_COMPRESSION = Deflater.DEFAULT_COMPRESSION;
    /**
     * A possible compression level.
     */
    public static final int NO_COMPRESSION = Deflater.NO_COMPRESSION;
    /**
     * A possible compression level.
     */
    public static final int BEST_SPEED = Deflater.BEST_SPEED;
    /**
     * A possible compression level.
     */
    public static final int BEST_COMPRESSION = Deflater.BEST_COMPRESSION;

    private static final byte[] stream = getIsoBytes("stream\n");
    private static final byte[] endstream = getIsoBytes("\nendstream\n");
    private static final byte[] openDict = getIsoBytes("<<");
    private static final byte[] closeDict = getIsoBytes(">>");
    private static final byte[] endIndirect = getIsoBytes(" R");
    private static final byte[] endIndirectWithZeroGenNr = getIsoBytes(" 0 R");

    /**
     * Document associated with PdfOutputStream.
     */
    protected PdfDocument document = null;
    /**
     * Contains the business logic for cryptography.
     */
    protected PdfEncryption crypto;

    public PdfOutputStream(java.io.OutputStream outputStream) {
        super(outputStream);
    }

    public PdfOutputStream write(PdfObject pdfObject) throws PdfException {
        switch (pdfObject.getType()) {
            case PdfObject.Array:
                write((PdfArray) pdfObject);
                break;
            case PdfObject.Dictionary:
                write((PdfDictionary) pdfObject);
                break;
            case PdfObject.IndirectReference:
                write((PdfIndirectReference) pdfObject);
                break;
            case PdfObject.Name:
                write((PdfName) pdfObject);
                break;
            case PdfObject.Null:
            case PdfObject.Boolean:
            case PdfObject.Literal:
                write((PdfPrimitiveObject) pdfObject);
                break;
            case PdfObject.String:
                write((PdfString) pdfObject);
                break;
            case PdfObject.Number:
                write((PdfNumber) pdfObject);
                break;
            case PdfObject.Stream:
                write((PdfStream) pdfObject);
                break;
            default:
                break;
        }
        return this;
    }

    /**
     * Sets the encryption options for this document. The userPassword and the
     * ownerPassword can be null or have zero length. In this case the ownerPassword
     * is replaced by a random string. The open permissions for the document can be
     * AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     * AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     * The permissions can be combined by ORing them.
     *
     * @param userPassword   the user password. Can be null or empty
     * @param ownerPassword  the owner password. Can be null or empty
     * @param permissions    the user permissions
     * @param encryptionType the type of encryption. It can be one of STANDARD_ENCRYPTION_40, STANDARD_ENCRYPTION_128 or ENCRYPTION_AES128.
     *                       Optionally DO_NOT_ENCRYPT_METADATA can be ored to output the metadata in cleartext
     * @throws com.itextpdf.basics.PdfException if the document is already open
     */
    public void setEncryption(final byte userPassword[], final byte ownerPassword[], final int permissions, final int encryptionType) throws PdfException {
        if (document != null)
            throw new PdfException(PdfException.EncryptionCanOnlyBeAddedBeforeOpeningDocument);
        crypto = new PdfEncryption();
        crypto.setCryptoMode(encryptionType, 0);
        crypto.setupAllKeys(userPassword, ownerPassword, permissions);
    }

    /**
     * Sets the certificate encryption options for this document. An array of one or more public certificates
     * must be provided together with an array of the same size for the permissions for each certificate.
     * The open permissions for the document can be
     * AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     * AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     * The permissions can be combined by ORing them.
     * Optionally DO_NOT_ENCRYPT_METADATA can be ored to output the metadata in cleartext
     *
     * @param certs          the public certificates to be used for the encryption
     * @param permissions    the user permissions for each of the certificates
     * @param encryptionType the type of encryption. It can be one of STANDARD_ENCRYPTION_40, STANDARD_ENCRYPTION_128 or ENCRYPTION_AES128.
     * @throws com.itextpdf.basics.PdfException if the document is already open
     */
    public void setEncryption(final Certificate[] certs, final int[] permissions, final int encryptionType) throws PdfException {
        if (document != null)
            throw new PdfException(PdfException.EncryptionCanOnlyBeAddedBeforeOpeningDocument);
        crypto = new PdfEncryption();
        if (certs != null) {
            for (int i = 0; i < certs.length; i++) {
                crypto.addRecipient(certs[i], permissions[i]);
            }
        }
        crypto.setCryptoMode(encryptionType, 0);
        crypto.getEncryptionDictionary();
    }

    PdfEncryption getEncryption() {
        return crypto;
    }

    protected void write(PdfArray pdfArray) throws PdfException {
        writeByte((byte) '[');
        for (int i = 0; i < pdfArray.size(); i++) {
            PdfObject value = pdfArray.get(i, false);
            PdfIndirectReference indirectReference;
            if ((indirectReference = value.getIndirectReference()) != null) {
                write(indirectReference);
            } else {
                write(value);
            }
            if (i < pdfArray.size() - 1)
                writeSpace();
        }
        writeByte((byte) ']');
    }

    protected void write(PdfDictionary pdfDictionary) throws PdfException {
        writeBytes(openDict);
        for (Map.Entry<PdfName, PdfObject> entry : pdfDictionary.entrySet()) {
            write(entry.getKey());
            writeSpace();
            PdfObject value = entry.getValue();
            PdfIndirectReference indirectReference;
            if ((indirectReference = value.getIndirectReference()) != null) {
                write(indirectReference);
            } else {
                write(value);
            }
        }
        writeBytes(closeDict);
    }

    protected void write(PdfIndirectReference indirectReference) throws PdfException {
        if (indirectReference.getGenNr() == 0) {
            writeInteger(indirectReference.getObjNr()).
                    writeBytes(endIndirectWithZeroGenNr);
        } else {
            writeInteger(indirectReference.getObjNr()).
                    writeSpace().
                    writeInteger(indirectReference.getGenNr()).
                    writeBytes(endIndirect);
        }
    }

    protected void write(PdfPrimitiveObject pdfPrimitive) throws PdfException {
        writeBytes(pdfPrimitive.getInternalContent());
    }

    protected void write(PdfString pdfString) throws PdfException {
        pdfString.encrypt(crypto);
        if (pdfString.isHexWriting()) {
            writeByte((byte) '<');
            writeBytes(pdfString.getInternalContent());
            writeByte((byte) '>');
        } else {
            writeByte((byte) '(');
            writeBytes(pdfString.getInternalContent());
            writeByte((byte) ')');
        }
    }


    protected void write(PdfName name) throws PdfException {
        writeByte((byte) '/');
        writeBytes(name.getInternalContent());
    }

    protected void write(PdfNumber pdfNumber) throws PdfException {
        if (pdfNumber.hasContent()) {
            writeBytes(pdfNumber.getInternalContent());
        } else if (pdfNumber.getValueType() == PdfNumber.Int) {
            writeInteger(pdfNumber.getIntValue());
        } else {
            writeDouble(pdfNumber.getValue());
        }
    }

    protected void write(PdfStream pdfStream) throws PdfException {
        try {
            //When document is opened in stamping mode the output stream can be uninitialized.
            //We shave to initialize it and write all data from streams input to streams output.
            if (pdfStream.getOutputStream() == null && pdfStream.getReader() != null) {
                byte[] bytes = pdfStream.getBytes(false);
                pdfStream.initOutputStream(new ByteArrayOutputStream(bytes.length));
                pdfStream.getOutputStream().write(bytes);
            }
            assert pdfStream.getOutputStream() != null : "PdfStream lost OutputStream";
            ByteArrayOutputStream byteStream = getFinalStream(pdfStream);
            pdfStream.put(PdfName.Length, new PdfNumber(byteStream.size()));
            write((PdfDictionary) pdfStream);
            writeBytes(PdfOutputStream.stream);
            byteStream.writeTo(this);
            writeBytes(PdfOutputStream.endstream);
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotWritePdfStream, e, pdfStream);
        }
    }

    protected boolean checkEncryption(PdfStream pdfStream) throws PdfException {
        if (crypto == null || crypto.isEmbeddedFilesOnly()) {
            return false;
        } else {
            PdfObject filter = pdfStream.get(PdfName.Filter, true);
            if (filter != null) {
                if (PdfName.Crypt.equals(filter)) {
                    return false;
                } else if (filter.getType() == PdfObject.Array) {
                    PdfArray filters = (PdfArray) filter;
                    if (!filters.isEmpty() && PdfName.Crypt.equals(filters.get(0, true))) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /**
     * Compresses the stream.
     */
    protected ByteArrayOutputStream getFinalStream(PdfStream pdfStream) throws PdfException {
        boolean compress = true;
        if (pdfStream.getCompressionLevel() == NO_COMPRESSION) {
            compress = false;
        }
        // check if a filter already exists
        PdfObject filter = pdfStream.get(PdfName.Filter);
        if (filter != null) {
            if (filter.getType() == PdfObject.Name) {
                if (PdfName.FlateDecode.equals(filter))
                    compress = false;
            } else if (filter.getType() == PdfObject.Array) {
                if (((PdfArray) filter).contains(PdfName.FlateDecode))
                    compress = false;
            } else {
                throw new PdfException(PdfException.StreamCouldNotBeCompressedFilterIsNotANameOrArray);
            }
        }
        try {
            ByteArrayOutputStream stream;
            if (!compress) {
                if (pdfStream instanceof PdfObjectStream) {
                    PdfObjectStream objectStream = (PdfObjectStream) pdfStream;
                    stream = new ByteArrayOutputStream();
                    ((ByteArrayOutputStream) objectStream.getIndexStream().getOutputStream()).writeTo(stream);
                    ((ByteArrayOutputStream) objectStream.getOutputStream().getOutputStream()).writeTo(stream);
                } else {
                    if (pdfStream.getOutputStream() == null && pdfStream.getReader() != null) {
                        byte[] bytes = pdfStream.getBytes(false);
                        stream = new ByteArrayOutputStream();
                        stream.write(bytes);
                    } else {
                        assert pdfStream.getOutputStream() != null : "Error in outputStream";
                        stream = (ByteArrayOutputStream) pdfStream.getOutputStream().getOutputStream();
                    }
                }
            } else {
                // compress
                stream = new ByteArrayOutputStream();
                Deflater deflater = new Deflater(pdfStream.getCompressionLevel());
                DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);
                if (pdfStream instanceof PdfObjectStream) {
                    PdfObjectStream objectStream = (PdfObjectStream) pdfStream;
                    ((ByteArrayOutputStream) objectStream.getIndexStream().getOutputStream()).writeTo(zip);
                    ((ByteArrayOutputStream) objectStream.getOutputStream().getOutputStream()).writeTo(zip);
                } else {
                    if (pdfStream.getOutputStream() == null && pdfStream.getReader() != null) {
                        byte[] bytes = pdfStream.getBytes(false);
                        zip.write(bytes);
                    } else {
                        assert pdfStream.getOutputStream() != null : "Error in outputStream";
                        ((ByteArrayOutputStream) pdfStream.getOutputStream().getOutputStream()).writeTo(zip);
                    }
                }

                zip.close();
                deflater.end();
                if (filter == null) {
                    pdfStream.put(PdfName.Filter, PdfName.FlateDecode);
                } else {
                    PdfArray filters = new PdfArray();
                    filters.add(PdfName.FlateDecode);
                    filters.add(filter);
                    pdfStream.put(PdfName.Filter, filters);
                }
            }
            if (checkEncryption(pdfStream)) {
                ByteArrayOutputStream encodedStream = new ByteArrayOutputStream();
                OutputStreamEncryption ose = crypto.getEncryptionStream(encodedStream);
                stream.writeTo(ose);
                ose.finish();
                stream = encodedStream;
            }
            return stream;
        } catch (IOException ioe) {
            throw new PdfException(PdfException.IoException, ioe);
        }
    }
}
