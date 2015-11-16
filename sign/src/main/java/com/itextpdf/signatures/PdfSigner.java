package com.itextpdf.signatures;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.basics.io.RASInputStream;
import com.itextpdf.basics.io.RandomAccessSource;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfSigFieldLockDictionary;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;

import java.io.*;
import java.util.*;

/**
 * Takes care of the cryptographic options and appearances
 * that form a signature.
 */
public class PdfSigner {

    /** Approval signature. */
    public static final int NOT_CERTIFIED = 0;

    /** Author signature, no changes allowed. */
    public static final int CERTIFIED_NO_CHANGES_ALLOWED = 1;

    /** Author signature, form filling allowed. */
    public static final int CERTIFIED_FORM_FILLING = 2;

    /** Author signature, form filling and annotations allowed. */
    public static final int CERTIFIED_FORM_FILLING_AND_ANNOTATIONS = 3;

    /** The certification level. */
    private int certificationLevel = NOT_CERTIFIED;

    /** The name of the field. */
    private String fieldName;

    /** The file right before the signature is added (can be null). */
    private RandomAccessFile raf;

    /** The bytes of the file right before the signature is added (if raf is null) */
    private byte[] bout;

    /** Array containing the byte positions of the bytes that need to be hashed. */
    private long[] range;

    private PdfDocument document;

    /** The crypto dictionary */
    private PdfSignature cryptoDictionary;

    /** Holds value of property signatureEvent. */
    private SignatureEvent signatureEvent;

    /** OutputStream for the bytes of the stamper. */
    private OutputStream originalOS;

    private ByteArrayOutputStream temporaryOS;

    private File tempFile;

    /** Name and content of keys that can only be added in the close() method. */
    private HashMap<PdfName, PdfLiteral> exclusionLocations;

    /** Indicates if the stamper has already been pre-closed. */
    private boolean preClosed = false;

    /** Signature field lock dictionary */
    private PdfSigFieldLockDictionary fieldLock;

    private PdfSignatureAppearance appearance;

    /** Holds value of property signDate. */
    private Calendar signDate;

