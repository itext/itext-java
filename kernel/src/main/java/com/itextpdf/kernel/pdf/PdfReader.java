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
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.source.WindowRandomAccessSource;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.securityhandler.UnsupportedSecurityHandlerException;
import com.itextpdf.kernel.pdf.filters.FilterHandlers;
import com.itextpdf.kernel.pdf.filters.IFilterHandler;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a PDF document.
 */
public class PdfReader implements Closeable, Serializable {

    private static final long serialVersionUID = -3584187443691964939L;

    private static final String endstream1 = "endstream";
    private static final String endstream2 = "\nendstream";
    private static final String endstream3 = "\r\nendstream";
    private static final String endstream4 = "\rendstream";
    private static final byte[] endstream = ByteUtils.getIsoBytes("endstream");
    private static final byte[] endobj = ByteUtils.getIsoBytes("endobj");

    protected static boolean correctStreamLength = true;

    private boolean unethicalReading;

    //indicate nearest first Indirect reference object which includes current reading the object, using for PdfString decrypt
    private PdfIndirectReference currentIndirectReference;

    // For internal usage only
    private String sourcePath;

    protected PdfTokenizer tokens;
    protected PdfEncryption decrypt;

    // here we store only the pdfVersion that is written in the document's header,
    // however it could differ from the actual pdf version that could be written in document's catalog
    protected PdfVersion headerPdfVersion;
    protected long lastXref;
    protected long eofPos;
    protected PdfDictionary trailer;
    protected PdfDocument pdfDocument;
    protected PdfAConformanceLevel pdfAConformanceLevel;

    protected ReaderProperties properties;

    protected boolean encrypted = false;
    protected boolean rebuiltXref = false;
    protected boolean hybridXref = false;
    protected boolean fixedXref = false;
    protected boolean xrefStm = false;
    /**
     * Constructs a new PdfReader.
     *
     * @param byteSource source of bytes for the reader
     * @param properties properties of the created reader
     */
    public PdfReader(IRandomAccessSource byteSource, ReaderProperties properties) throws IOException {
        this.properties = properties;
        this.tokens = getOffsetTokeniser(byteSource);
    }

