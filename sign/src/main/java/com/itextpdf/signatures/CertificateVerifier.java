package com.itextpdf.signatures;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Superclass for a series of certificate verifiers that will typically
 * be used in a chain. It wraps another <code>CertificateVerifier</code>
 * that is the next element in the chain of which the <code>verify()</code>
 * method will be called.
 */
public class CertificateVerifier {

    /** The previous CertificateVerifier in the chain of verifiers. */
    protected CertificateVerifier verifier;

    /** Indicates if going online to verify a certificate is allowed. */
    protected boolean onlineCheckingAllowed = true;

    /**
     * Creates the final CertificateVerifier in a chain of verifiers.
     * @param verifier	the previous verifier in the chain
     */
    public CertificateVerifier(CertificateVerifier verifier) {
        this.verifier = verifier;
    }

    /**
     * Decide whether or not online checking is allowed.
     * @param onlineCheckingAllowed
     */
    public void setOnlineCheckingAllowed(boolean onlineCheckingAllowed) {
        this.onlineCheckingAllowed = onlineCheckingAllowed;
    }

    /**
     * Checks the validity of the certificate, and calls the next
     * verifier in the chain, if any.
     * @param signCert	the certificate that needs to be checked
     * @param issuerCert	its issuer
     * @param signDate		the date the certificate needs to be valid
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException, IOException {
        // Check if the certificate is valid on the signDate
        if (signDate != null)
            signCert.checkValidity(signDate);
        // Check if the signature is valid
        if (issuerCert != null) {
            signCert.verify(issuerCert.getPublicKey());
        }
        // Also in case, the certificate is self-signed
        else {
            signCert.verify(signCert.getPublicKey());
        }
        List<VerificationOK> result = new ArrayList<>();
        if (verifier != null)
            result.addAll(verifier.verify(signCert, issuerCert, signDate));
        return result;
    }
}