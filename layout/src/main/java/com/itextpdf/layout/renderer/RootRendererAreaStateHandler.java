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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.RootLayoutArea;

import java.util.ArrayList;
import java.util.List;

class RootRendererAreaStateHandler {
    private RootLayoutArea storedPreviousArea;
    private RootLayoutArea storedNextArea;

    private List<Rectangle> storedPreviousFloatRenderAreas = null;
    private List<Rectangle> storedNextFloatRenderAreas = null;

    public boolean attemptGoBackToStoredPreviousStateAndStoreNextState(RootRenderer rootRenderer) {
        boolean result = false;
        if (storedPreviousArea != null) {
            storedNextArea = rootRenderer.currentArea;

            rootRenderer.currentArea = storedPreviousArea;

            storedNextFloatRenderAreas = new ArrayList<>(rootRenderer.floatRendererAreas);
            rootRenderer.floatRendererAreas = storedPreviousFloatRenderAreas;

            storedPreviousFloatRenderAreas = null;
            storedPreviousArea = null;

            result = true;
        }
        return result;
    }

    public boolean attemptGoForwardToStoredNextState(RootRenderer rootRenderer) {
        if (storedNextArea != null) {
            rootRenderer.currentArea = storedNextArea;
            rootRenderer.floatRendererAreas = storedNextFloatRenderAreas;

            storedNextArea = null;
            storedNextFloatRenderAreas = null;
            return true;
        } else {
            return false;
        }
    }

    public RootRendererAreaStateHandler storePreviousState(RootRenderer rootRenderer) {
        storedPreviousArea = rootRenderer.currentArea;
        storedPreviousFloatRenderAreas = new ArrayList<>(rootRenderer.floatRendererAreas);
        return this;
    }
}
