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

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;

/**
 * Represent Action tag in xfdf document structure.
 * Content model: ( URI | Launch | GoTo | GoToR | Named ).
 * Attributes: none.
 * For more details see paragraph 6.5.1 in Xfdf specification.
 */
public class ActionObject {

    /**
     * Type of inner action element. Possible values: {@link PdfName#URI}, {@link PdfName#Launch}, {@link PdfName#GoTo},
     * {@link PdfName#GoToR}, {@link PdfName#Named}.
     */
    private PdfName type;

    /**
     * Represents Name, required attribute of URI element. For more details see paragraph 6.5.30 in Xfdf specification.
     */
    private PdfString uri;

    /**
     * Represents IsMap, optional attribute of URI element. For more details see paragraph 6.5.30 in Xfdf specification.
     */
    private boolean isMap;

    /**
     * Represents Name, required attribute of Named element. For more details see paragraph 6.5.24 in Xfdf specification.
     */
    private PdfName nameAction;

    /**
     * Represents OriginalName required attribute of File inner element of GoToR or Launch element.
     * Corresponds to F key in go-to action or launch dictionaries.
     * For more details see paragraphs 6.5.11, 6.5.23 in Xfdf specification.
     */
    private String fileOriginalName;

    /**
     * Represents NewWindow, optional attribute of Launch element. For more details see paragraph 6.5.23 in Xfdf specification.
     */
    private boolean isNewWindow;

    /**
     * Represents Dest, inner element of link, GoTo, and GoToR elements.
     * Corresponds to Dest key in link annotation dictionary.
     * For more details see paragraph 6.5.10 in Xfdf specification.
     */
    private DestObject destination;

    /**
     * Creates an instance of {@link ActionObject}.
     *
     * @param type type of inner action element. Possible values: {@link PdfName#URI}, {@link PdfName#Launch},
     *             {@link PdfName#GoTo}, {@link PdfName#GoToR}, {@link PdfName#Named}
     */
    public ActionObject(PdfName type) {
        this.type = type;
    }

    /**
     * Returns the type of inner action element. Possible values: {@link PdfName#URI}, {@link PdfName#Launch},
     * {@link PdfName#GoTo}, {@link PdfName#GoToR}, {@link PdfName#Named}.
     *
     * @return {@link PdfName} type of inner action element.
     */
    public PdfName getType() {
        return type;
    }

    /**
     * Sets the type of inner action element. Possible values: {@link PdfName#URI}, {@link PdfName#Launch},
     * {@link PdfName#GoTo}, {@link PdfName#GoToR}, {@link PdfName#Named}.
     *
     * @param type {@link PdfName} type of inner action object
     *
     * @return current {@link ActionObject}.
     */
    public ActionObject setType(PdfName type) {
        this.type = type;
        return this;
    }

    /**
     * Gets the string value of URI elements. Corresponds to Name, required attribute of URI element.
     * For more details see paragraph 6.5.30 in Xfdf specification.
     *
     * @return {@link PdfString} value of URI element.
     */
    public PdfString getUri() {
        return uri;
    }

    /**
     * Sets the string value of URI element. Corresponds to Name, required attribute of URI element.
     * For more details see paragraph 6.5.30 in Xfdf specification.
     *
     * @param uri {@link PdfString} value to be set to URI element
     *
     * @return current {@link ActionObject}.
     */
    public ActionObject setUri(PdfString uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Gets IsMap, optional attribute of URI element. For more details see paragraph 6.5.30 in Xfdf specification.
     *
     * @return boolean indicating if URI element is a map.
     */
    public boolean isMap() {
        return isMap;
    }

    /**
     * Sets IsMap, optional attribute of URI element. For more details see paragraph 6.5.30 in Xfdf specification.
     *
     * @param map boolean indicating if URI element is a map
     *
     * @return current {@link ActionObject}.
     */
    public ActionObject setMap(boolean map) {
        isMap = map;
        return this;
    }

    /**
     * Gets the value of Name, required attribute of Named element.
     * For more details see paragraph 6.5.24 in Xfdf specification.
     *
     * @return {@link PdfName} value of Name attribute of a named action element.
     */
    public PdfName getNameAction() {
        return nameAction;
    }

    /**
     * Sets the value of Name, required attribute of Named element.
     * For more details see paragraph 6.5.24 in Xfdf specification.
     *
     * @param nameAction {@link PdfName} value to be set to Name attribute of a named action element
     *
     * @return current {@link ActionObject}.
     */
    public ActionObject setNameAction(PdfName nameAction) {
        this.nameAction = nameAction;
        return this;
    }

    /**
     * Gets the string value of OriginalName, required attribute of File inner element of GoToR or Launch element.
     * Corresponds to F key in go-to action or launch dictionaries.
     * For more details see paragraphs 6.5.11, 6.5.23 in Xfdf specification.
     *
     * @return {@link String} value of OriginalName attribute of current action object.
     */
    public String getFileOriginalName() {
        return fileOriginalName;
    }

    /**
     * Sets the string value of OriginalName, required attribute of File inner element of GoToR or Launch element.
     * Corresponds to F key in go-to action or launch dictionaries.
     * For more details see paragraphs 6.5.11, 6.5.23 in Xfdf specification.
     *
     * @param fileOriginalName {@link String} value of OriginalName attribute of action object
     *
     * @return current {@link ActionObject}.
     */
    public ActionObject setFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
        return this;
    }

    /**
     * Gets the boolean value of NewWindow, optional attribute of Launch element.
     * For more details see paragraph 6.5.23 in Xfdf specification.
     *
     * @return boolean indicating if current Launch action element should be opened in a new window.
     */
    public boolean isNewWindow() {
        return isNewWindow;
    }

    /**
     * Sets the boolean value of NewWindow, optional attribute of Launch element.
     * For more details see paragraph 6.5.23 in Xfdf specification.
     *
     * @param newWindow boolean indicating if current Launch action element should be opened in a new window
     *
     * @return current {@link ActionObject}.
     */
    public ActionObject setNewWindow(boolean newWindow) {
        isNewWindow = newWindow;
        return this;
    }

    /**
     * Gets Dest, inner element of link, GoTo, and GoToR elements.
     * Corresponds to Dest key in link annotation dictionary.
     * For more details see paragraph 6.5.10 in Xfdf specification.
     *
     * @return {@link DestObject} destination attribute of current action element.
     */
    public DestObject getDestination() {
        return destination;
    }

    /**
     * Sets Dest, inner element of link, GoTo, and GoToR elements.
     * Corresponds to Dest key in link annotation dictionary.
     * For more details see paragraph 6.5.10 in Xfdf specification.
     *
     * @param destination {@link DestObject} destination attribute of the action element
     *
     * @return current {@link ActionObject}.
     */
    public ActionObject setDestination(DestObject destination) {
        this.destination = destination;
        return this;
    }
}
