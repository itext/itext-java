package com.itextpdf.commons.bouncycastle.cert;

import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.math.BigInteger;
import java.util.Date;

public interface IX509v2CRLBuilder {
    IX509v2CRLBuilder addCRLEntry(BigInteger bigInteger, Date date, int i);

    IX509v2CRLBuilder setNextUpdate(Date nextUpdate);

    IX509CRLHolder build(IContentSigner signer);
}
