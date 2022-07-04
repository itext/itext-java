package com.itextpdf.commons.bouncycastle.cert.ocsp;

import java.util.Date;

public interface ISingleResp {
    ICertificateID getCertID();

    ICertificateStatus getCertStatus();

    Date getNextUpdate();

    Date getThisUpdate();
}
