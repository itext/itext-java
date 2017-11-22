package com.itextpdf.layout.tagging;

public final class TaggingHintKey {
    private IAccessibleElement elem;
    private boolean isArtifact;
    private boolean isFinished;
    private String overriddenRole;
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

    String getOverriddenRole() {
        return overriddenRole;
    }

    void setOverriddenRole(String overriddenRole) {
        this.overriddenRole = overriddenRole;
    }

    boolean isElementBasedFinishingOnly() {
        return elementBasedFinishingOnly;
    }
}
