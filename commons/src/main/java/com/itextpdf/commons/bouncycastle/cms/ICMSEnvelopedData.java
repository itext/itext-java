package com.itextpdf.commons.bouncycastle.cms;

/**
 * This interface represents the wrapper for CMSEnvelopedData that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ICMSEnvelopedData {
    /**
     * Calls actual {@code getRecipientInfos} method for the wrapped CMSEnvelopedData object.
     *
     * @return {@link IRecipientInformationStore} the wrapper for the received RecipientInformationStore object.
     */
    IRecipientInformationStore getRecipientInfos();
}
