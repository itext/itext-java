package com.itextpdf.signatures;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Verifies a certificate against a <code>KeyStore</code>
 * containing trusted anchors.
 */
public class RootStoreVerifier extends CertificateVerifier {

    /** A key store against which certificates can be verified. */
    protected KeyStore rootStore = null;

    /**
     * Creates a RootStoreVerifier in a chain of verifiers.
     *
     * @param verifier
     *            the next verifier in the chain
     */
    public RootStoreVerifier(CertificateVerifier verifier) {
        super(verifier);
    }

    /**
     * Sets the Key Store against which a certificate can be checked.
     *
     * @param keyStore
     *            a root store
     */
    public void setRootStore(KeyStore keyStore) {
        this.rootStore = keyStore;
    }

    /**
     * Verifies a single certificate against a key store (if present).
     *
     * @param signCert
     *            the certificate to verify
     * @param issuerCert
     *            the issuer certificate
     * @param signDate
     *            the date the certificate needs to be valid
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     */
    public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert,
                                       Date signDate) throws GeneralSecurityException, IOException {
        // verify using the CertificateVerifier if root store is missing
        if (rootStore == null)
            return super.verify(signCert, issuerCert, signDate);
        try {
            List<VerificationOK> result = new ArrayList<>();
            // loop over the trusted anchors in the root store
            for (Enumeration<String> aliases = rootStore.aliases(); aliases.hasMoreElements();) {
                String alias = aliases.nextElement();
                try {
                    if (!rootStore.isCertificateEntry(alias))
                        continue;
                    X509Certificate anchor = (X509Certificate) rootStore
                            .getCertificate(alias);
                    signCert.verify(anchor.getPublicKey());
                    result.add(new VerificationOK(signCert, this.getClass(), "Certificate verified against root store."));
                    result.addAll(super.verify(signCert, issuerCert, signDate));
                    return result;
                } catch (GeneralSecurityException e) {
                    continue;
                }
            }
            result.addAll(super.verify(signCert, issuerCert, signDate));
            return result;
        } catch (GeneralSecurityException e) {
            return super.verify(signCert, issuerCert, signDate);
        }
    }
}