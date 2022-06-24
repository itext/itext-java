package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipientId;

import org.bouncycastle.cms.RecipientId;

public class RecipientIdBC implements IRecipientId {
    private final RecipientId recipientId;

    public RecipientIdBC(RecipientId recipientId) {
        this.recipientId = recipientId;
    }

    public RecipientId getRecipientId() {
        return recipientId;
    }

    @Override
    public IRecipientId getRID() {
        return null;
    }
}
