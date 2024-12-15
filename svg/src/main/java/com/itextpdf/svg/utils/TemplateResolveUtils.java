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
package com.itextpdf.svg.utils;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.Map;

/**
 * Utility class which contains methods related to href resolving
 */
public class TemplateResolveUtils {

    private TemplateResolveUtils() {
        //private constructor for utility class
    }

    /**
     * Resolve href to other object within svg and fills renderer with its properties and children if needed.
     *
     * @param renderer renderer which should be updated after resolving its href attribute
     * @param context svg draw context instance
     */
    public static void resolve(IBranchSvgNodeRenderer renderer, SvgDrawContext context) {
        String href = renderer.getAttribute(SvgConstants.Attributes.HREF);
        if (href == null) {
            href = renderer.getAttribute(SvgConstants.Attributes.XLINK_HREF);
        }
        if (href == null || href.charAt(0) != '#') {
            return;
        }
        String normalizedName = SvgTextUtil.filterReferenceValue(href);
        ISvgNodeRenderer template = context.getNamedObject(normalizedName);

        if (!(template instanceof IBranchSvgNodeRenderer)) {
            return;
        }
        IBranchSvgNodeRenderer namedObject = (IBranchSvgNodeRenderer)template.createDeepCopy();
        resolve(namedObject, context);
        if (renderer.getChildren().isEmpty()) {
            for (ISvgNodeRenderer child : namedObject.getChildren()) {
                renderer.addChild(child);
            }
        }
        //href attributes inheritance rule are really simple, and only attributes not defined at renderer on which
        //href is resolved should be copied from referenced object
        Map<String, String> referencedAttributes = namedObject.getAttributeMapCopy();
        for (Map.Entry<String, String> entry : referencedAttributes.entrySet()) {
            if (renderer.getAttribute(entry.getKey()) == null) {
                renderer.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }
}
