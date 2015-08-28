package com.itextpdf.basics.font;


import com.itextpdf.basics.IntHashtable;

public class Type3Font extends FontProgram {

    private double[] fontMatrix = {0.001, 0, 0, 0.001, 0, 0};

    private IntHashtable widthsTable = new IntHashtable();

    @Override
    public int getPdfFontFlags() {
        return 0;
    }

    @Override
    protected int getRawWidth(int c, String name) {
        return 0;
    }

    @Override
    protected int[] getRawCharBBox(int c, String name) {
        return new int[0];
    }

    @Override
    public int getWidth(int char1) {
        return widthsTable.get(char1);
    }

    @Override
    public int getKerning(int char1, int char2) {
        throw new IllegalStateException();
    }

    public double[] getFontMatrix() {
        return fontMatrix;
    }

    public void setFontMatrix(double[] fontMatrix) {
        this.fontMatrix = fontMatrix;
    }

    public IntHashtable getWidthsTable() {
        return widthsTable;
    }


}
