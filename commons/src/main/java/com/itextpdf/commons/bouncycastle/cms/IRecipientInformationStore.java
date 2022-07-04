package com.itextpdf.commons.bouncycastle.cms;

import java.util.Collection;

public interface IRecipientInformationStore {
    Collection<IRecipientInformation> getRecipients();

    IRecipientInformation get(IRecipientId var1);
}
