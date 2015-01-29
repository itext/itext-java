package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

import java.util.*;

public class PdfStructElem extends PdfObjectWrapper<PdfDictionary> implements IPdfTag, IPdfStructElem {

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
    protected Integer structParentIndex = null;

    public PdfStructElem(PdfDictionary pdfObject) throws PdfException {
        this(pdfObject, null);
    }

    public PdfStructElem(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
        PdfName role = getPdfObject().getAsName(PdfName.S);
        type = getType(role);
    }

    public PdfStructElem(PdfDocument document, PdfName role) throws PdfException {
        this(document, role, -1);
    }

    public PdfStructElem(PdfDocument document, PdfName role, PdfPage page) throws PdfException {
        this(document, role, page.getNextMcid());
        getPdfObject().put(PdfName.Pg, page.getPdfObject());
        PdfNumber structParents = page.getPdfObject().getAsNumber(PdfName.StructParents);
        if (structParents != null)
            structParentIndex = structParents.getIntValue();
    }

    private PdfStructElem(PdfDocument document, final PdfName role, final int mcid) throws PdfException {
        this(new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
            put(PdfName.Type, PdfName.StructElem);
            put(PdfName.S, role);
        }}), document);
        if (mcid >= 0)
            getPdfObject().put(PdfName.K, new PdfNumber(mcid));
    }

    static public boolean isStructElem(PdfDictionary dictionary) throws PdfException {
        return (PdfName.StructElem.equals(dictionary.getAsName(PdfName.Type)) ||
                (dictionary.containsKey(PdfName.K) && dictionary.containsKey(PdfName.S)));
    }

    /**
     * Gets attributes dictionary.
     *
     * @param createNewIfNull sometimes attributes dictionary may not exist.
     *                        Pass {@code true} if you want to create empty dictionary in such case.
     *                        The attributes dictionary wil be stored inside element.
     * @return attributes dictionary.
     * @throws PdfException
     */
    public PdfDictionary getAttributes(boolean createNewIfNull) throws PdfException {
        PdfDictionary attributes = getPdfObject().getAsDictionary(PdfName.A);
        if (attributes == null && createNewIfNull) {
            attributes = new PdfDictionary();
            setAttributes(attributes);
        }
        return attributes;
    }

    public void setAttributes(PdfDictionary attributes) {
        getPdfObject().put(PdfName.A, attributes);
    }

    public PdfString getLang() throws PdfException {
        return getPdfObject().getAsString(PdfName.Lang);
    }

    public void setLang(PdfString lang) {
        getPdfObject().put(PdfName.Lang, lang);
    }

    public PdfString getAlt() throws PdfException {
        return getPdfObject().getAsString(PdfName.Alt);
    }

    public void setAlt(PdfString alt) {
        getPdfObject().put(PdfName.Alt, alt);
    }

    public PdfString getActualText() throws PdfException {
        return getPdfObject().getAsString(PdfName.ActualText);
    }

    public void setActualText(PdfString actualText) {
        getPdfObject().put(PdfName.ActualText, actualText);
    }

    public PdfString getE() throws PdfException {
        return getPdfObject().getAsString(PdfName.E);
    }

    public void setE(PdfString e) {
        getPdfObject().put(PdfName.E, e);
    }

    @Override
    public PdfName getRole() throws PdfException {
        return getPdfObject().getAsName(PdfName.S);
    }

    public void addKid(PdfStructElem kid) throws PdfException {
        if (type == InlineLevel || type == Illustration) {
            throw new PdfException(PdfException.InlineLevelOrIllustrationElementCannotContainKids, getPdfObject());
        }
        addKidObject(kid.getPdfObject());
        if (kid.structParentIndex != null) {
            getDocument().getStructTreeRoot().getNumsBranch(kid.structParentIndex).add(kid.getPdfObject());
        }
    }

    public void addKid(PdfMcr kid) throws PdfException {
        addKidObject(kid.getPdfObject());
        if (structParentIndex != null) {
            getDocument().getStructTreeRoot().getNumsBranch(structParentIndex).add(getPdfObject());
        }
    }

    @Override
    public IPdfStructElem getParent() throws PdfException {
        PdfDictionary parent = getPdfObject().getAsDictionary(PdfName.P);
        if (parent == null)
            return null;
        if (isStructElem(parent)) {
            return new PdfStructElem(parent, getDocument());
        } else {
            PdfName type = parent.getAsName(PdfName.Type);
            if (PdfName.StructTreeRoot.equals(type))
                return getDocument().getStructTreeRoot();
            else
                return null;
        }
    }

    @Override
    public List<IPdfStructElem> getKids() throws PdfException {
        final PdfObject k = getK();
        if (k instanceof PdfDictionary && isStructElem((PdfDictionary) k)) {
            return new ArrayList<IPdfStructElem>() {{
                add(new PdfStructElem((PdfDictionary) k, getDocument()));
            }};
        } else if (k instanceof PdfArray) {
            List<IPdfStructElem> kids = new ArrayList<IPdfStructElem>();
            PdfArray kArr = (PdfArray) k;
            for (int i = 0; i < kArr.size(); i++) {
                PdfDictionary d = kArr.getAsDictionary(i);
                if (d != null) {
                    if (isStructElem(d)) {
                        kids.add(new PdfStructElem(d, getDocument()));
                    } else {
                        PdfName type = d.getAsName(PdfName.Type);
                        if (PdfName.MCR.equals(type)) {
                            kids.add(new PdfMcr(d, this));
                        }
                    }
                }
            }
            return kids;
        }
        return null;
    }

    public PdfObject getK() throws PdfException {
        return getPdfObject().get(PdfName.K);
    }

    public PdfArray getKidsObject() throws PdfException {
        PdfObject k = getK();
        if (k instanceof PdfArray)
            return (PdfArray) k;
        else if (k instanceof PdfDictionary) {
            PdfArray a = new PdfArray();
            a.add(k);
            getPdfObject().put(PdfName.K, a);
            return a;
        } else
            return null;
    }

    public PdfDictionary getParentObject() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.P);
    }

    public void setParentObject(PdfDictionary parent) throws PdfException {
        if (PdfName.MCR.equals(getPdfObject().getAsName(PdfName.Type)))
            return;
        // Remove current tag from previous parent element.
        PdfDictionary oldParent = getParentObject();
        if (oldParent != null) {
            PdfArray oldChildren = oldParent.getAsArray(PdfName.K);
            if (oldChildren != null)
                oldChildren.remove(getPdfObject());
        }
        getPdfObject().put(PdfName.P, parent);
    }

    @Override
    public Integer getMcid() throws PdfException {
        PdfObject k = getK();
        if (k instanceof PdfNumber)
            return ((PdfNumber) k).getIntValue();
        else
            return null;
    }

    public Integer getStructParentIndex() throws PdfException {
        return structParentIndex;
    }

    private int getType(PdfName role) throws PdfException {
        PdfDictionary roleMap = getDocument().getStructTreeRoot().getRoleMap();
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

    private void addKidObject(PdfDictionary kid) throws PdfException {
        PdfObject k = getK();
        PdfArray a = null;
        if (k instanceof PdfNumber) {
            a = new PdfArray();
            if (PdfName.MCR.equals(kid.getAsName(PdfName.Type))) {
                a.add(k);
            }
            getPdfObject().put(PdfName.K, a);
        } else if (k instanceof PdfArray) {
            a = (PdfArray) k;
        } else {
            a = new PdfArray();
            getPdfObject().put(PdfName.K, a);
        }
        a.add(kid);
        if (isStructElem(kid))
            kid.put(PdfName.P, getPdfObject());
    }

}
