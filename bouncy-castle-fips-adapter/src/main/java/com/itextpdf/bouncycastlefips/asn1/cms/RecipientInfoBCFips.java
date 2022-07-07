package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cms.IKeyTransRecipientInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientInfo;

import org.bouncycastle.asn1.cms.RecipientInfo;

public class RecipientInfoBCFips extends ASN1EncodableBCFips implements IRecipientInfo {
    public RecipientInfoBCFips(RecipientInfo recipientInfo) {
        super(recipientInfo);
    }

    public RecipientInfoBCFips(IKeyTransRecipientInfo keyTransRecipientInfo) {
        super(new RecipientInfo(((KeyTransRecipientInfoBCFips) keyTransRecipientInfo).getKeyTransRecipientInfo()));
    }

    public RecipientInfo getRecipientInfo() {
        return (RecipientInfo) getEncodable();
    }
}
