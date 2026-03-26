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
package com.itextpdf.signatures;

import com.itextpdf.commons.actions.AbstractITextConfigurationEvent;
import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.producer.ProducerBuilder;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for providing signature creator for PdfSignaer class.
 */
final class GetSignatureCreatorEvent extends AbstractITextConfigurationEvent {

    private final WeakReference<PdfDocument> document;

    private String signatureCreator;

    /**
     * Creates a new instance of the GetSignatureCreatorEvent.
     *
     * @param document document in which the signature creator is required
     */
    GetSignatureCreatorEvent(PdfDocument document) {
        super();
        this.document = new WeakReference<>(document);
    }

    /**
     * Provides signature creator string, which can be accessed via {@link GetSignatureCreatorEvent#getSignatureCreator()}.
     *
     */
    @Override
    public void doAction() {
        final PdfDocument pdfDocument = (PdfDocument) document.get();
        if (pdfDocument == null) {
            return;
        }

        List<AbstractProductProcessITextEvent> events = getEvents(pdfDocument.getDocumentIdWrapper());
        if (events == null || events.isEmpty()) {
            signatureCreator = "";
            return;
        }

        String coreProductName = ITextCoreProductData.getInstance().getProductName();
        AbstractProductProcessITextEvent coreEvent = null;
        for (AbstractProductProcessITextEvent event : events) {
            if (coreProductName.equals(event.getProductName())) {
                coreEvent = event;
                break;
            }
        }

        if (coreEvent == null) {
            signatureCreator = ProducerBuilder.modifyProducer(Collections.singletonList(events.get(0)), null);
        } else {
            signatureCreator = ProducerBuilder.modifyProducer(Collections.singletonList(coreEvent), null);
        }
    }

    /**
     * Gets signature creator.
     *
     * @return String with a signature creator
     */
    String getSignatureCreator() {
        return signatureCreator;
    }
}
