package com.itextpdf.bouncycastle.cms.jcajce;

import com.itextpdf.bouncycastle.cms.RecipientBC;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;

import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;

/**
 * Wrapper class for {@link JceKeyTransEnvelopedRecipient}.
 */
public class JceKeyTransEnvelopedRecipientBC extends RecipientBC implements IJceKeyTransEnvelopedRecipient {
    /**
     * Creates new wrapper instance for {@link JceKeyTransEnvelopedRecipient}.
     *
     * @param jceKeyTransEnvelopedRecipient {@link JceKeyTransEnvelopedRecipient} to be wrapped
     */
    public JceKeyTransEnvelopedRecipientBC(JceKeyTransEnvelopedRecipient jceKeyTransEnvelopedRecipient) {
        super(jceKeyTransEnvelopedRecipient);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JceKeyTransEnvelopedRecipient}.
     */
    public JceKeyTransEnvelopedRecipient getJceKeyTransEnvelopedRecipient() {
        return (JceKeyTransEnvelopedRecipient) getRecipient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJceKeyTransEnvelopedRecipient setProvider(String provider) {
        getJceKeyTransEnvelopedRecipient().setProvider(provider);
        return this;
    }
}
