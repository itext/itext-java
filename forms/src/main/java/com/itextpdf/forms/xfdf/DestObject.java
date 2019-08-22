/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.forms.xfdf;


/**
 * Represents Dest element, a child of the link, GoTo, and GoToR elements.
 * Corresponds to the Dest key in the link annotations dictionary.
 * For more details see paragraph 6.5.10 in Xfdf document specification.
 * Content model: ( Named | XYZ | Fit | FitH | FitV | FitR | FitB | FitBH | FitBV )
 */
public class DestObject {

    /**
     * Respresents Name attribute of Named element, a child of Dest element.
     * Allows a destination to be referred to indirectly by means of a name object or a byte string.
     * For more details see paragraph 6.5.25 in Xfdf document specification.
     */
    private String name;

    /**
     * Represents the XYZ element, a child of the Dest element.
     * Corresponds to the XYZ key in the destination syntax.
     * Required attributes: Page, Left, Bottom, Right, Top.
     * For more details see paragraph 6.5.32 in Xfdf document specification.
     */
    private FitObject xyz;

    /**
     * Represents the Fit element, a child of the Dest element.
     * Corresponds to the Fit key in the destination syntax.
     * Required attributes: Page.
     * For more details see paragraph 6.5.13 in Xfdf document specification.
     */
    private FitObject fit;

    /**
     * Represents the FitH element, a child of the Dest element.
     * Corresponds to the FitH key in the destination syntax.
     * Required attributes: Page, Top.
     * For more details see paragraph 6.5.17 in Xfdf document specification.
     */
    private FitObject fitH;

    /**
     * Represents the FitV element, a child of the Dest element.
     * Corresponds to the FitV key in the destination syntax.
     * Required attributes: Page, Left.
     * For more details see paragraph 6.5.19 in Xfdf document specification.
     */
    private FitObject fitV;

    /**
     * Represents the FitR element, a child of the Dest element.
     * Corresponds to the FitR key in the destination syntax.
     * Required attributes: Page, Left, Bottom, Right, Top.
     * For more details see paragraph 6.5.18 in Xfdf document specification.
     */
    private FitObject fitR;

    /**
     * Represents the FitB element, a child of the Dest element.
     * Corresponds to the FitB key in the destination syntax.
     * Required attributes: Page.
     * For more details see paragraph 6.5.14 in Xfdf document specification.
     */
    private FitObject fitB;

    /**
     * Represents the FitBH element, a child of the Dest element.
     * Corresponds to the FitBH key in the destination syntax.
     * Required attributes: Page, Top.
     * For more details see paragraph 6.5.15 in Xfdf document specification.
     */
    private FitObject fitBH;

    /**
     * Represents the FitBV element, a child of the Dest element.
     * Corresponds to the FitBV key in the destination syntax.
     * Required attributes: Page, Left.
     * For more details see paragraph 6.5.16 in Xfdf document specification.
     */
    private FitObject fitBV;

    public DestObject() {
        //create empty DestObject
    }

    public String getName() {
        return name;
    }

    public DestObject setName(String name) {
        this.name = name;
        return this;
    }

    public FitObject getXyz() {
        return xyz;
    }

    public DestObject setXyz(FitObject xyz) {
        this.xyz = xyz;
        return this;
    }

    public FitObject getFit() {
        return fit;
    }

    public DestObject setFit(FitObject fit) {
        this.fit = fit;
        return this;
    }

    public FitObject getFitH() {
        return fitH;
    }

    public DestObject setFitH(FitObject fitH) {
        this.fitH = fitH;
        return this;
    }

    public FitObject getFitV() {
        return fitV;
    }

    public DestObject setFitV(FitObject fitV) {
        this.fitV = fitV;
        return this;
    }

    public FitObject getFitR() {
        return fitR;
    }

    public DestObject setFitR(FitObject fitR) {
        this.fitR = fitR;
        return this;
    }

    public FitObject getFitB() {
        return fitB;
    }

    public DestObject setFitB(FitObject fitB) {
        this.fitB = fitB;
        return this;
    }

    public FitObject getFitBH() {
        return fitBH;
    }

    public DestObject setFitBH(FitObject fitBH) {
        this.fitBH = fitBH;
        return this;
    }

    public FitObject getFitBV() {
        return fitBV;
    }

    public DestObject setFitBV(FitObject fitBV) {
        this.fitBV = fitBV;
        return this;
    }
}
