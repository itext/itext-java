package com.itextpdf.bouncycastlefips;

import com.itextpdf.commons.bouncycastle.IBouncyCastleTestConstantsFactory;

public class BouncyCastleFipsTestConstantsFactory implements IBouncyCastleTestConstantsFactory {

    @Override
    public String getCertificateInfoTestConst() {
        return "DEF length 8 object truncated by 4";
    }
}
