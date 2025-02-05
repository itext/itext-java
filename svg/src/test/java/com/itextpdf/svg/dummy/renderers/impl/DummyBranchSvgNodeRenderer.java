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
package com.itextpdf.svg.dummy.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import java.util.ArrayList;
import java.util.List;

public class DummyBranchSvgNodeRenderer extends DummySvgNodeRenderer implements IBranchSvgNodeRenderer {

    List<ISvgNodeRenderer> children = new ArrayList<>();

    public DummyBranchSvgNodeRenderer(String name) {
        super(name);
    }

    @Override
    public void addChild(ISvgNodeRenderer child) {
        children.add(child);
    }

    @Override
    public List<ISvgNodeRenderer> getChildren() {
        return children;
    }

    @Override
    public void draw(SvgDrawContext context) {
        System.out.println(name + ": Drawing in dummy node, children left: " + children.size());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DummyBranchSvgNodeRenderer)) {
            return false;
        }
        //Name
        DummyBranchSvgNodeRenderer otherDummy = (DummyBranchSvgNodeRenderer) o;
        if (!this.name.equals(otherDummy.name)) {
            return false;
        }
        //children
        if (!(this.children.isEmpty() && otherDummy.children.isEmpty())) {
            if (this.children.size() != otherDummy.children.size()) {
                return false;
            }
            boolean iterationResult = true;
            for (int i = 0; i < this.children.size(); i++) {
                iterationResult &= this.children.get(i).equals(otherDummy.getChildren().get(i));
            }
            if (!iterationResult) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }
}
