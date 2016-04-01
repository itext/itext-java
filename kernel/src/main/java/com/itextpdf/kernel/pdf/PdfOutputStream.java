package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.source.DeflaterOutputStream;
import com.itextpdf.io.source.OutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.pdf.filters.FlateDecodeFilter;

import java.io.IOException;
import java.io.Serializable;
import java.security.cert.Certificate;
import java.util.Map;

public class PdfOutputStream extends OutputStream<PdfOutputStream> implements Serializable{

    private static final long serialVersionUID = -548180479472231600L;

    //TODO review location and use of the constants
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
    public static final int UNDEFINED_COMPRESSION = Integer.MIN_VALUE;
    /**
     * A possible compression level.
     */
    public static final int DEFAULT_COMPRESSION = java.util.zip.Deflater.DEFAULT_COMPRESSION;
    /**
     * A possible compression level.
     */
    public static final int NO_COMPRESSION = java.util.zip.Deflater.NO_COMPRESSION;
    /**
     * A possible compression level.
     */
    public static final int BEST_SPEED = java.util.zip.Deflater.BEST_SPEED;
    /**
     * A possible compression level.
     */
    public static final int BEST_COMPRESSION = java.util.zip.Deflater.BEST_COMPRESSION;

    private static final byte[] stream = ByteUtils.getIsoBytes("stream\n");
    private static final byte[] endstream = ByteUtils.getIsoBytes("\nendstream");
    private static final byte[] openDict = ByteUtils.getIsoBytes("<<");
    private static final byte[] closeDict = ByteUtils.getIsoBytes(">>");
    private static final byte[] endIndirect = ByteUtils.getIsoBytes(" R");
    private static final byte[] endIndirectWithZeroGenNr = ByteUtils.getIsoBytes(" 0 R");

    /**
     * Document associated with PdfOutputStream.
     */
    protected PdfDocument document = null;
    /**
     * Contains the business logic for cryptography.
     */
    protected PdfEncryption crypto;

    /**
     * Do not use this constructor. This is only for internal usage.
     */
    private PdfOutputStream() {
        super();
    }

    public PdfOutputStream(java.io.OutputStream outputStream) {
        super(outputStream);
    }

