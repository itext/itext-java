package com.itextpdf.core.pdf.xobject;

import com.itextpdf.core.pdf.*;

public class PdfTransparencyGroup extends PdfObjectWrapper<PdfDictionary> {

    public PdfTransparencyGroup() {
        super(new PdfDictionary());
        put(PdfName.S, PdfName.Transparency);
    }

    /**
     * Determining the initial backdrop against which its stack is composited.
     * @param isolated
     */
    public void setIsolated(boolean isolated) {
        if (isolated)
            put(PdfName.I, PdfBoolean.PdfTrue);
        else
            remove(PdfName.I);
    }

    /**
     * Determining whether the objects within the stack are composited with one another or only with the group's backdrop.
     * @param knockout
     */
    public void setKnockout(boolean knockout) {
        if (knockout)
            put(PdfName.K, PdfBoolean.PdfTrue);
        else
            remove(PdfName.K);
    }

    public void setColorSpace(PdfName colorSpace) {
        put(PdfName.CS, colorSpace);
    }

    public void setColorSpace(PdfArray colorSpace) {
        put(PdfName.CS, colorSpace);
    }
}
