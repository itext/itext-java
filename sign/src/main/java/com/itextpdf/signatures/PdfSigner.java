/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.signatures;

import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfSigFieldLock;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.IsoKey;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDeveloperExtension;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.pdfa.PdfAAgnosticPdfDocument;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Takes care of the cryptographic options and appearances that form a signature.
 */
public class PdfSigner {

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
     * The name of the field.
     */
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
    protected ISignatureEvent signatureEvent;

    /**
     * OutputStream for the bytes of the document.
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
    protected PdfSigFieldLock fieldLock;

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
     * @param reader       PdfReader that reads the PDF file
     * @param outputStream OutputStream to write the signed PDF file
     * @param properties   {@link StampingProperties} for the signing document. Note that encryption will be
     *                     preserved regardless of what is set in properties.
     * @throws IOException if some I/O problem occurs
     */
    public PdfSigner(PdfReader reader, OutputStream outputStream, StampingProperties properties) throws IOException {
        this(reader, outputStream, null, properties);
    }

    /**
     * Creates a PdfSigner instance. Uses a {@link java.io.ByteArrayOutputStream} instead of a temporary file.
     *
     * @param reader       PdfReader that reads the PDF file
     * @param outputStream OutputStream to write the signed PDF file
     * @param path         File to which the output is temporarily written
     * @param properties   {@link StampingProperties} for the signing document. Note that encryption will be
     *                     preserved regardless of what is set in properties.
     * @throws IOException if some I/O problem occurs
     */
    public PdfSigner(PdfReader reader, OutputStream outputStream, String path, StampingProperties properties)
            throws IOException {
        StampingProperties localProps = new StampingProperties(properties).preserveEncryption();
        if (path == null) {
            temporaryOS = new ByteArrayOutputStream();
            document = initDocument(reader, new PdfWriter(temporaryOS), localProps);
        } else {
            this.tempFile = FileUtil.createTempFile(path);
            document = initDocument(reader, new PdfWriter(FileUtil.getFileOutputStream(tempFile)), localProps);
        }

        originalOS = outputStream;
        signDate = DateTimeUtil.getCurrentTimeCalendar();
        fieldName = getNewSigFieldName();
        appearance = new PdfSignatureAppearance(document, new Rectangle(0, 0), 1);
        appearance.setSignDate(signDate);

        closed = false;
    }

