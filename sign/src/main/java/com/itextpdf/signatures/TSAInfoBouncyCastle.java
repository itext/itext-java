package com.itextpdf.signatures;

import org.bouncycastle.tsp.TimeStampTokenInfo;

/**
 * Interface you can implement and pass to TSAClientBouncyCastle in case
 * you want to do something with the information returned
 */
public interface TSAInfoBouncyCastle {

    /**
     * When a timestamp is created using TSAClientBouncyCastle,
     * this method is triggered passing an object that contains
     * info about the timestamp and the time stamping authority.
     * @param info a TimeStampTokenInfo object
     */
    void inspectTimeStampTokenInfo(final TimeStampTokenInfo info);
}