    public PdfOutputStream write(PdfObject pdfObject) {
        if (pdfObject.checkState(PdfObject.MustBeIndirect) && document != null) {
            pdfObject.makeIndirect(document);
            pdfObject = pdfObject.getIndirectReference();
        }
        if (pdfObject.checkState(PdfObject.ReadOnly)) {
            throw new PdfException(PdfException.CannotWriteObjectAfterItWasReleased);
        }
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
                write((PdfPrimitiveObject) pdfObject);
                break;
            case PdfObject.Literal:
                write((PdfLiteral) pdfObject);
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
     * @throws PdfException if the document is already open
     */
    public void setEncryption(final byte userPassword[], final byte ownerPassword[], final int permissions, final int encryptionType) {
        if (document != null)
            throw new PdfException(PdfException.EncryptionCanOnlyBeAddedBeforeOpeningDocument);
        crypto = new PdfEncryption(userPassword, ownerPassword, permissions, encryptionType, PdfEncryption.generateNewDocumentId());
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
     * @throws PdfException if the document is already open
     */
    public void setEncryption(final Certificate[] certs, final int[] permissions, final int encryptionType) {
        if (document != null)
            throw new PdfException(PdfException.EncryptionCanOnlyBeAddedBeforeOpeningDocument);
        crypto = new PdfEncryption(certs, permissions, encryptionType);
    }

    PdfEncryption getEncryption() {
        return crypto;
    }

    protected void write(PdfArray pdfArray) {
        writeByte('[');
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
        writeByte(']');
    }

    protected void write(PdfDictionary pdfDictionary) {
        writeBytes(openDict);
        for (Map.Entry<PdfName, PdfObject> entry : pdfDictionary.entrySet()) {
            boolean isAlreadyWriteSpace = false;
            write(entry.getKey());
            PdfObject value = entry.getValue();
            if ((value.getType() == PdfObject.Number
                    || value.getType() == PdfObject.Literal
                    || value.getType() == PdfObject.Boolean
                    || value.getType() == PdfObject.Null
                    || value.getType() == PdfObject.IndirectReference
                    || value.checkState(PdfObject.MustBeIndirect))) {
                isAlreadyWriteSpace = true;
                writeSpace();
            }

            PdfIndirectReference indirectReference;
            if ((indirectReference = value.getIndirectReference()) != null) {
                if (!isAlreadyWriteSpace) {
                    writeSpace();
                }
                write(indirectReference);
            } else {
                write(value);
            }
        }
        writeBytes(closeDict);
    }

    protected void write(PdfIndirectReference indirectReference) {
        if (document != null && !indirectReference.getDocument().equals(document)) {
            throw new PdfException(PdfException.PdfInderectObjectBelongToOtherPdfDocument);
        }
        if (indirectReference.getRefersTo() == null) {
            write(PdfNull.PdfNull);
        } else if (indirectReference.getGenNumber() == 0) {
            writeInteger(indirectReference.getObjNumber()).
                    writeBytes(endIndirectWithZeroGenNr);
        } else {
            writeInteger(indirectReference.getObjNumber()).
                    writeSpace().
                    writeInteger(indirectReference.getGenNumber()).
                    writeBytes(endIndirect);
        }
    }

    protected void write(PdfPrimitiveObject pdfPrimitive) {
        writeBytes(pdfPrimitive.getInternalContent());
    }

    protected void write(PdfLiteral literal) {
        literal.setPosition(getCurrentPos());
        writeBytes(literal.getInternalContent());
    }

    protected void write(PdfString pdfString) {
        pdfString.encrypt(crypto);
        if (pdfString.isHexWriting()) {
            writeByte('<');
            writeBytes(pdfString.getInternalContent());
            writeByte('>');
        } else {
            writeByte('(');
            writeBytes(pdfString.getInternalContent());
            writeByte(')');
        }
    }


    protected void write(PdfName name) {
        writeByte('/');
        writeBytes(name.getInternalContent());
    }

    protected void write(PdfNumber pdfNumber) {
        if (pdfNumber.hasContent()) {
            writeBytes(pdfNumber.getInternalContent());
        } else if (pdfNumber.getValueType() == PdfNumber.Int) {
            writeInteger(pdfNumber.getIntValue());
        } else {
            writeDouble(pdfNumber.getValue());
        }
    }

    private boolean isNotMetadataPdfStream(PdfStream pdfStream) {
        return pdfStream.getAsName(PdfName.Type) == null ||
                (pdfStream.getAsName(PdfName.Type) != null && !pdfStream.getAsName(PdfName.Type).equals(PdfName.Metadata));

    }

    protected void write(PdfStream pdfStream) {
        try {
            boolean userDefinedCompression = pdfStream.getCompressionLevel() != UNDEFINED_COMPRESSION;
            if (!userDefinedCompression) {
                int defaultCompressionLevel = document != null ?
                        document.getWriter().getCompressionLevel() :
                        DEFAULT_COMPRESSION;
                pdfStream.setCompressionLevel(defaultCompressionLevel);
            }
            boolean toCompress = pdfStream.getCompressionLevel() != NO_COMPRESSION;
            boolean allowCompression = !pdfStream.containsKey(PdfName.Filter) && isNotMetadataPdfStream(pdfStream);

            if (pdfStream.getInputStream() != null) {
                java.io.OutputStream fout = this;
                DeflaterOutputStream def = null;
                OutputStreamEncryption ose = null;
                if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
                    fout = ose = crypto.getEncryptionStream(fout);
                }
                if (toCompress && (allowCompression || userDefinedCompression)) {
                    updateCompressionFilter(pdfStream);
                    fout = def = new DeflaterOutputStream(fout, pdfStream.getCompressionLevel(), 0x8000);
                }
                write((PdfDictionary) pdfStream);
                writeBytes(PdfOutputStream.stream);
                long beginStreamContent = getCurrentPos();
                byte buf[] = new byte[4192];
                while (true) {
                    int n = pdfStream.getInputStream().read(buf);
                    if (n <= 0)
                        break;
                    fout.write(buf, 0, n);
                }
                if (def != null) {
                    def.finish();
                }
                if (ose != null) {
                    ose.finish();
                }
                PdfNumber length = pdfStream.getAsNumber(PdfName.Length);
                length.setValue((int) (getCurrentPos() - beginStreamContent));
                pdfStream.updateLength(length.getIntValue());
                writeBytes(PdfOutputStream.endstream);
            } else {
                //When document is opened in stamping mode the output stream can be uninitialized.
                //We have to initialize it and write all data from streams input to streams output.
                if (pdfStream.getOutputStream() == null && pdfStream.getIndirectReference().getReader() != null) {
                    // If new specific compression is set for stream,
                    // then compressed stream should be decoded and written with new compression settings
                    byte[] bytes = pdfStream.getIndirectReference().getReader().readStreamBytes(pdfStream, false);
                    if (userDefinedCompression) {
                        bytes = decodeFlateBytes(pdfStream, bytes);
                    }
                    pdfStream.initOutputStream(new ByteArrayOutputStream(bytes.length));
                    pdfStream.getOutputStream().write(bytes);
                }
                assert pdfStream.getOutputStream() != null : "PdfStream lost OutputStream";
                ByteArrayOutputStream byteArrayStream;
                try {
                    if (toCompress && !containsFlateFilter(pdfStream) && (allowCompression || userDefinedCompression)) { // compress
                        updateCompressionFilter(pdfStream);
                        byteArrayStream = new ByteArrayOutputStream();
                        DeflaterOutputStream zip = new DeflaterOutputStream(byteArrayStream, pdfStream.getCompressionLevel());
                        if (pdfStream instanceof PdfObjectStream) {
                            PdfObjectStream objectStream = (PdfObjectStream) pdfStream;
                            ((ByteArrayOutputStream) objectStream.getIndexStream().getOutputStream()).writeTo(zip);
                            ((ByteArrayOutputStream) objectStream.getOutputStream().getOutputStream()).writeTo(zip);
                        } else {
                            assert pdfStream.getOutputStream() != null : "Error in outputStream";
                            ((ByteArrayOutputStream) pdfStream.getOutputStream().getOutputStream()).writeTo(zip);
                        }

                        zip.close();
                    } else {
                        if (pdfStream instanceof PdfObjectStream) {
                            PdfObjectStream objectStream = (PdfObjectStream) pdfStream;
                            byteArrayStream = new ByteArrayOutputStream();
                            ((ByteArrayOutputStream) objectStream.getIndexStream().getOutputStream()).writeTo(byteArrayStream);
                            ((ByteArrayOutputStream) objectStream.getOutputStream().getOutputStream()).writeTo(byteArrayStream);
                        } else {
                            assert pdfStream.getOutputStream() != null : "Error in outputStream";
                            byteArrayStream = (ByteArrayOutputStream) pdfStream.getOutputStream().getOutputStream();
                        }
                    }
                    if (checkEncryption(pdfStream)) {
                        ByteArrayOutputStream encodedStream = new ByteArrayOutputStream();
                        OutputStreamEncryption ose = crypto.getEncryptionStream(encodedStream);
                        byteArrayStream.writeTo(ose);
                        ose.finish();
                        byteArrayStream = encodedStream;
                    }
                } catch (IOException ioe) {
                    throw new PdfException(PdfException.IoException, ioe);
                }
                pdfStream.put(PdfName.Length, new PdfNumber(byteArrayStream.size()));
                pdfStream.updateLength(byteArrayStream.size());
                write((PdfDictionary) pdfStream);
                writeBytes(PdfOutputStream.stream);
                byteArrayStream.writeTo(this);
                writeBytes(PdfOutputStream.endstream);
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotWritePdfStream, e, pdfStream);
        }
    }

    protected boolean checkEncryption(PdfStream pdfStream) {
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

    protected boolean containsFlateFilter(PdfStream pdfStream) {
        PdfObject filter = pdfStream.get(PdfName.Filter);
        if (filter != null) {
            if (filter.getType() == PdfObject.Name) {
                if (PdfName.FlateDecode.equals(filter)) {
                    return true;
                }
            } else if (filter.getType() == PdfObject.Array) {
                if (((PdfArray) filter).contains(PdfName.FlateDecode))
                    return true;
            } else {
                throw new PdfException(PdfException.FilterIsNotANameOrArray);
            }
        }
        return false;
    }

    protected void updateCompressionFilter(PdfStream pdfStream) {
        PdfObject filter = pdfStream.get(PdfName.Filter);
        if (filter == null) {
            pdfStream.put(PdfName.Filter, PdfName.FlateDecode);
        } else {
            PdfArray filters = new PdfArray();
            filters.add(PdfName.FlateDecode);
            if (filter instanceof PdfArray) {
                filters.addAll((PdfArray) filter);
            } else {
                filters.add(filter);
            }
            PdfObject decodeParms = pdfStream.get(PdfName.DecodeParms);
            if (decodeParms != null) {
                if (decodeParms instanceof PdfDictionary) {
                    PdfArray array = new PdfArray();
                    array.add(new PdfNull());
                    array.add(decodeParms);
                    pdfStream.put(PdfName.DecodeParms, array);
                } else if (decodeParms instanceof PdfArray) {
                    ((PdfArray) decodeParms).add(0, new PdfNull());
                } else {
                    throw new PdfException(PdfException.DecodeParameterType1IsNotSupported).setMessageParams(decodeParms.getClass().toString());
                }
            }
            pdfStream.put(PdfName.Filter, filters);
        }
    }

    protected byte[] decodeFlateBytes(PdfStream stream, byte[] bytes) {
        PdfObject filterObject = stream.get(PdfName.Filter);
        if (filterObject == null)
            return bytes;

        // check if flateDecode filter is on top
        PdfName filterName;
        PdfArray filtersArray = null;
        if (filterObject instanceof PdfName) {
            filterName = (PdfName) filterObject;
        } else if (filterObject instanceof PdfArray) {
            filtersArray = (PdfArray) filterObject;
            filterName = filtersArray.getAsName(0);
        } else {
            throw new PdfException(PdfException.FilterIsNotANameOrArray);
        }

        if (!PdfName.FlateDecode.equals(filterName)) {
            return bytes;
        }

        // get decode params if present
        PdfDictionary decodeParams;
        PdfArray decodeParamsArray = null;
        PdfObject decodeParamsObject = stream.get(PdfName.DecodeParms);
        if (decodeParamsObject == null) {
            decodeParams = null;
        } else if (decodeParamsObject.getType() == PdfObject.Dictionary) {
            decodeParams = (PdfDictionary) decodeParamsObject;
        } else if (decodeParamsObject.getType() == PdfObject.Array) {
            decodeParamsArray = (PdfArray) decodeParamsObject;
            decodeParams = decodeParamsArray.getAsDictionary(0);
        } else {
            throw new PdfException(PdfException.DecodeParameterType1IsNotSupported).setMessageParams(decodeParamsObject.getClass().toString());
        }

        // decode
        byte[] res = FlateDecodeFilter.flateDecode(bytes, true);
        if (res == null)
            res = FlateDecodeFilter.flateDecode(bytes, false);
        bytes = FlateDecodeFilter.decodePredictor(res, decodeParams);


        //remove filter and decode params
        filterObject = null;
        if (filtersArray != null) {
            filtersArray.remove(0);
            if (filtersArray.size() == 1) {
                filterObject = filtersArray.get(0);
            } else if (!filtersArray.isEmpty()) {
                filterObject = filtersArray;
            }
        }

        decodeParamsObject = null;
        if (decodeParamsArray != null) {
            decodeParamsArray.remove(0);
            if (decodeParamsArray.size() == 1 && decodeParamsArray.get(0).getType() != PdfObject.Null) {
                decodeParamsObject = decodeParamsArray.get(0);
            } else if (!decodeParamsArray.isEmpty()) {
                decodeParamsObject = decodeParamsArray;
            }
        }

        if (filterObject == null)
            stream.remove(PdfName.Filter);
        else
            stream.put(PdfName.Filter, filterObject);

        if (decodeParamsObject == null)
            stream.remove(PdfName.DecodeParms);
        else
            stream.put(PdfName.DecodeParms, decodeParamsObject);

        return bytes;
    }
}
