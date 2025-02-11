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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.kernel.pdf.PdfName;

/**
 * A subclass of {@link CanvasTag} for Artifacts.
 *
 * In Tagged PDF, an object can be marked as an Artifact in order to signify
 * that it is more part of the document structure than of the document content.
 * Examples are page headers, layout features, etc. Screen readers can choose to
 * ignore Artifacts.
 */
public class CanvasArtifact extends CanvasTag {

    /**
     * Creates a CanvasArtifact object, which is a {@link CanvasTag} with a role
     * of {@link PdfName#Artifact Artifact}.
     */
    public CanvasArtifact() {
        super(PdfName.Artifact);
    }
}
