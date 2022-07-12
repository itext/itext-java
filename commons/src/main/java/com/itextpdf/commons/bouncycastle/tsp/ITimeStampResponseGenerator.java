package com.itextpdf.commons.bouncycastle.tsp;

import java.math.BigInteger;
import java.util.Date;

public interface ITimeStampResponseGenerator {
    ITimeStampResponse generate(ITimeStampRequest request, BigInteger bigInteger, Date date)
            throws AbstractTSPException;
}
