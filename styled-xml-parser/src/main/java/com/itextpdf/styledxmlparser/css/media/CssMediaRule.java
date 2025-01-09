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
package com.itextpdf.styledxmlparser.css.media;

import com.itextpdf.styledxmlparser.css.CssNestedAtRule;
import com.itextpdf.styledxmlparser.css.CssRuleName;
import com.itextpdf.styledxmlparser.css.CssRuleSet;
import com.itextpdf.styledxmlparser.css.CssStatement;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link CssNestedAtRule} implementation for media rules.
 */
public class CssMediaRule extends CssNestedAtRule {

    /** The media queries. */
    private List<MediaQuery> mediaQueries;

    /**
     * Creates a {@link CssMediaRule}.
     *
     * @param ruleParameters the rule parameters
     */
    public CssMediaRule(String ruleParameters) {
        super(CssRuleName.MEDIA, ruleParameters);
        mediaQueries = MediaQueryParser.parseMediaQueries(ruleParameters);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.CssNestedAtRule#getCssRuleSets(com.itextpdf.styledxmlparser.html.node.INode, com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription)
     */
    @Override
    public List<CssRuleSet> getCssRuleSets(INode element, MediaDeviceDescription deviceDescription) {
        List<CssRuleSet> result = new ArrayList<>();
        for (MediaQuery mediaQuery : mediaQueries) {
            if (mediaQuery.matches(deviceDescription)) {
                for (CssStatement childStatement : body) {
                    result.addAll(childStatement.getCssRuleSets(element, deviceDescription));
                }
                break;
            }
        }
        return result;
    }

    /**
     * Tries to match a media device.
     *
     * @param deviceDescription the device description
     * @return true, if successful
     */
    public boolean matchMediaDevice(MediaDeviceDescription deviceDescription) {
        for (MediaQuery mediaQuery : mediaQueries) {
            if (mediaQuery.matches(deviceDescription)) {
                return true;
            }
        }
        return false;
    }
}