    protected PdfDocument initDocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
        return new PdfAAgnosticPdfDocument(reader, writer, properties);
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
     * <ul>
     * <li>If you create new signature field (either use {@link #setFieldName} with
     * the name that doesn't exist in the document or don't specify it at all) then
     * the signature is invisible by default.
     * <li>If you sign already existing field, then the signature appearance object
     * is modified to have all the properties (page num., rect etc.) consistent with
     * the state of the field (<strong>if you customized the appearance object
     * before the {@link #setFieldName} call you'll have to do it again</strong>)
     * </ul>
     * <p>
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
     *                           <li>{@link #NOT_CERTIFIED}
     *                           <li>{@link #CERTIFIED_NO_CHANGES_ALLOWED}
     *                           <li>{@link #CERTIFIED_FORM_FILLING}
     *                           <li>{@link #CERTIFIED_FORM_FILLING_AND_ANNOTATIONS}
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
    public ISignatureEvent getSignatureEvent() {
        return this.signatureEvent;
    }

    /**
     * Sets the signature event to allow modification of the signature dictionary.
     *
     * @param signatureEvent the signature event
     */
    public void setSignatureEvent(ISignatureEvent signatureEvent) {
        this.signatureEvent = signatureEvent;
    }

    /**
     * Gets a new signature field name that doesn't clash with any existing name.
     *
     * @return A new signature field name.
     */
    public String getNewSigFieldName() {
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, true);
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
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, true);

            PdfFormField field = acroForm.getField(fieldName);
            if (field != null) {
                if (!PdfName.Sig.equals(field.getFormType())) {
                    throw new IllegalArgumentException(
                            SignExceptionMessageConstant.FIELD_TYPE_IS_NOT_A_SIGNATURE_FIELD_TYPE);
                }

                if (field.getValue() != null) {
                    throw new IllegalArgumentException(SignExceptionMessageConstant.FIELD_ALREADY_SIGNED);
                }

                appearance.setFieldName(fieldName);

                List<PdfWidgetAnnotation> widgets = field.getWidgets();
                if (widgets.size() > 0) {
                    PdfWidgetAnnotation widget = widgets.get(0);
                    appearance.setPageRect(getWidgetRectangle(widget));
                    appearance.setPageNumber(getWidgetPageNumber(widget));
                }
            } else {
                // Do not allow dots for new fields
                // For existing fields dots are allowed because there it might be fully qualified name
                if (fieldName.indexOf('.') >= 0) {
                    throw new IllegalArgumentException(SignExceptionMessageConstant.FIELD_NAMES_CANNOT_CONTAIN_A_DOT);
                }
            }

            this.fieldName = fieldName;
        }
    }

    /**
     * Gets the PdfDocument associated with this instance.
     *
     * @return the PdfDocument associated with this instance
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Sets the PdfDocument.
     *
     * @param document The PdfDocument
     */
    protected void setDocument(PdfDocument document) {
        if (null == document.getReader()) {
            throw new IllegalArgumentException(SignExceptionMessageConstant.DOCUMENT_MUST_HAVE_READER);
        }
        this.document = document;
    }

    /**
     * Setter for the OutputStream.
     *
     * @param originalOS OutputStream for the bytes of the document
     */
    public void setOriginalOutputStream(OutputStream originalOS) {
        this.originalOS = originalOS;
    }

    /**
     * Getter for the field lock dictionary.
     *
     * @return Field lock dictionary.
     */
    public PdfSigFieldLock getFieldLockDict() {
        return fieldLock;
    }

    /**
     * Setter for the field lock dictionary.
     * <p>
     * <strong>Be aware:</strong> if a signature is created on an existing signature field,
     * then its /Lock dictionary takes the precedence (if it exists).
     *
     * @param fieldLock Field lock dictionary
     */
    public void setFieldLockDict(PdfSigFieldLock fieldLock) {
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
     * @throws IOException              if some I/O problem occurs
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs
     */
    public void signDetached(IExternalDigest externalDigest, IExternalSignature externalSignature, Certificate[] chain,
            Collection<ICrlClient> crlList, IOcspClient ocspClient, ITSAClient tsaClient, int estimatedSize,
            CryptoStandard sigtype) throws IOException, GeneralSecurityException {
        signDetached(externalDigest, externalSignature, chain, crlList, ocspClient, tsaClient, estimatedSize, sigtype,
                (ISignaturePolicyIdentifier) null);
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
     * @param signaturePolicy   the signature policy (for EPES signatures)
     * @throws IOException              if some I/O problem occurs
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs
     */
    public void signDetached(IExternalDigest externalDigest, IExternalSignature externalSignature, Certificate[] chain,
            Collection<ICrlClient> crlList, IOcspClient ocspClient, ITSAClient tsaClient, int estimatedSize,
            CryptoStandard sigtype, SignaturePolicyInfo signaturePolicy) throws IOException, GeneralSecurityException {
        signDetached(externalDigest, externalSignature, chain, crlList, ocspClient, tsaClient, estimatedSize, sigtype,
                signaturePolicy.toSignaturePolicyIdentifier());
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
     * @param signaturePolicy   the signature policy (for EPES signatures)
     * @throws IOException              if some I/O problem occurs
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs
     */
    public void signDetached(IExternalDigest externalDigest, IExternalSignature externalSignature, Certificate[] chain,
            Collection<ICrlClient> crlList, IOcspClient ocspClient, ITSAClient tsaClient, int estimatedSize,
            CryptoStandard sigtype, ISignaturePolicyIdentifier signaturePolicy)
            throws IOException, GeneralSecurityException {
        if (closed) {
            throw new PdfException(SignExceptionMessageConstant.THIS_INSTANCE_OF_PDF_SIGNER_ALREADY_CLOSED);
        }

        if (certificationLevel > 0 && isDocumentPdf2()) {
            if (documentContainsCertificationOrApprovalSignatures()) {
                throw new PdfException(
                        SignExceptionMessageConstant.CERTIFICATION_SIGNATURE_CREATION_FAILED_DOC_SHALL_NOT_CONTAIN_SIGS);
            }
        }

        Collection<byte[]> crlBytes = null;
        int i = 0;
        while (crlBytes == null && i < chain.length) {
            crlBytes = processCrl(chain[i++], crlList);
        }
        if (estimatedSize == 0) {
            estimatedSize = 8192;
            if (crlBytes != null) {
                for (byte[] element : crlBytes) {
                    estimatedSize += element.length + 10;
                }
            }
            if (ocspClient != null) {
                estimatedSize += 4192;
            }
            if (tsaClient != null) {
                estimatedSize += 4192;
            }
        }
        PdfSignatureAppearance appearance = getSignatureAppearance();
        appearance.setCertificate(chain[0]);
        if (sigtype == CryptoStandard.CADES && !isDocumentPdf2()) {
            addDeveloperExtension(PdfDeveloperExtension.ESIC_1_7_EXTENSIONLEVEL2);
        }
        if (externalSignature.getSignatureAlgorithmName().startsWith("Ed")) {
            addDeveloperExtension(PdfDeveloperExtension.ISO_32002);
            // Note: at this level of abstraction, we have no easy way of determining whether we are signing using a
            // specific ECDSA curve, so we can't auto-declare the extension safely, since we don't know whether
            // the curve is on the ISO/TS 32002 allowed curves list. That responsibility is delegated to the user.
        }
        String hashAlgorithm = externalSignature.getDigestAlgorithmName();
        if(hashAlgorithm.startsWith("SHA3-") || hashAlgorithm.equals(DigestAlgorithms.SHAKE256)) {
            addDeveloperExtension(PdfDeveloperExtension.ISO_32001);
        }
        PdfSignature dic = new PdfSignature(PdfName.Adobe_PPKLite, sigtype == CryptoStandard.CADES
                ? PdfName.ETSI_CAdES_DETACHED
                : PdfName.Adbe_pkcs7_detached);
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setSignatureCreator(appearance.getSignatureCreator());
        dic.setContact(appearance.getContact());
        dic.setDate(new PdfDate(getSignDate())); // time-stamp will over-rule this
        cryptoDictionary = dic;

        Map<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, estimatedSize * 2 + 2);
        preClose(exc);

        PdfPKCS7 sgn = new PdfPKCS7((PrivateKey) null, chain, hashAlgorithm, null, externalDigest, false);
        if (signaturePolicy != null) {
            sgn.setSignaturePolicy(signaturePolicy);
        }
        InputStream data = getRangeStream();
        byte[] hash = DigestAlgorithms.digest(data, SignUtils.getMessageDigest(hashAlgorithm, externalDigest));
        List<byte[]> ocspList = new ArrayList<>();
        if (chain.length > 1 && ocspClient != null) {
            for (int j = 0; j < chain.length - 1; ++j) {
                byte[] ocsp = ocspClient.getEncoded((X509Certificate) chain[j], (X509Certificate) chain[j + 1], null);
                if (ocsp != null) {
                    ocspList.add(ocsp);
                }
            }
        }
        byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, sigtype, ocspList, crlBytes);
        byte[] extSignature = externalSignature.sign(sh);
        sgn.setExternalSignatureValue(
                extSignature,
                null,
                externalSignature.getSignatureAlgorithmName(),
                externalSignature.getSignatureMechanismParameters()
        );

        byte[] encodedSig = sgn.getEncodedPKCS7(hash, sigtype, tsaClient, ocspList, crlBytes);

        if (estimatedSize < encodedSig.length) {
            throw new IOException("Not enough space");
        }

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
     * @param estimatedSize              the reserved size for the signature
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs
     * @throws IOException              if some I/O problem occurs
     */
    public void signExternalContainer(IExternalSignatureContainer externalSignatureContainer, int estimatedSize) throws GeneralSecurityException, IOException {
        if (closed) {
            throw new PdfException(SignExceptionMessageConstant.THIS_INSTANCE_OF_PDF_SIGNER_ALREADY_CLOSED);
        }

        PdfSignature dic = new PdfSignature();
        PdfSignatureAppearance appearance = getSignatureAppearance();
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setSignatureCreator(appearance.getSignatureCreator());
        dic.setContact(appearance.getContact());
        dic.setDate(new PdfDate(getSignDate())); // time-stamp will over-rule this
        externalSignatureContainer.modifySigningDictionary(dic.getPdfObject());
        cryptoDictionary = dic;

        Map<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, estimatedSize * 2 + 2);
        preClose(exc);

        InputStream data = getRangeStream();
        byte[] encodedSig = externalSignatureContainer.sign(data);

        if (estimatedSize < encodedSig.length) {
            throw new IOException(SignExceptionMessageConstant.NOT_ENOUGH_SPACE);
        }

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
     * @param tsa           the timestamp generator
     * @param signatureName the signature name or null to have a name generated
     *                      automatically
     * @throws IOException              if some I/O problem occurs
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs
     */
    public void timestamp(ITSAClient tsa, String signatureName) throws IOException, GeneralSecurityException {
        if (closed) {
            throw new PdfException(SignExceptionMessageConstant.THIS_INSTANCE_OF_PDF_SIGNER_ALREADY_CLOSED);
        }

        int contentEstimated = tsa.getTokenSizeEstimate();
        if (!isDocumentPdf2()) {
            addDeveloperExtension(PdfDeveloperExtension.ESIC_1_7_EXTENSIONLEVEL5);
        }
        setFieldName(signatureName);

        PdfSignature dic = new PdfSignature(PdfName.Adobe_PPKLite, PdfName.ETSI_RFC3161);
        dic.put(PdfName.Type, PdfName.DocTimeStamp);
        cryptoDictionary = dic;

        Map<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, contentEstimated * 2 + 2);
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
        } catch (Exception e) {
            throw new GeneralSecurityException(e.getMessage(), e);
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
     * @param document                   the original PDF
     * @param fieldName                  the field to sign. It must be the last field
     * @param outs                       the output PDF
     * @param externalSignatureContainer the signature container doing the actual signing. Only the
     *                                   method ExternalSignatureContainer.sign is used
     * @throws IOException              if some I/O problem occurs
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs
     */
    public static void signDeferred(PdfDocument document, String fieldName, OutputStream outs, IExternalSignatureContainer externalSignatureContainer) throws IOException, GeneralSecurityException {
        SignatureUtil signatureUtil = new SignatureUtil(document);
        PdfSignature signature = signatureUtil.getSignature(fieldName);
        if (signature == null) {
            throw new PdfException(SignExceptionMessageConstant.THERE_IS_NO_FIELD_IN_THE_DOCUMENT_WITH_SUCH_NAME)
                    .setMessageParams(fieldName);
        }
        if (!signatureUtil.signatureCoversWholeDocument(fieldName)) {
            throw new PdfException(
                    SignExceptionMessageConstant.SIGNATURE_WITH_THIS_NAME_IS_NOT_THE_LAST_IT_DOES_NOT_COVER_WHOLE_DOCUMENT
            ).setMessageParams(fieldName);
        }

        PdfArray b = signature.getByteRange();
        long[] gaps = b.toLongArray();

        if (b.size() != 4 || gaps[0] != 0) {
            throw new IllegalArgumentException("Single exclusion space supported");
        }

        IRandomAccessSource readerSource = document.getReader().getSafeFile().createSourceView();
        InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(readerSource, gaps));
        byte[] signedContent = externalSignatureContainer.sign(rg);
        int spaceAvailable = (int) (gaps[2] - gaps[1]) - 2;
        if ((spaceAvailable & 1) != 0) {
            throw new IllegalArgumentException("Gap is not a multiple of 2");
        }
        spaceAvailable /= 2;
        if (spaceAvailable < signedContent.length) {
            throw new PdfException(SignExceptionMessageConstant.AVAILABLE_SPACE_IS_NOT_ENOUGH_FOR_SIGNATURE);
        }
        StreamUtil.copyBytes(readerSource, 0, gaps[1] + 1, outs);
        ByteBuffer bb = new ByteBuffer(spaceAvailable * 2);
        for (byte bi : signedContent) {
            bb.appendHex(bi);
        }
        int remain = (spaceAvailable - signedContent.length) * 2;
        for (int k = 0; k < remain; ++k) {
            bb.append((byte) 48);
        }
        byte[] bbArr = bb.toByteArray();
        outs.write(bbArr);
        StreamUtil.copyBytes(readerSource, gaps[2] - 1, gaps[3] + 1, outs);
    }

    /**
     * Processes a CRL list.
     *
     * @param cert    a Certificate if one of the CrlList implementations needs to retrieve the CRL URL from it.
     * @param crlList a list of CrlClient implementations
     * @return a collection of CRL bytes that can be embedded in a PDF
     * @throws CertificateEncodingException if an encoding error occurs in {@link Certificate}.
     */
    protected Collection<byte[]> processCrl(Certificate cert, Collection<ICrlClient> crlList)
            throws CertificateEncodingException {
        if (crlList == null) {
            return null;
        }
        List<byte[]> crlBytes = new ArrayList<>();
        for (ICrlClient cc : crlList) {
            if (cc == null) {
                continue;
            }
            Collection<byte[]> b = cc.getEncoded((X509Certificate) cert, null);
            if (b == null) {
                continue;
            }
            crlBytes.addAll(b);
        }
        return crlBytes.size() == 0 ? null : crlBytes;
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
     *                       calculation. The key is a PdfName and the value an Integer. At least the /Contents must be present
     * @throws IOException on error
     */
    protected void preClose(Map<PdfName, Integer> exclusionSizes) throws IOException {
        if (preClosed) {
            throw new PdfException(SignExceptionMessageConstant.DOCUMENT_ALREADY_PRE_CLOSED);
        }
        preClosed = true;
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, true);
        SignatureUtil sgnUtil = new SignatureUtil(document);
        String name = getFieldName();
        boolean fieldExist = sgnUtil.doesSignatureFieldExist(name);
        acroForm.setSignatureFlags(PdfAcroForm.SIGNATURE_EXIST | PdfAcroForm.APPEND_ONLY);
        PdfSigFieldLock fieldLock = null;

        if (cryptoDictionary == null) {
            throw new PdfException(SignExceptionMessageConstant.NO_CRYPTO_DICTIONARY_DEFINED);
        }

        cryptoDictionary.getPdfObject().makeIndirect(document);

        if (fieldExist) {
            fieldLock = populateExistingSignatureFormField(acroForm);
        } else {
            fieldLock = createNewSignatureFormField(acroForm, name);
        }

        exclusionLocations = new HashMap<>();

        PdfLiteral lit = new PdfLiteral(80);
        exclusionLocations.put(PdfName.ByteRange, lit);
        cryptoDictionary.put(PdfName.ByteRange, lit);
        for (Map.Entry<PdfName, Integer> entry : exclusionSizes.entrySet()) {
            PdfName key = entry.getKey();
            lit = new PdfLiteral((int) entry.getValue());
            exclusionLocations.put(key, lit);
            cryptoDictionary.put(key, lit);
        }
        if (certificationLevel > 0) {
            addDocMDP(cryptoDictionary);
        }
        if (fieldLock != null) {
            addFieldMDP(cryptoDictionary, fieldLock);
        }
        if (signatureEvent != null) {
            signatureEvent.getSignatureDictionary(cryptoDictionary);
        }

        if (certificationLevel > 0) {
            // add DocMDP entry to root
            PdfDictionary docmdp = new PdfDictionary();
            docmdp.put(PdfName.DocMDP, cryptoDictionary.getPdfObject());
            document.getCatalog().put(PdfName.Perms, docmdp);
            document.getCatalog().setModified();
        }
        document.checkIsoConformance(cryptoDictionary.getPdfObject(), IsoKey.SIGNATURE);
        cryptoDictionary.getPdfObject().flush(false);
        document.close();

        range = new long[exclusionLocations.size() * 2];
        long byteRangePosition = exclusionLocations.get(PdfName.ByteRange).getPosition();
        exclusionLocations.remove(PdfName.ByteRange);
        int idx = 1;
        for (PdfLiteral lit1 : exclusionLocations.values()) {
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
            for (int k = 0; k < range.length; ++k) {
                os.writeLong(range[k]).write(' ');
            }
            os.write(']');
            System.arraycopy(bos.toByteArray(), 0, bout, (int) byteRangePosition, (int) bos.size());
        } else {
            try {
                raf = FileUtil.getRandomAccessFile(tempFile);
                long len = raf.length();
                range[range.length - 1] = len - range[range.length - 2];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfOutputStream os = new PdfOutputStream(bos);
                os.write('[');
                for (int k = 0; k < range.length; ++k) {
                    os.writeLong(range[k]).write(' ');
                }
                os.write(']');
                raf.seek(byteRangePosition);
                raf.write(bos.toByteArray(), 0, (int) bos.size());
            } catch (IOException e) {
                try {
                    raf.close();
                } catch (Exception ignored) {
                }
                try {
                    tempFile.delete();
                } catch (Exception ignored) {
                }
                throw e;
            }
        }
    }

    /**
     * Populates already existing signature form field in the acroForm object.
     * This method is called during the {@link PdfSigner#preClose(Map)} method if the signature field already exists.
     *
     * @param acroForm {@link PdfAcroForm} object in which the signature field will be populated
     * @return signature field lock dictionary
     * @throws IOException if font for the appearance dictionary cannot be created
     */
    protected PdfSigFieldLock populateExistingSignatureFormField(PdfAcroForm acroForm) throws IOException {
        PdfSignatureFormField sigField = (PdfSignatureFormField) acroForm.getField(fieldName);
        sigField.put(PdfName.V, cryptoDictionary.getPdfObject());

        PdfSigFieldLock sigFieldLock = sigField.getSigFieldLockDictionary();

        if (sigFieldLock == null && this.fieldLock != null) {
            this.fieldLock.getPdfObject().makeIndirect(document);
            sigField.put(PdfName.Lock, this.fieldLock.getPdfObject());
            sigFieldLock = this.fieldLock;
        }

        sigField.put(PdfName.P, document.getPage(appearance.getPageNumber()).getPdfObject());
        sigField.put(PdfName.V, cryptoDictionary.getPdfObject());
        PdfObject obj = sigField.getPdfObject().get(PdfName.F);
        int flags = 0;

        if (obj != null && obj.isNumber()) {
            flags = ((PdfNumber) obj).intValue();
        }

        flags |= PdfAnnotation.LOCKED;
        sigField.put(PdfName.F, new PdfNumber(flags));

        if (appearance.isInvisible()) {
            // According to the spec, appearance stream is not required if the width and height of the rectangle are 0
            sigField.remove(PdfName.AP);
        } else {
            PdfDictionary ap = new PdfDictionary();
            ap.put(PdfName.N, appearance.getAppearance().getPdfObject());
            sigField.put(PdfName.AP, ap);
        }

        sigField.setModified();

        return sigFieldLock;
    }

    /**
     * Creates new signature form field and adds it to the acroForm object.
     * This method is called during the {@link PdfSigner#preClose(Map)} method if the signature field doesn't exist.
     *
     * @param acroForm {@link PdfAcroForm} object in which new signature field will be added
     * @param name     the name of the field
     * @return signature field lock dictionary
     * @throws IOException if font for the appearance dictionary cannot be created
     */
    protected PdfSigFieldLock createNewSignatureFormField(PdfAcroForm acroForm, String name) throws IOException {
        PdfWidgetAnnotation widget = new PdfWidgetAnnotation(appearance.getPageRect());
        widget.setFlags(PdfAnnotation.PRINT | PdfAnnotation.LOCKED);

        PdfSignatureFormField sigField = new SignatureFormFieldBuilder(document, name).createSignature();
        sigField.put(PdfName.V, cryptoDictionary.getPdfObject());
        sigField.addKid(widget);

        PdfSigFieldLock sigFieldLock = sigField.getSigFieldLockDictionary();

        if (this.fieldLock != null) {
            this.fieldLock.getPdfObject().makeIndirect(document);
            sigField.put(PdfName.Lock, this.fieldLock.getPdfObject());
            sigFieldLock = this.fieldLock;
        }

        int pagen = appearance.getPageNumber();
        widget.setPage(document.getPage(pagen));

        if (appearance.isInvisible()) {
            // According to the spec, appearance stream is not required if the width and height of the rectangle are 0
            widget.remove(PdfName.AP);
        } else {
            PdfDictionary ap = widget.getAppearanceDictionary();
            if (ap == null) {
                ap = new PdfDictionary();
                widget.put(PdfName.AP, ap);
            }
            ap.put(PdfName.N, appearance.getAppearance().getPdfObject());
        }

        acroForm.addField(sigField, document.getPage(pagen));

        if (acroForm.getPdfObject().isIndirect()) {
            acroForm.setModified();
        } else {
            //Acroform dictionary is a Direct dictionary,
            //for proper flushing, catalog needs to be marked as modified
            document.getCatalog().setModified();
        }

        return sigFieldLock;
    }

    /**
     * Gets the document bytes that are hashable when using external signatures.
     * The general sequence is:
     * {@link #preClose(Map)}, {@link #getRangeStream()} and {@link #close(PdfDictionary)}.
     *
     * @return The {@link InputStream} of bytes to be signed.
     * @throws IOException if some I/O problem occurs
     */
    protected InputStream getRangeStream() throws IOException {
        RandomAccessSourceFactory fac = new RandomAccessSourceFactory();
        IRandomAccessSource randomAccessSource = fac.createRanged(getUnderlyingSource(), range);
        return new RASInputStream(randomAccessSource);
    }

    /**
     * This is the last method to be called when using external signatures. The general sequence is:
     * preClose(), getDocumentBytes() and close().
     * <p>
     * update is a PdfDictionary that must have exactly the
     * same keys as the ones provided in {@link #preClose(Map)}.
     *
     * @param update a PdfDictionary with the key/value that will fill the holes defined
     *               in {@link #preClose(Map)}
     * @throws IOException on error
     */
    protected void close(PdfDictionary update) throws IOException {
        try {
            if (!preClosed)
                throw new PdfException(SignExceptionMessageConstant.DOCUMENT_MUST_BE_PRE_CLOSED);
            ByteArrayOutputStream bous = new ByteArrayOutputStream();
            PdfOutputStream os = new PdfOutputStream(bous);

            for (PdfName key : update.keySet()) {
                PdfObject obj = update.get(key);
                PdfLiteral lit = exclusionLocations.get(key);
                if (lit == null)
                    throw new IllegalArgumentException("The key didn't reserve space in preclose");
                bous.reset();
                os.write(obj);
                if (bous.size() > lit.getBytesCount()) {
                    throw new IllegalArgumentException(SignExceptionMessageConstant.TOO_BIG_KEY);
                }
                if (tempFile == null) {
                    System.arraycopy(bous.toByteArray(), 0, bout, (int) lit.getPosition(), (int) bous.size());
                } else {
                    raf.seek(lit.getPosition());
                    raf.write(bous.toByteArray(), 0, (int) bous.size());
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
                    byte[] buf = new byte[8192];
                    while (length > 0) {
                        int r = raf.read(buf, 0, (int) Math.min((long) buf.length, length));
                        if (r < 0)
                            throw new EOFException("unexpected eof");
                        originalOS.write(buf, 0, r);
                        length -= r;
                    }
                }
            }
        } finally {
            if (tempFile != null) {
                raf.close();

                if (originalOS != null) {
                    tempFile.delete();
                }
            }

            if (originalOS != null) {
                try {
                    originalOS.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Returns the underlying source.
     *
     * @return the underlying source
     * @throws IOException if some I/O problem occurs
     */
    protected IRandomAccessSource getUnderlyingSource() throws IOException {
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
        reference.put(PdfName.Data, document.getTrailer().get(PdfName.Root));
        PdfArray types = new PdfArray();
        types.add(reference);
        crypto.put(PdfName.Reference, types);
    }

    /**
     * Adds keys to the signature dictionary that define the field permissions.
     * This method is only used for signatures that lock fields.
     *
     * @param crypto    the signature dictionary
     * @param fieldLock the {@link PdfSigFieldLock} instance specified the field lock to be set
     */
    protected void addFieldMDP(PdfSignature crypto, PdfSigFieldLock fieldLock) {
        PdfDictionary reference = new PdfDictionary();
        PdfDictionary transformParams = new PdfDictionary();
        transformParams.putAll(fieldLock.getPdfObject());
        transformParams.put(PdfName.Type, PdfName.TransformParams);
        transformParams.put(PdfName.V, new PdfName("1.2"));
        reference.put(PdfName.TransformMethod, PdfName.FieldMDP);
        reference.put(PdfName.Type, PdfName.SigRef);
        reference.put(PdfName.TransformParams, transformParams);
        reference.put(PdfName.Data, document.getTrailer().get(PdfName.Root));
        PdfArray types = crypto.getPdfObject().getAsArray(PdfName.Reference);
        if (types == null) {
            types = new PdfArray();
            crypto.put(PdfName.Reference, types);
        }

        types.add(reference);
    }

    protected boolean documentContainsCertificationOrApprovalSignatures() {
        boolean containsCertificationOrApprovalSignature = false;

        PdfDictionary urSignature = null;
        PdfDictionary catalogPerms = document.getCatalog().getPdfObject().getAsDictionary(PdfName.Perms);
        if (catalogPerms != null) {
            urSignature = catalogPerms.getAsDictionary(PdfName.UR3);
        }

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, false);
        if (acroForm != null) {
            for (Map.Entry<String, PdfFormField> entry : acroForm.getAllFormFields().entrySet()) {
                PdfDictionary fieldDict = entry.getValue().getPdfObject();
                if (!PdfName.Sig.equals(fieldDict.get(PdfName.FT)))
                    continue;
                PdfDictionary sigDict = fieldDict.getAsDictionary(PdfName.V);
                if (sigDict == null)
                    continue;
                PdfSignature pdfSignature = new PdfSignature(sigDict);
                if (pdfSignature.getContents() == null || pdfSignature.getByteRange() == null) {
                    continue;
                }

                if (!pdfSignature.getType().equals(PdfName.DocTimeStamp) && sigDict != urSignature) {
                    containsCertificationOrApprovalSignature = true;
                    break;
                }
            }
        }
        return containsCertificationOrApprovalSignature;
    }

    /**
     * Get the rectangle associated to the provided widget.
     *
     * @param widget PdfWidgetAnnotation to extract the rectangle from
     * @return Rectangle
     */
    protected Rectangle getWidgetRectangle(PdfWidgetAnnotation widget) {
        return widget.getRectangle().toRectangle();
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
            pageNumber = document.getPageNumber(pageDict);
        } else {
            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                PdfPage page = document.getPage(i);
                if (!page.isFlushed()) {
                    if (page.containsAnnotation(widget)) {
                        pageNumber = i;
                        break;
                    }
                }
            }
        }
        return pageNumber;
    }

    private boolean isDocumentPdf2() {
        return document.getPdfVersion().compareTo(PdfVersion.PDF_2_0) >= 0;
    }

    /**
     * An interface to retrieve the signature dictionary for modification.
     */
    public interface ISignatureEvent {

        /**
         * Allows modification of the signature dictionary.
         *
         * @param sig The signature dictionary
         */
        void getSignatureDictionary(PdfSignature sig);
    }
}
