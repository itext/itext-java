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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.BezierCurve;
import com.itextpdf.kernel.geom.IShape;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Subpath;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.PathRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ColorInfoListener implements IEventListener {

    private final PdfPage page;
    private final List<ColorInfo> renderInfoList;
    private final boolean checkForIndividualCharacters;

    public ColorInfoListener(PdfPage page, List<ColorInfo> renderInfoList, boolean checkForIndividualCharacters) {
        this.page = page;
        this.renderInfoList = renderInfoList;
        this.checkForIndividualCharacters = checkForIndividualCharacters;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (EventType.RENDER_PATH == type) {
            PathRenderInfo pathRenderInfo = (PathRenderInfo) data;
            if (!this.checkIfLayerAndNeedsToBeIncluded(pathRenderInfo, page)) {
                return;
            }

            if (pathRenderInfo.isPathModifiesClippingPath()) {
                // clipping paths also generate render_paths events, they have a
                // default background black color which messes up the contrast calculation because to they
                // don't get rendered with color so to the eye they are transparent so we don't need them.
                // But this means in current implementation clipped out text will still be processed
                //TODO DEVSIX-9718 Improve clip path handling in contrast analysis
                return;
            }
            Path path = new Path();
            for (Subpath subpath : pathRenderInfo.getPath().getSubpaths()) {
                for (IShape segment : subpath.getSegments()) {
                    if (segment instanceof BezierCurve) {
                        //flatten bezier curves to triangles
                        path.addSubpath(flattenBezierCurve((BezierCurve) segment));
                    } else if (segment instanceof Line) {
                        path.addSubpath(subpath);
                    } else {
                        throw new PdfException(
                                "Unsupported shape segment found: " + segment.getClass().getName());
                    }
                }
            }
            renderInfoList.add(new BackgroundColorInfo(pathRenderInfo.getFillColor(), path));
        }
        if (EventType.RENDER_TEXT == type) {
            TextRenderInfo re = (TextRenderInfo) data;

            if (checkForIndividualCharacters) {
                for (TextRenderInfo characterRenderInfo : re.getCharacterRenderInfos()) {
                    Path p = buildPathFromTextRenderInfo(characterRenderInfo);
                    final String text = characterRenderInfo.getText();
                    //skip empty text render infos
                    if (text == null || text.isEmpty() || text.trim().isEmpty()) {
                        continue;
                    }
                    TextColorInfo contrastInformationRenderInfo = new TextColorInfo(
                            text,
                            re.getText(),
                            characterRenderInfo.getFillColor(),
                            p,
                            characterRenderInfo.getFontSize());
                    renderInfoList.add(contrastInformationRenderInfo);
                }
            } else {
                Path p = buildPathFromTextRenderInfo(re);
                final String text = re.getText();
                if (text == null || text.isEmpty() || text.trim().isEmpty()) {
                    return;
                }
                TextColorInfo contrastInformationRenderInfo = new TextColorInfo(
                        text, null, re.getFillColor(), p,
                        re.getFontSize());
                renderInfoList.add(contrastInformationRenderInfo);
            }
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return new HashSet<>(Arrays.asList(EventType.BEGIN_TEXT, EventType.RENDER_TEXT, EventType.END_TEXT,
                EventType.RENDER_IMAGE, EventType.RENDER_PATH, EventType.CLIP_PATH_CHANGED));
    }

    /**
     * Checks if a path render info belongs to a PDF layer and whether it should be included in analysis.
     * <p>
     * This method examines the canvas tag hierarchy to determine if the path is part of an
     * Optional Content Group (OCG/layer). If it is part of a layer, it checks whether that
     * layer is currently visible.
     * <p>
     * <b>Note:</b> Currently this method always returns @code{true} due a known issue.
     *
     * @param pathRenderInfo the path render information to check
     * @param page           the PDF page containing the path
     *
     * @return {@code true} if the path should be included in contrast analysis, {@code false} if it should be
     * skipped
     */
    private boolean checkIfLayerAndNeedsToBeIncluded(PathRenderInfo pathRenderInfo, PdfPage page) {
        //TODO DEVSIX-9719 if should be implemented when the ticket is fixed
        return true;
    }

    private static Path buildPathFromTextRenderInfo(TextRenderInfo characterRenderInfo) {
        Path path = new Path();
        Subpath subpath = new Subpath();

        Vector start = characterRenderInfo.getDescentLine().getStartPoint();

        LineSegment ascent = characterRenderInfo.getAscentLine();
        LineSegment descent = characterRenderInfo.getDescentLine();

        subpath.setStartPoint((float) start.get(0), (float) start.get(1));

        Point[] r = new Point[] {new Point(ascent.getStartPoint().get(0), ascent.getStartPoint().get(1)),
                new Point(ascent.getEndPoint().get(0), ascent.getEndPoint().get(1)),
                new Point(descent.getEndPoint().get(0), descent.getEndPoint().get(1)),
                new Point(descent.getStartPoint().get(0), descent.getStartPoint().get(1)),};

        // convert rectangle to path
        subpath.addSegment(new Line(r[0], r[1]));
        subpath.addSegment(new Line(r[1], r[2]));
        subpath.addSegment(new Line(r[2], r[3]));
        subpath.setClosed(true);

        path.addSubpath(subpath);

        return path;
    }


    /**
     * Flattens a Bezier curve into a series of line segments for geometric calculations.
     * This is necessary because intersection algorithms work with line segments, not curves.
     *
     * @param bezierCurve the Bezier curve to flatten
     *
     * @return a Subpath containing line segments that approximate the curve
     */
    private static Subpath flattenBezierCurve(BezierCurve bezierCurve) {
        List<Point> p = bezierCurve.getPiecewiseLinearApproximation(BezierCurve.curveCollinearityEpsilon, 2,
                BezierCurve.distanceToleranceManhattan);
        Subpath subpath = new Subpath();
        subpath.setStartPoint(p.get(0));
        for (int i = 1; i < p.size(); i++) {
            subpath.addSegment(new Line(p.get(i - 1), p.get(i)));
        }
        return subpath;
    }

}
