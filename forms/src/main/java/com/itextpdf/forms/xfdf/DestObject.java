/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.forms.xfdf;


/**
 * Represents Dest element, a child of the link, GoTo, and GoToR elements.
 * Corresponds to the Dest key in the link annotations dictionary.
 * For more details see paragraph 6.5.10 in XFDF document specification.
 * Content model: ( Named | XYZ | Fit | FitH | FitV | FitR | FitB | FitBH | FitBV )
 */
public class DestObject {

    /**
     * Represents Name attribute of Named element, a child of Dest element.
     * Allows a destination to be referred to indirectly by means of a name object or a byte string.
     * For more details see paragraph 6.5.25 in XFDF document specification.
     */
    private String name;

    /**
     * Represents the XYZ element, a child of the Dest element.
     * Corresponds to the XYZ key in the destination syntax.
     * Required attributes: Page, Left, Bottom, Right, Top.
     * For more details see paragraph 6.5.32 in XFDF document specification.
     */
    private FitObject xyz;

    /**
     * Represents the Fit element, a child of the Dest element.
     * Corresponds to the Fit key in the destination syntax.
     * Required attributes: Page.
     * For more details see paragraph 6.5.13 in XFDF document specification.
     */
    private FitObject fit;

    /**
     * Represents the FitH element, a child of the Dest element.
     * Corresponds to the FitH key in the destination syntax.
     * Required attributes: Page, Top.
     * For more details see paragraph 6.5.17 in XFDF document specification.
     */
    private FitObject fitH;

    /**
     * Represents the FitV element, a child of the Dest element.
     * Corresponds to the FitV key in the destination syntax.
     * Required attributes: Page, Left.
     * For more details see paragraph 6.5.19 in XFDF document specification.
     */
    private FitObject fitV;

    /**
     * Represents the FitR element, a child of the Dest element.
     * Corresponds to the FitR key in the destination syntax.
     * Required attributes: Page, Left, Bottom, Right, Top.
     * For more details see paragraph 6.5.18 in XFDF document specification.
     */
    private FitObject fitR;

    /**
     * Represents the FitB element, a child of the Dest element.
     * Corresponds to the FitB key in the destination syntax.
     * Required attributes: Page.
     * For more details see paragraph 6.5.14 in XFDF document specification.
     */
    private FitObject fitB;

    /**
     * Represents the FitBH element, a child of the Dest element.
     * Corresponds to the FitBH key in the destination syntax.
     * Required attributes: Page, Top.
     * For more details see paragraph 6.5.15 in XFDF document specification.
     */
    private FitObject fitBH;

    /**
     * Represents the FitBV element, a child of the Dest element.
     * Corresponds to the FitBV key in the destination syntax.
     * Required attributes: Page, Left.
     * For more details see paragraph 6.5.16 in XFDF document specification.
     */
    private FitObject fitBV;

    /**
     * Creates an instance of {@link DestObject}.
     */
    public DestObject() {
        // Create an empty DestObject.
    }

    /**
     * Gets the Name attribute of Named element, a child of Dest element.
     * Allows a destination to be referred to indirectly by means of a name object or a byte string.
     * For more details see paragraph 6.5.25 in XFDF document specification.
     *
     * @return string value of the Name attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Name attribute of Named element, a child of Dest element.
     * Allows a destination to be referred to indirectly by means of a name object or a byte string.
     *
     * @param name string value of the Name attribute
     *
     * @return this {@link DestObject} instance.
     */
    public DestObject setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets the XYZ element, a child of the Dest element.
     * Corresponds to the XYZ key in the destination syntax.
     * Required attributes: Page, Left, Bottom, Right, Top.
     * For more details see paragraph 6.5.32 in XFDF document specification.
     *
     * @return a {@link FitObject} that represents XYZ of Dest element.
     */
    public FitObject getXyz() {
        return xyz;
    }

    /**
     * Sets the XYZ element, a child of the Dest element.
     * Corresponds to the XYZ key in the destination syntax.
     * Required attributes: Page, Left, Bottom, Right, Top.
     *
     * @param xyz a {@link FitObject} that represents XYZ of Dest element
     *
     * @return this {@link DestObject} instance.
     */
    public DestObject setXyz(FitObject xyz) {
        this.xyz = xyz;
        return this;
    }

    /**
     * Gets the Fit element, a child of the Dest element.
     * Corresponds to the Fit key in the destination syntax.
     * Required attributes: Page.
     * For more details see paragraph 6.5.13 in XFDF document specification.
     *
     * @return a {@link FitObject} that represents Fit of Dest element.
     */
    public FitObject getFit() {
        return fit;
    }

    /**
     * Sets the Fit element, a child of the Dest element.
     * Corresponds to the Fit key in the destination syntax.
     * Required attributes: Page.
     *
     * @param fit a {@link FitObject} that represents Fit of Dest element
     *
     * @return this {@link DestObject} instance.
     */
    public DestObject setFit(FitObject fit) {
        this.fit = fit;
        return this;
    }

