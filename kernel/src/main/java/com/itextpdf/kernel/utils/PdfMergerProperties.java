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
package com.itextpdf.kernel.utils;

/**
 * Class with additional properties for {@link PdfMerger} processing.
 * Needs to be passed at merger initialization.
 */
public class PdfMergerProperties {
    private boolean closeSrcDocuments;
    private boolean mergeTags;
    private boolean mergeOutlines;
    private boolean mergeScripts;

    /**
     * Default constructor, use provided setters for configuration options.
     */
    public PdfMergerProperties() {
        closeSrcDocuments = false;
        mergeTags = true;
        mergeOutlines = true;
        mergeScripts = false;
    }

    /**
     * check if source documents should be close after merging
     *
     * @return true if they should, false otherwise
     */
    public boolean isCloseSrcDocuments() {
        return closeSrcDocuments;
    }

    /**
     * check if tags should be merged
     *
     * @return true if they should, false otherwise
     */
    public boolean isMergeTags() {
        return mergeTags;
    }

    /**
     * check if outlines should be merged
     *
     * @return true if they should, false otherwise
     */
    public boolean isMergeOutlines() {
        return mergeOutlines;
    }

    /**
     * check if ECMA scripts (which are executed at document opening) should be merged
     *
     * @return true if they should, false otherwise
     */
    public boolean isMergeScripts() {
        return mergeScripts;
    }

    /**
     * close source documents after merging
     *
     * @param closeSrcDocuments true to close, false otherwise
     *
     * @return <code>PdfMergerProperties</code> instance
     */
    public PdfMergerProperties setCloseSrcDocuments(boolean closeSrcDocuments) {
        this.closeSrcDocuments = closeSrcDocuments;
        return this;
    }

    /**
     * merge documents tags
     *
     * @param mergeTags true to merge, false otherwise
     *
     * @return <code>PdfMergerProperties</code> instance
     */
    public PdfMergerProperties setMergeTags(boolean mergeTags) {
        this.mergeTags = mergeTags;
        return this;
    }

    /**
     * merge documents outlines
     *
     * @param mergeOutlines true to merge, false otherwise
     *
     * @return <code>PdfMergerProperties</code> instance
     */
    public PdfMergerProperties setMergeOutlines(boolean mergeOutlines) {
        this.mergeOutlines = mergeOutlines;
        return this;
    }

    /**
     * merge documents ECMA scripts,
     * if AA or OpenAction or Names dictionaries contained in both documents than nothing will be merged
     *
     * @param mergeNames true to merge, false otherwise
     *
     * @return <code>PdfMergerProperties</code> instance
     */
    public PdfMergerProperties setMergeScripts(boolean mergeNames) {
        this.mergeScripts = mergeNames;
        return this;
    }
}
