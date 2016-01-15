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

    /* The Signature dictionary. Should contain values for /Filter and /SubFilter at minimum. */
    private PdfDictionary sigDic;

    /**
     * Creates an ExternalBlankSignatureContainer.
     * @param sigDic PdfDictionary containing signature iformation. /SubFilter and /Filter aren't set in this constructor.
     */
    public ExternalBlankSignatureContainer(PdfDictionary sigDic) {
        this.sigDic = sigDic;
    }

    /**
     * Creates an ExternalBlankSignatureContainer. This constructor will create the PdfDictionary for the
     * signature information and will insert the  /Filter and /SubFilter values into this dictionary.
     *
     * @param filter PdfName of the signature handler to use when validating this signature
     * @param subFilter PdfName that describes the encoding of the signature
     */
    public ExternalBlankSignatureContainer(PdfName filter, PdfName subFilter) {
        sigDic = new PdfDictionary();
        sigDic.put(PdfName.Filter, filter);
        sigDic.put(PdfName.SubFilter, subFilter);
    }

    @Override
    public byte[] sign(InputStream data) throws GeneralSecurityException {
        return new byte[0];
    }

    @Override
    public void modifySigningDictionary(PdfDictionary signDic) {
        signDic.putAll(sigDic);
    }

}
