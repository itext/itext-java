package com.itextpdf.layout.minmaxwidth.handler;

import com.itextpdf.layout.minmaxwidth.MinMaxWidth;

public abstract class AbstractWidthHandler {
    MinMaxWidth minMaxWidth;

    public AbstractWidthHandler(MinMaxWidth minMaxWidth) {
        this.minMaxWidth = minMaxWidth;
    }

    abstract public void updateMinChildWidth(float childMinWidth);
    abstract public void updateMaxChildWidth(float childMaxWidth);
}
