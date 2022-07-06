package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1SetBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEncryptedContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEnvelopedData;
import com.itextpdf.commons.bouncycastle.asn1.cms.IOriginatorInfo;

import org.bouncycastle.asn1.cms.EnvelopedData;

public class EnvelopedDataBCFips extends ASN1EncodableBCFips implements IEnvelopedData {
    public EnvelopedDataBCFips(EnvelopedData envelopedData) {
        super(envelopedData);
    }

    public EnvelopedDataBCFips(IOriginatorInfo originatorInfo, IASN1Set set,
            IEncryptedContentInfo encryptedContentInfo, IASN1Set set1) {
        super(new EnvelopedData(
                ((OriginatorInfoBCFips) originatorInfo).getOriginatorInfo(),
                ((ASN1SetBCFips) set).getSet(),
                ((EncryptedContentInfoBCFips) encryptedContentInfo).getEncryptedContentInfo(),
                ((ASN1SetBCFips) set1).getSet()));
    }

    public EnvelopedData getEnvelopedData() {
        return (EnvelopedData) getEncodable();
    }
}