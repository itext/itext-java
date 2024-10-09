/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.source.WindowRandomAccessSource;
import com.itextpdf.kernel.crypto.securityhandler.UnsupportedSecurityHandlerException;
import com.itextpdf.kernel.exceptions.InvalidXRefPrevException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.MemoryLimitsAwareException;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.XrefCycledReferencesException;
import com.itextpdf.kernel.pdf.filters.FilterHandlers;
import com.itextpdf.kernel.pdf.filters.IFilterHandler;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a PDF document.
 */
public class PdfReader implements Closeable {

    /**
     * The default {@link StrictnessLevel} to be used.
     */
    public static final StrictnessLevel DEFAULT_STRICTNESS_LEVEL = StrictnessLevel.LENIENT;

    private static final String endstream1 = "endstream";
    private static final String endstream2 = "\nendstream";
    private static final String endstream3 = "\r\nendstream";
    private static final String endstream4 = "\rendstream";
    private static final byte[] endstream = ByteUtils.getIsoBytes("endstream");
    private static final byte[] endobj = ByteUtils.getIsoBytes("endobj");

    protected static boolean correctStreamLength = true;

    private boolean unethicalReading;

    private boolean memorySavingMode;

    private StrictnessLevel strictnessLevel = DEFAULT_STRICTNESS_LEVEL;

    //indicate nearest first Indirect reference object which includes current reading the object, using for PdfString decrypt
    private PdfIndirectReference currentIndirectReference;

    private XrefProcessor xrefProcessor = new XrefProcessor();

    protected PdfTokenizer tokens;
    protected PdfEncryption decrypt;

    // here we store only the pdfVersion that is written in the document's header,
    // however it could differ from the actual pdf version that could be written in document's catalog
    protected PdfVersion headerPdfVersion;
    protected long lastXref;
    protected long eofPos;
    protected PdfDictionary trailer;
    protected PdfDocument pdfDocument;

    protected ReaderProperties properties;

    protected boolean encrypted = false;
    protected boolean rebuiltXref = false;
    protected boolean hybridXref = false;
    protected boolean fixedXref = false;
    protected boolean xrefStm = false;

    private XMPMeta xmpMeta;
    private PdfConformance pdfConformance;

    /**
     * Constructs a new PdfReader.
     *
     * @param byteSource source of bytes for the reader
     * @param properties properties of the created reader
     * @throws IOException if an I/O error occurs
     */
    public PdfReader(IRandomAccessSource byteSource, ReaderProperties properties) throws IOException {
        this(byteSource, properties, false);
    }

    /**
     * Reads and parses a PDF document.
     *
     * @param is         the {@code InputStream} containing the document. If the inputStream is an instance of
     *                   {@link RASInputStream} then the {@link IRandomAccessSource} would be extracted. Otherwise the stream
     *                   is read to the end but is not closed.
     * @param properties properties of the created reader
     *
     * @throws IOException on error
     */
    public PdfReader(InputStream is, ReaderProperties properties) throws IOException {
        this(new RandomAccessSourceFactory().extractOrCreateSource(is), properties, true);
    }

    /**
     * Reads and parses a PDF document.
     *
     * @param file the {@code File} containing the document.
     * @throws IOException           on error
     * @throws FileNotFoundException when the specified File is not found
     */
    public PdfReader(java.io.File file) throws FileNotFoundException, IOException {
        this(file.getAbsolutePath());
    }

    /**
     * Reads and parses a PDF document.
     *
     * @param is the {@code InputStream} containing the document. If the inputStream is an instance of
     *           {@link RASInputStream} then the {@link IRandomAccessSource} would be extracted. Otherwise the stream
     *           is read to the end but is not closed.
     *
     * @throws IOException on error
     */
    public PdfReader(InputStream is) throws IOException {
        this(is, new ReaderProperties());
    }

    /**
     * Reads and parses a PDF document.
     *
     * @param filename   the file name of the document
     * @param properties properties of the created reader
     * @throws IOException on error
     */
    public PdfReader(String filename, ReaderProperties properties) throws IOException {
        this(
                new RandomAccessSourceFactory()
                        .setForceRead(false)
                        .createBestSource(filename),
                properties,
                true
        );
    }

    /**
     * Reads and parses a PDF document.
     *
     * @param filename the file name of the document
     * @throws IOException on error
     */
    public PdfReader(String filename) throws IOException {
        this(filename, new ReaderProperties());

    }

    /**
     * Reads and parses a PDF document.
     *
     * @param file   the file of the document
     * @param properties properties of the created reader
     * @throws IOException on error
     */
    public PdfReader(File file, ReaderProperties properties) throws IOException {
        this(file.getAbsolutePath(), properties);
    }

    PdfReader(IRandomAccessSource byteSource, ReaderProperties properties, boolean closeStream) throws IOException {
        this.properties = properties;
        this.tokens = getOffsetTokeniser(byteSource, closeStream);
    }

    /**
     * Close {@link PdfTokenizer}.
     *
     * @throws IOException on error.
     */
    public void close() throws IOException {
        tokens.close();
    }

    /**
     * The iText is not responsible if you decide to change the
     * value of this parameter.
     *
     * @param unethicalReading true to enable unethicalReading, false to disable it.
     *                         By default unethicalReading is disabled.
     * @return this {@link PdfReader} instance.
     */
    public PdfReader setUnethicalReading(boolean unethicalReading) {
        this.unethicalReading = unethicalReading;
        return this;
    }

    /**
     * Defines if memory saving mode is enabled.
     * <p>
     * By default memory saving mode is disabled for the sake of time–memory trade-off.
     * <p>
     * If memory saving mode is enabled, document processing might slow down, but reading will be less memory demanding.
     *
     * @param memorySavingMode true to enable memory saving mode, false to disable it.
     * @return this {@link PdfReader} instance.
     */
    public PdfReader setMemorySavingMode(boolean memorySavingMode) {
        this.memorySavingMode = memorySavingMode;
        return this;
    }

    /**
     * Get the current {@link StrictnessLevel} of the reader.
     *
     * @return the current {@link StrictnessLevel}
     */
    public StrictnessLevel getStrictnessLevel() {
        return strictnessLevel;
    }

    /**
     * Set the {@link StrictnessLevel} for the reader. If the argument is {@code null}, then
     * the {@link PdfReader#DEFAULT_STRICTNESS_LEVEL} will be used.
     *
     * @param strictnessLevel the {@link StrictnessLevel} to set
     *
     * @return this {@link PdfReader} instance
     */
    public PdfReader setStrictnessLevel(StrictnessLevel strictnessLevel) {
        this.strictnessLevel = strictnessLevel == null ? DEFAULT_STRICTNESS_LEVEL : strictnessLevel;
        return this;
    }

    /**
     * Gets whether {@link #close()} method shall close input stream.
     *
     * @return true, if {@link #close()} method will close input stream,
     * otherwise false.
     */
    public boolean isCloseStream() {
        return tokens.isCloseStream();
    }

    /**
     * Sets whether {@link #close()} method shall close input stream.
     *
     * @param closeStream true, if {@link #close()} method shall close input stream,
     *                    otherwise false.
     */
    public void setCloseStream(boolean closeStream) {
        tokens.setCloseStream(closeStream);
    }

