package com.itextpdf.model.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagutils.AccessibleElementProperties;
import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.ListRenderer;

public class List extends BlockElement<List> {

    public static final String DEFAULT_LIST_SYMBOL = "- ";

    protected String preSymbolText = "";
    protected String postSymbolText = ". ";

    protected PdfName role = PdfName.L;
    protected AccessibleElementProperties tagProperties;

    public List() {
        super();
        setListSymbol(DEFAULT_LIST_SYMBOL);
    }

    public List(Property.ListNumberingType listNumberingType) {
        super();
        setListSymbol(listNumberingType);
    }

    public List add(ListItem listItem) {
        childElements.add(listItem);
        return this;
    }

    public List add(String text) {
        return add(new ListItem(text));
    }

    public List setItemStartIndex(int start) {
        return setProperty(Property.LIST_START, start);
    }

    public List setListSymbol(String symbol) {
        return setListSymbol(new Text(symbol));
    }

    public List setListSymbol(Text text) {
        return setProperty(Property.LIST_SYMBOL, text);
    }

    public List setListSymbol(Image image) {
        return setProperty(Property.LIST_SYMBOL, image);
    }

    public List setListSymbol(Property.ListNumberingType listNumberingType) {
        // Do not draw any points after ZapfDingbats special number symbol
        if (listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_1 || listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_2 ||
                listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_3 || listNumberingType == Property.ListNumberingType.ZAPF_DINGBATS_4) {
            postSymbolText = " ";
        }
        return setProperty(Property.LIST_SYMBOL, listNumberingType);
    }

    public Float getSymbolIndent() {
        return getProperty(Property.LIST_SYMBOL_INDENT);
    }

    public List setSymbolIndent(float symbolIndent) {
        return setProperty(Property.LIST_SYMBOL_INDENT, symbolIndent);
    }

    public String getPostSymbolText() {
        return postSymbolText;
    }

    public void setPostSymbolText(String postSymbolText) {
        this.postSymbolText = postSymbolText;
    }

    public String getPreSymbolText() {
        return preSymbolText;
    }

    public void setPreSymbolText(String preSymbolText) {
        this.preSymbolText = preSymbolText;
    }

    @Override
    public PdfName getRole() {
        return role;
    }

    @Override
    public void setRole(PdfName role) {
        this.role = role;
        if (PdfName.Artifact.equals(role)) {
            propagateArtifactRoleToChildElements();
        }
    }

    @Override
    public AccessibleElementProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new AccessibleElementProperties();
        }
        return tagProperties;
    }

    @Override
    protected ListRenderer makeNewRenderer() {
        return new ListRenderer(this);
    }
}
