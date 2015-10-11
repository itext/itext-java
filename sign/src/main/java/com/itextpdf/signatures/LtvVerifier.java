package com.itextpdf.signatures;

import com.itextpdf.core.pdf.*;
import com.itextpdf.forms.PdfAcroForm;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.itextpdf.signatures.LtvVerification.CertificateOption;

/**
 * Verifies the signatures in an LTV document.
 */
public class LtvVerifier extends RootStoreVerifier {
    /** The Logger instance */
    protected final static Logger LOGGER = LoggerFactory.getLogger(LtvVerifier.class);

    /** Do we need to check all certificate, or only the signing certificate? */
    protected CertificateOption option = CertificateOption.SIGNING_CERTIFICATE;
    /** Verify root. */
    protected boolean verifyRootCertificate = true;

    /** A document object for the revision that is being verified. */
    protected PdfDocument document;
    /** The fields in the revision that is being verified. */
    protected PdfAcroForm acroForm;
    /** The date the revision was signed, or <code>null</code> for the highest revision. */
    protected Date signDate;
    /** The signature that covers the revision. */
    protected String signatureName;
    /** The PdfPKCS7 object for the signature. */
    protected PdfPKCS7 pkcs7;
    /** Indicates if we're working with the latest revision. */
    protected boolean latestRevision = true;
    /** The document security store for the revision that is being verified */
    protected PdfDictionary dss;

    private SignatureUtil sgnUtil;

    /**
     * Creates a VerificationData object for a PdfReader
     * @param document The document we want to verify.
     * @throws GeneralSecurityException
     */
    public LtvVerifier(PdfDocument document) throws GeneralSecurityException {
        super(null);
        this.document = document;
        this.acroForm = PdfAcroForm.getAcroForm(document, true);
        this.sgnUtil = new SignatureUtil(document);
        List<String> names = sgnUtil.getSignatureNames();
        signatureName = names.get(names.size() - 1);
        this.signDate = new Date();
        pkcs7 = coversWholeDocument();
        LOGGER.info(String.format("Checking %ssignature %s", pkcs7.isTsp() ? "document-level timestamp " : "", signatureName));
    }

    /**
     * Sets an extra verifier.
     * @param verifier the verifier to set
     */
    public void setVerifier(CertificateVerifier verifier) {
        this.verifier = verifier;
    }

    /**
     * Sets the certificate option.
     * @param	option	Either CertificateOption.SIGNING_CERTIFICATE (default) or CertificateOption.WHOLE_CHAIN
     */
    public void setCertificateOption(CertificateOption option) {
        this.option = option;
    }

    /**
     * Set the verifyRootCertificate to false if you can't verify the root certificate.
     */
    public void setVerifyRootCertificate(boolean verifyRootCertificate) {
        this.verifyRootCertificate = verifyRootCertificate;
    }

    /**
     * Checks if the signature covers the whole document
     * and throws an exception if the document was altered
     * @return a PdfPKCS7 object
     * @throws GeneralSecurityException
     */
    protected PdfPKCS7 coversWholeDocument() throws GeneralSecurityException {
        PdfPKCS7 pkcs7 = sgnUtil.verifySignature(signatureName);
        if (sgnUtil.signatureCoversWholeDocument(signatureName)) {
            LOGGER.info("The timestamp covers whole document.");
        }
        else {
            throw new VerificationException(null, "Signature doesn't cover whole document.");
        }
        if (pkcs7.verify()) {
            LOGGER.info("The signed document has not been modified.");
            return pkcs7;
        }
        else {
            throw new VerificationException(null, "The document was altered after the final signature was applied.");
        }
    }

    /**
     * Verifies all the document-level timestamps and all the signatures in the document.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public List<VerificationOK> verify(List<VerificationOK> result) throws IOException, GeneralSecurityException {
        if (result == null)
            result = new ArrayList<VerificationOK>();
        while (pkcs7 != null) {
            result.addAll(verifySignature());
        }
        return result;
    }

    /**
     * Verifies a document level timestamp.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public List<VerificationOK> verifySignature() throws GeneralSecurityException, IOException {
        LOGGER.info("Verifying signature.");
        List<VerificationOK> result = new ArrayList<VerificationOK>();
        // Get the certificate chain
        Certificate[] chain = pkcs7.getSignCertificateChain();
        verifyChain(chain);
        // how many certificates in the chain do we need to check?
        int total = 1;
        if (CertificateOption.WHOLE_CHAIN.equals(option)) {
            total = chain.length;
        }
        // loop over the certificates
        X509Certificate signCert;
        X509Certificate issuerCert;
        for (int i = 0; i < total; ) {
            // the certificate to check
            signCert = (X509Certificate) chain[i++];
            // its issuer
            issuerCert = null;
            if (i < chain.length)
                issuerCert = (X509Certificate) chain[i];
            // now lets verify the certificate
            LOGGER.info(signCert.getSubjectDN().getName());
            List<VerificationOK> list = verify(signCert, issuerCert, signDate);
            if (list.size() == 0) {
                try {
                    signCert.verify(signCert.getPublicKey());
                    if (latestRevision && chain.length > 1) {
                        list.add(new VerificationOK(signCert, this.getClass(), "Root certificate in final revision"));
                    }
                    if (list.size() == 0 && verifyRootCertificate) {
                        throw new GeneralSecurityException();
                    }
                    else if (chain.length > 1)
                        list.add(new VerificationOK(signCert, this.getClass(), "Root certificate passed without checking"));
                }
                catch(GeneralSecurityException e) {
                    throw new VerificationException(signCert, "Couldn't verify with CRL or OCSP or trusted anchor");
                }
            }
            result.addAll(list);
        }
        // go to the previous revision
        switchToPreviousRevision();
        return result;
    }

    /**
     * Checks the certificates in a certificate chain:
     * are they valid on a specific date, and
     * do they chain up correctly?
     * @param chain
     * @throws GeneralSecurityException
     */
    public void verifyChain(Certificate[] chain) throws GeneralSecurityException {
        // Loop over the certificates in the chain
        for (int i = 0; i < chain.length; i++) {
            X509Certificate cert = (X509Certificate) chain[i];
            // check if the certificate was/is valid
            cert.checkValidity(signDate);
            // check if the previous certificate was issued by this certificate
            if (i > 0)
                chain[i-1].verify(chain[i].getPublicKey());
        }
        LOGGER.info("All certificates are valid on " + signDate.toString());
    }

