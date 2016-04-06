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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNull;
import com.itextpdf.kernel.pdf.PdfNumTree;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class MarkedContentReferencesManager {

    private PdfStructTreeRoot structTreeRoot;

    private Map<PdfDictionary, Integer> objRefs = new HashMap<>();

    /**
     * Represents parentTree in structTreeRoot. It contains only those entries that belong to the already flushed pages.
     */
    private PdfNumTree parentTree;

    /**
     * Contains marked content references for every page.
     * <p>
     * If new mcrs are added to the tag structure after this field is initialized, these new mcrs are also added to this map.
     * The idea that this field is initialized only once, therefore the struct tree would be traversed only once.
     * </p>
     * <p>
     * On this field initializing the whole tag structure is traversed.
     * This field is initialized:
     * <ul>
     *      <li> when some structure element is flushed;</li>
     *      <li> when {@code getPageMarkedContentReferences} method is called;</li>
     * </ul>
     * </p>
     *
     * <p>
     * If document structure tree was modified on low (PdfObjects) level information of this field could become incorrect.
     * In this case field shall be recalculated. This could be done by calling {@link #unregisterAllMcrs()} and then when
     * it is accessed it will be calculated again.
     * </p>
     */
    private Map<PdfDictionary, List<PdfMcr>> pageToPageMcrs;

    MarkedContentReferencesManager(PdfStructTreeRoot structTreeRoot) {
        this.structTreeRoot = structTreeRoot;
        parentTree = new PdfNumTree(structTreeRoot.getDocument().getCatalog(), PdfName.ParentTree);

    }

    /**
     * Creates and flushes parent tree entry for the page.
     * Effectively this means that new content mustn't be added to the page.
     * @param page {@link PdfPage} for which to create parent tree entry. Typically this page is flushed after this call.
     */
    public void createParentTreeEntryForPage(PdfPage page) {
        List<PdfMcr> mcrs = getPageMarkedContentReferences(page);
        if (mcrs == null) {
            return;
        }
        pageToPageMcrs.remove(page.getPdfObject());
        updateStructParentTreeEntries(page.getStructParentIndex(), mcrs);
    }

    /**
     * Gets a list of marked content references on page.
     */
    public List<PdfMcr> getPageMarkedContentReferences(PdfPage page) {
        registerAllMcrsIfNotRegistered();
        return pageToPageMcrs.get(page.getPdfObject());
    }

    // TODO THIS
    public int getStructParentIndex() {
        PdfArray nums = null;
        PdfArray kids = structTreeRoot.getParentTreeObject().getAsArray(PdfName.Kids);
        if (kids != null) {
            nums = new PdfArray();
            for (int i = 0; i < kids.size(); i++) {
                PdfObject o = kids.get(i);
                if (o instanceof PdfDictionary) {
                    PdfArray numsLocal = ((PdfDictionary) o).getAsArray(PdfName.Nums);
                    if (numsLocal != null) {
                        nums.addAll(numsLocal);
                    }
                } else {
                    LoggerFactory.getLogger(this.getClass()).warn("Suspicious nums element in StructParentTree", o);
                }
            }
        }

        int maxStructParentIndex = 0;
        if (nums == null)
            nums = structTreeRoot.getParentTreeObject().getAsArray(PdfName.Nums);
        if (nums != null) {
            for (int i = 0; i < nums.size(); i++) {
                PdfNumber n = nums.getAsNumber(i);
                if (n != null && n.getIntValue() > maxStructParentIndex)
                    maxStructParentIndex = n.getIntValue();
            }
        }

        return maxStructParentIndex;

    }

    public void registerMcr(PdfMcr mcr) {
        if (pageToPageMcrs == null) {
            return;
        }

        List<PdfMcr> pageMcrs = pageToPageMcrs.get(mcr.getPageObject());
        if (pageMcrs == null) {
            pageMcrs = new ArrayList<>();
            pageToPageMcrs.put(mcr.getPageObject(), pageMcrs);
        }
        pageMcrs.add(mcr);
        if (mcr instanceof PdfObjRef) {
            registerObjRef((PdfObjRef) mcr);
        }
    }

    public void unregisterMcr(PdfMcr mcrToUnregister) {
        if (pageToPageMcrs == null) {
            return;
        }

        List<PdfMcr> pageMcrs = pageToPageMcrs.get(mcrToUnregister.getPageObject());
        if (pageMcrs != null) {
            PdfMcr mcrObjectToRemove = null;
            for (PdfMcr mcr : pageMcrs) {
                if (mcr.getPdfObject() == mcrToUnregister.getPdfObject()) {
                    mcrObjectToRemove = mcr;
                    break;
                }
            }
            pageMcrs.remove(mcrObjectToRemove);

            if (mcrToUnregister instanceof PdfObjRef) {
                objRefs.remove(mcrToUnregister.getPdfObject());
            }
        }
    }

    PdfDictionary buildParentTree() {
        return parentTree.buildTree().makeIndirect(structTreeRoot.getDocument());
    }

    /**
     * It should be called when tag structure of document was modified on low level (on PdfObjects level).
     * E.g. this happens when we copy new pages into document.
     */
    void unregisterAllMcrs() {
        if (structTreeRoot.isStructTreeIsPartialFlushed()) {
            throw new PdfException(PdfException.CannotModifyTagStructureWhenItWasPartlyFlushed);
        }
        pageToPageMcrs = null;
        parentTree = new PdfNumTree(structTreeRoot.getDocument().getCatalog(), PdfName.ParentTree);
    }

    void registerAllMcrsIfNotRegistered() {
        if (pageToPageMcrs == null) {
            pageToPageMcrs = new HashMap<>();
            registerAllMcrs(structTreeRoot);

            Comparator<PdfMcr> mcrComparator = new Comparator<PdfMcr>() {
                @Override
                public int compare(PdfMcr o1, PdfMcr o2) {
                    Integer mcid1 = o1.getMcid();
                    Integer mcid2 = o2.getMcid();

                    if (mcid1 == null && mcid2 == null) {
                        return 0;
                    }
                    if (mcid1 == null) {
                        return -1;
                    }
                    if (mcid2 == null) {
                        return 1;
                    }

                    return Integer.compare(mcid1, mcid2);
                }
            };
            for (List<PdfMcr> pdfMcrs : pageToPageMcrs.values()) {
                Collections.sort(pdfMcrs, mcrComparator);
            }
        }
    }

    private void registerAllMcrs(IPdfStructElem element) {
        if (element == null)  return;
        if (element instanceof PdfMcr) {
            registerMcr((PdfMcr)element);
        } else {
            for (IPdfStructElem kid : element.getKids()) {
                registerAllMcrs(kid);
            }
        }
    }
    /**
     * Number and dictionary references in list shall be ordered by mcid ascending.
     * Number and dictionary references in list shall belong to the same page.
     * @param pageStructParentIndex structParent index of the page to which mcrs belong.
     * @param mcrs list of the marked content references, that belong to the page with given structParent index.
     */
    private void updateStructParentTreeEntries(Integer pageStructParentIndex, List<PdfMcr> mcrs) {
        // element indexes in parentsOfPageMcrs shall be the same as mcid of one of their kids.
        // See "Finding Structure Elements from Content Items" in pdf spec.
        PdfArray parentsOfPageMcrs = new PdfArray();
        int currentMcid = 0;
        for (PdfMcr mcr : mcrs) {
            if (mcr instanceof PdfObjRef) {
                Integer structParent = this.objRefs.get(mcr.getPdfObject());
                if (structParent != null) {
                    parentTree.addEntry(structParent, ((PdfStructElem) mcr.getParent()).getPdfObject());
                }
            } else {
                // if for some reason some mcr where not registered or don't exist, we ensure that the rest
                // of the parent objects were placed at correct index
                while (currentMcid++ < mcr.getMcid()) {
                    parentsOfPageMcrs.add(PdfNull.PdfNull);
                }
                parentsOfPageMcrs.add(((PdfStructElem)mcr.getParent()).getPdfObject());
            }
        }


        if (!parentsOfPageMcrs.isEmpty()) {
            parentsOfPageMcrs.makeIndirect(structTreeRoot.getDocument());
            parentTree.addEntry(pageStructParentIndex, parentsOfPageMcrs);
            parentsOfPageMcrs.flush();
        }
    }

    private void registerObjRef(PdfObjRef objRef) {
        if (objRef == null)
            return;
        PdfDictionary o = ((PdfDictionary) objRef.getPdfObject()).getAsDictionary(PdfName.Obj);
        if (o != null) {
            PdfNumber n = o.getAsNumber(PdfName.StructParent);
            if (n != null)
                objRefs.put((PdfDictionary) objRef.getPdfObject(), n.getIntValue());
        }
    }
}
