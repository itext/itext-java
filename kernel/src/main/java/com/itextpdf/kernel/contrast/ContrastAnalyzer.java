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
package com.itextpdf.kernel.contrast;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.contrast.ContrastResult.OverlappingArea;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.IShape;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Subpath;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.Point.LongPoint;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.PolyTree;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.ClipperBridge;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.DefaultClipper;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.ClipType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.PolyFillType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.PolyType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.Paths;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analyzes color contrast ratios between text and backgrounds in PDF pages.
 * This class is designed to help identify accessibility issues related to insufficient
 * contrast between text and background colors, which is important for WCAG compliance.
 *
 *
 * <h2>Features</h2>
 * <p>
 * *<b>Text-to-Background Contrast Analysis:</b> Calculates contrast ratios between text
 * and all overlapping background elements on a page.
 * *<b>Individual Character Analysis:</b> Optional character-by-character analysis for
 * improved accuracy (enabled by default).
 * *<b>Multiple Background Handling:</b> Correctly handles cases where text overlaps
 * multiple backgrounds by analyzing all intersecting backgrounds.
 * *<b>Color Space Support:</b> Supports DeviceRGB, DeviceGray, and DeviceCMYK color spaces.
 * Other color spaces may not be fully supported.
 * *<b>Geometric Calculations:</b> Uses polygon intersection algorithms to accurately
 * determine which backgrounds affect which text elements.
 * *<b>Default Background:</b> Assumes a white background for text that doesn't overlap
 * any explicit background elements.
 *
 *
 * <h2>Current Limitations</h2>
 * <p>
 * *<b>Clipping Path Support:</b> Clipped-out text is currently still processed
 * and analyzed. The analyzer does not respect clipping paths, so text that would be invisible
 * due to clipping will still appear in the contrast results.
 * *<b>Layer Visibility :</b> Content on PDF layers (Optional Content Groups) is
 * always analyzed regardless of layer visibility state. Content on hidden layers will be
 * included in the analysis as if they were visible.
 * *<b>Complex Color Spaces:</b> Advanced color spaces (Lab, ICC-based, Separation, DeviceN, etc.)
 * may not convert accurately to RGB for contrast calculations.
 * *<b>Transparency/Opacity:</b> Does not account for opacity or transparency effects.
 * All elements are treated as fully opaque.
 * *<b>Images as Backgrounds:</b> Currently only analyzes vector path backgrounds.
 * Images used as backgrounds are not considered in the contrast analysis.
 * *<b>Text Rendering Modes:</b> Only analyzes fill color. Stroke color for outlined text
 * is not considered.
 * *<b>Text on Text:</b> Text on Text not supported.
 * *<b>Performance:</b> Character-by-character analysis can be computationally expensive
 * for pages with large amounts of text.
 * *<b>Images</b>
 * Text drawn over images is not analyzed for contrast currently.l
 *
 * @see ContrastResult
 * @see OverlappingArea
 * @see ColorContrastCalculator
 */
