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
package com.itextpdf.kernel.pdf.canvas.parser.data;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;

/**
 * Represents the clipping path data.
 */
public class ClippingPathInfo extends AbstractRenderInfo {
    private Path path;
    private Matrix ctm;

    /**
     * Creates a new {@link ClippingPathInfo} instance.
     *
     * @param gs   the {@link CanvasGraphicsState canvas graphics state}
     * @param path the {@link Path} which represents current clipping path
     * @param ctm  the current {@link Matrix transformation matrix}
     */
    public ClippingPathInfo(CanvasGraphicsState gs, Path path, Matrix ctm) {
        super(gs);
        this.path = path;
        this.ctm = ctm;
    }

    /**
     * Gets the {@link Path} which represents current clipping path.
     *
     * @return the {@link Path} which represents current clipping path
     */
    public Path getClippingPath() {
        return path;
    }

    /**
     * Gets the current {@link Matrix transformation matrix}.
     *
     * @return the current {@link Matrix transformation matrix}
     */
    public Matrix getCtm() {
        return ctm;
    }
}
