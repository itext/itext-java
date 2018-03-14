package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.path.DefaultSvgPathShapeFactory;
import com.itextpdf.svg.renderers.path.IPathShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;path&gt; tag.
 */
public class PathSvgNodeRenderer extends AbstractSvgNodeRenderer {

    private static final String SEPERATOR = "";
    private static final String SPACE_CHAR = " ";

    @Override
    public void doDraw(SvgDrawContext context) {
        PdfCanvas canvas = context.getCurrentCanvas();

        for (IPathShape item : getShapes()) {
            item.draw( canvas );
        }
    }

    private Collection<IPathShape> getShapes() {

        Collection<String> parsedResults = parsePropertiesAndStyles();
        Collection<IPathShape> shapes = new ArrayList<>();

        for (String parsedResult : parsedResults) {

            //split att to {M , 100, 100}
            String[] pathPropertis = parsedResult.split( SPACE_CHAR );
            if (pathPropertis.length > 0 && !pathPropertis[0].equals( SEPERATOR )) {
                if (pathPropertis[0].equalsIgnoreCase( SvgTagConstants.PATH_DATA_CLOSE_PATH )) {

                    //This may be removed as closePathe could be added inside doDraw method
                    shapes.add( DefaultSvgPathShapeFactory.createPathShape( SvgTagConstants.PATH_DATA_CLOSE_PATH ) );
                } else {
                    //Implements (absolute) command value only
                    //TODO implement relative values e. C(absalute), c(relative)
                    IPathShape pathShape = DefaultSvgPathShapeFactory.createPathShape( pathPropertis[0].toUpperCase() );

                    pathShape.setCoordinates( Arrays.copyOfRange( pathPropertis, 1, pathPropertis.length ) );
                    shapes.add( pathShape );
                }
            }
        }
        return shapes;
    }

    private Collection<String> parsePropertiesAndStyles() {
        StringBuilder result = new StringBuilder();
        String attributes = attributesAndStyles.get( SvgTagConstants.D );
        String closePath = attributes.indexOf( 'z' ) > 0 ? attributes.substring( attributes.indexOf( 'z' ) ) : "".trim();

        if (!closePath.equals( SEPERATOR )) {
            attributes = attributes.replace( closePath, SEPERATOR ).trim();
        }

        String SPLIT_REGEX = "(?=[\\p{L}][^,;])";
        String[] coordinates = attributes.split(SPLIT_REGEX);//gets an array attributesAr of {M 100 100, L 300 100, L200, 300, z}

        for (String inst : coordinates) {
            if (!inst.equals( SEPERATOR )) {
                String instruction = inst.charAt( 0 ) + SPACE_CHAR;
                inst = instruction + inst.replace( inst.charAt( 0 ) + SEPERATOR, SEPERATOR ).replace( ",", SPACE_CHAR ).trim();
                result.append(SPACE_CHAR).append(inst);
            }
        }

        String[] resultArray = result.toString().split(SPLIT_REGEX);
        List<String> resultList = new ArrayList<>( Arrays.asList( resultArray ) );
        resultList.add( closePath );
        return resultList;
    }
}
