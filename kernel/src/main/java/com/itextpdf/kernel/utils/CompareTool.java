/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.kernel.utils;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.xmp.*;
import com.itextpdf.kernel.xmp.options.SerializeOptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;

public class CompareTool {
    private static final String cannotOpenOutputDirectory = "Cannot open output directory for <filename>.";
    private static final String gsFailed = "GhostScript failed for <filename>.";
    private static final String unexpectedNumberOfPages = "Unexpected number of pages for <filename>.";
    private static final String differentPages = "File <filename> differs on page <pagenumber>.";
    private static final String undefinedGsPath = "Path to GhostScript is not specified. Please use -DgsExec=<path_to_ghostscript> (e.g. -DgsExec=\"C:/Program Files/gs/gs9.14/bin/gswin32c.exe\")";
    private static final String ignoredAreasPrefix = "ignored_areas_";

    private static final String gsParams = " -dNOPAUSE -dBATCH -sDEVICE=png16m -r150 -sOutputFile=<outputfile> <inputfile>";
    private static final String compareParams = " \"<image1>\" \"<image2>\" \"<difference>\"";


    private String gsExec;
    private String compareExec;

    private String cmpPdf;
    private String cmpPdfName;
    private String cmpImage;
    private String outPdf;
    private String outPdfName;
    private String outImage;

    private List<PdfIndirectReference> outPagesRef;
    private List<PdfIndirectReference> cmpPagesRef;

    private int compareByContentErrorsLimit = 1;
    private boolean generateCompareByContentXmlReport = false;

    private boolean encryptionCompareEnabled = false;

    private boolean useCachedPagesForComparison = true;

    public CompareTool() {
        gsExec = System.getProperty("gsExec");
        compareExec = System.getProperty("compareExec");
    }

