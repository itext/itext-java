package com.itextpdf.kernel.actions.events;

import com.itextpdf.commons.actions.AbstractITextConfigurationEvent;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.actions.processors.ITextProductEventProcessor;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.FingerPrint;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.Collection;

/**
 * This class is responsible for adding a fingerprint.
 */
public final class AddFingerPrintEvent extends AbstractITextConfigurationEvent {

    private final WeakReference<PdfDocument> document;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFingerPrintEvent.class);

    private static final String AGPL_MODE = "AGPL";

    /**
     * Creates a new instance of the AddFingerPrintEvent.
     *
     * @param document document in which the fingerprint will be added
     */
    public AddFingerPrintEvent(PdfDocument document) {
        super();
        this.document = new WeakReference<>(document);
    }

    /**
     * Adds fingerprint to the document.
     */
    @Override
    public void doAction() {
        final PdfDocument pdfDocument = (PdfDocument) document.get();
        if (pdfDocument == null) {
            return;
        }

        final FingerPrint fingerPrint = pdfDocument.getFingerPrint();
        final Collection<ProductData> products = fingerPrint.getProducts();
        //if fingerprint is disabled and all licence types isn't AGPL then no actions required
        if (!fingerPrint.isFingerPrintEnabled()) {
            boolean nonAGPLMode = true;
            for (ProductData productData : products) {
                ITextProductEventProcessor processor = getActiveProcessor(productData.getProductName());
                if (processor ==  null){
                    continue;
                }

                if (AGPL_MODE.equals(processor.getUsageType())) {
                    nonAGPLMode = false;
                    break;
                }
            }
            
            if (nonAGPLMode) {
                return;
            }

            LOGGER.warn(KernelLogMessageConstant.FINGERPRINT_DISABLED_BUT_NO_REQUIRED_LICENCE);
        }


        PdfWriter writer = pdfDocument.getWriter();
        if (products.isEmpty()) {
            writer.writeString(MessageFormatUtil
                    .format("%iText-{0}-no-registered-products\n", ITextCoreProductData.getInstance().getVersion()));
            return;
        }

        for (ProductData productData : products) {
            writer.writeString(MessageFormatUtil
                    .format("%iText-{0}-{1}\n", productData.getPublicProductName(), productData.getVersion()));
        }
    }
}
