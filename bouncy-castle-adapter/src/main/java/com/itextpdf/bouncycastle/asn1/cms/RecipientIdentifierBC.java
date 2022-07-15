package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientIdentifier;

import org.bouncycastle.asn1.cms.RecipientIdentifier;

/**
 * Wrapper class for {@link RecipientIdentifier}.
 */
public class RecipientIdentifierBC extends ASN1EncodableBC implements IRecipientIdentifier {
    /**
     * Creates new wrapper instance for {@link RecipientIdentifier}.
     *
     * @param recipientIdentifier {@link RecipientIdentifier} to be wrapped
     */
    public RecipientIdentifierBC(RecipientIdentifier recipientIdentifier) {
        super(recipientIdentifier);
    }

    /**
     * Creates new wrapper instance for {@link RecipientIdentifier}.
     *
     * @param issuerAndSerialNumber IssuerAndSerialNumber wrapper to create {@link RecipientIdentifier}
     */
    public RecipientIdentifierBC(IIssuerAndSerialNumber issuerAndSerialNumber) {
        super(new RecipientIdentifier(((IssuerAndSerialNumberBC) issuerAndSerialNumber).getIssuerAndSerialNumber()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RecipientIdentifier}.
     */
    public RecipientIdentifier getRecipientIdentifier() {
        return (RecipientIdentifier) getEncodable();
    }
}
