package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipient;
import com.itextpdf.commons.bouncycastle.cms.IRecipientId;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformation;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientInformation;

public class RecipientInformationBCFips implements IRecipientInformation {
    private final RecipientInformation recipientInformation;

    public RecipientInformationBCFips(RecipientInformation recipientInformation) {
        this.recipientInformation = recipientInformation;
    }

    public RecipientInformation getRecipientInformation() {
        return recipientInformation;
    }

    @Override
    public byte[] getContent(IRecipient recipient) throws CMSExceptionBCFips {
        try {
            return recipientInformation.getContent(((RecipientBCFips) recipient).getRecipient());
        } catch (CMSException e) {
            throw new CMSExceptionBCFips(e);
        }
    }

    @Override
    public IRecipientId getRID() {
        return new RecipientIdBCFips(recipientInformation.getRID());
    }
}
