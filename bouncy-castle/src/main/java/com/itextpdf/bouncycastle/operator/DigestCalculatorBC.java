package com.itextpdf.bouncycastle.operator;

import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import org.bouncycastle.operator.DigestCalculator;

public class DigestCalculatorBC implements IDigestCalculator {
    private final DigestCalculator digestCalculator;
    
    public DigestCalculatorBC(DigestCalculator digestCalculator) {
        this.digestCalculator = digestCalculator;
    }

    public DigestCalculator getDigestCalculator() {
        return digestCalculator;
    }
}
