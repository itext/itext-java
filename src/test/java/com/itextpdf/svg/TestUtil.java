package com.itextpdf.svg;

import com.itextpdf.svg.renderers.ISvgNodeRenderer;

public class TestUtil {

    public static boolean compareDummyRendererTrees(ISvgNodeRenderer treeOne, ISvgNodeRenderer treeTwo){
        return compareDummyRendererTreesRecursive(treeOne,treeTwo);
    }

    private static boolean compareDummyRendererTreesRecursive(ISvgNodeRenderer treeNodeOne, ISvgNodeRenderer treeNodeTwo){
        //Name
        if(!treeNodeOne.toString().equals(treeNodeTwo.toString())){
            return false;
        }
        //Nr of children
        if(treeNodeOne.getChildren().size() != treeNodeTwo.getChildren().size()){
            return false;
        }
        //Expect empty collection when no children are present
        if(treeNodeOne.getChildren().isEmpty()){
            return true;
        }
        //Iterate over children
        boolean iterationResult = true;
        for (int i = 0; i <treeNodeOne.getChildren().size() ; i++) {
            iterationResult = iterationResult && compareDummyRendererTreesRecursive(treeNodeOne.getChildren().get(i),treeNodeTwo.getChildren().get(i));
        }
        return iterationResult;
    }
}
