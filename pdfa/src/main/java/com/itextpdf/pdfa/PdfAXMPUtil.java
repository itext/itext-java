/*
 *
 * This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
 * Authors: Bruno Lowagie, et al.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
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
