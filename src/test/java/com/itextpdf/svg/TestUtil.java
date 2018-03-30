package com.itextpdf.svg;

import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;

public class TestUtil {

    @Deprecated
    public static boolean compareDummyRendererTrees(ISvgNodeRenderer treeOne, ISvgNodeRenderer treeTwo) {
        return compareDummyRendererTreesRecursive(treeOne, treeTwo);
    }

    private static boolean compareDummyRendererTreesRecursive(ISvgNodeRenderer treeNodeOne, ISvgNodeRenderer treeNodeTwo) {
        //Name
        if (!treeNodeOne.toString().equals(treeNodeTwo.toString())) {
            return false;
        }
        //Nr of children
        if (treeNodeOne instanceof IBranchSvgNodeRenderer && treeNodeTwo instanceof IBranchSvgNodeRenderer) {
            IBranchSvgNodeRenderer one = (IBranchSvgNodeRenderer) treeNodeOne;
            IBranchSvgNodeRenderer two = (IBranchSvgNodeRenderer) treeNodeTwo;
            if (one.getChildren().size() != two.getChildren().size()) {
                return false;
            }
            //Expect empty collection when no children are present
            if (one.getChildren().isEmpty()) {
                return true;
            }
            //Iterate over children
            boolean iterationResult = true;
            for (int i = 0; i < one.getChildren().size(); i++) {
                iterationResult = iterationResult && compareDummyRendererTreesRecursive(one.getChildren().get(i), two.getChildren().get(i));
            }
            return iterationResult;
        }
        return false;
    }
}
