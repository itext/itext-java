package com.itextpdf.core.pdf;

import com.itextpdf.core.pdf.navigation.PdfDestination;

import java.util.ArrayList;
import java.util.List;

public class PdfOutline {

    private List<PdfOutline> children = new ArrayList<PdfOutline>();
    private String title;
    private PdfDictionary content;
    private PdfDestination destination;
    private PdfOutline parent;

    public PdfOutline (String title, PdfDictionary content, PdfOutline parent){
        this.title = title;
        this.content = content;
        this.parent = parent;
    }

    public void addChild(PdfOutline child){
        children.add(child);
    }

    public String getTitle() {
        return title;
    }

    public PdfDictionary getContent() {
        return content;
    }

    public List<PdfOutline> getAllChildren(){
        List<PdfOutline> clone = new ArrayList<PdfOutline>();
        clone.addAll(children);
        return clone;
    }

    public PdfOutline getParent(){
        return parent;
    }

    public PdfDestination getDestination() {
        return destination;
    }

    public void setDestination(PdfDestination destination){
        this.destination = destination;
    }
}
