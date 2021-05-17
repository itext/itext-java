package com.itextpdf.kernel.actions;

import com.itextpdf.kernel.actions.data.ProductData;

/**
 * Abstract class which defines general product events by encapsulating
 * {@link ProductData} of the product which generated event. Only for internal usage.
 */
public abstract class AbstractProductITextEvent extends AbstractITextEvent {
    private final ProductData productData;

    /**
     * Creates instance of abstract product iText event based
     * on passed product data. Only for internal usage.
     *
     * @param productData is a description of the product which has generated an event
     */
    public AbstractProductITextEvent(ProductData productData) {
        super();
        if (productData == null) {
            // IllegalStateException is thrown because AbstractProductITextEvent for internal usage
            throw new IllegalStateException("ProductData shouldn't be null.");
        }
        this.productData = productData;
    }

    /**
     * Gets a product data which generated the event.
     *
     * @return information about the product
     */
    public ProductData getProductData() {
        return productData;
    }

    /**
     * Gets a name of product which generated the event.
     *
     * @return product name
     */
    public String getProductName() {
        return productData.getModuleName();
    }
}
