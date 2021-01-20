/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.actions.processors;

import com.itextpdf.kernel.actions.events.AbstractITextProductEvent;
import com.itextpdf.kernel.actions.session.ClosingSession;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a default strategy of product event processing.
 */
public class DefaultITextProductEventProcessor implements ITextProductEventProcessor {
    // TODO: DEVSIX-5054 should be removed when new producer line building logic is implemented
    private final static String OLD_MECHANISM_PRODUCER_LINE_WAS_SET = "old-mechanism-producer-line-was-set";

    private final String productName;

    /**
     * Creates an instance of product event processor.
     *
     * @param productName is a product name
     */
    public DefaultITextProductEventProcessor(String productName) {
        this.productName = productName;
    }

    /**
     * {@inheritDoc}
     *
     * @param event to handle
     */
    @Override
    public void onEvent(AbstractITextProductEvent event) {
        // TODO: DEVSIX-4964 provide appropriate logic if any
    }

    /**
     * Collects info about products involved into document processing.
     *
     * @param closingSession is a closing session
     */
    @Override
    public void aggregationOnClose(ClosingSession closingSession) {
        if (closingSession.getProducer() == null) {
            closingSession.setProducer(new ArrayList<>());
        }
        closingSession.getProducer().add(getProducer());
    }

    /**
     * Updates meta info of the document.
     *
     * @param closingSession is a closing session
     */
    @Override
    public void completionOnClose(ClosingSession closingSession) {
        if (closingSession.getProducer() != null) {
            final List<String> lines = closingSession.getProducer();
            updateProducerLine(closingSession.getDocument(), lines);
            closingSession.setProducer(null);
        }

        //TODO: DEVSIX-5054 code below should be removed when new producer line building logic is implemented
        if (closingSession.getProperty(OLD_MECHANISM_PRODUCER_LINE_WAS_SET) == null) {
            if (closingSession.getDocument() != null) {
                closingSession.getDocument().updateProducerInInfoDictionary();
            }
            closingSession.setProperty(OLD_MECHANISM_PRODUCER_LINE_WAS_SET, Boolean.TRUE);
        }
    }

    /**
     * Gets a label which defines a product.
     *
     * @return a product label
     */
    protected String getProducer() {
        // TODO: DEVSIX-5054: probably productName + "(AGPL)"
        return productName;
    }

    private static void updateProducerLine(PdfDocument document, List<String> elements) {
        // TODO: DEVSIX-5054
    }
}
