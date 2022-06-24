package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipient;
import com.itextpdf.commons.bouncycastle.cms.IRecipientId;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformation;

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
}
