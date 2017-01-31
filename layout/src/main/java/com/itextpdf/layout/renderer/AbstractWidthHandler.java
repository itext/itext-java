package com.itextpdf.layout.renderer;

import com.itextpdf.layout.minmaxwidth.MinMaxWidth;

abstract class AbstractWidthHandler {
    MinMaxWidth minMaxWidth;

    public AbstractWidthHandler(MinMaxWidth minMaxWidth) {
        this.minMaxWidth = minMaxWidth;
    }

    abstract public void updateMinChildWidth(float childMinWidth);
    abstract public void updateMaxChildWidth(float childMaxWidth);

    public void updateMinMaxWidth(MinMaxWidth minMaxWidth) {
        if (minMaxWidth != null) {
            updateMaxChildWidth(minMaxWidth.getMaxWidth());
            updateMinChildWidth(minMaxWidth.getMinWidth());
        }
    }
}
