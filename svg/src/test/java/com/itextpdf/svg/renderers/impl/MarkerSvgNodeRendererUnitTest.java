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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ExtendedITextTest;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class MarkerSvgNodeRendererUnitTest extends ExtendedITextTest {

    @Test
    public void noObjectBoundingBoxTest() {
        MarkerSvgNodeRenderer renderer = new MarkerSvgNodeRenderer();
        Assertions.assertNull(renderer.getObjectBoundingBox(null));
    }

    @Test
    public void nullViewportTest() {
        MarkerSvgNodeRenderer renderer = new MarkerSvgNodeRenderer();

        Map<String, String> styles = new HashMap<String, String>();
        styles.put("markerwidth", "300pt");
        styles.put("markerheight", "300pt");
        renderer.setAttributesAndStyles(styles);

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> renderer.applyViewBox(new SvgDrawContext(null, null)));
        Assertions.assertEquals(SvgExceptionMessageConstant.CURRENT_VIEWPORT_IS_NULL, e.getMessage());
    }
}
