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
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.PdfSigFieldLock;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.LtvVerification.CertificateOption;
import com.itextpdf.signatures.LtvVerification.Level;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class performs signing with PaDES related profiles using provided parameters.
 */
public class PdfPadesSigner {
    private static final String TEMP_FILE_NAME = "tempPdfFile";
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String DEFAULT_DIGEST_ALGORITHM = DigestAlgorithms.SHA512;
    private static final Object LOCK_OBJECT = new Object();
    private static long increment = 0;
    
    private IOcspClient ocspClient = null;
    private ICrlClient crlClient;
    private IIssuingCertificateRetriever issuingCertificateRetriever = new IssuingCertificateRetriever();
    private int estimatedSize = 0;
    private String timestampSignatureName;
    private String temporaryDirectoryPath = null;
    private AccessPermissions accessPermissions = AccessPermissions.UNSPECIFIED;
    private PdfSigFieldLock fieldLock = null;
    private IExternalDigest externalDigest = new BouncyCastleDigest();
    private StampingProperties stampingProperties = new StampingProperties().useAppendMode();
    private StampingProperties stampingPropertiesWithMetaInfo = (StampingProperties) new StampingProperties()
            .useAppendMode().setEventCountingMetaInfo(new SignMetaInfo());

    private ByteArrayOutputStream tempOutputStream;
    private File tempFile;
    private final Set<File> tempFiles = new HashSet<>();

    private final PdfReader reader;
    private final OutputStream outputStream;

