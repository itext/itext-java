package com.itextpdf.basics.font.otf;

public class GposAnchor {
    public int XCoordinate;
    public int YCoordinate;
    
    public GposAnchor() {
    }
    
    public GposAnchor(GposAnchor other) {
        this.XCoordinate = other.XCoordinate;
        this.YCoordinate = other.YCoordinate;
    }
}
