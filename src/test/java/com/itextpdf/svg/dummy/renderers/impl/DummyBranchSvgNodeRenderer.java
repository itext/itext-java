/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.svg.dummy.renderers.impl;

import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author BenoîtLagae
 */
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
        return true;//*/
    }
}
