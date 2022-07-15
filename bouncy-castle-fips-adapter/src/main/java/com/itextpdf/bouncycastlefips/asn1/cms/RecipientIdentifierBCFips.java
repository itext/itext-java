package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientIdentifier;

import org.bouncycastle.asn1.cms.RecipientIdentifier;

/**
 * Wrapper class for {@link RecipientIdentifier}.
 */
public class RecipientIdentifierBCFips extends ASN1EncodableBCFips implements IRecipientIdentifier {
    /**
     * Creates new wrapper instance for {@link RecipientIdentifier}.
     *
     * @param recipientIdentifier {@link RecipientIdentifier} to be wrapped
     */
    public RecipientIdentifierBCFips(RecipientIdentifier recipientIdentifier) {
        super(recipientIdentifier);
    }

    /**
     * Creates new wrapper instance for {@link RecipientIdentifier}.
     *
     * @param issuerAndSerialNumber IssuerAndSerialNumber wrapper to create {@link RecipientIdentifier}
     */
    public RecipientIdentifierBCFips(IIssuerAndSerialNumber issuerAndSerialNumber) {
        super(new RecipientIdentifier(
                ((IssuerAndSerialNumberBCFips) issuerAndSerialNumber).getIssuerAndSerialNumber()));
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