    public PdfSigner(PdfReader reader, PdfWriter writer, boolean append) throws IOException {
        this(reader, writer, null, append);
    }

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
    }

    /**
     * Gets the signature date.
     * @return the signature date
     */
    public java.util.Calendar getSignDate() {
        return signDate;
    }

    /**
     * Sets the signature date.
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
     * @return The certified status.
     */
    public int getCertificationLevel() {
        return this.certificationLevel;
    }

    /**
     * Sets the document's certification level.
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
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Gets the document bytes that are hashable when using external signatures.
     * The general sequence is:
     * {@link #preClose(HashMap)}, {@link #getRangeStream()} and {@link #close(PdfDictionary)}.
     * @return The {@link InputStream} of bytes to be signed.
     */
    public InputStream getRangeStream() throws IOException {
        RandomAccessSourceFactory fac = new RandomAccessSourceFactory();
        return new RASInputStream(fac.createRanged(getUnderlyingSource(), range));
    }

    /**
     * Returns the user made signature dictionary. This is the dictionary at the /V key
     * of the signature field.
     * @return The user made signature dictionary.
     */
    public PdfSignature getSignatureDictionary() {
        return cryptoDictionary;
    }

    /**
     * Sets a user made signature dictionary. This is the dictionary at the /V key
     * of the signature field.
     * @param cryptoDictionary A new user made signature dictionary.
     */
    public void setCryptoDictionary(PdfSignature cryptoDictionary) {
        this.cryptoDictionary = cryptoDictionary;
    }

    /**
     * Getter for property signatureEvent.
     * @return Value of property signatureEvent.
     */
    public SignatureEvent getSignatureEvent() {
        return this.signatureEvent;
    }

    /**
     * Sets the signature event to allow modification of the signature dictionary.
     * @param signatureEvent the signature event
     */
    public void setSignatureEvent(SignatureEvent signatureEvent) {
        this.signatureEvent = signatureEvent;
    }

    /**
     * Gets a new signature field name that doesn't clash with any existing name.
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
     * Sets the name indicating the field to be signed. The field can
     * already be presented in the document but shall not be signed.
     * If the field is not presented in the document, it will be created.
     * @param fieldName The name indicating the field to be signed.
     */
    public void setFieldName(String fieldName) {
        if (fieldName != null) {
            if (fieldName.indexOf('.') >= 0) {
                throw new IllegalArgumentException("field.names.cannot.contain.a.dot"); // TODO: correct the message
            }

            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);

            if (acroForm.getField(fieldName) != null) {
                PdfFormField field = acroForm.getField(fieldName);

                if (!PdfName.Sig.equals(field.getFormType())) {
                    throw new IllegalArgumentException("the.field.1.is.not.a.signature.field"); // TODO: correct the message
                }

                if (field.getValue() != null) {
                    throw new IllegalArgumentException("field.already.signed"); // TODO: correct the message
                }

                appearance.setFieldName(fieldName);
                // TODO: retrieve rect and page number from existing field
            }

            this.fieldName = fieldName;
        }
    }

    /**
     * Gets the <CODE>PdfStamper</CODE> associated with this instance.
     * @return the <CODE>PdfStamper</CODE> associated with this instance
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Sets the PdfStamper
     */
    void setDocument(PdfDocument document) {
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
     * @param fieldLock Field lock dictionary.
     */
    public void setFieldLockDict(PdfSigFieldLockDictionary fieldLock) {
        this.fieldLock = fieldLock;
    }

    public void addDeveloperExtension(PdfDeveloperExtension extension) {
        document.getCatalog().addDeveloperExtension(extension);
    }

    /**
     * Checks if the document is in the process of closing.
     * @return <CODE>true</CODE> if the document is in the process of closing,
     * <CODE>false</CODE> otherwise
     */
    public boolean isPreClosed() {
        return preClosed;
    }

    /**
     * This is the first method to be called when using external signatures. The general sequence is:
     * preClose(), getDocumentBytes() and close().
     * <p>
     * If calling preClose() <B>dont't</B> call PdfStamper.close().
     * <p>
     * <CODE>exclusionSizes</CODE> must contain at least
     * the <CODE>PdfName.CONTENTS</CODE> key with the size that it will take in the
     * document. Note that due to the hex string coding this size should be
     * byte_size*2+2.
     * @param exclusionSizes a <CODE>HashMap</CODE> with names and sizes to be excluded in the signature
     * calculation. The key is a <CODE>PdfName</CODE> and the value an
     * <CODE>Integer</CODE>. At least the <CODE>PdfName.CONTENTS</CODE> must be present
     * @throws IOException on error
     */
    public void preClose(HashMap<PdfName, Integer> exclusionSizes) throws IOException {
        if (preClosed) {
            throw new RuntimeException("document.already.pre.closed"); // TODO: correct the message
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
            throw new /*DocumentException*/RuntimeException("No crypto dictionary defined."); // TODO: correct the message
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
     * This is the last method to be called when using external signatures. The general sequence is:
     * preClose(), getDocumentBytes() and close().
     * <p>
     * <CODE>update</CODE> is a <CODE>PdfDictionary</CODE> that must have exactly the
     * same keys as the ones provided in {@link #preClose(HashMap)}.
     * @param update a <CODE>PdfDictionary</CODE> with the key/value that will fill the holes defined
     * in {@link #preClose(HashMap)}
     * @throws IOException on error
     */
    public void close(PdfDictionary update) throws IOException {
        try {
            if (!preClosed)
                throw new RuntimeException("Document must be preclosed"); // TODO: correct the message
            ByteArrayOutputStream bous = new ByteArrayOutputStream();
            PdfOutputStream os = new PdfOutputStream(bous);

            for (PdfName key: update.keySet()) {
                PdfObject obj = update.get(key);
                PdfLiteral lit = exclusionLocations.get(key);
                if (lit == null)
                    throw new IllegalArgumentException("the.key.1.didn.t.reserve.space.in.preclose"); // TODO: correct the message
                bous.reset();
                os.write(obj);
                if (bous.size() > lit.getBytesCount())
                    throw new IllegalArgumentException("the.key.1.is.too.big.is.2.reserved.3"); // TODO: correct the message
                if (tempFile == null) {
                    System.arraycopy(bous.toByteArray(), 0, bout, (int) lit.getPosition(), bous.size());
                } else {
                    raf.seek(lit.getPosition());
                    raf.write(bous.toByteArray(), 0, bous.size());
                }
            }
            if (update.size() != exclusionLocations.size())
                throw new IllegalArgumentException("the.update.dictionary.has.less.keys.than.required"); // TODO: correct the message
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
                            throw new EOFException("unexpected.eof"); // TODO: correct the message
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
     * @return The underlying source.
     * @throws IOException
     */
    private RandomAccessSource getUnderlyingSource() throws IOException {
        RandomAccessSourceFactory fac = new RandomAccessSourceFactory();
        return raf == null ? fac.createSource(bout) : fac.createSource(raf);
    }

    /**
     * Adds keys to the signature dictionary that define
     * the certification level and the permissions.
     * This method is only used for Certifying signatures.
     * @param crypto the signature dictionary
     */
    private void addDocMDP(PdfSignature crypto) {
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
     * Adds keys to the signature dictionary that define
     * the field permissions.
     * This method is only used for signatures that lock fields.
     * @param crypto the signature dictionary
     */
    private void addFieldMDP(PdfSignature crypto, PdfSigFieldLockDictionary fieldLock) {
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
     * An interface to retrieve the signature dictionary for modification.
     */
    public interface SignatureEvent {

        /**
         * Allows modification of the signature dictionary.
         * @param sig The signature dictionary.
         */
        void getSignatureDictionary(PdfSignature sig);
    }
}