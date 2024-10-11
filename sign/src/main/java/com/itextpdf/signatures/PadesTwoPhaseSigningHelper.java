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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.cms.AlgorithmIdentifier;
import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.cms.CmsAttribute;
import com.itextpdf.signatures.cms.SignerInfo;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Helper class to perform signing operation in two steps.
 * <p>
 * Firstly {@link PadesTwoPhaseSigningHelper#createCMSContainerWithoutSignature} prepares document and placeholder
 * for future signature without actual signing process.
 * <p>
 * Secondly follow-up step signs prepared document with corresponding PAdES Baseline profile.
 */
public class PadesTwoPhaseSigningHelper {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private IOcspClient ocspClient;
    private ICrlClient crlClient;
    private ITSAClient tsaClient;
    private String temporaryDirectoryPath;
    private String timestampSignatureName;
    private StampingProperties stampingProperties = new StampingProperties().useAppendMode();
    private StampingProperties stampingPropertiesWithMetaInfo = (StampingProperties) new StampingProperties()
            .useAppendMode().setEventCountingMetaInfo(new SignMetaInfo());
    private IIssuingCertificateRetriever issuingCertificateRetriever = new IssuingCertificateRetriever();
    private int estimatedSize = -1;

    /**
     * Create instance of {@link PadesTwoPhaseSigningHelper}.
     * <p>
     * Same instance shall not be used for different signing operations, but can be used for both 
     * {@link PadesTwoPhaseSigningHelper#createCMSContainerWithoutSignature} and follow-up signing.
     */
    public PadesTwoPhaseSigningHelper() {
        // Empty constructor.
    }

    /**
     * Set {@link IOcspClient} to be used for LTV Verification.
     * <p>
     * This setter is only relevant if Baseline-LT Profile level or higher is used.
     * <p>
     * If none is set, there will be an attempt to create default OCSP Client instance using the certificate chain.
     *
     * @param ocspClient {@link IOcspClient} instance to be used for LTV Verification
     *
     * @return same instance of {@link PadesTwoPhaseSigningHelper}
     */
    public PadesTwoPhaseSigningHelper setOcspClient(IOcspClient ocspClient) {
        this.ocspClient = ocspClient;
        return this;
    }

    /**
     * Set certificate list to be used by the {@link IIssuingCertificateRetriever} to retrieve missing certificates.
     *
     * @param certificateList certificate list for getting missing certificates in chain
     *                        or CRL response issuer certificates.
     *
     * @return same instance of {@link PadesTwoPhaseSigningHelper}.
     */
    public PadesTwoPhaseSigningHelper setTrustedCertificates(List<Certificate> certificateList) {
        this.issuingCertificateRetriever.setTrustedCertificates(certificateList);
        return this;
    }

    /**
     * Set {@link ICrlClient} to be used for LTV Verification.
     * <p>
     * This setter is only relevant if Baseline-LT Profile level or higher is used.
     * <p>
     * If none is set, there will be an attempt to create default CRL Client instance using the certificate chain.
     *
     * @param crlClient {@link ICrlClient} instance to be used for LTV Verification
     *
     * @return same instance of {@link PadesTwoPhaseSigningHelper}
     */
    public PadesTwoPhaseSigningHelper setCrlClient(ICrlClient crlClient) {
        this.crlClient = crlClient;
        return this;
    }

    /**
     * Set {@link ITSAClient} to be used for timestamp signature creation.
     * <p>
     * This client has to be set for Baseline-T Profile level and higher.
     * 
     * @param tsaClient {@link ITSAClient} instance to be used for timestamp signature creation.
     * 
     * @return same instance of {@link PadesTwoPhaseSigningHelper}
     */
    public PadesTwoPhaseSigningHelper setTSAClient(ITSAClient tsaClient) {
        this.tsaClient = tsaClient;
        return this;
    }

    /**
     * Set {@link IIssuingCertificateRetriever} to be used before main signing operation.
     *
     * <p>
     * If none is set, {@link IssuingCertificateRetriever} instance will be used instead.
     *
     * @param issuingCertificateRetriever {@link IIssuingCertificateRetriever} instance to be used for getting missing
     *                                 certificates in chain or CRL response issuer certificates.
     * 
     * @return same instance of {@link PadesTwoPhaseSigningHelper}.
     */
    public PadesTwoPhaseSigningHelper setIssuingCertificateRetriever(
            IIssuingCertificateRetriever issuingCertificateRetriever) {
        this.issuingCertificateRetriever = issuingCertificateRetriever;
        return this;
    }

    /**
     * Set estimated size of a signature to be applied.
     * <p>
     * This parameter represents estimated amount of bytes to be preserved for the signature.
     * <p>
     * If none is set, 0 will be used and the required space will be calculated during the signing.
     *
     * @param estimatedSize amount of bytes to be used as estimated value
     *
     * @return same instance of {@link PadesTwoPhaseSigningHelper}
     */
    public PadesTwoPhaseSigningHelper setEstimatedSize(int estimatedSize) {
        this.estimatedSize = estimatedSize;
        return this;
    }

    /**
     * Set temporary directory to be used for temporary files creation.
     * <p>
     * If none is set, temporary documents will be created in memory.
     *
     * @param temporaryDirectoryPath {@link String} representing relative or absolute path to the directory
     *
     * @return same instance of {@link PadesTwoPhaseSigningHelper}
     */
    public PadesTwoPhaseSigningHelper setTemporaryDirectoryPath(String temporaryDirectoryPath) {
        this.temporaryDirectoryPath = temporaryDirectoryPath;
        return this;
    }

    /**
     * Set the name to be used for timestamp signature creation.
     * <p>
     * This setter is only relevant if
     * {@link PdfPadesSigner#signWithBaselineLTAProfile} or {@link PdfPadesSigner#prolongSignatures} methods are used.
     * <p>
     * If none is set, randomly generated signature name will be used.
     *
     * @param timestampSignatureName {@link String} representing the name of a timestamp signature to be applied
     *
     * @return same instance of {@link PadesTwoPhaseSigningHelper}
     */
    public PadesTwoPhaseSigningHelper setTimestampSignatureName(String timestampSignatureName) {
        this.timestampSignatureName = timestampSignatureName;
        return this;
    }

    /**
     * Set stamping properties to be used during main signing operation.
     * <p>
     * If none is set, stamping properties with append mode enabled will be used
     *
     * @param stampingProperties {@link StampingProperties} instance to be used during main signing operation
     *
     * @return same instance of {@link PadesTwoPhaseSigningHelper}
     */
    public PadesTwoPhaseSigningHelper setStampingProperties(StampingProperties stampingProperties) {
        this.stampingProperties = stampingProperties;
        if (stampingProperties.isEventCountingMetaInfoSet()) {
            this.stampingPropertiesWithMetaInfo = stampingProperties;
        }
        return this;
    }

    /**
     * Creates CMS container compliant with PAdES level. Prepares document and placeholder for the future signature
     * without actual signing process.
     *
     * @param certificates      certificates to be added to the CMS container
     * @param digestAlgorithm   the algorithm to generate the digest with
     * @param inputDocument     reader {@link PdfReader} instance to read original PDF file
     * @param outputStream      {@link OutputStream} output stream to write the resulting PDF file into
     * @param signerProperties  properties to be used in the signing operations
     *
     * @return prepared CMS container without signature.
     *
     * @throws IOException                  if an I/O error occurs.
     * @throws GeneralSecurityException     if some problem with signature or security occur.
     */
    public CMSContainer createCMSContainerWithoutSignature(Certificate[] certificates, String digestAlgorithm,
            PdfReader inputDocument, OutputStream outputStream, SignerProperties signerProperties)
            throws IOException, GeneralSecurityException {
        Certificate[] fullChain = issuingCertificateRetriever.retrieveMissingCertificates(certificates);
        X509Certificate[] x509FullChain = Arrays.asList(fullChain).toArray(new X509Certificate[0]);
        PdfTwoPhaseSigner pdfTwoPhaseSigner = new PdfTwoPhaseSigner(inputDocument, outputStream);
        pdfTwoPhaseSigner.setStampingProperties(stampingProperties);

        CMSContainer cms = new CMSContainer();
        SignerInfo signerInfo = new SignerInfo();
        String digestAlgorithmOid = DigestAlgorithms.getAllowedDigest(digestAlgorithm);
        signerInfo.setSigningCertificateAndAddToSignedAttributes(x509FullChain[0], digestAlgorithmOid);
        signerInfo.setDigestAlgorithm(new AlgorithmIdentifier(digestAlgorithmOid));
        cms.addCertificates(x509FullChain);
        cms.setSignerInfo(signerInfo);

        MessageDigest messageDigest = MessageDigest.getInstance(DigestAlgorithms.getDigest(digestAlgorithmOid));
        int realSignatureSize = (messageDigest.getDigestLength() + (int) cms.getSizeEstimation()) * 2 + 2;
        if (tsaClient != null) {
            realSignatureSize += tsaClient.getTokenSizeEstimate();
        }
        int expectedSignatureSize = estimatedSize < 0 ? realSignatureSize : estimatedSize;

        byte[] digestedDocumentBytes = pdfTwoPhaseSigner.prepareDocumentForSignature(signerProperties, digestAlgorithm,
                PdfName.Adobe_PPKLite, PdfName.ETSI_CAdES_DETACHED, expectedSignatureSize, true);
        signerInfo.setMessageDigest(digestedDocumentBytes);

        return cms;
    }

    /**
     * Follow-up step that signs prepared document with PAdES Baseline-B profile.
     *
     * @param externalSignature     external signature to do the actual signing
     * @param inputDocument         reader {@link PdfReader} instance to read prepared document
     * @param outputStream          the output PDF
     * @param signatureFieldName    the field to sign
     * @param cmsContainer          the finalized CMS container (e.g. created in the first step)
     *
     * @throws Exception if some exception occur.
     */
    public void signCMSContainerWithBaselineBProfile(IExternalSignature externalSignature, PdfReader inputDocument,
            OutputStream outputStream, String signatureFieldName, CMSContainer cmsContainer) throws Exception {
        setSignatureAlgorithmAndSignature(externalSignature, cmsContainer);

        try (PdfDocument document = new PdfDocument(inputDocument, stampingProperties)) {
            PdfTwoPhaseSigner.addSignatureToPreparedDocument(document, signatureFieldName, outputStream, cmsContainer);
        } finally {
            outputStream.close();
        }
    }

    /**
     * Follow-up step that signs prepared document with PAdES Baseline-T profile.
     *
     * @param externalSignature     external signature to do the actual signing
     * @param inputDocument         reader {@link PdfReader} instance to read prepared document
     * @param outputStream          the output PDF
     * @param signatureFieldName    the field to sign
     * @param cmsContainer          the finalized CMS container (e.g. created in the first step)
     *
     * @throws Exception if some exception occur.
     */
    public void signCMSContainerWithBaselineTProfile(IExternalSignature externalSignature, PdfReader inputDocument,
            OutputStream outputStream, String signatureFieldName, CMSContainer cmsContainer) throws Exception {
        byte[] signature = setSignatureAlgorithmAndSignature(externalSignature, cmsContainer);

        if (tsaClient == null) {
            throw new PdfException(SignExceptionMessageConstant.TSA_CLIENT_IS_MISSING);
        }
        byte[] signatureDigest = tsaClient.getMessageDigest().digest(signature);
        byte[] timestamp = tsaClient.getTimeStampToken(signatureDigest);
        try (IASN1InputStream tempStream = FACTORY.createASN1InputStream(new ByteArrayInputStream(timestamp))) {
            IASN1Sequence seq = FACTORY.createASN1Sequence(tempStream.readObject());
            CmsAttribute timestampAttribute = new CmsAttribute(
                    OID.AA_TIME_STAMP_TOKEN, FACTORY.createDERSet(seq));
            cmsContainer.getSignerInfo().addUnSignedAttribute(timestampAttribute);
        }

        try (PdfDocument document = new PdfDocument(inputDocument, stampingProperties)) {
            PdfTwoPhaseSigner.addSignatureToPreparedDocument(document, signatureFieldName, outputStream, cmsContainer);
        } finally {
            outputStream.close();
        }
    }

    /**
     * Follow-up step that signs prepared document with PAdES Baseline-LT profile.
     *
     * @param externalSignature     external signature to do the actual signing
     * @param inputDocument         reader {@link PdfReader} instance to read prepared document
     * @param outputStream          the output PDF
     * @param signatureFieldName    the field to sign
     * @param cmsContainer          the finalized CMS container (e.g. created in the first step)
     *
     * @throws Exception if some exception occur.
     */
    public void signCMSContainerWithBaselineLTProfile(IExternalSignature externalSignature, PdfReader inputDocument,
            OutputStream outputStream, String signatureFieldName, CMSContainer cmsContainer) throws Exception {
        PdfPadesSigner padesSigner = createPadesSigner(inputDocument, outputStream);
        padesSigner.createRevocationClients(cmsContainer.getSignerInfo().getSigningCertificate(), true);
        try (OutputStream tempOutput = padesSigner.createOutputStream()) {
            signCMSContainerWithBaselineTProfile(externalSignature, inputDocument, tempOutput, signatureFieldName,
                    cmsContainer);
            try (InputStream inputStream = padesSigner.createInputStream();
                 PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputStream), new PdfWriter(outputStream),
                         stampingPropertiesWithMetaInfo)) {
                padesSigner.performLtvVerification(pdfDocument,
                        Collections.singletonList(signatureFieldName),
                        LtvVerification.RevocationDataNecessity.REQUIRED_FOR_SIGNING_CERTIFICATE);
            }
        } finally {
            padesSigner.deleteTempFiles();
        }
    }

    /**
     * Follow-up step that signs prepared document with PAdES Baseline-LTA profile.
     *
     * @param externalSignature     external signature to do the actual signing
     * @param inputDocument         reader {@link PdfReader} instance to read prepared document
     * @param outputStream          the output PDF
     * @param signatureFieldName    the field to sign
     * @param cmsContainer          the finalized CMS container (e.g. created in the first step)
     *
     * @throws Exception if some exception occur.
     */
    public void signCMSContainerWithBaselineLTAProfile(IExternalSignature externalSignature, PdfReader inputDocument,
            OutputStream outputStream, String signatureFieldName, CMSContainer cmsContainer) throws Exception {
        PdfPadesSigner padesSigner = createPadesSigner(inputDocument, outputStream);
        padesSigner.createRevocationClients(cmsContainer.getSignerInfo().getSigningCertificate(), true);
        try (OutputStream tempOutput = padesSigner.createOutputStream()) {
            signCMSContainerWithBaselineTProfile(externalSignature, inputDocument, tempOutput, signatureFieldName,
                    cmsContainer);
            try (InputStream inputStream = padesSigner.createInputStream();
                 PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputStream),
                         new PdfWriter(padesSigner.createOutputStream()), stampingPropertiesWithMetaInfo)) {
                padesSigner.performLtvVerification(pdfDocument,
                        Collections.singletonList(signatureFieldName),
                        LtvVerification.RevocationDataNecessity.REQUIRED_FOR_SIGNING_CERTIFICATE);
                padesSigner.performTimestamping(pdfDocument, outputStream, tsaClient);
            }
        } finally {
            padesSigner.deleteTempFiles();
        }
    }

    private byte[] setSignatureAlgorithmAndSignature(IExternalSignature externalSignature, CMSContainer cmsContainer)
            throws IOException, GeneralSecurityException {
        String signatureDigest = externalSignature.getDigestAlgorithmName();
        String containerDigest = cmsContainer.getDigestAlgorithm().getAlgorithmOid();
        String providedSignatureAlgorithm = externalSignature.getSignatureAlgorithmName();
        if (!DigestAlgorithms.getAllowedDigest(signatureDigest).equals(containerDigest)) {
            throw new PdfException(MessageFormatUtil.format(
                    SignExceptionMessageConstant.DIGEST_ALGORITHMS_ARE_NOT_SAME,
                    DigestAlgorithms.getDigest(containerDigest), signatureDigest));
        }
        ISignatureMechanismParams signatureMechanismParams = externalSignature.getSignatureMechanismParameters();
        if (signatureMechanismParams == null) {
            cmsContainer.getSignerInfo().setSignatureAlgorithm(new AlgorithmIdentifier(
                    SignatureMechanisms.getSignatureMechanismOid(providedSignatureAlgorithm, signatureDigest)));
        } else {
            cmsContainer.getSignerInfo().setSignatureAlgorithm(new AlgorithmIdentifier(
                    SignatureMechanisms.getSignatureMechanismOid(providedSignatureAlgorithm, signatureDigest),
                    signatureMechanismParams.toEncodable().toASN1Primitive()));
        }

        byte[] signedAttributes = cmsContainer.getSerializedSignedAttributes();
        byte[] signature = externalSignature.sign(signedAttributes);
        cmsContainer.getSignerInfo().setSignature(signature);
        return signature;
    }

    private PdfPadesSigner createPadesSigner(PdfReader inputDocument, OutputStream outputStream) {
        PdfPadesSigner padesSigner = new PdfPadesSigner(inputDocument, outputStream);
        padesSigner.setOcspClient(ocspClient);
        padesSigner.setCrlClient(crlClient);
        padesSigner.setStampingProperties(stampingProperties);
        padesSigner.setTemporaryDirectoryPath(temporaryDirectoryPath);
        padesSigner.setTimestampSignatureName(timestampSignatureName);
        padesSigner.setIssuingCertificateRetriever(issuingCertificateRetriever);
        padesSigner.setEstimatedSize(estimatedSize);
        return padesSigner;
    }
}
