package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.DEROctetStringBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;

import org.bouncycastle.asn1.ocsp.ResponseBytes;

/**
 * Wrapper class for {@link ResponseBytes}.
 */
public class ResponseBytesBC extends ASN1EncodableBC implements IResponseBytes {
    /**
     * Creates new wrapper instance for {@link ResponseBytes}.
     *
     * @param responseBytes {@link ResponseBytes} to be wrapped
     */
    public ResponseBytesBC(ResponseBytes responseBytes) {
        super(responseBytes);
    }

    /**
     * Creates new wrapper instance for {@link ResponseBytes}.
     *
     * @param asn1ObjectIdentifier ASN1ObjectIdentifier wrapper
     * @param derOctetString       DEROctetString wrapper
     */
    public ResponseBytesBC(IASN1ObjectIdentifier asn1ObjectIdentifier, IDEROctetString derOctetString) {
        super(new ResponseBytes(
                ((ASN1ObjectIdentifierBC) asn1ObjectIdentifier).getASN1ObjectIdentifier(),
                ((DEROctetStringBC) derOctetString).getDEROctetString()));
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