    /**
     * If any exception generated while reading XRef section, PdfReader will try to rebuild it.
     *
     * @return true, if PdfReader rebuilt Cross-Reference section.
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public boolean hasRebuiltXref() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        return rebuiltXref;
    }

    /**
     * Some documents contain hybrid XRef, for more information see "7.5.8.4 Compatibility with Applications
     * That Do Not Support Compressed Reference Streams" in PDF 32000-1:2008 spec.
     *
     * @return true, if the document has hybrid Cross-Reference section.
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public boolean hasHybridXref() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        return hybridXref;
    }

    /**
     * Indicates whether the document has Cross-Reference Streams.
     *
     * @return true, if the document has Cross-Reference Streams.
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public boolean hasXrefStm() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        return xrefStm;
    }

    /**
     * If any exception generated while reading PdfObject, PdfReader will try to fix offsets of all objects.
     * <p>
     * This method's returned value might change over time, because PdfObjects reading
     * can be postponed even up to document closing.
     * @return true, if PdfReader fixed offsets of PdfObjects.
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public boolean hasFixedXref() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        return fixedXref;
    }

    /**
     * Gets position of the last Cross-Reference table.
     *
     * @return -1 if Cross-Reference table has rebuilt, otherwise position of the last Cross-Reference table.
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public long getLastXref() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        return lastXref;
    }

    /**
     * Reads, decrypt and optionally decode stream bytes.
     * Note, this method doesn't store actual bytes in any internal structures.
     *
     * @param stream a {@link PdfStream} stream instance to be read and optionally decoded.
     * @param decode true if to get decoded stream bytes, false if to leave it originally encoded.
     * @return byte[] array.
     * @throws IOException on error.
     */
    public byte[] readStreamBytes(PdfStream stream, boolean decode) throws IOException {
        byte[] b = readStreamBytesRaw(stream);
        if (decode && b != null) {
            return decodeBytes(b, stream);
        } else {
            return b;
        }
    }

    /**
     * Reads and decrypt stream bytes.
     * Note, this method doesn't store actual bytes in any internal structures.
     *
     * @param stream a {@link PdfStream} stream instance to be read
     * @return byte[] array.
     * @throws IOException on error.
     */
    public byte[] readStreamBytesRaw(PdfStream stream) throws IOException {
        if (stream == null) {
            throw new PdfException(KernelExceptionMessageConstant.UNABLE_TO_READ_STREAM_BYTES);
        }

        PdfName type = stream.getAsName(PdfName.Type);
        if (!PdfName.XRef.equals(type) && !PdfName.ObjStm.equals(type)) {
            checkPdfStreamLength(stream);
        }
        long offset = stream.getOffset();
        if (offset <= 0)
            return null;
        int length = stream.getLength();
        if (length <= 0)
            return new byte[0];
        RandomAccessFileOrArray file = tokens.getSafeFile();
        byte[] bytes = null;
        try {
            file.seek(offset);
            bytes = new byte[length];
            file.readFully(bytes);
            boolean embeddedStream = pdfDocument.doesStreamBelongToEmbeddedFile(stream);
            if (decrypt != null && (!decrypt.isEmbeddedFilesOnly() || embeddedStream)) {
                PdfObject filter = stream.get(PdfName.Filter, true);
                boolean skip = false;
                if (filter != null) {
                    if (filter.isFlushed()) {
                        IndirectFilterUtils.throwFlushedFilterException(stream);
                    }
                    if (PdfName.Crypt.equals(filter)) {
                        skip = true;
                    } else if (filter.getType() == PdfObject.ARRAY) {
                        PdfArray filters = (PdfArray) filter;
                        for (int k = 0; k < filters.size(); k++) {
                            if (filters.get(k).isFlushed()) {
                                IndirectFilterUtils.throwFlushedFilterException(stream);
                            }
                            if (!filters.isEmpty() && PdfName.Crypt.equals(filters.get(k, true))) {
                                skip = true;
                                break;
                            }
                        }
                    }
                    filter.release();
                }
                if (!skip) {
                    decrypt.setHashKeyForNextObject(stream.getIndirectReference().getObjNumber(), stream.getIndirectReference().getGenNumber());
                    bytes = decrypt.decryptByteArray(bytes);
                }
            }
        } finally {
            try {
                file.close();
            } catch (Exception e) {
                // ignored
            }
        }
        return bytes;
    }

    /**
     * Reads, decrypts and optionally decodes stream bytes into {@link ByteArrayInputStream}.
     * User is responsible for closing returned stream.
     *
     * @param stream a {@link PdfStream} stream instance to be read
     * @param decode true if to get decoded stream, false if to leave it originally encoded.
     * @return InputStream or {@code null} if reading was failed.
     * @throws IOException on error.
     */
    public InputStream readStream(PdfStream stream, boolean decode) throws IOException {
        byte[] bytes = readStreamBytes(stream, decode);
        return bytes != null ? new ByteArrayInputStream(bytes) : null;
    }

    /**
     * Decode bytes applying the filters specified in the provided dictionary using default filter handlers.
     *
     * @param b                the bytes to decode
     * @param streamDictionary the dictionary that contains filter information
     * @return the decoded bytes
     * @throws PdfException if there are any problems decoding the bytes
     */
    public static byte[] decodeBytes(byte[] b, PdfDictionary streamDictionary) {
        return decodeBytes(b, streamDictionary, FilterHandlers.getDefaultFilterHandlers());
    }

