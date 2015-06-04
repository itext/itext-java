package com.itextpdf.core.pdf.tagging;

import com.itextpdf.core.pdf.PdfName;

import java.util.List;

public class PdfArtifact implements IPdfTag {

    static public PdfArtifact instance = new PdfArtifact();

    private PdfArtifact() {

    }

    @Override
    public PdfName getRole() {
        return PdfName.Artifact;
    }

    @Override
    public Integer getMcid() {
        return null;
    }

//    @Override
//    public Integer getStructParentIndex() {
//        return null;
//    }

    @Override
    public IPdfStructElem getParent() {
        return null;
    }

    @Override
    public List<IPdfStructElem> getKids() {
        return null;
    }
}
