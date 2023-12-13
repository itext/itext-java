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
package com.itextpdf.svg.dummy.factories;

import com.itextpdf.svg.dummy.renderers.impl.DummyArgumentedConstructorSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummyProcessableSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummyProtectedSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DummySvgNodeMapper implements ISvgNodeRendererMapper {

    @Override
    public Map<String, Class<? extends ISvgNodeRenderer>> getMapping() {
        Map<String, Class<? extends ISvgNodeRenderer>> result = new HashMap<>();
        result.put("dummy", DummySvgNodeRenderer.class);
        result.put("processable", DummyProcessableSvgNodeRenderer.class);
        result.put("protected", DummyProtectedSvgNodeRenderer.class);
        result.put("argumented", DummyArgumentedConstructorSvgNodeRenderer.class);
        return result;
    }

    @Override
    public Collection<String> getIgnoredTags() {
        return new ArrayList<>();
    }
}
