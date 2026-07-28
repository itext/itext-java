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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.utils.objectpathitems.ObjectPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class containing results of the comparison of two pdf documents.
 */
public final class CompareToolResult {
    // LinkedHashMap to retain order. HashMap has different order in Java6/7 and Java8
    private final Map<ObjectPath, String> differences = new LinkedHashMap<>();
    private int messageLimit = 1;

    /**
     * Creates new empty instance of CompareToolResult with given limit of difference messages.
     *
     * @param messageLimit maximum number of difference messages to be handled by this CompareToolResult.
     */
    public CompareToolResult(int messageLimit) {
        this.messageLimit = messageLimit;
    }

    /**
     * Verifies if documents are considered equal after comparison.
     *
     * @return true if documents are equal, false otherwise.
     */
    public boolean isOk() {
        return differences.isEmpty();
    }

    /**
     * Returns number of differences between two documents detected during comparison.
     *
     * @return number of differences.
     */
    public int getErrorCount() {
        return differences.size();
    }

    /**
     * Converts this CompareToolResult into text form.
     *
     * @return text report on the differences between two documents.
     */
    public String getReport() {
        StringBuilder sb = new StringBuilder();
        boolean firstEntry = true;
        for (Map.Entry<ObjectPath, String> entry : differences.entrySet()) {
            if (!firstEntry)
                sb.append("-----------------------------").append("\n");
            ObjectPath diffPath = entry.getKey();
            sb.append(entry.getValue()).append("\n").append(diffPath.toString()).append("\n");
            firstEntry = false;
        }
        return sb.toString();
    }

    /**
     * Returns map with {@link ObjectPath} as keys and difference descriptions as values.
     *
     * @return differences map which could be used to find in the document the objects that are different.
     */
    public Map<ObjectPath, String> getDifferences() {
        return differences;
    }

    /**
     * Converts this CompareToolResult into xml form.
     *
     * @param stream output stream to which xml report will be written.
     * @throws ParserConfigurationException if a XML DocumentBuilder cannot be created
     *                                      which satisfies the configuration requested.
     * @throws TransformerException         if it is not possible to create an XML Transformer instance or
     *                                      an unrecoverable error occurs during the course of the transformation.
     */
    public void writeReportToXml(OutputStream stream) throws ParserConfigurationException, TransformerException {
        final Document xmlReport = XmlUtils.initNewXmlDocument();
        Element root = xmlReport.createElement("report");
        Element errors = xmlReport.createElement("errors");
        errors.setAttribute("count", String.valueOf(differences.size()));
        root.appendChild(errors);
        for (Map.Entry<ObjectPath, String> entry : differences.entrySet()) {
            Node errorNode = xmlReport.createElement("error");
            Node message = xmlReport.createElement("message");
            message.appendChild(xmlReport.createTextNode(entry.getValue()));
            Node path = entry.getKey().toXmlNode(xmlReport);
            errorNode.appendChild(message);
            errorNode.appendChild(path);
            errors.appendChild(errorNode);
        }
        xmlReport.appendChild(root);

        XmlUtils.writeXmlDocToStream(xmlReport, stream);
    }

    /**
     * Checks whether maximum number of difference messages to be handled by this CompareToolResult is reached.
     *
     * @return true if limit of difference messages is reached, false otherwise.
     */
    boolean isMessageLimitReached() {
        return differences.size() >= messageLimit;
    }


    /**
     * Returns set limit of difference messages.
     *
     * @return message limit.
     */
    int getMessageLimit() {
        return messageLimit;
    }

    /**
     * Adds an error message for the {@link ObjectPath}.
     *
     * @param path    {@link ObjectPath} for the two corresponding objects in the compared documents
     * @param message an error message
     */
    void addError(ObjectPath path, String message) {
        if (differences.size() < messageLimit) {
            differences.put(new ObjectPath(path), message);
        }
    }
}