/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.svg.renderers;

import java.util.List;

/**
 * Interface that defines branches in the NodeRenderer structure. Differs from a leaf renderer
 * in that a branch has children and as such methods that can add or retrieve those children.
 */
public interface IBranchSvgNodeRenderer extends ISvgNodeRenderer {

    /**
     * Adds a renderer as the last element of the list of children.
     *
     * @param child any renderer
     */
    void addChild(ISvgNodeRenderer child);

    /**
     * Gets all child renderers of this object.
     *
     * @return the list of child renderers (in the order that they were added)
     */
    List<ISvgNodeRenderer> getChildren();
}
