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

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.GhostscriptHelper;
import com.itextpdf.io.util.ImageMagickHelper;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.io.util.XmlUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNameTree;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.objectpathitems.ObjectPath;
import com.itextpdf.kernel.utils.objectpathitems.TrailerPath;
import com.itextpdf.kernel.xmp.PdfConst;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.XMPUtils;
import com.itextpdf.kernel.xmp.options.ParseOptions;
import com.itextpdf.kernel.xmp.options.SerializeOptions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This class provides means to compare two PDF files both by content and visually
 * and gives the report on their differences.
 * <p>
 * For visual comparison it uses external tools: Ghostscript and ImageMagick, which
 * should be installed on your machine. To allow CompareTool to use them, you need
 * to pass either java properties or environment variables with names "ITEXT_GS_EXEC" and
 * "ITEXT_MAGICK_COMPARE_EXEC", which would contain the commands to execute the
 * Ghostscript and ImageMagick tools.
 * <p>
 * CompareTool class was mainly designed for the testing purposes of iText in order to
 * ensure that the same code produces the same PDF document. For this reason you will
 * often encounter such parameter names as "outDoc" and "cmpDoc" which stand for output
 * document and document-for-comparison. The first one is viewed as the current result,
 * and the second one is referred as normal or ideal result. OutDoc is compared to the
 * ideal cmpDoc. Therefore all reports of the comparison are in the form: "Expected ...,
 * but was ...". This should be interpreted in the following way: "expected" part stands
 * for the content of the cmpDoc and "but was" part stands for the content of the outDoc.
 */
public class CompareTool {
    private static final String FILE_PROTOCOL = "file://";
    private static final String UNEXPECTED_NUMBER_OF_PAGES = "Unexpected number of pages for <filename>.";
    private static final String DIFFERENT_PAGES = "File " + FILE_PROTOCOL + "<filename> differs on page <pagenumber>.";
    private static final String IGNORED_AREAS_PREFIX = "ignored_areas_";

    private static final String VERSION_REGEXP = "(\\d+\\.)+\\d+(-SNAPSHOT)?";
    private static final String VERSION_REPLACEMENT = "<version>";
    private static final String COPYRIGHT_REGEXP = "\u00a9\\d+-\\d+ (?:iText Group NV|Apryse Group NV)";
    private static final String COPYRIGHT_REPLACEMENT = "\u00a9<copyright years> Apryse Group NV";
    private static final boolean MEMORY_FIRST_WRITER_DISABLED;

    private static final String NEW_LINES = "\\r|\\n";

    private String cmpPdfName;
    private String outPdfName;
    private String cmpPdf;
    private String cmpImage;
    private String outPdf;
    private String outImage;

    private ReaderProperties outProps;
    private ReaderProperties cmpProps;

    private List<PdfIndirectReference> outPagesRef;
    private List<PdfIndirectReference> cmpPagesRef;

    private int compareByContentErrorsLimit = 1000;
    private boolean generateCompareByContentXmlReport = false;

    private boolean encryptionCompareEnabled = false;
    private boolean kdfSaltCompareEnabled = true;

    private boolean useCachedPagesForComparison = true;
    private IMetaInfo metaInfo;

    private String gsExec;
    private String compareExec;

    static {
        MEMORY_FIRST_WRITER_DISABLED = "true".equalsIgnoreCase(
                SystemUtil.getPropertyOrEnvironmentVariable("DISABLE_MEMORY_FIRST_WRITER"));
    }

    /**
     * Create new {@link CompareTool} instance.
     */
    public CompareTool() {
    }

    CompareTool(String gsExec, String compareExec) {
        this.gsExec = gsExec;
        this.compareExec = compareExec;
    }

    /**
     * Create {@link PdfWriter} optimized for tests.
     *
     * @param filename File to write to when necessary.
     * @return {@link PdfWriter} to be used in tests.
     * @throws FileNotFoundException if the file exists but is a directory
     *                               rather than a regular file, does not exist but cannot
     *                               be created, or cannot be opened for any other reason.
     */
    public static PdfWriter createTestPdfWriter(String filename) throws IOException {
        return createTestPdfWriter(filename, new WriterProperties());
    }

    /**
     * Create {@link PdfWriter} optimized for tests.
     *
     * @param filename File to write to when necessary.
     * @param properties {@link WriterProperties} to use.
     * @return {@link PdfWriter} to be used in tests.
     * @throws FileNotFoundException if the file exists but is a directory
     *                               rather than a regular file, does not exist but cannot
     *                               be created, or cannot be opened for any other reason.
     */
    public static PdfWriter createTestPdfWriter(String filename, WriterProperties properties) throws IOException {
        if (MEMORY_FIRST_WRITER_DISABLED) {
            return new PdfWriter(filename, properties);
        } else {
            return new MemoryFirstPdfWriter(filename, properties); // Android-Conversion-Replace return new PdfWriter(filename, properties);
        }
    }

    /**
     * Create {@link PdfReader} out of the data created recently or read from disk.
     *
     * @param filename File to read the data from when necessary.
     * @param properties {@link ReaderProperties} to use.
     * @return {@link PdfReader} to be used in tests.
     * @throws IOException on error
     */
    public static PdfReader createOutputReader(String filename, ReaderProperties properties) throws IOException {
        MemoryFirstPdfWriter outWriter = MemoryFirstPdfWriter.get(filename);
        if (outWriter != null) {
            return new PdfReader(new ByteArrayInputStream(outWriter.getBAOutputStream().toByteArray()), properties);
        } else {
            return new PdfReader(filename, properties);
        }
    }

    /**
     * Create {@link PdfReader} out of the data created recently or read from disk.
     *
     * @param filename File to read the data from when necessary.
     * @return {@link PdfReader} to be used in tests.
     * @throws IOException on error
     */
    public static PdfReader createOutputReader(String filename) throws IOException {
        return CompareTool.createOutputReader(filename, new ReaderProperties());
    }

    /**
     * Clean up memory occupied for the tests.
     *
     * @param path Path to clean up memory for.
     */
    public static void cleanup(String path) {
        MemoryFirstPdfWriter.cleanup(path);
    }

    /**
     * Compares two PDF documents by content starting from Catalog dictionary and then recursively comparing
     * corresponding objects which are referenced from it. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <p>
     * The main difference between this method and the {@link #compareByContent(String, String, String, String)}
     * methods is the return value. This method returns a {@link CompareResult} class instance, which could be used
     * in code, whilst compareByContent methods in case of the differences simply return String value, which could
     * only be printed. Also, keep in mind that this method doesn't perform visual comparison of the documents.
     * <p>
     * For more explanations about what outDoc and cmpDoc are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outDocument a {@link PdfDocument} corresponding to the output file, which is to be compared with cmp-file.
     * @param cmpDocument a {@link PdfDocument} corresponding to the cmp-file, which is to be compared with output file.
     * @return the report on comparison of two files in the form of the custom class {@link CompareResult} instance.
     * @see CompareResult
     */
    public CompareResult compareByCatalog(PdfDocument outDocument, PdfDocument cmpDocument) {
        CompareResult compareResult = null;
        compareResult = new CompareResult(compareByContentErrorsLimit);
        ObjectPath catalogPath = new ObjectPath(cmpDocument.getCatalog().getPdfObject().getIndirectReference(),
                outDocument.getCatalog().getPdfObject().getIndirectReference());
        Set<PdfName> ignoredCatalogEntries = new LinkedHashSet<>(Arrays.asList(PdfName.Metadata));
        compareDictionariesExtended(outDocument.getCatalog().getPdfObject(), cmpDocument.getCatalog().getPdfObject(),
                catalogPath, compareResult, ignoredCatalogEntries);

        // Method compareDictionariesExtended eventually calls compareObjects method which doesn't compare page objects.
        // At least for now compare page dictionaries explicitly here like this.
        if (cmpPagesRef == null || outPagesRef == null) {
            return compareResult;
        }

        if (outPagesRef.size() != cmpPagesRef.size() && !compareResult.isMessageLimitReached()) {
            compareResult.addError(catalogPath, "Documents have different numbers of pages.");
        }
        for (int i = 0; i < Math.min(cmpPagesRef.size(), outPagesRef.size()); i++) {
            if (compareResult.isMessageLimitReached()) {
                break;
            }
            ObjectPath currentPath = new ObjectPath(cmpPagesRef.get(i), outPagesRef.get(i));
            PdfDictionary outPageDict = (PdfDictionary) outPagesRef.get(i).getRefersTo();
            PdfDictionary cmpPageDict = (PdfDictionary) cmpPagesRef.get(i).getRefersTo();
            compareDictionariesExtended(outPageDict, cmpPageDict, currentPath, compareResult);
        }
        return compareResult;
    }

    /**
     * Disables the default logic of pages comparison.
     * This option makes sense only for {@link CompareTool#compareByCatalog(PdfDocument, PdfDocument)} method.
     * <p>
     * By default, pages are treated as special objects and if they are met in the process of comparison, then they are
     * not checked as objects, but rather simply checked that they have same page numbers in both documents.
     * This behaviour is intended for the {@link CompareTool#compareByContent}
     * set of methods, because in them documents are compared in page by page basis. Thus, we don't need to check if pages
     * are of the same content when they are met in comparison process, we are sure that we will compare their content or
     * we have already compared them.
     * <p>
     * However, if you would use {@link CompareTool#compareByCatalog} with default behaviour
     * of pages comparison, pages won't be checked at all, every time when reference to the page dictionary is met,
     * only page numbers will be compared for both documents. You can say that in this case, comparison will be performed
     * for all document's catalog entries except /Pages (However in fact, document's page tree structures will be compared,
     * but pages themselves - won't).
     *
     * @return this {@link CompareTool} instance.
     */
    public CompareTool disableCachedPagesComparison() {
        this.useCachedPagesForComparison = false;
        return this;
    }

    /**
     * Sets the maximum errors count which will be returned as the result of the comparison.
     *
     * @param compareByContentMaxErrorCount the errors count.
     * @return this CompareTool instance.
     */
    public CompareTool setCompareByContentErrorsLimit(int compareByContentMaxErrorCount) {
        this.compareByContentErrorsLimit = compareByContentMaxErrorCount;
        return this;
    }

