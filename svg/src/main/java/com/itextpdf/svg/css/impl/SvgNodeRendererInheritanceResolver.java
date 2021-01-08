/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.css.impl;

import com.itextpdf.styledxmlparser.util.StyleUtil;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.css.SvgCssContext;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.AbstractBranchSvgNodeRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Style and attribute inheritance resolver for {@link ISvgNodeRenderer} objects.
 */
public class SvgNodeRendererInheritanceResolver {
    @Deprecated
    public SvgNodeRendererInheritanceResolver() {
        // After removing this constructor, make this class final and add private constructor
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

    /**
     * Apply style and attribute inheritance to the tree formed by the root and the subTree.
     *
     * @param root renderer to consider as the root of the subtree
     * @param subTree tree of {@link ISvgNodeRenderer}
     * @deprecated will be removed in 7.2 release, use
     * {@link #applyInheritanceToSubTree(ISvgNodeRenderer, ISvgNodeRenderer, SvgCssContext)} instead
     */
    @Deprecated
    public void applyInheritanceToSubTree(ISvgNodeRenderer root, ISvgNodeRenderer subTree) {
        SvgNodeRendererInheritanceResolver.applyInheritanceToSubTree(root, subTree, null);
    }

    @Deprecated
    protected void applyStyles(ISvgNodeRenderer parent, ISvgNodeRenderer child) {
        SvgNodeRendererInheritanceResolver.applyStyles(parent, child, null);
    }
}