    /**
     * Decode a byte[] applying the filters specified in the provided dictionary using the provided filter handlers.
     *
     * @param b                the bytes to decode
     * @param streamDictionary the dictionary that contains filter information
     * @param filterHandlers   the map used to look up a handler for each type of filter
     * @return the decoded bytes
     * @throws PdfException if there are any problems decoding the bytes
     */
    public static byte[] decodeBytes(byte[] b, PdfDictionary streamDictionary, Map<PdfName, IFilterHandler> filterHandlers) {
        if (b == null) {
            return null;
        }
        PdfObject filter = streamDictionary.get(PdfName.Filter);
        PdfArray filters = new PdfArray();
        if (filter != null) {
            if (filter.getType() == PdfObject.NAME) {
                filters.add(filter);
            } else if (filter.getType() == PdfObject.ARRAY) {
                filters = ((PdfArray) filter);
            }
        }

        MemoryLimitsAwareHandler memoryLimitsAwareHandler = null;
        if (null != streamDictionary.getIndirectReference()) {
            memoryLimitsAwareHandler = streamDictionary.getIndirectReference().getDocument().memoryLimitsAwareHandler;
        }

        final boolean memoryLimitsAwarenessRequired = null != memoryLimitsAwareHandler &&
                memoryLimitsAwareHandler.isMemoryLimitsAwarenessRequiredOnDecompression(filters);

        if(memoryLimitsAwarenessRequired) {
            memoryLimitsAwareHandler.beginDecompressedPdfStreamProcessing();
        }

        PdfArray dp = new PdfArray();
        PdfObject dpo = streamDictionary.get(PdfName.DecodeParms);
        if (dpo == null || (dpo.getType() != PdfObject.DICTIONARY && dpo.getType() != PdfObject.ARRAY)) {
            if (dpo != null) dpo.release();
            dpo = streamDictionary.get(PdfName.DP);
        }
        if (dpo != null) {
            if (dpo.getType() == PdfObject.DICTIONARY) {
                dp.add(dpo);
            } else if (dpo.getType() == PdfObject.ARRAY) {
                dp = ((PdfArray) dpo);
            }
            dpo.release();
        }
        for (int j = 0; j < filters.size(); ++j) {
            PdfName filterName = (PdfName) filters.get(j);
            IFilterHandler filterHandler = filterHandlers.get(filterName);
            if (filterHandler == null)
                throw new PdfException(KernelExceptionMessageConstant.THIS_FILTER_IS_NOT_SUPPORTED)
                        .setMessageParams(filterName);

            PdfDictionary decodeParams;
            if (j < dp.size()) {
                PdfObject dpEntry = dp.get(j, true);
                if (dpEntry == null || dpEntry.getType() == PdfObject.NULL) {
                    decodeParams = null;
                } else if (dpEntry.getType() == PdfObject.DICTIONARY) {
                    decodeParams = (PdfDictionary) dpEntry;
                } else {
                    throw new PdfException(KernelExceptionMessageConstant.THIS_DECODE_PARAMETER_TYPE_IS_NOT_SUPPORTED)
                            .setMessageParams(dpEntry.getClass().toString());
                }
            } else {
                decodeParams = null;
            }
            b = filterHandler.decode(b, filterName, decodeParams, streamDictionary);
            if (memoryLimitsAwarenessRequired) {
                memoryLimitsAwareHandler.considerBytesOccupiedByDecompressedPdfStream(b.length);
            }
        }
        if (memoryLimitsAwarenessRequired) {
            memoryLimitsAwareHandler.endDecompressedPdfStreamProcessing();
        }
        return b;
    }

    /**
     * Gets a new file instance of the original PDF
     * document.
     *
     * @return a new file instance of the original PDF document
     */
    public RandomAccessFileOrArray getSafeFile() {
        return tokens.getSafeFile();
    }

    /**
     * Provides the size of the opened file.
     *
     * @return The size of the opened file.
     */
    public long getFileLength() {
        return tokens.getSafeFile().length();
    }

