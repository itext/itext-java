package com.itextpdf.signatures;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.basics.io.ByteBuffer;
import com.itextpdf.basics.io.RASInputStream;
import com.itextpdf.basics.io.RandomAccessSource;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import com.itextpdf.basics.io.StreamUtil;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDate;
import com.itextpdf.core.pdf.PdfDeveloperExtension;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfLiteral;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfOutputStream;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.PdfVersion;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfSigFieldLockDictionary;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of the cryptographic options and appearances that form a signature.
 */
public class PdfSigner {

    /**
     * The Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfSigner.class);

    /**
     * Enum containing the Cryptographic Standards. Possible values are "CMS" and "CADES".
     */
    public enum CryptoStandard {
        /**
         * Cryptographic Message Syntax.
         */
        CMS,

        /**
         * CMS Advanced Electronic Signatures.
         */
        CADES
    }

    /**
     * Approval signature.
     */
    public static final int NOT_CERTIFIED = 0;

    /**
     * Author signature, no changes allowed.
     */
    public static final int CERTIFIED_NO_CHANGES_ALLOWED = 1;

    /**
     * Author signature, form filling allowed.
     */
    public static final int CERTIFIED_FORM_FILLING = 2;

    /**
     * Author signature, form filling and annotations allowed.
     */
    public static final int CERTIFIED_FORM_FILLING_AND_ANNOTATIONS = 3;

    /**
     * The certification level.
     */
    protected int certificationLevel = NOT_CERTIFIED;

    /**
     * The name of the field. */
    protected String fieldName;

    /**
     * The file right before the signature is added (can be null).
     */
    protected RandomAccessFile raf;

    /**
     * The bytes of the file right before the signature is added (if raf is null).
     */
    protected byte[] bout;

    /**
     * Array containing the byte positions of the bytes that need to be hashed.
     */
    protected long[] range;

    /**
     * The PdfDocument.
     */
    protected PdfDocument document;

    /**
     * The crypto dictionary.
     */
    protected PdfSignature cryptoDictionary;

    /**
     * Holds value of property signatureEvent.
     */
    protected SignatureEvent signatureEvent;

    /** OutputStream for the bytes of the document.
     */
    protected OutputStream originalOS;

    /**
     * Outputstream that temporarily holds the output in memory.
     */
    protected ByteArrayOutputStream temporaryOS;

    /**
     * Tempfile to hold the output temporarily.
     */
    protected File tempFile;

    /**
     * Name and content of keys that can only be added in the close() method.
     */
    protected Map<PdfName, PdfLiteral> exclusionLocations;

    /**
     * Indicates if the pdf document has already been pre-closed.
     */
    protected boolean preClosed = false;

    /**
     * Signature field lock dictionary.
     */
    protected PdfSigFieldLockDictionary fieldLock;

    /**
     * The signature appearance.
     */
    protected PdfSignatureAppearance appearance;

    /**
     * Holds value of property signDate.
     */
    protected Calendar signDate;

    /**
     * Boolean to check if this PdfSigner instance has been closed already or not.
     */
    protected boolean closed;

    /**
     * Creates a PdfSigner instance. Uses a {@link java.io.ByteArrayOutputStream} instead of a temporary file.
     *
     * @param reader PdfReader that reads the PDF file
     * @param writer PdfWriter to write the signed PDF file
     * @param append boolean to indicate whether the signing should happen in append mode or not
     * @throws IOException
     */
    public PdfSigner(PdfReader reader, PdfWriter writer, boolean append) throws IOException {
        this(reader, writer, null, append);
    }

    /**
     * Creates a PdfSigner instance. Uses a {@link java.io.ByteArrayOutputStream} instead of a temporary file.
     *
     * @param reader PdfReader that reads the PDF file
     * @param writer PdfWriter to write the signed PDF file
     * @param tempFile File to which the output is temporarily written
     * @param append boolean to indicate whether the signing should happen in append mode or not
     * @throws IOException
     */
    public PdfSigner(PdfReader reader, PdfWriter writer, File tempFile, boolean append) throws IOException {
        if (tempFile == null) {
            temporaryOS = new ByteArrayOutputStream();
            document = new PdfDocument(reader, new PdfWriter(temporaryOS), append);
        } else {
            if (tempFile.isDirectory()) {
                tempFile = File.createTempFile("pdf", null, tempFile);
            }

            OutputStream os = new FileOutputStream(tempFile);
            this.tempFile = tempFile;
            document = new PdfDocument(reader, new PdfWriter(os), append);
        }

        originalOS = writer == null ? null : writer.getOutputStream();
        signDate = new GregorianCalendar();
        fieldName = getNewSigFieldName();
        appearance = new PdfSignatureAppearance(document, new Rectangle(0, 0), 1);
        appearance.setSignDate(signDate);

        closed = false;
    }

