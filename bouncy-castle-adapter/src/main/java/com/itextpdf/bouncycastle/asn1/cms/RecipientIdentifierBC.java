package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientIdentifier;

import org.bouncycastle.asn1.cms.RecipientIdentifier;

public class RecipientIdentifierBC extends ASN1EncodableBC implements IRecipientIdentifier {
    public RecipientIdentifierBC(RecipientIdentifier recipientIdentifier) {
        super(recipientIdentifier);
    }

    public RecipientIdentifierBC(IIssuerAndSerialNumber issuerAndSerialNumber) {
        super(new RecipientIdentifier(((IssuerAndSerialNumberBC) issuerAndSerialNumber).getIssuerAndSerialNumber()));
    }

    public RecipientIdentifier getRecipientIdentifier() {
        return (RecipientIdentifier) getEncodable();
    }
}
