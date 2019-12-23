package com.itextpdf.io.image;

public class PngChromaticities {
    private float xW;
    private float yW;
    private float xR;
    private float yR;
    private float xG;
    private float yG;
    private float xB;
    private float yB;

    public PngChromaticities(float xW, float yW, float xR, float yR, float xG, float yG, float xB, float yB) {
        this.xW = xW;
        this.yW = yW;
        this.xR = xR;
        this.yR = yR;
        this.xG = xG;
        this.yG = yG;
        this.xB = xB;
        this.yB = yB;
    }

    public float getXW() {
        return xW;
    }

    public float getYW() {
        return yW;
    }

    public float getXR() {
        return xR;
    }

    public float getYR() {
        return yR;
    }

    public float getXG() {
        return xG;
    }

    public float getYG() {
        return yG;
    }

    public float getXB() {
        return xB;
    }

    public float getYB() {
        return yB;
    }
}
