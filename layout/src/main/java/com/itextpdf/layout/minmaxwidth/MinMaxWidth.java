package com.itextpdf.layout.minmaxwidth;

import java.io.Serializable;

public class MinMaxWidth implements Serializable {

    private static final long serialVersionUID = -4642527900783929637L;

    private float childrenMinWidth;
    private float childrenMaxWidth;
    private float additionalWidth;
    private float availableWidth;

    public MinMaxWidth(float additionalWidth, float availableWidth) {
        this(additionalWidth, availableWidth, 0, 0);
    }

    public MinMaxWidth(float additionalWidth, float availableWidth, float childrenMinWidth, float childrenMaxWidth) {
        this.childrenMinWidth = childrenMinWidth;
        this.childrenMaxWidth = childrenMaxWidth;
        this.additionalWidth = additionalWidth;
        this.availableWidth = availableWidth;
    }

    public float getChildrenMinWidth() {
        return childrenMinWidth;
    }

    public void setChildrenMinWidth(float childrenMinWidth) {
        this.childrenMinWidth = childrenMinWidth;
    }

    public float getChildrenMaxWidth() {
        return childrenMaxWidth;
    }

    public void setChildrenMaxWidth(float childrenMaxWidth) {
        this.childrenMaxWidth = childrenMaxWidth;
    }

    public float getAdditionalWidth() {
        return additionalWidth;
    }

    public float getAvailableWidth() {
        return availableWidth;
    }

    public void setAdditionalWidth(float additionalWidth) {
        this.additionalWidth = additionalWidth;
    }

    public float getMaxWidth() {
        return Math.min(childrenMaxWidth + additionalWidth, availableWidth);
    }

    public float getMinWidth() {
        return Math.min(childrenMinWidth + additionalWidth, getMaxWidth());
    }

    @Override
    public String toString() {
        return "min=" + (childrenMinWidth + additionalWidth) +
                ", max=" + (childrenMaxWidth + additionalWidth) +
                "; (" + availableWidth +
                ")";
    }
}
