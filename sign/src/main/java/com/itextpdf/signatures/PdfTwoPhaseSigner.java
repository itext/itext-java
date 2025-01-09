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
package com.itextpdf.signatures;

import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.mac.IMacContainerLocator;
import com.itextpdf.kernel.pdf.PdfDeveloperExtension;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that prepares document and adds the signature to it while performing signing operation in two steps
 * (see {@link PadesTwoPhaseSigningHelper} for more info).
 *
 * <p>
 * Firstly, this class allows to prepare the document for signing and calculate the document digest to sign.
 * Secondly, it adds an existing signature to a PDF where space was already reserved.
 */
public class PdfTwoPhaseSigner {

    private final PdfReader reader;
    private final OutputStream outputStream;
    private IExternalDigest externalDigest;
    private StampingProperties stampingProperties = new StampingProperties().useAppendMode();
    private boolean closed;

    /**
     * Creates new {@link PdfTwoPhaseSigner} instance.
     *
     * @param reader       {@link PdfReader} instance to read the original PDF file
     * @param outputStream {@link OutputStream} output stream to write the resulting PDF file into
     */
    public PdfTwoPhaseSigner(PdfReader reader, OutputStream outputStream) {
        this.reader = reader;
        this.outputStream = outputStream;
    }

    /**
     * Prepares document for signing, calculates the document digest to sign and closes the document.
     *
     * @param signerProperties {@link SignerProperties} properties to be used for main signing operation
     * @param digestAlgorithm  the algorithm to generate the digest with
     * @param filter           PdfName of the signature handler to use when validating this signature
     * @param subFilter        PdfName that describes the encoding of the signature
     * @param estimatedSize    the estimated size of the signature, this is the size of the space reserved for
     *                         the Cryptographic Message Container
     * @param includeDate      specifies if the signing date should be set to the signature dictionary
     *
     * @return the message digest of the prepared document.
     *
     * @throws IOException              if some I/O problem occurs.
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs.
     */
    public byte[] prepareDocumentForSignature(SignerProperties signerProperties, String digestAlgorithm,
                                              PdfName filter, PdfName subFilter, int estimatedSize, boolean includeDate)
            throws IOException, GeneralSecurityException {
        MessageDigest digest;
        if (externalDigest != null) {
            digest = externalDigest.getMessageDigest(digestAlgorithm);
        } else {
            digest = SignUtils.getMessageDigest(digestAlgorithm);
        }
        return prepareDocumentForSignature(signerProperties, digest, filter, subFilter,
                estimatedSize, includeDate);
    }

    /**
     * Adds an existing signature to a PDF where space was already reserved.
     *
     * @param document      the original PDF
     * @param fieldName     the field to sign. It must be the last field
     * @param outs          the output PDF
     * @param cmsContainer  the finalized CMS container
     *
     * @throws IOException              if some I/O problem occurs.
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs.
     *
     * @deprecated
     * {@link PdfTwoPhaseSigner#addSignatureToPreparedDocument(PdfReader, String, OutputStream, CMSContainer)}
     * should be used instead.
     */
    @Deprecated
    public static void addSignatureToPreparedDocument(PdfDocument document, String fieldName, OutputStream outs,
                                                      CMSContainer cmsContainer)
            throws IOException, GeneralSecurityException {
        PdfSigner.SignatureApplier applier = new PdfSigner.SignatureApplier(document, fieldName, outs);
        applier.apply(a -> cmsContainer.serialize());
    }

    /**
     * Adds an existing signature to a PDF where space was already reserved.
     *
     * @param reader        {@link PdfReader} that reads the PDF file
     * @param fieldName     the field to sign. It must be the last field
     * @param outs          the output PDF
     * @param cmsContainer  the finalized CMS container
     *
     * @throws IOException              if some I/O problem occurs.
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs.
     */
    public static void addSignatureToPreparedDocument(PdfReader reader, String fieldName, OutputStream outs,
            CMSContainer cmsContainer) throws IOException, GeneralSecurityException {
        PdfSigner.SignatureApplier applier = new PdfSigner.SignatureApplier(reader, fieldName, outs);
        applier.apply(a -> cmsContainer.serialize());
    }

