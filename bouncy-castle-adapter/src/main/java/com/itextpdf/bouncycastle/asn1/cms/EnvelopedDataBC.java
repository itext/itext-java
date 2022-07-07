package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1SetBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEncryptedContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEnvelopedData;
import com.itextpdf.commons.bouncycastle.asn1.cms.IOriginatorInfo;

import org.bouncycastle.asn1.cms.EnvelopedData;

public class EnvelopedDataBC extends ASN1EncodableBC implements IEnvelopedData {
    public EnvelopedDataBC(EnvelopedData envelopedData) {
        super(envelopedData);
    }

    public EnvelopedDataBC(IOriginatorInfo originatorInfo, IASN1Set set,
            IEncryptedContentInfo encryptedContentInfo, IASN1Set set1) {
        super(new EnvelopedData(
                ((OriginatorInfoBC) originatorInfo).getOriginatorInfo(),
                ((ASN1SetBC) set).getASN1Set(),
                ((EncryptedContentInfoBC) encryptedContentInfo).getEncryptedContentInfo(),
                ((ASN1SetBC) set1).getASN1Set()));
    }

    public EnvelopedData getEnvelopedData() {
        return (EnvelopedData) getEncodable();
    }
}