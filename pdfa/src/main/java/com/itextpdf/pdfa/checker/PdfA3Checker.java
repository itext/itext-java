/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * PdfA3Checker defines the requirements of the PDF/A-3 standard and contains a
 * number of methods that override the implementations of its superclass
 * {@link PdfA2Checker}.
 * 
 * The specification implemented by this class is ISO 19005-3
 */
public class PdfA3Checker extends PdfA2Checker{
    protected static final Set<PdfName> allowedAFRelationships = new HashSet<>(Arrays.asList(
            PdfName.Source, PdfName.Data, PdfName.Alternative,
            PdfName.Supplement, PdfName.Unspecified));
    private static final long serialVersionUID = 6280825718658124941L;

    /**
     * Creates a PdfA3Checker with the required conformance level
     * 
     * @param conformanceLevel the required conformance level, <code>a</code> or
     * <code>u</code> or <code>b</code>
     */
    public PdfA3Checker(PdfAConformanceLevel conformanceLevel) {
        super(conformanceLevel);
    }

    @Override
    protected void checkFileSpec(PdfDictionary fileSpec) {
        PdfName relationship = fileSpec.getAsName(PdfName.AFRelationship);
        if (relationship == null || !allowedAFRelationships.contains(relationship)) {
            throw new PdfAConformanceException(PdfAConformanceException.FileSpecificationDictionaryShallContainOneOfThePredefinedAFRelationshipKeys);
        }

        if (fileSpec.containsKey(PdfName.EF)) {
            if (!fileSpec.containsKey(PdfName.F) || !fileSpec.containsKey(PdfName.UF) || !fileSpec.containsKey(PdfName.Desc)) {
                throw new PdfAConformanceException(PdfAConformanceException.FileSpecificationDictionaryShallContainFKeyUFKeyAndDescKey);
            }


            PdfDictionary ef = fileSpec.getAsDictionary(PdfName.EF);
            PdfStream embeddedFile = ef.getAsStream(PdfName.F);
            if (embeddedFile == null) {
                throw new PdfAConformanceException(PdfAConformanceException.EFKeyOfFileSpecificationDictionaryShallContainDictionaryWithValidFKey);
            }

            if (!embeddedFile.containsKey(PdfName.Subtype)) {
                throw new PdfAConformanceException(PdfAConformanceException.MimeTypeShallBeSpecifiedUsingTheSubtypeKeyOfTheFileSpecificationStreamDictionary);
            }

            PdfDictionary params = embeddedFile.getAsDictionary(PdfName.Params);
            if (params == null) {
                throw new PdfAConformanceException(PdfAConformanceException.EmbeddedFileShallContainParamsKeyWithDictionaryAsValue);
            }

            if (params.getAsString(PdfName.ModDate) == null) {
                throw new PdfAConformanceException(PdfAConformanceException.EmbeddedFileShallContainParamsKeyWithValidModdateKey);
            }
        }
    }
}
