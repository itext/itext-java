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
package com.itextpdf.commons.actions.producer;

import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class is used to populate <code>usedProducts</code> placeholder. Placeholder should be configured
 * with parameter defining the format of output. Within format strings, unquoted letters from
 * <code>A</code> to <code>Z</code> and from <code>a</code> to <code>z</code> are process as pattern
 * letters representing appropriate component of <code>usedProducts</code> format. There are three
 * letters which are allowed in the outputformat:
 *
 * <p>
 * <ul>
 *     <li><code>P</code> stands for product name
 *     <li><code>V</code> stands for version of the product
 *     <li><code>T</code> is for usage type of the product
 * </ul>
 *
 * <p>
 * Text can be quoted using single quotes (') to avoid interpretation. All other characters are not
 * interpreted and just copied into the output string. String may contain escaped apostrophes
 * <code>\'</code> which processed as characters. Backslash is used for escaping so you need double
 * backslash to print it <code>\\</code>. All the rest backslashes (not followed by apostrophe or
 * one more backslash) are simply ignored.
 *
 * <p>
 * The result of the processing is the list of all products mentioned among events as a
 * comma-separated list. The order of the elements is defined by the order of products mentioning in
 * the <code>events</code>. Equal strings are skipped even if they were generated for different
 * products (i. e. format <code>P</code> stands for product name only: if several version of the
 * same product are used, it will be the only mentioning of that product).
 */
class UsedProductsPlaceholderPopulator extends AbstractFormattedPlaceholderPopulator {
    private static final char PRODUCT_NAME = 'P';
    private static final char VERSION = 'V';
    private static final char USAGE_TYPE = 'T';
    private static final String PRODUCTS_SEPARATOR = ", ";

    public UsedProductsPlaceholderPopulator() {
        // Empty constructor.
    }

    /**
     * Builds a replacement for a placeholder <code>usedProducts</code> in accordance with the
     * registered events and provided format.
     *
     * @param events is a list of event involved into document processing
     * @param parameter defines output format in accordance with the for description
     *
     * @return populated comma-separated list of used products in accordance with the format
     *
     * @throws IllegalArgumentException if format of the pattern is invalid
     */
    @Override
    public String populate(List<ConfirmedEventWrapper> events, String parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException(MessageFormatUtil.format(
                    CommonsExceptionMessageConstant.INVALID_USAGE_FORMAT_REQUIRED, "usedProducts"));
        }

        final Set<ProductRepresentation> usedProducts = new LinkedHashSet<>();
        for (ConfirmedEventWrapper event : events) {
            usedProducts.add(new ProductRepresentation(event));
        }

        final Set<String> usedProductsRepresentations = new LinkedHashSet<>();
        for (ProductRepresentation representation : usedProducts) {
            usedProductsRepresentations.add(formatProduct(representation, parameter));
        }
        final StringBuilder result = new StringBuilder();
        for (String stringRepresentation : usedProductsRepresentations) {
            if (result.length() > 0) {
                result.append(PRODUCTS_SEPARATOR);
            }

            result.append(stringRepresentation);
        }

        return result.toString();
    }

    private String formatProduct(ProductRepresentation product, String format) {
        final StringBuilder builder = new StringBuilder();
        char[] formatArray = format.toCharArray();

        for (int i = 0; i < formatArray.length; i++) {
            if (formatArray[i] == APOSTROPHE) {
                i = attachQuotedString(i, builder, formatArray);
            } else if (isLetter(formatArray[i])) {
                builder.append(formatLetter(formatArray[i], product));
            } else {
                builder.append(formatArray[i]);
            }
        }

        return builder.toString();
    }

    private String formatLetter(char letter, ProductRepresentation product) {
        if (letter == PRODUCT_NAME) {
            return product.getProductName();
        } else if (letter == VERSION) {
            return product.getVersion();
        } else if (letter == USAGE_TYPE) {
            return product.getProductUsageType();
        } else {
            throw new IllegalArgumentException(MessageFormatUtil.format(
                    CommonsExceptionMessageConstant.PATTERN_CONTAINS_UNEXPECTED_CHARACTER, letter));
        }
    }

    private static class ProductRepresentation {
        private static final Map<String, String> PRODUCT_USAGE_TYPE_TO_HUMAN_READABLE_FORM;

        private final String productName;
        private final String productUsageType;
        private final String version;

        static {
            Map<String, String> productUsageTypeMapping = new HashMap<>();
            productUsageTypeMapping.put("nonproduction", "non-production");
            PRODUCT_USAGE_TYPE_TO_HUMAN_READABLE_FORM = Collections.unmodifiableMap(productUsageTypeMapping);
        }

        public ProductRepresentation(ConfirmedEventWrapper event) {
            productName = event.getEvent().getProductData().getPublicProductName();
            if (PRODUCT_USAGE_TYPE_TO_HUMAN_READABLE_FORM.containsKey(event.getProductUsageType())) {
                productUsageType = PRODUCT_USAGE_TYPE_TO_HUMAN_READABLE_FORM.get(event.getProductUsageType());
            } else {
                productUsageType = event.getProductUsageType();
            }
            version = event.getEvent().getProductData().getVersion();
        }

        public String getProductName() {
            return productName;
        }

        public String getProductUsageType() {
            return productUsageType;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ProductRepresentation that = (ProductRepresentation) o;

            if (getProductName() == null ?
                    that.getProductName() != null : !getProductName().equals(that.getProductName())) {
                return false;
            }
            if (getProductUsageType() == null ?
                    that.getProductUsageType() != null : !getProductUsageType().equals(that.getProductUsageType())) {
                return false;
            }
            return getVersion() == null ? that.getVersion() == null : getVersion().equals(that.getVersion());
        }

        @Override
        public int hashCode() {
            int result = getProductName() == null ? 0 : getProductName().hashCode();
            result = 31 * result + (getProductUsageType() == null ? 0 : getProductUsageType().hashCode());
            result = 31 * result + (getVersion() == null ? 0 : getVersion().hashCode());
            return result;
        }
    }
}
