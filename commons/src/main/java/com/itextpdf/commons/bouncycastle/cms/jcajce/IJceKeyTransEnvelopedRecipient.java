package com.itextpdf.commons.bouncycastle.cms.jcajce;

import com.itextpdf.commons.bouncycastle.cms.IRecipient;

public interface IJceKeyTransEnvelopedRecipient extends IRecipient {
    IJceKeyTransEnvelopedRecipient setProvider(String provider);
}
