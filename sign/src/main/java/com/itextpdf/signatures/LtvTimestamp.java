package com.itextpdf.signatures;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDeveloperExtension;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfString;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;

/**
 * PAdES-LTV Timestamp
 * @author Paulo Soares
 */
public class LtvTimestamp {

    /**
     * Signs a document with a PAdES-LTV Timestamp. The document is closed at the end.
     * @param sap the signature appearance
     * @param tsa the timestamp generator
     * @param signatureName the signature name or null to have a name generated
     * automatically
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static void timestamp(PdfSigner sap, TSAClient tsa, String signatureName) throws IOException, GeneralSecurityException {
        int contentEstimated = tsa.getTokenSizeEstimate();
        sap.addDeveloperExtension(PdfDeveloperExtension.ESIC_1_7_EXTENSIONLEVEL5);
        sap.setFieldName(signatureName);

        PdfSignature dic = new PdfSignature(PdfName.Adobe_PPKLite, PdfName.ETSI_RFC3161);
        dic.put(PdfName.Type, PdfName.DocTimeStamp);
        sap.setCryptoDictionary(dic);

        HashMap<PdfName,Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, new Integer(contentEstimated * 2 + 2));
        sap.preClose(exc);
        InputStream data = sap.getRangeStream();
        MessageDigest messageDigest = tsa.getMessageDigest();
        byte[] buf = new byte[4096];
        int n;
        while ((n = data.read(buf)) > 0) {
            messageDigest.update(buf, 0, n);
        }
        byte[] tsImprint = messageDigest.digest();
        byte[] tsToken;
        try {
            tsToken = tsa.getTimeStampToken(tsImprint);
        }
        catch(Exception e) {
            throw new GeneralSecurityException(e);
        }

        if (contentEstimated + 2 < tsToken.length)
            throw new IOException("Not enough space");

        byte[] paddedSig = new byte[contentEstimated];
        System.arraycopy(tsToken, 0, paddedSig, 0, tsToken.length);

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.Contents, new PdfString(paddedSig).setHexWriting(true));
        sap.close(dic2);
    }
}
