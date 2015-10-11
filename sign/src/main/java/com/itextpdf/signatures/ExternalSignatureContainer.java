package com.itextpdf.signatures;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;

import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Interface to sign a document. The signing is fully done externally, including the container composition.
 * @author Paulo Soares
 */
public interface ExternalSignatureContainer {

    /**
     * Produces the container with the signature.
     * @param data the data to sign
     * @return a container with the signature and other objects, like CRL and OCSP. The container will generally be a PKCS7 one.
     * @throws GeneralSecurityException
     */
    byte[] sign(InputStream data) throws GeneralSecurityException;

    /**
     * Modifies the signature dictionary to suit the container. At least the keys {@link PdfName#Filter} and
     * {@link PdfName#SubFilter} will have to be set.
     * @param signDic the signature dictionary
     */
    void modifySigningDictionary(PdfDictionary signDic);
}
