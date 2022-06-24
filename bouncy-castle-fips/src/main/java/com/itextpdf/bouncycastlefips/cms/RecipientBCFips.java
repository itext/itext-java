package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipient;

import org.bouncycastle.cms.Recipient;

public class RecipientBCFips implements IRecipient {
    private final Recipient recipient;

    public RecipientBCFips(Recipient recipient) {
        this.recipient = recipient;
    }

    public Recipient getRecipient() {
        return recipient;
    }
}
