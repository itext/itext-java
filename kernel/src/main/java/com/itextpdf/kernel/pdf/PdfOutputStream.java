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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.source.DeflaterOutputStream;
import com.itextpdf.io.source.HighPrecisionOutputStream;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.filters.FlateDecodeFilter;
import com.itextpdf.commons.utils.MessageFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class PdfOutputStream extends HighPrecisionOutputStream<PdfOutputStream> {


    private static final byte[] stream = ByteUtils.getIsoBytes("stream\n");
    private static final byte[] endstream = ByteUtils.getIsoBytes("\nendstream");
    private static final byte[] openDict = ByteUtils.getIsoBytes("<<");
    private static final byte[] closeDict = ByteUtils.getIsoBytes(">>");
    private static final byte[] endIndirect = ByteUtils.getIsoBytes(" R");
    private static final byte[] endIndirectWithZeroGenNr = ByteUtils.getIsoBytes(" 0 R");
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfOutputStream.class);

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
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_WRITE_OBJECT_AFTER_IT_WAS_RELEASED);
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
                LOGGER.warn(MessageFormatUtil.format(IoLogMessageConstant.INVALID_KEY_VALUE_KEY_0_HAS_NULL_VALUE, key));
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
            throw new PdfException(KernelExceptionMessageConstant.PDF_INDIRECT_OBJECT_BELONGS_TO_OTHER_PDF_DOCUMENT);
        }
        if (indirectReference.isFree()) {
            LOGGER.error(IoLogMessageConstant.FLUSHED_OBJECT_CONTAINS_FREE_REFERENCE);
            write(PdfNull.PDF_NULL);
        } else if (indirectReference.refersTo == null
                && (indirectReference.checkState(PdfObject.MODIFIED) || indirectReference.getReader() == null
                    || !(indirectReference.getOffset() > 0 || indirectReference.getIndex() >= 0))) {
            LOGGER.error(IoLogMessageConstant.FLUSHED_OBJECT_CONTAINS_REFERENCE_WHICH_NOT_REFER_TO_ANY_OBJECT);
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
                if (crypto != null &&
                        (!crypto.isEmbeddedFilesOnly() || document.doesStreamBelongToEmbeddedFile(pdfStream))) {
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
                    if (toCompress && !containsFlateFilter(pdfStream) && decodeParamsArrayNotFlushed(pdfStream)
                            && (allowCompression || userDefinedCompression)) {
                        // compress
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
                    throw new PdfException(KernelExceptionMessageConstant.IO_EXCEPTION, ioe);
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
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_WRITE_TO_PDF_STREAM, e, pdfStream);
        }
    }

    protected boolean checkEncryption(PdfStream pdfStream) {
        if (crypto == null || (crypto.isEmbeddedFilesOnly() && !document.doesStreamBelongToEmbeddedFile(pdfStream))) {
            return false;
        }
        if (isXRefStream(pdfStream)) {
            // The cross-reference stream shall not be encrypted
            return false;
        }
        PdfObject filter = pdfStream.get(PdfName.Filter, true);
        if (filter == null) {
            return true;
        }
        if (filter.isFlushed()) {
            IndirectFilterUtils.throwFlushedFilterException(pdfStream);
        }
        if (PdfName.Crypt.equals(filter)) {
            return false;
        }
        if (filter.getType() == PdfObject.ARRAY) {
            PdfArray filters = (PdfArray) filter;
            if (filters.isEmpty()) {
                return true;
            }
            if (filters.get(0).isFlushed()) {
                IndirectFilterUtils.throwFlushedFilterException(pdfStream);
            }
            return !PdfName.Crypt.equals(filters.get(0, true));
        }
        return true;
    }

    protected boolean containsFlateFilter(PdfStream pdfStream) {
        PdfObject filter = pdfStream.get(PdfName.Filter);
        if (filter == null) {
            return false;
        }
        if (filter.isFlushed()) {
            IndirectFilterUtils.logFilterWasAlreadyFlushed(LOGGER, pdfStream);
            return true;
        }
        if (filter.getType() != PdfObject.NAME && filter.getType() != PdfObject.ARRAY) {
            throw new PdfException(KernelExceptionMessageConstant.FILTER_IS_NOT_A_NAME_OR_ARRAY);
        }
        if (filter.getType() == PdfObject.NAME) {
            return PdfName.FlateDecode.equals(filter);
        }
        for (PdfObject obj : (PdfArray) filter) {
            if (obj.isFlushed()) {
                IndirectFilterUtils.logFilterWasAlreadyFlushed(LOGGER, pdfStream);
                return true;
            }
        }
        return ((PdfArray) filter).contains(PdfName.FlateDecode);
    }

    protected void updateCompressionFilter(PdfStream pdfStream) {
        PdfObject filter = pdfStream.get(PdfName.Filter);
        if (filter == null) {
            // Remove if any
            pdfStream.remove(PdfName.DecodeParms);
            
            pdfStream.put(PdfName.Filter, PdfName.FlateDecode);
            return;
        }
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
                throw new PdfException(KernelExceptionMessageConstant.THIS_DECODE_PARAMETER_TYPE_IS_NOT_SUPPORTED)
                        .setMessageParams(decodeParms.getClass().toString());
            }
        }
        pdfStream.put(PdfName.Filter, filters);
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
            if (filtersArray.isFlushed()) {
                IndirectFilterUtils.logFilterWasAlreadyFlushed(LOGGER, stream);
                return bytes;
            }
            filterName = filtersArray.getAsName(0);
        } else {
            throw new PdfException(KernelExceptionMessageConstant.FILTER_IS_NOT_A_NAME_OR_ARRAY);
        }

        if (filterName.isFlushed()) {
            IndirectFilterUtils.logFilterWasAlreadyFlushed(LOGGER, stream);
            return bytes;
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
        } else if (decodeParamsObject.isFlushed()) {
            IndirectFilterUtils.logFilterWasAlreadyFlushed(LOGGER, stream);
            return bytes;
        } else if (decodeParamsObject.getType() == PdfObject.DICTIONARY) {
            decodeParams = (PdfDictionary) decodeParamsObject;
        } else if (decodeParamsObject.getType() == PdfObject.ARRAY) {
            decodeParamsArray = (PdfArray) decodeParamsObject;
            decodeParams = decodeParamsArray.getAsDictionary(0);
        } else {
            throw new PdfException(KernelExceptionMessageConstant.THIS_DECODE_PARAMETER_TYPE_IS_NOT_SUPPORTED)
                    .setMessageParams(decodeParamsObject.getClass().toString());
        }

        if (decodeParams != null && (decodeParams.isFlushed() || isFlushed(decodeParams, PdfName.Predictor)
                || isFlushed(decodeParams, PdfName.Columns) || isFlushed(decodeParams, PdfName.Colors) || isFlushed(
                decodeParams, PdfName.BitsPerComponent))) {
            IndirectFilterUtils.logFilterWasAlreadyFlushed(LOGGER, stream);
            return bytes;
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

    private static boolean isFlushed(PdfDictionary dict, PdfName name) {
        PdfObject obj = dict.get(name);
        return obj != null && obj.isFlushed();
    }

    private static boolean decodeParamsArrayNotFlushed(PdfStream pdfStream) {
        PdfArray decodeParams = pdfStream.getAsArray(PdfName.DecodeParms);
        if (decodeParams == null) {
            return true;
        }
        if (decodeParams.isFlushed()) {
            IndirectFilterUtils.logFilterWasAlreadyFlushed(LOGGER, pdfStream);
            return false;
        }
        return true;
    }
}