    /**
     * Gets the signature date.
     *
     * @return Calendar set to the signature date
     */
    public java.util.Calendar getSignDate() {
        return signDate;
    }

    /**
     * Sets the signature date.
     *
     * @param signDate the signature date
     */
    public void setSignDate(java.util.Calendar signDate) {
        this.signDate = signDate;
        this.appearance.setSignDate(signDate);
    }

    /**
     * Provides access to a signature appearance object. Use it to
     * customize the appearance of the signature.
     * <p>
     * Be aware:
     *     <ul>
     *         <li>If you create new signature field (either use {@link #setFieldName} with
     *         the name that doesn't exist in the document or don't specify it at all) then
     *         the signature is invisible by default.</li>
     *         <li>If you sign already existing field, then the signature appearance object
     *         is modified to have all the properties (page num., rect etc.) consistent with
     *         the state of the field (<strong>if you customized the appearance object
     *         before the {@link #setFieldName} call you'll have to do it again</strong>)</li>
     *     </ul>
     * </p>
     *
     * @return {@link PdfSignatureAppearance} object.
     */
    public PdfSignatureAppearance getSignatureAppearance() {
        return appearance;
    }

    /**
     * Returns the document's certification level.
     * For possible values see {@link #setCertificationLevel(int)}.
     *
     * @return The certified status.
     */
    public int getCertificationLevel() {
        return this.certificationLevel;
    }

    /**
     * Sets the document's certification level.
     *
     * @param certificationLevel a new certification level for a document.
     *                           Possible values are: <ul>
     *                              <li>{@link #NOT_CERTIFIED}</li>
     *                              <li>{@link #CERTIFIED_NO_CHANGES_ALLOWED}</li>
     *                              <li>{@link #CERTIFIED_FORM_FILLING}</li>
     *                              <li>{@link #CERTIFIED_FORM_FILLING_AND_ANNOTATIONS}</li>
     *                           </ul>
     */
    public void setCertificationLevel(int certificationLevel) {
        this.certificationLevel = certificationLevel;
    }

    /**
     * Gets the field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the user made signature dictionary. This is the dictionary at the /V key
     * of the signature field.
     *
     * @return The user made signature dictionary.
     */
    public PdfSignature getSignatureDictionary() {
        return cryptoDictionary;
    }

    /**
     * Getter for property signatureEvent.
     *
     * @return Value of property signatureEvent.
     */
    public SignatureEvent getSignatureEvent() {
        return this.signatureEvent;
    }

    /**
     * Sets the signature event to allow modification of the signature dictionary.
     *
     * @param signatureEvent the signature event
     */
    public void setSignatureEvent(SignatureEvent signatureEvent) {
        this.signatureEvent = signatureEvent;
    }

    /**
     * Gets a new signature field name that doesn't clash with any existing name.
     *
     * @return A new signature field name.
     */
    public String getNewSigFieldName() {
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        String name = "Signature";
        int step = 1;

        while (acroForm.getField(name + step) != null) {
            ++step;
        }

        return name + step;
    }

