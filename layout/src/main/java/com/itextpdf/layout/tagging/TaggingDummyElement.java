package com.itextpdf.layout.tagging;

import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.property.Property;

public class TaggingDummyElement implements IAccessibleElement, IPropertyContainer {
    private DefaultAccessibilityProperties properties;

    private Object id;

    public TaggingDummyElement(String role) {
        this.properties = new DefaultAccessibilityProperties(role);
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        return properties;
    }

    @Override
    public <T1> T1 getProperty(int property) {
        if (property == Property.TAGGING_HINT_KEY) {
            return (T1) id;
        }
        return (T1) (Object) null;
    }

    @Override
    public void setProperty(int property, Object value) {
        if (property == Property.TAGGING_HINT_KEY) {
            this.id = value;
        }
    }

    @Override
    public boolean hasProperty(int property) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasOwnProperty(int property) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T1> T1 getOwnProperty(int property) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteOwnProperty(int property) {
        throw new UnsupportedOperationException();
    }
}