    /**
     * Verifies certificates against a list of CRLs and OCSP responses.
     * @param signingCert
     * @param issuerCert
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     * @throws GeneralSecurityException
     * @throws IOException
     * @see com.itextpdf.text.pdf.security.RootStoreVerifier#verify(java.security.cert.X509Certificate, java.security.cert.X509Certificate)
     */
    public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert, Date signDate) throws GeneralSecurityException, IOException {
        // we'll verify agains the rootstore (if present)
        RootStoreVerifier rootStoreVerifier = new RootStoreVerifier(verifier);
        rootStoreVerifier.setRootStore(rootStore);
        // We'll verify against a list of CRLs
        CRLVerifier crlVerifier = new CRLVerifier(rootStoreVerifier, getCRLsFromDSS());
        crlVerifier.setRootStore(rootStore);
        crlVerifier.setOnlineCheckingAllowed(latestRevision || onlineCheckingAllowed);
        // We'll verify against a list of OCSPs
        OCSPVerifier ocspVerifier = new OCSPVerifier(crlVerifier, getOCSPResponsesFromDSS());
        ocspVerifier.setRootStore(rootStore);
        ocspVerifier.setOnlineCheckingAllowed(latestRevision || onlineCheckingAllowed);
        // We verify the chain
        return ocspVerifier.verify(signCert, issuerCert, signDate);
    }

    /**
     * Switches to the previous revision.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void switchToPreviousRevision() throws IOException, GeneralSecurityException {
        LOGGER.info("Switching to previous revision.");
        latestRevision = false;
        dss = document.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        Calendar cal = pkcs7.getTimeStampDate();
        if (cal == null)
            cal = pkcs7.getSignDate();
        // TODO: get date from signature
        signDate = cal.getTime();
        List<String> names = sgnUtil.getSignatureNames();
        if (names.size() > 1) {
            signatureName = names.get(names.size() - 2);
            document = new PdfDocument(new PdfReader(sgnUtil.extractRevision(signatureName)));
            this.acroForm = PdfAcroForm.getAcroForm(document, true);
            names = sgnUtil.getSignatureNames();
            signatureName = names.get(names.size() - 1);
            pkcs7 = coversWholeDocument();
            LOGGER.info(String.format("Checking %ssignature %s", pkcs7.isTsp() ? "document-level timestamp " : "", signatureName));
        }
        else {
            LOGGER.info("No signatures in revision");
            pkcs7 = null;
        }
    }

    /**
     * Gets a list of X509CRL objects from a Document Security Store.
     * @return	a list of CRLs
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public List<X509CRL> getCRLsFromDSS() throws GeneralSecurityException, IOException {
        List<X509CRL> crls = new ArrayList<X509CRL>();
        if (dss == null)
            return crls;
        PdfArray crlarray = dss.getAsArray(PdfName.CRLs);
        if (crlarray == null)
            return crls;
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        for (int i = 0; i < crlarray.size(); i++) {
            PdfStream stream = crlarray.getAsStream(i);
            X509CRL crl = (X509CRL)cf.generateCRL(new ByteArrayInputStream(stream.getBytes()));
            crls.add(crl);
        }
        return crls;
    }

    /**
     * Gets OCSP responses from the Document Security Store.
     * @return	a list of BasicOCSPResp objects
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public List<BasicOCSPResp> getOCSPResponsesFromDSS() throws IOException, GeneralSecurityException {
        List<BasicOCSPResp> ocsps = new ArrayList<BasicOCSPResp>();
        if (dss == null)
            return ocsps;
        PdfArray ocsparray = dss.getAsArray(PdfName.OCSPs);
        if (ocsparray == null)
            return ocsps;
        for (int i = 0; i < ocsparray.size(); i++) {
            PdfStream stream = ocsparray.getAsStream(i);
            OCSPResp ocspResponse = new OCSPResp(stream.getBytes());
            if (ocspResponse.getStatus() == 0)
                try {
                    ocsps.add((BasicOCSPResp) ocspResponse.getResponseObject());
                } catch (OCSPException e) {
                    throw new GeneralSecurityException(e);
                }
        }
        return ocsps;
    }
}
