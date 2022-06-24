package com.itextpdf.bouncycastle.cms.jcajce;

import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;

import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;

public class JceKeyTransEnvelopedRecipientBC implements IJceKeyTransEnvelopedRecipient {
    private final JceKeyTransEnvelopedRecipient jceKeyTransEnvelopedRecipient;

    public JceKeyTransEnvelopedRecipientBC(JceKeyTransEnvelopedRecipient jceKeyTransEnvelopedRecipient) {
        this.jceKeyTransEnvelopedRecipient = jceKeyTransEnvelopedRecipient;
    }

    public JceKeyTransEnvelopedRecipient getJceKeyTransEnvelopedRecipient() {
        return jceKeyTransEnvelopedRecipient;
    }

    @Override
    public IJceKeyTransEnvelopedRecipient setProvider(String provider) {
        return new JceKeyTransEnvelopedRecipientBC(
                (JceKeyTransEnvelopedRecipient) jceKeyTransEnvelopedRecipient.setProvider(provider));
    }
}
