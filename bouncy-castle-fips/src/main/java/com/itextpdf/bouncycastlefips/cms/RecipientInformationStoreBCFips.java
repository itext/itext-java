package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipientId;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformation;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformationStore;

import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

public class RecipientInformationStoreBCFips implements IRecipientInformationStore {

    RecipientInformationStore recipientInformationStore;

    public RecipientInformationStoreBCFips(RecipientInformationStore recipientInformationStore) {
        this.recipientInformationStore = recipientInformationStore;
    }

    public RecipientInformationStore getRecipientInformationStore() {
        return recipientInformationStore;
    }

    @Override
    public Collection<IRecipientInformation> getRecipients() {
        ArrayList<IRecipientInformation> iRecipientInformations = new ArrayList<>();
        Collection<RecipientInformation> recipients = recipientInformationStore.getRecipients();
        for (RecipientInformation recipient : recipients) {
            iRecipientInformations.add(new RecipientInformationBCFips(recipient));
        }
        return iRecipientInformations;
    }

    @Override
    public IRecipientInformation get(IRecipientId id) {
        return new RecipientInformationBCFips(recipientInformationStore.get(((RecipientIdBCFips) id).getRecipientId()));
    }
}