    public CompareResult compareByCatalog(PdfDocument outDocument, PdfDocument cmpDocument) {
        CompareResult compareResult = null;
        try {
            compareResult = new CompareResult(compareByContentErrorsLimit);
            ObjectPath catalogPath = new ObjectPath(cmpDocument.getCatalog().getPdfObject().getIndirectReference(),
                    outDocument.getCatalog().getPdfObject().getIndirectReference());
            Set<PdfName> ignoredCatalogEntries = new LinkedHashSet<>(Arrays.asList(PdfName.Metadata, PdfName.AcroForm));
            compareDictionariesExtended(outDocument.getCatalog().getPdfObject(), cmpDocument.getCatalog().getPdfObject(),
                    catalogPath, compareResult, ignoredCatalogEntries);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compareResult;
    }

    public CompareTool disableCachedPagesComparison() {
        this.useCachedPagesForComparison = false;
        return this;
    }

    /**
     * Sets the maximum errors count which will be returned as the result of the comparison.
     * @param compareByContentMaxErrorCount the errors count.
     * @return Returns this.
     */
    public CompareTool setCompareByContentErrorsLimit(int compareByContentMaxErrorCount) {
        this.compareByContentErrorsLimit = compareByContentMaxErrorCount;
        return this;
    }

    public CompareTool setGenerateCompareByContentXmlReport(boolean generateCompareByContentXmlReport) {
        this.generateCompareByContentXmlReport = generateCompareByContentXmlReport;
        return this;
    }

    public CompareTool enableEncryptionCompare() {
        this.encryptionCompareEnabled = true;
        return this;
    }


    public String compareVisually(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix) throws InterruptedException, IOException {
        return compareVisually(outPdf, cmpPdf, outPath, differenceImagePrefix, null);
    }

    public String compareVisually(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        init(outPdf, cmpPdf);
        return compareVisually(outPath, differenceImagePrefix, ignoredAreas);
    }

    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix) throws InterruptedException, IOException {
        return compareByContent(outPdf, cmpPdf, outPath, differenceImagePrefix, null, null, null);
    }

    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, byte[] outPass, byte[] cmpPass) throws InterruptedException, IOException {
        return compareByContent(outPdf, cmpPdf, outPath, differenceImagePrefix, null, outPass, cmpPass);
    }

    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        init(outPdf, cmpPdf);
        return compareByContent(outPath, differenceImagePrefix, ignoredAreas, null, null);
    }

    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas, byte[] outPass, byte[] cmpPass) throws InterruptedException, IOException {
        init(outPdf, cmpPdf);
        return compareByContent(outPath, differenceImagePrefix, ignoredAreas, outPass, cmpPass);
    }

    public boolean compareDictionaries(PdfDictionary outDict, PdfDictionary cmpDict) throws IOException {
        return compareDictionariesExtended(outDict, cmpDict, null, null);
    }

    public boolean compareStreams(PdfStream outStream, PdfStream cmpStream) throws IOException {
        return compareStreamsExtended(outStream, cmpStream, null, null);
    }

    public boolean compareArrays(PdfArray outArray, PdfArray cmpArray) throws IOException {
        return compareArraysExtended(outArray, cmpArray, null, null);
    }

    public boolean compareNames(PdfName outName, PdfName cmpName) {
        return cmpName.equals(outName);
    }

    public boolean compareNumbers(PdfNumber outNumber, PdfNumber cmpNumber) {
        return cmpNumber.getValue() == outNumber.getValue();
    }

    public boolean compareStrings(PdfString outString, PdfString cmpString) {
        return cmpString.getValue().equals(outString.getValue());
    }

    public boolean compareBooleans(PdfBoolean outBoolean, PdfBoolean cmpBoolean) {
        return cmpBoolean.getValue() == outBoolean.getValue();
    }

    public String compareXmp(String outPdf, String cmpPdf) {
        return compareXmp(outPdf, cmpPdf, false);
    }

    public String compareXmp(String outPdf, String cmpPdf, boolean ignoreDateAndProducerProperties) {
        init(outPdf, cmpPdf);
        PdfDocument cmpDocument = null;
        PdfDocument outDocument = null;
        try {
            cmpDocument = new PdfDocument(new PdfReader(this.cmpPdf));
            outDocument = new PdfDocument(new PdfReader(this.outPdf));
            byte[] cmpBytes = cmpDocument.getXmpMetadata(), outBytes = outDocument.getXmpMetadata();
            if (ignoreDateAndProducerProperties) {
                XMPMeta xmpMeta = XMPMetaFactory.parseFromBuffer(cmpBytes);

                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.CreateDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.ModifyDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.MetadataDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_PDF, PdfConst.Producer, true, true);

                cmpBytes = XMPMetaFactory.serializeToBuffer(xmpMeta, new SerializeOptions(SerializeOptions.SORT));

                xmpMeta = XMPMetaFactory.parseFromBuffer(outBytes);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.CreateDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.ModifyDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.MetadataDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_PDF, PdfConst.Producer, true, true);

                outBytes = XMPMetaFactory.serializeToBuffer(xmpMeta, new SerializeOptions(SerializeOptions.SORT));
            }

            if (!compareXmls(cmpBytes, outBytes)) {
                return "The XMP packages different!";
            }
        } catch (XMPException xmpExc) {
            return "XMP parsing failure!";
        } catch (IOException ioExc) {
            return "XMP parsing failure!";
        } catch (ParserConfigurationException parseExc)  {
            return "XMP parsing failure!";
        } catch (SAXException parseExc)  {
            return "XMP parsing failure!";
        }
        finally {
            if (cmpDocument != null)
                cmpDocument.close();
            if (outDocument != null)
                outDocument.close();
        }
        return null;
    }

    public boolean compareXmls(byte[] xml1, byte[] xml2) throws ParserConfigurationException, SAXException, IOException {
        return compareXmls(new ByteArrayInputStream(xml1), new ByteArrayInputStream(xml2));
    }

    public boolean compareXmls(String xmlFilePath1, String xmlFilePath2) throws ParserConfigurationException, SAXException, IOException {
        return compareXmls(new FileInputStream(xmlFilePath1), new FileInputStream(xmlFilePath2));
    }

    public String compareDocumentInfo(String outPdf, String cmpPdf, byte[] outPass, byte[] cmpPass) throws IOException {
        System.out.print("[itext] INFO  Comparing document info.......");
        String message = null;
        PdfDocument outDocument = new PdfDocument(new PdfReader(outPdf, outPass));
        PdfDocument cmpDocument = new PdfDocument(new PdfReader(cmpPdf, cmpPass));
        String[] cmpInfo = convertInfo(cmpDocument.getDocumentInfo());
        String[] outInfo = convertInfo(outDocument.getDocumentInfo());
        for (int i = 0; i < cmpInfo.length; ++i) {
            if (!cmpInfo[i].equals(outInfo[i])){
                message = "Document info fail";
                break;
            }
        }
        outDocument.close();
        cmpDocument.close();

        if (message == null)
            System.out.println("OK");
        else
            System.out.println("Fail");
        System.out.flush();
        return message;
    }

    public String compareDocumentInfo(String outPdf, String cmpPdf) throws IOException {
        return compareDocumentInfo(outPdf, cmpPdf, null, null);
    }

    public String compareLinkAnnotations(String outPdf, String cmpPdf) throws IOException {
        System.out.print("[itext] INFO  Comparing link annotations....");
        String message = null;
        PdfDocument outDocument = new PdfDocument(new PdfReader(outPdf));
        PdfDocument cmpDocument = new PdfDocument(new PdfReader(cmpPdf));
        for (int i = 0; i < outDocument.getNumberOfPages() && i < cmpDocument.getNumberOfPages(); i++) {
            List<PdfLinkAnnotation> outLinks = getLinkAnnotations(i + 1, outDocument);
            List<PdfLinkAnnotation> cmpLinks = getLinkAnnotations(i + 1, cmpDocument);

            if (cmpLinks.size() != outLinks.size()) {
                message = MessageFormat.format("Different number of links on page {0}.", i + 1);
                break;
            }
            for (int j = 0; j < cmpLinks.size(); j++) {
                if (!compareLinkAnnotations(cmpLinks.get(j), outLinks.get(j), cmpDocument, outDocument)) {
                    message = MessageFormat.format("Different links on page {0}.\n{1}\n{2}", i + 1, cmpLinks.get(j).toString(), outLinks.get(j).toString());
                    break;
                }
            }
        }
        outDocument.close();
        cmpDocument.close();
        if (message == null)
            System.out.println("OK");
        else
            System.out.println("Fail");
        System.out.flush();
        return message;
    }

    public String compareTagStructures(String outPdf, String cmpPdf) throws IOException, ParserConfigurationException, SAXException {
        System.out.print("[itext] INFO  Comparing tag structures......");

        String outXmlPath = outPdf.replace(".pdf", ".xml");
        String cmpXmlPath = outPdf.replace(".pdf", ".cmp.xml");

        String message = null;

        PdfReader readerOut = new PdfReader(outPdf);
        PdfDocument docOut = new PdfDocument(readerOut);
        FileOutputStream xmlOut = new FileOutputStream(outXmlPath);
        new TaggedPdfReaderTool(docOut).setRootTag("root").convertToXml(xmlOut);
        docOut.close();
        xmlOut.close();

        PdfReader readerCmp = new PdfReader(cmpPdf);
        PdfDocument docCmp = new PdfDocument(readerCmp);
        FileOutputStream xmlCmp = new FileOutputStream(cmpXmlPath);
        new TaggedPdfReaderTool(docCmp).setRootTag("root").convertToXml(xmlCmp);
        docCmp.close();
        xmlCmp.close();

        if (!compareXmls(outXmlPath, cmpXmlPath)) {
            message = "The tag structures are different.";
        }
        if (message == null)
            System.out.println("OK");
        else
            System.out.println("Fail");
        System.out.flush();
        return message;
    }

    private void init(String outPdf, String cmpPdf) {
        this.outPdf = outPdf;
        this.cmpPdf = cmpPdf;
        outPdfName =  new File(outPdf).getName();
        cmpPdfName = new File(cmpPdf).getName();
        outImage = outPdfName + "-%03d.png";
        if (cmpPdfName.startsWith("cmp_")) cmpImage = cmpPdfName + "-%03d.png";
        else cmpImage = "cmp_" + cmpPdfName + "-%03d.png";
    }

    private String compareVisually(String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        return compareVisually(outPath, differenceImagePrefix, ignoredAreas, null);
    }

    private String compareVisually(String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas, List<Integer> equalPages) throws IOException, InterruptedException {
        if (gsExec == null)
            return undefinedGsPath;
        if (!(new File(gsExec).exists())) {
            return new File(gsExec).getAbsolutePath() + " does not exist";
        }
        if (!outPath.endsWith("/"))
            outPath = outPath + "/";
        prepareOutputDirs(outPath, differenceImagePrefix);

        if (ignoredAreas != null && !ignoredAreas.isEmpty()) {
            createIgnoredAreasPdfs(outPath, ignoredAreas);
        }

        String imagesGenerationResult = runGhostScriptImageGeneration(outPath);
        if (imagesGenerationResult != null)
            return imagesGenerationResult;

        return compareImagesOfPdfs(outPath, differenceImagePrefix, equalPages);
    }

    private String compareImagesOfPdfs(String outPath, String differenceImagePrefix, List<Integer> equalPages) throws IOException, InterruptedException {
        File outputDir = new File(outPath);
        File[] imageFiles = outputDir.listFiles(new PngFileFilter());
        File[] cmpImageFiles = outputDir.listFiles(new CmpPngFileFilter());
        boolean bUnexpectedNumberOfPages = false;
        if (imageFiles.length != cmpImageFiles.length) {
            bUnexpectedNumberOfPages = true;
        }
        int cnt = Math.min(imageFiles.length, cmpImageFiles.length);
        if (cnt < 1) {
            return "No files for comparing.\nThe result or sample pdf file is not processed by GhostScript.";
        }
        Arrays.sort(imageFiles, new ImageNameComparator());
        Arrays.sort(cmpImageFiles, new ImageNameComparator());
        String differentPagesFail = null;
        boolean compareExecIsOk = compareExec != null && new File(compareExec).exists();
        List<Integer> diffPages = new ArrayList<>();

        for (int i = 0; i < cnt; i++) {
            if (equalPages != null && equalPages.contains(i))
                continue;
            System.out.print("Comparing page " + Integer.toString(i + 1) + " (" + imageFiles[i].getAbsolutePath() + ")...");
            FileInputStream is1 = new FileInputStream(imageFiles[i]);
            FileInputStream is2 = new FileInputStream(cmpImageFiles[i]);
            boolean cmpResult = compareStreams(is1, is2);
            is1.close();
            is2.close();
            if (!cmpResult) {
                differentPagesFail = " Page is different!";
                diffPages.add(i + 1);
                if (compareExecIsOk) {
                String currCompareParams = compareParams.replace("<image1>", imageFiles[i].getAbsolutePath())
                            .replace("<image2>", cmpImageFiles[i].getAbsolutePath())
                            .replace("<difference>", outPath + differenceImagePrefix + Integer.toString(i + 1) + ".png");
                    if (runProcessAndWait(compareExec, currCompareParams))
                        differentPagesFail += "\nPlease, examine " + outPath + differenceImagePrefix + Integer.toString(i + 1) + ".png for more details.";
                }
                System.out.println(differentPagesFail);
            } else {
                System.out.println(" done.");
            }
        }
        if (differentPagesFail != null) {
            String errorMessage = differentPages.replace("<filename>", outPdf).replace("<pagenumber>", diffPages.toString());
            if (!compareExecIsOk) {
                errorMessage += "\nYou can optionally specify path to ImageMagick compare tool (e.g. -DcompareExec=\"C:/Program Files/ImageMagick-6.5.4-2/compare.exe\") to visualize differences.";
            }
            return errorMessage;
        } else {
            if (bUnexpectedNumberOfPages)
                return unexpectedNumberOfPages.replace("<filename>", outPdf);
        }

        return null;
    }

    private void createIgnoredAreasPdfs(String outPath, Map<Integer, List<Rectangle>> ignoredAreas) throws IOException {
        PdfWriter outWriter = new PdfWriter(new FileOutputStream(outPath + ignoredAreasPrefix + outPdfName));
        PdfWriter cmpWriter = new PdfWriter(new FileOutputStream(outPath + ignoredAreasPrefix + cmpPdfName));

        PdfDocument pdfOutDoc = new PdfDocument(new PdfReader(outPdf), outWriter);
        PdfDocument pdfCmpDoc = new PdfDocument(new PdfReader(cmpPdf), cmpWriter);

        for (Map.Entry<Integer, List<Rectangle>> entry : ignoredAreas.entrySet()) {
            int pageNumber = entry.getKey();
            List<Rectangle> rectangles = entry.getValue();

            if (rectangles != null && !rectangles.isEmpty()) {
                //drawing rectangles manually, because we don't want to create dependency on itextpdf.canvas module for itextpdf.kernel
                PdfStream outStream = getPageContentStream(pdfOutDoc.getPage(pageNumber));
                PdfStream cmpStream = getPageContentStream(pdfCmpDoc.getPage(pageNumber));

                outStream.getOutputStream().writeBytes(ByteUtils.getIsoBytes("q\n"));
                outStream.getOutputStream().writeFloats(new float[]{0.0f, 0.0f, 0.0f}).writeSpace().writeBytes(ByteUtils.getIsoBytes("rg\n"));
                cmpStream.getOutputStream().writeBytes(ByteUtils.getIsoBytes("q\n"));
                cmpStream.getOutputStream().writeFloats(new float[]{0.0f, 0.0f, 0.0f}).writeSpace().writeBytes(ByteUtils.getIsoBytes("rg\n"));

                for (Rectangle rect : rectangles) {
                    outStream.getOutputStream().writeFloats(new float[]{rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()}).
                            writeSpace().
                            writeBytes(ByteUtils.getIsoBytes("re\n")).
                            writeBytes(ByteUtils.getIsoBytes("f\n"));

                    cmpStream.getOutputStream().writeFloats(new float[]{rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()}).
                            writeSpace().
                            writeBytes(ByteUtils.getIsoBytes("re\n")).
                            writeBytes(ByteUtils.getIsoBytes("f\n"));
                }
                outStream.getOutputStream().writeBytes(ByteUtils.getIsoBytes("Q\n"));
                cmpStream.getOutputStream().writeBytes(ByteUtils.getIsoBytes("Q\n"));
            }
        }

        pdfOutDoc.close();
        pdfCmpDoc.close();

        init(outPath + ignoredAreasPrefix + outPdfName, outPath + ignoredAreasPrefix + cmpPdfName);
    }

    private PdfStream getPageContentStream(PdfPage page) {
        PdfStream stream = page.getContentStream(page.getContentStreamCount() - 1);
        return stream.getOutputStream() == null ? page.newContentStreamAfter() : stream;
    }

    private void prepareOutputDirs(String outPath, String differenceImagePrefix) {
        File outputDir = new File(outPath);
        File[] imageFiles;
        File[] cmpImageFiles;
        File[] diffFiles;

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        } else {
            imageFiles = outputDir.listFiles(new PngFileFilter());
            for (File file : imageFiles) {
                file.delete();
            }
            cmpImageFiles = outputDir.listFiles(new CmpPngFileFilter());
            for (File file : cmpImageFiles) {
                file.delete();
            }

            diffFiles = outputDir.listFiles(new DiffPngFileFilter(differenceImagePrefix));
            for (File file : diffFiles) {
                file.delete();
            }
        }
    }

    /**
     * Runs ghostscript to create images of pdfs.
     *
     * @param outPath Path to the output folder.
     * @return Returns null if result is successful, else returns error message.
     * @throws IOException
     * @throws InterruptedException
     */
    private String runGhostScriptImageGeneration(String outPath) throws IOException, InterruptedException {
        File outputDir = new File(outPath);
        if (!outputDir.exists()) {
            return cannotOpenOutputDirectory.replace("<filename>", outPdf);
        }

        String currGsParams = gsParams.replace("<outputfile>", outPath + cmpImage).replace("<inputfile>", cmpPdf);
        if (!runProcessAndWait(gsExec, currGsParams)) {
            return gsFailed.replace("<filename>", cmpPdf);
        }
        currGsParams = gsParams.replace("<outputfile>", outPath + outImage).replace("<inputfile>", outPdf);
        if (!runProcessAndWait(gsExec, currGsParams)) {
            return gsFailed.replace("<filename>", outPdf);
        }
        return null;
    }


    private boolean runProcessAndWait(String execPath, String params) throws IOException, InterruptedException {
        StringTokenizer st = new StringTokenizer(params);
        String[] cmdArray = new String[st.countTokens() + 1];
        cmdArray[0] = execPath;
        for (int i = 1; st.hasMoreTokens(); ++i)
            cmdArray[i] = st.nextToken();

        Process p = Runtime.getRuntime().exec(cmdArray);
        printProcessOutput(p);
        return p.waitFor() == 0;
    }

    private void printProcessOutput(Process p) throws IOException {
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line;
        while ((line = bri.readLine()) != null) {
            System.out.println(line);
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            System.out.println(line);
        }
        bre.close();
    }

    private String compareByContent(String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        return compareByContent(outPath, differenceImagePrefix, ignoredAreas, null, null);
    }


    private String compareByContent(String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas, byte[] outPass, byte[] cmpPass) throws InterruptedException, IOException {
        System.out.print("[itext] INFO  Comparing by content..........");
        PdfDocument outDocument;
        try {
            outDocument = new PdfDocument(new PdfReader(outPdf, outPass));
        } catch (IOException e) {
            throw new IOException("File \"" + outPdf + "\" not found", e);
        }
        List<PdfDictionary> outPages = new ArrayList<>();
        outPagesRef = new ArrayList<>();
        loadPagesFromReader(outDocument, outPages, outPagesRef);

        PdfDocument cmpDocument;
        try {
            cmpDocument = new PdfDocument(new PdfReader(cmpPdf, cmpPass));
        } catch (IOException e) {
            throw new IOException("File \"" + cmpPdf + "\" not found", e);
        }
        List<PdfDictionary> cmpPages = new ArrayList<>();
        cmpPagesRef = new ArrayList<>();
        loadPagesFromReader(cmpDocument, cmpPages, cmpPagesRef);

        if (outPages.size() != cmpPages.size())
            return compareVisually(outPath, differenceImagePrefix, ignoredAreas);

        CompareResult compareResult = new CompareResult(compareByContentErrorsLimit);
        List<Integer> equalPages = new ArrayList<>(cmpPages.size());
        for (int i = 0; i < cmpPages.size(); i++) {
            ObjectPath currentPath = new ObjectPath(cmpPagesRef.get(i), outPagesRef.get(i));
            if (compareDictionariesExtended(outPages.get(i), cmpPages.get(i), currentPath, compareResult))
                equalPages.add(i);
        }

        ObjectPath catalogPath = new ObjectPath(cmpDocument.getCatalog().getPdfObject().getIndirectReference(),
                outDocument.getCatalog().getPdfObject().getIndirectReference());
        Set<PdfName> ignoredCatalogEntries = new LinkedHashSet<>(Arrays.asList(PdfName.Pages, PdfName.Metadata));
        compareDictionariesExtended(outDocument.getCatalog().getPdfObject(), cmpDocument.getCatalog().getPdfObject(),
                catalogPath, compareResult, ignoredCatalogEntries);

        if (encryptionCompareEnabled) {
            compareDocumentsEncryption(outDocument, cmpDocument, compareResult);
        }

        outDocument.close();
        cmpDocument.close();

        if (generateCompareByContentXmlReport) {
            try {
                compareResult.writeReportToXml(new FileOutputStream(outPath + "/report.xml"));
            } catch (Exception exc) {}
        }

        if (equalPages.size() == cmpPages.size() && compareResult.isOk()) {
            System.out.println("OK");
            System.out.flush();
            return null;
        } else {
            System.out.println("Fail");
            System.out.flush();
            String compareByContentReport = "Compare by content report:\n" + compareResult.getReport();
            System.out.println(compareByContentReport);
            System.out.flush();
            String message = compareVisually(outPath, differenceImagePrefix, ignoredAreas, equalPages);
            if (message == null || message.length() == 0)
                return "Compare by content fails. No visual differences";
            return message;
        }
    }

    private void loadPagesFromReader(PdfDocument doc, List<PdfDictionary> pages, List<PdfIndirectReference> pagesRef) {
        int numOfPages = doc.getCatalog().getNumberOfPages();
        for (int i = 0; i < numOfPages; ++i) {
            pages.add(doc.getCatalog().getPage(i + 1).getPdfObject());
            pagesRef.add(pages.get(i).getIndirectReference());
        }
    }

    private void compareDocumentsEncryption(PdfDocument outDocument, PdfDocument cmpDocument, CompareResult compareResult) throws IOException {
        PdfDictionary outEncrypt = outDocument.getTrailer().getAsDictionary(PdfName.Encrypt);
        PdfDictionary cmpEncrypt = cmpDocument.getTrailer().getAsDictionary(PdfName.Encrypt);

        if (outEncrypt == null && cmpEncrypt == null) {
            return;
        }

        TrailerPath trailerPath = new TrailerPath(cmpDocument, outDocument);
        if (outEncrypt == null) {
            compareResult.addError(trailerPath, "Expected encrypted document.");
            return;
        }
        if (cmpEncrypt == null) {
            compareResult.addError(trailerPath, "Expected not encrypted document.");
            return;
        }

        Set<PdfName> ignoredEncryptEntries = new LinkedHashSet<>(Arrays.asList(PdfName.O, PdfName.U, PdfName.OE, PdfName.UE, PdfName.Perms));
        ObjectPath objectPath = new ObjectPath(outEncrypt.getIndirectReference(), cmpEncrypt.getIndirectReference());
        compareDictionariesExtended(outEncrypt, cmpEncrypt, objectPath, compareResult, ignoredEncryptEntries);
    }

    private boolean compareStreams(InputStream is1, InputStream is2) throws IOException {
        byte[] buffer1 = new byte[64 * 1024];
        byte[] buffer2 = new byte[64 * 1024];
        int len1;
        int len2;
        for (; ;) {
            len1 = is1.read(buffer1);
            len2 = is2.read(buffer2);
            if (len1 != len2)
                return false;
            if (!Arrays.equals(buffer1, buffer2))
                return false;
            if (len1 == -1)
                break;
        }
        return true;
    }

    private boolean compareDictionariesExtended(PdfDictionary outDict, PdfDictionary cmpDict, ObjectPath currentPath, CompareResult compareResult) throws IOException {
        return compareDictionariesExtended(outDict, cmpDict, currentPath, compareResult, null);
    }

    private boolean compareDictionariesExtended(PdfDictionary outDict, PdfDictionary cmpDict, ObjectPath currentPath, CompareResult compareResult, Set<PdfName> excludedKeys) throws IOException {
        if (cmpDict != null && outDict == null || outDict != null && cmpDict == null) {
            compareResult.addError(currentPath, "One of the dictionaries is null, the other is not.");
            return false;
        }
        boolean dictsAreSame = true;
        // Iterate through the union of the keys of the cmp and out dictionaries
        Set<PdfName> mergedKeys = new TreeSet<>(cmpDict.keySet());
        mergedKeys.addAll(outDict.keySet());
        for (PdfName key : mergedKeys) {
            if (excludedKeys != null && excludedKeys.contains(key)) {
                continue;
            }
            if (key.equals(PdfName.Parent) || key.equals(PdfName.P) || key.equals(PdfName.ModDate)) continue;
            if (outDict.isStream() && cmpDict.isStream() && (key.equals(PdfName.Filter) || key.equals(PdfName.Length))) continue;
            if (key.equals(PdfName.BaseFont) || key.equals(PdfName.FontName)) {
                PdfObject cmpObj = cmpDict.get(key);
                if (cmpObj.isName() && cmpObj.toString().indexOf('+') > 0) {
                    PdfObject outObj = outDict.get(key);
                    if (!outObj.isName() || outObj.toString().indexOf('+') == -1) {
                        if (compareResult != null && currentPath != null)
                            compareResult.addError(currentPath, MessageFormat.format("PdfDictionary {0} entry: Expected: {1}. Found: {2}", key.toString(), cmpObj.toString(), outObj.toString()));
                        dictsAreSame = false;
                    } else {
                        String cmpName = cmpObj.toString().substring(cmpObj.toString().indexOf('+'));
                        String outName = outObj.toString().substring(outObj.toString().indexOf('+'));
                        if (!cmpName.equals(outName)) {
                            if (compareResult != null && currentPath != null)
                                compareResult.addError(currentPath, MessageFormat.format("PdfDictionary {0} entry: Expected: {1}. Found: {2}", key.toString(), cmpObj.toString(), outObj.toString()));
                            dictsAreSame = false;
                        }
                    }
                    continue;
                }
            }
            if (currentPath != null)
                currentPath.pushDictItemToPath(key);
            dictsAreSame = compareObjects(outDict.get(key, false), cmpDict.get(key, false), currentPath, compareResult) && dictsAreSame;
            if (currentPath != null)
                currentPath.pop();
            if (!dictsAreSame && (currentPath == null || compareResult == null || compareResult.isMessageLimitReached()))
                return false;
        }
        return dictsAreSame;
    }

    private boolean compareObjects(PdfObject outObj, PdfObject cmpObj, ObjectPath currentPath, CompareResult compareResult) throws IOException {
        PdfObject outDirectObj = null;
        PdfObject cmpDirectObj = null;
        if (outObj != null)
            outDirectObj = outObj.isIndirectReference() ? ((PdfIndirectReference)outObj).getRefersTo(false) : outObj;
        if (cmpObj != null)
            cmpDirectObj = cmpObj.isIndirectReference() ? ((PdfIndirectReference)cmpObj).getRefersTo(false) : cmpObj;

        if (cmpDirectObj == null && outDirectObj == null)
            return true;

        if (outDirectObj == null) {
            compareResult.addError(currentPath, "Expected object was not found.");
            return false;
        } else if (cmpDirectObj == null) {
            compareResult.addError(currentPath, "Found object which was not expected to be found.");
            return false;
        } else if (cmpDirectObj.getType() != outDirectObj.getType()) {
            compareResult.addError(currentPath, MessageFormat.format("Types do not match. Expected: {0}. Found: {1}.", cmpDirectObj.getClass().getSimpleName(), outDirectObj.getClass().getSimpleName()));
            return false;
        } else if (cmpObj.isIndirectReference() && !outObj.isIndirectReference()) {
            compareResult.addError(currentPath, "Expected indirect object.");
            return false;
        } else if (!cmpObj.isIndirectReference() && outObj.isIndirectReference()) {
            compareResult.addError(currentPath, "Expected direct object.");
            return false;
        }

        if (currentPath != null && cmpObj.isIndirectReference() && outObj.isIndirectReference()) {
            if (currentPath.isComparing((PdfIndirectReference) cmpObj, (PdfIndirectReference) outObj))
                return true;
            currentPath = currentPath.resetDirectPath((PdfIndirectReference) cmpObj,(PdfIndirectReference) outObj);
        }

        if (cmpDirectObj.isDictionary() && PdfName.Page.equals(((PdfDictionary) cmpDirectObj).getAsName(PdfName.Type))
                && useCachedPagesForComparison) {
            if (!outDirectObj.isDictionary() || !PdfName.Page.equals(((PdfDictionary)outDirectObj).getAsName(PdfName.Type))) {
                if (compareResult != null && currentPath != null)
                    compareResult.addError(currentPath, "Expected a page. Found not a page.");
                return false;
            }
            PdfIndirectReference cmpRefKey = cmpObj.isIndirectReference() ? (PdfIndirectReference) cmpObj : cmpObj.getIndirectReference();
            PdfIndirectReference outRefKey = outObj.isIndirectReference() ? (PdfIndirectReference) outObj : outObj.getIndirectReference();
            // References to the same page
            if (cmpPagesRef == null) {
                cmpPagesRef = new ArrayList<>();
                for (int i = 1; i <= cmpObj.getIndirectReference().getDocument().getNumberOfPages(); ++i) {
                    cmpPagesRef.add(cmpObj.getIndirectReference().getDocument().getPage(i).getPdfObject().getIndirectReference());
                }
            }
            if (outPagesRef == null) {
                outPagesRef = new ArrayList<>();
                for (int i = 1; i <= outObj.getIndirectReference().getDocument().getNumberOfPages(); ++i) {
                    outPagesRef.add(outObj.getIndirectReference().getDocument().getPage(i).getPdfObject().getIndirectReference());
                }
            }
            if (cmpPagesRef.contains(cmpRefKey) && cmpPagesRef.indexOf(cmpRefKey) == outPagesRef.indexOf(outRefKey))
                return true;
            if (compareResult != null && currentPath != null)
                compareResult.addError(currentPath, MessageFormat.format("The dictionaries refer to different pages. Expected page number: {0}. Found: {1}",
                        cmpPagesRef.indexOf(cmpRefKey), outPagesRef.indexOf(outRefKey)));
            return false;
        }

        if (cmpDirectObj.isDictionary()) {
            if (!compareDictionariesExtended((PdfDictionary)outDirectObj, (PdfDictionary)cmpDirectObj, currentPath, compareResult))
                return false;
        } else if (cmpDirectObj.isStream()) {
            if (!compareStreamsExtended((PdfStream) outDirectObj, (PdfStream) cmpDirectObj, currentPath, compareResult))
                return false;
        } else if (cmpDirectObj.isArray()) {
            if (!compareArraysExtended((PdfArray) outDirectObj, (PdfArray) cmpDirectObj, currentPath, compareResult))
                return false;
        } else if (cmpDirectObj.isName()) {
            if (!compareNamesExtended((PdfName) outDirectObj, (PdfName) cmpDirectObj, currentPath, compareResult))
                return false;
        } else if (cmpDirectObj.isNumber()) {
            if (!compareNumbersExtended((PdfNumber) outDirectObj, (PdfNumber) cmpDirectObj, currentPath, compareResult))
                return false;
        } else if (cmpDirectObj.isString()) {
            if (!compareStringsExtended((PdfString) outDirectObj, (PdfString) cmpDirectObj, currentPath, compareResult))
                return false;
        } else if (cmpDirectObj.isBoolean()) {
            if (!compareBooleansExtended((PdfBoolean) outDirectObj, (PdfBoolean) cmpDirectObj, currentPath, compareResult))
                return false;
        } else if (outDirectObj.isNull() && cmpDirectObj.isNull()) {
        } else {
            throw new UnsupportedOperationException();
        }
        return true;
    }

    private boolean compareStreamsExtended(PdfStream outStream, PdfStream cmpStream, ObjectPath currentPath, CompareResult compareResult) throws IOException {
        boolean toDecode = PdfName.FlateDecode.equals(outStream.get(PdfName.Filter));
        byte[] outStreamBytes = outStream.getBytes(toDecode);
        byte[] cmpStreamBytes = cmpStream.getBytes(toDecode);
        if (Arrays.equals(outStreamBytes, cmpStreamBytes)) {
            return compareDictionariesExtended(outStream, cmpStream, currentPath, compareResult);
        } else {
            String errorMessage = "";
            if (cmpStreamBytes.length != outStreamBytes.length) {
                errorMessage += MessageFormat.format("PdfStream. Lengths are different. Expected: {0}. Found: {1}", cmpStreamBytes.length, outStreamBytes.length) + "\n";
            } else {
                errorMessage += "PdfStream. Bytes are different.\n";
            }
            String bytesDifference = findBytesDifference(outStreamBytes, cmpStreamBytes);
            if (bytesDifference != null) {
                errorMessage += bytesDifference;
            }

            if (compareResult != null && currentPath != null) {
//            currentPath.pushOffsetToPath(firstDifferenceOffset);
                compareResult.addError(currentPath, errorMessage);
//            currentPath.pop();
            }
            return false;
        }
    }

    private String findBytesDifference(byte[] outStreamBytes, byte[] cmpStreamBytes) {
        int numberOfDifferentBytes = 0;
        int firstDifferenceOffset = 0;
        int minLength = Math.min(cmpStreamBytes.length, outStreamBytes.length);
        for (int i = 0; i < minLength; i++) {
            if (cmpStreamBytes[i] != outStreamBytes[i]) {
                ++numberOfDifferentBytes;
                if (numberOfDifferentBytes == 1) {
                    firstDifferenceOffset = i;
                }
            }
        }
        String errorMessage = null;
        if (numberOfDifferentBytes > 0) {
            int lCmp = Math.max(0, firstDifferenceOffset - 10);
            int rCmp = Math.min(cmpStreamBytes.length, firstDifferenceOffset + 10);
            int lOut = Math.max(0, firstDifferenceOffset - 10);
            int rOut = Math.min(outStreamBytes.length, firstDifferenceOffset + 10);


            String cmpByte = new String(new byte[]{cmpStreamBytes[firstDifferenceOffset]});
            String cmpByteNeighbours = new String(cmpStreamBytes, lCmp, rCmp - lCmp).replaceAll("\\r|\\n", " ");
            String outByte = new String(new byte[]{outStreamBytes[firstDifferenceOffset]});
            String outBytesNeighbours = new String(outStreamBytes, lOut, rOut - lOut).replaceAll("\\r|\\n", " ");
            errorMessage = MessageFormat.format("First bytes difference is encountered at index {0}. Expected: {1} ({2}). Found: {3} ({4}). Total number of different bytes: {5}",
                    Integer.valueOf(firstDifferenceOffset).toString(), cmpByte, cmpByteNeighbours, outByte, outBytesNeighbours, numberOfDifferentBytes);
        } else { // lengths are different
            errorMessage = MessageFormat.format("Bytes of the shorter array are the same as the first {0} bytes of the longer one.", minLength);
        }

        return errorMessage;
    }

    private boolean compareArraysExtended(PdfArray outArray, PdfArray cmpArray, ObjectPath currentPath, CompareResult compareResult) throws IOException {
        if (outArray == null) {
            if (compareResult != null && currentPath != null)
                compareResult.addError(currentPath, "Found null. Expected PdfArray.");
            return false;
        } else if (outArray.size() != cmpArray.size()) {
            if (compareResult != null && currentPath != null)
                compareResult.addError(currentPath, MessageFormat.format("PdfArrays. Lengths are different. Expected: {0}. Found: {1}.", cmpArray.size(), outArray.size()));
            return false;
        }
        boolean arraysAreEqual = true;
        for (int i = 0; i < cmpArray.size(); i++) {
            if (currentPath != null)
                currentPath.pushArrayItemToPath(i);
            arraysAreEqual = compareObjects(outArray.get(i, false), cmpArray.get(i, false), currentPath, compareResult) && arraysAreEqual;
            if (currentPath != null)
                currentPath.pop();
            if (!arraysAreEqual && (currentPath == null || compareResult == null || compareResult.isMessageLimitReached()))
                return false;
        }

        return arraysAreEqual;
    }

    private boolean compareNamesExtended(PdfName outName, PdfName cmpName, ObjectPath currentPath, CompareResult compareResult) {
        if (cmpName.equals(outName)) {
            return true;
        } else {
            if (compareResult != null && currentPath != null)
                compareResult.addError(currentPath, MessageFormat.format("PdfName. Expected: {0}. Found: {1}", cmpName.toString(), outName.toString()));
            return false;
        }
    }

    private boolean compareNumbersExtended(PdfNumber outNumber, PdfNumber cmpNumber, ObjectPath currentPath, CompareResult compareResult) {
        if (cmpNumber.getValue() == outNumber.getValue()) {
            return true;
        } else {
            if (compareResult != null && currentPath != null)
                compareResult.addError(currentPath, MessageFormat.format("PdfNumber. Expected: {0}. Found: {1}", cmpNumber, outNumber));
            return false;
        }
    }

    private boolean compareStringsExtended(PdfString outString, PdfString cmpString, ObjectPath currentPath, CompareResult compareResult) {
        if (Arrays.equals(convertPdfStringToBytes(cmpString), convertPdfStringToBytes(outString))) {
            return true;
        } else {
            String cmpStr = cmpString.toUnicodeString();
            String outStr = outString.toUnicodeString();
            if (cmpStr.length() != outStr.length()) {
                if (compareResult != null && currentPath != null)
                    compareResult.addError(currentPath, MessageFormat.format("PdfString. Lengths are different. Expected: {0}. Found: {1}", cmpStr.length(), outStr.length()));
            } else {
                for (int i = 0; i < cmpStr.length(); i++) {
                    if (cmpStr.charAt(i) != outStr.charAt(i)) {
                        int l = Math.max(0, i - 10);
                        int r = Math.min(cmpStr.length(), i + 10);
                        if (compareResult != null && currentPath != null) {
                            currentPath.pushOffsetToPath(i);
                            compareResult.addError(currentPath, MessageFormat.format("PdfString. Characters differ at position {0}. Expected: {1} ({2}). Found: {3} ({4}).",
                                    i, Character.toString(cmpStr.charAt(i)), cmpStr.substring(l, r).replace("\n", "\\n"),
                                    Character.toString(outStr.charAt(i)), outStr.substring(l, r).replace("\n", "\\n")));
                            currentPath.pop();
                        }
                        break;
                    }
                }
            }
            return false;
        }
    }

    private byte[] convertPdfStringToBytes(PdfString pdfString) {
        byte[] bytes;
        String value = pdfString.getValue();
        String encoding = pdfString.getEncoding();
        if (encoding != null && encoding.equals(PdfEncodings.UNICODE_BIG) && PdfEncodings.isPdfDocEncoding(value))
            bytes = PdfEncodings.convertToBytes(value, PdfEncodings.PDF_DOC_ENCODING);
        else
            bytes = PdfEncodings.convertToBytes(value, encoding);
        return bytes;
    }

    private boolean compareBooleansExtended(PdfBoolean outBoolean, PdfBoolean cmpBoolean, ObjectPath currentPath, CompareResult compareResult) {
        if (cmpBoolean.getValue() == outBoolean.getValue()) {
            return true;
        } else {
            if (compareResult != null && currentPath != null)
                compareResult.addError(currentPath, MessageFormat.format("PdfBoolean. Expected: {0}. Found: {1}.", cmpBoolean.getValue(), outBoolean.getValue()));
            return false;
        }
    }

    private boolean compareXmls(InputStream xml1, InputStream xml2) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc1 = db.parse(xml1);
        doc1.normalizeDocument();

        Document doc2 = db.parse(xml2);
        doc2.normalizeDocument();

        return doc2.isEqualNode(doc1);
    }

    private List<PdfLinkAnnotation> getLinkAnnotations(int pageNum, PdfDocument document) {
        List<PdfLinkAnnotation> linkAnnotations = new ArrayList<>();
        List<PdfAnnotation> annotations = document.getCatalog().getPage(pageNum).getAnnotations();
        for (PdfAnnotation annotation : annotations) {
            if(PdfName.Link.equals(annotation.getSubtype())) {
                linkAnnotations.add((PdfLinkAnnotation)annotation);
            }
        }
        return linkAnnotations;
    }

    private boolean compareLinkAnnotations(PdfLinkAnnotation cmpLink, PdfLinkAnnotation outLink,PdfDocument cmpDocument, PdfDocument outDocument) {
        // Compare link rectangles, page numbers the links refer to, and simple parameters (non-indirect, non-arrays, non-dictionaries)
        PdfObject cmpDestObject = cmpLink.getDestinationObject();
        PdfObject outDestObject = outLink.getDestinationObject();

        if(cmpDestObject != null && outDestObject != null) {
            if (cmpDestObject.getType() != outDestObject.getType())
                return false;
            else {
                PdfArray explicitCmpDest = null;
                PdfArray explicitOutDest = null;
                Map<String, PdfObject> cmpNamedDestinations = cmpDocument.getCatalog().getNameTree(PdfName.Dests).getNames();
                Map<String, PdfObject> outNamedDestinations = outDocument.getCatalog().getNameTree(PdfName.Dests).getNames();
                switch (cmpDestObject.getType()) {
                    case PdfObject.ARRAY:
                        explicitCmpDest = (PdfArray) cmpDestObject;
                        explicitOutDest = (PdfArray) outDestObject;
                        break;
                    case PdfObject.NAME:
                        explicitCmpDest = (PdfArray) cmpNamedDestinations.get(cmpDestObject);
                        explicitOutDest = (PdfArray) outNamedDestinations.get(outDestObject);
                        break;
                    case PdfObject.STRING:
                        explicitCmpDest = (PdfArray) cmpNamedDestinations.get(((PdfString) cmpDestObject).toUnicodeString());
                        explicitOutDest = (PdfArray) outNamedDestinations.get(((PdfString) outDestObject).toUnicodeString());
                        break;
                    default:
                        break;
                }

                if (getExplicitDestinationPageNum(explicitCmpDest) != getExplicitDestinationPageNum(explicitOutDest))
                    return false;
            }
        }


        PdfDictionary cmpDict = cmpLink.getPdfObject();
        PdfDictionary outDict = outLink.getPdfObject();
        if (cmpDict.size() != outDict.size())
            return false;

        Rectangle cmpRect = cmpDict.getAsRectangle(PdfName.Rect);
        Rectangle outRect = outDict.getAsRectangle(PdfName.Rect);

        if (cmpRect.getHeight() != outRect.getHeight() ||
                cmpRect.getWidth() != outRect.getWidth() ||
                cmpRect.getX() != outRect.getX() ||
                cmpRect.getY() != outRect.getY())
            return false;

        for (Map.Entry<PdfName, PdfObject> cmpEntry : cmpDict.entrySet()) {
            PdfObject cmpObj = cmpEntry.getValue();
            if (!outDict.containsKey(cmpEntry.getKey()))
                return false;
            PdfObject outObj = outDict.get(cmpEntry.getKey());
            if (cmpObj.getType() != outObj.getType())
                return false;

            switch (cmpObj.getType()) {
                case PdfObject.NULL:
                case PdfObject.BOOLEAN:
                case PdfObject.NUMBER:
                case PdfObject.STRING:
                case PdfObject.NAME:
                    if (!cmpObj.toString().equals(outObj.toString()))
                        return false;
                    break;
            }
        }
        return true;
    }

    private int getExplicitDestinationPageNum(PdfArray explicitDest) {
        PdfIndirectReference pageReference = (PdfIndirectReference) explicitDest.get(0, false);

        PdfDocument doc = pageReference.getDocument();
        for (int i = 1; i <= doc.getNumberOfPages(); ++i) {
            if (doc.getPage(i).getPdfObject().getIndirectReference().equals(pageReference))
                return i;
        }
        throw new IllegalArgumentException("PdfLinkAnnotation comparison: Page not found.");
    }

    private String[] convertInfo(PdfDocumentInfo info) {
        String[] convertedInfo = new String[]{"", "", "", ""};
        String infoValue = info.getTitle();
        if (infoValue != null)
            convertedInfo[0] = infoValue;
        infoValue = info.getAuthor();
        if (infoValue != null)
            convertedInfo[1] = infoValue;
        infoValue = info.getSubject();
        if (infoValue != null)
            convertedInfo[2] = infoValue;
        infoValue = info.getKeywords();
        if (infoValue != null)
            convertedInfo[3] = infoValue;
        return convertedInfo;
    }


    private class PngFileFilter implements FileFilter {

        public boolean accept(File pathname) {
            String ap = pathname.getName();
            boolean b1 = ap.endsWith(".png");
            boolean b2 = ap.contains("cmp_");
            return b1 && !b2 && ap.contains(outPdfName);
        }
    }

    private class CmpPngFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            String ap = pathname.getName();
            boolean b1 = ap.endsWith(".png");
            boolean b2 = ap.contains("cmp_");
            return b1 && b2 && ap.contains(cmpPdfName);
        }
    }

    private class DiffPngFileFilter implements FileFilter {
        private String differenceImagePrefix;

        public DiffPngFileFilter(String differenceImagePrefix) {
            this.differenceImagePrefix = differenceImagePrefix;
        }

        public boolean accept(File pathname) {
            String ap = pathname.getName();
            boolean b1 = ap.endsWith(".png");
            boolean b2 = ap.startsWith(differenceImagePrefix);
            return b1 && b2;
        }
    }

    private class ImageNameComparator implements Comparator<File> {
        public int compare(File f1, File f2) {
            String f1Name = f1.getName();
            String f2Name = f2.getName();
            return f1Name.compareTo(f2Name);
        }
    }

    public class CompareResult {
        // LinkedHashMap to retain order. HashMap has different order in Java6/7 and Java8
        protected Map<ObjectPath, String> differences = new LinkedHashMap<>();
        protected int messageLimit = 1;

        public CompareResult(int messageLimit) {
            this.messageLimit = messageLimit;
        }

        public boolean isOk() {
            return differences.size() == 0;
        }

        public int getErrorCount() {
            return differences.size();
        }

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

        public Map<ObjectPath, String> getDifferences() {
            return differences;
        }

        public void writeReportToXml(OutputStream stream) throws ParserConfigurationException, TransformerException {
            Document xmlReport = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
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

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(xmlReport);
            StreamResult result = new StreamResult(stream);
            transformer.transform(source, result);
        }

        protected boolean isMessageLimitReached() {
            return differences.size() >= messageLimit;
        }

        protected void addError(ObjectPath path, String message) {
            if (differences.size() < messageLimit) {
                differences.put(((ObjectPath) path.clone()), message);
            }
        }
    }

    public class ObjectPath {
        protected PdfIndirectReference baseCmpObject;
        protected PdfIndirectReference baseOutObject;
        protected Stack<PathItem> path = new Stack<PathItem>();
        protected Stack<Pair<PdfIndirectReference>> indirects = new Stack<Pair<PdfIndirectReference>>();

        public ObjectPath() {
        }

        protected ObjectPath(PdfIndirectReference baseCmpObject, PdfIndirectReference baseOutObject) {
            this.baseCmpObject = baseCmpObject;
            this.baseOutObject = baseOutObject;
        }

        private ObjectPath(PdfIndirectReference baseCmpObject, PdfIndirectReference baseOutObject, Stack<PathItem> path) {
            this.baseCmpObject = baseCmpObject;
            this.baseOutObject = baseOutObject;
            this.path = path;
        }


        public ObjectPath resetDirectPath(PdfIndirectReference baseCmpObject, PdfIndirectReference baseOutObject) {
            ObjectPath newPath = new ObjectPath(baseCmpObject, baseOutObject);
            newPath.indirects = (Stack<Pair<PdfIndirectReference>>) indirects.clone();
            newPath.indirects.add(new Pair<>(baseCmpObject, baseOutObject));
            return newPath;
        }

        public boolean isComparing(PdfIndirectReference baseCmpObject, PdfIndirectReference baseOutObject) {
            return indirects.contains(new Pair<>(baseCmpObject, baseOutObject));
        }

        public void pushArrayItemToPath(int index) {
            path.add(new ArrayPathItem(index));
        }

        public void pushDictItemToPath(PdfName key) {
            path.add(new DictPathItem(key));
        }

        public void pushOffsetToPath(int offset) {
            path.add(new OffsetPathItem(offset));
        }

        public void pop() {
            path.pop();
        }

        public Stack<PathItem> getPath() {
            return path;
        }

        public PdfIndirectReference getBaseCmpObject() {
            return baseCmpObject;
        }

        public PdfIndirectReference getBaseOutObject() {
            return baseOutObject;
        }

        public Node toXmlNode(Document document) {
            Element element = document.createElement("path");
            Element baseNode = document.createElement("base");
            baseNode.setAttribute("cmp", MessageFormat.format("{0} {1} obj", baseCmpObject.getObjNumber(), baseCmpObject.getGenNumber()));
            baseNode.setAttribute("out", MessageFormat.format("{0} {1} obj", baseOutObject.getObjNumber(), baseOutObject.getGenNumber()));
            element.appendChild(baseNode);
            for (PathItem pathItem : path) {
                element.appendChild(pathItem.toXmlNode(document));
            }
            return element;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(MessageFormat.format("Base cmp object: {0} obj. Base out object: {1} obj", baseCmpObject, baseOutObject));
            for (PathItem pathItem : path) {
                sb.append("\n");
                sb.append(pathItem.toString());
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int hashCode = (baseCmpObject != null ? baseCmpObject.hashCode() : 0) * 31 + (baseOutObject != null ? baseOutObject.hashCode() : 0);
            for (PathItem pathItem : path) {
                hashCode *= 31;
                hashCode += pathItem.hashCode();
            }
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ObjectPath && baseCmpObject.equals(((ObjectPath) obj).baseCmpObject) && baseOutObject.equals(((ObjectPath) obj).baseOutObject) &&
                    path.equals(((ObjectPath) obj).path);
        }

        @Override
        protected Object clone() {
            return new ObjectPath(baseCmpObject, baseOutObject, (Stack<PathItem>) path.clone());
        }

        private class Pair<T> {
            private T first;
            private T second;

            public Pair(T first, T second) {
                this.first = first;
                this.second = second;
            }

            @Override
            public int hashCode() {
                return first.hashCode() * 31 + second.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return (obj instanceof Pair && first.equals(((Pair) obj).first) && second.equals(((Pair) obj).second));
            }
        }

        public abstract class PathItem {
            protected abstract Node toXmlNode(Document document);
        }

        public class DictPathItem extends PathItem {
            PdfName key;
            public DictPathItem(PdfName key) {
                this.key = key;
            }

            @Override
            public String toString() {
                return "Dict key: " + key;
            }

            @Override
            public int hashCode() {
                return key.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof DictPathItem && key.equals(((DictPathItem) obj).key);
            }

            @Override
            protected Node toXmlNode(Document document) {
                Node element = document.createElement("dictKey");
                element.appendChild(document.createTextNode(key.toString()));
                return element;
            }

            public PdfName getKey() {
                return key;
            }
        }

        public class ArrayPathItem extends PathItem {
            int index;
            public ArrayPathItem(int index) {
                this.index = index;
            }

            @Override
            public String toString() {
                return "Array index: " + String.valueOf(index);
            }

            @Override
            public int hashCode() {
                return index;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof ArrayPathItem && index == ((ArrayPathItem) obj).index;
            }

            @Override
            protected Node toXmlNode(Document document) {
                Node element = document.createElement("arrayIndex");
                element.appendChild(document.createTextNode(String.valueOf(index)));
                return element;
            }

            public int getIndex() {
                return index;
            }
        }

        private class OffsetPathItem extends PathItem {
            int offset;
            public OffsetPathItem(int offset) {
                this.offset = offset;
            }

            @Override
            public String toString() {
                return "Offset: " + String.valueOf(offset);
            }

            @Override
            public int hashCode() {
                return offset;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof OffsetPathItem && offset == ((OffsetPathItem) obj).offset;
            }

            @Override
            protected Node toXmlNode(Document document) {
                Node element = document.createElement("offset");
                element.appendChild(document.createTextNode(String.valueOf(offset)));
                return element;
            }
        }
    }

    private class TrailerPath extends ObjectPath {
        private PdfDocument outDocument;
        private PdfDocument cmpDocument;

        public TrailerPath(PdfDocument cmpDoc, PdfDocument outDoc) {
            outDocument = outDoc;
            cmpDocument = cmpDoc;
        }


        public TrailerPath(PdfDocument cmpDoc, PdfDocument outDoc, Stack<PathItem> path) {
            this.outDocument = outDoc;
            this.cmpDocument = cmpDoc;
            this.path = path;
        }

        @Override
        public Node toXmlNode(Document document) {
            Element element = document.createElement("path");
            Element baseNode = document.createElement("base");
            baseNode.setAttribute("cmp", "trailer");
            baseNode.setAttribute("out", "trailer");
            element.appendChild(baseNode);
            for (PathItem pathItem : path) {
                element.appendChild(pathItem.toXmlNode(document));
            }
            return element;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Base cmp object: trailer. Base out object: trailer");
            for (PathItem pathItem : path) {
                sb.append("\n");
                sb.append(pathItem.toString());
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int hashCode = outDocument.hashCode() * 31 + cmpDocument.hashCode();
            for (PathItem pathItem : path) {
                hashCode *= 31;
                hashCode += pathItem.hashCode();
            }
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TrailerPath
                    && outDocument.equals(((TrailerPath) obj).outDocument)
                    && cmpDocument.equals(((TrailerPath) obj).cmpDocument)
                    && path.equals(((ObjectPath) obj).path);
        }

        @Override
        protected Object clone() {
            return new TrailerPath(cmpDocument, outDocument, (Stack<PathItem>) path.clone());
        }

    }
}
