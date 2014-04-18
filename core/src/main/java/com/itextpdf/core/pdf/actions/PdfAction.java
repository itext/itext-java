package com.itextpdf.core.pdf.actions;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.filespec.IPdfFileSpec;
import com.itextpdf.core.pdf.filespec.PdfStringFS;
import com.itextpdf.core.pdf.navigation.IPdfDestination;
import com.itextpdf.core.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.core.pdf.navigation.PdfStringDestination;
import com.itextpdf.core.pdf.objects.*;

public class PdfAction extends PdfDictionary {

    public PdfAction() {
        super();
        put(PdfName.Type, PdfName.Action);
    }

    public PdfAction(PdfDocument doc) {
        super(doc);
        put(PdfName.Type, PdfName.Action);
    }

    static public PdfAction createGoTo(IPdfDestination destination) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.GoTo);
        action.put(PdfName.D, (PdfObject) destination);
        return action;
    }

    static public PdfAction createGoTo(String destination) {
        return createGoTo(new PdfStringDestination(destination));
    }

    static public PdfAction createGoToR(IPdfFileSpec fileSpec, IPdfDestination destination, boolean newWindow) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.GoToR);
        action.put(PdfName.F, (PdfObject) fileSpec);
        action.put(PdfName.D, (PdfObject) destination);
        action.put(PdfName.NewWindow, new PdfBoolean(newWindow));
        return action;
    }

    static public PdfAction createGoToR(String filename, int pageNum) {
        return createGoToR(filename, pageNum, false);
    }

    static public PdfAction createGoToR(String filename, int pageNum, boolean newWindow) {
        return createGoToR(new PdfStringFS(filename), new PdfExplicitDestination().createFitH(pageNum, 10000), newWindow);
    }

    static public PdfAction createGoToR(String filename, String destination, boolean newWindow) {
        return createGoToR(new PdfStringFS(filename), new PdfStringDestination(destination), newWindow);
    }

    static public PdfAction createGoToR(String filename, String destination) {
        return createGoToR(filename, destination, false);
    }

    static public PdfAction createURI(String uri, boolean isMap) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.URI);
        action.put(PdfName.URI, new PdfString(uri));
        action.put(PdfName.IsMap, new PdfBoolean(isMap));
        return action;
    }

    static public PdfAction createURI(String uri) {
        return createURI(uri, false);
    }

}
