package com.itextpdf.layout.minmaxwidth.handler;

import com.itextpdf.layout.minmaxwidth.MinMaxWidth;

public class MaxSumWidthHandler extends AbstractWidthHandler {

    public MaxSumWidthHandler(MinMaxWidth minMaxWidth) {
        super(minMaxWidth);
    }

    @Override
    public void updateMinChildWidth(float childMinWidth) {
        minMaxWidth.setChildrenMinWidth(Math.max(minMaxWidth.getChildrenMinWidth(), childMinWidth));
    }

    @Override
    public void updateMaxChildWidth(float childMaxWidth) {
        minMaxWidth.setChildrenMaxWidth(minMaxWidth.getChildrenMaxWidth() + childMaxWidth);
    }
}
