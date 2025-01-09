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
package com.itextpdf.kernel.utils;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNameTree;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class which provides functionality to merge ECMA scripts from pdf documents
 */
public class PdfScriptMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfScriptMerger.class);
    private static final Set<PdfName> allowedAAEntries = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.WC,
                    PdfName.WS,
                    PdfName.DS,
                    PdfName.WP)));

    /**
     * Merges ECMA scripts from source to destinations from all possible places for them,
     * it only copies first action in chain for AA and OpenAction entries
     *
     * @param source source document from which script will be copied
     * @param destination destination document to which script will be copied
     */
    public static void mergeScripts(PdfDocument source, PdfDocument destination) {
        mergeOpenActionsScripts(source, destination);
        mergeAdditionalActionsScripts(source, destination);
        mergeNamesScripts(source, destination);
    }

    /**
     * Copies AA catalog entry ECMA scripts, it only copies first action in chain
     *
     * @param source source document from which script will be copied
     * @param destination destination document to which script will be copied
     */
    public static void mergeAdditionalActionsScripts(PdfDocument source, PdfDocument destination) {
        PdfDictionary sourceAA = source.getCatalog().getPdfObject().getAsDictionary(PdfName.AA);
        PdfDictionary destinationAA = destination.getCatalog().getPdfObject().getAsDictionary(PdfName.AA);
        if (sourceAA == null || sourceAA.isEmpty()) {
            return;
        }
        if (destinationAA == null) {
            destinationAA = new PdfDictionary();
            destination.getCatalog().getPdfObject().put(PdfName.AA, destinationAA);
        }
        for (Map.Entry<PdfName, PdfObject> entry : sourceAA.entrySet()) {
            if (destinationAA.containsKey(entry.getKey())) {
                LOGGER.error(MessageFormatUtil.format(KernelLogMessageConstant.CANNOT_MERGE_ENTRY, entry.getKey()));
                return;
            }
            if (!allowedAAEntries.contains(entry.getKey())) {
                continue;
            }
            destinationAA.put(entry.getKey(), copyECMAScriptActionsDictionary(destination, (PdfDictionary) entry.getValue()));
        }
    }

    /**
     * Copies open actions catalog entry ECMA scripts, it only copies first action in chain
     *
     * @param source source document from which script will be copied
     * @param destination destination document to which script will be copied
     */
    public static void mergeOpenActionsScripts(PdfDocument source, PdfDocument destination) {
        PdfObject sourceOpenAction =  source.getCatalog().getPdfObject().get(PdfName.OpenAction);
        if (sourceOpenAction instanceof PdfArray) {
            return;
        }
        PdfDictionary sourceOpenActionDict = source.getCatalog().getPdfObject().getAsDictionary(PdfName.OpenAction);
        if (sourceOpenActionDict == null || sourceOpenActionDict.isEmpty() || !PdfName.JavaScript.equals(sourceOpenActionDict.get(PdfName.S))) {
            return;
        }
        PdfObject destinationOpenAction = destination.getCatalog().getPdfObject().get(PdfName.OpenAction);
        if (destinationOpenAction != null) {
            LOGGER.error(MessageFormatUtil.format(KernelLogMessageConstant.CANNOT_MERGE_ENTRY, PdfName.OpenAction));
            return;
        }
        destination.getCatalog().getPdfObject().put(PdfName.OpenAction, copyECMAScriptActionsDictionary(destination, sourceOpenActionDict));
    }

    /**
     * Copies ECMA scripts from Names catalog entry
     *
     * @param source source document from which script will be copied
     * @param destination destination document to which script will be copied
     */
    public static void mergeNamesScripts(PdfDocument source, PdfDocument destination) {
        PdfDictionary namesDict = source.getCatalog().getPdfObject().getAsDictionary(PdfName.Names);
        if (namesDict == null || !namesDict.containsKey(PdfName.JavaScript)) {
            return;
        }
        PdfDictionary destinationNamesDict = destination.getCatalog().getPdfObject().getAsDictionary(PdfName.Names);
        if ((destinationNamesDict != null && destinationNamesDict.get(PdfName.JavaScript) != null)
                || destination.getCatalog().nameTreeContainsKey(PdfName.JavaScript)) {
            LOGGER.error(MessageFormatUtil.format(KernelLogMessageConstant.CANNOT_MERGE_ENTRY, PdfName.JavaScript));
            return;
        }


        PdfNameTree sourceTree = new PdfNameTree(source.getCatalog(), PdfName.JavaScript);
        PdfNameTree destinationTree = destination.getCatalog().getNameTree(PdfName.JavaScript);
        for (Map.Entry<PdfString, PdfObject> entry : sourceTree.getNames().entrySet()) {
            PdfDictionary ECMAScriptActionsDirectCopy = copyECMAScriptActionsDictionary(destination,
                    entry.getValue().isIndirect() ? (PdfDictionary) entry.getValue().getIndirectReference().getRefersTo()
                            : (PdfDictionary) entry.getValue());
            destinationTree.addEntry(entry.getKey(), ECMAScriptActionsDirectCopy);
        }
    }

    private static PdfDictionary copyECMAScriptActionsDictionary(PdfDocument destination, PdfDictionary actions) {
        PdfObject originalScriptSource = actions.get(PdfName.JS);
        PdfObject scriptType = actions.get(PdfName.S);
        PdfDictionary actionsCopy = new PdfDictionary();
        if (originalScriptSource != null) {
            actionsCopy.put(PdfName.JS, originalScriptSource.copyTo(destination));
        }
        if (scriptType != null) {
            actionsCopy.put(PdfName.S, scriptType.copyTo(destination));
        }
        return actionsCopy;
    }
}