    /**
     * Gets the FitH element, a child of the Dest element.
     * Corresponds to the FitH key in the destination syntax.
     * Required attributes: Page, Top.
     * For more details see paragraph 6.5.17 in XFDF document specification.
     *
     * @return a {@link FitObject} that represents FitH of Dest element.
     */
    public FitObject getFitH() {
        return fitH;
    }

    /**
     * Sets the FitH element, a child of the Dest element.
     * Corresponds to the FitH key in the destination syntax.
     * Required attributes: Page, Top.
     *
     * @param fitH a {@link FitObject} that represents FitH of Dest element
     *
     * @return this {@link DestObject} instance.
     */
    public DestObject setFitH(FitObject fitH) {
        this.fitH = fitH;
        return this;
    }

    /**
     * Gets the FitV element, a child of the Dest element.
     * Corresponds to the FitV key in the destination syntax.
     * Required attributes: Page, Left.
     * For more details see paragraph 6.5.19 in XFDF document specification.
     *
     * @return a {@link FitObject} that represents FitV of Dest element.
     */
    public FitObject getFitV() {
        return fitV;
    }

    /**
     * Sets the FitV element, a child of the Dest element.
     * Corresponds to the FitV key in the destination syntax.
     * Required attributes: Page, Left.
     *
     * @param fitV a {@link FitObject} that represents FitV of Dest element
     *
     * @return this {@link DestObject} instance.
     */
    public DestObject setFitV(FitObject fitV) {
        this.fitV = fitV;
        return this;
    }

    /**
     * Gets the FitR element, a child of the Dest element.
     * Corresponds to the FitR key in the destination syntax.
     * Required attributes: Page, Left, Bottom, Right, Top.
     * For more details see paragraph 6.5.18 in XFDF document specification.
     *
     * @return a {@link FitObject} that represents FitR of Dest element.
     */
    public FitObject getFitR() {
        return fitR;
    }

    /**
     * Sets the FitR element, a child of the Dest element.
     * Corresponds to the FitR key in the destination syntax.
     * Required attributes: Page, Left, Bottom, Right, Top.
     *
     * @param fitR a {@link FitObject} that represents FitR of Dest element
     *
     * @return this {@link DestObject} instance.
     */
    public DestObject setFitR(FitObject fitR) {
        this.fitR = fitR;
        return this;
    }

    /**
     * Sets the FitB element, a child of the Dest element.
     * Corresponds to the FitB key in the destination syntax.
     * Required attributes: Page.
     * For more details see paragraph 6.5.14 in XFDF document specification.
     *
     * @return a {@link FitObject} that represents FitB of Dest element.
     */
    public FitObject getFitB() {
        return fitB;
    }

    /**
     * Gets the FitB element, a child of the Dest element.
     * Corresponds to the FitB key in the destination syntax.
     * Required attributes: Page.
     * For more details see paragraph 6.5.14 in XFDF document specification.
     *
     * @param fitB a {@link FitObject} that represents FitB of Dest element
     *
     * @return this {@link DestObject} instance.
     */
    public DestObject setFitB(FitObject fitB) {
        this.fitB = fitB;
        return this;
    }

    /**
     * Sets the FitBH element, a child of the Dest element.
     * Corresponds to the FitBH key in the destination syntax.
     * Required attributes: Page, Top.
     * For more details see paragraph 6.5.15 in XFDF document specification.
     *
     * @return a {@link FitObject} that represents FitBH of Dest element.
     */
    public FitObject getFitBH() {
        return fitBH;
    }

    /**
     * Gets the FitBH element, a child of the Dest element.
     * Corresponds to the FitBH key in the destination syntax.
     * Required attributes: Page, Top.
     *
     * @param fitBH a {@link FitObject} that represents FitBH of Dest element
     *
     * @return this {@link DestObject} instance.
     */
    public DestObject setFitBH(FitObject fitBH) {
        this.fitBH = fitBH;
        return this;
    }

    /**
     * Sets the FitBV element, a child of the Dest element.
     * Corresponds to the FitBV key in the destination syntax.
     * Required attributes: Page, Left.
     * For more details see paragraph 6.5.16 in XFDF document specification.
     *
     * @return a {@link FitObject} that represents FitBV of Dest element.
     */
    public FitObject getFitBV() {
        return fitBV;
    }

    /**
     * Sets the FitBV element, a child of the Dest element.
     * Corresponds to the FitBV key in the destination syntax.
     * Required attributes: Page, Left.
     *
     * @param fitBV a {@link FitObject} that represents FitBV of Dest element
     *
     * @return this {@link DestObject} instance.
     */
    public DestObject setFitBV(FitObject fitBV) {
        this.fitBV = fitBV;
        return this;
    }
}
