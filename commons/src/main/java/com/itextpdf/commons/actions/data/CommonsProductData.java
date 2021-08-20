package com.itextpdf.commons.actions.data;

/**
 * Stores an instance of {@link ProductData} related to iText commons module.
 */
public class CommonsProductData {
    private static final String COMMONS_PUBLIC_PRODUCT_NAME = "Commons";
    private static final String COMMONS_PRODUCT_NAME = "commons";
    private static final String COMMONS_VERSION = "7.2.0-SNAPSHOT";
    private static final int COMMONS_COPYRIGHT_SINCE = 2000;
    private static final int COMMONS_COPYRIGHT_TO = 2021;

    private static final ProductData COMMONS_PRODUCT_DATA = new ProductData(COMMONS_PUBLIC_PRODUCT_NAME,
            COMMONS_PRODUCT_NAME, COMMONS_VERSION, COMMONS_COPYRIGHT_SINCE, COMMONS_COPYRIGHT_TO);

    /**
     * Getter for an instance of {@link ProductData} related to iText commons module.
     *
     * @return iText commons product description
     */
    public static ProductData getInstance() {
        return COMMONS_PRODUCT_DATA;
    }
}
