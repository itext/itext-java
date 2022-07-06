package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.cms.IKeyTransRecipientInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;

public class KeyTransRecipientInfoBCFips extends ASN1EncodableBCFips implements IKeyTransRecipientInfo {
    public KeyTransRecipientInfoBCFips(KeyTransRecipientInfo keyTransRecipientInfo) {
        super(keyTransRecipientInfo);
    }

    public KeyTransRecipientInfoBCFips(IRecipientIdentifier recipientIdentifier,
            IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        super(new KeyTransRecipientInfo(((RecipientIdentifierBCFips) recipientIdentifier).getRecipientIdentifier(),
                ((AlgorithmIdentifierBCFips) algorithmIdentifier).getAlgorithmIdentifier(),
                ((ASN1OctetStringBCFips) octetString).getOctetString()));
    }

    public KeyTransRecipientInfo getKeyTransRecipientInfo() {
        return (KeyTransRecipientInfo) getEncodable();
    }
}
