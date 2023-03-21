package com.itextpdf.forms.form.renderer.checkboximpl;

import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Abstract class for creating CheckBox Renderers. Subclasses must implement the
 * createFlatRenderer(), getDefaultSize(), getDefaultColor(), getDefaultCheckBoxType()
 * methods. It also provides a default implementation for getFillColor() and
 * shouldDrawChildren() methods.
 */
public abstract class AbstractCheckBoxRendererFactory {

    private static final float DEFAULT_SIZE = 8.25F;
    private final CheckBoxRenderer checkBoxRenderer;
    private float size;

    protected AbstractCheckBoxRendererFactory(CheckBoxRenderer checkBoxRenderer) {
        this.checkBoxRenderer = checkBoxRenderer;
    }

    /**
     * Gets the CheckBoxRenderer.
     *
     * @return the CheckBoxRenderer
     */
    public float getSize() {
        return size;
    }

    /**
     * Creates an instance of the flat renderer.
     *
     * @return the created flat renderer
     */
    public abstract IRenderer createFlatRenderer();

    /**
     * Gets the default size of the CheckBox.
     *
     * @return the default size of the CheckBox
     */
    protected float getDefaultSize() {
        return DEFAULT_SIZE;
    }

    /**
     * Gets the default color of the CheckBox.
     *
     * @return the default color of the CheckBox
     */
    protected Background getDefaultColor() {
        return null;
    }

    /**
     * Gets the default CheckBoxType of the CheckBox.
     *
     * @return the default CheckBoxType of the CheckBox
     */
    protected CheckBoxType getDefaultCheckBoxType() {
        return CheckBoxType.CROSS;
    }

    /**
     * Sets up the size of the CheckBox based on its height and width properties.
     */
    protected void setupSize() {
        final UnitValue heightUV = this.checkBoxRenderer.getPropertyAsUnitValue(Property.HEIGHT);
        final UnitValue widthUV = this.checkBoxRenderer.getPropertyAsUnitValue(Property.WIDTH);

        final float height = null == heightUV ? getDefaultSize() : heightUV.getValue();
        final float width = null == widthUV ? getDefaultSize() : widthUV.getValue();
        this.size = Math.min(height, width);
        checkBoxRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(width));
        checkBoxRenderer.setProperty(Property.HEIGHT, UnitValue.createPointValue(height));
    }

    /**
     * Gets the fill color of the CheckBox.
     *
     * @return the fill color of the CheckBox
     */
    public Color getFillColor() {
        return ColorConstants.BLACK;
    }

    /**
     * Gets the CheckBoxType of the CheckBox.
     *
     * @return the CheckBoxType of the CheckBox
     */
    public CheckBoxType getCheckBoxType() {
        if (checkBoxRenderer.hasProperty(FormProperty.FORM_CHECKBOX_TYPE)) {
            final CheckBoxType checkBoxType = (CheckBoxType) checkBoxRenderer.<CheckBoxType>getProperty(
                    FormProperty.FORM_CHECKBOX_TYPE);
            return checkBoxType == null ? getDefaultCheckBoxType() : checkBoxType;
        }
        return getDefaultCheckBoxType();
    }

    /**
     * Gets the background color of the CheckBox.
     *
     * @return the background color of the CheckBox
     */
    public Background getBackgroundColor() {
        final Background backgroundColor = checkBoxRenderer.<Background>getProperty(Property.BACKGROUND);
        return backgroundColor == null ? getDefaultColor() : backgroundColor;
    }

    /**
     * Checks if the CheckBox should draw its children.
     *
     * @return true if the CheckBox should draw its children, false otherwise
     */
    public boolean shouldDrawChildren() {
        return checkBoxRenderer.isBoxChecked();
    }

}
