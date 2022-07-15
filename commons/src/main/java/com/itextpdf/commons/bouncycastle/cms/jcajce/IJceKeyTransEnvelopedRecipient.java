package com.itextpdf.commons.bouncycastle.cms.jcajce;

import com.itextpdf.commons.bouncycastle.cms.IRecipient;

/**
 * This interface represents the wrapper for JceKeyTransEnvelopedRecipient that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJceKeyTransEnvelopedRecipient extends IRecipient {
    /**
     * Calls actual {@code setProvider} method for the wrapped JceKeyTransEnvelopedRecipient object.
     *
     * @param provider provider name
     *
     * @return {@link IJceKeyTransEnvelopedRecipient} this wrapper object.
     */
    IJceKeyTransEnvelopedRecipient setProvider(String provider);
}
