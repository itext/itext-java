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
package com.itextpdf.kernel.pdf.canvas.wmf;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineJoinStyle;

/**
 * Class to keep the state.
 */
public class MetaState {

    public static final int TA_NOUPDATECP = 0;
    public static final int TA_UPDATECP = 1;
    public static final int TA_LEFT = 0;
    public static final int TA_RIGHT = 2;
    public static final int TA_CENTER = 6;
    public static final int TA_TOP = 0;
    public static final int TA_BOTTOM = 8;
    public static final int TA_BASELINE = 24;

    public static final int TRANSPARENT = 1;
    public static final int OPAQUE = 2;

    public static final int ALTERNATE = 1;
    public static final int WINDING = 2;

    /**
     * Stack of saved states.
     */
    public Stack<MetaState> savedStates;

    /**
     * List of MetaObjects.
     */
    public List<MetaObject> MetaObjects;

    /**
     * Current Point.
     */
    public Point currentPoint;

    /**
     * Current Pen.
     */
    public MetaPen currentPen;

    /**
     * Current Brush.
     */
    public MetaBrush currentBrush;

    /**
     * Current Font.
     */
    public MetaFont currentFont;

    /**
     * The current background color. Default value is DeviceRgb#WHITE.
     */
    public Color currentBackgroundColor = ColorConstants.WHITE;

    /**
     * Current text color. Default value is DeviceRgb#BLACK.
     */
    public Color currentTextColor = ColorConstants.BLACK;

    /**
     * The current background mode. Default value is OPAQUE.
     */
    public int backgroundMode = OPAQUE;

    /**
     * Current polygon fill mode. Default value is ALTERNATE.
     */
    public int polyFillMode = ALTERNATE;

    /**
     * Curent line join. Default value is 1.
     */
    public int lineJoin = 1;

    /**
     * Current text alignment.
     */
    public int textAlign;

    /**
     * Current offset for Wx.
     */
    public int offsetWx;

    /**
     * Current offset for Wy.
     */
    public int offsetWy;

    /**
     * Current extent for Wx.
     */
    public int extentWx;

    /**
     * Current extent for Wy.
     */
    public int extentWy;

    /**
     * Current x value for scaling.
     */
    public float scalingX;

    /**
     * Current y value for scaling.
     */
    public float scalingY;


    /**
     * Creates new MetaState
     */
    public MetaState() {
        savedStates = new Stack<>();
        MetaObjects = new ArrayList<>();
        currentPoint = new Point(0, 0);
        currentPen = new MetaPen();
        currentBrush = new MetaBrush();
        currentFont = new MetaFont();
    }

    /**
     * Clones a new MetaState from the specified MetaState.
     *
     * @param state the state to clone
     */
    public MetaState(MetaState state) {
        setMetaState(state);
    }

    /**
     * Sets every field of this MetaState to the values of the fields of the specified MetaState.
     *
     * @param state state to copy
     */
    public void setMetaState(MetaState state) {
        savedStates = state.savedStates;
        MetaObjects = state.MetaObjects;
        currentPoint = state.currentPoint;
        currentPen = state.currentPen;
        currentBrush = state.currentBrush;
        currentFont = state.currentFont;
        currentBackgroundColor = state.currentBackgroundColor;
        currentTextColor = state.currentTextColor;
        backgroundMode = state.backgroundMode;
        polyFillMode = state.polyFillMode;
        textAlign = state.textAlign;
        lineJoin = state.lineJoin;
        offsetWx = state.offsetWx;
        offsetWy = state.offsetWy;
        extentWx = state.extentWx;
        extentWy = state.extentWy;
        scalingX = state.scalingX;
        scalingY = state.scalingY;
    }

    /**
     * Add a MetaObject to the State.
     *
     * @param object MetaObject to be added
     */
    public void addMetaObject(MetaObject object) {
        for (int k = 0; k < MetaObjects.size(); ++k) {
            if (MetaObjects.get(k) == null) {
                MetaObjects.set(k, object);
                return;
            }
        }
        MetaObjects.add(object);
    }

