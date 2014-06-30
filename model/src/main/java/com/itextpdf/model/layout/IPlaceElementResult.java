package com.itextpdf.model.layout;

/**
 * Element placement result.
 *
 * Currently this is only a 'enum' with Ok and NoMoreSpace values.
 * But later we'll probably add {@link com.itextpdf.model.elements.ElementPosition} field which will keep the position of a placed element.
 */
public interface IPlaceElementResult {

    static final public int Ok = 0;
    static final public int NoMoreSpace = 1;

    public int getPlacementStatus();

}
