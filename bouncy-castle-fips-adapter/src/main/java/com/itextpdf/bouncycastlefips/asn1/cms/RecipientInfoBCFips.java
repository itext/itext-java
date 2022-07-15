package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cms.IKeyTransRecipientInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientInfo;

import org.bouncycastle.asn1.cms.RecipientInfo;

/**
 * Wrapper class for {@link RecipientInfo}.
 */
public class RecipientInfoBCFips extends ASN1EncodableBCFips implements IRecipientInfo {
    /**
     * Creates new wrapper instance for {@link RecipientInfo}.
     *
     * @param recipientInfo {@link RecipientInfo} to be wrapped
     */
    public RecipientInfoBCFips(RecipientInfo recipientInfo) {
        super(recipientInfo);
    }

    /**
     * Creates new wrapper instance for {@link RecipientInfo}.
     *
     * @param keyTransRecipientInfo KeyTransRecipientInfo to create {@link RecipientInfo}
     */
    public RecipientInfoBCFips(IKeyTransRecipientInfo keyTransRecipientInfo) {
        super(new RecipientInfo(((KeyTransRecipientInfoBCFips) keyTransRecipientInfo).getKeyTransRecipientInfo()));
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
