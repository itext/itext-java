package com.itextpdf.kernel.pdf;


import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.layer.PdfOCProperties;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfDocumentUnitTest {

    @Test
    public void copyPagesWithOCGDifferentNames() throws IOException {
        List<List<String>> ocgNames = new ArrayList<>();
        List<String> ocgNames1 = new ArrayList<>();
        ocgNames1.add("Name1");
        List<String> ocgNames2 = new ArrayList<>();
        ocgNames2.add("Name2");
        ocgNames.add(ocgNames1);
        ocgNames.add(ocgNames2);
        List<byte[]> sourceDocuments = initSourceDocuments(ocgNames);

        try (PdfDocument outDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            for (byte[] docBytes : sourceDocuments) {
                try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
                    for (int i = 1; i <= fromDocument.getNumberOfPages(); i++) {
                        fromDocument.copyPagesTo(i, i, outDocument);
                    }
                }
            }
            Assert.assertNotNull(outDocument.catalog);
            Assert.assertNotNull(outDocument.catalog.ocProperties);
            Assert.assertEquals(2, outDocument.catalog.ocProperties.getLayers().size());
            PdfLayer layer = outDocument.catalog.ocProperties.getLayers().get(0);
            Assert.assertNotNull(layer);
            assertLayerNameEqual("Name1", layer);
            layer = outDocument.catalog.ocProperties.getLayers().get(1);
            Assert.assertNotNull(layer);
            assertLayerNameEqual("Name2", layer);
        }
    }

    @Test
    public void copyPagesWithOCGSameName() throws IOException {
        List<List<String>> ocgNames = new ArrayList<>();
        List<String> ocgNames1 = new ArrayList<>();
        ocgNames1.add("Name1");
        ocgNames.add(ocgNames1);
        ocgNames.add(ocgNames1);
        ocgNames.add(ocgNames1);
        ocgNames.add(ocgNames1);
        List<byte[]> sourceDocuments = initSourceDocuments(ocgNames);

        try (PdfDocument outDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            for (byte[] docBytes : sourceDocuments) {
                try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
                    for (int i = 1; i <= fromDocument.getNumberOfPages(); i++) {
                        fromDocument.copyPagesTo(i, i, outDocument);
                    }
                }
            }
            Assert.assertNotNull(outDocument.catalog);
            Assert.assertNotNull(outDocument.catalog.ocProperties);
            Assert.assertEquals(4, outDocument.catalog.ocProperties.getLayers().size());
            PdfLayer layer = outDocument.catalog.ocProperties.getLayers().get(0);
            Assert.assertNotNull(layer);
            assertLayerNameEqual("Name1", layer);
            layer = outDocument.catalog.ocProperties.getLayers().get(1);
            Assert.assertNotNull(layer);
            assertLayerNameEqual("Name1_0", layer);
            layer = outDocument.catalog.ocProperties.getLayers().get(2);
            Assert.assertNotNull(layer);
            assertLayerNameEqual("Name1_1", layer);
            layer = outDocument.catalog.ocProperties.getLayers().get(3);
            Assert.assertNotNull(layer);
            assertLayerNameEqual("Name1_2", layer);
        }
    }

    @Test
    public void copyPagesWithOCGSameObject() throws IOException {
        byte[] docBytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = document.addNewPage();
                PdfResources pdfResource = page.getResources();
                PdfDictionary ocg = new PdfDictionary();
                ocg.put(PdfName.Type, PdfName.OCG);
                ocg.put(PdfName.Name, new PdfString("name1"));
                pdfResource.addProperties(ocg);
                PdfPage page2 = document.addNewPage();
                PdfResources pdfResource2 = page2.getResources();
                pdfResource2.addProperties(ocg);
                document.getCatalog().getOCProperties(true);
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument outDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
                    fromDocument.copyPagesTo(1, fromDocument.getNumberOfPages(), outDocument);
            }
            Assert.assertNotNull(outDocument.catalog);
            Assert.assertNotNull(outDocument.catalog.ocProperties);
            Assert.assertEquals(2, outDocument.catalog.ocProperties.getLayers().size());
            PdfLayer layer = outDocument.catalog.ocProperties.getLayers().get(0);
            Assert.assertNotNull(layer);
            assertLayerNameEqual("name1", layer);
            layer = outDocument.catalog.ocProperties.getLayers().get(1);
            Assert.assertNotNull(layer);
            assertLayerNameEqual("name1_0", layer);
        }
    }

    @Test
    public void copyPagesFlushedResources() throws IOException {
        byte[] docBytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = document.addNewPage();
                PdfResources pdfResource = page.getResources();
                PdfDictionary ocg = new PdfDictionary();
                ocg.put(PdfName.Type, PdfName.OCG);
                ocg.put(PdfName.Name, new PdfString("name1"));
                pdfResource.addProperties(ocg);
                pdfResource.makeIndirect(document);
                PdfPage page2 = document.addNewPage();
                page2.setResources(pdfResource);
                document.getCatalog().getOCProperties(true);
            }
            docBytes = outputStream.toByteArray();
        }

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        writer.setSmartMode(true);
        try (PdfDocument outDocument = new PdfDocument(writer)) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
                fromDocument.copyPagesTo(1, 1, outDocument);
                outDocument.flushCopiedObjects(fromDocument);
                fromDocument.copyPagesTo(2, 2, outDocument);
            }
            Assert.assertNotNull(outDocument.catalog);
            Assert.assertNotNull(outDocument.catalog.ocProperties);
            Assert.assertEquals(1, outDocument.catalog.ocProperties.getLayers().size());
            PdfLayer layer = outDocument.catalog.ocProperties.getLayers().get(0);
            Assert.assertNotNull(layer);
            assertLayerNameEqual("name1", layer);
        }
    }


    private static List<byte[]> initSourceDocuments(List<List<String>> ocgNames) throws IOException {
        List<byte[]> result = new ArrayList<>();
        for(List<String> names: ocgNames) {
            result.add(initDocument(names));
        }
        return result;
    }

    private static byte[] initDocument(List<String> names) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = document.addNewPage();
                PdfResources pdfResource = page.getResources();
                for (String name : names) {
                    PdfDictionary ocg = new PdfDictionary();
                    ocg.put(PdfName.Type, PdfName.OCG);
                    ocg.put(PdfName.Name, new PdfString(name));
                    pdfResource.addProperties(ocg);
                }
                document.getCatalog().getOCProperties(true);
            }
            return outputStream.toByteArray();
        }
    }

    void assertLayerNameEqual(String name, PdfLayer layer) {
        PdfDictionary layerDictionary = layer.getPdfObject();
        Assert.assertNotNull(layerDictionary);
        Assert.assertNotNull(layerDictionary.get(PdfName.Name));
        String layerNameString = layerDictionary.get(PdfName.Name).toString();
        Assert.assertEquals(name, layerNameString);
    }
}
