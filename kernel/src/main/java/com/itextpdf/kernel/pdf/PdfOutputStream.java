/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.source.DeflaterOutputStream;
import com.itextpdf.io.source.OutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.pdf.filters.FlateDecodeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import com.itextpdf.io.util.MessageFormatUtil;

public class PdfOutputStream extends OutputStream<PdfOutputStream> {

    private static final long serialVersionUID = -548180479472231600L;

    private static final byte[] stream = ByteUtils.getIsoBytes("stream\n");
    private static final byte[] endstream = ByteUtils.getIsoBytes("\nendstream");
    private static final byte[] openDict = ByteUtils.getIsoBytes("<<");
    private static final byte[] closeDict = ByteUtils.getIsoBytes(">>");
    private static final byte[] endIndirect = ByteUtils.getIsoBytes(" R");
    private static final byte[] endIndirectWithZeroGenNr = ByteUtils.getIsoBytes(" 0 R");

    // For internal usage only
    private byte[] duplicateContentBuffer = null;

    /**
     * Document associated with PdfOutputStream.
     */
    protected PdfDocument document = null;
    /**
     * Contains the business logic for cryptography.
     */
    protected PdfEncryption crypto;

    /**
     * Create a pdfOutputSteam writing to the passed OutputStream.
     *
     * @param outputStream Outputstream to write to.
     */
    public PdfOutputStream(java.io.OutputStream outputStream) {
        super(outputStream);
    }

    /**
     * Write a PdfObject to the outputstream.
     *
     * @param pdfObject PdfObject to write
     * @return this PdfOutPutStream
     */
    @SuppressWarnings("ConstantConditions")
    public PdfOutputStream write(PdfObject pdfObject) {
        if (pdfObject.checkState(PdfObject.MUST_BE_INDIRECT) && document != null) {
            pdfObject.makeIndirect(document);
            pdfObject = pdfObject.getIndirectReference();
        }
        if (pdfObject.checkState(PdfObject.READ_ONLY)) {
            throw new PdfException(PdfException.CannotWriteObjectAfterItWasReleased);
        }
        switch (pdfObject.getType()) {
            case PdfObject.ARRAY:
                write((PdfArray) pdfObject);
                break;
            case PdfObject.DICTIONARY:
                write((PdfDictionary) pdfObject);
                break;
            case PdfObject.INDIRECT_REFERENCE:
                write((PdfIndirectReference) pdfObject);
                break;
            case PdfObject.NAME:
                write((PdfName) pdfObject);
                break;
            case PdfObject.NULL:
            case PdfObject.BOOLEAN:
                write((PdfPrimitiveObject) pdfObject);
                break;
            case PdfObject.LITERAL:
                write((PdfLiteral) pdfObject);
                break;
            case PdfObject.STRING:
                write((PdfString) pdfObject);
                break;
            case PdfObject.NUMBER:
                write((PdfNumber) pdfObject);
                break;
            case PdfObject.STREAM:
                write((PdfStream) pdfObject);
                break;
        }
        return this;
    }

    /**
     * Writes corresponding amount of bytes from a given long
     *
     * @param bytes a source of bytes, must be >= 0
     * @param size expected amount of bytes
     */
    void write(long bytes, int size) throws IOException {
        assert bytes >= 0;
        while (--size >= 0) {
            write((byte) (bytes >> 8 * size & 0xff));
        }
    }

    /**
     * Writes corresponding amount of bytes from a given int
     *
     * @param bytes a source of bytes, must be >= 0
     * @param size expected amount of bytes
     */
    void write(int bytes, int size) throws IOException {
        //safe convert to long, despite sign.
        write(bytes & 0xFFFFFFFFL, size);
    }

