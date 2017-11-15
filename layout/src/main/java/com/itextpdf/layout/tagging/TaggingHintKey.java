package com.itextpdf.layout.tagging;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;

public final class TaggingHintKey {
    private IAccessibleElement elem;
    private boolean isArtifact;
    private boolean isFinished;
    private PdfName overriddenRole;
    private boolean elementBasedFinishingOnly;

    TaggingHintKey(IAccessibleElement elem, boolean createdElementBased) {
        this.elem = elem;
        this.elementBasedFinishingOnly = createdElementBased;
    }

    public IAccessibleElement getAccessibleElement() {
        return elem;
    }

    boolean isFinished() {
        return isFinished;
    }

    void setFinished() {
        this.isFinished = true;
    }

    boolean isArtifact() {
        return isArtifact;
    }

    void setArtifact() {
        this.isArtifact = true;
    }

    void setOverriddenRole(PdfName overriddenRole) {
        this.overriddenRole = overriddenRole;
    }

    PdfName getOverriddenRole() {
        return overriddenRole;
    }

    boolean isElementBasedFinishingOnly() {
        return elementBasedFinishingOnly;
    }
}
