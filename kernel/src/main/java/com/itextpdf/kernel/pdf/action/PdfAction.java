package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.filespec.PdfStringFS;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;

import java.util.List;

public class PdfAction extends PdfObjectWrapper<PdfDictionary> {

    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_EXCLUDE = 1;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_INCLUDE_NO_VALUE_FIELDS = 2;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_HTML_FORMAT = 4;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_HTML_GET = 8;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_COORDINATES = 16;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_XFDF = 32;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_INCLUDE_APPEND_SAVES = 64;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_INCLUDE_ANNOTATIONS = 128;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_PDF = 256;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_CANONICAL_FORMAT = 512;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_EXCL_NON_USER_ANNOTS = 1024;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_EXCL_F_KEY = 2048;
    /**
     * a possible submitvalue
     */
    public static final int SUBMIT_EMBED_FORM = 8196;
    /**
     * a possible submitvalue
     */
    public static final int RESET_EXCLUDE = 1;

    public PdfAction() {
        this(new PdfDictionary());
        put(PdfName.Type, PdfName.Action);
    }

    public PdfAction(PdfDictionary pdfObject) {
        super(pdfObject);
        markObjectAsIndirect(getPdfObject());
    }

    public static PdfAction createGoTo(PdfDestination destination) {
        return new PdfAction().put(PdfName.S, PdfName.GoTo).put(PdfName.D, destination);
    }

    public static PdfAction createGoTo(String destination) {
        return createGoTo(new PdfStringDestination(destination));
    }

    public static PdfAction createGoToR(PdfFileSpec fileSpec, PdfDestination destination, boolean newWindow) {
        return new PdfAction().put(PdfName.S, PdfName.GoToR).put(PdfName.F, fileSpec).
                put(PdfName.D, destination).put(PdfName.NewWindow, new PdfBoolean(newWindow));
    }

    public static PdfAction createGoToR(PdfFileSpec fileSpec, PdfDestination destination) {
        return new PdfAction().put(PdfName.S, PdfName.GoToR).put(PdfName.F, fileSpec).
                put(PdfName.D, destination);
    }

    public static PdfAction createGoToR(String filename, int pageNum) {
        return createGoToR(filename, pageNum, false);
    }

    public static PdfAction createGoToR(String filename, int pageNum, boolean newWindow) {
        return createGoToR(new PdfStringFS(filename), PdfExplicitDestination.createFitH(pageNum, 10000), newWindow);
    }

    public static PdfAction createGoToR(String filename, String destination, boolean newWindow) {
        return createGoToR(new PdfStringFS(filename), new PdfStringDestination(destination), newWindow);
    }

    public static PdfAction createGoToR(String filename, String destination) {
        return createGoToR(filename, destination, false);
    }

    public static PdfAction createGoToE(PdfDestination destination, boolean newWindow, PdfTargetDictionary targetDictionary) {
        return createGoToE(null, destination, newWindow, targetDictionary);
    }

    public static PdfAction createGoToE(PdfFileSpec fileSpec, PdfDestination destination, boolean newWindow, PdfTargetDictionary targetDictionary) {
        return new PdfAction().put(PdfName.S, PdfName.GoToE).put(PdfName.F, fileSpec).put(PdfName.D, destination).
                put(PdfName.NewWindow, new PdfBoolean(newWindow)).put(PdfName.T, targetDictionary);
    }

    public static PdfAction createLaunch(PdfFileSpec fileSpec, boolean newWindow) {
        return createLaunch(fileSpec, null, newWindow);
    }

    public static PdfAction createLaunch(PdfFileSpec fileSpec) {
        return new PdfAction().put(PdfName.S, PdfName.Launch).put(PdfName.F, fileSpec);
    }

    public static PdfAction createLaunch(PdfFileSpec fileSpec, PdfWin win, boolean newWindow) {
        return new PdfAction().put(PdfName.S, PdfName.Launch).put(PdfName.F, fileSpec).put(PdfName.Win, win).
                put(PdfName.NewWindow, new PdfBoolean(newWindow));
    }

    public static PdfAction createThread(PdfFileSpec fileSpec, PdfObject destinationThread, PdfObject bead) {
        return new PdfAction().put(PdfName.S, PdfName.Launch).put(PdfName.F, fileSpec).
                put(PdfName.D, destinationThread).put(PdfName.B, bead);
    }

    public static PdfAction createThread(PdfFileSpec fileSpec) {
        return createThread(fileSpec, null, null);
    }

    public static PdfAction createURI(String uri) {
        return createURI(uri, false);
    }

    public static PdfAction createURI(String uri, boolean isMap) {
        return new PdfAction().put(PdfName.S, PdfName.URI).put(PdfName.URI, new PdfString(uri)).put(PdfName.IsMap, new PdfBoolean(isMap));
    }

    public static PdfAction createSound(PdfStream sound) {
        return new PdfAction().put(PdfName.S, PdfName.Sound).put(PdfName.Sound, sound);
    }

    public static PdfAction createSound(PdfStream sound, float volume, boolean synchronous, boolean repeat, boolean mix) {
        return new PdfAction().put(PdfName.S, PdfName.Sound).put(PdfName.Sound, sound).
                put(PdfName.Volume, new PdfNumber(volume)).put(PdfName.Synchronous, new PdfBoolean(synchronous)).
                put(PdfName.Repeat, new PdfBoolean(repeat)).put(PdfName.Mix, new PdfBoolean(mix));
    }

