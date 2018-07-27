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
