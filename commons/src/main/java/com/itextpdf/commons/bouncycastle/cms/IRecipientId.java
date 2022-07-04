package com.itextpdf.commons.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;

public interface IRecipientId {
    boolean match(IX509CertificateHolder holder);
}
