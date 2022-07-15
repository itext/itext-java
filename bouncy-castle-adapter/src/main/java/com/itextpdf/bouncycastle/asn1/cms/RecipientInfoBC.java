package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.cms.IKeyTransRecipientInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientInfo;

import org.bouncycastle.asn1.cms.RecipientInfo;

/**
 * Wrapper class for {@link RecipientInfo}.
 */
public class RecipientInfoBC extends ASN1EncodableBC implements IRecipientInfo {
    /**
     * Creates new wrapper instance for {@link RecipientInfo}.
     *
     * @param recipientInfo {@link RecipientInfo} to be wrapped
     */
    public RecipientInfoBC(RecipientInfo recipientInfo) {
        super(recipientInfo);
    }

    /**
     * Creates new wrapper instance for {@link RecipientInfo}.
     *
     * @param keyTransRecipientInfo KeyTransRecipientInfo to create {@link RecipientInfo}
     */
    public RecipientInfoBC(IKeyTransRecipientInfo keyTransRecipientInfo) {
        super(new RecipientInfo(((KeyTransRecipientInfoBC) keyTransRecipientInfo).getKeyTransRecipientInfo()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RecipientInfo}.
     */
    public RecipientInfo getRecipientInfo() {
        return (RecipientInfo) getEncodable();
    }
}
