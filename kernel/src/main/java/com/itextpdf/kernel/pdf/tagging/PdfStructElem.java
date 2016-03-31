package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.PdfException;
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
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfStructElem extends PdfObjectWrapper<PdfDictionary> implements IPdfStructElem {

    private static final long serialVersionUID = 7204356181229674005L;
	
    static public int Unknown = 0;
    static public int Grouping = 1;
    static public int BlockLevel = 2;
    static public int InlineLevel = 3;
    static public int Illustration = 4;

    static public Set<PdfName> groupingRoles = new HashSet<PdfName>() {{
        add(PdfName.Document);
        add(PdfName.Part);
        add(PdfName.Art);
        add(PdfName.Sect);
        add(PdfName.Div);
        add(PdfName.BlockQuote);
        add(PdfName.Caption);
        add(PdfName.Caption);
        add(PdfName.TOC);
        add(PdfName.TOCI);
        add(PdfName.Index);
        add(PdfName.NonStruct);
        add(PdfName.Private);
    }};

    static public Set<PdfName> blockLevelRoles = new HashSet<PdfName>() {{
        add(PdfName.P);
        add(PdfName.H1);
        add(PdfName.H2);
        add(PdfName.H3);
        add(PdfName.H4);
        add(PdfName.H5);
        add(PdfName.H6);
        add(PdfName.L);
        add(PdfName.Lbl);
        add(PdfName.LI);
        add(PdfName.LBody);
        add(PdfName.Table);

        add(PdfName.TR);
        add(PdfName.TH);
        add(PdfName.TD);
        add(PdfName.THead);
        add(PdfName.TBody);
        add(PdfName.TFoot);
    }};

    static public Set<PdfName> inlineLevelRoles = new HashSet<PdfName>() {{
        add(PdfName.Span);
        add(PdfName.Quote);
        add(PdfName.Note);
        add(PdfName.Reference);
        add(PdfName.BibEntry);
        add(PdfName.Code);
        add(PdfName.Link);
        add(PdfName.Annot);
        add(PdfName.Ruby);
        add(PdfName.Warichu);
        add(PdfName.RB);
        add(PdfName.RT);
        add(PdfName.RP);
        add(PdfName.WT);
        add(PdfName.WP);
    }};

    static public Set<PdfName> illustrationRoles = new HashSet<PdfName>() {{
        add(PdfName.Figure);
        add(PdfName.Formula);
        add(PdfName.Form);
    }};

    protected int type = Unknown;

    /**
     * @param pdfObject must be an indirect object.
     */
    public PdfStructElem(PdfDictionary pdfObject) {
        super(pdfObject);
        ensureObjectIsAddedToDocument(pdfObject);
        setForbidRelease();
        PdfName role = getPdfObject().getAsName(PdfName.S);
        type = identifyType(getDocument(), role);
    }

    public PdfStructElem(PdfDocument document, PdfName role, PdfPage page) {
        this(document, role);
        getPdfObject().put(PdfName.Pg, page.getPdfObject());
    }

    public PdfStructElem(PdfDocument document, PdfName role, PdfAnnotation annot) {
        this(document, role);
        if (annot.getPage() == null)
            throw new PdfException(PdfException.AnnotShallHaveReferenceToPage);
        getPdfObject().put(PdfName.Pg, annot.getPage().getPdfObject());
    }

    public PdfStructElem(PdfDocument document, final PdfName role) {
        this(new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
            put(PdfName.Type, PdfName.StructElem);
            put(PdfName.S, role);
        }}).makeIndirect(document));
    }

    /**
     * Method to to distinguish struct elements from other elements of the logical tree (like mcr or struct tree root).
     */
    static public boolean isStructElem(PdfDictionary dictionary) {
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
     * @throws PdfException
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
        getPdfObject().put(PdfName.A, attributes);
    }

    public PdfString getLang() {
        return getPdfObject().getAsString(PdfName.Lang);
    }

    public void setLang(PdfString lang) {
        getPdfObject().put(PdfName.Lang, lang);
    }

    public PdfString getAlt() {
        return getPdfObject().getAsString(PdfName.Alt);
    }

    public void setAlt(PdfString alt) {
        getPdfObject().put(PdfName.Alt, alt);
    }

    public PdfString getActualText() {
        return getPdfObject().getAsString(PdfName.ActualText);
    }

    public void setActualText(PdfString actualText) {
        getPdfObject().put(PdfName.ActualText, actualText);
    }

    public PdfString getE() {
        return getPdfObject().getAsString(PdfName.E);
    }

    public void setE(PdfString e) {
        getPdfObject().put(PdfName.E, e);
    }

    @Override
    public PdfName getRole() {
        return getPdfObject().getAsName(PdfName.S);
    }

    public void setRole(PdfName role) {
        getPdfObject().put(PdfName.S, role);
    }

    public PdfStructElem addKid(PdfStructElem kid) {
        return addKid(-1, kid);
    }

    public PdfStructElem addKid(int index, PdfStructElem kid) {
        if (type == InlineLevel || type == Illustration) {
            throw new PdfException(PdfException.InlineLevelOrIllustrationElementCannotContainKids, getPdfObject());
        }
        addKidObject(index, kid.getPdfObject());
        return kid;
    }

    public PdfMcr addKid(PdfMcr kid) {
        return addKid(-1, kid);
    }

    public PdfMcr addKid(int index, PdfMcr kid) {
        getDocument().getStructTreeRoot().registerMcr(kid);
        addKidObject(index, kid.getPdfObject());
        return kid;
    }

    public IPdfStructElem removeKid(int index) {
        PdfObject k = getK();
        if (k == null || !k.isArray() && index != 0) {
            throw new IndexOutOfBoundsException();
        }

        if (k.isArray()) {
            PdfArray kidsArray = (PdfArray) k;
            k = kidsArray.remove(index);
            if (kidsArray.isEmpty()) {
                remove(PdfName.K);
            }
        } else {
            remove(PdfName.K);
        }

        return convertPdfObjectToIPdfStructElem(k);
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

    @Override
    public void flush() {
        //TODO log that to prevent undefined behaviour, use StructTreeRoot#flushStructElem method
        super.flush();
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

    private void addKidObject(int index, PdfObject kid) {
        PdfDictionary pdfObject = getPdfObject();
        if (pdfObject.isFlushed()) {
            throw new PdfException(PdfException.CannotAddKidToTheFlushedElement);
        }
        if (!pdfObject.containsKey(PdfName.P)) {
            throw new PdfException(PdfException.StructureElementShallContainParentObject, pdfObject);
        }
        PdfObject k = getK();
        if (k == null)
            pdfObject.put(PdfName.K, kid);
        else {
            PdfArray a;
            if (k instanceof PdfArray) {
                a = (PdfArray) k;
            } else {
                a = new PdfArray();
                a.add(k);
                pdfObject.put(PdfName.K, a);
            }
            if (index == -1)
                a.add(kid);
            else
                a.add(index, kid);
        }
        if (kid instanceof PdfDictionary && isStructElem((PdfDictionary) kid))
            ((PdfDictionary) kid).put(PdfName.P, pdfObject);
    }

    private IPdfStructElem convertPdfObjectToIPdfStructElem(PdfObject obj) {
        if (obj.isIndirectReference()) {
            obj = ((PdfIndirectReference)obj).getRefersTo();
        }

        IPdfStructElem elem = null;
        switch (obj.getType()) {
            case PdfObject.Dictionary:
                PdfDictionary d = (PdfDictionary) obj;
                if (isStructElem(d))
                    elem = new PdfStructElem(d);
                else if (PdfName.MCR.equals(d.getAsName(PdfName.Type)))
                    elem = new PdfMcrDictionary(d, this);
                else if (PdfName.OBJR.equals(d.getAsName(PdfName.Type)))
                    elem = new PdfObjRef(d, this);
                break;
            case PdfObject.Number:
                elem = new PdfMcrNumber((PdfNumber) obj, this);
                break;
            default:
                break;
        }

        return elem;
    }
}
