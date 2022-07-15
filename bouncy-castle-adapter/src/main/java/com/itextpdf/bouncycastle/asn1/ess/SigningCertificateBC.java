package com.itextpdf.bouncycastle.asn1.ess;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;

import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.SigningCertificate;

/**
 * Wrapper class for {@link SigningCertificate}.
 */
public class SigningCertificateBC extends ASN1EncodableBC implements ISigningCertificate {
    /**
     * Creates new wrapper instance for {@link SigningCertificate}.
     *
     * @param signingCertificate {@link SigningCertificate} to be wrapped
     */
    public SigningCertificateBC(SigningCertificate signingCertificate) {
        super(signingCertificate);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SigningCertificate}.
     */
    public SigningCertificate getSigningCertificate() {
        return (SigningCertificate) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IESSCertID[] getCerts() {
        ESSCertID[] certs = getSigningCertificate().getCerts();
        IESSCertID[] certsBC = new IESSCertID[certs.length];
        for (int i = 0; i < certsBC.length; i++) {
            certsBC[i] = new ESSCertIDBC(certs[i]);
        }
        return certsBC;
    }
}