    /**
     * Adds an existing signature to a PDF where space was already reserved.
     *
     * @param document      the original PDF
     * @param fieldName     the field to sign. It must be the last field
     * @param outs          the output PDF
     * @param signedContent the bytes for the signed data
     *
     * @throws IOException              if some I/O problem occurs.
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs.
     *
     * @deprecated {@link PdfTwoPhaseSigner#addSignatureToPreparedDocument(PdfReader, String, OutputStream, byte[])}
     * should be used instead.
     */
    @Deprecated
    public static void addSignatureToPreparedDocument(PdfDocument document, String fieldName, OutputStream outs,
                                                      byte[] signedContent)
            throws IOException, GeneralSecurityException {
        PdfSigner.SignatureApplier applier = new PdfSigner.SignatureApplier(document, fieldName, outs);
        applier.apply(a -> signedContent);
    }

    /**
     * Adds an existing signature to a PDF where space was already reserved.
     *
     * @param reader        {@link PdfReader} that reads the PDF file
     * @param fieldName     the field to sign. It must be the last field
     * @param outs          the output PDF
     * @param signedContent the bytes for the signed data
     *
     * @throws IOException              if some I/O problem occurs.
     * @throws GeneralSecurityException if some problem during apply security algorithms occurs.
     */
    public static void addSignatureToPreparedDocument(PdfReader reader, String fieldName, OutputStream outs,
            byte[] signedContent)
            throws IOException, GeneralSecurityException {
        PdfSigner.SignatureApplier applier = new PdfSigner.SignatureApplier(reader, fieldName, outs);
        applier.apply(a -> signedContent);
    }

    /**
     * Use the external digest to inject specific digest implementations
     *
     * @param externalDigest the IExternalDigest instance to use to generate Digests
     * @return same instance of {@link PdfTwoPhaseSigner}
     */
    public PdfTwoPhaseSigner setExternalDigest(IExternalDigest externalDigest) {
        this.externalDigest = externalDigest;
        return this;
    }

    /**
     * Set stamping properties to be used during main signing operation.
     * <p>
     * If none is set, stamping properties with append mode enabled will be used
     *
     * @param stampingProperties {@link StampingProperties} instance to be used during main signing operation
     *
     * @return same instance of {@link PdfTwoPhaseSigner}
     */
    public PdfTwoPhaseSigner setStampingProperties(StampingProperties stampingProperties) {
        this.stampingProperties = stampingProperties;
        return this;
    }

    PdfSigner createPdfSigner(SignerProperties signerProperties) throws IOException {
        return new PdfSigner(reader, outputStream, null, stampingProperties, signerProperties);
    }

    private byte[] prepareDocumentForSignature(SignerProperties signerProperties, MessageDigest messageDigest,
                                               PdfName filter, PdfName subFilter, int estimatedSize,
                                               boolean includeDate) throws IOException {
        if (closed) {
            throw new PdfException(SignExceptionMessageConstant.THIS_INSTANCE_OF_PDF_SIGNER_ALREADY_CLOSED);
        }
        PdfSigner pdfSigner = createPdfSigner(signerProperties);


        PdfDocument document = pdfSigner.getDocument();
        if (document.getPdfVersion().compareTo(PdfVersion.PDF_2_0) < 0) {
            document.getCatalog().addDeveloperExtension(PdfDeveloperExtension.ESIC_1_7_EXTENSIONLEVEL2);
        }
        document.getCatalog().addDeveloperExtension(PdfDeveloperExtension.ISO_32002);
        document.getCatalog().addDeveloperExtension(PdfDeveloperExtension.ISO_32001);



        PdfSignature cryptoDictionary = pdfSigner.createSignatureDictionary(includeDate);
        cryptoDictionary.put(PdfName.Filter, filter);
        cryptoDictionary.put(PdfName.SubFilter, subFilter);
        pdfSigner.cryptoDictionary = cryptoDictionary;

        Map<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, estimatedSize * 2 + 2);
        pdfSigner.preClose(exc);

        InputStream data = pdfSigner.getRangeStream();
        byte[] digest = DigestAlgorithms.digest(data, messageDigest);
        byte[] paddedSig = new byte[estimatedSize];

        if (document.getDiContainer().getInstance(IMacContainerLocator.class).isMacContainerLocated()) {
            byte[] encodedSig = pdfSigner.embedMacTokenIntoSignatureContainer(paddedSig);
            if (estimatedSize < encodedSig.length) {
                throw new IOException(SignExceptionMessageConstant.NOT_ENOUGH_SPACE);
            }
            System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);
        }

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.Contents, new PdfString(paddedSig).setHexWriting(true));
        pdfSigner.close(dic2);
        pdfSigner.closed = true;
        closed = true;
        return digest;
    }

}
