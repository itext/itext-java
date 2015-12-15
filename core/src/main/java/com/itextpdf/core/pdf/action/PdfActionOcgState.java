package com.itextpdf.core.pdf.action;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;

import java.util.ArrayList;
import java.util.List;

/**
 * USed in Set OCG State actions.
 */
public class PdfActionOcgState {

    /**
     * Can be: OFF, ON, Toggle
     */
    private PdfName state;

    private List<PdfDictionary> ocgs;

    public PdfActionOcgState(PdfName state, List<PdfDictionary> ocgs) {
        this.state = state;
        this.ocgs = ocgs;
    }

    public PdfName getState() {
        return state;
    }

    public List<PdfDictionary> getOcgs() {
        return ocgs;
    }

    public List<PdfObject> getObjectList() {
        List<PdfObject> states = new ArrayList<PdfObject>();
        states.add(state);
        states.addAll(ocgs);
        return states;
    }

}
