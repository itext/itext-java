package com.itextpdf.bouncycastle;

import com.itextpdf.commons.bouncycastle.IBouncyCastleTestConstantsFactory;

public class BouncyCastleTestConstantsFactory implements IBouncyCastleTestConstantsFactory {

    @Override
    public String getCertificateInfoTestConst() {
        return "corrupted stream - out of bounds length found: 8 >= 6";
    }
}
