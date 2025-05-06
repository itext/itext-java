/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.helper.Validate;

import java.util.List;

public abstract class LeafNode extends Node {
    Object value; // either a string value, or an attribute map (in the rare case multiple attributes are set)

    protected final boolean hasAttributes() {
        return value instanceof Attributes;
    }

    @Override
    public final Attributes attributes() {
        ensureAttributes();
        return (Attributes) value;
    }

    private void ensureAttributes() {
        if (!hasAttributes()) {
            Object coreValue = value;
            Attributes attributes = new Attributes();
            value = attributes;
            if (coreValue != null)
                attributes.put(nodeName(), (String) coreValue);
        }
    }

    String coreValue() {
        return attr(nodeName());
    }

    void coreValue(String value) {
        attr(nodeName(), value);
    }

    @Override
    public String attr(String key) {
        Validate.notNull(key);
        if (!hasAttributes()) {
            return key.equals(nodeName()) ? (String) value : EmptyString;
        }
        return super.attr(key);
    }

    @Override
    public Node attr(String key, String value) {
        if (!hasAttributes() && key.equals(nodeName())) {
            this.value = value;
        } else {
            ensureAttributes();
            super.attr(key, value);
        }
        return this;
    }

    @Override
    public boolean hasAttr(String key) {
        ensureAttributes();
        return super.hasAttr(key);
    }

    @Override
    public Node removeAttr(String key) {
        ensureAttributes();
        return super.removeAttr(key);
    }

    @Override
    public String absUrl(String key) {
        ensureAttributes();
        return super.absUrl(key);
    }

    @Override
    public String baseUri() {
        return hasParent() ? parent().baseUri() : "";
    }

    @Override
    protected void doSetBaseUri(String baseUri) {
        // noop
    }

    @Override
    public int childNodeSize() {
        return 0;
    }

    @Override
    public Node empty() {
        return this;
    }

    @Override
    protected List<Node> ensureChildNodes() {
        return EmptyNodes;
    }

    @Override
    protected Node doClone(Node parent) {
        LeafNode clone = (LeafNode) super.doClone(parent);

        // Object value could be plain string or attributes - need to clone
        if (hasAttributes())
            clone.value = ((Attributes) value).clone();

        return clone;
    }
}
