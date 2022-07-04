package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import org.bouncycastle.asn1.DERIA5String;

public class DERIA5StringBC extends ASN1PrimitiveBC implements IDERIA5String {
    public DERIA5StringBC(DERIA5String deria5String) {
        super(deria5String);
    }
    
    public DERIA5StringBC(String str) {
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
