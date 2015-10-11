package com.itextpdf.signatures;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;

import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Produces a blank (or empty) signature. Useful for deferred signing with
 * MakeSignature.signExternalContainer().
 * @author Paulo Soares
 */
public class ExternalBlankSignatureContainer implements ExternalSignatureContainer {

    private PdfDictionary sigDic;

    public ExternalBlankSignatureContainer(PdfDictionary sigDic) {
        this.sigDic = sigDic;
    }

    public ExternalBlankSignatureContainer(PdfName filter, PdfName subFilter) {
        sigDic = new PdfDictionary();
        sigDic.put(PdfName.Filter, filter);
        sigDic.put(PdfName.SubFilter, subFilter);
    }

    public byte[] sign(InputStream data) throws GeneralSecurityException {
        return new byte[0];
    }

    public void modifySigningDictionary(PdfDictionary signDic) {
        signDic.putAll(sigDic);
    }

}
