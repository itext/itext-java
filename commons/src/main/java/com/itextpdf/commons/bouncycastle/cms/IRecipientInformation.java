package com.itextpdf.commons.bouncycastle.cms;

/**
 * This interface represents the wrapper for RecipientInformation that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IRecipientInformation {
    /**
     * Calls actual {@code getContent} method for the wrapped RecipientInformation object.
     *
     * @param recipient wrapper for recipient object to use to recover content encryption key
     *
     * @return the content inside the EnvelopedData this RecipientInformation is associated with.
     *
     * @throws AbstractCMSException wrapped CMSException if the content-encryption/MAC key cannot be recovered.
     */
    byte[] getContent(IRecipient recipient) throws AbstractCMSException;

    /**
     * Calls actual {@code getRID} method for the wrapped RecipientInformation object.
     *
     * @return {@link IRecipientId} the wrapper for received RecipientId object.
     */
    IRecipientId getRID();
}
