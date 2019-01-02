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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.IsoKey;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A wrapper for structure element dictionaries (ISO-32000 14.7.2 "Structure Hierarchy").
 * <p>
 * The logical structure of a document shall be described by a hierarchy of objects called
 * the structure hierarchy or structure tree. At the root of the hierarchy shall be a dictionary object
 * called the structure tree root (see {@link PdfStructTreeRoot}). Immediate children of the structure tree root
 * are structure elements. Structure elements are other structure elements or content items.
 */
public class PdfStructElem extends PdfObjectWrapper<PdfDictionary> implements IStructureNode {

    private static final long serialVersionUID = 7204356181229674005L;

    public PdfStructElem(PdfDictionary pdfObject) {
        super(pdfObject);
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
        this((PdfDictionary) new PdfDictionary().makeIndirect(document));
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
        getDocEnsureIndirectForKids().getStructTreeRoot().getParentTreeHandler().registerMcr(kid);
        addKidObject(getPdfObject(), index, kid.getPdfObject());
        return kid;
    }

    public IStructureNode removeKid(int index) {
        return removeKid(index, false);
    }

    public IStructureNode removeKid(int index, boolean prepareForReAdding) {
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

        IStructureNode removedKid = convertPdfObjectToIPdfStructElem(k);
        PdfDocument doc = getDocument();
        if (removedKid instanceof PdfMcr && doc != null && !prepareForReAdding) {
            doc.getStructTreeRoot().getParentTreeHandler().unregisterMcr((PdfMcr) removedKid);
        }
        return removedKid;
    }

    public int removeKid(IStructureNode kid) {
        if (kid instanceof PdfMcr) {
            PdfMcr mcr = (PdfMcr) kid;
            PdfDocument doc = getDocument();
            if (doc != null) {
                doc.getStructTreeRoot().getParentTreeHandler().unregisterMcr(mcr);
            }
            return removeKidObject(mcr.getPdfObject());
        } else if (kid instanceof PdfStructElem) {
            return removeKidObject(((PdfStructElem) kid).getPdfObject());
        }
        return -1;
    }

    /**
     * @return parent of the current structure element. Returns null if parent isn't set or if either current element or parent are invalid.
     */
    @Override
    public IStructureNode getParent() {
        PdfDictionary parent = getPdfObject().getAsDictionary(PdfName.P);
        if (parent == null) {
            return null;
        }

        if (parent.isFlushed()) {
            PdfDocument pdfDoc = getDocument();
            if (pdfDoc == null) {
                return null;
            }
            PdfStructTreeRoot structTreeRoot = pdfDoc.getStructTreeRoot();
            return structTreeRoot.getPdfObject() == parent ? (IStructureNode) structTreeRoot : new PdfStructElem(parent);
        }

        if (isStructElem(parent)) {
            return new PdfStructElem(parent);
        } else {
            PdfDocument pdfDoc = getDocument();
            boolean parentIsRoot = pdfDoc != null && PdfName.StructTreeRoot.equals(parent.getAsName(PdfName.Type));
            parentIsRoot = parentIsRoot || pdfDoc != null && pdfDoc.getStructTreeRoot().getPdfObject() == parent;
            if (parentIsRoot) {
                return pdfDoc.getStructTreeRoot();
            } else {
                return null;
            }
        }
    }

