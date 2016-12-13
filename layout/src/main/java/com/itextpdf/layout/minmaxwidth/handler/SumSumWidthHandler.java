package com.itextpdf.layout.minmaxwidth.handler;

import com.itextpdf.layout.minmaxwidth.MinMaxWidth;

public class SumSumWidthHandler extends AbstractWidthHandler {

    public SumSumWidthHandler(MinMaxWidth minMaxWidth) {
        super(minMaxWidth);
    }

    @Override
    public void updateMinChildWidth(float childMinWidth) {
        minMaxWidth.setChildrenMinWidth(minMaxWidth.getChildrenMinWidth() + childMinWidth);
    }

    @Override
    public void updateMaxChildWidth(float childMaxWidth) {
        minMaxWidth.setChildrenMaxWidth(minMaxWidth.getChildrenMaxWidth() + childMaxWidth);
    }
}
