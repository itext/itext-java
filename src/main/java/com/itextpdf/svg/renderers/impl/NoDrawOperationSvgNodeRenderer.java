package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * This renderer doesn't have any drawing operations.
 */
public class NoDrawOperationSvgNodeRenderer extends AbstractBranchSvgNodeRenderer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        // no draw to be performed
    }
}