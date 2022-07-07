package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEncryptedContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.cms.EncryptedContentInfo;

public class EncryptedContentInfoBCFips extends ASN1EncodableBCFips implements IEncryptedContentInfo {
    public EncryptedContentInfoBCFips(EncryptedContentInfo encryptedContentInfo) {
        super(encryptedContentInfo);
    }

    public EncryptedContentInfoBCFips(IASN1ObjectIdentifier data, IAlgorithmIdentifier algorithmIdentifier,
            IASN1OctetString octetString) {
        super(new EncryptedContentInfo(((ASN1ObjectIdentifierBCFips) data).getASN1ObjectIdentifier(),
                ((AlgorithmIdentifierBCFips) algorithmIdentifier).getAlgorithmIdentifier(),
                ((ASN1OctetStringBCFips) octetString).getOctetString()));
    }

    public EncryptedContentInfo getEncryptedContentInfo() {
        return (EncryptedContentInfo) getEncodable();
    }
}