//TODO DEVSIX-9719 Improve layer handling in contrast analysis
//TODO DEVSIX-9718 Improve clip path handling in contrast analysis
public class ContrastAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContrastAnalyzer.class);

    private boolean checkForIndividualCharacters;
    private int maxAmountOfPointInPolygon = 30;

    /**
     * Creates a new {@link ContrastAnalyzer} with default settings.
     *
     * @param checkForIndividualCharacters {@code true} to analyze each character separately, {@code false} to analyze
     *                                     whole text as it
     *                                     would be processed by the PDF renderer. @see
     *                                     ContrastAnalyzer#setCheckForIndividualCharacters(boolean)
     */
    public ContrastAnalyzer(boolean checkForIndividualCharacters) {
        setCheckForIndividualCharacters(checkForIndividualCharacters);
    }

    /**
     * Sets the maximum number of points allowed in a polygon for contrast calculations.
     * <p>
     * This setting helps prevent performance issues when processing complex shapes.
     * If either the text or background polygon exceeds this number of points,
     * the contrast calculation between them will be skipped.
     * This is particularly useful for handling complex vector graphics
     * The default value is 30 points.
     *
     * @param maxAmountOfPointInPolygon the maximum number of points allowed in a polygon
     */
    public void setMaxAmountOfPointInPolygon(int maxAmountOfPointInPolygon) {
        this.maxAmountOfPointInPolygon = maxAmountOfPointInPolygon;
    }

    /**
     * Sets whether to check contrast for individual characters.
     * <p>
     * When enabled (default), each character in a text string is analyzed separately for contrast.
     * This provides more accurate results as different characters may have different backgrounds,
     * but it significantly impacts performance on pages with large amounts of text.
     * <p>
     * When disabled, entire text render operations are analyzed as a single unit, which is faster
     * but may miss contrast issues that only affect specific characters within a text string.
     *
     * @param checkForIndividualCharacters true to analyze each character separately, false to analyze whole text as it
     *                                     would be processed by the PDF renderer
     *
     * @return the {@link ContrastAnalyzer} instance for method chaining
     */
    public final ContrastAnalyzer setCheckForIndividualCharacters(boolean checkForIndividualCharacters) {
        this.checkForIndividualCharacters = checkForIndividualCharacters;
        return this;
    }

    /**
     * Analyzes the contrast ratios between text and backgrounds on the given PDF page.
     * <p>
     * This method processes all text and background elements on the page, calculating contrast
     * ratios for each text element against all overlapping backgrounds. The analysis includes:
     * <p>
     * *Extracting all text render operations and their bounding boxes
     * *Extracting all path render operations that serve as backgrounds
     * *Computing geometric intersections between text and backgrounds
     * *Calculating contrast ratios using WCAG formulas
     * *Handling cases where text overlaps multiple backgrounds
     *
     * @param page the PDF page to analyze for contrast issues
     *
     * @return a list of contrast results, one for each text element that has overlapping backgrounds.
     * Returns an empty list if no text elements with backgrounds are found.
     *
     * @throws PdfException if unsupported shape segments are encountered during analysis
     */
    public List<ContrastResult> checkPageContrast(PdfPage page) {
        if (isPageOrUnderlyingStreamFlushed(page)) {
            LOGGER.warn(KernelLogMessageConstant.PAGE_IS_FLUSHED_NO_CONTRAST);
            return new ArrayList<>();
        }

        List<ContrastResult> contrastResults = new ArrayList<>();
        List<ColorInfo> renderInfoList = new ArrayList<>();
        //Add one render info with white background to compare with all other render infos
        addDefaultBackground(renderInfoList, page);
        IEventListener listener = new ColorInfoListener(page, renderInfoList, this.checkForIndividualCharacters);
        PdfCanvasProcessor canvasProcessor = new FontResolvingDocumentProcessor(listener,
                (fontDict) -> page.getDocument().getFont(fontDict));

        int pageNumber = page.getDocument().getPageNumber(page);
        canvasProcessor.processPageContent(page);
        for (int i = 0; i < renderInfoList.size(); i++) {
            ContrastResult textContrastInformation = calculateContrastOfTextRenderer(renderInfoList,
                    renderInfoList.get(i), pageNumber, i);
            if (textContrastInformation != null) {
                contrastResults.add(textContrastInformation);
            }
        }
        return contrastResults;
    }

    /**
     * Checks if the page or any of its underlying content streams have been flushed.
     * <p>
     * Flushed content cannot be processed for contrast analysis as the content stream
     * data is no longer available in memory. This method verifies both the page itself
     * and all its content streams to determine if analysis is possible.
     *
     * @param page the PDF page to check
     *
     * @return {@code true} if the page or any of its content streams are flushed, {@code false} otherwise
     */
    private boolean isPageOrUnderlyingStreamFlushed(PdfPage page) {
        if (page.isFlushed()) {
            return true;
        }

        for (int i = 0; i < page.getContentStreamCount(); i++) {
            if (page.getContentStream(i).isFlushed()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Calculates the contrast result for a single text renderer against all background elements.
     * <p>
     * This method processes backgrounds from top to bottom (end to start of the list) to prioritize
     * topmost elements. If the text is fully covered by the topmost element(s), processing stops.
     * Otherwise, it continues analyzing all background elements below and sums up the intersection
     * areas until reaching a background that completely covers the text.
     * <p>
     * Only text render information is processed; background elements passed as possibleTextRenderer
     * will return null.
     *
     * @param allRenderers         the complete list of all render information objects (text and backgrounds) on the
     *                             page
     * @param possibleTextRenderer the renderer to analyze, must be a TextContrastInformation to be processed
     * @param pageNumber           the page number where the renderer is located
     *
     * @return a {@link ContrastResult} containing all background intersections and their contrast ratios,
     * or {@code null} if possibleTextRenderer is not text or has no intersecting backgrounds
     */
    private ContrastResult calculateContrastOfTextRenderer(List<ColorInfo> allRenderers, ColorInfo possibleTextRenderer,
            int pageNumber, int currentDepth) {
        if (!(possibleTextRenderer instanceof TextColorInfo)) {
            // we only calculate contrast between text and background
            // so we will only process text render infos here
            return null;
        }
        TextColorInfo textContrastInfo = (TextColorInfo) possibleTextRenderer;
        final ContrastResult contrastResult = new ContrastResult(textContrastInfo, pageNumber);

        for (int j = currentDepth - 1; j >= 0; j--) {
            final ColorInfo backGround = allRenderers.get(j);
            if (backGround instanceof TextColorInfo) {
                //We are only interested in background and clip render infos here
                continue;
            }

            final boolean hasTooManyPoints =
                    textContrastInfo.getPath().getSubpaths().size() > this.maxAmountOfPointInPolygon
                            || backGround.getPath().getSubpaths().size() > this.maxAmountOfPointInPolygon;

            if (hasTooManyPoints) {
                // instead of warning we could kinda flatten the paths here to reduce the amount of points
                // the big amount of background mainly happens on svg images with lot of details
                LOGGER.warn("Skipping contrast calculation between text and background for "
                        + "text: '" + textContrastInfo.getText() + "' on page " + pageNumber
                        + " because one of them has too "
                        + "many points in polygon. Text points: " + textContrastInfo.getPath().getSubpaths().size()
                        + " Background points: " + backGround.getPath().getSubpaths().size()
                        + " if this is intended you can increase the maxAmountOfPointInPolygon property.");
                continue;
            }

            final PolyTree intersectionPathBetweenTextAndBackground = calculateIntersectionPath(
                    textContrastInfo.getPath(), backGround.getPath());

            if (intersectionPathBetweenTextAndBackground.getTotalSize() != 0) {
                final DeviceRgb color1 = convertToRGB(textContrastInfo.getColor());
                final DeviceRgb color2 = convertToRGB(backGround.getColor());
                //fast check first for unsupported color spaces to avoid unnecessary calculations
                if (color1 == null || color2 == null) {
                    //Means color space can't be converted to be usable for contrast calculation
                    continue;
                }

                final OverlappingArea overlappingArea = new OverlappingArea((BackgroundColorInfo) backGround,
                        ColorContrastCalculator.contrastRatio(color1, color2));

                contrastResult.addContrastResult(overlappingArea);
                final Path unionOfAllIntersectionPaths = getOutlinesOfAllPoints(
                        contrastResult.getOverlappingAreas().stream().map(p -> p.getBackgroundRenderInfo().getPath())
                                .collect(Collectors.toList()));

                final PolyTree outlinePaths = calculateIntersectionPath(textContrastInfo.getPath(),
                        unionOfAllIntersectionPaths);
                final Path intersectionOutlinePath = new ClipperBridge(unionOfAllIntersectionPaths).convertToPath(
                        outlinePaths);
                final double intersectionAreaAll = calculatePolygonArea(convertPathToPoints(intersectionOutlinePath));
                final double textRenderArea = calculatePolygonArea(convertPathToPoints(textContrastInfo.getPath()));
                final double intersectionAreaCoversText = intersectionAreaAll / textRenderArea;
                overlappingArea.setOverlapRatio(intersectionAreaCoversText);

                if (intersectionAreaAll >= textRenderArea - 0.01) {
                    //The text render info is completely covered by the union of all background render infos
                    // we can stop processing more background render infos because all the underlying colors
                    // do not
                    // matter for the final contrast ratio as they should not be visible anyway
                    break;
                }
            }
        }
        final boolean hasIntersectionWithBackGround = !contrastResult.getOverlappingAreas().isEmpty();
        return hasIntersectionWithBackGround ? contrastResult : null;
    }


    private static DeviceRgb convertToRGB(Color color) {
        if (color == null) {
            return null;
        }
        if (color instanceof DeviceRgb) {
            return (DeviceRgb) color;
        } else if (color instanceof DeviceGray) {
            float gray = color.getColorValue()[0];
            return new DeviceRgb(gray, gray, gray);
        } else if (color instanceof DeviceCmyk) {
            return Color.convertCmykToRgb((DeviceCmyk) color);
        } else if (color instanceof PatternColor) {
            return null;
        } else {
            float[] components = color.getColorValue();
            if (components.length == 1) {
                return new DeviceRgb(components[0], components[0], components[0]);
            } else if (components.length == 3) {
                return new DeviceRgb(components[0], components[1], components[2]);
            } else {
                LOGGER.warn(MessageFormatUtil.format(KernelLogMessageConstant.UNSUPPORTED_COLOR_SPACE_CONTRAST,
                        color.getClass().getName()));
                return null;
            }
        }
    }


    /**
     * Calculates the area of a polygon defined by an array of vertices.
     * This method first computes the convex hull of the points to handle cases where
     * the points may not be in order or form a complex polygon, then calculates the
     * area using the shoelace formula.
     *
     * @param vertices the array of points defining the polygon vertices
     *
     * @return the area of the polygon in square units
     */
    private static double calculatePolygonArea(Point[] vertices) {
        //We calculate the convex hull of the points to avoid issues with complex polygons and the Points not being
        // in order.
        List<Point> hull = ConvexHullArea.convexHull(Arrays.asList(vertices));
        return polygonArea(hull);
    }


    /**
     * Calculates the area of a polygon using the shoelace formula.
     * The polygon is defined by an ordered list of vertices.
     *
     * @param polygon the list of points defining the polygon vertices in order
     *
     * @return the area of the polygon, or 0 if the polygon has fewer than 3 vertices
     */
    //Shoelace formula
    private static double polygonArea(List<Point> polygon) {
        int n = polygon.size();
        if (n < 3) {
            return 0;
        }

        double area = 0;
        for (int i = 0; i < n; i++) {
            Point p1 = polygon.get(i);
            Point p2 = polygon.get((i + 1) % n);
            area += (p1.getX() * p2.getY()) - (p2.getX() * p1.getY());
        }
        return Math.abs(area) / 2.0;
    }

    /**
     * Computes the union of multiple paths to create a single outline path.
     * This is used to determine the total area covered by multiple overlapping backgrounds.
     * Uses the Clipper library to perform polygon union operations.
     *
     * @param pathPoints the list of paths to combine
     *
     * @return a {@link Path} representing the union of all input paths, or an empty Path if the operation fails
     */
    private static Path getOutlinesOfAllPoints(List<Path> pathPoints) {
        Path[] pathsArray = pathPoints.toArray(new Path[0]);
        ClipperBridge clipperBridge = new ClipperBridge(pathsArray);
        DefaultClipper clipper = new DefaultClipper();

        for (Path path : pathPoints) {
            Point[] pathAsPointsTextRender = convertPathToPoints(path);
            clipperBridge.addPolygonToClipper(clipper, pathAsPointsTextRender, IClipper.PolyType.SUBJECT);
        }

        Paths paths = new Paths();
        boolean result = clipper.execute(ClipType.UNION, paths, IClipper.PolyFillType.NON_ZERO, PolyFillType.NON_ZERO);
        if (!result) {
            return new Path();
        }
        Path resultPath = new Path();
        for (List<LongPoint> longPoints : paths) {
            List<Point> floatPoints = clipperBridge.convertToFloatPoints(longPoints);
            Subpath subpath = new Subpath();
            if (floatPoints.isEmpty()) {
                continue;
            }
            subpath.setStartPoint(floatPoints.get(0));
            for (int i = 1; i < floatPoints.size(); i++) {
                subpath.addSegment(new Line(floatPoints.get(i - 1), floatPoints.get(i)));
            }
            subpath.setClosed(true);
            resultPath.addSubpath(subpath);
        }

        return resultPath;
    }

    /**
     * Calculates the intersection between a text path and a background path.
     * This determines which parts of the text overlap with which backgrounds, enabling
     * accurate contrast calculations only for overlapping regions.
     * Uses the Clipper library for polygon intersection operations.
     *
     * @param textPath       the path representing the text bounding box
     * @param backgroundPath the path representing the background shape
     *
     * @return a {@link PolyTree} representing the intersection, or an empty {@link PolyTree}
     * if there is no intersection
     */
    private static PolyTree calculateIntersectionPath(Path textPath, Path backgroundPath) {
        final ClipperBridge clipperBridge = new ClipperBridge(textPath, backgroundPath);
        final DefaultClipper clipper = new DefaultClipper();

        final Point[] pathAsPointsTextRender = convertPathToPoints(textPath);
        clipperBridge.addPolygonToClipper(clipper, pathAsPointsTextRender, IClipper.PolyType.SUBJECT);

        final Point[] pathsAsPointBackgroundRender = convertPathToPoints(backgroundPath);
        clipperBridge.addPolygonToClipper(clipper, pathsAsPointBackgroundRender, PolyType.CLIP);

        final PolyTree paths = new PolyTree();
        boolean result = clipper.execute(ClipType.INTERSECTION, paths, IClipper.PolyFillType.NON_ZERO,
                PolyFillType.NON_ZERO);
        if (!result) {
            return new PolyTree();
        }
        return paths;
    }

    private static Point[] convertPathToPoints(Path path) {
        List<Point> points = new ArrayList<>();
        for (Subpath subpath : path.getSubpaths()) {
            for (IShape segment : subpath.getSegments()) {
                points.addAll(segment.getBasePoints());
            }
        }
        return points.toArray(new Point[0]);
    }

    /**
     * Adds a default white background that covers the entire page to the contrast information list.
     * This ensures that all text elements have at least one background to compare against, even if
     * they don't overlap with any explicitly drawn backgrounds in the PDF.
     *
     * @param contrastInfoList the list to add the default background to
     * @param page             the PDF page whose dimensions define the background rectangle
     */
    private static void addDefaultBackground(List<ColorInfo> contrastInfoList, PdfPage page) {
        Path backgroundPath = new Path();
        Subpath backgroundSubpath = new Subpath();
        backgroundSubpath.setStartPoint(0, 0);
        backgroundSubpath.addSegment(new Line(new Point(0, 0), new Point(page.getPageSize().getWidth(), 0)));
        backgroundSubpath.addSegment(new Line(new Point(page.getPageSize().getWidth(), 0),
                new Point(page.getPageSize().getWidth(), page.getPageSize().getHeight())));
        backgroundSubpath.addSegment(new Line(new Point(page.getPageSize().getWidth(), page.getPageSize().getHeight()),
                new Point(0, page.getPageSize().getHeight())));
        backgroundSubpath.addSegment(new Line(new Point(0, page.getPageSize().getHeight()), new Point(0, 0)));
        backgroundPath.addSubpath(backgroundSubpath);

        contrastInfoList.add(new BackgroundColorInfo(ColorConstants.WHITE, backgroundPath));
    }

    private static final class FontResolvingDocumentProcessor extends PdfCanvasProcessor {
        private final Function<PdfDictionary, PdfFont> fontSupplier;

        public FontResolvingDocumentProcessor(IEventListener listener, Function<PdfDictionary, PdfFont> fontSupplier) {
            super(listener);
            this.fontSupplier = fontSupplier;
        }

        @Override
        protected PdfFont getFont(PdfDictionary fontDict) {
            PdfFont font = fontSupplier.apply(fontDict);
            if (font != null) {
                return font;
            }
            return super.getFont(fontDict);
        }
    }
}

