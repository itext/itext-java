/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.svg.renderers;

import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererMapper;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DefaultRendererMapperTest extends ExtendedITextTest {

    @Test
    public void mapperNotEmptyTest() {
        DefaultSvgNodeRendererMapper mapper = new DefaultSvgNodeRendererMapper();
        Assert.assertFalse(mapper.getMapping().isEmpty());
    }

    @Test
    public void createAllRenderersTest() throws InstantiationException, IllegalAccessException {
        DefaultSvgNodeRendererMapper mapper = new DefaultSvgNodeRendererMapper();

        for (Class<? extends ISvgNodeRenderer> rendererClazz : mapper.getMapping().values()) {
            // the test is that this method does not throw an exception on any class here
            // meaning that every (non-abstract) implementation must have a public no-args constructor
            AssertUtil.doesNotThrow(() -> rendererClazz.newInstance());
        }
    }
}
