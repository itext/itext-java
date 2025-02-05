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
package com.itextpdf.svg.dummy.factories;

import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.svg.dummy.renderers.impl.DummyArgumentedConstructorSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummyBranchSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummyProcessableSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;

/**
 * A dummy implementation of {@link ISvgNodeRendererFactory}for testing purposes
 */
public class DummySvgNodeFactory implements ISvgNodeRendererFactory{

    @Override
    public ISvgNodeRenderer createSvgNodeRendererForTag(IElementNode tag, ISvgNodeRenderer parent) {
        ISvgNodeRenderer result;
        if ("svg".equals(tag.name())) {
            result = new DummyBranchSvgNodeRenderer(tag.name());
        } else if ("processable".equals(tag.name())) {
            result = new DummyProcessableSvgNodeRenderer();
        } else if ("argumented".equals(tag.name())) {
            result = new DummyArgumentedConstructorSvgNodeRenderer(15);
        } else {
            result = new DummySvgNodeRenderer(tag.name());
        }
        result.setParent(parent);
        return result;
    }

    @Override
    public boolean isTagIgnored(IElementNode tag) {
        return false;
    }
}
