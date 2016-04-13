/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.filespec.PdfStringFS;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;

import java.util.List;

public class PdfAction extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -3945353673249710860L;
	
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
        return new PdfAction().put(PdfName.S, PdfName.GoTo).put(PdfName.D, destination.getPdfObject());
    }

    public static PdfAction createGoTo(String destination) {
        return createGoTo(new PdfStringDestination(destination));
    }

    public static PdfAction createGoToR(PdfFileSpec fileSpec, PdfDestination destination, boolean newWindow) {
        return new PdfAction().put(PdfName.S, PdfName.GoToR).put(PdfName.F, fileSpec.getPdfObject()).
                put(PdfName.D, destination.getPdfObject()).put(PdfName.NewWindow, new PdfBoolean(newWindow));
    }

    public static PdfAction createGoToR(PdfFileSpec fileSpec, PdfDestination destination) {
        return new PdfAction().put(PdfName.S, PdfName.GoToR).put(PdfName.F, fileSpec.getPdfObject()).
                put(PdfName.D, destination.getPdfObject());
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
        PdfAction action = new PdfAction().put(PdfName.S, PdfName.GoToE).put(PdfName.NewWindow, new PdfBoolean(newWindow));
        if (fileSpec != null) {
            action.put(PdfName.F, fileSpec.getPdfObject());
        }
        if (destination != null) {
            action.put(PdfName.D, destination.getPdfObject());
        }
        if (targetDictionary != null) {
            action.put(PdfName.T, targetDictionary.getPdfObject());
        }
        return action;
    }

    public static PdfAction createLaunch(PdfFileSpec fileSpec, boolean newWindow) {
        return createLaunch(fileSpec, null, newWindow);
    }

    public static PdfAction createLaunch(PdfFileSpec fileSpec) {
        PdfAction action = new PdfAction().put(PdfName.S, PdfName.Launch);
        if (fileSpec != null) {
            action.put(PdfName.F, fileSpec.getPdfObject());
        }
        return action;
    }

    public static PdfAction createLaunch(PdfFileSpec fileSpec, PdfWin win, boolean newWindow) {
        PdfAction action = new PdfAction().put(PdfName.S, PdfName.Launch).put(PdfName.NewWindow, new PdfBoolean(newWindow));
        if (fileSpec != null) {
            action.put(PdfName.F, fileSpec.getPdfObject());
        }
        if (win != null) {
            action.put(PdfName.Win, win.getPdfObject());
        }
        return action;
    }

    public static PdfAction createThread(PdfFileSpec fileSpec, PdfObject destinationThread, PdfObject bead) {
        PdfAction action = new PdfAction().put(PdfName.S, PdfName.Launch).put(PdfName.D, destinationThread).put(PdfName.B, bead);
        if (fileSpec != null) {
            action.put(PdfName.F, fileSpec.getPdfObject());
        }
        return action;
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
        PdfAction action = new PdfAction().put(PdfName.S, PdfName.Movie).put(PdfName.T, new PdfString(title))
                .put(PdfName.Operation, operation);
        if (annotation != null) {
            action.put(PdfName.Annotation, annotation.getPdfObject());
        }
        return action;
    }

    public static PdfAction createHide(PdfAnnotation annotation, boolean hidden) {
        return new PdfAction().put(PdfName.S, PdfName.Hide).put(PdfName.T, annotation.getPdfObject()).
                put(PdfName.H, new PdfBoolean(hidden));
    }

    public static PdfAction createHide(PdfAnnotation[] annotations, boolean hidden) {
        return new PdfAction().put(PdfName.S, PdfName.Hide).put(PdfName.T, getPdfArrayFromAnnotationsList(annotations)).
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

    public static PdfAction createRendition(String file, PdfFileSpec fileSpec, String mimeType, PdfAnnotation screenAnnotation) {
        return new PdfAction().put(PdfName.S, PdfName.Rendition).
                put(PdfName.OP, new PdfNumber(0)).put(PdfName.AN, screenAnnotation.getPdfObject()).
                put(PdfName.R, new PdfRendition(file, fileSpec, mimeType).getPdfObject());
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

    public static PdfAction createResetForm(Object[] names, int flags) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.ResetForm);
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
        wrapper.getPdfObject().put(PdfName.AA, dic);
    }


    /**
     * Add a chained action.
     *
     * @param na
     */
    public void next(PdfAction na) {
        PdfObject nextAction = getPdfObject().get(PdfName.Next);
        if (nextAction == null) {
            put(PdfName.Next, na.getPdfObject());
        } else if (nextAction.isDictionary()) {
            PdfArray array = new PdfArray(nextAction);
            array.add(na.getPdfObject());
            put(PdfName.Next, array);
        } else {
            ((PdfArray) nextAction).add(na.getPdfObject());
        }
    }

    public PdfAction put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    private static PdfArray getPdfArrayFromAnnotationsList(PdfAnnotation[] wrappers) {
        PdfArray arr = new PdfArray();
        for (PdfAnnotation wrapper : wrappers) {
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
                array.add(((PdfAnnotation) obj).getPdfObject());
            else
                throw new PdfException("the.array.must.contain.string.or.pdfannotation");
        }
        return array;
    }
}
