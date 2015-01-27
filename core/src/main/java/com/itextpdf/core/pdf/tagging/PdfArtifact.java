package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfName;

import java.util.List;

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

    @Override
    public Integer getStructParentIndex() throws PdfException {
        return null;
    }

    @Override
    public IPdfStructElem getParent() throws PdfException {
        return null;
    }

    @Override
    public List<IPdfStructElem> getKids() throws PdfException {
        return null;
    }
}
