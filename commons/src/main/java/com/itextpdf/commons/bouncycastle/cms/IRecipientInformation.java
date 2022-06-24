package com.itextpdf.commons.bouncycastle.cms;

public interface IRecipientInformation {
    byte[] getContent(IRecipient recipient) throws AbstractCMSException;

    IRecipientId getRID();
}
