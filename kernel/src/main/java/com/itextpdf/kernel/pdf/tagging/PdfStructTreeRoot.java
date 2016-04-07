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
import com.itextpdf.kernel.pdf.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfStructTreeRoot extends PdfObjectWrapper<PdfDictionary> implements IPdfStructElem {

    private static final long serialVersionUID = 2168384302241193868L;

    private ParentTreeHandler parentTreeHandler;

    public PdfStructTreeRoot(PdfDocument document) {
        this(new PdfDictionary().makeIndirect(document));
        getPdfObject().put(PdfName.Type, PdfName.StructTreeRoot);
    }

    /**
     * @param pdfObject must be an indirect object.
     */
    public PdfStructTreeRoot(PdfDictionary pdfObject) {
        super(pdfObject);
        ensureObjectIsAddedToDocument(pdfObject);
        setForbidRelease();
        parentTreeHandler = new ParentTreeHandler(this);
        getRoleMap();
    }

    public PdfStructElem addKid(PdfStructElem structElem) {
        return addKid(-1, structElem);
    }

    public PdfStructElem addKid(int index, PdfStructElem structElem) {
        addKidObject(index, structElem.getPdfObject());
        return structElem;
    }

    @Override
    public IPdfStructElem getParent() {
        return null;
    }

    /**
     * Gets list of the direct kids of StructTreeRoot.
     * If certain kid is flushed, there will be a {@code null} in the list on it's place.
     * @return list of the direct kids of StructTreeRoot.
     */
    @Override
    public List<IPdfStructElem> getKids() {
        PdfObject k = getPdfObject().get(PdfName.K);
        List<IPdfStructElem> kids = new ArrayList<>();

        if (k != null) {
            if (k.isArray()) {
                PdfArray a = (PdfArray) k;
                for (int i = 0; i < a.size(); i++) {
                    ifKidIsStructElementAddToList(a.get(i), kids);
                }
            } else {
                ifKidIsStructElementAddToList(k, kids);
            }
        }
        return kids;
    }

    public PdfArray getKidsObject() {
        PdfArray k = null;
        PdfObject kObj = getPdfObject().get(PdfName.K);
        if (kObj != null && kObj.isArray()) {
            k = (PdfArray) kObj;
        }
        if (k == null) {
            k = new PdfArray();
            getPdfObject().put(PdfName.K, k);
            if (kObj != null) {
                k.add(kObj);
            }
        }
        return k;
    }

    public PdfDictionary getRoleMap() {
        PdfDictionary roleMap = getPdfObject().getAsDictionary(PdfName.RoleMap);
        if (roleMap == null) {
            roleMap = new PdfDictionary();
            getPdfObject().put(PdfName.RoleMap, roleMap);
        }
        return roleMap;
    }

    /**
     * Creates and flushes parent tree entry for the page.
     * Effectively this means that new content mustn't be added to the page.
     * @param page {@link PdfPage} for which to create parent tree entry. Typically this page is flushed after this call.
     */
    public void createParentTreeEntryForPage(PdfPage page) {
        getParentTreeHandler().createParentTreeEntryForPage(page);
    }

    /**
     * Gets an unmodifiable collection of marked content references on page.
     * <br/><br/>
     * NOTE: Do not remove tags when iterating over returned collection, this could
     * lead to the ConcurrentModificationException, because returned collection is backed by the internal list of the
     * actual page tags.
     */
    public Collection<PdfMcr> getPageMarkedContentReferences(PdfPage page) {
        Map<Integer, PdfMcr> pageMcrs = getParentTreeHandler().getPageMarkedContentReferences(page);
        return pageMcrs != null ? Collections.unmodifiableCollection(pageMcrs.values()) : null;
    }

    public PdfMcr findMcrByMcid(PdfDictionary pageDict, int mcid) {
        return getParentTreeHandler().findMcrByMcid(pageDict, mcid);
    }

    public PdfObjRef findObjRefByStructParentIndex(PdfDictionary pageDict, int structParentIndex) {
        return getParentTreeHandler().findObjRefByStructParentIndex(pageDict, structParentIndex);
    }

    @Override
    public PdfName getRole() {
        return null;
    }

    @Override
    public void flush() {
        for (int i = 0; i < getDocument().getNumberOfPages(); ++i) {
            createParentTreeEntryForPage(getDocument().getPage(i + 1));
        }
        put(PdfName.ParentTree, getParentTreeHandler().buildParentTree());
        put(PdfName.ParentTreeNextKey, new PdfNumber(getDocument().getNextStructParentIndex()));
        flushAllKids(this);
        super.flush();
    }

    /**
     * Copies structure to a {@code destDocument}.
     * <br/><br/>
     * NOTE: Works only for {@code PdfStructTreeRoot} that is read from the document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param destDocument document to copy structure to. Shall not be current document.
     * @param page2page  association between original page and copied page.
     * @throws PdfException
     */
    public void copyTo(PdfDocument destDocument, Map<PdfPage, PdfPage> page2page) {
        StructureTreeCopier.copyTo(destDocument, page2page, getDocument());
    }

    /**
     * Copies structure to a {@code destDocument} and insert it in a specified position in the document.
     * <br/><br/>
     * NOTE: Works only for {@code PdfStructTreeRoot} that is read from the document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param destDocument       document to copy structure to.
     * @param insertBeforePage indicates where the structure to be inserted.
     * @param page2page        association between original page and copied page.
     * @throws PdfException
     */
    public void copyTo(PdfDocument destDocument, int insertBeforePage, Map<PdfPage, PdfPage> page2page) {
        StructureTreeCopier.copyTo(destDocument, insertBeforePage, page2page, getDocument());
    }

    public int getParentTreeNextKey() {
        // /ParentTreeNextKey entry is always inited on MaredContentReferencesManager initialization
        return getPdfObject().getAsNumber(PdfName.ParentTreeNextKey).getIntValue();
    }

    public int getNextMcidForPage(PdfPage page) {
        return getParentTreeHandler().getNextMcidForPage(page);
    }

    public PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }

    ParentTreeHandler getParentTreeHandler() {
        return parentTreeHandler;
    }

    void addKidObject(int index, PdfDictionary structElem) {
        if (index == -1)
            getKidsObject().add(structElem);
        else
            getKidsObject().add(index, structElem);
        if (PdfStructElem.isStructElem(structElem))
            structElem.put(PdfName.P, getPdfObject());
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    private void flushAllKids(IPdfStructElem elem) {
        for (IPdfStructElem kid : elem.getKids()) {
            if (kid instanceof PdfStructElem) {
                flushAllKids(kid);
                ((PdfStructElem) kid).flush();
            }
        }
    }

    private void ifKidIsStructElementAddToList(PdfObject kid, List<IPdfStructElem> kids) {
        if (kid.isFlushed()) {
            kids.add(null);
        } else if (kid.getType() == PdfObject.Dictionary && PdfStructElem.isStructElem((PdfDictionary) kid)) {
            kids.add(new PdfStructElem((PdfDictionary) kid));
        }
    }
}
