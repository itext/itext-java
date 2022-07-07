package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cms.IRecipientId;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecipientIdBCFips that = (RecipientIdBCFips) o;
        return Objects.equals(recipientId, that.recipientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipientId);
    }

    @Override
    public String toString() {
        return recipientId.toString();
    }
}
