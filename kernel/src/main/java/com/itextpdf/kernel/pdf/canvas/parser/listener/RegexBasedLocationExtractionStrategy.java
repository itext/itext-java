package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;

import javax.xml.soap.Text;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is designed to search for the occurrences of a regular expression and return the resultant rectangles.
 */
public class RegexBasedLocationExtractionStrategy implements ILocationExtractionStrategy {

    private Pattern pattern;
    private List<CharacterRenderInfo> parseResult = new ArrayList<>();

    public RegexBasedLocationExtractionStrategy(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public RegexBasedLocationExtractionStrategy(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Collection<IPdfTextLocation> getResultantLocations() {

        // align characters in "logical" order
        Collections.sort(parseResult);

        // process parse results
        List<IPdfTextLocation> retval = new ArrayList<>();

        CharacterRenderInfo.StringConversionInfo txt = CharacterRenderInfo.mapString(parseResult);

        Matcher mat = pattern.matcher(txt.text);
        while (mat.find()) {
            int startIndex = txt.indexMap.get(mat.start());
            int endIndex = txt.indexMap.get(mat.end());
            for (Rectangle r : toRectangles(parseResult.subList(startIndex, endIndex))) {
                retval.add(new DefaultPdfTextLocation(0, r, mat.group(0)));
            }
        }

        /* sort
         * even though the return type is Collection<Rectangle>, we apply a sorting algorithm here
         * This is to ensure that tests that use this functionality (for instance to generate pdf with
         * areas of interest highlighted) will not break when compared.
         */
        java.util.Collections.sort(retval, new Comparator<IPdfTextLocation>() {
            @Override
            public int compare(IPdfTextLocation l1, IPdfTextLocation l2) {
                Rectangle o1 = l1.getRectangle();
                Rectangle o2 = l2.getRectangle();
                if (o1.getY() == o2.getY()) {
                    return o1.getX() == o2.getX() ? 0 : (o1.getX() < o2.getX() ? -1 : 1);
                } else {
                    return o1.getY() < o2.getY() ? -1 : 1;
                }
            }
        });

        return retval;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof TextRenderInfo) {
            parseResult.addAll(toCRI((TextRenderInfo) data));
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }

    /**
     * Convert {@code TextRenderInfo} to {@code CharacterRenderInfo}
     * This method is public and not final so that custom implementations can choose to override it.
     * Other implementations of {@code CharacterRenderInfo} may choose to store different properties than
     * merely the {@code Rectangle} describing the bounding box. E.g. a custom implementation might choose to
     * store {@code Color} information as well, to better match the content surrounding the redaction {@code Rectangle}.
     *
     * @param tri
     * @return
     */
    protected List<CharacterRenderInfo> toCRI(TextRenderInfo tri) {
        List<CharacterRenderInfo> cris = new ArrayList<>();
        for (TextRenderInfo subTri : tri.getCharacterRenderInfos()) {
            cris.add(new CharacterRenderInfo(subTri));
        }
        return cris;
    }

    /**
     * Converts {@code CharacterRenderInfo} objects to {@code Rectangles}
     * This method is protected and not final so that custom implementations can choose to override it.
     * E.g. other implementations may choose to add padding/margin to the Rectangles.
     * This method also offers a convenient access point to the mapping of {@code CharacterRenderInfo} to {@code Rectangle}.
     * This mapping enables (custom implementations) to match color of text in redacted Rectangles,
     * or match color of background, by the mere virtue of offering access to the {@code CharacterRenderInfo} objects
     * that generated the {@code Rectangle}.
     *
     * @param cris
     * @return
     */
    protected List<Rectangle> toRectangles(List<CharacterRenderInfo> cris) {
        List<Rectangle> retval = new ArrayList<>();
        if (cris.isEmpty())
            return retval;

        int prev = 0;
        int curr = 0;
        while (curr < cris.size()) {
            while (curr < cris.size() && cris.get(curr).sameLine(cris.get(prev))) {
                curr++;
            }
            float x = cris.get(prev).getBoundingBox().getX();
            float y = cris.get(prev).getBoundingBox().getY();
            float w = cris.get(curr - 1).getBoundingBox().getX() - cris.get(prev).getBoundingBox().getX() + cris.get(curr - 1).getBoundingBox().getWidth();
            float h = 0f;
            for (CharacterRenderInfo cri : cris.subList(prev, curr)) {
                h = Math.max(h, cri.getBoundingBox().getHeight());
            }
            retval.add(new Rectangle(x, y, w, h));
            prev = curr;
        }

        // return
        return retval;
    }

}
