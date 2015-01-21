package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfName;

public class PdfArtifact implements IPdfTag {

    static public PdfArtifact instance = new PdfArtifact();

    private PdfArtifact() {

    }

    @Override
    public PdfName getRole() throws PdfException {
        return PdfName.Artifact;
    }

    @Override
    public Integer getMcid() throws PdfException {
        return null;
    }
}
