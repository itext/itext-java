package com.itextpdf.bouncycastlefips.operator;

import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import org.bouncycastle.operator.DigestCalculator;

public class DigestCalculatorBCFips implements IDigestCalculator {
    private final DigestCalculator digestCalculator;

    public DigestCalculatorBCFips(DigestCalculator digestCalculator) {
        this.digestCalculator = digestCalculator;
    }

    public DigestCalculator getDigestCalculator() {
        return digestCalculator;
    }
}
