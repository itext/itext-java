package com.itextpdf.commons.bouncycastle.cms;

import java.util.Collection;

/**
 * This interface represents the wrapper for RecipientInformationStore that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IRecipientInformationStore {
    /**
     * Calls actual {@code getRecipients} method for the wrapped RecipientInformationStore object.
     *
     * @return a collection of wrapped recipients.
     */
    Collection<IRecipientInformation> getRecipients();

    /**
     * Calls actual {@code get} method for the wrapped RecipientInformationStore object.
     *
     * @param var1 RecipientId wrapper
     *
     * @return {@link IRecipientInformation} the wrapper for received RecipientInformation object.
     */
    IRecipientInformation get(IRecipientId var1);
}
