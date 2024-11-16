/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ExtendedITextTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class EllipseSvgNodeRendererUnitTest extends ExtendedITextTest {

    @Test
    public void getObjectBoundingBoxTest() {
        EllipseSvgNodeRenderer renderer = new EllipseSvgNodeRenderer();
        SvgDrawContext context = new SvgDrawContext(new ResourceResolver(""), new FontProvider());
        renderer.setAttributesAndStyles(new HashMap<>());
        Assertions.assertNull(renderer.getObjectBoundingBox(context));
    }

    @Test
    public void noViewPortTest() {
        EllipseSvgNodeRenderer renderer = new EllipseSvgNodeRenderer();
        SvgDrawContext context = new SvgDrawContext(new ResourceResolver(""), new FontProvider());
        Map<String, String> styles = new HashMap<>();
        styles.put("rx", "50%");
        styles.put("ry", "50%");
        renderer.setAttributesAndStyles(styles);
        Exception e = Assertions.assertThrows(SvgProcessingException.class, () -> renderer.setParameters(context));
        Assertions.assertEquals(SvgExceptionMessageConstant.ILLEGAL_RELATIVE_VALUE_NO_VIEWPORT_IS_SET, e.getMessage());
    }
}
