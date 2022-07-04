package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.DEROctetStringBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;

import org.bouncycastle.asn1.ocsp.ResponseBytes;

public class ResponseBytesBC extends ASN1EncodableBC implements IResponseBytes {
    public ResponseBytesBC(ResponseBytes encodable) {
        super(encodable);
    }

    public ResponseBytesBC(IASN1ObjectIdentifier asn1ObjectIdentifier, IDEROctetString derOctetString) {
        super(new ResponseBytes(
                ((ASN1ObjectIdentifierBC) asn1ObjectIdentifier).getObjectIdentifier(),
                ((DEROctetStringBC) derOctetString).getDEROctetString()));
    }

    public ResponseBytes getResponseBytes() {
        return (ResponseBytes) getEncodable();
    }
}
