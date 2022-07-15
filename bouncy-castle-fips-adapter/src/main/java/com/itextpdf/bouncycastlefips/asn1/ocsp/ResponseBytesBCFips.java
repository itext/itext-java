package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.DEROctetStringBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;

import org.bouncycastle.asn1.ocsp.ResponseBytes;

/**
 * Wrapper class for {@link ResponseBytes}.
 */
public class ResponseBytesBCFips extends ASN1EncodableBCFips implements IResponseBytes {
    /**
     * Creates new wrapper instance for {@link ResponseBytes}.
     *
     * @param responseBytes {@link ResponseBytes} to be wrapped
     */
    public ResponseBytesBCFips(ResponseBytes responseBytes) {
        super(responseBytes);
    }

    /**
     * Creates new wrapper instance for {@link ResponseBytes}.
     *
     * @param asn1ObjectIdentifier ASN1ObjectIdentifier wrapper
     * @param derOctetString       DEROctetString wrapper
     */
    public ResponseBytesBCFips(IASN1ObjectIdentifier asn1ObjectIdentifier, IDEROctetString derOctetString) {
        super(new ResponseBytes(
                ((ASN1ObjectIdentifierBCFips) asn1ObjectIdentifier).getASN1ObjectIdentifier(),
                ((DEROctetStringBCFips) derOctetString).getDEROctetString()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ResponseBytes}.
     */
    public ResponseBytes getResponseBytes() {
        return (ResponseBytes) getEncodable();
    }
}