    private void write(PdfArray pdfArray) {
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

    private void write(PdfDictionary pdfDictionary) {
        writeBytes(openDict);
        for (PdfName key : pdfDictionary.keySet()) {
            boolean isAlreadyWriteSpace = false;
            write(key);
            PdfObject value = pdfDictionary.get(key, false);
            if (value == null) {
                Logger logger = LoggerFactory.getLogger(PdfOutputStream.class);
                logger.warn(MessageFormatUtil.format(LogMessageConstant.INVALID_KEY_VALUE_KEY_0_HAS_NULL_VALUE, key));
                value = PdfNull.PDF_NULL;
            }
            if ((value.getType() == PdfObject.NUMBER
                    || value.getType() == PdfObject.LITERAL
                    || value.getType() == PdfObject.BOOLEAN
                    || value.getType() == PdfObject.NULL
                    || value.getType() == PdfObject.INDIRECT_REFERENCE
                    || value.checkState(PdfObject.MUST_BE_INDIRECT))) {
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

    private void write(PdfIndirectReference indirectReference) {
        if (document != null && !indirectReference.getDocument().equals(document)) {
            throw new PdfException(PdfException.PdfIndirectObjectBelongsToOtherPdfDocument);
        }
        if (indirectReference.isFree()) {
            Logger logger = LoggerFactory.getLogger(PdfOutputStream.class);
            logger.error(LogMessageConstant.FLUSHED_OBJECT_CONTAINS_FREE_REFERENCE);
            write(PdfNull.PDF_NULL);
        } else if (indirectReference.getRefersTo() == null) {
            Logger logger = LoggerFactory.getLogger(PdfOutputStream.class);
            logger.error(LogMessageConstant.FLUSHED_OBJECT_CONTAINS_REFERENCE_WHICH_NOT_REFER_TO_ANY_OBJECT);
            write(PdfNull.PDF_NULL);
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

    private void write(PdfPrimitiveObject pdfPrimitive) {
        writeBytes(pdfPrimitive.getInternalContent());
    }

    private void write(PdfLiteral literal) {
        literal.setPosition(getCurrentPos());
        writeBytes(literal.getInternalContent());
    }

    private void write(PdfString pdfString) {
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

    private void write(PdfName name) {
        writeByte('/');
        writeBytes(name.getInternalContent());
    }

    private void write(PdfNumber pdfNumber) {
        if (pdfNumber.hasContent()) {
            writeBytes(pdfNumber.getInternalContent());
        } else if (pdfNumber.isDoubleNumber()) {
            writeDouble(pdfNumber.getValue());
        } else {
            writeInteger(pdfNumber.intValue());
        }
    }

    private boolean isNotMetadataPdfStream(PdfStream pdfStream) {
        return pdfStream.getAsName(PdfName.Type) == null ||
                (pdfStream.getAsName(PdfName.Type) != null && !pdfStream.getAsName(PdfName.Type).equals(PdfName.Metadata));
    }

    private boolean isXRefStream(PdfStream pdfStream) {
        return PdfName.XRef.equals(pdfStream.getAsName(PdfName.Type));
    }

    private void write(PdfStream pdfStream) {
        try {
            boolean userDefinedCompression = pdfStream.getCompressionLevel() != CompressionConstants.UNDEFINED_COMPRESSION;
            if (!userDefinedCompression) {
                int defaultCompressionLevel = document != null ?
                        document.getWriter().getCompressionLevel() :
                        CompressionConstants.DEFAULT_COMPRESSION;
                pdfStream.setCompressionLevel(defaultCompressionLevel);
            }
            boolean toCompress = pdfStream.getCompressionLevel() != CompressionConstants.NO_COMPRESSION;
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
                this.write((PdfDictionary) pdfStream);
                writeBytes(PdfOutputStream.stream);
                long beginStreamContent = getCurrentPos();
                byte[] buf = new byte[4192];
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
                pdfStream.updateLength(length.intValue());
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
                        zip.finish();
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
                pdfStream.updateLength((int) byteArrayStream.size());
                this.write((PdfDictionary) pdfStream);
                writeBytes(PdfOutputStream.stream);
                byteArrayStream.writeTo(this);
                byteArrayStream.close();
                writeBytes(PdfOutputStream.endstream);
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotWriteToPdfStream, e, pdfStream);
        }
    }

    protected boolean checkEncryption(PdfStream pdfStream) {
        if (crypto == null || crypto.isEmbeddedFilesOnly()) {
            return false;
        } else if (isXRefStream(pdfStream)) {
            // The cross-reference stream shall not be encrypted
            return false;
        } else {
            PdfObject filter = pdfStream.get(PdfName.Filter, true);
            if (filter != null) {
                if (PdfName.Crypt.equals(filter)) {
                    return false;
                } else if (filter.getType() == PdfObject.ARRAY) {
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
            if (filter.getType() == PdfObject.NAME) {
                if (PdfName.FlateDecode.equals(filter)) {
                    return true;
                }
            } else if (filter.getType() == PdfObject.ARRAY) {
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
        if (filterObject == null) {
            return bytes;
        }
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
        } else if (decodeParamsObject.getType() == PdfObject.DICTIONARY) {
            decodeParams = (PdfDictionary) decodeParamsObject;
        } else if (decodeParamsObject.getType() == PdfObject.ARRAY) {
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
            if (decodeParamsArray.size() == 1 && decodeParamsArray.get(0).getType() != PdfObject.NULL) {
                decodeParamsObject = decodeParamsArray.get(0);
            } else if (!decodeParamsArray.isEmpty()) {
                decodeParamsObject = decodeParamsArray;
            }
        }

        if (filterObject == null) {
            stream.remove(PdfName.Filter);
        } else {
            stream.put(PdfName.Filter, filterObject);
        }

        if (decodeParamsObject == null) {
            stream.remove(PdfName.DecodeParms);
        } else {
            stream.put(PdfName.DecodeParms, decodeParamsObject);
        }

        return bytes;
    }

    /**
     * This method is invoked while deserialization
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (outputStream == null && duplicateContentBuffer != null) {
            outputStream = new ByteArrayOutputStream();
            write(duplicateContentBuffer);
            duplicateContentBuffer = null;
        }
    }

    /**
     * This method is invoked while serialization
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        java.io.OutputStream tempOutputStream = outputStream;
        if (outputStream instanceof java.io.ByteArrayOutputStream) {
            duplicateContentBuffer = ((java.io.ByteArrayOutputStream) outputStream).toByteArray();
        }
        outputStream = null;
        out.defaultWriteObject();
        outputStream = tempOutputStream;
    }
}
