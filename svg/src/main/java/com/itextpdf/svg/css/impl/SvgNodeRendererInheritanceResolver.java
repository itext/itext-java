/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.AbstractBranchSvgNodeRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Style and attribute inheritance resolver for {@link ISvgNodeRenderer} objects
 */
public class SvgNodeRendererInheritanceResolver {

    private StyleResolverUtil sru;

    public SvgNodeRendererInheritanceResolver(){
        sru = new StyleResolverUtil();
    }
    /**
     * Apply style and attribute inheritance to the tree formed by the root and the subTree
     * @param root Renderer to consider as the root of the substree
     * @param subTree tree of {@link ISvgNodeRenderer}s
     */
    public void applyInheritanceToSubTree(ISvgNodeRenderer root, ISvgNodeRenderer subTree){
       //Merge inherited style declarations from parent into child
       applyStyles(root,subTree);
       //If subtree, iterate over tree
        if(subTree instanceof AbstractBranchSvgNodeRenderer) {
            AbstractBranchSvgNodeRenderer subTreeAsBranch = (AbstractBranchSvgNodeRenderer) subTree;
            for (ISvgNodeRenderer child : subTreeAsBranch.getChildren()) {
                applyInheritanceToSubTree(subTreeAsBranch,child);
            }
        }
    }

    protected void applyStyles(ISvgNodeRenderer parent, ISvgNodeRenderer child){
        if(parent != null && child != null) {
            Map<String, String> childStyles = child.getAttributeMapCopy();
            if(childStyles == null){
                childStyles = new HashMap<String,String>();
            }
            Map<String, String> parentStyles = parent.getAttributeMapCopy();
            String parentFontSize = parent.getAttribute(SvgConstants.Attributes.FONT_SIZE);
            if(parentFontSize == null){
                parentFontSize = "0";
            }

            for (Map.Entry<String, String> parentAttribute : parentStyles.entrySet()) {
                sru.mergeParentStyleDeclaration(childStyles, parentAttribute.getKey(), parentAttribute.getValue(), parentFontSize);
            }
            child.setAttributesAndStyles(childStyles);
        }
    }



}
