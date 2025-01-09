/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.tagging;

import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.RootRenderer;

/**
 * TaggingHintKey instances are created in the scope of {@link RootRenderer#addChild(IRenderer)}
 * to preserve logical order of layout elements from model elements.
 */
public final class TaggingHintKey {
    private IAccessibleElement elem;
    private boolean isArtifact;
    private boolean isFinished;
    private String overriddenRole;
    private boolean elementBasedFinishingOnly;
    private  TagTreePointer tagPointer;

    /**
     * Instantiate a new {@link TaggingHintKey} instance.
     *
     * @param elem element this hint key will be created for.
     * @param createdElementBased {@code true} if element implements {@link IElement}.
     */
    TaggingHintKey(IAccessibleElement elem, boolean createdElementBased) {
        this.elem = elem;
        this.elementBasedFinishingOnly = createdElementBased;
    }

    /**
     * Get accessible element.
     *
     * @return the accessible element.
     */
    public IAccessibleElement getAccessibleElement() {
        return elem;
    }


    /**
     * Gets the TagTreePointer.
     *
     * @return the {@link TagTreePointer} or null if there is no associated one yet.
     */
    public TagTreePointer getTagPointer() {
        return tagPointer;
    }

    /**
     * Sets the TagTreePointer.
     *
     * @param tag the TagTreePointer to set.
     */
    public void setTagPointer(TagTreePointer tag) {
       this.tagPointer = tag;
    }

    AccessibilityProperties getAccessibilityProperties() {
        if (elem == null){
            return null;
        }
        return elem.getAccessibilityProperties();
    }
    /**
     * Retrieve hint key finished flag.
     *
     * @return {@code true} if hint key is finished, {@code false} otherwise.
     */
    boolean isFinished() {
        return isFinished;
    }

    /**
     * Set finished flag for hint key instance.
     */
    void setFinished() {
        this.isFinished = true;
    }

    /**
     * Retrieve information whether this hint key is artifact or not.
     *
     * @return {@code true} if hint key corresponds to artifact, {@code false} otherwise.
     */
    boolean isArtifact() {
        return isArtifact;
    }

    /**
     * Specify that hint key instance corresponds to artifact.
     */
    void setArtifact() {
        this.isArtifact = true;
    }

    /**
     * Get overridden role.
     *
     * @return the overridden role.
     */
    String getOverriddenRole() {
        return overriddenRole;
    }

    /**
     * Set the overridden role.
     *
     * @param overriddenRole overridden role.
     */
    void setOverriddenRole(String overriddenRole) {
        this.overriddenRole = overriddenRole;
    }

    /**
     * Retrieve information whether the element backed by this hint key implements {@link IElement}.
     *
     * @return {@code} true if element implements {@link IElement}.
     */
    boolean isElementBasedFinishingOnly() {
        return elementBasedFinishingOnly;
    }
}