    /**
     * Sets the name indicating the field to be signed. The field can already be presented in the
     * document but shall not be signed. If the field is not presented in the document, it will be created.
     *
     * @param fieldName The name indicating the field to be signed.
     */
    public void setFieldName(String fieldName) {
        if (fieldName != null) {
            if (fieldName.indexOf('.') >= 0) {
                throw new IllegalArgumentException(PdfException.FieldNamesCannotContainADot);
            }

            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);

            if (acroForm.getField(fieldName) != null) {
                PdfFormField field = acroForm.getField(fieldName);

                if (!PdfName.Sig.equals(field.getFormType())) {
                    throw new IllegalArgumentException(PdfException.FieldTypeIsNotASignatureFieldType);
                }

                if (field.getValue() != null) {
                    throw new IllegalArgumentException(PdfException.FieldIsAlreadySigned);
                }

                appearance.setFieldName(fieldName);

                List<PdfWidgetAnnotation> widgets = field.getWidgets();
                if (!widgets.isEmpty()) {
                    PdfWidgetAnnotation widget = widgets.get(0);
                    appearance.setPageRect(getWidgetRectangle(widget));
                    appearance.setPageNumber(getWidgetPageNumber(widget));
                }
            }

            this.fieldName = fieldName;
        }
    }

    /**
     * Gets the PdfDocument associated with this instance.
     * @return the PdfDocument associated with this instance
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Sets the PdfDocument.
     */
    protected void setDocument(PdfDocument document) {
        this.document = document;
    }

    /**
     * Setter for the OutputStream.
     */
    public void setOriginalOutputStream(OutputStream originalOS) {
        this.originalOS = originalOS;
    }

    /**
     * Getter for the field lock dictionary.
     *
     * @return Field lock dictionary.
     */
    public PdfSigFieldLockDictionary getFieldLockDict() {
        return fieldLock;
    }

    /**
     * Setter for the field lock dictionary.
     * <p><strong>Be aware:</strong> if a signature is created on an existing signature field,
     * then its /Lock dictionary takes the precedence (if it exists).</p>
     *
     * @param fieldLock Field lock dictionary
     */
    public void setFieldLockDict(PdfSigFieldLockDictionary fieldLock) {
        this.fieldLock = fieldLock;
    }

    /**
     * Signs the document using the detached mode, CMS or CAdES equivalent.
     * <br><br>
     * NOTE: This method closes the underlying pdf document. This means, that current instance
     * of PdfSigner cannot be used after this method call.
     *
     * @param externalSignature the interface providing the actual signing
     * @param chain             the certificate chain
     * @param crlList           the CRL list
     * @param ocspClient        the OCSP client
     * @param tsaClient         the Timestamp client
     * @param externalDigest    an implementation that provides the digest
     * @param estimatedSize     the reserved size for the signature. It will be estimated if 0
     * @param sigtype           Either Signature.CMS or Signature.CADES
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void signDetached(ExternalDigest externalDigest, ExternalSignature externalSignature, Certificate[] chain, Collection<CrlClient> crlList, OcspClient ocspClient,
                                    TSAClient tsaClient, int estimatedSize, CryptoStandard sigtype) throws IOException, GeneralSecurityException {
        if (closed) {
            throw new PdfException(PdfException.ThisInstanceOfPdfSignerIsAlreadyClosed);
        }

        Collection<byte[]> crlBytes = null;
        int i = 0;
        while (crlBytes == null && i < chain.length)
            crlBytes = processCrl(chain[i++], crlList);
        if (estimatedSize == 0) {
            estimatedSize = 8192;
            if (crlBytes != null) {
                for (byte[] element : crlBytes) {
                    estimatedSize += element.length + 10;
                }
            }
            if (ocspClient != null)
                estimatedSize += 4192;
            if (tsaClient != null)
                estimatedSize += 4192;
        }
        PdfSignatureAppearance appearance = getSignatureAppearance();
        appearance.setCertificate(chain[0]);
        if (sigtype == CryptoStandard.CADES) {
            addDeveloperExtension(PdfDeveloperExtension.ESIC_1_7_EXTENSIONLEVEL2);
        }
        PdfSignature dic = new PdfSignature(PdfName.Adobe_PPKLite, sigtype == CryptoStandard.CADES ? PdfName.ETSI_CAdES_DETACHED : PdfName.Adbe_pkcs7_detached);
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setSignatureCreator(appearance.getSignatureCreator());
        dic.setContact(appearance.getContact());
        dic.setDate(new PdfDate(getSignDate())); // time-stamp will over-rule this
        cryptoDictionary = dic;

        Map<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, new Integer(estimatedSize * 2 + 2));
        preClose(exc);

        String hashAlgorithm = externalSignature.getHashAlgorithm();
        PdfPKCS7 sgn = new PdfPKCS7(null, chain, hashAlgorithm, null, externalDigest, false);
        InputStream data = getRangeStream();
        byte hash[] = DigestAlgorithms.digest(data, externalDigest.getMessageDigest(hashAlgorithm));
        byte[] ocsp = null;
        if (chain.length >= 2 && ocspClient != null) {
            ocsp = ocspClient.getEncoded((X509Certificate) chain[0], (X509Certificate) chain[1], null);
        }
        byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, ocsp, crlBytes, sigtype);
        byte[] extSignature = externalSignature.sign(sh);
        sgn.setExternalDigest(extSignature, null, externalSignature.getEncryptionAlgorithm());

        byte[] encodedSig = sgn.getEncodedPKCS7(hash, tsaClient, ocsp, crlBytes, sigtype);

        if (estimatedSize < encodedSig.length)
            throw new IOException("Not enough space");

        byte[] paddedSig = new byte[estimatedSize];
        System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.Contents, new PdfString(paddedSig).setHexWriting(true));
        close(dic2);

        closed = true;
    }

    /**
     * Sign the document using an external container, usually a PKCS7. The signature is fully composed
     * externally, iText will just put the container inside the document.
     * <br><br>
     * NOTE: This method closes the underlying pdf document. This means, that current instance
     * of PdfSigner cannot be used after this method call.
     *
     * @param externalSignatureContainer the interface providing the actual signing
     * @param estimatedSize the reserved size for the signature
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void signExternalContainer(ExternalSignatureContainer externalSignatureContainer, int estimatedSize) throws GeneralSecurityException, IOException {
        if (closed) {
            throw new PdfException(PdfException.ThisInstanceOfPdfSignerIsAlreadyClosed);
        }

        PdfSignature dic = new PdfSignature(null, null);
        PdfSignatureAppearance appearance = getSignatureAppearance();
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setSignatureCreator(appearance.getSignatureCreator());
        dic.setContact(appearance.getContact());
        dic.setDate(new PdfDate(getSignDate())); // time-stamp will over-rule this
        externalSignatureContainer.modifySigningDictionary(dic.getPdfObject());
        cryptoDictionary = dic;

        Map<PdfName, Integer> exc = new HashMap<PdfName, Integer>();
        exc.put(PdfName.Contents, new Integer(estimatedSize * 2 + 2));
        preClose(exc);

        InputStream data = getRangeStream();
        byte[] encodedSig = externalSignatureContainer.sign(data);

        if (estimatedSize < encodedSig.length)
            throw new IOException("Not enough space");

        byte[] paddedSig = new byte[estimatedSize];
        System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.Contents, new PdfString(paddedSig).setHexWriting(true));
        close(dic2);

        closed = true;
    }

    /**
     * Signs a document with a PAdES-LTV Timestamp. The document is closed at the end.
     * <br><br>
     * NOTE: This method closes the underlying pdf document. This means, that current instance
     * of PdfSigner cannot be used after this method call.
     *
     * @param tsa the timestamp generator
     * @param signatureName the signature name or null to have a name generated
     * automatically
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void timestamp(TSAClient tsa, String signatureName) throws IOException, GeneralSecurityException {
        if (closed) {
            throw new PdfException(PdfException.ThisInstanceOfPdfSignerIsAlreadyClosed);
        }

        int contentEstimated = tsa.getTokenSizeEstimate();
        addDeveloperExtension(PdfDeveloperExtension.ESIC_1_7_EXTENSIONLEVEL5);
        setFieldName(signatureName);

        PdfSignature dic = new PdfSignature(PdfName.Adobe_PPKLite, PdfName.ETSI_RFC3161);
        dic.put(PdfName.Type, PdfName.DocTimeStamp);
        cryptoDictionary = dic;

        Map<PdfName,Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, new Integer(contentEstimated * 2 + 2));
        preClose(exc);
        InputStream data = getRangeStream();
        MessageDigest messageDigest = tsa.getMessageDigest();
        byte[] buf = new byte[4096];
        int n;
        while ((n = data.read(buf)) > 0) {
            messageDigest.update(buf, 0, n);
        }
        byte[] tsImprint = messageDigest.digest();
        byte[] tsToken;
        try {
            tsToken = tsa.getTimeStampToken(tsImprint);
        }
        catch(Exception e) {
            throw new GeneralSecurityException(e);
        }

        if (contentEstimated + 2 < tsToken.length)
            throw new IOException("Not enough space");

        byte[] paddedSig = new byte[contentEstimated];
        System.arraycopy(tsToken, 0, paddedSig, 0, tsToken.length);

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.Contents, new PdfString(paddedSig).setHexWriting(true));
        close(dic2);

        closed = true;
    }

    /**
     * Signs a PDF where space was already reserved.
     *
     * @param document the original PDF
     * @param fieldName the field to sign. It must be the last field
     * @param outs the output PDF
     * @param externalSignatureContainer the signature container doing the actual signing. Only the
     * method ExternalSignatureContainer.sign is used
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static void signDeferred(PdfDocument document, String fieldName, OutputStream outs, ExternalSignatureContainer externalSignatureContainer) throws IOException, GeneralSecurityException {
        SignatureUtil signatureUtil = new SignatureUtil(document);
        PdfDictionary v = signatureUtil.getSignatureDictionary(fieldName);

        if (v == null) {
            new PdfException(PdfException.ThereIsNoFieldInTheDocumentWithSuchName1).setMessageParams(fieldName);
        }

        if (!signatureUtil.signatureCoversWholeDocument(fieldName)) {
            new PdfException(PdfException.SignatureWithName1IsNotTheLastItDoesntCoverWholeDocument).setMessageParams(fieldName);
        }

        PdfArray b = v.getAsArray(PdfName.ByteRange);
        long[] gaps = SignatureUtil.asLongArray(b); // TODO: refactor

        if (b.size() != 4 || gaps[0] != 0) {
            throw new IllegalArgumentException("Single exclusion space supported");
        }

        RandomAccessSource readerSource = document.getReader().getSafeFile().createSourceView();
        InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(readerSource, gaps));
        byte[] signedContent = externalSignatureContainer.sign(rg);
        int spaceAvailable = (int)(gaps[2] - gaps[1]) - 2;
        if ((spaceAvailable & 1) != 0) {
            throw new IllegalArgumentException("Gap is not a multiple of 2");
        }
        spaceAvailable /= 2;
        if (spaceAvailable < signedContent.length) {
            throw new PdfException(PdfException.AvailableSpaceIsNotEnoughForSignature);
        }
        StreamUtil.CopyBytes(readerSource, 0, gaps[1] + 1, outs);
        ByteBuffer bb = new ByteBuffer(spaceAvailable * 2);
        for (byte bi : signedContent) {
            bb.appendHex(bi);
        }
        int remain = (spaceAvailable - signedContent.length) * 2;
        for (int k = 0; k < remain; ++k) {
            bb.append((byte)48);
        }
        byte[] bbArr = bb.toByteArray();
        outs.write(bbArr);
        StreamUtil.CopyBytes(readerSource, gaps[2] - 1, gaps[3] + 1, outs);
    }

    /**
     * Processes a CRL list.
     *
     * @param cert    a Certificate if one of the CrlList implementations needs to retrieve the CRL URL from it.
     * @param crlList a list of CrlClient implementations
     * @return a collection of CRL bytes that can be embedded in a PDF
     */
    protected Collection<byte[]> processCrl(Certificate cert, Collection<CrlClient> crlList) {
        if (crlList == null)
            return null;
        List<byte[]> crlBytes = new ArrayList<>();
        for (CrlClient cc : crlList) {
            if (cc == null)
                continue;
            Collection<byte[]> b = cc.getEncoded((X509Certificate) cert, null);
            if (b == null)
                continue;
            crlBytes.addAll(b);
        }
        if (crlBytes.isEmpty())
            return null;
        else
            return crlBytes;
    }

    protected void addDeveloperExtension(PdfDeveloperExtension extension) {
        document.getCatalog().addDeveloperExtension(extension);
    }

    /**
     * Checks if the document is in the process of closing.
     *
     * @return true if the document is in the process of closing, false otherwise
     */
    protected boolean isPreClosed() {
        return preClosed;
    }

    /**
     * This is the first method to be called when using external signatures. The general sequence is:
     * preClose(), getDocumentBytes() and close().
     * <p>
     * <CODE>exclusionSizes</CODE> must contain at least
     * the <CODE>PdfName.CONTENTS</CODE> key with the size that it will take in the
     * document. Note that due to the hex string coding this size should be byte_size*2+2.
     *
     * @param exclusionSizes Map with names and sizes to be excluded in the signature
     * calculation. The key is a PdfName and the value an Integer. At least the /Contents must be present
     * @throws IOException on error
     */
    protected void preClose(Map<PdfName, Integer> exclusionSizes) throws IOException {
        if (preClosed) {
            throw new PdfException(PdfException.DocumentAlreadyPreClosed);
        }

        // TODO: add mergeVerification functionality

        preClosed = true;
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        SignatureUtil sgnUtil = new SignatureUtil(document);
        String name = getFieldName();
        boolean fieldExist = sgnUtil.doesSignatureFieldExist(name);
        acroForm.setSignatureFlags(PdfAcroForm.SIGNATURE_EXIST | PdfAcroForm.APPEND_ONLY);
        PdfSigFieldLockDictionary fieldLock = null;

        if (cryptoDictionary == null) {
            throw new PdfException(PdfException.NoCryptoDictionaryDefined);
        }

        cryptoDictionary.getPdfObject().makeIndirect(document);

        if (fieldExist) {
            PdfSignatureFormField sigField = (PdfSignatureFormField) acroForm.getField(fieldName);
            sigField.put(PdfName.V, cryptoDictionary);

            fieldLock = sigField.getSigFieldLockDictionary();

            if (fieldLock == null && this.fieldLock != null) {
                this.fieldLock.getPdfObject().makeIndirect(document);
                sigField.put(PdfName.Lock, this.fieldLock);
                fieldLock = this.fieldLock;
            }

            sigField.put(PdfName.P, document.getPage(appearance.getPageNumber()));
            sigField.put(PdfName.V, cryptoDictionary);
            PdfObject obj = sigField.getPdfObject().get(PdfName.F);
            int flags = 0;

            if (obj != null && obj.isNumber()) {
                flags = ((PdfNumber) obj).getIntValue();
            }

            flags |= PdfAnnotation.Locked;
            sigField.put(PdfName.F, new PdfNumber(flags));
            PdfDictionary ap = new PdfDictionary();
            ap.put(PdfName.N, appearance.getAppearance().getPdfObject());
            sigField.put(PdfName.AP, ap);
            sigField.setModified();
        } else {
            PdfWidgetAnnotation widget = new PdfWidgetAnnotation(document, appearance.getPageRect());
            widget.setFlags(PdfAnnotation.Print | PdfAnnotation.Locked);

            PdfSignatureFormField sigField = PdfFormField.createSignature(document);
            sigField.setFieldName(name);
            sigField.put(PdfName.V, cryptoDictionary);
            sigField.addKid(widget);

            if (this.fieldLock != null) {
                this.fieldLock.getPdfObject().makeIndirect(document);
                sigField.put(PdfName.Lock, this.fieldLock);
                fieldLock = this.fieldLock;
            }

            int pagen = appearance.getPageNumber();
            widget.setPage(document.getPage(pagen));
            PdfDictionary ap = widget.getAppearanceDictionary();

            if (ap == null) {
                ap = new PdfDictionary();
                widget.put(PdfName.AP, ap);
            }

            ap.put(PdfName.N, appearance.getAppearance().getPdfObject());
            acroForm.addField(sigField, document.getPage(pagen));
            acroForm.setModified(); // TODO: test this (ain't sure whether I need this)
        }

        exclusionLocations = new HashMap<>();

        PdfLiteral lit = new PdfLiteral(80);
        exclusionLocations.put(PdfName.ByteRange, lit);
        cryptoDictionary.put(PdfName.ByteRange, lit);
        for (Map.Entry<PdfName, Integer> entry: exclusionSizes.entrySet()) {
            PdfName key = entry.getKey();
            Integer v = entry.getValue();
            lit = new PdfLiteral(v.intValue());
            exclusionLocations.put(key, lit);
            cryptoDictionary.put(key, lit);
        }
        if (certificationLevel > 0)
            addDocMDP(cryptoDictionary);
        if (fieldLock != null)
            addFieldMDP(cryptoDictionary, fieldLock);
        if (signatureEvent != null)
            signatureEvent.getSignatureDictionary(cryptoDictionary);

        if (certificationLevel > 0) {
            // add DocMDP entry to root
            PdfDictionary docmdp = new PdfDictionary();
            docmdp.put(PdfName.DocMDP, cryptoDictionary.getPdfObject());
            document.getCatalog().put(PdfName.Perms, docmdp); // TODO: setModified?
        }

        document.close();

        range = new long[exclusionLocations.size() * 2];
        long byteRangePosition = exclusionLocations.get(PdfName.ByteRange).getPosition();
        exclusionLocations.remove(PdfName.ByteRange);
        int idx = 1;
        for (PdfLiteral lit1: exclusionLocations.values()) {
            long n = lit1.getPosition();
            range[idx++] = n;
            range[idx++] = lit1.getBytesCount() + n;
        }
        Arrays.sort(range, 1, range.length - 1);
        for (int k = 3; k < range.length - 2; k += 2)
            range[k] -= range[k - 1];

        if (tempFile == null) {
            bout = temporaryOS.toByteArray();
            range[range.length - 1] = bout.length - range[range.length - 2];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfOutputStream os = new PdfOutputStream(bos);
            os.write('[');
            for (int k = 0; k < range.length; ++k)
                os.writeLong(range[k]).write(' ');
            os.write(']');
            System.arraycopy(bos.toByteArray(), 0, bout, (int) byteRangePosition, bos.size());
        } else {
            try {
                raf = new RandomAccessFile(tempFile, "rw");
                long len = raf.length();
                range[range.length - 1] = len - range[range.length - 2];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfOutputStream os = new PdfOutputStream(bos);
                os.write('[');
                for (int k = 0; k < range.length; ++k)
                    os.writeLong(range[k]).write(' ');
                os.write(']');
                raf.seek(byteRangePosition);
                raf.write(bos.toByteArray(), 0, bos.size());
            }
            catch (IOException e) {
                try{raf.close();}catch(Exception ee){}
                try{tempFile.delete();}catch(Exception ee){}
                throw e;
            }
        }
    }

    /**
     * Gets the document bytes that are hashable when using external signatures.
     * The general sequence is:
     * {@link #preClose(HashMap)}, {@link #getRangeStream()} and {@link #close(PdfDictionary)}.
     *
     * @return The {@link InputStream} of bytes to be signed.
     */
    protected InputStream getRangeStream() throws IOException {
        RandomAccessSourceFactory fac = new RandomAccessSourceFactory();
        return new RASInputStream(fac.createRanged(getUnderlyingSource(), range));
    }

    /**
     * This is the last method to be called when using external signatures. The general sequence is:
     * preClose(), getDocumentBytes() and close().
     * <p>
     * update is a PdfDictionary that must have exactly the
     * same keys as the ones provided in {@link #preClose(HashMap)}.
     * @param update a PdfDictionary with the key/value that will fill the holes defined
     * in {@link #preClose(HashMap)}
     * @throws IOException on error
     */
    protected void close(PdfDictionary update) throws IOException {
        try {
            if (!preClosed)
                throw new PdfException(PdfException.DocumentMustBePreclosed);
            ByteArrayOutputStream bous = new ByteArrayOutputStream();
            PdfOutputStream os = new PdfOutputStream(bous);

            for (PdfName key: update.keySet()) {
                PdfObject obj = update.get(key);
                PdfLiteral lit = exclusionLocations.get(key);
                if (lit == null)
                    throw new IllegalArgumentException("The key didn't reserve space in preclose");
                bous.reset();
                os.write(obj);
                if (bous.size() > lit.getBytesCount())
                    throw new IllegalArgumentException("The key is too big");
                if (tempFile == null) {
                    System.arraycopy(bous.toByteArray(), 0, bout, (int) lit.getPosition(), bous.size());
                } else {
                    raf.seek(lit.getPosition());
                    raf.write(bous.toByteArray(), 0, bous.size());
                }
            }
            if (update.size() != exclusionLocations.size())
                throw new IllegalArgumentException("The update dictionary has less keys than required");
            if (tempFile == null) {
                originalOS.write(bout, 0, bout.length);
            } else {
                if (originalOS != null) {
                    raf.seek(0);
                    long length = raf.length();
                    byte buf[] = new byte[8192];
                    while (length > 0) {
                        int r = raf.read(buf, 0, (int)Math.min((long)buf.length, length));
                        if (r < 0)
                            throw new EOFException("unexpected eof");
                        originalOS.write(buf, 0, r);
                        length -= r;
                    }
                }
            }
        }
        finally {
            if (tempFile != null) {
                raf.close();

                if (originalOS != null) {
                    tempFile.delete();
                }
            }

            if (originalOS != null) {
                try {
                    originalOS.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Returns the underlying source.
     *
     * @return The underlying source
     * @throws IOException
     */
    protected RandomAccessSource getUnderlyingSource() throws IOException {
        RandomAccessSourceFactory fac = new RandomAccessSourceFactory();
        return raf == null ? fac.createSource(bout) : fac.createSource(raf);
    }

    /**
     * Adds keys to the signature dictionary that define the certification level and the permissions.
     * This method is only used for Certifying signatures.
     *
     * @param crypto the signature dictionary
     */
    protected void addDocMDP(PdfSignature crypto) {
        PdfDictionary reference = new PdfDictionary();
        PdfDictionary transformParams = new PdfDictionary();
        transformParams.put(PdfName.P, new PdfNumber(certificationLevel));
        transformParams.put(PdfName.V, new PdfName("1.2"));
        transformParams.put(PdfName.Type, PdfName.TransformParams);
        reference.put(PdfName.TransformMethod, PdfName.DocMDP);
        reference.put(PdfName.Type, PdfName.SigRef);
        reference.put(PdfName.TransformParams, transformParams);
        if (document.getPdfVersion().compareTo(PdfVersion.PDF_1_6) < 0) { // TODO: refactor
            reference.put(new PdfName("DigestValue"), new PdfString("aa"));
            PdfArray loc = new PdfArray();
            loc.add(new PdfNumber(0));
            loc.add(new PdfNumber(0));
            reference.put(new PdfName("DigestLocation"), loc);
            reference.put(new PdfName("DigestMethod"), new PdfName("MD5"));
        }
        reference.put(PdfName.Data, document.getTrailer().get(PdfName.Root));
        PdfArray types = new PdfArray();
        types.add(reference);
        crypto.put(PdfName.Reference, types);
    }

    /**
     * Adds keys to the signature dictionary that define the field permissions.
     * This method is only used for signatures that lock fields.
     *
     * @param crypto the signature dictionary
     */
    protected void addFieldMDP(PdfSignature crypto, PdfSigFieldLockDictionary fieldLock) {
        PdfDictionary reference = new PdfDictionary();
        PdfDictionary transformParams = new PdfDictionary();
        transformParams.putAll(fieldLock.getPdfObject());
        transformParams.put(PdfName.Type, PdfName.TransformParams);
        transformParams.put(PdfName.V, new PdfName("1.2"));
        reference.put(PdfName.TransformMethod, PdfName.FieldMDP);
        reference.put(PdfName.Type, PdfName.SigRef);
        reference.put(PdfName.TransformParams, transformParams);
        reference.put(new PdfName("DigestValue"), new PdfString("aa"));
        PdfArray loc = new PdfArray();
        loc.add(new PdfNumber(0));
        loc.add(new PdfNumber(0));
        reference.put(new PdfName("DigestLocation"), loc);
        reference.put(new PdfName("DigestMethod"), new PdfName("MD5"));
        reference.put(PdfName.Data, document.getTrailer().get(PdfName.Root));
        PdfArray types = crypto.getPdfObject().getAsArray(PdfName.Reference);
        if (types == null)
            types = new PdfArray();
        types.add(reference);
        crypto.put(PdfName.Reference, types);
    }

    /**
     * Get the rectangle associated to the provided widget.
     *
     * @param widget PdfWidgetAnnotation to extract the rectangle from
     * @return Rectangle
     */
    protected Rectangle getWidgetRectangle(PdfWidgetAnnotation widget) {
        PdfArray r = widget.getRectangle();
        float x = r.getAsFloat(0);
        float y = r.getAsFloat(1);
        float width = r.getAsFloat(2) - x;
        float height = r.getAsFloat(3) - y;
        return new Rectangle(x, y, width, height);
    }

    /**
     * Get the page number associated to the provided widget.
     *
     * @param widget PdfWidgetAnnotation from which to extract the page number
     * @return page number
     */
    protected int getWidgetPageNumber(PdfWidgetAnnotation widget) {
        int pageNumber = 0;
        PdfDictionary pageDict = widget.getPdfObject().getAsDictionary(PdfName.P);
        if (pageDict != null) {
            pageNumber = document.getCatalog().getPageNum(pageDict);
        } else {
            for (int i = 1; i <= document.getNumOfPages() && pageNumber == 0; ++i) {
                PdfArray annots = document.getPage(i).getPdfObject().getAsArray(PdfName.Annots);
                if (annots == null) continue;

                for (PdfObject obj : annots) {
                    if (obj.isIndirectReference()) {
                        if (widget.getPdfObject().getIndirectReference().equals(obj)) {
                            pageNumber = i;
                            break;
                        }
                    }
                }
            }
        }
        return pageNumber;
    }

    /**
     * An interface to retrieve the signature dictionary for modification.
     */
    public interface SignatureEvent {

        /**
         * Allows modification of the signature dictionary.
         *
         * @param sig The signature dictionary
         */
        void getSignatureDictionary(PdfSignature sig);
    }
}