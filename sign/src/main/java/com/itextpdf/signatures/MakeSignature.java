package com.itextpdf.signatures;

import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.io.*;
import com.itextpdf.core.pdf.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.itextpdf.forms.PdfAcroForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that signs your PDF.
 * @author Paulo Soares
 */
public class MakeSignature {

    /**
     * The Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MakeSignature.class);

    public enum CryptoStandard {
        CMS, CADES
    }

    /**
     * Signs the document using the detached mode, CMS or CAdES equivalent.
     *
     * @param sap               the PdfSignatureAppearance
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
     * @throws NoSuchAlgorithmException
     * @throws Exception
     */
    public static void signDetached(PdfSigner sap, ExternalDigest externalDigest, ExternalSignature externalSignature, Certificate[] chain, Collection<CrlClient> crlList, OcspClient ocspClient,
                                    TSAClient tsaClient, int estimatedSize, CryptoStandard sigtype) throws IOException, GeneralSecurityException {
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
        PdfSignatureAppearance appearance = sap.getSignatureAppearance();
        appearance.setCertificate(chain[0]);
        if (sigtype == CryptoStandard.CADES) {
            sap.addDeveloperExtension(PdfDeveloperExtension.ESIC_1_7_EXTENSIONLEVEL2);
        }
        PdfSignature dic = new PdfSignature(PdfName.Adobe_PPKLite, sigtype == CryptoStandard.CADES ? PdfName.ETSI_CAdES_DETACHED : PdfName.Adbe_pkcs7_detached);
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setSignatureCreator(appearance.getSignatureCreator());
        dic.setContact(appearance.getContact());
        dic.setDate(new PdfDate(sap.getSignDate())); // time-stamp will over-rule this
        sap.setCryptoDictionary(dic);

        HashMap<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, new Integer(estimatedSize * 2 + 2));
        sap.preClose(exc);

        String hashAlgorithm = externalSignature.getHashAlgorithm();
        PdfPKCS7 sgn = new PdfPKCS7(null, chain, hashAlgorithm, null, externalDigest, false);
        InputStream data = sap.getRangeStream();
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
        sap.close(dic2);
    }

    /**
     * Processes a CRL list.
     *
     * @param cert    a Certificate if one of the CrlList implementations needs to retrieve the CRL URL from it.
     * @param crlList a list of CrlClient implementations
     * @return a collection of CRL bytes that can be embedded in a PDF.
     */
    public static Collection<byte[]> processCrl(Certificate cert, Collection<CrlClient> crlList) {
        if (crlList == null)
            return null;
        ArrayList<byte[]> crlBytes = new ArrayList<>();
        for (CrlClient cc : crlList) {
            if (cc == null)
                continue;
            LOGGER.info("Processing " + cc.getClass().getName());
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

    /**
     * Sign the document using an external container, usually a PKCS7. The signature is fully composed
     * externally, iText will just put the container inside the document.
     * @param sap the PdfSignatureAppearance
     * @param externalSignatureContainer the interface providing the actual signing
     * @param estimatedSize the reserved size for the signature
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static void signExternalContainer(PdfSigner sap, ExternalSignatureContainer externalSignatureContainer, int estimatedSize) throws GeneralSecurityException, IOException {
        PdfSignature dic = new PdfSignature(null, null);
        PdfSignatureAppearance appearance = sap.getSignatureAppearance();
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setSignatureCreator(appearance.getSignatureCreator());
        dic.setContact(appearance.getContact());
        dic.setDate(new PdfDate(sap.getSignDate())); // time-stamp will over-rule this
        externalSignatureContainer.modifySigningDictionary(dic.getPdfObject());
        sap.setCryptoDictionary(dic);

        HashMap<PdfName, Integer> exc = new HashMap<PdfName, Integer>();
        exc.put(PdfName.Contents, new Integer(estimatedSize * 2 + 2));
        sap.preClose(exc);

        InputStream data = sap.getRangeStream();
        byte[] encodedSig = externalSignatureContainer.sign(data);

        if (estimatedSize < encodedSig.length)
            throw new IOException("Not enough space");

        byte[] paddedSig = new byte[estimatedSize];
        System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.Contents, new PdfString(paddedSig).setHexWriting(true));
        sap.close(dic2);
    }

    /**
     * Signs a PDF where space was already reserved.
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
            /*throw new DocumentException("No field");*/
            // TODO: add some exception in the future
        }

        if (!signatureUtil.signatureCoversWholeDocument(fieldName)) {
/*            throw new DocumentException("Not the last signature");*/
            // TODO: add some exception in the future
        }

        PdfArray b = v.getAsArray(PdfName.ByteRange);
        long[] gaps = SignatureUtil.asLongArray(b); // TODO: refactor

        if (b.size() != 4 || gaps[0] != 0) {
            /*throw new DocumentException("Single exclusion space supported");*/
            // TODO: add some exception in the future
        }

        RandomAccessSource readerSource = document.getReader().getSafeFile().createSourceView();
        InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(readerSource, gaps));
        byte[] signedContent = externalSignatureContainer.sign(rg);
        int spaceAvailable = (int)(gaps[2] - gaps[1]) - 2;
        if ((spaceAvailable & 1) != 0) {
            /*throw new DocumentException("Gap is not a multiple of 2");*/
            // TODO: add some exception in the future
        }
        spaceAvailable /= 2;
        if (spaceAvailable < signedContent.length) {
            /*throw new DocumentException("Not enough space");*/
            // TODO: add some exception in the future
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
}