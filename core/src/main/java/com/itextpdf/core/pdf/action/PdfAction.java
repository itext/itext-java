package com.itextpdf.core.pdf.action;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annots.PdfAnnotation;
import com.itextpdf.core.pdf.filespec.PdfFileSpec;
import com.itextpdf.core.pdf.filespec.PdfStringFS;
import com.itextpdf.core.pdf.navigation.PdfDestination;
import com.itextpdf.core.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.core.pdf.navigation.PdfStringDestination;

public class PdfAction extends PdfObjectWrapper<PdfDictionary> {

    protected PdfArray nextActionsChain;

    public PdfAction() {
        this(new PdfDictionary());
        put(PdfName.Type, PdfName.Action);
    }

    public PdfAction(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfAction(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    static public PdfAction createGoTo(PdfDestination destination) {
        return new PdfAction().put(PdfName.S, PdfName.GoTo).put(PdfName.D, destination);
    }

    static public PdfAction createGoTo(String destination) {
        return createGoTo(new PdfStringDestination(destination));
    }

    static public PdfAction createGoToR(PdfFileSpec fileSpec, PdfDestination destination, boolean newWindow) {
        return new PdfAction().put(PdfName.S, PdfName.GoToR).put(PdfName.F, fileSpec).
                put(PdfName.D, destination).put(PdfName.NewWindow, new PdfBoolean(newWindow));
    }

    static public PdfAction createGoToR(PdfFileSpec fileSpec, PdfDestination destination) {
        return new PdfAction().put(PdfName.S, PdfName.GoToR).put(PdfName.F, fileSpec).
                put(PdfName.D, destination);
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

    static public PdfAction createGoToE(PdfDestination destination, boolean newWindow, PdfTargetDictionary targetDictionary) {
        return createGoToE(null, destination, newWindow, targetDictionary);
    }

    static public PdfAction createGoToE(PdfFileSpec fileSpec, PdfDestination destination, boolean newWindow, PdfTargetDictionary targetDictionary) {
        return new PdfAction().put(PdfName.S, PdfName.GoToE).put(PdfName.F, fileSpec).put(PdfName.D, destination).
                put(PdfName.NewWindow, new PdfBoolean(newWindow)).put(PdfName.T, targetDictionary);
    }

    static public PdfAction createLaunch(PdfFileSpec fileSpec, boolean newWindow) {
        return createLaunch(fileSpec, null, newWindow);
    }

    static public PdfAction createLaunch(PdfFileSpec fileSpec) {
        return new PdfAction().put(PdfName.S, PdfName.Launch).put(PdfName.F, fileSpec);
    }

    static public PdfAction createLaunch(PdfFileSpec fileSpec, PdfWin win, boolean newWindow) {
        return new PdfAction().put(PdfName.S, PdfName.Launch).put(PdfName.F, fileSpec).put(PdfName.Win, win).
                put(PdfName.NewWindow, new PdfBoolean(newWindow));
    }

    static public PdfAction createThread(PdfFileSpec fileSpec, PdfObject destinationThread, PdfObject bead) {
        return new PdfAction().put(PdfName.S, PdfName.Launch).put(PdfName.F, fileSpec).
                put(PdfName.D, destinationThread).put(PdfName.B, bead);
    }

    static public PdfAction createThread(PdfFileSpec fileSpec) {
        return createThread(fileSpec, null, null);
    }

    static public PdfAction createURI(String uri) {
        return createURI(uri, false);
    }

    static public PdfAction createURI(String uri, boolean isMap) {
        return new PdfAction().put(PdfName.S, PdfName.URI).put(PdfName.URI, new PdfString(uri)).put(PdfName.IsMap, new PdfBoolean(isMap));
    }

    static public PdfAction createSound(PdfStream sound) {
        return new PdfAction().put(PdfName.S, PdfName.Sound).put(PdfName.Sound, sound);
    }

    static public PdfAction createSound(PdfStream sound, float volume, boolean synchronous, boolean repeat, boolean mix) {
        return new PdfAction().put(PdfName.S, PdfName.Sound).put(PdfName.Sound, sound).
                put(PdfName.Volume, new PdfNumber(volume)).put(PdfName.Synchronous, new PdfBoolean(synchronous)).
                put(PdfName.Repeat, new PdfBoolean(repeat)).put(PdfName.Mix, new PdfBoolean(mix));
    }

    static public PdfAction createMovie(PdfAnnotation annotation, String title, PdfName operation) {
        return new PdfAction().put(PdfName.S, PdfName.Movie).put(PdfName.Annotation, annotation).
                put(PdfName.T, new PdfString(title)).put(PdfName.Operation, operation);
    }

//    public PdfAction next(PdfAction action) {
//        initNextActionChain();
//        nextActionsChain.add(action);
//        return this;
//    }
//
//    public PdfAction next(List<PdfAction> actionList) {
//        initNextActionChain();
//        nextActionsChain.addAll(actionList);
//        return this;
//    }
//
//    public PdfAction next(PdfArray actionList) {
//        initNextActionChain();
//        nextActionsChain.addAll(actionList);
//        return this;
//    }
//
//    protected void initNextActionChain() {
//        if (nextActionsChain == null)
//            nextActionsChain = new PdfArray();
//    }

    private PdfAction put(PdfName key, PdfObject value) {
        if (value != null)
            getPdfObject().put(key, value);
        return this;
    }

    private PdfAction put(PdfName key, PdfObjectWrapper value) {
        return put(key, value == null ? null : value.getPdfObject());
    }

    static public class PdfTargetDictionary extends PdfObjectWrapper<PdfDictionary> {

        public PdfTargetDictionary(PdfDictionary pdfObject) {
            super(pdfObject);
        }

        public PdfTargetDictionary(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
            super(pdfObject, pdfDocument);
        }

        public PdfTargetDictionary(PdfName r) {
            this(new PdfDictionary());
            put(PdfName.R, r);
        }

        public PdfTargetDictionary(PdfName r, PdfString n, PdfObject p, PdfObject a, PdfTargetDictionary t) {
            this(new PdfDictionary());
            put(PdfName.R, r).put(PdfName.N, n).
                    put(PdfName.P, p).
                    put(PdfName.A, a).put(PdfName.T, t);
        }

        private PdfTargetDictionary put(PdfName key, PdfObject value) {
            getPdfObject().put(key, value);
            return this;
        }

        private PdfTargetDictionary put(PdfName key, PdfTargetDictionary value) {
            return put(key, value == null ? null : value.getPdfObject());
        }


    }

    static public class PdfWin extends PdfObjectWrapper<PdfDictionary> {

        public PdfWin(PdfDictionary pdfObject) {
            super(pdfObject);
        }

        public PdfWin(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
            super(pdfObject, pdfDocument);
        }

        public PdfWin(PdfString f) {
            this(new PdfDictionary());
            put(PdfName.F, f);
        }

        public PdfWin(PdfString f, PdfString d, PdfString o, PdfString p) {
            this(new PdfDictionary());
            put(PdfName.F, f).put(PdfName.D, d).put(PdfName.O, o).put(PdfName.P, p);
        }


        private PdfWin put(PdfName key, PdfObject value) {
            if (value != null)
                getPdfObject().put(key, value);
            return this;
        }

    }


}
