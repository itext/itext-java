package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.Utilities;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSource;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.source.WindowRandomAccessSource;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.BadPasswordException;
import com.itextpdf.kernel.pdf.filters.DoNothingFilter;
import com.itextpdf.kernel.pdf.filters.FilterHandler;
import com.itextpdf.kernel.pdf.filters.FilterHandlers;
import com.itextpdf.kernel.security.ExternalDecryptionProcess;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.Map;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.RecipientInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfReader implements Closeable {

    protected static boolean correctStreamLength = true;

    protected PdfTokenizer tokens;
    protected PdfEncryption decrypt;
    protected PdfVersion pdfVersion;
    protected long lastXref;
    protected long eofPos;
    protected PdfDictionary trailer;
    protected PdfDocument pdfDocument;

    protected int rValue;
    protected long pValue;
    protected byte[] password; //added by ujihara for decryption
    protected Key certificateKey; //added by Aiken Sam for certificate decryption
    protected Certificate certificate; //added by Aiken Sam for certificate decryption
    protected String certificateKeyProvider; //added by Aiken Sam for certificate decryption
    protected ExternalDecryptionProcess externalDecryptionProcess;
    private boolean ownerPasswordUsed;
    private boolean unethicalReading;
    private PdfObject cryptoRef;

    //indicate nearest first Indirect reference object which includes current reading the object, using for PdfString decrypt
    private PdfIndirectReference currentIndirectReference;
    protected boolean encrypted = false;
    protected boolean rebuiltXref = false;
    protected boolean hybridXref = false;
    protected boolean fixedXref = false;
    protected boolean xrefStm = false;

    private static final String endstream1 = "endstream";
    private static final String endstream2 = "\nendstream";
    private static final String endstream3 = "\r\nendstream";
    private static final String endstream4 = "\rendstream";
    private static final byte[] endstream = PdfOutputStream.getIsoBytes("endstream");
    private static final byte[] endobj = PdfOutputStream.getIsoBytes("endobj");

    /**
     * Constructs a new PdfReader. This is the master constructor.
     *
     * @param byteSource                source of bytes for the reader
     * @param ownerPassword             the password or null if no password is required
     * @param certificate               the certificate or null if no certificate is required
     * @param certificateKey            the key or null if no certificate key is required
     * @param certificateKeyProvider    the name of the key provider, or null if no key is required
     * @param externalDecryptionProcess External decryption process
     */
    public PdfReader(RandomAccessSource byteSource, byte ownerPassword[], Certificate certificate, Key certificateKey,
                     String certificateKeyProvider, ExternalDecryptionProcess externalDecryptionProcess) throws IOException {
        this.password = ownerPassword;
        this.certificate = certificate;
        this.certificateKey = certificateKey;
        this.certificateKeyProvider = certificateKeyProvider;
        this.externalDecryptionProcess = externalDecryptionProcess;
        this.tokens = getOffsetTokeniser(byteSource);
    }

    /**
     * Reads and parses a PDF document.
     *
     * @param is            the {@code InputStream} containing the document. The stream is read to the
     *                      end but is not closed
     * @param ownerPassword the password or null if no password is required
     * @throws IOException                      on error
     * @throws PdfException on error
     */
    public PdfReader(InputStream is, byte ownerPassword[]) throws IOException {
        this(new RandomAccessSourceFactory().createSource(is), ownerPassword, null, null, null, null);
    }

    /**
     * Reads and parses a PDF document.
     *
     * @param is the {@code InputStream} containing the document. Stream is closed automatically, when document is closed,
     *           if user doesn't want to close stream, he should set closeStream=false;
     * @throws IOException                      on error
     * @throws PdfException on error
     */
    public PdfReader(InputStream is) throws IOException {
        this(is, null);
    }

    /**
     * Reads and parses a PDF document.
     *
     * @param filename                  the file name of the document
     * @param certificate               the certificate or null if no certificate is required
     * @param externalDecryptionProcess External decryption process
     * @throws IOException on error
     */
    public PdfReader(final String filename, Certificate certificate, final ExternalDecryptionProcess externalDecryptionProcess) throws IOException {
        this(
                new RandomAccessSourceFactory()
                        .setForceRead(false)
                        .createBestSource(filename),
                null,
                certificate,
                null,
                null,
                externalDecryptionProcess);

    }

    /**
     * Reads and parses a PDF document.
     *
     * @param filename the file name of the document
     * @throws IOException on error
     */
    public PdfReader(final String filename) throws IOException {
        this(filename, null);

    }

    /**
     * Reads and parses a PDF document.
     *
     * @param filename      the file name of the document
     * @param ownerPassword the password to read the document
     * @throws IOException on error
     */
    public PdfReader(final String filename, final byte ownerPassword[]) throws IOException {
        this(
                new RandomAccessSourceFactory()
                        .setForceRead(false)
                        .createBestSource(filename),
                ownerPassword,
                null,
                null,
                null,
                null
        );
    }

    public void close() throws IOException {
        tokens.close();
    }

    public boolean isCloseStream() {
        return tokens.isCloseStream();
    }

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
     * Reads and gets stream bytes.
     *
     * @param decode true if to get decoded stream bytes, false if to leave it originally encoded.
     * @return byte[]
     * @throws IOException
     * @throws PdfException
     */
    public byte[] readStreamBytes(PdfStream stream, boolean decode) throws IOException {
        byte[] b = readStreamBytesRaw(stream);
        if (decode && b != null) {
            return decodeBytes(b, stream);
        } else {
            return b;
        }
    }

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
            if (decrypt != null) {
                PdfObject filter = stream.get(PdfName.Filter, true);
                boolean skip = false;
                if (filter != null) {
                    if (PdfName.Crypt.equals(filter)) {
                        skip = true;
                    } else if (filter.getType() == PdfObject.Array) {
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
                    decrypt.setHashKey(stream.getIndirectReference().getObjNumber(), stream.getIndirectReference().getGenNumber());
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
     * Gets the input stream associated with PdfStream.
     * User is responsible for closing returned stream.
     *
     * @param decode true if to get decoded stream, false if to leave it originally encoded.
     * @return InputStream
     * @throws IOException
     * @throws PdfException
     */
    public InputStream readStream(PdfStream stream, boolean decode) throws IOException {
        byte[] bytes = readStreamBytes(stream, decode);
        return bytes != null ? new ByteArrayInputStream(bytes) : null;
    }

    /**
     * Decode a byte[] applying the filters specified in the provided dictionary using default filter handlers.
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
    public static byte[] decodeBytes(byte[] b, PdfDictionary streamDictionary, Map<PdfName, FilterHandler> filterHandlers) {
        if (b == null) {
            return null;
        }
        PdfObject filter = streamDictionary.get(PdfName.Filter);
        PdfArray filters = new PdfArray();
        if (filter != null) {
            if (filter.getType() == PdfObject.Name) {
                filters.add(filter);
            } else if (filter.getType() == PdfObject.Array) {
                filters = ((PdfArray) filter);
            }
        }
        PdfArray dp = new PdfArray();
        PdfObject dpo = streamDictionary.get(PdfName.DecodeParms);
        if (dpo == null || (dpo.getType() != PdfObject.Dictionary && dpo.getType() != PdfObject.Array)) {
            if (dpo != null) dpo.release();
            dpo = streamDictionary.get(PdfName.DP);
        }
        if (dpo != null) {
            if (dpo.getType() == PdfObject.Dictionary) {
                dp.add(dpo);
            } else if (dpo.getType() == PdfObject.Array) {
                dp = ((PdfArray) dpo);
            }
            dpo.release();
        }
        for (int j = 0; j < filters.size(); ++j) {
            PdfName filterName = (PdfName) filters.get(j);
            FilterHandler filterHandler = filterHandlers.get(filterName);
            if (filterHandler == null)
                filterHandler =  new DoNothingFilter();
                //throw new PdfException(PdfException.Filter1IsNotSupported).setMessageParams(filterName); //TODO replace with some kind of UnsupportedException

            PdfDictionary decodeParams;
            if (j < dp.size()) {
                PdfObject dpEntry = dp.get(j, true);
                if (dpEntry == null || dpEntry.getType() == PdfObject.Null) {
                    decodeParams = null;
                } else if (dpEntry.getType() == PdfObject.Dictionary) {
                    decodeParams = (PdfDictionary) dpEntry;
                } else {
                    throw new PdfException(PdfException.DecodeParameterType1IsNotSupported).setMessageParams(dpEntry.getClass().toString()); //TODO replace with some kind of UnsupportedException
                }
            } else {
                decodeParams = null;
            }
            b = filterHandler.decode(b, filterName, decodeParams, streamDictionary);
        }
        return b;
    }

    /** Gets a new file instance of the original PDF
     * document.
     * @return a new file instance of the original PDF document
     */
    public RandomAccessFileOrArray getSafeFile() {
        return tokens.getSafeFile();
    }

    /**
     * Provides the size of the opened file.
     * @return The size of the opened file.
     * @throws IOException
     */
    public long getFileLength() throws IOException {
        return tokens.getSafeFile().length();
    }

    public void setUnethicalReading(boolean unethicalReading) {
        this.unethicalReading = unethicalReading;
    }

    public boolean isOpenedWithFullPermission() {
        return !encrypted || ownerPasswordUsed || unethicalReading;
    }

    public long getPermissions() {
        return pValue;
    }

    public int getCryptoMode() {
        if (decrypt == null)
            return -1;
        else
            return decrypt.getCryptoMode();
    }

    /**
     * Parses the entire PDF
     */
    protected void readPdf() throws IOException {
        String version = tokens.checkPdfHeader();
        try {
            this.pdfVersion = PdfVersion.fromString(version);
        } catch (IllegalArgumentException exc) {
            throw new PdfException(PdfException.PdfVersionNotValid, version);
        }
        try {
            readXref();
        } catch (RuntimeException ex) {
            rebuildXref();
        }
        readDecryptObj();
    }

    private void readDecryptObj() {
        if (encrypted)
            return;
        PdfDictionary enc = trailer.getAsDictionary(PdfName.Encrypt);
        if (enc == null)
            return;
        byte[] encryptionKey = null;
        encrypted = true;

        PdfNumber number;
        PdfArray documentIDs = trailer.getAsArray(PdfName.ID);
        byte documentID[] = null;
        if (documentIDs != null) {
            documentID = documentIDs.getAsString(0).getIsoBytes();
        }
        // just in case we have a broken producer
        if (documentID == null) {
            documentID = new byte[0];
        }
        byte uValue[] = null;
        byte oValue[] = null;
        int cryptoMode = PdfWriter.STANDARD_ENCRYPTION_40;
        int lengthValue = 0;
        PdfObject filter = enc.get(PdfName.Filter, true);
        if (filter.equals(PdfName.Standard)) {
            uValue = enc.getAsString(PdfName.U).getIsoBytes();
            oValue = enc.getAsString(PdfName.O).getIsoBytes();
            number = enc.getAsNumber(PdfName.P);
            if (number == null)
                throw new PdfException(PdfException.IllegalPValue);
            pValue = number.getLongValue();

            number = enc.getAsNumber(PdfName.R);
            if (number == null)
                throw new PdfException(PdfException.IllegalRValue);
            rValue = number.getIntValue();

            switch (rValue) {
                case 2:
                    cryptoMode = PdfWriter.STANDARD_ENCRYPTION_40;
                    break;
                case 3:
                    number = enc.getAsNumber(PdfName.Length);
                    if (number == null)
                        throw new PdfException(PdfException.IllegalLengthValue);
                    lengthValue = number.getIntValue();
                    if (lengthValue > 128 || lengthValue < 40 || lengthValue % 8 != 0)
                        throw new PdfException(PdfException.IllegalLengthValue);
                    cryptoMode = PdfWriter.STANDARD_ENCRYPTION_128;
                    break;
                case 4:
                    PdfDictionary dic = (PdfDictionary) enc.get(PdfName.CF);
                    if (dic == null)
                        throw new PdfException(PdfException.CfNotFoundEncryption);
                    dic = (PdfDictionary) dic.get(PdfName.StdCF);
                    if (dic == null)
                        throw new PdfException(PdfException.StdcfNotFoundEncryption);
                    if (PdfName.V2.equals(dic.get(PdfName.CFM))) {
                        cryptoMode = PdfWriter.STANDARD_ENCRYPTION_128;
                    } else if (PdfName.AESV2.equals(dic.get(PdfName.CFM))) {
                        cryptoMode = PdfWriter.ENCRYPTION_AES_128;
                    } else {
                        throw new PdfException(PdfException.NoCompatibleEncryptionFound);
                    }
                    PdfBoolean em = enc.getAsBoolean(PdfName.EncryptMetadata);
                    if (em != null && !em.getValue()) {
                        cryptoMode |= PdfWriter.DO_NOT_ENCRYPT_METADATA;
                    }
                    break;
                case 5:
                    cryptoMode = PdfWriter.ENCRYPTION_AES_256;
                    PdfBoolean em5 = enc.getAsBoolean(PdfName.EncryptMetadata);
                    if (em5 != null && !em5.getValue()) {
                        cryptoMode |= PdfWriter.DO_NOT_ENCRYPT_METADATA;
                    }
                    break;
                default:
                    throw new PdfException(PdfException.UnknownEncryptionTypeREq1).setMessageParams(rValue);
            }
        } else if (filter.equals(PdfName.Adobe_PubSec)) {
            boolean foundRecipient = false;
            byte[] envelopedData = null;
            PdfArray recipients;
            number = enc.getAsNumber(PdfName.V);
            if (number == null)
                throw new PdfException(PdfException.IllegalVValue);
            int vValue = number.getIntValue();
            switch (vValue) {
                case 1:
                    cryptoMode = PdfWriter.STANDARD_ENCRYPTION_40;
                    lengthValue = 40;
                    recipients = enc.getAsArray(PdfName.Recipients);
                    break;
                case 2:
                    number = enc.getAsNumber(PdfName.Length);
                    if (number == null)
                        throw new PdfException(PdfException.IllegalLengthValue);
                    lengthValue = number.getIntValue();
                    if (lengthValue > 128 || lengthValue < 40 || lengthValue % 8 != 0)
                        throw new PdfException(PdfException.IllegalLengthValue);
                    cryptoMode = PdfWriter.STANDARD_ENCRYPTION_128;
                    recipients = enc.getAsArray(PdfName.Recipients);
                    break;
                case 4:
                case 5:
                    PdfDictionary dic = enc.getAsDictionary(PdfName.CF);
                    if (dic == null)
                        throw new PdfException(PdfException.CfNotFoundEncryption);
                    dic = (PdfDictionary) dic.get(PdfName.DefaultCryptFilter);
                    if (dic == null)
                        throw new PdfException(PdfException.DefaultcryptfilterNotFoundEncryption);
                    if (PdfName.V2.equals(dic.get(PdfName.CFM))) {
                        cryptoMode = PdfWriter.STANDARD_ENCRYPTION_128;
                        lengthValue = 128;
                    } else if (PdfName.AESV2.equals(dic.get(PdfName.CFM))) {
                        cryptoMode = PdfWriter.ENCRYPTION_AES_128;
                        lengthValue = 128;
                    } else if (PdfName.AESV3.equals(dic.get(PdfName.CFM))) {
                        cryptoMode = PdfWriter.ENCRYPTION_AES_256;
                        lengthValue = 256;
                    } else {
                        throw new PdfException(PdfException.NoCompatibleEncryptionFound);
                    }
                    PdfBoolean em = dic.getAsBoolean(PdfName.EncryptMetadata);
                    if (em != null && !em.getValue()) {
                        cryptoMode |= PdfWriter.DO_NOT_ENCRYPT_METADATA;
                    }
                    recipients = (PdfArray) dic.get(PdfName.Recipients);
                    break;
                default:
                    throw new PdfException(PdfException.UnknownEncryptionTypeVEq1, vValue);
            }
            X509CertificateHolder certHolder;
            try {
                certHolder = new X509CertificateHolder(certificate.getEncoded());
            } catch (Exception f) {
                throw new PdfException(PdfException.PdfDecryption, f);
            }
            if (externalDecryptionProcess == null) {
                for (int i = 0; i < recipients.size(); i++) {
                    PdfString recipient = recipients.getAsString(i);
                    CMSEnvelopedData data;
                    try {
                        data = new CMSEnvelopedData(recipient.getValueBytes());
                        Iterator<RecipientInformation> recipientCertificatesIt = data.getRecipientInfos().getRecipients().iterator();
                        while (recipientCertificatesIt.hasNext()) {
                            RecipientInformation recipientInfo = recipientCertificatesIt.next();

                            if (recipientInfo.getRID().match(certHolder) && !foundRecipient) {
                                envelopedData = PdfEncryptor.getContent(recipientInfo, (PrivateKey) certificateKey, certificateKeyProvider);
                                foundRecipient = true;
                            }
                        }
                    } catch (Exception f) {
                        throw new PdfException(PdfException.PdfDecryption, f);
                    }
                }
            } else {
                for (int i = 0; i < recipients.size(); i++) {
                    PdfString recipient = recipients.getAsString(i);
                    CMSEnvelopedData data;
                    try {
                        data = new CMSEnvelopedData(recipient.getValueBytes());
                        RecipientInformation recipientInfo = data.getRecipientInfos().get(externalDecryptionProcess.getCmsRecipientId());
                        if (recipientInfo != null) {
                            envelopedData = recipientInfo.getContent(externalDecryptionProcess.getCmsRecipient());
                            foundRecipient = true;
                        }
                    } catch (Exception f) {
                        throw new PdfException(PdfException.PdfDecryption, f);
                    }
                }
            }

            if (!foundRecipient || envelopedData == null) {
                throw new PdfException(PdfException.BadCertificateAndKey);
            }

            MessageDigest md;
            try {
                if ((cryptoMode & PdfWriter.ENCRYPTION_MASK) == PdfWriter.ENCRYPTION_AES_256) {
                    md = MessageDigest.getInstance("SHA-256");
                } else {
                    md = MessageDigest.getInstance("SHA-1");
                }
                md.update(envelopedData, 0, 20);
                for (int i = 0; i < recipients.size(); i++) {
                    byte[] encodedRecipient = recipients.getAsString(i).getValueBytes();
                    md.update(encodedRecipient);
                }
                if ((cryptoMode & PdfWriter.DO_NOT_ENCRYPT_METADATA) != 0) {
                    md.update(new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255});
                }
                encryptionKey = md.digest();
            } catch (Exception f) {
                throw new PdfException(PdfException.PdfDecryption, f);
            }
        }

        decrypt = new PdfEncryption();
        decrypt.setCryptoMode(cryptoMode, lengthValue);

        if (filter.equals(PdfName.Standard)) {
            if (rValue == 5) {
                ownerPasswordUsed = decrypt.readKey(enc, password);
                decrypt.documentID = documentID;
                pValue = decrypt.getPermissions();
            } else {
                //check by owner password
                decrypt.setupByOwnerPassword(documentID, password, uValue, oValue, pValue);
                if (!Utilities.equalsArray(uValue, decrypt.userKey, rValue == 3 || rValue == 4 ? 16 : 32)) {
                    //check by user password
                    decrypt.setupByUserPassword(documentID, password, oValue, pValue);
                    if (!Utilities.equalsArray(uValue, decrypt.userKey, rValue == 3 || rValue == 4 ? 16 : 32)) {
                        throw new BadPasswordException(PdfException.BadUserPassword);
                    }
                } else {
                    ownerPasswordUsed = true;
                }
            }
        } else if (filter.equals(PdfName.Adobe_PubSec)) {
            if ((cryptoMode & PdfWriter.ENCRYPTION_MASK) == PdfWriter.ENCRYPTION_AES_256) {
                decrypt.setKey(encryptionKey);
            } else {
                decrypt.setupByEncryptionKey(encryptionKey, lengthValue);
            }
            ownerPasswordUsed = true;
        }
        filter.release();
        if (enc.getIndirectReference() != null) {
            cryptoRef = enc.getIndirectReference();
            enc.release();
        }
    }

    protected void readObjectStream(PdfStream objectStream) throws IOException {
        int objectStreamNumber = objectStream.getIndirectReference().getObjNumber();
        int first = objectStream.getAsNumber(PdfName.First).getIntValue();
        int n = objectStream.getAsNumber(PdfName.N).getIntValue();
        byte[] bytes = readStreamBytes(objectStream, true);
        PdfTokenizer saveTokens = tokens;
        try {
            tokens = new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(bytes)));
            int address[] = new int[n];
            int objNumber[] = new int[n];
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
                throw new PdfException(PdfException.ErrorReadingObjectStream);
            for (int k = 0; k < n; ++k) {
                tokens.seek(address[k]);
                tokens.nextToken();
                PdfObject obj;
                if (tokens.getTokenType() == PdfTokenizer.TokenType.Number) {
                    obj = new PdfNumber(tokens.getByteContent());
                } else {
                    tokens.seek(address[k]);
                    obj = readObject(false);
                }
                PdfIndirectReference reference = pdfDocument.getXref().get(objNumber[k]);
                // Check if this object has no incremental updates (e.g. no append mode)
                if (reference.getObjStreamNumber() == objectStreamNumber) {
                    reference.setRefersTo(obj);
                    obj.setIndirectReference(reference);
                }
            }
            objectStream.getIndirectReference().setState(PdfObject.OriginalObjectStream);
        } finally {
            tokens = saveTokens;
        }
    }

    protected PdfObject readObject(PdfIndirectReference reference) {
        return readObject(reference, true);
    }

    protected PdfObject readObject(boolean readAsDirect) throws IOException {
        tokens.nextValidToken();
        PdfTokenizer.TokenType type = tokens.getTokenType();
        switch (type) {
            case StartDic: {
                PdfDictionary dict = readDictionary();
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
                return readArray();
            case Number:
                return new PdfNumber(tokens.getByteContent());
            case String: {
                PdfString pdfString = new PdfString(tokens.getByteContent(), tokens.isHexString());
                if(currentIndirectReference != null) {
                    pdfString.setDecryptInfoNum(currentIndirectReference.getObjNumber());
                    pdfString.setDecryptInfoGen(currentIndirectReference.getGenNumber());
                }
                return password == null ? pdfString : pdfString.decrypt(decrypt);
            }
            case Name:
                return readPdfName(readAsDirect);
            case Ref:
                int num = tokens.getObjNr();
                PdfXrefTable table = pdfDocument.getXref();
                PdfIndirectReference reference = table.get(num);
                if (reference != null) {
                    if (reference.isFree()) {
                        return PdfNull.PdfNull;
                    }
                    if (reference.getGenNumber() != tokens.getGenNr()) {
                        if (fixedXref) {
                            Logger logger = LoggerFactory.getLogger(PdfReader.class);
                            logger.warn(String.format(LogMessageConstant.INVALID_INDIRECT_REFERENCE + " %d %d R", tokens.getObjNr(), tokens.getGenNr()));
                            return new PdfNull();
                        } else {
                            throw new PdfException(PdfException.InvalidIndirectReference1);
                        }
                    }
                    return reference;
                } else {
                    PdfIndirectReference ref = new PdfIndirectReference(pdfDocument,
                            num, tokens.getGenNr(), 0).setState(PdfObject.Reading);
                    table.add(ref);
                    return ref;
                }
            case EndOfFile:
                throw new PdfException(PdfException.UnexpectedEndOfFile);
            default:
                if (tokens.tokenValueEqualsTo(PdfTokenizer.Null)) {
                    if (readAsDirect) {
                        return PdfNull.PdfNull;
                    } else {
                        return new PdfNull();
                    }
                } else if (tokens.tokenValueEqualsTo(PdfTokenizer.True)) {
                    if (readAsDirect) {
                        return PdfBoolean.PdfTrue;
                    } else {
                        return new PdfBoolean(true);
                    }
                } else if (tokens.tokenValueEqualsTo(PdfTokenizer.False)) {
                    if (readAsDirect) {
                        return PdfBoolean.PdfFalse;
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

    protected PdfDictionary readDictionary() throws IOException {
        PdfDictionary dic = new PdfDictionary();
        while (true) {
            tokens.nextValidToken();
            if (tokens.getTokenType() == PdfTokenizer.TokenType.EndDic)
                break;
            if (tokens.getTokenType() != PdfTokenizer.TokenType.Name)
                tokens.throwError(PdfException.DictionaryKey1IsNotAName, tokens.getStringValue());
            PdfName name = readPdfName(true);
            PdfObject obj = readObject(true);
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

    protected PdfArray readArray() throws IOException {
        PdfArray array = new PdfArray();
        while (true) {
            PdfObject obj = readObject(true);
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
            if (prev.getLongValue() == startxref)
                throw new PdfException(PdfException.TrailerPrevEntryPointsToItsOwnCrossReferenceSection);
            startxref = prev.getLongValue();
            tokens.seek(startxref);
            trailer2 = readXrefSection();
        }
    }

    protected PdfDictionary readXrefSection() throws IOException {
        tokens.nextValidToken();
        if (!tokens.tokenValueEqualsTo(PdfTokenizer.Xref))
            tokens.throwError(PdfException.XrefSubsectionNotFound);
        PdfXrefTable xref = pdfDocument.getXref();
        int end = 0;
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
            end = tokens.getIntValue() + start;
            for (int num = start; num < end; num++) {
                tokens.nextValidToken();
                long pos = tokens.getLongValue();
                tokens.nextValidToken();
                int gen = tokens.getIntValue();
                tokens.nextValidToken();
                PdfIndirectReference reference = xref.get(num);
                if (reference == null) {
                    reference = new PdfIndirectReference(pdfDocument, num, gen, pos);
                } else if (reference.checkState(PdfObject.Reading) && reference.getGenNumber() == gen) {
                    reference.setOffset(pos);
                    reference.clearState(PdfObject.Reading);
                } else {
                    continue;
                }
                if (tokens.tokenValueEqualsTo(PdfTokenizer.N)) {
                    if (xref.get(num) == null) {
                        if (pos == 0)
                            tokens.throwError(PdfException.FilePosition0CrossReferenceEntryInThisXrefSubsection);
                        xref.add(reference);
                    }
                } else if (tokens.tokenValueEqualsTo(PdfTokenizer.F)) {
                    if (xref.get(num) == null) {
                        reference.setFree();
                        xref.add(reference);
                    }
                } else
                    tokens.throwError(PdfException.InvalidCrossReferenceEntryInThisXrefSubsection);
            }
        }
        PdfDictionary trailer = (PdfDictionary) readObject(false);
        PdfNumber xrefSize = (PdfNumber) trailer.get(PdfName.Size);
        if (xrefSize == null || (xrefSize.getIntValue() != end && end > 0)) {
            throw new PdfException(PdfException.InvalidXrefSection);
        }

        PdfObject xrs = trailer.get(PdfName.XRefStm);
        if (xrs != null && xrs.getType() == PdfObject.Number) {
            int loc = ((PdfNumber) xrs).getIntValue();
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

    protected boolean readXrefStream(final long ptr) throws IOException {
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
        if (object.getType() == PdfObject.Stream) {
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

        int size = ((PdfNumber) xrefStream.get(PdfName.Size)).getIntValue();
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
            prev = ((PdfNumber) obj).getLongValue();
        xref.setCapacity(size);
        byte[] b = readStreamBytes(xrefStream, true);
        int bptr = 0;
        int[] wc = new int[3];
        for (int k = 0; k < 3; ++k) {
            wc[k] = w.getAsNumber(k).getIntValue();
        }
        for (int idx = 0; idx < index.size(); idx += 2) {
            int start = index.getAsNumber(idx).getIntValue();
            int length = index.getAsNumber(idx + 1).getIntValue();
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
                        if (base == 0) {
                            //indirect reference with number = 0 can't be overridden
                            //xref table already has indirect reference 0 65535 R
                            newReference = xref.get(base);
                        } else {
                            newReference = new PdfIndirectReference(pdfDocument, base, field3, 0);
                            newReference.setFree();
                        }
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
                if (xref.get(base) == null) {
                    xref.add(newReference);
                } else if (xref.get(base).checkState(PdfObject.Reading)
                        && xref.get(base).getObjNumber() == newReference.getObjNumber()
                        && xref.get(base).getGenNumber() == newReference.getGenNumber()) {
                    PdfIndirectReference reference = xref.get(base);
                    reference.setOffset(newReference.getOffset());
                    reference.setObjStreamNumber(newReference.getObjStreamNumber());
                    reference.clearState(PdfObject.Reading);
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
                int obj[] = PdfTokenizer.checkObjectStart(lineTokeniser);
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

    public byte[] getOriginalFileId() {
        PdfArray id = trailer.getAsArray(PdfName.ID);
        if (id != null) {
            return PdfOutputStream.getIsoBytes(id.getAsString(0).getValue());
        } else {
            return PdfEncryption.createDocumentId();
        }
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    PdfObject getCryptoRef() {
        return cryptoRef;
    }

    /**
     * Utility method that checks the provided byte source to see if it has junk bytes at the beginning.  If junk bytes
     * are found, construct a tokeniser that ignores the junk.  Otherwise, construct a tokeniser for the byte source as it is
     *
     * @param byteSource the source to check
     * @return a tokeniser that is guaranteed to start at the PDF header
     * @throws IOException if there is a problem reading the byte source
     */
    private static PdfTokenizer getOffsetTokeniser(RandomAccessSource byteSource) throws IOException {
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(byteSource));
        int offset = tok.getHeaderOffset();
        if (offset != 0) {
            RandomAccessSource offsetSource = new WindowRandomAccessSource(byteSource, offset);
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

    private void checkPdfStreamLength(final PdfStream pdfStream) throws IOException {
        if (!correctStreamLength)
            return;
        long fileLength = tokens.length();
        long start = pdfStream.getOffset();
        boolean calc = false;
        int streamLength = 0;
        PdfNumber pdfNumber = pdfStream.getAsNumber(PdfName.Length);
        if (pdfNumber != null) {
            streamLength = pdfNumber.getIntValue();
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
                    streamLength = (int)(pos - start);
                    break;
                } else if (line.startsWith(endobj)) {
                    tokens.seek(pos - 16);
                    String s = tokens.readString(16);
                    int index = s.indexOf(endstream1);
                    if (index >= 0)
                        pos = pos - 16 + index;
                    streamLength = (int)(pos - start);
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

    protected static class ReusableRandomAccessSource implements RandomAccessSource {
        private ByteBuffer buffer;

        public ReusableRandomAccessSource(ByteBuffer buffer) {
            if (buffer == null) throw new NullPointerException();
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

    /**
     * @return byte array of computed user password, or null if not encrypted or no ownerPassword is used.
     */
    public byte[] computeUserPassword() {
        if (!encrypted || !ownerPasswordUsed) return null;
        return decrypt.computeUserPassword(password);
    }
}
