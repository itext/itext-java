package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
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
    public boolean match(IX509CertificateHolder holder) {
        return recipientId.match(((X509CertificateHolderBCFips) holder).getCertificateHolder());
    }
}
