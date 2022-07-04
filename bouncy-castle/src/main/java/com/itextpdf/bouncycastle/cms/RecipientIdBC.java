package com.itextpdf.bouncycastle.cms;

import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
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
    public boolean match(IX509CertificateHolder holder) {
        return recipientId.match(((X509CertificateHolderBC) holder).getCertificateHolder());
    }
}
