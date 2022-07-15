package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.ASN1OctetStringBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEncryptedContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.cms.EncryptedContentInfo;

/**
 * Wrapper class for {@link EncryptedContentInfo}.
 */
public class EncryptedContentInfoBC extends ASN1EncodableBC implements IEncryptedContentInfo {
    /**
     * Creates new wrapper instance for {@link EncryptedContentInfo}.
     *
     * @param encryptedContentInfo {@link EncryptedContentInfo} to be wrapped
     */
    public EncryptedContentInfoBC(EncryptedContentInfo encryptedContentInfo) {
        super(encryptedContentInfo);
    }

    /**
     * Creates new wrapper instance for {@link EncryptedContentInfo}.
     *
     * @param data                ASN1ObjectIdentifier wrapper
     * @param algorithmIdentifier AlgorithmIdentifier wrapper
     * @param octetString         ASN1OctetString wrapper
     */
    public EncryptedContentInfoBC(IASN1ObjectIdentifier data, IAlgorithmIdentifier algorithmIdentifier,
            IASN1OctetString octetString) {
        super(new EncryptedContentInfo(((ASN1ObjectIdentifierBC) data).getASN1ObjectIdentifier(),
                ((AlgorithmIdentifierBC) algorithmIdentifier).getAlgorithmIdentifier(),
                ((ASN1OctetStringBC) octetString).getASN1OctetString()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link EncryptedContentInfo}.
     */
    public EncryptedContentInfo getEncryptedContentInfo() {
        return (EncryptedContentInfo) getEncodable();
    }
}
