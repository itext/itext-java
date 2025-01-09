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
package com.itextpdf.styledxmlparser.css.validate.impl.datatype;


import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator;

/**
 * {@link ICssDataTypeValidator} implementation for css transform property .
 */
public class CssTransformValidator implements ICssDataTypeValidator {

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.validate.ICssDataTypeValidator#isValid(java.lang.String)
     */
    @Override
    public boolean isValid(String objectString) {
        if (CommonCssConstants.NONE.equals(objectString))
            return true;
        String[] components = objectString.split("\\)");
        for (String component : components)
            if (!isValidComponent(component))
                return false;
        return true;
    }

    private boolean isValidComponent(String objectString) {
        String function, args;
        if (!CommonCssConstants.NONE.equals(objectString) && objectString.indexOf('(') > 0) {
            function = objectString.substring(0, objectString.indexOf('(')).trim();
            args = objectString.substring(objectString.indexOf('(') + 1);
        } else {
            return false;
        }
        if (CommonCssConstants.MATRIX.equals(function) || CommonCssConstants.SCALE.equals(function) ||
                CommonCssConstants.SCALE_X.equals(function) || CommonCssConstants.SCALE_Y.equals(function)) {
            String[] arg = args.split(",");
            if (arg.length == 6 && CommonCssConstants.MATRIX.equals(function) ||
                    (arg.length == 1 || arg.length == 2) && CommonCssConstants.SCALE.equals(function) ||
                    arg.length == 1 && (CommonCssConstants.SCALE_X.equals(function) || CommonCssConstants.SCALE_Y.equals(function))) {
                int i = 0;
                for (; i < arg.length; i++) {
                    try {
                        Float.parseFloat(arg[i].trim());
                    } catch (NumberFormatException exc) {
                        return false;
                    }
                }
                if (i == arg.length)
                    return true;
            }
            return false;
        } else if (CommonCssConstants.TRANSLATE.equals(function)
                || CommonCssConstants.TRANSLATE_X.equals(function) || CommonCssConstants.TRANSLATE_Y.equals(function)) {
            String[] arg = args.split(",");
            if ((arg.length == 1 || arg.length == 2 && CommonCssConstants.TRANSLATE.equals(function))) {
                for (String a : arg)
                    if (!isValidForTranslate(a))
                        return false;
                return true;
            }
            return false;
        } else if (CommonCssConstants.ROTATE.equals(function)) {
            try {
                float value = Float.parseFloat(args);
                if (value == 0.0f)
                    return true;
            } catch (NumberFormatException exc) {
            }
            int deg = args.indexOf('d');
            int rad = args.indexOf('r');
            if (deg > 0 && args.substring(deg).equals("deg") || rad > 0 && args.substring(rad).equals("rad")) {
                try {
                    Double.parseDouble(args.substring(0, deg > 0 ? deg : rad));
                } catch (NumberFormatException exc) {
                    return false;
                }
                return true;
            }
            return false;
        } else if (CommonCssConstants.SKEW.equals(function)
                || CommonCssConstants.SKEW_X.equals(function) || CommonCssConstants.SKEW_Y.equals(function)) {
            String[] arg = args.split(",");
            if ((arg.length == 1) || (arg.length == 2 && CommonCssConstants.SKEW.equals(function))) {
                for (int k = 0; k < arg.length; k++) {
                    try {
                        float value = Float.parseFloat(arg[k]);
                        if (value != 0.0f)
                            return false;
                    } catch (NumberFormatException exc) {
                    }
                    int deg = arg[k].indexOf('d');
                    int rad = arg[k].indexOf('r');
                    if (deg < 0 && rad < 0)
                        return false;
                    if (deg > 0 && !arg[k].substring(deg).equals("deg") && rad < 0 || (rad > 0 && !arg[k].substring(rad).equals("rad")))
                        return false;
                    try {
                        Float.parseFloat(arg[k].trim().substring(0, rad > 0 ? rad : deg));
                    } catch (NumberFormatException exc) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isValidForTranslate(String string) {
        if (string == null)
            return false;
        int pos = 0;
        while (pos < string.length()) {
            if (string.charAt(pos) == '+' ||
                    string.charAt(pos) == '-' ||
                    string.charAt(pos) == '.' ||
                    string.charAt(pos) >= '0' && string.charAt(pos) <= '9') {
                pos++;
            } else {
                break;
            }
        }
        if (pos > 0) {
            try {
                Float.parseFloat(string.substring(0, pos));
            } catch (NumberFormatException exc) {
                return false;
            }
            return (Float.parseFloat(string.substring(0, pos)) == 0.0f || string.substring(pos).equals(CommonCssConstants.PT) || string.substring(pos).equals(CommonCssConstants.IN) ||
                    string.substring(pos).equals(CommonCssConstants.CM) || string.substring(pos).equals(CommonCssConstants.Q) ||
                    string.substring(pos).equals(CommonCssConstants.MM) || string.substring(pos).equals(CommonCssConstants.PC) ||
                    string.substring(pos).equals(CommonCssConstants.PX) || string.substring(pos).equals(CommonCssConstants.PERCENTAGE));
        }
        return false;
    }
}
