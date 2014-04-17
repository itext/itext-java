package com.itextpdf.model.layout;

public interface IPlaceElementResult {

    static final public int Ok = 0;
    static final public int NoMorePlace = 1;

    public int getPlacementStatus();

}
