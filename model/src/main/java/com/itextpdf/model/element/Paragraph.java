package com.itextpdf.model.element;

import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.ParagraphRenderer;

import java.util.Arrays;
import java.util.TreeMap;

public class Paragraph extends BlockElement<Paragraph> {

    public Paragraph() {
    }

    public Paragraph(String text) {
        this(new Text(text));
    }

    public Paragraph(Text text) {
        add(text);
    }

    public <T extends Paragraph> T add(String text) {
        return add(new Text(text));
    }

    public <T extends Paragraph> T add(ILeafElement element) {
        childElements.add(element);
        return (T) this;
    }

    public <T extends Paragraph> T addAll(java.util.List<? extends ILeafElement> elements) {
        for (ILeafElement element : elements) {
            add(element);
        }
        return (T) this;
    }

    public <T extends Paragraph> T addTabStops(TabStop ... tabStops) {
        addTabStopsAsProperty(Arrays.asList(tabStops));
        return (T) this;
    }

    public <T extends Paragraph> T addTabStops(java.util.List<TabStop> tabStops) {
        addTabStopsAsProperty(tabStops);
        return (T) this;
    }

    public <T extends Paragraph> T removeTabStop(float tabStopPosition) {
        TreeMap<Float, TabStop> tabStops = getProperty(Property.TAB_STOPS);
        if (tabStops != null) {
            tabStops.remove(tabStopPosition);
        }
        return (T) this;
    }

    @Override
    public <T> T getDefaultProperty(Property property) {
        switch (property) {
            case LEADING:
                return (T) new Property.Leading(Property.Leading.MULTIPLIED, childElements.size() == 1 && childElements.get(0) instanceof Image ? 1 : 1.35f);
            case FIRST_LINE_INDENT:
                return (T) Float.valueOf(0);
            case MARGIN_TOP:
            case MARGIN_BOTTOM:
                return (T) Float.valueOf(4);
            case TAB_DEFAULT:
                return (T) Float.valueOf(50);
            default:
                return super.getDefaultProperty(property);
        }
    }

    public <T extends Paragraph> T setFirstLineIndent(float indent) {
        setProperty(Property.FIRST_LINE_INDENT, indent);
        return (T) this;
    }

    public <T extends Paragraph> T setFixedLeading(float leading) {
        setProperty(Property.LEADING, new Property.Leading(Property.Leading.FIXED, leading));
        return (T) this;
    }

    public <T extends Paragraph> T setMultipliedLeading(float leading) {
        setProperty(Property.LEADING, new Property.Leading(Property.Leading.MULTIPLIED, leading));
        return (T) this;
    }


    @Override
    protected ParagraphRenderer makeNewRenderer() {
        return new ParagraphRenderer(this);
    }

    private void addTabStopsAsProperty(java.util.List<TabStop> newTabStops) {
        TreeMap<Float, TabStop> tabStops = getProperty(Property.TAB_STOPS);
        if (tabStops == null) {
            tabStops = new TreeMap<>();
            setProperty(Property.TAB_STOPS, tabStops);
        }
        for (TabStop tabStop : newTabStops) {
            tabStops.put(tabStop.getTabPosition(), tabStop);
        }
    }
}