    /**
     * Enables or disables the generation of the comparison report in the form of an xml document.
     * <p>
     * IMPORTANT NOTE: this flag affects only the comparison performed by compareByContent methods!
     *
     * @param generateCompareByContentXmlReport true to enable xml report generation, false - to disable.
     * @return this CompareTool instance.
     */
    public CompareTool setGenerateCompareByContentXmlReport(boolean generateCompareByContentXmlReport) {
        this.generateCompareByContentXmlReport = generateCompareByContentXmlReport;
        return this;
    }

    /**
     * Sets {@link IMetaInfo} info that will be used for both read and written documents creation.
     *
     * @param metaInfo meta info to set
     */
    public void setEventCountingMetaInfo(IMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    /**
     * Enables the comparison of the encryption properties of the documents. Encryption properties comparison
     * results are returned along with all other comparison results.
     * <p>
     * IMPORTANT NOTE: this flag affects only the comparison performed by compareByContent methods!
     * {@link #compareByCatalog(PdfDocument, PdfDocument)} doesn't compare encryption properties
     * because encryption properties aren't part of the document's Catalog.
     *
     * @return this CompareTool instance.
     */
    public CompareTool enableEncryptionCompare() {
        return enableEncryptionCompare(true);
    }

    /**
     * Enables the comparison of the encryption properties of the documents. Encryption properties comparison
     * results are returned along with all other comparison results.
     * <p>
     * IMPORTANT NOTE: this flag affects only the comparison performed by compareByContent methods!
     * {@link #compareByCatalog(PdfDocument, PdfDocument)} doesn't compare encryption properties
     * because encryption properties aren't part of the document's Catalog.
     *
     * @param kdfSaltCompareEnabled set to {@code true} if {@link PdfName#KDFSalt} entry must be compared,
     *                             {code false} otherwise
     * @return this CompareTool instance.
     */
    public CompareTool enableEncryptionCompare(boolean kdfSaltCompareEnabled) {
        this.encryptionCompareEnabled = true;
        this.kdfSaltCompareEnabled = kdfSaltCompareEnabled;
        return this;
    }

    /**
     * Gets {@link ReaderProperties} to be passed later to the {@link PdfReader} of the cmp document.
     * <p>
     * Documents for comparison are opened in reader mode. This method is intended to alter {@link ReaderProperties}
     * which are used to open the cmp document. This is particularly useful for comparison of encrypted documents.
     * <p>
     * For more explanations about what outDoc and cmpDoc are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @return {@link ReaderProperties} instance to be passed later to the {@link PdfReader} of the cmp document.
     */
    public ReaderProperties getCmpReaderProperties() {
        if (cmpProps == null) {
            cmpProps = new ReaderProperties();
        }
        return cmpProps;
    }

    /**
     * Gets {@link ReaderProperties} to be passed later to the {@link PdfReader} of the output document.
     * <p>
     * Documents for comparison are opened in reader mode. This method is intended to alter {@link ReaderProperties}
     * which are used to open the output document. This is particularly useful for comparison of encrypted documents.
     * <p>
     * For more explanations about what outDoc and cmpDoc are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @return {@link ReaderProperties} instance to be passed later to the {@link PdfReader} of the output document.
     */
    public ReaderProperties getOutReaderProperties() {
        if (outProps == null) {
            outProps = new ReaderProperties();
        }
        return outProps;
    }

    /**
     * Compares two documents visually. For the comparison two external tools are used: Ghostscript and ImageMagick.
     * For more info about needed configuration for visual comparison process see {@link CompareTool} class description.
     * <p>
     * Note, that this method uses {@link ImageMagickHelper} and {@link GhostscriptHelper} classes and therefore may
     * create temporary files and directories.
     * <p>
     * During comparison for every page of the two documents an image file will be created in the folder specified by
     * outPath parameter. Then those page images will be compared and if there are any differences for some pages,
     * another image file will be created with marked differences on it.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked differences if there is any.
     * @return string containing list of the pages that are visually different, or null if there are no visual differences.
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting
     *                              for ghostscript or imagemagic processes, then the wait is ended and
     *                              an {@link InterruptedException} is thrown.
     * @throws IOException          is thrown if any of the input files are missing or any of the auxiliary files
     *                              that are created during comparison process weren't possible to be created.
     */
    public String compareVisually(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix) throws InterruptedException, IOException {
        return compareVisually(outPdf, cmpPdf, outPath, differenceImagePrefix, null);
    }

    /**
     * Compares two documents visually. For the comparison two external tools are used: Ghostscript and ImageMagick.
     * For more info about needed configuration for visual comparison process see {@link CompareTool} class description.
     * <p>
     * Note, that this method uses {@link ImageMagickHelper} and {@link GhostscriptHelper} classes and therefore may
     * create temporary files and directories.
     * <p>
     * During comparison for every page of two documents an image file will be created in the folder specified by
     * outPath parameter. Then those page images will be compared and if there are any differences for some pages,
     * another image file will be created with marked differences on it.
     * <p>
     * It is possible to ignore certain areas of the document pages during visual comparison. This is useful for example
     * in case if documents should be the same except certain page area with date on it. In this case, in the folder
     * specified by the outPath, new pdf documents will be created with the black rectangles at the specified ignored
     * areas, and visual comparison will be performed on these new documents.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked differences if there is any.
     * @param ignoredAreas          a map with one-based page numbers as keys and lists of ignored rectangles as values.
     * @return string containing list of the pages that are visually different, or null if there are no visual differences.
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting
     *                              for ghostscript or imagemagic processes, then the wait is ended and
     *                              an {@link InterruptedException} is thrown.
     * @throws IOException          is thrown if any of the input files are missing or any of the auxiliary files
     *                              that are created during comparison process weren't possible to be created.
     */
    public String compareVisually(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        init(outPdf, cmpPdf);
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outPdf));
        System.out.println("Cmp pdf: " + UrlUtil.getNormalizedFileUriString(cmpPdf)+ "\n");
        return compareVisually(outPath, differenceImagePrefix, ignoredAreas);
    }

    /**
     * Compares two PDF documents by content starting from page dictionaries and then recursively comparing
     * corresponding objects which are referenced from them. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <p>
     * When comparison by content is finished, if any differences were found, visual comparison is automatically started.
     * For this overload, differenceImagePrefix value is generated using diff_%outPdfFileName%_ format.
     * <p>
     * For more explanations about what outPdf and cmpPdf are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outPdf  the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf  the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath the absolute path to the folder, which will be used to store image files for visual comparison.
     * @return string containing text report on the encountered content differences and also list of the pages that are
     * visually different, or null if there are no content and therefore no visual differences.
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting
     *                              for ghostscript or imagemagic processes, then the wait is ended and an {@link InterruptedException} is thrown.
     * @throws IOException          is thrown if any of the input files are missing or any of the auxiliary files
     *                              that are created during comparison process weren't possible to be created.
     * @see #compareVisually(String, String, String, String)
     */
    public String compareByContent(String outPdf, String cmpPdf, String outPath) throws InterruptedException, IOException {
        return compareByContent(outPdf, cmpPdf, outPath, null, null, null, null);
    }

    /**
     * Compares two PDF documents by content starting from page dictionaries and then recursively comparing
     * corresponding objects which are referenced from them. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <p>
     * When comparison by content is finished, if any differences were found, visual comparison is automatically started.
     * <p>
     * For more explanations about what outPdf and cmpPdf are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked visual differences if there are any;
     *                              if it's set to null the prefix defaults to diff_%outPdfFileName%_ format.
     * @return string containing text report on the encountered content differences and also list of the pages that are
     * visually different, or null if there are no content and therefore no visual differences.
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting
     *                              for ghostscript or imagemagic processes, then the wait is ended and an {@link InterruptedException} is thrown.
     * @throws IOException          is thrown if any of the input files are missing or any of the auxiliary files
     *                              that are created during comparison process weren't possible to be created.
     * @see #compareVisually(String, String, String, String)
     */
    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix) throws InterruptedException, IOException {
        return compareByContent(outPdf, cmpPdf, outPath, differenceImagePrefix, null, null, null);
    }

    /**
     * This method overload is used to compare two encrypted PDF documents. Document passwords are passed with
     * outPass and cmpPass parameters.
     * <p>
     * Compares two PDF documents by content starting from page dictionaries and then recursively comparing
     * corresponding objects which are referenced from them. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <p>
     * When comparison by content is finished, if any differences were found, visual comparison is automatically started.
     * For more info see {@link #compareVisually(String, String, String, String)}.
     * <p>
     * For more explanations about what outPdf and cmpPdf are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked visual differences if there is any;
     *                              if it's set to null the prefix defaults to diff_%outPdfFileName%_ format.
     * @param outPass               password for the encrypted document specified by the outPdf absolute path.
     * @param cmpPass               password for the encrypted document specified by the cmpPdf absolute path.
     * @return string containing text report on the encountered content differences and also list of the pages that are
     * visually different, or null if there are no content and therefore no visual differences.
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting
     *                              for ghostscript or imagemagic processes, then the wait is ended and an {@link InterruptedException} is thrown.
     * @throws IOException          is thrown if any of the input files are missing or any of the auxiliary files
     *                              that are created during comparison process weren't possible to be created.
     * @see #compareVisually(String, String, String, String)
     */
    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, byte[] outPass, byte[] cmpPass) throws InterruptedException, IOException {
        return compareByContent(outPdf, cmpPdf, outPath, differenceImagePrefix, null, outPass, cmpPass);
    }

    /**
     * Compares two PDF documents by content starting from page dictionaries and then recursively comparing
     * corresponding objects which are referenced from them. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <p>
     * When comparison by content is finished, if any differences were found, visual comparison is automatically started.
     * <p>
     * For more explanations about what outPdf and cmpPdf are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked visual differences if there are any;
     *                              if it's set to null the prefix defaults to diff_%outPdfFileName%_ format.
     * @param ignoredAreas          a map with one-based page numbers as keys and lists of ignored rectangles as values.
     * @return string containing text report on the encountered content differences and also list of the pages that are
     * visually different, or null if there are no content and therefore no visual differences.
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting
     *                              for ghostscript or imagemagic processes, then the wait is ended and an {@link InterruptedException} is thrown.
     * @throws IOException          is thrown if any of the input files are missing or any of the auxiliary files
     *                              that are created during comparison process weren't possible to be created.
     * @see #compareVisually(String, String, String, String)
     */
    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        return compareByContent(outPdf, cmpPdf, outPath, differenceImagePrefix, ignoredAreas, null, null);
    }

    /**
     * This method overload is used to compare two encrypted PDF documents. Document passwords are passed with
     * outPass and cmpPass parameters.
     * <p>
     * Compares two PDF documents by content starting from page dictionaries and then recursively comparing
     * corresponding objects which are referenced from them. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <p>
     * When comparison by content is finished, if any differences were found, visual comparison is automatically started.
     * <p>
     * For more explanations about what outPdf and cmpPdf are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked visual differences if there are any;
     *                              if it's set to null the prefix defaults to diff_%outPdfFileName%_ format.
     * @param ignoredAreas          a map with one-based page numbers as keys and lists of ignored rectangles as values.
     * @param outPass               password for the encrypted document specified by the outPdf absolute path.
     * @param cmpPass               password for the encrypted document specified by the cmpPdf absolute path.
     * @return string containing text report on the encountered content differences and also list of the pages that are
     * visually different, or null if there are no content and therefore no visual differences.
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting
     *                              for ghostscript or imagemagic processes, then the wait is ended and an {@link InterruptedException} is thrown.
     * @throws IOException          is thrown if any of the input files are missing or any of the auxiliary files
     *                              that are created during comparison process weren't possible to be created.
     * @see #compareVisually(String, String, String, String)
     */
    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas, byte[] outPass, byte[] cmpPass) throws InterruptedException, IOException {
        init(outPdf, cmpPdf);
        System.out.println("Out pdf: " + UrlUtil.getNormalizedFileUriString(outPdf));
        System.out.println("Cmp pdf: " + UrlUtil.getNormalizedFileUriString(cmpPdf)+ "\n");
        setPassword(outPass, cmpPass);
        return compareByContent(outPath, differenceImagePrefix, ignoredAreas);
    }

    /**
     * Simple method that compares two given PdfDictionaries by content. This is "deep" comparing, which means that all
     * nested objects are also compared by content.
     *
     * @param outDict dictionary to compare.
     * @param cmpDict dictionary to compare.
     * @return true if dictionaries are equal by content, otherwise false.
     */
    public boolean compareDictionaries(PdfDictionary outDict, PdfDictionary cmpDict) {
        return compareDictionariesExtended(outDict, cmpDict, null, null);
    }

    /**
     * Recursively compares structures of two corresponding dictionaries from out and cmp PDF documents. You can roughly
     * imagine it as depth-first traversal of the two trees that represent pdf objects structure of the documents.
     * <p>
     * Both out and cmp {@link PdfDictionary} shall have indirect references.
     * <p>
     * By default page dictionaries are excluded from the comparison when met and are instead compared in a special manner,
     * simply comparing their page numbers. This behavior can be disabled by calling {@link #disableCachedPagesComparison()}.
     * <p>
     * For more explanations about what outPdf and cmpPdf are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outDict an indirect {@link PdfDictionary} from the output file, which is to be compared to cmp-file dictionary.
     * @param cmpDict an indirect {@link PdfDictionary} from the cmp-file file, which is to be compared to output file dictionary.
     * @return {@link CompareResult} instance containing differences between the two dictionaries,
     * or {@code null} if dictionaries are equal.
     */
    public CompareResult compareDictionariesStructure(PdfDictionary outDict, PdfDictionary cmpDict) {
        return compareDictionariesStructure(outDict, cmpDict, null);
    }

    /**
     * Recursively compares structures of two corresponding dictionaries from out and cmp PDF documents. You can roughly
     * imagine it as depth-first traversal of the two trees that represent pdf objects structure of the documents.
     * <p>
     * Both out and cmp {@link PdfDictionary} shall have indirect references.
     * <p>
     * By default page dictionaries are excluded from the comparison when met and are instead compared in a special manner,
     * simply comparing their page numbers. This behavior can be disabled by calling {@link #disableCachedPagesComparison()}.
     * <p>
     * For more explanations about what outPdf and cmpPdf are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outDict      an indirect {@link PdfDictionary} from the output file, which is to be compared to cmp-file dictionary.
     * @param cmpDict      an indirect {@link PdfDictionary} from the cmp-file file, which is to be compared to output file dictionary.
     * @param excludedKeys a {@link Set} of names that designate entries from {@code outDict} and {@code cmpDict} dictionaries
     *                     which are to be skipped during comparison.
     * @return {@link CompareResult} instance containing differences between the two dictionaries,
     * or {@code null} if dictionaries are equal.
     */
    public CompareResult compareDictionariesStructure(PdfDictionary outDict, PdfDictionary cmpDict, Set<PdfName> excludedKeys) {
        if (outDict.getIndirectReference() == null || cmpDict.getIndirectReference() == null) {
            throw new IllegalArgumentException("The 'outDict' and 'cmpDict' objects shall have indirect references.");
        }

        CompareResult compareResult = new CompareResult(compareByContentErrorsLimit);
        final ObjectPath currentPath = new ObjectPath(cmpDict.getIndirectReference(), outDict.getIndirectReference());
        if (!compareDictionariesExtended(outDict, cmpDict, currentPath, compareResult, excludedKeys)) {
            assert !compareResult.isOk();
            System.out.println(compareResult.getReport());
            return compareResult;
        }
        assert compareResult.isOk();
        return null;
    }

    /**
     * Compares structures of two corresponding streams from out and cmp PDF documents. You can roughly
     * imagine it as depth-first traversal of the two trees that represent pdf objects structure of the documents.
     * <p>
     * For more explanations about what outPdf and cmpPdf are see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outStream      a {@link PdfStream} from the output file, which is to be compared to cmp-file stream.
     * @param cmpStream     a {@link PdfStream} from the cmp-file file, which is to be compared to output file stream.
     * @return {@link CompareResult} instance containing differences between the two streams,
     * or {@code null} if streams are equal.
     */
    public CompareResult compareStreamsStructure(PdfStream outStream, PdfStream cmpStream) {
        CompareResult compareResult = new CompareResult(compareByContentErrorsLimit);
        final ObjectPath currentPath = new ObjectPath(cmpStream.getIndirectReference(),
                outStream.getIndirectReference());
        if (!compareStreamsExtended(outStream, cmpStream, currentPath, compareResult)) {
            assert !compareResult.isOk();
            System.out.println(compareResult.getReport());
            return compareResult;
        }
        assert compareResult.isOk();
        return null;
    }

    /**
     * Simple method that compares two given PdfStreams by content. This is "deep" comparing, which means that all
     * nested objects are also compared by content.
     *
     * @param outStream stream to compare.
     * @param cmpStream stream to compare.
     * @return true if stream are equal by content, otherwise false.
     */
    public boolean compareStreams(PdfStream outStream, PdfStream cmpStream) {
        return compareStreamsExtended(outStream, cmpStream, null, null);
    }

    /**
     * Simple method that compares two given PdfArrays by content. This is "deep" comparing, which means that all
     * nested objects are also compared by content.
     *
     * @param outArray array to compare.
     * @param cmpArray array to compare.
     * @return true if arrays are equal by content, otherwise false.
     */
    public boolean compareArrays(PdfArray outArray, PdfArray cmpArray) {
        return compareArraysExtended(outArray, cmpArray, null, null);
    }

    /**
     * Simple method that compares two given PdfNames.
     *
     * @param outName name to compare.
     * @param cmpName name to compare.
     * @return true if names are equal, otherwise false.
     */
    public boolean compareNames(PdfName outName, PdfName cmpName) {
        return cmpName.equals(outName);
    }

    /**
     * Simple method that compares two given PdfNumbers.
     *
     * @param outNumber number to compare.
     * @param cmpNumber number to compare.
     * @return true if numbers are equal, otherwise false.
     */
    public boolean compareNumbers(PdfNumber outNumber, PdfNumber cmpNumber) {
        return cmpNumber.getValue() == outNumber.getValue();
    }

    /**
     * Simple method that compares two given PdfStrings.
     *
     * @param outString string to compare.
     * @param cmpString string to compare.
     * @return true if strings are equal, otherwise false.
     */
    public boolean compareStrings(PdfString outString, PdfString cmpString) {
        return cmpString.getValue().equals(outString.getValue());
    }

    /**
     * Simple method that compares two given PdfBooleans.
     *
     * @param outBoolean boolean to compare.
     * @param cmpBoolean boolean to compare.
     * @return true if booleans are equal, otherwise false.
     */
    public boolean compareBooleans(PdfBoolean outBoolean, PdfBoolean cmpBoolean) {
        return cmpBoolean.getValue() == outBoolean.getValue();
    }

    /**
     * Compares xmp metadata of the two given PDF documents.
     *
     * @param outPdf the absolute path to the output file, which xmp is to be compared to cmp-file.
     * @param cmpPdf the absolute path to the cmp-file, which xmp is to be compared to output file.
     * @return text report on the xmp differences, or null if there are no differences.
     */
    public String compareXmp(String outPdf, String cmpPdf) {
        return compareXmp(outPdf, cmpPdf, false);
    }

    /**
     * Compares xmp metadata of the two given PDF documents.
     *
     * @param outPdf                          the absolute path to the output file, which xmp is to be compared to cmp-file.
     * @param cmpPdf                          the absolute path to the cmp-file, which xmp is to be compared to output file.
     * @param ignoreDateAndProducerProperties true, if to ignore differences in date or producer xmp metadata
     *                                        properties.
     * @return text report on the xmp differences, or null if there are no differences.
     */
    public String compareXmp(String outPdf, String cmpPdf, boolean ignoreDateAndProducerProperties) {
        init(outPdf, cmpPdf);
        try (PdfReader readerCmp = CompareTool.createOutputReader(this.cmpPdf);
                PdfDocument cmpDocument = new PdfDocument(readerCmp,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo));
                PdfReader readerOut = CompareTool.createOutputReader(this.outPdf);
                PdfDocument outDocument = new PdfDocument(readerOut,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo))) {
            byte[] cmpBytes = cmpDocument.getXmpMetadataBytes();
            byte[] outBytes = outDocument.getXmpMetadataBytes();
            if (ignoreDateAndProducerProperties) {
                XMPMeta xmpMeta = XMPMetaFactory.parseFromBuffer(cmpBytes, new ParseOptions().setOmitNormalization(true));

                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.CreateDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.ModifyDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.MetadataDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_PDF, PdfConst.Producer, true, true);

                cmpBytes = XMPMetaFactory.serializeToBuffer(xmpMeta, new SerializeOptions(SerializeOptions.SORT));

                xmpMeta = XMPMetaFactory.parseFromBuffer(outBytes, new ParseOptions().setOmitNormalization(true));
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.CreateDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.ModifyDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.MetadataDate, true, true);
                XMPUtils.removeProperties(xmpMeta, XMPConst.NS_PDF, PdfConst.Producer, true, true);

                outBytes = XMPMetaFactory.serializeToBuffer(xmpMeta, new SerializeOptions(SerializeOptions.SORT));
            }

            if (!compareXmls(cmpBytes, outBytes)) {
                return "The XMP packages different!";
            }
        } catch (Exception e) {
            return "XMP parsing failure!";
        }
        return null;
    }

    /**
     * Utility method that provides simple comparison of the two xml files stored in byte arrays.
     *
     * @param xml1 first xml file data to compare.
     * @param xml2 second xml file data to compare.
     * @return true if xml structures are identical, false otherwise.
     * @throws ParserConfigurationException if a XML DocumentBuilder cannot be created
     *                                      which satisfies the configuration requested.
     * @throws SAXException                 if any XML parse errors occur.
     * @throws IOException                  If any IO errors occur during reading XML files.
     */
    public boolean compareXmls(byte[] xml1, byte[] xml2) throws ParserConfigurationException, SAXException, IOException {
        return XmlUtils.compareXmls(new ByteArrayInputStream(xml1), new ByteArrayInputStream(xml2));
    }

    /**
     * Utility method that provides simple comparison of the two xml files.
     *
     * @param outXmlFile absolute path to the out xml file to compare.
     * @param cmpXmlFile absolute path to the cmp xml file to compare.
     * @return true if xml structures are identical, false otherwise.
     * @throws ParserConfigurationException if a XML DocumentBuilder cannot be created
     *                                      which satisfies the configuration requested.
     * @throws SAXException                 if any XML parse errors occur.
     * @throws IOException                  If any IO errors occur during reading XML files.
     */
    public boolean compareXmls(String outXmlFile, String cmpXmlFile) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("Out xml: " + UrlUtil.getNormalizedFileUriString(outXmlFile));
        System.out.println("Cmp xml: " + UrlUtil.getNormalizedFileUriString(cmpXmlFile) + "\n");
        try (InputStream outXmlStream = FileUtil.getInputStreamForFile(outXmlFile);
                InputStream cmpXmlStream = FileUtil.getInputStreamForFile(cmpXmlFile)) {
            return XmlUtils.compareXmls(outXmlStream, cmpXmlStream);
        }
    }

    /**
     * Compares document info dictionaries of two pdf documents.
     * <p>
     * This method overload is used to compare two encrypted PDF documents. Document passwords are passed with
     * outPass and cmpPass parameters.
     *
     * @param outPdf  the absolute path to the output file, which info is to be compared to cmp-file info.
     * @param cmpPdf  the absolute path to the cmp-file, which info is to be compared to output file info.
     * @param outPass password for the encrypted document specified by the outPdf absolute path.
     * @param cmpPass password for the encrypted document specified by the cmpPdf absolute path.
     * @return text report on the differences in documents infos.
     * @throws IOException if PDF reader cannot be created due to IO issues
     */
    public String compareDocumentInfo(String outPdf, String cmpPdf, byte[] outPass, byte[] cmpPass) throws IOException {
        System.out.print("[itext] INFO  Comparing document info.......");
        String message = null;
        setPassword(outPass, cmpPass);
        try (PdfReader readerOut = CompareTool.createOutputReader(outPdf, getOutReaderProperties());
                PdfDocument outDocument = new PdfDocument(readerOut,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo));
                PdfReader readerCmp = CompareTool.createOutputReader(cmpPdf, getCmpReaderProperties());
                PdfDocument cmpDocument = new PdfDocument(readerCmp,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo))) {
            String[] cmpInfo = convertDocInfoToStrings(cmpDocument.getDocumentInfo());
            String[] outInfo = convertDocInfoToStrings(outDocument.getDocumentInfo());
            for (int i = 0; i < cmpInfo.length; ++i) {
                if (!cmpInfo[i].equals(outInfo[i])) {
                    message = MessageFormatUtil.format("Document info fail. Expected: \"{0}\", actual: \"{1}\"", cmpInfo[i], outInfo[i]);
                    break;
                }
            }
        }
        if (message == null) {
            System.out.println("OK");
        } else {
            CompareTool.writeOnDisk(outPdf);
            CompareTool.writeOnDiskIfNotExists(cmpPdf);
            System.out.println("Fail");
        }
        System.out.flush();
        return message;
    }

    /**
     * Compares document info dictionaries of two pdf documents.
     *
     * @param outPdf the absolute path to the output file, which info is to be compared to cmp-file info.
     * @param cmpPdf the absolute path to the cmp-file, which info is to be compared to output file info.
     * @return text report on the differences in documents infos.
     * @throws IOException if PDF reader cannot be created due to IO issues
     */
    public String compareDocumentInfo(String outPdf, String cmpPdf) throws IOException {
        return compareDocumentInfo(outPdf, cmpPdf, null, null);
    }

    /**
     * Checks if two documents have identical link annotations on corresponding pages.
     *
     * @param outPdf the absolute path to the output file, which links are to be compared to cmp-file links.
     * @param cmpPdf the absolute path to the cmp-file, which links are to be compared to output file links.
     * @return text report on the differences in documents links.
     * @throws IOException if PDF reader cannot be created due to IO issues
     */
    public String compareLinkAnnotations(String outPdf, String cmpPdf) throws IOException {
        System.out.print("[itext] INFO  Comparing link annotations....");
        String message = null;
        try (PdfReader readerOut = CompareTool.createOutputReader(outPdf);
                PdfDocument outDocument = new PdfDocument(readerOut,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo));
                PdfReader readerCmp = CompareTool.createOutputReader(cmpPdf);
                PdfDocument cmpDocument = new PdfDocument(readerCmp,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo))){
            for (int i = 0; i < outDocument.getNumberOfPages() && i < cmpDocument.getNumberOfPages(); i++) {
                List<PdfLinkAnnotation> outLinks = getLinkAnnotations(i + 1, outDocument);
                List<PdfLinkAnnotation> cmpLinks = getLinkAnnotations(i + 1, cmpDocument);

                if (cmpLinks.size() != outLinks.size()) {
                    message = MessageFormatUtil.format("Different number of links on page {0}.", i + 1);
                    break;
                }
                for (int j = 0; j < cmpLinks.size(); j++) {
                    if (!compareLinkAnnotations(cmpLinks.get(j), outLinks.get(j), cmpDocument, outDocument)) {
                        message = MessageFormatUtil.format("Different links on page {0}.\n{1}\n{2}", i + 1, cmpLinks.get(j).toString(), outLinks.get(j).toString());
                        break;
                    }
                }
            }
        }
        if (message == null) {
            System.out.println("OK");
        } else {
            CompareTool.writeOnDisk(outPdf);
            CompareTool.writeOnDiskIfNotExists(cmpPdf);
            System.out.println("Fail");
        }
        System.out.flush();
        return message;
    }

    /**
     * Compares tag structures of the two PDF documents.
     * <p>
     * This method creates xml files in the same folder with outPdf file. These xml files contain documents tag structures
     * converted into the xml structure. These xml files are compared if they are equal.
     *
     * @param outPdf the absolute path to the output file, which tags are to be compared to cmp-file tags.
     * @param cmpPdf the absolute path to the cmp-file, which tags are to be compared to output file tags.
     * @return text report of the differences in documents tags.
     * @throws IOException                 is thrown if any of the input files are missing or any of the auxiliary files
     *                                     that are created during comparison process weren't possible to be created.
     * @throws ParserConfigurationException if a XML DocumentBuilder cannot be created
     *                                      which satisfies the configuration requested.
     * @throws SAXException                 if any XML parse errors occur.
     */
    public String compareTagStructures(String outPdf, String cmpPdf) throws IOException, ParserConfigurationException, SAXException {
        System.out.print("[itext] INFO  Comparing tag structures......");

        String outXmlPath = outPdf.replace(".pdf", ".xml");
        String cmpXmlPath = outPdf.replace(".pdf", ".cmp.xml");

        String message = null;
        try (PdfReader readerOut = CompareTool.createOutputReader(outPdf);
                PdfDocument docOut = new PdfDocument(readerOut,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo));
                OutputStream xmlOut = FileUtil.getFileOutputStream(outXmlPath)) {
            new TaggedPdfReaderTool(docOut).setRootTag("root").convertToXml(xmlOut);
        }
        try (PdfReader readerCmp = CompareTool.createOutputReader(cmpPdf);
                PdfDocument docCmp = new PdfDocument(readerCmp,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo));
                OutputStream xmlCmp = FileUtil.getFileOutputStream(cmpXmlPath)) {
            new TaggedPdfReaderTool(docCmp).setRootTag("root").convertToXml(xmlCmp);
        }

        if (!compareXmls(outXmlPath, cmpXmlPath)) {
            message = "The tag structures are different.";
        }
        if (message == null) {
            System.out.println("OK");
        } else {
            CompareTool.writeOnDisk(outPdf);
            CompareTool.writeOnDiskIfNotExists(cmpPdf);
            System.out.println("Fail");
        }
        System.out.flush();
        return message;
    }

    /**
     * Converts document info into a string array.
     * <p>
     * Converts document info into a string array. It can be used to compare PdfDocumentInfo later on.
     * Default implementation retrieves title, author, subject, keywords and producer.
     *
     * @param info an instance of PdfDocumentInfo to be converted.
     * @return String array with all the document info tester is interested in.
     */
    protected String[] convertDocInfoToStrings(PdfDocumentInfo info) {
        String[] convertedInfo = new String[]{"", "", "", "", ""};
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
        infoValue = info.getProducer();
        if (infoValue != null) {
            convertedInfo[4] = convertProducerLine(infoValue);
        }
        return convertedInfo;
    }

    String convertProducerLine(String producer) {
        return producer.replaceAll(VERSION_REGEXP, VERSION_REPLACEMENT).replaceAll(COPYRIGHT_REGEXP,
                COPYRIGHT_REPLACEMENT);
    }

    private void init(String outPdf, String cmpPdf) {
        this.outPdf = outPdf;
        this.cmpPdf = cmpPdf;
        outPdfName = new File(outPdf).getName();
        cmpPdfName = new File(cmpPdf).getName();
        outImage = outPdfName;
        if (cmpPdfName.startsWith("cmp_")) {
            cmpImage = cmpPdfName;
        } else {
            cmpImage = "cmp_" + cmpPdfName;
        }
    }

    private void setPassword(byte[] outPass, byte[] cmpPass) {
        if (outPass != null) {
            getOutReaderProperties().setPassword(outPass);
        }
        if (cmpPass != null) {
            getCmpReaderProperties().setPassword(outPass);
        }
    }

    private String compareVisually(String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        return compareVisually(outPath, differenceImagePrefix, ignoredAreas, null);
    }

    private String compareVisually(String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas, List<Integer> equalPages) throws IOException, InterruptedException {
        if (!outPath.endsWith("/")) {
            outPath = outPath + "/";
        }
        if (differenceImagePrefix == null) {
            String fileBasedPrefix = "";
            if (outPdfName != null) {
                // should always be initialized by this moment
                fileBasedPrefix = outPdfName + "_";
            }
            differenceImagePrefix = "diff_" + fileBasedPrefix;
        }

        prepareOutputDirs(outPath, differenceImagePrefix);

        System.out.println("Comparing visually..........");

        if (ignoredAreas != null && !ignoredAreas.isEmpty()) {
            createIgnoredAreasPdfs(outPath, ignoredAreas);
        }

        GhostscriptHelper ghostscriptHelper = null;
        try {
            ghostscriptHelper = new GhostscriptHelper(gsExec);
        } catch (IllegalArgumentException e) {
            throw new CompareToolExecutionException(e.getMessage());
        }

        ghostscriptHelper.runGhostScriptImageGeneration(outPdf, outPath, outImage);
        ghostscriptHelper.runGhostScriptImageGeneration(cmpPdf, outPath, cmpImage);
        return compareImagesOfPdfs(outPath, differenceImagePrefix, equalPages);
    }

    private String compareImagesOfPdfs(String outPath, String differenceImagePrefix, List<Integer> equalPages) throws IOException, InterruptedException {
        File[] imageFiles = FileUtil.listFilesInDirectoryByFilter(outPath, new PngFileFilter(outPdfName));
        File[] cmpImageFiles = FileUtil.listFilesInDirectoryByFilter(outPath, new CmpPngFileFilter(cmpPdfName));
        boolean bUnexpectedNumberOfPages = false;
        if (imageFiles.length != cmpImageFiles.length) {
            bUnexpectedNumberOfPages = true;
        }
        int cnt = Math.min(imageFiles.length, cmpImageFiles.length);
        if (cnt < 1) {
            throw new CompareToolExecutionException(
                    "No files for comparing. The result or sample pdf file is not processed by GhostScript.");
        }
        Arrays.sort(imageFiles, new ImageNameComparator());
        Arrays.sort(cmpImageFiles, new ImageNameComparator());

        boolean compareExecIsOk;
        String imageMagickInitError = null;
        ImageMagickHelper imageMagickHelper = null;
        try {
            imageMagickHelper = new ImageMagickHelper(compareExec);
            compareExecIsOk = true;
        } catch (IllegalArgumentException e) {
            compareExecIsOk = false;
            imageMagickInitError = e.getMessage();
            LoggerFactory.getLogger(CompareTool.class).warn(e.getMessage());
        }

        List<Integer> diffPages = new ArrayList<>();
        String differentPagesFail = null;

        for (int i = 0; i < cnt; i++) {
            if (equalPages != null && equalPages.contains(i))
                continue;
            System.out.println("Comparing page " + Integer.toString(i + 1) + ": " + UrlUtil.getNormalizedFileUriString(imageFiles[i].getName()) + " ...");
            System.out.println("Comparing page " + Integer.toString(i + 1) + ": " + UrlUtil.getNormalizedFileUriString(imageFiles[i].getName()) + " ...");
            InputStream is1 = FileUtil.getInputStreamForFile(imageFiles[i].getAbsolutePath());
            InputStream is2 = FileUtil.getInputStreamForFile(cmpImageFiles[i].getAbsolutePath());
            boolean cmpResult = compareStreams(is1, is2);
            is1.close();
            is2.close();
            if (!cmpResult) {
                differentPagesFail = "Page is different!";
                diffPages.add(i + 1);
                if (compareExecIsOk) {
                    String diffName = outPath + differenceImagePrefix + Integer.toString(i + 1) + ".png";
                    if (!imageMagickHelper.runImageMagickImageCompare(imageFiles[i].getAbsolutePath(),
                            cmpImageFiles[i].getAbsolutePath(), diffName)) {
                        File diffFile = new File(diffName);
                        differentPagesFail += "\nPlease, examine " + FILE_PROTOCOL
                                + UrlUtil.toNormalizedURI(diffFile).getPath() + " for more details.";
                    }
                }
                System.out.println(differentPagesFail);
            } else {
                System.out.println(" done.");
            }
        }
        if (differentPagesFail != null) {
            String errorMessage = DIFFERENT_PAGES.replace("<filename>", UrlUtil.toNormalizedURI(outPdf).getPath()).replace("<pagenumber>", listDiffPagesAsString(diffPages));
            if (!compareExecIsOk) {
                errorMessage += "\n" + imageMagickInitError;
            }
            return errorMessage;
        } else {
            if (bUnexpectedNumberOfPages)
                return UNEXPECTED_NUMBER_OF_PAGES.replace("<filename>", outPdf);
        }

        return null;
    }

    private String listDiffPagesAsString(List<Integer> diffPages) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < diffPages.size(); i++) {
            sb.append(diffPages.get(i));
            if (i < diffPages.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void createIgnoredAreasPdfs(String outPath, Map<Integer, List<Rectangle>> ignoredAreas) throws IOException {
        StampingProperties properties = new StampingProperties();
        properties.setEventCountingMetaInfo(metaInfo);
        try (PdfWriter outWriter = new PdfWriter(outPath + IGNORED_AREAS_PREFIX + outPdfName);
                PdfReader readerOut = CompareTool.createOutputReader(outPdf);
                PdfDocument pdfOutDoc = new PdfDocument(readerOut, outWriter, properties);
                PdfWriter cmpWriter = new PdfWriter(outPath + IGNORED_AREAS_PREFIX + cmpPdfName);
                PdfReader readerCmp = CompareTool.createOutputReader(cmpPdf);
                PdfDocument pdfCmpDoc = new PdfDocument(readerCmp, cmpWriter, properties)) {
            for (Map.Entry<Integer, List<Rectangle>> entry : ignoredAreas.entrySet()) {
                int pageNumber = entry.getKey();
                List<Rectangle> rectangles = entry.getValue();

                if (rectangles != null && !rectangles.isEmpty()) {
                    PdfCanvas outCanvas = new PdfCanvas(pdfOutDoc.getPage(pageNumber));
                    PdfCanvas cmpCanvas = new PdfCanvas(pdfCmpDoc.getPage(pageNumber));

                    outCanvas.saveState();
                    cmpCanvas.saveState();
                    for (Rectangle rect : rectangles) {
                        outCanvas.rectangle(rect).fill();
                        cmpCanvas.rectangle(rect).fill();
                    }
                    outCanvas.restoreState();
                    cmpCanvas.restoreState();
                }
            }
        }

        init(outPath + IGNORED_AREAS_PREFIX + outPdfName, outPath + IGNORED_AREAS_PREFIX + cmpPdfName);
    }

    private void prepareOutputDirs(String outPath, String differenceImagePrefix) {
        File[] imageFiles;
        File[] cmpImageFiles;
        File[] diffFiles;

        if (!FileUtil.directoryExists(outPath)) {
            FileUtil.createDirectories(outPath);
        } else {
            imageFiles = FileUtil.listFilesInDirectoryByFilter(outPath, new PngFileFilter(cmpPdfName));
            for (File file : imageFiles) {
                file.delete();
            }
            cmpImageFiles = FileUtil.listFilesInDirectoryByFilter(outPath, new CmpPngFileFilter(cmpPdfName));
            for (File file : cmpImageFiles) {
                file.delete();
            }

            diffFiles = FileUtil.listFilesInDirectoryByFilter(outPath, new DiffPngFileFilter(differenceImagePrefix));
            for (File file : diffFiles) {
                file.delete();
            }
        }
    }

    private void printOutCmpDirectories() {
        System.out.println("Out file folder: " + FILE_PROTOCOL
                + UrlUtil.toNormalizedURI(new File(outPdf).getParentFile()).getPath());
        System.out.println("Cmp file folder: " + FILE_PROTOCOL
                + UrlUtil.toNormalizedURI(new File(cmpPdf).getParentFile()).getPath());
    }

    private String compareByContent(String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        printOutCmpDirectories();
        System.out.print("Comparing by content..........");

        try (PdfReader readerOut = CompareTool.createOutputReader(outPdf, getOutReaderProperties());
                PdfDocument outDocument = new PdfDocument(readerOut,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo));
                PdfReader readerCmp = CompareTool.createOutputReader(cmpPdf, getCmpReaderProperties());
                PdfDocument cmpDocument = new PdfDocument(readerCmp,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo))) {

            List<PdfDictionary> outPages = new ArrayList<>();
            outPagesRef = new ArrayList<>();
            loadPagesFromReader(outDocument, outPages, outPagesRef);

            List<PdfDictionary> cmpPages = new ArrayList<>();
            cmpPagesRef = new ArrayList<>();
            loadPagesFromReader(cmpDocument, cmpPages, cmpPagesRef);

            if (outPages.size() != cmpPages.size()) {
                CompareTool.writeOnDisk(outPdf);
                CompareTool.writeOnDiskIfNotExists(cmpPdf);
                return compareVisuallyAndCombineReports("Documents have different numbers of pages.", outPath, differenceImagePrefix, ignoredAreas, null);
            }

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
                compareDocumentsMac(outDocument, cmpDocument, compareResult);
            }
            if (generateCompareByContentXmlReport) {
                String outPdfName = new File(outPdf).getName();
                OutputStream xml = FileUtil.getFileOutputStream(outPath + "/" + outPdfName.substring(0, outPdfName.length() - 3) + "report.xml");
                try {
                    compareResult.writeReportToXml(xml);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                } finally {
                    xml.close();
                }
            }

            if (equalPages.size() == cmpPages.size() && compareResult.isOk()) {
                System.out.println("OK");
                System.out.flush();
                return null;
            } else {
                CompareTool.writeOnDisk(outPdf);
                CompareTool.writeOnDiskIfNotExists(cmpPdf);
                return compareVisuallyAndCombineReports(compareResult.getReport(), outPath, differenceImagePrefix, ignoredAreas, equalPages);
            }
        } catch (Exception e) {
            CompareTool.writeOnDisk(outPdf);
            CompareTool.writeOnDiskIfNotExists(cmpPdf);
            throw e;
        }
    }

    private static void writeOnDisk(String filename) throws IOException {
        MemoryFirstPdfWriter outWriter = MemoryFirstPdfWriter.get(filename);
        if (outWriter != null) {
            outWriter.dump();
        }
    }

    private static void writeOnDiskIfNotExists(String filename) throws IOException {
        if (!new File(filename).exists()) {
            CompareTool.writeOnDisk(filename);
        }
    }

    private String compareVisuallyAndCombineReports(String compareByFailContentReason, String outPath, String differenceImagePrefix,
                                                    Map<Integer, List<Rectangle>> ignoredAreas,
                                                    List<Integer> equalPages) throws IOException, InterruptedException {
        System.out.println("Fail");
        System.out.flush();
        String compareByContentReport = "Compare by content report:\n" + compareByFailContentReason;
        System.out.println(compareByContentReport);
        System.out.flush();
        String message = compareVisually(outPath, differenceImagePrefix, ignoredAreas, equalPages);
        if (message == null || message.length() == 0)
            return "Compare by content fails. No visual differences";
        return message;
    }

    private void loadPagesFromReader(PdfDocument doc, List<PdfDictionary> pages, List<PdfIndirectReference> pagesRef) {
        int numOfPages = doc.getNumberOfPages();
        for (int i = 0; i < numOfPages; ++i) {
            pages.add(doc.getPage(i + 1).getPdfObject());
            pagesRef.add(pages.get(i).getIndirectReference());
        }
    }

    private void compareDocumentsEncryption(PdfDocument outDocument, PdfDocument cmpDocument, CompareResult compareResult) {
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

        Set<PdfName> ignoredEncryptEntries = new LinkedHashSet<>(Arrays.asList(PdfName.O, PdfName.U, PdfName.OE, PdfName.UE, PdfName.Perms, PdfName.CF, PdfName.Recipients));
        ObjectPath objectPath = new ObjectPath(outEncrypt.getIndirectReference(), cmpEncrypt.getIndirectReference());
        compareDictionariesExtended(outEncrypt, cmpEncrypt, objectPath, compareResult, ignoredEncryptEntries);

        PdfDictionary outCfDict = outEncrypt.getAsDictionary(PdfName.CF);
        PdfDictionary cmpCfDict = cmpEncrypt.getAsDictionary(PdfName.CF);
        if (cmpCfDict != null || outCfDict != null) {
            if (cmpCfDict != null && outCfDict == null || cmpCfDict == null) {
                compareResult.addError(objectPath, "One of the dictionaries is null, the other is not.");
            } else {
                Set<PdfName> mergedKeys = new TreeSet<>(outCfDict.keySet());
                mergedKeys.addAll(cmpCfDict.keySet());
                for (PdfName key : mergedKeys) {
                    objectPath.pushDictItemToPath(key);
                    LinkedHashSet<PdfName> excludedKeys = new LinkedHashSet<>(Arrays.asList(PdfName.Recipients));
                    compareDictionariesExtended(outCfDict.getAsDictionary(key), cmpCfDict.getAsDictionary(key), objectPath, compareResult, excludedKeys);
                    objectPath.pop();
                }
            }
        }
    }

    private void compareDocumentsMac(PdfDocument outDocument, PdfDocument cmpDocument, CompareResult compareResult) {
        PdfDictionary outAuthCode = outDocument.getTrailer().getAsDictionary(PdfName.AuthCode);
        PdfDictionary cmpAuthCode = cmpDocument.getTrailer().getAsDictionary(PdfName.AuthCode);
        if (outAuthCode == null && cmpAuthCode == null) {
            return;
        }

        ObjectPath trailerPath = new TrailerPath(cmpDocument, outDocument);
        if (outAuthCode == null) {
            compareResult.addError(trailerPath, "Output document does not contain MAC.");
            return;
        }
        if (cmpAuthCode == null) {
            compareResult.addError(trailerPath, "Output document contains MAC which is not expected.");
            return;
        }

        compareDictionariesExtended(outAuthCode, cmpAuthCode, trailerPath, compareResult,
                new HashSet<>(Arrays.asList(PdfName.ByteRange, PdfName.MAC)));
    }

    private boolean compareStreams(InputStream is1, InputStream is2) throws IOException {
        byte[] buffer1 = new byte[64 * 1024];
        byte[] buffer2 = new byte[64 * 1024];
        int len1;
        int len2;
        for (; ; ) {
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

    private boolean compareDictionariesExtended(PdfDictionary outDict, PdfDictionary cmpDict, ObjectPath currentPath, CompareResult compareResult) {
        return compareDictionariesExtended(outDict, cmpDict, currentPath, compareResult, null);
    }

    private boolean compareDictionariesExtended(PdfDictionary outDict, PdfDictionary cmpDict, ObjectPath currentPath, CompareResult compareResult, Set<PdfName> excludedKeys) {
        if (cmpDict != null && outDict == null || outDict != null && cmpDict == null) {
            compareResult.addError(currentPath, "One of the dictionaries is null, the other is not.");
            return false;
        }
        boolean dictsAreSame = true;
        // Iterate through the union of the keys of the cmp and out dictionaries
        Set<PdfName> mergedKeys = new TreeSet<>(cmpDict.keySet());
        mergedKeys.addAll(outDict.keySet());
        for (PdfName key : mergedKeys) {
            if (!dictsAreSame && (currentPath == null || compareResult == null || compareResult.isMessageLimitReached())) {
                return false;
            }

            if (excludedKeys != null && excludedKeys.contains(key)) {
                continue;
            }
            if (key.equals(PdfName.Parent) || key.equals(PdfName.P) || key.equals(PdfName.ModDate) ||
                    (key.equals(PdfName.KDFSalt) && !kdfSaltCompareEnabled)) {
                continue;
            }
            if (outDict.isStream() && cmpDict.isStream() && (key.equals(PdfName.Filter) || key.equals(PdfName.Length)))
                continue;
            if (key.equals(PdfName.BaseFont) || key.equals(PdfName.FontName)) {
                PdfObject cmpObj = cmpDict.get(key);
                if (cmpObj != null && cmpObj.isName() && cmpObj.toString().indexOf('+') > 0) {
                    PdfObject outObj = outDict.get(key);
                    if (!outObj.isName() || outObj.toString().indexOf('+') == -1) {
                        if (compareResult != null && currentPath != null)
                            compareResult.addError(currentPath, MessageFormatUtil.format("PdfDictionary {0} entry: Expected: {1}. Found: {2}", key.toString(), cmpObj.toString(), outObj.toString()));
                        dictsAreSame = false;
                    } else {
                        String cmpName = cmpObj.toString().substring(cmpObj.toString().indexOf('+'));
                        String outName = outObj.toString().substring(outObj.toString().indexOf('+'));
                        if (!cmpName.equals(outName)) {
                            if (compareResult != null && currentPath != null)
                                compareResult.addError(currentPath, MessageFormatUtil.format("PdfDictionary {0} entry: Expected: {1}. Found: {2}", key.toString(), cmpObj.toString(), outObj.toString()));
                            dictsAreSame = false;
                        }
                    }
                    continue;
                }
            }
            // A number tree can be stored in multiple, semantically equivalent ways.
            // Flatten to a single array, in order to get a canonical representation.
            if (key.equals(PdfName.ParentTree) || key.equals(PdfName.PageLabels)) {
                if (currentPath != null) {
                    currentPath.pushDictItemToPath(key);
                }
                PdfDictionary outNumTree = outDict.getAsDictionary(key);
                PdfDictionary cmpNumTree = cmpDict.getAsDictionary(key);
                LinkedList<PdfObject> outItems = new LinkedList<PdfObject>();
                LinkedList<PdfObject> cmpItems = new LinkedList<PdfObject>();
                PdfNumber outLeftover = flattenNumTree(outNumTree, null, outItems);
                PdfNumber cmpLeftover = flattenNumTree(cmpNumTree, null, cmpItems);
                if (outLeftover != null) {
                    LoggerFactory.getLogger(CompareTool.class).warn(IoLogMessageConstant.NUM_TREE_SHALL_NOT_END_WITH_KEY);
                    if (cmpLeftover == null) {
                        if (compareResult != null && currentPath != null) {
                            compareResult.addError(currentPath, "Number tree unexpectedly ends with a key");
                        }
                        dictsAreSame = false;
                    }
                }
                if (cmpLeftover != null) {
                    LoggerFactory.getLogger(CompareTool.class).warn(IoLogMessageConstant.NUM_TREE_SHALL_NOT_END_WITH_KEY);
                    if (outLeftover == null) {
                        if (compareResult != null && currentPath != null) {
                            compareResult.addError(currentPath, "Number tree was expected to end with a key (although it is invalid according to the specification), but ended with a value");
                        }
                        dictsAreSame = false;
                    }
                }
                if (outLeftover != null && cmpLeftover != null && !compareNumbers(outLeftover, cmpLeftover)) {
                    if (compareResult != null && currentPath != null) {
                        compareResult.addError(currentPath, "Number tree was expected to end with a different key (although it is invalid according to the specification)");
                    }
                    dictsAreSame = false;
                }
                PdfArray outArray = new PdfArray(outItems, outItems.size());
                PdfArray cmpArray = new PdfArray(cmpItems, cmpItems.size());
                if (!compareArraysExtended(outArray, cmpArray, currentPath, compareResult)) {
                    if (compareResult != null && currentPath != null) {
                        compareResult.addError(currentPath, "Number trees were flattened, compared and found to be different.");
                    }
                    dictsAreSame = false;
                }

                if (currentPath != null) {
                    currentPath.pop();
                }
                continue;
            }

            if (currentPath != null) {
                currentPath.pushDictItemToPath(key);
            }
            dictsAreSame = compareObjects(outDict.get(key, false), cmpDict.get(key, false), currentPath, compareResult) && dictsAreSame;
            if (currentPath != null) {
                currentPath.pop();
            }
        }
        return dictsAreSame;
    }

    private PdfNumber flattenNumTree(PdfDictionary dictionary, PdfNumber leftOver, LinkedList<PdfObject> items /*Map<PdfNumber, PdfObject> items*/) {
        PdfArray nums = dictionary.getAsArray(PdfName.Nums);
        if (nums != null) {
            for (int k = 0; k < nums.size(); k++) {
                PdfNumber number;
                if (leftOver == null)
                    number = nums.getAsNumber(k++);
                else {
                    number = leftOver;
                    leftOver = null;
                }
                if (k < nums.size()) {
                    items.addLast(number);
                    items.addLast(nums.get(k, false));
                } else {
                    return number;
                }
            }
        } else if ((nums = dictionary.getAsArray(PdfName.Kids)) != null) {
            for (int k = 0; k < nums.size(); k++) {
                PdfDictionary kid = nums.getAsDictionary(k);
                leftOver = flattenNumTree(kid, leftOver, items);
            }
        }
        return null;
    }

    /**
     * Compare PDF objects.
     *
     * @param outObj        out object corresponding to the output file, which is to be compared with cmp object
     * @param cmpObj        cmp object corresponding to the cmp-file, which is to be compared with out object
     * @param currentPath   current objects {@link ObjectPath} path
     * @param compareResult {@link CompareResult} for the results of the comparison of the two documents
     *
     * @return true if objects are equal, false otherwise.
     */
    protected boolean compareObjects(PdfObject outObj, PdfObject cmpObj, ObjectPath currentPath, CompareResult compareResult) {
        PdfObject outDirectObj = null;
        PdfObject cmpDirectObj = null;
        if (outObj != null)
            outDirectObj = outObj.isIndirectReference() ? ((PdfIndirectReference) outObj).getRefersTo(false) : outObj;
        if (cmpObj != null)
            cmpDirectObj = cmpObj.isIndirectReference() ? ((PdfIndirectReference) cmpObj).getRefersTo(false) : cmpObj;

        if (cmpDirectObj == null && outDirectObj == null)
            return true;

        if (outDirectObj == null) {
            compareResult.addError(currentPath, "Expected object was not found.");
            return false;
        } else if (cmpDirectObj == null) {
            compareResult.addError(currentPath, "Found object which was not expected to be found.");
            return false;
        } else if (cmpDirectObj.getType() != outDirectObj.getType()) {
            compareResult.addError(currentPath, MessageFormatUtil.format("Types do not match. Expected: {0}. Found: {1}.", cmpDirectObj.getClass().getSimpleName(), outDirectObj.getClass().getSimpleName()));
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
            currentPath = currentPath.resetDirectPath((PdfIndirectReference) cmpObj, (PdfIndirectReference) outObj);
        }

        if (cmpDirectObj.isDictionary() && PdfName.Page.equals(((PdfDictionary) cmpDirectObj).getAsName(PdfName.Type))
                && useCachedPagesForComparison) {
            if (!outDirectObj.isDictionary() || !PdfName.Page.equals(((PdfDictionary) outDirectObj).getAsName(PdfName.Type))) {
                if (compareResult != null && currentPath != null)
                    compareResult.addError(currentPath, "Expected a page. Found not a page.");
                return false;
            }
            PdfIndirectReference cmpRefKey = cmpObj.isIndirectReference() ? (PdfIndirectReference) cmpObj : cmpObj.getIndirectReference();
            PdfIndirectReference outRefKey = outObj.isIndirectReference() ? (PdfIndirectReference) outObj : outObj.getIndirectReference();
            // References to the same page
            if (cmpPagesRef == null) {
                cmpPagesRef = new ArrayList<>();
                for (int i = 1; i <= cmpRefKey.getDocument().getNumberOfPages(); ++i) {
                    cmpPagesRef.add(cmpRefKey.getDocument().getPage(i).getPdfObject().getIndirectReference());
                }
            }
            if (outPagesRef == null) {
                outPagesRef = new ArrayList<>();
                for (int i = 1; i <= outRefKey.getDocument().getNumberOfPages(); ++i) {
                    outPagesRef.add(outRefKey.getDocument().getPage(i).getPdfObject().getIndirectReference());
                }
            }

            // If at least one of the page dictionaries is in the document's page tree, we don't proceed with deep comparison,
            // because pages are compared at different level, so we compare only their index.
            // However only if both page dictionaries are not in the document's page trees, we continue to comparing them as normal dictionaries.
            if (cmpPagesRef.contains(cmpRefKey) || outPagesRef.contains(outRefKey)) {
                if (cmpPagesRef.contains(cmpRefKey) && cmpPagesRef.indexOf(cmpRefKey) == outPagesRef.indexOf(outRefKey)) {
                    return true;
                }
                if (compareResult != null && currentPath != null)
                    compareResult.addError(currentPath, MessageFormatUtil.format("The dictionaries refer to different pages. Expected page number: {0}. Found: {1}",
                            cmpPagesRef.indexOf(cmpRefKey) + 1, outPagesRef.indexOf(outRefKey) + 1));
                return false;
            }
        }

        if (cmpDirectObj.isDictionary()) {
            return compareDictionariesExtended((PdfDictionary) outDirectObj, (PdfDictionary) cmpDirectObj, currentPath, compareResult);
        } else if (cmpDirectObj.isStream()) {
            return compareStreamsExtended((PdfStream) outDirectObj, (PdfStream) cmpDirectObj, currentPath, compareResult);
        } else if (cmpDirectObj.isArray()) {
            return compareArraysExtended((PdfArray) outDirectObj, (PdfArray) cmpDirectObj, currentPath, compareResult);
        } else if (cmpDirectObj.isName()) {
            return compareNamesExtended((PdfName) outDirectObj, (PdfName) cmpDirectObj, currentPath, compareResult);
        } else if (cmpDirectObj.isNumber()) {
            return compareNumbersExtended((PdfNumber) outDirectObj, (PdfNumber) cmpDirectObj, currentPath, compareResult);
        } else if (cmpDirectObj.isString()) {
            return compareStringsExtended((PdfString) outDirectObj, (PdfString) cmpDirectObj, currentPath, compareResult);
        } else if (cmpDirectObj.isBoolean()) {
            return compareBooleansExtended((PdfBoolean) outDirectObj, (PdfBoolean) cmpDirectObj, currentPath, compareResult);
        } else if (outDirectObj.isNull() && cmpDirectObj.isNull()) {
            return true;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private boolean compareStreamsExtended(PdfStream outStream, PdfStream cmpStream, ObjectPath currentPath, CompareResult compareResult) {
        boolean toDecode = PdfName.FlateDecode.equals(outStream.get(PdfName.Filter));
        byte[] outStreamBytes = outStream.getBytes(toDecode);
        byte[] cmpStreamBytes = cmpStream.getBytes(toDecode);
        if (Arrays.equals(outStreamBytes, cmpStreamBytes)) {
            return compareDictionariesExtended(outStream, cmpStream, currentPath, compareResult);
        } else {
            StringBuilder errorMessage = new StringBuilder();
            if (cmpStreamBytes.length != outStreamBytes.length) {
                errorMessage.append(MessageFormatUtil.format("PdfStream. Lengths are different. Expected: {0}. Found: {1}\n", cmpStreamBytes.length, outStreamBytes.length));
            } else {
                errorMessage.append("PdfStream. Bytes are different.\n");
            }
            int firstDifferenceOffset = findBytesDifference(outStreamBytes, cmpStreamBytes, errorMessage);

            if (compareResult != null && currentPath != null) {
                currentPath.pushOffsetToPath(firstDifferenceOffset);
                compareResult.addError(currentPath, errorMessage.toString());
                currentPath.pop();
            }
            return false;
        }
    }

    /**
     * @return first difference offset
     */
    private int findBytesDifference(byte[] outStreamBytes, byte[] cmpStreamBytes, StringBuilder errorMessage) {
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
        String bytesDifference = null;
        if (numberOfDifferentBytes > 0) {
            int diffBytesAreaL = 10;
            int diffBytesAreaR = 10;
            int lCmp = Math.max(0, firstDifferenceOffset - diffBytesAreaL);
            int rCmp = Math.min(cmpStreamBytes.length, firstDifferenceOffset + diffBytesAreaR);
            int lOut = Math.max(0, firstDifferenceOffset - diffBytesAreaL);
            int rOut = Math.min(outStreamBytes.length, firstDifferenceOffset + diffBytesAreaR);


            String cmpByte = new String(new byte[]{cmpStreamBytes[firstDifferenceOffset]}, StandardCharsets.ISO_8859_1);
            String cmpByteNeighbours = new String(cmpStreamBytes, lCmp, rCmp - lCmp, StandardCharsets.ISO_8859_1).replaceAll(NEW_LINES, " ");
            String outByte = new String(new byte[]{outStreamBytes[firstDifferenceOffset]}, StandardCharsets.ISO_8859_1);
            String outBytesNeighbours = new String(outStreamBytes, lOut, rOut - lOut, StandardCharsets.ISO_8859_1).replaceAll(NEW_LINES, " ");
            bytesDifference = MessageFormatUtil.format("First bytes difference is encountered at index {0}. Expected: {1} ({2}). Found: {3} ({4}). Total number of different bytes: {5}",
                    Integer.valueOf(firstDifferenceOffset).toString(), cmpByte, cmpByteNeighbours, outByte, outBytesNeighbours, numberOfDifferentBytes);
        } else {
            // lengths are different
            firstDifferenceOffset = minLength;
            bytesDifference = MessageFormatUtil.format("Bytes of the shorter array are the same as the first {0} bytes of the longer one.", minLength);
        }

        errorMessage.append(bytesDifference);
        return firstDifferenceOffset;
    }

    private boolean compareArraysExtended(PdfArray outArray, PdfArray cmpArray, ObjectPath currentPath, CompareResult compareResult) {
        if (outArray == null) {
            if (compareResult != null && currentPath != null)
                compareResult.addError(currentPath, "Found null. Expected PdfArray.");
            return false;
        } else if (outArray.size() != cmpArray.size()) {
            if (compareResult != null && currentPath != null)
                compareResult.addError(currentPath, MessageFormatUtil.format("PdfArrays. Lengths are different. Expected: {0}. Found: {1}.", cmpArray.size(), outArray.size()));
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
                compareResult.addError(currentPath, MessageFormatUtil.format("PdfName. Expected: {0}. Found: {1}", cmpName.toString(), outName.toString()));
            return false;
        }
    }

    private boolean compareNumbersExtended(PdfNumber outNumber, PdfNumber cmpNumber, ObjectPath currentPath, CompareResult compareResult) {
        if (cmpNumber.getValue() == outNumber.getValue()) {
            return true;
        } else {
            if (compareResult != null && currentPath != null)
                compareResult.addError(currentPath, MessageFormatUtil.format("PdfNumber. Expected: {0}. Found: {1}", cmpNumber, outNumber));
            return false;
        }
    }

    private boolean compareStringsExtended(PdfString outString, PdfString cmpString, ObjectPath currentPath, CompareResult compareResult) {
        if (Arrays.equals(convertPdfStringToBytes(cmpString), convertPdfStringToBytes(outString))) {
            return true;
        } else {
            String cmpStr = cmpString.toUnicodeString();
            String outStr = outString.toUnicodeString();
            StringBuilder errorMessage = new StringBuilder();
            if (cmpStr.length() != outStr.length()) {
                errorMessage.append(MessageFormatUtil.format("PdfString. Lengths are different. Expected: {0}. Found: {1}\n", cmpStr.length(), outStr.length()));
            } else {
                errorMessage.append("PdfString. Characters are different.\n");
            }
            int firstDifferenceOffset = findStringDifference(outStr, cmpStr, errorMessage);

            if (compareResult != null && currentPath != null) {
                currentPath.pushOffsetToPath(firstDifferenceOffset);
                compareResult.addError(currentPath, errorMessage.toString());
                currentPath.pop();
            }
            return false;
        }
    }

    private int findStringDifference(String outString, String cmpString, StringBuilder errorMessage) {
        int numberOfDifferentChars = 0;
        int firstDifferenceOffset = 0;
        int minLength = Math.min(cmpString.length(), outString.length());
        for (int i = 0; i < minLength; i++) {
            if (cmpString.charAt(i) != outString.charAt(i)) {
                ++numberOfDifferentChars;
                if (numberOfDifferentChars == 1) {
                    firstDifferenceOffset = i;
                }
            }
        }
        String stringDifference = null;
        if (numberOfDifferentChars > 0) {
            int diffBytesAreaL = 15;
            int diffBytesAreaR = 15;
            int lCmp = Math.max(0, firstDifferenceOffset - diffBytesAreaL);
            int rCmp = Math.min(cmpString.length(), firstDifferenceOffset + diffBytesAreaR);
            int lOut = Math.max(0, firstDifferenceOffset - diffBytesAreaL);
            int rOut = Math.min(outString.length(), firstDifferenceOffset + diffBytesAreaR);


            String cmpByte = String.valueOf(cmpString.charAt(firstDifferenceOffset));
            String cmpByteNeighbours = cmpString.substring(lCmp, rCmp).replaceAll(NEW_LINES, " ");
            String outByte = String.valueOf(outString.charAt(firstDifferenceOffset));
            String outBytesNeighbours = outString.substring(lOut, rOut).replaceAll(NEW_LINES, " ");
            stringDifference = MessageFormatUtil.format("First characters difference is encountered at index {0}.\nExpected: {1} ({2}).\nFound: {3} ({4}).\nTotal number of different characters: {5}",
                    Integer.valueOf(firstDifferenceOffset).toString(), cmpByte, cmpByteNeighbours, outByte, outBytesNeighbours, numberOfDifferentChars);
        } else {
            // lengths are different

            firstDifferenceOffset = minLength;
            stringDifference = MessageFormatUtil.format("All characters of the shorter string are the same as the first {0} characters of the longer one.", minLength);
        }

        errorMessage.append(stringDifference);
        return firstDifferenceOffset;
    }

    private byte[] convertPdfStringToBytes(PdfString pdfString) {
        byte[] bytes;
        String value = pdfString.getValue();
        String encoding = pdfString.getEncoding();
        if (encoding != null && PdfEncodings.UNICODE_BIG.equals(encoding) && PdfEncodings.isPdfDocEncoding(value))
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
                compareResult.addError(currentPath, MessageFormatUtil.format("PdfBoolean. Expected: {0}. Found: {1}.", cmpBoolean.getValue(), outBoolean.getValue()));
            return false;
        }
    }

    private List<PdfLinkAnnotation> getLinkAnnotations(int pageNum, PdfDocument document) {
        List<PdfLinkAnnotation> linkAnnotations = new ArrayList<>();
        List<PdfAnnotation> annotations = document.getPage(pageNum).getAnnotations();
        for (PdfAnnotation annotation : annotations) {
            if (PdfName.Link.equals(annotation.getSubtype())) {
                linkAnnotations.add((PdfLinkAnnotation) annotation);
            }
        }
        return linkAnnotations;
    }

    private boolean compareLinkAnnotations(PdfLinkAnnotation cmpLink, PdfLinkAnnotation outLink, PdfDocument cmpDocument, PdfDocument outDocument) {
        // Compare link rectangles, page numbers the links refer to, and simple parameters (non-indirect, non-arrays, non-dictionaries)
        PdfObject cmpDestObject = cmpLink.getDestinationObject();
        PdfObject outDestObject = outLink.getDestinationObject();

        if (cmpDestObject != null && outDestObject != null) {
            if (cmpDestObject.getType() != outDestObject.getType())
                return false;
            else {
                PdfArray explicitCmpDest = null;
                PdfArray explicitOutDest = null;
                PdfNameTree cmpNamedDestinations = cmpDocument
                        .getCatalog().getNameTree(PdfName.Dests);
                PdfNameTree outNamedDestinations = outDocument
                        .getCatalog().getNameTree(PdfName.Dests);
                switch (cmpDestObject.getType()) {
                    case PdfObject.ARRAY:
                        explicitCmpDest = (PdfArray) cmpDestObject;
                        explicitOutDest = (PdfArray) outDestObject;
                        break;
                    case PdfObject.NAME:
                        String cmpDestName = ((PdfName) cmpDestObject).getValue();
                        explicitCmpDest = (PdfArray) cmpNamedDestinations.getEntry(cmpDestName);
                        String outDestName = ((PdfName) outDestObject).getValue();
                        explicitOutDest = (PdfArray) outNamedDestinations.getEntry(outDestName);
                        break;
                    case PdfObject.STRING:
                        explicitCmpDest = (PdfArray) cmpNamedDestinations
                                .getEntry((PdfString) cmpDestObject);
                        explicitOutDest = (PdfArray) outNamedDestinations
                                .getEntry((PdfString) outDestObject);
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

    private static class PngFileFilter implements FileFilter {
        private String currentOutPdfName;

        public PngFileFilter (String currentOutPdfName) {
            this.currentOutPdfName = currentOutPdfName;
        }

        public boolean accept(File pathname) {
            String ap = pathname.getName();
            boolean b1 = ap.endsWith(".png");
            boolean b2 = ap.contains("cmp_");
            return b1 && !b2 && ap.contains(currentOutPdfName);
        }
    }

    private static class CmpPngFileFilter implements FileFilter {
        private String currentCmpPdfName;

        public CmpPngFileFilter (String currentCmpPdfName) {
            this.currentCmpPdfName = currentCmpPdfName;
        }

        public boolean accept(File pathname) {
            String ap = pathname.getName();
            boolean b1 = ap.endsWith(".png");
            boolean b2 = ap.contains("cmp_");
            return b1 && b2 && ap.contains(currentCmpPdfName);
        }
    }

    private static class DiffPngFileFilter implements FileFilter {
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

    private static class ImageNameComparator implements Comparator<File> {
        public int compare(File f1, File f2) {
            String f1Name = f1.getName();
            String f2Name = f2.getName();
            return f1Name.compareTo(f2Name);
        }
    }

    /**
     * Class containing results of the comparison of two documents.
     */
    public static class CompareResult {
        // LinkedHashMap to retain order. HashMap has different order in Java6/7 and Java8
        protected Map<ObjectPath, String> differences = new LinkedHashMap<>();
        protected int messageLimit = 1;

        /**
         * Creates new empty instance of CompareResult with given limit of difference messages.
         *
         * @param messageLimit maximum number of difference messages to be handled by this CompareResult.
         */
        public CompareResult(int messageLimit) {
            this.messageLimit = messageLimit;
        }

        /**
         * Verifies if documents are considered equal after comparison.
         *
         * @return true if documents are equal, false otherwise.
         */
        public boolean isOk() {
            return differences.size() == 0;
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
         * Converts this CompareResult into text form.
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
         * Converts this CompareResult into xml form.
         *
         * @param stream output stream to which xml report will be written.
         * @throws ParserConfigurationException if a XML DocumentBuilder cannot be created
         *                                      which satisfies the configuration requested.
         * @throws TransformerException         if it is not possible to create an XML Transformer instance or
         *                                      an unrecoverable error occurs during the course of the transformation.
         */
        public void writeReportToXml(OutputStream stream) throws ParserConfigurationException, TransformerException {
            final Document xmlReport = XmlUtil.initNewXmlDocument();
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
         * Checks whether maximum number of difference messages to be handled by this CompareResult is reached.
         *
         * @return true if limit of difference messages is reached, false otherwise.
         */
        protected boolean isMessageLimitReached() {
            return differences.size() >= messageLimit;
        }

        /**
         * Adds an error message for the {@link ObjectPath}.
         *
         * @param path    {@link ObjectPath} for the two corresponding objects in the compared documents
         * @param message an error message
         */
        protected void addError(ObjectPath path, String message) {
            if (differences.size() < messageLimit) {
                differences.put(new ObjectPath(path), message);
            }
        }
    }

    /**
     * Exceptions thrown when errors occur during generation and comparison of images obtained on the basis of pdf
     * files.
     */
    public static class CompareToolExecutionException extends RuntimeException {
        /**
         * Creates a new {@link CompareToolExecutionException}.
         *
         * @param msg the detail message.
         */
        public CompareToolExecutionException(String msg) {
            super(msg);
        }
    }
}
