package com.itextpdf.signatures;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

/**
 * Dictionary that stores signature build properties.
 *
 * @author Kwinten Pisman
 */
public class PdfSignatureBuildProperties extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates new PdfSignatureBuildProperties.
     */
    public PdfSignatureBuildProperties() {
        super(new PdfDictionary());
    }

    /**
     * Creates new PdfSignatureBuildProperties with preset values.
     *
     * @param dict PdfDictionary containing preset values
     */
    public PdfSignatureBuildProperties(PdfDictionary dict) {
        super(dict);
    }

    /**
     * Sets the signatureCreator property in the underlying
     * {@link PdfSignatureAppDictionary} dictionary.
     *
     * @param name
     */
    public void setSignatureCreator(String name) {
        getPdfSignatureAppProperty().setSignatureCreator(name);
    }

    /**
     * Gets the {@link PdfSignatureAppDictionary} from this dictionary. If it
     * does not exist, it adds a new {@link PdfSignatureAppDictionary} and
     * returns this instance.
     *
     * @return {@link PdfSignatureAppDictionary}
     */
    private PdfSignatureAppDictionary getPdfSignatureAppProperty() {
        PdfDictionary appPropDic = getPdfObject().getAsDictionary(PdfName.App);

        if (appPropDic == null) {
            appPropDic = new PdfDictionary();
            put(PdfName.App, appPropDic);
        }

        return new PdfSignatureAppDictionary(appPropDic);
    }
}