/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.pdfa;

/**
 * Utilities to construct an XMP for a PDF/A file.
 */
public class PdfAXMPUtil {

    public static final String PDF_UA_EXTENSION =
            "    <x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n" +
                    "      <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                    "        <rdf:Description rdf:about=\"\" xmlns:pdfaExtension=\"http://www.aiim.org/pdfa/ns/extension/\" xmlns:pdfaSchema=\"http://www.aiim.org/pdfa/ns/schema#\" xmlns:pdfaProperty=\"http://www.aiim.org/pdfa/ns/property#\">\n" +
                    "          <pdfaExtension:schemas>\n" +
                    "            <rdf:Bag>\n" +
                    "              <rdf:li rdf:parseType=\"Resource\">\n" +
                    "                <pdfaSchema:namespaceURI>http://www.aiim.org/pdfua/ns/id/</pdfaSchema:namespaceURI>\n" +
                    "                <pdfaSchema:prefix>pdfuaid</pdfaSchema:prefix>\n" +
                    "                <pdfaSchema:schema>PDF/UA identification schema</pdfaSchema:schema>\n" +
                    "                <pdfaSchema:property>\n" +
                    "                  <rdf:Seq>\n" +
                    "                    <rdf:li rdf:parseType=\"Resource\">\n" +
                    "                      <pdfaProperty:category>internal</pdfaProperty:category>\n" +
                    "                      <pdfaProperty:description>PDF/UA version identifier</pdfaProperty:description>\n" +
                    "                      <pdfaProperty:name>part</pdfaProperty:name>\n" +
                    "                      <pdfaProperty:valueType>Integer</pdfaProperty:valueType>\n" +
                    "                    </rdf:li>\n" +
                    "                    <rdf:li rdf:parseType=\"Resource\">\n" +
                    "                      <pdfaProperty:category>internal</pdfaProperty:category>\n" +
                    "                      <pdfaProperty:description>PDF/UA amendment identifier</pdfaProperty:description>\n" +
                    "                      <pdfaProperty:name>amd</pdfaProperty:name>\n" +
                    "                      <pdfaProperty:valueType>Text</pdfaProperty:valueType>\n" +
                    "                    </rdf:li>\n" +
                    "                    <rdf:li rdf:parseType=\"Resource\">\n" +
                    "                      <pdfaProperty:category>internal</pdfaProperty:category>\n" +
                    "                      <pdfaProperty:description>PDF/UA corrigenda identifier</pdfaProperty:description>\n" +
                    "                      <pdfaProperty:name>corr</pdfaProperty:name>\n" +
                    "                      <pdfaProperty:valueType>Text</pdfaProperty:valueType>\n" +
                    "                    </rdf:li>\n" +
                    "                  </rdf:Seq>\n" +
                    "                </pdfaSchema:property>\n" +
                    "              </rdf:li>\n" +
                    "            </rdf:Bag>\n" +
                    "          </pdfaExtension:schemas>\n" +
                    "        </rdf:Description>\n" +
                    "      </rdf:RDF>\n" +
                    "    </x:xmpmeta>";
}
