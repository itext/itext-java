package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1OctetStringBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.cms.IKeyTransRecipientInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;

/**
 * Wrapper class for {@link KeyTransRecipientInfo}.
 */
public class KeyTransRecipientInfoBC extends ASN1EncodableBC implements IKeyTransRecipientInfo {
    /**
     * Creates new wrapper instance for {@link KeyTransRecipientInfo}.
     *
     * @param keyTransRecipientInfo {@link KeyTransRecipientInfo} to be wrapped
     */
    public KeyTransRecipientInfoBC(KeyTransRecipientInfo keyTransRecipientInfo) {
        super(keyTransRecipientInfo);
    }

    /**
     * Creates new wrapper instance for {@link KeyTransRecipientInfo}.
     *
     * @param recipientIdentifier RecipientIdentifier wrapper
     * @param algorithmIdentifier AlgorithmIdentifier wrapper
     * @param octetString         ASN1OctetString wrapper
     */
    public KeyTransRecipientInfoBC(IRecipientIdentifier recipientIdentifier, IAlgorithmIdentifier algorithmIdentifier,
            IASN1OctetString octetString) {
        super(new KeyTransRecipientInfo(((RecipientIdentifierBC) recipientIdentifier).getRecipientIdentifier(),
                ((AlgorithmIdentifierBC) algorithmIdentifier).getAlgorithmIdentifier(),
                ((ASN1OctetStringBC) octetString).getASN1OctetString()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link KeyTransRecipientInfo}.
     */
    public KeyTransRecipientInfo getKeyTransRecipientInfo() {
        return (KeyTransRecipientInfo) getEncodable();
    }
}
