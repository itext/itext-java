package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.DEROctetStringBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;

import org.bouncycastle.asn1.ocsp.ResponseBytes;

public class ResponseBytesBCFips extends ASN1EncodableBCFips implements IResponseBytes {
    public ResponseBytesBCFips(ResponseBytes encodable) {
        super(encodable);
    }

    public ResponseBytesBCFips(IASN1ObjectIdentifier asn1ObjectIdentifier, IDEROctetString derOctetString) {
        super(new ResponseBytes(
                ((ASN1ObjectIdentifierBCFips) asn1ObjectIdentifier).getObjectIdentifier(),
                ((DEROctetStringBCFips) derOctetString).getDEROctetString()));
    }

    public ResponseBytes getResponseBytes() {
        return (ResponseBytes) getEncodable();
    }
}
