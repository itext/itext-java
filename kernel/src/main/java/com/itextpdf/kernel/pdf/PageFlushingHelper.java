/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class allows to free the memory taken by already processed pages when handling big PDF files.
 * It provides three alternative approaches for this, each of which has its own advantages and most suitable use cases:
 * {@link #unsafeFlushDeep(int)}, {@link #releaseDeep(int)}, {@link #appendModeFlush(int)}.
 * <p>
 * Each approach is designed to be most suitable for specific modes of document processing. There are four document
 * processing modes: reading, writing, stamping and append mode.
 * <p>
 * Reading mode: The {@link PdfDocument} instance is initialized using only {@link PdfReader} by
 * {@link PdfDocument#PdfDocument(PdfReader)} constructor.
 * <p>
 * Writing mode: The {@link PdfDocument} instance is initialized using only {@link PdfWriter} by
 * {@link PdfDocument#PdfDocument(PdfWriter)} constructor.
 * <p>
 * Stamping mode: The {@link PdfDocument} instance is initialized using both {@link PdfReader} and {@link PdfWriter} by
 * {@link PdfDocument#PdfDocument(PdfReader, PdfWriter)} constructor. If the optional third {@link StampingProperties}
 * argument is passed, its {@link StampingProperties#useAppendMode()} method shall NOT be called. <br>
 * This mode allows to update the existing document by completely recreating it. The complete document will be rewritten
 * by the end of {@link PdfDocument#close()} call.
 * <p>
 * Append mode: The {@link PdfDocument} instance is initialized using both {@link PdfReader} and {@link PdfWriter} by
 * {@link PdfDocument#PdfDocument(PdfReader, PdfWriter, StampingProperties)} constructor. The third {@link StampingProperties}
 * argument shall have {@link StampingProperties#useAppendMode()} method called. <br>
 * This mode preserves the document intact with all its data, but adds additional data at the end of the file,
 * which "overrides" and introduces amends to the original document. In this mode it's not required to rewrite the
 * complete document which can be highly beneficial for big PDF documents handling.
 * <p>
 * The {@link PageFlushingHelper} class operates with two concepts of PDF objects states: flushed and released objects.
 * <p>
 * Flushed object is the one which is finalized and has been completely written to the output stream. This frees its
 * memory but makes it impossible to modify it or read data from it. Whenever there is an attempt to modify or to fetch
 * flushed object inner contents an exception will be thrown. Flushing is only possible for objects in the writing
 * and stamping modes, also its possible to flush modified objects in append mode.
 * <p>
 * Released object is the one which has not been modified and has been "detached" from the {@link PdfDocument}, making it
 * possible to remove it from memory during the GC, even if the document is not closed yet. All released object instances
 * become read-only and any modifications will not be reflected in the resultant document. Read-only instances should be
 * considered as copies of the original objects. Released objects can be re-read, however after re-reading new object
 * instances are created. Releasing is only possible for not modified objects in reading, stamping and append modes.
 * It's important to remember though, that during {@link PdfDocument#close()} in stamping mode all released objects
 * will be re-read.
 * <p>
 * The {@link PageFlushingHelper} class doesn't work with PdfADocument instances.
 */
public class PageFlushingHelper {

    private static final DeepFlushingContext pageContext;

    static {
        pageContext = initPageFlushingContext();
    }

    private PdfDocument pdfDoc;
    private boolean release;
    // only PdfDictionary/PdfStream or PdfArray can be in this set.
    // Explicitly using HashSet for as field type for the sake of autoporting.
    private HashSet<PdfObject> currNestedObjParents = new HashSet<>();

    private Set<PdfIndirectReference> layersRefs = new HashSet<>();


    public PageFlushingHelper(PdfDocument pdfDoc) {
        this.pdfDoc = pdfDoc;
    }

    /**
     * Flushes to the output stream all objects belonging to the given page. This frees the memory taken by those
     * objects, but makes it impossible to modify them or read data from them.
     * <p>
     * This method is mainly designed for writing and stamping modes. It will throw an exception for documents
     * opened in reading mode (see {@link PageFlushingHelper} for more details on modes). This method can also be used for append
     * mode if new pages are added or existing pages are heavily modified and {@link #appendModeFlush(int)} is not enough.
     * <p>
     * This method is highly effective in freeing the memory and works properly for the vast majority of documents
     * and use cases, however it can potentially cause failures. If document handling fails with exception after
     * using this method, one should re-process the document with a "safe flushing" alternative
     * (see {@link PdfPage#flush()} or consider using append mode and {@link #appendModeFlush(int)} method).
     * <p>
     * The unsafety comes from the possibility of objects being shared between pages and the fact that object data
     * cannot be read after the flushing. Whenever flushed object is attempted to be modified or its data is fetched
     * the exception will be thrown (flushed object can be added to the other objects, though).
     * <p>
     * In stamping/append mode the issue occurs if some object is shared between two or more pages, and the first page
     * is flushed, and later for processing of the second page this object is required to be read/modified. Normally only
     * page resources (like images and fonts) are shared, which are often not required for page processing: for example
     * for page stamping (e.g. adding watermarks, headers, etc) only new resources are added. Among examples of when the
     * page resources are indeed required (and therefore the risk of this method causing failures being high) would be
     * page contents parsing: text extraction, any general {@link PdfCanvasProcessor} class usage, usage of pdfSweep addon.
     * <p>
     * In writing mode this method normally will work without issues: by default iText creates page objects in such way
     * that they are independent from each other. Again, the resources can be shared, but as mentioned above
     * it's safe to add already flushed resources to the other pages because this doesn't require reading data from them.
     * <p>
     * For append mode only modified objects are flushed, all others are released and can be re-read later on.
     * <p>
     * This method shall be used only when it's known that the page and its inner structures processing is finished.
     * This includes reading data from pages, page modification and page handling via addons/utilities.
     *
     * @param pageNum the page number which low level objects structure is to be flushed to the output stream.
     */
    public void unsafeFlushDeep(int pageNum) {
        if (pdfDoc.getWriter() == null) {
            throw new IllegalArgumentException(PdfException.FlushingHelperFLushingModeIsNotForDocReadingMode);
        }
        release = false;
        flushPage(pageNum);
    }

    /**
     * Releases memory taken by all not modified objects belonging to the given page, including the page dictionary itself.
     * This affects only the objects that are read from the existing input PDF.
     * <p>
     * This method is mainly designed for reading mode and also can be used in append mode (see {@link PageFlushingHelper}
     * for more details on modes). In append mode modified objects will be kept in memory.
     * The page and all its inner structure objects can be re-read again.
     * <p>
     * This method will not have any effect in the writing mode. It is also not advised to be used in stamping mode:
     * even though it will indeed release the objects, they will be definitely re-read again on document closing, which
     * would affect performance.
     * <p>
     * When using this method in append mode (or in stamping mode), be careful not to try to modify the object instances
     * obtained before the releasing! See {@link PageFlushingHelper} for details on released objects state.
     * <p>
     * This method shall be used only when it's known that the page and its inner structures processing is finished.
     * This includes reading data from pages, page modification and page handling via addons/utilities.
     *
     * @param pageNum the page number which low level objects structure is to be released from memory.
     */
    public void releaseDeep(int pageNum) {
        release = true;
        flushPage(pageNum);
    }

    /**
     * Flushes to the output stream modified objects that can belong only to the given page, which makes this method
     * "safe" compared to the {@link #unsafeFlushDeep(int)}. Flushed object frees the memory, but it's impossible to
     * modify such objects or read data from them. This method releases all other page structure objects that are not
     * modified.
     * <p>
     * This method is mainly designed for the append mode. It is similar to the {@link PdfPage#flush()}, but it
     * additionally releases all page objects that were not flushed. This method is ideal for small amendments of pages,
     * but it makes more sense to use {@link PdfPage#flush()} for newly created or heavily modified pages. <br>
     * This method will throw an exception for documents opened in reading mode (see {@link PageFlushingHelper}
     * for more details on modes). It is also not advised to be used in stamping mode: even though it will indeed
     * release the objects and free the memory, the released objects will definitely be re-read again on document
     * closing, which would affect performance.
     * <p>
     * When using this method in append mode (or in stamping mode), be careful not to try to modify the object instances
     * obtained before this method call! See {@link PageFlushingHelper} for details on released and flushed objects state.
     * <p>
     * This method shall be used only when it's known that the page and its inner structures processing is finished.
     * This includes reading data from pages, page modification and page handling via addons/utilities.
     *
     * @param pageNum the page number which low level objects structure is to be flushed or released from memory.
     */
    public void appendModeFlush(int pageNum) {
        if (pdfDoc.getWriter() == null) {
            throw new IllegalArgumentException(PdfException.FlushingHelperFLushingModeIsNotForDocReadingMode);
        }

        PdfPage page = pdfDoc.getPage(pageNum);
        if (page.isFlushed()) {
            return;
        }
        page.getDocument().dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.END_PAGE, page));

        boolean pageWasModified = page.getPdfObject().isModified();
        page.setModified();
        release = true;
        pageWasModified = flushPage(pageNum) || pageWasModified;

        PdfArray annots = page.getPdfObject().getAsArray(PdfName.Annots);
        if (annots != null && !annots.isFlushed()) {
            arrayFlushIfModified(annots);
        }

        PdfObject thumb = page.getPdfObject().get(PdfName.Thumb, false);
        flushIfModified(thumb);

        PdfObject contents = page.getPdfObject().get(PdfName.Contents, false);
        if (contents instanceof PdfIndirectReference) {
            if (contents.checkState(PdfObject.MODIFIED) && !contents.checkState(PdfObject.FLUSHED)) {
                PdfObject contentsDirectObj = ((PdfIndirectReference) contents).getRefersTo();
                if (contentsDirectObj.isArray()) {
                    arrayFlushIfModified((PdfArray) contentsDirectObj);
                } else {
                    contentsDirectObj.flush(); // already checked that modified
                }
            }
        } else if (contents instanceof PdfArray){
            arrayFlushIfModified((PdfArray) contents);
        } else if (contents instanceof PdfStream) {
            flushIfModified(contents);
        }

        // Page tags flushing is supported only in PdfPage#flush and #unsafeFlushDeep: it makes sense to flush tags
        // completely for heavily modified or new pages. For the slightly modified pages it should be enough to release
        // the tag structure objects via tag structure releasing utility.

        if (!pageWasModified) {
            page.getPdfObject().getIndirectReference().clearState(PdfObject.MODIFIED);
            pdfDoc.getCatalog().getPageTree().releasePage(pageNum);
            page.unsetForbidRelease();
            page.getPdfObject().release();
        } else {
            // inherited and modified resources are handled in #flushPage call in the beginning of method
            page.releaseInstanceFields();
            page.getPdfObject().flush();
        }
    }

    private boolean flushPage(int pageNum) {
        PdfPage page = pdfDoc.getPage(pageNum);
        if (page.isFlushed()) {
            return false;
        }
        boolean pageChanged = false;

        if (!release) {
            pdfDoc.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.END_PAGE, page));
            initCurrentLayers(pdfDoc);
        }

        PdfDictionary pageDict = page.getPdfObject();
        // Using PdfPage package internal methods in order to avoid PdfResources initialization: initializing PdfResources
        // limits processing possibilities only to cases in which resources and specific resource type dictionaries are not flushed.
        PdfDictionary resourcesDict = page.initResources(false); // inits /Resources dict entry if not inherited and not created yet
        PdfResources resources = page.getResources(false);
        if (resources != null && resources.isModified() && !resources.isReadOnly()) {
            resourcesDict = resources.getPdfObject();
            pageDict.put(PdfName.Resources, resources.getPdfObject());
            pageDict.setModified();
            pageChanged = true;
        }

        if (!resourcesDict.isFlushed()) {
            flushDictRecursively(resourcesDict, null);
            flushOrRelease(resourcesDict);
        }

        flushDictRecursively(pageDict, pageContext);

        if (release) {
            if (!page.getPdfObject().isModified()) {
                pdfDoc.getCatalog().getPageTree().releasePage(pageNum);
                page.unsetForbidRelease();
                page.getPdfObject().release();
            }
        } else {
            if (pdfDoc.isTagged() && !pdfDoc.getStructTreeRoot().isFlushed()) {
                page.tryFlushPageTags();
            }
            if (!pdfDoc.isAppendMode() || page.getPdfObject().isModified()) {
                page.releaseInstanceFields();
                page.getPdfObject().flush();
            } else { // it's append mode
                pdfDoc.getCatalog().getPageTree().releasePage(pageNum);
                page.unsetForbidRelease();
                page.getPdfObject().release();
            }
        }

        layersRefs.clear();

        return pageChanged;
    }

    private void initCurrentLayers(PdfDocument pdfDoc) {
        if (pdfDoc.getCatalog().isOCPropertiesMayHaveChanged()) {
            List<PdfLayer> layers = pdfDoc.getCatalog().getOCProperties(false).getLayers();
            for (PdfLayer layer : layers) {
                layersRefs.add(layer.getPdfObject().getIndirectReference());
            }
        }
    }

    private void flushObjectRecursively(PdfObject obj, DeepFlushingContext context) {
        if (obj == null) {
            return;
        }
        boolean avoidReleaseForIndirectObjInstance = false;
        if (obj.isIndirectReference()) {
            PdfIndirectReference indRef = (PdfIndirectReference) obj;
            if (indRef.refersTo == null || indRef.checkState(PdfObject.FLUSHED)) {
                return;
            }

            obj = indRef.getRefersTo();
        } else if (obj.isFlushed()) {
            return;
        } else if (release && obj.isIndirect()) {
            // We should avoid the case when object is going to be released but is stored in containing object
            // not as indirect reference. This can happen when containing object is somehow modified.
            // Generally containing objects should not contain released read-only object instance.
            assert obj.isReleaseForbidden() || obj.getIndirectReference() == null;
            avoidReleaseForIndirectObjInstance = true;
        }
        if (pdfDoc.isDocumentFont(obj.getIndirectReference()) || layersRefs.contains(obj.getIndirectReference())) {
            return;
        }

        if (obj.isDictionary() || obj.isStream()) {
            if (!currNestedObjParents.add(obj)) {
                return;
            }
            flushDictRecursively((PdfDictionary) obj, context);
            currNestedObjParents.remove(obj);
        } else if (obj.isArray()) {
            if (!currNestedObjParents.add(obj)) {
                return;
            }
            PdfArray array = (PdfArray) obj;
            for (int i = 0; i < array.size(); ++i) {
                flushObjectRecursively(array.get(i, false), context);
            }
            currNestedObjParents.remove(obj);
        }

        if (!avoidReleaseForIndirectObjInstance) {
            flushOrRelease(obj);
        }
    }

    private void flushDictRecursively(PdfDictionary dict, DeepFlushingContext context) {
        for (PdfName key : dict.keySet()) {
            DeepFlushingContext innerContext = null;
            if (context != null) {
                if (context.isKeyInBlackList(key)) {
                    continue;
                }
                innerContext = context.getInnerContextFor(key);
            }
            PdfObject value = dict.get(key, false);
            flushObjectRecursively(value, innerContext);
        }
    }

    private void flushOrRelease(PdfObject obj) {
        if (release) {
            if (!obj.isReleaseForbidden()) {
                obj.release();
            }
        } else {
            makeIndirectIfNeeded(obj);
            if (!pdfDoc.isAppendMode() || obj.isModified()) {
                obj.flush();
            } else if (!obj.isReleaseForbidden()) {
                obj.release();
            }
        }
    }

    private void flushIfModified(PdfObject o) {
        if (o != null && !(o instanceof PdfIndirectReference)) {
            makeIndirectIfNeeded(o);
            o = o.getIndirectReference();
        }
        if (o != null && o.checkState(PdfObject.MODIFIED) && !o.checkState(PdfObject.FLUSHED)) {
            ((PdfIndirectReference) o).getRefersTo().flush();
        }
    }

    private void arrayFlushIfModified(PdfArray contentsArr) {
        for (int i = 0; i < contentsArr.size(); ++i) {
            PdfObject c = contentsArr.get(i, false);
            flushIfModified(c);
        }
    }

    private void makeIndirectIfNeeded(PdfObject o) {
        if (o.checkState(PdfObject.MUST_BE_INDIRECT)) {
            o.makeIndirect(pdfDoc);
        }
    }

    private static DeepFlushingContext initPageFlushingContext() {
        Set<PdfName> ALL_KEYS_IN_BLACK_LIST = null;
        Map<PdfName, DeepFlushingContext> NO_INNER_CONTEXTS = Collections.<PdfName, DeepFlushingContext>emptyMap();


        // --- action dictionary context ---
        DeepFlushingContext actionContext = new DeepFlushingContext(
                // actions keys flushing blacklist
                new LinkedHashSet<>(Arrays.asList(
                        PdfName.D,
                        PdfName.SD,
                        PdfName.Dp,
                        PdfName.B,
                        PdfName.Annotation,
                        PdfName.T,
                        PdfName.AN,
                        PdfName.TA
                )),
                NO_INNER_CONTEXTS
        );

        DeepFlushingContext aaContext = new DeepFlushingContext(
                // all inner entries leading to this context
                actionContext
        );
        // ---


        // --- annotation dictionary context ---
        LinkedHashMap<PdfName, DeepFlushingContext> annotInnerContexts = new LinkedHashMap<>();

        DeepFlushingContext annotsContext = new DeepFlushingContext(
                // annotations flushing blacklist
                new LinkedHashSet<>(Arrays.asList(
                        PdfName.P,
                        PdfName.Popup,
                        PdfName.Dest,
                        PdfName.Parent,
                        // keys that belong to form fields which can be merged with widget annotations
                        PdfName.V
                )),
                annotInnerContexts
        );

        annotInnerContexts.put(PdfName.A, actionContext);
        annotInnerContexts.put(PdfName.PA, actionContext);
        annotInnerContexts.put(PdfName.AA, aaContext);
        // ---

        // --- separation info dictionary context ---
        DeepFlushingContext sepInfoContext = new DeepFlushingContext(
                // separation info dict flushing blacklist
                new LinkedHashSet<>(Collections.singletonList(
                        PdfName.Pages
                )),
                NO_INNER_CONTEXTS
        );
        // ---

        // --- bead dictionary context ---
        DeepFlushingContext bContext = new DeepFlushingContext(
                // bead dict flushing blacklist
                ALL_KEYS_IN_BLACK_LIST,
                NO_INNER_CONTEXTS
        );
        // ---

        // --- pres steps dictionary context ---
        LinkedHashMap<PdfName, DeepFlushingContext> presStepsInnerContexts = new LinkedHashMap<>();

        DeepFlushingContext presStepsContext = new DeepFlushingContext(
                // pres step dict flushing blacklist
                new LinkedHashSet<>(Collections.singletonList(
                        PdfName.Prev
                )),
                presStepsInnerContexts
        );

        presStepsInnerContexts.put(PdfName.NA, actionContext);
        presStepsInnerContexts.put(PdfName.PA, actionContext);
        // ---


        // --- page dictionary context ---
        LinkedHashMap<PdfName, DeepFlushingContext> pageInnerContexts = new LinkedHashMap<>();

        DeepFlushingContext pageContext = new DeepFlushingContext(
                new LinkedHashSet<>(Arrays.asList(
                        PdfName.Parent,
                        PdfName.DPart
                )),
                pageInnerContexts
        );

        pageInnerContexts.put(PdfName.Annots, annotsContext);
        pageInnerContexts.put(PdfName.B, bContext);
        pageInnerContexts.put(PdfName.AA, aaContext);
        pageInnerContexts.put(PdfName.SeparationInfo, sepInfoContext);
        pageInnerContexts.put(PdfName.PresSteps, presStepsContext);
        // ---

        return pageContext;
    }

    private static class DeepFlushingContext {
        Set<PdfName> blackList; // null stands for every key to be in black list
        Map<PdfName, DeepFlushingContext> innerContexts; // null stands for every key to be taking unconditional context
        DeepFlushingContext unconditionalInnerContext;

        public DeepFlushingContext(Set<PdfName> blackList, Map<PdfName, DeepFlushingContext> innerContexts) {
            this.blackList = blackList;
            this.innerContexts = innerContexts;
        }

        public DeepFlushingContext(DeepFlushingContext unconditionalInnerContext) {
            this.blackList = Collections.<PdfName>emptySet();
            this.innerContexts = null;
            this.unconditionalInnerContext = unconditionalInnerContext;
        }

        public boolean isKeyInBlackList(PdfName key) {
            return blackList == null || blackList.contains(key);
        }

        public DeepFlushingContext getInnerContextFor(PdfName key) {
            return innerContexts == null ? unconditionalInnerContext : innerContexts.get(key);
        }
    }
}