    /**
     * Select the MetaObject at the specified index and prepare the PdfCanvas.
     *
     * @param index position of the MetaObject
     * @param cb PdfCanvas to prepare
     */
    public void selectMetaObject(int index, PdfCanvas cb) {
        MetaObject obj = MetaObjects.get(index);
        if (obj == null)
            return;
        int style;
        switch (obj.getType()) {
            case MetaObject.META_BRUSH:
                currentBrush = (MetaBrush)obj;
                style = currentBrush.getStyle();
                if (style == MetaBrush.BS_SOLID) {
                    Color color = currentBrush.getColor();
                    cb.setFillColor(color);
                }
                else if (style == MetaBrush.BS_HATCHED) {
                    Color color = currentBackgroundColor;
                    cb.setFillColor(color);
                }
                break;
            case MetaObject.META_PEN:
            {
                currentPen = (MetaPen)obj;
                style = currentPen.getStyle();
                if (style != MetaPen.PS_NULL) {
                    Color color = currentPen.getColor();
                    cb.setStrokeColor(color);
                    cb.setLineWidth(Math.abs(currentPen.getPenWidth() * scalingX / extentWx));
                    switch (style) {
                        case MetaPen.PS_DASH:
                            cb.setLineDash(18, 6, 0);
                            break;
                        case MetaPen.PS_DASHDOT:
                            cb.writeLiteral("[9 6 3 6]0 d\n");
                            break;
                        case MetaPen.PS_DASHDOTDOT:
                            cb.writeLiteral("[9 3 3 3 3 3]0 d\n");
                            break;
                        case MetaPen.PS_DOT:
                            cb.setLineDash(3, 0);
                            break;
                        default:
                            cb.setLineDash(0);
                            break;
                    }
                }
                break;
            }
            case MetaObject.META_FONT:
            {
                currentFont = (MetaFont)obj;
                break;
            }
        }
    }

    /**
     * Deletes the MetaObject at the specified index.
     *
     * @param index index of the MetaObject to delete
     */
    public void deleteMetaObject(int index) {
        MetaObjects.set(index, null);
    }

    /**
     * Saves the state of this MetaState object.
     *
     * @param cb PdfCanvas object on which saveState() will be called
     */
    public void saveState(PdfCanvas cb) {
        cb.saveState();
        MetaState state = new MetaState(this);
        savedStates.push(state);
    }

    /**
     * Restores the state to the next state on the saved states stack.
     *
     * @param index index of the state to be restored
     * @param cb PdfCanvas object on which restoreState() will be called
     */
    public void restoreState(int index, PdfCanvas cb) {
        int pops;
        if (index < 0)
            pops = Math.min(-index, savedStates.size());
        else
            pops = Math.max(savedStates.size() - index, 0);
        if (pops == 0)
            return;
        MetaState state = null;
        while (pops-- != 0) {
            cb.restoreState();
            state = savedStates.pop();
        }
        setMetaState(state);
    }

    /**
     * Restres the state of the specified PdfCanvas object for as many times as there are saved states on the stack.
     *
     * @param cb PdfCanvas object
     */
    public void cleanup(PdfCanvas cb) {
        int k = savedStates.size();
        while (k-- > 0)
            cb.restoreState();
    }

    /**
     * Transform the specified value.
     *
     * @param x the value to transform
     * @return the transformed value
     */
    public float transformX(int x) {
        return ((float)x - offsetWx) * scalingX / extentWx;
    }

    /**
     * Transform the specified value.
     *
     * @param y the value to transform
     * @return transformed value
     */
    public float transformY(int y) {
        return (1f - ((float)y - offsetWy) / extentWy) * scalingY;
    }

    /**
     * Sets the x value for scaling.
     *
     * @param scalingX x value for scaling
     */
    public void setScalingX(float scalingX) {
        this.scalingX = scalingX;
    }

    /**
     * Sets the y value for scaling.
     *
     * @param scalingY y value for scaling
     */
    public void setScalingY(float scalingY) {
        this.scalingY = scalingY;
    }

    /**
     * Sets the Wx offset value.
     *
     * @param offsetWx Wx offset value
     */
    public void setOffsetWx(int offsetWx) {
        this.offsetWx = offsetWx;
    }

    /**
     * Sets the Wy offset value.
     *
     * @param offsetWy Wy offset value
     */
    public void setOffsetWy(int offsetWy) {
        this.offsetWy = offsetWy;
    }