    /**
     * Reads and parses a PDF document.
     *
     * @param is         the {@code InputStream} containing the document. The stream is read to the
     *                   end but is not closed.
     * @param properties properties of the created reader
     * @throws IOException on error
     */
    public PdfReader(InputStream is, ReaderProperties properties) throws IOException {
        this(new RandomAccessSourceFactory().createSource(is), properties);
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
     * @param is the {@code InputStream} containing the document. the {@code InputStream} containing the document. The stream is read to the
     *                   end but is not closed.
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
                properties
        );
        this.sourcePath = filename;
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
     */
    public PdfReader setUnethicalReading(boolean unethicalReading) {
        this.unethicalReading = unethicalReading;
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
     */
    public boolean hasRebuiltXref() {
        return rebuiltXref;
    }

    /**
     * Some documents contain hybrid XRef, for more information see "7.5.8.4 Compatibility with Applications
     * That Do Not Support Compressed Reference Streams" in PDF 32000-1:2008 spec.
     *
     * @return true, if the document has hybrid Cross-Reference section.
     */
    public boolean hasHybridXref() {
        return hybridXref;
    }

    /**
     * Indicates whether the document has Cross-Reference Streams.
     *
     * @return true, if the document has Cross-Reference Streams.
     */
    public boolean hasXrefStm() {
        return xrefStm;
    }

    /**
     * If any exception generated while reading PdfObject, PdfReader will try to fix offsets of all objects.
     *
     * @return true, if PdfReader fixed offsets of PdfObjects.
     */
    public boolean hasFixedXref() {
        return fixedXref;
    }

    /**
     * Gets position of the last Cross-Reference table.
     *
     * @return -1 if Cross-Reference table has rebuilt, otherwise position of the last Cross-Reference table.
     */
    public long getLastXref() {
        return lastXref;
    }

    /**
     * Reads, decrypt and optionally decode stream bytes.
     * Note, this method doesn't store actual bytes in any internal structures.
     *
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
     * @return byte[] array.
     * @throws IOException on error.
     */
    public byte[] readStreamBytesRaw(PdfStream stream) throws IOException {
        PdfName type = stream.getAsName(PdfName.Type);
        if (!PdfName.XRefStm.equals(type) && !PdfName.ObjStm.equals(type))
            checkPdfStreamLength(stream);
        long offset = stream.getOffset();
        if (offset <= 0)
            return null;
        int length = stream.getLength();
        if (length <= 0)
            return new byte[0];
        RandomAccessFileOrArray file = tokens.getSafeFile();
        byte[] bytes = null;
        try {
            file.seek(stream.getOffset());
            bytes = new byte[length];
            file.readFully(bytes);
            if (decrypt != null && !decrypt.isEmbeddedFilesOnly()) {
                PdfObject filter = stream.get(PdfName.Filter, true);
                boolean skip = false;
                if (filter != null) {
                    if (PdfName.Crypt.equals(filter)) {
                        skip = true;
                    } else if (filter.getType() == PdfObject.ARRAY) {
                        PdfArray filters = (PdfArray) filter;
                        for (int k = 0; k < filters.size(); k++) {
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
            } catch (Exception ignored) {
            }
        }
        return bytes;
    }

    /**
     * Reads, decrypt and optionally decode stream bytes into {@link ByteArrayInputStream}.
     * User is responsible for closing returned stream.
     *
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
        if (null != memoryLimitsAwareHandler) {
            HashSet<PdfName> filterSet = new HashSet<>();
            int index;
            for (index = 0; index < filters.size(); index++) {
                PdfName filterName = filters.getAsName(index);
                if (!filterSet.add(filterName)) {
                    memoryLimitsAwareHandler.beginDecompressedPdfStreamProcessing();
                    break;
                }
            }
            if (index == filters.size()) { // The stream isn't suspicious. We shouldn't process it.
                memoryLimitsAwareHandler = null;
            }
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
                throw new PdfException(PdfException.Filter1IsNotSupported).setMessageParams(filterName);

            PdfDictionary decodeParams;
            if (j < dp.size()) {
                PdfObject dpEntry = dp.get(j, true);
                if (dpEntry == null || dpEntry.getType() == PdfObject.NULL) {
                    decodeParams = null;
                } else if (dpEntry.getType() == PdfObject.DICTIONARY) {
                    decodeParams = (PdfDictionary) dpEntry;
                } else {
                    throw new PdfException(PdfException.DecodeParameterType1IsNotSupported).setMessageParams(dpEntry.getClass().toString());
                }
            } else {
                decodeParams = null;
            }
            b = filterHandler.decode(b, filterName, decodeParams, streamDictionary);
            if (null != memoryLimitsAwareHandler) {
                memoryLimitsAwareHandler.considerBytesOccupiedByDecompressedPdfStream(b.length);
            }
        }
        if (null != memoryLimitsAwareHandler) {
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
     * @throws IOException on error.
     */
    public long getFileLength() throws IOException {
        return tokens.getSafeFile().length();
    }

    /**
     * Checks if the document was opened with the owner password so that the end application
     * can decide what level of access restrictions to apply. If the document is not encrypted
     * it will return {@code true}.
     *
     * @return {@code true} if the document was opened with the owner password or if it's not encrypted,
     * {@code false} if the document was opened with the user password.
     */
    public boolean isOpenedWithFullPermission() {
        return !encrypted || decrypt.isOpenedWithFullPermission() || unethicalReading;
    }

    /**
     * Gets the encryption permissions. It can be used directly in
     * {@link WriterProperties#setStandardEncryption(byte[], byte[], int, int)}.
     * See ISO 32000-1, Table 22 for more details.
     *
     * @return the encryption permissions, an unsigned 32-bit quantity.
     */
    public long getPermissions() {
        long perm = 0;
        if (encrypted && decrypt.getPermissions() != null) {
            perm = (long) decrypt.getPermissions();
        }
        return perm;
    }

    /**
     * Gets encryption algorithm and access permissions.
     *
     * @see EncryptionConstants
     */
    public int getCryptoMode() {
        if (decrypt == null)
            return -1;
        else
            return decrypt.getCryptoMode();
    }

    /**
     * Gets the declared Pdf/A conformance level of the source document that is being read.
     * Note that this information is provided via XMP metadata and is not verified by iText.
     *
     * @return conformance level of the source document, or {@code null} if no Pdf/A
     * conformance level information is specified.
     */
    public PdfAConformanceLevel getPdfAConformanceLevel() {
        return pdfAConformanceLevel;
    }

    /**
     * Computes user password if standard encryption handler is used with Standard40, Standard128 or AES128 encryption algorithm.
     *
     * @return user password, or null if not a standard encryption handler was used or if ownerPasswordUsed wasn't use to open the document.
     */
    public byte[] computeUserPassword() {
        if (!encrypted || !decrypt.isOpenedWithFullPermission()) return null;
        return decrypt.computeUserPassword(properties.password);
    }

    /**
     * Gets original file ID, the first element in {@link PdfName#ID} key of trailer.
     * If the size of ID array does not equal 2, an empty array will be returned.
     *
     * @return byte array represents original file ID.
     * @see PdfDocument#getOriginalDocumentId(). The ultimate document id should be taken from PdfDocument
     */
    public byte[] getOriginalFileId() {
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
     *
     * @return byte array represents modified file ID.
     * @see PdfDocument#getModifiedDocumentId()
     */
    public byte[] getModifiedFileId() {
        PdfArray id = trailer.getAsArray(PdfName.ID);
        if (id != null && id.size() == 2) {
            return ByteUtils.getIsoBytes(id.getAsString(1).getValue());
        } else {
            return new byte[0];
        }
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * Parses the entire PDF
     */
    protected void readPdf() throws IOException {
        String version = tokens.checkPdfHeader();
        try {
            this.headerPdfVersion = PdfVersion.fromString(version);
        } catch (IllegalArgumentException exc) {
            throw new PdfException(PdfException.PdfVersionNotValid, version);
        }
        try {
            readXref();
        } catch (RuntimeException ex) {
            Logger logger = LoggerFactory.getLogger(PdfReader.class);
            logger.error(LogMessageConstant.XREF_ERROR, ex);

            rebuildXref();
        }
        pdfDocument.getXref().markReadingCompleted();
        readDecryptObj();
    }

    protected void readObjectStream(PdfStream objectStream) throws IOException {
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
                throw new PdfException(PdfException.ErrorWhileReadingObjectStream);
            for (int k = 0; k < n; ++k) {
                tokens.seek(address[k]);
                tokens.nextToken();
                PdfObject obj;
                if (tokens.getTokenType() == PdfTokenizer.TokenType.Number) {
                    obj = new PdfNumber(tokens.getByteContent());
                } else {
                    tokens.seek(address[k]);
                    obj = readObject(false, true);
                }
                PdfIndirectReference reference = pdfDocument.getXref().get(objNumber[k]);
                // Check if this object has no incremental updates (e.g. no append mode)
                if (reference.getObjStreamNumber() == objectStreamNumber) {
                    reference.setRefersTo(obj);
                    obj.setIndirectReference(reference);
                }
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
                logger.warn(MessageFormatUtil.format(LogMessageConstant.INVALID_INDIRECT_REFERENCE, tokens.getObjNr(), tokens.getGenNr()));
                return createPdfNullInstance(readAsDirect);
            }
            if (reference.getGenNumber() != tokens.getGenNr()) {
                if (fixedXref) {
                    Logger logger = LoggerFactory.getLogger(PdfReader.class);
                    logger.warn(MessageFormatUtil.format(LogMessageConstant.INVALID_INDIRECT_REFERENCE, tokens.getObjNr(), tokens.getGenNr()));
                    return createPdfNullInstance(readAsDirect);
                } else {
                    throw new PdfException(PdfException.InvalidIndirectReference1,
                            MessageFormatUtil.format("{0} {1} R", reference.getObjNumber(), reference.getGenNumber()));
                }
            }
        } else {
            if (table.isReadingCompleted()) {
                Logger logger = LoggerFactory.getLogger(PdfReader.class);
                logger.warn(MessageFormatUtil.format(LogMessageConstant.INVALID_INDIRECT_REFERENCE, tokens.getObjNr(), tokens.getGenNr()));
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
                    if (ch != '\n')
                        ch = tokens.read();
                    if (ch != '\n')
                        tokens.backOnePosition(ch);
                    return new PdfStream(tokens.getPosition(), dict);
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
                if (isEncrypted() && !decrypt.isEmbeddedFilesOnly() && !objStm) {
                    pdfString.setDecryption(currentIndirectReference.getObjNumber(), currentIndirectReference.getGenNumber(), decrypt);
                }
                return pdfString;
            }
            case Name:
                return readPdfName(readAsDirect);
            case Ref:
                return readReference(readAsDirect);
            case EndOfFile:
                throw new PdfException(PdfException.UnexpectedEndOfFile);
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
            if (tokens.getTokenType() == PdfTokenizer.TokenType.EndDic)
                break;
            if (tokens.getTokenType() != PdfTokenizer.TokenType.Name)
                tokens.throwError(PdfException.DictionaryKey1IsNotAName, tokens.getStringValue());
            PdfName name = readPdfName(true);
            PdfObject obj = readObject(true, objStm);
            if (obj == null) {
                if (tokens.getTokenType() == PdfTokenizer.TokenType.EndDic)
                    tokens.throwError(PdfException.UnexpectedGtGt);
                if (tokens.getTokenType() == PdfTokenizer.TokenType.EndArray)
                    tokens.throwError(PdfException.UnexpectedCloseBracket);
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
                if (tokens.getTokenType() == PdfTokenizer.TokenType.EndArray)
                    break;
                if (tokens.getTokenType() == PdfTokenizer.TokenType.EndDic)
                    tokens.throwError(PdfException.UnexpectedGtGt);
            }
            array.add(obj);
        }
        return array;
    }

    protected void readXref() throws IOException {
        tokens.seek(tokens.getStartxref());
        tokens.nextToken();
        if (!tokens.tokenValueEqualsTo(PdfTokenizer.Startxref))
            throw new PdfException(PdfException.PdfStartxrefNotFound, tokens);
        tokens.nextToken();
        if (tokens.getTokenType() != PdfTokenizer.TokenType.Number)
            throw new PdfException(PdfException.PdfStartxrefIsNotFollowedByANumber, tokens);
        long startxref = tokens.getLongValue();
        lastXref = startxref;
        eofPos = tokens.getPosition();
        try {
            if (readXrefStream(startxref)) {
                xrefStm = true;
                return;
            }
        } catch (Exception ignored) {
        }
        // clear xref because of possible issues at reading xref stream.
        pdfDocument.getXref().clear();

        tokens.seek(startxref);
        trailer = readXrefSection();

        //  Prev key - integer value
        //  (Present only if the file has more than one cross-reference section; shall be an indirect reference)
        // The byte offset in the decoded stream from the beginning of the file
        // to the beginning of the previous cross-reference section.
        PdfDictionary trailer2 = trailer;
        while (true) {
            PdfNumber prev = (PdfNumber) trailer2.get(PdfName.Prev);
            if (prev == null)
                break;
            if (prev.longValue() == startxref)
                throw new PdfException(PdfException.TrailerPrevEntryPointsToItsOwnCrossReferenceSection);
            startxref = prev.longValue();
            tokens.seek(startxref);
            trailer2 = readXrefSection();
        }

        Integer xrefSize = trailer.getAsInt(PdfName.Size);
        if (xrefSize == null) {
            throw new PdfException(PdfException.InvalidXrefTable);
        }
    }

    protected PdfDictionary readXrefSection() throws IOException {
        tokens.nextValidToken();
        if (!tokens.tokenValueEqualsTo(PdfTokenizer.Xref))
            tokens.throwError(PdfException.XrefSubsectionNotFound);
        PdfXrefTable xref = pdfDocument.getXref();
        while (true) {
            tokens.nextValidToken();
            if (tokens.tokenValueEqualsTo(PdfTokenizer.Trailer)) {
                break;
            }
            if (tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
                tokens.throwError(PdfException.ObjectNumberOfTheFirstObjectInThisXrefSubsectionNotFound);
            }
            int start = tokens.getIntValue();
            tokens.nextValidToken();
            if (tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
                tokens.throwError(PdfException.NumberOfEntriesInThisXrefSubsectionNotFound);
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
                boolean refFirstEncountered = reference == null
                        || !refReadingState && reference.getDocument() == null; // for references that are added by xref table itself (like 0 entry)

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
                        tokens.throwError(PdfException.FilePosition1CrossReferenceEntryInThisXrefSubsection);
                    }
                } else if (tokens.tokenValueEqualsTo(PdfTokenizer.F)) {
                    if (refFirstEncountered) {
                        reference.setState(PdfObject.FREE);
                    }
                } else {
                    tokens.throwError(PdfException.InvalidCrossReferenceEntryInThisXrefSubsection);
                }

                if (refFirstEncountered) {
                    xref.add(reference);
                }
            }
        }
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
        obj = xrefStream.get(PdfName.Prev);
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
                        throw new PdfException(PdfException.InvalidXrefStream);
                }

                PdfIndirectReference reference = xref.get(base);
                boolean refReadingState = reference != null && reference.checkState(PdfObject.READING) && reference.getGenNumber() == newReference.getGenNumber();
                boolean refFirstEncountered = reference == null
                        || !refReadingState && reference.getDocument() == null; // for references that are added by xref table itself (like 0 entry)

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
        return prev == -1 || readXrefStream(prev);
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
            if (!tokens.readLineSegment(buffer, true)) // added boolean because of mailing list issue (17 Feb. 2014)
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
        PdfTokenizer lineTokeniser = new PdfTokenizer(new RandomAccessFileOrArray(new ReusableRandomAccessSource(buffer)));
        for (; ; ) {
            long pos = tokens.getPosition();
            buffer.reset();
            if (!tokens.readLineSegment(buffer, true)) // added boolean because of mailing list issue (17 Feb. 2014)
                break;
            if (buffer.get(0) == 't') {
                if (!PdfTokenizer.checkTrailer(buffer))
                    continue;
                tokens.seek(pos);
                tokens.nextToken();
                pos = tokens.getPosition();
                try {
                    PdfDictionary dic = (PdfDictionary) readObject(false);
                    if (dic.get(PdfName.Root, false) != null)
                        trailer = dic;
                    else
                        tokens.seek(pos);
                } catch (Exception e) {
                    tokens.seek(pos);
                }
            } else if (buffer.get(0) >= '0' && buffer.get(0) <= '9') {
                int[] obj = PdfTokenizer.checkObjectStart(lineTokeniser);
                if (obj == null)
                    continue;
                int num = obj[0];
                int gen = obj[1];
                if (xref.get(num) == null || xref.get(num).getGenNumber() <= gen) {
                    xref.add(new PdfIndirectReference(pdfDocument, num, gen, pos));
                }
            }
        }
        if (trailer == null)
            throw new PdfException(PdfException.TrailerNotFound);
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
                throw new PdfException(PdfException.CertificateIsNotProvidedDocumentIsEncryptedWithPublicKeyCertificate);
            }
            decrypt = new PdfEncryption(enc, properties.certificateKey, properties.certificate,
                    properties.certificateKeyProvider, properties.externalDecryptionProcess);
        } else if (PdfName.Standard.equals(filter)) {
            decrypt = new PdfEncryption(enc, properties.password, getOriginalFileId());
        } else {
            throw new UnsupportedSecurityHandlerException(MessageFormatUtil.format(UnsupportedSecurityHandlerException.UnsupportedSecurityHandler, filter));
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
    private static PdfTokenizer getOffsetTokeniser(IRandomAccessSource byteSource) throws IOException {
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(byteSource));
        int offset = tok.getHeaderOffset();
        if (offset != 0) {
            IRandomAccessSource offsetSource = new WindowRandomAccessSource(byteSource, offset);
            tok = new PdfTokenizer(new RandomAccessFileOrArray(offsetSource));
        }
        return tok;
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
                        tokens.throwError(PdfException.InvalidOffsetForObject1, reference.toString());
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
            throw new PdfException(PdfException.CannotReadPdfObject, e);
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
                if (!tokens.readLineSegment(line, false)) // added boolean because of mailing list issue (17 Feb. 2014)
                    break;
                if (line.startsWith(endstream)) {
                    streamLength = (int) (pos - start);
                    break;
                } else if (line.startsWith(endobj)) {
                    tokens.seek(pos - 16);
                    String s = tokens.readString(16);
                    int index = s.indexOf(endstream1);
                    if (index >= 0)
                        pos = pos - 16 + index;
                    streamLength = (int) (pos - start);
                    break;
                }
            }
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
     * This method is invoked while deserialization
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (sourcePath != null && tokens == null) {
            tokens = getOffsetTokeniser(new RandomAccessSourceFactory().setForceRead(false).createBestSource(sourcePath));
        }
    }

    /**
     * This method is invoked while serialization
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        if (sourcePath != null) {
            PdfTokenizer tempTokens = tokens;
            tokens = null;
            out.defaultWriteObject();
            tokens = tempTokens;
        } else {
            out.defaultWriteObject();
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
        public void close() throws IOException {
            buffer = null;
        }
    }
}
