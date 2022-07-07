package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipientId;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformation;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformationStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

public class RecipientInformationStoreBC implements IRecipientInformationStore {

    private final RecipientInformationStore recipientInformationStore;

    public RecipientInformationStoreBC(RecipientInformationStore recipientInformationStore) {
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
            iRecipientInformations.add(new RecipientInformationBC(recipient));
        }
        return iRecipientInformations;
    }

    @Override
    public IRecipientInformation get(IRecipientId id) {
        return new RecipientInformationBC(recipientInformationStore.get(((RecipientIdBC) id).getRecipientId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecipientInformationStoreBC that = (RecipientInformationStoreBC) o;
        return Objects.equals(recipientInformationStore, that.recipientInformationStore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipientInformationStore);
    }

    @Override
    public String toString() {
        return recipientInformationStore.toString();
    }
}
