package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipientId;

import org.bouncycastle.cms.RecipientId;

public class RecipientIdBCFips implements IRecipientId {
    private final RecipientId recipientId;

    public RecipientIdBCFips(RecipientId recipientId) {
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
