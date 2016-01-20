package com.itextpdf.signatures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Class that allows you to verify a certificate against
 * one or more Certificate Revocation Lists.
 */
public class CRLVerifier extends RootStoreVerifier {

    /** The Logger instance */
    protected final static Logger LOGGER = LoggerFactory.getLogger(CRLVerifier.class);

    /** The list of CRLs to check for revocation date. */
    List<X509CRL> crls;

    /**
     * Creates a CRLVerifier instance.
     * @param verifier	the next verifier in the chain
     * @param crls a list of CRLs
     */
    public CRLVerifier(CertificateVerifier verifier, List<X509CRL> crls) {
        super(verifier);
        this.crls = crls;
    }

    /**
     * Verifies if a a valid CRL is found for the certificate.
     * If this method returns false, it doesn't mean the certificate isn't valid.
     * It means we couldn't verify it against any CRL that was available.
     * @param signCert	the certificate that needs to be checked
     * @param issuerCert	its issuer
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     * @see com.itextpdf.text.pdf.security.RootStoreVerifier#verify(java.security.cert.X509Certificate, java.security.cert.X509Certificate, java.util.Date)
     */
    public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException, IOException {
        List<VerificationOK> result = new ArrayList<>();
        int validCrlsFound = 0;
        // first check the list of CRLs that is provided
        if (crls != null) {
            for (X509CRL crl : crls) {
                if (verify(crl, signCert, issuerCert, signDate))
                    validCrlsFound++;
            }
        }
        // then check online if allowed
        boolean online = false;
        if (onlineCheckingAllowed && validCrlsFound == 0) {
            if (verify(getCRL(signCert, issuerCert), signCert, issuerCert, signDate)) {
                validCrlsFound++;
                online = true;
            }
        }
        // show how many valid CRLs were found
        LOGGER.info("Valid CRLs found: " + validCrlsFound);
        if (validCrlsFound > 0) {
            result.add(new VerificationOK(signCert, this.getClass(), "Valid CRLs found: " + validCrlsFound + (online ? " (online)" : "")));
        }
        if (verifier != null)
            result.addAll(verifier.verify(signCert, issuerCert, signDate));
        // verify using the previous verifier in the chain (if any)
        return result;
    }

    /**
     * Verifies a certificate against a single CRL.
     * @param crl	the Certificate Revocation List
     * @param signCert	a certificate that needs to be verified
     * @param issuerCert	its issuer
     * @param signDate		the sign date
     * @return true if the verification succeeded
     * @throws GeneralSecurityException
     */
    public boolean verify(X509CRL crl, X509Certificate signCert, X509Certificate issuerCert, Date signDate) throws GeneralSecurityException {
        if (crl == null || signDate == null)
            return false;
        // We only check CRLs valid on the signing date for which the issuer matches
        if (crl.getIssuerX500Principal().equals(signCert.getIssuerX500Principal())
                && signDate.after(crl.getThisUpdate()) && signDate.before(crl.getNextUpdate())) {
            // the signing certificate may not be revoked
            if (isSignatureValid(crl, issuerCert) && crl.isRevoked(signCert)) {
                throw new VerificationException(signCert, "The certificate has been revoked.");
            }
            return true;
        }
        return false;
    }

    /**
     * Fetches a CRL for a specific certificate online (without further checking).
     * @param signCert	the certificate
     * @param issuerCert	its issuer
     * @return	an X509CRL object
     */
    public X509CRL getCRL(X509Certificate signCert, X509Certificate issuerCert) {
        if (issuerCert == null)
            issuerCert = signCert;
        try {
            // gets the URL from the certificate
            String crlurl = CertificateUtil.getCRLURL(signCert);
            if (crlurl == null)
                return null;
            LOGGER.info("Getting CRL from " + crlurl);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // Creates the CRL
            return (X509CRL) cf.generateCRL(new URL(crlurl).openStream());
        }
        catch(IOException e) {
            return null;
        }
        catch(GeneralSecurityException e) {
            return null;
        }
    }

    /**
     * Checks if a CRL verifies against the issuer certificate or a trusted anchor.
     * @param crl	the CRL
     * @param crlIssuer	the trusted anchor
     * @return	true if the CRL can be trusted
     */
    public boolean isSignatureValid(X509CRL crl, X509Certificate crlIssuer) {
        // check if the CRL was issued by the issuer
        if (crlIssuer != null) {
            try {
                crl.verify(crlIssuer.getPublicKey());
                return true;
            } catch (GeneralSecurityException e) {
                LOGGER.warn("CRL not issued by the same authority as the certificate that is being checked");
            }
        }
        // check the CRL against trusted anchors
        if (rootStore == null)
            return false;
        try {
            // loop over the certificate in the key store
            for (Enumeration<String> aliases = rootStore.aliases(); aliases.hasMoreElements();) {
                String alias = aliases.nextElement();
                try {
                    if (!rootStore.isCertificateEntry(alias))
                        continue;
                    // check if the crl was signed by a trusted party (indirect CRLs)
                    X509Certificate anchor = (X509Certificate)rootStore.getCertificate(alias);
                    crl.verify(anchor.getPublicKey());
                    return true;
                } catch (GeneralSecurityException e) {
                    continue;
                }
            }
        }
        catch (GeneralSecurityException e) {
            return false;
        }
        return false;
    }
}
