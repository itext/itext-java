/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.properties.margins;

import com.itextpdf.layout.Style;

/**
 * Class representing properties to customize footnotes.
 *
 * <p>
 * Can be specified via {@link com.itextpdf.layout.Document#setFootnotesProperties(FootnotesProperties)} or
 * {@link com.itextpdf.layout.element.SectionBreak#setFootnotesProperties(FootnotesProperties)}.
 */
public class FootnotesProperties {

    private FootnoteNumberingType footnoteNumberingType;
    private FootnoteNumberingConfig footnoteNumberingConfig = FootnoteNumberingConfig.PER_PAGE;
    private Style footnotesContainerStyle = null;
    private Style footnoteAnchorLabelStyle = null;
    private Style footnoteAnchorStyle = null;

    /**
     * Creates new {@link FootnotesProperties} instance.
     */
    public FootnotesProperties() {
        // Empty constructor in order for default one to not be removed if another one is added.
    }

    /**
     * Gets {@link FootnoteNumberingType} representing numbering type for footnote anchors.
     *
     * @return {@link FootnoteNumberingType} numbering type
     */
    public FootnoteNumberingType getFootnoteNumberingType() {
        return footnoteNumberingType;
    }

    /**
     * Sets {@link FootnoteNumberingType} representing numbering type for footnote anchors.
     *
     * @param footnoteNumberingType {@link FootnoteNumberingType} representing numbering type for footnote anchors
     *
     * @return this same {@link FootnotesProperties} instance
     */
    public FootnotesProperties setFootnoteNumberingType(FootnoteNumberingType footnoteNumberingType) {
        this.footnoteNumberingType = footnoteNumberingType;
        return this;
    }

    /**
     * Gets {@link FootnoteNumberingConfig} representing numbering configuration for footnotes.
     *
     * @return {@link FootnoteNumberingConfig} representing footnotes numbering configuration
     */
    public FootnoteNumberingConfig getFootnoteNumberingConfig() {
        return footnoteNumberingConfig;
    }

    /**
     * Sets {@link FootnoteNumberingConfig} representing numbering configuration for footnotes.
     *
     * @param footnoteNumberingConfig {@link FootnoteNumberingConfig} representing footnotes numbering configuration
     *
     * @return this same {@link FootnotesProperties} instance
     */
    public FootnotesProperties setFootnoteNumberingConfig(FootnoteNumberingConfig footnoteNumberingConfig) {
        this.footnoteNumberingConfig = footnoteNumberingConfig;
        return this;
    }

    /**
     * Gets {@link Style} storing style properties for footnotes container.
     *
     * @return {@link Style} storing properties for footnotes container
     */
    public Style getFootnotesContainerStyle() {
        return footnotesContainerStyle;
    }

    /**
     * Sets {@link Style} storing style properties for footnotes container.
     *
     * @param footnotesContainerStyle {@link Style} storing properties for footnotes container
     *
     * @return this same {@link FootnotesProperties} instance
     */
    public FootnotesProperties setFootnotesContainerStyle(Style footnotesContainerStyle) {
        this.footnotesContainerStyle = footnotesContainerStyle;
        return this;
    }

    /**
     * Gets {@link Style} storing style properties for footnote anchors that are placed inside the footnotes container.
     *
     * @return {@link Style} storing properties for footnote anchors that are inside the footnotes
     */
    public Style getFootnoteAnchorLabelStyle() {
        return footnoteAnchorLabelStyle;
    }

    /**
     * Sets {@link Style} storing style properties for footnote anchors that are placed inside the footnotes container.
     *
     * @param footnoteAnchorLabelStyle {@link Style} storing properties for footnote anchors inside the footnotes
     *
     * @return this same {@link FootnotesProperties} instance
     */
    public FootnotesProperties setFootnoteAnchorLabelStyle(Style footnoteAnchorLabelStyle) {
        this.footnoteAnchorLabelStyle = footnoteAnchorLabelStyle;
        return this;
    }

    /**
     * Gets {@link Style} storing style properties for footnote anchors.
     *
     * @return {@link Style} storing properties for footnote anchors
     */
    public Style getFootnoteAnchorStyle() {
        return footnoteAnchorStyle;
    }

    /**
     * Sets {@link Style} storing style properties for footnote anchors.
     *
     * @param footnoteAnchorStyle {@link Style} storing properties for footnote anchors
     *
     * @return this same {@link FootnotesProperties} instance
     */
    public FootnotesProperties setFootnoteAnchorStyle(Style footnoteAnchorStyle) {
        this.footnoteAnchorStyle = footnoteAnchorStyle;
        return this;
    }
}
