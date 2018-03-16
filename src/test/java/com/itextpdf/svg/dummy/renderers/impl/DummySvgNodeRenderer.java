package com.itextpdf.svg.dummy.renderers.impl;

import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A dummy implementation of {@link ISvgNodeRenderer} for testing purposes
 */
public class DummySvgNodeRenderer implements ISvgNodeRenderer {
    ISvgNodeRenderer parent;
    List<ISvgNodeRenderer> children;
    String name;
    
    public DummySvgNodeRenderer() {
        this("dummy");
    }

    public DummySvgNodeRenderer(String name) {
        this.name = name;
        this.children = new ArrayList<>();
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
        System.out.println(name+": Drawing in dummy node, children left: " + children.size());
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
    public void setAttributesAndStyles(Map<String, String> attributesAndStyles) {

    }

    @Override
    public String getAttribute(String key) {
        return "";
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof DummySvgNodeRenderer)){
            return false;
        }
        //Name
        DummySvgNodeRenderer otherDummy = (DummySvgNodeRenderer)o;
        if(!this.name.equals(otherDummy.name)){
            return false;
        }
        //children
        if(!(this.children.isEmpty() && otherDummy.children.isEmpty())){
            if(this.children.size() != otherDummy.children.size()){
                return false;
            }
            boolean iterationResult = true;
            for (int i = 0; i < this.children.size(); i++) {
                iterationResult &= this.children.get(i).equals(otherDummy.getChildren().get(i));
            }
            if(!iterationResult){
                return false;
            }
        }
        return true;
    }

}
