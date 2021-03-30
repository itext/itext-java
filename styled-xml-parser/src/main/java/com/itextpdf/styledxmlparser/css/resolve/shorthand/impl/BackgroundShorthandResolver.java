/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.CssDefaults;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.ShorthandResolverFactory;
import com.itextpdf.styledxmlparser.css.util.CssBackgroundUtils;
import com.itextpdf.styledxmlparser.css.util.CssBackgroundUtils.BackgroundPropertyType;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.css.validate.CssDeclarationValidationMaster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link IShorthandResolver} implementation for backgrounds.
 */
public class BackgroundShorthandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundShorthandResolver.class);

    // With CSS3, you can apply multiple backgrounds to elements. These are layered atop one another
    // with the first background you provide on top and the last background listed in the back. Only
    // the last background can include a background color.

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver#resolveShorthand(java.lang.String)
     */
    @Override
    public List<CssDeclaration> resolveShorthand(final String shorthandExpression) {
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.BACKGROUND_COLOR, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_ATTACHMENT, shorthandExpression)
            );
        }
        if (shorthandExpression.trim().isEmpty()) {
            LOGGER.warn(MessageFormatUtil.format(
                    LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, CommonCssConstants.BACKGROUND));
            return new ArrayList<>();
        }

        final List<List<String>> propsList = CssUtils.extractShorthandProperties(shorthandExpression);

        final Map<CssBackgroundUtils.BackgroundPropertyType, String> resolvedProps = new HashMap<>();
        fillMapWithPropertiesTypes(resolvedProps);
        for (final List<String> props : propsList) {
            if (!processProperties(props, resolvedProps)) {
                return new ArrayList<>();
            }
        }
        if (resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_COLOR) == null) {
            resolvedProps.put(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_COLOR,
                    CommonCssConstants.TRANSPARENT);
        }
        if (!checkProperties(resolvedProps)) {
            return new ArrayList<>();
        }

        return Arrays.asList(
                new CssDeclaration(CssBackgroundUtils.getBackgroundPropertyNameFromType(
                        CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_COLOR),
                        resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_COLOR)),
                new CssDeclaration(CssBackgroundUtils.getBackgroundPropertyNameFromType(
                        CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE),
                        resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE)),
                new CssDeclaration(CssBackgroundUtils.getBackgroundPropertyNameFromType(
                        CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION),
                        resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION)),
                new CssDeclaration(CssBackgroundUtils.getBackgroundPropertyNameFromType(
                        CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE),
                        resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE)),
                new CssDeclaration(CssBackgroundUtils.getBackgroundPropertyNameFromType(
                        CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_REPEAT),
                        resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_REPEAT)),
                new CssDeclaration(CssBackgroundUtils.getBackgroundPropertyNameFromType(
                        CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ORIGIN),
                        resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ORIGIN)),
                new CssDeclaration(CssBackgroundUtils.getBackgroundPropertyNameFromType(
                        CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_CLIP),
                        resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_CLIP)),
                new CssDeclaration(CssBackgroundUtils.getBackgroundPropertyNameFromType(
                        CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ATTACHMENT),
                        resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ATTACHMENT))
        );
    }

    private static boolean checkProperties(Map<CssBackgroundUtils.BackgroundPropertyType, String> resolvedProps) {
        for (final Map.Entry<CssBackgroundUtils.BackgroundPropertyType, String> property : resolvedProps.entrySet()) {
            if (!CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(
                    CssBackgroundUtils.getBackgroundPropertyNameFromType(property.getKey()), property.getValue()))) {
                LOGGER.warn(MessageFormatUtil.format(
                        LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, property.getValue()));
                return false;
            }
            final IShorthandResolver resolver = ShorthandResolverFactory
                    .getShorthandResolver(CssBackgroundUtils.getBackgroundPropertyNameFromType(property.getKey()));
            if (resolver != null && resolver.resolveShorthand(property.getValue()).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static void removeSpacesAroundSlash(final List<String> props) {
        for (int i = 0; i < props.size(); ++i) {
            if ("/".equals(props.get(i))) {
                if (i != 0 && i != props.size() - 1) {
                    final String property = props.get(i - 1) + props.get(i) + props.get(i + 1);
                    props.set(i + 1, property);
                    props.remove(i);
                    props.remove(i - 1);
                }
                return;
            }
            if (props.get(i).startsWith("/")) {
                if (i != 0) {
                    final String property = props.get(i - 1) + props.get(i);
                    props.set(i, property);
                    props.remove(i - 1);
                }
                return;
            }
            if (props.get(i).endsWith("/")) {
                if (i != props.size() - 1) {
                    final String property = props.get(i) + props.get(i + 1);
                    props.set(i + 1, property);
                    props.remove(i);
                }
                return;
            }
        }
    }

    private static void fillMapWithPropertiesTypes(
            Map<CssBackgroundUtils.BackgroundPropertyType, String> resolvedProps) {
        resolvedProps.put(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_COLOR, null);
        resolvedProps.put(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE, null);
        resolvedProps.put(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION, null);
        resolvedProps.put(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE, null);
        resolvedProps.put(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_REPEAT, null);
        resolvedProps.put(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ORIGIN, null);
        resolvedProps.put(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_CLIP, null);
        resolvedProps.put(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ATTACHMENT, null);
    }

    private static boolean processProperties(List<String> props,
                                             Map<CssBackgroundUtils.BackgroundPropertyType, String> resolvedProps) {
        if (props.isEmpty()) {
            LOGGER.warn(MessageFormatUtil.format(
                    LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, CommonCssConstants.BACKGROUND));
            return false;
        }
        if (resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_COLOR) != null) {
            LOGGER.warn(LogMessageConstant.ONLY_THE_LAST_BACKGROUND_CAN_INCLUDE_BACKGROUND_COLOR);
            return false;
        }
        removeSpacesAroundSlash(props);
        final Set<CssBackgroundUtils.BackgroundPropertyType> usedTypes = new HashSet<>();
        if (processAllSpecifiedProperties(props, resolvedProps, usedTypes)) {
            fillNotProcessedProperties(resolvedProps, usedTypes);
            return true;
        } else {
            return false;
        }
    }

    private static boolean processAllSpecifiedProperties(List<String> props,
            Map<CssBackgroundUtils.BackgroundPropertyType, String> resolvedProps,
            Set<CssBackgroundUtils.BackgroundPropertyType> usedTypes) {
        final List<String> boxValues = new ArrayList<>();
        boolean slashEncountered = false;
        boolean propertyProcessedCorrectly = true;
        for (final String value : props) {
            final int slashCharInd = value.indexOf('/');
            if (slashCharInd > 0 && slashCharInd < value.length() - 1 && !slashEncountered && !value.contains("url(")) {
                slashEncountered = true;
                propertyProcessedCorrectly = processValueWithSlash(value, slashCharInd, resolvedProps, usedTypes);
            } else {
                final BackgroundPropertyType type = CssBackgroundUtils.resolveBackgroundPropertyType(value);
                if (BackgroundPropertyType.BACKGROUND_ORIGIN_OR_CLIP == type) {
                    boxValues.add(value);
                } else {
                    propertyProcessedCorrectly = putPropertyBasedOnType(changePropertyType(type, slashEncountered),
                            value, resolvedProps, usedTypes);
                }
            }
            if (!propertyProcessedCorrectly) {
                return false;
            }
        }
        return addBackgroundClipAndBackgroundOriginBoxValues(boxValues, resolvedProps, usedTypes);
    }

    private static boolean addBackgroundClipAndBackgroundOriginBoxValues(List<String> boxValues,
            Map<BackgroundPropertyType, String> resolvedProps,
            Set<BackgroundPropertyType> usedTypes) {
        if (boxValues.size() == 1) {
            return putPropertyBasedOnType(BackgroundPropertyType.BACKGROUND_CLIP,
                    boxValues.get(0), resolvedProps, usedTypes);
        } else if (boxValues.size() >= 2) {
            for (int i = 0; i < 2; i++) {
                final BackgroundPropertyType type =
                        i == 0 ? BackgroundPropertyType.BACKGROUND_ORIGIN : BackgroundPropertyType.BACKGROUND_CLIP;
                if (!putPropertyBasedOnType(type, boxValues.get(i), resolvedProps, usedTypes)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean processValueWithSlash(String value, int slashCharInd,
                                                 Map<CssBackgroundUtils.BackgroundPropertyType, String> resolvedProps,
                                                 Set<CssBackgroundUtils.BackgroundPropertyType> usedTypes) {
        final String value1 = value.substring(0, slashCharInd);
        final CssBackgroundUtils.BackgroundPropertyType typeBeforeSlash =
                changePropertyType(CssBackgroundUtils.resolveBackgroundPropertyType(value1), false);
        if (typeBeforeSlash != CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION &&
                typeBeforeSlash != CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_OR_SIZE) {
            LOGGER.warn(MessageFormatUtil.format(LogMessageConstant.UNKNOWN_PROPERTY,
                    CommonCssConstants.BACKGROUND_POSITION, value1));
            return false;
        }

        final String value2 = value.substring(slashCharInd + 1);
        final CssBackgroundUtils.BackgroundPropertyType typeAfterSlash =
                changePropertyType(CssBackgroundUtils.resolveBackgroundPropertyType(value2), true);
        if (typeAfterSlash != CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE &&
                typeAfterSlash != CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_OR_SIZE) {
            LOGGER.warn(MessageFormatUtil.format(LogMessageConstant.UNKNOWN_PROPERTY,
                    CommonCssConstants.BACKGROUND_SIZE, value2));
            return false;
        }

        return putPropertyBasedOnType(typeBeforeSlash, value1, resolvedProps, usedTypes) &&
                putPropertyBasedOnType(typeAfterSlash, value2, resolvedProps, usedTypes);
    }

    private static void fillNotProcessedProperties(Map<CssBackgroundUtils.BackgroundPropertyType, String> resolvedProps,
                                                   final Set<CssBackgroundUtils.BackgroundPropertyType> usedTypes) {
        for (final CssBackgroundUtils.BackgroundPropertyType type : new ArrayList<>(resolvedProps.keySet())) {
            if (!usedTypes.contains(type) && type != CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_COLOR) {
                if (resolvedProps.get(type) == null) {
                    resolvedProps.put(type,
                            CssDefaults.getDefaultValue(CssBackgroundUtils.getBackgroundPropertyNameFromType(type)));
                } else {
                    resolvedProps.put(type, resolvedProps.get(type) + "," +
                            CssDefaults.getDefaultValue(CssBackgroundUtils.getBackgroundPropertyNameFromType(type)));
                }
            }
        }
    }

    private static CssBackgroundUtils.BackgroundPropertyType changePropertyType(
            CssBackgroundUtils.BackgroundPropertyType propertyType,
            boolean slashEncountered) {
        if (propertyType == CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_X
                || propertyType == CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_Y) {
            propertyType = CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION;
        }
        if (propertyType == CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_OR_SIZE) {
            return slashEncountered ? CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE :
                    CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION;
        }
        if (propertyType == CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE && !slashEncountered) {
            return CssBackgroundUtils.BackgroundPropertyType.UNDEFINED;
        }
        if (propertyType == CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION && slashEncountered) {
            return CssBackgroundUtils.BackgroundPropertyType.UNDEFINED;
        }
        return propertyType;
    }

    /**
     * Registers a property based on its type.
     *
     * @param type          the property type
     * @param value         the property value
     * @param resolvedProps the resolved properties
     * @param usedTypes     already used types
     * @return false if the property is invalid. True in all other cases
     */
    private static boolean putPropertyBasedOnType(CssBackgroundUtils.BackgroundPropertyType type, String value,
                                                  Map<CssBackgroundUtils.BackgroundPropertyType, String> resolvedProps,
                                                  Set<CssBackgroundUtils.BackgroundPropertyType> usedTypes) {
        if (type == CssBackgroundUtils.BackgroundPropertyType.UNDEFINED) {
            LOGGER.warn(MessageFormatUtil.format(
                    LogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES, value));
            return false;
        }

        if (resolvedProps.get(type) == null) {
            resolvedProps.put(type, value);
        } else if (usedTypes.contains(type)) {
            resolvedProps.put(type, resolvedProps.get(type) + " " + value);
        } else {
            resolvedProps.put(type, resolvedProps.get(type) + "," + value);
        }
        usedTypes.add(type);
        return true;
    }
}
