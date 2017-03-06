/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.IsoKey;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfStructElem extends PdfObjectWrapper<PdfDictionary> implements IPdfStructElem {

    private static final long serialVersionUID = 7204356181229674005L;

    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    public static int Unknown = 0;
    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    public static int Grouping = 1;
    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    public static int BlockLevel = 2;
    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    public static int InlineLevel = 3;
    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    public static int Illustration = 4;

    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    public static Set<PdfName> groupingRoles = new HashSet<PdfName>();
    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    public static Set<PdfName> blockLevelRoles = new HashSet<PdfName>();
    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    public static Set<PdfName> inlineLevelRoles = new HashSet<PdfName>();
    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    public static Set<PdfName> illustrationRoles = new HashSet<PdfName>();

    static {
        groupingRoles.add(PdfName.Document);
        groupingRoles.add(PdfName.Part);
        groupingRoles.add(PdfName.Art);
        groupingRoles.add(PdfName.Sect);
        groupingRoles.add(PdfName.Div);
        groupingRoles.add(PdfName.BlockQuote);
        groupingRoles.add(PdfName.Caption);
        groupingRoles.add(PdfName.Caption);
        groupingRoles.add(PdfName.TOC);
        groupingRoles.add(PdfName.TOCI);
        groupingRoles.add(PdfName.Index);
        groupingRoles.add(PdfName.NonStruct);
        groupingRoles.add(PdfName.Private);

        blockLevelRoles.add(PdfName.P);
        blockLevelRoles.add(PdfName.H);
        blockLevelRoles.add(PdfName.H1);
        blockLevelRoles.add(PdfName.H2);
        blockLevelRoles.add(PdfName.H3);
        blockLevelRoles.add(PdfName.H4);
        blockLevelRoles.add(PdfName.H5);
        blockLevelRoles.add(PdfName.H6);
        blockLevelRoles.add(PdfName.L);
        blockLevelRoles.add(PdfName.Lbl);
        blockLevelRoles.add(PdfName.LI);
        blockLevelRoles.add(PdfName.LBody);
        blockLevelRoles.add(PdfName.Table);
        blockLevelRoles.add(PdfName.TR);
        blockLevelRoles.add(PdfName.TH);
        blockLevelRoles.add(PdfName.TD);
        blockLevelRoles.add(PdfName.THead);
        blockLevelRoles.add(PdfName.TBody);
        blockLevelRoles.add(PdfName.TFoot);

        inlineLevelRoles.add(PdfName.Span);
        inlineLevelRoles.add(PdfName.Quote);
        inlineLevelRoles.add(PdfName.Note);
        inlineLevelRoles.add(PdfName.Reference);
        inlineLevelRoles.add(PdfName.BibEntry);
        inlineLevelRoles.add(PdfName.Code);
        inlineLevelRoles.add(PdfName.Link);
        inlineLevelRoles.add(PdfName.Annot);
        inlineLevelRoles.add(PdfName.Ruby);
        inlineLevelRoles.add(PdfName.Warichu);
        inlineLevelRoles.add(PdfName.RB);
        inlineLevelRoles.add(PdfName.RT);
        inlineLevelRoles.add(PdfName.RP);
        inlineLevelRoles.add(PdfName.WT);
        inlineLevelRoles.add(PdfName.WP);

        illustrationRoles.add(PdfName.Figure);
        illustrationRoles.add(PdfName.Formula);
        illustrationRoles.add(PdfName.Form);
    }

    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    protected int type = Unknown;

    /**
     * @param pdfObject must be an indirect object.
     */
    public PdfStructElem(PdfDictionary pdfObject) {
        super(pdfObject);
        ensureObjectIsAddedToDocument(pdfObject);
        setForbidRelease();
    }

    public PdfStructElem(PdfDocument document, PdfName role, PdfPage page) {
        this(document, role);
        getPdfObject().put(PdfName.Pg, page.getPdfObject());
    }

    public PdfStructElem(PdfDocument document, PdfName role, PdfAnnotation annot) {
        this(document, role);
        if (annot.getPage() == null)
            throw new PdfException(PdfException.AnnotationShallHaveReferenceToPage);
        getPdfObject().put(PdfName.Pg, annot.getPage().getPdfObject());
    }

    public PdfStructElem(PdfDocument document, PdfName role) {
        this(new PdfDictionary().makeIndirect(document));
        getPdfObject().put(PdfName.Type, PdfName.StructElem);
        getPdfObject().put(PdfName.S, role);
    }

    /**
     * Method to to distinguish struct elements from other elements of the logical tree (like mcr or struct tree root).
     */
    public static boolean isStructElem(PdfDictionary dictionary) {
        return (PdfName.StructElem.equals(dictionary.getAsName(PdfName.Type)) ||
                dictionary.containsKey(PdfName.S)); // required key of the struct elem
    }

    /**
     * Gets attributes object.
     *
     * @param createNewIfNull sometimes attributes object may not exist.
     *                        Pass {@code true} if you want to create empty dictionary in such case.
     *                        The attributes dictionary will be stored inside element.
     * @return attributes dictionary.
     */
    public PdfObject getAttributes(boolean createNewIfNull) {
        PdfObject attributes = getPdfObject().get(PdfName.A);
        if (attributes == null && createNewIfNull) {
            attributes = new PdfDictionary();
            setAttributes(attributes);
        }
        return attributes;
    }

    public void setAttributes(PdfObject attributes) {
        put(PdfName.A, attributes);
    }

    public PdfString getLang() {
        return getPdfObject().getAsString(PdfName.Lang);
    }

    public void setLang(PdfString lang) {
        put(PdfName.Lang, lang);
    }

    public PdfString getAlt() {
        return getPdfObject().getAsString(PdfName.Alt);
    }

    public void setAlt(PdfString alt) {
        put(PdfName.Alt, alt);
    }

    public PdfString getActualText() {
        return getPdfObject().getAsString(PdfName.ActualText);
    }

    public void setActualText(PdfString actualText) {
        put(PdfName.ActualText, actualText);
    }

    public PdfString getE() {
        return getPdfObject().getAsString(PdfName.E);
    }

    public void setE(PdfString e) {
        put(PdfName.E, e);
    }

    @Override
    public PdfName getRole() {
        return getPdfObject().getAsName(PdfName.S);
    }

    public void setRole(PdfName role) {
        put(PdfName.S, role);
    }

    public PdfStructElem addKid(PdfStructElem kid) {
        return addKid(-1, kid);
    }

    public PdfStructElem addKid(int index, PdfStructElem kid) {
        addKidObject(getPdfObject(), index, kid.getPdfObject());
        return kid;
    }

    public PdfMcr addKid(PdfMcr kid) {
        return addKid(-1, kid);
    }

    public PdfMcr addKid(int index, PdfMcr kid) {
        getDocument().getStructTreeRoot().getParentTreeHandler().registerMcr(kid);
        addKidObject(getPdfObject(), index, kid.getPdfObject());
        return kid;
    }

    public IPdfStructElem removeKid(int index) {
        PdfObject k = getK();
        if (k == null || !k.isArray() && index != 0) {
            throw new IndexOutOfBoundsException();
        }

        if (k.isArray()) {
            PdfArray kidsArray = (PdfArray) k;
            k = kidsArray.get(index);
            kidsArray.remove(index);
            if (kidsArray.isEmpty()) {
                getPdfObject().remove(PdfName.K);
            }
        } else {
            getPdfObject().remove(PdfName.K);
        }
        setModified();

        IPdfStructElem removedKid = convertPdfObjectToIPdfStructElem(k);
        if (removedKid instanceof PdfMcr) {
            getDocument().getStructTreeRoot().getParentTreeHandler().unregisterMcr((PdfMcr) removedKid);
        }
        return removedKid;
    }

    public int removeKid(IPdfStructElem kid) {
        if (kid instanceof PdfMcr) {
            PdfMcr mcr = (PdfMcr) kid;
            getDocument().getStructTreeRoot().getParentTreeHandler().unregisterMcr(mcr);
            return removeKidObject(mcr.getPdfObject());
        } else if (kid instanceof PdfStructElem) {
            return removeKidObject(((PdfStructElem) kid).getPdfObject());
        }
        return -1;
    }

    /**
     * @return parent of the current structure element. If parent is already flushed it returns null.
     */
    @Override
    public IPdfStructElem getParent() {
        PdfDictionary parent = getPdfObject().getAsDictionary(PdfName.P);
        if (parent == null || parent.isFlushed())
            return null;
        if (isStructElem(parent)) {
            return new PdfStructElem(parent);
        } else {
            PdfName type = parent.getAsName(PdfName.Type);
            if (PdfName.StructTreeRoot.equals(type))
                return getDocument().getStructTreeRoot();
            else
                return null;
        }
    }

    /**
     * Gets list of the direct kids of structure element.
     * If certain kid is flushed, there will be a {@code null} in the list on it's place.
     *
     * @return list of the direct kids of structure element.
     */
    @Override
    public List<IPdfStructElem> getKids() {
        PdfObject k = getK();
        List<IPdfStructElem> kids = new ArrayList<>();
        if (k != null) {
            if (k.isArray()) {
                PdfArray a = (PdfArray) k;
                for (int i = 0; i < a.size(); i++) {
                    addKidObjectToStructElemList(a.get(i), kids);
                }
            } else {
                addKidObjectToStructElemList(k, kids);
            }
        }
        return kids;
    }

    public PdfObject getK() {
        return getPdfObject().get(PdfName.K);
    }

    /**
     * A {@link PdfName#Ref} identifies the structure element or elements to which the item of content, contained
     * within this structure element, refers (e.g. footnotes, endnotes, sidebars, etc.).
     * @return a {@link List<PdfStructElem>} containing zero, one or more structure elements.
     */
    public List<PdfStructElem> getRefsList() {
        PdfArray refsArray = getPdfObject().getAsArray(PdfName.Ref);
        if (refsArray == null) {
            return Collections.<PdfStructElem>emptyList();
        } else {
            List<PdfStructElem> refs = new ArrayList<>(refsArray.size());
            for (int i = 0; i < refsArray.size(); ++i) {
                refs.add(new PdfStructElem(refsArray.getAsDictionary(i)));
            }
            return refs;
        }
    }

    /**
     * A {@link PdfName#Ref} identifies the structure element to which the item of content, contained
     * within this structure element, refers (e.g. footnotes, endnotes, sidebars, etc.).
     * <p>This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.</p>
     * @param ref a {@link PdfStructElem} to which the item of content, contained within this structure element, refers.
     */
    public void addRef(PdfStructElem ref) {
        VersionConforming.ensurePdfVersionForDictEntry(getDocument(), PdfVersion.PDF_2_0, PdfName.Ref, PdfName.StructElem);
        PdfArray refsArray = getPdfObject().getAsArray(PdfName.Ref);
        if (refsArray == null) {
            refsArray = new PdfArray();
            put(PdfName.Ref, refsArray);
        }
        refsArray.add(ref.getPdfObject());
        setModified();
    }

    /**
     * A namespace this element belongs to (see ISO 32000-2 14.7.4, "Namespaces"). If not present, the
     * element shall be considered to be in the default standard structure namespace.
     * @return a {@link PdfNamespace} this element belongs to.
     */
    public PdfNamespace getNamespace() {
        PdfDictionary nsDict = getPdfObject().getAsDictionary(PdfName.NS);
        return nsDict != null ? new PdfNamespace(nsDict) : null;
    }

    /**
     * A namespace this element belongs to (see ISO 32000-2 14.7.4, "Namespaces").
     * <p>This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.</p>
     * @param namespace a {@link PdfNamespace} this element belongs to, or null if element is desired to be considered
     *                  in the default standard structure namespace.
     */
    public void setNamespace(PdfNamespace namespace) {
        VersionConforming.ensurePdfVersionForDictEntry(getDocument(), PdfVersion.PDF_2_0, PdfName.NS, PdfName.StructElem);
        if (namespace != null) {
            put(PdfName.NS, namespace.getPdfObject());
        } else {
            getPdfObject().remove(PdfName.NS);
            setModified();
        }
    }

    /**
     * Attribute for a structure element that may be used as pronunciation hint. It is an exact replacement for content
     * enclosed by the structure element and its children.
     * <p>This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.</p>
     * @param elementPhoneme a {@link PdfString} which defines an exact replacement for content enclosed by the structure
     *                       element and its children. This value is to be interpreted based on the PhoneticAlphabet attribute in effect.
     */
    public void setPhoneme(PdfString elementPhoneme) {
        VersionConforming.ensurePdfVersionForDictEntry(getDocument(), PdfVersion.PDF_2_0, PdfName.Phoneme, PdfName.StructElem);
        put(PdfName.Phoneme, elementPhoneme);
    }

    /**
     * Attribute for a structure element that may be used as pronunciation hint. It is an exact replacement for content
     * enclosed by the structure element and its children.
     * @return a {@link PdfString} which defines an exact replacement for content enclosed by the structure
     * element and its children. This value is to be interpreted based on the PhoneticAlphabet attribute in effect.
     */
    public PdfString getPhoneme() {
        return getPdfObject().getAsString(PdfName.Phoneme);
    }

    /**
     * Attribute for a structure element that indicates the phonetic alphabet used by a  {@link PdfName#Phoneme} attribute.
     * Applies to the structure element and its children, except where overridden by a child structure element.
     * <p>This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.</p>
     * @param phoneticAlphabet the {@link PdfName} which defines phonetic alphabet used by a {@link PdfName#Phoneme}
     *                         attribute. Possible values are:
     *                         <ul>
     *                         <li>{@link PdfName#ipa} for the International Phonetic Alphabet by the International Phonetic Association;</li>
     *                         <li>{@link PdfName#x_sampa} for Extended Speech Assessment Methods Phonetic Alphabet (X-SAMPA);</li>
     *                         <li>{@link PdfName#zh_Latn_pinyin} for Pinyin Latin romanization (Mandarin);</li>
     *                         <li>{@link PdfName#zh_Latn_wadegile} for Wade-Giles romanization (Mandarin).</li>
     *                         </ul>
     *                         Other values may be used.
     */
    public void setPhoneticAlphabet(PdfName phoneticAlphabet) {
        VersionConforming.ensurePdfVersionForDictEntry(getDocument(), PdfVersion.PDF_2_0, PdfName.PhoneticAlphabet, PdfName.StructElem);
        put(PdfName.PhoneticAlphabet, phoneticAlphabet);
    }

    /**
     * Attribute for a structure element that indicates the phonetic alphabet used by a  {@link PdfName#Phoneme} attribute.
     * Applies to the structure element and its children, except where overridden by a child structure element.
     * @return the {@link PdfName} which defines phonetic alphabet used by a {@link PdfName#Phoneme}, or null if not defined,
     * default value {@link PdfName#ipa}. See {@link #setPhoneticAlphabet(PdfName)} for other possible values.
     */
    public PdfName getPhoneticAlphabet() {
        return getPdfObject().getAsName(PdfName.PhoneticAlphabet);
    }

    /**
     * @deprecated shall be removed in iText 7.1. Since PDF 2.0, standard role types are not strictly defined based on
     * element's role, but are rather dependent on the role usage, it kids and position in the tree. Moreover, role types
     * might be different for the different standard structure namespaces.
     */
    @Deprecated
    public static int identifyType(PdfDocument doc, PdfName role) {
        PdfDictionary roleMap = doc.getStructTreeRoot().getRoleMap();
        if (roleMap.containsKey(role))
            role = roleMap.getAsName(role);
        if (groupingRoles.contains(role))
            return Grouping;
        else if (blockLevelRoles.contains(role))
            return BlockLevel;
        else if (inlineLevelRoles.contains(role))
            return InlineLevel;
        else if (illustrationRoles.contains(role))
            return Illustration;
        else
            return Unknown;
    }

    public PdfStructElem put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }

    @Override
    public void flush() {
        getDocument().checkIsoConformance(getPdfObject(), IsoKey.TAG_STRUCTURE_ELEMENT);
        super.flush();
    }


    /**
     * @deprecated See {@link PdfStructElem#identifyType(PdfDocument, PdfName)}.
     */
    @Deprecated
    protected int getType() {
        if (type == Unknown) {
            PdfName role = getPdfObject().getAsName(PdfName.S);
            type = identifyType(getDocument(), role);
        }
        return type;
    }

    static void addKidObject(PdfDictionary parent, int index, PdfObject kid) {
        if (parent.isFlushed()) {
            throw new PdfException(PdfException.CannotAddKidToTheFlushedElement);
        }
        if (!parent.containsKey(PdfName.P)) {
            throw new PdfException(PdfException.StructureElementShallContainParentObject, parent);
        }
        PdfObject k = parent.get(PdfName.K);
        if (k == null) {
            parent.put(PdfName.K, kid);
        } else {
            PdfArray a;
            if (k instanceof PdfArray) {
                a = (PdfArray) k;
            } else {
                a = new PdfArray();
                a.add(k);
                parent.put(PdfName.K, a);
            }
            if (index == -1) {
                a.add(kid);
            } else {
                a.add(index, kid);
            }
        }
        parent.setModified();
        if (kid instanceof PdfDictionary && isStructElem((PdfDictionary) kid)) {
            ((PdfDictionary) kid).put(PdfName.P, parent);
            kid.setModified();
        }
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    protected PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }

    private void addKidObjectToStructElemList(PdfObject k, List<IPdfStructElem> list) {
        if (k.isFlushed()) {
            list.add(null);
            return;
        }

        list.add(convertPdfObjectToIPdfStructElem(k));
    }

    private IPdfStructElem convertPdfObjectToIPdfStructElem(PdfObject obj) {
        IPdfStructElem elem = null;
        switch (obj.getType()) {
            case PdfObject.DICTIONARY:
                PdfDictionary d = (PdfDictionary) obj;
                if (isStructElem(d))
                    elem = new PdfStructElem(d);
                else if (PdfName.MCR.equals(d.getAsName(PdfName.Type)))
                    elem = new PdfMcrDictionary(d, this);
                else if (PdfName.OBJR.equals(d.getAsName(PdfName.Type)))
                    elem = new PdfObjRef(d, this);
                break;
            case PdfObject.NUMBER:
                elem = new PdfMcrNumber((PdfNumber) obj, this);
                break;
            default:
                break;
        }

        return elem;
    }

    private int removeKidObject(PdfObject kid) {
        PdfObject k = getK();
        if (k == null || !k.isArray()
                && k != kid && k != kid.getIndirectReference()) {
            return -1;
        }

        int removedIndex = -1;
        if (k.isArray()) {
            PdfArray kidsArray = (PdfArray) k;
            removedIndex = removeObjectFromArray(kidsArray, kid);
            if (kidsArray.isEmpty()) {
                getPdfObject().remove(PdfName.K);
            }
        }
        if (!k.isArray() || k.isArray() && ((PdfArray) k).isEmpty()) {
            getPdfObject().remove(PdfName.K);
            removedIndex = 0;
        }
        setModified();

        return removedIndex;
    }

    static int removeObjectFromArray(PdfArray array, PdfObject toRemove) {
        int i;
        for (i = 0; i < array.size(); ++i) {
            PdfObject obj = array.get(i);
            if (obj == toRemove || obj == toRemove.getIndirectReference()) {
                array.remove(i);
                break;
            }
        }
        return i;
    }
}