    /**
     * Checks if the document was opened with the owner password so that the end application
     * can decide what level of access restrictions to apply. If the document is not encrypted
     * it will return {@code true}.
     *
     * @return {@code true} if the document was opened with the owner password or if it's not encrypted,
     * {@code false} if the document was opened with the user password.
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public boolean isOpenedWithFullPermission() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        return !encrypted || decrypt.isOpenedWithFullPermission() || unethicalReading;
    }

    /**
     * Gets the encryption permissions. It can be used directly in
     * {@link WriterProperties#setStandardEncryption(byte[], byte[], int, int)}.
     * See ISO 32000-1, Table 22 for more details.
     *
     * @return the encryption permissions.
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public int getPermissions() {

        /* !pdfDocument.getXref().isReadingCompleted() can be used for encryption properties as well,
         * because decrypt object is initialized in private readDecryptObj method which is called in our code
         * in the next line after the setting isReadingCompleted line. This means that there's no way for users
         * when this method would work incorrectly right now.
         */
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        int perm = 0;
        if (encrypted && decrypt.getPermissions() != null) {
            perm = decrypt.getPermissions().intValue();
        }
        return perm;
    }

    /**
     * Gets encryption algorithm and access permissions.
     *
     * @return {@code int} value corresponding to a certain type of encryption.
     * @see EncryptionConstants
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public int getCryptoMode() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        if (decrypt == null)
            return -1;
        else
            return decrypt.getCryptoMode();
    }

    /**
     * Gets the declared PDF conformance of the source document that is being read.
     * Note that this information is provided via XMP metadata and is not verified by iText.
     * Conformance is lazy initialized.
     * It will be initialized during the first call of this method.
     *
     * @return conformance of the source document
     */
    public PdfConformance getPdfConformance() {
        if (pdfConformance == null) {
            if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
                throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
            }

            try {
                if (xmpMeta == null && pdfDocument.getXmpMetadata() != null) {
                    xmpMeta = pdfDocument.getXmpMetadata();
                }
                pdfConformance = PdfConformance.getConformance(xmpMeta);
            } catch (XMPException ignored) {
            }
        }

        return pdfConformance;
    }

    /**
     * Computes user password if standard encryption handler is used with Standard40, Standard128 or AES128 encryption algorithm.
     *
     * @return user password, or null if not a standard encryption handler was used or if ownerPasswordUsed wasn't use to open the document.
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public byte[] computeUserPassword() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        if (!encrypted || !decrypt.isOpenedWithFullPermission()) {
            return null;
        }

        return decrypt.computeUserPassword(properties.password);
    }

    /**
     * Gets original file ID, the first element in {@link PdfName#ID} key of trailer.
     * If the size of ID array does not equal 2, an empty array will be returned.
     * <p>
     * The returned value reflects the value that was written in opened document. If document is modified,
     * the ultimate document id can be retrieved from {@link PdfDocument#getOriginalDocumentId()}.
     *
     * @return byte array represents original file ID.
     * @see PdfDocument#getOriginalDocumentId()
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public byte[] getOriginalFileId() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        PdfArray id = trailer.getAsArray(PdfName.ID);
        if (id != null && id.size() == 2) {
            return ByteUtils.getIsoBytes(id.getAsString(0).getValue());
        } else {
            return new byte[0];
        }
    }

    /**
     * Gets modified file ID, the second element in {@link PdfName#ID} key of trailer.
     * If the size of ID array does not equal 2, an empty array will be returned.
     * <p>
     * The returned value reflects the value that was written in opened document. If document is modified,
     * the ultimate document id can be retrieved from {@link PdfDocument#getModifiedDocumentId()}.
     *
     * @return byte array represents modified file ID.
     * @see PdfDocument#getModifiedDocumentId()
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public byte[] getModifiedFileId() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        PdfArray id = trailer.getAsArray(PdfName.ID);
        if (id != null && id.size() == 2) {
            return ByteUtils.getIsoBytes(id.getAsString(1).getValue());
        } else {
            return new byte[0];
        }
    }

    /**
     * Checks if the {@link PdfDocument} read with this {@link PdfReader} is encrypted.
     *
     * @return {@code true} is the document is encrypted, otherwise {@code false}.
     * @throws PdfException if the method has been invoked before the PDF document was read.
     */
    public boolean isEncrypted() {
        if (pdfDocument == null || !pdfDocument.getXref().isReadingCompleted()) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET);
        }

        return encrypted;
    }

    /**
     * Gets a copy of {@link ReaderProperties} used to create this instance of {@link PdfReader}.
     *
     * @return a copy of {@link ReaderProperties} used to create this instance of {@link PdfReader}
     */
    public ReaderProperties getPropertiesCopy() {
        return new ReaderProperties(properties);
    }

    /**
     * Parses the entire PDF
     *
     * @throws IOException if an I/O error occurs.
     */
    protected void readPdf() throws IOException {
        String version = tokens.checkPdfHeader();
        try {
            this.headerPdfVersion = PdfVersion.fromString(version);
        } catch (IllegalArgumentException exc) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_VERSION_IS_NOT_VALID, version);
        }
        try {
            readXref();
        } catch (XrefCycledReferencesException | MemoryLimitsAwareException | InvalidXRefPrevException ex) {
            // Throws an exception when xref stream has cycled references(due to lack of opportunity to fix such an
            // issue) or xref tables have cycled references and PdfReader.StrictnessLevel set to CONSERVATIVE.
            // Also throw an exception when xref structure size exceeds jvm memory limit.
            throw ex;
        } catch (RuntimeException ex) {
            if (StrictnessLevel.CONSERVATIVE.isStricter(this.getStrictnessLevel())) {
                logXrefException(ex);
                rebuildXref();
            } else {
                throw ex;
            }
        }
        pdfDocument.getXref().markReadingCompleted();
        readDecryptObj();
    }

    protected void readObjectStream(PdfStream objectStream) throws IOException {
        if (objectStream == null) {
            throw new PdfException(KernelExceptionMessageConstant.UNABLE_TO_READ_OBJECT_STREAM);
        }

        int objectStreamNumber = objectStream.getIndirectReference().getObjNumber();
        int first = objectStream.getAsNumber(PdfName.First).intValue();
        int n = objectStream.getAsNumber(PdfName.N).intValue();
        byte[] bytes = readStreamBytes(objectStream, true);
        PdfTokenizer saveTokens = tokens;
        try {
            tokens = new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(bytes)));
            int[] address = new int[n];
            int[] objNumber = new int[n];
            boolean ok = true;
            for (int k = 0; k < n; ++k) {
                ok = tokens.nextToken();
                if (!ok)
                    break;
                if (tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
                    ok = false;
                    break;
                }
                objNumber[k] = tokens.getIntValue();
                ok = tokens.nextToken();
                if (!ok)
                    break;
                if (tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
                    ok = false;
                    break;
                }
                address[k] = tokens.getIntValue() + first;
            }
            if (!ok)
                throw new PdfException(KernelExceptionMessageConstant.ERROR_WHILE_READING_OBJECT_STREAM);
            for (int k = 0; k < n; ++k) {
                tokens.seek(address[k]);
                tokens.nextToken();
                PdfObject obj;
                PdfIndirectReference reference = pdfDocument.getXref().get(objNumber[k]);
                if (reference.refersTo != null || reference.getObjStreamNumber() != objectStreamNumber) {
                    // We skip reading of objects stream's element k if either it is already available in xref
                    // or if corresponding indirect object reference points to a different object stream.
                    // The first check prevents from re-initializing objects which are already read. One of the cases
                    // when this can happen is that some other object from this objects stream was released and requested
                    // to be re-read.
                    // Second check ensures that object has no incremental updates and is not freed in append mode.

                    continue;
                }
                if (tokens.getTokenType() == PdfTokenizer.TokenType.Number) {
                    // This ensure that we don't even try to read as indirect reference token (two numbers and "R")
                    // which are forbidden in object streams.
                    obj = new PdfNumber(tokens.getByteContent());
                } else {
                    tokens.seek(address[k]);
                    obj = readObject(false, true);
                }
                reference.setRefersTo(obj);
                obj.setIndirectReference(reference);
            }
            objectStream.getIndirectReference().setState(PdfObject.ORIGINAL_OBJECT_STREAM);
        } finally {
            tokens = saveTokens;
        }
    }

    protected PdfObject readObject(PdfIndirectReference reference) {
        return readObject(reference, true);
    }

    protected PdfObject readObject(boolean readAsDirect) throws IOException {
        return readObject(readAsDirect, false);
    }

    protected PdfObject readReference(boolean readAsDirect) {
        int num = tokens.getObjNr();
        if (num < 0) {
            return createPdfNullInstance(readAsDirect);
        }
        PdfXrefTable table = pdfDocument.getXref();
        PdfIndirectReference reference = table.get(num);
        if (reference != null) {
            if (reference.isFree()) {
                Logger logger = LoggerFactory.getLogger(PdfReader.class);
                logger.warn(MessageFormatUtil.format(IoLogMessageConstant.INVALID_INDIRECT_REFERENCE, tokens.getObjNr(),
                        tokens.getGenNr()));
                return createPdfNullInstance(readAsDirect);
            }
            if (reference.getGenNumber() != tokens.getGenNr()) {
                if (fixedXref) {
                    Logger logger = LoggerFactory.getLogger(PdfReader.class);
                    logger.warn(
                            MessageFormatUtil.format(IoLogMessageConstant.INVALID_INDIRECT_REFERENCE, tokens.getObjNr(),
                                    tokens.getGenNr()));
                    return createPdfNullInstance(readAsDirect);
                } else {
                    throw new PdfException(MessageFormatUtil.format(
                            KernelExceptionMessageConstant.INVALID_INDIRECT_REFERENCE
                            , reference.getObjNumber(), reference.getGenNumber()), reference);
                }
            }
        } else {
            if (table.isReadingCompleted()) {
                Logger logger = LoggerFactory.getLogger(PdfReader.class);
                logger.warn(MessageFormatUtil.format(IoLogMessageConstant.INVALID_INDIRECT_REFERENCE, tokens.getObjNr(),
                        tokens.getGenNr()));
                return createPdfNullInstance(readAsDirect);
            } else {
                reference = table.add((PdfIndirectReference) new PdfIndirectReference(pdfDocument,
                        num, tokens.getGenNr(), 0).setState(PdfObject.READING));
            }
        }
        return reference;
    }

    protected PdfObject readObject(boolean readAsDirect, boolean objStm) throws IOException {
        tokens.nextValidToken();
        PdfTokenizer.TokenType type = tokens.getTokenType();
        switch (type) {
            case StartDic: {
                PdfDictionary dict = readDictionary(objStm);
                long pos = tokens.getPosition();
                // be careful in the trailer. May not be a "next" token.
                boolean hasNext;
                do {
                    hasNext = tokens.nextToken();
                } while (hasNext && tokens.getTokenType() == PdfTokenizer.TokenType.Comment);

                if (hasNext && tokens.tokenValueEqualsTo(PdfTokenizer.Stream)) {
                    //skip whitespaces
                    int ch;
                    do {
                        ch = tokens.read();
                    } while (ch == 32 || ch == 9 || ch == 0 || ch == 12);
                    if (ch != '\n') {
                        ch = tokens.read();
                    }
                    if (ch != '\n') {
                        tokens.backOnePosition(ch);
                    }
                    PdfStream pdfStream = new PdfStream(tokens.getPosition(), dict);
                    tokens.seek(pdfStream.getOffset() + pdfStream.getLength());
                    return pdfStream;
                } else {
                    tokens.seek(pos);
                    return dict;
                }
            }
            case StartArray:
                return readArray(objStm);
            case Number:
                return new PdfNumber(tokens.getByteContent());
            case String: {
                PdfString pdfString = new PdfString(tokens.getByteContent(), tokens.isHexString());
                if (encrypted && !decrypt.isEmbeddedFilesOnly() && !objStm) {
                    pdfString.setDecryption(currentIndirectReference.getObjNumber(), currentIndirectReference.getGenNumber(), decrypt);
                }
                return pdfString;
            }
            case Name:
                return readPdfName(readAsDirect);
            case Ref:
                return readReference(readAsDirect);
            case EndOfFile:
                throw new PdfException(KernelExceptionMessageConstant.UNEXPECTED_END_OF_FILE);
            default:
                if (tokens.tokenValueEqualsTo(PdfTokenizer.Null)) {
                    return createPdfNullInstance(readAsDirect);
                } else if (tokens.tokenValueEqualsTo(PdfTokenizer.True)) {
                    if (readAsDirect) {
                        return PdfBoolean.TRUE;
                    } else {
                        return new PdfBoolean(true);
                    }
                } else if (tokens.tokenValueEqualsTo(PdfTokenizer.False)) {
                    if (readAsDirect) {
                        return PdfBoolean.FALSE;
                    } else {
                        return new PdfBoolean(false);
                    }
                }
                return null;
        }
    }

    protected PdfName readPdfName(boolean readAsDirect) {
        if (readAsDirect) {
            PdfName cachedName = PdfName.staticNames.get(tokens.getStringValue());
            if (cachedName != null)
                return cachedName;
        }
        // an indirect name (how odd...), or a non-standard one
        return new PdfName(tokens.getByteContent());
    }

    protected PdfDictionary readDictionary(boolean objStm) throws IOException {
        PdfDictionary dic = new PdfDictionary();
        while (true) {
            tokens.nextValidToken();
            if (tokens.getTokenType() == PdfTokenizer.TokenType.EndDic) {
                break;
            }
            if (tokens.getTokenType() != PdfTokenizer.TokenType.Name) {
                tokens.throwError(
                        KernelExceptionMessageConstant.THIS_DICTIONARY_KEY_IS_NOT_A_NAME, tokens.getStringValue());
            }
            PdfName name = readPdfName(true);
            PdfObject obj = readObject(true, objStm);
            if (obj == null) {
                if (tokens.getTokenType() == PdfTokenizer.TokenType.EndDic)
                    tokens.throwError(MessageFormatUtil.
                            format(KernelExceptionMessageConstant.UNEXPECTED_TOKEN, ">>"));
                if (tokens.getTokenType() == PdfTokenizer.TokenType.EndArray)
                    tokens.throwError(MessageFormatUtil.
                            format(KernelExceptionMessageConstant.UNEXPECTED_TOKEN, "]"));
            }
            dic.put(name, obj);
        }
        return dic;
    }

    protected PdfArray readArray(boolean objStm) throws IOException {
        PdfArray array = new PdfArray();
        while (true) {
            PdfObject obj = readObject(true, objStm);
            if (obj == null) {
                if (tokens.getTokenType() != PdfTokenizer.TokenType.EndArray) {
                    processArrayReadError();
                }
                break;
            }
            array.add(obj);
        }
        return array;
    }

    protected void readXref() throws IOException {
        tokens.seek(tokens.getStartxref());
        tokens.nextToken();
        if (!tokens.tokenValueEqualsTo(PdfTokenizer.Startxref)) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_STARTXREF_NOT_FOUND, tokens);
        }
        tokens.nextToken();
        if (tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_STARTXREF_IS_NOT_FOLLOWED_BY_A_NUMBER, tokens);
        }
        long startxref = tokens.getLongValue();
        lastXref = startxref;
        eofPos = tokens.getPosition();
        try {
            if (readXrefStream(startxref)) {
                xrefStm = true;
                return;
            }
        } catch (XrefCycledReferencesException
                 | MemoryLimitsAwareException
                 | InvalidXRefPrevException exceptionWhileReadingXrefStream) {
            throw exceptionWhileReadingXrefStream;
        } catch (Exception e) {
            // Do nothing.
        }
        // clear xref because of possible issues at reading xref stream.
        pdfDocument.getXref().clear();

        tokens.seek(startxref);
        trailer = readXrefSection();

        //  Prev key - integer value.
        //  (Present only if the file has more than one cross-reference section; shall be an indirect reference).
        // The byte offset in the decoded stream from the beginning of the file
        // to the beginning of the previous cross-reference section.
        PdfDictionary trailer2 = trailer;
        final Set<Long> alreadyVisitedXrefTables = new HashSet<>();
        while (true) {
            alreadyVisitedXrefTables.add(startxref);
            PdfNumber prev = getXrefPrev(trailer2.get(PdfName.Prev, false));
            if (prev == null) {
                break;
            }
            long prevXrefOffset = prev.longValue();
            if (alreadyVisitedXrefTables.contains(prevXrefOffset)) {
                if (StrictnessLevel.CONSERVATIVE.isStricter(this.getStrictnessLevel())) {
                    // Throw the exception to rebuild xref table, it'll be caught in method above.
                    throw new PdfException(KernelExceptionMessageConstant.
                            TRAILER_PREV_ENTRY_POINTS_TO_ITS_OWN_CROSS_REFERENCE_SECTION);
                } else {
                    throw new XrefCycledReferencesException(
                            KernelExceptionMessageConstant.XREF_TABLE_HAS_CYCLED_REFERENCES);
                }
            }
            startxref = prevXrefOffset;
            tokens.seek(startxref);
            trailer2 = readXrefSection();
        }

        Integer xrefSize = trailer.getAsInt(PdfName.Size);
        if (xrefSize == null) {
            throw new PdfException(KernelExceptionMessageConstant.INVALID_XREF_TABLE);
        }
    }

    protected PdfDictionary readXrefSection() throws IOException {
        tokens.nextValidToken();
        if (!tokens.tokenValueEqualsTo(PdfTokenizer.Xref))
            tokens.throwError(KernelExceptionMessageConstant.XREF_SUBSECTION_NOT_FOUND);
        PdfXrefTable xref = pdfDocument.getXref();
        while (true) {
            tokens.nextValidToken();
            if (tokens.tokenValueEqualsTo(PdfTokenizer.Trailer)) {
                break;
            }
            if (tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
                tokens.throwError(
                        KernelExceptionMessageConstant.OBJECT_NUMBER_OF_THE_FIRST_OBJECT_IN_THIS_XREF_SUBSECTION_NOT_FOUND);
            }
            int start = tokens.getIntValue();
            tokens.nextValidToken();
            if (tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
                tokens.throwError(KernelExceptionMessageConstant.NUMBER_OF_ENTRIES_IN_THIS_XREF_SUBSECTION_NOT_FOUND);
            }
            int end = tokens.getIntValue() + start;
            for (int num = start; num < end; num++) {
                tokens.nextValidToken();
                long pos = tokens.getLongValue();
                tokens.nextValidToken();
                int gen = tokens.getIntValue();
                tokens.nextValidToken();
                if (pos == 0L && gen == 65535 && num == 1 && start != 0) {
                    // Very rarely can an XREF have an incorrect start number. (SUP-1557)
                    // e.g.
                    // xref
                    // 1 13
                    // 0000000000 65535 f
                    // 0000000009 00000 n
                    // 0000215136 00000 n
                    // [...]
                    // Because of how iText reads (and initializes) the XREF, this will lead to the XREF having two 0000 65535 entries.
                    // This throws off the parsing and other operations you'd like to perform.
                    // To fix this we reset our index and decrease the limit when we've encountered the magic entry at position 1.
                    num = 0;
                    end--;
                    continue;
                }
                PdfIndirectReference reference = xref.get(num);
                boolean refReadingState = reference != null && reference.checkState(PdfObject.READING) && reference.getGenNumber() == gen;
                // for references that are added by xref table itself (like 0 entry)
                boolean refFirstEncountered = reference == null
                        || !refReadingState && reference.getDocument() == null;

                if (refFirstEncountered) {
                    reference = new PdfIndirectReference(pdfDocument, num, gen, pos);
                } else if (refReadingState) {
                    reference.setOffset(pos);
                    reference.clearState(PdfObject.READING);
                } else {
                    continue;
                }

                if (tokens.tokenValueEqualsTo(PdfTokenizer.N)) {
                    if (pos == 0) {
                        tokens.throwError(
                                KernelExceptionMessageConstant.FILE_POSITION_0_CROSS_REFERENCE_ENTRY_IN_THIS_XREF_SUBSECTION);
                    }
                } else if (tokens.tokenValueEqualsTo(PdfTokenizer.F)) {
                    if (refFirstEncountered) {
                        reference.setState(PdfObject.FREE);
                    }
                } else {
                    tokens.throwError(
                            KernelExceptionMessageConstant.INVALID_CROSS_REFERENCE_ENTRY_IN_THIS_XREF_SUBSECTION);
                }

                if (refFirstEncountered) {
                    xref.add(reference);
                }
            }
        }
        processXref(xref);
        PdfDictionary trailer = (PdfDictionary) readObject(false);
        PdfObject xrs = trailer.get(PdfName.XRefStm);
        if (xrs != null && xrs.getType() == PdfObject.NUMBER) {
            int loc = ((PdfNumber) xrs).intValue();
            try {
                readXrefStream(loc);
                xrefStm = true;
                hybridXref = true;
            } catch (IOException e) {
                xref.clear();
                throw e;
            }
        }
        return trailer;
    }

    protected boolean readXrefStream(long ptr) throws IOException {
        final Set<Long> alreadyVisitedXrefStreams = new HashSet<>();
        while (ptr != -1) {
            tokens.seek(ptr);
            if (!tokens.nextToken()) {
                return false;
            }
            if (tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
                return false;
            }
            if (!tokens.nextToken() || tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
                return false;
            }
            if (!tokens.nextToken() || !tokens.tokenValueEqualsTo(PdfTokenizer.Obj)) {
                return false;
            }
            alreadyVisitedXrefStreams.add(ptr);
            PdfXrefTable xref = pdfDocument.getXref();
            PdfObject object = readObject(false);
            PdfStream xrefStream;
            if (object.getType() == PdfObject.STREAM) {
                xrefStream = (PdfStream) object;
                if (!PdfName.XRef.equals(xrefStream.get(PdfName.Type))) {
                    return false;
                }
            } else {
                return false;
            }
            if (trailer == null) {
                trailer = new PdfDictionary();
                trailer.putAll(xrefStream);
                trailer.remove(PdfName.DecodeParms);
                trailer.remove(PdfName.Filter);
                trailer.remove(PdfName.Prev);
                trailer.remove(PdfName.Length);
            }

            int size = ((PdfNumber) xrefStream.get(PdfName.Size)).intValue();
            PdfArray index;
            PdfObject obj = xrefStream.get(PdfName.Index);
            if (obj == null) {
                index = new PdfArray();
                index.add(new PdfNumber(0));
                index.add(new PdfNumber(size));
            } else {
                index = (PdfArray) obj;
            }
            PdfArray w = xrefStream.getAsArray(PdfName.W);
            long prev = -1;
            obj = getXrefPrev(xrefStream.get(PdfName.Prev, false));
            if (obj != null)
                prev = ((PdfNumber) obj).longValue();
            xref.setCapacity(size);
            byte[] b = readStreamBytes(xrefStream, true);
            int bptr = 0;
            int[] wc = new int[3];
            for (int k = 0; k < 3; ++k) {
                wc[k] = w.getAsNumber(k).intValue();
            }
            for (int idx = 0; idx < index.size(); idx += 2) {
                int start = index.getAsNumber(idx).intValue();
                int length = index.getAsNumber(idx + 1).intValue();
                xref.setCapacity(start + length);
                while (length-- > 0) {
                    int type = 1;
                    if (wc[0] > 0) {
                        type = 0;
                        for (int k = 0; k < wc[0]; ++k) {
                            type = (type << 8) + (b[bptr++] & 0xff);
                        }
                    }
                    long field2 = 0;
                    for (int k = 0; k < wc[1]; ++k) {
                        field2 = (field2 << 8) + (b[bptr++] & 0xff);
                    }
                    int field3 = 0;
                    for (int k = 0; k < wc[2]; ++k) {
                        field3 = (field3 << 8) + (b[bptr++] & 0xff);
                    }
                    int base = start;
                    PdfIndirectReference newReference;
                    switch (type) {
                        case 0:
                            newReference = (PdfIndirectReference) new PdfIndirectReference(pdfDocument, base, field3, field2).setState(PdfObject.FREE);
                            break;
                        case 1:
                            newReference = new PdfIndirectReference(pdfDocument, base, field3, field2);
                            break;
                        case 2:
                            newReference = new PdfIndirectReference(pdfDocument, base, 0, field3);
                            newReference.setObjStreamNumber((int) field2);
                            break;
                        default:
                            throw new PdfException(KernelExceptionMessageConstant.INVALID_XREF_STREAM);
                    }

                    PdfIndirectReference reference = xref.get(base);
                    boolean refReadingState = reference != null && reference.checkState(PdfObject.READING) && reference.getGenNumber() == newReference.getGenNumber();
                    // for references that are added by xref table itself (like 0 entry)
                    boolean refFirstEncountered = reference == null
                            || !refReadingState && reference.getDocument() == null;

                    if (refFirstEncountered) {
                        xref.add(newReference);
                    } else if (refReadingState) {
                        reference.setOffset(newReference.getOffset());
                        reference.setObjStreamNumber(newReference.getObjStreamNumber());
                        reference.clearState(PdfObject.READING);
                    }
                    ++start;
                }
            }
            processXref(xref);
            ptr = prev;
            if (alreadyVisitedXrefStreams.contains(ptr)) {
                throw new XrefCycledReferencesException(
                        KernelExceptionMessageConstant.XREF_STREAM_HAS_CYCLED_REFERENCES);
            }
        }
        return true;
    }

    protected void fixXref() throws IOException {
        fixedXref = true;
        PdfXrefTable xref = pdfDocument.getXref();
        tokens.seek(0);
        ByteBuffer buffer = new ByteBuffer(24);
        PdfTokenizer lineTokeniser = new PdfTokenizer(new RandomAccessFileOrArray(new ReusableRandomAccessSource(buffer)));
        for (; ; ) {
            long pos = tokens.getPosition();
            buffer.reset();

            // added boolean because of mailing list issue (17 Feb. 2014)
            if (!tokens.readLineSegment(buffer, true))
                break;
            if (buffer.get(0) >= '0' && buffer.get(0) <= '9') {
                int[] obj = PdfTokenizer.checkObjectStart(lineTokeniser);
                if (obj == null)
                    continue;
                int num = obj[0];
                int gen = obj[1];
                PdfIndirectReference reference = xref.get(num);
                if (reference != null && reference.getGenNumber() == gen) {
                    reference.fixOffset(pos);
                }
            }
        }
    }


    protected void rebuildXref() throws IOException {
        xrefStm = false;
        hybridXref = false;
        rebuiltXref = true;
        PdfXrefTable xref = pdfDocument.getXref();
        xref.clear();
        tokens.seek(0);
        trailer = null;
        ByteBuffer buffer = new ByteBuffer(24);
        try (PdfTokenizer lineTokenizer = new PdfTokenizer(
                new RandomAccessFileOrArray(new ReusableRandomAccessSource(buffer)))) {
            Long trailerIndex = null;

            for (; ; ) {
                long pos = tokens.getPosition();
                buffer.reset();

                // added boolean because of mailing list issue (17 Feb. 2014)
                if (!tokens.readLineSegment(buffer, true)) {
                    break;
                }
                if (buffer.get(0) == 't') {
                    if (!PdfTokenizer.checkTrailer(buffer)) {
                        continue;
                    }
                    tokens.seek(pos);
                    tokens.nextToken();
                    pos = tokens.getPosition();
                    if (isCurrentObjectATrailer()) {
                        // if the pdf is linearized it is possible that the trailer has been read
                        // before the actual objects it refers to this causes the trailer to have
                        // objects in READING state that's why we keep track of the position  of the
                        // trailer and then asign it when the whole pdf has been loaded
                        trailerIndex = pos;
                    } else {
                        tokens.seek(pos);
                    }
                } else if (buffer.get(0) >= '0' && buffer.get(0) <= '9') {
                    int[] obj = PdfTokenizer.checkObjectStart(lineTokenizer);
                    if (obj == null) {
                        continue;
                    }
                    int num = obj[0];
                    int gen = obj[1];
                    if (xref.get(num) == null || xref.get(num).getGenNumber() <= gen) {
                        xref.add(new PdfIndirectReference(pdfDocument, num, gen, pos));
                    }
                }
            }
            // now that the document has been read fully the underlying trailer references won't be
            // in READING state when the pdf has been linearised now we can assign the trailer
            // and it will have the right references
            setTrailerFromTrailerIndex(trailerIndex);
        }
    }

    private boolean isCurrentObjectATrailer() {
        try {
            final PdfDictionary dic = (PdfDictionary) readObject(false);
            return dic.get(PdfName.Root, false) != null;
        } catch (MemoryLimitsAwareException e){
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    private void setTrailerFromTrailerIndex(Long trailerIndex) throws IOException {
        if (trailerIndex == null) {
            throw new PdfException(KernelExceptionMessageConstant.TRAILER_NOT_FOUND);
        }
        tokens.seek((long)trailerIndex);
        final PdfDictionary dic = (PdfDictionary) readObject(false);
        if (dic.get(PdfName.Root, false) != null) {
            trailer = dic;
        }
        if (trailer == null) {
            throw new PdfException(KernelExceptionMessageConstant.TRAILER_NOT_FOUND);
        }
    }

    protected PdfNumber getXrefPrev(PdfObject prevObjectToCheck) {
        if (prevObjectToCheck == null) {
            return null;
        }

        if (prevObjectToCheck.getType() == PdfObject.NUMBER) {
            return (PdfNumber) prevObjectToCheck;
        } else {
            if (prevObjectToCheck.getType() == PdfObject.INDIRECT_REFERENCE &&
                    StrictnessLevel.CONSERVATIVE.isStricter(this.getStrictnessLevel())) {
                final PdfObject value = ((PdfIndirectReference) prevObjectToCheck).getRefersTo(true);
                if (value != null && value.getType() == PdfObject.NUMBER) {
                    return (PdfNumber) value;
                }
            }
            throw new InvalidXRefPrevException(
                    KernelExceptionMessageConstant.XREF_PREV_SHALL_BE_DIRECT_NUMBER_OBJECT);
        }
    }

    boolean isMemorySavingMode() {
        return memorySavingMode;
    }

    void setXrefProcessor(XrefProcessor xrefProcessor) {
        this.xrefProcessor = xrefProcessor;
    }

    private void processArrayReadError() {
        final String error = MessageFormatUtil.format(KernelExceptionMessageConstant.UNEXPECTED_TOKEN,
                new String(tokens.getByteContent(), StandardCharsets.UTF_8));
        if (StrictnessLevel.CONSERVATIVE.isStricter(this.getStrictnessLevel())) {
            final Logger logger = LoggerFactory.getLogger(PdfReader.class);
            logger.error(error);
        } else {
            tokens.throwError(error);
        }
    }

    private void readDecryptObj() {
        if (encrypted)
            return;
        PdfDictionary enc = trailer.getAsDictionary(PdfName.Encrypt);
        if (enc == null)
            return;
        encrypted = true;
        PdfName filter = enc.getAsName(PdfName.Filter);
        if (PdfName.Adobe_PubSec.equals(filter)) {
            if (properties.certificate == null) {
                throw new PdfException(
                        KernelExceptionMessageConstant.CERTIFICATE_IS_NOT_PROVIDED_DOCUMENT_IS_ENCRYPTED_WITH_PUBLIC_KEY_CERTIFICATE);
            }
            decrypt = new PdfEncryption(enc, properties.certificateKey, properties.certificate,
                    properties.certificateKeyProvider, properties.externalDecryptionProcess);
        } else if (PdfName.Standard.equals(filter)) {
            decrypt = new PdfEncryption(enc, properties.password, getOriginalFileId());
        } else {
            throw new UnsupportedSecurityHandlerException(MessageFormatUtil.format(KernelExceptionMessageConstant.UNSUPPORTED_SECURITY_HANDLER, filter));
        }

        decrypt.configureEncryptionParametersFromReader(pdfDocument, trailer);
    }

    private PdfObject readObject(PdfIndirectReference reference, boolean fixXref) {
        if (reference == null)
            return null;
        if (reference.refersTo != null)
            return reference.refersTo;
        try {
            currentIndirectReference = reference;
            if (reference.getObjStreamNumber() > 0) {
                PdfStream objectStream = (PdfStream) pdfDocument.getXref().
                        get(reference.getObjStreamNumber()).getRefersTo(false);
                if (objectStream == null) {
                    throw new PdfException(MessageFormatUtil.format(
                            KernelExceptionMessageConstant.INVALID_OBJECT_STREAM_NUMBER, reference.getObjNumber()
                            , reference.getObjStreamNumber(), reference.getIndex()));
                }

                readObjectStream(objectStream);
                return reference.refersTo;
            } else if (reference.getOffset() > 0) {
                PdfObject object;
                try {
                    tokens.seek(reference.getOffset());
                    tokens.nextValidToken();
                    if (tokens.getTokenType() != PdfTokenizer.TokenType.Obj
                            || tokens.getObjNr() != reference.getObjNumber()
                            || tokens.getGenNr() != reference.getGenNumber()) {
                        tokens.throwError(
                                KernelExceptionMessageConstant.INVALID_OFFSET_FOR_THIS_OBJECT, reference.toString());
                    }
                    object = readObject(false);
                } catch (RuntimeException ex) {
                    if (fixXref && reference.getObjStreamNumber() == 0) {
                        fixXref();
                        object = readObject(reference, false);
                    } else {
                        throw ex;
                    }
                }
                return object != null ? object.setIndirectReference(reference) : null;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_READ_PDF_OBJECT, e);
        }
    }

    private void checkPdfStreamLength(PdfStream pdfStream) throws IOException {
        if (!correctStreamLength)
            return;
        long fileLength = tokens.length();
        long start = pdfStream.getOffset();
        boolean calc = false;
        int streamLength = 0;
        PdfNumber pdfNumber = pdfStream.getAsNumber(PdfName.Length);
        if (pdfNumber != null) {
            streamLength = pdfNumber.intValue();
            if (streamLength + start > fileLength - 20) {
                calc = true;
            } else {
                tokens.seek(start + streamLength);
                String line = tokens.readString(20);
                if (!line.startsWith(endstream2) && !line.startsWith(endstream3) &&
                        !line.startsWith(endstream4) && !line.startsWith(endstream1)) {
                    calc = true;
                }
            }
        } else {
            pdfNumber = new PdfNumber(0);
            pdfStream.put(PdfName.Length, pdfNumber);
            calc = true;
        }
        if (calc) {
            ByteBuffer line = new ByteBuffer(16);
            tokens.seek(start);
            long pos;
            while (true) {
                pos = tokens.getPosition();
                line.reset();

                // added boolean because of mailing list issue (17 Feb. 2014)
                if (!tokens.readLineSegment(line, false)) {
                    if (!StrictnessLevel.CONSERVATIVE.isStricter(this.strictnessLevel)) {
                        throw new PdfException(KernelExceptionMessageConstant.STREAM_SHALL_END_WITH_ENDSTREAM);
                    }
                    break;
                }
                if (line.startsWith(endstream)) {
                    break;
                } else if (line.startsWith(endobj)) {
                    tokens.seek(pos - 16);
                    String s = tokens.readString(16);
                    int index = s.indexOf(endstream1);
                    if (index >= 0)
                        pos = pos - 16 + index;
                    break;
                }
            }
            streamLength = (int) (pos - start);
            tokens.seek(pos - 2);
            if (tokens.read() == 13) {
                streamLength--;
            }
            tokens.seek(pos - 1);
            if (tokens.read() == 10) {
                streamLength--;
            }
            pdfNumber.setValue(streamLength);
            pdfStream.updateLength(streamLength);
        }
    }

    private PdfObject createPdfNullInstance(boolean readAsDirect) {
        if (readAsDirect) {
            return PdfNull.PDF_NULL;
        } else {
            return new PdfNull();
        }
    }

    /**
     * Utility method that checks the provided byte source to see if it has junk bytes at the beginning.  If junk bytes
     * are found, construct a tokeniser that ignores the junk.  Otherwise, construct a tokeniser for the byte source as it is
     *
     * @param byteSource the source to check
     * @return a tokeniser that is guaranteed to start at the PDF header
     * @throws IOException if there is a problem reading the byte source
     */
    private static PdfTokenizer getOffsetTokeniser(IRandomAccessSource byteSource, boolean closeStream)
            throws IOException {
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(byteSource));
        int offset;
        try {
            offset = tok.getHeaderOffset();
        } catch (com.itextpdf.io.exceptions.IOException ex) {
            if (closeStream) {
                tok.close();
            }
            throw ex;
        }
        if (offset != 0) {
            IRandomAccessSource offsetSource = new WindowRandomAccessSource(byteSource, offset);
            tok = new PdfTokenizer(new RandomAccessFileOrArray(offsetSource));
        }
        return tok;
    }

    private void processXref(PdfXrefTable xrefTable) throws IOException {
        long currentPosition = tokens.getPosition();
        try {
            xrefProcessor.processXref(xrefTable, tokens);
        } finally {
            tokens.seek(currentPosition);
        }
    }

    private static void logXrefException(RuntimeException ex) {
        Logger logger = LoggerFactory.getLogger(PdfReader.class);
        if (ex.getCause() != null) {
            logger.error(MessageFormatUtil.format(
                    IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE
                    , ex.getCause().getMessage()));
        } else if (ex.getMessage() !=null) {
            logger.error(MessageFormatUtil.format(
                    IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE
                    , ex.getMessage()));
        } else {
            logger.error(IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT);
        }
    }

    protected static class ReusableRandomAccessSource implements IRandomAccessSource {
        private ByteBuffer buffer;

        public ReusableRandomAccessSource(ByteBuffer buffer) {
            if (buffer == null) throw new IllegalArgumentException("Passed byte buffer can not be null.");
            this.buffer = buffer;
        }

        @Override
        public int get(long offset) {
            if (offset >= buffer.size()) return -1;
            return 0xff & buffer.getInternalBuffer()[(int) offset];
        }

        @Override
        public int get(long offset, byte[] bytes, int off, int len) {
            if (buffer == null) throw new IllegalStateException("Already closed");

            if (offset >= buffer.size())
                return -1;

            if (offset + len > buffer.size())
                len = (int) (buffer.size() - offset);

            System.arraycopy(buffer.getInternalBuffer(), (int) offset, bytes, off, len);

            return len;
        }

        @Override
        public long length() {
            return buffer.size();
        }

        @Override
        public void close() {
            buffer = null;
        }
    }

    /**
     * Enumeration representing the strictness level for reading.
     */
    public enum StrictnessLevel {
        /**
         * The reading strictness level at which iText fails (throws an exception) in case of
         * contradiction with PDF specification, but still recovers from mild parsing errors
         * and ambiguities.
         */
        CONSERVATIVE(5000),
        /**
         * The reading strictness level at which iText tries to recover from parsing
         * errors if possible.
         */
        LENIENT(3000);

        private final int levelValue;

        StrictnessLevel(int levelValue) {
            this.levelValue = levelValue;
        }

        /**
         * Checks whether the current instance represents more strict reading level than
         * the provided one. Note that the {@code null} is less strict than any other value.
         *
         * @param compareWith the {@link StrictnessLevel} to compare with
         *
         * @return {@code true} if the current level is stricter than the provided one
         */
        public boolean isStricter(StrictnessLevel compareWith) {
            return compareWith == null || this.levelValue > compareWith.levelValue;
        }
    }

    /**
     * Class containing a callback which is called on every xref table reading.
     */
    static class XrefProcessor {
        /**
         * Process xref table.
         *
         * @param xrefTable {@link PdfXrefTable} to be processed
         * @param tokenizer {@link PdfTokenizer} to be processed
         *
         * @throws IOException in case of input-output related exceptions during PDF document reading
         */
        void processXref(PdfXrefTable xrefTable, PdfTokenizer tokenizer) throws IOException {
            // Do nothing.
        }
    }
}
