package com.itextpdf.bouncycastlefips.cms.jcajce;

import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;

import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;

public class JceKeyTransEnvelopedRecipientBCFips implements IJceKeyTransEnvelopedRecipient {
    private final JceKeyTransEnvelopedRecipient jceKeyTransEnvelopedRecipient;

    public JceKeyTransEnvelopedRecipientBCFips(JceKeyTransEnvelopedRecipient jceKeyTransEnvelopedRecipient) {
        this.jceKeyTransEnvelopedRecipient = jceKeyTransEnvelopedRecipient;
    }

    public JceKeyTransEnvelopedRecipient getJceKeyTransEnvelopedRecipient() {
        return jceKeyTransEnvelopedRecipient;
    }

    @Override
    public IJceKeyTransEnvelopedRecipient setProvider(String provider) {
        return new JceKeyTransEnvelopedRecipientBCFips(
                (JceKeyTransEnvelopedRecipient) jceKeyTransEnvelopedRecipient.setProvider(provider));
    }
}
