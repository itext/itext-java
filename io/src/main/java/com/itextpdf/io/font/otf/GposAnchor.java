package com.itextpdf.io.font.otf;

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
