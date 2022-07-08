package com.itextpdf.bouncycastlefips.cms.jcajce;

import com.itextpdf.bouncycastlefips.cms.RecipientBCFips;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;

import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;

public class JceKeyTransEnvelopedRecipientBCFips extends RecipientBCFips implements IJceKeyTransEnvelopedRecipient {
    public JceKeyTransEnvelopedRecipientBCFips(JceKeyTransEnvelopedRecipient jceKeyTransEnvelopedRecipient) {
        super(jceKeyTransEnvelopedRecipient);
    }

    public JceKeyTransEnvelopedRecipient getJceKeyTransEnvelopedRecipient() {
        return (JceKeyTransEnvelopedRecipient) getRecipient();
    }

    @Override
    public IJceKeyTransEnvelopedRecipient setProvider(String provider) { 
        getJceKeyTransEnvelopedRecipient().setProvider(provider);
        return this;
    }
}
