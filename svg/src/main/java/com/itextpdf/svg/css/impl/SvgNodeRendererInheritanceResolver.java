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
package com.itextpdf.svg.css.impl;

import com.itextpdf.styledxmlparser.util.StyleUtil;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.css.SvgCssContext;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.AbstractBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextSvgBranchRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Style and attribute inheritance resolver for {@link ISvgNodeRenderer} objects.
 */
public final class SvgNodeRendererInheritanceResolver {

    private SvgNodeRendererInheritanceResolver() {
    }

    /**
     * Apply style and attribute inheritance to the tree formed by the root and the subTree.
     *
     * @param root the renderer to consider as the root of the subtree
     * @param subTree the tree of {@link ISvgNodeRenderer}
     * @param cssContext the current SVG CSS context
     */
    public static void applyInheritanceToSubTree(ISvgNodeRenderer root, ISvgNodeRenderer subTree,
            SvgCssContext cssContext) {
        // Merge inherited style declarations from parent into child
        applyStyles(root, subTree, cssContext);
        // If subtree, iterate over tree
        if (subTree instanceof AbstractBranchSvgNodeRenderer) {
            AbstractBranchSvgNodeRenderer subTreeAsBranch = (AbstractBranchSvgNodeRenderer) subTree;
            for (ISvgNodeRenderer child : subTreeAsBranch.getChildren()) {
                applyInheritanceToSubTree(subTreeAsBranch, child, cssContext);
            }
        }
        if (subTree instanceof TextSvgBranchRenderer) {
            TextSvgBranchRenderer subTreeAsBranch = (TextSvgBranchRenderer) subTree;
            for (ISvgNodeRenderer child : subTreeAsBranch.getChildren()) {
                applyInheritanceToSubTree(subTreeAsBranch, child, cssContext);
            }
        }
    }

    private static void applyStyles(ISvgNodeRenderer parent, ISvgNodeRenderer child, SvgCssContext cssContext) {
        if (parent != null && child != null) {
            Map<String, String> childStyles = child.getAttributeMapCopy();
            if (childStyles == null) {
                childStyles = new HashMap<>();
            }
            final Map<String, String> parentStyles = parent.getAttributeMapCopy();
            final String parentFontSize = parent.getAttribute(SvgConstants.Attributes.FONT_SIZE);

            for (Map.Entry<String, String> parentAttribute : parentStyles.entrySet()) {
                childStyles = StyleUtil.mergeParentStyleDeclaration(childStyles, parentAttribute.getKey(),
                        parentAttribute.getValue(), parentFontSize, SvgStyleResolver.INHERITANCE_RULES);
            }

            SvgStyleResolver.resolveFontSizeStyle(childStyles, cssContext, parentFontSize);

            child.setAttributesAndStyles(childStyles);
        }
    }
}
