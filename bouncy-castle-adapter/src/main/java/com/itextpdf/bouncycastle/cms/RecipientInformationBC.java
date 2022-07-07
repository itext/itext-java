package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipient;
import com.itextpdf.commons.bouncycastle.cms.IRecipientId;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformation;

import java.util.Objects;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientInformation;

public class RecipientInformationBC implements IRecipientInformation {
    private final RecipientInformation recipientInformation;

    public RecipientInformationBC(RecipientInformation recipientInformation) {
        this.recipientInformation = recipientInformation;
    }

    public RecipientInformation getRecipientInformation() {
        return recipientInformation;
    }

    @Override
    public byte[] getContent(IRecipient recipient) throws CMSExceptionBC {
        try {
            return recipientInformation.getContent(((RecipientBC) recipient).getRecipient());
        } catch (CMSException e) {
            throw new CMSExceptionBC(e);
        }
    }

    @Override
    public IRecipientId getRID() {
        return new RecipientIdBC(recipientInformation.getRID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecipientInformationBC that = (RecipientInformationBC) o;
        return Objects.equals(recipientInformation, that.recipientInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipientInformation);
    }

    @Override
    public String toString() {
        return recipientInformation.toString();
    }
}
