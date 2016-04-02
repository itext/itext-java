package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.pdfa.PdfAConformanceLevel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PdfA3Checker extends PdfA2Checker{
    protected static final Set<PdfName> allowedAFRelationships = new HashSet<>(Arrays.asList(
            PdfName.Source, PdfName.Data, PdfName.Alternative,
            PdfName.Supplement, PdfName.Unspecified));

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