    /**
     * Sets the Wx extent value.
     *
     * @param extentWx Wx extent value
     */
    public void setExtentWx(int extentWx) {
        this.extentWx = extentWx;
    }

    /**
     * Sets the Wy extent value.
     *
     * @param extentWy Wy extent value
     */
    public void setExtentWy(int extentWy) {
        this.extentWy = extentWy;
    }

    /**
     * Transforms the specified angle. If scalingY is less than 0, the angle is multiplied by -1. If scalingX is less
     * than 0, the angle is subtracted from Math.PI.
     *
     * @param angle the angle to transform
     * @return the transformed angle
     */
    public float transformAngle(float angle) {
        float ta = scalingY < 0 ? -angle : angle;
        return (float)(scalingX < 0 ? Math.PI - ta : ta);
    }

    /**
     * Sets the current Point to the specified Point.
     *
     * @param p Point to set
     */
    public void setCurrentPoint(Point p) {
        currentPoint = p;
    }

    /**
     * Returns the current Point.
     *
     * @return current Point
     */
    public Point getCurrentPoint() {
        return currentPoint;
    }

    /**
     * Returns the current MetaBrush object.
     *
     * @return current MetaBrush
     */
    public MetaBrush getCurrentBrush() {
        return currentBrush;
    }

    /**
     * Returns the current MetaPen object.
     *
     * @return current MetaPen
     */
    public MetaPen getCurrentPen() {
        return currentPen;
    }

    /**
     * Returns the current MetaFont object.
     *
     * @return current MetaFont
     */
    public MetaFont getCurrentFont() {
        return currentFont;
    }

    /**
     * Getter for property currentBackgroundColor.
     * @return Value of property currentBackgroundColor.
     */
    public Color getCurrentBackgroundColor() {
        return currentBackgroundColor;
    }

    /** Setter for property currentBackgroundColor.
     * @param currentBackgroundColor New value of property currentBackgroundColor.
     */
    public void setCurrentBackgroundColor(Color currentBackgroundColor) {
        this.currentBackgroundColor = currentBackgroundColor;
    }

    /** Getter for property currentTextColor.
     * @return Value of property currentTextColor.
     */
    public Color getCurrentTextColor() {
        return currentTextColor;
    }

    /** Setter for property currentTextColor.
     * @param currentTextColor New value of property currentTextColor.
     */
    public void setCurrentTextColor(Color currentTextColor) {
        this.currentTextColor = currentTextColor;
    }

    /** Getter for property backgroundMode.
     * @return Value of property backgroundMode.
     */
    public int getBackgroundMode() {
        return backgroundMode;
    }

    /** Setter for property backgroundMode.
     * @param backgroundMode New value of property backgroundMode.
     */
    public void setBackgroundMode(int backgroundMode) {
        this.backgroundMode = backgroundMode;
    }

    /** Getter for property textAlign.
     * @return Value of property textAlign.
     */
    public int getTextAlign() {
        return textAlign;
    }

    /** Setter for property textAlign.
     * @param textAlign New value of property textAlign.
     */
    public void setTextAlign(int textAlign) {
        this.textAlign = textAlign;
    }

    /** Getter for property polyFillMode.
     * @return Value of property polyFillMode.
     */
    public int getPolyFillMode() {
        return polyFillMode;
    }

    /** Setter for property polyFillMode.
     * @param polyFillMode New value of property polyFillMode.
     */
    public void setPolyFillMode(int polyFillMode) {
        this.polyFillMode = polyFillMode;
    }

    /**
     * Sets the line join style to {@link LineJoinStyle#MITER} if lineJoin isn't 0.
     *
     * @param cb PdfCanvas to set the line join style
     */
    public void setLineJoinRectangle(PdfCanvas cb) {
        if (lineJoin != 0) {
            lineJoin = 0;
            cb.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER);
        }
    }

    /**
     * Sets the line join style to {@link LineJoinStyle#ROUND} if lineJoin is 0.
     *
     * @param cb PdfCanvas to set the line join style
     */
    public void setLineJoinPolygon(PdfCanvas cb) {
        if (lineJoin == 0) {
            lineJoin = 1;
            cb.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND);
        }
    }

    /**
     * Returns true if lineJoin is 0.
     *
     * @return true if lineJoin is 0
     */
    public boolean getLineNeutral() {
        return lineJoin == 0;
    }

}
