package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import org.bouncycastle.asn1.DERIA5String;

public class DERIA5StringBCFips extends ASN1PrimitiveBCFips implements IDERIA5String {
    public DERIA5StringBCFips(DERIA5String deria5String) {
        super(deria5String);
    }

    public DERIA5StringBCFips(String str) {
        this(new DERIA5String(str));
    }

    public DERIA5String getDerIA5String() {
        return (DERIA5String) getEncodable();
    }

    @Override
    public String getString() {
        return getDerIA5String().getString();
    }
}