    /**
     * Create an instance of PdfPadesSigner class. One instance shall be used for one signing operation.
     * 
     * @param reader {@link PdfReader} instance to read original PDF file
     * @param outputStream {@link OutputStream} output stream to write the resulting PDF file into
     */
    public PdfPadesSigner(PdfReader reader, OutputStream outputStream) {
        this.reader = reader;
        this.outputStream = outputStream;
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-B Profile.
     * 
     * @param signerProperties {@link SignerProperties} properties to be used for main signing operation
     * @param chain the chain of certificates to be used for signing operation
     * @param externalSignature {@link IExternalSignature} instance to be used for main signing operation
     * 
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineBProfile(SignerProperties signerProperties, Certificate[] chain,
           IExternalSignature externalSignature) throws GeneralSecurityException, IOException {
        performSignDetached(signerProperties, true, externalSignature, chain, null);
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-B Profile.
     *
     * @param signerProperties {@link SignerProperties} properties to be used for main signing operation
     * @param chain the chain of certificates to be used for signing operation
     * @param privateKey {@link PrivateKey} instance to be used for main signing operation
     *
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineBProfile(SignerProperties signerProperties, Certificate[] chain, PrivateKey privateKey)
            throws GeneralSecurityException, IOException {
        IExternalSignature externalSignature =
                new PrivateKeySignature(privateKey, getDigestAlgorithm(privateKey), FACTORY.getProviderName());
        signWithBaselineBProfile(signerProperties, chain, externalSignature);
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-T Profile.
     *
     * @param signerProperties {@link SignerProperties} properties to be used for main signing operation
     * @param chain the chain of certificates to be used for signing operation
     * @param externalSignature {@link IExternalSignature} instance to be used for main signing operation
     * @param tsaClient {@link ITSAClient} instance to be used for timestamp creation
     *
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineTProfile(SignerProperties signerProperties, Certificate[] chain,
            IExternalSignature externalSignature, ITSAClient tsaClient) throws GeneralSecurityException, IOException {
        performSignDetached(signerProperties, true, externalSignature, chain, tsaClient);
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-T Profile.
     *
     * @param signerProperties {@link SignerProperties} properties to be used for main signing operation
     * @param chain the chain of certificates to be used for signing operation
     * @param privateKey {@link PrivateKey} instance to be used for main signing operation
     * @param tsaClient {@link ITSAClient} instance to be used for timestamp creation
     *
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineTProfile(SignerProperties signerProperties, Certificate[] chain, PrivateKey privateKey,
            ITSAClient tsaClient) throws GeneralSecurityException, IOException {
        IExternalSignature externalSignature =
                new PrivateKeySignature(privateKey, getDigestAlgorithm(privateKey), FACTORY.getProviderName());
        signWithBaselineTProfile(signerProperties, chain, externalSignature, tsaClient);
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-LT Profile.
     *
     * @param signerProperties {@link SignerProperties} properties to be used for main signing operation
     * @param chain the chain of certificates to be used for signing operation
     * @param externalSignature {@link IExternalSignature} instance to be used for main signing operation
     * @param tsaClient {@link ITSAClient} instance to be used for timestamp creation
     *
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineLTProfile(SignerProperties signerProperties, Certificate[] chain,
            IExternalSignature externalSignature, ITSAClient tsaClient) throws GeneralSecurityException, IOException {
        createRevocationClients(chain[0], true);
        try {
            performSignDetached(signerProperties, false, externalSignature, chain, tsaClient);
            try (InputStream inputStream = createInputStream();
                    PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputStream),
                            new PdfWriter(outputStream), stampingPropertiesWithMetaInfo)) {
                performLtvVerification(pdfDocument, Collections.singletonList(signerProperties.getFieldName()),
                        LtvVerification.RevocationDataNecessity.REQUIRED_FOR_SIGNING_CERTIFICATE);
            }
        } finally {
            deleteTempFiles();
        }
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-LT Profile.
     *
     * @param signerProperties {@link SignerProperties} properties to be used for main signing operation
     * @param chain the chain of certificates to be used for signing operation
     * @param privateKey {@link PrivateKey} instance to be used for main signing operation
     * @param tsaClient {@link ITSAClient} instance to be used for timestamp creation
     *
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineLTProfile(SignerProperties signerProperties, Certificate[] chain, PrivateKey privateKey,
            ITSAClient tsaClient) throws GeneralSecurityException, IOException {
        IExternalSignature externalSignature =
                new PrivateKeySignature(privateKey, getDigestAlgorithm(privateKey), FACTORY.getProviderName());
        signWithBaselineLTProfile(signerProperties, chain, externalSignature, tsaClient);
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-LTA Profile.
     *
     * @param signerProperties {@link SignerProperties} properties to be used for main signing operation
     * @param chain the chain of certificates to be used for signing operation
     * @param externalSignature {@link IExternalSignature} instance to be used for main signing operation
     * @param tsaClient {@link ITSAClient} instance to be used for timestamp creation
     *
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineLTAProfile(SignerProperties signerProperties, Certificate[] chain,
            IExternalSignature externalSignature, ITSAClient tsaClient) throws IOException, GeneralSecurityException {
        createRevocationClients(chain[0], true);
        try {
            performSignDetached(signerProperties, false, externalSignature, chain, tsaClient);
            try (InputStream inputStream = createInputStream();
                    PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputStream),
                            new PdfWriter(createOutputStream()), stampingPropertiesWithMetaInfo)) {
                performLtvVerification(pdfDocument, Collections.singletonList(signerProperties.getFieldName()),
                        LtvVerification.RevocationDataNecessity.REQUIRED_FOR_SIGNING_CERTIFICATE);
                performTimestamping(pdfDocument, outputStream, tsaClient);
            }
        } finally {
            deleteTempFiles();
        }
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-LTA Profile.
     *
     * @param signerProperties {@link SignerProperties} properties to be used for main signing operation
     * @param chain the chain of certificates to be used for signing operation
     * @param privateKey {@link PrivateKey} instance to be used for main signing operation
     * @param tsaClient {@link ITSAClient} instance to be used for timestamp creation
     *
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineLTAProfile(SignerProperties signerProperties, Certificate[] chain,
            PrivateKey privateKey, ITSAClient tsaClient) throws GeneralSecurityException, IOException {
        IExternalSignature externalSignature =
                new PrivateKeySignature(privateKey, getDigestAlgorithm(privateKey), FACTORY.getProviderName());
        signWithBaselineLTAProfile(signerProperties, chain, externalSignature, tsaClient);
    }

    /**
     * Add revocation information for all the signatures which could be found in the provided document.
     * Also add timestamp signature on top of that.
     *
     * @param tsaClient {@link ITSAClient} TSA Client to be used for timestamp signature creation
     * 
     * @throws IOException in case of files related exceptions
     * @throws GeneralSecurityException in case of signing related exceptions
     */
    public void prolongSignatures(ITSAClient tsaClient)
            throws IOException, GeneralSecurityException {
        OutputStream documentOutputStream = tsaClient == null ? outputStream : createOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(reader, new PdfWriter(documentOutputStream),
                stampingProperties)) {
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            List<String> signatureNames = signatureUtil.getSignatureNames();
            if (signatureNames.isEmpty()) {
                throw new PdfException(SignExceptionMessageConstant.NO_SIGNATURES_TO_PROLONG);
            }
            createRevocationClients(null, false);
            performLtvVerification(pdfDocument, signatureNames, LtvVerification.RevocationDataNecessity.OPTIONAL);
            if (tsaClient != null) {
                performTimestamping(pdfDocument, outputStream, tsaClient);
            }
        }
    }

    /**
     * Add revocation information for all the signatures which could be found in the provided document.
     *
     * @throws IOException in case of files related exceptions
     * @throws GeneralSecurityException in case of signing related exceptions
     */
    public void prolongSignatures()
            throws IOException, GeneralSecurityException {
        prolongSignatures(null);
    }

    /**
     * Set temporary directory to be used for temporary files creation.
     * <p>
     * If none is set, temporary documents will be created in memory.
     * 
     * @param temporaryDirectoryPath {@link String} representing relative or absolute path to the directory
     * 
     * @return same instance of {@link PdfPadesSigner}
     */
    public PdfPadesSigner setTemporaryDirectoryPath(String temporaryDirectoryPath) {
        this.temporaryDirectoryPath = temporaryDirectoryPath;
        return this;
    }

    /**
     * Set certification level which specifies DocMDP level which is expected to be set.
     *
     * @param accessPermissions {@link AccessPermissions} certification level
     *
     * @return same instance of {@link PdfPadesSigner}
     */
    public PdfPadesSigner setCertificationLevel(AccessPermissions accessPermissions) {
        this.accessPermissions = accessPermissions;
        return this;
    }

    /**
     * Set FieldMDP rules to be applied for this signature.
     *
     * @param fieldLock {@link PdfSigFieldLock} field lock dictionary.
     *
     * @return same instance of {@link PdfPadesSigner}
     */
    public PdfPadesSigner setSignatureFieldLock(PdfSigFieldLock fieldLock) {
        this.fieldLock = fieldLock;
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
     * @return same instance of {@link PdfPadesSigner}
     */
    public PdfPadesSigner setTimestampSignatureName(String timestampSignatureName) {
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
     * @return same instance of {@link PdfPadesSigner}
     */
    public PdfPadesSigner setStampingProperties(StampingProperties stampingProperties) {
        this.stampingProperties = stampingProperties;
        if (stampingProperties.isEventCountingMetaInfoSet()) {
            this.stampingPropertiesWithMetaInfo = stampingProperties;
        }
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
     * @return same instance of {@link PdfPadesSigner}
     */
    public PdfPadesSigner setEstimatedSize(int estimatedSize) {
        this.estimatedSize = estimatedSize;
        return this;
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
     * @return same instance of {@link PdfPadesSigner}
     */
    public PdfPadesSigner setOcspClient(IOcspClient ocspClient) {
        this.ocspClient = ocspClient;
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
     * @return same instance of {@link PdfPadesSigner}
     */
    public PdfPadesSigner setCrlClient(ICrlClient crlClient) {
        this.crlClient = crlClient;
        return this;
    }

    /**
     * Set {@link IExternalDigest} to be used for main signing operation.
     * <p>
     * If none is set, {@link BouncyCastleDigest} instance will be used instead.
     * 
     * @param externalDigest {@link IExternalDigest} to be used for main signing operation.
     * 
     * @return same instance of {@link PdfPadesSigner}
     */
    public PdfPadesSigner setExternalDigest(IExternalDigest externalDigest) {
        this.externalDigest = externalDigest;
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
     * @return same instance of {@link PdfPadesSigner}.
     */
    public PdfPadesSigner setIssuingCertificateRetriever(IIssuingCertificateRetriever issuingCertificateRetriever) {
        this.issuingCertificateRetriever = issuingCertificateRetriever;
        return this;
    }

    /**
     * Set certificate list to be used by the {@link IIssuingCertificateRetriever} to retrieve missing certificates.
     *
     * @param certificateList certificate list for getting missing certificates in chain
     *                        or CRL response issuer certificates.
     *
     * @return same instance of {@link PdfPadesSigner}.
     */
    public PdfPadesSigner setTrustedCertificates(List<Certificate> certificateList) {
        this.issuingCertificateRetriever.setTrustedCertificates(certificateList);
        return this;
    }

    void performTimestamping(PdfDocument document, OutputStream outputStream, ITSAClient tsaClient)
            throws IOException, GeneralSecurityException {
        PdfSigner timestampSigner = new PdfSigner(document, outputStream, tempOutputStream, tempFile);
        timestampSigner.timestamp(tsaClient, timestampSignatureName);
    }

    PdfSigner createPdfSigner(SignerProperties signerProperties, boolean isFinal) throws IOException {
        String tempFilePath = null;
        if (temporaryDirectoryPath != null) {
            tempFilePath = getNextTempFile().getAbsolutePath();
        }
        return new PdfSigner(reader,
                isFinal ? outputStream : createOutputStream(), tempFilePath, stampingProperties, signerProperties);
    }

    void performLtvVerification(PdfDocument pdfDocument, List<String> signatureNames,
                                        LtvVerification.RevocationDataNecessity revocationDataNecessity)
            throws IOException, GeneralSecurityException {
        LtvVerification ltvVerification = new LtvVerification(pdfDocument)
                .setRevocationDataNecessity(revocationDataNecessity)
                .setIssuingCertificateRetriever(issuingCertificateRetriever);
        for (String signatureName : signatureNames) {
            ltvVerification.addVerification(signatureName, ocspClient, crlClient,
                    CertificateOption.ALL_CERTIFICATES, Level.OCSP_OPTIONAL_CRL,
                    LtvVerification.CertificateInclusion.YES);
        }
        ltvVerification.merge();
    }

    void deleteTempFiles() {
        for (File tempFile : tempFiles) {
            tempFile.delete();
        }
    }

    OutputStream createOutputStream() throws FileNotFoundException {
        if (temporaryDirectoryPath != null) {
            return FileUtil.getFileOutputStream(getNextTempFile());
        }
        tempOutputStream = new ByteArrayOutputStream();
        return tempOutputStream;
    }

    InputStream createInputStream() throws IOException {
        if (temporaryDirectoryPath != null) {
            return FileUtil.getInputStreamForFile(tempFile);
        }
        return new ByteArrayInputStream(tempOutputStream.toByteArray());
    }

    void createRevocationClients(Certificate signingCert, boolean clientsRequired) {
        if (crlClient == null && ocspClient == null && clientsRequired) {
            X509Certificate signingCertificate = (X509Certificate) signingCert;
            if (CertificateUtil.getOCSPURL(signingCertificate) == null &&
                    CertificateUtil.getCRLURLs(signingCertificate).isEmpty()) {
                throw new PdfException(SignExceptionMessageConstant.DEFAULT_CLIENTS_CANNOT_BE_CREATED);
            }
        }
        if (crlClient == null) {
            crlClient = new CrlClientOnline();
        }
        if (ocspClient == null) {
            ocspClient = new OcspClientBouncyCastle();
        }
    }

    private void performSignDetached(SignerProperties signerProperties, boolean isFinal,
            IExternalSignature externalSignature, Certificate[] chain, ITSAClient tsaClient)
            throws GeneralSecurityException, IOException {
        Certificate[] fullChain = issuingCertificateRetriever.retrieveMissingCertificates(chain);
        PdfSigner signer = createPdfSigner(signerProperties, isFinal);
        signer.setCertificationLevel(accessPermissions);
        signer.setFieldLockDict(fieldLock);
        try {
            signer.signDetached(externalDigest, externalSignature, fullChain, null, null, tsaClient,
                    estimatedSize, CryptoStandard.CADES);
        } finally {
            signer.originalOS.close();
        }
    }

    private File getNextTempFile() {
        if (!FileUtil.directoryExists(temporaryDirectoryPath)) {
            throw new PdfException(MessageFormatUtil.format(SignExceptionMessageConstant.PATH_IS_NOT_DIRECTORY,
                    temporaryDirectoryPath));
        }
        synchronized (LOCK_OBJECT) {
            do {
                increment++;
                tempFile = new File(temporaryDirectoryPath + "/" + TEMP_FILE_NAME + increment + ".pdf");
            } while (tempFile.exists());
            tempFiles.add(tempFile);
        }
        return tempFile;
    }

    private String getDigestAlgorithm(PrivateKey privateKey) {
        String signatureAlgorithm = SignUtils.getPrivateKeyAlgorithm(privateKey);
        switch (signatureAlgorithm) {
            case "Ed25519":
                return DigestAlgorithms.SHA512;
            case "Ed448":
                return DigestAlgorithms.SHAKE256;
            default:
                return DEFAULT_DIGEST_ALGORITHM;
        }
    }
}
