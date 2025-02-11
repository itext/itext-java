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
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A dummy implementation of {@link ISvgNodeRenderer} for testing purposes
 */
public class DummySvgNodeRenderer implements ISvgNodeRenderer {

    Map<String, String> attributes;
    ISvgNodeRenderer parent;
    String name;
    boolean drawn = false;

    public DummySvgNodeRenderer() {
        this("dummy");
    }

    public DummySvgNodeRenderer(String name) {
        this(name, new HashMap<String, String>());
    }

    public DummySvgNodeRenderer(String name, Map<String, String> attr) {
        this.name = name;
        this.attributes = attr;
    }

    @Override
    public void setParent(ISvgNodeRenderer parent) {
        this.parent = parent;
    }

    @Override
    public ISvgNodeRenderer getParent() {
        return parent;
    }


    @Override
    public void draw(SvgDrawContext context) {
        System.out.println(name + ": Drawing in dummy node");
        this.drawn = true;
    }

    @Override
    public void setAttributesAndStyles(Map<String, String> attributesAndStyles) {
        this.attributes = attributesAndStyles;
    }

    @Override
    public String getAttribute(String key) {
        if (SvgConstants.Attributes.WIDTH.equalsIgnoreCase(key) ||
            SvgConstants.Attributes.HEIGHT.equalsIgnoreCase(key)) {
            return "10";
        }
        return this.attributes.get(key);
    }

    @Override
    public void setAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    @Override
    public Map<String, String> getAttributeMapCopy() {
        return null;
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        return new DummySvgNodeRenderer(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DummySvgNodeRenderer)) {
            return false;
        }
        //Name
        DummySvgNodeRenderer otherDummy = (DummySvgNodeRenderer) o;
        return this.name.equals(otherDummy.name);
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }

    public boolean isDrawn() {
        return this.drawn;
    }

}
