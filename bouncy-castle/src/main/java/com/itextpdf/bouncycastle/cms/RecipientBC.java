package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipient;

import org.bouncycastle.cms.Recipient;

public class RecipientBC implements IRecipient {
    private final Recipient recipient;

    public RecipientBC(Recipient recipient) {
        this.recipient = recipient;
    }

    public Recipient getRecipient() {
        return recipient;
    }
}
