package com.itextpdf.bouncycastle.cms.jcajce;

import com.itextpdf.bouncycastle.cms.RecipientBC;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyTransEnvelopedRecipient;

import java.util.Objects;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;

public class JceKeyTransEnvelopedRecipientBC extends RecipientBC implements IJceKeyTransEnvelopedRecipient {
    public JceKeyTransEnvelopedRecipientBC(JceKeyTransEnvelopedRecipient jceKeyTransEnvelopedRecipient) {
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
