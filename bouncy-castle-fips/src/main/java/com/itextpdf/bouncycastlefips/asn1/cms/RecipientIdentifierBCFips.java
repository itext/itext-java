package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientIdentifier;

import org.bouncycastle.asn1.cms.RecipientIdentifier;

public class RecipientIdentifierBCFips extends ASN1EncodableBCFips implements IRecipientIdentifier {
    public RecipientIdentifierBCFips(RecipientIdentifier recipientIdentifier) {
        super(recipientIdentifier);
    }

    public RecipientIdentifierBCFips(IIssuerAndSerialNumber issuerAndSerialNumber) {
        super(new RecipientIdentifier(
                ((IssuerAndSerialNumberBCFips) issuerAndSerialNumber).getIssuerAndSerialNumber()));
    }

    public RecipientIdentifier getRecipientIdentifier() {
        return (RecipientIdentifier) getEncodable();
    }
}
