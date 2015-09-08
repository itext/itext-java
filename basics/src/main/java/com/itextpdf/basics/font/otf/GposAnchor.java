/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.basics.font.otf;

/**
 *
 * @author admin
 */
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
