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
package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
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
                    StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, CommonCssConstants.BACKGROUND));
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
                        StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, property.getValue()));
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
                    StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, CommonCssConstants.BACKGROUND));
            return false;
        }
        if (resolvedProps.get(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_COLOR) != null) {
            LOGGER.warn(StyledXmlParserLogMessageConstant.ONLY_THE_LAST_BACKGROUND_CAN_INCLUDE_BACKGROUND_COLOR);
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
            if (slashCharInd > 0 && slashCharInd < value.length() - 1 && !slashEncountered && !value.contains("url(")
                    && !value.contains("device-cmyk(")) {
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
            LOGGER.warn(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY,
                    CommonCssConstants.BACKGROUND_POSITION, value1));
            return false;
        }

        final String value2 = value.substring(slashCharInd + 1);
        final CssBackgroundUtils.BackgroundPropertyType typeAfterSlash =
                changePropertyType(CssBackgroundUtils.resolveBackgroundPropertyType(value2), true);
        if (typeAfterSlash != CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE &&
                typeAfterSlash != CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_OR_SIZE) {
            LOGGER.warn(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY,
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
                    StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES, value));
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
