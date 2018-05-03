/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.path.DefaultSvgPathShapeFactory;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.svg.renderers.path.impl.CurveTo;
import com.itextpdf.svg.renderers.path.impl.SmoothSCurveTo;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;path&gt; tag.
 */
public class PathSvgNodeRenderer extends AbstractSvgNodeRenderer {

    private static final String SEPERATOR = "";
    private static final String SPACE_CHAR = " ";
    private final String SPLIT_REGEX = "(?=[\\p{L}][^,;])";

    @Override
    public void doDraw(SvgDrawContext context) {
        PdfCanvas canvas = context.getCurrentCanvas();
        canvas.writeLiteral("% path\n");
        for (IPathShape item : getShapes()) {
            item.draw( canvas );
        }
    }

    private Collection<IPathShape> getShapes() {
        Collection<String> parsedResults = parsePropertiesAndStyles();
        List<IPathShape> shapes = new ArrayList<>();

        for (String parsedResult : parsedResults) {
            //split att to {M , 100, 100}
            String[] pathProperties = parsedResult.split( SPACE_CHAR );
            if (pathProperties.length > 0 && !pathProperties[0].equals( SEPERATOR )) {
                if (pathProperties[0].equalsIgnoreCase( SvgConstants.Attributes.PATH_DATA_CLOSE_PATH )) {
                    continue;
                } else {
                    String[] startingControlPoint = new String[2];

                    //Implements (absolute) command value only
                    //TODO implement relative values e. C(absalute), c(relative)
                    IPathShape pathShape = DefaultSvgPathShapeFactory.createPathShape( pathProperties[0].toUpperCase() );
                    if (pathShape instanceof SmoothSCurveTo) {
                        IPathShape previousCommand = !shapes.isEmpty() ? shapes.get( shapes.size() - 1 ) : null;
                        if (previousCommand != null) {
                            Map<String, String> coordinates = previousCommand.getCoordinates();

                            /*if the previous command was a C or S use its last control point*/
                            if (((previousCommand instanceof CurveTo) || (previousCommand instanceof SmoothSCurveTo))) {
                                float reflectedX= (float) (2* CssUtils.parseFloat(coordinates.get( SvgConstants.Attributes.X ))-CssUtils.parseFloat(coordinates.get(SvgConstants.Attributes.X2)));
                                float reflectedy= (float) (2* CssUtils.parseFloat(coordinates.get( SvgConstants.Attributes.Y ))-CssUtils.parseFloat(coordinates.get(SvgConstants.Attributes.Y2)));

                                startingControlPoint[0] = SvgCssUtils.convertFloatToString( reflectedX );
                                startingControlPoint[1] = SvgCssUtils.convertFloatToString( reflectedy );
                            } else {
                                startingControlPoint[0] = coordinates.get( SvgConstants.Attributes.X );
                                startingControlPoint[1] = coordinates.get( SvgConstants.Attributes.Y );
                            }
                        } else {
                            // TODO RND-951
                            startingControlPoint[0] = pathProperties[1];
                            startingControlPoint[1] = pathProperties[2];
                        }
                        String[] properties = concatenate( startingControlPoint, Arrays.copyOfRange( pathProperties, 1, pathProperties.length ) );
                        pathShape.setCoordinates( properties );
                        shapes.add( pathShape );
                    } else {
                        pathShape.setCoordinates( Arrays.copyOfRange( pathProperties, 1, pathProperties.length ) );
                        shapes.add( pathShape );
                    }
                }
            }
        }
        return shapes;
    }

    private static String[] concatenate(String[] first, String[] second) {
        String[] arr = new String[first.length + second.length];
        System.arraycopy( first, 0, arr, 0, first.length );
        System.arraycopy( second, 0, arr, first.length, second.length );
        return arr;
    }

    private Collection<String> parsePropertiesAndStyles() {
        StringBuilder result = new StringBuilder();
        String attributes = attributesAndStyles.get( SvgConstants.Attributes.D );
        String closePath = attributes.indexOf( 'z' ) > 0 ? attributes.substring( attributes.indexOf( 'z' ) ) : "".trim();

        if (!closePath.equals( SEPERATOR )) {
            attributes = attributes.replace( closePath, SEPERATOR ).trim();
        }

        String[] coordinates = attributes.split( SPLIT_REGEX );//gets an array attributesAr of {M 100 100, L 300 100, L200, 300, z}

        for (String inst : coordinates) {
            if (!inst.equals( SEPERATOR )) {
                String instruction = inst.charAt( 0 ) + SPACE_CHAR;
                String temp = instruction + inst.replace( inst.charAt( 0 ) + SEPERATOR, SEPERATOR ).replace( ",", SPACE_CHAR ).trim();
                result.append(SPACE_CHAR).append(temp);
            }
        }

        String[] resultArray = result.toString().split(SPLIT_REGEX);
        List<String> resultList = new ArrayList<>( Arrays.asList( resultArray ) );
        resultList.add( closePath );

        return resultList;
    }
}
