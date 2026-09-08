package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.ParagraphOrphansControl;
import com.itextpdf.layout.properties.ParagraphWidowsControl;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * An abstract class that represents a paragraph of text in a document. It provides methods for adding text
 * and other elements to the paragraph, as well as managing properties such as leading, indentation, and margins.
 * @param <T> the type of the concrete subclass extending this abstract class
 */
public abstract class AbstractParagraph<T extends AbstractParagraph<T>> extends BlockElement<T> {
    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Creates a new instance of {@link AbstractParagraph}. This constructor is protected to allow subclassing.
     */
    protected AbstractParagraph() {
        super();
    }

    /**
     * Adds a piece of text to this {@link Paragraph}.
     *
     * @param text the content to be added, as a {@link String}
     *
     * @return this Element
     */
    public T add(String text) {
        return add(new Text(text));
    }

    /**
     * Adds a {@link ILeafElement element} to this {@link Paragraph}.
     *
     * @param element the content to be added, any {@link ILeafElement}
     *
     * @return this Element
     */
    public T add(ILeafElement element) {
        childElements.add(element);
        return (T)this;
    }

    /**
     * Adds a {@link Paragraph element}.
     *
     * @param element the content to be added, any {@link Paragraph}
     *
     * @return this Element
     */
    public T add(Paragraph element) {
        childElements.add(element);
        return (T)this;
    }

    /**
     * Sets the indent value for the first line of the {@link Paragraph}.
     *
     * @param indent the indent value that must be applied to the first line of
     * the Paragraph, as a <code>float</code>
     *
     * @return this Element
     */
    public T setFirstLineIndent(float indent) {
        setProperty(Property.FIRST_LINE_INDENT, indent);
        return (T)this;
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.LEADING:
                return (T1) (Object) new Leading(Leading.MULTIPLIED,
                        childElements.size() == 1 && childElements.get(0) instanceof Image ? 1 : 1.35f);
            case Property.FIRST_LINE_INDENT:
                return (T1) (Object) 0f;
            case Property.MARGIN_TOP:
            case Property.MARGIN_BOTTOM:
                return (T1) (Object) UnitValue.createPointValue(4f);
            case Property.TAB_DEFAULT:
                return (T1) (Object) 50f;
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }
    /**
     * Sets orphans restriction on a {@link Paragraph}.
     *
     * @param orphansControl an instance of {@link ParagraphOrphansControl}
     *
     * @return this Element
     */
    public T setOrphansControl(ParagraphOrphansControl orphansControl) {
        setProperty(Property.ORPHANS_CONTROL, orphansControl);
        return (T)this;
    }

    /**
     * Sets widows restriction on a {@link Paragraph}.
     *
     * @param widowsControl an instance of {@link ParagraphWidowsControl}
     *
     * @return this Element
     */
    public T setWidowsControl(ParagraphWidowsControl widowsControl) {
        setProperty(Property.WIDOWS_CONTROL, widowsControl);
        return (T)this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.P);
        }
        return tagProperties;
    }

    /**
     * Returns a map of unsupported properties. The map is empty by default,
     * but can be overridden by subclasses to specify unsupported properties.
     * @return a map of unsupported properties, where the key is the property ID and the value is the property name
     */
    public Map<Integer, String> getUnsupportedProperties() {
        return Collections.<Integer, String>emptyMap();
    }

    /**
     * Adds a list of {@link TabStop} objects to the paragraph's properties.
     *
     * @param newTabStops the list of {@link TabStop} objects to be added
     */
    protected void addTabStopsAsProperty(java.util.List<TabStop> newTabStops) {
        Map<Float, TabStop> tabStops = this.<Map<Float, TabStop>>getProperty(Property.TAB_STOPS);
        if (tabStops == null) {
            tabStops = new TreeMap<>();
            setProperty(Property.TAB_STOPS, tabStops);
        }
        for (TabStop tabStop : newTabStops) {
            tabStops.put(tabStop.getTabPosition(), tabStop);
        }
    }
}