    public static PdfAction createMovie(PdfAnnotation annotation, String title, PdfName operation) {
        return new PdfAction().put(PdfName.S, PdfName.Movie).put(PdfName.Annotation, annotation).
                put(PdfName.T, new PdfString(title)).put(PdfName.Operation, operation);
    }

    public static PdfAction createHide(PdfAnnotation annotation, boolean hidden) {
        return new PdfAction().put(PdfName.S, PdfName.Hide).put(PdfName.T, annotation).
                put(PdfName.H, new PdfBoolean(hidden));
    }

    public static PdfAction createHide(PdfAnnotation[] annotations, boolean hidden) {
        return new PdfAction().put(PdfName.S, PdfName.Hide).put(PdfName.T, getArrayFromWrappersList(annotations)).
                put(PdfName.H, new PdfBoolean(hidden));
    }

    public static PdfAction createHide(String text, boolean hidden) {
        return new PdfAction().put(PdfName.S, PdfName.Hide).put(PdfName.T, new PdfString(text)).
                put(PdfName.H, new PdfBoolean(hidden));
    }

    public static PdfAction createHide(String[] text, boolean hidden) {
        return new PdfAction().put(PdfName.S, PdfName.Hide).put(PdfName.T, getArrayFromStringList(text)).
                put(PdfName.H, new PdfBoolean(hidden));
    }

    public static PdfAction createNamed(PdfName namedAction) {
        return new PdfAction().put(PdfName.S, PdfName.Named).put(PdfName.N, namedAction);
    }

    public static PdfAction createSetOcgState(List<PdfActionOcgState> states) {
        return createSetOcgState(states, false);
    }

    public static PdfAction createSetOcgState(List<PdfActionOcgState> states, boolean preserveRb) {
        PdfArray stateArr = new PdfArray();
        for (PdfActionOcgState state : states)
            stateArr.addAll(state.getObjectList());
        return new PdfAction().put(PdfName.S, PdfName.SetOCGState).put(PdfName.State, stateArr).put(PdfName.PreserveRB, new PdfBoolean(preserveRb));
    }

    public static PdfAction createRendition(PdfDocument pdfDocument, String file, PdfFileSpec fs, String mimeType, PdfAnnotation screenAnnotation) {
        return new PdfAction().put(PdfName.S, PdfName.Rendition).
                put(PdfName.OP, new PdfNumber(0)).put(PdfName.AN, screenAnnotation).
                put(PdfName.R, new PdfRendition(pdfDocument, file, fs, mimeType));
    }

    public static PdfAction createJavaScript(String javaScript) {
        return new PdfAction().put(PdfName.S, PdfName.JavaScript).put(PdfName.JS, new PdfString(javaScript));
    }

    public static PdfAction createSubmitForm(String file, Object[] names, int flags) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.SubmitForm);
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.F, new PdfString(file));
        dic.put(PdfName.FS, PdfName.URL);
        action.put(PdfName.F, dic);
        if (names != null) {
            action.put(PdfName.Fields, buildArray(names));
        }
        action.put(PdfName.Flags, new PdfNumber(flags));
        return action;
    }

    public static void setAdditionalAction(PdfObjectWrapper<PdfDictionary> wrapper, PdfName key, PdfAction action) {
        PdfDictionary dic;
        PdfObject obj = wrapper.getPdfObject().get(PdfName.AA);
        if (obj != null && obj.isDictionary())
            dic = (PdfDictionary) obj;
        else
            dic = new PdfDictionary();
        dic.put(key, action.getPdfObject());
        wrapper.put(PdfName.AA, dic);
    }


    /**
     * Add a chained action.
     *
     * @param na
     */
    public void next(PdfAction na) {
        PdfObject nextAction = getPdfObject().get(PdfName.Next);
        if (nextAction == null)
            put(PdfName.Next, na);
        else if (nextAction.isDictionary()) {
            PdfArray array = new PdfArray(nextAction);
            array.add(na.getPdfObject());
            put(PdfName.Next, array);
        } else {
            ((PdfArray) nextAction).add(na.getPdfObject());
        }
    }

    private static PdfArray getArrayFromWrappersList(PdfObjectWrapper[] wrappers) {
        PdfArray arr = new PdfArray();
        for (PdfObjectWrapper wrapper : wrappers) {
            arr.add(wrapper.getPdfObject());
        }
        return arr;
    }

    private static PdfArray getArrayFromStringList(String[] strings) {
        PdfArray arr = new PdfArray();
        for (String string : strings) {
            arr.add(new PdfString(string));
        }
        return arr;
    }

    private static PdfArray buildArray(Object names[]) {
        PdfArray array = new PdfArray();
        for (Object obj : names) {
            if (obj instanceof String)
                array.add(new PdfString((String) obj));
            else if (obj instanceof PdfAnnotation)
                array.add(((PdfAnnotation) obj).getPdfObject().getIndirectReference());
            else
                throw new PdfException("the.array.must.contain.string.or.pdfannotation");
        }
        return array;
    }
}