    /**
     * Gets list of the direct kids of structure element.
     * If certain kid is flushed, there will be a {@code null} in the list on it's place.
     *
     * @return list of the direct kids of structure element.
     */
    @Override
    public List<IStructureNode> getKids() {
        PdfObject k = getK();
        List<IStructureNode> kids = new ArrayList<>();
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
     *
     * @return a {@link List<  PdfStructElem  >} containing zero, one or more structure elements.
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
     *
     * @param ref a {@link PdfStructElem} to which the item of content, contained within this structure element, refers.
     */
    public void addRef(PdfStructElem ref) {
        if (!ref.getPdfObject().isIndirect()) {
            throw new PdfException(PdfException.RefArrayItemsInStructureElementDictionaryShallBeIndirectObjects);
        }
        VersionConforming.validatePdfVersionForDictEntry(getDocument(), PdfVersion.PDF_2_0, PdfName.Ref, PdfName.StructElem);
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
     *
     * @return a {@link PdfNamespace} this element belongs to.
     */
    public PdfNamespace getNamespace() {
        PdfDictionary nsDict = getPdfObject().getAsDictionary(PdfName.NS);
        return nsDict != null ? new PdfNamespace(nsDict) : null;
    }

    /**
     * A namespace this element belongs to (see ISO 32000-2 14.7.4, "Namespaces").
     * <p>This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.</p>
     *
     * @param namespace a {@link PdfNamespace} this element belongs to, or null if element is desired to be considered
     *                  in the default standard structure namespace.
     */
    public void setNamespace(PdfNamespace namespace) {
        VersionConforming.validatePdfVersionForDictEntry(getDocument(), PdfVersion.PDF_2_0, PdfName.NS, PdfName.StructElem);
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
     *
     * @param elementPhoneme a {@link PdfString} which defines an exact replacement for content enclosed by the structure
     *                       element and its children. This value is to be interpreted based on the PhoneticAlphabet attribute in effect.
     */
    public void setPhoneme(PdfString elementPhoneme) {
        VersionConforming.validatePdfVersionForDictEntry(getDocument(), PdfVersion.PDF_2_0, PdfName.Phoneme, PdfName.StructElem);
        put(PdfName.Phoneme, elementPhoneme);
    }

    /**
     * Attribute for a structure element that may be used as pronunciation hint. It is an exact replacement for content
     * enclosed by the structure element and its children.
     *
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
     *
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
        VersionConforming.validatePdfVersionForDictEntry(getDocument(), PdfVersion.PDF_2_0, PdfName.PhoneticAlphabet, PdfName.StructElem);
        put(PdfName.PhoneticAlphabet, phoneticAlphabet);
    }

    /**
     * Attribute for a structure element that indicates the phonetic alphabet used by a  {@link PdfName#Phoneme} attribute.
     * Applies to the structure element and its children, except where overridden by a child structure element.
     *
     * @return the {@link PdfName} which defines phonetic alphabet used by a {@link PdfName#Phoneme}, or null if not defined,
     * default value {@link PdfName#ipa}. See {@link #setPhoneticAlphabet(PdfName)} for other possible values.
     */
    public PdfName getPhoneticAlphabet() {
        return getPdfObject().getAsName(PdfName.PhoneticAlphabet);
    }

    /**
     * <p>
     * Adds file associated with structure element and identifies the relationship between them.
     * </p>
     * <p>
     * Associated files may be used in Pdf/A-3 and Pdf 2.0 documents.
     * The method adds file to array value of the AF key in the structure element dictionary.
     * If description is provided, it also will add file description to catalog Names tree.
     * </p>
     * <p>
     * For associated files their associated file specification dictionaries shall include the AFRelationship key
     * </p>
     *
     * @param description the file description
     * @param fs          file specification dictionary of associated file
     */
    public void addAssociatedFile(String description, PdfFileSpec fs) {
        if (null == ((PdfDictionary) fs.getPdfObject()).get(PdfName.AFRelationship)) {
            Logger logger = LoggerFactory.getLogger(PdfStructElem.class);
            logger.error(LogMessageConstant.ASSOCIATED_FILE_SPEC_SHALL_INCLUDE_AFRELATIONSHIP);
        }
        if (null != description) {
            getDocument().getCatalog().getNameTree(PdfName.EmbeddedFiles).addEntry(description, fs.getPdfObject());
        }
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null) {
            afArray = new PdfArray();
            put(PdfName.AF, afArray);
        }
        afArray.add(fs.getPdfObject());
    }

    /**
     * <p>
     * Adds file associated with structure element and identifies the relationship between them.
     * </p>
     * <p>
     * Associated files may be used in Pdf/A-3 and Pdf 2.0 documents.
     * The method adds file to array value of the AF key in the structure element dictionary.
     * </p>
     * <p>
     * For associated files their associated file specification dictionaries shall include the AFRelationship key
     * </p>
     *
     * @param fs file specification dictionary of associated file
     */
    public void addAssociatedFile(PdfFileSpec fs) {
        addAssociatedFile(null, fs);
    }

    /**
     * Returns files associated with structure element.
     *
     * @param create iText will create AF array if it doesn't exist and create value is true
     * @return associated files array.
     */
    public PdfArray getAssociatedFiles(boolean create) {
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null && create) {
            afArray = new PdfArray();
            put(PdfName.AF, afArray);
        }
        return afArray;
    }

    public PdfStructElem put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }

    @Override
    public void flush() {
        PdfDictionary pageDict = getPdfObject().getAsDictionary(PdfName.Pg);
        if (pageDict == null || pageDict.getIndirectReference() != null && pageDict.getIndirectReference().isFree()) {
            getPdfObject().remove(PdfName.Pg);
        }

        PdfDocument doc = getDocument();
        if (doc != null) {
            doc.checkIsoConformance(getPdfObject(), IsoKey.TAG_STRUCTURE_ELEMENT);
        }
        super.flush();
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
            if (!parent.isIndirect()) {
                throw new PdfException(PdfException.StructureElementDictionaryShallBeAnIndirectObjectInOrderToHaveChildren);
            }
            ((PdfDictionary) kid).put(PdfName.P, parent);
            kid.setModified();
        }
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    protected PdfDocument getDocument() {
        PdfDictionary structDict = getPdfObject();
        PdfIndirectReference indRef = structDict.getIndirectReference();
        if (indRef == null && structDict.getAsDictionary(PdfName.P) != null) {
            // If parent is direct - it's definitely an invalid structure tree.
            // MustBeIndirect state won't be met during reading, and all newly created struct elements shall have ind ref.
            indRef = structDict.getAsDictionary(PdfName.P).getIndirectReference();
        }
        return indRef != null ? indRef.getDocument() : null;
    }

    private PdfDocument getDocEnsureIndirectForKids() {
        PdfDocument doc = getDocument();
        if (doc == null) {
            throw new PdfException(PdfException.StructureElementDictionaryShallBeAnIndirectObjectInOrderToHaveChildren);
        }
        return doc;
    }

    private void addKidObjectToStructElemList(PdfObject k, List<IStructureNode> list) {
        if (k.isFlushed()) {
            list.add(null);
            return;
        }

        list.add(convertPdfObjectToIPdfStructElem(k));
    }

    private IStructureNode convertPdfObjectToIPdfStructElem(PdfObject obj) {
        IStructureNode elem = null;
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
        }
        if (!k.isArray() || k.isArray() && ((PdfArray) k).isEmpty()) {
            getPdfObject().remove(PdfName.K);
            removedIndex = 0;
        }
        setModified();

        return removedIndex;
    }

    private static int removeObjectFromArray(PdfArray array, PdfObject toRemove) {
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
