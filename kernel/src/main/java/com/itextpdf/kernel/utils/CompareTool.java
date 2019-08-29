/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.FileUtil;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.io.util.SystemUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.counter.event.IMetaInfo;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
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
 * and gives the report of their differences.
 * <br><br>
 * For visual comparison it uses external tools: Ghostscript and ImageMagick, which
 * should be installed on your machine. To allow CompareTool to use them, you need
 * to pass either java properties or environment variables with names "gsExec" and
 * "compareExec", which would contain the paths to the executables of correspondingly
 * Ghostscript and ImageMagick tools.
 * <br><br>
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
    private static final String cannotOpenOutputDirectory = "Cannot open output directory for <filename>.";
    private static final String gsFailed = "GhostScript failed for <filename>.";
    private static final String unexpectedNumberOfPages = "Unexpected number of pages for <filename>.";
    private static final String differentPages = "File file:///<filename> differs on page <pagenumber>.";
    private static final String undefinedGsPath = "Path to GhostScript is not specified. Please use -DgsExec=<path_to_ghostscript> (e.g. -DgsExec=\"C:/Program Files/gs/gs9.14/bin/gswin32c.exe\")";
    private static final String ignoredAreasPrefix = "ignored_areas_";

    private static final String gsParams = " -dSAFER -dNOPAUSE -dBATCH -sDEVICE=png16m -r150 -sOutputFile='<outputfile>' '<inputfile>'";
    private static final String compareParams = " '<image1>' '<image2>' '<difference>'";

    private static final String versionRegexp = "(iText\u00ae( pdfX(FA|fa)| DITO)?|iTextSharp\u2122) (\\d+\\.)+\\d+(-SNAPSHOT)?";
    private static final String versionReplacement = "iText\u00ae <version>";
    private static final String copyrightRegexp = "\u00a9\\d+-\\d+ iText Group NV";
    private static final String copyrightReplacement = "\u00a9<copyright years> iText Group NV";

    private String gsExec;
    private String compareExec;

    private String cmpPdf;
    private String cmpPdfName;
    private String cmpImage;
    private String outPdf;
    private String outPdfName;
    private String outImage;

    private ReaderProperties outProps;
    private ReaderProperties cmpProps;

    private List<PdfIndirectReference> outPagesRef;
    private List<PdfIndirectReference> cmpPagesRef;

    private int compareByContentErrorsLimit = 1000;
    private boolean generateCompareByContentXmlReport = false;

    private boolean encryptionCompareEnabled = false;

    private boolean useCachedPagesForComparison = true;
    private IMetaInfo metaInfo;

    /**
     * Creates an instance of the CompareTool.
     */
    public CompareTool() {
        gsExec = SystemUtil.getPropertyOrEnvironmentVariable("gsExec");
        compareExec = SystemUtil.getPropertyOrEnvironmentVariable("compareExec");
    }

    /**
     * Compares two PDF documents by content starting from Catalog dictionary and then recursively comparing
     * corresponding objects which are referenced from it. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <br><br>
     * The main difference between this method and the {@link #compareByContent(String, String, String, String)}
     * methods is the return value. This method returns a {@link CompareResult} class instance, which could be used
     * in code, however compareByContent methods in case of the differences simply return String value, which could
     * only be printed. Also, keep in mind that this method doesn't perform visual comparison of the documents.
     * <br><br>
     * For more explanations about what is outDoc and cmpDoc see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outDocument the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpDocument the absolute path to the cmp-file, which is to be compared to output file.
     * @return the report of comparison of two files in the form of the custom class instance.
     * See {@link CompareResult} for more info.
     * @throws IOException
     */
    public CompareResult compareByCatalog(PdfDocument outDocument, PdfDocument cmpDocument) throws IOException {
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
     * not checked as objects, but rather simply checked that they has same page numbers in both documents.
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
     * Enables or disables the generation of the comparison report in the form of the xml document.
     * <br>
     * IMPORTANT NOTE: this flag affect only the comparison made by compareByContent methods!
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
     * <br>
     * IMPORTANT NOTE: this flag affect only the comparison made by compareByContent methods!
     *
     * @return this CompareTool instance.
     */
    public CompareTool enableEncryptionCompare() {
        this.encryptionCompareEnabled = true;
        return this;
    }

    /**
     * Documents for comparison are opened in reader mode. This method is intended to alter {@link ReaderProperties}
     * which are used to open output document. This is particularly useful for comparison of encrypted documents.
     * <p>
     * For more explanations about what is outDoc and cmpDoc see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @return {@link ReaderProperties} instance which will be later passed to the output document {@link PdfReader}.
     */
    public ReaderProperties getOutReaderProperties() {
        if (outProps == null) {
            outProps = new ReaderProperties();
        }
        return outProps;
    }

    /**
     * Documents for comparison are opened in reader mode. This method is intended to alter {@link ReaderProperties}
     * which are used to open cmp document. This is particularly useful for comparison of encrypted documents.
     * <p>
     * For more explanations about what is outDoc and cmpDoc see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @return {@link ReaderProperties} instance which will be later passed to the cmp document {@link PdfReader}.
     */
    public ReaderProperties getCmpReaderProperties() {
        if (cmpProps == null) {
            cmpProps = new ReaderProperties();
        }
        return cmpProps;
    }

    /**
     * Compares two documents visually. For the comparison two external tools are used: Ghostscript and ImageMagick.
     * For more info about needed configuration for visual comparison process see {@link CompareTool} class description.
     * <br>
     * During comparison for every page of two documents an image file will be created in the folder specified by
     * outPath absolute path. Then those page images will be compared and if there are any differences for some pages,
     * another image file will be created with marked differences on it.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked differences if there is any.
     * @return string containing list of the pages that are visually different, or null if there are no visual differences.
     * @throws InterruptedException
     * @throws IOException
     */
    public String compareVisually(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix) throws InterruptedException, IOException {
        return compareVisually(outPdf, cmpPdf, outPath, differenceImagePrefix, null);
    }

    /**
     * Compares two documents visually. For the comparison two external tools are used: Ghostscript and ImageMagick.
     * For more info about needed configuration for visual comparison process see {@link CompareTool} class description.
     * <br>
     * During comparison for every page of two documents an image file will be created in the folder specified by
     * outPath absolute path. Then those page images will be compared and if there are any differences for some pages,
     * another image file will be created with marked differences on it.
     * <br>
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
     * @throws InterruptedException
     * @throws IOException
     */
    public String compareVisually(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        init(outPdf, cmpPdf);
        System.out.println("Out pdf: file:///" + UrlUtil.toNormalizedURI(outPdf).getPath());
        System.out.println("Cmp pdf: file:///" + UrlUtil.toNormalizedURI(cmpPdf).getPath() + "\n");
        return compareVisually(outPath, differenceImagePrefix, ignoredAreas);
    }

    /**
     * Compares two PDF documents by content starting from page dictionaries and then recursively comparing
     * corresponding objects which are referenced from them. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <p>
     * Unlike {@link #compareByCatalog(PdfDocument, PdfDocument)} this method performs content comparison page by page
     * and doesn't compare the tag structure, acroforms and all other things that doesn't belong to specific pages.
     * <br>
     * When comparison by content is finished, if any differences were found, visual comparison is automatically started.
     * For more info see {@link #compareVisually(String, String, String, String)}.
     * For this overload, differenceImagePrefix value is generated using diff_%outPdfFileName%_ format.
     * <p>
     * For more explanations about what is outPdf and cmpPdf see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outPdf  the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf  the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath the absolute path to the folder, which will be used to store image files for visual comparison.
     * @return string containing text report of the encountered content differences and also list of the pages that are
     * visually different, or null if there are no content and therefore no visual differences.
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting
     *                              for ghostscript or imagemagic processes, then the wait is ended and an {@link InterruptedException} is thrown.
     * @throws IOException          is thrown if any of the input files are missing or any of the auxiliary files
     *                              that are created during comparison process wasn't possible to be created.
     */
    public String compareByContent(String outPdf, String cmpPdf, String outPath) throws InterruptedException, IOException {
        return compareByContent(outPdf, cmpPdf, outPath, null, null, null, null);
    }

    /**
     * Compares two PDF documents by content starting from page dictionaries and then recursively comparing
     * corresponding objects which are referenced from them. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <br><br>
     * Unlike {@link #compareByCatalog(PdfDocument, PdfDocument)} this method performs content comparison page by page
     * and doesn't compare the tag structure, acroforms and all other things that doesn't belong to specific pages.
     * <br>
     * When comparison by content is finished, if any differences were found, visual comparison is automatically started.
     * For more info see {@link #compareVisually(String, String, String, String)}.
     * <br><br>
     * For more explanations about what is outPdf and cmpPdf see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked visual differences if there is any;
     *                              if it's set to null the prefix defaults to diff_%outPdfFileName%_ format.
     * @return string containing text report of the encountered content differences and also list of the pages that are
     * visually different, or null if there are no content and therefore no visual differences.
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting
     *                              for ghostscript or imagemagic processes, then the wait is ended and an {@link InterruptedException} is thrown.
     * @throws IOException          is thrown if any of the input files are missing or any of the auxiliary files
     *                              that are created during comparison process wasn't possible to be created.
     */
    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix) throws InterruptedException, IOException {
        return compareByContent(outPdf, cmpPdf, outPath, differenceImagePrefix, null, null, null);
    }

    /**
     * This method overload is used to compare two encrypted PDF documents. Document passwords are passed with
     * outPass and cmpPass parameters.
     * <br><br>
     * Compares two PDF documents by content starting from page dictionaries and then recursively comparing
     * corresponding objects which are referenced from them. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <br><br>
     * Unlike {@link #compareByCatalog(PdfDocument, PdfDocument)} this method performs content comparison page by page
     * and doesn't compare the tag structure, acroforms and all other things that doesn't belong to specific pages.
     * <br>
     * When comparison by content is finished, if any differences were found, visual comparison is automatically started.
     * For more info see {@link #compareVisually(String, String, String, String)}.
     * <br><br>
     * For more explanations about what is outPdf and cmpPdf see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked visual differences if there is any;
     *                              if it's set to null the prefix defaults to diff_%outPdfFileName%_ format.
     * @param outPass               password for the encrypted document specified by the outPdf absolute path.
     * @param cmpPass               password for the encrypted document specified by the cmpPdf absolute path.
     * @return string containing text report of the encountered content differences and also list of the pages that are
     * visually different, or null if there are no content and therefore no visual differences.
     * @throws InterruptedException
     * @throws IOException
     */
    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, byte[] outPass, byte[] cmpPass) throws InterruptedException, IOException {
        return compareByContent(outPdf, cmpPdf, outPath, differenceImagePrefix, null, outPass, cmpPass);
    }

    /**
     * Compares two PDF documents by content starting from page dictionaries and then recursively comparing
     * corresponding objects which are referenced from them. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <br><br>
     * Unlike {@link #compareByCatalog(PdfDocument, PdfDocument)} this method performs content comparison page by page
     * and doesn't compare the tag structure, acroforms and all other things that doesn't belong to specific pages.
     * <br>
     * When comparison by content is finished, if any differences were found, visual comparison is automatically started.
     * For more info see {@link #compareVisually(String, String, String, String, Map)}.
     * <br><br>
     * For more explanations about what is outPdf and cmpPdf see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked visual differences if there is any;
     *                              if it's set to null the prefix defaults to diff_%outPdfFileName%_ format.
     * @param ignoredAreas          a map with one-based page numbers as keys and lists of ignored rectangles as values.
     * @return string containing text report of the encountered content differences and also list of the pages that are
     * visually different, or null if there are no content and therefore no visual differences.
     * @throws InterruptedException
     * @throws IOException
     */
    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        init(outPdf, cmpPdf);
        return compareByContent(outPath, differenceImagePrefix, ignoredAreas);
    }

    /**
     * This method overload is used to compare two encrypted PDF documents. Document passwords are passed with
     * outPass and cmpPass parameters.
     * <br><br>
     * Compares two PDF documents by content starting from page dictionaries and then recursively comparing
     * corresponding objects which are referenced from them. You can roughly imagine it as depth-first traversal
     * of the two trees that represent pdf objects structure of the documents.
     * <br><br>
     * Unlike {@link #compareByCatalog(PdfDocument, PdfDocument)} this method performs content comparison page by page
     * and doesn't compare the tag structure, acroforms and all other things that doesn't belong to specific pages.
     * <br>
     * When comparison by content is finished, if any differences were found, visual comparison is automatically started.
     * For more info see {@link #compareVisually(String, String, String, String, Map)}.
     * <br><br>
     * For more explanations about what is outPdf and cmpPdf see last paragraph of the {@link CompareTool}
     * class description.
     *
     * @param outPdf                the absolute path to the output file, which is to be compared to cmp-file.
     * @param cmpPdf                the absolute path to the cmp-file, which is to be compared to output file.
     * @param outPath               the absolute path to the folder, which will be used to store image files for visual comparison.
     * @param differenceImagePrefix file name prefix for image files with marked visual differences if there is any;
     *                              if it's set to null the prefix defaults to diff_%outPdfFileName%_ format.
     * @param ignoredAreas          a map with one-based page numbers as keys and lists of ignored rectangles as values.
     * @param outPass               password for the encrypted document specified by the outPdf absolute path.
     * @param cmpPass               password for the encrypted document specified by the cmpPdf absolute path.
     * @return string containing text report of the encountered content differences and also list of the pages that are
     * visually different, or null if there are no content and therefore no visual differences.
     * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting
     *                              for ghostscript or imagemagic processes, then the wait is ended and an {@link InterruptedException} is thrown.
     * @throws IOException          is thrown if any of the input files are missing or any of the auxiliary files
     *                              that are created during comparison process wasn't possible to be created.
     */
    public String compareByContent(String outPdf, String cmpPdf, String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas, byte[] outPass, byte[] cmpPass) throws InterruptedException, IOException {
        init(outPdf, cmpPdf);
        System.out.println("Out pdf: file:///" + UrlUtil.toNormalizedURI(outPdf).getPath());
        System.out.println("Cmp pdf: file:///" + UrlUtil.toNormalizedURI(cmpPdf).getPath() + "\n");
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
     * @throws IOException
     */
    public boolean compareDictionaries(PdfDictionary outDict, PdfDictionary cmpDict) throws IOException {
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
     * For more explanations about what is outPdf and cmpPdf see last paragraph of the {@link CompareTool}
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
     * For more explanations about what is outPdf and cmpPdf see last paragraph of the {@link CompareTool}
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
        CompareTool.ObjectPath currentPath = new ObjectPath(cmpDict.getIndirectReference(), outDict.getIndirectReference());
        if (!compareDictionariesExtended(outDict, cmpDict, currentPath, compareResult, excludedKeys)) {
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
     * @throws IOException
     */
    public boolean compareStreams(PdfStream outStream, PdfStream cmpStream) throws IOException {
        return compareStreamsExtended(outStream, cmpStream, null, null);
    }

    /**
     * Simple method that compares two given PdfArrays by content. This is "deep" comparing, which means that all
     * nested objects are also compared by content.
     *
     * @param outArray array to compare.
     * @param cmpArray array to compare.
     * @return true if arrays are equal by content, otherwise false.
     * @throws IOException
     */
    public boolean compareArrays(PdfArray outArray, PdfArray cmpArray) throws IOException {
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
     * @return text report of the xmp differences, or null if there are no differences.
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
     * @return text report of the xmp differences, or null if there are no differences.
     */
    public String compareXmp(String outPdf, String cmpPdf, boolean ignoreDateAndProducerProperties) {
        init(outPdf, cmpPdf);
        PdfDocument cmpDocument = null;
        PdfDocument outDocument = null;
        try {
            cmpDocument = new PdfDocument(new PdfReader(this.cmpPdf), new DocumentProperties().setEventCountingMetaInfo(metaInfo));
            outDocument = new PdfDocument(new PdfReader(this.outPdf), new DocumentProperties().setEventCountingMetaInfo(metaInfo));
            byte[] cmpBytes = cmpDocument.getXmpMetadata(), outBytes = outDocument.getXmpMetadata();
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
        } catch (Exception ex) {
            return "XMP parsing failure!";
        } finally {
            if (cmpDocument != null)
                cmpDocument.close();
            if (outDocument != null)
                outDocument.close();
        }
        return null;
    }

    /**
     * Utility method that provides simple comparison of the two xml files stored in byte arrays.
     *
     * @param xml1 first xml file data to compare.
     * @param xml2 second xml file data to compare.
     * @return true if xml structures are identical, false otherwise.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
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
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public boolean compareXmls(String outXmlFile, String cmpXmlFile) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("Out xml: file:///" + UrlUtil.toNormalizedURI(outXmlFile).getPath());
        System.out.println("Cmp xml: file:///" + UrlUtil.toNormalizedURI(cmpXmlFile).getPath() + "\n");
        return XmlUtils.compareXmls(new FileInputStream(outXmlFile), new FileInputStream(cmpXmlFile));
    }

    /**
     * This method overload is used to compare two encrypted PDF documents. Document passwords are passed with
     * outPass and cmpPass parameters.
     * <br><br>
     * Compares document info dictionaries of two pdf documents.
     *
     * @param outPdf  the absolute path to the output file, which info is to be compared to cmp-file info.
     * @param cmpPdf  the absolute path to the cmp-file, which info is to be compared to output file info.
     * @param outPass password for the encrypted document specified by the outPdf absolute path.
     * @param cmpPass password for the encrypted document specified by the cmpPdf absolute path.
     * @return text report of the differences in documents infos.
     * @throws IOException
     */
    public String compareDocumentInfo(String outPdf, String cmpPdf, byte[] outPass, byte[] cmpPass) throws IOException {
        System.out.print("[itext] INFO  Comparing document info.......");
        String message = null;
        setPassword(outPass, cmpPass);
        PdfDocument outDocument = new PdfDocument(new PdfReader(outPdf, getOutReaderProperties()), new DocumentProperties().setEventCountingMetaInfo(metaInfo));
        PdfDocument cmpDocument = new PdfDocument(new PdfReader(cmpPdf, getCmpReaderProperties()), new DocumentProperties().setEventCountingMetaInfo(metaInfo));
        String[] cmpInfo = convertInfo(cmpDocument.getDocumentInfo());
        String[] outInfo = convertInfo(outDocument.getDocumentInfo());
        for (int i = 0; i < cmpInfo.length; ++i) {
            if (!cmpInfo[i].equals(outInfo[i])) {
                message = MessageFormatUtil.format("Document info fail. Expected: \"{0}\", actual: \"{1}\"", cmpInfo[i], outInfo[i]);
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

    /**
     * Compares document info dictionaries of two pdf documents.
     *
     * @param outPdf the absolute path to the output file, which info is to be compared to cmp-file info.
     * @param cmpPdf the absolute path to the cmp-file, which info is to be compared to output file info.
     * @return text report of the differences in documents infos.
     * @throws IOException
     */
    public String compareDocumentInfo(String outPdf, String cmpPdf) throws IOException {
        return compareDocumentInfo(outPdf, cmpPdf, null, null);
    }

    /**
     * Compares if two documents has identical link annotations on corresponding pages.
     *
     * @param outPdf the absolute path to the output file, which links are to be compared to cmp-file links.
     * @param cmpPdf the absolute path to the cmp-file, which links are to be compared to output file links.
     * @return text report of the differences in documents links.
     * @throws IOException
     */
    public String compareLinkAnnotations(String outPdf, String cmpPdf) throws IOException {
        System.out.print("[itext] INFO  Comparing link annotations....");
        String message = null;
        PdfDocument outDocument = new PdfDocument(new PdfReader(outPdf), new DocumentProperties().setEventCountingMetaInfo(metaInfo));
        PdfDocument cmpDocument = new PdfDocument(new PdfReader(cmpPdf), new DocumentProperties().setEventCountingMetaInfo(metaInfo));
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
        outDocument.close();
        cmpDocument.close();
        if (message == null)
            System.out.println("OK");
        else
            System.out.println("Fail");
        System.out.flush();
        return message;
    }

    /**
     * Compares tag structures of the two PDF documents.
     * <br>
     * This method creates xml files in the same folder with outPdf file. These xml files contain documents tag structures
     * converted into the xml structure. These xml files are compared if they are equal.
     *
     * @param outPdf the absolute path to the output file, which tags are to be compared to cmp-file tags.
     * @param cmpPdf the absolute path to the cmp-file, which tags are to be compared to output file tags.
     * @return text report of the differences in documents tags.
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public String compareTagStructures(String outPdf, String cmpPdf) throws IOException, ParserConfigurationException, SAXException {
        System.out.print("[itext] INFO  Comparing tag structures......");

        String outXmlPath = outPdf.replace(".pdf", ".xml");
        String cmpXmlPath = outPdf.replace(".pdf", ".cmp.xml");

        String message = null;

        PdfReader readerOut = new PdfReader(outPdf);
        PdfDocument docOut = new PdfDocument(readerOut, new DocumentProperties().setEventCountingMetaInfo(metaInfo));
        FileOutputStream xmlOut = new FileOutputStream(outXmlPath);
        new TaggedPdfReaderTool(docOut).setRootTag("root").convertToXml(xmlOut);
        docOut.close();
        xmlOut.close();

        PdfReader readerCmp = new PdfReader(cmpPdf);
        PdfDocument docCmp = new PdfDocument(readerCmp, new DocumentProperties().setEventCountingMetaInfo(metaInfo));
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

    String[] convertInfo(PdfDocumentInfo info) {
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
        return producer.replaceAll(versionRegexp, versionReplacement).replaceAll(copyrightRegexp, copyrightReplacement);
    }

    private void init(String outPdf, String cmpPdf) {
        this.outPdf = outPdf;
        this.cmpPdf = cmpPdf;
        outPdfName = new File(outPdf).getName();
        cmpPdfName = new File(cmpPdf).getName();
        outImage = outPdfName + "-%03d.png";
        if (cmpPdfName.startsWith("cmp_")) cmpImage = cmpPdfName + "-%03d.png";
        else cmpImage = "cmp_" + cmpPdfName + "-%03d.png";
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
        if (gsExec == null) {
            throw new CompareToolExecutionException(undefinedGsPath);
        }
        if (!(new File(gsExec).canExecute())) {
            throw new CompareToolExecutionException(new File(gsExec).getAbsolutePath() + " is not an executable program");
        }
        if (!outPath.endsWith("/")) {
            outPath = outPath + "/";
        }
        if (differenceImagePrefix == null) {
            String fileBasedPrefix = "";
            if (outPdfName != null) { // should always be initialized by this moment
                fileBasedPrefix = outPdfName + "_";
            }
            differenceImagePrefix = "diff_" + fileBasedPrefix;
        }

        prepareOutputDirs(outPath, differenceImagePrefix);

        System.out.println("Comparing visually..........");

        if (ignoredAreas != null && !ignoredAreas.isEmpty()) {
            createIgnoredAreasPdfs(outPath, ignoredAreas);
        }

        runGhostScriptImageGeneration(outPath);

        return compareImagesOfPdfs(outPath, differenceImagePrefix, equalPages);
    }

    private String compareImagesOfPdfs(String outPath, String differenceImagePrefix, List<Integer> equalPages) throws IOException, InterruptedException {
        File[] imageFiles = FileUtil.listFilesInDirectoryByFilter(outPath, new PngFileFilter());
        File[] cmpImageFiles = FileUtil.listFilesInDirectoryByFilter(outPath, new CmpPngFileFilter());
        boolean bUnexpectedNumberOfPages = false;
        if (imageFiles.length != cmpImageFiles.length) {
            bUnexpectedNumberOfPages = true;
        }
        int cnt = Math.min(imageFiles.length, cmpImageFiles.length);
        if (cnt < 1) {
            throw new CompareToolExecutionException("No files for comparing. The result or sample pdf file is not processed by GhostScript.");
        }
        Arrays.sort(imageFiles, new ImageNameComparator());
        Arrays.sort(cmpImageFiles, new ImageNameComparator());
        String differentPagesFail = null;
        boolean compareExecIsOk = compareExec != null && new File(compareExec).canExecute();
        if (compareExec != null && !compareExecIsOk) {
            throw new CompareToolExecutionException(new File(compareExec).getAbsolutePath() + " is not an executable program");
        }
        List<Integer> diffPages = new ArrayList<>();

        for (int i = 0; i < cnt; i++) {
            if (equalPages != null && equalPages.contains(i))
                continue;
            System.out.println("Comparing page " + Integer.toString(i + 1) + ": file:///" + UrlUtil.toNormalizedURI(imageFiles[i]).getPath() + " ...");
            FileInputStream is1 = new FileInputStream(imageFiles[i].getAbsolutePath());
            FileInputStream is2 = new FileInputStream(cmpImageFiles[i].getAbsolutePath());
            boolean cmpResult = compareStreams(is1, is2);
            is1.close();
            is2.close();
            if (!cmpResult) {
                differentPagesFail = "Page is different!";
                diffPages.add(i + 1);
                if (compareExecIsOk) {
                    String currCompareParams = compareParams.replace("<image1>", imageFiles[i].getAbsolutePath())
                            .replace("<image2>", cmpImageFiles[i].getAbsolutePath())
                            .replace("<difference>", outPath + differenceImagePrefix + Integer.toString(i + 1) + ".png");
                    if (!SystemUtil.runProcessAndWait(compareExec, currCompareParams))
                        differentPagesFail += "\nPlease, examine " + outPath + differenceImagePrefix + Integer.toString(i + 1) + ".png for more details.";
                }
                System.out.println(differentPagesFail);
            } else {
                System.out.println(" done.");
            }
        }
        if (differentPagesFail != null) {
            String errorMessage = differentPages.replace("<filename>", UrlUtil.toNormalizedURI(outPdf).getPath()).replace("<pagenumber>", listDiffPagesAsString(diffPages));
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
        PdfWriter outWriter = new PdfWriter(outPath + ignoredAreasPrefix + outPdfName);
        PdfWriter cmpWriter = new PdfWriter(outPath + ignoredAreasPrefix + cmpPdfName);

        StampingProperties properties = new StampingProperties();
        properties.setEventCountingMetaInfo(metaInfo);
        PdfDocument pdfOutDoc = new PdfDocument(new PdfReader(outPdf), outWriter, properties);
        PdfDocument pdfCmpDoc = new PdfDocument(new PdfReader(cmpPdf), cmpWriter, properties);

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

        pdfOutDoc.close();
        pdfCmpDoc.close();

        init(outPath + ignoredAreasPrefix + outPdfName, outPath + ignoredAreasPrefix + cmpPdfName);
    }

    private void prepareOutputDirs(String outPath, String differenceImagePrefix) {
        File[] imageFiles;
        File[] cmpImageFiles;
        File[] diffFiles;

        if (!FileUtil.directoryExists(outPath)) {
            FileUtil.createDirectories(outPath);
        } else {
            imageFiles = FileUtil.listFilesInDirectoryByFilter(outPath, new PngFileFilter());
            for (File file : imageFiles) {
                file.delete();
            }
            cmpImageFiles = FileUtil.listFilesInDirectoryByFilter(outPath, new CmpPngFileFilter());
            for (File file : cmpImageFiles) {
                file.delete();
            }

            diffFiles = FileUtil.listFilesInDirectoryByFilter(outPath, new DiffPngFileFilter(differenceImagePrefix));
            for (File file : diffFiles) {
                file.delete();
            }
        }
    }

    /**
     * Runs ghostscript to create images of pdfs.
     *
     * @param outPath Path to the output folder.
     * @throws CompareToolExecutionException
     * @throws IOException
     * @throws InterruptedException
     */
    private void runGhostScriptImageGeneration(String outPath) throws IOException, InterruptedException {
        if (!FileUtil.directoryExists(outPath)) {
            throw new CompareToolExecutionException(cannotOpenOutputDirectory.replace("<filename>", outPdf));
        }

        String currGsParams = gsParams.replace("<outputfile>", outPath + cmpImage).replace("<inputfile>", cmpPdf);
        if (!SystemUtil.runProcessAndWait(gsExec, currGsParams)) {
            throw new CompareToolExecutionException(gsFailed.replace("<filename>", cmpPdf));
        }
        currGsParams = gsParams.replace("<outputfile>", outPath + outImage).replace("<inputfile>", outPdf);
        if (!SystemUtil.runProcessAndWait(gsExec, currGsParams)) {
            throw new CompareToolExecutionException(gsFailed.replace("<filename>", outPdf));
        }
    }

    private void printOutCmpDirectories() {
        System.out.println("Out file folder: file:///" + UrlUtil.toNormalizedURI(new File(outPdf).getParentFile()).getPath());
        System.out.println("Cmp file folder: file:///" + UrlUtil.toNormalizedURI(new File(cmpPdf).getParentFile()).getPath());
    }

    private String compareByContent(String outPath, String differenceImagePrefix, Map<Integer, List<Rectangle>> ignoredAreas) throws InterruptedException, IOException {
        printOutCmpDirectories();
        System.out.print("Comparing by content..........");
        PdfDocument outDocument;
        try {
            outDocument = new PdfDocument(new PdfReader(outPdf, getOutReaderProperties()), new DocumentProperties().setEventCountingMetaInfo(metaInfo));
        } catch (IOException e) {
            throw new IOException("File \"" + outPdf + "\" not found", e);
        }
        List<PdfDictionary> outPages = new ArrayList<>();
        outPagesRef = new ArrayList<>();
        loadPagesFromReader(outDocument, outPages, outPagesRef);

        PdfDocument cmpDocument;
        try {
            cmpDocument = new PdfDocument(new PdfReader(cmpPdf, getCmpReaderProperties()), new DocumentProperties().setEventCountingMetaInfo(metaInfo));
        } catch (IOException e) {
            throw new IOException("File \"" + cmpPdf + "\" not found", e);
        }
        List<PdfDictionary> cmpPages = new ArrayList<>();
        cmpPagesRef = new ArrayList<>();
        loadPagesFromReader(cmpDocument, cmpPages, cmpPagesRef);

        if (outPages.size() != cmpPages.size())
            return compareVisuallyAndCombineReports("Documents have different numbers of pages.", outPath, differenceImagePrefix, ignoredAreas, null);

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
            String outPdfName = new File(outPdf).getName();
            FileOutputStream xml = new FileOutputStream(outPath + "/" + outPdfName.substring(0, outPdfName.length() - 3) + "report.xml");
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
            return compareVisuallyAndCombineReports(compareResult.getReport(), outPath, differenceImagePrefix, ignoredAreas, equalPages);
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
            if (key.equals(PdfName.Parent) || key.equals(PdfName.P) || key.equals(PdfName.ModDate)) continue;
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
                    LoggerFactory.getLogger(CompareTool.class).warn(LogMessageConstant.NUM_TREE_SHALL_NOT_END_WITH_KEY);
                    if (cmpLeftover == null) {
                        if (compareResult != null && currentPath != null) {
                            compareResult.addError(currentPath, "Number tree unexpectedly ends with a key");
                        }
                        dictsAreSame = false;
                    }
                }
                if (cmpLeftover != null) {
                    LoggerFactory.getLogger(CompareTool.class).warn(LogMessageConstant.NUM_TREE_SHALL_NOT_END_WITH_KEY);
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

    private boolean compareObjects(PdfObject outObj, PdfObject cmpObj, ObjectPath currentPath, CompareResult compareResult) {
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

            // If at least on of the page dictionaries is in the document's page tree, we don't proceed with deep comparison,
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
            if (!compareDictionariesExtended((PdfDictionary) outDirectObj, (PdfDictionary) cmpDirectObj, currentPath, compareResult))
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


            String cmpByte = new String(new byte[]{cmpStreamBytes[firstDifferenceOffset]});
            String cmpByteNeighbours = new String(cmpStreamBytes, lCmp, rCmp - lCmp).replaceAll("\\r|\\n", " ");
            String outByte = new String(new byte[]{outStreamBytes[firstDifferenceOffset]});
            String outBytesNeighbours = new String(outStreamBytes, lOut, rOut - lOut).replaceAll("\\r|\\n", " ");
            bytesDifference = MessageFormatUtil.format("First bytes difference is encountered at index {0}. Expected: {1} ({2}). Found: {3} ({4}). Total number of different bytes: {5}",
                    Integer.valueOf(firstDifferenceOffset).toString(), cmpByte, cmpByteNeighbours, outByte, outBytesNeighbours, numberOfDifferentBytes);
        } else { // lengths are different
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
            String cmpByteNeighbours = cmpString.substring(lCmp, rCmp).replaceAll("\\r|\\n", " ");
            String outByte = String.valueOf(outString.charAt(firstDifferenceOffset));
            String outBytesNeighbours = outString.substring(lOut, rOut).replaceAll("\\r|\\n", " ");
            stringDifference = MessageFormatUtil.format("First characters difference is encountered at index {0}.\nExpected: {1} ({2}).\nFound: {3} ({4}).\nTotal number of different characters: {5}",
                    Integer.valueOf(firstDifferenceOffset).toString(), cmpByte, cmpByteNeighbours, outByte, outBytesNeighbours, numberOfDifferentChars);
        } else { // lengths are different
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
                Map<String, PdfObject> cmpNamedDestinations = cmpDocument.getCatalog().getNameTree(PdfName.Dests).getNames();
                Map<String, PdfObject> outNamedDestinations = outDocument.getCatalog().getNameTree(PdfName.Dests).getNames();
                switch (cmpDestObject.getType()) {
                    case PdfObject.ARRAY:
                        explicitCmpDest = (PdfArray) cmpDestObject;
                        explicitOutDest = (PdfArray) outDestObject;
                        break;
                    case PdfObject.NAME:
                        explicitCmpDest = (PdfArray) cmpNamedDestinations.get(((PdfName) cmpDestObject).getValue());
                        explicitOutDest = (PdfArray) outNamedDestinations.get(((PdfName) outDestObject).getValue());
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

    /**
     * Class containing results of the comparison of two documents.
     */
    public class CompareResult {
        // LinkedHashMap to retain order. HashMap has different order in Java6/7 and Java8
        protected Map<ObjectPath, String> differences = new LinkedHashMap<>();
        protected int messageLimit = 1;

        /**
         * Creates new empty instance of CompareResult with given limit of difference messages.
         *
         * @param messageLimit maximum number of difference messages handled by this CompareResult.
         */
        public CompareResult(int messageLimit) {
            this.messageLimit = messageLimit;
        }

        /**
         * Is used to define if documents are considered equal after comparison.
         *
         * @return true if documents are equal, false otherwise.
         */
        public boolean isOk() {
            return differences.size() == 0;
        }

        /**
         * Returns number of differences between two documents met during comparison.
         *
         * @return number of differences.
         */
        public int getErrorCount() {
            return differences.size();
        }

        /**
         * Converts this CompareResult into text form.
         *
         * @return text report of the differences between two documents.
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
         * @return differences map which could be used to find in the document objects that are different.
         */
        public Map<ObjectPath, String> getDifferences() {
            return differences;
        }

        /**
         * Converts this CompareResult into xml form.
         *
         * @param stream output stream to which xml report will be written.
         * @throws ParserConfigurationException
         * @throws TransformerException
         */
        public void writeReportToXml(OutputStream stream) throws ParserConfigurationException, TransformerException {
            Document xmlReport = XmlUtils.initNewXmlDocument();
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

        protected boolean isMessageLimitReached() {
            return differences.size() >= messageLimit;
        }

        protected void addError(ObjectPath path, String message) {
            if (differences.size() < messageLimit) {
                differences.put(((ObjectPath) path.clone()), message);
            }
        }
    }

    /**
     * Class that helps to find two corresponding objects in the comparing documents and also keeps track of the
     * already met in comparing process parent indirect objects.
     * <p>
     * You could say that ObjectPath instance consists of two parts: direct path and indirect path. Direct path defines
     * path to the currently comparing objects in relation to base objects. It could be empty, which would mean that
     * currently comparing objects are base objects themselves. Base objects are the two indirect objects from the comparing
     * documents which are in the same position in the pdf trees. Another part, indirect path, defines which indirect
     * objects were met during comparison process to get to the current base objects. Indirect path is needed to avoid
     * infinite loops during comparison.
     */
    public class ObjectPath {
        protected PdfIndirectReference baseCmpObject;
        protected PdfIndirectReference baseOutObject;
        protected Stack<LocalPathItem> path = new Stack<LocalPathItem>();
        protected Stack<IndirectPathItem> indirects = new Stack<IndirectPathItem>();

        /**
         * Creates empty ObjectPath.
         */
        public ObjectPath() {
        }

        /**
         * Creates ObjectPath with corresponding base objects in two documents.
         *
         * @param baseCmpObject base object in cmp document.
         * @param baseOutObject base object in out document.
         */
        protected ObjectPath(PdfIndirectReference baseCmpObject, PdfIndirectReference baseOutObject) {
            this.baseCmpObject = baseCmpObject;
            this.baseOutObject = baseOutObject;
            indirects.push(new IndirectPathItem(baseCmpObject, baseOutObject));
        }

        private ObjectPath(PdfIndirectReference baseCmpObject, PdfIndirectReference baseOutObject,
                           Stack<LocalPathItem> path, Stack<IndirectPathItem> indirects) {
            this.baseCmpObject = baseCmpObject;
            this.baseOutObject = baseOutObject;
            this.path = path;
            this.indirects = indirects;
        }

        /**
         * Creates a new ObjectPath instance with two new given base objects, which are supposed to be nested in the base
         * objects of the current instance of the ObjectPath. This method is used to avoid infinite loop in case of
         * circular references in pdf documents objects structure.
         * <br>
         * Basically, this method creates copy of the current ObjectPath instance, but resets information of the direct
         * paths, and also adds current ObjectPath instance base objects to the indirect references chain that denotes
         * a path to the new base objects.
         *
         * @param baseCmpObject new base object in cmp document.
         * @param baseOutObject new base object in out document.
         * @return new ObjectPath instance, which stores chain of the indirect references which were already met to get
         * to the new base objects.
         */
        public ObjectPath resetDirectPath(PdfIndirectReference baseCmpObject, PdfIndirectReference baseOutObject) {
            ObjectPath newPath = new ObjectPath(baseCmpObject, baseOutObject,
                    new Stack<LocalPathItem>(), (Stack<IndirectPathItem>) indirects.clone());
            newPath.indirects.push(new IndirectPathItem(baseCmpObject, baseOutObject));
            return newPath;
        }

        /**
         * This method is used to define if given objects were already met in the path to the current base objects.
         * If this method returns true it basically means that we found a loop in the objects structure and that we
         * already compared these objects.
         *
         * @param cmpObject cmp object to check if it was already met in base objects path.
         * @param outObject out object to check if it was already met in base objects path.
         * @return true if given objects are contained in the path and therefore were already compared.
         */
        public boolean isComparing(PdfIndirectReference cmpObject, PdfIndirectReference outObject) {
            return indirects.contains(new IndirectPathItem(cmpObject, outObject));
        }

        /**
         * Adds array item to the direct path. See {@link ArrayPathItem}.
         *
         * @param index index in the array of the direct object to be compared.
         */
        public void pushArrayItemToPath(int index) {
            path.push(new ArrayPathItem(index));
        }

        /**
         * Adds dictionary item to the direct path. See {@link DictPathItem}.
         *
         * @param key key in the dictionary to which corresponds direct object to be compared.
         */
        public void pushDictItemToPath(PdfName key) {
            path.push(new DictPathItem(key));
        }

        /**
         * Adds offset item to the direct path. See {@link OffsetPathItem}.
         *
         * @param offset offset to the specific byte in the stream that is compared.
         */
        public void pushOffsetToPath(int offset) {
            path.push(new OffsetPathItem(offset));
        }

        /**
         * Removes the last path item from the direct path.
         */
        public void pop() {
            path.pop();
        }

        /**
         * Gets local (or direct) path that denotes sequence of the path items from base object to the comparing
         * direct object.
         *
         * @return direct path to the comparing object.
         */
        public Stack<LocalPathItem> getLocalPath() {
            return path;
        }

        /**
         * Gets indirect path which denotes sequence of the indirect references that were passed in comparing process
         * to get to the current base objects.
         *
         * @return indirect path to the current base objects.
         */
        public Stack<IndirectPathItem> getIndirectPath() {
            return indirects;
        }

        /**
         * @return current base object in the cmp document.
         */
        public PdfIndirectReference getBaseCmpObject() {
            return baseCmpObject;
        }

        /**
         * @return current base object in the out document.
         */
        public PdfIndirectReference getBaseOutObject() {
            return baseOutObject;
        }

        /**
         * Creates an xml node that describes a direct path stored in this ObjectPath instance.
         *
         * @param document xml document, to which this xml node will be added.
         * @return an xml node describing direct path.
         */
        public Node toXmlNode(Document document) {
            Element element = document.createElement("path");
            Element baseNode = document.createElement("base");
            baseNode.setAttribute("cmp", MessageFormatUtil.format("{0} {1} obj", baseCmpObject.getObjNumber(), baseCmpObject.getGenNumber()));
            baseNode.setAttribute("out", MessageFormatUtil.format("{0} {1} obj", baseOutObject.getObjNumber(), baseOutObject.getGenNumber()));
            element.appendChild(baseNode);
            Stack<LocalPathItem> pathClone = (Stack<LocalPathItem>) path.clone();
            List<LocalPathItem> localPathItems = new ArrayList<>(path.size());
            for (int i = 0; i < path.size(); ++i) {
                localPathItems.add(pathClone.pop());
            }

            for (int i = localPathItems.size() - 1; i >= 0; --i) {
                element.appendChild(localPathItems.get(i).toXmlNode(document));
            }
            return element;
        }

        /**
         * @return string representation of the direct path stored in this ObjectPath instance.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(MessageFormatUtil.format("Base cmp object: {0} obj. Base out object: {1} obj", baseCmpObject, baseOutObject));

            Stack<LocalPathItem> pathClone = (Stack<LocalPathItem>) path.clone();
            List<LocalPathItem> localPathItems = new ArrayList<>(path.size());
            for (int i = 0; i < path.size(); ++i) {
                localPathItems.add(pathClone.pop());
            }
            for (int i = localPathItems.size() - 1; i >= 0; --i) {
                sb.append("\n");
                sb.append(localPathItems.get(i).toString());
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int hashCode = (baseCmpObject != null ? baseCmpObject.hashCode() : 0) * 31 + (baseOutObject != null ? baseOutObject.hashCode() : 0);
            for (LocalPathItem pathItem : path) {
                hashCode *= 31;
                hashCode += pathItem.hashCode();
            }
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            return obj.getClass() == getClass() && baseCmpObject.equals(((ObjectPath) obj).baseCmpObject) && baseOutObject.equals(((ObjectPath) obj).baseOutObject) &&
                    path.equals(((ObjectPath) obj).path);
        }

        @Override
        protected Object clone() {
            return new ObjectPath(baseCmpObject, baseOutObject, (Stack<LocalPathItem>) path.clone(),
                    (Stack<IndirectPathItem>) indirects.clone());
        }

        /**
         * An item in the indirect path (see {@link ObjectPath}. It encapsulates two corresponding objects from the two
         * comparing documents that were met to get to the path base objects during comparing process.
         */
        public class IndirectPathItem {
            private PdfIndirectReference cmpObject;
            private PdfIndirectReference outObject;

            /**
             * Creates IndirectPathItem instance for two corresponding objects from two comparing documents.
             *
             * @param cmpObject an object from the cmp document.
             * @param outObject an object from the out document.
             */
            public IndirectPathItem(PdfIndirectReference cmpObject, PdfIndirectReference outObject) {
                this.cmpObject = cmpObject;
                this.outObject = outObject;
            }

            /**
             * @return an object from the cmp object that was met to get to the path base objects during comparing process.
             */
            public PdfIndirectReference getCmpObject() {
                return cmpObject;
            }

            /**
             * @return an object from the out object that was met to get to the path base objects during comparing process.
             */
            public PdfIndirectReference getOutObject() {
                return outObject;
            }

            @Override
            public int hashCode() {
                return cmpObject.hashCode() * 31 + outObject.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return (obj.getClass() == getClass() && cmpObject.equals(((IndirectPathItem) obj).cmpObject)
                        && outObject.equals(((IndirectPathItem) obj).outObject));
            }
        }

        /**
         * An abstract class for the items in the direct path (see {@link ObjectPath}.
         */
        public abstract class LocalPathItem {

            /**
             * Creates an xml node that describes this direct path item.
             *
             * @param document xml document, to which this xml node will be added.
             * @return an xml node describing direct path item.
             */
            protected abstract Node toXmlNode(Document document);
        }

        /**
         * Direct path item (see {@link ObjectPath}, which describes transition to the
         * {@link PdfDictionary} entry which value is now a currently comparing direct object.
         */
        public class DictPathItem extends LocalPathItem {
            PdfName key;

            /**
             * Creates an instance of the {@link DictPathItem}.
             *
             * @param key the key which defines to which entry of the {@link PdfDictionary}
             *            the transition was performed.
             */
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
                return obj.getClass() == getClass() && key.equals(((DictPathItem) obj).key);
            }

            /**
             * The key which defines to which entry of the {@link PdfDictionary} the transition was performed.
             * See {@link DictPathItem} for more info.
             *
             * @return a {@link PdfName} which is the key which defines to which entry of the dictionary
             * the transition was performed.
             */
            public PdfName getKey() {
                return key;
            }

            @Override
            protected Node toXmlNode(Document document) {
                Element element = document.createElement("dictKey");
                element.appendChild(document.createTextNode(key.toString()));
                return element;
            }
        }

        /**
         * Direct path item (see {@link ObjectPath}, which describes transition to the
         * {@link PdfArray} element which is now a currently comparing direct object.
         */
        public class ArrayPathItem extends LocalPathItem {
            int index;

            /**
             * Creates an instance of the {@link ArrayPathItem}.
             *
             * @param index the index which defines element of the {@link PdfArray} to which
             *              the transition was performed.
             */
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
                return obj.getClass() == getClass() && index == ((ArrayPathItem) obj).index;
            }

            /**
             * The index which defines element of the {@link PdfArray} to which the transition was performed.
             * See {@link ArrayPathItem} for more info.
             *
             * @return the index which defines element of the array to which the transition was performed
             */
            public int getIndex() {
                return index;
            }

            @Override
            protected Node toXmlNode(Document document) {
                Element element = document.createElement("arrayIndex");
                element.appendChild(document.createTextNode(String.valueOf(index)));
                return element;
            }
        }

        /**
         * Direct path item (see {@link ObjectPath}, which describes transition to the
         * specific position in {@link PdfStream}.
         */
        public class OffsetPathItem extends LocalPathItem {
            int offset;

            /**
             * Creates an instance of the {@link OffsetPathItem}.
             *
             * @param offset bytes offset to the specific position in {@link PdfStream}.
             */
            public OffsetPathItem(int offset) {
                this.offset = offset;
            }

            /**
             * The bytes offset of the stream which defines specific position in the {@link PdfStream}, to which transition
             * was performed.
             *
             * @return an integer defining bytes offset to the specific position in stream.
             */
            public int getOffset() {
                return offset;
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
                return obj.getClass() == getClass() && offset == ((OffsetPathItem) obj).offset;
            }

            @Override
            protected Node toXmlNode(Document document) {
                Element element = document.createElement("offset");
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


        public TrailerPath(PdfDocument cmpDoc, PdfDocument outDoc, Stack<LocalPathItem> path) {
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
            for (LocalPathItem pathItem : path) {
                element.appendChild(pathItem.toXmlNode(document));
            }
            return element;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Base cmp object: trailer. Base out object: trailer");
            for (LocalPathItem pathItem : path) {
                sb.append("\n");
                sb.append(pathItem.toString());
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int hashCode = outDocument.hashCode() * 31 + cmpDocument.hashCode();
            for (LocalPathItem pathItem : path) {
                hashCode *= 31;
                hashCode += pathItem.hashCode();
            }
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            return obj.getClass() == getClass()
                    && outDocument.equals(((TrailerPath) obj).outDocument)
                    && cmpDocument.equals(((TrailerPath) obj).cmpDocument)
                    && path.equals(((ObjectPath) obj).path);
        }

        @Override
        protected Object clone() {
            return new TrailerPath(cmpDocument, outDocument, (Stack<LocalPathItem>) path.clone());
        }

    }

    public class CompareToolExecutionException extends RuntimeException {
        public CompareToolExecutionException(String msg) {
            super(msg);
        }
    }
}
