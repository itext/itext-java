package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.cms.IKeyTransRecipientInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientInfo;

import org.bouncycastle.asn1.cms.RecipientInfo;

public class RecipientInfoBC extends ASN1EncodableBC implements IRecipientInfo {
    public RecipientInfoBC(RecipientInfo recipientInfo) {
        super(recipientInfo);
    }

    public RecipientInfoBC(IKeyTransRecipientInfo keyTransRecipientInfo) {
        super(new RecipientInfo(((KeyTransRecipientInfoBC) keyTransRecipientInfo).getKeyTransRecipientInfo()));
    }

    public RecipientInfo getRecipientInfo() {
        return (RecipientInfo) getEncodable();
    }
}
