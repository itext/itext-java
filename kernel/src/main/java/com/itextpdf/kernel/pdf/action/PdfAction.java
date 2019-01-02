/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.io.LogMessageConstant;
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
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitRemoteGoToDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStructureDestination;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A wrapper for action dictionaries (ISO 32000-1 section 12.6).
 * An action dictionary defines the characteristics and behaviour of an action.
 */
public class PdfAction extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -3945353673249710860L;

    /**
     * A possible submit value
     */
    public static final int SUBMIT_EXCLUDE = 1;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_INCLUDE_NO_VALUE_FIELDS = 2;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_HTML_FORMAT = 4;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_HTML_GET = 8;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_COORDINATES = 16;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_XFDF = 32;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_INCLUDE_APPEND_SAVES = 64;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_INCLUDE_ANNOTATIONS = 128;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_PDF = 256;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_CANONICAL_FORMAT = 512;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_EXCL_NON_USER_ANNOTS = 1024;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_EXCL_F_KEY = 2048;
    /**
     * A possible submit value
     */
    public static final int SUBMIT_EMBED_FORM = 8196;
    /**
     * A possible submit value
     */
    public static final int RESET_EXCLUDE = 1;

    /**
     * Constructs an empty action that can be further modified.
     */
    public PdfAction() {
        this(new PdfDictionary());
        put(PdfName.Type, PdfName.Action);
    }

    /**
     * Constructs a {@link PdfAction} instance with a given dictionary. It can be used for handy
     * property reading in reading mode or modifying in stamping mode.
     *
     * @param pdfObject the dictionary to construct the wrapper around
     */
    public PdfAction(PdfDictionary pdfObject) {
        super(pdfObject);
        markObjectAsIndirect(getPdfObject());
    }

    /**
     * Creates a GoTo action (section 12.6.4.2 of ISO 32000-1) via a given destination.
     *
     * @param destination the desired destination of the action
     * @return created action
     */
    public static PdfAction createGoTo(PdfDestination destination) {
        validateNotRemoteDestination(destination);
        return new PdfAction().put(PdfName.S, PdfName.GoTo).put(PdfName.D, destination.getPdfObject());
    }

    /**
     * Creates a GoTo action (section 12.6.4.2 of ISO 32000-1) via a given {@link PdfStringDestination} name.
     *
     * @param destination {@link PdfStringDestination} name
     * @return created action
     */
    public static PdfAction createGoTo(String destination) {
        return createGoTo(new PdfStringDestination(destination));
    }

    /**
     * Creates a GoToR action, or remote action (section 12.6.4.3 of ISO 32000-1).
     *
     * @param fileSpec    the file in which the destination shall be located
     * @param destination the destination in the remote document to jump to
     * @param newWindow   a flag specifying whether to open the destination document in a new window
     * @return created action
     */
    public static PdfAction createGoToR(PdfFileSpec fileSpec, PdfDestination destination, boolean newWindow) {
        return createGoToR(fileSpec, destination).put(PdfName.NewWindow, PdfBoolean.valueOf(newWindow));
    }

    /**
     * Creates a GoToR action, or remote action (section 12.6.4.3 of ISO 32000-1).
     *
     * @param fileSpec    the file in which the destination shall be located
     * @param destination the destination in the remote document to jump to
     * @return created action
     */
    public static PdfAction createGoToR(PdfFileSpec fileSpec, PdfDestination destination) {
        validateRemoteDestination(destination);
        return new PdfAction().put(PdfName.S, PdfName.GoToR).put(PdfName.F, fileSpec.getPdfObject()).
                put(PdfName.D, destination.getPdfObject());
    }

    /**
     * Creates a GoToR action, or remote action (section 12.6.4.3 of ISO 32000-1).
     *
     * @param filename the remote destination file to jump to
     * @param pageNum  the remote destination document page to jump to
     * @return created action
     */
    public static PdfAction createGoToR(String filename, int pageNum) {
        return createGoToR(filename, pageNum, false);
    }

    /**
     * Creates a GoToR action, or remote action (section 12.6.4.3 of ISO 32000-1).
     *
     * @param filename  the remote destination file to jump to
     * @param pageNum   the remote destination document page to jump to
     * @param newWindow a flag specifying whether to open the destination document in a new window
     * @return created action
     */
    public static PdfAction createGoToR(String filename, int pageNum, boolean newWindow) {
        return createGoToR(new PdfStringFS(filename), PdfExplicitRemoteGoToDestination.createFitH(pageNum, 10000), newWindow);
    }

    /**
     * Creates a GoToR action, or remote action (section 12.6.4.3 of ISO 32000-1).
     *
     * @param filename    the remote destination file to jump to
     * @param destination the string destination in the remote document to jump to
     * @param newWindow   a flag specifying whether to open the destination document in a new window
     * @return created action
     */
    public static PdfAction createGoToR(String filename, String destination, boolean newWindow) {
        return createGoToR(new PdfStringFS(filename), new PdfStringDestination(destination), newWindow);
    }

    /**
     * Creates a GoToR action, or remote action (section 12.6.4.3 of ISO 32000-1).
     *
     * @param filename    the remote destination file to jump to
     * @param destination the string destination in the remote document to jump to
     * @return created action
     */
    public static PdfAction createGoToR(String filename, String destination) {
        return createGoToR(filename, destination, false);
    }

    /**
     * Creates a GoToE action, or embedded file action (section 12.6.4.4 of ISO 32000-1).
     *
     * @param destination      the destination in the target to jump to
     * @param newWindow        if true, the destination document should be opened in a new window;
     *                         if false, the destination document should replace the current document in the same window
     * @param targetDictionary A target dictionary specifying path information to the target document.
     *                         Each target dictionary specifies one element in the full path to the target and
     *                         may have nested target dictionaries specifying additional elements
     * @return created action
     */
    public static PdfAction createGoToE(PdfDestination destination, boolean newWindow, PdfTarget targetDictionary) {
        return createGoToE(null, destination, newWindow, targetDictionary);
    }

    /**
     * Creates a GoToE action, or embedded file action (section 12.6.4.4 of ISO 32000-1).
     *
     * @param fileSpec         The root document of the target relative to the root document of the source
     * @param destination      the destination in the target to jump to
     * @param newWindow        if true, the destination document should be opened in a new window;
     *                         if false, the destination document should replace the current document in the same window
     * @param targetDictionary A target dictionary specifying path information to the target document.
     *                         Each target dictionary specifies one element in the full path to the target and
     *                         may have nested target dictionaries specifying additional elements
     * @return created action
     */
    public static PdfAction createGoToE(PdfFileSpec fileSpec, PdfDestination destination, boolean newWindow, PdfTarget targetDictionary) {
        PdfAction action = new PdfAction().put(PdfName.S, PdfName.GoToE).put(PdfName.NewWindow, PdfBoolean.valueOf(newWindow));
        if (fileSpec != null) {
            action.put(PdfName.F, fileSpec.getPdfObject());
        }
        if (destination != null) {
            validateRemoteDestination(destination);
            action.put(PdfName.D, destination.getPdfObject());
        } else {
            LoggerFactory.getLogger(PdfAction.class).warn(LogMessageConstant.EMBEDDED_GO_TO_DESTINATION_NOT_SPECIFIED);
        }
        if (targetDictionary != null) {
            action.put(PdfName.T, targetDictionary.getPdfObject());
        }
        return action;
    }

    /**
     * Creates a Launch action (section 12.6.4.5 of ISO 32000-1).
     *
     * @param fileSpec  the application that shall be launched or the document that shall beopened or printed
     * @param newWindow a flag specifying whether to open the destination document in a new window
     * @return created action
     */
    public static PdfAction createLaunch(PdfFileSpec fileSpec, boolean newWindow) {
        return createLaunch(fileSpec).put(PdfName.NewWindow, new PdfBoolean(newWindow));
    }

    /**
     * Creates a Launch action (section 12.6.4.5 of ISO 32000-1).
     *
     * @param fileSpec the application that shall be launched or the document that shall beopened or printed
     * @return created action
     */
    public static PdfAction createLaunch(PdfFileSpec fileSpec) {
        PdfAction action = new PdfAction().put(PdfName.S, PdfName.Launch);
        if (fileSpec != null) {
            action.put(PdfName.F, fileSpec.getPdfObject());
        }
        return action;
    }

    /**
     * Creates a Thread action (section 12.6.4.6 of ISO 32000-1).
     * A thread action jumps to a specified bead on an article thread (see 12.4.3, "Articles"),
     * in either the current document or a different one. Table 205 shows the action dictionary
     * entries specific to this type of action.
     *
     * @param fileSpec          the file containing the thread. If this entry is absent, the thread is in the current file
     * @param destinationThread the destination thread
     * @param bead              the bead in the destination thread
     * @return created action
     */
    public static PdfAction createThread(PdfFileSpec fileSpec, PdfObject destinationThread, PdfObject bead) {
        PdfAction action = new PdfAction().put(PdfName.S, PdfName.Launch).put(PdfName.D, destinationThread).put(PdfName.B, bead);
        if (fileSpec != null) {
            action.put(PdfName.F, fileSpec.getPdfObject());
        }
        return action;
    }

    /**
     * Creates a Thread action (section 12.6.4.6 of ISO 32000-1).
     * A thread action jumps to a specified bead on an article thread (see 12.4.3, "Articles"),
     * in either the current document or a different one. Table 205 shows the action dictionary
     * entries specific to this type of action.
     *
     * @param fileSpec the file containing the thread. If this entry is absent, the thread is in the current file
     * @return created action
     */
    public static PdfAction createThread(PdfFileSpec fileSpec) {
        return createThread(fileSpec, null, null);
    }

    /**
     * Creates a URI action (section 12.6.4.7 of ISO 32000-1).
     *
     * @param uri the uniform resource identifier to resolve
     * @return created action
     */
    public static PdfAction createURI(String uri) {
        return createURI(uri, false);
    }

    /**
     * Creates a URI action (section 12.6.4.7 of ISO 32000-1).
     *
     * @param uri   the uniform resource identifier to resolve
     * @param isMap a flag specifying whether to track the mouse position when the URI is resolved
     * @return created action
     */
    public static PdfAction createURI(String uri, boolean isMap) {
        return new PdfAction().put(PdfName.S, PdfName.URI).put(PdfName.URI, new PdfString(uri)).put(PdfName.IsMap, PdfBoolean.valueOf(isMap));
    }

    /**
     * Creates a Sound action (section 12.6.4.8 of ISO 32000-1). Deprecated in PDF 2.0.
     *
     * @param sound a sound object defining the sound that shall be played (see section 13.3 of ISO 32000-1)
     * @return created action
     */
    public static PdfAction createSound(PdfStream sound) {
        return new PdfAction().put(PdfName.S, PdfName.Sound).put(PdfName.Sound, sound);
    }

    /**
     * Creates a Sound action (section 12.6.4.8 of ISO 32000-1). Deprecated in PDF 2.0.
     *
     * @param sound       a sound object defining the sound that shall be played (see section 13.3 of ISO 32000-1)
     * @param volume      the volume at which to play the sound, in the range -1.0 to 1.0. Default value: 1.0
     * @param synchronous a flag specifying whether to play the sound synchronously or asynchronously.
     *                    If this flag is <code>true</code>, the conforming reader retains control, allowing no further user
     *                    interaction other than canceling the sound, until the sound has been completely played.
     *                    Default value: <code>false</code>
     * @param repeat      a flag specifying whether to repeat the sound indefinitely
     *                    If this entry is present, the Synchronous entry shall be ignored. Default value: <code>false</code>
     * @param mix         a flag specifying whether to mix this sound with any other sound already playing
     * @return created action
     */
    public static PdfAction createSound(PdfStream sound, float volume, boolean synchronous, boolean repeat, boolean mix) {
        if (volume < -1 || volume > 1) {
            throw new IllegalArgumentException("volume");
        }
        return new PdfAction().put(PdfName.S, PdfName.Sound).put(PdfName.Sound, sound).
                put(PdfName.Volume, new PdfNumber(volume)).put(PdfName.Synchronous, PdfBoolean.valueOf(synchronous)).
                put(PdfName.Repeat, PdfBoolean.valueOf(repeat)).put(PdfName.Mix, PdfBoolean.valueOf(mix));
    }

    /**
     * Creates a Movie annotation (section 12.6.4.9 of ISO 32000-1). Deprecated in PDF 2.0.
     *
     * @param annotation a movie annotation identifying the movie that shall be played
     * @param title      the title of a movie annotation identifying the movie that shall be played
     * @param operation  the operation that shall be performed on the movie. Shall be one of the following:
     *                   {@link PdfName#Play}, {@link PdfName#Stop}, {@link PdfName#Pause}, {@link PdfName#Resume}
     * @return created annotation
     */
    public static PdfAction createMovie(PdfAnnotation annotation, String title, PdfName operation) {
        PdfAction action = new PdfAction().put(PdfName.S, PdfName.Movie).put(PdfName.T, new PdfString(title))
                .put(PdfName.Operation, operation);
        if (annotation != null) {
            action.put(PdfName.Annotation, annotation.getPdfObject());
        }
        return action;
    }

    /**
     * Creates a Hide action (section 12.6.4.10 of ISO 32000-1).
     *
     * @param annotation the annotation to be hidden or shown
     * @param hidden     a flag indicating whether to hide the annotation (<code>true</code>) or show it (<code>false</code>)
     * @return created action
     */
    public static PdfAction createHide(PdfAnnotation annotation, boolean hidden) {
        return new PdfAction().put(PdfName.S, PdfName.Hide).put(PdfName.T, annotation.getPdfObject()).
                put(PdfName.H, PdfBoolean.valueOf(hidden));
    }

    /**
     * Creates a Hide action (section 12.6.4.10 of ISO 32000-1).
     *
     * @param annotations the annotations to be hidden or shown
     * @param hidden      a flag indicating whether to hide the annotation (<code>true</code>) or show it (<code>false</code>)
     * @return created action
     */
    public static PdfAction createHide(PdfAnnotation[] annotations, boolean hidden) {
        return new PdfAction().put(PdfName.S, PdfName.Hide).put(PdfName.T, getPdfArrayFromAnnotationsList(annotations)).
                put(PdfName.H, PdfBoolean.valueOf(hidden));
    }

    /**
     * Creates a Hide action (section 12.6.4.10 of ISO 32000-1).
     *
     * @param text   a text string giving the fully qualified field name of an interactive form field whose
     *               associated widget annotation or annotations are to be affected
     * @param hidden a flag indicating whether to hide the annotation (<code>true</code>) or show it (<code>false</code>)
     * @return created action
     */
    public static PdfAction createHide(String text, boolean hidden) {
        return new PdfAction().put(PdfName.S, PdfName.Hide).put(PdfName.T, new PdfString(text)).
                put(PdfName.H, PdfBoolean.valueOf(hidden));
    }

    /**
     * Creates a Hide action (section 12.6.4.10 of ISO 32000-1).
     *
     * @param text   a text string array giving the fully qualified field names of interactive form fields whose
     *               associated widget annotation or annotations are to be affected
     * @param hidden a flag indicating whether to hide the annotation (<code>true</code>) or show it (<code>false</code>)
     * @return created action
     */
    public static PdfAction createHide(String[] text, boolean hidden) {
        return new PdfAction().put(PdfName.S, PdfName.Hide).put(PdfName.T, getArrayFromStringList(text)).
                put(PdfName.H, PdfBoolean.valueOf(hidden));
    }

    /**
     * Creates a Named action (section 12.6.4.11 of ISO 32000-1).
     *
     * @param namedAction the name of the action that shall be performed. Shall be one of the following:
     *                    {@link PdfName#NextPage}, {@link PdfName#PrevPage}, {@link PdfName#FirstPage}, {@link PdfName#LastPage}
     * @return created action
     */
    public static PdfAction createNamed(PdfName namedAction) {
        return new PdfAction().put(PdfName.S, PdfName.Named).put(PdfName.N, namedAction);
    }

    /**
     * Creates a Set-OCG-State action (section 12.6.4.12 of ISO 32000-1).
     *
     * @param states a list of {@link PdfActionOcgState} state descriptions
     * @return created action
     */
    public static PdfAction createSetOcgState(List<PdfActionOcgState> states) {
        return createSetOcgState(states, false);
    }

    /**
     * Creates a Set-OCG-State action (section 12.6.4.12 of ISO 32000-1).
     *
     * @param states     states a list of {@link PdfActionOcgState} state descriptions
     * @param preserveRb If true, indicates that radio-button state relationships between optional content groups
     *                   should be preserved when the states are applied
     * @return created action
     */
    public static PdfAction createSetOcgState(List<PdfActionOcgState> states, boolean preserveRb) {
        PdfArray stateArr = new PdfArray();
        for (PdfActionOcgState state : states)
            stateArr.addAll(state.getObjectList());
        return new PdfAction().put(PdfName.S, PdfName.SetOCGState).put(PdfName.State, stateArr).put(PdfName.PreserveRB, PdfBoolean.valueOf(preserveRb));
    }

    /**
     * Creates a Rendition action (section 12.6.4.13 of ISO 32000-1).
     *
     * @param file             the name of the media clip, for use in the user interface.
     * @param fileSpec         a full file specification or form XObject that specifies the actual media data
     * @param mimeType         an ASCII string identifying the type of data
     * @param screenAnnotation a screen annotation
     * @return created action
     */
    public static PdfAction createRendition(String file, PdfFileSpec fileSpec, String mimeType, PdfAnnotation screenAnnotation) {
        return new PdfAction().put(PdfName.S, PdfName.Rendition).
                put(PdfName.OP, new PdfNumber(0)).put(PdfName.AN, screenAnnotation.getPdfObject()).
                put(PdfName.R, new PdfRendition(file, fileSpec, mimeType).getPdfObject());
    }

    /**
     * Creates a JavaScript action (section 12.6.4.16 of ISO 32000-1).
     *
     * @param javaScript a text string containing the JavaScript script to be executed.
     * @return created action
     */
    public static PdfAction createJavaScript(String javaScript) {
        return new PdfAction().put(PdfName.S, PdfName.JavaScript).put(PdfName.JS, new PdfString(javaScript));
    }

    /**
     * Creates a Submit-Form Action (section 12.7.5.2 of ISO 32000-1).
     *
     * @param file  a uniform resource locator, as described in 7.11.5, "URL Specifications"
     * @param names an array identifying which fields to include in the submission or which to exclude,
     *              depending on the setting of the Include/Exclude flag in the Flags entry.
     *              This is an optional parameter and can be <code>null</code>
     * @param flags a set of flags specifying various characteristics of the action (see Table 237 of ISO 32000-1).
     *              Default value to be passed: 0.
     * @return created action
     */
    public static PdfAction createSubmitForm(String file, Object[] names, int flags) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.SubmitForm);

        PdfDictionary urlFileSpec = new PdfDictionary();
        urlFileSpec.put(PdfName.F, new PdfString(file));
        urlFileSpec.put(PdfName.FS, PdfName.URL);
        action.put(PdfName.F, urlFileSpec);

        if (names != null) {
            action.put(PdfName.Fields, buildArray(names));
        }
        action.put(PdfName.Flags, new PdfNumber(flags));
        return action;
    }

    /**
     * Creates a Reset-Form Action (section 12.7.5.3 of ISO 32000-1).
     *
     * @param names an array identifying which fields to reset or which to exclude from resetting,
     *              depending on the setting of the Include/Exclude flag in the Flags entry (see Table 239 of ISO 32000-1).
     * @param flags a set of flags specifying various characteristics of the action (see Table 239 of ISO 32000-1).
     *              Default value to be passed: 0.
     * @return created action
     */
    public static PdfAction createResetForm(Object[] names, int flags) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.ResetForm);
        if (names != null) {
            action.put(PdfName.Fields, buildArray(names));
        }
        action.put(PdfName.Flags, new PdfNumber(flags));
        return action;
    }

    /**
     * Adds an additional action to the provided {@link PdfObjectWrapper}&lt;{@link PdfDictionary}&gt; wrapper.
     *
     * @param wrapper the wrapper to add an additional action to
     * @param key     a {@link PdfName} specifying the name of an additional action
     * @param action  the {@link PdfAction} to add as an additional action
     */
    public static void setAdditionalAction(PdfObjectWrapper<PdfDictionary> wrapper, PdfName key, PdfAction action) {
        PdfDictionary dic;
        PdfObject obj = wrapper.getPdfObject().get(PdfName.AA);
        if (obj != null && obj.isDictionary()) {
            dic = (PdfDictionary) obj;
        } else {
            dic = new PdfDictionary();
        }
        dic.put(key, action.getPdfObject());
        wrapper.getPdfObject().put(PdfName.AA, dic);
        wrapper.getPdfObject().setModified();
    }

    /**
     * Adds a chained action.
     *
     * @param nextAction the next action or sequence of actions that shall be performed after the current action
     */
    public void next(PdfAction nextAction) {
        PdfObject currentNextAction = getPdfObject().get(PdfName.Next);
        if (currentNextAction == null) {
            put(PdfName.Next, nextAction.getPdfObject());
        } else if (currentNextAction.isDictionary()) {
            PdfArray array = new PdfArray(currentNextAction);
            array.add(nextAction.getPdfObject());
            put(PdfName.Next, array);
        } else {
            ((PdfArray) currentNextAction).add(nextAction.getPdfObject());
        }
    }

    /**
     * Inserts the value into the underlying object of this {@link PdfAction} and associates it with the specified key.
     * If the key is already present in this {@link PdfAction}, this method will override the old value with the specified one.
     *
     * @param key   key to insert or to override
     * @param value the value to associate with the specified key
     * @return this {@link PdfAction} instance
     */
    public PdfAction put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
    }

    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        super.flush();
    }

    /**
     * {@inheritDoc}
     */
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

    private static PdfArray buildArray(Object[] names) {
        PdfArray array = new PdfArray();
        for (Object obj : names) {
            if (obj instanceof String) {
                array.add(new PdfString((String) obj));
            } else if (obj instanceof PdfAnnotation) {
                array.add(((PdfAnnotation) obj).getPdfObject());
            } else {
                throw new PdfException("The array must contain string or PDFAnnotation");
            }
        }
        return array;
    }

    private static void validateRemoteDestination(PdfDestination destination) {
        // No page object can be specified for a destination associated with a remote go-to action because the
        // destination page is in a different PDF document. In this case, the page parameter specifies an integer
        // page number within the remote document instead of a page object in the current document.
        // See section 12.3.2.2 of ISO 32000-1.
        if (destination instanceof PdfExplicitDestination) {
            PdfObject firstObj = ((PdfArray)destination.getPdfObject()).get(0);
            if (firstObj.isDictionary()) {
                throw new IllegalArgumentException("Explicit destinations shall specify page number in remote go-to actions instead of page dictionary");
            }
        } else if (destination instanceof PdfStructureDestination) {
            // No structure element dictionary can be specified for a structure destination associated with a remote
            // go-to action because the destination structure element is in a
            // different PDF document. In this case, the indirect reference to the structure element dictionary shall be
            // replaced by a byte string representing a structure element ID
            PdfObject firstObj = ((PdfArray)destination.getPdfObject()).get(0);
            if (firstObj.isDictionary()) {
                PdfDictionary structElemObj = (PdfDictionary)firstObj;
                PdfString id = structElemObj.getAsString(PdfName.ID);
                if (id == null) {
                    throw new IllegalArgumentException("Structure destinations shall specify structure element ID in remote go-to actions. Structure element that has no ID is specified instead");
                } else {
                    LoggerFactory.getLogger(PdfAction.class).warn(LogMessageConstant.STRUCTURE_ELEMENT_REPLACED_BY_ITS_ID_IN_STRUCTURE_DESTINATION);
                    ((PdfArray)destination.getPdfObject()).set(0, id);
                    destination.getPdfObject().setModified();
                }
            }
        }
    }

    public static void validateNotRemoteDestination(PdfDestination destination) {
        if (destination instanceof PdfExplicitRemoteGoToDestination) {
            LoggerFactory.getLogger(PdfAction.class).warn(LogMessageConstant.INVALID_DESTINATION_TYPE);
        } else if (destination instanceof PdfExplicitDestination) {
            // No page number can be specified for a destination associated with a not remote go-to action because the
            // destination page is in a current PDF document. See section 12.3.2.2 of ISO 32000-1.
            PdfObject firstObj = ((PdfArray)destination.getPdfObject()).get(0);
            if (firstObj.isNumber()) {
                LoggerFactory.getLogger(PdfAction.class).warn(LogMessageConstant.INVALID_DESTINATION_TYPE);
            }
        }
    }
}
