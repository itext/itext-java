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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;

import java.io.InputStream;

/**
 * Specify the colour characteristics of output devices on which the document might be rendered
 * See ISO 32000-1 14.11.5: Output Intents.
 */
public class PdfOutputIntent extends PdfObjectWrapper<PdfDictionary> {


    /**
     * Creates output intent dictionary. Null values are allowed to
     * suppress any key.
     * By default output intent subtype is GTS_PDFA1, use setter to change it.
     *
     * @param outputConditionIdentifier (required) identifying the intended output device or production condition in
     *                                  human or machine readable form
     * @param outputCondition           (optional) identifying the intended output device or production
     *                                  condition in human-readable form
     * @param registryName              identifying the registry in which the condition designated by
     *                                  {@code outputConditionIdentifier} is defined
     * @param info                      (required if {@code outputConditionIdentifier} does not specify a standard
     *                                  production condition; optional otherwise) containing additional information or
     *                                  comments about the intended target device or production condition
     * @param iccStream                 ICC profile stream. User is responsible for closing the stream
     */
    public PdfOutputIntent(String outputConditionIdentifier, String outputCondition, String registryName, String info, InputStream iccStream) {
        super(new PdfDictionary());
        setOutputIntentSubtype(PdfName.GTS_PDFA1);
        getPdfObject().put(PdfName.Type, PdfName.OutputIntent);
        if (outputCondition != null)
            setOutputCondition(outputCondition);
        if (outputConditionIdentifier != null)
            setOutputConditionIdentifier(outputConditionIdentifier);
        if (registryName != null)
            setRegistryName(registryName);
        if (info != null)
            setInfo(info);
        if (iccStream != null) {
            setDestOutputProfile(iccStream);
        }
    }

    public PdfOutputIntent(PdfDictionary outputIntentDict) {
        super(outputIntentDict);
    }

    public PdfStream getDestOutputProfile() {
        return getPdfObject().getAsStream(PdfName.DestOutputProfile);
    }

    public void setDestOutputProfile(InputStream iccStream) {
        PdfStream stream = PdfCieBasedCs.IccBased.getIccProfileStream(iccStream);
        getPdfObject().put(PdfName.DestOutputProfile, stream);
    }

    public PdfString getInfo() {
        return getPdfObject().getAsString(PdfName.Info);
    }

    public void setInfo(String info) {
        getPdfObject().put(PdfName.Info, new PdfString(info));
    }

    public PdfString getRegistryName() {
        return getPdfObject().getAsString(PdfName.RegistryName);
    }

    public void setRegistryName(String registryName) {
        getPdfObject().put(PdfName.RegistryName, new PdfString(registryName));
    }

    public PdfString getOutputConditionIdentifier() {
        return getPdfObject().getAsString(PdfName.OutputConditionIdentifier);
    }

    public void setOutputConditionIdentifier(String outputConditionIdentifier) {
        getPdfObject().put(PdfName.OutputConditionIdentifier, new PdfString(outputConditionIdentifier));
    }

    public PdfString getOutputCondition() {
        return getPdfObject().getAsString(PdfName.OutputCondition);
    }

    public void setOutputCondition(String outputCondition) {
        getPdfObject().put(PdfName.OutputCondition, new PdfString(outputCondition));
    }

    public PdfName getOutputIntentSubtype() {
        return getPdfObject().getAsName(PdfName.S);
    }

    public void setOutputIntentSubtype(PdfName subtype) {
        getPdfObject().put(PdfName.S, subtype);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

}
