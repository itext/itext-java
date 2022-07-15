package com.itextpdf.bouncycastlefips.cert.jcajce;

import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateHolder;

import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

/**
 * Wrapper class for {@link JcaX509CertificateHolder}.
 */
public class JcaX509CertificateHolderBCFips extends X509CertificateHolderBCFips implements IJcaX509CertificateHolder {
    /**
     * Creates new wrapper instance for {@link JcaX509CertificateHolder}.
     *
     * @param certificateHolder {@link JcaX509CertificateHolder} to be wrapped
     */
    public JcaX509CertificateHolderBCFips(JcaX509CertificateHolder certificateHolder) {
        super(certificateHolder);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaX509CertificateHolder}.
     */
    public JcaX509CertificateHolder getJcaCertificateHolder() {
        return (JcaX509CertificateHolder) getCertificateHolder();
    }
}
