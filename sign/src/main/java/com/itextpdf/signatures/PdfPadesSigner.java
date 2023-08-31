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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
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
import java.util.HashSet;
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
    
    private ITSAClient tsaClient;
    private IOcspClient ocspClient = new OcspClientBouncyCastle(null);
    private ICrlClient crlClient;
    private int estimatedSize = 0;
    private String timestampSignatureName;
    private String temporaryDirectoryPath = null;
    private IExternalDigest externalDigest = new BouncyCastleDigest();
    private final PdfSigner pdfSigner;
    private final IExternalSignature externalSignature;

    private ByteArrayOutputStream tempOutputStream;
    private File tempFile;
    private final Set<File> tempFiles = new HashSet<>();

    /**
     * Creates PdfPadesSigner instance using provided {@link PdfSigner} and {@link PrivateKey} parameters.
     * <p>
     * {@link PdfSigner} instance shall be newly created and not closed.
     * <p>
     * Same instance of {@link PdfPadesSigner} shall not be used for more than one signing operation.
     * 
     * @param pdfSigner {@link PdfSigner} to be used for main signing operation
     * @param privateKey {@link PrivateKey} private key to be used for main signing operation
     */
    public PdfPadesSigner(PdfSigner pdfSigner, PrivateKey privateKey) {
        this.pdfSigner = pdfSigner;
        this.externalSignature =
                new PrivateKeySignature(privateKey, DEFAULT_DIGEST_ALGORITHM, FACTORY.getProviderName());
    }

    /**
     * Creates PdfPadesSigner instance using provided {@link PdfSigner} and {@link IExternalSignature} parameters.
     * <p>
     * {@link PdfSigner} instance shall be newly created and not closed.
     * <p>
     * Same instance of {@link PdfPadesSigner} shall not be used for more than one signing operation.
     *
     * @param pdfSigner {@link PdfSigner} to be used for main signing operation
     * @param externalSignature {@link IExternalSignature} external signature to be used for main signing operation
     */
    public PdfPadesSigner(PdfSigner pdfSigner, IExternalSignature externalSignature) {
        this.pdfSigner = pdfSigner;
        this.externalSignature = externalSignature;
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-B Profile.
     * 
     * @param chain the chain of certificates to be used for signing operation
     * 
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineBProfile(Certificate[] chain)
            throws GeneralSecurityException, IOException {
        performSignDetached(chain, null);
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-T Profile.
     *
     * @param chain the chain of certificates to be used for signing operation
     *
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineTProfile(Certificate[] chain)
            throws GeneralSecurityException, IOException {
        createTsaClient(chain);
        performSignDetached(chain, tsaClient);
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-LT Profile.
     *
     * @param chain the chain of certificates to be used for signing operation
     *
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineLTProfile(Certificate[] chain)
            throws GeneralSecurityException, IOException {
        createTsaClient(chain);
        createCrlClient(chain);
        try {
            OutputStream originalOS = substituteOutputStream();
            performSignDetached(chain, tsaClient);
            performLtvVerification(createInputStream(), originalOS);
        } finally {
            deleteTempFiles();
        }
    }

    /**
     * Sign the document provided in {@link PdfSigner} instance with PaDES Baseline-LTA Profile.
     *
     * @param chain the chain of certificates to be used for signing operation
     *
     * @throws GeneralSecurityException in case of signing related exceptions
     * @throws IOException in case of files related exceptions
     */
    public void signWithBaselineLTAProfile(Certificate[] chain)
            throws IOException, GeneralSecurityException {
        createTsaClient(chain);
        createCrlClient(chain);
        try {
            OutputStream originalOS = substituteOutputStream();
            performSignDetached(chain, tsaClient);
            performLtvVerification(createInputStream(), createOutputStream());
            performTimestamping(originalOS);
        } finally {
            deleteTempFiles();
        }
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
     * Set the name to be used for timestamp signature creation.
     * <p>
     * This setter is only relevant if {@link PdfPadesSigner#signWithBaselineLTAProfile(Certificate[])} method is used.
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
     * Set {@link ITSAClient} to be used for both timestamp signature and usual signature.
     * <p>
     * This setter is only relevant if Baseline-T Profile level or higher is used.
     * <p>
     * If none is set, there will be an attempt to create default TSA Client instance using the certificate chain.
     * 
     * @param tsaClient {@link ITSAClient} instance to be used for timestamping
     * 
     * @return same instance of {@link PdfPadesSigner}
     */
    public PdfPadesSigner setTsaClient(ITSAClient tsaClient) {
        this.tsaClient = tsaClient;
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

    private void performTimestamping(OutputStream outputStream)
            throws IOException, GeneralSecurityException {
        try (InputStream tempInputStream = createInputStream()) {
            PdfSigner timestampSigner = new PdfSigner(
                    new PdfReader(tempInputStream), outputStream, new StampingProperties().useAppendMode());
            timestampSigner.timestamp(tsaClient, timestampSignatureName);
        }
    }

    private void performSignDetached(Certificate[] chain, ITSAClient tsaClient)
            throws GeneralSecurityException, IOException {
        try {
            pdfSigner.signDetached(externalDigest, externalSignature, chain, null, null, tsaClient,
                    estimatedSize, CryptoStandard.CADES);
        } finally {
            pdfSigner.originalOS.close();
        }
    }

    private void performLtvVerification(InputStream inputStream, OutputStream outputStream)
            throws IOException, GeneralSecurityException {
        PdfReader tempReader = new PdfReader(inputStream);
        try (PdfDocument tempDocument = new PdfDocument(tempReader, new PdfWriter(outputStream),
                new StampingProperties().useAppendMode())) {
            LtvVerification ltvVerification = new LtvVerification(tempDocument);
            ltvVerification.addVerification(pdfSigner.fieldName, ocspClient, crlClient,
                    LtvVerification.CertificateOption.SIGNING_CERTIFICATE, LtvVerification.Level.OCSP_CRL,
                    LtvVerification.CertificateInclusion.YES);
            ltvVerification.merge();
        } finally {
            inputStream.close();
        }
    }

    private OutputStream substituteOutputStream() throws FileNotFoundException {
        OutputStream originalOS = pdfSigner.originalOS;
        pdfSigner.originalOS = createOutputStream();
        return originalOS;
    }

    private void deleteTempFiles() {
        for (File tempFile : tempFiles) {
            tempFile.delete();
        }
    }

    private OutputStream createOutputStream() throws FileNotFoundException {
        if (temporaryDirectoryPath != null) {
            return FileUtil.getFileOutputStream(getNextTempFile());
        }
        tempOutputStream = new ByteArrayOutputStream();
        return tempOutputStream;
    }

    private InputStream createInputStream() throws IOException {
        if (temporaryDirectoryPath != null) {
            return FileUtil.getInputStreamForFile(tempFile);
        }
        return new ByteArrayInputStream(tempOutputStream.toByteArray());
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

    private void createTsaClient(Certificate[] chain) {
        if (tsaClient == null) {
            tsaClient = getTsaClientFromChain(chain);
        }
        if (tsaClient == null) {
            throw new PdfException(MessageFormatUtil.format(
                    SignExceptionMessageConstant.DOCUMENT_CANNOT_BE_SIGNED, "TSA Client"));
        }
    }
    
    private void createCrlClient(Certificate[] chain) {
        if (crlClient == null) {
            crlClient = new CrlClientOnline(chain);
            if (((CrlClientOnline) crlClient).urls.isEmpty()) {
                throw new PdfException(MessageFormatUtil.format(
                        SignExceptionMessageConstant.DOCUMENT_CANNOT_BE_SIGNED, "CRL Client"));
            }
        }
    }

    private static ITSAClient getTsaClientFromChain(Certificate[] chain) {
        for (Certificate certificate : chain) {
            if (certificate instanceof X509Certificate) {
                X509Certificate x509Certificate = (X509Certificate) certificate;
                String tsaUrl = CertificateUtil.getTSAURL(x509Certificate);
                if (tsaUrl != null) {
                    return new TSAClientBouncyCastle(tsaUrl);
                }
            }
        }
        return null;
    }
}
